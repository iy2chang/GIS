package com.esri.svr.cmn;


/**
 * A Ring is part of polygon
 */
public class Ring implements java.io.Serializable {

    private boolean _isInterior = false;
    private int _exteriorRingIndex = -1; // -1 means that the current ring is an exterior ring
    private double[] x;
    private double[] y;

    /**
     * Teh default constructor.
     */
    public Ring() {
    }

    /**
     * Set exterior ring index, -1 means that the current ring is an exterior ring
     * @param index exterior ring index
     */
    public void setExteriorRingIndex(int index) {
        _exteriorRingIndex = index;
    }

    /**
     * Get exterior ring index
     * @return the index of exterior ring
     */
    public int getExteriorRingIndex() {
        return _exteriorRingIndex;
    }

    /**
     * Set ring points
     * @param x points' x coordinates
     * @param y points' y coordinates
     */
    public void setPoints(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set ring's x coordinate array
     * @param x
     */
    public void setX(double[] x) {
        this.x = x;
    }

    /**
     * Set ring's y coordinate array
     * @param y
     */
    public void setY(double[] y) {
        this.y = y;
    }

    /**
     * Get ring's x coordinates
     * @return
     */
    public double[] getX() {
        return x;
    }

    /**
     * Get ring's y coordinates
     * @return
     */
    public double[] getY() {
        return y;
    }

    /**
     * Set current ring as interior
     * @param isInterior the boolean indicating the ring is interior or not.
     */
    public void setInterior(boolean isInterior) {
        _isInterior = isInterior;
    }

    /**
     * Is the ring an interior or not
     * @return a boolean indicating the ring is interior or not.
     */
    public boolean isInterior() {
        return _isInterior;
    }
}