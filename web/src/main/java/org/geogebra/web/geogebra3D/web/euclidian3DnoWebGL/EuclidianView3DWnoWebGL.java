package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import java.util.HashMap;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;

/**
 * (dummy) 3D view for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class EuclidianView3DWnoWebGL extends EuclidianView3DW {

	private GBufferedImage thumb;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 * @param settings
	 *            settings
	 */
	public EuclidianView3DWnoWebGL(EuclidianController3D ec,
	        EuclidianSettings settings) {
		super(ec, settings);
		setCurrentFile(((AppW) ec.getApplication()).getCurrentFile());
	}

	@Override
	protected Renderer createRenderer() {
		return new RendererWnoWebGL(this);
	}

	@Override
	public void repaintView() {
		// repaint will be done only when resized
	}

	@Override
	public void repaint() {
		if (thumb != null) {
			this.g2p.drawImage(thumb, 0, 0);
		}

		this.g2p.setColor(GColor.BLACK);
		if (!getApplication().isScreenshotGenerator()) {
			this.g2p.drawString(getApplication().getPlain("NoWebGL"), 10, 20);
		}
	}

	@Override
	public void setCurrentFile(Object f) {
		HashMap<String, String> file = (HashMap<String, String>) f;
		if (file != null && file.get("geogebra_thumbnail.png") != null) {
			ImageElement img = Document.get().createImageElement();
			img.setSrc(file.get("geogebra_thumbnail.png"));
			thumb = new GBufferedImageW(img);
		}

		repaint();
	}

	public void onResize() {
		g2p.setCoordinateSpaceSize(this.getWidth(), this.getHeight());
		g2p.getCanvas().getElement().getParentElement().getStyle()
		        .setWidth(g2p.getCoordinateSpaceWidth(), Unit.PX);
		g2p.getCanvas().getElement().getParentElement().getStyle()
		        .setHeight(g2p.getCoordinateSpaceHeight(), Unit.PX);
	}

}
