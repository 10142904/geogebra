package org.geogebra.web.html5.util;

import java.util.HashMap;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.move.ggtapi.models.AjaxCallback;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.GgbAPIW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

public class ViewW {

	private HashMap<String, String> archiveContent;

	private Element container;
	private AppW app;

	/** Loads file into active GeoGebraFrame */
	public static final LoadFilePresenter fileLoader = new LoadFilePresenter();

	public ViewW(Element container, AppW app) {
		this.app = app;
		this.container = container;
	}

	public static String checkLAF() {
		NodeList<Element> nodes = Dom
		        .getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		for (int i = 0; i < nodes.getLength(); i++) {
			if ("modern"
			        .equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return "modern";
			}
			if ("smart".equals(nodes.getItem(i).getAttribute("data-param-laf"))) {
				return "smart";
			}
		}
		return "standard";
	}

	public Element getContainer() {
		return container;
	}

	public String getDataParamFileName() {
		return ((ArticleElement) container).getDataParamFileName();
	}

	public String getDataParamJSON() {
		return ((ArticleElement) container).getDataParamJSON();
	}

	public String getDataParamBase64String() {
		return ((ArticleElement) container).getDataParamBase64String();
	}

	public String getDataParamLanguage() {
		return ((ArticleElement) container).getDataParamLanguage();
	}

	public String getDataParamCountry() {
		return ((ArticleElement) container).getDataParamCountry();
	}

	public boolean getDataParamUseBrowserForJS() {
		return ((ArticleElement) container).getDataParamUseBrowserForJS();
	}

	public boolean getDataParamEnableLabelDrags() {
		return ((ArticleElement) container).getDataParamEnableLabelDrags();
	}

	public boolean getDataParamEnableUndoRedo() {
		return ((ArticleElement) container).getDataParamEnableUndoRedo();
	}

	public boolean getDataParamEnableRightClick() {
		return ((ArticleElement) container).getDataParamEnableRightClick();
	}

	public boolean getDataParamShowMenuBar(boolean def) {
		return ((ArticleElement) container).getDataParamShowMenuBar(def);
	}

	public boolean getDataParamShowAlgebraInput(boolean def) {
		return ((ArticleElement) container).getDataParamShowAlgebraInput(def);
	}

	public boolean getDataParamShowToolBar(boolean def) {
		return ((ArticleElement) container).getDataParamShowToolBar(def);
	}

	public boolean getDataParamShowToolBarHelp() {
		// return ((ArticleElement) container).getDataParamShowToolBarHelp();
		return false;
	}

	public boolean getDataParamShiftDragZoomEnabled() {
		return ((ArticleElement) container).getDataParamShiftDragZoomEnabled();
	}

	public boolean getDataParamShowResetIcon() {
		return ((ArticleElement) container).getDataParamShowResetIcon();
	}

	public boolean getDataParamShowAnimationButton() {
		return ((ArticleElement) container).getDataParamShowAnimationButton();
	}

	public int getDataParamCapturingThreshold() {
		return ((ArticleElement) container).getDataParamCapturingThreshold();
	}

	public boolean getDataParamAllowSymbolTable() {
		return ((ArticleElement) container).getDataParamAllowSymbolTable();
	}

	public boolean getDataParamErrorDialogsActive() {
		return ((ArticleElement) container).getDataParamErrorDialogsActive();
	}

	public String getDataParamPerspective() {
		return ((ArticleElement) container).getDataParamPerspective();
	}

	public boolean getDataParamAllowStyleBar() {
		return ((ArticleElement) container).getDataParamAllowStyleBar();
	}

	public ArticleElement getArticleElement() {
		return ((ArticleElement) container);
	}

	private native void log(Object ex)/*-{
		if ($wnd.console) {
			$wnd.console.log(ex);
		}
	}-*/;

