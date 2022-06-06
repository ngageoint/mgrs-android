package mil.nga.mgrs.grid.style;

import android.graphics.Paint;
import android.graphics.Typeface;

import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.Labeler;

/**
 * Grid with Android specific styling
 */
public class Grid extends mil.nga.mgrs.grid.Grid {

    /**
     * Grid line paint
     */
    private Paint linePaint;

    /**
     * Grid label paint
     */
    private Paint labelPaint;

    /**
     * Constructor
     *
     * @param type grid type
     */
    protected Grid(GridType type) {
        super(type);
    }

    /**
     * Get the grid line paint, create if needed
     *
     * @return grid line paint
     */
    public Paint getLinePaint() {
        if (linePaint == null) {
            createLinePaint();
        }
        return linePaint;
    }

    /**
     * Create the grid line paint
     *
     * @return grid line paint
     */
    public Paint createLinePaint() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth((float) getWidth());
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(getColor().getColorWithAlpha());
        return linePaint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(double width) {
        super.setWidth(width);
        resetLinePaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColor(Color color) {
        super.setColor(color);
        resetLinePaint();
    }

    /**
     * Reset the grid line paint
     */
    public void resetLinePaint() {
        setLinePaint(null);
    }

    /**
     * Set the grid line paint
     *
     * @param linePaint grid line paint
     */
    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    /**
     * Get the grid label paint, create if needed
     *
     * @return grid label paint, null if no labeler
     */
    public Paint getLabelPaint() {
        if (labelPaint == null) {
            createLabelPaint();
        }
        return labelPaint;
    }

    /**
     * Create the grid label paint
     *
     * @return grid label paint, null if no labeler
     */
    public Paint createLabelPaint() {
        Labeler labeler = getLabeler();
        if (labeler != null) {
            labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            labelPaint.setColor(labeler.getColor().getColorWithAlpha());
            labelPaint.setTextSize((float) labeler.getTextSize());
            labelPaint.setTypeface(Typeface.MONOSPACE);
        } else {
            labelPaint = null;
        }
        return labelPaint;
    }

    /**
     * Reset the grid label paint
     */
    public void resetLabelPaint() {
        setLabelPaint(null);
    }

    /**
     * Set the grid label paint
     *
     * @param labelPaint grid label paint
     */
    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    /**
     * Reset the grid line and label paint
     */
    public void resetPaint() {
        resetLinePaint();
        resetLabelPaint();
    }

}
