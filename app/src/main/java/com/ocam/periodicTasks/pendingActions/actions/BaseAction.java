package com.ocam.periodicTasks.pendingActions.actions;


import android.content.Context;

import com.ocam.model.PendingAction;
import com.ocam.util.Constants;
import com.ocam.util.PreferencesUtils;
import com.ocam.volley.NukeSSLCerts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAction implements Action {

    Context context;
    List<String> parametros;
    PendingAction pendingAction;
    private ActionFinishListener listener;

    public BaseAction (Context context, PendingAction pendingAction) {
        this.context = context;
        this.pendingAction = pendingAction;
        new NukeSSLCerts().nuke(); // Entorno de desarrollo, ignora certificados
    }

    public void setListener(ActionFinishListener listener) {
        this.listener = listener;
    }

    /**
     * Notifica al servicio de que la acción se ejecutó con éxito
     */
    void onActionFinish() {
        if (this.listener != null) {
            listener.onActionFinish(this.pendingAction);
        }
    }

    /**
     * Devuelve la cabecera de autenticación para la request
     * @return
     */
    Map<String,String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_AUTH_NAME, PreferencesUtils.getMonitorizationTokenHiker(this.context));
        return headers;
    }
}
