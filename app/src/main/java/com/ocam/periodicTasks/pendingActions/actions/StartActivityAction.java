package com.ocam.periodicTasks.pendingActions.actions;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ocam.manager.App;
import com.ocam.model.Activity;
import com.ocam.model.ActivityDao;
import com.ocam.model.DaoSession;
import com.ocam.model.Hiker;
import com.ocam.model.JoinActivityHikers;
import com.ocam.model.JoinActivityHikersDao;
import com.ocam.model.PendingAction;
import com.ocam.model.types.ActivityStatus;
import com.ocam.periodicTasks.PeriodicTask;
import com.ocam.util.Constants;
import com.ocam.util.NotificationUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.VolleyManager;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.Iterator;
import java.util.List;

public class StartActivityAction extends BaseAction {

    private static final String TAG = "START ACTIVITY ACTION";

    public StartActivityAction(Context context, PendingAction pendingAction) {
        super(context, pendingAction);
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters == null || parameters.size() < 3) {
            Log.d(TAG, "Parámetros de entrada inválidos");
        }
        this.parametros = parameters;
        Log.d(TAG, "Ejecuta con "+parameters.get(0)+", "+parameters.get(1)+", "+parameters.get(2));

        ICommand<Void> myCommand = new MyStartActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_START_ACTIVITY,
                Request.Method.POST, Void.class, getHeaders(), getBody(Long.parseLong(this.parametros.get(0)), this.parametros.get(1)),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
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

    private class MyStartActivityCommand implements ICommand<Void> {

        /**
         * Respuesta 2XX del servidor
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            NotificationUtils.sendNotification(
                    context, 01, "Actividad", "Abriste con éxito la actividad", false);
            onActionFinish();
        }

        /**
         * Respuesta 4XX o timeout del servidor
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            if (error != null && error.getMessage() != null) {
                Log.d(TAG, ""+error.getMessage());
            }
            NotificationUtils.sendNotification(
                    context, Constants.ONGOING_NOTIFICATION_ID, "Actividad", "Error abriendo la actividad", false);
            removeAndCloseHikerFromActivity();
            removeAllPendingReports();
            onActionFinish();
            PeriodicTask.stopBroadcast(context);
        }
    }

    /**
     * Elimina de la DB local al hiker de la actividad y la asociación entre ellos
     */
    private void removeAndCloseHikerFromActivity() {
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        ActivityDao actDao = daoSession.getActivityDao();
        Activity act = actDao.queryBuilder().where(ActivityDao.Properties.Id.eq(this.parametros.get(0))).unique();
        if (act != null) {
            Iterator<Hiker> iter = act.getHikers().iterator();

            Hiker hiker = null;
            while (iter.hasNext()) {
                hiker = iter.next();
                if (hiker.getLogin().equals(this.parametros.get(2))) {
                    iter.remove();
                    actDao.insertOrReplace(act);
                    break;
                }
            }

            if (hiker != null && hiker.getLogin().equals(this.parametros.get(2))) {
                JoinActivityHikersDao joinDao = daoSession.getJoinActivityHikersDao();
                JoinActivityHikers join = joinDao.queryBuilder()
                        .where(JoinActivityHikersDao.Properties.ActivityId.eq(act.getId_local()),
                                JoinActivityHikersDao.Properties.HikerId.eq(hiker.getId_local()))
                        .unique();
                joinDao.delete(join);
            }
            act.setStatus(ActivityStatus.PENDING);
        }
    }

    private void removeAllPendingReports() {
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        daoSession.getReportDao().deleteAll();
    }
}
