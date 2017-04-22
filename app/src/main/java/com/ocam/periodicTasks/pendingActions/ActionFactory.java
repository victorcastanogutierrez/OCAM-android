package com.ocam.periodicTasks.pendingActions;


import android.content.Context;

import com.ocam.model.PendingAction;
import com.ocam.model.types.ActionType;
import com.ocam.periodicTasks.pendingActions.actions.Action;
import com.ocam.periodicTasks.pendingActions.actions.JoinActivityAction;

public class ActionFactory {

    public static Action getPendingAction(Context context, PendingAction pendingAction) {
        if (pendingAction.getActionType().equals(ActionType.JOIN_ACTIVITY)) {
            return new JoinActivityAction(context, pendingAction);
        }
        return null;
    }
}
