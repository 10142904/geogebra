/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLinePointLine3D extends AlgoOrtho {

 

    public AlgoOrthoLinePointLine3D(Construction cons, String label, GeoPointND point, GeoLineND line) {
        super(cons,label,point, (GeoElement) line);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoOrthoLinePointLine;
    }


    private GeoLineND getInputLine(){
    	return (GeoLineND) getInputOrtho();
    }

  
    @Override
	public final void compute() {
    	
    	GeoLineND line = getInputLine();
    	Coords o = line.getPointInD(3, 0);
    	Coords v1 = line.getPointInD(3, 1).sub(o);
    	Coords o2 = getPoint().getCoordsInD(3);
    	Coords v2 = o2.sub(o);
    	
    	Coords v3 = v1.crossProduct(v2);
    	Coords v = v3.crossProduct(v1);
    	
    	if (v.equalsForKernel(0, Kernel.STANDARD_PRECISION))
    		getLine().setUndefined();
    	else
    		getLine().setCoord(getPoint().getCoordsInD(3), v.normalize());
        
    }

	// TODO Consider locusequability

}
