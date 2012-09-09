package geogebra.common.euclidian.draw;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Drawable representation of a bar graph
 * 
 */
public class DrawBarGraph extends Drawable {

	public static final int TYPE_VERTICAL_BAR = 0;
	public static final int TYPE_HORIZONTAL_BAR = 1;
	public static final int TYPE_STEP_GRAPH = 2;
	private int drawType = TYPE_VERTICAL_BAR;

	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];
	private GeneralPathClipped gp;
	private GeoNumeric sum;
	private AlgoBarChart algo;

	private GeoPoint[] pts;
	private DrawPoint[] drawPoints;

	/*************************************************
	 * @param view
	 *            view
	 * @param n
	 *            number (bar chart)
	 */
	public DrawBarGraph(EuclidianView view, GeoNumeric n) {
		this.view = view;
		sum = n;
		geo = n;

		n.setDrawable(true);

		init();
		update();
	}

	/**
	 * @return type of graph to draw
	 */
	public int getType() {
		return drawType;
	}

	/**
	 * @param type
	 *            type of graph to draw
	 */
	public void setType(int type) {
		this.drawType = type;
	}

	private void init() {
		algo = (AlgoBarChart) geo.getDrawAlgorithm();

		createPts();
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		return gp.getBounds();
	}

	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			try {
				if (geo.doHighlighting()) {
					g2.setPaint(sum.getSelColor());
					g2.setStroke(selStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
			}

			try {
				if (algo.getDrawType() != TYPE_STEP_GRAPH) {
					fill(g2, gp, false); // fill using default/hatching/image as
											// appropriate
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (geo.lineThickness > 0) {
					g2.setPaint(sum.getObjectColor());
					g2.setStroke(objStroke);
					g2.draw(gp);
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}

			// point
			if (algo.hasPoints()) {
				for (int i = 0; i < algo.getIntervals(); i++) {
					drawPoints[i].draw(g2);
				}
			}
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		return gp != null
				&& (gp.contains(x, y) || gp.intersects(x - 3, y - 3, 6, 6));
	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public void update() {

		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		if (!geo.getDrawAlgorithm().equals(geo.getParentAlgorithm()))
			init();
		labelVisible = geo.isLabelVisible();
		updateStrokes(sum);

		if (gp == null)
			gp = new GeneralPathClipped(view);
		// init gp
		gp.reset();

		double[] xVal = algo.getLeftBorder();
		double[] yVal = algo.getValues();

		double width = algo.getWidth();
		int N = algo.getIntervals();

		drawType = algo.getDrawType();

		for (int i = 0; i < N; i++) {
			coords[0] = xVal[i];
			coords[1] = yVal[i];
			pts[i].setCoords(coords[0], coords[1], 1.0);
			pts[i].setPointSize(2 + (geo.lineThickness + 1) / 3);
			drawPoints[i].update();
		}

		switch (drawType) {

		case TYPE_VERTICAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}
			}

			break;

		case TYPE_HORIZONTAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp.lineTo(coords[0], coords[1]);
				}
			}
			break;

		case TYPE_STEP_GRAPH:

			double halfWidth = width / 2;

			for (int i = 0; i < N - 1; i++) {

				// move to start point
				coords[0] = xVal[i] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp.moveTo(coords[0], coords[1]);

				// across
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp.lineTo(coords[0], coords[1]);

				// up
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i + 1];
				view.toScreenCoords(coords);
				gp.lineTo(coords[0], coords[1]);
			}

			// up to last point
			coords[0] = xVal[N - 1] + halfWidth;
			coords[1] = yVal[N - 1];
			view.toScreenCoords(coords);
			gp.lineTo(coords[0], coords[1]);

			break;
		}

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// off screen points too
		}

		// TODO: improve label position
		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	private void createPts() {

		pts = new GeoPoint[algo.getIntervals()];
		drawPoints = new DrawPoint[algo.getIntervals()];

		for (int i = 0; i < pts.length; i++) {
			pts[i] = new GeoPoint(view.getKernel().getConstruction());
			pts[i].setPointStyle(EuclidianStyleConstants.POINT_STYLE_DOT);
			pts[i].setLabelVisible(false);
			
			drawPoints[i] = new DrawPoint(view, pts[i]);
			drawPoints[i].setGeoElement(pts[i]);
			
		}

	}

}
