package com.ocam.osm.maptypes;


import com.ocam.model.types.GPSPoint;

public class MapTypesUtil {

    public static GPSPoint getPointFromXY(double x, double y) {
        double lng = x / 256 * 360 - 180;
        double n = Math.PI - 2 * Math.PI * y / 256;
        double lat = (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
        return new GPSPoint(lat, lng);
    }
}
