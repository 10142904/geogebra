package geogebra3D.euclidian3D.opengl;

import geogebra.common.awt.GPoint;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hits3D;
import geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra3D.awt.GPointWithZ;
import geogebra3D.euclidian3D.opengl.RendererJogl.GLlocal;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.glu.GLU;

/**
 * Renderer using GL2
 * 
 * @author mathieu
 * 
 */
public class RendererGL2 extends RendererD {

	// openGL variables
	protected GLU glu = new GLU();
	

	protected IntBuffer selectBuffer;

	/**
	 * Constructor
	 * 
	 * @param view
	 * @param useCanvas
	 */
	public RendererGL2(EuclidianView3D view, boolean useCanvas) {
		super(view, useCanvas);
	}
	
	@Override
	public void setClipPlanes(double[][] minMax){
		CoordMatrix mInvTranspose = view3D.getToSceneMatrixTranspose();		
		setClipPlane(0, mInvTranspose.mul( new Coords(1,0,0,-minMax[0][0])).get());
		setClipPlane(1, mInvTranspose.mul( new Coords(-1,0,0,minMax[0][1])).get());
		setClipPlane(2, mInvTranspose.mul( new Coords(0,1,0,-minMax[1][0])).get());
		setClipPlane(3, mInvTranspose.mul( new Coords(0,-1,0,minMax[1][1])).get());
		setClipPlane(4, mInvTranspose.mul( new Coords(0,0,1,-minMax[2][0])).get());
		setClipPlane(5, mInvTranspose.mul( new Coords(0,0,-1,minMax[2][1])).get());
	}

	private void setClipPlane(int n, double[] equation) {
		jogl.getGL2().glClipPlane(GL_CLIP_PLANE[n], equation, 0);
	}

	@Override
	protected void setMatrixView() {
		jogl.getGL2().glPushMatrix();
		view3D.getToScreenMatrix().get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}

	@Override
	protected void unsetMatrixView() {
		jogl.getGL2().glPopMatrix();
	}



	@Override
	public void setColor(float r, float g, float b, float a) {
		jogl.getGL2().glColor4f(r,g,b,a);
	}
	
	private double[] tmpDouble16 = new double[16];

	@Override
	public void initMatrix() {
		jogl.getGL2().glPushMatrix();
		m_drawingMatrix.get(tmpDouble16);
		jogl.getGL2().glMultMatrixd(tmpDouble16, 0);
	}

	@Override
	public void resetMatrix() {
		jogl.getGL2().glPopMatrix();
	}

	@Override
	public void drawMouseCursor() {
		// Application.debug("ici");

		initMatrix();
		disableBlending();
		jogl.getGL2().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_POINT);
		jogl.getGL2().glColor4f(0, 0, 0, 1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		jogl.getGL2().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_LINE);
		jogl.getGL2().glColor4f(0, 0, 0, 1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		jogl.getGL2().glPolygonMode(GLlocal.GL_FRONT, GLlocal.GL_FILL);
		jogl.getGL2().glColor4f(1, 1, 1, 1);
		geometryManager.draw(geometryManager.getMouseCursor().getIndex());
		enableBlending();
		resetMatrix();
	}

	private IntBuffer createSelectBufferForPicking(int bufSize) {
		// Set Up the Selection Buffer
		// Application.debug(bufSize);
		IntBuffer ret = RendererJogl.newIntBuffer(bufSize);
		jogl.getGL2().glSelectBuffer(bufSize, ret); // Tell OpenGL To Use Our
													// Array For Selection
		return ret;
	}

