package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;

/**
 * manager packing geometries
 *
 */
public class ManagerShadersElementsGlobalBufferPacking extends ManagerShadersElementsGlobalBuffer {

	/**
	 * alpha value for invisible parts
	 */
	static final public float ALPHA_INVISIBLE_VALUE = -1f;

	private GLBufferManagerCurves bufferManagerCurves;
	private GLBufferManagerSurfaces bufferManagerSurfaces, bufferManagerSurfacesClosed;
	private GLBufferManager currentBufferManager;
	private GColor currentColor;
	private int currentTextureType;
	private ReusableArrayList<Short> indices;

	private class GeometriesSetElementsGlobalBufferPacking extends GeometriesSetElementsGlobalBuffer {

		private GLBufferManager bufferManager;
		private static final long serialVersionUID = 1L;
		private GColor color;
		private int index;

		/**
		 * constructor
		 * 
		 * @param bufferManager
		 *            gl buffer manager
		 * 
		 * @param color
		 *            color
		 */
		public GeometriesSetElementsGlobalBufferPacking(GLBufferManager bufferManager, GColor color) {
			this.color = color;
			this.bufferManager = bufferManager;
		}

		@Override
		public void setIndex(int index, GColor color) {
			this.index = index;
			this.color = color;
		}

		/**
		 * 
		 * @return geometry set index
		 */
		public int getIndex() {
			return index;
		}

		@Override
		protected Geometry newGeometry(Type type) {
			return new GeometryElementsGlobalBufferPacking(this, type, currentGeometryIndex);
		}

		@Override
		public void bindGeometry(int size, TypeElement type) {
			bufferManager.setIndices(size, type);
		}

		/**
		 * update all geometries color for this set
		 * 
		 * @param color
		 *            color
		 */
		public void updateColor(GColor color) {
			this.color = color;
			bufferManager.updateColor(index, getGeometriesLength(), color);
		}

		/**
		 * update all geometries visibility for this set
		 * 
		 * @param visible
		 *            if visible
		 */
		public void updateVisibility(boolean visible) {
			bufferManager.updateVisibility(index, getGeometriesLength(), visible);

		}

		public GColor getColor() {
			return color;
		}

		/**
		 * 
		 * @return gl buffer manager
		 */
		public GLBufferManager getBufferManager() {
			return bufferManager;
		}

		@Override
		public void removeBuffers() {
			bufferManager.remove(index, getGeometriesLength());
		}

		/**
		 * geometry handler for buffer packing
		 *
		 */
		public class GeometryElementsGlobalBufferPacking extends Geometry {

			private int geometryIndex;
			private GeometriesSetElementsGlobalBufferPacking geometrySet;

			public GeometryElementsGlobalBufferPacking(GeometriesSetElementsGlobalBufferPacking geometrySet, Type type,
					int geometryIndex) {
				super(type);
				this.geometrySet = geometrySet;
				this.geometryIndex = geometryIndex;
			}

			@Override
			protected void setBuffers() {
				// no internal buffer needed here
			}

			@Override
			public void setType(Type type) {
				this.type = type;
			}

			@Override
			public void setVertices(ArrayList<Double> array, int length) {
				geometrySet.getBufferManager().setCurrentIndex(geometrySet.getIndex(), geometryIndex);
				geometrySet.getBufferManager().setVertexBuffer(array, length);
			}

			@Override
			public void setNormals(ArrayList<Double> array, int length) {
				geometrySet.getBufferManager().setNormalBuffer(array, length);
			}

			@Override
			public void setTextures(ArrayList<Double> array, int length) {
				geometrySet.getBufferManager().setTextureBuffer(array, length);
			}

			@Override
			public void setTexturesEmpty() {
				// not implemented yet
			}

			@Override
			public void setColors(ArrayList<Double> array, int length) {
				// not implemented yet
			}

