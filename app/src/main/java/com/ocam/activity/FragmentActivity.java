package com.ocam.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ocam.R;
import com.ocam.activity.track.TrackActivity;
import com.ocam.model.Activity;
import com.ocam.util.ViewUtils;

import static com.ocam.util.DateUtils.formatDate;

/**
 * Fragment para la vista del detalle de una actividad
 */
public class FragmentActivity extends Fragment implements ActivityView {

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
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;

    public FragmentActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        this.activityPresenter = new ActivityPresenterImpl(this, v.getContext());
        this.txDescripcion = (TextView) v.findViewById(R.id.lbDescripcion);
        this.txFecha = (TextView) v.findViewById(R.id.lbFecha);
        this.txOrganiza = (TextView) v.findViewById(R.id.lbOrganiza);
        this.txDetalle = (TextView) v.findViewById(R.id.lbDetalle);
        this.txMide = (TextView) v.findViewById(R.id.lbMide);
        this.txPlazas = (TextView) v.findViewById(R.id.lbPlazas);
        this.txEstado = (TextView) v.findViewById(R.id.lbEstado);
        this.guiasLayout = (LinearLayout) v.findViewById(R.id.linearGuia);
        this.btComenzar = (Button) v.findViewById(R.id.btComenzar);
        this.mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(v.getContext(), android.R.style.Theme_Panel);
        Bundle args = getArguments();
        setUpActivityData(new Gson().fromJson(args.getString("activity"), Activity.class));
        btComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });
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

    /**
     * Método que muestra el dialog de confirmacion de iniciar actividad
     * con la solicitud de la password al usuario
     */
    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setTitle("Introduce una password para la actividad");

        final EditText input = new EditText(getView().getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pw = input.getText().toString();
                if (assertPasswordValid(pw)) {
                    activityPresenter.startActivity(activity.getId(), input.getText().toString());
                } else {
                    showError("Debes introducir una password");
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private Boolean assertPasswordValid(String password) {
        return password != null && !password.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayProgress() {
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();
        this.mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        mOverlayDialog.cancel();
        this.mProgress.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(String error) {
        ViewUtils.showToast(getView().getContext(), Toast.LENGTH_SHORT, error);
    }
}
