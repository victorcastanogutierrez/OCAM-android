package com.ocam.activity;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.manager.UserManager;
import com.ocam.manager.VolleyManager;
import com.ocam.model.Activity;
import com.ocam.model.Hiker;
import com.ocam.model.types.ActivityStatus;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.password;

/**
 * Presentador para la vista de detalles de una actividad
 */
public class ActivityPresenterImpl implements ActivityPresenter {

    private ActivityView activityView;
    private Context context;

    public ActivityPresenterImpl(ActivityView activityView, Context context) {
        this.activityView = activityView;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isUserGuide(Activity activity) {
        UserManager userManager = UserManager.getInstance();
        for (Hiker h : activity.getGuides()){
            if (h.getEmail().equals(userManager.getUserTokenDTO().getEmail())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean assertActivityRunning(Activity activity) {
        return ActivityStatus.RUNNING.equals(activity.getStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startActivity(Long activityId, String password) {
        this.activityView.displayProgress();
        ICommand<Void> myCommand = new MyCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_START_ACTIVITY,
                Request.Method.POST, Void.class, getHeaders(), getBody(activityId, password),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean puedeMonitorizar(Activity activity) {
        if (!assertActivityRunning(activity)) {
            return Boolean.FALSE;
        }
        if (isUserGuide(activity)) {
            return Boolean.TRUE;
        }
        return esParticipante(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePasswordActivity(Long activityId, String password) {
        this.activityView.displayProgress();
        ICommand<Void> myCommand = new MyUpdateCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(
                Constants.API_UPDATE_PASSWORD_ACTIVITY + '/' + activityId + '/' + password,
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean puedeUnirse(Activity activity) {
        return !isUserGuide(activity) && !esParticipante(activity) && assertActivityRunning(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unirseActividad(Activity activity) {
        this.activityView.displayProgress();
        UserManager userManager = UserManager.getInstance();
        ICommand<Void> myCommand = new MyJoinActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(
                Constants.API_UNIRSE_ACTIVIDAD + '/' + activity.getId() + '/' + userManager.getUserTokenDTO().getLogin(),
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * Comprueba si el usuario logueado es participante de la actividad
     * @param activity
     * @return
     */
    private Boolean esParticipante(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        for (Hiker h : activity.getHikers()) {
            if (h.getLogin().equals(loggedHiker)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Método que construye un String (JSON) con la información de la actividad para hacer la petición
     * POST a la API
     * @return
     */
    private String getBody(Long activityId, String password) {
        Activity activityDTO = new Activity();
        activityDTO.setId(activityId);
        activityDTO.setPassword(password);
        return new Gson().toJson(activityDTO).toString();
    }

    /**
     * Devuelve un Map con los headers para la petición
     * @return
     */
    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para dar por iniciada una actividad
     */
    private class MyCommand implements ICommand<Void> {

        /**
         * Oculta la barra de progreso y notifica al usuario de la actualización
         * del estado de la actividad
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            activityView.hideProgress();
            activityView.onActivityOpen();
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            activityView.hideProgress();
            activityView.notifyUser("La actividad no puede ser iniciada.");
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para actualizar la password de una actividad
     */
    private class MyUpdateCommand implements ICommand<Void> {

        /**
         * Oculta la barra de progreso y notifica al usuario de la actualización
         * de la password
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            activityView.hideProgress();
            activityView.notifyUser("Password actualizada");
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            activityView.hideProgress();
            activityView.notifyUser("No se ha podido actualizar la password");
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para unir al usuario a una actividad
     */
    private class MyJoinActivityCommand implements ICommand<Void> {

        /**
         * Oculta la barra de progreso y notifica al usuario
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            activityView.hideProgress();
            activityView.onHikerJoinActivity();
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            activityView.hideProgress();
            JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
            activityView.notifyUser(objError.get("message").getAsString());
        }
    }
}
