package com.ocam.model;

import com.ocam.model.types.GPSPoint;

import java.util.List;

/**
 * Created by Victor on 06/04/2017.
 */

public class Track {

    private List<GPSPoint> puntos;

    public List<GPSPoint> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<GPSPoint> puntos) {
        this.puntos = puntos;
    }
}
