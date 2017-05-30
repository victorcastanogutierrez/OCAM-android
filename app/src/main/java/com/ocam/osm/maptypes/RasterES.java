package com.ocam.osm.maptypes;

import com.ocam.model.types.GPSPoint;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.views.MapView;

import static com.ocam.osm.maptypes.MapTypesUtil.getPointFromXY;

public class RasterES extends BaseMapType {

    public RasterES(String mapName) {
        super(mapName);
    }

    @Override
    public void setMap(MapView mapView) {
        mapView.setTileSource(new OnlineTileSourceBase(getMapTypeName(), 0, 18, 256, "",
                new String[] { "http://www.ign.es/wms-inspire/mapa-raster?request=GetMap&service=WMS&VERSION=1.3.0&LAYERS=mtn_rasterizado&STYLES=&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT=TRUE&CRS=EPSG:4326&WIDTH=250&HEIGHT=250&BBOX="}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                double zpow = Math.pow(2, aTile.getZoomLevel());
                GPSPoint ulw = getPointFromXY(aTile.getX() * 256.0 / zpow, (aTile.getY() + 1) * 256.0 / zpow);
                GPSPoint lrw = getPointFromXY((aTile.getX() + 1) * 256.0 / zpow, (aTile.getY()) * 256.0 / zpow);
                String bbox = ulw.getLatitude() + "," + ulw.getLongitude() + "," + lrw.getLatitude() + "," + lrw.getLongitude();
                return getBaseUrl() + bbox;
            }
        });
    }
}
