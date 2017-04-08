package com.ocam.activity;

import com.ocam.model.Activity;

/**
 * Created by Victor on 08/04/2017.
 */

public interface ActivityPresenter {

    Boolean isUserGuide(Activity activity);

    Boolean assertActivityRunning(Activity activity);
}
