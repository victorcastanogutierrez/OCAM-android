package com.ocam.osm;


import com.ocam.osm.maptypes.HikerMap;
import com.ocam.osm.maptypes.MapBoxSatelite;
import com.ocam.osm.maptypes.MapBoxTerrain;
import com.ocam.osm.maptypes.MapType;
import com.ocam.osm.maptypes.OSMType;
import com.ocam.osm.maptypes.PNOAIGN;
import com.ocam.osm.maptypes.RasterES;
import com.ocam.osm.maptypes.RasterFR;
import com.ocam.osm.maptypes.ThunderForestCycle;
import com.ocam.osm.maptypes.ThunderForestLandscape;
import com.ocam.osm.maptypes.ThunderForestOutdoors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapTypesManager {

    private static final MapTypesManager ourInstance = new MapTypesManager();
    private Map<String, MapType> mapTypes;

    static MapTypesManager getInstance() {
        return ourInstance;
    }

    private MapTypesManager() {
        initMapTypes();
    }

    private void initMapTypes() {
        this.mapTypes = new HashMap<>();
        this.mapTypes.put("PNOA IGN", new PNOAIGN("PNOA IGN"));
        this.mapTypes.put("Raster ES", new RasterES("Raster ES"));
        this.mapTypes.put("Raster FR", new RasterFR("Raster FR"));
        this.mapTypes.put("ThunderForest Landscape", new ThunderForestLandscape("ThunderForest Landscape"));
        this.mapTypes.put("ThunderForest Cycle", new ThunderForestCycle("ThunderForest Cycle"));
        this.mapTypes.put("ThunderForest Outdoors", new ThunderForestOutdoors("ThunderForest Outdoors"));
        this.mapTypes.put("OSM", new OSMType("OSM"));
        this.mapTypes.put("HikerMap", new HikerMap("hikerMap"));
        this.mapTypes.put("MapBoxSatellite", new MapBoxSatelite("MapBox Satelite"));
        this.mapTypes.put("MapBoxTerrain", new MapBoxTerrain("MapBox Terrain"));
    }

    public Map<String, MapType> getMapTypes() {
        return mapTypes;
    }

    public List<String> getMapTypeNames() {
        return new ArrayList<String>(mapTypes.keySet());
    }
}
