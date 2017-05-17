package com.ocam.osm.maptypes;


public abstract class BaseMapType implements MapType {

    String mapName;

    public BaseMapType(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public String getMapTypeName() {
        return this.mapName;
    }
}
