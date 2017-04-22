package com.ocam.periodicTasks.pendingActions.actions;


import java.util.List;

public interface Action {

    void execute(List<String> parameters);

    void setListener(ActionFinishListener listener);
}
