package org.geogebra.desktop.awt;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;

public class GEllipse2DDoubleD implements GRectangularShapeD, GEllipse2DDouble {

	private Ellipse2D.Double impl;

	/*
	 * public Double(java.awt.geom.Ellipse2D.Double ellipse2d) { impl =
	 * ellipse2d; }
	 */
	public GEllipse2DDoubleD() {
		impl = new Ellipse2D.Double();
	}

	public GEllipse2DDoubleD(Ellipse2D.Double ellipse) {
		impl = ellipse;
	}

	public GEllipse2DDoubleD(int i, int j, int k, int l) {
		impl = new Ellipse2D.Double(i, j, k, l);
	}

	public void setFrame(double xUL, double yUL, double diameter,
			double diameter2) {
		impl.setFrame(xUL, yUL, diameter, diameter2);
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangle getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(at), flatness));
	}

	public boolean intersects(double x, double y, double w, double h) {
		return impl.intersects(x, y, w, h);
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

	public void setFrameFromCenter(double i, double j, double d, double e) {
		impl.setFrameFromCenter(i, j, d, e);

	}

}
