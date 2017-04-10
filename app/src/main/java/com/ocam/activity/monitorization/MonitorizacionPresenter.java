package com.ocam.activity.monitorization;


public interface MonitorizacionPresenter {

    /**
     * Carga los datos del track de la actividad
     */
    void loadActivityData(Long activityId);

    /**
     * Carga el ultimo reporte de cada Hiker unido a la actividad
     * @param activityId
     */
    void loadReportsData(Long activityId);
}
