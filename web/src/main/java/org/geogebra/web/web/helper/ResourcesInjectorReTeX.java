package org.geogebra.web.web.helper;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.resources.JavaScriptInjector;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.web.css.GuiResources;

public class ResourcesInjectorReTeX extends ResourcesInjector {
	@Override
	protected void injectResourcesGUI() {
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jQueryJs());
		jQueryNoConflict();
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.jqueryUI());
		StyleInjector.inject(GuiResources.INSTANCE.mowStyle());
		StyleInjector.inject(GuiResources.INSTANCE.spreadsheetStyle());
		StyleInjector.inject(GuiResources.INSTANCE.openScreenStyle());
		StyleInjector
				.inject(GuiResourcesSimple.INSTANCE.jqueryStyle().getText());
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
		JavaScriptInjector.inject(KeyboardResources.INSTANCE.wavesScript());
		StyleInjector.inject(KeyboardResources.INSTANCE.wavesStyle());
	}

	/**
	 * Runs JQ in noconflict mode; note that when running injectResourcesGUI
	 * twice jQuery is undefined on the second run
	 */
	private native void jQueryNoConflict() /*-{
		if ($wnd.jQuery && $wnd.jQuery.noConflict) {
			$wnd.$ggbQuery = $wnd.jQuery.noConflict(true);
		}
	}-*/;
}
