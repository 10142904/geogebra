package org.geogebra.web.keyboard;

import org.geogebra.web.html5.util.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface KeyboardResources extends ClientBundle {

	KeyboardResources INSTANCE = GWT.create(KeyboardResources.class);

	// ONSCREENKEYBOARD
	@Source("org/geogebra/common/icons/png/keyboard/view_close.png")
	ImageResource keyboard_close();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_shiftDown.png")
	ImageResource keyboard_shiftDown();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_shift.png")
	ImageResource keyboard_shift();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_backspace.png")
	ImageResource keyboard_backspace();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_enter.png")
	ImageResource keyboard_enter();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowLeft.png")
	ImageResource keyboard_arrowLeft();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowRight.png")
	ImageResource keyboard_arrowRight();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_open.png")
	ImageResource keyboard_show();

	@Source("org/geogebra/web/keyboard/css/keyboard-styles.scss")
	SassResource keyboardStyle();

}
