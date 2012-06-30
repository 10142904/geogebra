package geogebra.awt;

import geogebra.common.awt.GAffineTransform;
import geogebra.common.awt.GPathIterator;
import geogebra.common.awt.GRectangle2D;
import geogebra.main.Application;

import java.awt.Shape;

public class GRectangleD implements geogebra.awt.GRectangle2DD, geogebra.common.awt.Rectangle{

	java.awt.Rectangle impl;
	public GRectangleD(){
		impl = new java.awt.Rectangle();
	}
	public GRectangleD(geogebra.common.awt.Rectangle r){
		impl = ((GRectangleD)r).impl;
	}
	public GRectangleD(int x, int y, int w, int h){
		impl = new java.awt.Rectangle(x,y,w,h);
	}
	public GRectangleD(int w, int h){
		impl = new java.awt.Rectangle(w,h);
	}
	
	public GRectangleD(java.awt.Rectangle frameBounds) {
		impl = frameBounds;
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

	

	
	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);
		
	}

	
	public void setLocation(int x, int y) {
		impl.setLocation(x, y);
		
	}

	
	public void setBounds(geogebra.common.awt.Rectangle r) {
		impl.setBounds((int)r.getX(),(int)r.getY(),(int)r.getWidth(),(int)r.getHeight());
		
	}

	
	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}
	/**
	 * @param rect Common rectangle to unwrap
	 * @return java.awt.Rectangle from the wrapper or null for wrong input type 
	 */
	public static java.awt.Rectangle getAWTRectangle(geogebra.common.awt.Rectangle rect) {
		if(!(rect instanceof GRectangleD)){
			if (rect!= null) Application.debug("other type");
			return null;
		}
		return ((GRectangleD)rect).impl;
	}
	
	public boolean contains(geogebra.common.awt.Rectangle labelRectangle) {
		// TODO Auto-generated method stub
		return impl.contains(getAWTRectangle(labelRectangle));
		
		//return false;
	}
	
	public void add(geogebra.common.awt.Rectangle bb) {
		impl.add(((GRectangleD)bb).impl);
		
	}
	
	public double getMinX() {
		return impl.getMinX();
	}
	
	public double getMinY() {
		return impl.getMinY();
	}
	
	public double getMaxX() {
		return impl.getMaxX();
	}
	
	public double getMaxY() {
		return impl.getMaxY();
	}
	
	public void add(double x, double y) {
		impl.add(x, y);		
	}
	
	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}
	
	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);	
	}
	
	public boolean intersects(double x, double y, double lengthX,
			double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}
	
	public boolean intersects(geogebra.common.awt.Rectangle viewRect) {
		return impl.intersects(GRectangleD.getAWTRectangle(viewRect)) ;
	}

	/*
	public boolean contains(PathPoint prevP) {
		return impl.contains(Point2D.getAwtPoint2D(prevP));
	}
	*/
	
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
	public boolean contains(GRectangleD rectangle) {
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
	public boolean contains(geogebra.common.awt.GPoint2D p1) {
		return impl.contains(geogebra.awt.GPoint2DD.getAwtPoint2D(p1));
	}
	public geogebra.common.awt.Rectangle union(
			geogebra.common.awt.Rectangle bounds) {
		return new geogebra.awt.GRectangleD(
				impl.union(GRectangleD.getAWTRectangle(bounds)));
	}
	public GRectangle2D createIntersection(GRectangle2D r) {
		return new geogebra.awt.GGenericRectangle2DD(impl.createIntersection(
				geogebra.awt.GGenericRectangle2DD.getAWTRectangle2D(r)));
	}
	public void setSize(int width, int height) {
		impl.setSize(width, height);
	}

}