			@Override
			public void setColorsEmpty() {
				geometrySet.getBufferManager().setColorBuffer(geometrySet.getColor());
			}

		}

	}

	/**
	 * constructor
	 * 
	 * @param renderer
	 *            renderer
	 * @param view3d
	 *            3D view
	 */
	public ManagerShadersElementsGlobalBufferPacking(Renderer renderer,
			EuclidianView3D view3d) {
		super(renderer, view3d);
		bufferManagerCurves = new GLBufferManagerCurves();
		bufferManagerSurfaces = new GLBufferManagerSurfaces(this);
		bufferManagerSurfacesClosed = new GLBufferManagerSurfaces(this);
		currentBufferManager = null;
	}

	@Override
	protected GeometriesSet newGeometriesSet() {
		if (currentBufferManager != null) {
			return new GeometriesSetElementsGlobalBufferPacking(currentBufferManager, currentColor);
		}
		return super.newGeometriesSet();
	}

	/**
	 * draw curves
	 * 
	 * @param renderer
	 *            renderer
	 * @param hidden
	 *            if hidden
	 */
	public void drawCurves(Renderer renderer, boolean hidden) {
		bufferManagerCurves.draw((RendererShadersInterface) renderer, hidden);
	}

	/**
	 * draw surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawSurfaces(Renderer renderer) {
		bufferManagerSurfaces.draw((RendererShadersInterface) renderer);
	}

	/**
	 * draw closed surfaces
	 * 
	 * @param renderer
	 *            renderer
	 */
	public void drawSurfacesClosed(Renderer renderer) {
		bufferManagerSurfacesClosed.draw((RendererShadersInterface) renderer);
	}

	@Override
	public void setPackCurve(GColor color, int lineType, int lineTypeHidden) {
		currentBufferManager = bufferManagerCurves;
		this.currentColor = color;
		this.currentTextureType = Textures.getDashIdFromLineType(lineType, lineTypeHidden);
	}

	@Override
	public void updateColor(GColor color, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet != null) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet).updateColor(color);
		}
	}

	@Override
	public void updateVisibility(boolean visible, int index) {
		GeometriesSet geometrySet = getGeometrySet(index);
		if (geometrySet != null) {
			((GeometriesSetElementsGlobalBufferPacking) geometrySet).updateVisibility(visible);
		}
	}

	@Override
	protected void texture(double x) {
		texture(x, currentTextureType);
	}

	@Override
	public int startNewList(int old) {
		int index = super.startNewList(old);
		currentGeometriesSet.setIndex(index, currentColor);
		return index;
	}

	@Override
	public boolean packBuffers() {
		return true;
	}

	@Override
	public void reset() {
		bufferManagerCurves.reset();
		bufferManagerSurfaces.reset();
		bufferManagerSurfacesClosed.reset();
	}

	@Override
	public int startPolygons(Drawable3D d) {
		if (d.shouldBePacked()) {
			if (d.addedFromClosedSurface()) {
				currentBufferManager = bufferManagerSurfacesClosed;
			} else {
				currentBufferManager = bufferManagerSurfaces;
			}
			this.currentColor = d.getSurfaceColor();
		}
		return super.startPolygons(d);
	}

	@Override
	public void endPolygons(Drawable3D d) {
		super.endPolygons(d);
		if (d.shouldBePacked()) {
			endPacking();
		}
	}

	@Override
	public void endPacking() {
		currentBufferManager = null;
	}

	@Override
	protected void setIndicesForDrawTriangleFans(int size) {
		if (indices == null) {
			indices = new ReusableArrayList<Short>(size);
		}
		indices.setLength(0);
	}

	@Override
	protected void putToIndicesForDrawTriangleFans(short index) {
		indices.addValue(index);
	}

	@Override
	protected void rewindIndicesForDrawTriangleFans() {
		// nothing to do
	}

	/**
	 * 
	 * @return current indices
	 */
	public ReusableArrayList<Short> getIndices() {
		return indices;
	}

}
