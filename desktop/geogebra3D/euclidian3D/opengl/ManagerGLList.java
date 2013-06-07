package geogebra3D.euclidian3D.opengl;

import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import java.nio.FloatBuffer;

import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

/**
 * 
 * Manager using GL lists
 * 
 * @author ggb3D
 *
 */
public class ManagerGLList extends Manager {
	
	// GL 
	private GLUtessellator tesselator;
	

	/** common constructor
	 * @param renderer 
	 * @param view3D 3D view
	 */
	public ManagerGLList(Renderer renderer, EuclidianView3D view3D) {
		super(renderer,view3D);
	}

	
	/////////////////////////////////////////////
	// LISTS METHODS
	/////////////////////////////////////////////

	private int genLists(int nb){
		return renderer.gl.glGenLists(nb);
	}
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	@Override
	public int startNewList(){
		// generates a new list
		int ret = genLists(1);
		
		renderer.gl.glNewList(ret, GLlocal.GL_COMPILE);
		
		return ret;
	}
	
	private void newList(int index){
		renderer.gl.glNewList(index, GLlocal.GL_COMPILE);
	}	
	
	@Override
	public void endList(){	
		renderer.gl.glEndList();
	}
	
	@Override
	public void startGeometry(int type){
		renderer.gl.glBegin(type);
	}
	
	@Override
	public void endGeometry(){
		renderer.gl.glEnd();
	}
	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////
	

	
    
	@Override
	public int startPolygons(){
		
		// generates a new list
		int ret = genLists(1);
		
		//Application.debug("ret = "+ret);
		
		// if ret == 0, there's no list
		if (ret == 0)
			return 0;
		
	    RendererTesselCallBack tessCallback = new RendererTesselCallBack(renderer);
	    
	    tesselator = renderer.glu.gluNewTess();

	    renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
	    renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
	    renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
	    renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
	    renderer.glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);
    
	    newList(ret);
	    
	    return ret;
	}
    
    @Override
	public void drawPolygon(Coords n, Coords[] v){
    	
       	//starts the polygon
	    renderer.glu.gluTessBeginPolygon(tesselator, null);
	    renderer.glu.gluTessBeginContour(tesselator);
	    
	    //set normal
	    float nx = (float) n.getX();
	    float ny = (float) n.getY();
	    float nz = (float) n.getZ();
	    renderer.glu.gluTessNormal(tesselator,nx,ny,nz);
		normal(nx, ny, nz);
		
		//set texture
		texture(0,0);
		
		//set vertices
		for(int i = 0; i < v.length; i++){
			double[] point = v[i].get();
			renderer.glu.gluTessVertex(tesselator, point, 0, point);
		}
		
		//end the polygon
    	renderer.glu.gluTessEndContour(tesselator);
    	renderer.glu.gluTessEndPolygon(tesselator);
    	
    }
    
    @Override
	public void endPolygons(){
    	
	    renderer.gl.glEndList();
	    renderer.glu.gluDeleteTess(tesselator);
    }
    
    
    /** remove the polygon from gl memory
     * @param index
     */
    @Override
	public void remove(int index){
    	
    	renderer.gl.glDeleteLists(index, 1);  	
    }
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	@Override
	public void draw(int index){
		renderer.gl.glCallList(index);
	}
	
	@Override
	protected void texture(float x, float y){		
		renderer.gl.glTexCoord2f(x,y);	
	}
	
	
	@Override
	protected void normal(float x, float y, float z){
		
		renderer.gl.glNormal3f(x,y,z); 	
	}
		
	@Override
	protected void vertex(float x, float y, float z){
		
		renderer.gl.glVertex3f(x,y,z); 	
	}
	
	@Override
	protected void vertices(FloatBuffer v, int count){
		v.rewind();
		renderer.gl.glEnableClientState(GLlocal.GL_VERTEX_ARRAY);
		renderer.gl.glVertexPointer(3, GLlocal.GL_FLOAT, 0, v);
		renderer.gl.glDrawArrays(GLlocal.GL_TRIANGLES, 0, 3);
		renderer.gl.glDisableClientState(GLlocal.GL_VERTEX_ARRAY);
	}
	
	@Override
	protected void color(float r, float g, float b){
		renderer.gl.glColor3f(r,g,b);
	}
	
	@Override
	protected void color(float r, float g, float b, float a){
		renderer.gl.glColor4f(r,g,b,a);
	}
	
	@Override
	protected void lineWidth(float width){
		renderer.gl.glLineWidth(width);
	}
	
	@Override
	protected void pointSize(float size){
		renderer.gl.glPointSize(size);
	}
	
	
	


}
