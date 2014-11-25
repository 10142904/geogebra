package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoTranslateVector3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoVectorPoint3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoTranslateVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdTranslate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.MyError;

public class CmdTranslate3D extends CmdTranslate {




	public CmdTranslate3D(Kernel kernel) {
		super(kernel);
	}




	@Override
	public GeoElement[] process(Command c) throws MyError,
	CircularDefinitionException {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			//check if there is a 3D geo
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){

				// translate object
				if ((ok[0] = (arg[0] instanceof Translateable
						|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
						&& (ok[1] = (arg[1].isGeoVector()))) {				
					ret = kernelA.getManager3D().Translate3D(label, arg[0], (GeoVectorND) arg[1]);
					return ret;
				} else if ((ok[0] = (arg[0] instanceof Translateable
						|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
						&& (ok[1] = (arg[1] instanceof GeoPointND))) {

					// wrap (1,2,3) as Vector[(1,2,3)]
					AlgoVectorPoint3D algoVP = new AlgoVectorPoint3D(cons, (GeoPointND)arg[1]);
					cons.removeFromConstructionList(algoVP);

					ret = kernelA.getManager3D().Translate3D(label, arg[0], algoVP.getVector());
					return ret;
				}
			}
			break;
		}

		return super.process(c);
	}

	@Override
	protected AlgoTranslateVector getAlgoTranslateVector(String label, GeoElement v, GeoElement P){

		if (v.isGeoElement3D()){
			return new AlgoTranslateVector3D(cons, label, (GeoVector3D) v, (GeoPointND) P);
		}

		return super.getAlgoTranslateVector(label, v, P);
	}


}
