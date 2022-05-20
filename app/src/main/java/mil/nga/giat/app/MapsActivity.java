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

import mil.nga.mgrs.app.R;
import mil.nga.mgrs.tile.MGRSTileProvider;

/**
 * MGRS Example Application
 *
 * @author wnewman
 * @author osbornb
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    /**
     * Google map
     */
    private GoogleMap map;

    /**
     * MGRS label
     */
    private TextView mgrsLabel;

    /**
     * Zoom label
     */
    private TextView zoomLabel;

    /**
     * Zoom level label formatter
     */
    private DecimalFormat zoomFormatter = new DecimalFormat("0.0");

    /**
     * MGRS tile provider
     */
    private MGRSTileProvider tileProvider = null;

    /**
     * {@inheritDoc}
     */
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

        int tileWidth = getResources().getInteger(R.integer.tile_width);
        int tileHeight = getResources().getInteger(R.integer.tile_height);

        tileProvider = MGRSTileProvider.create(tileWidth, tileHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        map.setOnCameraIdleListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = map.getCameraPosition();
        LatLng center = cameraPosition.target;
        float zoom = cameraPosition.zoom;
        mgrsLabel.setText(tileProvider.getCoordinate(center, (int) zoom));
        zoomLabel.setText(zoomFormatter.format(zoom));
    }

}
