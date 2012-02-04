package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.CasEvaluableFunction;

public class AlgoSimplify extends AlgoCasBase {

	public AlgoSimplify(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoSimplify;
	}

	@Override
	protected void applyCasCommand(StringTemplate tpl) {
		g.setUsingCasCommand("Simplify(%)", f, false);		
	}
	
}
