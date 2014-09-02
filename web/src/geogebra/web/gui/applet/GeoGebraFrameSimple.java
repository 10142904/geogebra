package geogebra.web.gui.applet;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppW;
import geogebra.html5.main.AppWsimple;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.WebStatic;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

public class GeoGebraFrameSimple extends GeoGebraFrame {

	public GeoGebraFrameSimple() {
		super(new GLookAndFeel());
	}

	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf, GLookAndFeel laf) {
		AppW app = new AppWsimple(ae, gf);
		WebStatic.lastApp = app;
		return app;
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * @param geoGebraMobileTags
	 *          list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrameSimple();
			inst.ae = articleElement;
			GeoGebraLogger.startLogger(inst.ae);
			inst.createSplash(articleElement);	
			if(WebStatic.panelForApplets == null){
				RootPanel.get(articleElement.getId()).add(inst);
			}else{
				WebStatic.panelForApplets.add(inst);
			}
		}
	}
	
	
	/**
	 * @param el html element to render into
	 */
	public static void renderArticleElement(Element el) {
		GeoGebraFrame.renderArticleElementWithFrame(el, new GeoGebraFrameSimple());
	}
}
