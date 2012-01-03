/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.common.euclidian;

import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;


/**
 * 
 * @author Markus
 * @version
 */
public final class DrawPoint extends Drawable {

	private int HIGHLIGHT_OFFSET, SELECTION_OFFSET;

	// used by getSelectionDiamaterMin()
	private static int SELECTION_DIAMETER_MIN = 25;

	private GeoPointND P;

	private int diameter, hightlightDiameter, selDiameter, pointSize;
	private boolean isVisible, labelVisible;
	// for dot and selection
	private geogebra.common.awt.Ellipse2DDouble circle = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
	private geogebra.common.awt.Ellipse2DDouble circleHighlight = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
	private geogebra.common.awt.Ellipse2DDouble circleSel = geogebra.common.factories.AwtFactory.prototype.newEllipse2DDouble();
	private geogebra.common.awt.Line2D line1, line2, line3, line4;// for cross
	private geogebra.common.awt.GeneralPath gp = null;

	private static geogebra.common.awt.BasicStroke borderStroke = EuclidianStatic
			.getDefaultStroke();
	private static geogebra.common.awt.BasicStroke[] crossStrokes = new geogebra.common.awt.BasicStroke[10];

	private boolean isPreview;

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view
	 * @param P
	 */
	public DrawPoint(EuclidianViewInterface2D view, GeoPointND P) {
		this(view, P, false);
	}

	/**
	 * Creates new DrawPoint
	 * 
	 * @param view
	 * @param P
	 * @param isPreview
	 */
	public DrawPoint(EuclidianViewInterface2D view, GeoPointND P, boolean isPreview) {
		this.view = view;
		this.P = P;
		geo = (GeoElement) P;

		this.isPreview = isPreview;

		// crossStrokes[1] = new BasicStroke(1f);

		update();
	}

	@Override
	final public void update() {

		if (gp != null)
			gp.reset(); // stop trace being left when (filled diamond) point
						// moved

		isVisible = geo.isEuclidianVisible();

		double[] coords = new double[2];
		if (isPreview) {
			Coords p = P.getInhomCoordsInD(2);
			coords[0] = p.getX();
			coords[1] = p.getY();
		} else {
			// looks if it's on view
			Coords p = view.getCoordsForView(P.getInhomCoordsInD(3));
			if (!Kernel.isZero(p.getZ())) {
				isVisible = false;
			} else {
				coords[0] = p.getX();
				coords[1] = p.getY();
			}
		}

		// still needs updating if it's being traced to the spreadsheet
		if (!isVisible && !P.getSpreadsheetTrace())
			return;
		labelVisible = geo.isLabelVisible();

		// convert to screen
		view.toScreenCoords(coords);

		// point outside screen?
		if (Double.isNaN(coords[0]) || Double.isNaN(coords[1])) { // fix for #63
			isVisible = false;
		} else if (coords[0] > view.getWidth() + P.getPointSize()
				|| coords[0] < -P.getPointSize()
				|| coords[1] > view.getHeight() + P.getPointSize()
				|| coords[1] < -P.getPointSize()) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (pointSize != P.getPointSize()) {
			pointSize = P.getPointSize();
			diameter = 2 * pointSize;
			HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			// HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			hightlightDiameter = diameter + 2 * HIGHLIGHT_OFFSET + 1;

			selDiameter = hightlightDiameter;

			if (selDiameter < getSelectionDiamaterMin())
				selDiameter = getSelectionDiamaterMin();

			SELECTION_OFFSET = (selDiameter - diameter) / 2;

		}

		double xUL = (coords[0] - pointSize);
		double yUL = (coords[1] - pointSize);

		// Math.round to make sure that highlight circles drawn symmetrically
		// (eg after zoom when points aren't on pixel boundaries)
		if (!view.getApplication().isExporting()) {
			xUL = Math.round(xUL);
			yUL = Math.round(yUL);
		}

		// Florian Sonner 2008-07-17
		int pointStyle = P.getPointStyle();

		if (pointStyle == -1)
			pointStyle = view.getPointStyle();

		double root3over2;

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			double xR = coords[0] + pointSize;
			double yB = coords[1] + pointSize;

			if (gp == null) {
				gp = AwtFactory.prototype.newGeneralPath();
			}
			gp.moveTo((float) (xUL + xR) / 2, (float) yUL);
			gp.lineTo((float) xUL, (float) (yB + yUL) / 2);
			gp.lineTo((float) (xUL + xR) / 2, (float) yB);
			gp.lineTo((float) xR, (float) (yB + yUL) / 2);
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

			double direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH)
				direction = -1.0;

