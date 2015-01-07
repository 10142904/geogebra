package geogebra.phone.gui.view.euclidian;

import geogebra.phone.gui.view.AbstractStyleBar;
import geogebra.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

public class EuclidianStyleBar extends AbstractStyleBar {
	
	private EuclidianStyleBarW euclidianStyleBar;
	
	public EuclidianStyleBar(EuclidianStyleBarW euclidianStyleBar) {
		this.euclidianStyleBar = euclidianStyleBar;
	}

	@Override
	protected IsWidget createStyleBar() {
		return euclidianStyleBar;
	}

	@Override
	protected ImageResource createStyleBarIcon() {
		return (ImageResource) resources.styleBar_graphicsView();
	}

	@Override
	public void setOpen(boolean showStyleBar) {
		euclidianStyleBar.setOpen(showStyleBar);
	}
}
