package com.ocam.activityList;

import com.ocam.model.Activity;

import java.util.Date;
import java.util.List;

/**
 * Created by Victor on 04/04/2017.
 */

public interface ListPresenter {

    void loadActivities();

    void reloadActivities();

    /**
     * Obtiene de una lista de actividades las que tiene fecha de inicio menor a la actual
     * @param date
     * @param originalData
     * @return
     */
    List<Activity> getFilteredList(Date date, List<Activity> originalData);
}
