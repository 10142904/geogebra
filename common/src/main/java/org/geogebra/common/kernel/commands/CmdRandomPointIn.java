package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRandomPoint;
import org.geogebra.common.kernel.algos.AlgoRandomPointInConic;
import org.geogebra.common.kernel.algos.AlgoRandomPointInPoints;
import org.geogebra.common.kernel.algos.AlgoRandomPointInPolygon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Processor for random points
 *
 */
public class CmdRandomPointIn extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomPointIn(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n == 1) {
			if (arg[0].isGeoPolygon()) {

				AlgoRandomPointInPolygon algo = new AlgoRandomPointInPolygon(
						cons, c.getLabel(), (GeoPolygon) arg[0]);

				GeoElement[] ret = { algo.getRandomPoint() };
				return ret;
			} else if (arg[0].isGeoConic()) {
				AlgoRandomPointInConic algo = new AlgoRandomPointInConic(cons,
						c.getLabel(), (GeoConicND) arg[0]);

				GeoElement[] ret = { algo.getRandomPoint() };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}
		} else if (n == 4 && arg[0].isNumberValue() && arg[1].isNumberValue()
				&& arg[2].isNumberValue() && arg[3].isNumberValue()) {

			return randomPoint(c.getLabel(), (GeoNumberValue) arg[0],
					(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
					(GeoNumberValue) arg[3]);

		} else if (n > 2) {
			arg = resArgs(c);
			GeoPointND[] points = new GeoPointND[n];

			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(c, arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
			}
			// everything ok

			AlgoRandomPointInPoints algo = new AlgoRandomPointInPoints(cons,
					c.getLabel(), points);

			GeoElement[] ret = { algo.getRandomPoint() };
			return ret;
		} else {
			throw argNumErr(c);
		}
	}

	/**
	 * @param label
	 *            label
	 * @param a
	 *            x-min
	 * @param b
	 *            x-max
	 * @param c
	 *            y-min
	 * @param d
	 *            y-max
	 * @return random point (p,q) where p is between a & b and q is between c &
	 *         d
	 */
	protected GeoElement[] randomPoint(String label, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue c, GeoNumberValue d) {
		AlgoRandomPoint arp = new AlgoRandomPoint(cons, label, a, b, c, d);
		return new GeoElement[] { arp.getOutput(0) };
	}

}
