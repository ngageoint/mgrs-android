package mil.nga.mgrs.app;

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

import mil.nga.mgrs.grid.style.Grids;
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
     * WGS84 coordinate label
     */
    private TextView wgs84Label;

    /**
     * Zoom label
     */
    private TextView zoomLabel;

    /**
     * Coordinate label formatter
     */
    private DecimalFormat coordinateFormatter = new DecimalFormat("0.#####");

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
        wgs84Label = (TextView) findViewById(R.id.wgs84);
        zoomLabel = (TextView) findViewById(R.id.zoom);
        zoomFormatter.setRoundingMode(RoundingMode.DOWN);

        tileProvider = MGRSTileProvider.create(this);
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
        wgs84Label.setText(coordinateFormatter.format(center.longitude)
                + "," + coordinateFormatter.format(center.latitude));
        zoomLabel.setText(zoomFormatter.format(zoom));
    }

}
