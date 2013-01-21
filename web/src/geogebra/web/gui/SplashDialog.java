package geogebra.web.gui;

import geogebra.common.GeoGebraConstants;
import geogebra.web.css.GuiResources;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class SplashDialog extends SimplePanel {
	
	boolean appLoaded = false;
	boolean timerEllapsed = false;
	
	private Timer t = new Timer() {
		@Override
        public void run() {
			if (appLoaded) {
				hide();
			}
			timerEllapsed = true;
		}
	};

	public SplashDialog() {
	    HTML svg = new HTML("<div style=\"position: absolute; z-index: 1000000; border: 1px solid black; background-color: white; \"" + GuiResources.INSTANCE.ggb4Splash().getText() + GuiResources.INSTANCE.ggbSinnerHtml().getText() + "</div>");
	    add(svg);
	    t.schedule(GeoGebraConstants.SPLASH_DIALOG_DELAY);
    }

	protected void hide() {
		this.removeFromParent();
    }

	public void canNowHide() {
	   appLoaded = true;
	   if (timerEllapsed) {
		   hide();
	   }	    
    }

}
