package mil.nga.mgrs.app;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.style.Grids;
import mil.nga.mgrs.tile.MGRSTileProvider;

/**
 * README example tests
 *
 * @author osbornb
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class ReadmeTest {


    @Before
    public void launchActivity() {
        ActivityScenario.launch(MapsActivity.class);
    }

    /**
     * Test MGRS
     */
    @Test
    public void testMGRS() {

        Context context = getApplicationContext();

        testTileProvider(context);
        testOptions(MGRSTileProvider.create(context));
        testCustomGrids(context);

    }

    /**
     * Test tile provider
     *
     * @param context app context
     */
    private void testTileProvider(Context context) {

        // Context context = ...;
        // GoogleMap map = ...;

        // Tile size determined from display density
        MGRSTileProvider tileProvider = MGRSTileProvider.create(context);

        // Manually specify tile size
        MGRSTileProvider tileProvider2 = MGRSTileProvider.create(512, 512);

        // GZD only grid
        MGRSTileProvider gzdTileProvider = MGRSTileProvider.createGZD(context);

        // Specified grids
        MGRSTileProvider customTileProvider = MGRSTileProvider.create(context,
                GridType.GZD, GridType.HUNDRED_KILOMETER);

        //map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

    }

    /**
     * Test tile provider options
     *
     * @param tileProvider tile provider
     */
    private void testOptions(MGRSTileProvider tileProvider) {

        int x = 8;
        int y = 12;
        int zoom = 5;

        // Manually get a tile or draw the tile bitmap
        Tile tile = tileProvider.getTile(x, y, zoom);
        Bitmap tileBitmap = tileProvider.drawTile(x, y, zoom);

        double latitude = 63.98862388;
        double longitude = 29.06755082;
        LatLng latLng = new LatLng(latitude, longitude);

        // MGRS Coordinates
        MGRS mgrs = tileProvider.getMGRS(latLng);
        String coordinate = tileProvider.getCoordinate(latLng);
        String zoomCoordinate = tileProvider.getCoordinate(latLng, zoom);

        String mgrsGZD = tileProvider.getCoordinate(latLng, GridType.GZD);
        String mgrs100k = tileProvider.getCoordinate(latLng, GridType.HUNDRED_KILOMETER);
        String mgrs10k = tileProvider.getCoordinate(latLng, GridType.TEN_KILOMETER);
        String mgrs1k = tileProvider.getCoordinate(latLng, GridType.KILOMETER);
        String mgrs100m = tileProvider.getCoordinate(latLng, GridType.HUNDRED_METER);
        String mgrs10m = tileProvider.getCoordinate(latLng, GridType.TEN_METER);
        String mgrs1m = tileProvider.getCoordinate(latLng, GridType.METER);

    }

    /**
     * Test custom grids
     *
     * @param context app context
     */
    private void testCustomGrids(Context context) {

        Grids grids = Grids.create();

        grids.setColor(GridType.GZD, Color.red());
        grids.setWidth(GridType.GZD, 5.0);

        grids.setLabelMinZoom(GridType.GZD, 3);
        grids.setLabelMaxZoom(GridType.GZD, 8);
        grids.setLabelTextSize(GridType.GZD, 32.0);

        grids.setMinZoom(GridType.HUNDRED_KILOMETER, 4);
        grids.setMaxZoom(GridType.HUNDRED_KILOMETER, 8);
        grids.setColor(GridType.HUNDRED_KILOMETER, Color.blue());

        grids.setLabelColor(GridType.HUNDRED_KILOMETER, Color.orange());
        grids.setLabelBuffer(GridType.HUNDRED_KILOMETER, 0.1);
        grids.getLabelPaint(GridType.HUNDRED_KILOMETER).setTypeface(Typeface.DEFAULT_BOLD);

        grids.setColor(Color.darkGray(), GridType.TEN_KILOMETER, GridType.KILOMETER,
                GridType.HUNDRED_METER, GridType.TEN_METER);

        grids.disable(GridType.METER);

        grids.enableLabeler(GridType.TEN_KILOMETER);

        MGRSTileProvider tileProvider = MGRSTileProvider.create(context, grids);

    }

}
