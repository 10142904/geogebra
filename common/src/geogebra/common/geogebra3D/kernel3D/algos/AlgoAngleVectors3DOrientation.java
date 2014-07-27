package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
* angle for three points, oriented
* @author  mathieu
*/
public class AlgoAngleVectors3DOrientation extends AlgoAngleVectors3D{
	
	private GeoDirectionND orientation;

	AlgoAngleVectors3DOrientation(Construction cons, String label, GeoVectorND v, GeoVectorND w, GeoDirectionND orientation) {
		super(cons, label, v, w, orientation);
	}
	

	@Override
	protected void setInput(GeoVectorND v, GeoVectorND w, GeoDirectionND orientation){

		super.setInput(v, w, orientation);
		this.orientation = orientation;
	}
	
	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}
	
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) getv();
		input[1] = (GeoElement) getw();
		input[2] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, getAngle());
		setDependencies(); // done by AlgoElement
	}


    @Override
	public void compute() {
    	
    	super.compute();
    	
    	if (orientation == kernel.getSpace()){ // no orientation with space
    		return;
    	}
    	
    	if (!getAngle().isDefined() || Kernel.isZero(getAngle().getValue())){
    		return;
    	}
    	
    	if (vn.dotproduct(orientation.getDirectionInD3()) < 0){
    		GeoAngle a = getAngle();
    		a.setValue(2*Math.PI-a.getValue());
    		vn = vn.mul(-1);
    	}
    }

    @Override
	public String toString(StringTemplate tpl) {

		//return loc.getPlain("AngleBetweenABOrientedByC", getv().getLabel(tpl),
		//		getw().getLabel(tpl), orientation.getLabel(tpl));
		
    	// clearer just as "angle between u and v"
		return loc.getPlain("AngleBetweenAB", getv().getLabel(tpl),
				getw().getLabel(tpl));

	}
    
    
}
