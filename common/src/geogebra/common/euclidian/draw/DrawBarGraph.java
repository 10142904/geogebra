package geogebra.common.euclidian.draw;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.FillType;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;

import java.util.ArrayList;

/**
 * Drawable representation of a bar graph
 * 
 */
public class DrawBarGraph extends Drawable {

	// graph types
	public static final int DRAW_VERTICAL_BAR = 0;
	public static final int DRAW_HORIZONTAL_BAR = 1;
	public static final int DRAW_STEP_GRAPH_CONTINUOUS = 2;
	public static final int DRAW_STEP_GRAPH_JUMP = 3;
	private int drawType = DRAW_VERTICAL_BAR;

	// point types
	public static final int POINT_RIGHT = 1;
	public static final int POINT_RIGHT_OPEN_LEFT = 2;
	public static final int POINT_NONE = 0;
	public static final int POINT_LEFT = -1;
	public static final int POINT_LEFT_OPEN_RIGHT = -2;
	private int pointType = POINT_NONE;

	private boolean isVisible, labelVisible;
	private double[] coords = new double[2];
	/*
	 * Use an array  to  customize  bars
	 */
	private GeneralPathClipped []gp;
	private GeoNumeric sum;
	private AlgoBarChart algo;
	private ArrayList<GeoPoint> pts;
	private ArrayList<DrawPoint> drawPoints;

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
		drawType = algo.getDrawType();

