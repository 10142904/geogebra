/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.gui.view.functioninspector;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.advanced.AlgoCurvature;
import geogebra.common.kernel.advanced.AlgoOsculatingCircle;
import geogebra.common.kernel.algos.AlgoDependentFunction;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFunctionInterval;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.AlgoRoots;
import geogebra.common.kernel.algos.AlgoRootsPolynomial;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoDerivative;
import geogebra.common.kernel.cas.AlgoIntegralDefinite;
import geogebra.common.kernel.cas.AlgoLengthFunction;
import geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.optimization.ExtremumFinder;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.Localization;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;


/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class FunctionInspectorModel {

	public interface IFunctionInspectorListener {

		void updateXYTable(boolean isTable);

		String format(double value);

		void updateInterval(ArrayList<String> property, ArrayList<String> value);

		void setXYValueAt(Double value, int row, int col);

		Object getXYValueAt(int row, int col);

		void addTableColumn(String name);

		void setGeoName(String name);

		void changeTableSelection();

		void updateHighAndLow(boolean isAscending, boolean isLowSelected);

		void setStepText(String text);
		
		void setStepVisible(boolean isVisible);
		
		GColor getColor(Colors id);

		int getSelectedXYRow();
		

	}

	// ggb fields
	private App app;
	private Kernel kernel;
	private Construction cons;
	private EuclidianView activeEV;
	private IFunctionInspectorListener listener;
	protected final Localization loc;
	public enum Colors {GEO, GEO2, EVEN_ROW, GRID};

	// column types
	private static final int COL_DERIVATIVE = 0;
	private static final int COL_DERIVATIVE2 = 1;
	private static final int COL_DIFFERENCE = 2;
	private static final int COL_CURVATURE = 3;

	// list to store column types of dynamically appended columns
	private ArrayList<Integer> extraColumnList;

	// Geos
	private GeoElement tangentLine, oscCircle, xSegment, ySegment;
	private GeoElement functionInterval, integralGeo, lengthGeo, areaGeo;
	private GeoFunction derivative, derivative2, selectedGeo;
	private GeoPoint testPoint, lowPoint, highPoint, minPoint, maxPoint;
	private GeoList pts;

	private ArrayList<GeoElement> intervalTabGeoList, pointTabGeoList,
	hiddenGeoList;
	private GeoElement[] rootGeos;

	// stores lists of column data from the point panel table
	private ArrayList<Double[]> xyTableCopyList = new ArrayList<Double[]>();

	private double xMin, xMax, start = -1, step = 0.1;
	private double initialX;

	//	private boolean isChangingValue;
	private int pointCount = 9;

	private GeoElementSelectionListener sl;


	private ArrayList<String> property = new ArrayList<String>();
	private ArrayList<String> value = new ArrayList<String>();
	// store number values for copy
	private ArrayList<Double[]> value2 = new ArrayList<Double[]>();
	private String[] columnNames;

	/***************************************************************
	 * Constructs a FunctionInspecor
	 * 
	 * @param app
	 * @param selectedGeo
	 */
	public FunctionInspectorModel(App app, GeoFunction selectedGeo, IFunctionInspectorListener listener) {

		this.app = app;
		loc = app.getLocalization();
		kernel = app.getKernel();
		this.listener = listener;
		cons = kernel.getConstruction();
		
		extraColumnList = new ArrayList<Integer>();

		// lists of all geos we create
		intervalTabGeoList = new ArrayList<GeoElement>();
		pointTabGeoList = new ArrayList<GeoElement>();
		hiddenGeoList = new ArrayList<GeoElement>();


		activeEV = (EuclidianView) app.getActiveEuclidianView();
		createGeoElementSlectionListener();

		// load selected function
		this.selectedGeo = selectedGeo;
		columnNames = new String[4];
		setColumnNames();
	}

	public void setColumnNames() {
		columnNames[COL_DERIVATIVE] = loc.getPlain("fncInspector.Derivative");
		columnNames[COL_DERIVATIVE2] = loc.getPlain("fncInspector.Derivative2");
		columnNames[COL_CURVATURE] = loc.getPlain("fncInspector.Curvature");
		columnNames[COL_DIFFERENCE] = loc.getPlain("fncInspector.Difference");
	}

	public String getColumnName(int col) {
		return col < columnNames.length ? columnNames[col] : "-";
	}
	public String getTitleString() {

		if (selectedGeo == null)
			return loc.getMenu("SelectObject");
		return selectedGeo.getAlgebraDescriptionDefault();
	}
	// =====================================
	// Update
	// =====================================

	public void updatePoints(boolean isTangent, boolean isOscCircle, 
			boolean isXYSegments, boolean isTable){

		tangentLine.setEuclidianVisible(isTangent);
		tangentLine.update();
		oscCircle.setEuclidianVisible(isOscCircle);
		oscCircle.update();
		xSegment.setEuclidianVisible(isXYSegments);
		xSegment.update();
		ySegment.setEuclidianVisible(isXYSegments);
		ySegment.update();
		pts.setEuclidianVisible(isTable);
		pts.updateRepaint();
		listener.setStepVisible(isTable);
		listener.updateXYTable(isTable);
	}

	/**
	 * Updates the tab panels and thus the entire GUI. Also updates the active EV to
	 * hide/show temporary GeoElements associated with the FunctionInspector
	 * (e.g. points, integral)
	 */
	public void updateGeos(boolean isInterval) {

		for (GeoElement geo : intervalTabGeoList) {
			geo.setEuclidianVisible(isInterval);
			geo.update();
		}
		for (GeoElement geo : pointTabGeoList) {
			geo.setEuclidianVisible(!isInterval);
			geo.update();
		}

		activeEV.repaint();


	}

	public GeoPoint getLowPoint() {
		return lowPoint;
	}

	public GeoPoint getHighPoint() {
		return highPoint;
	}


	/**
	 * Updates the interval table. The max, min, roots, area etc. for the
	 * current interval are calculated and put into the IntervalTable model.
	 */
	public void updateIntervalTable() {

		property.clear();
		value.clear();
		value2.clear();

		// prepare algos and other objects needed for the calcs
		// =======================================================

		double[] coords = new double[3];
		lowPoint.getCoords(coords);
		xMin = coords[0];
		highPoint.getCoords(coords);
		xMax = coords[0];

		ExtremumFinder ef = new ExtremumFinder();
		RealRootFunction fun = selectedGeo.getRealRootFunctionY();

		// get the table
		double integral = ((GeoNumeric) integralGeo).getDouble();
		double area = ((GeoNumeric) areaGeo).getDouble();
		double mean = integral / (xMax - xMin);
		double length = ((GeoNumeric) lengthGeo).getDouble();

		double yMin = selectedGeo.evaluate(xMin);
		double yMax = selectedGeo.evaluate(xMax);
		double xMinInt = ef.findMinimum(xMin, xMax, fun, 5.0E-8);
		double xMaxInt = ef.findMaximum(xMin, xMax, fun, 5.0E-8);
		double yMinInt = selectedGeo.evaluate(xMinInt);
		double yMaxInt = selectedGeo.evaluate(xMaxInt);

		if (yMin < yMinInt) {
			yMinInt = yMin;
			xMinInt = xMin;
		}

		if (yMax > yMaxInt) {
			yMaxInt = yMax;
			xMaxInt = xMax;
		}

		minPoint.setCoords(xMinInt, yMinInt, 1.0);
		// minPoint.setEuclidianVisible(!(minPoint.isEqual(lowPoint) ||
		// minPoint.isEqual(highPoint)));
		minPoint.update();
		maxPoint.setCoords(xMaxInt, yMaxInt, 1.0);
		// maxPoint.setEuclidianVisible(!(maxPoint.isEqual(lowPoint) ||
		// maxPoint.isEqual(highPoint)));
		maxPoint.update();

		// set the property/value pairs
		// =================================================

		property.add(loc.getCommand("Min"));
		value.add("(" + listener.format(xMinInt) + " , " + listener.format(yMinInt) + ")");
		Double[] min = { xMinInt, yMinInt };
		value2.add(min);

		property.add(loc.getCommand("Max"));
		value.add("(" + listener.format(xMaxInt) + " , " + listener.format(yMaxInt) + ")");
		Double[] max = { xMaxInt, yMaxInt };
		value2.add(max);

		property.add(null);
		value.add(null);
		value2.add(null);

		// calculate roots
		ExpressionNode low = new ExpressionNode(kernel, lowPoint,
				Operation.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint,
				Operation.XCOORD, null);
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);

		AlgoElement roots;

		if (selectedGeo.isPolynomialFunction(false)) {
			roots = new AlgoRootsPolynomial(cons, selectedGeo);
		} else {
			roots = new AlgoRoots(cons, selectedGeo,
					(GeoNumeric) xLow.getGeoElements()[0],
					(GeoNumeric) xHigh.getGeoElements()[0]);
		}

		cons.removeFromConstructionList(roots);
		rootGeos = roots.getGeoElements();

		property.add(loc.getCommand("Root"));

		int count = 0;
		double root = Double.NaN;

		// count how many roots in range
		for (int i = 0; i < rootGeos.length; i++) {
			GeoPoint p = ((GeoPoint) rootGeos[i]);
			if (p.isDefined()) {
				double rt = p.inhomX;
				if (Kernel.isGreaterEqual(rt, xMin)
						&& Kernel.isGreaterEqual(xMax, rt)) {
					root = rt;
					count++;
				}
			}
		}
		StringTemplate tpl = StringTemplate.defaultTemplate;
		switch (count) {
		case 0:
			value.add(loc.getPlain("fncInspector.NoRoots"));
			value2.add(null);
			break;
		case 1:
			value.add(kernel.format(root, tpl));
			Double[] r = { root };
			value2.add(r);
			break;
		default:
			value.add(loc.getPlain("fncInspector.MultipleRoots"));
			value2.add(null);

		}

		property.add(null);
		value.add(null);
		value2.add(null);

		property.add(loc.getCommand("Integral"));
		value.add(listener.format(integral));
		Double[] in = { integral };
		value2.add(in);

		property.add(loc.getCommand("Area"));
		value.add(listener.format(area));
		Double[] a = { area };
		value2.add(a);

		property.add(loc.getCommand("Mean"));
		value.add(listener.format(mean));
		Double[] m = { mean };
		value2.add(m);

		property.add(loc.getCommand("Length"));
		value.add(listener.format(length));
		Double[] l = { length };
		value2.add(l);

		listener.updateInterval(property, value);


	}

	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	public void updateXYTable(int rowCount, boolean isTable) {


		// String lbl = selectedGeo.getLabel();
		GeoFunction f = selectedGeo;

		// init the copy array
		xyTableCopyList.clear();
		Double[] xArray = new Double[rowCount];
		Double[] yArray = new Double[rowCount];

		if (isTable) {
			double x = start - step * (pointCount - 1) / 2;
			double y;
			for (int i = 0; i < rowCount; i++) {
				y = f.evaluate(x);
				listener.setXYValueAt(x, i, 0);
				listener.setXYValueAt(y, i, 1);
				((GeoPoint) pts.get(i)).setCoords(x, y, 1);

				// collect x, y points into the copy arrays
				xArray[i] = x;
				yArray[i] = y;

				x = x + step;
			}

			pts.updateRepaint();
		} else {
			double x = start;
			double y = f.evaluate(x);
			listener.setXYValueAt(x, 0, 0);
			listener.setXYValueAt(y, 0, 1);

			// collect x, y points into the copy arrays
			xArray[0] = x;
			yArray[0] = y;

		}

		xyTableCopyList.add(xArray);
		xyTableCopyList.add(yArray);

		// update any extra columns added by the user (these will show
		// derivatives, differences etc.)
		updateExtraColumns(rowCount);

	}

	/**
	 * Updates any extra columns added by the user to the XYTable.
	 */
	private void updateExtraColumns(int rowCount) {

		if (extraColumnList.size() == 0)
			return;

		for (int column = 2; column < extraColumnList.size() + 2; column++) {

			Double[] copyArray = new Double[rowCount];

			int columnType = extraColumnList.get(column - 2);
			switch (columnType) {

			case COL_DERIVATIVE:

				for (int row = 0; row < rowCount; row++) {
					double x = Double.parseDouble((String) listener.getXYValueAt(
							row, 0));
					double d = derivative.evaluate(x);// evaluateExpression(derivative.getLabel()
					// + "(" + x + ")");
					listener.setXYValueAt(d, row, column);
					copyArray[row] = d;
				}
				break;

			case COL_DERIVATIVE2:

				for (int row = 0; row < rowCount; row++) {
					double x = Double.parseDouble((String) listener.getXYValueAt(
							row, 0));
					double d2 = derivative2.evaluate(x);// evaluateExpression(derivative2.getLabel()
					// + "(" + x + ")");
					listener.setXYValueAt(d2, row, column);
					copyArray[row] = d2;
				}
				break;

			case COL_CURVATURE:

				for (int row = 0; row < rowCount; row++) {
					double x = Double.parseDouble((String) listener.getXYValueAt(
							row, 0));
					double y = Double.parseDouble((String) listener.getXYValueAt(
							row, 1));

					MyVecNode vec = new MyVecNode(kernel, new MyDouble(kernel,
							x), new MyDouble(kernel, y));

					ExpressionNode point = new ExpressionNode(kernel, vec,
							Operation.NO_OPERATION, null);
					point.setForcePoint();

					AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons,
							point, false);
					cons.removeFromConstructionList(pointAlgo);

					AlgoCurvature curvature = new AlgoCurvature(cons,
							(GeoPoint) pointAlgo.getGeoElements()[0],
							selectedGeo);
					cons.removeFromConstructionList(curvature);

					double c = ((GeoNumeric) curvature.getGeoElements()[0])
							.getDouble();

					// double c = evaluateExpression(
					// "Curvature[ (" + x + "," + y + ")," +
					// selectedGeo.getLabel() + "]");
					listener.setXYValueAt(c, row, column);
					copyArray[row] = c;
				}
				break;

			case COL_DIFFERENCE:

				for (int row = 1; row < rowCount; row++) {
					Object prevValue = listener.getXYValueAt(row - 1, column - 1);
					if (prevValue != null) {
						double prev = Double.parseDouble((String)prevValue);
						double x = Double.parseDouble((String)listener.getXYValueAt(
								row, column - 1));

						listener.setXYValueAt(x - prev, row, column);
						copyArray[row] = x - prev;
					} else {
						listener.setXYValueAt(null, row, column);
						copyArray[row] = null;
					}

				}
				break;

			}

			xyTableCopyList.add(copyArray);

		}
	}

	public void addColumn(int columnType) {
		extraColumnList.add(columnType);
		listener.addTableColumn(getColumnName(columnType));
	}

	public void removeColumn() {
		extraColumnList.remove(extraColumnList.size() - 1);
	}

	public void applyStep(double value) {
		step = value;
	}

	public void applyHigh(double value) {
		double y = selectedGeo.evaluate(value);
		highPoint.setCoords(value, y, 1);
		highPoint.updateCascade();
		highPoint.updateRepaint();
	}

	public void applyLow(double value) {
		double y = selectedGeo.evaluate(value);
		lowPoint.setCoords(value, y, 1);
		lowPoint.updateCascade();
		lowPoint.updateRepaint();
	}

	// ====================================================
	// View Implementation
	// ====================================================

	public boolean isValid() {
		return (selectedGeo == null && testPoint == null && lowPoint == null
				&& highPoint == null);
	}
	public void update(GeoElement geo, boolean isPoints) {

		if (selectedGeo.equals(geo)) {
			listener.setGeoName(selectedGeo
					.toString(StringTemplate.defaultTemplate));
		}

		else if (isPoints && testPoint.equals(geo)) {
			double[] coords = new double[3];
			testPoint.getCoords(coords);
			this.start = coords[0];
			listener.changeTableSelection();
			return;
		}

		else if (!isPoints && (lowPoint.equals(geo) || highPoint.equals(geo))) {
			listener.updateHighAndLow(lowPoint.x > highPoint.x, lowPoint.equals(geo));

			return;
		}

	}

	// ====================================================
	// Geo Selection Listener
	// ====================================================

	private void createGeoElementSlectionListener() {
		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo,
					boolean addToSelection) {
				insertGeoElement(geo);
			}
		};
	}

	/**
	 * Sets the function to be inspected and updates the entire GUI
	 *  
	 * @param geo The function to be inspected
	 */
	public void insertGeoElement(GeoElement geo) {

		clearGeoList();

		selectedGeo = (GeoFunction) geo;

		listener.setGeoName(getTitleString());

		initialX = 0.5 * (activeEV.getXmin() - activeEV.getXmin());
		start = initialX;

		// initial step = EV grid step
		step = 0.25 * kernel.getApplication().getActiveEuclidianView()
				.getGridDistances()[0];
		listener.setStepText("" + step);


		defineDisplayGeos();

		double x = initialX - 4 * step;
		double y = selectedGeo.evaluate(x);
		lowPoint.setCoords(x, y, 1);

		x = initialX + 4 * step;
		y = selectedGeo.evaluate(x);
		highPoint.setCoords(x, y, 1);

		lowPoint.updateCascade();
		highPoint.updateCascade();
		activeEV = (EuclidianView) app.getActiveEuclidianView();

	}

	public void stepStartForward() {
		start += step; 
	}

	public void stepStartBackward() {
		start -= step; 
	}

	// ====================================================
	// Update/Create Display Geos
	// ====================================================

	private void defineDisplayGeos() {

		// remove all geos
		clearGeoList();

		GeoFunction f = selectedGeo;

		// create XY table geos
		// ========================================
		// test point
		AlgoPointOnPath pAlgo = new AlgoPointOnPath(cons, f,
				(activeEV.getXmin() + activeEV.getXmax()) / 2, 0);
		cons.removeFromConstructionList(pAlgo);
		testPoint = (GeoPoint) pAlgo.getGeoElements()[0];
		testPoint.setObjColor(listener.getColor(Colors.GEO));
		testPoint.setPointSize(4);
		testPoint.setLayer(f.getLayer() + 1);
		pointTabGeoList.add(testPoint);

		// X segment
		ExpressionNode xcoord = new ExpressionNode(kernel, testPoint,
				Operation.XCOORD, null);
		MyVecNode vec = new MyVecNode(kernel, xcoord, new MyDouble(kernel, 0.0));
		ExpressionNode point = new ExpressionNode(kernel, vec,
				Operation.NO_OPERATION, null);
		point.setForcePoint();
		AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point,
				false);
		cons.removeFromConstructionList(pointAlgo);

		AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons, testPoint,
				(GeoPoint) pointAlgo.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg1);
		xSegment = seg1.getGeoElements()[0];
		xSegment.setSelectionAllowed(false);
		xSegment.setObjColor(listener.getColor(Colors.GEO));
		xSegment.setLineThickness(3);
		xSegment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		xSegment.setEuclidianVisible(true);
		xSegment.setFixed(true);
		pointTabGeoList.add(xSegment);

		// Y segment
		ExpressionNode ycoord = new ExpressionNode(kernel, testPoint,
				Operation.YCOORD, null);
		MyVecNode vecy = new MyVecNode(kernel, new MyDouble(kernel, 0.0),
				ycoord);
		ExpressionNode pointy = new ExpressionNode(kernel, vecy,
				Operation.NO_OPERATION, null);
		pointy.setForcePoint();
		AlgoDependentPoint pointAlgoy = new AlgoDependentPoint(cons, pointy,
				false);
		cons.removeFromConstructionList(pointAlgoy);

		AlgoJoinPointsSegment seg2 = new AlgoJoinPointsSegment(cons, testPoint,
				(GeoPoint) pointAlgoy.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg2);

		ySegment = seg2.getGeoElements()[0];
		ySegment.setSelectionAllowed(false);
		ySegment.setObjColor(listener.getColor(Colors.GEO));
		ySegment.setLineThickness(3);
		ySegment.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		ySegment.setEuclidianVisible(true);
		ySegment.setFixed(true);
		pointTabGeoList.add(ySegment);

		// tangent line
		AlgoTangentFunctionPoint tangent = new AlgoTangentFunctionPoint(cons,
				testPoint, f);
		cons.removeFromConstructionList(tangent);
		tangentLine = tangent.getGeoElements()[0];
		tangentLine.setSelectionAllowed(false);
		tangentLine.setObjColor(listener.getColor(Colors.GEO));
		tangentLine.setEuclidianVisible(false);
		pointTabGeoList.add(tangentLine);

		// osculating circle
		AlgoOsculatingCircle oc = new AlgoOsculatingCircle(cons, testPoint, f);
		cons.removeFromConstructionList(oc);
		oscCircle = oc.getGeoElements()[0];
		oscCircle.setSelectionAllowed(false);
		oscCircle.setObjColor(listener.getColor(Colors.GEO));
		oscCircle.setEuclidianVisible(false);
		pointTabGeoList.add(oscCircle);

		// derivative
		AlgoDerivative deriv = new AlgoDerivative(cons, f, true);
		cons.removeFromConstructionList(deriv);
		derivative = (GeoFunction) deriv.getGeoElements()[0];
		derivative.setEuclidianVisible(false);
		hiddenGeoList.add(derivative);

		// 2nd derivative
		AlgoDerivative deriv2 = new AlgoDerivative(cons, derivative, true);
		cons.removeFromConstructionList(deriv2);
		derivative2 = (GeoFunction) deriv2.getGeoElements()[0];
		derivative2.setEuclidianVisible(false);
		hiddenGeoList.add(derivative2);

		// point list
		pts = new GeoList(cons);
		pts.setEuclidianVisible(true);
		pts.setObjColor(GeoGebraColorConstants.DARKGRAY);
		pts.setPointSize(3);
		pts.setLayer(f.getLayer() + 1);
		for (int i = 0; i < pointCount; i++) {
			pts.add(new GeoPoint(cons));
		}
		pointTabGeoList.add(pts);

		// create interval table geos
		// ================================================

		// interval points
		AlgoPointOnPath pxAlgo = new AlgoPointOnPath(cons, f,
				(2 * activeEV.getXmin() + activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pxAlgo);
		lowPoint = (GeoPoint) pxAlgo.getGeoElements()[0];
		lowPoint.setEuclidianVisible(false);
		lowPoint.setPointSize(4);
		lowPoint.setObjColor(listener.getColor(Colors.GEO));
		lowPoint.setLayer(f.getLayer() + 1);
		intervalTabGeoList.add(lowPoint);

		AlgoPointOnPath pyAlgo = new AlgoPointOnPath(cons, f,
				(activeEV.getXmin() + 2 * activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pyAlgo);
		highPoint = (GeoPoint) pyAlgo.getGeoElements()[0];
		highPoint.setEuclidianVisible(false);
		highPoint.setPointSize(4);
		highPoint.setObjColor(listener.getColor(Colors.GEO));
		highPoint.setLayer(f.getLayer() + 1);
		intervalTabGeoList.add(highPoint);

		ExpressionNode low = new ExpressionNode(kernel, lowPoint,
				Operation.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint,
				Operation.XCOORD, null);
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);

		AlgoFunctionInterval interval = new AlgoFunctionInterval(cons, f,
				(NumberValue) xLow.getGeoElements()[0],
				(NumberValue) xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(interval);

		functionInterval = interval.getGeoElements()[0];
		functionInterval.setSelectionAllowed(false);
		functionInterval.setEuclidianVisible(false);
		functionInterval.setLineThickness(selectedGeo.getLineThickness() + 5);
		functionInterval
		.setObjColor(listener.getColor(Colors.GEO));
		functionInterval.setLayer(f.getLayer() + 1);
		intervalTabGeoList.add(functionInterval);

		AlgoIntegralDefinite inte = new AlgoIntegralDefinite(cons, selectedGeo,
				(NumberValue) xLow.getGeoElements()[0],
				(NumberValue) xHigh.getGeoElements()[0], null, false);
		cons.removeFromConstructionList(inte);
		integralGeo = inte.getGeoElements()[0];
		integralGeo.setSelectionAllowed(false);
		integralGeo.setEuclidianVisible(false);
		integralGeo.setObjColor(listener.getColor(Colors.GEO));
		intervalTabGeoList.add(integralGeo);

		ExpressionNode en = new ExpressionNode(kernel, selectedGeo,
				Operation.ABS, null);
		AlgoDependentFunction funAlgo = new AlgoDependentFunction(cons,
				(Function) en.evaluate(StringTemplate.defaultTemplate));
		cons.removeFromConstructionList(funAlgo);

		// the antiderivative of a function containing the absolute function
		// might be difficult to find if it exists at all. Therefore the
		// definite integral is calculated numerically.
		AlgoIntegralDefinite area = new AlgoIntegralDefinite(cons,
				(GeoFunction) funAlgo.getGeoElements()[0],
				(NumberValue) xLow.getGeoElements()[0],
				(NumberValue) xHigh.getGeoElements()[0], null, true);
		cons.removeFromConstructionList(area);
		areaGeo = area.getGeoElements()[0];
		areaGeo.setSelectionAllowed(false);
		areaGeo.setEuclidianVisible(false);
		intervalTabGeoList.add(areaGeo);

		AlgoLengthFunction len = new AlgoLengthFunction(cons, selectedGeo,
				(GeoNumeric) xLow.getGeoElements()[0],
				(GeoNumeric) xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(len);
		lengthGeo = len.getGeoElements()[0];
		hiddenGeoList.add(lengthGeo);

		minPoint = new GeoPoint(cons);
		minPoint.setEuclidianVisible(false);
		minPoint.setPointSize(4);
		minPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND);
		minPoint.setObjColor(listener.getColor(Colors.GEO)
				.darker());
		minPoint.setLayer(f.getLayer() + 1);
		minPoint.setFixed(true);
		intervalTabGeoList.add(minPoint);

		maxPoint = new GeoPoint(cons);
		maxPoint.setEuclidianVisible(false);
		maxPoint.setPointSize(4);
		maxPoint.setPointStyle(EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND);
		maxPoint.setObjColor(listener.getColor(Colors.GEO)
				.darker());
		maxPoint.setLayer(f.getLayer() + 1);
		maxPoint.setFixed(true);
		intervalTabGeoList.add(maxPoint);

		// process the geos
		// ==================================================

		// add the display geos to the active EV and hide the tooltips
		for (GeoElement geo : intervalTabGeoList) {
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();

		}
		for (GeoElement geo : pointTabGeoList) {
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();
		}

		updateTestPoint();
		activeEV.repaint();

	}

	public void updateTestPoint() {

		if (testPoint == null)
			return;

		int row = listener.getSelectedXYRow();
		if (row >= 0) {
			double x = Double.parseDouble((String) listener.getXYValueAt(row, 0));
			double y = selectedGeo.evaluate(x);
			testPoint.setCoords(x, y, 1);
			testPoint.updateRepaint();
		}

	}

	public void clearGeoList() {
		for (GeoElement geo : intervalTabGeoList) {
			if (geo != null) {
				geo.remove();
			}
		}
		intervalTabGeoList.clear();

		for (GeoElement geo : pointTabGeoList) {
			if (geo != null) {
				geo.remove();
			}
		}
		pointTabGeoList.clear();

		for (GeoElement geo : hiddenGeoList) {
			if (geo != null) {
				geo.remove();
			}
		}
		hiddenGeoList.clear();

		rootGeos = null;
	}


	public void updateIntervalGeoVisiblity() {

		// minPoint.setEuclidianVisible(tableInterval.isRowSelected(0));
		minPoint.setEuclidianVisible(false);
		minPoint.update();
		// maxPoint.setEuclidianVisible(tableInterval.isRowSelected(1));
		maxPoint.setEuclidianVisible(false);
		maxPoint.update();

		// integralGeo.setEuclidianVisible(tableInterval.isRowSelected(5));
		areaGeo.setEuclidianVisible(false);
		areaGeo.update();
		integralGeo.setEuclidianVisible(true);
		integralGeo.update();

		activeEV.repaint();
	}


	public void copyPointsToSpreadsheet(int colCount, int rowCount) {

		Construction cons = app.getKernel().getConstruction();
		GeoElement geo = null;

		int targetColumn = app.getSpreadsheetTableModel()
				.getHighestUsedColumn();

		for (int c = 0; c < colCount; c++) {
			targetColumn++;
			for (int row = 0; row < rowCount + 1; row++) {
				// copy table header
				if (row == 0) {
					geo = new GeoText(cons, getColumnName(c));
					processCellGeo(geo, targetColumn, row);
				}
				// copy column data value
				else if (xyTableCopyList.get(c)[row - 1] != null) {
					geo = new GeoNumeric(cons,
							xyTableCopyList.get(c)[row - 1]);
					processCellGeo(geo, targetColumn, row);
				}
			}
		}

	}

	public void copyIntervalsToSpreadsheet(int colCount, int rowCount) {
		Construction cons = app.getKernel().getConstruction();
		GeoElement geo = null;

		int targetColumn = app.getSpreadsheetTableModel()
				.getHighestUsedColumn();
		for (int c = 0; c < colCount; c++) {
			targetColumn++;
			for (int row = 0; row < rowCount; row++) {

				// first column has property names
				if (c == 0 && property.get(row) != null) {
					geo = new GeoText(cons, property.get(row));
					processCellGeo(geo, targetColumn, row);
				}

				// remaining columns have data
				else if (value2.get(row) != null) {

					for (int k = 0; k < value2.get(row).length; k++)
						if (value2.get(row)[k] != null) {
							geo = new GeoNumeric(cons, value2.get(row)[k]);
							processCellGeo(geo, targetColumn + k, row);
						}
				}
			}
		}
	}



	private static void processCellGeo(GeoElement geo, int column, int row) {
		geo.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(column, row));
		geo.setEuclidianVisible(false);
		geo.setAuxiliaryObject(true);
		geo.update();
	}

	public void setStart(double value) {
		start = value;
	}

	public String[] getColumnNames() {
		return columnNames;
	}


}
