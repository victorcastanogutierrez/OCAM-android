package com.ocam.periodicTasks.pendingActions;


import android.content.Context;

import com.ocam.model.PendingAction;
import com.ocam.model.types.ActionType;
import com.ocam.periodicTasks.pendingActions.actions.Action;
import com.ocam.periodicTasks.pendingActions.actions.JoinActivityAction;
import com.ocam.periodicTasks.pendingActions.actions.StartActivityAction;

public class ActionFactory {

    public static Action getPendingAction(Context context, PendingAction pendingAction) {
        if (pendingAction.getActionType().equals(ActionType.JOIN_ACTIVITY)) {
            return new JoinActivityAction(context, pendingAction);
        } else if (pendingAction.getActionType().equals(ActionType.START_ACTIVITY)) {
            return new StartActivityAction(context, pendingAction);
        }
        return null;
    }
}
