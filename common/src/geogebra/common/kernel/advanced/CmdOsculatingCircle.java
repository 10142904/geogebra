package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * OsculatingCircle[<Point>,<Function>],OsculatingCircle[<Point>,<Curve>]
 * 
 * @author Victor Franco Espino
 */

public class CmdOsculatingCircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOsculatingCircle(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {

				AlgoOsculatingCircle algo = new AlgoOsculatingCircle(cons,
						c.getLabel(), (GeoPoint) arg[0], (GeoFunction) arg[1]);

				GeoElement[] ret = { algo.getCircle() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoCurveCartesian) arg[1]);

				GeoElement[] ret = { algo.getCircle() };
				return ret;
			}
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
				GeoElement[] ret = { algo.getCircle() };
				return ret;
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
