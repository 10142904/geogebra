	package geogebra3D.geogebra.common.geogebra3D.euclidian3D.draw;




import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

import java.util.ArrayList;






/**
 * Class for drawing multiple polygons within intersection curve.
 * @author matthieu
 *
 */
public class DrawIntersectionCurve3D extends Drawable3DCurves implements Previewable {


	private ArrayList<Drawable3D> drawables;
	
	/**
	 * Common constructor
	 * @param a_view3D 3D view
	 * @param geo first geo
	 */
	public DrawIntersectionCurve3D(EuclidianView3D a_view3D, GeoElement geo){
		
		super(a_view3D, geo);
		
		drawables = new ArrayList<Drawable3D>();
		
	}
	
	/**
	 * add a polygon to draw
	 * @param d drawable
	 */
	public void add(Drawable3D d){
		drawables.add(d);
	}

	
	//drawing

	@Override
	public void drawGeometry(Renderer renderer) {

		for (Drawable3D d : drawables){
			d.drawGeometry(renderer);
		}

	}
	
	
	

	
	



	@Override
	public int getPickOrder(){

		return DRAW_PICK_ORDER_1D; 

	}	




    
    
	
	
	@Override
	protected boolean updateForItSelf(){
		
		for (Drawable3D d : drawables){
			d.updateForItSelf();
		}

		return true;
		
	}
	
	

	@Override
	protected void updateForView(){

		for (Drawable3D d : drawables){
			d.updateForView();
		}
	}
	
	
	////////////////////////////////
	// Previewable interface 

	









	public void updateMousePos(double xRW, double yRW) {	
		// TODO Auto-generated method stub
		
	}



	public void updatePreview() {
		
		setWaitForUpdate();
		
		
	}
	
	@Override
	public void disposePreview() {
		super.disposePreview();

		for (Drawable3D d : drawables){
			d.disposePreview();
		}
		
	}



}
