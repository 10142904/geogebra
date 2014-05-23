package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidianForPlane.EuclidianViewForPlaneInterface;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import geogebra.common.geogebra3D.kernel3D.geos.GeoSpace;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdOrthogonalLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalLine3D extends CmdOrthogonalLine {
	
	
	
	public CmdOrthogonalLine3D(Kernel kernel) {
		super(kernel);
	}
	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoCoordSys2D ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoCoordSys2D) arg[1])};
	    		return ret;
	    	}else if (((ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND )))){
	    		
	    		
	    		// first check if we're in macro
	    		if (kernelA.isMacroKernel()){
		    		//check if there is a 3D geo: then use 3D algo
		    		if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
		    			GeoElement[] ret =
		    				{
		    					(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
		    							c.getLabel(),
		    							(GeoPointND) arg[0],
		    							(GeoLineND) arg[1])};
		    			return ret;
		    		}

		    		//only 2D objects : use 2D algo
		    		GeoElement[] ret =
		    			{
		    				getAlgoDispatcher().OrthogonalLine(
		    						c.getLabel(),
		    						(GeoPoint) arg[0],
		    						(GeoLine) arg[1])};
		    		return ret;
		    		

	    		}
	    		
	    		//first check if it's an input line call, with 2D/3D view active
	    		EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
	    		if (!kernelA.getLoadingMode() && view!=null){
	    			if (app.getActiveEuclidianView().isDefault2D()){
	    				//xOy view is active : force parallel to xOy plane
	    				GeoElement[] ret =
	    	    			{
	    	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    	    						c.getLabel(),
	    	    						(GeoPointND) arg[0],
	    	    						(GeoLineND) arg[1],
	    	    						(GeoDirectionND) kernelA.getXOYPlane())};
	    	    		return ret;
	    			}
	    			
	    			if (view instanceof EuclidianViewForPlaneInterface){
	    				//plane view is active : force parallel to the plane
	    				GeoElement[] ret =
	    	    			{
	    	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    	    						c.getLabel(),
	    	    						(GeoPointND) arg[0],
	    	    						(GeoLineND) arg[1],
	    	    						((EuclidianViewForPlaneCompanion) ((EuclidianView) view).getCompanion()).getPlane())};
	    	    		return ret;
	    			}
	    			
	    			//3D view is active : force "in space"
	    			GeoElement[] ret =
	    				{
	    					(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoLineND) arg[1])};
	    			return ret;
	    			
	    		}
	    		
	    		
	    		//check if there is a 3D geo: then use 3D algo
	    		if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
	    			GeoElement[] ret =
	    				{
	    					(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoLineND) arg[1])};
	    			return ret;
	    		}

	    		//else use 3D algo, parallel to xOyPlane
	    		GeoElement[] ret =
	    			{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoLineND) arg[1],
	    						(GeoDirectionND) kernelA.getXOYPlane())};
	    		return ret;

	    	}else if (
	    			((ok[0] = (arg[0] instanceof GeoLineND ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND )))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoLineND) arg[0],
	    						(GeoLineND) arg[1])};
	    		return ret;
	    	}
	    	break;
	    
	    case 3 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoDirectionND )) 
	    			&& (ok[2] = (arg[2] instanceof GeoDirectionND )
	    			//"space" not allowed as 2nd arg
	    			&& !(arg[1] instanceof GeoSpace)
	    			//check if it's not 2 planes (or plane-"space")
	    			&& !((arg[1] instanceof GeoCoordSys2D) && (arg[2] instanceof GeoCoordSys2D || arg[2] instanceof GeoSpace))
	    					)
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoDirectionND) arg[1],
	    						(GeoDirectionND) arg[2])};
	    		return ret;
	    	}
	    	
	    	break;
	    }
	    

	    return super.process(c);
	}
	
}
