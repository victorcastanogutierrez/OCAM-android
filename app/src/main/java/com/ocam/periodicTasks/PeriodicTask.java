package com.ocam.periodicTasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ocam.util.Constants;

import static android.content.Context.ALARM_SERVICE;

/**
 * Clase con métodos de utilidad para iniciar y parar las tareas que se realizan
 * de manera periódica
 */
public class PeriodicTask {

    /**
     * Cancela el broadcast
     * @param context
     */
    public static void cancelBroadcast(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, ReportSender.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.BROADCAST_INTENT, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * Crea e inicia el broadcast para notificaciones
     * @param context
     */
    public static void iniciarBroadcast(Context context) {
        Intent intent = new Intent(context, ReportSender.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.BROADCAST_INTENT, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constants.REPORTS_PERIODICITY, pendingIntent);
        Log.d("REPORTES", "Inicia proceso");
    }
}
