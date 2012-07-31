/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Area of polygon P[0], ..., P[n]
 *
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionConditional;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * algorithm for FDistribution[0,1,x]
 * @author  Michael
 */
public class AlgoFDistributionDF extends AlgoElement implements AlgoDistributionDF {

	private NumberValue d1, d2;  // input
	private BooleanValue cumulative; // optional input
	private GeoFunctionConditional ret;     // output           

	private GeoFunction ifFun, elseFun, condFun;           
        
    @SuppressWarnings("javadoc")
	public AlgoFDistributionDF(Construction cons, String label, NumberValue mean, NumberValue sd, BooleanValue cumulative) {       
  	  	this(cons, mean, sd, cumulative);
        ret.setLabel(label);
      }   
    
    @SuppressWarnings("javadoc")
	public AlgoFDistributionDF(Construction cons, NumberValue a, NumberValue b, BooleanValue cumulative) {       
  	  super(cons); 
        this.d1 = a;
        this.d2 = b;
        this.cumulative = cumulative;
        ret = new GeoFunctionConditional(cons); 

        // make function x<0
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		Function tempFun = new Function(en.lessThan(0),fv);
		condFun = new GeoFunction(cons, tempFun);
		ret.setConditionalFunction(condFun);
		
        // make function x=0
		fv = new FunctionVariable(kernel);	
		en = new ExpressionNode(kernel, 0);
		tempFun = new Function(en,fv);
		ifFun = new GeoFunction(cons, tempFun);
		ret.setIfFunction(ifFun);

		setInputOutput(); // for AlgoElement
        
        // compute angle
        compute();     
      }   
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoFDistributionDF;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	
    	// dummy function for the "x" argument, eg
    	// Normal[0,1,x]
    	// Normal[0,1,x,true]
		FunctionVariable fv = new FunctionVariable(kernel);	
		ExpressionNode en = new ExpressionNode(kernel,fv);
		Function tempFun = new Function(en,fv);
		GeoFunction dummyFun = new GeoFunction(cons, tempFun);
    	
        input =  new GeoElement[cumulative == null ? 3 : 4];
        input[0] = d1.toGeoElement();
        input[1] = d2.toGeoElement();
        input[2] = dummyFun;
        if (cumulative != null) {
        	input[3] = (GeoElement) cumulative;
        }
        
        super.setOutputLength(1);
        super.setOutput(0, ret);
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * @return Normal PDF or CDF function
     */
    public GeoFunction getResult() { return ret; }        

    @Override
	public void compute() {
		FunctionVariable fv = new FunctionVariable(kernel);
		ExpressionNode fvEn = new ExpressionNode(kernel, fv);
		ExpressionNode d1En = new ExpressionNode(kernel, d1);
		ExpressionNode d2En = new ExpressionNode(kernel, d2);
		
		ExpressionNode halfd1 = d1En.divide(2);
		ExpressionNode halfd2 = d2En.divide(2);
		

		ExpressionNode en;
		
		if (cumulative != null && cumulative.getBoolean()) {


			en = d1En.divide(2).betaRegularized(halfd2, fvEn.multiply(d1).divide(fvEn.multiply(d1).plus(d2)));


			// old hack:
			//command = "If[x<0,0,betaRegularized(("+d1+")/2,("+d2+")/2,("+d1+")*x/(("+d1+")*x+"+d2+"))]";

		} else {

			
			ExpressionNode beta = halfd1.beta(halfd2);
			
			ExpressionNode mult = d2En.power(halfd2);
			
			mult = fvEn.multiply(d1).power(halfd1).multiply(mult);

			ExpressionNode div = fvEn.multiply(d1).plus(d2).power(d1En.plus(halfd2)).multiply(fv).multiply(beta);
			
			en = mult.divide(div);
			
			// old hack:
			//command = "If[x<0,0,((("+d1+")*x)^(("+d1+")/2)*("+d2+")^(("+d2+")/2))/(x*(("+d1+")*x+"+d2+")^(("+d1+"+"+d2+")/2)*beta(("+d1+")/2,("+d2+")/2))]";
		}
		
		Function tempFun = new Function(en, fv);
		tempFun.initFunction();
		
		elseFun = new GeoFunction(cons, tempFun);
		
		ret.setElseFunction(elseFun);


    }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

	
}
