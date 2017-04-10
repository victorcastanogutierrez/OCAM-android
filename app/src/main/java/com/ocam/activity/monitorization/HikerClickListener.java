package com.ocam.activity.monitorization;

import com.ocam.model.Hiker;
import com.ocam.model.Report;


public interface HikerClickListener {

    /**
     * Maneja el click sobre el checkbox de cada hiker
     * @param Report
     */
    void onClick(Report Report);
}
