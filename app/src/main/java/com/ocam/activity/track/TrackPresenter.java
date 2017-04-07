package com.ocam.activity.track;

import com.ocam.model.types.GPSPoint;

import java.util.List;


public interface TrackPresenter {

    List<GPSPoint> parseTrack(String track);
}
