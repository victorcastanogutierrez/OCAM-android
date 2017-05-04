package com.ocam.periodicTasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;


public class LocationUtils implements LocationListener,
        GoogleApiClient.ConnectionCallbacks{

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Context context;
    UpdateLocationListener listener;


    public LocationUtils(Context context, UpdateLocationListener listener) {
        this.listener = listener;
        this.context = context;
        //show error dialog if GoolglePlayServices not available
        if (isGooglePlayServicesAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(10.0f);  /* min dist for location change, here it is 10 meter */
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();

            mGoogleApiClient.connect();
            Log.d("-", "crea Google API");
        } else{
            Log.d("ERROR", "GoogleAPI no disponible");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("-", "Llega localizacion");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        listener.onLocationUpdate(location);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return ConnectionResult.SUCCESS == status;
    }

    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("", "Solicita localizacion");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (IllegalStateException e) {}
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}