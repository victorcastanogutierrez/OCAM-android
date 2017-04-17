package com.ocam.periodicTasks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.ocam.util.Constants;

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
    public static void cancelService(Context context) {
        context.stopService(new Intent(context, LocService.class));
    }

    /**
     * Inicia el servicio para notificaciones
     * @param context
     */
    public static void startService(Context context) {
        Intent intent = new Intent (context, LocService.class);
        context.startService(intent);
        Log.d("REPORTES", "Inicia proceso");
    }
}