	private void maybeLoadFile() {
		if (app == null || archiveContent == null) {
			return;
		}

		try {
			app.loadGgbFile(archiveContent);
			Log.debug("loadggb finished" + System.currentTimeMillis());
		} catch (Throwable ex) {
			ex.printStackTrace();
			log(ex);
			return;
		}
		archiveContent = null;

		// app.getScriptManager().ggbOnInit(); //this line is moved from here
		// too,
		// it should load after the images are loaded

		Log.debug("file loaded");
		// This is used also by touch where dialog manager is null
		app.notifyFileLoaded();

		// reiniting of navigation bar, to show the correct numbers on the label
		if (app.getGuiManager() != null && app.getUseFullGui()) {
			ConstructionProtocolNavigation cpNav = this.getApplication()
			        .getGuiManager()
			        .getConstructionProtocolNavigationIfExists();
			if (cpNav != null) {
				cpNav.update();
			}
		}
		Log.debug("end unzipping" + System.currentTimeMillis());
	}

	public void maybeLoadFile(HashMap<String, String> archiveCont) {
		archiveContent = archiveCont;
		maybeLoadFile();
	}

	public AppW getApplication() {
		return app;
	}

	public void processBase64String(String dataParamBase64String) {
		populateArchiveContent(getBase64Reader(dataParamBase64String));
	}

	private int zippedLength = 0;

	private void putIntoArchiveContent(String key, String value) {
		archiveContent.put(key, value);
		if (archiveContent.size() == zippedLength) {
			maybeLoadFile();
		}
	}

	private void populateArchiveContent(JavaScriptObject ggbReader) {
		String workerUrls = prepareFileReading();
		GgbAPIW.setWorkerURL(workerUrls, false);
		populateArchiveContent(workerUrls, this, ggbReader);
	}
	private native void populateArchiveContent(String workerUrls, ViewW view,
			JavaScriptObject ggbReader) /*-{
	                                                      
	                                                      
	                                                      
      // Writer for ASCII strings
      function ASCIIWriter() {
	      var that = this, data;
	      
	      function init(callback, onerror) {
		      data = "";
		      callback();
	      }
	      
	      function writeUint8Array(array, callback, onerror) {
		      var i;
		      for (i = 0; i < array.length; i++) {
		      	data += $wnd.String.fromCharCode(array[i]);
		      }
		      callback();
	      }
	      
	      function getData(callback) {		
	      	callback(data);
	      }
	      
	      that.init = init;
	      that.writeUint8Array = writeUint8Array;
	      that.getData = getData;
      }
      ASCIIWriter.prototype = new $wnd.zip.Writer();
      ASCIIWriter.prototype.constructor = ASCIIWriter;
      
      function decodeUTF8(str_data) {
	      var tmp_arr = [], i = 0, ac = 0, c1 = 0, c2 = 0, c3 = 0;
	      
	      str_data += '';
	      
	      while (i < str_data.length) {
		      c1 = str_data.charCodeAt(i);
		      if (c1 < 128) {
			      tmp_arr[ac++] = String.fromCharCode(c1);
			      i++;
		      } else if (c1 > 191 && c1 < 224) {
			      c2 = str_data.charCodeAt(i + 1);
			      tmp_arr[ac++] = String.fromCharCode(((c1 & 31) << 6) | (c2 & 63));
			      i += 2;
		      } else {
			      c2 = str_data.charCodeAt(i + 1);
			      c3 = str_data.charCodeAt(i + 2);
			      tmp_arr[ac++] = String.fromCharCode(((c1 & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
			      i += 3;
		      }
	      }
	      
	      return tmp_arr.join('');
      }		
      
      // see GGB-63
      var imageRegex = /\.(png|jpg|jpeg|gif|bmp|tif|tiff)$/i;    
      
      var readerCallback = function(reader) {
	      reader.getEntries(function(entries) {
		      view.@org.geogebra.web.html5.util.ViewW::zippedLength = entries.length;
		      for (var i = 0, l = entries.length; i < l; i++) {
			      (function(entry){	            		
			      var filename = entry.filename;
			      if (entry.filename.match(imageRegex)) {
				      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(filename+" : image");
				      var filenameParts = filename.split(".");
				      entry.getData(new $wnd.zip.Data64URIWriter("image/"+filenameParts[filenameParts.length - 1]), function (data) {
				      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,data);
				      });
			      } else {
				      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(entry.filename+" : text");
				      if ($wnd.zip.useWebWorkers === false || (typeof $wnd.zip.forceDataURIWriter !== "undefined" && $wnd.zip.forceDataURIWriter === true)) {
					      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("no worker of forced dataURIWriter");
					      entry.getData(new $wnd.zip.Data64URIWriter("text/plain"), function(data) {
					      var decoded = $wnd.atob(data.substr(data.indexOf(",")+1));
					      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(decoded));
					      });
				      } else {
					      @org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("worker");
					      entry.getData(new ASCIIWriter(), function(text) {
					      view.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(filename,decodeUTF8(text));
					      });
				      }
	      
	      		}
		      })(entries[i]);
		      } 
	     // reader.close();
	      });
      };
      
      var errorCallback = function (error) {
      	view.@org.geogebra.web.html5.util.ViewW::onError(Ljava/lang/String;)(error);
      };
      
      $wnd.zip.createReader(ggbReader,readerCallback, errorCallback);
       
       
      }-*/;

