package com.ocam.activityList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ocam.manager.VolleyManager;
import com.ocam.model.Activity;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Clase presentador para la lista de actividades
 */
public class ListPresenterImpl implements ListPresenter {

    private ListActivityView listActivityView;
    private VolleyManager volleyManager;
    private Context context;

    public ListPresenterImpl(ListActivityView listActivityView, Context context) {
        this.listActivityView = listActivityView;
        this.context = context;
        this.volleyManager = VolleyManager.getInstance(this.context);
    }

    @Override
    public void loadActivities() {
        this.listActivityView.showProgress();

        ICommand<Activity[]> myCommand = new MyCommand();
        GsonRequest<Activity[]> request = new GsonRequest<Activity[]>(Constants.API_FIND_ALL_ACTIVITIES,
                Request.Method.GET, Activity[].class, new HashMap<String, String>(),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        volleyManager.addToRequestQueue(request);
    }

    @Override
    public void reloadActivities() {
        this.listActivityView.showProgress();

        ICommand<Activity[]> myCommand = new MyUpdateCommand();
        GsonRequest<Activity[]> request = new GsonRequest<Activity[]>(Constants.API_FIND_ALL_ACTIVITIES,
                Request.Method.GET, Activity[].class, new HashMap<String, String>(),
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        volleyManager.addToRequestQueue(request);
    }

    private class MyUpdateCommand implements ICommand<Activity[]> {

        @Override
        public void executeResponse(Activity[] response) {
            listActivityView.hideProgress();
            listActivityView.setUpRecyclerView(Arrays.asList(response));
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            listActivityView.notifyError(Constants.ERROR_LOADING_ACTIVITIES);
        }
    }

    private class MyCommand implements ICommand<Activity[]> {

        @Override
        public void executeResponse(Activity[] response) {
            listActivityView.hideProgress();
            listActivityView.setUpRecyclerView(Arrays.asList(response));
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            listActivityView.notifyError(Constants.ERROR_LOADING_ACTIVITIES);
        }
    }
}
