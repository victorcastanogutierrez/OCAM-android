package com.ocam.activity.monitorization;


import com.ocam.model.types.GPSPoint;

import java.util.List;

public interface MonitorizacionView {

    /**
     * Instancia la vista con los datos de los hiker
     */
    void setUpRecyclerView();

    /**
     * Muestra una barra de progreso con un layout superpuesto a la vista
     * para evitar la interacción del usuario
     */
    void displayProgress();

    /**
     * Oculta la barra de progreso y el dialog superpuesto
     */
    void hideProgress();

    /**
     * Notificación a usuario
     * @param notification
     */
    void notifyText(String notification);

    /**
     * Método para mostrar el track de la ruta al usuario
     * @param puntos
     */
    void showTrack(List<GPSPoint> puntos);
}
