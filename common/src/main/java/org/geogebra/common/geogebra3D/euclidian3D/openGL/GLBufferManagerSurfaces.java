package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;

/**
 * manager for packing buffers (for surfaces)
 */
public class GLBufferManagerSurfaces extends GLBufferManager {

	// complex materials need not more than 100
	static final private int ELEMENTS_SIZE_START = 128;
	// use 1.5 empirical factor observed from materials
	static final private int INDICES_SIZE_START = (ELEMENTS_SIZE_START * 3) / 2;

	private ManagerShadersElementsGlobalBufferPacking manager;

	/**
	 * constructor
	 * 
	 * @param manager
	 *            geometries manager
	 */
	public GLBufferManagerSurfaces(ManagerShadersElementsGlobalBufferPacking manager) {
		this.manager = manager;
	}

	@Override
	protected int calculateIndicesLength(int size, TypeElement type) {
		switch (type) {
		case FAN_DIRECT:
		case FAN_INDIRECT:
			return 3 * (size - 2);
		case SURFACE:
			return size;
		default:
			// should not happen
			return 0;
		}
	}

	@Override
	protected void putIndices(int size, TypeElement type) {
		switch (type) {
		case FAN_DIRECT:
			short k = 1;
			short zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k);
				k++;
				putToIndices(k);
			}
			break;
		case FAN_INDIRECT:
			short k2 = 2;
			k = 1;
			zero = 0;
			while (k < size - 1) {
				putToIndices(zero);
				putToIndices(k2);
				putToIndices(k);
				k++;
				k2++;
			}
			break;
		case SURFACE:
			ReusableArrayList<Short> indices = manager.getIndices();
			for (int i = 0; i < indices.getLength(); i++) {
				putToIndices(indices.get(i));
			}
			break;
		default:
			// should not happen
			break;
		}
	}

	/**
	 * draw
	 * 
	 * @param r
	 *            renderer
	 */
	public void draw(RendererShadersInterface r) {
		drawBufferPacks(r);
	}

	@Override
	protected boolean currentBufferSegmentDoesNotFit(int indicesLength, TypeElement type) {
		if (type == TypeElement.SURFACE || currentBufferSegment.type == TypeElement.SURFACE
				|| type != currentBufferSegment.type) {
			addCurrentToAvailableSegments();
			return true;
		}
		return super.currentBufferSegmentDoesNotFit(indicesLength, type);
	}

	@Override
	protected int getElementSizeStart() {
		return ELEMENTS_SIZE_START;
	}

	@Override
	protected int getIndicesSizeStart() {
		return INDICES_SIZE_START;
	}

}
