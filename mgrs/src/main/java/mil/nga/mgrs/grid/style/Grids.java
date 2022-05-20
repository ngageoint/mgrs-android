package mil.nga.mgrs.grid.style;

import android.graphics.Paint;

import java.util.Collection;

import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.Labeler;

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

}
