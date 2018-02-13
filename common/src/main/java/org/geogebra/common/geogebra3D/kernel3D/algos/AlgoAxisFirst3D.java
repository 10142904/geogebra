package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.advanced.AlgoAxisFirst;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 * Major axis
 */
public class AlgoAxisFirst3D extends AlgoAxisFirst {
	private GeoLine3D axis; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoAxisFirst3D(Construction cons, String label, GeoConicND c) {
		super(cons, c);
		axis = new GeoLine3D(cons);
		finishSetup(label);
	}

	@Override
	public final void compute() {
		// axes are lines with directions of eigenvectors
		// through midpoint b
		axis.setCoord(getConic().getMidpoint3D(), getConic().getEigenvec3D(0));
		P.setCoords(getConic().getMidpoint3D(), false);
	}

	@Override
	public GeoLineND getAxis() {
		return axis;
	}

}
