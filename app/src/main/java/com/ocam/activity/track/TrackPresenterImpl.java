package com.ocam.activity.track;


import android.widget.Toast;

import com.ocam.manager.UserManager;
import com.ocam.model.Activity;
import com.ocam.model.Hiker;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.ViewUtils;
import com.ocam.util.XMLUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TrackPresenterImpl implements TrackPresenter {

    private TrackView trackView;

    public TrackPresenterImpl(TrackView trackView) {
        this.trackView = trackView;
    }


    @Override
    public List<GPSPoint> parseTrack(String track) {
        InputStream stream = new ByteArrayInputStream(track.getBytes(StandardCharsets.UTF_8));
        List<GPSPoint> result = new ArrayList<GPSPoint>();
        try {
            result = XMLUtils.parse(stream);
        } catch (XmlPullParserException | IOException e) {
            trackView.parseTrackError();
        }
        return result;
    }
}
