package mil.nga.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.util.List;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.MGRSTile;
import mil.nga.mgrs.R;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Pixel;
import mil.nga.mgrs.features.PixelRange;
import mil.nga.mgrs.features.Point;

/**
 * MGRS Tile Provider
 *
 * @author wnewman
 * @author osbornb
 */
public class MGRSTileProvider implements TileProvider {

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
    public MGRSTileProvider(Context context) {
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
        Bitmap bitmap = null;

        Grids grids = Grid.getGrids(zoom);
        if (grids.hasGrids()) {

            bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            MGRSTile mgrsTile = new MGRSTile(tileWidth, tileHeight, x, y, zoom);

            GridRange gridRange = GridZones.getGridRange(mgrsTile.getBounds());

            for (Grid grid : grids) {

                // draw this grid for each zone
                for (GridZone zone : gridRange) {

                    List<Line> lines = zone.getLines(mgrsTile.getBounds(), grid.getPrecision());
                    drawLines(lines, mgrsTile, zone, canvas);

                    if (grid == Grid.GZD && zoom > 3) {
                        List<Label> labels = zone.getLabels(mgrsTile.getBounds(), grid.getPrecision());
                        drawLabels(labels, mgrsTile, zone, canvas);
                    }

                    if (grid == Grid.HUNDRED_KILOMETER && zoom > 5) {
                        List<Label> labels = zone.getLabels(mgrsTile.getBounds(), grid.getPrecision());
                        drawLabels(labels, mgrsTile, zone, canvas);
                    }
                }
            }
        }

        return bitmap;
    }

    private void drawLines(List<Line> lines, MGRSTile tile, GridZone zone, Canvas canvas) {
        Bounds zoneBounds = zone.getBounds().toMeters();
        for (Line line : lines) {
            drawLine(line, tile, zoneBounds, canvas);
        }
    }

    private void drawLabels(List<Label> labels, MGRSTile tile, GridZone zone, Canvas canvas) {
        for (Label label : labels) {
            drawLabel(label, tile, canvas);
        }
    }

    /**
     * Draw the shape on the canvas
     */
    private void drawLine(Line line, MGRSTile tile, Bounds clipBounds, Canvas canvas) {
        canvas.save();

        // TODO grid based paint
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.rgb(239, 83, 80));

        Bounds tileBounds = tile.getWebMercatorBounds();

        PixelRange pixelRange = clipBounds.getPixelRange(tile, tileBounds);
        canvas.clipRect(pixelRange.getLeft(), pixelRange.getTop(), pixelRange.getRight(), pixelRange.getBottom(), Region.Op.INTERSECT);

        Path linePath = new Path();
        addPolyline(tile, linePath, line);
        canvas.drawPath(linePath, linePaint);

        canvas.restore();
    }

    /**
     * Add the polyline to the path
     *
     * @param path
     * @param path
     * @param line
     */
    private void addPolyline(MGRSTile tile, Path path, Line line) {

        line = line.toMeters();
        Point point1 = line.getPoint1();
        Point point2 = line.getPoint2();

        Bounds bounds = tile.getWebMercatorBounds();

        Pixel pixel = point1.getPixel(tile, bounds);
        path.moveTo(pixel.getX(), pixel.getY());

        Pixel pixel2 = point2.getPixel(tile, bounds);
        path.lineTo(pixel2.getX(), pixel2.getY());

    }

    private void drawLabel(Label label, MGRSTile tile, Canvas canvas) {

        // TODO grid based paint
        Paint oneHundredKLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oneHundredKLabelPaint.setColor(Color.rgb(76, 175, 80));
        oneHundredKLabelPaint.setTextSize(24);
        oneHundredKLabelPaint.setTypeface(Typeface.MONOSPACE);

        // Determine the text bounds
        Rect textBounds = new Rect();
        oneHundredKLabelPaint.getTextBounds(label.getName(), 0, label.getName().length(), textBounds);

        Bounds tileBounds = tile.getWebMercatorBounds();
        Bounds labelBounds = label.getBounds();

        PixelRange pixelRange = labelBounds.getPixelRange(tile, tileBounds);

        float zoneWidth = pixelRange.getMaxX() - pixelRange.getMinX();
        float zoneHeight = pixelRange.getMaxY() - pixelRange.getMinY();

        Point center = label.getCenter().toMeters();
        Pixel centerPixel = center.getPixel(tile, tileBounds);

        if (label.getName().equals("KV") || label.getName().equals("GE")) {
            Log.i("", "");
        }

        float textWidth = oneHundredKLabelPaint.measureText(label.getName(), 0, label.getName().length());

        double textWidthPercent = textWidth * 2 / zoneWidth;
        double textHeightPercent = textBounds.height() * 2 / zoneHeight;

//        if (zoneWidth > textBounds.width() + 20 && zoneHeight > textBounds.height() + 20) {
        if (textWidthPercent < .80 && textHeightPercent < .80 && textBounds.width() < zoneWidth && textBounds.height() < zoneHeight) {
            canvas.drawText(label.getName(), centerPixel.getX() - textBounds.exactCenterX(), centerPixel.getY() - textBounds.exactCenterY(), oneHundredKLabelPaint);
        }
    }

}
