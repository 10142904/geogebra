package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * Text[ <text> ]
 */
public class CmdText extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:

			arg = resArgs(c);	
			GeoElement[] ret = { kernelA.Text(c.getLabel(),
					arg[0]) };
			return ret;



		case 2:

			arg = resArgs(c);	
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { kernelA.Text(c.getLabel(),
						arg[0], (GeoBoolean)arg[1]) };
				return ret2;
			}
			else if (arg[1].isGeoPoint()) {
				GeoElement[] ret2 = { kernelA.Text(c.getLabel(),
						arg[0], (GeoPoint2)arg[1]) };
				return ret2;
			}
			else
				throw argErr(app, c.getName(), arg[1]);     

		case 3:
			boolean ok;
			arg = resArgs(c);	
			if ((ok = arg[1].isGeoPoint()) && arg[2].isGeoBoolean()) {
				GeoElement[] ret2 = { kernelA.Text(c.getLabel(),
						arg[0], (GeoPoint2)arg[1], (GeoBoolean)arg[2]) };
				return ret2;
			}
			throw argErr(app, c.getName(), ok ? arg[2] : arg[1]);     

		case 4:
			boolean ok1 = false;
			arg = resArgs(c);	
			if ((ok = arg[1].isGeoPoint()) && (ok1 = arg[2].isGeoBoolean()) && arg[3].isGeoBoolean()) {
				GeoElement[] ret2 = { kernelA.Text(c.getLabel(),
						arg[0], (GeoPoint2)arg[1], (GeoBoolean)arg[2], (GeoBoolean)arg[3]) };
				return ret2;
			}
			throw argErr(app, c.getName(), ok ? (ok1 ? arg[3] : arg[2]) : arg[1]);     

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
