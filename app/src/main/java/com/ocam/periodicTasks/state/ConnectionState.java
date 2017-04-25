package com.ocam.periodicTasks.state;


import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.model.HikerDTO;
import com.ocam.model.Report;
import com.ocam.model.ReportDTO;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPoint;
import com.ocam.periodicTasks.PeriodicTask;
import com.ocam.util.Constants;
import com.ocam.util.DateUtils;
import com.ocam.util.NotificationUtils;
import com.ocam.util.PreferencesUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.NukeSSLCerts;
import com.ocam.volley.VolleyManager;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que implementa el patrón state en el estado de conexión del dispositivo. Envía el reporte
 * al servidor mediante una petición POST
 */
public class ConnectionState extends BaseReportState {

   // private PendingResult result;
    private List<Request<Void>> requests = new ArrayList<>();
    private VolleyManager volleyManager;


    public ConnectionState(Context context, PendingResult result) {
        super(context, result);
        this.volleyManager = VolleyManager.getInstance(context);
    }

    /**
     * Envía el reporte al servidor y los que estén almacenados de manera local
     * {@inheritDoc}
     */
    @Override
    public void doReport(Location location) {
        new NukeSSLCerts().nuke();
        if (location != null) {
            sendServerLocation(location, context);
        } else {
            NotificationUtils.sendNotification(context, 01,
                    "Localización error", "No hemos podido obtener la localización del dispositivo!",
                    Boolean.FALSE);
        }
    }

    private void testing(String step) {

        List reports2 = this.reportDao.queryBuilder()
                .where(ReportDao.Properties.Pending.eq(Boolean.TRUE))
                .list();
        Log.d(step, "Hay " + reports2.size()+" pendientes");
    }

    /**
     * Envía la POST al servidor con los reportes de posición. El que le toca enviar más todos los
     * que tenga almacenados de manera local
     *
     * @param location
     */
    private void sendServerLocation(Location location, Context context) {
        getReport(location);
        List<Report> reports = getPendingReports();

        ICommand<Void> myCommand = new ReportCommand();

        GsonRequest<Void> request;
        for (Report report : reports) {
            request = new GsonRequest<Void>(Constants.API_SAVE_REPORT,
                    Request.Method.POST, Void.class, getHeaders(), getReportData(report),
                    new GenericResponseListener<>(myCommand), new GenericErrorListener<>(myCommand));

            this.requests.add(request);

        }
        this.volleyManager.addToRequestQueue(requests.remove(0));
    }

    /**
     * Obtiene la lista de reports pendientes de envio guardados de manera local en el dispositivo
     * @return
     */
    private List<Report> getPendingReports() {
        return this.reportDao.queryBuilder()
                .where(ReportDao.Properties.Pending.eq(Boolean.TRUE))
                .list();
    }

    /**
     * Obtiene el report actual, lo guarda para que se reenvie en caso de haber timeout en el svr
     * @return
     */
    private Report getReport(Location location) {
        GPSPoint point = new GPSPoint(location);
        Long pid = this.gpsPointDao.insert(point);

        Report report = new Report();
        report.setPoint(point);
        report.setPending(Boolean.TRUE);
        report.setDate(new Date().getTime());
        this.reportDao.insert(report);

        report.setGpsPointId(pid);
        return report;
    }

    /**
     * Obtiene el body de la petición: objeto JSON con el report a guardar en el servidor
     * @param report
     * @return
     */
    private String getReportData(Report report) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setPoint(new GPSPoint(
                report.getPoint().getLatitude(), report.getPoint().getLongitude()));
        reportDTO.setDate(report.getDate());
        HikerDTO hikerDTO = new HikerDTO();
        hikerDTO.setLogin(PreferencesUtils.getMonitorizationHiker(this.context));
        reportDTO.setHikerDTO(hikerDTO);
        return new Gson().toJson(reportDTO);
    }

    /**
     * Devuelve la cabecera de autenticación para la request
     * @return
     */
    public Map<String,String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_AUTH_NAME, PreferencesUtils.getMonitorizationTokenHiker(this.context));
        return headers;
    }

    /**
     * Comando que encapsula la lógica a ejecutar con la respuesta a la petición POST al servidor
     */
    private class ReportCommand implements ICommand<Void> {
        @Override
        public void executeResponse(Void response) {
            Log.d("Exito", "Reporte enviado con exito");
            if (requests.size() > 0) {
                volleyManager.addToRequestQueue(requests.remove(0));
            } else {
                NotificationUtils.sendNotification(context, Constants.ONGOING_NOTIFICATION_ID,
                        "Participas en una actividad en curso", "Último reporte enviado a las " +
                                DateUtils.formatDate(new Date(), "HH:mm"), Boolean.TRUE);

                List<Report> reports = reportDao.queryBuilder().where(ReportDao.Properties.Pending.eq(Boolean.TRUE)).list();
                if (reports != null && reports.size() > 0) {
                    Log.d("-", "Elimina "+reports.size()+" reportes pendientes");
                    reportDao.deleteInTx(reports);
                }
                result.finish();
            }
        }

        @Override
        public void executeError(VolleyError error) {
            if (error != null && error.getMessage() != null) {
                Log.d("Error", error.getMessage());
                JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
                //Si la actividad cerró, deja de enviar reportes
                if (objError.get("status").getAsString().equals(Constants.HTTP_422)) {
                    Log.d("Cerrada", "Cierra la actividad");
                    NotificationUtils.sendNotification(context, Constants.ONGOING_NOTIFICATION_ID,
                            "Actividad concluida", "La monitorización de la actividad finalizó",
                            Boolean.FALSE);
                    PeriodicTask.stopBroadcast(context);
                }
            }
            result.finish();
        }
    }
}
