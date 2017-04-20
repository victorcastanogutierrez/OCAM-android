package com.ocam.manager;

import android.app.Application;
import android.util.Log;

import com.ocam.model.DaoMaster;
import com.ocam.model.DaoMaster.DevOpenHelper;
import com.ocam.model.DaoSession;

import org.greenrobot.greendao.database.Database;


public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, "ocam.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
