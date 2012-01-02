package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalPlane extends CommandProcessor {

	public CmdOrthogonalPlane(Kernel kernel) {
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
			if (arg[0].isGeoPoint()) {
				if (arg[1] instanceof GeoLineND) {
					return new GeoElement[] { (GeoElement) kernelA
							.getManager3D().OrthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoLineND) arg[1]) };
				} else if (arg[1] instanceof GeoVectorND) {
					return new GeoElement[] { (GeoElement) kernelA
							.getManager3D().OrthogonalPlane3D(c.getLabel(),
									(GeoPointND) arg[0], (GeoVectorND) arg[1]) };
				} else {
					throw argErr(app, c.getName(), arg[1]);
				}
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
