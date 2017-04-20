package com.ocam.model.types;

public enum ActivityStatus {
	PENDING, RUNNING, CLOSED;

	public static String getFormattedStatus(ActivityStatus status) {
		return new String("RUNNING").equals(status.toString()) ? "EN CURSO" :
				new String("CLOSED").equals(status.toString()) ? "FINALIZADA" : "PENDIENTE";
	}
}
