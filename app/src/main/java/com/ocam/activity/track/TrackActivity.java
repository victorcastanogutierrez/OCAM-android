package com.ocam.activity.track;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ocam.R;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.ViewUtils;

import java.util.List;

import static com.ocam.R.id.map;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback, TrackView {

    private GoogleMap mMap;
    private TrackPresenter trackPresenter;
    private List<GPSPoint> puntos;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private static final Integer PERMISSIONS_CODE = 123;

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        // Configuración de toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trackToolbar);
        toolbar.setTitle("Track de la ruta");
        toolbar.getBackground().setAlpha(180);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(TrackActivity.this, android.R.style.Theme_Panel);

        trackPresenter = new TrackPresenterImpl(this, TrackActivity.this);

        displayProgress();
        Bundle extras = getIntent().getExtras();
        this.trackPresenter.getActivityTrack(extras.getLong("activityId"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_track, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        LatLngBounds.Builder b = new LatLngBounds.Builder();
        PolylineOptions polylineOptions = getPolylineOptions(b);
        LatLngBounds bounds = b.build();
        this.mMap.addPolyline(polylineOptions);
        this.addStartFinishMarkers();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
    }

    /**
     * Muestra la barra de progreso e impide la interacción con cualquier elemento de la vista
     */
    private void displayProgress() {
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
        ViewUtils.showToast(TrackActivity.this, Toast.LENGTH_SHORT, error);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void showTrack(List<GPSPoint> puntos) {
        this.puntos = puntos;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Genera un objeto PolyLineOptions para construir la polylinea en el mapa
     * con el track de la ruta
     * @return
     */
    private PolylineOptions getPolylineOptions(LatLngBounds.Builder bounds) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(6)
                .color(Color.RED);

        LatLng latlng = null;
        for (GPSPoint gps: this.puntos) {
            latlng = new LatLng(gps.getLatitude(), gps.getLongitude());
            polylineOptions.add(latlng);
            bounds.include(latlng);
        }
        return polylineOptions;
    }

    /**
     * Añade dos markers al mapa, uno al principio y otro al final del track
     */
    private void addStartFinishMarkers() {
        this.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        this.puntos.get(0).getLatitude(),
                        this.puntos.get(0).getLongitude()))
                .title("Inicio"));

        this.mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        this.puntos.get(this.puntos.size()-1).getLatitude(),
                        this.puntos.get(this.puntos.size()-1).getLongitude()))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Fin"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trackMapType:
                showMapTypeSelectorDialog();
                return true;
            case android.R.id.home:
                //Click en back button de la toolbar (cerramos la actividad)
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método obtenido de documentación para seleccionar el tipo de mapa
     * entre los permitidos de Google Maps
     */
    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
}
