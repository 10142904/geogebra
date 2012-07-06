package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
 * ParametricDerivative[ <GeoCurveCartesian> ]
 */
public class CmdParametricDerivative extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParametricDerivative(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg, arg2;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoCurveCartesian()) {
				GeoCurveCartesian f = (GeoCurveCartesian) arg[0];
				GeoElement[] ret = { kernelA.ParametricDerivative(label, f) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
