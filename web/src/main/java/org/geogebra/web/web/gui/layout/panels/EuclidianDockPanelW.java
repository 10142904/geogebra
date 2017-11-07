package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract implements EuclidianPanelWAbstract{

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null;// static foreground

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar (is there stylebar?)
	 */
	public EuclidianDockPanelW(boolean stylebar, App app1) {
		super(
				App.VIEW_EUCLIDIAN,	// view id 
				"DrawingPad", 				// view title
				//ToolBar.getAllToolsNoMacros(true),  // toolbar string... TODO: ToolBarW.getAllTools(app);
				null,
				stylebar, // style bar?
				true, // zoom panel?
				5,							// menu order
				'1' // ctrl-shift-1
			);


		if (app1 != null && app1.has(Feature.DYNAMIC_STYLEBAR)) {
			setViewImage(new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE.gear().getSafeUri(), 0, 0,
					24, 24, false, false));
		} else {
			setViewImage(getResources().styleBar_graphicsView());
		}
		//TODO: temporary fix to make applets work until
		// dockpanels works for applets
		
		if(stylebar){
			component = loadComponent();
		}else{
			component = loadComponent();
			buildDockPanel();
		}
	}
	
	/**
	 * This constructor is used by the applet
	 * @param application
	 * @param stylebar
	 */
	public EuclidianDockPanelW(AppW application, boolean stylebar) {
		this(stylebar, application);
		attachApp(application);


	}

	public void attachApp(AppW application) {
		app = application;

		// GuiManager can be null at the startup of the application,
		// but then the addNavigationBar method will be called explicitly.
		// By the way, this method is only called from AppWapplet,
		// so this will be actually null here.
		if (app.getGuiManager() != null
				&& app.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			addNavigationBar();
		}
	}

	
	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.getAbsolutePanel().add(eview1);
		}

		return euclidianpanel;
	}

	public void reset() {
		if (euclidianpanel != null) {
			euclidianpanel.oldWidth = 0;
			euclidianpanel.oldHeight = 0;
		}
	}



	@Override
	protected Widget loadStyleBar() {

		if (espanel == null) {
			espanel = app.getEuclidianView1().getStyleBar();
		}

		return (Widget) espanel;
	}

	@Override
	public Canvas getCanvas() {
	    return eview1;
    }

	@Override
	public EuclidianPanel getEuclidianPanel() {
	    return euclidianpanel;
    }

	public void remove(Widget w) {
		euclidianpanel.remove(w);
    }

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
	}

	@Override
    public EuclidianView getEuclidianView() {
		if (app != null) {
			return app.getEuclidianView1();
		}
		return null;
	}

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics();
	}

	@Override
	public void calculateEnvironment() {
		app.getEuclidianView1().getEuclidianController().calculateEnvironment();

	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsViewDimChanged(width, height);
	}
}
