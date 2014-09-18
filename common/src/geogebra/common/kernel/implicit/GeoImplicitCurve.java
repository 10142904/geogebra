package geogebra.common.kernel.implicit;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.EuclidianViewCE;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

import java.util.List;

/**
 * GeoElement representing an implicit curve.
 * 
 */
public class GeoImplicitCurve extends GeoElement implements EuclidianViewCE {

	private FunctionNVar expression;
	private GeoLocus locus;

	private int gridWidth;
	private int gridHeight;

	private GeoImplicitCurve(Construction c) {
		super(c);
		locus = new GeoLocus(c);
		locus.setDefined(true);
		c.registerEuclidianViewCE(this);
	}

	/**
	 * Constructs an implicit curve object with given equation containing
	 * variables as x and y.
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param equation
	 *            equation of the implicit curve
	 */
	public GeoImplicitCurve(Construction c, String label, Equation equation) {
		this(c);
		setLabel(label);
		fromEquation(equation);
		updatePath();
	}

	/**
	 * Constructs and implicit curve with given function in x and y.
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param function
	 *            function defining the implicit curve
	 */
	public GeoImplicitCurve(Construction c, String label, FunctionNVar function) {
		this(c);
		setLabel(label);
		fromFunction(function);
		updatePath();
	}

	private void fromEquation(Equation equation) {
		ExpressionNode leftHandSide = equation.getLHS();
		ExpressionNode rightHandSide = equation.getRHS();

		ExpressionNode functionExpression = new ExpressionNode(kernel,
				leftHandSide, Operation.MINUS, rightHandSide);
		FunctionVariable x = new FunctionVariable(kernel, "x");
		FunctionVariable y = new FunctionVariable(kernel, "y");
		VariableReplacer repl = VariableReplacer.getReplacer();
		VariableReplacer.addVars("x", x);
		VariableReplacer.addVars("y", y);
		functionExpression.traverse(repl);
				
		expression = new FunctionNVar(functionExpression,
				new FunctionVariable[] { x,
						y });
	}

	private void fromFunction(FunctionNVar function) {
		expression = function;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMPLICIT_CURVE;
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDefined() {
		return expression != null;
	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	public String toValueString(StringTemplate tpl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		// TODO Auto-generated method stub
		return false;
	}

	private double[] evalArray = new double[2];

	/**
	 * @param x
	 *            function variable x
	 * @param y
	 *            function variable y
	 * @return the value of the function
	 */
	public double evaluateImplicitCurve(double x, double y) {
		evalArray[0] = x;
		evalArray[1] = y;
		return evaluateImplicitCurve(evalArray);
	}

	/**
	 * @param values
	 *            function variables ({x, y})
	 * @return the value of the function
	 */
	public double evaluateImplicitCurve(double[] values) {
		return expression.evaluate(values);
	}

	/**
	 * @return Locus representing this curve
	 */
	public GeoLocus getLocus() {
		return locus;
	}

	/**
	 * Updates the path of the curve.
	 */
	public void updatePath() {
		double[] viewBounds = kernel.getViewBoundsForGeo(this);
		if (viewBounds[0] == Double.POSITIVE_INFINITY) { // no active View
			viewBounds = new double[] { -10, 10, -10, 10, 10, 10 }; // get some
																	// value...
		}
		// increase grid size for big screen, #1563
		gridWidth = 60;
		gridHeight = 60;
		updatePath(viewBounds[0], viewBounds[3], viewBounds[1] - viewBounds[0],
				viewBounds[3] - viewBounds[2], viewBounds[4], viewBounds[5]);
	}

	private double[][] grid;
	private boolean[][] evald;

	private double rectX;
	private double rectY;
	private double rectW;
	private double rectH;

	/**
	 * @param rectX
	 *            top of the view
	 * @param rectY
	 *            left of the view
	 * @param rectW
	 *            width of the view
	 * @param rectH
	 *            height of the view
	 * @param xScale
	 *            x-scale of the view
	 * @param yScale
	 *            y-scale of the view
	 */
	public void updatePath(double rectX, double rectY, double rectW,
			double rectH, double xScale, double yScale) {
		this.rectX = rectX;
		this.rectY = rectY;
		this.rectW = rectW;
		this.rectH = rectH;
		App.debug(rectX + "x" + rectY + "," + rectW + "," + rectH);
		App.debug(gridWidth + "x" + gridHeight);
		App.debug("res" + xScale + " " + yScale);

		grid = new double[gridHeight][gridWidth];
		evald = new boolean[gridHeight - 1][gridWidth - 1];

		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				grid[i][j] = evaluateImplicitCurve(getRealWorldCoordinates(i, j));
			}
		}
		List<MyPoint> locusPoints = locus.getPoints();
		locusPoints.clear();
		// check the squares marching algorithm
		int i = 0;
		int j = -1;
		while (true) {
			if (j >= gridWidth - 2) {
				j = 0;
				i++;
			} else {
				j++;
			}
			if (i >= gridHeight - 2) {
				break;
			}
			
			MyPoint[] ps = getPointsForGrid(i, j);
			if (ps != null) {
				for (MyPoint p : ps) {
					locusPoints.add(p);
				}
			}
		}
	}

