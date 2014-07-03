/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.TangentAlgo;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algorithm for tangent of function
 */
public class AlgoTangentFunctionPoint extends AlgoElement implements TangentAlgo {

	private GeoPointND P; // input
	private GeoLine tangent; // output
	private GeoFunction f;
	private GeoPoint T;
	private boolean pointOnFunction;
	private GeoFunction deriv;
	private AlgoDerivative algo;

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point on function
	 * @param f function
	 */
	public AlgoTangentFunctionPoint(Construction cons, String label,
			GeoPointND P, GeoFunction f) {
		this(cons, P, f);
		tangent.setLabel(label);
	}

	/**
	 * @param cons construction
	 * @param P point on function
	 * @param f function
	 */
	public AlgoTangentFunctionPoint(Construction cons, GeoPointND P,
			GeoFunction f) {
		super(cons);
		this.P = P;
		this.f = f;

		tangent = new GeoLine(cons);

		// check if P is defined as a point of the function's graph
		pointOnFunction = false;
		if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath algo = (AlgoPointOnPath) P.getParentAlgorithm();
			pointOnFunction = algo.getPath() == f;
		}

		if (pointOnFunction)
			T = (GeoPoint) P;
		else
			T = new GeoPoint(cons);
		tangent.setStartPoint(T);

		// derivative of f
		// use fast non-CAS derivative
		algo = new AlgoDerivative(cons, f, true);
		deriv = (GeoFunction) algo.getResult();
		cons.removeFromConstructionList(algo);

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = f;

		setOutputLength(1);
		setOutput(0, tangent);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting tangent
	 */
	public GeoLine getTangent() {
		return tangent;
	}

	/**
	 * @return function
	 */
	GeoFunction getFunction() {
		return f;
	}

	/**
	 * @return point on function
	 */
	GeoPointND getPoint() {
		return P;
	}

	/**
	 * @return point on function
	 */
	GeoPoint getTangentPoint() {
		return T;
	}
	
    public GeoPoint getTangentPoint(GeoElement geo, GeoLine line) {
        if (geo != f)
            return null;
        if (line != tangent) {
        	return null;
        }
        return T;
    }

	// calc tangent at x=a
	@Override
	public final void compute() {
		if (!(f.isDefined() && P.isDefined() && deriv.isDefined())) {
			tangent.setUndefined();
			return;
		}

		// calc the tangent;
		double a = P.getInhomX();
		double fa = f.evaluate(a);
		double slope = deriv.evaluate(a);
		tangent.setCoords(-slope, 1.0, a * slope - fa);

		if (!pointOnFunction)
			T.setCoords(a, fa, 1.0);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return loc.getPlain("TangentToAatB", f.getLabel(tpl),
				"x = x(" + P.getLabel(tpl) + ")");

	}

	// TODO Consider locusequability

}
