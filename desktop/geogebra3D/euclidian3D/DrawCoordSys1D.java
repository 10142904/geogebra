package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoCoordSys1D;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * @author matthieu
 *
 */
public abstract class DrawCoordSys1D extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];
	


	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param cs1D
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoElement cs1D){
		
		super(a_view3D, cs1D);
	}	
	
	
	
	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);
		
	}

	
	
	/**
	 * sets the values of drawable extremities
	 * @param drawMin
	 * @param drawMax
	 */
	public void setDrawMinMax(double drawMin, double drawMax){
		this.drawMinMax[0] = drawMin;
		this.drawMinMax[1] = drawMax;
	}
	
	
	/**
	 * @return the min-max extremity
	 */
	public double[] getDrawMinMax(){
		return drawMinMax;
	}
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}
	
	

	@Override
	public void drawInObjFormat(Renderer renderer) throws IOException{
		if(isVisible()){
			renderer.getGeometryManager().drawInObjFormat(getGeoElement(),getGeometryIndex());
		}
	}
	
	
	
	private Coords startPoint, endPoint;
	
	protected boolean updateForItSelf(){
		
		//updateColors();
		
		GeoLineND cs = (GeoLineND) getGeoElement();
		double[] minmax = getDrawMinMax(); 
		updateForItSelf(cs.getPointInD(3,minmax[0]).getInhomCoords(), cs.getPointInD(3,minmax[1]).getInhomCoords());
		
		return true;
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * @param p1
	 * @param p2
	 */
	final protected void updateForItSelf(Coords p1, Coords p2){

		//TODO prevent too large values
		startPoint = p1;
		endPoint = p2;
		
		double[] minmax = getDrawMinMax(); 
		
		if (Math.abs(minmax[0])>1E10)
			return;
		
		if (Math.abs(minmax[1])>1E10)
			return;
		
		if (minmax[0]>minmax[1])
			return;
		
		
		Renderer renderer = getView3D().getRenderer();
		


		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture( (float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);		

		brush.segment(p1, p2);
		setGeometryIndex(brush.end());
		
		
		
	}
	
	
	/**
	 * @return the line thickness
	 */
	protected int getLineThickness(){
		return getGeoElement().getLineThickness();
	}
	
	
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings("unchecked")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoCoordSys1D cs1D){
		
		super(a_view3D);
		
		
		cs1D.setIsPickable(false);
		setGeoElement(cs1D);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {

		if (selectedPoints == null){ //when intersection curve
			setWaitForUpdate();
			return;
		} 

		if (selectedPoints.size()==2){
			GeoPointND firstPoint = (GeoPointND) selectedPoints.get(0);
			GeoPointND secondPoint = (GeoPointND) selectedPoints.get(1);
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getInhomCoordsInD(3), secondPoint.getInhomCoordsInD(3));
			getGeoElement().setEuclidianVisible(true);
			//setWaitForUpdate();
		}else if (selectedPoints.size()==1){
			GeoPointND firstPoint = (GeoPointND) selectedPoints.get(0);
			GeoPointND secondPoint = getView3D().getCursor3D();
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getInhomCoordsInD(3), secondPoint.getInhomCoordsInD(3));
			getGeoElement().setEuclidianVisible(true);
			//setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
			//setWaitForUpdate();
		}
		
		//Application.debug("selectedPoints : "+selectedPoints+" -- isEuclidianVisible : "+getGeoElement().isEuclidianVisible());
	
		setWaitForUpdate();	
	}
	
	

	
	@Override
	public boolean hit(Hitting hitting){
		
		Coords[] project = CoordMatrixUtil.nearestPointsFromTwoLines(hitting.origin, hitting.direction, startPoint, endPoint.sub(startPoint));
		
		// check if hitting and line are parallel
		double parameterOnHitting = project[2].getX();
		if (Double.isNaN(parameterOnHitting)){
			return false;
		}
		
		// check if point on line is visible
		double parameterOnCS = project[2].getY();
		if (parameterOnCS < 0 || parameterOnCS > 1){
			return false;
		}
		
		double d = project[0].distance(project[1]);
		if (d * getView3D().getScale() <= getGeoElement().getLineThickness() + 2){
			double z = -parameterOnHitting;
			setZPick(z, z);
			return true;
		}
		
		return false;
	}
	


}
