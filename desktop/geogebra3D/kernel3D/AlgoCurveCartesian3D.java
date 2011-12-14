package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoCurveCartesian;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoCurveCartesianND;


public class AlgoCurveCartesian3D extends AlgoCurveCartesian {

	public AlgoCurveCartesian3D(Construction cons, String label,
			NumberValue[] coords, GeoNumeric localVar, NumberValue from,
			NumberValue to) {
		super(cons, label, coords, localVar, from, to);
	}
	
	protected GeoCurveCartesianND createCurve(Construction cons, Function[] fun){
    	return new GeoCurveCartesian3D(cons, fun);
    }

}
