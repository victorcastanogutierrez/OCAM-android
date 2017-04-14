package com.ocam.periodicTasks.state;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.ocam.R;
import com.ocam.manager.UserManager;
import com.ocam.model.Report;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPoint;
import com.ocam.periodicTasks.GPSLocation;
import com.ocam.util.Constants;

import java.util.Date;

/**
 * Clase que implementa el patrón state en el estado de desconexión del dispositivo.
 * Guarda el report en la base de datos interna del dispositivo usando el ORM greenDAO
 */
public class DisconnectedState extends BaseReportState {

    public DisconnectedState(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     * Almacena el report de manera local
     */
    @Override
    public void doReport() {
        saveReport();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.ONGOING_NOTIFICATION_ID); // Cancelamos la que hay para recrearla
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Guarda local")
                .setContentText("-")
                .setSmallIcon(R.drawable.mountain_home)
                .build();

        notificationManager.notify(01, notification);
    }

    /**
     * Crea el report con los datos necesarios
     * @return
     */
    private void saveReport() {
        GPSPoint gpsPoint = new GPSPoint(GPSLocation.getLastKnownLocation(this.context));
        Long pointId = this.gpsPointDao.insert(gpsPoint);

        Report report = new Report();
        report.setDate(new Date());
        report.setPoint(gpsPoint);
        reportDao.insert(report);
        report.setGpsPointId(pointId);
    }
}
