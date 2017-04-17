package com.ocam.periodicTasks.state;

import android.location.Location;

/**
 * Interface para implementar el patrón State en función del estado de conexión del dispositivo
 * a la hora de generar un reporte de posición
 */
public interface ReportState {

    /**
     * Genera el reporte de posición y lo gestiona de diferentes manera dependiendo
     * del estado del dispositivo
     */
    void doReport(Location location);
}
