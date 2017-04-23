package com.ocam.periodicTasks.pendingActions.actions;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ocam.model.PendingAction;
import com.ocam.periodicTasks.PeriodicTask;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.VolleyManager;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.List;

public class LeaveActivityAction extends BaseAction {

    private static final String TAG = "LEAVE ACTIVITY ACTION";

    public LeaveActivityAction(Context context, PendingAction pendingAction) {
        super(context, pendingAction);
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters == null || parameters.size() < 2) {
            Log.d(TAG, "Parámetros de entrada inválidos");
        }
        this.parametros = parameters;
        Log.d(TAG, "Ejecuta con "+parameters.get(0)+", "+parameters.get(1));

        ICommand<Void> myCommand = new MyLeaveActivityCommand();
        GsonRequest<Void> request = new GsonRequest<Void>(Constants.API_LEAVE_ACTIVITY + '/'
                    + parameters.get(0) + '/' + parameters.get(1),
                Request.Method.POST, Void.class, getHeaders(),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    private class MyLeaveActivityCommand implements ICommand<Void> {

        /**
         * Respuesta 2XX del servidor
         * @param response
         */
        @Override
        public void executeResponse(Void response) {
            onActionFinish();
            PeriodicTask.stopBroadcast(context);
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
            PeriodicTask.stopBroadcast(context);
            onActionFinish(); // No permite más intentos en caso de fallar
        }
    }
}
