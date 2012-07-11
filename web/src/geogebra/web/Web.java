package geogebra.web;


import java.util.ArrayList;
import java.util.Date;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.web.asyncservices.HandleGoogleDriveService;
import geogebra.web.asyncservices.HandleGoogleDriveServiceAsync;
import geogebra.web.asyncservices.HandleOAuth2Service;
import geogebra.web.asyncservices.HandleOAuth2ServiceAsync;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.helper.JavaScriptInjector;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;
import geogebra.web.util.JSON;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;



/**
 * @author apa
 *
 */
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Web implements EntryPoint {
	

	/**
	 * Google Authorization class entry point.
	 */
	public static final Auth AUTH = Auth.get();
	/**
	 * Global Async for communication with the server
	 */
	public final static HandleGoogleDriveServiceAsync gdAsync = GWT.create(HandleGoogleDriveService.class);
	public final static HandleOAuth2ServiceAsync oaAsync = GWT.create(HandleOAuth2Service.class);
	
	public void t(String s,AlgebraProcessor ap) throws Exception{
		ap.processAlgebraCommandNoExceptionHandling(s, false,false, true);
	}
	
	private ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Date creationDate = new Date();
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i+creationDate.getTime());
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}
	
	/**
	 * @author gabor
	 * Describes the Gui type that needed to load
	 *
	 */
	
	public enum GuiToLoad {
		/**
		 * Gui For an App.
		 */
		APP, 
		/**
		 * Gui for a mobile
		 */
		MOBILE,
		/**
		 * No Gui, only euclidianView
		 */
		VIEWER
	}
	
	/**
	 * GUI currently Loaded
	 */
	public static GuiToLoad currentGUI = null;

	public void onModuleLoad() {
		//do we have an app?
		Web.currentGUI = checkIfNeedToLoadGUI();
		if (!Web.currentGUI.equals(GuiToLoad.VIEWER)) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.propertiesKeysJS().getText());
			// popup when the user wants to exit accidentally
	        Window.addWindowClosingHandler(new Window.ClosingHandler() {
	            public void onWindowClosing(ClosingEvent event) {
	            	// TODO: Localize this, or omit message completely,
	            	// and maybe put this somewhere else (where i18n is already available).
	                event.setMessage("Now you are about to close the GeoGebra application and lose all unsaved data.");
	            }
	        });
		}
		
		injectResources();
		
//		setLocaleToQueryParam();
				
		if (Web.currentGUI.equals(GuiToLoad.VIEWER)) {
			//we dont want to parse out of the box sometimes...
			if (!calledFromExtension()) {
				startGeoGebra(getGeoGebraMobileTags());
			} else {
				exportArticleTagRenderer();
			}
		} else if (Web.currentGUI.equals(GuiToLoad.APP)) {
			loadAppAsync();
		}
	}

	public static void injectResources() {
	    // insert mathquill css
		StyleInjector.inject(GuiResources.INSTANCE.mathquillCss().getText());

		//insert zip.js
		JavaScriptInjector.inject(GuiResources.INSTANCE.zipJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.jQueryJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.mathquillJs().getText());
		Web.webWorkerSupported = checkWorkerSupport(GWT.getModuleBaseURL());
		if (!webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.arrayBufferJs().getText());
		//strange, but iPad can blow it away again...
		if (checkIfFallbackSetExplicitlyInArrayBufferJs() && webWorkerSupported) {
			JavaScriptInjector.inject(GuiResources.INSTANCE.deflateJs().getText());
			JavaScriptInjector.inject(GuiResources.INSTANCE.inflateJs().getText());
		}
		JavaScriptInjector.inject(GuiResources.INSTANCE.dataViewJs().getText());
		JavaScriptInjector.inject(GuiResources.INSTANCE.base64Js().getText());
    }
	
	

	private void loadAppAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				GeoGebraAppFrame app = new GeoGebraAppFrame();
			}

			public void onFailure(Throwable reason) {
				App.debug(reason);
			}
		});
	    
    }
	
	
	/*
	 * Checks, if the <body data-param-app="true" exists in html document
	 * if yes, GeoGebraWeb will be loaded as a full app.
	 * 
	 * @return true if bodyelement has data-param-app=true
	 */
	private static GuiToLoad checkIfNeedToLoadGUI() {
	    if ("true".equals(RootPanel.getBodyElement().getAttribute("data-param-app"))) {
	    	return GuiToLoad.APP;
	    } else if ("true".equals(RootPanel.getBodyElement().getAttribute("data-param-mobile"))) {
	    	return GuiToLoad.MOBILE;
	    }
	    return GuiToLoad.VIEWER;
    }
	
	private native void exportArticleTagRenderer() /*-{
	    $wnd.GGW_ext.render = $entry(@geogebra.web.gui.applet.GeoGebraFrame::renderArticleElemnt(Lgeogebra/web/html5/ArticleElement;));
    }-*/;
    
	private native boolean calledFromExtension() /*-{
	    return (typeof $wnd.GGW_ext !== "undefined");
    }-*/;
	
	public static boolean webWorkerSupported = false; 
	
	private void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.web.gui.applet.GeoGebraFrame.main(geoGebraMobileTags);
	    
    }
	
	
	private static native boolean checkIfFallbackSetExplicitlyInArrayBufferJs() /*-{
		if ($wnd.zip.useWebWorkers === false) {
			//we set this explicitly in arraybuffer.js
			@geogebra.web.main.AppW::debug(Ljava/lang/String;)("INIT: workers maybe supported, but fallback set explicitly in arraybuffer.js");
			return true;;
		}
		return false;
	}-*/;
	
	
	private static native boolean checkWorkerSupport(String workerpath) /*-{
	    try {
	    	var worker = new $wnd.Worker(workerpath+"js/workercheck.js");
	    } catch (e) {
	    	@geogebra.web.main.AppW::debug(Ljava/lang/String;)("INIT: worker not supported, fallback for simple js");
	    	
	    	return false;
	    }
	    @geogebra.web.main.AppW::debug(Ljava/lang/String;)("INIT: workers are supported");
	    	
	    worker.terminate();
	    return true;
    }-*/;
	
}
