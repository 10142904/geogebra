package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Compute the bottom of a limited quadric
 * @author matthieu
 *
 */
public class AlgoQuadricEndBottom extends AlgoQuadricEnd {

	public AlgoQuadricEndBottom(Construction cons, String label, GeoQuadric3DLimited quadric) {
		super(cons, label, quadric);
	}

	@Override
	protected Coords getOrigin(Coords o1, Coords o2) {
		return o1;
	}

	@Override
	protected Coords getV1(Coords v1) {
		return v1.mul(-1);
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoQuadricEndBottom;
	}

	// TODO Consider locusequability

}
