package com.ocam.osm.maptypes;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.views.MapView;



public class HikerMap extends BaseMapType {

    public HikerMap(String mapName) {
        super(mapName);
    }

    @Override
    public void setMap(MapView mapView) {
        mapView.setTileSource(new XYTileSource(getMapTypeName(), 0, 18, 256, ".png",
                new String[] { "http://a.tiles.wmflabs.org/hikebike/",
                        "http://b.tiles.wmflabs.org/hikebike/",
                        "http://c.tiles.wmflabs.org/hikebike/"}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY()
                        + mImageFilenameEnding;
            }
        });
    }
}
