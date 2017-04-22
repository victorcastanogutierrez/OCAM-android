package com.ocam.periodicTasks.pendingActions.actions;


import android.content.Context;

import com.ocam.model.PendingAction;

public abstract class BaseAction implements Action {

    Context context;
    PendingAction pendingAction;
    private ActionFinishListener listener;

    public BaseAction (Context context, PendingAction pendingAction) {
        this.context = context;
        this.pendingAction = pendingAction;
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
}
