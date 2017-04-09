package com.ocam.activity.monitorization;


public interface MonitorizacionPresenter {

    /**
     * Carga los datos de los hikers que están siendo monitorizados
     */
    void loadHikersData(Long activityId);
}
