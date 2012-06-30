package geogebra.web.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.GRectangle2D;

public class GLine2DW extends geogebra.common.awt.GLine2D implements GShapeW {
	private geogebra.web.openjdk.awt.geom.Line2D impl;
	
	public GLine2DW() {
		impl = new geogebra.web.openjdk.awt.geom.Line2D.Double();
	}
	
	public boolean intersects(int x, int y, int w, int h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}

	
	public Rectangle getBounds() {
		return new geogebra.web.awt.Rectangle(impl.getBounds());
	}

	
	public GRectangle2D getBounds2D() {
		return new geogebra.web.awt.GRectangle2DW(impl.getBounds2D());
	}

	
	public boolean contains(Rectangle r) {
		return impl.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry,yTry);
	}

	
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return (GPathIterator) impl.getPathIterator((geogebra.web.openjdk.awt.geom.AffineTransform) affineTransform);
	}

	
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return (GPathIterator) impl.getPathIterator((geogebra.web.openjdk.awt.geom.AffineTransform) at, flatness);
	}

	
	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	
	public boolean intersects(GRectangle2D r) {
		return impl.intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	
	public geogebra.web.openjdk.awt.geom.Shape getGawtShape() {
		return impl;
	}

	
	public void setLine(double x1, double y1, double x2, double y2) {
		impl.setLine(x1, y1, x2, y2);
	}

	@Override
    public GPoint2D getP1() {
		geogebra.web.openjdk.awt.geom.Point2D p = impl.getP1();
		if (p==null) return null;
		return new geogebra.web.awt.GPoint2DW(p.getX(), p.getY());
    }

	@Override
    public GPoint2D getP2() {
		geogebra.web.openjdk.awt.geom.Point2D p2 = impl.getP2();
		if (p2==null) return null;
		return new geogebra.web.awt.GPoint2DW(p2.getX(), p2.getY());
    }

}
