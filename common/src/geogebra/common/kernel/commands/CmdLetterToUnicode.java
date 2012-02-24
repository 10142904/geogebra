package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * LetterToUnicode[&lt Letter>]
 */
public class CmdLetterToUnicode extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLetterToUnicode(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoText()) {
				GeoElement[] ret = { 
						kernelA.LetterToUnicode(c.getLabel(),
						(GeoText) arg[0] ) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