	@Override
	protected void setGLForPicking() {

		// The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>,
		// [3] Is <width>
		int[] viewport = new int[4];
		jogl.getGL2().glGetIntegerv(GLlocal.GL_VIEWPORT, viewport, 0);
		Dimension dim = canvas.getSize();
		// Puts OpenGL In Selection Mode. Nothing Will Be Drawn. Object ID's and
		// Extents Are Stored In The Buffer.
		jogl.getGL2().glRenderMode(GLlocal.GL_SELECT);
		jogl.getGL2().glInitNames(); // Initializes The Name Stack
		jogl.getGL2().glPushName(0); // Push 0 (At Least One Entry) Onto The
										// Stack

		jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();

		/*
		 * create MOUSE_PICK_WIDTH x MOUSE_PICK_WIDTH pixel picking region near
		 * cursor location
		 */
		double x = mouse.getX();
		double y = mouse.getY();

		// if we use an input3D, scale x & y values
		if (mouse instanceof GPointWithZ) {

			if (view3D.getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE) {
				double f = eyeToScreenDistance
						/ (eyeToScreenDistance - ((GPointWithZ) mouse).getZ());
				x = dim.width / 2 + f * (x - dim.width / 2);
				y = dim.height / 2 + f * (y - dim.height / 2);

			} else if (view3D.getProjection() == EuclidianView3D.PROJECTION_GLASSES) {
				double f = eyeToScreenDistance
						/ (eyeToScreenDistance - ((GPointWithZ) mouse).getZ() - view3D
								.getScreenZOffset());
				x = dim.width / 2 + f * (x + glassesEyeSep - glassesEyesSide - dim.width / 2)
						- glassesEyeSep + glassesEyesSide;
				y = dim.height / 2 + f * (y + glassesEyesHeight - dim.height / 2) 
						- glassesEyesHeight;

			}

		}

		glu.gluPickMatrix(x, dim.height - y, view3D.getMousePickWidth(),
				view3D.getMousePickWidth(), viewport, 0);
		setProjectionMatrixForPicking();
		jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);

		jogl.getGL2().glDisable(GLlocal.GL_ALPHA_TEST);
		jogl.getGL2().glDisable(GLlocal.GL_BLEND);
		jogl.getGL2().glDisable(GLlocal.GL_LIGHTING);
		disableTextures();

