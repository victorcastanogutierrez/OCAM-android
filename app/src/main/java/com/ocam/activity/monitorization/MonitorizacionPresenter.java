package com.ocam.activity.monitorization;


import com.ocam.model.ReportDTO;

import java.util.List;

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

    /**
     * Guarda la lista de reportes en local
     * @param activityId
     * @param datos
     */
    void saveLocalData(Long activityId, List<ReportDTO> datos);
}
