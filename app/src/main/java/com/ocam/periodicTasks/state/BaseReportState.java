package com.ocam.periodicTasks.state;


import android.content.BroadcastReceiver;
import android.content.Context;

import com.ocam.manager.App;
import com.ocam.model.DaoSession;
import com.ocam.model.HikerDao;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPointDao;

public abstract class BaseReportState implements ReportState{

    Context context;
    ReportDao reportDao;
    GPSPointDao gpsPointDao;
    HikerDao hikerDao;
    BroadcastReceiver.PendingResult result;

    public BaseReportState (Context context, BroadcastReceiver.PendingResult result) {
        this.context = context;
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.reportDao = daoSession.getReportDao();
        this.gpsPointDao = daoSession.getGPSPointDao();
        this.hikerDao = daoSession.getHikerDao();
        this.result = result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ReportDao getReportDao() {
        return reportDao;
    }

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }
}
