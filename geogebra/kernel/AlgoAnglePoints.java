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

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAnglePoints extends AlgoElement  implements AlgoDrawInformation{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPointND An, Bn, Cn; // input
    private GeoAngle angle; // output  
    
    /** standard normal vector */
    private static final Coords STANDARD_VN = new Coords(0,0,1,0);

    private AlgoAnglePolygon algoAnglePoly;

    transient private double bx, by, vx, vy, wx, wy;

    protected AlgoAnglePoints(
        Construction cons,
        String label,
        GeoPointND A,
        GeoPointND B,
        GeoPointND C) {
        this(cons, A, B, C);
        angle.setLabel(label);
    }

    AlgoAnglePoints(
        Construction cons,
        AlgoAnglePolygon algoAnglePoly,
        GeoPointND A,
        GeoPointND B,
        GeoPointND C) {
        this(cons, A, B, C);
        this.algoAnglePoly = algoAnglePoly;
    }

    public String getClassName() {
        return "AlgoAnglePoints";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    public AlgoAnglePoints(
        Construction cons,
        GeoPointND A,
        GeoPointND B,
        GeoPointND C) {
        super(cons);
        this.An = A;
        this.Bn = B;
        this.Cn = C;
        angle = newGeoAngle(cons);
        setInputOutput(); // for AlgoElement

        // compute angle
        compute();
    }
    
    protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle(cons);
    }
    
    public AlgoAnglePoints(
                     GeoPointND A,
            GeoPointND B,
            GeoPointND C,Construction cons) {
    	super(cons);
    	this.cons=cons;
            this.An = A;
            this.Bn = B;
            this.Cn = C;
         
     }
    
    public AlgoAnglePoints(GeoPointND A, GeoPointND B, GeoPointND C) {
    	super(((GeoElement) A).cons, false);
    	this.An = A;
        this.Bn = B;
        this.Cn = C;
	}

	public AlgoAnglePoints copy(){
    	return new AlgoAnglePoints((GeoPointND)An.copy(),(GeoPointND)Bn.copy(),(GeoPointND)Cn.copy());
    }

    void setAlgoAnglePolygon(AlgoAnglePolygon algo) {
        algoAnglePoly = algo;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = (GeoElement) An;
        input[1] = (GeoElement) Bn;
        input[2] = (GeoElement) Cn;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public void remove() {
        if (algoAnglePoly != null)
            algoAnglePoly.remove();
        else
            super.remove();
    }

    public int getConstructionIndex() {
        if (algoAnglePoly != null)
			return algoAnglePoly.getConstructionIndex();
		else
			return super.getConstructionIndex();
    }

    public GeoAngle getAngle() {
        return angle;
    }
    public GeoPointND getA() {
        return An;
    }
    public GeoPointND getB() {
        return Bn;
    }
    public GeoPointND getC() {
        return Cn;
    }
    
    public Coords getVn(){
    	return STANDARD_VN;
    }

    // calc angle between vectors A-B and C-B    
    // angle in range [0, pi]
    protected void compute() {
    	
    	GeoPoint A = (GeoPoint) An;
    	GeoPoint B = (GeoPoint) Bn;
    	GeoPoint C = (GeoPoint) Cn;
    	
        if (!A.isFinite() || !B.isFinite() || !C.isFinite()) {
            angle.setUndefined(); // undefined
            return;
        }                
        
        // get vectors v=BA and w=BC                        
        bx = B.inhomX;
        by = B.inhomY;
        vx = A.inhomX - bx;
        vy = A.inhomY - by;
        wx = C.inhomX - bx;
        wy = C.inhomY - by;
                
        if (Kernel.isZero(vx) && Kernel.isZero(vy) ||
        		Kernel.isZero(wx) && Kernel.isZero(wy)) {
        	angle.setUndefined();
        	return;
        }
        
       	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = vx * wy - vy * wx;
    	double prod = vx * wx + vy * wy;    	    
    	double value = Math.atan2(det, prod);                  	    	       

        angle.setValue(value);
    }

    final public String toString() {
        
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (algoAnglePoly != null) 
            return app.getPlain("AngleBetweenABCofD",An.getLabel(),Bn.getLabel(),Cn.getLabel(),algoAnglePoly.getPolygon().getNameDescription());
        else
           	return app.getPlain("AngleBetweenABC",An.getLabel(),Bn.getLabel(),Cn.getLabel());


    }
}
