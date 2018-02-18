package org.geogebra.web.full.gui.util;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class VerticalSeparator extends SimplePanel {
	
	public VerticalSeparator(int height) {
		setStyleName("VerticalSeparator");
		getElement().getStyle().setHeight(height, Unit.PX);
	}
}
