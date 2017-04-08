package com.ocam.activity;

import com.ocam.manager.UserManager;
import com.ocam.model.Activity;
import com.ocam.model.Hiker;
import com.ocam.model.types.ActivityStatus;

/**
 * Created by Victor on 08/04/2017.
 */

public class ActivityPresenterImpl implements ActivityPresenter {

    /**
     * Comprueba que el usuario logueado en la aplicación sea uno de los guías de la actividad
     * @param activity
     * @return
     */
    @Override
    public Boolean isUserGuide(Activity activity) {
        UserManager userManager = UserManager.getInstance();
        for (Hiker h : activity.getGuides()){
            if (h.getEmail().equals(userManager.getUserTokenDTO().getEmail())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Comprueba que la actividad esté en curso
     * @param activity
     * @return
     */
    @Override
    public Boolean assertActivityRunning(Activity activity) {
        return ActivityStatus.RUNNING.equals(activity.getStatus());
    }
}
