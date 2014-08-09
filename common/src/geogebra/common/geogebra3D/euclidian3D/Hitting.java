package geogebra.common.geogebra3D.euclidian3D;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;

/**
 * class for rays, spheres, etc. that can hit 3D objects in 3D view
 * @author Proprietaire
 *
 */
public class Hitting {
	
	/**
	 *  origin of the ray
	 */
	public Coords origin;

	/**
	 *  direction of the ray
	 */
	public Coords direction;
	
	/**
	 * last mouse pos (centered)
	 */
	public GPoint pos;
	
	
	private EuclidianView3D view;
	
	/**
	 * current threshold
	 */
	private int threshold;

	/**
	 * constructor
	 * @param view 3D view
	 */
	public Hitting(EuclidianView3D view){
		this.view = view;
		pos = new GPoint();
	}
	
	/**
	 * set the hits
	 * @param mouseLoc mouse location
	 * @param threshold threshold
	 */
	public void setHits(GPoint mouseLoc, int threshold){
		
		view.setCenteredPosition(mouseLoc, pos);
		
		Hits3D hits = view.getHits3D();
		hits.init();

		origin = view.getPickPoint(mouseLoc); 
		if (view.getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE 
				|| view.getProjection() ==  EuclidianView3D.PROJECTION_GLASSES) {
			origin = view.getRenderer().getPerspEye().copyVector();
		}
		view.toSceneCoords3D(origin); 
		direction = view.getViewDirection();
		
		this.threshold = threshold;

		if (view.getShowPlane()){
			view.getPlaneDrawable().hitIfVisibleAndPickable(this, hits);
		}
		for (int i = 0; i < 3; i++) {
			view.getAxisDrawable(i).hitIfVisibleAndPickable(this, hits); 
		}
		view.getDrawList3D().hit(this, hits);

		hits.sort();
	}
	
	/**
	 * 
	 * @param mouseLoc mouse location
	 * @return first hitted label geo
	 */
	public GeoElement getLabelHit(GPoint mouseLoc){
		view.setCenteredPosition(mouseLoc, pos);
		return view.getDrawList3D().getLabelHit(pos);
	}
	
	/**
	 * 
	 * @param p point coords
	 * @return true if the point is inside the clipping box (if used)
	 */
	final public boolean isInsideClipping(Coords p){
		if (view.useClippingCube()){
			return view.isInside(p);
		}
		
		return true;
		
	}
	
	/**
	 * 
	 * @return current threshold
	 */
	public int getThreshold(){
		return threshold;
	}
}
