/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.plot.CurvePlotter;
import geogebra.common.euclidian.plot.CurvePlotter.Gap;
import geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import geogebra.common.kernel.AlgoCasCellInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;

/**
 * Draws definite Integral of a GeoFunction
 * 
 * @author Markus Hohenwarter
 */
public class DrawIntegral extends Drawable {

	private GeoNumeric n;
	private GeoFunction f;
	private NumberValue a, b;
	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible, labelVisible;
	private boolean isCasObject;

	/**
	 * Creates new drawable for integral
	 * 
	 * @param view view
	 * @param n integral
	 * @param casObject true if n was created from a GeoCasCell
	 */
	public DrawIntegral(EuclidianView view, GeoNumeric n, boolean casObject) {
		this.view = view;
		this.n = n;
		geo = n;
		isCasObject = casObject;

		n.setDrawable(true);

		init();
		update();
	}

	private void init() {
		if (isCasObject) {
			initFromCasObject();
			return;
		}
		AlgoIntegralDefinite algo = (AlgoIntegralDefinite) n.getDrawAlgorithm();
		f = algo.getFunction();
		a = algo.getA();
		b = algo.getB();
	}
	
	private void initFromCasObject() {
		AlgoCasCellInterface algo = (AlgoCasCellInterface) n.getDrawAlgorithm();
		GeoCasCell cell = algo.getCasCell();
		Command cmd = cell.getInputVE().getTopLevelCommand();
		Kernel kernel = cmd.getKernel();
		f = new GeoFunction(kernel.getConstruction(), new Function(cmd.getArgument(0).wrap().replaceCasCommands()));
		a = new MyDouble(cmd.getKernel(), cmd.getArgument(1).wrap().replaceCasCommands().evaluateDouble());
		b = new MyDouble(cmd.getKernel(), cmd.getArgument(2).wrap().replaceCasCommands().evaluateDouble());
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(n);
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();

		if (gp == null)
			gp = new GeneralPathClippedForCurvePlotter(view);
		else
			gp.reset();

		// init gp
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		// for DrawParametricCurve.plotCurve to work with special values,
		// these changes are needed (also filter out out of screen integrals)
		// see #1234
		aRW = Math.max(aRW, view.getXmin() - EuclidianStatic.CLIP_DISTANCE);
		if (aRW > view.getXmax() + EuclidianStatic.CLIP_DISTANCE)
			return;

		bRW = Math.min(bRW, view.getXmax() + EuclidianStatic.CLIP_DISTANCE);
		if (bRW < view.getXmin() - EuclidianStatic.CLIP_DISTANCE)
			return;

		double ax = view.toScreenCoordXd(aRW);
		double bx = view.toScreenCoordXd(bRW);
		float y0 = (float) view.getyZero();

		// plot definite integral

		if (Kernel.isEqual(aRW, bRW)) {
			gp.moveTo(ax, y0);
			gp.lineTo(ax, view.toScreenCoordYd(f.evaluate(aRW)));
			gp.lineTo(ax, y0);
			return;
		}

		gp.moveTo(ax, y0);
		CurvePlotter.plotCurve(f, aRW, bRW, view, gp, false,
				Gap.LINE_TO);
		gp.lineTo(bx, y0);
		gp.lineTo(ax, y0);

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}

		if (labelVisible) {
			xLabel = (int) Math.round((ax + bx) / 2) - 6;
			yLabel = (int) view.getyZero() - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
	}

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(n.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			fill(g2, gp, true); // fill using default/hatching/image as
								// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(n.getObjectColor());
				g2.setStroke(objStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}

	@Override
	final public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return gp != null && gp.intersects(rect);
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}
}
