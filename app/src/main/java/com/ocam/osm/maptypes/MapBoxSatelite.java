package com.ocam.osm.maptypes;

import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.views.MapView;



public class MapBoxSatelite extends BaseMapType {

    public MapBoxSatelite(String mapName) {
        super(mapName);
    }

    @Override
    public void setMap(MapView mapView) {
        final MapBoxTileSource tileSource = new MapBoxTileSource();
        tileSource.setAccessToken("pk.eyJ1IjoidmljdG9yb3ZpOTQiLCJhIjoiY2oyMWxiZXRjMDAwNzJxcnhoNXViYmdzdCJ9.4YJrWbfBoyK2h9h7JtRm7w");
        tileSource.setMapboxMapid("mapbox.satellite");
        mapView.setTileSource(tileSource);
    }
}
