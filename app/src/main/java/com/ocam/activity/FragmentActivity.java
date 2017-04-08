package com.ocam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.track.TrackActivity;
import com.ocam.model.Activity;

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
    private Activity activity;
    private ActivityPresenter activityPresenter;
    private LinearLayout guiasLayout;
    private Button btComenzar;

    public FragmentActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.activityPresenter = new ActivityPresenterImpl();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_activity, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        this.guiasLayout = (LinearLayout) v.findViewById(R.id.linearGuia);
        this.btComenzar = (Button) v.findViewById(R.id.btComenzar);
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
        if (this.activityPresenter.isUserGuide(this.activity)) {
            this.guiasLayout.setVisibility(View.VISIBLE);
            if (!this.activityPresenter.assertActivityRunning(activity)) {
                this.btComenzar.setEnabled(Boolean.TRUE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_track:
                mostrarTrack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
