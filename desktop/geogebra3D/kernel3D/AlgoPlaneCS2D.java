package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Create a plane containing a 2D coord sys
 * @author ggb3D
 *
 */
public class AlgoPlaneCS2D extends AlgoElement3D {
	
	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;
	
	
	/** polygon */
	private GeoCoordSys2D csInput;
	
	/**
	 * create a plane joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param csInput 
	 */
	public AlgoPlaneCS2D(Construction c, String label, GeoCoordSys2D csInput) {

		this(c, csInput);
		
		((GeoElement) cs).setLabel(label);
		
	}
	
	public AlgoPlaneCS2D(Construction c, GeoCoordSys2D csInput) {
		super(c);
		 
		this.csInput = csInput;

		cs = new GeoPlane3D(c);
		
		//set input and output		
		setInputOutput(new GeoElement[]{(GeoElement) csInput}, new GeoElement[]{(GeoElement) cs});
		
		
	}
	
	
	@Override
	public void compute() {
		
		CoordSys coordsys = cs.getCoordSys();
		

		if (!((GeoElement) csInput).isDefined()){
			coordsys.setUndefined();
			return;
		}
		
		
		//copy the coord sys
		coordsys.set(csInput.getCoordSys());
		
		//recalc equation vector (not existing for polygons, ...)
		if (coordsys.isDefined()) 
			coordsys.makeEquationVector();
		
		
	}

	
	/**
	 * return the cs
	 * @return the cs
	 */
	public GeoCoordSys2D getCoordSys() {		
		return cs;
	}
	
	
	
	@Override
	public Algos getClassName() {
		return Algos.AlgoPlane;
	}
	
    @Override
	final public String toString(StringTemplate tpl) {
    	return app.getPlain("PlaneContainingA",((GeoElement) csInput).getLabel(tpl));

    }

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

}
