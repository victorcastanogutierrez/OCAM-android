package com.ocam.periodicTasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ocam.settings.SettingsFactory;
import com.ocam.util.Constants;
import com.ocam.settings.PreferencesSettingsImpl;

import static android.content.Context.ALARM_SERVICE;

/**
 * Clase con métodos de utilidad para iniciar y parar las tareas que se realizan
 * de manera periódica
 */
public class PeriodicTask {

    /**
     * Cancela el servicio
     * @param context
     */
    public static void stopBroadcast(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, ReportSender.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.BROADCAST_INTENT, intent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d("REPORTES", "Para proceso");
    }

    /**
     * Inicia el servicio para notificaciones
     * @param context
     */
    public static void startBroadcast(Context context) {
        Intent intent = new Intent(context, ReportSender.class);
        Integer periodicity = SettingsFactory.getPreferencesSettingsImpl(context).getMinutesConfiguration() * 60 * 1000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.BROADCAST_INTENT, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, periodicity, pendingIntent);
        Log.d("REPORTES", "Inicia proceso");
    }
}
