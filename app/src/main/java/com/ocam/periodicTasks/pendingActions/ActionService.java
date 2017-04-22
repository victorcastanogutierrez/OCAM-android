package com.ocam.periodicTasks.pendingActions;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ocam.manager.App;
import com.ocam.model.DaoSession;
import com.ocam.model.PendingAction;
import com.ocam.periodicTasks.pendingActions.actions.Action;
import com.ocam.periodicTasks.pendingActions.actions.ActionFinishListener;

import java.util.List;

/**
 * Servicio que es ejecutado para resolver todas las acciones pendientes de ejecución
 */
public class ActionService extends Service implements ActionFinishListener {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DaoSession daoSession = ((App) this.getApplicationContext()).getDaoSession();
        List<PendingAction> actions = daoSession.getPendingActionDao().queryBuilder().list();
        Log.d("Servicio", "Ejecuta servicio acciones pendientes con "+actions.size()+" pendientes");
        for (PendingAction action : actions) {
            solveAction(action);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Obtiene la accion correspondoiente al pendingAction local y la ejecuta
     * @param pedingAction
     */
    private void solveAction (PendingAction pedingAction) {
        Action action = ActionFactory.getPendingAction(this, pedingAction);
        action.setListener(this);
        action.execute(pedingAction.getParametros());
    }

    @Override
    public void onActionFinish(PendingAction pendingAction) {
        Log.d("Accion", "Accion resuelta");
        DaoSession daoSession = ((App) this.getApplicationContext()).getDaoSession();
        daoSession.getPendingActionDao().delete(pendingAction);
    }
}
