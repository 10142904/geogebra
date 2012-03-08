package geogebra.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {
	
	GuiResources INSTANCE = GWT.create(GuiResources.class);
	
	@Source("geogebra/resources/images/ggb4-splash-h120.png")
	ImageResource getGeoGebraWebSplash();
	
	@Source("geogebra/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();
	
	@Source("geogebra/resources/images/nav_play.png")
	ImageResource navPlay();
	
	@Source("geogebra/resources/images/nav_pause.png")
	ImageResource navPause();
	
	@Source("geogebra/resources/images/view-refresh.png")
	ImageResource viewRefresh();
	
	@Source("geogebra/resources/images/10x1.png")
	ImageResource png10x1();
	
	@Source("geogebra/resources/images/splash-ggb4.svg")
	TextResource ggb4Splash();
	
	@Source("geogebra/resources/js/zipjs/dataview.js")
	TextResource dataViewJs();
	
	@Source("geogebra/resources/js/zipjs/zip.js")
	TextResource zipJs();

	@Source("geogebra/resources/js/zipjs/inflate.js")
	TextResource inflateJs();
	
	

}
