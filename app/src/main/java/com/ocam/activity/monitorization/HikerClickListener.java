package com.ocam.activity.monitorization;

import com.ocam.model.ReportDTO;


public interface HikerClickListener {

    /**
     * Maneja el click sobre el checkbox de cada hiker
     * @param ReportDTO
     */
    void onClick(ReportDTO ReportDTO);
}
