package com.ocam.activity.monitorization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ocam.R;
import com.ocam.manager.UserManager;
import com.ocam.model.ReportDTO;
import com.ocam.model.types.GPSPoint;
import com.ocam.osm.MapTypeSelector;
import com.ocam.osm.MarkerOverlay;
import com.ocam.util.ConnectionUtils;
import com.ocam.util.ViewUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Activity para la vista de la monitorización de una actividad
 */
public class MonitorizationActivity extends AppCompatActivity implements MonitorizacionView, HikerClickListener{

    private Long activityId;
    private RecyclerView recyclerView;
    private MonitorizacionPresenter monitorizationPresenter;
    private List<GPSPoint> puntos;
    private HikerAdapter hikerAdapter;
    private Map<String, Marker> markers;
    private SwipeRefreshLayout refreshLayout;
    private Map<Integer, Float> colorMap;
    private TextView lbReportesEnviados;
    private TextView lbReportesEncolados;
    private static final Integer PERMISSIONS_CODE = 123;
    private MapView mMapView;
    private String[] permissionsNeeded = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private Boolean cached = Boolean.FALSE;

    public MonitorizationActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorization);
        setUpSwipeRefresh();
        setUpRecyclerData();
        this.markers = new HashMap<>();
        this.activityId = getActivityParameter();
        this.lbReportesEnviados = (TextView) findViewById(R.id.lbReportesEnviados);
        this.lbReportesEncolados = (TextView) findViewById(R.id.lbReportesEncolados);
        this.monitorizationPresenter = new MonitorizacionPresenterImpl(MonitorizationActivity.this, this);
        setUpColorMap();
        setUpToolbar();
        if (ConnectionUtils.isConnected(MonitorizationActivity.this)) {
            this.monitorizationPresenter.loadActivityData(activityId);
        } else {
            this.monitorizationPresenter.loadReportsData(this.activityId);
            ViewUtils.showToast(MonitorizationActivity.this, Toast.LENGTH_LONG,
                    MonitorizationActivity.this.getResources().getString(R.string.noConnectionWarning));
        }
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

            MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), mMapView);
            mLocationOverlay.enableMyLocation();
            mMapView.getOverlays().add(mLocationOverlay);
            CompassOverlay mCompassOverlay = new CompassOverlay(getApplicationContext(),
                    new InternalCompassOrientationProvider(getApplicationContext()), mMapView);
            mCompassOverlay.enableCompass();
            mMapView.getOverlays().add(mCompassOverlay);
            mMapView.invalidate();

            addStartFinishMarkers();
            mMapView.getOverlayManager().add(getPolylineOptions());
        } else {
            onBackPressed();
            ViewUtils.showToast(getApplicationContext(), Toast.LENGTH_LONG, "Necesitamos los permisos necesarios para mostrar el mapa");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tiposMapa:
                MapTypeSelector.show(MonitorizationActivity.this, this.mMapView);
                return true;
            case R.id.refreshHikers:
                this.monitorizationPresenter.loadReportsData(this.activityId);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_monitorizacion, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayProgress() {
        this.refreshLayout.setRefreshing(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideProgress() {
        this.refreshLayout.setRefreshing(false);
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

        ActivityCompat.requestPermissions(MonitorizationActivity.this, permissionsNeeded, PERMISSIONS_CODE);

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
     * Añade dos markers al mapa, uno al principio y otro al final del track
     */
    private void addStartFinishMarkers() {
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
        OverlayItem pto = new OverlayItem("Track", "Inicio", new GeoPoint(this.puntos.get(0).getLatitude(),
                this.puntos.get(0).getLongitude()));
        overlayItemArray.add(pto);
        pto = new OverlayItem("Track", "Fin", new GeoPoint(this.puntos.get(this.puntos.size()-1).getLatitude(),
                this.puntos.get(this.puntos.size()-1).getLongitude()));
        overlayItemArray.add(pto);

        MarkerOverlay overlay = new MarkerOverlay(this, overlayItemArray);
        mMapView.getOverlays().add(overlay);
        mMapView.getController().setCenter(new GeoPoint(this.puntos.get(0).getLatitude(),
                this.puntos.get(0).getLongitude()));
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
    public void refreshHikersData(List<ReportDTO> datos) {
        setRandomColors(datos);
        this.hikerAdapter.setData(getLastReportHiker(datos));
        this.hikerAdapter.notifyDataSetChanged();
        for (Map.Entry<String, Marker> entry : this.markers.entrySet()) {
            entry.getValue().remove();
        }
        this.markers.clear();
        if (ConnectionUtils.isConnected(MonitorizationActivity.this) && this.cached.equals(Boolean.FALSE)) {
            monitorizationPresenter.saveLocalData(activityId, datos);
            cached = Boolean.TRUE;
        }
        setUpResumen(datos);
    }

    /**
     * Obtiene una lista con el ultiumo report de cada hiker, es decir,
     * agrupa los reports por hiker quedandose siempre con el de fecha mas actual
     * @param datos
     * @return
     */
    private List<ReportDTO> getLastReportHiker(List<ReportDTO> datos) {
        Map<String, ReportDTO> reports = new HashMap<>();
        for (ReportDTO reportDTO : datos) {
            if (!reports.containsKey(reportDTO.getHikerDTO().getLogin())) {
                reports.put(reportDTO.getHikerDTO().getLogin(), reportDTO);
            } else {
                if (reports.get(reportDTO.getHikerDTO().getLogin()).getDate() < reportDTO.getDate()) {
                    reports.put(reportDTO.getHikerDTO().getLogin(), reportDTO);
                }
            }
        }
        return new ArrayList<>(reports.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(ReportDTO reportDTO) {
        String login = reportDTO.getHikerDTO().getLogin();
        if (!this.markers.containsKey(login)) {
            //TODO
            /*LatLng markerPos = new LatLng(
                    reportDTO.getPoint().getLatitude(),
                    reportDTO.getPoint().getLongitude());
            this.markers.put(login, this.mMap.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(colorMap.get(reportDTO.getColor())))
                    .title(login + ": " + reportDTO.getPoint().getLatitude() + "," +
                            reportDTO.getPoint().getLongitude())));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerPos)
                    .zoom(18)
                    .build();
            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        } else {
            /*this.markers.get(login).remove();
            this.markers.remove(login);*/
        }
    }

    /**
     * Instancia el recyclerView sin datos
     */
    private void setUpRecyclerData() {
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerHikers);
        this.recyclerView.setHasFixedSize(true);
        this.hikerAdapter = new HikerAdapter(new ArrayList<ReportDTO>(), this);

        this.recyclerView.setAdapter(this.hikerAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(MonitorizationActivity.this,LinearLayoutManager.VERTICAL,false));

        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Configura el SwiftRefreshLayout
     */
    private void setUpSwipeRefresh() {
        this.refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshMonitorization);
        this.refreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent);
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                monitorizationPresenter.loadReportsData(activityId);
            }
        });
    }

    private void setUpColorMap() {
        this.colorMap = new HashMap<>();
        this.colorMap.put(Color.parseColor("#FF0000"), BitmapDescriptorFactory.HUE_RED);
        this.colorMap.put(Color.parseColor("#FFA500"), BitmapDescriptorFactory.HUE_ORANGE);
        this.colorMap.put(Color.parseColor("#FFFF00"), BitmapDescriptorFactory.HUE_YELLOW);
        this.colorMap.put(Color.parseColor("#00FF00"), BitmapDescriptorFactory.HUE_GREEN);
        this.colorMap.put(Color.parseColor("#00FFFF"), BitmapDescriptorFactory.HUE_CYAN);
        this.colorMap.put(Color.parseColor("#F0FFFF"), BitmapDescriptorFactory.HUE_AZURE);
        this.colorMap.put(Color.parseColor("#0000FF"), BitmapDescriptorFactory.HUE_BLUE);
        this.colorMap.put(Color.parseColor("#EE82EE"), BitmapDescriptorFactory.HUE_VIOLET);
        this.colorMap.put(Color.parseColor("#FF00FF"), BitmapDescriptorFactory.HUE_MAGENTA);
        this.colorMap.put(Color.parseColor("#FFC0CB"), BitmapDescriptorFactory.HUE_ROSE);
    }

    private void setRandomColors(List<ReportDTO> datos) {
        Random rand = new Random();
        Map<Integer, Float> copy = new HashMap<>(this.colorMap);
        for (ReportDTO report : datos) {
            if (copy.size() == 0) {
                copy = new HashMap<>(this.colorMap);
            }
            Integer random = rand.nextInt(copy.size());
            Integer color = new ArrayList<>(copy.keySet()).get(random);
            copy.remove(color);
            report.setColor(color);
        }
    }

    /**
     * Muestra los datos de reportes enviados y encolados
     */
    private void setUpResumen(List<ReportDTO> datos) {
        int reportesEnviados = 0;
        String ulogged = UserManager.getInstance().getUserTokenDTO().getLogin();
        for (ReportDTO report : datos) {
            if (report.getHikerDTO().getLogin().equals(ulogged)) {
                reportesEnviados++;
            }
        }
        this.lbReportesEnviados.setText("Reportes enviados: "+reportesEnviados);
        this.lbReportesEncolados.setText("Reportes encolados: "+this.monitorizationPresenter.getReportesEncolados()+"");
    }
}
