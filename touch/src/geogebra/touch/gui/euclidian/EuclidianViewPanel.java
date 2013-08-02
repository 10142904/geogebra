package geogebra.touch.gui.euclidian;

import geogebra.touch.controller.TouchController;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the canvas and the
 * euclidianView.
 */
public class EuclidianViewPanel extends AbsolutePanel {
	private EuclidianViewM euclidianView;

	/**
	 * @return euclidianView
	 */
	public EuclidianViewM getEuclidianView() {
		return this.euclidianView;
	}

	/**
	 * Creates the {@link EuclidianViewM euclidianView} and initializes the
	 * canvas on it.
	 * 
	 * @param ec
	 *            MobileEuclidianController
	 * @param widget
	 */
	public void initEuclidianView(TouchController ec, Widget widget, int width,
			int height) {
		this.euclidianView = new EuclidianViewM(this, ec, widget, width, height);
		this.add(this.euclidianView.getCanvas());
	}

	public void onResize(ResizeEvent event) {
		this.euclidianView.onResize(event);
	}

	public void repaint() {
		this.euclidianView.repaint();
	}

	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		if (this.euclidianView != null) {
			this.euclidianView.setPixelSize(width, height);
		}
	}

	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);
		this.euclidianView.setPixelSize(Integer.valueOf(width).intValue(),
				Integer.valueOf(height).intValue());
	}
}
