package geogebra.geogebra3D.web.euclidian3D.openGL;

import geogebra.common.geogebra3D.euclidian3D.openGL.GLBuffer;

import java.util.ArrayList;

import com.googlecode.gwtgl.array.Float32Array;

/**
 * buffers for openGL
 * 
 * @author mathieu
 *
 */
public class GLBufferW implements GLBuffer {

	private MyFloat32Array impl;

	/**
	 * constructor from float array
	 */
	public GLBufferW() {
		isEmpty = true;
		currentLength = 0;
	}

	private boolean isEmpty;

	private int currentLength;

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty() {
		isEmpty = true;
	}

	public void allocate(int length) {
		// allocate buffer only at start and when length change
		if (impl == null || impl.getLength() < length) {
			impl = (MyFloat32Array) Float32Array.create(length);
		}

		index = 0;

	}

	private int index = 0;

	public void setLimit(int length) {
		currentLength = length;
		isEmpty = false;
	}

	public void put(float value) {
		impl.set(index, value);
		index++;
	}


	public void set(ArrayList<Float> array, int length) {

		allocate(length);

		for (int i = 0; i < length; i++) {
			impl.set(i, array.get(i));
		}

		setLimit(length);
	}

	public int capacity() {
		return currentLength;
	}

	public void array(float[] ret) {
		for (int i = 0; i < ret.length; i++) {
			ret[i] = impl.get(i);
		}
	}

	/**
	 * 
	 * @return buffer
	 */
	public Float32Array getBuffer() {
		return impl.subarray(0, currentLength);
	}

}
