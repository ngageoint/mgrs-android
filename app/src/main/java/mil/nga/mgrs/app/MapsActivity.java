package mil.nga.mgrs.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.style.Grid;
import mil.nga.mgrs.grid.style.Grids;
import mil.nga.mgrs.tile.MGRSTileProvider;
import mil.nga.mgrs.tile.TileUtils;
import mil.nga.mgrs.utm.UTM;

/**
 * MGRS Example Application
 *
 * @author wnewman
 * @author osbornb
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener {

    /**
     * Location permission request code
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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
     * Search button
     */
    private ImageButton searchButton;

    /**
     * Search MGRS result
     */
    private String searchMGRSResult = null;

    /**
     * Map type button
     */
    private ImageButton mapTypeButton;

    /**
     * Coordinate label formatter
     */
    private DecimalFormat coordinateFormatter = new DecimalFormat("0.0####");

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
        searchButton = (ImageButton) findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClick(v);
            }
        });
        mapTypeButton = (ImageButton) findViewById(R.id.mapType);
        mapTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapTypeClick(v);
            }
        });
        mgrsLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(getString(R.string.mgrs_label), mgrsLabel.getText());
                return true;
            }
        });
        wgs84Label.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(getString(R.string.wgs84_label), wgs84Label.getText());
                return true;
            }
        });
        zoomLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(getString(R.string.zoom_label), zoomLabel.getText());
                return true;
            }
        });
        coordinateFormatter.setRoundingMode(RoundingMode.HALF_UP);

        Grids grids = Grids.create();
        grids.setLabelMinZoom(GridType.GZD, 3);

        tileProvider = MGRSTileProvider.create(this, grids);
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
        enableMyLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCameraIdle() {
        CameraPosition cameraPosition = map.getCameraPosition();
        LatLng center = cameraPosition.target;
        float zoom = cameraPosition.zoom;
        String mgrs = null;
        if (searchMGRSResult != null) {
            mgrs = searchMGRSResult;
            searchMGRSResult = null;
        } else {
            mgrs = tileProvider.getCoordinate(center, (int) zoom);
        }
        mgrsLabel.setText(mgrs);
        wgs84Label.setText(getString(R.string.wgs84_label_format,
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
        builder.setTitle(R.string.map_type_title);

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

    /**
     * Copy text to the clipboard
     *
     * @param label label
     * @param text  text
     */
    private void copyToClipboard(String label, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), getString(R.string.copied_message, label),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle search click
     *
     * @param v view
     */
    private void onSearchClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.search_title);
        final EditText input = new EditText(this);
        input.setSingleLine();
        builder.setView(input);

        builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                search(input.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Search and move to the coordiante
     *
     * @param coordinate MGRS, UTM, or WGS84 coordinate
     */
    private void search(String coordinate) {
        searchMGRSResult = null;
        Point point = null;
        Integer zoom = null;
        float currentZoom = map.getCameraPosition().zoom;
        try {
            coordinate = coordinate.trim();
            if (MGRS.isMGRS(coordinate)) {
                MGRS mgrs = MGRS.parse(coordinate);
                GridType gridType = MGRS.precision(coordinate);
                if (gridType == GridType.GZD) {
                    point = mgrs.getGridZone().getBounds().getSouthwest();
                } else {
                    point = mgrs.toPoint();
                }
                searchMGRSResult = coordinate.toUpperCase();
                zoom = mgrsCoordinateZoom(gridType, currentZoom);
            } else if (UTM.isUTM(coordinate)) {
                point = UTM.parse(coordinate).toPoint();
            } else {
                String[] parts = coordinate.split("\\s*,\\s*");
                if (parts.length == 2) {
                    point = Point.create(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                }
            }
        } catch (Exception e) {
            Log.e(MapsActivity.class.getSimpleName(),
                    "Unsupported coordinate: " + coordinate, e);
        }
        if (point != null) {
            LatLng latLng = TileUtils.toLatLng(point);
            if (searchMGRSResult == null) {
                searchMGRSResult = tileProvider.getCoordinate(latLng, (int) currentZoom);
            }
            CameraUpdate update = null;
            if (zoom != null) {
                update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            } else {
                update = CameraUpdateFactory.newLatLng(latLng);
            }
            map.animateCamera(update);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.search_error);
            alert.setMessage(coordinate);
            alert.setPositiveButton(R.string.ok, null);
            alert.setCancelable(true);
            alert.create().show();
        }
    }

    /**
     * Get the MGRS coordinate zoom level
     *
     * @param gridType grid type precision
     * @param zoom     current zoom
     * @return zoom level or null
     * @throws ParseException upon failure to parse coordinate
     */
    private Integer mgrsCoordinateZoom(GridType gridType, float zoom) throws ParseException {
        Integer mgrsZoom = null;
        Grid grid = tileProvider.getGrid(gridType);
        int minZoom = grid.getLinesMinZoom();
        if (zoom < minZoom) {
            mgrsZoom = minZoom;
        } else {
            Integer maxZoom;
            if (gridType == GridType.GZD) {
                maxZoom = tileProvider.getGrid(GridType.HUNDRED_KILOMETER).getMinZoom() - 1;
            } else {
                maxZoom = grid.getLinesMaxZoom();
            }
            if (maxZoom != null && zoom >= maxZoom + 1) {
                mgrsZoom = maxZoom;
            }
        }
        return mgrsZoom;
    }

    /**
     * Enables the My Location layer if fine or coarse location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Location permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION) || isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            enableMyLocation();
        }
    }

    /**
     * Is permission granted
     *
     * @param permissions  permissions
     * @param grantResults grant results
     * @param permission   permission
     * @return true if granted
     */
    private static boolean isPermissionGranted(String[] permissions, int[] grantResults,
                                               String permission) {
        for (int i = 0; i < permissions.length; i++) {
            if (permission.equals(permissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

}
