package mil.nga.giat.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.app.R;
import mil.nga.mgrs.tile.MGRSTileProvider;
import mil.nga.mgrs.tile.TileUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private GoogleMap map;
    private TextView mgrsLabel;
    private TextView zoomLabel;
    private DecimalFormat zoomFormatter = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mgrsLabel = (TextView) findViewById(R.id.mgrs);
        zoomLabel = (TextView) findViewById(R.id.zoom);
        zoomFormatter.setRoundingMode(RoundingMode.DOWN);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        int tileWidth = getResources().getInteger(R.integer.tile_width);
        int tileHeight = getResources().getInteger(R.integer.tile_height);

        MGRSTileProvider tileProvider = MGRSTileProvider.create(tileWidth, tileHeight);

        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        map.setOnCameraIdleListener(this);
    }

    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = map.getCameraPosition();
        LatLng center = cameraPosition.target;
        MGRS mgrs = MGRS.from(TileUtils.toPoint(center));
        mgrsLabel.setText(mgrs.toString());
        zoomLabel.setText(zoomFormatter.format(cameraPosition.zoom));
    }
}
