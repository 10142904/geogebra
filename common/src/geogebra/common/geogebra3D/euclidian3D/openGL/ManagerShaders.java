package geogebra.common.geogebra3D.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.kernel.Matrix.Coords;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;


/**
 * 
 * Manager using shaders
 * 
 * @author ggb3D
 *
 */
public class ManagerShaders extends Manager {
	

	protected Renderer renderer;
	

	/** common constructor
	 * @param renderer 
	 * @param view3D 3D view
	 */
	public ManagerShaders(Renderer renderer, EuclidianView3D view3D) {
		super(renderer,view3D);
		
		
		
	}
	

	
	
	private ArrayList<Float> vertices, normals, textures, colors;
	
	private int verticesLength, verticesSize, normalsLength, normalsSize, texturesLength, texturesSize, colorsLength, colorsSize;
	
	@Override
	protected void initGeometriesList(){
		geometriesSetList = new TreeMap<Integer, ManagerShaders.GeometriesSet>();
		geometriesSetMaxIndex = -1;
		indicesRemoved = new Stack<Integer>();
		
		vertices = new ArrayList<Float>();
		verticesSize = 0;
		normals = new ArrayList<Float>();
		normalsSize = 0;
		textures = new ArrayList<Float>();
		texturesSize = 0;
		colors = new ArrayList<Float>();
		colorsSize = 0;
		
	}

	@Override
	protected void setRenderer(Renderer renderer){
		this.renderer = renderer;
	}
	
	@Override
	protected Renderer getRenderer(){
		return renderer;
	}

	
	/////////////////////////////////////////////
	// LISTS METHODS
	/////////////////////////////////////////////
	
	
	/**
	 * Geometry (set of vertices, normals, etc. for e.g. triangles)
	 * @author mathieu
	 *
	 */
	protected class Geometry{
		/**
		 * type of primitives
		 */
		private Type type;
		
		private GLBuffer v, n, t, c;
		
		private int length;
		
		
		/**
		 * Start a new geometry
		 * @param type of primitives
		 */
		public Geometry(Type type){
			this.type = type;
			this.v = GLFactory.prototype.newBuffer();			
			this.n = GLFactory.prototype.newBuffer();
			this.t = GLFactory.prototype.newBuffer();
			this.c = GLFactory.prototype.newBuffer();
			
			
		}
		
		/**
		 * set the geometry type and mark buffers as empty
		 * @param type geometry type
		 */
		public void setType(Type type){
			this.type = type;
			this.v.setEmpty();		
			this.n.setEmpty();
			this.t.setEmpty();
			this.c.setEmpty();
		}
		
		/**
		 * 
		 * @return type of primitives
		 */
		public Type getType(){
			return type;
		}
		

		/**
		 * set float buffer for vertices
		 * @param array float array
		 * @param length length to copy
		 */
		public void setVertices(ArrayList<Float> array, int length){
			//this.v = GLFactory.prototype.newBuffer();
			this.v.set(array, length);
		}
		
		/**
		 * 
		 * @return vertices buffer
		 */
		public GLBuffer getVertices(){
			return v;
		}
		
		/**
		 * set float buffer for normals
		 * @param array float array
		 * @param length length to copy
		 */
		public void setNormals(ArrayList<Float> array, int length){
			this.n.set(array, length);
		}
		
		/**
		 * 
		 * @return normals buffer
		 */
		public GLBuffer getNormals(){
			return n;
		}
		
		/**
		 * set float buffer for texture
		 * @param array float array
		 * @param length length to copy
		 */
		public void setTextures(ArrayList<Float> array, int length){
			this.t.set(array, length);
		}
		
		

		
		/**
		 * 
		 * @return texture buffer
		 */
		public GLBuffer getTextures(){
			return t;
		}

		
		/**
		 * set float buffer for colors
		 * @param array float array
		 * @param length length to copy
		 */
		public void setColors(ArrayList<Float> array, int length){
			this.c.set(array, length);
		}
		
		
		/**
		 * 
		 * @return colors buffer
		 */
		public GLBuffer getColors(){
			return c;
		}


		
		/**
		 * set vertices length
		 * @param l vertices length
		 */
		public void setLength(int l){
			this.length = l;
		}
		
		/**
		 * 
		 * @return vertices length
		 */
		public int getLength(){
			return length;
		}

	}
	
