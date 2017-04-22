package com.ocam.activity;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ocam.R;
import com.ocam.manager.App;
import com.ocam.manager.UserManager;
import com.ocam.model.Activity;
import com.ocam.model.ActivityDao;
import com.ocam.model.DaoSession;
import com.ocam.model.Hiker;
import com.ocam.model.HikerDao;
import com.ocam.model.JoinActivityHikers;
import com.ocam.model.JoinActivityHikersDao;
import com.ocam.model.PendingAction;
import com.ocam.model.PendingActionDao;
import com.ocam.model.UserTokenDTO;
import com.ocam.model.types.ActionType;
import com.ocam.model.types.ActivityStatus;
import com.ocam.periodicTasks.PeriodicTask;
import com.ocam.util.ConnectionUtils;
import com.ocam.util.Constants;
import com.ocam.util.NotificationUtils;
import com.ocam.util.ViewUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.VolleyManager;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Presentador para la vista de detalles de una actividad
 */
public class ActivityPresenterImpl implements ActivityPresenter {

    private ActivityView activityView;
    private Context context;
    private ActivityDao activityDao;
    private HikerDao hikerDao;
    private JoinActivityHikersDao joinActivityHikersDao;
    private PendingActionDao pendingActionDao;

    public ActivityPresenterImpl(ActivityView activityView, Context context) {
        this.activityView = activityView;
        this.context = context;
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.activityDao = daoSession.getActivityDao();
        this.hikerDao = daoSession.getHikerDao();
        this.joinActivityHikersDao = daoSession.getJoinActivityHikersDao();
        this.pendingActionDao = daoSession.getPendingActionDao();
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
        if (ConnectionUtils.isConnected(this.context)) {
            ICommand<Void> myCommand = new MyCommand();
            GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_START_ACTIVITY,
                    Request.Method.POST, Void.class, getHeaders(), getBody(activityId, password),
                    new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

            VolleyManager.getInstance(this.context).addToRequestQueue(request);
        } else {
            persistStartPendingAction(activityId, password);
            this.activityView.notifyUserDialog(context.getResources()
                    .getString(R.string.startActivityDisconnected));
            activityView.onActivityOpen();
            activityView.iniciarMonitorizacion();
            this.activityView.hideProgress();
        }
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
        return !esParticipante(activity) && assertActivityRunning(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void joinActivity(Activity activity, String password) {
        this.activityView.displayProgress();
        if (ConnectionUtils.isConnected(this.context)) {
            UserManager userManager = UserManager.getInstance();
            ICommand<Void> myCommand = new MyJoinActivityCommand();
            GsonRequest<Void> request = new GsonRequest<Void>(
                    Constants.API_UNIRSE_ACTIVIDAD + '/' + activity.getId() +
                            '/' + userManager.getUserTokenDTO().getLogin() + '/' + password,
                    Request.Method.POST, Void.class, null,
                    new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

            VolleyManager.getInstance(this.context).addToRequestQueue(request);
        } else {
            //Si no tiene conexion persistimos el intento de unirse
            persistJoinPendingAction(activity, password);
            joinHikerActivity(activity);
            this.activityView.hideProgress();
            this.activityView.onHikerJoinActivity();
            activityView.notifyUserDialog(this.context.getResources()
                    .getString(R.string.joinActivityDisconnected));
            activityView.iniciarMonitorizacion();
        }
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
     */
    @Override
    public void leaveActivity(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        this.activityView.displayProgress();
        removeHikerFromList(activity);
        PeriodicTask.stopBroadcast(this.context);
        NotificationUtils.sendNotification(this.context, Constants.ONGOING_NOTIFICATION_ID, "Abandonaste la actividad", "Se ha interrumpido la monitorización.", Boolean.FALSE);

        ICommand<Void> myCommand = new MyLeaveCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_LEAVE_ACTIVITY + '/' +
                activity.getId() + '/' + loggedHiker,
                Request.Method.POST, Void.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
        activityView.onLeaveActivity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean puedeAbandonar(Activity activity) {
        return !isUserGuide(activity) && esParticipante(activity) && assertActivityRunning(activity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Activity findLocalActivity(Long activityLocalId) {
        return activityDao.queryBuilder()
                .where(ActivityDao.Properties.Id_local.eq(activityLocalId))
                .unique();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveActivity(Activity activity) {
        this.activityDao.save(activity);
    }

    @Override
    public void joinHikerActivity(Activity activity) {
        UserTokenDTO hdto = UserManager.getInstance().getUserTokenDTO();
        Hiker hiker = hikerDao.queryBuilder().where(
                HikerDao.Properties.Login.eq(hdto.getLogin())).unique();
        if (hiker == null) {
            hiker = new Hiker(null, hdto.getEmail(), hdto.getLogin());
            hikerDao.insert(hiker);
        }

        List<JoinActivityHikers> joinList = joinActivityHikersDao.queryBuilder()
                .where(JoinActivityHikersDao.Properties.ActivityId.eq(activity.getId_local()),
                        JoinActivityHikersDao.Properties.HikerId.eq(hiker.getId_local())).list();
        if (joinList != null && joinList.size() > 0) {
            joinActivityHikersDao.deleteInTx(joinList);
        }

        JoinActivityHikers join = new JoinActivityHikers(null, activity.getId_local(), hiker.getId_local());
        joinActivityHikersDao.insertOrReplace(join);
        activity.getHikers().add(hiker);
        activityDao.insertOrReplace(activity);
    }

    /**
     * Elimina al usuario identificado en la aplicación de entre los participantes de la actividad
     * @param activity
     */
    private void removeHikerFromList(Activity activity) {
        String loggedHiker = UserManager.getInstance().getUserTokenDTO().getLogin();
        for (Iterator<Hiker> iterator = activity.getHikers().iterator(); iterator.hasNext();) {
            Hiker hiker = iterator.next();
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
     * Persiste de manera local el intento de unirse a una actividad
     * @param activity
     * @param password
     */
    private void persistJoinPendingAction(Activity activity, String password) {
        PendingAction pendingAction = new PendingAction(ActionType.JOIN_ACTIVITY);
        pendingAction.getParametros().add(activity.getId().toString());
        pendingAction.getParametros().add(UserManager.getInstance().getUserTokenDTO().getLogin());
        pendingAction.getParametros().add(password);
        pendingActionDao.insertOrReplace(pendingAction);
    }

    /**
     * Persiste de manera local el intento de unirse a una actividad
     * @param activityId
     * @param password
     */
    private void persistStartPendingAction(Long activityId, String password) {
        PendingAction pendingAction = new PendingAction(ActionType.START_ACTIVITY);
        pendingAction.getParametros().add(activityId.toString());
        pendingAction.getParametros().add(password);
        pendingAction.getParametros().add(UserManager.getInstance().getUserTokenDTO().getLogin());
        pendingActionDao.insertOrReplace(pendingAction);
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
            if (error  != null && error.getMessage() != null) {
                JsonObject objError = new Gson().fromJson(error.getMessage(), JsonObject.class);
                if (Constants.HTTP_422.equals(objError.get("status").getAsString())) {
                    activityView.notifyUser(objError.get("message").getAsString());
                } else {
                    activityView.notifyUser("No te has podido unir. Prueba de nuevo más tarde");
                }
            } else {
                // No debería darse
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
