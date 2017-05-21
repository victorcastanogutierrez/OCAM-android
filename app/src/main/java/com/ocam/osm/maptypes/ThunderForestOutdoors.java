package com.ocam.osm.maptypes;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.views.MapView;

public class ThunderForestOutdoors extends BaseMapType {

    public ThunderForestOutdoors(String mapName) {
        super(mapName);
    }

    @Override
    public void setMap(MapView mapView) {
        mapView.setTileSource(new OnlineTileSourceBase(getMapTypeName(), 0, 18, 256, ".png",
                new String[] { "https://a.tile.thunderforest.com/outdoors/" ,
                        "https://b.tile.thunderforest.com/outdoors/",
                        "https://c.tile.thunderforest.com/outdoors/"}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY()
                        + mImageFilenameEnding + "?apikey=e19fe4da0a7b49c3b3f1381d6f091a42";
            }
        });
    }
}
