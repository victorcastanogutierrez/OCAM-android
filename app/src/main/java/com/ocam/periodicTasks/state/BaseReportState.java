package com.ocam.periodicTasks.state;


import android.content.Context;

import com.ocam.manager.App;
import com.ocam.model.DaoSession;
import com.ocam.model.ReportDao;
import com.ocam.model.types.GPSPointDao;

public abstract class BaseReportState implements ReportState{

    protected Context context;
    protected ReportDao reportDao;
    protected GPSPointDao gpsPointDao;

    public BaseReportState (Context context) {
        this.context = context;
        DaoSession daoSession = ((App) context.getApplicationContext()).getDaoSession();
        this.reportDao = daoSession.getReportDao();
        this.gpsPointDao = daoSession.getGPSPointDao();
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
