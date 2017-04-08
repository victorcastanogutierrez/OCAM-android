package com.ocam.activity.track;

import com.ocam.model.types.GPSPoint;

import java.util.List;

/**
 * Created by Victor on 07/04/2017.
 */

public interface TrackView {

    /**
     * Muestra un error en un Toast
     * @param error
     */
    void showError(String error);

    /**
     * Guarda los puntos ya parseados y crea el mapa
     * @param puntos
     */
    void showTrack(List<GPSPoint> puntos);

    /**
     * Oculta la barra de progreso
     */
    void hideProgress();
}
