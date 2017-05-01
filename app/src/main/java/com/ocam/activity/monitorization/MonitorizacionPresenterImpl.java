package com.ocam.activity.monitorization;


import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.manager.App;
import com.ocam.model.Activity;
import com.ocam.model.ActivityDao;
import com.ocam.model.DaoSession;
import com.ocam.model.Hiker;
import com.ocam.model.HikerDao;
import com.ocam.model.Report;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPointDao;
import com.ocam.util.ConnectionUtils;
import com.ocam.volley.VolleyManager;
import com.ocam.model.ReportDTO;
import com.ocam.model.types.GPSPoint;
import com.ocam.model.types.Track;
import com.ocam.util.Constants;
import com.ocam.util.XMLUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MonitorizacionPresenterImpl implements MonitorizacionPresenter {

    private Context context;
    private MonitorizacionView monitorizacionView;
    private List<ReportDTO> reportDTOs;
    private ActivityDao activityDao;
    private ReportDao reportDao;
    private GPSPointDao gpsPointDao;
    private HikerDao hikerDao;

    public MonitorizacionPresenterImpl(Context context, MonitorizacionView monitorizacionView) {
        this.context = context;
        this.monitorizacionView = monitorizacionView;
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.activityDao = daoSession.getActivityDao();
        this.reportDao = daoSession.getReportDao();
        this.gpsPointDao = daoSession.getGPSPointDao();
        this.hikerDao = daoSession.getHikerDao();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadActivityData(Long activityId) {
        monitorizacionView.displayProgress();
        ICommand<Track> myCommand = new trackCommand();
        GsonRequest<Track> trackRequest = new GsonRequest<Track>(Constants.API_FIND_ACTIVITY + "/" + activityId,
                Request.Method.GET, Track.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(trackRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadReportsData(Long activityId) {
        if (ConnectionUtils.isConnected(this.context)) {
            monitorizacionView.displayProgress();
            ICommand<ReportDTO[]> reportsCommand = new reportsCommand();
            GsonRequest<ReportDTO[]> hikersRequest = new GsonRequest<ReportDTO[]>(Constants.API_ALL_ACTIVITY_REPORTS + "/" + activityId,
                    Request.Method.GET, ReportDTO[].class, null,
                    new GenericResponseListener<>(reportsCommand), new GenericErrorListener(reportsCommand));

            VolleyManager.getInstance(this.context).addToRequestQueue(hikersRequest);
        } else {
            Activity act = activityDao.queryBuilder()
                    .where(ActivityDao.Properties.Id.eq(activityId)).unique();
            if (act != null) {
                List<Report> reports = reportDao.queryBuilder()
                        .where(ReportDao.Properties.Pending.eq(Boolean.FALSE),
                                ReportDao.Properties.ActivityId.eq(act.getId_local()))
                        .list();

                //Fuerzo a descargar tambien la relaccion con los hiker
                for (Report r : reports) {
                    r.getHiker();
                    r.getPoint();
                }

                this.reportDTOs = Arrays.asList(new Gson().fromJson(new Gson().toJson(reports), ReportDTO[].class));
                monitorizacionView.refreshHikersData(this.reportDTOs);
            }
            monitorizacionView.hideProgress();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveLocalData(Long activityId, List<ReportDTO> datos) {
        Activity act = activityDao.queryBuilder().where(ActivityDao.Properties.Id.eq(activityId)).unique();
        if (act != null) {

            List<Report> originalReports = reportDao.queryBuilder()
                    .where(ReportDao.Properties.Pending.eq(Boolean.FALSE),
                            ReportDao.Properties.ActivityId.eq(act.getId_local()))
                    .list();

            reportDao.deleteInTx(originalReports);
            act.resetReports();

            for (ReportDTO reportDTO : datos) {
                Hiker hiker = hikerDao.queryBuilder().where(HikerDao.Properties.Login.eq(reportDTO.getHikerDTO().getLogin())).unique();
                if (hiker == null) {
                    hiker = new Hiker(null,
                            reportDTO.getHikerDTO().getEmail(), reportDTO.getHikerDTO().getLogin());
                    hikerDao.insertOrReplace(hiker);
                }

                Long gpsPointId = gpsPointDao.insertOrReplace(
                        new GPSPoint(null,
                                reportDTO.getPoint().getLatitude(),
                                reportDTO.getPoint().getLongitude()));

                Report r = new Report();
                r.setDate(reportDTO.getDate());
                r.setPending(Boolean.FALSE);
                r.setGpsPointId(gpsPointId);
                r.setActivityId(act.getId_local());
                r.setHiker(hiker);
                reportDao.insertOrReplace(r);
                act.getReports().add(r);
                act.update();
            }
        }
    }

    @Override
    public int getReportesEncolados() {
        return reportDao.queryBuilder().where(ReportDao.Properties.Pending.eq(Boolean.TRUE)).list().size();
    }

    private List<GPSPoint> parseTrack(String track) {
        InputStream stream = new ByteArrayInputStream(track.getBytes(StandardCharsets.UTF_8));
        List<GPSPoint> result = new ArrayList<GPSPoint>();
        try {
            result = XMLUtils.parse(stream);
        } catch (XmlPullParserException | IOException e) {
            monitorizacionView.notifyText("Error procesando el track de la ruta");
        }
        return result;
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para obtener el track de la ruta
     */
    private class trackCommand implements ICommand<Track> {

        @Override
        public void executeResponse(Track response) {
            List<GPSPoint> track = parseTrack(response.getTrack());
            monitorizacionView.hideProgress();
            monitorizacionView.showTrack(track);
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            monitorizacionView.notifyText("Error obteniendo el track de la ruta");
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para obtener los reportes de los hikers de la actividad
     */
    private class reportsCommand implements ICommand<ReportDTO[]> {

        @Override
        public void executeResponse(ReportDTO[] response) {
            monitorizacionView.hideProgress();
            reportDTOs = Arrays.asList(response);
            monitorizacionView.refreshHikersData(reportDTOs);
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            Log.d("Error", error.getMessage()+"");
            monitorizacionView.notifyText("Error obteniendo los hikers de la actividad");
            monitorizacionView.hideProgress();
        }
    }
}
