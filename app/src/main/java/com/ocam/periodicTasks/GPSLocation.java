package com.ocam.periodicTasks;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import java.util.List;


public class GPSLocation {

    /**
     * Comprueba los permisos necesarios para obtener la posición del dispositivo
     * @param context
     * @return
     */
    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Comprueba que sea posible obtener la posición del dispositivo vía GPS
     * @param context
     * @return
     */
    public static boolean checkGPSEnabled(final Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER );
    }
}
