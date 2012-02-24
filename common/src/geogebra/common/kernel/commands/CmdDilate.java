package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Dilate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] Dilate[ <GeoLine>,
 * <NumberValue>, <GeoPoint> ] Dilate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Dilate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
public class CmdDilate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDilate(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoPolygon() || arg[0]
					.isGeoList()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				NumberValue phi = (NumberValue) arg[1];
				ret = kernelA.Dilate(label, arg[0], phi);
				return ret;
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		case 3:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoPolygon() || arg[0]
					.isGeoList()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				NumberValue phi = (NumberValue) arg[1];
				GeoPoint2 Q = (GeoPoint2) arg[2];
				ret = kernelA.Dilate(label, arg[0], phi, Q);
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
