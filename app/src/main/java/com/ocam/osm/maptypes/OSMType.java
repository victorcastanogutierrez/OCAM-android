package com.ocam.osm.maptypes;


import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class OSMType extends BaseMapType {

    public OSMType(String mapName) {
        super(mapName);
    }

    @Override
    public void setMap(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
    }
}
