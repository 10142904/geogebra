package org.geogebra.web.geogebra3D.web.euclidian3D;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

public class AxesAndPlanePopup extends PopupMenuButtonW {

	private EuclidianView3D ev;
	private ImageOrText defaultIcon;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            icons
	 * @param ev
	 *            view
	 */
	public AxesAndPlanePopup(AppW app, ImageOrText[] data, EuclidianView3D ev) {
		super(app, data, -1, data.length, SelectionTable.MODE_ICON, true,
				false, null);
		this.ev = ev;
		defaultIcon = data.length > 1 ? data[1] : null;

		this.setIcon(data[getIndexFromEV()]);
	}

	private int getIndexFromEV() {
		int ret = 0;
		if (ev.getShowXaxis()) {
			ret++;
		}
		if (ev.getShowPlane()) {
			ret += 2;
		}
		return ret;
	}

	public void setIndexFromEV() {
		setSelectedIndex(getIndexFromEV());
	}

	/**
	 * set euclidian view from index
	 */
	public void setEVFromIndex() {
		int index = getSelectedIndex();
		ev.getSettings().beginBatch();
		ev.getSettings().setShowAxes(MyDouble.isOdd(index));
		ev.getSettings().setShowPlate(index >= 2);
		ev.getSettings().endBatch();
		((EuclidianView3DW) ev).doRepaint();

	}

	@Override
	public void update(Object[] geos) {	
		if (app.has(Feature.CLEAR_VIEW_STYLEBAR)) {
			this.setVisible(geos.length == 0);
		} else {
			this.setVisible(
					geos.length == 0 && !EuclidianView.isPenMode(app.getMode())
							&& app.getMode() != EuclidianConstants.MODE_DELETE);
		}
	}

	@Override
	public void setIcon(ImageOrText icon) {
		if (getSelectedIndex() == 0 && defaultIcon != null) {
			super.setIcon(defaultIcon);
			this.removeStyleName("selected");
		} else {
			super.setIcon(icon);
			this.addStyleName("selected");
		}
	}
}
