package com.ocam.activity.monitorization;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ocam.manager.VolleyManager;
import com.ocam.model.Hiker;
import com.ocam.model.Report;
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
import java.util.Arrays;
import java.util.List;

public class MonitorizacionPresenterImpl implements MonitorizacionPresenter {

    private Context context;
    private MonitorizacionView monitorizacionView;
    private List<Report> reports;

    public MonitorizacionPresenterImpl(Context context, MonitorizacionView monitorizacionView) {
        this.context = context;
        this.monitorizacionView = monitorizacionView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadActivityData(Long activityId) {
        monitorizacionView.displayProgress();
        ICommand<Track> myCommand = new trackCommand();
        GsonRequest<Track> trackRequest = new GsonRequest<Track>(Constants.API_FIND_ACTIVITY + "/" + activityId,
                Request.Method.GET, Track.class, null,
                new GenericResponseListener<>(myCommand), new GenericErrorListener(myCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(trackRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadReportsData(Long activityId) {
        monitorizacionView.displayProgress();
        ICommand<Report[]> reportsCommand = new reportsCommand();
        GsonRequest<Report[]> hikersRequest = new GsonRequest<Report[]>(Constants.API_FIND_ACTIVITY_REPORTS + "/" + activityId,
                Request.Method.GET, Report[].class, null,
                new GenericResponseListener<>(reportsCommand), new GenericErrorListener(reportsCommand));

        VolleyManager.getInstance(this.context).addToRequestQueue(hikersRequest);
    }

    private List<GPSPoint> parseTrack(String track) {
        InputStream stream = new ByteArrayInputStream(track.getBytes(StandardCharsets.UTF_8));
        List<GPSPoint> result = new ArrayList<GPSPoint>();
        try {
            result = XMLUtils.parse(stream);
        } catch (XmlPullParserException | IOException e) {
            monitorizacionView.notifyText("Error procesando el track de la ruta");
        }
        return result;
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para obtener el track de la ruta
     */
    private class trackCommand implements ICommand<Track> {

        @Override
        public void executeResponse(Track response) {
            List<GPSPoint> track = parseTrack(response.getTrack());
            monitorizacionView.hideProgress();
            monitorizacionView.showTrack(track);
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            monitorizacionView.notifyText("Error obteniendo el track de la ruta");
        }
    }

    /**
     * Command genérico para manejar la respuesta HTTP a la llamada a la API del servidor
     * para obtener los reportes de los hikers de la actividad
     */
    private class reportsCommand implements ICommand<Report[]> {

        @Override
        public void executeResponse(Report[] response) {
            monitorizacionView.hideProgress();
            reports = Arrays.asList(response);
            monitorizacionView.setUpRecyclerView(reports);
        }

        /**
         * Muestra el error retornado por el servidor al usuario
         * @param error
         */
        @Override
        public void executeError(VolleyError error) {
            Log.d("Error", error.getMessage()+"");
            monitorizacionView.notifyText("Error obteniendo los hikers de la actividad");
            monitorizacionView.hideProgress();
        }
    }
}
