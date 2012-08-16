/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

//
 
package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;



/**
 * Computes intersection of two polygons
 * @author George Sturr
 *
 */
public class AlgoPolygonIntersection extends AlgoPolygonOperation {
    
	/**
	 * @param cons construction
	 * @param labels labels for output
	 * @param inPoly0 first input polygon
	 * @param inPoly1 second input polygon
	 */
	public AlgoPolygonIntersection(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {
		
		super(cons, labels, inPoly0, inPoly1, PolyOperation.INTERSECTION);
	}
	
	@Override
	public Algos getClassName() {
		return Algos.AlgoPolygonIntersection;
	}

	// TODO Consider locusequability
	
}