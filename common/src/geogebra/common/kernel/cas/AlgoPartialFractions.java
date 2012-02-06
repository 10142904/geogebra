/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;

public class AlgoPartialFractions extends AlgoCasBase {

	public AlgoPartialFractions(Construction cons, String label,
			CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoPartialFractions;
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {

		// f.getVarString() can return a number in wrong alphabet (need ASCII)
		
		// get variable string with tmp prefix,
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = f.getVarString(tpl);
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);

		sbAE.setLength(0);
		sbAE.append("PartialFractions(%");
		sbAE.append(",");
		sbAE.append(varStr);
		sbAE.append(")");

		g.setUsingCasCommand(sbAE.toString(), f, false);
	}

}