		if (algo.hasPoints()) {
			pts = new ArrayList<GeoPoint>();
			drawPoints = new ArrayList<DrawPoint>();
			updatePointLists();
		}
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !geo.isEuclidianVisible()) {
			return null;
		}
		GRectangle rect=geogebra.common.factories.AwtFactory.prototype.newRectangle(
				(int)(algo.getLeftBorder()[0]),(int)algo.getFreqMax(),
				(int)(algo.getLeftBorder().length*algo.getWidth()),(int)algo.getFreqMax());
		return rect;
	}

	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {
		//Save fill, color and alfa of object 
		GColor color = geo.getSelColor();
		FillType fillType=geo.getFillType();
		int hatchingDistance=geo.getHatchingDistance();
		String symbol=geo.getFillSymbol();
		double hatchingAngle=geo.getHatchingAngle();
		String fileName=geo.getImageFileName();
		float alpha=geo.getAlphaValue();
		int k;
		if (isVisible) {
			try {
				if (geo.doHighlighting()) {
					g2.setPaint(sum.getSelColor());
					g2.setStroke(selStroke);
					for(int i=0;i<gp.length;i++){
						k=i+1;
						if (geo.getTag("barColor"+k) != null) {
							String []rgb=geo.getTag("barColor"+k).split("_");
							g2.setPaint(AwtFactory.prototype.newColor(Float.parseFloat(rgb[0]),Float.parseFloat(rgb[1])
									, Float.parseFloat(rgb[2]),Float.parseFloat(rgb[3])));
						}
						g2.draw(gp[i]);
						g2.setPaint(color);
					}
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
			}

			try {
				if (algo.getDrawType() != DRAW_STEP_GRAPH_CONTINUOUS) {
					/*
					 * Use tags for draw if there are
					 */
					for (int i=0;i<gp.length;i++) {
						k=i+1;
						if (geo.getTag("barColor"+k)!=null) {
							String []rgb=geo.getTag("barColor"+k).split("_");
							geo.setObjColor(AwtFactory.prototype.newColor(Float.parseFloat(rgb[0]),Float.parseFloat(rgb[1])
									, Float.parseFloat(rgb[2]),Float.parseFloat(rgb[3])));
							geo.setAlphaValue(Float.parseFloat(rgb[3]));
						}
						if (geo.getTag("barAlpha"+k)!=null) {
							geo.setAlphaValue(Float.parseFloat(geo.getTag("barAlpha"+k)));
						}
						if (geo.getTag("barFillType"+k)!=null) {
							geo.setFillType(FillType.values()[Integer.parseInt((geo.getTag("barFillType"+k)))]);
						}
						if (geo.getTag("barSymbol"+k)!=null) {
							geo.setFillSymbol(geo.getTag("barSymbol"+k));
						} 
						if (geo.getTag("barImage"+k)!=null) {
							geo.setImageFileName(geo.getTag("barImage"+k));
						}
						if (geo.getTag("barHatchDistance"+k)!=null) {
							geo.setHatchingDistance(Integer.parseInt((geo.getTag("barHatchDistance"+k))));
						}
						if (geo.getTag("barHatchAngle"+k)!=null) {
							geo.setHatchingAngle(Integer.parseInt((geo.getTag("barHatchAngle"+k))));
						}
						
						fill(g2, gp[i], false); // fill using default/hatching/image as
											// appropriate
						//Restore values
						geo.setObjColor(color);
						geo.setFillType(fillType);
						geo.setHatchingAngle((int)hatchingAngle);
						geo.setHatchingDistance(hatchingDistance);
						geo.setFillSymbol(symbol);
						geo.setImageFileName(fileName);
						geo.setAlphaValue(alpha);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (geo.lineThickness > 0) {
					g2.setPaint(sum.getObjectColor());
					g2.setStroke(objStroke);
					for(int i=0;i<gp.length;i++) {
						k=i+1;
						if (geo.getTag("barColor"+k)!=null) {
							String []rgb=geo.getTag("barColor"+k).split("_");
							g2.setPaint(AwtFactory.prototype.newColor(Float.parseFloat(rgb[0]),Float.parseFloat(rgb[1])
									, Float.parseFloat(rgb[2])));
						}
						g2.draw(gp[i]);
						g2.setPaint(color);
					}
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
				for (int i = 0; i < drawPoints.size(); i++) {
					drawPoints.get(i).draw(g2);
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
		if (gp != null){
			for(int i=0;i<gp.length;i++){
				if ((gp[i].contains(x, y) || gp[i].intersects(x - 3, y - 3, 6, 6))){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (gp != null ){
			for(int i=0;i<gp.length;i++){
				if (gp[i].intersects(rect)){
					return true;
				}
			}
		}
		return false;
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
			
		// init gp
		gp = new GeneralPathClipped[algo.getIntervals()];
		for (int i = 0; i < gp.length; i++) {
			gp[i] = new GeneralPathClipped(view);
			gp[i].reset();
		}

		double[] xVal = algo.getLeftBorder();
		double[] yVal = algo.getValues();

		double width = algo.getWidth();
		int N = algo.getIntervals();

		if (algo.hasPoints()) {
			updatePointLists();
		}

		drawType = algo.getDrawType();
		pointType = algo.getPointType();
		int pointStyle;

		if (algo.hasPoints() && pointType != POINT_NONE) {

			if (pointType == POINT_LEFT || pointType == POINT_LEFT_OPEN_RIGHT) {
				pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			} else {
				pointStyle = EuclidianStyleConstants.POINT_STYLE_CIRCLE;
			}

			for (int i = 0; i < N; i++) {
				coords[0] = xVal[i];
				coords[1] = yVal[i];
				pts.get(i).setCoords(coords[0], coords[1], 1.0);
				pts.get(i).setObjColor(geo.getObjectColor());
				pts.get(i).setPointSize(2 + (geo.lineThickness + 1) / 3);
				pts.get(i).setPointStyle(pointStyle);
				if (pointType == POINT_RIGHT) {
					pts.get(i).setEuclidianVisible(false);
				}
				drawPoints.get(i).update();
			}

			if (drawType == DRAW_STEP_GRAPH_CONTINUOUS
					|| drawType == DRAW_STEP_GRAPH_JUMP) {

				if (pointType == POINT_LEFT
						|| pointType == POINT_LEFT_OPEN_RIGHT) {
					pointStyle = EuclidianStyleConstants.POINT_STYLE_CIRCLE;
				} else {
					pointStyle = EuclidianStyleConstants.POINT_STYLE_DOT;
				}

				// step graph right points
				for (int i = 0; i < N - 1; i++) {
					coords[0] = xVal[i + 1];
					coords[1] = yVal[i];
					pts.get(N + i).setCoords(coords[0], coords[1], 1.0);
					pts.get(N + i).setObjColor(geo.getObjectColor());
					pts.get(N + i)
							.setPointSize(2 + (geo.lineThickness + 1) / 3);
					pts.get(N + i).setPointStyle(pointStyle);
					if (pointType == POINT_LEFT) {
						pts.get(N + i).setEuclidianVisible(false);
					}
					drawPoints.get(N + i).update();
				}
			}

		}

		double halfWidth = width / 2;

		switch (drawType) {

		case DRAW_VERTICAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i] + width;
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = 0;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}
			}

			break;

		case DRAW_HORIZONTAL_BAR:

			if (width <= 0) {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}

			} else {
				for (int i = 0; i < N; i++) {
					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].moveTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = xVal[i];
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i] + width;
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);

					coords[0] = 0;
					coords[1] = yVal[i];
					view.toScreenCoords(coords);
					gp[i].lineTo(coords[0], coords[1]);
				}
			}
			break;

		case DRAW_STEP_GRAPH_CONTINUOUS:

			for (int i = 0; i < N - 1; i++) {

				// move to start point
				coords[0] = xVal[i] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].moveTo(coords[0], coords[1]);

				// across
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);

				// up
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i + 1];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);
			}

			// up to last point
			coords[0] = xVal[N - 1] + halfWidth;
			coords[1] = yVal[N - 1];
			view.toScreenCoords(coords);
			gp[gp.length-1].lineTo(coords[0], coords[1]);

			break;

		case DRAW_STEP_GRAPH_JUMP:

			for (int i = 0; i < N - 1; i++) {

				// move to start point
				coords[0] = xVal[i] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].moveTo(coords[0], coords[1]);

				// across
				coords[0] = xVal[i + 1] + halfWidth;
				coords[1] = yVal[i];
				view.toScreenCoords(coords);
				gp[i].lineTo(coords[0], coords[1]);

			}

			break;
		}

		// gp on screen?
		isVisible = false;
		// don't return here to make sure that getBounds() works for
		// off screen points too
		for (int i=0;i<gp.length;i++){
			if (gp[i].intersects(0, 0, view.getWidth(), view.getHeight())) {				
				isVisible=true;
				break;
			}
		}
		// TODO: improve label position
		if (labelVisible) {
			xLabel = (int) coords[0];
			yLabel = (int) coords[1] - view.getFontSize();
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

	}

	private void updatePointLists() {

		// find the number of points to draw
		int n;
		if (drawType == DRAW_STEP_GRAPH_CONTINUOUS
				|| drawType == DRAW_STEP_GRAPH_JUMP) {
			n = 2 * algo.getIntervals() - 1;
		} else {
			n = algo.getIntervals();
		}

		// adjust the lists
		if (n > pts.size()) {
			// add 
			for (int i = pts.size(); i < n; i++) {
				addPt();
			}
		} else if (n < pts.size()) {
			// remove 
			for (int i = n; n < pts.size(); i++) {
				pts.remove(i);
				drawPoints.remove(i);
			}
		}
	}

	private void addPt() {

		GeoPoint p = new GeoPoint(view.getKernel().getConstruction());
		p.setLabelVisible(false);
		DrawPoint d = new DrawPoint(view, p);
		d.setGeoElement(p);

		pts.add(p);
		drawPoints.add(d);
	}

}
