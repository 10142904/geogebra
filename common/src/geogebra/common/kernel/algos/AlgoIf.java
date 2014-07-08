/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Test;
import geogebra.common.plugin.Operation;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoIf extends AlgoElement {

	private GeoBoolean condition; // input
	private GeoElement ifGeo, elseGeo; // input
	private GeoElement result; // output

	/**
	 * Algorithm for handling of an if-then-else construct
	 * 
	 * @param cons
	 * @param label
	 * @param condition
	 * @param ifGeo
	 * @param elseGeo
	 *            may be null
	 */
	public AlgoIf(Construction cons, String label, GeoBoolean condition,
			GeoElement ifGeo, GeoElement elseGeo) {
		super(cons);
		this.condition = condition;
		this.ifGeo = ifGeo;
		this.elseGeo = elseGeo;

		// create output GeoElement of same type as ifGeo
		if (Test.canSet(elseGeo, ifGeo)) {
			result = elseGeo.copyInternal(cons);
		} else {
			result = ifGeo.copyInternal(cons);
		}

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		result.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.If;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (elseGeo != null)
			input = new GeoElement[3];
		else
			input = new GeoElement[2];
		input[0] = condition;
		input[1] = ifGeo;
		if (elseGeo != null)
			input[2] = elseGeo;

		super.setOutputLength(1);
		super.setOutput(0, result);
		setDependencies(); // done by AlgoElement
	}

	public GeoElement getGeoElement() {
		return result;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		/*
		 * TODO do we want this? if
		 * (!ifGeo.getClass().isAssignableFrom(elseGeo.getClass()) &&
		 * !elseGeo.getClass().isAssignableFrom(ifGeo.getClass())) {
		 * result.setUndefined(); return; }
		 */

		try {
			if (condition.getBoolean()) {
				result.set(ifGeo);
				if (ifGeo.getDrawAlgorithm() instanceof DrawInformationAlgo) {
					result.setDrawAlgorithm(((DrawInformationAlgo) ifGeo
							.getDrawAlgorithm()).copy());
				}
			} else {
				if (elseGeo == null)
					result.setUndefined();
				else
					result.set(elseGeo);
				if (elseGeo.getDrawAlgorithm() instanceof DrawInformationAlgo) {
					result.setDrawAlgorithm(((DrawInformationAlgo) elseGeo
							.getDrawAlgorithm()).copy());
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			result.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	// Curve[If[t>0,t^2,-t^2],t,t,-5,5]
	public ExpressionNode toExpression() {
		if (this.elseGeo == null) {
			return new ExpressionNode(kernel,
					kernel.convertNumberValueToExpressionNode(this.condition),
					Operation.IF,
					kernel.convertNumberValueToExpressionNode(this.ifGeo));
		}
		return new ExpressionNode(kernel, new MyNumberPair(kernel,
				kernel.convertNumberValueToExpressionNode(this.condition),
				kernel.convertNumberValueToExpressionNode(this.ifGeo)),
				Operation.IF_ELSE,
				kernel.convertNumberValueToExpressionNode(this.elseGeo));
	}

	// TODO Consider locusequability
}
