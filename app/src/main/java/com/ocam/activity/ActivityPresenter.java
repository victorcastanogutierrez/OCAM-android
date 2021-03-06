package com.ocam.activity;

import com.ocam.model.Activity;
import com.ocam.model.ActivityDTO;
import com.ocam.model.types.GPSPoint;

import org.greenrobot.greendao.DaoException;

import java.util.List;

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

    /**
     * Método que evalua si el usuario logueado puede monitorizar. Podrá en caso que la actividad
     * esté en curso, sea guía o bien participante
     * @param activity
     * @return
     */
    Boolean puedeMonitorizar(Activity activity);

    /**
     * Actualiza la password a la actividad pasada por parámetro
     * @param activityId
     * @param password
     */
    void updatePasswordActivity(Long activityId, String password);

    /**
     * Método que evalua si el usuario logueado puede unirse a la actividad
     * @param activity
     * @return
     */
    Boolean puedeUnirse(Activity activity);

    /**
     * Une al usuario logueado en la actividad
     * @param activity
     */
    void joinActivity(Activity activity, String password);

    /**
     * Cierra la actividad en curso cambiándola el estado a CLOSED
     * @param activity
     */
    void closeActivity(Activity activity);

    /**
     * Saca al hiker de la actividad
     * @param activity
     */
    void leaveActivity(Activity activity);

    /**
     * Evalúa si el usuario puede abandonar la actividad
     * @param activity
     * @return
     */
    Boolean puedeAbandonar(Activity activity);

    /**
     * Busca una actividad en local por su ID
     * @param activityLocalId
     * @return
     */
    Activity findLocalActivity(Long activityLocalId);

    /**
     * Persiste los cambios en local de una actividad
     * @param activity
     */
    void saveActivity(Activity activity);

    /**
     * Incluye al usuario identificado como hiker de la actividad
     * @param activity
     */
    void joinHikerActivity(Activity activity);

    /**
     * Comprueba si el usuario logueado es participante de la actividad
     * @param activity
     * @return
     */
    Boolean esParticipante(Activity activity);
}