	public void onError(String s) {
		Log.error(s);
		// eg 403
		if (s.startsWith("Error 40")) {
			this.app.getScriptManager().ggbOnInit();
			ToolTipManagerW.sharedInstance().showBottomMessage(
					app.getLocalization().getMenu("FileLoadingError"), false,
					app);
		}
	}

	public void processFileName(String url) {
		if (url.endsWith(".off")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost(url, "", new AjaxCallback() {

				@Override
				public void onSuccess(String response) {
					app.openOFF(response);

				}

				@Override
				public void onError(String error) {
					// TODO Auto-generated method stub

				}
			});
			return;

		}
		if (url.endsWith(".csv")) {

			HttpRequestW request = new HttpRequestW();
			request.sendRequestPost(url, "", new AjaxCallback() {

				@Override
				public void onSuccess(String response) {
					app.openCSV(response);

				}

				@Override
				public void onError(String error) {
					// TODO Auto-generated method stub

				}
			});
			return;

		}

		populateArchiveContent(getHTTPReader(url));
	}

	private native JavaScriptObject getHTTPReader(String url)/*-{
		return new $wnd.zip.HttpReader(url);
	}-*/;



	/**
	 * @param binary
	 *            string (zipped GGB)
	 */
	public void processBinaryString(JavaScriptObject binary) {
		populateArchiveContent(getBinaryReader(binary));

	}

	private native JavaScriptObject getBinaryReader(Object blob) /*-{
		return new $wnd.zip.BlobReader(blob);
	}-*/;

	private native JavaScriptObject getBase64Reader(String base64str)/*-{
		return new $wnd.zip.Data64URIReader(base64str);
	}-*/;

	private String prepareFileReading() {
		archiveContent = new HashMap<String, String>();
		String workerUrls = GgbAPIW.zipJSworkerURL();
		Log.debug("start unzipping" + System.currentTimeMillis());
		return workerUrls;
	}

	private void prepare(int t) {
		archiveContent = new HashMap<String, String>();
		this.zippedLength = t;
	}

	public boolean getDataParamApp() {
		return ((ArticleElement) container).getDataParamApp();
	}

	/**
	 * @param encoded
	 *            JSON encoded ZIP file (zip.js format)
	 */
	public native void processJSON(String encoded) /*-{

		var content = JSON.parse(encoded).archive;
		this.@org.geogebra.web.html5.util.ViewW::prepare(I)(content.length);
		for (var k = 0; k < content.length; k++) {
			this.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(content[k].fileName,content[k].fileContent);
		}

	}-*/;

	public native void processJSON(JavaScriptObject zip) /*-{
		var that = this;
		$wnd
				.setTimeout(
						function() {
							var content = zip.archive;
							that.@org.geogebra.web.html5.util.ViewW::prepare(I)(content.length);
							for (var k = 0; k < content.length; k++) {
								that.@org.geogebra.web.html5.util.ViewW::putIntoArchiveContent(Ljava/lang/String;Ljava/lang/String;)(content[k].fileName,content[k].fileContent);
							}
						}, 0);

	}-*/;

	public void adjustScale() {
		((ArticleElement) this.container).adjustScale();
	}

	public String getDataParamTubeID() {
		return ((ArticleElement) this.container).getDataParamTubeID();
	}
}
