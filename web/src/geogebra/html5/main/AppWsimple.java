package geogebra.html5.main;

import geogebra.common.GeoGebraConstants;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.euclidian.EuclidianSimplePanelW;
import geogebra.html5.gui.GeoGebraFrame;
import geogebra.html5.util.ArticleElement;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class AppWsimple extends AppW {

	private GeoGebraFrame frame = null;

	/******************************************************
	 * Constructs AppW for applets with undo enabled
	 * 
	 * @param ae
	 * @param gf
	 */
	public AppWsimple(ArticleElement ae, GeoGebraFrame gf) {
		this(ae, gf, true);
	}

	/******************************************************
	 * Constructs AppW for applets
	 * 
	 * @param undoActive
	 *            if true you can undo by CTRL+Z and redo by CTRL+Y
	 */
	public AppWsimple(ArticleElement ae, GeoGebraFrame gf, final boolean undoActive) {
		super(ae, 2, null);
		this.frame = gf;
		setAppletHeight(frame.getComputedHeight());
		setAppletWidth(frame.getComputedWidth());

		this.useFullGui = false;


		Log.info("GeoGebra " + GeoGebraConstants.VERSION_STRING + " "
		        + GeoGebraConstants.BUILD_DATE + " "
		        + Window.Navigator.getUserAgent());
		initCommonObjects();
		initing = true;

		// TODO: EuclidianSimplePanelW
		this.euclidianViewPanel = new EuclidianSimplePanelW(this, false);
		//(EuclidianDockPanelW)getGuiManager().getLayout().getDockManager().getPanel(App.VIEW_EUCLIDIAN);
		this.canvas = this.euclidianViewPanel.getCanvas();
		canvas.setWidth("1px");
		canvas.setHeight("1px");
		canvas.setCoordinateSpaceHeight(1);
		canvas.setCoordinateSpaceWidth(1);
		initCoreObjects(undoActive, this);
		afterCoreObjectsInited();
		resetFonts();
		removeDefaultContextMenu(this.getArticleElement());
	}

	public GeoGebraFrame getGeoGebraFrame() {
		return frame;
	}

	@Override
	protected void afterCoreObjectsInited() {
		// Code to run before buildApplicationPanel

		//initGuiManager();// TODO: comment it out

		GeoGebraFrame.finishAsyncLoading(articleElement, frame, this);
		initing = false;
	}

	public void buildApplicationPanel() {
		if (frame != null) {
			frame.clear();
			frame.add((Widget)getEuclidianViewpanel());
			getEuclidianViewpanel().setPixelSize(
					getSettings().getEuclidian(1).getPreferredSize().getWidth(),
					getSettings().getEuclidian(1).getPreferredSize().getHeight());
		}
	}

	@Override
    public void afterLoadFileAppOrNot() {

		buildApplicationPanel();

		getScriptManager().ggbOnInit();	// put this here from Application constructor because we have to delay scripts until the EuclidianView is shown
		
		initUndoInfoSilent();

		getEuclidianView1().synCanvasSize();
		getEuclidianView1().createImage();
		getAppletFrame().resetAutoSize();

		getEuclidianView1().doRepaint2();
		stopCollectingRepaints();
		if(frame.splash != null){
			frame.splash.canNowHide();
		}
		if (!articleElement.preventFocus()) {
			requestFocusInWindow();
		}
		setDefaultCursor();
		GeoGebraFrame.useDataParamBorder(getArticleElement(), getGeoGebraFrame());
		GeoGebraProfiler.getInstance().profileEnd();
    }

	@Override
	public void focusLost() {
		GeoGebraFrame.useDataParamBorder(
				getArticleElement(),
				getGeoGebraFrame());
		this.getGlobalKeyDispatcher().InFocus = false;
	}

	@Override
	public void focusGained() {
		GeoGebraFrame.useFocusedBorder(
				getArticleElement(),
				getGeoGebraFrame());
	}

	@Override
    public void syncAppletPanelSize(int widthDiff, int heightDiff, int evno) {

		// not sure this is needed here

		/*if (widthDiff != 0 || heightDiff != 0)
			getEuclidianViewpanel().setPixelSize(
				getEuclidianViewpanel().getOffsetWidth() + widthDiff,
				getEuclidianViewpanel().getOffsetHeight() + heightDiff);
		*/
	}
	
	@Override
    public Element getFrameElement(){
		return frame.getElement();
	}
	
	@Override
    public HasAppletProperties getAppletFrame() {
		return frame;
	}
	
	@Override
    public String getArticleId() {
		return articleElement.getId();
	}

	@Override
    public void openSearch() {
	    //no browser
    }

	@Override
    public void showLanguageUI() {
	    //no language UI
	    
    }

	@Override
    public void uploadToGeoGebraTube() {
	    //no upload
	    
    }

	@Override
    public void set1rstMode() {
	    setMoveMode();
    }
}
