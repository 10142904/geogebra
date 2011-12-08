/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;


public class AlgoIntersection extends AlgoIntersectAbstract {

	private GeoList inputList; //input
	private GeoList inputList2; //input
    private GeoList outputList; //output	
    private int size, size2;

    public AlgoIntersection(Construction cons, String label, GeoList inputList, GeoList inputList2) {
        super(cons);        
        
        this.inputList = inputList;
        this.inputList2 = inputList2;
               
        outputList = new GeoList(cons);

        setInputOutput();
        compute();
        outputList.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoIntersection";
    }

  
    @Override
	protected void setInputOutput(){
        input = new GeoElement[2];
        
	    input[0] = inputList;
	    input[1] = inputList2;

        super.setOutputLength(1);
        super.setOutput(0, outputList);
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return outputList;
    }

    @Override
	public final void compute() {
    	
    	size = inputList.size();
    	size2 = inputList2.size();
   	
    	if (!inputList.isDefined() || !inputList2.isDefined()) {
    		outputList.setUndefined();
    		return;
    	} 
       
    	outputList.setDefined(true);
    	outputList.clear();
    	  	
        for (int i=0 ; i < size2 ; i++) {
        	
        	for (int j = 0 ; j < size ; j++) {
        		GeoElement geo = inputList2.get(i);
        		if (inputList.get(j).isEqual(geo)) {
        			
        			boolean alreadyInOutputList = false;
        			for (int k = 0 ; k < outputList.size() ; k++)
        			if (geo.isEqual(outputList.get(k))) {
        				alreadyInOutputList = true;
        				break;
        			}
        			
        			if (!alreadyInOutputList) outputList.add(geo.copy());
        			break;
        		}
        	}        		
        }       	
    }
   
  
}
