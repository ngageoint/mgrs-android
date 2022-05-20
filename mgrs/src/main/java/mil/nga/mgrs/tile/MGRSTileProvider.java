package mil.nga.mgrs.tile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.util.Collection;
import java.util.List;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.Label;
import mil.nga.mgrs.grid.style.Grid;
import mil.nga.mgrs.grid.style.Grids;
import mil.nga.mgrs.grid.style.ZoomGrids;
import mil.nga.mgrs.gzd.GridRange;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.gzd.GridZones;

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
     * Grids
     */
    private Grids grids;

    /**
     * Create a tile provider with all grids
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     */
    public static MGRSTileProvider create(int tileWidth, int tileHeight) {
        return new MGRSTileProvider(tileWidth, tileHeight);
    }

    /**
     * Create a tile provider with grid types
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param types      grids types to enable
     */
    public static MGRSTileProvider create(int tileWidth, int tileHeight, GridType... types) {
        return new MGRSTileProvider(tileWidth, tileHeight, types);
    }

    /**
     * Create a tile provider with grid types
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param types      grids types to enable
     */
    public static MGRSTileProvider create(int tileWidth, int tileHeight, Collection<GridType> types) {
        return new MGRSTileProvider(tileWidth, tileHeight, types);
    }

    /**
     * Create a tile provider with grids
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param grids      grids
     */
    public static MGRSTileProvider create(int tileWidth, int tileHeight, Grids grids) {
        return new MGRSTileProvider(tileWidth, tileHeight, grids);
    }

    /**
     * Create a tile provider with Grid Zone Designator grids
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     */
    public static MGRSTileProvider createGZD(int tileWidth, int tileHeight) {
        return new MGRSTileProvider(tileWidth, tileHeight, Grids.createGZD());
    }

    /**
     * Constructor
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     */
    public MGRSTileProvider(int tileWidth, int tileHeight) {
        this(tileWidth, tileHeight, Grids.create());
    }

    /**
     * Constructor
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param types      grids types to enable
     */
    public MGRSTileProvider(int tileWidth, int tileHeight, GridType... types) {
        this(tileWidth, tileHeight, Grids.create(types));
    }

    /**
     * Constructor
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param types      grids types to enable
     */
    public MGRSTileProvider(int tileWidth, int tileHeight, Collection<GridType> types) {
        this(tileWidth, tileHeight, Grids.create(types));
    }

    /**
     * Constructor
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param grids      grids
     */
    public MGRSTileProvider(int tileWidth, int tileHeight, Grids grids) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.grids = grids;
    }

    /**
     * Get the tile width
     *
     * @return tile width
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Set the tile width
     *
     * @param tileWidth tile width
     */
    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    /**
     * Get the tile height
     *
     * @return tile height
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Set the tile height
     *
     * @param tileHeight tile height
     */
    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    /**
     * Get the grids
     *
     * @return grids
     */
    public Grids getGrids() {
        return grids;
    }

    /**
     * Set the grids
     *
     * @param grids grids
     */
    public void setGrids(Grids grids) {
        this.grids = grids;
    }

    /**
     * Get the Military Grid Reference System coordinate for the location in one
     * meter precision
     *
     * @param latLng location
     * @return MGRS coordinate
     */
    public String getCoordinate(LatLng latLng) {
        return grids.getCoordinate(TileUtils.toPoint(latLng));
    }

    /**
     * Get the Military Grid Reference System coordinate for the location in the
     * zoom level precision
     *
     * @param latLng location
     * @param zoom   zoom level precision
     * @return MGRS coordinate
     */
    public String getCoordinate(LatLng latLng, int zoom) {
        return grids.getCoordinate(TileUtils.toPoint(latLng), zoom);
    }

    /**
     * Get the Military Grid Reference System coordinate for the location in the
     * grid type precision
     *
     * @param latLng location
     * @param type   grid type precision
     * @return MGRS coordinate
     */
    public String getCoordinate(LatLng latLng, GridType type) {
        return grids.getCoordinate(TileUtils.toPoint(latLng), type);
    }

    /**
     * Get the Military Grid Reference System for the location
     *
     * @param latLng location
     * @return MGRS
     */
    public MGRS getMGRS(LatLng latLng) {
        return grids.getMGRS(TileUtils.toPoint(latLng));
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
        Bitmap bitmap = null;

        ZoomGrids zoomGrids = grids.getGrids(zoom);
        if (zoomGrids.hasGrids()) {

            bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            MGRSTile mgrsTile = MGRSTile.create(tileWidth, tileHeight, x, y, zoom);

            GridRange gridRange = GridZones.getGridRange(mgrsTile.getBounds());

            for (Grid grid : zoomGrids.grids()) {

                Paint linePaint = grid.getLinePaint();
                Paint labelPaint = grid.getLabelPaint();

                // draw this grid for each zone
                for (GridZone zone : gridRange) {

                    List<Line> lines = grid.getLines(mgrsTile, zone);
                    drawLines(lines, mgrsTile, zone, canvas, linePaint);

                    List<Label> labels = grid.getLabels(mgrsTile, zone);
                    if (labels != null) {
                        drawLabels(labels, grid.getLabelBuffer(), mgrsTile, canvas, labelPaint);
                    }

                }
            }
        }

        return bitmap;
    }

    /**
     * Draw the lines on the tile
     *
     * @param lines  lines to draw
     * @param tile   tile
     * @param zone   grid zone
     * @param canvas draw canvas
     */
    private void drawLines(List<Line> lines, MGRSTile tile, GridZone zone, Canvas canvas, Paint paint) {
        Bounds zoneBounds = zone.getBounds().toMeters();
        for (Line line : lines) {
            drawLine(line, tile, zoneBounds, canvas, paint);
        }
    }

    /**
     * Draw the labels on the tile
     *
     * @param labels labels to draw
     * @param buffer grid zone edge buffer
     * @param tile   tile
     * @param canvas draw canvas
     * @param paint  label paint
     */
    private void drawLabels(List<Label> labels, double buffer, MGRSTile tile, Canvas canvas, Paint paint) {
        for (Label label : labels) {
            drawLabel(label, buffer, tile, canvas, paint);
        }
    }

    /**
     * Draw the shape on the canvas
     *
     * @param line       line to draw
     * @param tile       tile
     * @param zoneBounds grid zone bounds
     * @param canvas     draw canvas
     * @param paint      line paint
     */
    private void drawLine(Line line, MGRSTile tile, Bounds zoneBounds, Canvas canvas, Paint paint) {
        canvas.save();

        PixelRange pixelRange = zoneBounds.getPixelRange(tile);
        canvas.clipRect(pixelRange.getLeft(), pixelRange.getTop(), pixelRange.getRight(), pixelRange.getBottom(), Region.Op.INTERSECT);

        Path linePath = new Path();
        addPolyline(tile, linePath, line);
        canvas.drawPath(linePath, paint);

        canvas.restore();
    }

    /**
     * Add the polyline to the path
     *
     * @param tile tile
     * @param path line path
     * @param line line to draw
     */
    private void addPolyline(MGRSTile tile, Path path, Line line) {

        line = line.toMeters();
        Point point1 = line.getPoint1();
        Point point2 = line.getPoint2();

        Pixel pixel = point1.getPixel(tile);
        path.moveTo(pixel.getX(), pixel.getY());

        Pixel pixel2 = point2.getPixel(tile);
        path.lineTo(pixel2.getX(), pixel2.getY());

    }

    /**
     * Draw the label
     *
     * @param label  label to draw
     * @param buffer grid zone edge buffer
     * @param tile   tile
     * @param canvas draw canvas
     * @param paint  label paint
     */
    private void drawLabel(Label label, double buffer, MGRSTile tile, Canvas canvas, Paint paint) {

        String name = label.getName();

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);
        float textWidth = paint.measureText(name);
        int textHeight = textBounds.height();

        // Determine the pixel width and height of the label grid zone to the tile
        PixelRange pixelRange = label.getBounds().getPixelRange(tile);

        // Determine the maximum width and height a label in the grid should be
        double gridPercentage = 1.0 - (2 * buffer);
        double maxWidth = gridPercentage * pixelRange.getWidth();
        double maxHeight = gridPercentage * pixelRange.getHeight();

        // If it fits, draw the label in the center of the grid zone
        if (textWidth <= maxWidth && textHeight <= maxHeight) {
            Pixel centerPixel = label.getCenter().getPixel(tile);
            canvas.drawText(name, centerPixel.getX() - textBounds.exactCenterX(), centerPixel.getY() - textBounds.exactCenterY(), paint);
        }

    }

}
