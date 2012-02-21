package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra.common.kernel.Kernel;

/*
 * Sum[ list ]
 * adapted from CmdLcm by Michael Borcherds 2008-02-16
 */
public class CmdSum extends CommandProcessor {

	public CmdSum(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		// needed for Sum[]
		if (arg.length == 0) {
			throw argNumErr(app, c.getName(), n);
		}

		// set all to either true or false
		boolean allNumbers = arg[0].isGeoList();
		boolean allFunctions = allNumbers;
		boolean allNumbersVectorsPoints = allNumbers;
		boolean allText = allNumbers;

		GeoList list = null;
		int size = -1;

		if (arg[0].isGeoList()) {
			list = (GeoList) arg[0];
			size = list.size();

			for (int i = 0; i < size; i++) {
				GeoElement geo = list.get(i);
				if (!geo.isGeoFunctionable()) {
					allFunctions = false;
				}
				if (!geo.isNumberValue()) {
					allNumbers = false;
				}
				if (!geo.isNumberValue() && !geo.isGeoVector()
						&& !geo.isGeoPoint()) {
					allNumbersVectorsPoints = false;
				}
				if (!geo.isGeoText()) {
					allText = false;
				}
			}
		}

		// this is bad - list can be saved later with size 0
		// if (size == 0) throw argErr(app, c.getName(), arg[0]);

		switch (n) {
		case 1:
			if (allNumbers) {
				GeoElement[] ret = { kernelA.Sum(c.getLabel(), list) };
				return ret;
			} else if (allNumbersVectorsPoints) {
				GeoElement[] ret = { kernelA.SumPoints(c.getLabel(), list) };
				return ret;
			} else if (allFunctions) {
				GeoElement[] ret = { kernelA.SumFunctions(c.getLabel(), list) };
				return ret;
			} else if (allText) {
				GeoElement[] ret = { kernelA.SumText(c.getLabel(), list) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			if (arg[1].isGeoNumeric()) {

				if (allNumbers) {

					GeoElement[] ret = { kernelA.Sum(c.getLabel(), list,
							(GeoNumeric) arg[1]) };
					return ret;
				} else if (allFunctions) {
					GeoElement[] ret = { kernelA.SumFunctions(c.getLabel(),
							list, (GeoNumeric) arg[1]) };
					return ret;
				} else if (allNumbersVectorsPoints) {
					GeoElement[] ret = { kernelA.SumPoints(c.getLabel(), list,
							(GeoNumeric) arg[1]) };
					return ret;
				} else if (allText) {
					GeoElement[] ret = { kernelA.SumText(c.getLabel(), list,
							(GeoNumeric) arg[1]) };
					return ret;

				} else {
					throw argErr(app, c.getName(), arg[0]);
				}
			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			// try to create list of numbers
			if (arg[0].isNumberValue()) {
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.NUMERIC);
				if (wrapList != null) {
					GeoElement[] ret = { kernelA.Sum(c.getLabel(), wrapList) };
					return ret;
				}
			} else if (arg[0].isVectorValue()) {
				// try to create list of points
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.POINT);
				if (wrapList != null) {
					GeoElement[] ret = { kernelA.SumPoints(c.getLabel(),
							wrapList) };
					return ret;
				}
			} else if (arg[0].isGeoFunction()) {
				// try to create list of functions
				GeoList wrapList = wrapInList(kernelA, arg, arg.length,
						GeoClass.FUNCTION);
				if (wrapList != null) {
					GeoElement[] ret = { kernelA.SumFunctions(c.getLabel(),
							wrapList) };
					return ret;
				}
			}
			throw argNumErr(app, c.getName(), n);
		}
	}

}
