/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.discrete.tsp.impl.Point;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.MyMath;

/**
 * Lightweight point with lineTo flag that can be easily transformed into
 * GeoPoint
 */
public class MyPoint extends GPoint2D implements Point {
	/** x-coord */
	public double x;
	/** y-coord */
	public double y;
	/** lineto flag */
	public boolean lineTo = true;

	/**
	 * Creates new MyPoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param lineTo
	 *            lineto flag
	 */
	public MyPoint(double x, double y, boolean lineTo) {
		this.x = x;
		this.y = y;
		this.lineTo = lineTo;
	}

	/**
	 * Creates new empty MyPoint for cache
	 */
	public MyPoint() {
		//
	}

	/**
	 * Creates new lineto MyPoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public MyPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param px
	 *            x-coordinate
	 * @param py
	 *            y-coordinate
	 * @return euclidian distance to otherpoint squared
	 */
	public double distSqr(double px, double py) {
		double vx = px - x;
		double vy = py - y;
		return vx * vx + vy * vy;
	}

	/**
	 * @param px
	 *            x-coord
	 * @param py
	 *            y-coord
	 * @return true if points are equal (Kernel.MIN_PRECISION)
	 */
	public boolean isEqual(double px, double py) {
		return Kernel.isEqual(x, px, Kernel.MIN_PRECISION)
				&& Kernel.isEqual(y, py, Kernel.MIN_PRECISION);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	/**
	 * @param p
	 *            other point
	 * @return euclidian distance from p
	 */
	public double distance(Point p) {
		return MyMath.length(p.getX() - x, p.getY() - y);
	}

	/**
	 * Converts this into GeoPoint
	 * 
	 * @param cons
	 *            construction for the new point
	 * @return GeoPoint equivalent
	 */
	public GeoPoint getGeoPoint(Construction cons) {
		return new GeoPoint(cons, null, x, y, 1.0);
	}

	/**
	 * @return lineTo flag
	 */
	public boolean getLineTo() {
		return lineTo;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	/**
	 * @return 0; for 3D compatibility
	 */
	public double getZ() {
		return 0;
	}

	@Override
	public double distance(double x1, double y1) {
		return GPoint2D.distanceSq(getX(), getY(), x1, y1);
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double distance(GPoint2D q) {
		return distance(q.getX(), q.getY());
	}

	/**
	 * 
	 * @param point
	 *            point
	 * @return true if same (x,y)
	 */
	public boolean isEqual(MyPoint point) {
		return isEqual(point.x, point.y);
	}

	/**
	 * 
	 * @return true if coords are finite numbers
	 */
	public boolean isFinite() {
		return isFinite(x) && isFinite(y);
	}

	/**
	 * 
	 * @param value
	 *            value
	 * @return true if the value is finite number
	 */
	static final protected boolean isFinite(double value) {
		return !java.lang.Double.isInfinite(value)
				&& !java.lang.Double.isNaN(value);
	}

	/**
	 * 
	 * @param t
	 *            parameter
	 * @param point2
	 *            second point
	 * @return (1-t) * this + t * point2
	 */
	public MyPoint barycenter(double t, MyPoint point2) {
		return new MyPoint((1 - t) * x + t * point2.x,
				(1 - t) * y + t * point2.y, false);
	}

	/**
	 * Change to lineto /moveto point
	 * 
	 * @param lineTo
	 *            whether this shoul be linto point
	 */
	public void setLineTo(boolean lineTo) {
		this.lineTo = lineTo;

	}

	public double distanceSqr(Point to) {
		return distSqr(to.getX(), to.getY());
	}

	public boolean isActive() {
		// reuse field "lineTo"
		return lineTo;
	}

	public void setActive(boolean active) {
		// re-use field "lineTo"
		this.lineTo = active;

	}
}
