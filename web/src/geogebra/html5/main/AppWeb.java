package geogebra.html5.main;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.factories.CASFactory;
import geogebra.common.factories.SwingFactory;
import geogebra.common.gui.SetLabels;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.AnimationManager;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.UndoManager;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.barycentric.AlgoCubicSwitch;
import geogebra.common.kernel.barycentric.AlgoKimberlingWeights;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.AlgoCubicSwitchInterface;
import geogebra.common.main.AlgoCubicSwitchParams;
import geogebra.common.main.AlgoKimberlingWeightsInterface;
import geogebra.common.main.AlgoKimberlingWeightsParams;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.move.events.BaseEventPool;
import geogebra.common.move.events.NativeEventAttacher;
import geogebra.common.move.operations.Network;
import geogebra.common.move.operations.NetworkOperation;
import geogebra.common.move.views.OfflineView;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.Language;
import geogebra.common.util.MD5EncrypterGWTImpl;
import geogebra.common.util.NormalizerMinimal;
import geogebra.common.util.debug.Log;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.io.ConstructionException;
import geogebra.html5.io.MyXMLioW;
import geogebra.html5.kernel.AnimationManagerW;
import geogebra.html5.kernel.UndoManagerW;
import geogebra.html5.kernel.commands.CommandDispatcherW;
import geogebra.html5.sound.SoundManagerW;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.ImageManager;
import geogebra.html5.util.ScriptLoadCallback;
import geogebra.html5.util.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

public abstract class AppWeb extends App implements SetLabels{
	
	public static final String DEFAULT_APPLET_ID = "ggbApplet";
	private DrawEquationWeb drawEquation;
	private SoundManager soundManager;
	private NormalizerMinimal normalizerMinimal;
	private GgbAPI ggbapi;
	private final LocalizationW loc;
	private ImageManager imageManager;
	private HashMap<String, String> currentFile = null;
	private LinkedList<Map<String, String>> fileList = new LinkedList<Map<String, String>>();
	// random id to identify ggb files
	// eg so that GeoGebraTube can notice it's a version of the same file
	private String uniqueId = null;// FIXME: generate new UUID: +
	                               // UUID.randomUUID();

	protected AppWeb(){
		loc = new LocalizationW();
	}
	
	@Override
	public final String getUniqueId() {
		return uniqueId;
	}

