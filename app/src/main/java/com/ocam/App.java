package com.ocam;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ocam.model.DaoMaster;
import com.ocam.model.DaoMaster.DevOpenHelper;
import com.ocam.model.DaoSession;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.greendao.database.Database;


public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        DevOpenHelper helper = new DevOpenHelper(this, "ocam.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
