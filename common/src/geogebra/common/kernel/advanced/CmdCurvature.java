package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * Curvature[<Point>,<Curve>], Curvature[<Point>,<Function>]
 * 
 * @author Victor Franco Espino
 */
public class CmdCurvature extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurvature(Kernel kernel) {
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
				
				AlgoCurvature algo = new AlgoCurvature(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoFunction) arg[1]);
				GeoElement[] ret = { algo.getResult() };
				
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				
				AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoCurveCartesianND) arg[1]);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1] instanceof GeoFunctionNVar))) {
				
				// Gaussian Curvature
				AlgoCurvatureSurface algo = new AlgoCurvatureSurface(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoFunctionNVar) arg[1]);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))){
				AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoConicND)arg[1]);
				GeoElement[] ret = { algo.getResult() };
				return ret;			
			}  else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
