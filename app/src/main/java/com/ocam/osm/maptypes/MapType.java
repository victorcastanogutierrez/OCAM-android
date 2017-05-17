package com.ocam.osm.maptypes;


import org.osmdroid.views.MapView;

public interface MapType {

    String getMapTypeName();

    void setMap(MapView mapView);
}
