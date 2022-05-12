package mil.nga.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.R;
import mil.nga.mgrs.features.LatLng;
import mil.nga.mgrs.features.LatLngLine;
import mil.nga.mgrs.features.Point;

/**
 * GZD Grid Tile Provider
 *
 * @author wnewman
 * @author osbornb
 */
public class GZDGridTileProvider implements TileProvider {

    private static int TEXT_SIZE = 16;

    private Context context;
    private int tileWidth;
    private int tileHeight;

    public GZDGridTileProvider(Context context) {
        this.context = context;

        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tile getTile(int x, int y, int zoom) {

        Bitmap bitmap = drawTile(x, y, zoom);

        byte[] bytes = TileUtils.toBytes(bitmap);

        Tile tile = null;
        if (bytes != null) {
            tile = new Tile(tileWidth, tileHeight, bytes);
        }

        return tile;
    }

    /**
     * Draw the tile
     *
     * @param x    x coordinate
     * @param y    y coordinate
     * @param zoom zoom level
     * @return bitmap
     */
    private Bitmap drawTile(int x, int y, int zoom) {

        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        double[] boundingBox = MGRSUtils.getBoundingBox(x, y, zoom);
        double[] webMercatorBoundingBox = MGRSUtils.getWebMercatorBoundingBox(x, y, zoom);

        for (GridZone gridZone : GridZones.getGridRange(boundingBox)) {
            Bounds gridBounds = gridZone.getBounds();
            Double minLat = gridBounds.getMinLatitude();
            Double maxLat = gridBounds.getMaxLatitude();

            Double minLon = gridBounds.getMinLongitude();
            Double maxLon = gridBounds.getMaxLongitude();

            drawLine(webMercatorBoundingBox, canvas, new LatLngLine(new LatLng(maxLat, minLon), new LatLng(maxLat, maxLon)), Color.RED);
            drawLine(webMercatorBoundingBox, canvas, new LatLngLine(new LatLng(minLat, maxLon), new LatLng(maxLat, maxLon)), Color.RED);

            if (gridZone.getLetter() == MGRSConstants.MIN_BAND_LETTER) {
                drawLine(webMercatorBoundingBox, canvas, new LatLngLine(new LatLng(minLat, minLon), new LatLng(minLat, maxLon)), Color.RED);
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
    private void drawLine(double[] boundingBox, Canvas canvas, LatLngLine line, int color) {

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

    private void drawName(Bitmap bitmap, GridZone gridZone, double[] boundingBox, Canvas canvas) {

        String name = Integer.toString(gridZone.getNumber()) + gridZone.getLetter();
        Log.e("GZD GRID NAME", name);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAlpha(128);
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);

        // Determine the center of the tile
        Bounds zoneBounds = gridZone.getBounds();
        Point point = zoneBounds.getCenterPoint();
        float x = MGRSUtils.getXPixel(tileWidth, boundingBox, point.getX());
        float y = MGRSUtils.getYPixel(tileHeight, boundingBox, point.getY());

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
    private void addPolyline(double[] boundingBox, Path path, LatLngLine line) {

        Point point = line.getCoordinate1().toPoint();
        float x = MGRSUtils.getXPixel(tileWidth, boundingBox, point.getX());
        float y = MGRSUtils.getYPixel(tileHeight, boundingBox, point.getY());
        path.moveTo(x, y);

        point = line.getCoordinate2().toPoint();
        x = MGRSUtils.getXPixel(tileWidth, boundingBox, point.getX());
        y = MGRSUtils.getYPixel(tileHeight, boundingBox, point.getY());
        path.lineTo(x, y);
    }

}
