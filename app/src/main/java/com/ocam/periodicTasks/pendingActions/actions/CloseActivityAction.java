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
import com.ocam.model.Report;
import com.ocam.model.ReportDao;
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

public class CloseActivityAction extends BaseAction {

    private static final String TAG = "CLOSE ACTIVITY ACTION";

    public CloseActivityAction(Context context, PendingAction pendingAction) {
        super(context, pendingAction);
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters == null || parameters.size() < 1) {
            Log.d(TAG, "Parámetros de entrada inválidos");
        }
        this.parametros = parameters;
        Log.d(TAG, "Ejecuta con "+parameters.get(0));

        ICommand<Void> myCommand = new MyCloseActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_CLOSE_ACTIVITY + '/' + parameters.get(0),
                Request.Method.POST, Void.class, getHeaders(),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    private class MyCloseActivityCommand implements ICommand<Void> {

        /**
         * Respuesta 2XX del servidor
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            NotificationUtils.sendNotification(
                    context, Constants.ONGOING_NOTIFICATION_ID, "Actividad", "Cerraste con éxito la actividad", false);
            PeriodicTask.stopBroadcast(context);
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
                    context, Constants.ONGOING_NOTIFICATION_ID, "Actividad",
                    "Ocurrió un error cerrando la actividad", false);
            reOpenActivity();
        }
    }

    private void reOpenActivity() {
        Long activityId = Long.parseLong(this.parametros.get(0));
        if (activityId != null) {
            DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
            ActivityDao actDao = daoSession.getActivityDao();
            Activity act = actDao.queryBuilder().where(ActivityDao.Properties.Id.eq(activityId)).unique();
            if (act != null) {
                act.setStatus(ActivityStatus.RUNNING);
                act.update();
            }
        }
    }
}
