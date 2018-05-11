package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * FirstAxis[ &lt;GeoConic> ]
 */
public class CmdFirstAxis extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFirstAxis(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAxisFirst algo = getAlgoAxisFirst(cons, c.getLabel(),
						(GeoConicND) arg[0]);

				GeoElement[] ret = { algo.getAxis().toGeoElement() };
				return ret;
			}
			throw argErr(app, c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param cons1
	 *            construction
	 * @param label
	 *            label
	 * @param geoConicND
	 *            conic
	 * @return axis algo
	 */
	protected AlgoAxisFirst getAlgoAxisFirst(Construction cons1, String label,
			GeoConicND geoConicND) {

		return new AlgoAxisFirst(cons1, label, geoConicND);
	}
}
