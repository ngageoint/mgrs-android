package mil.nga.mgrs.grid.style;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Collection;
import java.util.List;

import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.Label;
import mil.nga.mgrs.grid.Labeler;
import mil.nga.mgrs.gzd.GridRange;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.gzd.GridZones;
import mil.nga.mgrs.tile.MGRSTile;
import mil.nga.mgrs.tile.TileDraw;

/**
 * Grids with Android specific styling
 */
public class Grids extends mil.nga.mgrs.grid.Grids {

    /**
     * Create with all grid types enabled
     *
     * @return grids
     */
    public static Grids create() {
        return new Grids();
    }

    /**
     * Create with grids to enable
     *
     * @param types grid types to enable
     * @return grids
     */
    public static Grids create(GridType... types) {
        return new Grids(types);
    }

    /**
     * Create with grids to enable
     *
     * @param types grid types to enable
     * @return grids
     */
    public static Grids create(Collection<GridType> types) {
        return new Grids(types);
    }

    /**
     * Create only Grid Zone Designator grids
     *
     * @return grids
     */
    public static Grids createGZD() {
        return create(GridType.GZD);
    }

    /**
     * Constructor, all grid types enabled
     */
    public Grids() {

    }

    /**
     * Constructor
     *
     * @param types grid types to enable
     */
    public Grids(GridType... types) {
        super(types);
    }

    /**
     * Constructor
     *
     * @param types grid types to enable
     */
    public Grids(Collection<GridType> types) {
        super(types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Grid newGrid(GridType type, boolean enabled, int minZoom,
                           Integer maxZoom, Color color, double width, Labeler labeler) {
        return new Grid(type, enabled, minZoom, maxZoom, color, width, labeler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ZoomGrids newZoomGrids(int zoom) {
        return new ZoomGrids(zoom);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grid getGrid(GridType type) {
        return (Grid) super.getGrid(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZoomGrids getGrids(int zoom) {
        return (ZoomGrids) super.getGrids(zoom);
    }

    /**
     * Draw a tile with the dimensions and XYZ coordinate
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param x          x coordinate
     * @param y          y coordinate
     * @param zoom       zoom level
     * @return bitmap tile
     */
    public Bitmap drawTile(int tileWidth, int tileHeight, int x, int y, int zoom) {
        Bitmap bitmap = null;
        ZoomGrids zoomGrids = getGrids(zoom);
        if (zoomGrids.hasGrids()) {
            bitmap = drawTile(MGRSTile.create(tileWidth, tileHeight, x, y, zoom), zoomGrids);
        }
        return bitmap;
    }

    /**
     * Draw a tile with the dimensions and bounds
     *
     * @param tileWidth  tile width
     * @param tileHeight tile height
     * @param bounds     bounds
     * @return bitmap tile
     */
    public Bitmap drawTile(int tileWidth, int tileHeight, Bounds bounds) {
        return drawTile(MGRSTile.create(tileWidth, tileHeight, bounds));
    }

    /**
     * Draw the tile
     *
     * @param mgrsTile tile
     * @return bitmap tile
     */
    public Bitmap drawTile(MGRSTile mgrsTile) {
        Bitmap bitmap = null;
        ZoomGrids zoomGrids = getGrids(mgrsTile.getZoom());
        if (zoomGrids.hasGrids()) {
            bitmap = drawTile(mgrsTile, zoomGrids);
        }
        return bitmap;
    }

    /**
     * Draw the tile
     *
     * @param mgrsTile  MGRS tile
     * @param zoomGrids zoom grids
     * @return bitmap tile
     */
    private Bitmap drawTile(MGRSTile mgrsTile, ZoomGrids zoomGrids) {

        Bitmap bitmap = Bitmap.createBitmap(mgrsTile.getWidth(), mgrsTile.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        GridRange gridRange = GridZones.getGridRange(mgrsTile.getBounds());

        for (Grid grid : zoomGrids.grids()) {

            Paint linePaint = grid.getLinePaint();
            Paint labelPaint = grid.getLabelPaint();

            // draw this grid for each zone
            for (GridZone zone : gridRange) {

                List<Line> lines = grid.getLines(mgrsTile, zone);
                if (lines != null) {
                    TileDraw.drawLines(lines, mgrsTile, zone, canvas, linePaint);
                }

                List<Label> labels = grid.getLabels(mgrsTile, zone);
                if (labels != null) {
                    TileDraw.drawLabels(labels, grid.getLabelBuffer(), mgrsTile, canvas, labelPaint);
                }

            }
        }

        return bitmap;
    }

    /**
     * Get the grid line paint for the grid type
     *
     * @param type grid type
     * @return grid line paint
     */
    public Paint getLinePaint(GridType type) {
        return getGrid(type).getLinePaint();
    }

    /**
     * Set the grid line paint for the grid type
     *
     * @param type      grid type
     * @param linePaint grid line paint
     */
    public void setLinePaint(GridType type, Paint linePaint) {
        getGrid(type).setLinePaint(linePaint);
    }

    /**
     * Get the grid label paint for the grid type
     *
     * @param type grid type
     * @return grid label paint
     */
    public Paint getLabelPaint(GridType type) {
        return getGrid(type).getLabelPaint();
    }

    /**
     * Set the grid label paint for the grid type
     *
     * @param type       grid type
     * @param labelPaint grid label paint
     */
    public void setLabelPaint(GridType type, Paint labelPaint) {
        getGrid(type).setLabelPaint(labelPaint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLabelColor(GridType type, Color color) {
        super.setLabelColor(type, color);
        getGrid(type).resetLabelPaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLabelTextSize(GridType type, double textSize) {
        super.setLabelTextSize(type, textSize);
        getGrid(type).resetLabelPaint();
    }

}
