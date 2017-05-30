package com.ocam.periodicTasks;


import android.location.Location;

public interface UpdateLocationListener {

    void onLocationUpdate(Location location);

    void onErrorLocationUpdate();
}
