package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * panel for postioning widgets in Graphics
 *
 */
public class GBoxW extends GBox {

	private HorizontalPanel impl;
	private EuclidianController ec;

	/**
	 * @param ec
	 *            euclidian controller
	 */
	public GBoxW(EuclidianController ec) {
		this.ec = ec;
		impl = new HorizontalPanel();
		impl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	}

	/**
	 * @param box
	 *            box
	 * @return underlying panel
	 */
	public static HorizontalPanel getImpl(GBox box) {
		if (!(box instanceof GBoxW)) {
			return null;
		}
		return ((GBoxW) box).impl;
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((AutoCompleteTextFieldW) textField);

	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		impl.setWidth(rect.getWidth() + "");
		impl.setHeight(rect.getHeight() + "");

		if (impl.getParent() instanceof AbsolutePanel) {
			((AbsolutePanel) (impl.getParent())).setWidgetPosition(impl,
					(int) rect.getMinX(), (int) rect.getMinY());
		}
	}

	@Override
	public GRectangle getBounds() {
		int left = impl.getAbsoluteLeft();
		int top = impl.getAbsoluteTop();

		if (impl.getParent() != null) {
			left -= impl.getParent().getAbsoluteLeft();
			top -= impl.getParent().getAbsoluteTop();
		}

		if (ec != null && ec.getEnvironmentStyle() != null) {
			left = (int) (left * (1 / ec.getEnvironmentStyle().getScaleX()));
			top = (int) (top * (1 / ec.getEnvironmentStyle().getScaleY()));
		} else {
			Log.debug("ec null");
		}

		return new Rectangle(left, top, impl.getOffsetWidth(),
				impl.getOffsetHeight());
	}

	@Override
	public void revalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isVisible() {
		return impl.isVisible();
	}

}
