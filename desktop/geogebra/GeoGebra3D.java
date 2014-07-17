/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/**
 * GeoGebra Application
 *
 * @author Markus Hohenwarter
 */
package geogebra;

import geogebra3D.euclidian3D.opengl.RendererJogl;

public class GeoGebra3D extends GeoGebra {

	static {
		RendererJogl.initCaps();
	}

	public static void main(String[] cmdArgs) {
		(new GeoGebra3D()).doMain(cmdArgs);
	}

	protected GeoGebra3D() {
	}

	protected void startGeoGebra(CommandLineArguments args) {
		// create and open first GeoGebra window
		geogebra.gui.app.GeoGebraFrame3D.main(args);
	}

}