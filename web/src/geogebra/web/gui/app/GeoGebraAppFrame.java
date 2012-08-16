/**
 * 
 */
package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.main.App;
import geogebra.web.cas.mpreduce.CASmpreduceW;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.html5.View;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;
import geogebra.web.util.JSON;

import java.util.Date;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends Composite {

	interface Binder extends UiBinder<DockLayoutPanel, GeoGebraAppFrame> { }
	private static final Binder binder = GWT.create(Binder.class);
	
	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();
	
	//declared in uibinder xml!
	public static int GGWVIewWrapper_WIDTH = 300;
	public static int GGWToolBar_HEIGHT = 50;
	private static final int GGWStyleBar_HEIGHT = 65;
	public static int GGWCommandLine_HEIGHT = 50;
	
	@UiField GGWToolBar ggwToolBar;
	@UiField GGWCommandLine ggwCommandLine;
	@UiField GGWMenuBar ggwMenuBar;
	MySplitLayoutPanel ggwSplitLayoutPanel;
	
	DockLayoutPanel outer = null;

	private App app;
	
	public GeoGebraAppFrame() {
		initWidget(outer = binder.createAndBindUi(this));
		outer.add(ggwSplitLayoutPanel = new MySplitLayoutPanel());
		
	    // Get rid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("0px");
	    addStyleName("GeoGebraAppFrame");

	    // Add the outer panel to the RootLayoutPanel, so that it will be
	    // displayed.
	    RootLayoutPanel root = RootLayoutPanel.get();
	    root.add(this);
	    root.forceLayout();
	}
	
	@Override
    protected void onLoad() {
//		init();
		setVisible(false);
		geoIPCall();				
	}
	
	
	private void geoIPCall() {
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(GeoGebraConstants.GEOIP_URL));
		
		try {
			Request request = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					AppW.geoIPCountryName = "";
					AppW.geoIPLanguage = "";
					init();
				}
				
				public void onResponseReceived(Request request, Response response) {
					if (200 == response.getStatusCode()) {
						JavaScriptObject geoIpInfos = JSON.parse(response.getText());
						AppW.geoIPCountryName = JSON.get(geoIpInfos, "geoIp");
						String acceptLanguage = (JSON.get(geoIpInfos,"acceptLanguage") != null) ? JSON.get(geoIpInfos,"acceptLanguage") : "" ;
						AppW.geoIPLanguage = JSON.get(geoIpInfos, "acceptLanguage").substring(0, acceptLanguage.indexOf(","));
						init();
					} else {
						AppW.geoIPCountryName = "";
						AppW.geoIPLanguage = "";
						init();
					}
				}
			});
		} catch (Exception e) {
		       App.error(e.getLocalizedMessage());
		       AppW.geoIPCountryName = "";
		       AppW.geoIPLanguage = "";
			   init();
	    }
	}	
	
	private int cw;
	private int ch;


	protected void init() {
		setVisible(true);
		ArticleElement article = ArticleElement.as(Dom.querySelector(GeoGebraConstants.GGM_CLASS_NAME));
		Date creationDate = new Date();
		article.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		cw = (Window.getClientWidth() - (GGWVIewWrapper_WIDTH + ggwSplitLayoutPanel.getSplitLayoutPanel().getSplitterSize())); 
		ch = (Window.getClientHeight() - (GGWToolBar_HEIGHT + GGWCommandLine_HEIGHT + GGWStyleBar_HEIGHT));
		app = createApplication(article,this);
//		((AppW)app).initializeLanguage();
		ggwSplitLayoutPanel.attachApp(app);
		ggwCommandLine.attachApp(app);
		ggwMenuBar.init(app);
		
		//Debugging purposes
		AppW.displaySupportedLocales();
		AppW.displayLocaleCookie();
		
		CASmpreduceW casMPReduce = (CASmpreduceW) ((GeoGebraCAS)(app.getKernel().getGeoGebraCAS())).getMPReduce();
	    CASmpreduceW.getStaticInterpreter(casMPReduce);
    }
	
	/**
	 * @return int computed width of the canvas
	 * 
	 * (Window.clientWidth - GGWViewWrapper (left - side) - splitter size)
	 */
	public int getCanvasCountedWidth() {
		return cw;
	}
	
	/**
	 * @return int computed height of the canvas
	 * 
	 * (Window.clientHeight - GGWToolbar - GGWCommandLine)
	 */
	public int getCanvasCountedHeight() {
		return ch;
	}


	private App createApplication(ArticleElement article,
            GeoGebraAppFrame geoGebraAppFrame) {
		return new AppW(article, geoGebraAppFrame);
    }


	public void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraAppFrame ins, AppW app) {
	    handleLoadFile(articleElement,app);
	    
    }
	
	private static void handleLoadFile(ArticleElement articleElement,
			AppW app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}
	
	/**
	 * @return Canvas
	 * 
	 * Return the canvas in UiBinder of EuclidianView1.ui.xml
	 */
	public Canvas getEuclidianView1Canvas() {
		return ggwSplitLayoutPanel.getGGWGraphicsView().getEuclidianView1Wrapper().getCanvas();
	}
	
	/**
	 * @return AbsolutePanel
	 * 
	 * EuclidianViewPanel for wrapping textfields
	 */
	public EuclidianPanel getEuclidianView1Panel() {
		return ggwSplitLayoutPanel.getGGWGraphicsView()	.getEuclidianView1Wrapper().getEuclidianPanel();
	}
	
	public SplitLayoutPanel getGGWSplitLayoutPanel() {
		return ggwSplitLayoutPanel.getSplitLayoutPanel();
	}

	
	/**
	 * @return GGWToolbar the Toolbar container
	 */
	public GGWToolBar getGGWToolbar() {
	    return ggwToolBar;
    }

}
