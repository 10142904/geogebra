/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import java.util.Date;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.Prover;
import geogebra.common.kernel.prover.Prover.ProverEngine;
import geogebra.common.main.AbstractApplication;

/**
 *
 * @author  Zoltan Kovacs <zoltan@geogebra.org>
 */
public class AlgoProve extends AlgoElement {

    private GeoElement root;  // input
    private GeoBoolean bool;     // output
    private Boolean result;
        
    /**
     * Proves the given statement and gives a yes/no answer (boolean)
     * @param cons The construction
     * @param label Label for the output
     * @param root Input statement
     */
    public AlgoProve(Construction cons, String label, GeoElement root) {
    	super(cons);
        this.root = root;  
        
        bool = new GeoBoolean(cons);
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        initialCompute();
        compute();
        bool.setLabel(label);
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoProve;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = root;
        
        super.setOutputLength(1);
        super.setOutput(0, bool);
        setDependencies(); // done by AlgoElement
    }    
    
    /**
     * Returns the output for the Prove command
     * @return A boolean: true/false
     */
    public GeoBoolean getGeoBoolean() { return bool; }
    
    // calc the current value of the arithmetic tree
	public final void initialCompute() {
	
	// Create and initialize the prover 
     	Prover p = new Prover();
        if ("OpenGeoProver".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.OPENGEOPROVER);
        else if ("Botana".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.BOTANAS_PROVER);
        else if ("Recio".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.RECIOS_PROVER);
        else if ("PureSymbolic".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.PURE_SYMBOLIC_PROVER);
        else if ("Auto".equalsIgnoreCase(AbstractApplication.proverEngine))
            p.setProverEngine(ProverEngine.AUTO);
        p.setTimeout(AbstractApplication.proverTimeout);
    	p.setConstruction(cons);
    	p.setStatement(root);
    	
    	// Adding benchmarking:
    	Date date = new Date();
        long startTime = date.getTime();
    	p.compute(); // the computation of the proof
    	date = new Date();
    	long elapsedTime = date.getTime() - startTime;
    	AbstractApplication.debug("Benchmarking: " + elapsedTime + " ms");
    	
    	result = p.getYesNoAnswer();
    	AbstractApplication.debug("Statement is " + result);
    }   
	
    @Override
    // Not sure how to do this hack normally. 
    final public String getCommandName(StringTemplate tpl) {
    	return "Prove";
    }
    
    @Override
	final public String toString(StringTemplate tpl) {
    	return getCommandDescription(tpl);
    }
    
    @Override
	public void compute(){
    	if (result != null)
    		bool.setValue(result);
    	else
    		bool.setValue(false);
    }
}

