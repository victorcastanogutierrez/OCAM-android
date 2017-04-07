package com.ocam.activity.track;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.ocam.R.id.map;
import static com.ocam.activity.track.TrackUtils.DrawArrowHead;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback, TrackView {

    private GoogleMap mMap;
    private List<GPSPoint> puntos;
    private TrackPresenter trackPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        // Configuración de toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trackToolbar);
        toolbar.setTitle("Track de la ruta");
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        trackPresenter = new TrackPresenterImpl(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.puntos = getTrack();

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
    public void parseTrackError() {
        ViewUtils.showToast(TrackActivity.this, Toast.LENGTH_SHORT, "Error procesando el track de la ruta");
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
        GPSPoint anterior = null;
        for (GPSPoint gps: this.puntos) {
            latlng = new LatLng(gps.getLatitude(), gps.getLongitude());
            polylineOptions.add(latlng);
            bounds.include(latlng);

            if (anterior != null) {
                TrackUtils.DrawArrowHead(TrackActivity.this, mMap,
                        new LatLng(anterior.getLatitude(), anterior.getLongitude()),
                        new LatLng(gps.getLatitude(), gps.getLongitude()));
            }
            anterior = gps;

        }
        return polylineOptions;
    }

    /**
     * Extrae los datos del track de la ruta y los recupera del presentador parseados a objeto
     * GPSPoint
     */
    private List<GPSPoint> getTrack() {
        Bundle extras = getIntent().getExtras();
        return trackPresenter.parseTrack(extras.getString("track"));
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
        //Click en back button de la toolbar (cerramos la actividad)
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