	private MyPoint[] getPointsForGrid(int i, int j) {
		GridTypeEnum gridType = getGridType(i, j);
		MyPoint P, Q;
		double qx, qy, px, py;
		double[] A = getRealWorldCoordinates(i, j);
		double[] B = getRealWorldCoordinates(i, j + 1);
		double[] C = getRealWorldCoordinates(i + 1, j + 1);
		double[] D = getRealWorldCoordinates(i + 1, j);
		switch (gridType) {
		case T1000:
			qx = A[0];
			py = A[1];
			qy = (A[1]+D[1])/2.0;
					//((-(f(D)))/(f(A)-f(D)))*(A[1]-D[1]) + D[1];
			px = (B[0]+A[0])/2.0;
				//((-(f(B)))/(f(A)-f(B)))*(A[0]-B[0]) + B[0];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T0100:
			qx = B[0];
			py = B[1];
			qy = (B[1]+D[1])/2.0;
					//((-(f(B)))/(f(D)-f(B)))*(D[1]-B[1]) + B[1];
			px = (B[0] + A[0]) / 2.0;
					//((-(f(B)))/(f(A)-f(B)))*(A[0]-B[0]) + B[0];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T0010:
			qx = B[0];
			py = C[1];
			qy = (D[1]+B[1])/2.0;
					//((-(f(B)))/(f(D)-f(B)))*(D[1]-B[1]) + B[1];
			px = (D[0]+C[0])/2.0;
					//((-(f(C)))/(f(D)-f(C)))*(D[0]-C[0]) + C[0];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T0001:
			qx = A[0];
			py = C[1];
			qy = (A[1] + D[1])/2.0;
					//((-(f(D)))/(f(A)-f(D)))*(A[1]-D[1]) + D[1];
			px = (D[0]+C[0])/2.0;
					//((-(f(C)))/(f(D)-f(C)))*(D[0]-C[0]) + C[0];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T1001:
			py = C[1];
			px = (D[0]+C[0]) / 2.0;
					//((-(f(C)))/(f(D)-f(C)))*(D[0]-C[0]) + C[0];
			qy = A[1];
			qx = (A[0]+B[0])/2.0;
					//((-(f(B)))/(f(A)-f(B)))*(A[0]-B[0]) + B[0];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T1100:
			qx = A[0];
			qy = (A[1]+D[1])/2.0;
					//((-(f(D)))/(f(A)-f(D)))*(A[1]-D[1]) + D[1];
			px = B[0];
			py = (D[1]+B[1])/2.0;
					//((-(f(B)))/(f(D)-f(B)))*(D[1]-B[1]) + B[1];
			P = new MyPoint(px, py, false);
			Q = new MyPoint(qx, qy, true);
			return new MyPoint[] {P, Q};
		case T1010:
			
			P = new MyPoint(0, 0, false);
			Q = new MyPoint(0, 0, true);
			return new MyPoint[] {P, Q};
		case T0101:

			P = new MyPoint(0, 0, false);
			Q = new MyPoint(0, 0, true);
			return new MyPoint[] {P, Q};
		}
		return null;
	}
	
	private double f(double[] vals) {
		return evaluateImplicitCurve(vals);
	}

	private GridTypeEnum getGridType(int i, int j) {
		double nw = grid[i][j];
		double ne = grid[i][j + 1];
		double sw = grid[i + 1][j];
		double se = grid[i + 1][j + 1];

		int snw = mySignFun(nw);
		int sne = mySignFun(ne);
		int ssw = mySignFun(sw);
		int sse = mySignFun(se);

		double sum = Math.abs(snw + sne + ssw + sse);
		if (sum == 4) { // all corners have the same sign
			return GridTypeEnum.T0000;
		}
		if (sum == 2) { // three corners have the same sign
			if (snw != sne) {
				if (snw != ssw) {
					return GridTypeEnum.T1000;
				}
				return GridTypeEnum.T0100;
			}
			if (ssw != snw) {
				return GridTypeEnum.T0001;
			}
			return GridTypeEnum.T0010;
		}
		// two corners have the same sign
		if (snw == ssw) {
			return GridTypeEnum.T1001;
		}
		if (snw == sne) {
			return GridTypeEnum.T1100;
		}
		if (snw > 0) {
			return GridTypeEnum.T1010;
		}
		return GridTypeEnum.T0101;
	}

	private int mySignFun(double val) {
		if (val > 0) {
			return 1;
		}
		return -1;
	}

	private int signAbsSum(double args[]) {
		int s = 0;
		for (int i = 0; i < args.length; i++) {
			s += mySignFun(args[i]);
		}
		return Math.abs(s);
	}

	private double getRealWorldY(int i) {
		double s = rectH / gridHeight;
		return rectY - i * s;
	}

	private double getRealWorldX(int j) {
		double s = rectW/gridWidth;
		return rectX + j * s;
	}

	private double[] rwCoords = new double[2];

	private double[] getRealWorldCoordinates(int i, int j) {
		double x = getRealWorldX(j);
		double y = getRealWorldY(i);
		return new double[]{x,y};
	}

	public boolean euclidianViewUpdate() {
		if (isDefined()) {
			updatePath();
			return true;
		}
		return false;
	}

	private enum GridTypeEnum {
		/** None NW */
		T0000, T1000, T0100, T0010, T0001, T1001, T1100, T1010, T0101;
	}

}
