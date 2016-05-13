package org.geogebra.common.kernel.kernelND;

import java.util.TreeMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.DistanceFunction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.util.debug.Log;

/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoCurveCartesianND extends GeoElement implements Traceable, Path, VarString, CasEvaluableFunction {

	/** samples to find interval with closest parameter position to given point */
	protected static final int CLOSEST_PARAMETER_SAMPLES = 100;


	/** coordinates  functions */
	protected final Function[] fun;
	/** coordinates with expanded function references */
	protected final Function[] funExpanded;
	/** flag for each coordinate whether it depends on a function */
	protected final boolean[] containsFunctions;
	/** derivative  functions */
	protected Function[] funD1;
	/** second derivative  functions */
	protected Function[] funD2;
	/** start parameter */
	protected double startParam;
	/**end parameter*/
	protected double endParam;

	/** flag for isDefined()*/
	protected boolean isDefined = true;
	/**
	 * distFun(t) evaluates distance this(t) to a distant point (attribute of
	 * the distFun)
	 */
	protected DistanceFunction distFun;

	private ExpressionNode point;

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param dimension
	 *            dimension 2 or 3
	 */
	public GeoCurveCartesianND(Construction c, int dimension,
			ExpressionNode point) {
		super(c);
		this.fun = new Function[dimension];
		this.funExpanded = new Function[dimension];
		this.containsFunctions = new boolean[dimension];
		this.point = point;
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

	}

	/** constructor with functions
	 * @param c construction
	 * @param fun functions of parameter
	 */
	public GeoCurveCartesianND(Construction c, Function[] fun,
			ExpressionNode point) {
		super(c);

		this.fun = fun;
		this.funExpanded = new Function[fun.length];
		this.containsFunctions = new boolean[fun.length];
		this.point = point;
		setConstructionDefaults();
	}	

	/**
	 * set functions
	 * @param fun functions
	 */
	public void setFun(Function[] fun){
		for(int i=0; i<fun.length;i++){
			this.fun[i] = fun[i];
		}
	}

	@Override
	public boolean isGeoCurveCartesian() {
		return true;
	}	

	/**
	 * Replaces geo and all its dependent geos in this function's
	 * expression by copies of their values.
	 * @param geo Element to be replaced
	 */
	public void replaceChildrenByValues(GeoElement geo) {

		for (int i=0; i<fun.length; i++)
			if (fun[i] != null) {
				fun[i].replaceChildrenByValues(geo);
			}
	}

	/** 
	 * Sets the start and end parameter value of this curve.
	 * @param startParam start parameter
	 * @param endParam end parameter
	 */
	public void setInterval(double startParam, double endParam) {

		this.startParam = startParam;
		this.endParam = endParam;

		isDefined = startParam <= endParam;	
	}



	/**
	 * Returns the start parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return start parameter
	 */
	public double getMinParameter() {
		return startParam;
	}

	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return end parameter
	 */
	public double getMaxParameter() {
		return endParam;
	}






	/**
	 * returns all class-specific xml tags for getXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		//	line thickness and type  
		getLineStyleXML(sb);

	}


	@Override
	public boolean isPath() {
		return true;
	}



	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	/**
	 * @param defined new value of defined flag
	 */
	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	@Override
	public void setUndefined() {
		isDefined = false;
	}



	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToString = new StringBuilder(80);

		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			// sbToString.append('(');
			// sbToString.append(funX.getVarString());
			// sbToString.append(") = ");
			// changed to ':' to make LaTeX output better
			sbToString.append(':');
		}
		sbToString.append(toValueString(tpl));
		return sbToString.toString();
	}






	@Override
	public String toValueString(StringTemplate tpl) {
		if (isDefined) {
			StringBuilder sbTemp = new StringBuilder(80);

			sbTemp.setLength(0);
			sbTemp.append('(');

			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toValueString(tpl));
				if (i<fun.length-1)
					sbTemp.append(", ");
			}

			sbTemp.append(')');
			return sbTemp.toString();
		}
		return "?";
	}	

	/**
	 * @param tpl string template
	 * @return symbolic string representation
	 */
	public String toSymbolicString(StringTemplate tpl) {
		StringBuilder sbTemp = null;
		if (isDefined) {
			sbTemp = new StringBuilder(80);

			sbTemp.setLength(0);
			sbTemp.append('(');

			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toString(tpl));
				if (i<fun.length-1)
					sbTemp.append(", ");
			}

			sbTemp.append(')');
			return sbTemp.toString();
		} 
		return "?";
	}

	/**
	 * @param i
	 * @return i-th function
	 */
	public Function getFun(int i){
		return fun[i];
	}

	public final void update(){
		super.update();
		for(int i=0; i< this.funExpanded.length; i++){
			this.funExpanded[i]=null;
		}
	}

	public FunctionVariable[] getFunctionVariables() {
		return getFun(0).getFunctionVariables();
	}

	public String getVarString(StringTemplate tpl) {
		return getFun(0).getVarString(tpl);
	}

	/**
	 * Set this curve by applying CAS command to f.
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic,MyArbitraryConstant arbconst) {
		GeoCurveCartesianND c = (GeoCurveCartesianND) f;

		if (c.isDefined()) {
			//register the variable name to make sure parsing of CAS output runs OK, see #3006
			GeoNumeric geo = new GeoNumeric(this.cons);
			this.cons.addLocalVariable(getFun(0).getVarString(StringTemplate.defaultTemplate), geo);
			this.isDefined = true;
			for(int k = 0; k < getDimension(); k++){
				setFun(k, (Function) c.getFunExpanded(k).evalCasCommand(ggbCasCmd, symbolic,arbconst));
				this.isDefined = this.isDefined && getFun(k) != null;
			}
			this.cons.removeLocalVariable(getFun(0).getVarString(StringTemplate.defaultTemplate));
			if (this.isDefined)
				setInterval(c.startParam, c.endParam);
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	public void clearCasEvalMap(String key) {
		for(int k = 0; k < getDimension(); k++){
			getFun(k).clearCasEvalMap(key);
		}		
	}

	public void printCASEvalMapXML(StringBuilder sb) {
		for (int k = 0; k < getDimension(); k++) {
			// getFun(k).printCasEvalMap(sb);
		}
	}

	public void updateCASEvalMap(TreeMap<String, String> map) {
		// TODO
	}

	protected void setFun(int i, Function f) {
		this.fun[i] = f;
		this.funExpanded[i]=null;
		this.containsFunctions[i]=AlgoDependentFunction.containsFunctions(this.fun[i].getExpression());
	}

	protected Function getFunExpanded(int i) {
		if(!this.containsFunctions[i]){
			return getFun(i);
		}
		if(this.funExpanded[i] == null){
			this.funExpanded[i] = new Function(getFun(i),this.kernel);
			ExpressionNode expr = AlgoDependentFunction
					.expandFunctionDerivativeNodes(
							getFun(i).getExpression().deepCopy(this.kernel),
							false)
					.wrap();
			this.funExpanded[i].setExpression(expr);
		}
		return this.funExpanded[i];
	}

	/**
	 * Set this curve to the n-th derivative of c
	 * @param curve curve whose derivative we want
	 * 
	 * @param n
	 *            order of derivative
	 */
	public void setDerivative(GeoCurveCartesianND curve, int n) {
		if (curve.isDefined()) {
			this.isDefined = true;
			for(int k = 0; k < getDimension(); k++) {
				// changed from getFunExpanded() (didn't work)
				// now handled in ExpressionNode.derivative() case FUNCTION:
				setFun(k, curve.getFun(k).getDerivative(n, true));
				this.isDefined = this.isDefined && getFun(k) != null;
			}

			if (this.isDefined)
				setInterval(curve.startParam, curve.endParam);
		} else {
			this.isDefined = false;
		}
		this.distFun = null;
	}

	public int getDimension() {
		return this.fun.length;
	}

	/**
	 * 
	 * @param n n
	 * @return x, y, z, for n = 0, 1, 2
	 */
	protected String getVariable(int n) {
		if (n < getDimension() && n >= 0) {

			switch (n) {
			case 0: return "x";
			case 1: return "y";
			case 2: return "z";

			}
		}		
		Log.debug("problem with variable number");
		return "";

	}

	public abstract double getClosestParameter(GeoPointND a, double minParameter);

	public abstract double evaluateCurvature(double t);

	private boolean hideRangeInFormula;

	/**
	 * @return whether range is hidden in algebra
	 */
	public boolean isHiddenRange(){
		return this.hideRangeInFormula;
	}

	/**
	 * Hide range in formula -- needed when the curve is infinite and 
	 * range is used for drawing only (e.g. rotated functions)
	 * @param b true to hide
	 */
	public void setHideRangeInFormula(boolean b) {
		this.hideRangeInFormula = b;
	}

	public boolean isLaTeXDrawableGeo() {
		return true;
	}

	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		if (this.isDefined) {
			StringBuilder sbTemp =
					new StringBuilder(80);

			String param = getVarString(tpl);

			if (this.kernel.getApplication().isLatexMathQuillStyle(tpl)) {
				sbTemp.append("\\prcurve{ ");
				if (!hideRangeInFormula) {
					sbTemp.append("\\parametric{ ");
				}
				sbTemp.append("\\prtable{");
				if (point == null) {
					for (int i = 0; i < getDimension(); i++) {
						sbTemp.append("\\ggbtr{ \\ggbtdL{  ");
						sbTemp.append(getVariable(i));
						sbTemp.append(" = ");
						sbTemp.append(getFun(i).toLaTeXString(symbolic, tpl));
						sbTemp.append("} }");

					}
				} else {
					sbTemp.append("\\ggbtr{ \\ggbtdL{  ");
					sbTemp.append(point.toLaTeXString(true, tpl));
					sbTemp.append("} }");
				}

				sbTemp.append("}");
				if (!hideRangeInFormula) {
					sbTemp.append("}");
					sbTemp.append("\\prcondition{");
					sbTemp.append(this.kernel.format(this.startParam, tpl));
					sbTemp.append(" \\prle ");
					sbTemp.append(param);
					sbTemp.append(" \\prle ");
					sbTemp.append(this.kernel.format(this.endParam, tpl));
					sbTemp.append("}");
				}
				sbTemp.append("}");
			} else {

				if (!hideRangeInFormula && point == null) {
					sbTemp.append("\\left.");
				}
				if (point == null) {
					sbTemp.append("\\begin{array}{lll}");

					for (int i = 0; i < getDimension(); i++) {

						if (i > 0) {
							sbTemp.append("\\\\ ");
						}
						sbTemp.append(getVariable(i));
						sbTemp.append(" = ");
						sbTemp.append(getFun(i).toLaTeXString(symbolic, tpl));

					}
					sbTemp.append(" \\end{array}");
				} else {
					sbTemp.append(point.toLaTeXString(true, tpl));
				}

				if (!hideRangeInFormula) {
					if (point == null) {
						sbTemp.append("\\right\\} \\; ");
					} else {
						sbTemp.append(", \\;\\;\\;\\; \\left(");
					}
					sbTemp.append(this.kernel.format(this.startParam, tpl));
					sbTemp.append(" \\le ");
					sbTemp.append(param);
					sbTemp.append(" \\le ");
					sbTemp.append(this.kernel.format(this.endParam, tpl));
					if (point == null) {
						// nothing to do here
					} else {
						sbTemp.append("\\right)");
					}
				}
			}
			return sbTemp.toString();
		}
		return "?";
	}

	/**
	 * @param order order of derivative
	 * @return derivative as curve
	 */
	public GeoCurveCartesianND getGeoDerivative(int order) {
		if (this.derivGeoFun == null) {
			this.derivGeoFun = newGeoCurveCartesian(this.cons);
		}

		this.derivGeoFun.setDerivative(this, order);
		return this.derivGeoFun;
	}

	protected abstract GeoCurveCartesianND newGeoCurveCartesian(Construction cons);

	protected GeoCurveCartesianND derivGeoFun;

	public abstract ExpressionValue evaluateCurve(double double1);

	public boolean isParametric() {
		return true;
	}

	public ExpressionNode getPointExpression() {
		return point;
	}

	/**
	 * update distance function
	 */
	abstract public void updateDistanceFunction();

	public void evaluateCurve(double t, double[] f1eval) {
		// TODO Auto-generated method stub

	}

}
