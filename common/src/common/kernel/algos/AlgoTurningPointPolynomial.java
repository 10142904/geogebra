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
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint2;


/**
 * Finds all inflection points of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTurningPointPolynomial extends AlgoRootsPolynomial {
        
    public AlgoTurningPointPolynomial(Construction cons, String [] labels, GeoFunction f) {
        super(cons, labels, f);             
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoTurningPointPolynomial;    
    }
            
    public GeoPoint2 [] getInflectionPoints() {
        return super.getRootPoints();
    }
    
    @Override
	public final void compute() {              
        if (f.isDefined()) {
            yValFunction = f.getFunction();                                                                    
            
            // roots of second derivative 
            //(roots without change of sign are removed)
            calcRoots(yValFunction, 2);                                                          
        } else {
            curRealRoots = 0;                           
        }                       
                
        setRootPoints(curRoots, curRealRoots);                  
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("InflectionPointofA",f.getLabel());
    }

}
