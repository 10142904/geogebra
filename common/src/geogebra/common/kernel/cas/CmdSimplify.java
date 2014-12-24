package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * Simplify
 */
public class CmdSimplify extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSimplify(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isCasEvaluableObject())) {

				AlgoCasBaseSingleArgument algo = new AlgoCasBaseSingleArgument(
						cons, c.getLabel(), (CasEvaluableFunction) arg[0],
						Commands.Simplify);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((arg[0].isGeoText())) {

				AlgoSimplifyText algo = new AlgoSimplifyText(cons,
						c.getLabel(), (GeoText) arg[0]);

				GeoElement[] ret = { algo.getGeoText() };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
