package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.geogebra3D.util.CopyPaste3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.AppCompanion;
import geogebra.common.main.DialogManager;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.EuclidianController3DW;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.euclidian3D.openGL.GLFactoryW;
import geogebra.geogebra3D.web.gui.GuiManager3DW;
import geogebra.html5.Browser;
import geogebra.html5.euclidian.EuclidianPanelWAbstract;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.dialog.DialogManager3DW;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppWapplication;
import geogebra.web.main.GDevice;

import java.util.HashMap;

/**
 * for 3D
 * 
 * @author mathieu
 *
 */
public class AppWapplication3D extends AppWapplication {

	private EuclidianView3DW euclidianView3D;
	private EuclidianController3DW euclidianController3D;

	/**
	 * constructor
	 * 
	 * @param article
	 * @param geoGebraAppFrame
	 */
	public AppWapplication3D(ArticleElement article,
	        GeoGebraAppFrame geoGebraAppFrame, GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, 3, laf, device);
		// Window.alert("AppWapplication3D : I will be threeD :-)");
	}

	public AppWapplication3D(final ArticleElement article,
	        final GeoGebraAppFrame geoGebraAppFrame, final boolean undoActive,
	        final int dimension, final GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, undoActive, dimension, laf, device);
	}

	public AppWapplication3D(final ArticleElement article,
	        final GeoGebraAppFrame geoGebraAppFrame, final int dimension,
	        final GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, dimension, laf, device);
	}

	@Override
	protected Kernel newKernel(App this_app) {
		return App3DW.newKernel(this_app);
	}

	@Override
	public boolean hasEuclidianView3D() {
		return this.euclidianController3D != null;
	}

	@Override
	public EuclidianView3DInterface getEuclidianView3D() {
		if (this.euclidianView3D == null) {
			euclidianView3D = App3DW.newEuclidianView3DW(euclidianController3D,
			        getSettings().getEuclidian(3));
			App.printStacktrace("");
		}
		// Window.alert("getEuclidianView3D()");
		return euclidianView3D;
	}

	@Override
	public boolean isEuclidianView3Dinited() {
		return euclidianView3D != null;
	}

	@Override
	public void initEuclidianViews() {

		super.initEuclidianViews();
		euclidianController3D = App3DW.newEuclidianController3DW(kernel);
		// euclidianView3D = App3DW.newEuclidianView3DW(euclidianController3D,
		// getSettings().getEuclidian(3));

	}

	@Override
	protected GuiManagerW newGuiManager() {
		return App3DW.newGuiManager(this, this.device);
	}

	private GCheckBoxMenuItem itemEuclidian3D;

	@Override
	public boolean supportsView(int viewID) {
		if (viewID == App.VIEW_EUCLIDIAN3D) {
			if (Browser.supportsWebGL()) {
				return true;
			}
		}
		return super.supportsView(viewID);
	}

	@Override
	public void recalculateEnvironments() {

		super.recalculateEnvironments();

		if (getEuclidianView3D() != null) {
			getEuclidianView3D().getEuclidianController()
			        .calculateEnvironment();
		}

		((App3DCompanionW) companion).recalculateEnvironments();
	}

	@Override
	protected void initFactories() {

		super.initFactories();
		geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory.prototype = new GLFactoryW();
		geogebra.common.util.CopyPaste.INSTANCE = new CopyPaste3D();
	}

	@Override
	public void updateViewSizes() {
		super.updateViewSizes();
		if (((GuiManager3DW) getGuiManager()).getEuclidian3DPanel() != null) {
			((GuiManager3DW) getGuiManager()).getEuclidian3DPanel()
			        .deferredOnResize();
		}
		((App3DCompanionW) companion).updateViewSizes();
	}

	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		if (showView(App.VIEW_EUCLIDIAN3D)) {
			getEuclidianView3D().getStyleBar().updateStyleBar();
		}
	}

	@Override
	public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel,
	        EuclidianController ec, boolean[] showAxes, boolean showGrid,
	        int id, EuclidianSettings settings) {
		return App3DW.newEuclidianView(evPanel, ec, showAxes, showGrid, id,
		        settings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return App3DW.newEuclidianController(kernel);

	}

	@Override
	protected AppCompanion newAppCompanion() {
		return new App3DCompanionW(this);
	}

	@Override
	public void setCurrentFile(HashMap<String, String> file) {
		super.setCurrentFile(file);
		if (this.isEuclidianView3Dinited()) {
			((EuclidianView3DW) getEuclidianView3D()).setCurrentFile(file);
		}
	}

	@Override
	public DialogManager getDialogManager() {
		if (dialogManager == null) {
			dialogManager = new DialogManager3DW(this);
		}
		return dialogManager;
	}

}
