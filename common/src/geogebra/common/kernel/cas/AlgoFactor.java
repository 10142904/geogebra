/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;

/**
 * Factor a function
 * 
 * @author Markus Hohenwarter
 */
public class AlgoFactor extends AlgoCasBase {
   
	public AlgoFactor(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFactor;
	}

	@Override
	protected void applyCasCommand() {
		// factor value form of f
		g.setUsingCasCommand("Factor(%)", f, false);		
	}
}