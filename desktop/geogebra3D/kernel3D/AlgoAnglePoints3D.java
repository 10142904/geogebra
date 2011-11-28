/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAnglePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.algos.AlgoAnglePoints;
import geogebra.kernel.geos.GeoAngle;
import geogebra.kernel.kernelND.GeoPointND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAnglePoints3D extends AlgoAnglePoints{
	
	private Coords vn;

	AlgoAnglePoints3D(Construction cons, String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		super(cons, label, A, B, C);
	}
	
	
    final protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle3D(cons);
    }
	
    public final void compute() {
    	Coords center = getB().getInhomCoordsInD(3);
    	Coords v1 = getA().getInhomCoordsInD(3).sub(center);
    	Coords v2 = getC().getInhomCoordsInD(3).sub(center);
    	
    	v1.calcNorm();
    	double l1 = v1.getNorm();
    	v2.calcNorm();
    	double l2 = v2.getNorm();
    	
    	if (Kernel.isZero(l1) || Kernel.isZero(l2)){
    		getAngle().setUndefined();
        	return;
    	}
    	
    	double c = v1.dotproduct(v2)/(l1*l2); //cosinus of the angle
    	
    	getAngle().setValue(Math.acos(c));
    	
    	//normal vector
    	vn = v1.crossProduct4(v2).normalized();
    	

    }
	

    public Coords getVn(){
    	return vn;
    }
    
    
	
	
}
