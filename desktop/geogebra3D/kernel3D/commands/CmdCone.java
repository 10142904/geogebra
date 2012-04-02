package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.MyError;

public class CmdCone extends CommandProcessor {
	
	
	
	
	public CmdCone(Kernel kernel) {
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
	    			(ok[0] = (arg[0] instanceof GeoConicND ) )
	    			&& (ok[1] = (arg[1] .isNumberValue() )) 
	    	) {
	    		return kernelA.getManager3D().ConeLimited(
	    				c.getLabels(),(GeoConicND) arg[0], (NumberValue) arg[1]);
	    	}

	    	if (!ok[0])
	    		throw argErr(arg[0]);
	    	throw argErr(arg[1]);

	    
	    case 3 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] .isGeoVector() ))
	    			&& (ok[2] = (arg[2] .isNumberValue() )) 
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernelA.getManager3D().Cone(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoVectorND) arg[1],
	    						(NumberValue) arg[2])};
	    		return ret;
	    	}else if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] .isGeoPoint() ))
	    			&& (ok[2] = (arg[2] .isNumberValue() )) 
	    	) {
	    		return conePointPointRadius(
	    						c,
	    						(GeoPointND) arg[0],
	    						(GeoPointND) arg[1],
	    						(NumberValue) arg[2]);
	    	}else if(
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND ))
	    			&& (ok[2] = (arg[2] .isNumberValue() )) 
	    	) {
	    		GeoElement[] ret =
	    		{
	    				kernelA.getManager3D().Cone(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoLineND) arg[1],
	    						(NumberValue) arg[2])};
	    		return ret;
	    	}else{
	    		if (!ok[0])
	    			throw argErr(arg[0]);
	    		else if (!ok[1])
	    			throw argErr(arg[1]);
	    		else
	    			throw argErr(arg[2]);
	    	}

	    default :
	    	throw argNumErr(n);
	    }
	    

	}
	
	

	//overridded by CmdConeInfinite
	
	protected GeoElement[] conePointPointRadius(Command c, GeoPointND p1, GeoPointND p2, NumberValue r){
		return kernelA.getManager3D().ConeLimited(
				c.getLabels(),p1,p2,r);
	}
	
	protected MyError argErr(GeoElement geo){
		return argErr(app,"Cone",geo);
	}
	
	protected MyError argNumErr(int n){
		return argNumErr(app,"Cone",n);
	}
	
}
