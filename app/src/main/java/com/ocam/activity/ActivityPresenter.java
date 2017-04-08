package com.ocam.activity;

import com.ocam.model.Activity;

/**
 * Created by Victor on 08/04/2017.
 */

public interface ActivityPresenter {

    /**
     * Comprueba que el usuario logueado en la aplicación es uno de los guías
     * de la actividad pasada por parámetro
     * @param activity
     * @return
     */
    Boolean isUserGuide(Activity activity);

    /**
     * Comprueba que el estado de la actividad sea RUNNING
     * @param activity
     * @return
     */
    Boolean assertActivityRunning(Activity activity);

    /**
     * Da por iniciada una actividad. Llama a la API correspondiente para actualizar
     * el estado de la misma e incluir los guías como hikers de la actividad pasada por parámetro
     * @param activityId
     * @param password
     */
    void startActivity(Long activityId, String password);
}
