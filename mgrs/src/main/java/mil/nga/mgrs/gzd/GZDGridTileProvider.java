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
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Pixel;
import mil.nga.mgrs.features.Point;

/**
 * GZD Grid Tile Provider
 *
 * @author wnewman
 * @author osbornb
 */
public class GZDGridTileProvider implements TileProvider {

    /**
     * Name text size
     */
    private static final int TEXT_SIZE = 16;

    /**
     * Context
     */
    private Context context;

    /**
     * Tile width
     */
    private int tileWidth;

    /**
     * Tile height
     */
    private int tileHeight;

    /**
     * Constructor
     *
     * @param context context
     */
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
        return TileUtils.toTile(bitmap);
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

        Bounds bounds = MGRSUtils.getBounds(x, y, zoom);

        for (GridZone gridZone : GridZones.getGridRange(bounds)) {

            Bounds gridBounds = gridZone.getBounds();

            Point northeast = gridBounds.getNortheast();
            Point southeast = gridBounds.getSoutheast();

            drawLine(bounds, canvas, Line.line(gridBounds.getNorthwest(), northeast), Color.RED);
            drawLine(bounds, canvas, Line.line(southeast, northeast), Color.RED);

            if (gridZone.getLetter() == MGRSConstants.MIN_BAND_LETTER) {
                drawLine(bounds, canvas, Line.line(gridBounds.getSouthwest(), southeast), Color.RED);
            }

            if (zoom > 3) {
                drawName(gridZone, bounds, canvas);
            }
        }

        return bitmap;
    }

    /**
     * Draw the shape on the canvas
     *
     * @param bounds tile bounds
     * @param canvas draw canvas
     * @param line   line to draw
     * @param color  color
     */
    private void drawLine(Bounds bounds, Canvas canvas, Line line, int color) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setAlpha(128);

        Path linePath = new Path();
        addPolyline(bounds, linePath, line);
        canvas.drawPath(linePath, paint);

    }

    /**
     * Draw a grid name
     *
     * @param gridZone grid zone
     * @param bounds   tile bounds
     * @param canvas   draw canvas
     */
    private void drawName(GridZone gridZone, Bounds bounds, Canvas canvas) {

        String name = gridZone.getName();
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
        Point center = zoneBounds.getCenter();
        Pixel pixel = center.getPixel(tileWidth, tileHeight, bounds);

        // Draw the text
        canvas.drawText(name, pixel.getX() - textBounds.exactCenterX(),
                pixel.getY() - textBounds.exactCenterY(), paint);
    }

    /**
     * Add the polyline to the path
     *
     * @param bounds tile bounds
     * @param path   path
     * @param line   line to draw
     */
    private void addPolyline(Bounds bounds, Path path, Line line) {

        line = line.toMeters();

        Point point1 = line.getPoint1();
        Point point2 = line.getPoint2();

        Pixel pixel1 = point1.getPixel(tileWidth, tileHeight, bounds);
        path.moveTo(pixel1.getX(), pixel1.getY());

        Pixel pixel2 = point2.getPixel(tileWidth, tileHeight, bounds);
        path.lineTo(pixel2.getX(), pixel2.getY());

    }

}
