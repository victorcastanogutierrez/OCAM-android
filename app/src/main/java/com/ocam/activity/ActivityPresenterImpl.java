package com.ocam.activity;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.manager.UserManager;
import com.ocam.periodicTasks.PeriodicTask;
import com.ocam.util.NotificationUtils;
import com.ocam.util.ViewUtils;
import com.ocam.volley.VolleyManager;
import com.ocam.model.Activity;
import com.ocam.model.HikerDTO;
import com.ocam.model.types.ActivityStatus;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        for (HikerDTO h : activity.getGuides()){
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
    public void joinActivity(Activity activity, String password) {
        this.activityView.displayProgress();
        UserManager userManager = UserManager.getInstance();
        ICommand<Void> myCommand = new MyJoinActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(
                Constants.API_UNIRSE_ACTIVIDAD + '/' + activity.getId() +
                        '/' + userManager.getUserTokenDTO().getLogin() + '/' + password,
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeActivity(Activity activity) {
        this.activityView.displayProgress();
        ICommand<Void> myCommand = new CloseActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_CLOSE_ACTIVITY + '/' + activity.getId(),
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    /**
     * {@inheritDoc}
     * @param activity
     */
    @Override
    public void leaveActivity(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        this.activityView.displayProgress();
        removeHikerFromList(activity);
        PeriodicTask.cancelBroadcast(this.context);
        NotificationUtils.sendNotification(this.context, Constants.ONGOING_NOTIFICATION_ID, "Abandonaste la actividad", "Se ha interrumpido la monitorización.", Boolean.FALSE);

        ICommand<Void> myCommand = new MyLeaveCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_LEAVE_ACTIVITY + '/' +
                activity.getId() + '/' + loggedHiker,
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
        activityView.onLeaveActivity();
    }

    @Override
    public Boolean puedeAbandonar(Activity activity) {
        return !isUserGuide(activity) && esParticipante(activity) && assertActivityRunning(activity);
    }

    /**
     * Elimina al usuario identificado en la aplicación de entre los participantes de la actividad
     * @param activity
     */
    private void removeHikerFromList(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        for (Iterator<HikerDTO> iterator = activity.getHikers().iterator(); iterator.hasNext();) {
            HikerDTO hiker = iterator.next();
            if (hiker.getLogin().equals(loggedHiker)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Comprueba si el usuario logueado es participante de la actividad
     * @param activity
     * @return
     */
    private Boolean esParticipante(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        for (HikerDTO h : activity.getHikers()) {
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
         * del estado de la actividad.
         * Comienza el broadcast para recogida de los datos
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            activityView.hideProgress();
            activityView.onActivityOpen();
            activityView.iniciarMonitorizacion();
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
            activityView.iniciarMonitorizacion();
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            activityView.hideProgress();
            JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
            if (Constants.HTTP_422.equals(objError.get("status").getAsString())) {
                activityView.notifyUser(objError.get("message").getAsString());
            } else {
                activityView.notifyUser("No te has podido unir. Prueba de nuevo más tarde");
            }
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para dar por concluida una actividad
     */
    private class CloseActivityCommand implements ICommand<Void> {

        /**
         * Oculta la barra de progreso y notifica al usuario de la actualización
         * del estado de la actividad
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            activityView.hideProgress();
            activityView.onActivityClosed();
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            activityView.hideProgress();
            activityView.notifyUser("La actividad no pudo ser cerrada debido a un error.");
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para eliminar a un hiker de la actividad
     */
    private class MyLeaveCommand implements ICommand<Void> {

        /**
         * Notifica al usuario
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            ViewUtils.showToast(context, Toast.LENGTH_SHORT, "Abandonaste la actividad.");
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            ViewUtils.showToast(context, Toast.LENGTH_SHORT, "No has podido abandonar pero se interrumpe la monitorización.");
        }
    }
}
