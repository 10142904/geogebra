package geogebra3D.euclidian3D;




import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoClippingCube3D;




/**
 * Class for drawing 3D constant planes.
 * @author matthieu
 *
 */
public class DrawClippingCube3D extends Drawable3DCurves {
	
	/** "border extension" for clipping cube */
	private float clippingBorder;
	/** min-max values clipping cube */
	/*
	private double xmin, xmax, 
			ymin, ymax, 
			zmin, zmax;
			*/
	
	private double[][] minMax;
	
	private Coords[] vertices;
	

	

	
	//cube reduction
	static public int REDUCTION_SMALL = 0;
	static public int REDUCTION_MEDIUM = 1;
	static public int REDUCTION_LARGE = 2;
	
	static private double[] REDUCTION_VALUES = {
		(1-1./Math.sqrt(3))/2, //small
		(1-1./Math.sqrt(2))/2, //medium
		(1-1./1)/2	 //large
	};
	
	private int reduction = REDUCTION_MEDIUM;
	//private double reduction = (1-1./2.)/2;
	
	/**
	 * Common constructor
	 * @param a_view3D view
	 * @param clippingCube geo
	 */
	public DrawClippingCube3D(EuclidianView3D a_view3D, GeoClippingCube3D clippingCube){
		
		super(a_view3D, clippingCube);
		
		minMax = new double[3][];
		for (int i=0; i<3; i++)
			minMax[i] = new double[2];
		
		vertices = new Coords[8];
		for (int i=0; i<8; i++)
			vertices[i] = new Coords(0,0,0,1);
	}
	
	/*
	public double xmin(){ return minMax[0][0]; }
	public double ymin(){ return minMax[1][0]; }	
	public double zmin(){ return minMax[2][0]; }	
	public double xmax(){ return minMax[0][1]; }
	public double ymax(){ return minMax[1][1]; }	
	public double zmax(){ return minMax[2][1]; }	
	*/
	
	/**
	 * sets the reduction of the cube
	 * @param value reduction
	 */
	public void setReduction(int value){
		reduction = value;
	}
	
	/**
	 * 
	 * @return the reduction of the cube
	 */
	public int getReduction(){
		return reduction;
	}
	
	
	/**
	 * update the x,y,z min/max values
	 * @return the min/max values
	 */
	public double[][] updateMinMax(){
		
		EuclidianView3D view = getView3D(); 
		
		Renderer renderer = view.getRenderer();
		

		double scale = view.getScale();

		
		Coords origin = getView3D().getToSceneMatrix().getOrigin();
		double x0 = origin.getX(), y0 = origin.getY(), z0 = origin.getZ();
		
		double xmin = (renderer.getLeft())/scale+x0;
		double xmax = (renderer.getRight())/scale+x0;
		double ymin = (renderer.getBottom())/scale+z0;
		double ymax = (renderer.getTop())/scale+z0;
		double zmin  = (renderer.getFront(false))/scale+y0;
		double zmax = (renderer.getBack(false))/scale+y0;
		
		
		double xr = (xmax-xmin)*REDUCTION_VALUES[reduction];
		double yr = (ymax-ymin)*REDUCTION_VALUES[reduction];
		double zr = (zmax-zmin)*REDUCTION_VALUES[reduction];
		
		
		minMax[0][0] = xmin+xr;
		minMax[0][1] = xmax-xr;
		minMax[2][0] = ymin+yr;
		minMax[2][1] = ymax-yr;
		minMax[1][0] = zmin+zr;
		minMax[1][1] = zmax-zr;
		
		setVertices();
		//Application.debug(xmin+","+xmax+","+ymin+","+ymax+","+zmin+","+zmax);
		
		
		view.setXmin(minMax[0][0]);view.setXmax(minMax[0][1]);
		view.setYmin(minMax[1][0]);view.setYmax(minMax[1][1]);
		
		return minMax;
	}
	
	private void setVertices(){
		for (int x=0; x<2; x++)
			for (int y=0; y<2; y++)
				for (int z=0; z<2; z++){
					Coords vertex = vertices[x+2*y+4*z];
					vertex.setX(minMax[0][x]);
					vertex.setY(minMax[1][y]);
					vertex.setZ(minMax[2][z]);				
				}
	}
	
	/**
	 * 
	 * @param i index
	 * @return i-th vertex
	 */
	public Coords getVertex(int i){
		return vertices[i];
	}
	
	private Coords getVertexWithBorder(int x, int y, int z){
		return vertices[x+2*y+4*z].add(new Coords(clippingBorder*(1-2*x),clippingBorder*(1-2*y),clippingBorder*(1-2*z),0));
	}
	
	/*
	@Override
	protected boolean isVisible(){
		return getView3D().useClippingCube();
	}
    */

	@Override
	protected boolean updateForItSelf(){
		

		Renderer renderer = getView3D().getRenderer();
		

		//clippingBorder =  (float) (GeoElement.MAX_LINE_WIDTH*PlotterBrush.LINE3D_THICKNESS/getView3D().getScale());
				
		//geometry
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.start(8);
		clippingBorder = 
				brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture(
				0.5f,  0.25f);

		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(1, 0, 0));
		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(0, 1, 0));
		brush.segment(getVertexWithBorder(0, 0, 0),getVertexWithBorder(0, 0, 1));
		
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(0, 1, 0));
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(1, 0, 0));
		brush.segment(getVertexWithBorder(1, 1, 0),getVertexWithBorder(1, 1, 1));
		
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(0, 0, 1));
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(1, 1, 1));
		brush.segment(getVertexWithBorder(1, 0, 1),getVertexWithBorder(1, 0, 0));
		
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(1, 1, 1));
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(0, 0, 1));
		brush.segment(getVertexWithBorder(0, 1, 1),getVertexWithBorder(0, 1, 0));
		
		setGeometryIndex(brush.end());

		
		updateEquations();
		
		return true;
	}
	
	
	private void updateEquations(){
		Renderer renderer = getView3D().getRenderer();
		CoordMatrix mInvTranspose = getView3D().getToSceneMatrixTranspose();		
		renderer.setClipPlane(0, mInvTranspose.mul( new Coords(1,0,0,-minMax[0][0])).get());
		renderer.setClipPlane(1, mInvTranspose.mul( new Coords(-1,0,0,minMax[0][1])).get());
		renderer.setClipPlane(2, mInvTranspose.mul( new Coords(0,1,0,-minMax[1][0])).get());
		renderer.setClipPlane(3, mInvTranspose.mul( new Coords(0,-1,0,minMax[1][1])).get());
		renderer.setClipPlane(4, mInvTranspose.mul( new Coords(0,0,1,-minMax[2][0])).get());
		renderer.setClipPlane(5, mInvTranspose.mul( new Coords(0,0,-1,minMax[2][1])).get());

	}
	
	

	@Override
	protected void updateForView(){

	}
	

	@Override
	public void drawGeometry(Renderer renderer) {
		
		renderer.getGeometryManager().draw(getGeometryIndex());		
	}


	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
