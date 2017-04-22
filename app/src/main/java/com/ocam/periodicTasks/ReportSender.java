package com.ocam.periodicTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.ocam.manager.App;
import com.ocam.model.DaoSession;
import com.ocam.model.PendingAction;
import com.ocam.periodicTasks.pendingActions.ActionService;
import com.ocam.periodicTasks.state.ConnectionState;
import com.ocam.periodicTasks.state.DisconnectedState;
import com.ocam.periodicTasks.state.ReportState;
import com.ocam.util.ConnectionUtils;

import java.util.List;


public class ReportSender extends BroadcastReceiver implements UpdateLocationListener {

    private PendingResult result;
    private Context context;
    private List<PendingAction> actions;

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
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.actions = daoSession.getPendingActionDao().queryBuilder().list();
        new LocationUtils(context, this); // Inicia la solicitud de localizacion
    }

    /**
     * Obtiene el objeto correspondiente al estado del dispositivo
     *
     * Si tiene acciones por completar el estado será el de desconectado hasta que las complete
     * para así guardar los reportes de posición de manera local
     *
     * @param context
     * @return
     */
    public ReportState getConnection(Context context) {

        if (this.actions.size() > 0) {
            return new DisconnectedState(context);
        }
        return ConnectionUtils.isConnected(context) ? new ConnectionState(context, this.result) :
            new DisconnectedState(context);
    }

    /**
     * Método llamado una vez se obtiene la posición
     *
     * Si está conectado y tiene acciones por completar las completará
     * @param location
     */
    @Override
    public void onLocationUpdate(Location location) {
        ReportState state = getConnection(context);
        state.doReport(location);
        if (ConnectionUtils.isConnected(this.context) && this.actions.size() > 0) {
            Log.d("-", "Completa acciones pendientes");
            Intent i = new Intent(context, ActionService.class);
            this.context.startService(i);
        }
    }
}
