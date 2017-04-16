package com.ocam.periodicTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ocam.periodicTasks.state.ConnectionState;
import com.ocam.periodicTasks.state.DisconnectedState;
import com.ocam.periodicTasks.state.ReportState;
import com.ocam.util.ConnectionUtils;

/**
 * BroadcastReceiver ejecutado periódicamente cuando el usuario está en una actividad en curso.
 * Genera un reporte de posición y dependiendo del estado del dispositivo lo gestiona de una
 * manera u otra
 */
public class ReportSender extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ReportState state = getConnection(context);
        state.doReport();
    }

    /**
     * Obtiene el objeto correspondiente al estado del dispositivo
     * @param context
     * @return
     */
    public ReportState getConnection(Context context) {
        return ConnectionUtils.isConnected(context) ? new ConnectionState(context, goAsync()) :
                new DisconnectedState(context);
    }
}
