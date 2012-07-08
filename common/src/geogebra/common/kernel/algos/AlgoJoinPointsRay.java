/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoRay;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.locusequ.elements.EquationJoinPointsRay;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsRay extends AlgoElement {

    private GeoPoint P, Q;  // input
    private GeoRay  ray;     // output       
        
    /** Creates new AlgoJoinPoints */
    public AlgoJoinPointsRay(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        ray = new GeoRay(cons, P); 
        ray.setEndPoint(Q);
        

        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      

        ray.setLabel(label);
        addIncidence();
    }   
    
    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
    	P.addIncidence(ray);
    	Q.addIncidence(ray);
	}
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoJoinPointsRay;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_RAY;
    }
       
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
         
        super.setOutputLength(1);
        super.setOutput(0, ray);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoRay getRay() { return ray; }
    public GeoPoint getP() { return P; }
    public GeoPoint getQ() { return Q; }
    
    // calc the line g through P and Q    
    @Override
	public final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        GeoVec3D.lineThroughPoints(P, Q, ray);        
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
    	return app.getPlain("RayThroughAB",P.getLabel(tpl),Q.getLabel(tpl));

    }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return new EquationJoinPointsRay(element, scope);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
