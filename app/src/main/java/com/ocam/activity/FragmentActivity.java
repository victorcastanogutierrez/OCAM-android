package com.ocam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.track.TrackActivity;
import com.ocam.model.Activity;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.ViewUtils;
import com.ocam.util.XMLUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.ocam.util.DateUtils.formatDate;

/**
 * Fragment para la vista del detalle de una actividad
 */
public class FragmentActivity extends Fragment {

    private TextView txDescripcion;
    private TextView txFecha;
    private TextView txOrganiza;
    private TextView txDetalle;
    private TextView txMide;
    private TextView txPlazas;
    private TextView txEstado;
    private TextView txTrack;
    private Activity activity;

    public FragmentActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Eliminamos la opción de recarga
     * @param menu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_detail, container, false);
        this.txDescripcion = (TextView) v.findViewById(R.id.lbDescripcion);
        this.txFecha = (TextView) v.findViewById(R.id.lbFecha);
        this.txOrganiza = (TextView) v.findViewById(R.id.lbOrganiza);
        this.txDetalle = (TextView) v.findViewById(R.id.lbDetalle);
        this.txMide = (TextView) v.findViewById(R.id.lbMide);
        this.txPlazas = (TextView) v.findViewById(R.id.lbPlazas);
        this.txEstado = (TextView) v.findViewById(R.id.lbEstado);
        this.txTrack = (TextView) v.findViewById(R.id.lbTrackText);
        this.txTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTrack();
            }
        });
        Bundle args = getArguments();
        setUpActivityData(new Gson().fromJson(args.getString("activity"), Activity.class));
        return v;
    }

    /**
     * Expone los datos de la actividad en los label correspondientes
     * @param activity
     */
    private void setUpActivityData(Activity activity) {
        this.activity = activity;
        this.txEstado.setText(activity.getFormattedStatus());
        this.txDescripcion.setText(activity.getShortDescription());
        this.txFecha.setText(formatDate(activity.getStartDate()));
        this.txOrganiza.setText(activity.getOwner().getEmail());
        if (activity.getLongDescription() != null) {
            this.txDetalle.setText(activity.getLongDescription());
            this.txDetalle.setVisibility(View.VISIBLE);
        }
        if (activity.getMide() != null) {
            this.txMide.setText("Enlace a detalle: "+activity.getMide());
            this.txMide.setVisibility(View.VISIBLE);
        }
        if (activity.getMaxPlaces() != null) {
            this.txPlazas.setText(activity.getMaxPlaces().toString()+" plazas máximas");
            this.txPlazas.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Método onClick para ver el track de la ruta
     */
    public void mostrarTrack() {
        Intent i = new Intent(getContext(), TrackActivity.class);
        i.putExtra("track", this.activity.getTrack());
        startActivity(i);
    }
}
