/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.main.App;

import org.apache.commons.math.distribution.BinomialDistribution;

/**
 * 
 * @author G. Sturr
 */

public class AlgoBinomialDist extends AlgoDistribution {

	/**
	 * @param cons construction
	 * @param label label
	 * @param a number of trials
	 * @param b probability of success
	 * @param c value of random variable
	 * @param isCumulative cumulative
	 */
	public AlgoBinomialDist(Construction cons, String label, NumberValue a,
			NumberValue b, NumberValue c, GeoBoolean isCumulative) {
		super(cons, label, a, b, c, isCumulative);
	}
	/**
	 * @param cons construction
	 * @param a number of trials
	 * @param b probability of success
	 * @param c value of random variable
	 * @param isCumulative cumulative
	 */
	public AlgoBinomialDist(Construction cons, NumberValue a, NumberValue b,
			NumberValue c, GeoBoolean isCumulative) {
		super(cons, a, b, c, isCumulative);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoBinomialDist;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()
				&& input[2].isDefined() && input[3].isDefined()) {
			int param = (int) Math.round(a.getDouble());
			double param2 = b.getDouble();
			double val = c.getDouble();
			try {
				BinomialDistribution dist = getBinomialDistribution(param,
						param2);
				if (isCumulative.getBoolean()) {
					num.setValue(dist.cumulativeProbability(val)); // P(X <=
																	// val)
				} else {
					num.setValue(dist.probability(val)); // P(X = val)
				}
			} catch (Exception e) {
				App.debug(e.getMessage());
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
