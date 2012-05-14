package geogebra.common.cas;

import geogebra.common.cas.mpreduce.AbstractCASmpreduce;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.kernel.cas.GeoGebraCasInterface;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.AbstractApplication.CasType;
import geogebra.common.util.MaxSizeHashMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class provides an interface for GeoGebra to use an underlying computer algebra
 * system like MPReduce, Maxima or MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS implements GeoGebraCasInterface {

	private AbstractApplication app;
	private CASparser casParser;
	private CASgeneric cas;
	private AbstractCASmpreduce casMPReduce;
	private CasType currentCAS = CasType.NO_CAS;

	/**
	 * Creates new CAS interface
	 * @param kernel kernel
	 */
	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel.getParser());

		// DO NOT init underlying CAS here to avoid hanging animation,
		// see http://www.geogebra.org/trac/ticket/1565
		// getCurrentCAS();
	}

	public CASparser getCASparser() {
		return casParser;
	}

	public synchronized CASgeneric getCurrentCAS() {
		if (cas == null) {
			setCurrentCAS(Kernel.DEFAULT_CAS);
		}
		
		return cas;
	}

	public StringType getCurrentCASstringType() {
		switch (currentCAS) {
		case MAXIMA:
			return StringType.MAXIMA;

		case MPREDUCE:
			return StringType.MPREDUCE;

		default:
		case MATHPIPER:
			return StringType.MATH_PIPER;
		}
	}

	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * 
	 * @param CAS
	 *            use CAS_MATHPIPER or CAS_MAXIMA
	 */
	public synchronized void setCurrentCAS(CasType CAS) {
		try {
			switch (CAS) {
			/*
			 * case MAXIMA: cas = getMaxima(); ((CASmaxima) cas).initialize();
			 * currentCAS = CAS; break;
			 */

			default:
				cas = getMPReduce();
				app.getSettings().getCasSettings().addListener(cas);
				currentCAS = CAS;
				break;
			/*
			 * case MATHPIPER: cas = getMathPiper(); currentCAS = CAS; break;
			 */
			}
			/*
			 * }catch (MaximaVersionUnsupportedExecption e){
			 * app.showError("CAS.MaximaVersionUnsupported");
			 * setCurrentCAS(CasType.MPREDUCE);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		getCurrentCAS().reset();
	}

	/**
	 * Sets the number of signficiant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers significant figures
	 */
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		getCurrentCAS().setSignificantFiguresForNumeric(significantNumbers);
	}

	/*
	 * private CASmathpiper getMathPiper() { if
	 * (currentCAS.equals(CasType.MATHPIPER)) return (CASmathpiper) cas; else
	 * return new CASmathpiper(casParser, new CasParserToolsImpl('e')); }
	 */

	/*
	 * private CASmaxima getMaxima() { if (currentCAS.equals(CasType.MAXIMA))
	 * return (CASmaxima) cas; else return new CASmaxima(casParser, new
	 * CasParserToolsImpl('b')); }
	 */

	private synchronized AbstractCASmpreduce getMPReduce() {
		if (casMPReduce == null)
			casMPReduce = geogebra.common.factories.CASFactory.prototype.newMPReduce(casParser,
					new CasParserToolsImpl('e'),app.getKernel());
		return casMPReduce;
	}

	// /**
	// * Returns whether var is a defined variable.
	// */
	// public boolean isVariableBound(String var) {
	// return cas.isVariableBound(var);
	// }

	/**
	 * Unbinds (deletes) variable.
	 * 
	 * @param var variable to be unbound
	 */
	public void unbindVariable(String var) {
		getCurrentCAS().unbindVariable(var);
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra
	 * notation.
	 * 
	 * @param casInput
	 *            Input in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException if there is a timeout or the expression cannot be evaluated
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput,MyArbitraryConstant arbconst,StringTemplate tpl)
			throws CASException {

		String result = null;
		CASException exception = null;
		try {
			result = getCurrentCAS().evaluateGeoGebraCAS(casInput,arbconst,tpl);
		} catch (CASException ce) {
			exception = ce;
		} finally {
			//do nothing
		}

		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (casInput.isKeepInputUsed()
				&& (exception != null || "?".equals(result))) {
			// return original input
			return casInput.toString(StringTemplate.defaultTemplate);
		}

		// pass on exception
		if (exception != null)
			throw exception;

		// success
		if (result != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = app.getKernel().removeCASVariablePrefix(result, " ");
		}

		return result;
	}

	/**
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * 
	 * @param exp expression to be evaluated
	 * @return result string in GeoGebra syntax (null possible)
	 * @throws CASException if there is a timeout or the expression cannot be evaluated
	 */
	final public String evaluateGeoGebraCAS(String exp,MyArbitraryConstant arbconst) throws CASException {
		try {
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
			String ret = evaluateGeoGebraCAS(inVE,arbconst);
			if(ret == null)
				throw new CASException(
						new Exception(app.getError("CAS.GeneralErrorMessage")));
			return ret;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CASException(t);
		}
	}

	/**
	 * Evaluates an expression in the syntax of the currently active CAS
	 * 
	 * @return result string (null possible)
	 * @throws Throwable if there is a timeout or the expression cannot be evaluated
	 */
	final public String evaluateRaw(String exp) throws Throwable {
		return getCurrentCAS().evaluateRaw(exp);
	}

	

	/**
	 * Evaluates an expression given in MPReduce syntax.
	 * 
	 * @return result string (null possible)
	 * @param exp
	 *            the expression
	 * @throws CASException if there is a timeout or the expression cannot be evaluated
	 * */
	final public String evaluateMPReduce(String exp) throws CASException {
		return getMPReduce().evaluateMPReduce(exp);
	}

	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<String, String[]>(
			Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();

	/**
	 * Expands the given MPreduce expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String polyExpr, String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);

		String[] result = getPolynomialCoeffsCache.get(getPolynomialCoeffsSB
				.toString());
		if (result != null)
			return result;

		sbPolyCoeffs.setLength(0);
		sbPolyCoeffs.append("coeff(");
		sbPolyCoeffs.append(getPolynomialCoeffsSB.toString());
		sbPolyCoeffs.append(')');

		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }"
			String tmp = evaluateMPReduce(sbPolyCoeffs.toString());

			// no result
			if ("{}".equals(tmp) || "".equals(tmp) || tmp == null)
				return null;

			// not a polynomial: result still includes the variable, e.g. "x"
			if (tmp.indexOf(variable) >= 0)
				return null;

			// get names of escaped global variables right
			// e.g. "ggbcasvara" needs to be changed to "a"
			tmp = app.getKernel().removeCASVariablePrefix(tmp);

			tmp = tmp.substring(1, tmp.length() - 1); // strip '{' and '}'
			result = tmp.split(",");

			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(),
					result);
			return result;
		} catch (Throwable e) {
			AbstractApplication.debug("GeoGebraCAS.getPolynomialCoeffs(): "
					+ e.getMessage());
			// e.printStackTrace();
		}

		return null;
	}

	final private static String toString(ExpressionValue ev, boolean symbolic,StringTemplate tpl) {
		if(ev.isExpressionNode() && ev.isLeaf()){
			ExpressionValue lft = ((ExpressionNode)ev).getLeft();
			if(lft instanceof GeoFunction || lft instanceof GeoFunctionNVar)
				return ((GeoElement)lft).getAssignmentLHS(tpl);
			if(lft instanceof GeoCasCell){
				if(((GeoCasCell)lft).getVarString(tpl).length()>0)
					return ((GeoElement)lft).getLabel(tpl)+"("+((GeoCasCell)lft).getVarString(tpl)+")";
			}
		}
		if (symbolic) {
			return ev.toString(tpl);
		}
		return ev.toValueString(tpl);
	}

	/**
	 * Returns the CAS command for the currently set CAS using the given key and
	 * command arguments. For example, getCASCommand("Expand.1", {"3*(a+b)"})
	 * returns "ExpandBrackets( 3*(a+b) )" when MathPiper is the currently used
	 * CAS.
	 */
	final synchronized public String getCASCommand(String name,
			ArrayList<ExpressionNode> args, boolean symbolic,StringTemplate tpl) {
		StringBuilder sbCASCommand = new StringBuilder(80);

		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");

		String translation = getCurrentCAS().getTranslatedCASCommand(sbCASCommand
				.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = args.get(0);
						String str = toString(ev, symbolic,tpl);
						if (ev.isListValue()) {
							// is a list, remove { and }
							// resp. list( ... ) in mpreduce
							if (currentCAS.equals(CasType.MPREDUCE))
								sbCASCommand.append(str.substring(22,
										str.length() - 2));
							else
								sbCASCommand.append(str.substring(1,
										str.length() - 1));
						} else {
							// not a list, just append
							sbCASCommand.append(str);
						}
					} else {
						for (int j = 0; j < args.size(); j++) {
							ExpressionValue ev = args.get(j);
							sbCASCommand.append(toString(ev, symbolic,tpl));
							sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
					}
				} else {
					sbCASCommand.append(ch);
				}

			}

			return sbCASCommand.toString();
		}

		// build command key as name + "." + args.size()
		// remove 'N'
		sbCASCommand.setLength(sbCASCommand.length() - 1);
		// add eg '3'
		sbCASCommand.append(args.size());

		// get translation ggb -> MathPiper/Maxima
		translation = getCurrentCAS().getTranslatedCASCommand(sbCASCommand.toString());
		sbCASCommand.setLength(0);

		// no translation found:
		// use key as function name
		if (translation == null) {

			// convert command names x, y, z to xcoord, ycoord, ycoord to
			// protect it in CAS
			// see http://www.geogebra.org/trac/ticket/1440
			boolean handled = false;
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					if(args.get(0).isListValue()){
						sbCASCommand.append("applyfunction(");
						sbCASCommand.append(ch);
						sbCASCommand.append("coord,");
					}
					else{
						sbCASCommand.append(ch);
						sbCASCommand.append("coord(");
					}
					handled = true;
				}
			}

			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled){
				sbCASCommand.append(app.getKernel().printVariableName(name,tpl));
				sbCASCommand.append('(');
			}
			for (int i = 0; i < args.size(); i++) {
				ExpressionValue ev = args.get(i);
				sbCASCommand.append(toString(ev, symbolic,tpl));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length() - 1, ')');
		}

		// translation found:
		// replace %0, %1, etc. in translation by command arguments
		else {
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						sbCASCommand.append(toString(ev,symbolic,tpl));
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
				} else {
					sbCASCommand.append(ch);
				}
			}
		}

		return sbCASCommand.toString();
	}

	/**
	 * Returns whether the given command is available in the underlying CAS.
	 * 
	 * @param cmd
	 *            command with name and number of arguments
	 * @return whether command is available
	 */
	final public boolean isCommandAvailable(Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (getCurrentCAS().isCommandAvailable(sbCASCommand.toString()))
			return true;

		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (getCurrentCAS().isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the two input expressions are structurally equal. For
	 * example "2 + 2/3" is structurally equal to "2 + (2/3)" but unequal to
	 * "(2 + 2)/3"
	 * 
	 * @param inputVE
	 *            includes internal command names
	 * @param localizedInput
	 *            includes localized command names
	 */
	public boolean isStructurallyEqual(ValidExpression inputVE,
			String localizedInput) {
		try {
			// current input
			String input1normalized = casParser.toString(inputVE,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// new input
			ValidExpression ve2 = casParser
					.parseGeoGebraCASInputAndResolveDummyVars(localizedInput);
			String input2normalized = casParser.toString(ve2,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} catch (Throwable th) {
			System.err.println("GeoGebraCAS.isStructurallyEqual: "
					+ th.getMessage());
			return false;
		}
	}

	public CasType getCurrentCASType() {
		return currentCAS;
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		getCurrentCAS().evaluateGeoGebraCASAsync(c);
	}

	public String evaluateGeoGebraCAS(ValidExpression evalVE,
			MyArbitraryConstant arbconst) {
		return evaluateGeoGebraCAS(evalVE,arbconst,StringTemplate.defaultTemplate);
	}

	


}