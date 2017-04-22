package com.ocam.model.types;


import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    JOIN_ACTIVITY (1),
    START_ACTIVITY (2);

    private Integer actionId;

    ActionType(Integer actionId) {
        this.actionId = actionId;
    }

    private static Map<Integer, ActionType> map = new HashMap<Integer, ActionType>();

    static {
        for (ActionType actionEnum : ActionType.values()) {
            map.put(actionEnum.actionId, actionEnum);
        }
    }

    public static ActionType valueOf(Integer actionId) {
        return map.get(actionId);
    }

    public int getActionId() {
        return actionId;
    }
}
