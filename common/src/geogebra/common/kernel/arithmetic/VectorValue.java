/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * VectorValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.geos.GeoVec2D;

/**
 *
 * @author  Markus
 */
public interface VectorValue extends ExpressionValue { 
	/**
	 * @return this vector value as GeoVec2D
	 */
    public GeoVec2D getVector();
    /**
     * Returns coord mode
     *  POLAR, COMPLEX or CARTESIAN
     * @return Kernel.COORD_*
     */
    public int getMode(); 
    /**
     * Sets coord mode 
     * @param mode Kernel.COORD_*
     */
    public void setMode(int mode);  
}
