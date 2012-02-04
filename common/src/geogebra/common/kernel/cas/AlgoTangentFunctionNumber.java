/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTangents.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.cas;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoTangentFunctionNumber extends AlgoUsingTempCASalgo {

	private NumberValue n; // input
	private GeoElement ngeo;
	private GeoFunction f; // input
	private GeoLine tangent; // output

	private GeoPoint2 T;
	private GeoFunction deriv;

	public AlgoTangentFunctionNumber(Construction cons, String label,
			NumberValue n, GeoFunction f) {
		super(cons);
		this.n = n;
		ngeo = (GeoElement) n.toGeoElement();
		this.f = f;

		tangent = new GeoLine(cons);
		T = new GeoPoint2(cons);
		tangent.setStartPoint(T);

		// derivative of f
		algoCAS = new AlgoDerivative(cons, f);
		deriv = (GeoFunction) ((AlgoDerivative) algoCAS).getResult();
		cons.removeFromConstructionList(algoCAS);

		setInputOutput(); // for AlgoElement
		compute();
		tangent.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTangentFunctionNumber;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = ngeo;
		input[1] = f;

		setOutputLength(1);
		setOutput(0, tangent);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getTangent() {
		return tangent;
	}

	GeoFunction getFunction() {
		return f;
	}

	// calc tangent at x=a
	@Override
	public final void compute() {
		double a = n.getDouble();
		if (!f.isDefined() || !deriv.isDefined() || Double.isInfinite(a)
				|| Double.isNaN(a)) {
			tangent.setUndefined();
			return;
		}

		// calc the tangent;
		double fa = f.evaluate(a);
		double slope = deriv.evaluate(a);
		tangent.setCoords(-slope, 1.0, a * slope - fa);
		T.setCoords(a, fa, 1.0);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("TangentToAatB", f.getLabel(tpl),
				"x = " + ngeo.getLabel(tpl));
	}
}
