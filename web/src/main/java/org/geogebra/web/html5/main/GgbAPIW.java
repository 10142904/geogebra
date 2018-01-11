package org.geogebra.web.html5.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.Assignment;
import org.geogebra.common.util.Assignment.Result;
import org.geogebra.common.util.Exercise;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.ViewW;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class GgbAPIW extends GgbAPI {

	public GgbAPIW(App app) {
		this.app = app;
		this.kernel = app.getKernel();
		this.algebraprocessor = kernel.getAlgebraProcessor();
		this.construction = kernel.getConstruction();
	}

	@Override
	public byte[] getGGBfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public Context2d getContext2D() {
		return ((AppW) app).getCanvas().getContext2d();
	}

	@Override
	public void setBase64(String base64) {
		resetPerspective();
		ViewW view = new ViewW(RootPanel.getBodyElement(), (AppW) app);
		view.processBase64String(base64);
	}

	private void resetPerspective() {
		((AppW) app).resetPerspectiveParam();
	}

	public void setBase64(String base64, final JavaScriptObject callback) {
		if (callback != null) {
			OpenFileListener listener = new OpenFileListener() {

				@Override
				public void onOpenFile() {
					ScriptManagerW.runCallback(callback);
					app.unregisterOpenFileListener(this);
				}
			};
			app.registerOpenFileListener(listener);
		}
		setBase64(base64);
	}

	public void openFile(String filename, final JavaScriptObject callback) {
		if (callback != null) {
			OpenFileListener listener = new OpenFileListener() {

				@Override
				public void onOpenFile() {
					ScriptManagerW.runCallback(callback);
					app.unregisterOpenFileListener(this);
				}
			};
			app.registerOpenFileListener(listener);
		}
		openFile(filename);
	}

	@Override
	public void setErrorDialogsActive(boolean flag) {
		app.setErrorDialogsActive(flag);
	}

	@Override
	public void refreshViews() {
		app.refreshViews();
	}

	@Override
	public void openFile(String filename) {
		resetPerspective();
		ViewW view = new ViewW(RootPanel.getBodyElement(), (AppW) app);
		view.processFileName(filename);
	}

	/**
	 * 
	 * @param exportScale
	 * @param transparent
	 * @param DPI
	 * @return png as String with "data:image/png;base64," header
	 */
	private String getPNG(double exportScale, boolean transparent, double DPI) {
		String url;

		EuclidianViewWInterface ev = ((EuclidianViewWInterface) app
				.getActiveEuclidianView());

		// get export image
		// DPI ignored
		url = ((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getExportImageDataUrl(exportScale, transparent);

		if (MyDouble.isFinite(DPI) && DPI > 0 && ev instanceof EuclidianViewW) {

			JavaScriptInjector
					.inject(GuiResourcesSimple.INSTANCE.rewritePHYS());

			url = addDPI(url, DPI);

		}

		return url;
	}

	@Override
	public boolean writePNGtoFile(String filename, double exportScale,
			boolean transparent, double DPI) {

		// make browser save/download PNG file
		Browser.exportImage(getPNG(exportScale, transparent, DPI), filename);

		return true;
	}

	@Override
	public String getPNGBase64(double exportScale, boolean transparent,
			double DPI, boolean copyToClipboard) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getLayout().getDockManager().ensureFocus();

			if (app.getGuiManager().getLayout().getDockManager()
					.getFocusedViewId() == App.VIEW_PROBABILITY_CALCULATOR) {
				return pngBase64(((EuclidianViewWInterface) app.getGuiManager()
						.getPlotPanelEuclidanView()).getExportImageDataUrl(
								exportScale, transparent));
			}
		}
		return pngBase64(getPNG(exportScale, transparent, DPI));
	}

	private static String pngBase64(String pngURL) {
		return pngURL.substring(StringUtil.pngMarker.length());
	}

	public String getLaTeXBase64(String label, boolean value) {
		Canvas c = Canvas.createIfSupported();
		GeoElement geo = kernel.lookupLabel(label);
		if (geo == null) {
			return "";
		}
		String str;
		if (value) {
			str = geo.toValueString(StringTemplate.latexTemplate);
		} else {
			str = geo instanceof GeoCasCell ? ((GeoCasCell) geo)
					.getLaTeXInput(StringTemplate.latexTemplate) : geo
					.toString(StringTemplate.latexTemplate);
		}
		DrawEquationW.paintOnCanvasOutput(geo, str, c, app.getFontSizeWeb());
		return c.toDataUrl().substring(StringUtil.pngMarker.length());
	}

	public void getGGB(final boolean includeThumbnail,
			final JavaScriptObject callback) {
		GgbFile archiveContent = createArchiveContent(includeThumbnail);
		final boolean oldWorkers = setWorkerURL(zipJSworkerURL(), false);
		final JavaScriptObject arch = prepareToEntrySet(archiveContent);
		getGGBZipJs(arch, callback, nativeCallback(new StringHandler() {
			@Override
			public void handle(String s) {
				if (oldWorkers && !isUsingWebWorkers()) {
					Log.warn(
							"Saving with workers failed, trying without workers.");
					JavaScriptInjector
							.inject(GuiResourcesSimple.INSTANCE.deflateJs());
					getGGBZipJs(arch, callback, null);
				}

			}
		}));

	}

	/**
	 * @return URL of zip worker directory
	 */
	public static String zipJSworkerURL() {
		// FIXME disabled workers in Touch for now
		if ("tablet".equals(GWT.getModuleName())
				|| "tabletWin".equals(GWT.getModuleName())) {
			return "false";
		}
		return Browser.webWorkerSupported() ? GWT.getModuleBaseURL()
				+ "js/zipjs/" : "false";
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getBase64(boolean includeThumbnail, JavaScriptObject callback) {
		GgbFile archiveContent = createArchiveContent(includeThumbnail);

		getBase64ZipJs(prepareToEntrySet(archiveContent), callback,
				zipJSworkerURL(), false);
	}

	public void getMacrosBase64(boolean includeThumbnail,
			JavaScriptObject callback) {
		GgbFile archiveContent = createMacrosArchive();

		getBase64ZipJs(prepareToEntrySet(archiveContent), callback,
				zipJSworkerURL(), false);
	}

	public JavaScriptObject getFileJSON(boolean includeThumbnail) {
		GgbFile archiveContent = createArchiveContent(includeThumbnail);

		return prepareToEntrySet(archiveContent);
	}

	public void setFileJSON(JavaScriptObject obj) {
		resetPerspective();
		ViewW view = new ViewW(RootPanel.getBodyElement(), (AppW) app);
		view.processJSON(obj);
	}

	private static class StoreString implements StringHandler {
		private String result = "";

		protected StoreString() {

		}

		@Override
		public void handle(String s) {
			this.result = s;
		}

		public String getResult() {
			return result;
		}
	}

	@Override
	public String getBase64(boolean includeThumbnail) {
		StoreString storeString = new StoreString();
		GgbFile archiveContent = createArchiveContent(includeThumbnail);
		JavaScriptObject jso = prepareToEntrySet(archiveContent);
		if (Browser.webWorkerSupported()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
		}
		getBase64ZipJs(jso, nativeCallback(storeString), "false", true);
		return storeString.getResult();

	}

	public String getMacrosBase64() {
		StoreString storeString = new StoreString();
		Map<String, String> archiveContent = createMacrosArchive();
		JavaScriptObject jso = prepareToEntrySet(archiveContent);
		if (Browser.webWorkerSupported()) {
			JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.deflateJs());
		}
		getBase64ZipJs(jso, nativeCallback(storeString), "false", true);
		return storeString.getResult();

	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getBase64(boolean includeThumbnail, StringHandler callback) {
		getBase64(includeThumbnail, nativeCallback(callback));
	}

	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @param callback
	 *            callback
	 */
	public void getMacrosBase64(boolean includeThumbnail, StringHandler callback) {
		getMacrosBase64(includeThumbnail, nativeCallback(callback));
	}

	private native JavaScriptObject nativeCallback(StringHandler callback) /*-{
		return function(b) {
			callback.@org.geogebra.web.html5.main.StringHandler::handle(Ljava/lang/String;)(b);
		};
	}-*/;

	private native String addDPI(String base64, double dpi) /*-{

		var pngHeader = "data:image/png;base64,";

		if (base64.startsWith(pngHeader)) {
			base64 = base64.substr(pngHeader.length);
		}

		// convert dots per inch into dots per metre
		var pixelsPerM = dpi * 100 / 2.54;

		//console.log("base64 = " + base64);

		// encode PNG as Uint8Array
		var binary_string = window.atob(base64);
		var len = binary_string.length;
		//console.log("len = " + len);
		var bytes = new Uint8Array(len);
		for (var i = 0; i < len; i++) {
			bytes[i] = binary_string.charCodeAt(i);
		}

		// change / add pHYs chunk 
		// pixels per metre
		var ppm = Math.round(dpi / 2.54 * 100);
		bytes = $wnd.rewrite_pHYs_chunk(bytes, ppm, ppm);

		// re-encode PNG (btoa method)
		var b64encoded = btoa(String.fromCharCode.apply(null, bytes));
		//console.log("b64encoded = " + b64encoded);

		return 'data:image/png;base64,' + b64encoded;

	}-*/;


	/**
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @return
	 */
	public GgbFile createArchiveContent(boolean includeThumbnail) {
		GgbFile archiveContent = new GgbFile();
		boolean isSaving = getKernel().isSaving();
		// return getNativeBase64(includeThumbnail);
		getKernel().setSaving(true);
		adjustConstructionImages(getConstruction());
		String constructionXml = getApplication().getXML();
		String macroXml = getApplication().getMacroXMLorEmpty();
		StringBuilder defaults2d = new StringBuilder();
		StringBuilder defaults3d = null;
		if (app.is3D()) {
			defaults3d = new StringBuilder();
		}
		getKernel().getConstruction().getConstructionDefaults()
				.getDefaultsXML(defaults2d, defaults3d);
		String geogebra_javascript = getKernel().getLibraryJavaScript();

		if (!"".equals(macroXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}

		if (defaults2d.length() > 0) {
			archiveContent.put(MyXMLio.XML_FILE_DEFAULTS_2D,
					defaults2d.toString());
		}

		if (defaults3d != null && defaults3d.length() > 0) {
			archiveContent.put(MyXMLio.XML_FILE_DEFAULTS_3D,
					defaults3d.toString());
		}

		if (!StringUtil.emptyTrim(geogebra_javascript)) {
			archiveContent.put(MyXMLio.JAVASCRIPT_FILE, geogebra_javascript);
		}

		archiveContent.put(MyXMLio.XML_FILE, constructionXml);

		// GGB-1758 write images at the end
		writeConstructionImages(getConstruction(), "", archiveContent);

		// write construction thumbnails
		if (includeThumbnail) {
			addImageToZip(MyXMLio.XML_FILE_THUMBNAIL,
					getViewForThumbnail().getCanvasBase64WithTypeString(),
					archiveContent);
		}

		getKernel().setSaving(isSaving);
		return archiveContent;
	}

	private EuclidianViewWInterface getViewForThumbnail() {
		if (app.isEuclidianView3Dinited()
				&& app.showView(App.VIEW_EUCLIDIAN3D)) {
			return (EuclidianViewWInterface) app.getEuclidianView3D();
		}
		return ((EuclidianViewWInterface) app.getActiveEuclidianView());

	}

	/**
	 * @return archive with macros + icons
	 */
	public GgbFile createMacrosArchive() {
		GgbFile archiveContent = new GgbFile();
		writeMacroImages(archiveContent);
		String macroXml = getApplication().getMacroXMLorEmpty();
		if (!"".equals(macroXml)) {
			writeMacroImages(archiveContent);
			archiveContent.put(MyXMLio.XML_FILE_MACRO, macroXml);
		}
		return archiveContent;
	}

	private JavaScriptObject prepareToEntrySet(Map<String, String> archive) {
		JavaScriptObject nativeEntry = JavaScriptObject.createObject();

		if (archive.entrySet() != null) {
			for (Entry<String, String> entry : archive.entrySet()) {
				pushIntoNativeEntry(entry.getKey(), entry.getValue(),
						nativeEntry);
			}
		}
		return nativeEntry;
	}

	private native void pushIntoNativeEntry(String key, String value,
			JavaScriptObject ne) /*-{
		if (typeof ne["archive"] === "undefined") { //needed because gwt gives an __objectId key :-(
			ne["archive"] = [];
		}
		var obj = {};
		obj.fileName = key;
		obj.fileContent = value;
		ne["archive"].push(obj);
	}-*/;

	private native void getGGBZipJs(JavaScriptObject arch,
			JavaScriptObject clb, JavaScriptObject errorClb) /*-{

		function encodeUTF8(string) {
			var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
			for (n = 0; n < stringl; n++) {
				c1 = string.charCodeAt(n);
				enc = null;
				if (c1 < 128)
					end++;
				else if (c1 > 127 && c1 < 2048)
					enc = String.fromCharCode((c1 >> 6) | 192)
							+ String.fromCharCode((c1 & 63) | 128);
				else
					enc = String.fromCharCode((c1 >> 12) | 224)
							+ String.fromCharCode(((c1 >> 6) & 63) | 128)
							+ String.fromCharCode((c1 & 63) | 128);
				if (enc != null) {
					if (end > start)
						utftext += string.slice(start, end);
					utftext += enc;
					start = end = n + 1;
				}
			}
			if (end > start)
				utftext += string.slice(start, stringl);
			return utftext;
		}

		function ASCIIReader(text) {
			var that = this;

			function init(callback, onerror) {
				that.size = text.length;
				callback();
			}

			function readUint8Array(index, length, callback, onerror) {
				if (text.length <= index) {
					return new $wnd.Uint8Array(0);
				} else if (index < 0) {
					return new $wnd.Uint8Array(0);
				} else if (length <= 0) {
					return new $wnd.Uint8Array(0);
				} else if (text.length < index + length) {
					length = text.length - index;
				}
				var i, data = new $wnd.Uint8Array(length);
				for (i = index; i < index + length; i++)
					data[i - index] = text.charCodeAt(i);
				callback(data);
			}

			that.size = 0;
			that.init = init;
			that.readUint8Array = readUint8Array;
		}
		ASCIIReader.prototype = new $wnd.zip.Reader();
		ASCIIReader.prototype.constructor = ASCIIReader;

		$wnd.zip
				.createWriter(
						new $wnd.zip.BlobWriter(),
						function(zipWriter) {

							function addImage(name, data, callback) {
								var data2 = data.substr(data.indexOf(',') + 1);
								zipWriter.add(name,
										new $wnd.zip.Data64URIReader(data2),
										callback);
							}

							function addText(name, data, callback) {
								zipWriter.add(name, new ASCIIReader(data),
										callback);
							}

							function checkIfStillFilesToAdd() {
								var item, imgExtensions = [ "jpg", "jpeg",
										"png", "gif", "bmp" ];
								if (arch.archive.length > 0) {
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("arch.archive.length: "+arch.archive.length);
									item = arch.archive.shift();
									var ind = item.fileName.lastIndexOf('.');
									if (ind > -1
											&& imgExtensions
													.indexOf(item.fileName
															.substr(ind + 1)
															.toLowerCase()) > -1) {
										//if (item.fileName.indexOf(".png") > -1) 
										//@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("image zipped: " + item.fileName);
										addImage(item.fileName,
												item.fileContent, function() {
													checkIfStillFilesToAdd();
												});
									} else {
										//@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("text zipped: " + item.fileName);
										addText(item.fileName,
												encodeUTF8(item.fileContent),
												function() {
													checkIfStillFilesToAdd();
												});
									}
								} else {
									zipWriter
											.close(function(dataURI) {
												if (typeof clb === "function") {
													clb(dataURI);
													// that's right, this truncation is necessary
													//clb(dataURI.substr(dataURI.indexOf(',')+1));
												} else {
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("not callback was given");
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(dataURI);
												}
											});
								}
							}

							checkIfStillFilesToAdd();

						},
						function(error) {
							if (typeof errorClb === "function") {
								errorClb(error + "");
							}
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while creating ggb zip");
						});

	}-*/;

	void getBase64ZipJs(final JavaScriptObject arch, final JavaScriptObject clb,
			String workerUrls, boolean sync) {
		final boolean oldWorkers = setWorkerURL(workerUrls, sync);
		getBase64ZipJs(arch, clb, nativeCallback(new StringHandler() {
			@Override
			public void handle(String s) {
				if (oldWorkers && !isUsingWebWorkers()) {
					Log.warn(
							"Saving with workers failed, trying without workers.");
					JavaScriptInjector
							.inject(GuiResourcesSimple.INSTANCE.deflateJs());
					getBase64ZipJs(arch, clb, "false", false);
				}

			}
		}));
	}


	private native void getBase64ZipJs(JavaScriptObject arch,
			JavaScriptObject clb, JavaScriptObject errorClb) /*-{

		function encodeUTF8(string) {
			var n, c1, enc, utftext = [], start = 0, end = 0, stringl = string.length;
			for (n = 0; n < stringl; n++) {
				c1 = string.charCodeAt(n);
				enc = null;
				if (c1 < 128)
					end++;
				else if (c1 > 127 && c1 < 2048)
					enc = String.fromCharCode((c1 >> 6) | 192)
							+ String.fromCharCode((c1 & 63) | 128);
				else
					enc = String.fromCharCode((c1 >> 12) | 224)
							+ String.fromCharCode(((c1 >> 6) & 63) | 128)
							+ String.fromCharCode((c1 & 63) | 128);
				if (enc != null) {
					if (end > start)
						utftext += string.slice(start, end);
					utftext += enc;
					start = end = n + 1;
				}
			}
			if (end > start)
				utftext += string.slice(start, stringl);
			return utftext;
		}

		function ASCIIReader(text) {
			var that = this;

			function init(callback, onerror) {
				that.size = text.length;
				callback();
			}

			function readUint8Array(index, length, callback, onerror) {
				if (text.length <= index) {
					return new $wnd.Uint8Array(0);
				} else if (index < 0) {
					return new $wnd.Uint8Array(0);
				} else if (length <= 0) {
					return new $wnd.Uint8Array(0);
				} else if (text.length < index + length) {
					length = text.length - index;
				}
				var i, data = new $wnd.Uint8Array(length);
				for (i = index; i < index + length; i++)
					data[i - index] = text.charCodeAt(i);
				callback(data);
			}

			that.size = 0;
			that.init = init;
			that.readUint8Array = readUint8Array;
		}
		ASCIIReader.prototype = new $wnd.zip.Reader();
		ASCIIReader.prototype.constructor = ASCIIReader;

		//$wnd.zip.useWebWorkers = false;
		$wnd.zip
				.createWriter(
						new $wnd.zip.Data64URIWriter(
								"application/vnd.geogebra.file"),
						function(zipWriter) {
							function addImage(name, data, callback) {
								var data2 = data.substr(data.indexOf(',') + 1);
								zipWriter.add(name,
										new $wnd.zip.Data64URIReader(data2),
										callback);
							}

							function addText(name, data, callback) {
								zipWriter.add(name, new ASCIIReader(data),
										callback);
							}

							function checkIfStillFilesToAdd() {
								var item, imgExtensions = [ "jpg", "jpeg",
										"png", "gif", "bmp" ];
								if (arch.archive.length > 0) {
									item = arch.archive.shift();
									var ind = item.fileName.lastIndexOf('.');
									if (ind > -1
											&& imgExtensions
													.indexOf(item.fileName
															.substr(ind + 1)
															.toLowerCase()) > -1) {

										@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("image zipped: " + item.fileName);
										addImage(item.fileName,
												item.fileContent, function() {
													checkIfStillFilesToAdd();
												});
									} else {
										@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("text zipped: " + item.fileName);
										addText(item.fileName,
												encodeUTF8(item.fileContent),
												function() {
													checkIfStillFilesToAdd();
												});
									}
								} else {
									zipWriter
											.close(function(dataURI) {
												if (typeof clb === "function") {
													// that's right, this truncation is necessary
													clb(dataURI.substr(dataURI
															.indexOf(',') + 1));
												} else {
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("not callback was given");
													@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)(dataURI);
												}
											});
								}
							}

							checkIfStillFilesToAdd();

						},
						function(error) {
							@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("error occured while creating base64 zip");
							if (typeof errorClb === "function") {
								errorClb(error + "");
							}
						});
	}-*/;

	private void writeMacroImages(Map<String, String> archive) {
		if (kernel.hasMacros()) {
			ArrayList<Macro> macros = kernel.getAllMacros();
			writeMacroImages(macros, archive);
		}
	}

	private void writeMacroImages(ArrayList<Macro> macros,
			Map<String, String> archive) {
		if (macros == null) {
			return;
		}

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = macros.get(i);
			// macro may contain images GGB-1865
			writeConstructionImages(macro.getMacroConstruction(), "",
					archive);
			String fileName = macro.getIconFileName();
			if (fileName != null && !fileName.isEmpty()) {
				String url = ((ImageManagerW) app.getImageManager())
						.getExternalImageSrc(fileName);
				if (url != null) {
					FileExtensions ext = StringUtil.getFileExtension(fileName);

					MyImageW img = new MyImageW(
							ImageElement.as((new Image(url)).getElement()),
							FileExtensions.SVG.equals(ext));

					addImageToArchive("", fileName, url, ext, img, archive);
				}
			}
			/*
			 * // save macro icon String fileName = macro.getIconFileName();
			 * BufferedImage img =
			 * ((Application)app).getExternalImage(fileName); if (img != null)
			 * // Modified for Intergeo File Format (Yves Kreis) --> //
			 * writeImageToZip(zip, fileName, img); writeImageToZip(zipjs,
			 * filePath + fileName, img); // <-- Modified for Intergeo File
			 * Format (Yves Kreis)
			 */
		}
	}

	private void adjustConstructionImages(Construction cons) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			String fileName = geo.getImageFileName();
			// for some reason we sometimes get null and sometimes "" if there
			// is no image used
			if (fileName != null && fileName.length() > 0) {
				geo.getGraphicsAdapter().convertToSaveableFormat();
				String newName = geo.getGraphicsAdapter().getImageFileName();
				((ImageManagerW) app.getImageManager()).replace(fileName,
						newName);
			}
		}
	}

	private void writeConstructionImages(Construction cons, String filePath,
			Map<String, String> archive) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			String fileName = geo.getImageFileName();
			if (!"".equals(fileName)) {
				String url = ((ImageManagerW) app.getImageManager())
						.getExternalImageSrc(fileName);
				FileExtensions ext = StringUtil.getFileExtension(fileName);

				MyImageW img = (MyImageW) geo.getFillImage();

				Log.debug("filename = " + fileName);
				Log.debug("ext = " + ext);
				addImageToArchive(filePath, fileName, url, ext, img, archive);
			}
		}
	}

	private static void addImageToArchive(String filePath, String fileName,
			String url, FileExtensions ext, MyImageW img,
			Map<String, String> archive) {
		if (ext.equals(FileExtensions.SVG)) {
			addSvgToArchive(fileName, img, archive);
			return;
		}
		String dataURL;
		if ((url == null || url.startsWith("http"))
				&& (img != null && img.getImage() != null)) {
			dataURL = convertImgToPng(img);
		} else {
			dataURL = url;
		}
		if (dataURL != null) {
			if (ext.isAllowedImage()) {
				// png, jpg, jpeg
				// NOT SVG (filtered earlier)
				addImageToZip(filePath + fileName, dataURL, archive);
			} else {
				// not supported, so saved as PNG
				addImageToZip(
						filePath
								+ StringUtil.changeFileExtension(fileName,
										FileExtensions.PNG), dataURL, archive);
			}
		}
	}

	private static String convertImgToPng(MyImageW img) {
		String url;
		Canvas cv = Canvas.createIfSupported();
		cv.setCoordinateSpaceWidth(img.getWidth());
		cv.setCoordinateSpaceHeight(img.getHeight());
		Context2d c2d = cv.getContext2d();
		c2d.drawImage(img.getImage(), 0, 0);
		url = cv.toDataUrl("image/png");
		// Opera and Safari cannot toDataUrl jpeg (much less the others)
		// if ("jpg".equals(ext) || "jpeg".equals(ext))
		// addImageToZip(filePath + fileName, cv.toDataUrl("image/jpg"));
		// else
		return url;
	}

	private static void addSvgToArchive(String fileName, MyImageW img,
			Map<String, String> archive) {
		ImageElement svg = img.getImage();

		// TODO
		// String svgAsXML =
		// "<svg width=\"100\" height=\"100\"> <circle cx=\"50\" cy=\"50\" r=\"40\" stroke=\"green\" stroke-width=\"4\" fill=\"yellow\" /></svg>";
		String svgAsXML = svg.getAttribute("src");

		// remove eg data:image/svg+xml;base64,
		int index = svgAsXML.indexOf(',');
		svgAsXML = svgAsXML.substring(index + 1);

		svgAsXML = Browser.decodeBase64(svgAsXML);

		Log.debug("svgAsXML (decoded): " + svgAsXML.length() + "bytes");

		archive.put(fileName, svgAsXML);
	}

	private static void addImageToZip(String filename, String base64img,
			Map<String, String> archive) {
		archive.put(filename, base64img);
	}

	/**
	 * @param material
	 *            material ID
	 */
	public void openMaterial(final String material) {
		((AppW) app).openMaterial(material, new Runnable() {

			@Override
			public void run() {
				Log.debug("Loading failed for id" + material);

			}
		});
	}

	/**
	 * @param width
	 *            setst the applet width
	 */
	public void setWidth(int width) {
		setArticleParam("width", width);
		((AppW) app).getAppletFrame().setWidth(width);
	}

	/**
	 * @param height
	 *            sets the applet height
	 */
	public void setHeight(int height) {
		setArticleParam("height", height);
		((AppW) app).getAppletFrame().setHeight(height);
	}

	/**
	 * @param width
	 *            height
	 * @param height
	 *            width
	 * 
	 *            Sets the size of the applet
	 */
	public void setSize(int width, int height) {
		setArticleParam("width", width);
		setArticleParam("height", height);
		((AppW) app).getAppletFrame().setSize(width, height);
	}



	private void setArticleParam(String name, int value) {
		((AppW) app).getArticleElement().setAttribute("data-param-" + name,
				value + "");
		
	}

	/**
	 * @param show
	 * 
	 *            wheter show the toolbar in geogebra-web applets or not
	 */
	public void showToolBar(boolean show) {
		((AppW) app).getAppletFrame().showToolBar(show);
	}

	/**
	 * @param show
	 * 
	 *            wheter show the menubar in geogebra-web applets or not
	 */
	public void showMenuBar(boolean show) {
		if (app.getGuiManager() != null) {
			((GuiManagerInterfaceW) app.getGuiManager()).showMenuBar(show);
		}
	}

	/**
	 * @param show
	 * 
	 *            wheter show the algebrainput in geogebra-web applets or not
	 */
	public void showAlgebraInput(boolean show) {
		((AppW) app).getAppletFrame().showAlgebraInput(show);
	}

	/**
	 * @param show
	 * 
	 *            wheter show the reseticon in geogebra-web applets or not
	 */
	public void showResetIcon(boolean show) {
		((AppW) app).getAppletFrame().showResetIcon(show);
	}

	public void insertImage(String s) {
		((AppW) app).urlDropHappened(s, 0, 0);
	}

	/**
	 * recalculates euclidianviews environments
	 */
	public void recalculateEnvironments() {
		((AppW) app).recalculateEnvironments();
	}

	/**
	 * remove applet from the page, and free memory. If applet is the last one,
	 * it remove the style elements injected by the applet too.
	 */
	public void removeApplet() {
		((AppW) app).getAppletFrame().remove();
	}

	@Override
	public void showTooltip(String tooltip) {
		ToolTipManagerW.sharedInstance().showBottomMessage(tooltip, false,
				(AppW) app);
	}

	/**
	 * If there are Macros or an Exercise present in the current file this can
	 * be used to check if parts of the construction are equivalent to the
	 * Macros in the file. <br />
	 * If you don't want that a Standard Exercise (using all the Macros in the
	 * Construction and setting each fraction to 100) will be created, check if
	 * this is a Exercise with {@link #isExercise()} first. <br>
	 * Hint will be empty unless specified otherwise with the ExerciseBuilder. <br />
	 * Fraction will be 0 or 1 unless specified otherwise with the
	 * ExerciseBuilder. <br />
	 * Result will be in {@link Result},i.e: <br />
	 * CORRECT, The assignment is CORRECT <br />
	 * WRONG, if the assignment is WRONG and we can't tell why <br />
	 * NOT_ENOUGH_INPUTS if there are not enough input geos, so we cannot check <br />
	 * WRONG_INPUT_TYPES, if there are enough input geos, but one or more are of
	 * the wrong type <br />
	 * WRONG_OUTPUT_TYPE, if there is no output geo matching our macro <br />
	 * WRONG_AFTER_RANDOMIZE, if the assignment was correct in the first place
	 * but wrong after randomization <br />
	 * UNKNOWN, if the assignment could not be checked
	 * 
	 * @return JavaScriptObject representation of the exercise result. For
	 *         Example: "{"Tool1":{ "result":"CORRECT", "hint":"",
	 *         "fraction":1}}", will be empty if now Macros or Assignments have
	 *         been found.
	 */
	@Override
	public JavaScriptObject getExerciseResult() {
		Exercise ex = kernel.getExercise();
		ex.checkExercise();
		JSONObject result = new JSONObject();
		ArrayList<Assignment> parts = ex.getParts();
		for (Assignment part : parts) {
			JSONObject partresult = new JSONObject();
			result.put(part.getDisplayName(), partresult);
			partresult.put("result", new JSONString(part.getResult().name()));
			String hint = part.getHint();
			hint = hint == null ? "" : hint;
			partresult.put("hint", new JSONString(hint));
			partresult.put("fraction", new JSONNumber(part.getFraction()));
		}
		return result.getJavaScriptObject();
	}

	/**
	 * If you want to make use of the values of random geo a BoolAssignment
	 * depends on, this is an easy way to retrieve these values and stop
	 * randomizing them in order to store the same assignment that was presented
	 * to the student.
	 * 
	 * @return JavaScriptObject containing all variables and values of which a
	 *         BoolAssignment is depending and stops randomizing all these
	 *         values. Example:
	 *         "Object {level: 1, randNum: 5, a: 1, b: 1, answer: NaN}"
	 */
	public JavaScriptObject startExercise() {
		ArrayList<GeoNumeric> randomizedVars = app.getKernel().getExercise()
				.stopRandomizeAndGetValuesForBoolAssignments();
		JSONObject vars = new JSONObject();

		for (GeoNumeric geo : randomizedVars) {
			JSONNumber var = new JSONNumber(geo.getDouble());
			vars.put(geo.getLabelSimple(), var);
			geo.setRandom(false);
		}

		return vars.getJavaScriptObject();
	}

	public void setExternalPath(String s) {
		((AppW) app).setExternalPath(s);
	}

	public void checkSaved(final JavaScriptObject callback) {
		((AppW) app).checkSaved(new Runnable() {
			@Override
			public void run() {
				ScriptManagerW.runCallback(callback);
			}
		});
	}

	public void setCustomToolBar(String toolbarString) {
		GuiManagerInterfaceW gm = ((GuiManagerInterfaceW) app.getGuiManager());
		gm.setToolBarDefinition(toolbarString);
		gm.setGeneralToolBarDefinition(toolbarString);
		gm.updateToolbar();
	}

	public void getScreenshotBase64(JavaScriptObject callback) {
		getScreenshotURL(((AppW) app).getPanel().getElement(), callback);
	}

	public native void getScreenshotURL(Element el, JavaScriptObject callback)/*-{
		var canvas = document.createElement("canvas");
		canvas.height = el.offsetHeight;
		canvas.width = el.offsetWidth;
		var context = canvas.getContext('2d');
		el.className = el.className + " ggbScreenshot";
		$wnd.domvas.toImage(el, function() {
			// Look ma, I just converted this element to an image and can now to funky stuff!
			context.drawImage(this, 0, 0);
			el.className = el.className.replace(/\bggbScreenshot\b/, '');
			callback(@org.geogebra.web.html5.main.GgbAPIW::pngBase64(Ljava/lang/String;)(canvas.toDataURL()));
		});
	}-*/;

	public static native boolean setWorkerURL(String workerUrls,
			boolean sync) /*-{
		if (workerUrls === "false" || !workerUrls || sync) {
			$wnd.zip.useWebWorkers = false;
			$wnd.zip.synchronous = sync;
		} else {
			$wnd.zip.synchronous = false;
			$wnd.zip.useWebWorkers = true;
			$wnd.zip.workerScriptsPath = workerUrls;
		}
		return $wnd.zip.useWebWorkers;
	}-*/;

	native boolean isUsingWebWorkers()/*-{
		return $wnd.zip.useWebWorkers;
	}-*/;

	/**
	 * GGB-1780
	 * 
	 * @return current construction as SVG
	 */
	final public String exportSVG(String filename) {
		EuclidianView ev = app.getActiveEuclidianView();

		if (ev instanceof EuclidianViewW) {
			EuclidianViewW evw = (EuclidianViewW) ev;

			String svg = evw.getExportSVG(1, true);

			if (filename != null) {
				// can't use data:image/svg+xml;utf8 in IE11 / Edge
				Browser.exportImage(Browser.encodeSVG(svg), filename);
			}

			return svg;
		}

		return null;

	}

	/**
	 * Experimental GGB-2150
	 * 
	 */
	final public String exportPDF(double scale, String filename) {
		EuclidianView ev = app.getActiveEuclidianView();

		if (ev instanceof EuclidianViewW) {
			EuclidianViewW evw = (EuclidianViewW) ev;

			String pdf = evw.getExportPDF(scale);

			if (filename != null) {
				Browser.exportImage(pdf, filename);
			}

			return pdf;

		}

		return null;


	}

}
