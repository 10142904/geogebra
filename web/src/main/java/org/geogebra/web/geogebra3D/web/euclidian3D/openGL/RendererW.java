package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.Stack;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.MyZoomer;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLabel3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GLBufferIndices;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager.Type;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShadersNoTriangleFan;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererShadersInterface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Textures;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders.GpuBlacklist;
import org.geogebra.web.geogebra3D.web.euclidian3D.openGL.shaders.ShaderProvider;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.googlecode.gwtgl.binding.WebGLBuffer;
import com.googlecode.gwtgl.binding.WebGLProgram;
import com.googlecode.gwtgl.binding.WebGLRenderingContext;
import com.googlecode.gwtgl.binding.WebGLShader;
import com.googlecode.gwtgl.binding.WebGLTexture;
import com.googlecode.gwtgl.binding.WebGLUniformLocation;

/**
 * class for web openGL renderer
 * 
 * @author mathieu
 *
 */
public class RendererW extends Renderer implements RendererShadersInterface,
		RendererWInterface {

	protected WebGLRenderingContext glContext;

	private Canvas webGLCanvas;
	private WebGLProgram shaderProgram;
	static protected int GLSL_ATTRIB_POSITION, GLSL_ATTRIB_COLOR,
			GLSL_ATTRIB_NORMAL,
	        GLSL_ATTRIB_TEXTURE;

	private Timer loopTimer;

	// location values for shader fields
	private WebGLUniformLocation matrixLocation; // matrix
	private WebGLUniformLocation lightPositionLocation, ambiantDiffuseLocation,
			enableLightLocation, enableShineLocation; // light
	private WebGLUniformLocation eyePositionLocation; // eye position
	private WebGLUniformLocation cullingLocation; // culling
	private WebGLUniformLocation dashValuesLocation; // dash values
	private WebGLUniformLocation textureTypeLocation; // textures
	private WebGLUniformLocation colorLocation; // color
	private WebGLUniformLocation normalLocation; // one normal for all vertices
	private WebGLUniformLocation centerLocation; // center
	private WebGLUniformLocation enableClipPlanesLocation,
	        clipPlanesMinLocation, clipPlanesMaxLocation; // enable / disable
	                                                      // clip planes
	private WebGLUniformLocation labelRenderingLocation, labelOriginLocation;

	protected WebGLBuffer vboVertices, vboColors, vboNormals, vboTextureCoords,
			vboIndices;

	final static private int TEXTURE_TYPE_NONE = 0;
	final static private int TEXTURE_TYPE_FADING = 1;
	final static private int TEXTURE_TYPE_TEXT = 2;
	final static private int TEXTURE_TYPE_DASH = 4;

	/**
	 * constructor
	 * 
	 * @param view
	 *            3D view
	 */
	public RendererW(EuclidianView3DW view) {
		super(view, RendererType.SHADER);

		hitting = new Hitting(view3D);

		webGLCanvas = Canvas.createIfSupported();

		createGLContext(false);

	}

	@Override
	public Canvas getCanvas() {
		return webGLCanvas;
	}

	/**
	 * create the webGL context
	 */
	protected void createGLContext(boolean preserveDrawingBuffer) {
		if (preserveDrawingBuffer) {
			glContext = getBufferedContext(webGLCanvas.getElement());

		} else {
		glContext = (WebGLRenderingContext) webGLCanvas
		        .getContext("experimental-webgl");
		}
		if (glContext == null) {
			Window.alert("Sorry, Your Browser doesn't support WebGL!");
		}

	}

	private static native WebGLRenderingContext getBufferedContext(
	        Element element) /*-{
		return element.getContext("experimental-webgl", {
			preserveDrawingBuffer : true
		});
	}-*/;

	private double ratio = 1;

	public void setPixelRatio(double ratio) {
		this.ratio = ratio;
	}
	@Override
	public void setView(int x, int y, int w, int h) {
		if (glContext == null || webGLCanvas == null) {
			return;
		}
		webGLCanvas.setCoordinateSpaceWidth((int) (w * ratio));
		webGLCanvas.setCoordinateSpaceHeight((int) (h * ratio));
		glContext.viewport(0, 0, (int) (w * ratio), (int) (h * ratio));
		webGLCanvas.setHeight(h + "px");
		webGLCanvas.setWidth(w + "px");
		super.setView(x, y, w, h);

		start();

	}

	/**
	 * set GL view port width and height
	 * 
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	/*
	 * protected void setGLViewPort(int w, int h) { glContext.viewport(0, 0, w,
	 * h); }
	 */

	/**
	 * 
	 * @return openGL canvas
	 */
	public Canvas getGLCanvas() {
		return webGLCanvas;
	}

	protected void start() {

		((EuclidianView3DW) view3D).setReadyToRender();

		// use loop timer for e.g. automatic rotation
		loopTimer = new Timer() {
			@Override
			public void run() {
				if (view3D.isAnimated()) {
					view3D.repaintView();
				}
			}
		};
		loopTimer.scheduleRepeating(MyZoomer.DELAY);

	}

	/**
	 * init shaders
	 */
	@Override
	public void initShaders() {
		boolean needsSmallFragmentShader = GpuBlacklist
				.isCurrentGpuBlacklisted(glContext);
		boolean shiny = view3D.getApplication().has(Feature.SHINY_3D);
		WebGLShader fragmentShader = getShader(
		        WebGLRenderingContext.FRAGMENT_SHADER,
				ShaderProvider.getFragmentShader(needsSmallFragmentShader,
						shiny));
		WebGLShader vertexShader = getShader(
		        WebGLRenderingContext.VERTEX_SHADER,
				ShaderProvider.getVertexShader(needsSmallFragmentShader, shiny));

		// create shader program
		shaderProgram = glContext.createProgram();

		// attach shaders
		glContext.attachShader(shaderProgram, vertexShader);
		glContext.attachShader(shaderProgram, fragmentShader);

		// bind attributes location
		glContext.bindAttribLocation(shaderProgram, 0, "attribute_Position");
		glContext.bindAttribLocation(shaderProgram, 1, "attribute_Normal");
		glContext.bindAttribLocation(shaderProgram, 2, "attribute_Color");
		glContext.bindAttribLocation(shaderProgram, 3, "attribute_Texture");

		// link the program
		glContext.linkProgram(shaderProgram);

		if (!glContext.getProgramParameterb(shaderProgram,
		        WebGLRenderingContext.LINK_STATUS)) {
			throw new RuntimeException("Could not initialise shaders");
		}

		// use the program
		glContext.useProgram(shaderProgram);

		// attributes : note that vertex shader must use it, otherwise
		// getAttribLocation will return -1 (undefined)
		GLSL_ATTRIB_POSITION = glContext.getAttribLocation(shaderProgram,
		        "attribute_Position");
		GLSL_ATTRIB_NORMAL = glContext.getAttribLocation(shaderProgram,
		        "attribute_Normal");
		GLSL_ATTRIB_COLOR = glContext.getAttribLocation(shaderProgram,
		        "attribute_Color");
		GLSL_ATTRIB_TEXTURE = glContext.getAttribLocation(shaderProgram,
		        "attribute_Texture");

		Log.debug("vertexPositionAttribute=" + GLSL_ATTRIB_POSITION + ","
		        + "normalAttribute=" + GLSL_ATTRIB_NORMAL + ","
				+ "colorAttribute=" + GLSL_ATTRIB_COLOR + ","
		        + "textureAttribute=" + GLSL_ATTRIB_TEXTURE);

		// uniform location
		matrixLocation = glContext.getUniformLocation(shaderProgram, "matrix");

		lightPositionLocation = glContext.getUniformLocation(shaderProgram,
		        "lightPosition");
		ambiantDiffuseLocation = glContext.getUniformLocation(shaderProgram,
		        "ambiantDiffuse");
		eyePositionLocation = glContext.getUniformLocation(shaderProgram,
		        "eyePosition");
		enableLightLocation = glContext.getUniformLocation(shaderProgram,
		        "enableLight");
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			enableShineLocation = glContext.getUniformLocation(shaderProgram,
					"enableShine");
		}

		cullingLocation = glContext
		        .getUniformLocation(shaderProgram, "culling");

		dashValuesLocation = glContext.getUniformLocation(shaderProgram,
		        "dashValues");

		// texture
		textureTypeLocation = glContext.getUniformLocation(shaderProgram,
		        "textureType");

		// color
		colorLocation = glContext.getUniformLocation(shaderProgram, "color");

		// normal
		normalLocation = glContext.getUniformLocation(shaderProgram, "normal");

		// center
		centerLocation = glContext.getUniformLocation(shaderProgram, "center");

		// clip planes
		enableClipPlanesLocation = glContext.getUniformLocation(shaderProgram,
		        "enableClipPlanes");
		clipPlanesMinLocation = glContext.getUniformLocation(shaderProgram,
		        "clipPlanesMin");
		clipPlanesMaxLocation = glContext.getUniformLocation(shaderProgram,
		        "clipPlanesMax");

		// label rendering
		labelRenderingLocation = glContext.getUniformLocation(shaderProgram,
		        "labelRendering");
		labelOriginLocation = glContext.getUniformLocation(shaderProgram,
		        "labelOrigin");

		// VBOs
		vboColors = glContext.createBuffer();
		vboVertices = glContext.createBuffer();
		vboNormals = glContext.createBuffer();
		vboTextureCoords = glContext.createBuffer();
		vboIndices = glContext.createBuffer();
		
		attribPointers();

	}

	private WebGLShader getShader(int type, String source) {
		WebGLShader shader = glContext.createShader(type);

		glContext.shaderSource(shader, source);
		glContext.compileShader(shader);

		if (!glContext.getShaderParameterb(shader,
		        WebGLRenderingContext.COMPILE_STATUS)) {
			Log.debug("ERROR COMPILING SHADER: "
			        + glContext.getShaderInfoLog(shader));
			throw new RuntimeException(glContext.getShaderInfoLog(shader));
		}

		return shader;
	}

	protected final void setModelViewIdentity() {
		projectionMatrix.getForGL(tmpFloat16);
		glContext.uniformMatrix4fv(matrixLocation, false, tmpFloat16);
	}

	@Override
	public void drawScene() {

		super.drawScene();

		// clear alpha channel to 1.0 to avoid transparency to html background
		setColorMask(false, false, false, true);
		clearColorBuffer();
		setColorMask(true, true, true, true);

	}

	@Override
	protected void draw() {

		resetOneNormalForAllVertices();
		disableTextures();

		setModelViewIdentity();

		super.draw();

		/*
		 * 
		 * ///////////////////////////////////
		 * 
		 * // labels //drawFaceToScreen();
		 * 
		 * // init drawing matrix to view3D toScreen matrix setMatrixView();
		 * 
		 * setLightPosition(); setLight(0);
		 * 
		 * 
		 * // drawing the cursor enableLighting(); disableAlphaTest();
		 * enableCulling(); //view3D.drawCursor(this);
		 * 
		 * 
		 * // drawing hidden part enableAlphaTest(); disableTextures();
		 * //drawable3DLists.drawHiddenNotTextured(this); enableDash();
		 * //drawable3DLists.drawHiddenTextured(this); enableFading(); // from
		 * RendererShaders -- check when enable textures if already done
		 * //drawNotTransp(); disableTextures(); disableAlphaTest();
		 * 
		 * 
		 * // drawing transparents parts disableDepthMask(); enableFading();
		 * enableBlending(); drawTransp(); enableDepthMask();
		 * 
		 * // drawing labels disableTextures(); enableCulling();
		 * disableBlending();
		 * 
		 * // drawing hiding parts setColorMask(false, false, false, false); //
		 * no writing in color buffer
		 * 
		 * setCullFaceFront(); // draws inside parts
		 * 
		 * // drawable3DLists.drawClosedSurfacesForHiding(this); // closed
		 * surfaces // // back-faces // if
		 * (drawable3DLists.containsClippedSurfaces()) { //
		 * enableClipPlanesIfNeeded(); //
		 * drawable3DLists.drawClippedSurfacesForHiding(this); // clipped // //
		 * surfaces // // back-faces // disableClipPlanesIfNeeded(); // }
		 * 
		 * disableCulling(); //drawable3DLists.drawSurfacesForHiding(this); //
		 * non closed surfaces
		 * 
		 * // getGL().glColorMask(true,true,true,true); setColorMask();
		 * 
		 * // re-drawing transparents parts for better transparent effect //
		 * TODO improve it ! enableFading(); disableDepthMask();
		 * enableBlending(); //drawTransp(); enableDepthMask();
		 * disableTextures();
		 * 
		 * // drawing hiding parts setColorMask(false, false, false, false); //
		 * no writing in color buffer disableBlending(); enableCulling();
		 * setCullFaceBack(); // draws inside parts
		 * 
		 * // drawable3DLists.drawClosedSurfacesForHiding(this); // closed
		 * surfaces // // front-faces // if
		 * (drawable3DLists.containsClippedSurfaces()) { //
		 * enableClipPlanesIfNeeded(); //
		 * drawable3DLists.drawClippedSurfacesForHiding(this); // clipped // //
		 * surfaces // // back-faces // disableClipPlanesIfNeeded(); // }
		 * 
		 * setColorMask();
		 * 
		 * // re-drawing transparents parts for better transparent effect //
		 * TODO improve it ! enableTextures(); disableDepthMask();
		 * enableBlending(); //drawTransp(); enableDepthMask();
		 * 
		 * // drawing not hidden parts disableTextures(); // added from
		 * RendererShaders enableCulling(); //drawable3DLists.draw(this);
		 * 
		 * // primitives.disableVBO(gl);
		 * 
		 * // FPS disableLighting(); disableDepthTest();
		 * 
		 * // drawWireFrame();
		 * 
		 * unsetMatrixView();
		 * 
		 * // drawFPS();
		 * 
		 * enableDepthTest(); enableLighting();
		 */
	}

	/*
	 * protected void drawTransp() {
	 * 
	 * setLight(1);
	 * 
	 * getTextures().loadTextureLinear(Textures.FADING);
	 * 
	 * disableCulling(); drawable3DLists.drawTransp(this);
	 * //drawable3DLists.drawTranspClosedNotCurved(this);
	 * 
	 * // TODO fix it // getGL().glDisable(GLlocal.GL_TEXTURE_2D); // TODO
	 * improve this !
	 * 
	 * enableCulling(); setCullFaceFront();
	 * 
	 * // drawable3DLists.drawTranspClosedCurved(this);// draws inside parts //
	 * if (drawable3DLists.containsClippedSurfaces()) { //
	 * enableClipPlanesIfNeeded(); // drawable3DLists.drawTranspClipped(this);
	 * // clipped surfaces // // back-faces // disableClipPlanesIfNeeded(); // }
	 * 
	 * setCullFaceBack();
	 * 
	 * // drawable3DLists.drawTranspClosedCurved(this);// draws outside parts //
	 * if (drawable3DLists.containsClippedSurfaces()) { //
	 * enableClipPlanesIfNeeded(); // drawable3DLists.drawTranspClipped(this);
	 * // clipped surfaces // // back-faces // disableClipPlanesIfNeeded(); // }
	 * 
	 * 
	 * setLight(0);
	 * 
	 * }
	 */

	/*
	 * @Override protected void draw() {
	 * 
	 * 
	 * setMatrixView(); setLightPosition(); setLight(0);
	 * 
	 * 
	 * 
	 * 
	 * 
	 * float[] vertices = { 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f };
	 * 
	 * float[] normals = { 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f };
	 * 
	 * setColor(1f, 0f, 0f, 1f); drawTriangle(vertices, normals, null);
	 * 
	 * vertices = new float[] { 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
	 * 
	 * normals = new float[] { 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f };
	 * 
	 * setColor(0f, 1f, 0f, 1f); drawTriangle(vertices, normals, null);
	 * 
	 * vertices = new float[] { 0f, 0f, 0f, 0f, 0f, 1f, 1f, 0f, 0f };
	 * 
	 * normals = new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f };
	 * 
	 * setColor(0f, 0f, 1f, 1f); drawTriangle(vertices, normals, null);
	 * 
	 * }
	 */

	@Override
	protected void clearColorBuffer() {
		glContext.clear(WebGLRenderingContext.COLOR_BUFFER_BIT);
	}

	@Override
	protected void clearDepthBuffer() {
		glContext.clear(WebGLRenderingContext.DEPTH_BUFFER_BIT);
	}

	@Override
	protected Manager createManager() {
		// if (Browser.supportsWebGLTriangleFan()){ // no TRIANGLE_FAN in
		// internet explorer
		// Log.debug("================= supportsWebGLTriangleFan");
		// return new ManagerShaders(this, view3D);
		// }
		// Log.debug("================= doens't supportsWebGLTriangleFan");
		// return new ManagerShadersNoTriangleFan(this, view3D);

		// wait for fix : detect webGL support correctly

		return new ManagerShadersNoTriangleFan(this, view3D);
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	private int cullingType = 1;

	@Override
	public void enableCulling() {
		glContext.enable(WebGLRenderingContext.CULL_FACE);
	}

	@Override
	public void disableCulling() {
		glContext.disable(WebGLRenderingContext.CULL_FACE);
		glContext.uniform1i(cullingLocation, 1);
	}

	@Override
	public void setCullFaceFront() {
		glContext.cullFace(WebGLRenderingContext.FRONT);
		glContext.uniform1i(cullingLocation, -1);
	}

	@Override
	public void setCullFaceBack() {
		glContext.cullFace(WebGLRenderingContext.BACK);
		glContext.uniform1i(cullingLocation, 1);
	}

	@Override
	public void disableBlending() {
		glContext.disable(WebGLRenderingContext.BLEND);

	}

	@Override
	public void enableBlending() {
		glContext.enable(WebGLRenderingContext.BLEND);

	}

	@Override
	public void enableMultisample() {
		// glContext.enable(WebGLRenderingContext.SAMPLE_COVERAGE);

	}

	@Override
	public void disableMultisample() {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableAlphaTest() {
		// done by shader
	}

	@Override
	public void disableAlphaTest() {
		// done by shader
	}

	@Override
	protected void setMatrixView() {
		tmpMatrix1.setMul(projectionMatrix, view3D.getToScreenMatrix());
		tmpMatrix1.getForGL(tmpFloat16);
		glContext.uniformMatrix4fv(matrixLocation, false, tmpFloat16);
	}

	@Override
	protected void unsetMatrixView() {
		setModelViewIdentity();
	}

	@Override
	public void enableDepthMask() {
		glContext.depthMask(true);

	}

	@Override
	public void disableDepthMask() {
		glContext.depthMask(false);

	}

	@Override
	public void enableDepthTest() {
		glContext.enable(WebGLRenderingContext.DEPTH_TEST);

	}

	@Override
	public void disableDepthTest() {
		glContext.disable(WebGLRenderingContext.DEPTH_TEST);

	}

	@Override
	public void setColorMask(boolean r, boolean g, boolean b, boolean a) {

		glContext.colorMask(r, g, b, a);

	}

	@Override
	public void setLineWidth(double width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		glContext.uniform4f(colorLocation, r, g, b, a);
	}

	@Override
	public void setLayer(float l) {

		glContext.polygonOffset(-l * 0.05f, -l * 10);

	}

	private float[] tmpFloat16 = new float[16];

	@Override
	public void initMatrix() {
		tmpMatrix1.setMul(projectionMatrix,
		        tmpMatrix2.setMul(view3D.getToScreenMatrix(), getMatrix()));
		tmpMatrix1.getForGL(tmpFloat16);
		glContext.uniformMatrix4fv(matrixLocation, false, tmpFloat16);
	}

	@Override
	public void initMatrixForFaceToScreen() {

		tmpMatrix1.setMul(projectionMatrix, getMatrix());
		tmpMatrix1.getForGL(tmpFloat16);

		glContext.uniformMatrix4fv(matrixLocation, false, tmpFloat16);
	}



	@Override
	public void resetMatrix() {
		setMatrixView();
	}


	@Override
	protected void setGLForPicking() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void pushSceneMatrix() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doPick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pickIntersectionCurves() {

		ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
		        .getEuclidianController()).getIntersectionCurves();

		// picking objects
		for (IntersectionCurve intersectionCurve : curves) {
			Drawable3D d = intersectionCurve.drawable;
			d.updateForHitting(); // we may need an update
			if (!d.hit(hitting)
			        || d.getPickingType() != PickingType.POINT_OR_CURVE) { // we
				                                                           // assume
				                                                           // that
				                                                           // hitting
				                                                           // infos
				                                                           // are
				                                                           // updated
				                                                           // from
				                                                           // last
				                                                           // mouse
				                                                           // move
				d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
			}

		}

	}

	@Override
	public void glLoadName(int loop) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setLightPosition(float[] values) {
		glContext.uniform3fv(lightPositionLocation, values);
		if (view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE
		        || view3D.getMode() == EuclidianView3D.PROJECTION_PERSPECTIVE) {
			glContext.uniform4fv(eyePositionLocation, view3D.getViewDirection()
			        .get4ForGL());
		} else {
			glContext.uniform4fv(eyePositionLocation, view3D.getEyePosition()
			        .get4ForGL());
		}
	}

	private float[][] ambiantDiffuse;

	@Override
	protected void setLightAmbiantDiffuse(float ambiant0, float diffuse0,
	        float ambiant1, float diffuse1) {

		float coeff = 1.414f;

		float a0 = ambiant0 * coeff;
		float d0 = 1 - a0;
		float a1 = ambiant1 * coeff;
		float d1 = 1 - a1;

		ambiantDiffuse = new float[][] { { a0, d0 }, { a1, d1 } };

		// ambiantDiffuse = new float[][] {
		// {ambiant0, diffuse0},
		// {ambiant1, diffuse1}
		// };

	}

	@Override
	protected void setLight(int light) {

		glContext.uniform2fv(ambiantDiffuseLocation, ambiantDiffuse[light]);
	}

	@Override
	public void setClearColor(float r, float g, float b, float a) {
		glContext.clearColor(r, g, b, a);
	}

	@Override
	protected void setColorMaterial() {
		// glContext.enable(WebGLRenderingContext.COLOR_MATERIAL);
	}

	@Override
	protected void setLightModel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAlphaFunc() {
		// done by shader

	}

	@Override
	public void resumeAnimator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setView() {

		setProjectionMatrix();

	}


	private CoordMatrix4x4 projectionMatrix = new CoordMatrix4x4();
	private CoordMatrix4x4 tmpMatrix1 = new CoordMatrix4x4(),
	        tmpMatrix2 = new CoordMatrix4x4();

	@Override
	protected void disableStencilLines() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setStencilLines() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void viewOrtho() {
		// the projection matrix is updated in updateOrthoValues()
	}

	@Override
	final public void updateOrthoValues() {

		projectionMatrix.set(1, 1, 2.0 / getWidth());
		projectionMatrix.set(2, 2, 2.0 / getHeight());
		projectionMatrix.set(3, 3, -2.0 / getVisibleDepth());
		projectionMatrix.set(4, 4, 1);

		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		projectionMatrix.set(1, 3, 0);
		projectionMatrix.set(2, 3, 0);
		projectionMatrix.set(4, 3, 0);

		projectionMatrix.set(1, 4, 0);
		projectionMatrix.set(2, 4, 0);
		projectionMatrix.set(3, 4, 0);

	}

	@Override
	protected void viewPersp() {
		// the projection matrix is updated in updatePerspValues()

	}

	@Override
	protected void updatePerspValues() {

		super.updatePerspValues();

		projectionMatrix.set(1, 1, 2 * perspNear[eye] / (perspRight[eye] - perspLeft[eye]));
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(2, 2, 2 * perspNear[eye] / (perspTop[eye] - perspBottom[eye]));
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		perspXZ = (perspRight[eye] + perspLeft[eye]) / (perspRight[eye] - perspLeft[eye]);

		projectionMatrix.set(1, 3, perspXZ);
		projectionMatrix.set(2, 3, (perspTop[eye] + perspBottom[eye])
				/ (perspTop[eye] - perspBottom[eye]));
		projectionMatrix.set(3, 3, 2 * perspFocus[eye] / getVisibleDepth());
		projectionMatrix.set(4, 3, -1);

		projectionMatrix.set(1, 4, 0);// (perspRight+perspLeft)/(perspRight-perspLeft)
										// * perspFocus);
		projectionMatrix.set(2, 4, 0);// (perspTop+perspBottom)/(perspTop-perspBottom)
										// * perspFocus);
		projectionMatrix.set(3, 4, getVisibleDepth() / 2);
		projectionMatrix.set(4, 4, -perspFocus[eye]);

	}

	private double perspXZ, glassesXZ;

	@Override
	public void updateGlassesValues() {
		super.updateGlassesValues();
		glassesXZ = (perspNear[eye] * (glassesEyeX[EYE_LEFT] - glassesEyeX[EYE_RIGHT]) / perspFocus[eye])
				/ (perspRight[eye] - perspLeft[eye]);
	}

	@Override
	protected void viewGlasses() {

		if (eye == EYE_LEFT) {
			projectionMatrix.set(1, 3, perspXZ + glassesXZ);
		} else {
			projectionMatrix.set(1, 3, perspXZ - glassesXZ);
		}

	}

	@Override
	protected void viewOblique() {
		// the projection matrix is updated in updateProjectionObliqueValues()
	}

	@Override
	public void updateProjectionObliqueValues() {
		super.updateProjectionObliqueValues();

		projectionMatrix.set(1, 1, 2.0 / getWidth());
		projectionMatrix.set(2, 1, 0);
		projectionMatrix.set(3, 1, 0);
		projectionMatrix.set(4, 1, 0);

		projectionMatrix.set(1, 2, 0);
		projectionMatrix.set(2, 2, 2.0 / getHeight());
		projectionMatrix.set(3, 2, 0);
		projectionMatrix.set(4, 2, 0);

		projectionMatrix.set(1, 3, obliqueX * 2.0 / getWidth());
		projectionMatrix.set(2, 3, obliqueY * 2.0 / getHeight());
		projectionMatrix.set(3, 3, -2.0 / getVisibleDepth());
		projectionMatrix.set(4, 3, 0);

		projectionMatrix.set(1, 4, 0);
		projectionMatrix.set(2, 4, 0);
		projectionMatrix.set(3, 4, 0);
		projectionMatrix.set(4, 4, 1);
	}

	@Override
	public void enableTextures2D() {
		// glContext.enable(WebGLRenderingContext.TEXTURE_2D);

	}

	@Override
	public void disableTextures2D() {
		// glContext.disable(WebGLRenderingContext.TEXTURE_2D);

	}

	@Override
	public void genTextures2D(int number, int[] index) {

		int size = texturesArray.size();
		for (int i = 0; i < number; i++) { // add new textures
			index[i] = size + i;
			texturesArray.add(glContext.createTexture());
		}

	}

	private ArrayList<WebGLTexture> texturesArray = new ArrayList<WebGLTexture>();

	@Override
	public GBufferedImage createBufferedImage(DrawLabel3D label) {

		// update width and height
		label.setDimensionPowerOfTwo(
		        firstPowerOfTwoGreaterThan(label.getWidth()),
		        firstPowerOfTwoGreaterThan(label.getHeight()));

		// create and return a buffered image with power-of-two dimensions
		return new GBufferedImageW(label.getWidthPowerOfTwo(),
				label.getHeightPowerOfTwo(), 1);
	}

	@Override
	public void createAlphaTexture(DrawLabel3D label, GBufferedImage bimg) {

		// values for picking (ignore transparent bytes)
		label.setPickingDimension(0, 0, label.getWidth(), label.getHeight());

		// check if image is ready
		ImageElement image = ((GBufferedImageW) bimg).getImageElement();
		if (!image.getPropertyBoolean("complete")) {
			ImageWrapper.nativeon(image, "load", new AlphaTextureCreator(label,
			        image, (GBufferedImageW) bimg, this));
		} else {
			createAlphaTexture(label, image, (GBufferedImageW) bimg);
		}

	}

	private class AlphaTextureCreator implements ImageLoadCallback {

		private DrawLabel3D label;
		private ImageElement image;
		private GBufferedImageW bimg;
		private RendererW renderer;

		public AlphaTextureCreator(DrawLabel3D label, ImageElement image,
		        GBufferedImageW bimg, RendererW renderer) {
			this.label = label;
			this.image = image;
			this.bimg = bimg;
			this.renderer = renderer;
		}

		@Override
		public void onLoad() {

			// image ready : create the texture
			renderer.createAlphaTexture(label, image, bimg);

			// repaint the view
			renderer.getView().repaintView();
		}
	}


	/**
	 * create alpha texture from image for the label
	 * 
	 * @param label
	 *            label
	 * @param image
	 *            image
	 */
	protected void createAlphaTexture(DrawLabel3D label, ImageElement image,
	        GBufferedImageW bimg) {

		if (label.isPickable()) {
			// values for picking (ignore transparent bytes)
			ImageData data = bimg.getImageData();
			int xmin = label.getWidth(), xmax = 0, ymin = label.getHeight(), ymax = 0;
			for (int y = 0; y < label.getHeight(); y++) {
				for (int x = 0; x < label.getWidth(); x++) {
					int alpha = data.getAlphaAt(x, y);
					if (alpha != 0) {
						if (x < xmin) {
							xmin = x;
						}
						if (x > xmax) {
							xmax = x;
						}
						if (y < ymin) {
							ymin = y;
						}
						if (y > ymax) {
							ymax = y;
						}
					}
				}
			}
			label.setPickingDimension(xmin, ymin, xmax - xmin + 1, ymax - ymin
			        + 1);
		}

		// create texture
		WebGLTexture texture;

		int textureIndex = label.getTextureIndex();

		if (textureIndex == -1) {
			textureIndex = texturesArray.size();
			texture = glContext.createTexture();
			texturesArray.add(texture);
		} else {
			texture = texturesArray.get(textureIndex);
		}

		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, texture);

		glContext.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
		        WebGLRenderingContext.RGBA, WebGLRenderingContext.RGBA,
		        WebGLRenderingContext.UNSIGNED_BYTE, image);

		glContext.generateMipmap(WebGLRenderingContext.TEXTURE_2D);

		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D, null);

		label.setTextureIndex(textureIndex);
	}

	@Override
	public void bindTexture(int index) {
		glContext.bindTexture(WebGLRenderingContext.TEXTURE_2D,
				texturesArray.get(index));
	}

	@Override
	public void textureImage2D(int sizeX, int sizeY, byte[] buf) {
		// no need for now (dash and fading are made by the shader)
	}

	@Override
	public void setTextureLinear() {

		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_MAG_FILTER,
		        WebGLRenderingContext.LINEAR);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_MIN_FILTER,
		        WebGLRenderingContext.LINEAR);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_WRAP_S,
		        WebGLRenderingContext.CLAMP_TO_EDGE); // prevent repeating the
		                                              // texture
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_WRAP_T,
		        WebGLRenderingContext.CLAMP_TO_EDGE); // prevent repeating the
		                                              // texture

	}

	@Override
	public void setTextureNearest() {

		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_MAG_FILTER,
		        WebGLRenderingContext.NEAREST);
		glContext.texParameteri(WebGLRenderingContext.TEXTURE_2D,
		        WebGLRenderingContext.TEXTURE_MIN_FILTER,
		        WebGLRenderingContext.NEAREST);

	}

	@Override
	protected void setStencilFunc(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void exportImage() {
		// TODO Auto-generated method stub

	}

	private void createBuffer(GPUBuffer buffer, Stack<WebGLBuffer> stack) {
		if (stack.isEmpty()) {
			((GPUBufferW) buffer).set(glContext.createBuffer());
		} else {
			((GPUBufferW) buffer).set(stack.pop());
		}

	}

	@Override
	public void createArrayBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedBuffers);
	}

	@Override
	public void createElementBuffer(GPUBuffer buffer) {
		createBuffer(buffer, removedElementBuffers);
	}

	private Stack<WebGLBuffer> removedBuffers = new Stack<WebGLBuffer>();

	private Stack<WebGLBuffer> removedElementBuffers = new Stack<WebGLBuffer>();

	private static void removeBuffer(GPUBuffer buffer, Stack<WebGLBuffer> stack) {
		stack.push(((GPUBufferW) buffer).get());
	}

	@Override
	public void removeArrayBuffer(GPUBuffer buffer) {
		removeBuffer(buffer, removedBuffers);
	}

	@Override
	public void removeElementBuffer(GPUBuffer buffer) {
		removeBuffer(buffer, removedElementBuffers);
	}

	@Override
	public void storeBuffer(GLBuffer fb, int length, int size,
			GPUBuffer buffer, int attrib) {
		// Select the VBO, GPU memory data
		bindBuffer(buffer);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(fb);

	}

	@Override
	public void storeElementBuffer(short[] fb, int length, GPUBuffer buffers) {
		// Select the VBO, GPU memory data
		bindBufferForIndices(buffers);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glContext.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
				MyInt16Array.create(fb), GL_TYPE_DRAW_TO_BUFFER);

	}

	@Override
	public void bindBufferForIndices(GPUBuffer buffer) {
		// Select the VBO, GPU memory data
		glContext.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
				((GPUBufferW) buffer).get());
	}

	final private void bindBuffer(GPUBuffer buffer) {
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
				((GPUBufferW) buffer).get());
	}

	@Override
	public void bindBufferForVertices(GPUBuffer buffer, int size) {
		// Select the VBO, GPU memory data
		bindBuffer(buffer);
		// Associate Vertex attribute 0 with the last bound VBO
		glContext.vertexAttribPointer(GLSL_ATTRIB_POSITION, size,
				WebGLRenderingContext.FLOAT, false, 0, 0);

		// enable VBO
		glContext.enableVertexAttribArray(GLSL_ATTRIB_POSITION);
	}

	@Override
	public void bindBufferForColors(GPUBuffer buffer, int size,
			GLBuffer fbColors) {
		if (fbColors == null || fbColors.isEmpty()) {
			glContext.disableVertexAttribArray(GLSL_ATTRIB_COLOR);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		glContext.vertexAttribPointer(
						GLSL_ATTRIB_COLOR/* the color attribute */, 4 /*
																	 * 4 color
																	 * values
																	 * used for
																	 * each
																	 * vertex
																	 */,
				WebGLRenderingContext.FLOAT, false /* normalized? */,
						0 /* stride */, 0 /* The bound VBO data offset */);

		glContext.enableVertexAttribArray(GLSL_ATTRIB_COLOR);
	}

	@Override
	public void bindBufferForNormals(GPUBuffer buffer, int size,
			GLBuffer fbNormals) {
		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			glContext.disableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			glContext.disableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			fbNormals.array(tmpNormal3);
			glContext.uniform3fv(normalLocation, tmpNormal3);
			oneNormalForAllVertices = true;
			return;
		}

		// ///////////////////////////////////
		// VBO - normals

		if (oneNormalForAllVertices) {
			resetOneNormalForAllVertices();
		}

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		glContext.vertexAttribPointer(
						GLSL_ATTRIB_NORMAL /* the vertex attribute */, 3 /*
																		 * 3
																		 * normal
																		 * values
																		 * used
																		 * for
																		 * each
																		 * vertex
																		 */,
				WebGLRenderingContext.FLOAT, false /* normalized? */,
						0 /* stride */, 0 /* The bound VBO data offset */);

		glContext.enableVertexAttribArray(GLSL_ATTRIB_NORMAL);
	}

	@Override
	public void bindBufferForTextures(GPUBuffer buffer, int size,
			GLBuffer fbTextures) {
		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			glContext.disableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
			return;
		}

		setCurrentGeometryHasTexture();

		// Select the VBO, GPU memory data, to use for normals
		bindBuffer(buffer);

		// Associate Vertex attribute 1 with the last bound VBO
		glContext.vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2 /*
															 * 2 texture values
															 * used for each
															 * vertex
															 */,
				WebGLRenderingContext.FLOAT,
				false /* normalized? */, 0 /* stride */, 0 /*
														 * The bound VBO data
														 * offset
														 */);

		glContext.enableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	@Override
	public void loadColorBuffer(GLBuffer fbColors, int length) {

		if (fbColors == null || fbColors.isEmpty()) {
			glContext.disableVertexAttribArray(GLSL_ATTRIB_COLOR);
			return;
		}

		// prevent use of global color
		setColor(-1, -1, -1, -1);

		// ///////////////////////////////////
		// VBO - colors

		// Select the VBO, GPU memory data
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboColors);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(fbColors);

		// Associate attribute
		vertexAttribPointerGlobal(GLSL_ATTRIB_COLOR, 4);

		// VBO
		glContext.enableVertexAttribArray(GLSL_ATTRIB_COLOR);

	}

	private boolean oneNormalForAllVertices;

	protected void resetOneNormalForAllVertices() {
		oneNormalForAllVertices = false;
		glContext.uniform3f(normalLocation, 2, 2, 2);
	}

	private float[] tmpNormal3 = new float[3];

	@Override
	public void loadNormalBuffer(GLBuffer fbNormals, int length) {

		if (fbNormals == null || fbNormals.isEmpty()) { // no normals
			glContext.disableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			return;
		}

		if (fbNormals.capacity() == 3) { // one normal for all vertices
			glContext.disableVertexAttribArray(GLSL_ATTRIB_NORMAL);
			fbNormals.array(tmpNormal3);
			glContext.uniform3fv(normalLocation, tmpNormal3);
			oneNormalForAllVertices = true;
			return;
		}

		// ///////////////////////////////////
		// VBO - normals

		if (oneNormalForAllVertices) {
			resetOneNormalForAllVertices();
		}

		// ///////////////////////////////////
		// VBO - normals

		// Select the VBO, GPU memory data
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboNormals);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(fbNormals);

		// Associate attribute
		vertexAttribPointerGlobal(GLSL_ATTRIB_NORMAL, 3);

		// VBO
		glContext.enableVertexAttribArray(GLSL_ATTRIB_NORMAL);

	}

	/**
	 * set vertex attribute pointer
	 * 
	 * @param attrib
	 *            attribute
	 * @param size
	 *            size
	 */
	private void vertexAttribPointer(int attrib, int size) {
		glContext.vertexAttribPointer(attrib, size,
				WebGLRenderingContext.FLOAT, false, 0, 0);
	}

	private void vertexAttribPointerGlobal(int attrib, int size) {
		// vertexAttribPointer(attrib, size);
	}

	/**
	 * attribute vertex pointers
	 */
	private void attribPointers() {

		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboVertices);
		vertexAttribPointer(GLSL_ATTRIB_POSITION, 3);

		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboNormals);
		vertexAttribPointer(GLSL_ATTRIB_NORMAL, 3);

		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboColors);
		vertexAttribPointer(GLSL_ATTRIB_COLOR, 4);

		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
				vboTextureCoords);
		vertexAttribPointer(GLSL_ATTRIB_TEXTURE, 2);
	}

	@Override
	public void loadTextureBuffer(GLBuffer fbTextures, int length) {

		if (fbTextures == null || fbTextures.isEmpty()) {
			setCurrentGeometryHasNoTexture();
			glContext.disableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
			return;
		}

		setCurrentGeometryHasTexture();

		// ///////////////////////////////////
		// VBO - texture

		// Select the VBO, GPU memory data
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
		        vboTextureCoords);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(fbTextures);

		// Associate attribute
		vertexAttribPointerGlobal(GLSL_ATTRIB_TEXTURE, 2);

		// VBO
		glContext.enableVertexAttribArray(GLSL_ATTRIB_TEXTURE);

	}

	protected void glBufferData(GLBuffer fb) {
		glContext
		        .bufferData(WebGLRenderingContext.ARRAY_BUFFER,
				((GLBufferW) fb).getBuffer(), GL_TYPE_DRAW_TO_BUFFER);
	}

	final static private int GL_TYPE_DRAW_TO_BUFFER = WebGLRenderingContext.STREAM_DRAW;

	@Override
	public void loadVertexBuffer(GLBuffer fbVertices, int length) {

		// ///////////////////////////////////
		// VBO - vertices

		// Select the VBO, GPU memory data, to use for vertices
		glContext.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, vboVertices);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glBufferData(fbVertices);

		// Associate Vertex attribute 0 with the last bound VBO
		vertexAttribPointerGlobal(GLSL_ATTRIB_POSITION, 3);

		// VBO
		glContext.enableVertexAttribArray(GLSL_ATTRIB_POSITION);

	}

	@Override
	public void loadIndicesBuffer(GLBufferIndices arrayI, int length) {

		// ///////////////////////////////////
		// VBO - indices

		// Select the VBO, GPU memory data, to use for indices
		glContext.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
				vboIndices);

		// transfer data to VBO, this perform the copy of data from CPU -> GPU
		// memory
		glContext.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
						((GLBufferIndicesW) arrayI).getBuffer(),
						GL_TYPE_DRAW_TO_BUFFER);

	}

	@Override
	public void draw(Type type, int length) {
		glContext.drawArrays(getGLType(type), 0, length);

	}

	/**
	 * 
	 * @param type
	 *            Manager type
	 * @return GL type
	 */
	protected static int getGLType(Type type) {
		switch (type) {
		case TRIANGLE_STRIP:
			return WebGLRenderingContext.TRIANGLE_STRIP;
		case TRIANGLE_FAN:
			// if (Browser.supportsWebGLTriangleFan()){ // no TRIANGLE_FAN for
			// internet explorer
			// return WebGLRenderingContext.TRIANGLE_FAN;
			// }

			// wait for fix : detect webGL support correctly
			return WebGLRenderingContext.TRIANGLE_STRIP;
		case TRIANGLES:
			return WebGLRenderingContext.TRIANGLES;
		case LINE_LOOP:
			return WebGLRenderingContext.LINE_LOOP;
		case LINE_STRIP:
			return WebGLRenderingContext.LINE_STRIP;
		}

		return WebGLRenderingContext.TRIANGLES;
	}

	@Override
	protected void setDepthFunc() {
		glContext.depthFunc(WebGLRenderingContext.LEQUAL);
	}

	@Override
	protected void enablePolygonOffsetFill() {
		glContext.enable(WebGLRenderingContext.POLYGON_OFFSET_FILL);
	}

	@Override
	protected void setBlendFunc() {
		glContext.blendFunc(WebGLRenderingContext.SRC_ALPHA,
		        WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
		// glContext.blendFunc(WebGLRenderingContext.ONE,
		// WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);

		// glContext.blendFunc(WebGLRenderingContext.ONE,
		// WebGLRenderingContext.ZERO);
		/*
		 * glContext.blendEquationSeparate(WebGLRenderingContext.FUNC_ADD,
		 * WebGLRenderingContext.FUNC_ADD); glContext.blendFuncSeparate(
		 * WebGLRenderingContext.SRC_ALPHA,
		 * WebGLRenderingContext.ONE_MINUS_SRC_ALPHA,
		 * WebGLRenderingContext.SRC_ALPHA,
		 * WebGLRenderingContext.ONE_MINUS_SRC_ALPHA);
		 */
	}

	@Override
	protected void enableNormalNormalized() {
		// TODO Auto-generated method stub
	}

	private boolean texturesEnabled;

	@Override
	final public void enableTextures() {
		texturesEnabled = true;
		setCurrentGeometryHasNoTexture(); // let first geometry init textures
	}

	@Override
	public void disableTextures() {
		texturesEnabled = false;
		setCurrentTextureType(TEXTURE_TYPE_NONE);
		glContext.disableVertexAttribArray(GLSL_ATTRIB_TEXTURE);
	}

	/**
	 * tells that current geometry has a texture
	 */
	final public void setCurrentGeometryHasTexture() {
		if (areTexturesEnabled() && currentTextureType == TEXTURE_TYPE_NONE) {
			setCurrentTextureType(oldTextureType);
		}
	}

	/**
	 * tells that current geometry has no texture
	 */
	final public void setCurrentGeometryHasNoTexture() {
		if (areTexturesEnabled() && currentTextureType != TEXTURE_TYPE_NONE) {
			oldTextureType = currentTextureType;
			setCurrentTextureType(TEXTURE_TYPE_NONE);

		}
	}

	@Override
	public void enableFading() {
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_FADING);
	}

	private int currentDash = Textures.DASH_INIT;

	@Override
	public void enableDash() {
		currentDash = Textures.DASH_INIT;
		enableTextures();
		setCurrentTextureType(TEXTURE_TYPE_DASH);
	}

	/**
	 * enable text textures
	 */
	@Override
	final public void enableTexturesForText() {
		super.enableTexturesForText();
		setCurrentTextureType(TEXTURE_TYPE_TEXT);
	}

	private int currentTextureType = TEXTURE_TYPE_NONE;
	private int oldTextureType = TEXTURE_TYPE_NONE;

	protected void setCurrentTextureType(int type) {
		currentTextureType = type;
		glContext.uniform1i(textureTypeLocation, type);
	}

	/**
	 * @return true if textures are enabled
	 */
	@Override
	public boolean areTexturesEnabled() {
		return texturesEnabled;
	}

	@Override
	protected float[] getLightPosition() {
		return LIGHT_POSITION_W;
	}

	@Override
	public void setDashTexture(int index) {
		if (currentDash == index) {
			return;
		}

		currentDash = index;

		if (index == Textures.DASH_NONE) {
			disableTextures();
		} else {
			enableTextures();
			setCurrentTextureType(TEXTURE_TYPE_DASH + index);
			glContext.uniform1fv(dashValuesLocation,
			        Textures.DASH_SHADERS_VALUES[index - 1]);
		}
	}

	@Override
	protected void drawSurfacesOutline() {

		// TODO

	}

	@Override
	public void enableLighting() {
		if (view3D.getUseLight()) {
			glContext.uniform1i(enableLightLocation, 1);
		}
	}

	@Override
	protected void enableLightingOnInit() {
		// no need for shaders
	}

	@Override
	protected void initCulling() {
		// no need for shaders
	}

	@Override
	public void initLighting() {
		if (view3D.getUseLight()) {
			glContext.uniform1i(enableLightLocation, 1);
		} else {
			glContext.uniform1i(enableLightLocation, 0);
		}
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			glContext.uniform1i(enableShineLocation, 0);
		}
	}

	@Override
	public void disableLighting() {
		if (view3D.getUseLight()) {
			glContext.uniform1i(enableLightLocation, 0);
		}
	}

	@Override
	public void disableShine() {
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			if (view3D.getUseLight()) {
				glContext.uniform1i(enableShineLocation, 0);
			}
		}
	}

	@Override
	public void enableShine() {
		if (view3D.getApplication().has(Feature.SHINY_3D)) {
			if (view3D.getUseLight()) {
				glContext.uniform1i(enableShineLocation, 1);
			}
		}
	}

	@Override
	protected void enableClipPlanes() {
		glContext.uniform1i(enableClipPlanesLocation, 1);
	}

	@Override
	protected void disableClipPlanes() {
		glContext.uniform1i(enableClipPlanesLocation, 0);
	}

	private float[] clipPlanesMin = new float[3];
	private float[] clipPlanesMax = new float[3];

	@Override
	public void setClipPlanes(double[][] minMax) {
		if (view3D.getApplication().has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			for (int i = 0; i < 3; i++) {
				double scale = view3D.getScale(i);
				clipPlanesMin[i] = (float) (minMax[i][0] * scale);
				clipPlanesMax[i] = (float) (minMax[i][1] * scale);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				clipPlanesMin[i] = (float) minMax[i][0];
				clipPlanesMax[i] = (float) minMax[i][1];
			}
		}
	}

	private void setClipPlanesToShader() {

		glContext.uniform3fv(clipPlanesMinLocation, clipPlanesMin);
		glContext.uniform3fv(clipPlanesMaxLocation, clipPlanesMax);

	}

	@Override
	protected void initRenderingValues() {

		super.initRenderingValues();

		// clip planes
		setClipPlanesToShader();
	}

	@Override
	protected void drawFaceToScreen() {
		glContext.uniform1i(labelRenderingLocation, 1);
		resetCenter();
		super.drawFaceToScreen();
		glContext.uniform1i(labelRenderingLocation, 0);
	}
	
	@Override
	protected void drawFaceToScreenEnd() {
		glContext.uniform1i(labelRenderingLocation, 1);
		resetCenter();
		super.drawFaceToScreenEnd();
		glContext.uniform1i(labelRenderingLocation, 0);
	}

	@Override
	public void setLabelOrigin(Coords origin) {
		glContext.uniform3fv(labelOriginLocation, origin.get3ForGL());
	}

	private Hitting hitting;

	@Override
	public void setHits(GPoint mouseLoc, int threshold) {

		if (mouseLoc == null) {
			return;
		}

		hitting.setHits(mouseLoc, threshold);

	}

	@Override
	public GeoElement getLabelHit(GPoint mouseLoc) {

		if (mouseLoc == null) {
			return null;
		}

		return hitting.getLabelHit(mouseLoc);
	}

	@Override
	public boolean useLogicalPicking() {
		return true;
	}

	@Override
	public Hitting getHitting() {
		return hitting;
	}

	@Override
	public void setCenter(Coords center) {
		float[] c = center.get4ForGL();
		// set radius info
		c[3] *= DrawPoint3D.DRAW_POINT_FACTOR / view3D.getScale();
		glContext.uniform4fv(centerLocation, c);

	}

	private float[] resetCenter = { 0f, 0f, 0f, 0f };

	@Override
	public void resetCenter() {
		glContext.uniform4fv(centerLocation, resetCenter);
	}

	@Override
	protected void drawTranspNotCurved() {
		enableCulling();
		setCullFaceFront();
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);
		setCullFaceBack();
		drawable3DLists.drawTransp(this);
		drawable3DLists.drawTranspClosedNotCurved(this);

	}

	@Override
	protected void setBufferLeft() {
		// TODO
	}

	@Override
	protected void setBufferRight() {
		// TODO
	}

	public void setBuffering(boolean b) {
		this.createGLContext(b);
	}

	@Override
	public boolean useShaders() {
		return true;
	}

	@Override
	protected void needExportImage(double scale, int w, int h) {
		// TODO
	}

	@Override
	final protected void setExportImageDimension(int w, int h) {
		// TODO
	}

	@Override
	protected void exportImageEquirectangular() {
		// TODO
	}

	@Override
	protected void initExportImageEquirectangularTiles() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setExportImageEquirectangularTileLeft(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setExportImageEquirectangularFromTiles() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setExportImageEquirectangularTileRight(int i) {
		// TODO Auto-generated method stub

	}

}
