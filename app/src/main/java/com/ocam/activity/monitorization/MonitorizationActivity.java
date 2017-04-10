package com.ocam.activity.monitorization;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ocam.R;
import com.ocam.model.Hiker;
import com.ocam.model.Report;
import com.ocam.model.types.GPSPoint;
import com.ocam.util.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;

/**
 * Activity para la vista de la monitorización de una actividad
 */
public class MonitorizationActivity extends AppCompatActivity implements MonitorizacionView, OnMapReadyCallback, HikerClickListener{

    private Long activityId;
    private RecyclerView recyclerView;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private MonitorizacionPresenter monitorizationPresenter;
    private List<GPSPoint> puntos;
    private GoogleMap mMap;
    private HikerAdapter hikerAdapter;
    private Map<String, Marker> markers;

    public MonitorizationActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.markers = new HashMap<>();
        setContentView(R.layout.activity_monitorization);
        this.activityId = getActivityParameter();
        this.mProgress = (ProgressBar) findViewById(R.id.progressBar);
        this.mOverlayDialog = new Dialog(MonitorizationActivity.this, android.R.style.Theme_Panel);
        this.monitorizationPresenter = new MonitorizacionPresenterImpl(MonitorizationActivity.this, this);
        setUpToolbar();
        this.monitorizationPresenter.loadActivityData(activityId);
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

        //Una vez mostrado el track descargamos los datos de los hikers
        this.monitorizationPresenter.loadReportsData(this.activityId);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUpRecyclerView(List<Report> datos) {

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerHikers);
        this.recyclerView.setHasFixedSize(true);
        this.hikerAdapter = new HikerAdapter(datos, this);

        this.recyclerView.setAdapter(this.hikerAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(MonitorizationActivity.this,LinearLayoutManager.VERTICAL,false));

        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(Report report) {
        String login = report.getHiker().getLogin();
        if (!this.markers.containsKey(login)) {
            LatLng markerPos = new LatLng(
                    report.getPoint().getLatitude(),
                    report.getPoint().getLongitude());
            this.markers.put(login, this.mMap.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(login)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerPos)      // Sets the center of the map to Mountain View
                    .zoom(30)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            this.markers.get(login).remove();
            this.markers.remove(login);
        }
    }
}
