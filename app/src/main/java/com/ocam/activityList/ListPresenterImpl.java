package com.ocam.activityList;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ocam.manager.App;
import com.ocam.model.ActivityDTO;
import com.ocam.model.ActivityDao;
import com.ocam.model.DaoSession;
import com.ocam.model.Hiker;
import com.ocam.model.HikerDao;
import com.ocam.model.JoinActivityGuides;
import com.ocam.model.JoinActivityGuidesDao;
import com.ocam.model.JoinActivityHikers;
import com.ocam.model.JoinActivityHikersDao;
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
    private HikerDao hikerDao;
    private JoinActivityGuidesDao joinActivityGuidesDao;
    private JoinActivityHikersDao joinActivityHikersDao;

    public ListPresenterImpl(ListActivityView listActivityView, Context context) {
        this.listActivityView = listActivityView;
        this.context = context;
        this.volleyManager = VolleyManager.getInstance(this.context);
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.activityDao = daoSession.getActivityDao();
        this.hikerDao = daoSession.getHikerDao();
        this.joinActivityGuidesDao = daoSession.getJoinActivityGuidesDao();
        this.joinActivityHikersDao = daoSession.getJoinActivityHikersDao();
    }

    @Override
    public void loadActivities() {
        //Si tiene conexión las obtenemos del servidor
        if (ConnectionUtils.isConnected(this.context)) {
            this.listActivityView.showProgress();

            ICommand<ActivityDTO[]> myCommand = new MyCommand();
            GsonRequest<ActivityDTO[]> request = new GsonRequest<ActivityDTO[]>(Constants.API_FIND_ALL_ACTIVITIES,
                    Request.Method.GET, ActivityDTO[].class, new HashMap<String, String>(),
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

            ICommand<ActivityDTO[]> myCommand = new MyUpdateCommand();
            GsonRequest<ActivityDTO[]> request = new GsonRequest<ActivityDTO[]>(Constants.API_FIND_ALL_ACTIVITIES,
                    Request.Method.GET, ActivityDTO[].class, new HashMap<String, String>(),
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
    private List<Activity> saveListData(List<ActivityDTO> activities) {
        removeData();
        List<Activity> result = new ArrayList<Activity>();
        for (ActivityDTO act : activities) {
            //Convierte ActivityDTO en activity pasandolo por JSON
            Activity activity = new Gson().fromJson(new Gson().toJson(act), Activity.class);

            //Persiste el dueño de la actividad
            Hiker owner = new Hiker(act.getOwner().getId(), null, act.getOwner().getEmail(), act.getOwner().getLogin());
            Long ownerId = hikerDao.insert(owner);
            activity.setOwnerId(ownerId);
            activity.setOwner(owner);

            Long activityLocalId = activityDao.insert(activity);

            //Persiste los guías de la actividad
            persistActivityGuides(act, activity, activityLocalId);

            //Persiste los participantes de la actividad
            persistActivityHikers(act, activity, activityLocalId);

            result.add(activity);
        }
        return result;
    }

    private void persistActivityHikers(ActivityDTO act, Activity activity, Long activityLocalId) {
        for (Hiker h : act.getHikers()) {
            Hiker actHiker = hikerDao.queryBuilder().where(HikerDao.Properties.Id.eq(h.getId())).unique();
            if (actHiker == null) {
                actHiker = new Hiker(h.getId(), null, h.getEmail(), h.getLogin());
                hikerDao.insert(actHiker);
            }

            JoinActivityHikers join = new JoinActivityHikers(null, activityLocalId, actHiker.getId_local());
            joinActivityHikersDao.insert(join);

            activity.getHikers().add(actHiker);
        }
    }

    private void persistActivityGuides(ActivityDTO act, Activity activity, Long activityLocalId) {
        for (Hiker h : act.getGuides()) {
            Hiker guide = hikerDao.queryBuilder().where(HikerDao.Properties.Id.eq(h.getId())).unique();
            if (guide == null) {
                guide = new Hiker(h.getId(), null, h.getEmail(), h.getLogin());
                hikerDao.insert(guide);
            }

            JoinActivityGuides join = new JoinActivityGuides(null, activityLocalId, guide.getId_local());
            joinActivityGuidesDao.insert(join);

            activity.getGuides().add(guide);
        }
    }

    private void removeData() {
        this.activityDao.deleteAll();
        this.hikerDao.deleteAll();
        this.joinActivityGuidesDao.deleteAll();
        this.joinActivityHikersDao.deleteAll();
    }

    private class MyUpdateCommand implements ICommand<ActivityDTO[]> {

        @Override
        public void executeResponse(ActivityDTO[] response) {
            listActivityView.hideProgress();
            List<Activity> acts = saveListData(Arrays.asList(response));
            listActivityView.reloadRecyclerData(acts);
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            listActivityView.notifyError(Constants.ERROR_LOADING_ACTIVITIES);
        }
    }

    private class MyCommand implements ICommand<ActivityDTO[]> {

        @Override
        public void executeResponse(ActivityDTO[] response) {
            listActivityView.hideProgress();
            List<Activity> acts = saveListData(Arrays.asList(response));
            listActivityView.setUpRecyclerView(acts);
        }

        @Override
        public void executeError(VolleyError error) {
            listActivityView.hideProgress();
            listActivityView.notifyError(Constants.ERROR_LOADING_ACTIVITIES);
        }
    }
}
