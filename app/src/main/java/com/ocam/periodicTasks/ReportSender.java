package com.ocam.periodicTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.ocam.periodicTasks.state.ConnectionState;
import com.ocam.periodicTasks.state.DisconnectedState;
import com.ocam.periodicTasks.state.ReportState;
import com.ocam.util.ConnectionUtils;


public class ReportSender extends BroadcastReceiver implements UpdateLocationListener {

    private PendingResult result;
    private Context context;

    /**
     * Solicita actualización de la localización
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("-", "Solicita actualizacion de localizacion");
        this.result = goAsync();
        this.context = context;
        new LocationUtils(context, this); // Inicia la solicitud de localizacion
    }

    /**
     * Obtiene el objeto correspondiente al estado del dispositivo
     * @param context
     * @return
     */
    public ReportState getConnection(Context context) {
        return ConnectionUtils.isConnected(context) ? new ConnectionState(context, this.result) :
        new DisconnectedState(context);
    }

    /**
     * Método llamado una vez se obtiene la posición
     * @param location
     */
    @Override
    public void onLocationUpdate(Location location) {
        ReportState state = getConnection(context);
        state.doReport(location);
    }
}
