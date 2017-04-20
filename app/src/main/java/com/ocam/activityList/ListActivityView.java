package com.ocam.activityList;

import com.ocam.model.Activity;

import java.util.List;

/**
 * Created by Victor on 04/04/2017.
 */

public interface ListActivityView {

    void showProgress();

    void hideProgress();

    void setUpRecyclerView(List<Activity> datos);

    void reloadRecyclerData(List<Activity> datos);

    void notifyError(String err);

    void notifySnackbar(String notification);
}
