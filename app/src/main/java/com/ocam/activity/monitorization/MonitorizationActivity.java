package com.ocam.activity.monitorization;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.ocam.activity.track.TrackActivity;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.ViewUtils;

import java.util.List;

/**
 * Activity para la vista de la monitorización de una actividad
 */
public class MonitorizationActivity extends AppCompatActivity implements MonitorizacionView, OnMapReadyCallback {

    private Long activityId;
    private RecyclerView recyclerView;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private MonitorizacionPresenter monitorizationPresenter;
    private List<GPSPoint> puntos;
    private GoogleMap mMap;

    public MonitorizationActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorization);
        this.activityId = getActivityParameter();
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(MonitorizationActivity.this, android.R.style.Theme_Panel);
        this.monitorizationPresenter = new MonitorizacionPresenterImpl(MonitorizationActivity.this, this);
        setUpToolbar();
        this.monitorizationPresenter.loadHikersData(activityId);
    }

    /**
     * Método ejecutado cuando el mapa está listo
     * @param googleMap
     */
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
     * {@inheritDoc}
     */
    @Override
    public void setUpRecyclerView() {

        /*this.recyclerView = (RecyclerView) getView().findViewById(R.id.reciclerView);
        this.recyclerView.setHasFixedSize(true);
        this.adapter = new ActivityAdapter(datos);

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        this.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityDetail(adapter.getData().get(recyclerView.getChildAdapterPosition(v)));
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_monitorizacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tiposMapa:
                ViewUtils.showMapTypeSelectorDialog(MonitorizationActivity.this, mMap);
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
    public void notifyText(String notification) {
        ViewUtils.showToast(MonitorizationActivity.this, Toast.LENGTH_SHORT, notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showTrack(List<GPSPoint> puntos) {
        this.puntos = puntos;

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    /**
     * Obtiene la ID de la actividad pasada por parámetro al fragment
     * @return
     */
    private Long getActivityParameter() {
        return getIntent().getLongExtra("activityId", -1);
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

    /**
     * Muestra la toolbar de la actividad
     */
    private void setUpToolbar() {
        // Configuración de toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.monitorizationToolbar);
        toolbar.setTitle("Monitorización");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
