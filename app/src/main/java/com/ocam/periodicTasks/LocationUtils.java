package com.ocam.periodicTasks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ocam.util.NotificationUtils;

import static com.ocam.util.Constants.GPS_ERROR;


public class LocationUtils implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<LocationSettingsResult> {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Context context;
    UpdateLocationListener listener;
    LocationSettingsRequest mLocationSettingsRequest;


    public LocationUtils(Context context, UpdateLocationListener listener) {
        this.listener = listener;
        this.context = context;
        //show error dialog if GoolglePlayServices not available
        if (isGooglePlayServicesAvailable()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setNumUpdates(1);
            //mLocationRequest.setSmallestDisplacement(10.0f);  /* min dist for location change, here it is 10 meter */
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();

            mGoogleApiClient.connect();
            Log.d("-", "crea Google API");
        } else{
            Log.d("ERROR", "GoogleAPI no disponible");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("-", "Llega localizacion");
        Log.d("-", location.getLatitude()+", "+location.getLongitude());
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        listener.onLocationUpdate(location);
        Log.d("-", location.getProvider());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return ConnectionResult.SUCCESS == status;
    }

    protected void startLocationUpdates() {
        checkLocationSettings();
    }

    private void requestLocation() {
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
    public void onResult(LocationSettingsResult locationSettingsResult) {
        Log.d("-", "Llega solicitud de permisos");
        final Status status = locationSettingsResult.getStatus();
        Boolean locValid = Boolean.TRUE;
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                NotificationUtils.sendNotification(context, GPS_ERROR,
                        "GPS inaccesible", "La configuración del telefono no permite acceder al GPS.",
                        false, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                locValid = Boolean.FALSE;
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                NotificationUtils.sendNotification(context, GPS_ERROR, "GPS inaccesible",
                        "La configuración del telefono no permite acceder al GPS.",
                        false, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                locValid = Boolean.FALSE;
                break;
        }
        if (Boolean.TRUE.equals(locValid)) {
            requestLocation();
        } else {
            this.listener.onErrorLocationUpdate();
        }
    }

    private void checkLocationSettings() {
        Log.d("-", "Solicita settings");
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}