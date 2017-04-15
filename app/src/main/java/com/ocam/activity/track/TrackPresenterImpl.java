package com.ocam.activity.track;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ocam.volley.VolleyManager;
import com.ocam.model.types.GPSPoint;
import com.ocam.model.types.Track;
import com.ocam.util.Constants;
import com.ocam.util.XMLUtils;
import com.ocam.volley.GsonRequest;
import com.ocam.volley.listeners.GenericErrorListener;
import com.ocam.volley.listeners.GenericResponseListener;
import com.ocam.volley.listeners.ICommand;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TrackPresenterImpl implements TrackPresenter {

    private TrackView trackView;
    private Context context;

    public TrackPresenterImpl(TrackView trackView, Context context) {
        this.trackView = trackView;
        this.context = context;
    }

    @Override
    public void getActivityTrack(Long activityId) {

        ICommand<Track> myCommand = new MyCommand();
        GsonRequest<Track> request = new GsonRequest<Track>(Constants.API_FIND_ACTIVITY + "/" + activityId,
                Request.Method.GET, Track.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(request);
    }

    private List<GPSPoint> parseTrack(String track) {
        InputStream stream = new ByteArrayInputStream(track.getBytes(StandardCharsets.UTF_8));
        List<GPSPoint> result = new ArrayList<GPSPoint>();
        try {
            result = XMLUtils.parse(stream);
        } catch (XmlPullParserException | IOException e) {
            trackView.showError("Error procesando el track de la ruta");
        }
        return result;
    }

    private class MyCommand implements ICommand<Track> {

        @Override
        public void executeResponse(Track response) {
            List<GPSPoint> track = parseTrack(response.getTrack());
            trackView.hideProgress();
            trackView.showTrack(track);
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            trackView.showError("Error obteniendo el track de la ruta");
            trackView.hideProgress();
        }
    }
}