	/**
	 * Set of geometries
	 * @author mathieu
	 *
	 */
	protected class GeometriesSet extends ArrayList<Geometry>{
		
		private Geometry currentGeometry;
		
		private int currentGeometryIndex;
		
		private int geometriesLength;
		

		public GeometriesSet() {
			reset();
		}
		
		/**
		 * says this geometry set is reset
		 */
		public void reset(){
			currentGeometryIndex = 0;
			geometriesLength = 0;
		}
		
		/**
		 * 
		 * @return geometries length
		 */
		public int getGeometriesLength(){
			return geometriesLength;
		}
		
		/**
		 * start a new geometry
		 * @param type type of primitives
		 */
		public void startGeometry(Type type){
			if (currentGeometryIndex < size()){
				currentGeometry = get(currentGeometryIndex);
			}else{
				currentGeometry = new Geometry(type);
				add(currentGeometry);
			}
			
			currentGeometryIndex++;
			geometriesLength++;
		}
		
		/**
		 * set vertices for current geometry
		 * @param vertices vertices
		 * @param length vertices length
		 */
		public void setVertices(ArrayList<Float> vertices, int length){
			currentGeometry.setVertices(vertices, length);
			currentGeometry.setLength(length/3);
		}
		
		public void setNormals(ArrayList<Float> normals, int length){
			if (length == 3){ // only one normal for all vertices
				//currentGeometry.setNormals(ManagerShaders.floatBuffer(normals, currentGeometry.getLength()));
				currentGeometry.setNormals(normals, length);
			}else if (length == 3 * currentGeometry.getLength()){
				currentGeometry.setNormals(normals, length);
			}
		}
		
		public void setTextures(ArrayList<Float> textures, int length){
			if (length == 2 * currentGeometry.getLength()){
				currentGeometry.setTextures(textures, length);
			}
		}
		
		public void setColors(ArrayList<Float> colors, int length){
			if (length == 4 * currentGeometry.getLength()){
				currentGeometry.setColors(colors, length);
			}
		}
	
		
	}
	
	protected TreeMap<Integer, GeometriesSet> geometriesSetList;
	
	private int geometriesSetMaxIndex;
	
	protected GeometriesSet currentGeometriesSet;
	
	private Stack<Integer> indicesRemoved;
	

	private int currentOld;
	
	@Override
	public int startNewList(int old){
		
		currentOld = old;
		if (currentOld >= 0){
			currentGeometriesSet = geometriesSetList.get(old);			
		}else{
			currentGeometriesSet = null;
		}
		
		int index = currentOld;
		if (currentGeometriesSet == null){

			currentOld = -1;
			
			if (indicesRemoved.empty()){
				geometriesSetMaxIndex++;
				index = geometriesSetMaxIndex;
			}else{
				index = indicesRemoved.pop();
			}

			currentGeometriesSet = new GeometriesSet();
			geometriesSetList.put(index, currentGeometriesSet);
		}else{
			currentGeometriesSet.reset();
		}
		
		return index;
	}
	
	
	@Override
	public void endList(){	
		//renderer.getGL2().glEndList();
	}
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	
	@Override
	public void startGeometry(Type type){
		currentGeometriesSet.startGeometry(type);
		verticesLength = 0;
		normalsLength = 0;
		texturesLength = 0;
		colorsLength = 0;
	}
	
	@Override
	public void endGeometry(){
		currentGeometriesSet.setVertices(vertices, verticesLength);
		currentGeometriesSet.setNormals(normals, normalsLength);
		currentGeometriesSet.setTextures(textures, texturesLength);
		currentGeometriesSet.setColors(colors, colorsLength);
	}
	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////
	

    
	@Override
	public int startPolygons(int old){
		
    	int index = startNewList(old);
    	
	    return index;
	    
	}
    
    @Override
	public void drawPolygon(Coords n, Coords[] v){
    	
    	startGeometry(Manager.Type.TRIANGLES);
    	
    	// set normal
    	normal(n);
       	
       	for (int i = 0 ; i < 3/*v.length*/ ; i++){
      		vertex(v[i]);
       	}
       	
       	endGeometry();
    
    }
    

    
    
    @Override
	public void endPolygons(){
    	endList();
    }
    
    
 
