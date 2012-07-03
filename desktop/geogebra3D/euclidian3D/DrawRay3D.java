package geogebra3D.euclidian3D;

import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoRayND;
import geogebra.main.AppD;
import geogebra3D.Application3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoRay3D;

import java.util.ArrayList;

/**
 * Class for drawing a 3D ray.
 * @author matthieu
 *
 */
public class DrawRay3D extends DrawCoordSys1D {

	
	/**
	 * common constructor
	 * @param a_view
	 * @param ray
	 */
	public DrawRay3D(EuclidianView3D a_view, GeoRayND ray)
	{
 		super(a_view, (GeoElement) ray);
	}
	
	
	
	@Override
	protected boolean updateForItSelf(){

		updateForItSelf(true);
		
		return true;

	}
	
	/**
	 * update when the element is modified
	 * @param updateDrawMinMax update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}
	
	
	
	/**
	 * update min and max values
	 */
	protected void updateDrawMinMax(){

		
		GeoLineND line = (GeoLineND) getGeoElement();

		Coords o = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 0).getInhomCoordsInSameDimension());  
		Coords v = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 1).getInhomCoordsInSameDimension()).sub(o);
		
				
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {0,Double.POSITIVE_INFINITY},
				o, v, true);
		
		setDrawMinMax(minmax[0], minmax[1]);
		
		/*
		if (((GeoElement) line).getLabel().equals("l"))
			Application.debug(line+"\n"+o+"\n"+v+"\n"+minmax[0]+", "+minmax[1]);
		*/
		
	}
	
	

	@Override
	protected void updateForView(){
		if (getView3D().viewChanged())
			updateForItSelf();
	}
	


	

	

	////////////////////////////////
	// Previewable interface 
	
	
	/**
	 * Constructor for previable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawRay3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoRay3D(a_view3D.getKernel().getConstruction()));
		

		
	}	

}
