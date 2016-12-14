package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.advanced.AlgoAxisSecond;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

public class AlgoAxisSecond3D extends AlgoAxisSecond {
	private GeoLine3D axis; // output

	public AlgoAxisSecond3D(Construction cons, String label, GeoConicND c) {
		super(cons, c);

		axis = new GeoLine3D(cons);
		finishSetup(label);
	}

	@Override
	public final void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b
		axis.setCoord(getConic().getMidpoint3D(), getConic().getEigenvec3D(1));

		P.setCoords(getConic().getMidpoint3D(), false);
	}

	public GeoLineND getAxis() {
		return axis;
	}

}
