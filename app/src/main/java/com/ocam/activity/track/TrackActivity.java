package com.ocam.activity.track;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ocam.R;
import com.ocam.model.types.GPSPoint;
import com.ocam.osm.MapTypeSelector;
import com.ocam.osm.MarkerOverlay;
import com.ocam.util.ViewUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity implements TrackView {

    private TrackPresenter trackPresenter;
    private List<GPSPoint> puntos;
    private ProgressBar mProgress;
    private Dialog mOverlayDialog;
    private static final Integer PERMISSIONS_CODE = 123;
    private MapView mMapView;
    private String[] permissionsNeeded = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

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
        ActivityCompat.requestPermissions(TrackActivity.this, permissionsNeeded, PERMISSIONS_CODE);
    }

    /**
     * Genera un objeto Polyline para construir la polylinea en el mapa
     * con el track de la ruta
     * @return
     */
    private Polyline getPolylineOptions() {
        Polyline line = new Polyline();
        line.setWidth(6f);
        List<GeoPoint> pts = new ArrayList<>();
        for (GPSPoint gps: this.puntos) {
            pts.add(new GeoPoint(gps.getLatitude(), gps.getLongitude()));

        }
        line.setPoints(pts);
        line.setGeodesic(true);
        line.setColor(Color.RED);
        return line;
    }

    /**
     * Añade dos markers al mapa, uno al principio y otro al final del track
     */
    private void addStartFinishMarkers() {
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
        OverlayItem pto = new OverlayItem("Track", "Inicio", new GeoPoint(this.puntos.get(0).getLatitude(),
                this.puntos.get(0).getLongitude()));
        pto.setMarker(this.getResources().getDrawable(R.drawable.inicio));
        overlayItemArray.add(pto);
        pto = new OverlayItem("Track", "Fin", new GeoPoint(this.puntos.get(this.puntos.size()-1).getLatitude(),
                this.puntos.get(this.puntos.size()-1).getLongitude()));
        pto.setMarker(this.getResources().getDrawable(R.drawable.fin));
        overlayItemArray.add(pto);

        MarkerOverlay overlay = new MarkerOverlay(this, overlayItemArray);
        mMapView.getOverlays().add(overlay);
        mMapView.getController().setCenter(new GeoPoint(this.puntos.get(0).getLatitude(),
                this.puntos.get(0).getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (isGrantedPermissions(permissions, grantResults) && requestCode == PERMISSIONS_CODE) {
            mMapView = (MapView) findViewById(R.id.map);
            mMapView.setTileSource(TileSourceFactory.MAPNIK);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            IMapController mMapController = mMapView.getController();
            mMapController.setZoom(16);
            GeoPoint gPt = new GeoPoint(51500000, -150000);
            mMapController.setCenter(gPt);

            MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(TrackActivity.this), mMapView);
            mLocationOverlay.enableMyLocation();
            mMapView.getOverlays().add(mLocationOverlay);
            CompassOverlay mCompassOverlay = new CompassOverlay(TrackActivity.this, new InternalCompassOrientationProvider(TrackActivity.this), mMapView);
            mCompassOverlay.enableCompass();
            mMapView.getOverlays().add(mCompassOverlay);
            mMapView.invalidate();

            addStartFinishMarkers();
            mMapView.getOverlayManager().add(getPolylineOptions());
        } else {
            finish();
            ViewUtils.showToast(TrackActivity.this, Toast.LENGTH_LONG, "Necesitamos los permisos necesarios para mostrar el mapa");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trackMapType:
                MapTypeSelector.show(TrackActivity.this, this.mMapView);
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
     * Comprueba que se hayan dado los permisos necesarios para mostrar el mapa
     * @param permissions
     * @param grantResults
     * @return
     */
    private Boolean isGrantedPermissions(String permissions[], int[] grantResults) {
        Boolean result = Boolean.TRUE;
        for (int i : grantResults) {
            if (i != PackageManager.PERMISSION_GRANTED) {
                result = Boolean.FALSE;
                break;
            }
        }
        return result && permissionsNeeded.length == permissions.length;
    }
}
