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
    private List<Activity> activities;

    public ListPresenterImpl(ListActivityView listActivityView, Context context) {
        this.listActivityView = listActivityView;
        this.context = context;
        this.volleyManager = VolleyManager.getInstance(this.context);
        this.activities = new ArrayList<Activity>();
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

    private class MyCommand implements ICommand<Activity[]> {

        @Override
        public void executeResponse(Activity[] response) {
            listActivityView.hideProgress();
            activities = Arrays.asList(response);
            listActivityView.setUpRecyclerView(activities);
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            Log.d("Mal", ""+error.getMessage());
        }
    }
}