    @Override
	public void remove(int index){
    	
    	if (index >= 0 && index != currentOld){ // negative index is for no geometry
     		indicesRemoved.push(index);
    		geometriesSetList.remove(index);
    	}
    	
    	currentOld = -1;
    }
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	@Override
	public void draw(int index){

		currentGeometriesSet = geometriesSetList.get(index);
		if (currentGeometriesSet != null){
			for (int i = 0 ; i < currentGeometriesSet.getGeometriesLength() ; i++){
				Geometry geometry = currentGeometriesSet.get(i);
				((RendererShadersInterface) renderer).loadVertexBuffer(geometry.getVertices(), geometry.getLength());
				((RendererShadersInterface) renderer).loadNormalBuffer(geometry.getNormals(), geometry.getLength());
				((RendererShadersInterface) renderer).loadColorBuffer(geometry.getColors(), geometry.getLength());
				if (((RendererShadersInterface) renderer).areTexturesEnabled()){
					((RendererShadersInterface) renderer).loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
				}
				((RendererShadersInterface) renderer).draw(geometry.getType(), geometry.getLength());
			}
		}
	}
	
	
	@Override
	protected void texture(float x, float y){		
		
		if (texturesLength == texturesSize){
			textures.add(x);
			textures.add(y);
			texturesSize += 2;
		}else{
			textures.set(texturesLength, x);
			textures.set(texturesLength + 1, y);
		}
		
		texturesLength += 2;
	}
	
	
	@Override
	protected void setDummyTexture(){
		//nothing needed for the shader
	}
	
	
	@Override
	protected void normal(float x, float y, float z){
		
		if (normalsLength == normalsSize){
			normals.add(x);
			normals.add(y);
			normals.add(z);
			normalsSize += 3;
		}else{
			normals.set(normalsLength, x);
			normals.set(normalsLength + 1, y);
			normals.set(normalsLength + 2, z);
		}
		
		normalsLength += 3;
		
	}
		
	@Override
	protected void vertex(float x, float y, float z){
		
		if (verticesLength == verticesSize){
			vertices.add(x);
			vertices.add(y);
			vertices.add(z);
			verticesSize += 3;
		}else{
			vertices.set(verticesLength, x);
			vertices.set(verticesLength + 1, y);
			vertices.set(verticesLength + 2, z);
		}
		
		verticesLength += 3;
	}
	
	@Override
	protected void vertexInt(int x, int y, int z){		
		vertex(x,y,z);
	}

	
	
	@Override
	protected void color(float r, float g, float b){
		color(r,g,b,1f);
	}
	
	@Override
	protected void color(float r, float g, float b, float a){
		
		if (colorsLength == colorsSize){
			colors.add(r);
			colors.add(g);
			colors.add(b);
			colors.add(a);
			colorsSize += 4;
		}else{
			colors.set(colorsLength, r);
			colors.set(colorsLength + 1, g);
			colors.set(colorsLength + 2, b);
			colors.set(colorsLength + 3, a);
		}
		
		colorsLength += 4;
	}
	
	
	@Override
	protected void pointSize(float size){
		//renderer.getGL2().glPointSize(size);
	}

	@Override
	protected void vertices(double[] vertices) {
		// TODO Auto-generated method stub		
	}
	
	
	@Override
	public void rectangle(int x, int y, int z, int width, int height){
		
		currentGeometriesSet = new GeometriesSet();
		startGeometry(Manager.Type.TRIANGLE_STRIP);
		texture(0, 0);
		vertexInt(x,y,z); 
		texture(1, 0);
		vertexInt(x+width,y,z); 
		texture(0, 1);
		vertexInt(x,y+height,z); 	
		texture(1, 1);
		vertexInt(x+width,y+height,z); 
		endGeometry();
		
		for (Geometry geometry : currentGeometriesSet){
			((RendererShadersInterface) renderer).loadVertexBuffer(geometry.getVertices(), geometry.getLength());
			//renderer.loadNormalBuffer(geometry.getNormals(), geometry.getLength());
			if (((RendererShadersInterface) renderer).areTexturesEnabled()){
				((RendererShadersInterface) renderer).loadTextureBuffer(geometry.getTextures(), geometry.getLength());	
			}
			((RendererShadersInterface) renderer).draw(geometry.getType(), geometry.getLength());
		}
	}

	@Override
	public void rectangleBounds(int x, int y, int z, int width, int height){
		getText().rectangleBounds(x, y, z, width, height);
	}

	



}
