package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.SurfaceEvaluable;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Class for drawing a 2-var function
 * 
 * @author matthieu
 * 
 */
public class DrawSurface3D extends Drawable3DSurfaces {


	/** The function being rendered */
	SurfaceEvaluable surfaceGeo;
	
	private static final long MAX_SPLIT = 4;

	private TreeMap<CoordsIndex, Coords> mesh;

	/**
	 * common constructor
	 * 
	 * @param a_view3d
	 * @param function
	 */
	public DrawSurface3D(EuclidianView3D a_view3d, SurfaceEvaluable surface) {
		super(a_view3d, (GeoElement) surface);
		this.surfaceGeo = surface;
		this.mesh = new TreeMap<CoordsIndex, Coords>(new CompareUthenV());
		
	}


	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getSurfaceIndex());
	}
	
	@Override
	protected void drawSurfaceGeometry(Renderer renderer){
		drawGeometry(renderer);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawOutline(Renderer renderer) {
		// no outline
	}

	@Override
	protected boolean updateForItSelf() {
		
		Renderer renderer = getView3D().getRenderer();

		PlotterSurface surface = renderer.getGeometryManager().getSurface();

		
		double uMin = surfaceGeo.getMinParameter(0);
		double uMax = surfaceGeo.getMaxParameter(0);
		double vMin = surfaceGeo.getMinParameter(1);
		double vMax = surfaceGeo.getMaxParameter(1);

		Coords p1 = surfaceGeo.evaluatePoint(uMin, vMin);
		Coords p2 = surfaceGeo.evaluatePoint(uMax, vMin);
		Coords p3 = surfaceGeo.evaluatePoint(uMin, vMax);
		Coords p4 = surfaceGeo.evaluatePoint(uMax, vMax);
		
		mesh.put(new CoordsIndex(0, 0), p1);
		mesh.put(new CoordsIndex(0, 0), p1);
		mesh.put(new CoordsIndex(0, 0), p1);
		mesh.put(new CoordsIndex(0, 0), p1);
		
		
		
		
		
		surface.start();
		
		surface.drawQuadNoTexture(p1,p3,p4,p2);

		setSurfaceIndex(surface.end());

		return true;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChanged()){
			updateForItSelf();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}
	
	
	class Node {
		private Coords pointPos;
		private Node n,s,w,e;
		
		public Node(Coords p, Node n, Node s, Node w, Node e) {
			this.pointPos = p;
			this.n = n;
			this.s = s;
			this.w = w;
			this.e = e;
		}
	}
	
	
	
	class CoordsIndex {

		private long iu, iv ;
		
		public CoordsIndex(long iu, long iv){
			this.iu = iu;
			this.iv = iv;
		}
		
		
	}
	
	class CompareUthenV implements Comparator<CoordsIndex>{

		public int compare(CoordsIndex c1, CoordsIndex c2) {
			if (c1.iu>c2.iu) return 1;
			if (c1.iu<c2.iu) return -1;
			if (c1.iv>c2.iv) return 1;
			if (c1.iv<c2.iv) return -1;
			return 0;
		}
		
	}
	class CompareVthenU implements Comparator<CoordsIndex>{

		public int compare(CoordsIndex c1, CoordsIndex c2) {
			if (c1.iv>c2.iv) return 1;
			if (c1.iv<c2.iv) return -1;
			if (c1.iu>c2.iu) return 1;
			if (c1.iu<c2.iu) return -1;
			return 0;
		}
		
	}
	
}
