package org.geogebra.web.geogebra3D.web.euclidian3D.openGL;

import org.geogebra.common.geogebra3D.euclidian3D.openGL.GPUBuffer;

import com.googlecode.gwtgl.binding.WebGLBuffer;

/**
 * Wrapper for {@link com.googlecode.gwtgl.binding.WebGLBuffer}
 *
 */
public class GPUBufferW implements GPUBuffer {

	private WebGLBuffer impl;

	@Override
	public void set(Object index) {
		impl = (WebGLBuffer) index;
	}

	@Override
	public WebGLBuffer get() {
		return impl;
	}

}
