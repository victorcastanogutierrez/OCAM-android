package com.ocam.periodicTasks.state;


import android.content.Context;
import android.location.Location;

import com.ocam.model.Report;
import com.ocam.model.types.GPSPoint;
import com.ocam.periodicTasks.GPSLocation;
import com.ocam.util.NotificationUtils;

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
    public void doReport(Location location) {
        saveReport(location);
    }

    /**
     * Crea el report con los datos necesarios
     * @return
     */
    private void saveReport(Location location) {
        //Location location = GPSLocation.getLastKnownLocation(this.context);
        if (location != null) {
            GPSPoint gpsPoint = new GPSPoint(location);
            Long pointId = this.gpsPointDao.insert(gpsPoint);

            Report report = new Report();
            report.setDate(new Date());
            report.setPoint(gpsPoint);
            reportDao.insert(report);
            report.setGpsPointId(pointId);
        } else {
            NotificationUtils.sendNotification(context, 01,
                    "Localización error", "No hemos podido obtener la localización del dispositivo!",
                    Boolean.FALSE);
        }
    }
}
