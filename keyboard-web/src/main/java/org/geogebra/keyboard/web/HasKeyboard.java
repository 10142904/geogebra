package org.geogebra.keyboard.web;

import org.geogebra.web.html5.gui.util.KeyboardLocale;

public interface HasKeyboard {
	
	void updateKeyboardHeight();

	double getInnerWidth();

	KeyboardLocale getLocalization();

	boolean needsSmallKeyboard();

	boolean hasKeyboardInProbCalculator();

	void updateCenterPanelAndViews();

	// used only for feature check
	boolean ggbtransFeature();
}
