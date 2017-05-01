package com.ocam.periodicTasks.state;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ocam.model.Report;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.Constants;
import com.ocam.util.DateUtils;
import com.ocam.util.NotificationUtils;

import java.util.Date;

/**
 * Clase que implementa el patrón state en el estado de desconexión del dispositivo.
 * Guarda el report en la base de datos interna del dispositivo usando el ORM greenDAO
 */
public class DisconnectedState extends BaseReportState {

    public DisconnectedState(Context context, BroadcastReceiver.PendingResult result) {
        super(context, result);
    }

    /**
     * {@inheritDoc}
     * Almacena el report de manera local
     */
    @Override
    public void doReport(Location location) {
        saveReport(location);
    }

    /**
     * Crea el report con los datos necesarios
     * @return
     */
    private void saveReport(Location location) {
        if (location != null) {
            Log.d("-", "Guarda reporte en local");
            GPSPoint gpsPoint = new GPSPoint(location);
            Long pointId = this.gpsPointDao.insert(gpsPoint);

            Report report = new Report();
            report.setDate(new Date().getTime());
            report.setPoint(gpsPoint);
            report.setPending(Boolean.TRUE);
            reportDao.insert(report);
            report.setGpsPointId(pointId);

            Integer encolados = reportDao.queryBuilder().where(ReportDao.Properties.Pending.eq(Boolean.TRUE)).list().size();

            NotificationUtils.sendNotification(context, Constants.NOTIFICATION_NOTCONNECTED,
                    "No tienes conexión a internet", "Reportes encolados " + encolados, Boolean.FALSE);
        } else {
            NotificationUtils.sendNotification(context, 01,
                    "Localización error", "No hemos podido obtener la localización del dispositivo!",
                    Boolean.FALSE);
        }
        result.finish();
    }
}
