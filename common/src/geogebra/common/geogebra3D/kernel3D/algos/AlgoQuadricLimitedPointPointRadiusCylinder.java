package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * Algo for cylinder between two end points and given radius.
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedPointPointRadiusCylinder extends
		AlgoQuadricLimitedPointPointRadius {

	/**
	 * 
	 * @param c
	 * @param labels
	 * @param origin
	 * @param secondPoint
	 * @param r
	 */
	public AlgoQuadricLimitedPointPointRadiusCylinder(Construction c,
			String[] labels, GeoPointND origin, GeoPointND secondPoint,
			NumberValue r) {
		super(c, labels, origin, secondPoint, r,
				GeoQuadricNDConstants.QUADRIC_CYLINDER);

	}

	@Override
	protected void createEnds() {
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, getQuadric());
		cons.removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = algo2.getSection2();

	}

	@Override
	protected void setOutput() {
		setOutput(new GeoElement[] { getQuadric(), getQuadric().getBottom(),
				getQuadric().getTop(), getQuadric().getSide() });
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, double r,
			double min, double max) {
		getQuadric().setCylinder(o1, d, r, min, max);
	}

	@Override
	public Commands getClassName() {
		return Commands.Cylinder;
	}

	// //////////////////////
	// ALGOTRANSFORMABLE
	// //////////////////////

	@Override
	protected AlgoElement getTransformedAlgo(String[] labels, GeoPointND p1,
			GeoPointND p2, GeoNumeric r) {
		return new AlgoQuadricLimitedPointPointRadiusCylinder(this.cons,
				labels, p1, p2, r);
	}

}