		// picking
		pickingLoop = 0;
	}

	@Override
	protected void pushSceneMatrix() {
		// set the scene matrix
		jogl.getGL2().glPushMatrix();
		view3D.getToScreenMatrix().get(tmpDouble16);
		jogl.getGL2().glLoadMatrixd(tmpDouble16, 0);
	}
	
	/**
	 * returns the depth between 0 and 2, in double format, from an integer
	 * offset lowest is depth, nearest is the object
	 * 
	 * @param ptr
	 *            the integer offset
	 * */
	private final static float getDepth(int ptr, IntBuffer selectBuffer) {

		return (float) (selectBuffer.get(ptr) & 0xffffffffL) / 0x7fffffff;
	}

	@Override
	protected void storePickingInfos(Hits3D hits3D, int pointAndCurvesLoop,
			int labelLoop) {

		int hits = jogl.getGL2().glRenderMode(GLlocal.GL_RENDER); // Switch To
																	// Render
																	// Mode,
																	// Find Out
																	// How Many

		int names, ptr = 0;
		double zFar, zNear;
		int num;

		// App.error("");

		for (int i = 0; i < hits; i++) {

			names = selectBuffer.get(ptr);
			ptr++; // min z
			zNear = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));
			ptr++; // max z
			zFar = getScreenZFromPickingDepth(getDepth(ptr, selectBuffer));

			ptr++;

			for (int j = 0; j < names; j++) {
				num = selectBuffer.get(ptr);

				if (hits3D == null) { // just update z min/max values for the
										// drawable
					drawHits[num].setZPick(zNear, zFar);
				} else { // if for hits array, some checks are done
							// App.debug("\n"+drawHits[num].getGeoElement());
					if (!(mouse instanceof GPointWithZ)
							|| intersectsMouse3D(zNear, zFar,
									((GPointWithZ) mouse).getZ())) { // check if
																		// mouse
																		// is
																		// nearer
																		// than
																		// objet
																		// (for
																		// 3D
																		// input)
						PickingType type;
						if (num >= labelLoop) {
							type = PickingType.LABEL;
						} else if (num >= pointAndCurvesLoop) {
							type = PickingType.POINT_OR_CURVE;
						} else {
							type = PickingType.SURFACE;
						}
						hits3D.addDrawable3D(drawHits[num], type, zNear, zFar);
						// App.debug("\n"+drawHits[num].getGeoElement()+"\nzFar = "+zFar+"\nmouse z ="+((GPointWithZ)
						// mouse).getZ());
					}
				}

				// Application.debug(drawHits[num]+"\nzMin="+zMin+", zMax="+zMax);
				ptr++;
			}
		}
	}

	@Override
	protected void doPick() {

		//App.debug("geoToPickSize = "+geoToPickSize);
		if (geoToPickSize != oldGeoToPickSize || needsNewPickingBuffer) {
			int bufSize = geoToPickSize * 2 + 1 + 20; // TODO remove "+20" due
														// to intersection curve
			selectBuffer = createSelectBufferForPicking(bufSize);
			drawHits = createDrawableListForPicking(bufSize);
			oldGeoToPickSize = geoToPickSize;
			needsNewPickingBuffer = false;
		}

		setGLForPicking();
		pushSceneMatrix();

		// picking surfaces
		drawable3DLists.drawForPickingSurfaces(this);

		// picking points and curves
		int pointAndCurvesLoop = pickingLoop;
		drawable3DLists.drawForPickingPointsAndCurves(this);

		// set off the scene matrix
		jogl.getGL2().glPopMatrix();

		// picking labels
		int labelLoop = pickingLoop;

		if (pickingMode == PICKING_MODE_LABELS) {
			// picking labels
			drawable3DLists.drawLabelForPicking(this);
		}

		// end picking

		// hits are stored
		// Hits3D hits3D = new Hits3D();
		Hits3D hits3D = view3D.getHits3D();
		hits3D.init();
		storePickingInfos(hits3D, pointAndCurvesLoop, labelLoop);

		// sets the GeoElements in view3D
		hits3D.sort();
		/*
		 * DEBUG / StringBuilder sbd = new StringBuilder();
		 * sbd.append("hits~~~"+hits3D.toString()); for (int i = 0;
		 * i<drawHits.length; i++) { if (drawHits[i]!=null &&
		 * drawHits[i].getGeoElement()!=null) { if
		 * (hits3D.contains(drawHits[i].getGeoElement())) { sbd.append("\n" +
		 * drawHits[i].getGeoElement().getLabel()+ "~~~ zPickMin=" +
		 * drawHits[i].zPickMin + "  zPickMax=" + drawHits[i].zPickMax);}} }
		 * Application.debug(sbd.toString()); / END DEBUG
		 */
		// view3D.setHits(hits3D);

		// App.debug(hits3D);

		waitForPick = false;

		jogl.getGL2().glEnable(GLlocal.GL_LIGHTING);
	}

	@Override
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
				.getEuclidianController()).getIntersectionCurves();

		int bufSize = curves.size();
		// IntBuffer selectBuffer=createSelectBufferForPicking(bufSize);
		// Drawable3D[] drawHits=createDrawableListForPicking(bufSize);
		if (bufSize > geoToPickSize) {
			selectBuffer = createSelectBufferForPicking(bufSize);
			drawHits = createDrawableListForPicking(bufSize);
			oldGeoToPickSize = -1;
		}

		setGLForPicking();
		pushSceneMatrix();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.setZPick(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
			pick(d, true, PickingType.POINT_OR_CURVE);
		}

		// set off the scene matrix
		jogl.getGL2().glPopMatrix();

		storePickingInfos(null, 0, 0); // 0, 0 will be ignored since hits are
										// passed as null

		jogl.getGL2().glEnable(GLlocal.GL_LIGHTING);
	}

	@Override
	public void glLoadName(int loop) {
		jogl.getGL2().glLoadName(loop);
	}

	@Override
	protected void setLightPosition(float[] values) {
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_POSITION, values, 0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_POSITION, values, 0);
	}

	@Override
	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
			float ambiant1, float diffuse1) {

		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_AMBIENT,
				new float[] { ambiant0, ambiant0, ambiant0, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT0, GLlocal.GL_DIFFUSE,
				new float[] { diffuse0, diffuse0, diffuse0, 1.0f }, 0);

		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_AMBIENT,
				new float[] { ambiant1, ambiant1, ambiant1, 1.0f }, 0);
		jogl.getGL2().glLightfv(GLlocal.GL_LIGHT1, GLlocal.GL_DIFFUSE,
				new float[] { diffuse1, diffuse1, diffuse1, 1.0f }, 0);
	}

	@Override
	protected void setLight(int light) {
		if (light == 0) {
			getGL().glDisable(GLlocal.GL_LIGHT1);
			getGL().glEnable(GLlocal.GL_LIGHT0);
		} else {
			getGL().glDisable(GLlocal.GL_LIGHT0);
			getGL().glEnable(GLlocal.GL_LIGHT1);
		}
	}

	@Override
	protected void setColorMaterial() {
		jogl.getGL2().glColorMaterial(GLlocal.GL_FRONT_AND_BACK,
				GLlocal.GL_AMBIENT_AND_DIFFUSE);
		getGL().glEnable(GLlocal.GL_COLOR_MATERIAL);
	}

	@Override
	protected void setLightModel() {
		jogl.getGL2().glShadeModel(GLlocal.GL_SMOOTH);
		jogl.getGL2().glLightModeli(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,
				GLlocal.GL_TRUE);
		jogl.getGL2().glLightModelf(GLlocal.GL_LIGHT_MODEL_TWO_SIDE,
				GLlocal.GL_TRUE);
	}

	@Override
	protected void setAlphaFunc() {
		jogl.getGL2().glAlphaFunc(GLlocal.GL_NOTEQUAL, 0);// pixels with alpha=0
															// are not drawn
		// jogl.getGL2().glAlphaFunc(GLlocal.GL_GREATER, 0.8f);//pixels with
		// alpha=0 are not drawn
	}

	@Override
	protected void setView() {
		jogl.getGL2().glViewport(0, 0, right - left, top - bottom);

		jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();

		setProjectionMatrix();

		jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);
	}

	@Override
	protected void setStencilLines() {

		// disable clip planes if used
		if (enableClipPlanes)
			disableClipPlanes();

		final int w = right - left;
		final int h = top - bottom;
		// App.debug(w+" * "+h+" = "+(w*h));

		// projection for real 2D
		jogl.getGL2().glViewport(0, 0, w, h);

		jogl.getGL2().glMatrixMode(GLlocal.GL_PROJECTION);
		jogl.getGL2().glLoadIdentity();
		glu.gluOrtho2D(0, w, h, 0);

		jogl.getGL2().glMatrixMode(GLlocal.GL_MODELVIEW);
		jogl.getGL2().glLoadIdentity();

		jogl.getGL2().glEnable(GLlocal.GL_STENCIL_TEST);

		// draw stencil pattern
		jogl.getGL2().glStencilMask(0xFF);
		jogl.getGL2().glClear(GLlocal.GL_STENCIL_BUFFER_BIT); // needs mask=0xFF

		// no multisample here to prevent ghosts
		jogl.getGL2().glDisable(GLlocal.GL_MULTISAMPLE);

		// data for stencil : one line = 0, one line = 1, etc.

		/*
		 * final int h2 = h+10;// (int) (h*1.1) ; //TODO : understand why buffer
		 * doens't match glDrawPixels dimension ByteBuffer data =
		 * newByteBuffer(w * h2); byte b = 0; for (int y=0; y<h2; y++){ b=(byte)
		 * (1-b); for (int x=0; x<w; x++){ data.put(b); } } data.rewind();
		 * 
		 * // check if we start with 0 or with 1 int y =
		 * (canvas.getLocationOnScreen().y) % 2;
		 * 
		 * gl.glRasterPos2i(0, h-y); //App.debug("== "+w+" * "+h+" = "+(w*h));
		 * gl.glDrawPixels(w, h, GLlocal.GL_STENCIL_INDEX,
		 * GLlocal.GL_UNSIGNED_BYTE, data);
		 */

		ByteBuffer data = RendererJogl.newByteBuffer(w);
		byte b = 1;
		for (int x = 0; x < w; x++) {
			data.put(b);
		}

		data.rewind();

		// check if we start with 0 or with 1
		// seems to be sensible to canvas location on screen and to parent
		// relative location
		// (try docked with neighboors / undocked or docked alone)
		int y0 = (canvas.getParent().getLocation().y
				+ canvas.getLocationOnScreen().y + 1) % 2;

		// App.debug("\nparent.y="+canvas.getParent().getLocation().y+"\ncanvas.y="+canvas.getLocation().y+"\nscreen.y="+canvas.getLocationOnScreen().y+"\nh="+h+"\ny0="+y0);
		// App.debug("== "+w+" * "+h+" = "+(w*h)+"\ny0="+y0);

		for (int y = 0; y < h / 2; y++) {
			jogl.getGL2().glRasterPos2i(0, 2 * y + y0);
			jogl.getGL2().glDrawPixels(w, 1, GLlocal.GL_STENCIL_INDEX,
					GLlocal.GL_UNSIGNED_BYTE, data);
		}

		// current mask for stencil test
		jogl.getGL2().glStencilMask(0x00);

		// back to multisample
		jogl.getGL2().glEnable(GLlocal.GL_MULTISAMPLE);

		waitForSetStencilLines = false;

		// restore clip planes
		if (enableClipPlanes)
			enableClipPlanes();

	}
	
	@Override
	final public void updateOrthoValues(){
		// nothing to do here
	}

	@Override
	protected void viewOrtho() {

		jogl.getGL2().glOrtho(getLeft(), getRight(), getBottom(), getTop(),
				-getVisibleDepth() / 2, getVisibleDepth() / 2);
	}

	@Override
	protected void viewPersp() {

		jogl.getGL2().glFrustum(perspLeft, perspRight, perspBottom, perspTop,
				perspNear, perspFar);
		jogl.getGL2().glTranslated(0, 0, perspFocus);
	}

	@Override
	protected void viewGlasses() {

		// eye separation
		double eyesep, eyesep1;
		if (eye == EYE_LEFT) {
			eyesep = -glassesEyeSep;
			eyesep1 = -glassesEyeSep1;
		} else {
			eyesep = glassesEyeSep;
			eyesep1 = glassesEyeSep1;
		}

		jogl.getGL2().glFrustum(perspLeft + eyesep1 - glassesEyesSide1, perspRight + eyesep1 - glassesEyesSide1,
				perspBottom - glassesEyesHeight1, perspTop - glassesEyesHeight1, perspNear, perspFar);
		jogl.getGL2().glTranslated(eyesep - glassesEyesSide,  -glassesEyesHeight, perspFocus);
	}

	@Override
	protected void viewOblique() {
		viewOrtho();

		jogl.getGL2().glMultMatrixd(
				new double[] { 1, 0, 0, 0, 0, 1, 0, 0, obliqueX, obliqueY, 1,
						0, 0, 0, 0, 1 }, 0);
	}

	@Override
	protected Manager createManager() {
		return new ManagerGLList(this, view3D);
	}

	@Override
	final public void enableTextures() {
		getGL().glEnable(GLlocal.GL_TEXTURE_2D);
	}

	@Override
	final public void disableTextures() {
		//bindTexture(-1);
		getGL().glDisable(GLlocal.GL_TEXTURE_2D);
		
	}
	
    @Override
	public void setLineWidth(int width){
    	jogl.getGL2().glLineWidth(width);
    }   
    
    
    
	@Override
	public void enableFading(){  
		enableTextures();
		getTextures().loadTextureLinear(Textures.FADING);
	}
	

	
	private int currentDash = Textures.DASH_INIT;


	@Override
	public void enableDash(){  
		currentDash = Textures.DASH_INIT;
		enableTextures();
	}
	
	
	@Override
	protected float[] getLightPosition(){
		return LIGHT_POSITION_D;
	}
	
	
	@Override
	public void setDashTexture(int index){
		if (currentDash == index){
			return;
		}
		
		currentDash = index;
		bindTexture(getTextures().getIndex(index));
		setTextureNearest();
	}

	@Override
	protected void drawSurfacesOutline(){
		
		jogl.getGL2().glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_LINE);
		setLineWidth(5f);
		
		setCullFaceFront();
		disableLighting();
		disableBlending();

		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);
		drawable3DLists.drawTranspClosedCurved(this); 
		if (drawable3DLists.containsClippedSurfaces()){ 
			enableClipPlanesIfNeeded();
			drawable3DLists.drawTranspClipped(this); 
			disableClipPlanesIfNeeded(); 
		}

		enableBlending();
		enableLighting();
		setCullFaceBack();
		
		jogl.getGL2().glPolygonMode(GLlocal.GL_BACK, GLlocal.GL_FILL);
		
	}
	
	

    protected static final int[] GL_CLIP_PLANE = {GLlocal.GL_CLIP_PLANE0, GLlocal.GL_CLIP_PLANE1, GLlocal.GL_CLIP_PLANE2, GLlocal.GL_CLIP_PLANE3, GLlocal.GL_CLIP_PLANE4, GLlocal.GL_CLIP_PLANE5};
    

    @Override
    protected void enableClipPlanes() {
    	for (int n = 0; n < 6; n++)
    		enableClipPlane(n);
    }
    
    @Override
	protected void disableClipPlanes() {
		for (int n = 0; n < 6; n++)
			disableClipPlane(n);
	}


    protected void enableClipPlane(int n){
    	getGL().glEnable( GL_CLIP_PLANE[n] );   	
    }

    protected void disableClipPlane(int n){
    	getGL().glDisable( GL_CLIP_PLANE[n] );   	
    }
    
    
    @Override
	public void setLabelOrigin(Coords origin){
    	// only used in shaders
    }

    
    @Override
	public void setHits(GPoint mouseLoc, int threshold){

    	// sets the flag and mouse location for openGL picking
    	setMouseLoc(mouseLoc, Renderer.PICKING_MODE_LABELS);
    	
    }
    
    @Override
	public GeoElement getLabelHit(GPoint mouseLoc){
    	return view3D.getHits3D().getLabelHit();
    }


	@Override
	public void enableLighting(){
		getGL().glEnable(GLlocal.GL_LIGHTING);
	}


	@Override
	public void disableLighting(){
		getGL().glDisable(GLlocal.GL_LIGHTING);
	}
	
	@Override
	public boolean useLogicalPicking(){
		return false;
	}


}