			if (gp == null) {
				gp = AwtFactory.prototype.newGeneralPath();;
			}
			root3over2 = Math.sqrt(3.0) / 2.0;
			gp.moveTo((float) coords[0], (float) (coords[1] + direction
					* pointSize));
			gp.lineTo((float) (coords[0] + pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) (coords[0] - pointSize * root3over2),
					(float) (coords[1] - direction * pointSize / 2));
			gp.lineTo((float) coords[0], (float) (coords[1] + direction
					* pointSize));
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

			direction = 1.0;
			if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST)
				direction = -1.0;

			if (gp == null) {
				gp = AwtFactory.prototype.newGeneralPath();
			}
			root3over2 = Math.sqrt(3.0) / 2.0;
			gp.moveTo((float) (coords[0] + direction * pointSize),
					(float) coords[1]);
			gp.lineTo((float) (coords[0] - direction * pointSize / 2),
					(float) (coords[1] + pointSize * root3over2));
			gp.lineTo((float) (coords[0] - direction * pointSize / 2),
					(float) (coords[1] - pointSize * root3over2));
			gp.lineTo((float) (coords[0] + direction * pointSize),
					(float) coords[1]);
			gp.closePath();
			break;

		case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
				line2 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
			}
			if (line3 == null) {
				line3 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
				line4 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
			}
			line1.setLine((xUL + xR) / 2, yUL, xUL, (yB + yUL) / 2);
			line2.setLine(xUL, (yB + yUL) / 2, (xUL + xR) / 2, yB);
			line3.setLine((xUL + xR) / 2, yB, xR, (yB + yUL) / 2);
			line4.setLine(xR, (yB + yUL) / 2, (xUL + xR) / 2, yUL);
			break;

		case EuclidianStyleConstants.POINT_STYLE_PLUS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
				line2 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
			}
			line1.setLine((xUL + xR) / 2, yUL, (xUL + xR) / 2, yB);
			line2.setLine(xUL, (yB + yUL) / 2, xR, (yB + yUL) / 2);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CROSS:
			xR = coords[0] + pointSize;
			yB = coords[1] + pointSize;

			if (line1 == null) {
				line1 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
				line2 = geogebra.common.factories.AwtFactory.prototype.newLine2D();
			}
			line1.setLine(xUL, yUL, xR, yB);
			line2.setLine(xUL, yB, xR, yUL);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			break;

		// case EuclidianStyleConstants.POINT_STYLE_DOT:
		// default:
		}

		// circle might be needed at least for tracing
		circle.setFrame(xUL, yUL, diameter, diameter);

		// selection area
		circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET,
				yUL - HIGHLIGHT_OFFSET, hightlightDiameter, hightlightDiameter);

		circleSel.setFrame(xUL - SELECTION_OFFSET, yUL - SELECTION_OFFSET,
				selDiameter, selDiameter);

		// G.Sturr 2010-6-28 spreadsheet trace is now handled in
		// GeoElement.update()
		// if (P.getSpreadsheetTrace()) recordToSpreadsheet(P);
		if (P == view.getEuclidianController().getRecordObject())
			recordToSpreadsheet((GeoElement) P);

		// draw trace
		if (P.getTrace()) {
			isTracing = true;
			geogebra.common.awt.Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}

		if (isVisible && labelVisible) {
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int) Math.round(yUL - pointSize);
			addLabelOffset(true);
		}
	}

	private Drawable drawable;

	private void drawClippedSection(GeoElement geo, geogebra.common.awt.Graphics2D g2) {

		switch (geo.getGeoClassType()) {
		case LINE:
			drawable = new DrawLine(view, (GeoLine) geo);
			break;
		case SEGMENT:
			drawable = new DrawSegment(view, (GeoSegment) geo);
			break;
		case CONIC:
			drawable = new DrawConic(view, (GeoConic) geo);
			break;
		case FUNCTION:
			drawable = new DrawParametricCurve(view, (GeoFunction) geo);
			break;
		case AXIS:
			drawable = null;
			break;
		case CONICPART:
			drawable = new DrawConicPart(view, (GeoConicPart) geo);
			break;

		default:
			drawable = null;
			AbstractApplication.debug("unsupported type for restriced drawing "
					+ geo.getClass() + "");
		}

		if (drawable != null) {
			double[] coords = new double[2];
			P.getInhomCoords(coords);

			view.toScreenCoords(coords);

			geogebra.common.awt.Ellipse2DFloat circle = geogebra.common.factories.AwtFactory.prototype.newEllipse2DFloat((int) coords[0] - 30,
					(int) coords[1] - 30, 60, 60);
			g2.clip(circle);
			geo.forceEuclidianVisible(true);
			drawable.update();
			drawable.draw(g2);
			geo.forceEuclidianVisible(false);
			g2.setClip(null);
		}
	}

	@Override
	final public void draw(geogebra.common.awt.Graphics2D g2) {

		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.fill(circleHighlight);
			}

			// option "show trimmed intersecting lines"
			if (geo.getShowTrimmedIntersectionLines()) {
				AlgoElement algo = geo.getParentAlgorithm();

				if (algo instanceof AlgoIntersectAbstract) {
					GeoElement[] geos = algo.getInput();
					drawClippedSection(geos[0], g2);
					if (geos.length > 1)
						drawClippedSection(geos[1], g2);
				}
			}

			// Florian Sonner 2008-07-17
			int pointStyle = P.getPointStyle();

			if (pointStyle == -1)
				pointStyle = view.getPointStyle();

			switch (pointStyle) {
			case EuclidianStyleConstants.POINT_STYLE_PLUS:
			case EuclidianStyleConstants.POINT_STYLE_CROSS:
				// draw cross like: X or +
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getCrossStroke(pointSize));
				g2.draw(line1);
				g2.draw(line2);
				break;

			case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
				// draw diamond
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getCrossStroke(pointSize));
				g2.draw(line1);
				g2.draw(line2);
				g2.draw(line3);
				g2.draw(line4);
				break;

			case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
				// draw diamond
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getCrossStroke(pointSize));
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
				g2.fill(gp);
				break;

			case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
				// draw a circle
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(getCrossStroke(pointSize));
				g2.draw(circle);
				break;

			// case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			default:
				// draw a dot
				g2.setPaint(geo.getObjectColor());
				g2.fill(circle);

				// black stroke
				g2.setPaint(geogebra.common.awt.Color.black);
				g2.setStroke(borderStroke);
				g2.draw(circle);
			}

			// label
			if (labelVisible) {
				g2.setFont(view.getFontPoint());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	/**
	 * Draw trace of point
	 * 
	 * @param g2
	 *            graphics to be used
	 */
	final void drawTrace(geogebra.common.awt.Graphics2D g2) {
		g2.setPaint(geo.getObjectColor());

		// Florian Sonner 2008-07-17
		int pointStyle = P.getPointStyle();

		switch (pointStyle) {
		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			g2.setStroke(getCrossStroke(pointSize));
			g2.draw(circle);
			break;

		case EuclidianStyleConstants.POINT_STYLE_CROSS:
		default: // case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			g2.fill(circle);
		}
	}

	/**
	 * was this object clicked at? (mouse pointer location (x,y) in screen
	 * coords)
	 */
	@Override
	final public boolean hit(int x, int y) {
		return circleSel.contains(x, y);
	}

	@Override
	final public boolean isInside(geogebra.common.awt.Rectangle rect) {
		return rect.contains(circle.getBounds());
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.Rectangle getBounds() {
		// return selection circle's bounding box
		if (!geo.isEuclidianVisible()) {
			return null;
		}
		return circleSel.getBounds();
	}

	@Override
	final public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	final public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/*
	 * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
	 */
	final private static geogebra.common.awt.BasicStroke getCrossStroke(int pointSize) {

		if (pointSize > 9)
			return AwtFactory.prototype.newBasicStroke(pointSize / 2f);

		if (crossStrokes[pointSize] == null)
			crossStrokes[pointSize] = AwtFactory.prototype.newBasicStroke(pointSize / 2f);

		return crossStrokes[pointSize];
	}

	private int getSelectionDiamaterMin() {
		return view.getCapturingThreshold() + SELECTION_DIAMETER_MIN;
	}

}
