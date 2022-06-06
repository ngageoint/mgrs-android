package mil.nga.mgrs.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import mil.nga.mgrs.tile.MGRSTileProvider;

/**
 * MGRS Example Application
 *
 * @author wnewman
 * @author osbornb
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener {

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
     * Map type button
     */
    private ImageButton mapTypeButton;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mgrsLabel = (TextView) findViewById(R.id.mgrs);
        wgs84Label = (TextView) findViewById(R.id.wgs84);
        zoomLabel = (TextView) findViewById(R.id.zoom);
        zoomFormatter.setRoundingMode(RoundingMode.DOWN);
        mapTypeButton = (ImageButton) findViewById(R.id.mapType);
        mapTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapTypeClick(v);
            }
        });

        tileProvider = MGRSTileProvider.create(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        map.setOnCameraIdleListener(this);
        map.setOnMapClickListener(this);
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
        wgs84Label.setText(getString(R.string.wgs84_label,
                coordinateFormatter.format(center.longitude),
                coordinateFormatter.format(center.latitude)));
        zoomLabel.setText(zoomFormatter.format(zoom));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapClick(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Handle map type click
     *
     * @param v view
     */
    private void onMapTypeClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.map_type_title));

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                new CharSequence[]{
                        getString(R.string.map_type_normal),
                        getString(R.string.map_type_satellite),
                        getString(R.string.map_type_terrain),
                        getString(R.string.map_type_hybrid)},
                map.getMapType() - 1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        map.setMapType(item + 1);
                        dialog.dismiss();
                    }
                }
        );

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

}
