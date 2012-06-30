package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.Rectangle;
import geogebra.common.awt.GRectangle2D;
import geogebra.main.Application;

import java.awt.Shape;

public class GGenericRectangle2DD implements geogebra.awt.GRectangle2DD{

	private java.awt.geom.Rectangle2D impl;
	public GGenericRectangle2DD(){
		impl = new java.awt.geom.Rectangle2D.Double();
	}
	
	public GGenericRectangle2DD(java.awt.geom.Rectangle2D bounds2d) {
		impl = bounds2d;
	}

	
	public double getY() {
		return impl.getY();
	}

	
	public double getX() {
		return impl.getX();
	}

	
	public double getWidth() {
		return impl.getWidth();
	}

	
	public double getHeight() {
		return impl.getHeight();
	}

	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
		
	}

	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);
		
	}

	
	public boolean intersects(double minX, double minY, double lengthX,
			double lengthY) {
		return impl.intersects(minX, minY, lengthX, lengthY);
	}

	
	public boolean intersects(Rectangle viewRect) {
		return impl.intersects(geogebra.awt.GRectangleD.getAWTRectangle(viewRect));
	}
	
	public static java.awt.geom.Rectangle2D getAWTRectangle2D(geogebra.common.awt.GRectangle2D r2d) {
		if (r2d instanceof geogebra.awt.GGenericRectangle2DD){
			return ((geogebra.awt.GGenericRectangle2DD)r2d).impl;
		} else if(r2d instanceof geogebra.awt.GRectangleD){
			return ((geogebra.awt.GRectangleD)r2d).impl;
		}
		if (r2d!= null) Application.debug("other type");
		return null;
		
	}

	public boolean contains(double xTry, double yTry) {
		return impl.contains(xTry, yTry);
	}

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}
	public boolean contains(int x, int y) {
		return impl.contains(x,y);
	}
	
	public geogebra.awt.GRectangleD getBounds() {
		return new geogebra.awt.GRectangleD(impl.getBounds());
	}
	public GRectangle2D getBounds2D() {
		return new geogebra.awt.GGenericRectangle2DD(impl.getBounds2D());
	}
	public boolean contains(Rectangle rectangle) {
		return impl.contains(geogebra.awt.GRectangleD.getAWTRectangle(rectangle));
	}
	
	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(geogebra.awt.GAffineTransformD.getAwtAffineTransform(affineTransform)));
	}
	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new geogebra.awt.GPathIteratorD(impl.getPathIterator(geogebra.awt.GAffineTransformD.getAwtAffineTransform(at), flatness));
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(geogebra.awt.GGenericRectangle2DD.getAWTRectangle2D(r));
	}
	
	public Shape getAwtShape() {
		return impl;
	}

	public GRectangle2D createIntersection(GRectangle2D r) {
		return new geogebra.awt.GGenericRectangle2DD(impl.createIntersection(
				geogebra.awt.GGenericRectangle2DD.getAWTRectangle2D(r)));
	}

}