	@Override
	public final void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public final void resetUniqueId() {
		uniqueId = null;// FIXME: generate new UUID: + UUID.randomUUID();
	}
	
	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb();
		}

		return drawEquation;
	}
	
	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}
	
	@Override
	public geogebra.html5.main.GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new geogebra.html5.main.GgbAPI(this);
		}
		return ggbapi;
	}
	
	public abstract Canvas getCanvas();
	
	@Override
	public final StringType getPreferredFormulaRenderingType() {
		return StringType.LATEX;
	}
	
	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}
	
	@Override
    public final SwingFactory getSwingFactory() {
	    return SwingFactory.getPrototype();
    }
	
	protected static void initFactories()
	{
		geogebra.common.factories.FormatFactory.prototype = new geogebra.html5.factories.FormatFactoryW();
		geogebra.common.factories.AwtFactory.prototype = new geogebra.html5.factories.AwtFactoryW();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.html5.euclidian.EuclidianStaticW();
		geogebra.common.factories.SwingFactory.setPrototype(new geogebra.html5.factories.SwingFactoryW());
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();
		geogebra.common.factories.CASFactory.setPrototype(new geogebra.html5.factories.CASFactoryW());

	}
	
	private GlobalKeyDispatcherW globalKeyDispatcher;

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}
	
	@Override
	public EuclidianViewWeb getEuclidianView1() {
		return (EuclidianViewWeb) euclidianView;
	}
	private TimerSystemW timers;
	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	public abstract void showMessage(String error);
	
	public abstract ViewManager getViewManager();

	public void syncAppletPanelSize(int width, int height, int evNo) {
	    // TODO Auto-generated method stub
	    
    }
	@Override
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerW(this);
		}
		return scriptManager;
	}
	
	// ================================================
		// NATIVE JS
		// ================================================

		

		public native void evalScriptNative(String script) /*-{
			$wnd.eval(script);
		}-*/;

		public native void callNativeJavaScript(String funcname) /*-{
			if ($wnd[funcname]) {
				$wnd[funcname]();
			}
		}-*/;

		public native void callNativeJavaScript(String funcname, String arg) /*-{
			if ($wnd[funcname]) {
				$wnd[funcname](arg);
			}
		}-*/;
		
		public native void callNativeJavaScriptMultiArg(String funcname, JavaScriptObject arg) /*-{
			if ($wnd[funcname]) {
				$wnd[funcname](arg);
			}
		}-*/;

		public static native void ggbOnInit() /*-{
			if (typeof $wnd.ggbOnInit === 'function')
				$wnd.ggbOnInit();
		}-*/;

		public static native void ggbOnInit(String arg) /*-{
			if (typeof $wnd.ggbOnInit === 'function')
				$wnd.ggbOnInit(arg);
		}-*/;
		
		@Override
		public void callAppletJavaScript(String fun, Object[] args) {
			if (args == null || args.length == 0) {
				callNativeJavaScript(fun);
			} else if (args.length == 1) {
				App.debug("calling function: " + fun + "(" + args[0].toString()
				        + ")");
				callNativeJavaScript(fun, args[0].toString());
			} else {
				JsArrayString jsStrings = (JsArrayString)JsArrayString.createArray();
				for( Object obj : args ){
					jsStrings.push( obj.toString() );
				}
				callNativeJavaScriptMultiArg(fun, jsStrings);
			}

		}

		public String getDataParamId() {
	        return getAppletId();
        }
		
		private MyXMLioW xmlio;

		@Override
		public boolean loadXML(String xml) throws Exception {
			getXMLio().processXMLString(xml, true, false);
			return true;
		}

		@Override
		public MyXMLioW getXMLio() {
			if (xmlio == null) {
				xmlio = createXMLio(kernel.getConstruction());
			}
			return xmlio;
		}

		@Override
		public MyXMLioW createXMLio(Construction cons) {
			return new MyXMLioW(cons.getKernel(), cons);
		}
		
		public void setLanguage(final String browserLang) {
			if (browserLang != null && browserLang.equals(loc.getLanguage())) {
				setLabels();
				return;
			}

			if (browserLang == null || "".equals(browserLang)) {

				App.error("language being set to empty string");
				setLanguage("en");
				return;
			}
			final String lang = Language.getClosestGWTSupportedLanguage(browserLang);
			App.debug("setting language to:" + lang + ", browser lang" + browserLang);

			// load keys (into a JavaScript <script> tag)
			DynamicScriptElement script = (DynamicScriptElement) Document.get()
			        .createScriptElement();
			script.setSrc(GWT.getModuleBaseURL() + "js/properties_keys_" + lang
			        + ".js");
			script.addLoadHandler(new ScriptLoadCallback() {

				public void onLoad() {
					// force reload
					resetCommandDictionary();

					((LocalizationW)getLocalization()).setLanguage(lang);

					// make sure digits are updated in all numbers
					getKernel().updateConstruction();

					// update display & Input Bar Dictionary etc
					setLabels();

					// inputField.setDictionary(getCommandDictionary());

				}

				
			});
			Document.get().getBody().appendChild(script);
		}
		
		protected abstract void resetCommandDictionary();

		public void setLanguage(String language, String country) {

			if (language == null || "".equals(language)) {
				Log.warn("error calling setLanguage(), setting to English (US): "
				        + language + "_" + country);
				setLanguage("en");
				return;
			}

			if (country == null || "".equals(country)) {
				setLanguage(language);
				return;
			}
			this.setLanguage(language + "_" + country);
		}
		
		@Override
		public Localization getLocalization() {
			return loc;
		}
		
		/**
		 * This method checks if the command is stored in the command properties
		 * file as a key or a value.
		 * 
		 * @param command
		 *            : a value that should be in the command properties files (part
		 *            of Internationalization)
		 * @return the value "command" after verifying its existence.
		 */
		@Override
		final public String getReverseCommand(String command) {

			if (loc.getLanguage() == null) {
				// keys not loaded yet
				return command;
			}

			return super.getReverseCommand(command);
		}
		
		public String getEnglishCommand(String pageName) {
			loc.initCommand();
			// String ret = commandConstants
			// .getString(crossReferencingPropertiesKeys(pageName));
			// if (ret != null)
			// return ret;
			return pageName;
		}

		public abstract void appSplashCanNowHide();

		public abstract String getLanguageFromCookie();

		public abstract void showLoadingAnimation(boolean b);
		
		public void loadGgbFile(HashMap<String, String> archiveContent)
		        throws Exception {
			loadFile(archiveContent);
		}

		/**
		 * @param dataUrl the data url to load the ggb file
		 */
		public void loadGgbFileAsBase64Again(String dataUrl) {
			prepareReloadGgbFile();
			View view = new View(null,this);
			view.processBase64String(dataUrl);
		}
		
		public void loadGgbFileAsBinaryAgain(JavaScriptObject binary) {
			prepareReloadGgbFile();
			View view = new View(null,this);
			view.processBinaryString(binary);
		}

		private void prepareReloadGgbFile() {
	        ((DrawEquationWeb) getDrawEquation())
	        .deleteLaTeXes((EuclidianViewWeb) getActiveEuclidianView());
			getImageManager().reset();
        }
		
		
		
		private void loadFile(HashMap<String, String> archiveContent)
		        throws Exception {

			beforeLoadFile();

			HashMap<String, String> archive = (HashMap<String, String>) archiveContent
			        .clone();

			// Handling of construction and macro file
			String construction = archive.remove(MyXMLio.XML_FILE);
			String macros = archive.remove(MyXMLio.XML_FILE_MACRO);
			String libraryJS = archive.remove(MyXMLio.JAVASCRIPT_FILE);

			// Construction (required)
			if (construction == null) {
				throw new ConstructionException(
				        "File is corrupt: No GeoGebra data found");
			}

			// Macros (optional)
			if (macros != null) {
				// macros = DataUtil.utf8Decode(macros);
				// //DataUtil.utf8Decode(macros);
				getXMLio().processXMLString(macros, true, true);
			}
			
			// Library JavaScript (optional)
			if (libraryJS == null) { //TODO: && !isGGTfile)
				kernel.resetLibraryJavaScript();
			} else {
				kernel.setLibraryJavaScript(libraryJS);
			}


			if (archive.entrySet() != null) {
				for (Entry<String, String> entry : archive.entrySet()) {
					maybeProcessImage(entry.getKey(), entry.getValue());
				}
			}
			if (!getImageManager().hasImages()) {
				// Process Construction
				// construction =
				// DataUtil.utf8Decode(construction);//DataUtil.utf8Decode(construction);
				
				//Before opening the file,
				//hide navigation bar for construction steps if visible.
				//(Don't do this for ggt files.)
				setShowConstructionProtocolNavigation(false);
				
				App.debug("start processing"+System.currentTimeMillis());
				getXMLio().processXMLString(construction, true, false);
				App.debug("end processing"+System.currentTimeMillis());
				setCurrentFile(archiveContent);
				afterLoadFileAppOrNot();
			} else {
				// on images do nothing here: wait for callback when images loaded.
				getImageManager().triggerImageLoading(
				/* DataUtil.utf8Decode( */construction/*
													 * )/*DataUtil.utf8Decode
													 * (construction)
													 */, getXMLio(), this);
				setCurrentFile(archiveContent);
				

			}
		}
		
		public abstract void afterLoadFileAppOrNot();

		public void beforeLoadFile() {
			startCollectingRepaints();
			//make sure the image manager will not wait for images from the *old* file
			if(this.getImageManager()!=null){
				this.getImageManager().reset();
			}
			getEuclidianView1().setReIniting(true);
			if (hasEuclidianView2EitherShowingOrNot()) {
				getEuclidianView2().setReIniting(true);
			}
		}
		
		public void setCurrentFile(HashMap<String, String> file) {
			if (currentFile == file) {
				return;
			}

			currentFile = file;
			if (currentFile != null) {
				addToFileList(currentFile);
			}

			// if (!isIniting() && isUsingFullGui()) {
			// updateTitle();
			// getGuiManager().updateMenuWindow();
			// }
		}

		public void addToFileList(Map<String, String> file) {
			if (file == null) {
				return;
			}
			// add or move fileName to front of list
			fileList.remove(file);
			fileList.addFirst(file);
		}

		public Map<String, String> getFromFileList(int i) {
			if (fileList.size() > i) {
				return fileList.get(i);
			}
			return null;
		}

		public int getFileListSize() {
			return fileList.size();
		}
		
		public Map<String, String> getCurrentFile() {
			return currentFile;
		}
		
		@Override
		public void reset() {
			if (currentFile != null) {
				try {
					loadGgbFile(currentFile);
				} catch (Exception e) {
					clearConstruction();
				}
			} else {
				clearConstruction();
			}
		}
		
		private static final ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
		static {
			IMAGE_EXTENSIONS.add("bmp");
			IMAGE_EXTENSIONS.add("gif");
			IMAGE_EXTENSIONS.add("jpg");
			IMAGE_EXTENSIONS.add("jpeg");
			IMAGE_EXTENSIONS.add("png");
		}

		private void maybeProcessImage(String filename, String binaryContent) {
			String fn = filename.toLowerCase();
			if (fn.equals(MyXMLio.XML_FILE_THUMBNAIL)) {
				return; // Ignore thumbnail
			}

			int index = fn.lastIndexOf('.');
			if (index == -1) {
				return; // Ignore files without extension
			}

			String ext = fn.substring(index + 1).toLowerCase();
			if (!IMAGE_EXTENSIONS.contains(ext)) {
				return; // Ignore non image files
			}

			// for file names e.g. /geogebra/main/nav_play.png in GeoButtons
			
			addExternalImage(filename, binaryContent);
		}
		
		public void addExternalImage(String filename, String src) {
			getImageManager().addExternalImage(filename, src);
		}
		
		@Override
		public ImageManager getImageManager() {
			return imageManager;
		}
		
		protected void initImageManager() {
			imageManager = new ImageManager();
		}
		
		@Override
		public final void setXML(String xml, boolean clearAll) {
			if (clearAll) {
				setCurrentFile(null);
			}

			try {
				// make sure objects are displayed in the correct View
				setActiveView(App.VIEW_EUCLIDIAN);
				getXMLio().processXMLString(xml, clearAll, false);
			} catch (MyError err) {
				err.printStackTrace();
				showError(err);
			} catch (Exception e) {
				e.printStackTrace();
				showError("LoadFileFailed");
			}
		}
		
		@Override
		public boolean clearConstruction() {
			// if (isSaved() || saveCurrentFile()) {
			kernel.clearConstruction(true);

			kernel.initUndoInfo();
			resetMaxLayerUsed();
			setCurrentFile(null);
			setMoveMode();

			((DrawEquationWeb) getDrawEquation())
			        .deleteLaTeXes((EuclidianViewWeb) getActiveEuclidianView());
			return true;

			// }
			// return false;
		}
		
		@Override
		public final GBufferedImage getExternalImageAdapter(String fileName) {
			ImageElement im = getImageManager().getExternalImage(fileName);
			if (im == null)
				return null;
			return new geogebra.html5.awt.GBufferedImageW(im);
		}
		
		@Override
		public final AnimationManager newAnimationManager(Kernel kernel2) {
			return new AnimationManagerW(kernel2);
		}
		
		@Override
		public final UndoManager getUndoManager(Construction cons) {
			return new UndoManagerW(cons);
		}

		@Override
		public final GeoElementGraphicsAdapter newGeoElementGraphicsAdapter() {
			return new geogebra.html5.kernel.GeoElementGraphicsAdapter(this);
		}
		
		@Override
		public final void runScripts(GeoElement geo1, String string) {
			geo1.runClickScripts(string);
		}
		
		@Override
		public final CASFactory getCASFactory() {
			return CASFactory.getPrototype();
		}
		
		@Override
		public void fileNew() {

			// clear all
			// triggers the "do you want to save" dialog
			// so must be called first
			if (!clearConstruction()) {
				return;
			}

			clearInputBar();

			// reset spreadsheet columns, reset trace columns
			if (isUsingFullGui()) {
				// getGuiManager().resetSpreadsheet();
			}

			getEuclidianView1().resetXYMinMaxObjects();
			if (hasEuclidianView2EitherShowingOrNot()) {
				getEuclidianView2().resetXYMinMaxObjects();
			}

			resetUniqueId();

			resetStorageInfo();

		}
		
		/**
		 * Opens the ggb or ggt file
		 * 
		 * @param fileToHandle
		 * @return returns true, if fileToHandle is ggb or ggt file, otherwise
		 *         returns false. Note that If the function returns true, it's don't
		 *         mean, that the file opening was successful, and the opening
		 *         finished already.
		 */
		public native boolean openFileAsGgb(JavaScriptObject fileToHandle,
		        JavaScriptObject callback) /*-{
			var ggbRegEx = /\.(ggb|ggt)$/i;
			if (!fileToHandle.name.toLowerCase().match(ggbRegEx))
				return false;

			var appl = this;
			var reader = new FileReader();
			reader.onloadend = function(ev) {
				if (reader.readyState === reader.DONE) {
					var fileStr = reader.result;
					appl.@geogebra.html5.main.AppWeb::loadGgbFileAsBase64Again(Ljava/lang/String;)(fileStr);
					if (callback != null)
						callback();
				}
			};
			reader.readAsDataURL(fileToHandle);
			return true;
		}-*/;					

		protected void clearInputBar() {
	        // TODO Auto-generated method stub
	        
        }

		protected void resetStorageInfo() {
	        // TODO Auto-generated method stub
	        
        }
		public void addFileLoadListener(FileLoadListener f){
			this.fileLoadListeners.add(f);
		}
		
		private ArrayList<FileLoadListener> fileLoadListeners = new ArrayList<FileLoadListener>();
		public final void notifyFileLoaded() {
	        for(FileLoadListener listener: fileLoadListeners){
	        	listener.onFileLoad();
	        }
	        
        }
		
		@Override
		public double getMillisecondTime() {
			return getMillisecondTimeNative();
		}
		
		private native double getMillisecondTimeNative() /*-{
			if ($wnd.performance) {
				return $wnd.performance.now();
			}
			
			// for IE9
			return new Date().getTime();
		}-*/;

		@Override
		public void copyBase64ToClipboard() {
			// unimplemented
		}

		public void openMaterial(String s) {
	        // TODO Auto-generated method stub
	        
        }

		public void ensureEditing() {
	        // TODO Auto-generated method stub
	        
        }
		
		private NetworkOperation networkOperation;
		
		/*
		 * True if showing the "alpha" in Input Boxes is allowed.
		 * (we can hide the symbol buttons with data-param-allowSymbolTable
		 * parameter)
		 */
		private boolean allowSymbolTables;
		
		/**
		 * @return OfflineOperation event flow
		 */
		public NetworkOperation getNetworkOperation() {
			return networkOperation;
		}
		
		protected void initNetworkEventFlow() {
			
			Network network = new Network() {
				
				private native boolean checkOnlineState() /*-{
					return $wnd.navigator.onLine;
				}-*/;
				
				public boolean onLine() {
					return checkOnlineState();
				}
			};
			
			NativeEventAttacher attacher = new NativeEventAttacher() {
				
				private native void nativeAttach(String t, BaseEventPool ep) /*-{
							$wnd.addEventListener(t, function() {
								ep.@geogebra.common.move.events.BaseEventPool::trigger()();
							});
				}-*/;
				
				public void attach(String type, BaseEventPool eventPool) {
					nativeAttach(type, eventPool);
				}
			};
			
			networkOperation = new NetworkOperation(network);
			BaseEventPool offlineEventPool = new BaseEventPool(networkOperation, false);	
			attacher.attach("offline", offlineEventPool);
			BaseEventPool onlineEventPool = new BaseEventPool(networkOperation, true);	
			attacher.attach("online", onlineEventPool);
			OfflineView ov = new OfflineView();
			networkOperation.setView(ov);
	    }

		public void setAllowSymbolTables(boolean allowST) {
	       allowSymbolTables = allowST;
        }
		
		/*
		 * Return true, if alpha buttons may be visible in input boxes.
		 */
		public boolean isAllowedSymbolTables(){
			return allowSymbolTables;
		}

	@Override
	public AlgoKimberlingWeightsInterface getAlgoKimberlingWeights() {
		if (kimberlingw != null) {
			return kimberlingw;
		}
		
	    GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				kimberlingw = new AlgoKimberlingWeights();
				setKimberlingWeightFunction(kimberlingw);
				kernel.updateConstruction();
			}
			public void onFailure(Throwable reason) {
				App.debug("AlgoKimberlingWeights loading failure");
			}
		});
		return kimberlingw;
	}

	public native void setKimberlingWeightFunction(AlgoKimberlingWeightsInterface kimberlingw) /*-{
		$wnd.geogebraKimberlingWeight = function(obj) {
			return kimberlingw.@geogebra.common.main.AlgoKimberlingWeightsInterface::weight(Lgeogebra/common/main/AlgoKimberlingWeightsParams;)(obj);
		}
	}-*/;

	@Override
	public native double kimberlingWeight(AlgoKimberlingWeightsParams kparams) /*-{

		if ($wnd.geogebraKimberlingWeight) {
			return $wnd.geogebraKimberlingWeight(kparams);
		}

		// should not execute!
		return 0;

	}-*/;

	@Override
	public AlgoCubicSwitchInterface getAlgoCubicSwitch() {
		if (cubicw != null) {
			return cubicw;
		}
		
	    GWT.runAsync(new RunAsyncCallback() {
	    	public void onSuccess() {
				cubicw = new AlgoCubicSwitch();
				setCubicSwitchFunction(cubicw);
				kernel.updateConstruction();
			}
			public void onFailure(Throwable reason) {
				App.debug("AlgoKimberlingWeights loading failure");
			}
		});
		return cubicw;
	}

	public native void setCubicSwitchFunction(AlgoCubicSwitchInterface cubicw) /*-{
		$wnd.geogebraCubicSwitch = function(obj) {
			return cubicw.@geogebra.common.main.AlgoCubicSwitchInterface::getEquation(Lgeogebra/common/main/AlgoCubicSwitchParams;)(obj);
		}
	}-*/;

	@Override
	public native String cubicSwitch(AlgoCubicSwitchParams kparams) /*-{

		if ($wnd.geogebraCubicSwitch) {
			return $wnd.geogebraCubicSwitch(kparams);
		}

		// should not execute!
		return 0;

	}-*/;
	
	@Override
    public CommandDispatcher getCommandDispatcher(Kernel k){
		return new CommandDispatcherW(k);
	}
	
	/**
	 * @return The default applet id
	 * Note,that this will be the articleelement id in lower subclasses
	 */
	public String getAppletId() {
		return DEFAULT_APPLET_ID;
	}
	
	/**
	 * @return GeoGebraFrame like frame
	 */
	public abstract HasAppletProperties getAppletFrame();

	/**
	 * @param viewId 
	 * @return the plotpanel euclidianview
	 */
	public EuclidianViewWeb getPlotPanelEuclidianView(int viewId) {
		if (getGuiManager() == null) {
			return null;
		}
		return (EuclidianViewWeb) getGuiManager().getPlotPanelView(viewId);
	}

	public boolean isPlotPanelEuclidianView(int viewID) {
	    if (getGuiManager() == null) {
	    	return false;
	    }
	    return getGuiManager().getPlotPanelView(viewID) != null;
    }
	
	@Override
	public void loseFocus() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Loads an image and puts it on the canvas (this happens on webcam input)
	 * On drag&drop or insert from URL this would be called too, but that would
	 * set security exceptions
	 * 
	 * @param url
	 *            - the data url of the image
	 * @param clientx
	 *            - desired position on the canvas (x) - unused
	 * @param clienty
	 *            - desired position on the canvas (y) - unused
	 */
	public void urlDropHappened(String url, int clientx, int clienty) {

		// Filename is temporarily set until a better solution is found
		// TODO: image file name should be reset after the file data is
		// available

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(url);

		// with dummy extension, maybe gif or jpg in real
		String imgFileName = zip_directory + ".png";

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, url, null);
	}
	
	/**
	 * Loads an image and puts it on the canvas (this happens by drag & drop)
	 * 
	 * @param imgFileName
	 *            - the file name of the image
	 * @param fileStr
	 *            - the image data url
	 * @param fileStr2
	 *            - the image binary string
	 * @param clientx
	 *            - desired position on the canvas (x)
	 * @param clienty
	 *            - desired position on the canvas (y)
	 */
	public void imageDropHappened(String imgFileName, String fileStr,
	        String fileStr2, GeoPoint loc) {

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(fileStr2);

		String fn = imgFileName;
		int index = imgFileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename without
		}
		// path
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		imgFileName = zip_directory + '/' + fn;

		doDropHappened(imgFileName, fileStr, loc);
	}

	private void doDropHappened(String imgFileName, String fileStr, GeoPoint loc) {

		Construction cons = getKernel().getConstruction();
		EuclidianViewInterfaceCommon ev = getActiveEuclidianView();
		getImageManager().addExternalImage(imgFileName,
		        fileStr);
		GeoImage geoImage = new GeoImage(cons);
		getImageManager().triggerSingleImageLoading(
		        imgFileName, geoImage);
		geoImage.setImageFileName(imgFileName);

		if (loc == null) {
			double cx = ev.getXmin() + (ev.getXmax() - ev.getXmin()) / 4;
			double cy = ev.getYmin() + (ev.getYmax() - ev.getYmin()) / 4;
			GeoPoint gsp = new GeoPoint(cons, cx, cy, 1);
			gsp.setLabel(null);
			gsp.setLabelVisible(false);
			gsp.update();
			geoImage.setCorner(gsp, 0);

			cx = ev.getXmax() - (ev.getXmax() - ev.getXmin()) / 4;
			GeoPoint gsp2 = new GeoPoint(cons, cx, cy, 1);
			gsp2.setLabel(null);
			gsp2.setLabelVisible(false);
			gsp2.update();
			geoImage.setCorner(gsp2, 1);
		} else {
			geoImage.setCorner(loc, 0);
		}

		geoImage.setLabel(null);
		GeoImage.updateInstances();

		// these things are done in Desktop GuiManager.loadImage too
		GeoElement[] geos = { geoImage };
		getActiveEuclidianView().getEuclidianController().clearSelections();
		getActiveEuclidianView().getEuclidianController()
		        .memorizeJustCreatedGeos(geos);
		setDefaultCursor();
	}
	
	/**
	 * Opens the image file
	 * 
	 * @param fileToHandle
	 * @param callback
	 * @return returns true, if fileToHandle image file, otherwise return false.
	 *         Note that If the function returns true, it's don't mean, that the
	 *         file opening was successful, and the opening finished already.
	 */
	public native boolean openFileAsImage(JavaScriptObject fileToHandle,
	        JavaScriptObject callback) /*-{
		var imageRegEx = /\.(png|jpg|jpeg|gif|bmp)$/i;
		if (!fileToHandle.name.toLowerCase().match(imageRegEx))
			return false;

		var appl = this;
		var reader = new FileReader();
		reader.onloadend = function(ev) {
			if (reader.readyState === reader.DONE) {
				var fileStr = reader.result;
				var fileName = fileToHandle.name;
				appl.@geogebra.web.main.AppW::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lgeogebra/common/kernel/geos/GeoPoint;)(fileName, fileStr, fileStr, null);
				if (callback != null){
					callback();
				}
			}
		};
		reader.readAsDataURL(fileToHandle);
		return true;
	}-*/;
	
	/**
	 * recalculates eulcidianviews environments.
	 */
	public void recalculateEnvironments() {
		App.debug("Must be implemented in subclasses");
	}

	
	/**
	 * @return the id of the articleelement
	 */
	public String getArticleId() {
		App.debug("must be implemented in subclasses");
	    return null;
    }

	/**
	 * @param articleid the article id added by scriptManager
	 * 
	 * this methdo is called by scriptmanager after ggbOnInit
	 */
	public static native void appletOnLoad(String articleid) /*-{
	    if (typeof $wnd.ggbAppletOnLoad === "function") {
	    	$wnd.ggbAppletOnLoad(articleid);
	    }
    }-*/;
	
}
