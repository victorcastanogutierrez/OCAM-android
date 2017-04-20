package com.ocam.activityList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ocam.manager.App;
import com.ocam.model.ActivityDao;
import com.ocam.model.DaoSession;
import com.ocam.util.ConnectionUtils;
import com.ocam.volley.VolleyManager;
import com.ocam.model.Activity;
import com.ocam.util.Constants;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Clase presentador para la lista de actividades
 */
public class ListPresenterImpl implements ListPresenter {

    private ListActivityView listActivityView;
    private VolleyManager volleyManager;
    private Context context;
    private ActivityDao activityDao;

    public ListPresenterImpl(ListActivityView listActivityView, Context context) {
        this.listActivityView = listActivityView;
        this.context = context;
        this.volleyManager = VolleyManager.getInstance(this.context);
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.activityDao = daoSession.getActivityDao();
    }

    @Override
    public void loadActivities() {
        //Si tiene conexión las obtenemos del servidor
        if (ConnectionUtils.isConnected(this.context)) {
            this.listActivityView.showProgress();

            ICommand<Activity[]> myCommand = new MyCommand();
            GsonRequest<Activity[]> request = new GsonRequest<Activity[]>(Constants.API_FIND_ALL_ACTIVITIES,
                    Request.Method.GET, Activity[].class, new HashMap<String, String>(),
                    new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

            volleyManager.addToRequestQueue(request);
        } else {
            //Si no tiene conexión las busca en local
            listActivityView.hideProgress();
            listActivityView.setUpRecyclerView(this.activityDao.queryBuilder().list());
            listActivityView.notifySnackbar("No tienes conexión a internet");
        }
    }

    @Override
    public void reloadActivities() {
        if (ConnectionUtils.isConnected(this.context)) {
            this.listActivityView.showProgress();

            ICommand<Activity[]> myCommand = new MyUpdateCommand();
            GsonRequest<Activity[]> request = new GsonRequest<Activity[]>(Constants.API_FIND_ALL_ACTIVITIES,
                    Request.Method.GET, Activity[].class, new HashMap<String, String>(),
                    new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

            volleyManager.addToRequestQueue(request);
        } else {
            listActivityView.notifyError("Opción no disponible sin conexión");
            listActivityView.hideProgress();
        }
    }

    /**
     * {@inheritDoc}
     * @param date
     * @param originalData
     * @return
     */
    public List<Activity> getFilteredList(Date date, List<Activity> originalData) {
        List<Activity> result = new ArrayList<Activity>();
        for (Activity activity : originalData) {
            if (activity.getStartDate().compareTo(date) <= 0) {
                result.add(activity);
            }
        }
        return result;
    }

    /**
     * Guarda en local los datos de las actividades
     * @param activities
     */
    private void saveListData(List<Activity> activities) {
        this.activityDao.deleteAll();
        this.activityDao.insertInTx(activities);
    }

    private class MyUpdateCommand implements ICommand<Activity[]> {

        @Override
        public void executeResponse(Activity[] response) {
            listActivityView.hideProgress();
            listActivityView.reloadRecyclerData(Arrays.asList(response));
            saveListData(Arrays.asList(response));
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
            saveListData(Arrays.asList(response));
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            listActivityView.notifyError(Constants.ERROR_LOADING_ACTIVITIES);
        }
    }
}
