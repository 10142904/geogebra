package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * MatrixRank[Matrix]
 * @author zbynek
 *
 */
public class CmdMatrixRank extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdMatrixRank(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if (args.length != 1)
			throw argNumErr(app, c.getName(), args.length);
		if (!args[0].isGeoList())
			throw argErr(app, c.getName(), args[0]);
		return new GeoElement[] { kernelA.MatrixRank(c.getLabel(), (GeoList) args[0]) };
	}

}
