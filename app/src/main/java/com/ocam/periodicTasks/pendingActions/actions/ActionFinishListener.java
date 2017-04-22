package com.ocam.periodicTasks.pendingActions.actions;

import com.ocam.model.PendingAction;


public interface ActionFinishListener {

    void onActionFinish(PendingAction pendingAction);
}
