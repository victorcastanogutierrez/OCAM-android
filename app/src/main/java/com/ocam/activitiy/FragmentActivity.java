package com.ocam.activitiy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.model.Activity;
import com.ocam.util.DateUtils;

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

    public FragmentActivity() {

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
        Bundle args = getArguments();
        setUpActivityData(new Gson().fromJson(args.getString("activity"), Activity.class));
        return v;
    }

    /**
     * Expone los datos de la actividad en los label correspondientes
     * @param activity
     */
    private void setUpActivityData(Activity activity) {
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
            this.txPlazas.setText("Plazas máximas: "+activity.getMaxPlaces().toString());
            this.txMide.setVisibility(View.VISIBLE);
        }
    }
}
