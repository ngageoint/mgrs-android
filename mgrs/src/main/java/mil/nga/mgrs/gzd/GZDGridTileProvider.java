package mil.nga.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import androidx.core.util.Pair;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mil.nga.mgrs.Line;
import mil.nga.mgrs.R;
import mil.nga.mgrs.TileBoundingBoxUtils;
import mil.nga.mgrs.wgs84.LatLng;

/**
 * Created by wnewman on 11/17/16.
 */
public class GZDGridTileProvider implements TileProvider {

    /**
     * Half the world distance in either direction
     */
    public static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;

    private static int TEXT_SIZE = 16;

    private Context context;
    private int tileWidth;
    private int tileHeight;

    public GZDGridTileProvider(Context context) {
        this.context = context;

        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

        Bitmap bitmap = drawTile(x, y, zoom);

        byte[] bytes = null;
        try {
            bytes = toBytes(bitmap);
        } catch (IOException e) {
            // uhhhh
            Log.e("FOO", "UHH", e);
        }

        Tile tile = new Tile(tileWidth, tileHeight, bytes);

        return tile;


    }

    private Bitmap drawTile(int x, int y, int zoom) {

        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        double[] boundingBox = TileBoundingBoxUtils.getBoundingBox(x, y, zoom);
        double[] webMercatorBoundingBox = TileBoundingBoxUtils.getWebMercatorBoundingBox(x, y, zoom);


        for (GridZoneDesignator gridZone : GZDZones.zonesWithin(boundingBox)) {
            double[] gridBounds = gridZone.zoneBounds();
            Double minLat = gridBounds[1];
            Double maxLat = gridBounds[3];

            Double minLon = gridBounds[0];
            Double maxLon = gridBounds[2];

            drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(maxLat, minLon), new LatLng(maxLat, maxLon)), Color.RED);
            drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(minLat, maxLon), new LatLng(maxLat, maxLon)), Color.RED);

            if (gridZone.zoneLetter().equals('C')) {
                drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(minLat, minLon), new LatLng(minLat, maxLon)), Color.RED);
            }

            if (zoom > 3) {
                drawName(bitmap, gridZone, webMercatorBoundingBox, canvas);
            }
        }

        return bitmap;
    }

    /**
     * Draw the shape on the canvas
     *
     * @param boundingBox
     * @param canvas
     */
    private void drawLine(double[] boundingBox, Canvas canvas, Line line, int color) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setAlpha(128);

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);

    }

    private void drawName(Bitmap bitmap, GridZoneDesignator gridZone, double[] boundingBox, Canvas canvas) {

        String name = gridZone.zoneNumber().toString() + gridZone.zoneLetter();
        Log.e("GZD GRID NAME", name);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAlpha(128);
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);

        // Determine the center of the tile
        // TODO determine the center of the bounding box for this grid
        double[] zoneBounds = gridZone.zoneBounds();
        double centerLon = ((zoneBounds[2] - zoneBounds[0]) / 2.0) + zoneBounds[0];
        double centerLat = ((zoneBounds[3] - zoneBounds[1]) / 2.0) + zoneBounds[1];

        double[] meters = TileBoundingBoxUtils.degreesToMeters(new LatLng(centerLat, centerLon));
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);

        // Draw the text
        canvas.drawText(name, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), paint);
    }

    /**
     * Add the polyline to the path
     *
     * @param boundingBox
     * @param path
     * @param line
     */
    private void addPolyline(double[] boundingBox, Path path, Line line) {

        double[] meters = TileBoundingBoxUtils.degreesToMeters(line.p1);
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);
        path.moveTo(x, y);

        meters = TileBoundingBoxUtils.degreesToMeters(line.p2);
        x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);
        path.lineTo(x, y);
    }

    /**
     * Compress the bitmap to a byte array
     *
     * @param bitmap bitmap
     * @return bytes
     * @throws IOException upon error
     */
    public static byte[] toBytes(Bitmap bitmap) throws IOException {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        int quality = 100;

        byte[] bytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(format, quality, byteStream);
            bytes = byteStream.toByteArray();
        } finally {
            byteStream.close();
        }
        return bytes;
    }

    private boolean gzdWithinBounds(Pair<Double, Double> gzdZone, double[] bounds) {

        return false;
    }
}
