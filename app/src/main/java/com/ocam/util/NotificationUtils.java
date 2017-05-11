package com.ocam.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ocam.R;

public class NotificationUtils {

    /**
     * Método que envía una notificación con la configuración pasada por parámetro
     * @param context
     * @param code
     * @param title
     * @param content
     * @param onGoing
     */
    public static void sendNotification(Context context, Integer code, String title, String content, Boolean onGoing) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setOngoing(onGoing)
                .setSmallIcon(R.drawable.testing1)
                .build();
        notificationManager.notify(code, notification);
    }

    public static void sendNotification(Context context, Integer code, String title, String content, Boolean onGoing, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setOngoing(onGoing)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.testing1)
                .build();
        notificationManager.notify(code, notification);
    }


}
