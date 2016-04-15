package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.ggbjdk.java.awt.geom.Point2D;

public class GPoint2DW extends GPoint2D {

	private Point2D.Double impl;

	public GPoint2DW() {
		impl = new Point2D.Double();
	}

	public GPoint2DW(double x, double y) {
		impl = new Point2D.Double(x, y);
	}

	public double getX() {
		return impl.getX();
	}

	public double getY() {
		return impl.getY();
	}

	public void setX(double x) {
		impl.setLocation(x, getY());
	}

	public void setY(double y) {
		impl.setLocation(getX(), y);
	}

	public double distance(org.geogebra.common.awt.GPoint2D q) {
		return impl.distance(q.getX(), q.getY());
	}

	public double distance(double x, double y) {
		return impl.distance(x, y);
	}

	public static org.geogebra.ggbjdk.java.awt.geom.Point2D.Double getGawtPoint2D(
	        org.geogebra.common.awt.GPoint2D p) {
		if (p == null)
			return null;
		return new org.geogebra.ggbjdk.java.awt.geom.Point2D.Double(p.getX(),
		        p.getY());
	}

}
