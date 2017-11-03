/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * Image with given filename and corners
 */
public class GeoImage extends GeoElement implements Locateable,
		AbsoluteScreenLocateable, PointRotateable, Mirrorable, Translateable,
		Dilateable, MatrixTransformable, Transformable {
	/** Index of the center in corners array */
	public static final int CENTER_INDEX = 3;
	// private String imageFileName = ""; // image file
	private GeoPoint[] corners; // corners of the image
	// private BufferedImage image;
	/** width in pixels */
	protected int pixelWidth;
	/** height in pixels */
	protected int pixelHeight;
	private boolean inBackground, defined;
	private boolean hasAbsoluteLocation;
	private boolean interpolate = true;

	// for absolute screen location
	private int screenX, screenY;
	private boolean hasAbsoluteScreenLocation = false;

	// corner points for transformations
	private GeoPoint[] tempPoints;
	// coords is the 2d result array for (x, y); n is 0, 1, or 2
	private double[] tempCoords = new double[2];
	private ArrayList<GeoPointND> al = null;
	private boolean centered = false;
	/**
	 * Creates new image
	 * 
	 * @param c
	 *            construction
	 */
	public GeoImage(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setAlphaValue(1f);
		// setAlgebraVisible(false); // don't show in algebra view
		setAuxiliaryObject(true);

		if (kernel.getApplication().has(Feature.CENTER_IMAGE)) {
			// three corners of the image: first, second, fourth and the center
			corners = new GeoPoint[4];
		} else {
			// three corners of the image: first, second and fourth
			corners = new GeoPoint[3];
		}

		kernel.getApplication().images.add(this);
		defined = true;
	}

	/**
	 * Creates new labeled image
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param fileName
	 *            path to the image
	 */
	public GeoImage(Construction c, String label, String fileName) {
		this(c);
		setImageFileName(fileName);
		setLabel(label);
	}

	/**
	 * Copy constructor
	 * 
	 * @param img
	 *            source image
	 */
	public GeoImage(GeoImage img) {
		this(img.cons);
		set(img);
	}

	@Override
	public GeoElement copy() {
		return new GeoImage(this);
	}

	@Override
	public int getRelatedModeID() {

		return EuclidianConstants.MODE_IMAGE;
		/*
		 * switch (this.image.getType()){ case 5: return
		 * EuclidianConstants.MODE_IMAGE; case 6: return
		 * EuclidianConstants.MODE_PEN; default: return -1; }
		 */
	}

	private void initTempPoints() {
		if (tempPoints == null) {
			// temp corner points for transformations and absolute location
			tempPoints = new GeoPoint[4];
			for (int i = 0; i < tempPoints.length; i++) {
				tempPoints[i] = new GeoPoint(cons);
			}
		}

		if (corners[0] == null) {
			corners[0] = tempPoints[0];
		}
	}

	@Override
	public void set(GeoElementND geo) {
		GeoImage img = (GeoImage) geo;
		setImageFileName(img.getGraphicsAdapter().getImageFileName());

		// macro output: don't set corners
		if (cons != geo.getConstruction() && isAlgoMacroOutput()) {
			return;
		}

		// location settings
		hasAbsoluteScreenLocation = img.hasAbsoluteScreenLocation;

		if (hasAbsoluteScreenLocation) {
			screenX = img.screenX;
			screenY = img.screenY;
		} else {
			hasAbsoluteLocation = true;
			for (int i = 0; i < corners.length; i++) {
				if (img.corners[i] == null) {
					corners[i] = null;
				} else {
					initTempPoints();

					tempPoints[i].setCoords(img.corners[i]);
					corners[i] = tempPoints[i];
				}
			}
		}

		// interpolation settings
		interpolate = img.interpolate;
		defined = img.defined;
		if (img.centered) {
			setCentered(true);
		}
	}

	@Override
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);

		if (geo.isGeoImage()) {
			inBackground = ((GeoImage) geo).inBackground;
		}
	}

	/**
	 * Reloads images from internal image cache
	 * 
	 * @param kernel
	 *            kernel for which we want to do the replacement
	 */
	public static void updateInstances(App kernel) {
		for (int i = kernel.images.size() - 1; i >= 0; i--) {
			GeoImage geo = kernel.images.get(i);
			geo.setImageFileName(geo.getGraphicsAdapter().getImageFileName());
			geo.updateCascade();
		}
	}

	@Override
	public boolean showToolTipText() {
		return !inBackground && super.showToolTipText();
	}

	/**
	 * True for background images
	 * 
	 * @return true for background images
	 */
	final public boolean isInBackground() {
		return inBackground;
	}

	/**
	 * Switch to background image (or vice versa)
	 * 
	 * @param flag
	 *            true to make it background image
	 */
	public void setInBackground(boolean flag) {
		inBackground = flag;
	}

	/**
	 * Tries to load the image using the given fileName.
	 * 
	 * @param fileName
	 *            filename
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public void setImageFileName(String fileName, int width, int height) {

		if (fileName == null) {
			return;
		}
		if (fileName.equals(this.getGraphicsAdapter().getImageFileName())) {
			return;
		}

		this.getGraphicsAdapter().setImageFileNameOnly(fileName);

		this.getGraphicsAdapter().setImageOnly(kernel.getApplication()
				.getExternalImageAdapter(fileName, width, height));
		if (this.getGraphicsAdapter().getImageOnly() != null) {
			pixelWidth = this.getGraphicsAdapter().getImageOnly().getWidth();
			pixelHeight = this.getGraphicsAdapter().getImageOnly().getHeight();
		} else {
			pixelWidth = 0;
			pixelHeight = 0;
		}
	}

	@Override
	public void setImageFileName(String fileName) {
		setImageFileName(fileName, 0, 0);
	}

	// final public BufferedImage getFillImage() {
	// return image;
	// }

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {
		setCorner(p, 0);
	}

	@Override
	public void removeStartPoint(GeoPointND p) {
		for (int i = 0; i < corners.length; i++) {
			if (corners[i] == p) {
				setCorner(null, i);
			}
		}
	}

	@Override
	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setCorner(p, number);
	}

	/**
	 * Sets the startpoint without performing any checks. This is needed for
	 * macros.
	 */
	@Override
	public void initStartPoint(GeoPointND p, int number) {
		corners[number] = (GeoPoint) p;
	}

	/**
	 * Sets a corner of this image.
	 * 
	 * @param p
	 *            corner point
	 * @param number0
	 *            0, 1 or 2 (first, second and fourth corner)
	 */
	public void setCorner(GeoPointND p, int number0) {
		int number = isCentered() ? 3 : number0;
		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) {
			return;
		}

		if (corners[0] == null && number > 0 && number < CENTER_INDEX) {
			return;
		}

		// check for circular definition
		if (isParentOf(p)) {
			// throw new CircularDefinitionException();
			return;
		}

		// set new location
		if (!(p instanceof GeoPoint)) {
			// remove old dependencies
			if (corners[number] != null) {
				corners[number].getLocateableList().unregisterLocateable(this);
			}

			// copy old first corner as absolute position
			if (number == 0 && corners[0] != null) {
				GeoPoint temp = new GeoPoint(cons);
				temp.setCoords(corners[0]);
				corners[0] = temp;
			} else {
				corners[number] = null;
			}
		} else {
			// check if this point is already available
			for (int i = 0; i < corners.length; i++) {
				if (p == corners[i]) {
					return;
				}
			}

			// remove old dependencies
			if (corners[number] != null) {
				corners[number].getLocateableList().unregisterLocateable(this);
			}

			corners[number] = (GeoPoint) p;
			// add new dependencies
			corners[number].getLocateableList().registerLocateable(this);
		}

		// absolute screen position should be deactivated
		setAbsoluteScreenLocActive(false);
		updateHasAbsoluteLocation();

	}

	/**
	 * Sets hasAbsoluteLocation flag to true iff all corners are absolute start
	 * points (i.e. independent and unlabeled).
	 */
	private void updateHasAbsoluteLocation() {
		hasAbsoluteLocation = true;

		for (int i = 0; i < corners.length; i++) {
			if (!(corners[i] == null || corners[i].isAbsoluteStartPoint())) {
				hasAbsoluteLocation = false;
				return;
			}
		}
	}

	@Override
	public void doRemove() {
		kernel.getApplication().images.remove(this);

		// remove background image
		if (inBackground) {
			inBackground = false;
			notifyUpdate();
		}

		super.doRemove();
		for (int i = 0; i < corners.length; i++) {
			// tell corner
			if (corners[i] != null) {
				corners[i].getLocateableList().unregisterLocateable(this);
			}
		}
	}

	@Override
	public GeoPoint getStartPoint() {
		return corners[0];
	}

	@Override
	public GeoPoint[] getStartPoints() {
		return corners;
	}

	/**
	 * Returns n-th corner point
	 * 
	 * @param number
	 *            1 for boottom left, others clockwise
	 * @return corner point
	 */
	final public GeoPoint getCorner(int number) {
		return corners[number];
	}

	@Override
	final public boolean hasAbsoluteLocation() {
		return hasAbsoluteLocation;
	}

	/**
	 * 
	 * @return true if the image wants to be interpolated
	 */
	final public boolean isInterpolate() {
		return interpolate;
	}

	/**
	 * sets if the image want to be interpolated
	 * 
	 * @param flag
	 *            true to turn interpolation on
	 */
	final public void setInterpolate(boolean flag) {
		interpolate = flag;
	}

	@Override
	public void setWaitForStartPoint() {
		// this can be ignored for an image
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}

	@Override
	final public boolean isDefined() {
		if (!defined) {
			return false;
		}
		for (int i = 0; i < corners.length; i++) {
			if (i == 3) {
				return true;
			}
			if (corners[i] != null && !corners[i].isDefined()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * makes image invisible needed for Sequence's cached images
	 */
	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String toString(StringTemplate tpl) {
		return label == null ? getLoc().getMenu("Image") : label;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return getGraphicsAdapter().getImageOnly() != null && isDefined();
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMAGE;
	}

	/**
	 * Returns whether this image can be moved in Euclidian View.
	 */
	@Override
	final public boolean isMoveable() {
		return (hasAbsoluteScreenLocation || hasAbsoluteLocation)
				&& isPointerChangeable();
	}

	/**
	 * Returns whether this image can be rotated in Euclidian View.
	 */
	@Override
	final public boolean isRotateMoveable() {
		return !hasAbsoluteScreenLocation && hasAbsoluteLocation
				&& isPointerChangeable();
	}

	/**
	 * Returns whether this image can be fixed.
	 * 
	 * public boolean isFixable() { return (hasAbsoluteScreenLocation ||
	 * hasAbsoluteLocation) && isIndependent(); }
	 */

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean isGeoImage() {
		return true;
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {

		// name of image file
		sb.append("\t<file name=\"");
		// Michael Borcherds 2007-12-10 this line restored (not needed now MD5
		// code put in the correct place)
		sb.append(StringUtil
				.encodeXML(this.getGraphicsAdapter().getImageFileName()));
		sb.append("\"/>\n");

		// name of image file
		sb.append("\t<inBackground val=\"");
		sb.append(inBackground);
		sb.append("\"/>\n");

		// image has to be interpolated
		if (!isInterpolate()) {
			sb.append("\t<interpolate val=\"false\"/>\n");
		}

		// locateion of image

		if (isCentered()) {
			sb.append("\t<centered val=\"true\"/>\n");
		}

		if (hasAbsoluteScreenLocation) {
			sb.append(getXMLabsScreenLoc());
		} else {
			// store location of corners
			for (int i = 0; i < corners.length; i++) {
				if (corners[i] != null) {
					sb.append(getCornerPointXML(i));
				}
			}
		}

		getAuxiliaryXML(sb);

		// sb.append(getXMLvisualTags());
		// sb.append(getBreakpointXML());
		super.getXMLtags(sb);

	}

	private String getXMLabsScreenLoc() {
		StringBuilder sb = new StringBuilder();

		sb.append("\t<absoluteScreenLocation x=\"");
		sb.append(screenX);
		sb.append("\" y=\"");
		sb.append(screenY);
		sb.append("\"/>");
		return sb.toString();
	}

	private String getCornerPointXML(int number) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t<startPoint number=\"");
		sb.append(number);
		sb.append("\"");

		if (corners[number].isAbsoluteStartPoint()) {
			sb.append(" x=\"" + corners[number].x + "\"");
			sb.append(" y=\"" + corners[number].y + "\"");
			sb.append(" z=\"" + corners[number].z + "\"");
		} else {
			sb.append(" exp=\"");
			StringUtil.encodeXML(sb,
					corners[number].getLabel(StringTemplate.xmlTemplate));
			sb.append("\"");
		}
		sb.append("/>\n");
		return sb.toString();
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		screenX = x;
		screenY = y;
		if (!hasScreenLocation() && (x != 0 && y != 0)) {
			setScreenLocation(x, y);
		}
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return screenX;
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return screenY;
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		GeoPoint locPoint = getStartPoint();
		if (locPoint == null) {
			locPoint = new GeoPoint(cons);
			setCorner(locPoint, 0);
		}
		locPoint.setCoords(x, y, 1.0);
	}

	@Override
	public double getRealWorldLocX() {
		if (corners[0] == null) {
			return 0;
		}
		return corners[0].inhomX;
	}

	@Override
	public double getRealWorldLocY() {
		if (corners[0] == null) {
			return 0;
		}
		return corners[0].inhomY;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;
		if (flag) {
			// remove startpoints
			for (int i = 0; i < 3; i++) {
				if (corners[i] != null) {
					corners[i].getLocateableList().unregisterLocateable(this);
				}
			}
			corners[1] = null;
			corners[2] = null;
		}
	}

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return isIndependent();
	}

	/*
	 * ************************************** Transformations
	 * *************************************
	 */

	/**
	 * Calculates the n-th corner point of this image in real world coordinates.
	 * Note: if this image has an absolute screen location, result is set to
	 * undefined.
	 * 
	 * @param result
	 *            here the result is stored.
	 * @param n
	 *            number of the corner point (1, 2, 3 or 4)
	 */
	public void calculateCornerPoint(GeoPoint result, int n) {
		if (hasAbsoluteScreenLocation) {
			result.setUndefined();
			return;
		}

		if (corners[0] == null && !centered) {
			initTempPoints();
		}

		switch (n) {
		case 1: // get A
			result.setCoords(getCornerAx(), getCornerAy(), 1);
			break;

		case 2: // get B
			getInternalCornerPointCoords(tempCoords, 1);
			result.setCoords(tempCoords[0], tempCoords[1], 1.0);
			break;

		case 3: // get C
			double[] b = new double[2];
			double[] d = new double[2];
			getInternalCornerPointCoords(b, 1);
			getInternalCornerPointCoords(d, 2);
			result.setCoords(d[0] + b[0] - getCornerAx(),
					d[1] + b[1] - getCornerAy(), 1.0);
			break;

		case 4: // get D
			getInternalCornerPointCoords(tempCoords, 2);
			result.setCoords(tempCoords[0], tempCoords[1], 1.0);
			break;

		default:
			result.setUndefined();
		}
	}

	private double getCornerAx() {
		if (!centered) {
			return corners[0].inhomX;
		}

		EuclidianView ev = kernel.getApplication().getEuclidianView1();
		GeoPoint c = corners[CENTER_INDEX];
		if (c != null) { // may be null while loading file
			return ev.toRealWorldCoordX(ev.toScreenCoordX(c.inhomX) - pixelWidth / 2);
		}
		return 0;
	}

	private double getCornerAy() {
		if (!centered) {
			return corners[0].inhomY;
		}

		EuclidianView ev = kernel.getApplication().getEuclidianView1();
		GeoPoint c = corners[CENTER_INDEX];
		if (c != null) { // may be null while loading file
			return ev.toRealWorldCoordY(
					ev.toScreenCoordY(c.inhomY) + pixelHeight / 2);
		}
		return 0;
	}

	private void getInternalCornerPointCoords(double[] coords, int n) {
		double ax = getCornerAx();
		double ay = getCornerAy();
		GeoPoint B = corners[1];
		GeoPoint D = corners[2];

		double xscale = kernel.getXscale();
		double yscale = kernel.getYscale();
		double width = pixelWidth;
		double height = pixelHeight;

		// different scales: change height
		if (xscale != yscale) {
			height = height * yscale / xscale;
		}

		switch (n) {
		case 0: // get A
			coords[0] = ax;
			coords[1] = ay;
			break;

		case 1: // get B
			if (B != null) {
				coords[0] = B.inhomX;
				coords[1] = B.inhomY;
			} else { // B is not defined
				if (D == null) {
					// B and D are not defined
					coords[0] = ax + width / xscale;
					coords[1] = ay;
				} else {
					// D is defined, B isn't
					double nx = D.inhomY - ay;
					double ny = ax - D.inhomX;
					double factor = width / height;
					coords[0] = ax + factor * nx;
					coords[1] = ay + factor * ny;
				}
			}
			break;

		case 2: // D
			if (D != null) {
				coords[0] = D.inhomX;
				coords[1] = D.inhomY;
			} else { // D is not defined
				if (B == null) {
					// B and D are not defined
					coords[0] = ax;
					coords[1] = ay + height / yscale;
				} else {
					// B is defined, D isn't
					double nx = ay - B.inhomY;
					double ny = B.inhomX - ax;
					double factor = height / width;
					coords[0] = ax + factor * nx;
					coords[1] = ay + factor * ny;
				}
			}
			break;

		default:
			coords[0] = Double.NaN;
			coords[1] = Double.NaN;
		}
	}

	private boolean initTransformPoints() {
		if (hasAbsoluteScreenLocation || !hasAbsoluteLocation) {
			return false;
		}

		initTempPoints();
		calculateCornerPoint(tempPoints[0], 1);
		calculateCornerPoint(tempPoints[1], 2);
		calculateCornerPoint(tempPoints[2], 4);
		return true;
	}

	/**
	 * rotate this image by angle phi around (0,0)
	 */
	@Override
	final public void rotate(NumberValue phiValue) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			tempPoints[i].rotate(phiValue);
			corners[i] = tempPoints[i];
		}
	}

	/**
	 * rotate this image by angle phi around Q
	 */
	@Override
	final public void rotate(NumberValue phiValue, GeoPointND Q) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			tempPoints[i].rotate(phiValue, Q);
			corners[i] = tempPoints[i];
		}
	}

	@Override
	public void mirror(Coords Q) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			tempPoints[i].mirror(Q);
			corners[i] = tempPoints[i];
		}
	}

	@Override
	public void matrixTransform(double a, double b, double c, double d) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			GeoVec2D vec = tempPoints[i].getVector();
			vec.matrixTransform(a, b, c, d);
			if (corners[i] == null) {
				corners[i] = new GeoPoint(cons);
			}
			corners[i].setCoords(vec);
		}
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void mirror(GeoLineND g) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			tempPoints[i].mirror(g);
			corners[i] = tempPoints[i];
		}
	}

	@Override
	public void translate(Coords v) {
		if (!initTransformPoints()) {
			return;
		}
		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			if (corners[i] != null) {
				tempPoints[i].translate(v);
				corners[i] = tempPoints[i];
			}
		}
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	@Override
	public void dilate(NumberValue r, Coords S) {
		if (!initTransformPoints()) {
			return;
		}

		// calculate the new corner points
		for (int i = 0; i < corners.length; i++) {
			tempPoints[i].dilate(r, S);
			corners[i] = tempPoints[i];
		}
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElementND geo) {
		// return false if it's a different type
		if (!geo.isGeoImage()) {
			return false;
		}

		// check sizes
		if (((GeoImage) geo).pixelWidth != this.pixelWidth) {
			return false;
		}
		if (((GeoImage) geo).pixelHeight != this.pixelHeight) {
			return false;
		}

		String imageFileName = this.getGraphicsAdapter().getImageFileName();
		String md5A = imageFileName.substring(0,
				kernel.getApplication().getMD5folderLength(imageFileName));
		String imageFileName2 = ((GeoImage) geo).getGraphicsAdapter()
				.getImageFileName();
		String md5B = imageFileName2.substring(0,
				kernel.getApplication().getMD5folderLength(imageFileName));
		// MD5 checksums equal, so images almost certainly identical
		if (md5A.equals(md5B)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public boolean hasMoveableInputPoints(EuclidianViewInterfaceSlim view) {

		if (hasAbsoluteLocation()) {
			return false;
		}

		for (int i = 0; i < corners.length; i++) {
			if (corners[i] != null && !corners[i].isMoveable(view)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns all free parent points of this GeoElement.
	 */
	@Override
	public ArrayList<GeoPointND> getFreeInputPoints(
			EuclidianViewInterfaceSlim view) {
		if (hasAbsoluteLocation()) {
			return null;
		}

		if (al == null) {
			al = new ArrayList<GeoPointND>();
		} else {
			al.clear();
		}

		for (int i = 0; i < corners.length; i++) {
			if (corners[i] != null) {
				al.add(corners[i]);
			}
		}

		return al;
	}

	@Override
	final public boolean isAuxiliaryObjectByDefault() {
		return true;
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return !isIndependent();
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		// http://forum.geogebra.org/viewtopic.php?f=8&t=38230
		initTempPoints();

		for (int i = 0; i < corners.length; i++) {
			GeoVec2D vec = tempPoints[i].getVector();
			vec.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
			if (corners[i] == null) {
				corners[i] = new GeoPoint(cons);
			}
			corners[i].setCoords(vec);
		}

	}

	/**
	 * Clears the image
	 */
	public void clearFillImage() {
		this.getGraphicsAdapter()
				.setImageOnly(AwtFactory.getPrototype().newMyImage(pixelWidth,
						pixelHeight, GBufferedImage.TYPE_INT_ARGB));
		this.updateRepaint();

	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	@Override
	public void updateLocation() {
		updateGeo(false);
		kernel.notifyUpdateLocation(this);
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return pixelWidth;
	}

	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return pixelHeight;
	}

	@Override
	public boolean isAlgebraDuplicateable() {
		return false;
	}

	@Override
	public String toLaTeXString(final boolean symbolic, StringTemplate tpl) {
		return getGraphicsAdapter().toLaTeXStringBase64();
	}

	@Override
	public String getFormulaString(final StringTemplate tpl,
			final boolean substituteNumbers) {
		// assume LaTeX
		return toLaTeXString(true, null);
	}

	/**
	 * @return if image is centered.
	 */
	public boolean isCentered() {
		if (!getKernel().getApplication().has(Feature.CENTER_IMAGE)) {
			return false;
		}
		return centered;
	}

	/**
	 * Sets image centered/uncentered. Calling it with false restores the
	 * original position.
	 * 
	 * @param centered
	 *            to set.
	 */
	public void setCentered(boolean centered) {
		if (!getKernel().getApplication().has(Feature.CENTER_IMAGE)) {
			return;
		}

		this.centered = centered;
		if (centered) {
			center();
		} else {
			uncenter();
		}
		updateRepaint();
	}

	private void center() {
		removeCorner(1);
		removeCorner(2);
		corners[CENTER_INDEX] = corners[0];
		corners[0] = null;
	}

	private void uncenter() {
		corners[0] = corners[CENTER_INDEX];
		for (int i = 1; i < 4; i++) {
			corners[i] = null;
		}
	}

	private void removeCorner(int idx) {
		if (corners[idx] == null || corners[idx].hasChildren()) {
			return;
		}
		setCorner(null, idx);
		corners[idx].remove();
		kernel.notifyRemove(corners[idx]);
		corners[idx] = null;
	}
}
