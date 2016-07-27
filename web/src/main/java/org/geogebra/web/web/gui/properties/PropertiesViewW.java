package org.geogebra.web.web.gui.properties;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.geogebra.web.html5.util.tabpanel.MyTabPanel;
import org.geogebra.web.html5.util.tabpanel.TabPanelInterface;
import org.geogebra.web.web.gui.dialog.options.OptionPanelW;
import org.geogebra.web.web.gui.dialog.options.OptionsAdvancedW;
import org.geogebra.web.web.gui.dialog.options.OptionsAlgebraW;
import org.geogebra.web.web.gui.dialog.options.OptionsCASW;
import org.geogebra.web.web.gui.dialog.options.OptionsDefaultsW;
import org.geogebra.web.web.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.web.gui.dialog.options.OptionsLayoutW;
import org.geogebra.web.web.gui.dialog.options.OptionsObjectW;
import org.geogebra.web.web.gui.dialog.options.OptionsSpreadsheetW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * PropertiesView for Web
 *
 */
public class PropertiesViewW extends PropertiesView
		implements RequiresResize, SetLabels {


	private FlowPanel wrappedPanel;

	// option panels
	private OptionsDefaultsW defaultsPanel;
	private OptionsEuclidianW euclidianPanel, euclidianPanel2, euclidianPanel3D;
	private OptionsSpreadsheetW spreadsheetPanel;
	private OptionsCASW casPanel;
	private OptionsAdvancedW advancedPanel;
	private OptionsLayoutW layoutPanel;
	private OptionsAlgebraW algebraPanel;
	
	// current OptionPanel
	private OptionPanelW optionPanel;

	private PropertiesStyleBarW styleBar;

	private Label notImplemented;

	private FlowPanel contentsPanel;
	private OptionType optionType;

	// For autoopen AV feature
	// private boolean wasAVShowing;
	//
	// private boolean auxWasVisible;
	//
	// private boolean isObjectOptionsVisible;
	/**
	 * 
	 * @param app
	 *            app
	 * @param ot
	 *            initial options type
	 */
	public PropertiesViewW(AppW app, OptionType ot) {
		super(app);
		this.wrappedPanel = new FlowPanel();
		app.setPropertiesView(this);

		app.setWaitCursor();   

		notImplemented = new Label("Not implemented");
		optionType = ot;
		initGUI();
		app.setDefaultCursor();
	}

	private void initGUI() {

		wrappedPanel.addStyleName("PropertiesViewW");
		//		getStyleBar();

		//mainPanel = new FlowPanel();
		
		contentsPanel = new FlowPanel();
		contentsPanel.addStyleName("contentsPanel");
		if (app.has(Feature.MULTIROW_TAB_PROPERTIES)) {
			contentsPanel.addStyleName("contentsPanel2");
		}
		//wrappedPanel.addStyleName("propertiesView");
		//mainPanel.add(contentsPanel);
		wrappedPanel.add(contentsPanel);
		wrappedPanel.add(getStyleBar().getWrappedPanel());
		
//		if(!((AppW) app).getLAF().isSmart()){
		//mainPanel.add(getStyleBar().getWrappedPanel());
		//	}
			
		//wrappedPanel.add(mainPanel);

		setOptionPanel(optionType, 0);
		//createButtonPanel();
		//add(buttonPanel, BorderLayout.SOUTH);

	}

	/**
	 * @return the style bar for this view.
	 */
	public PropertiesStyleBarW getStyleBar() {
		if (styleBar == null) {
			styleBar = newPropertiesStyleBar();
		}

		return styleBar;
	}

	/**
	 * @return properties stylebar
	 */
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBarW(this, app);
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 *            panel type
	 * @param subType
	 *            tab number for given panel
	 * @return options panel
	 */
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		if (styleBar != null) {
			styleBar.updateGUI();
		}	
		switch (type) {
		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaultsW((AppW) app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASW((AppW) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianW((AppW) app,
						((AppW) app).getActiveEuclidianView());
				euclidianPanel.setLabels();
				euclidianPanel.setView(((AppW)app).getEuclidianView1());
				euclidianPanel.showCbView(false);
			}

			Log.debug("euclidianPanel");
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView2(1));
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((AppW)app).getEuclidianView2(1));
				euclidianPanel2.showCbView(false);
			}
			Log.debug("euclidianPanel2");
			return euclidianPanel2;
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
		//		euclidianPanel3D.setView(((AppW)app).getEuclidianView3D());
				euclidianPanel3D.showCbView(false);
			}
			Log.debug("euclidianPanel2");
			return euclidianPanel2;

			
		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetW((AppW)app, ((AppW)app)
						.getGuiManager().getSpreadsheetView());
			}
			return spreadsheetPanel;

		case ADVANCED:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvancedW((AppW) app);
			}
			return advancedPanel;

		case ALGEBRA:
			if (algebraPanel == null) {
				algebraPanel = new OptionsAlgebraW((AppW) app);
			}
			return algebraPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutW((AppW) app);
			}
			layoutPanel.getWrappedPanel().setStyleName("layoutPanel");
			
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObjectW((AppW) app, false,
						new Runnable() {

							public void run() {
								updatePropertiesView();
							}
						});

			}

			Log.debug("obect prop SELECTING TAB " + subType);
			((OptionsObjectW) objectPanel).selectTab(subType);
			return (OptionPanelW) objectPanel;
		}
		return null;
	}

	/**
	 * TODO disabled; decide if we want this
	 * 
	 * @param visible
	 *            whether to show AV
	 */
	public void updateAVvisible(boolean visible) {
		// if ((visible && this.optionPanel instanceof OptionsObjectW) ==
		// this.isObjectOptionsVisible) {
		// return;
		// }
		// this.isObjectOptionsVisible = !this.isObjectOptionsVisible;
		// if (visible) {
		// wasAVShowing = app.getGuiManager().hasAlgebraViewShowing();
		// auxWasVisible = app.getSettings().getAlgebra()
		// .getShowAuxiliaryObjects();
		// if (!wasAVShowing) {
		// app.getGuiManager().setShowView(true, App.VIEW_ALGEBRA);
		// app.updateViewSizes();
		// }
		// app.setShowAuxiliaryObjects(true);
		//
		// } else {
		// if (!auxWasVisible) {
		// app.setShowAuxiliaryObjects(false);
		// }
		// if (!wasAVShowing) {
		// app.getGuiManager().setShowView(false, App.VIEW_ALGEBRA);
		// app.updateViewSizes();
		// }
		// }

	}

	private OptionsObjectW getObjectPanel() {
		return objectPanel != null ? (OptionsObjectW) objectPanel:null;
	}

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
	}	



	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
	}

	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		// TODO Auto-generated method stub
		Log.debug("update visual style");
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		updatePropertiesGUI();

	}

	public void repaintView() {
		// nothing on repaint
	}

	public void reset() {
		// TODO Auto-generated method stub
		Log.debug("reset");
	}

	public void clearView() {
		Log.debug("Clear View");
	}

	public void setMode(int mode,ModeSetter m) {
		// TODO Auto-generated method stub
		Log.debug("setting mode");
	}

	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateSelection() {
		if (app.getSelectionManager().selectedGeosSize() != 0 && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		else if (app.getSelectionManager().selectedGeosSize() == 0) {
			if (optionType != OptionType.EUCLIDIAN
					|| optionType != OptionType.EUCLIDIAN2
					|| optionType != OptionType.EUCLIDIAN3D
					|| optionType != OptionType.EUCLIDIAN_FOR_PLANE) {
				if (app.getActiveEuclidianView().isEuclidianView3D()) {
					setOptionPanel(OptionType.EUCLIDIAN3D);
				} else if (app.getActiveEuclidianView().isDefault2D()) {
					setOptionPanel(app.getActiveEuclidianView().getEuclidianViewNo() == 1
						? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2);
				} else {
					setOptionPanel(OptionType.EUCLIDIAN_FOR_PLANE);
				}
			}
		}


		updatePropertiesGUI();
	}

	@Override
	protected void setOptionPanelWithoutCheck(OptionType type) {
		int sType = 0;
		if (type == OptionType.OBJECTS && this.objectPanel != null) {
			TabPanelInterface tabPanel = ((OptionsObjectW) this.objectPanel)
					.getTabPanel();
			if (tabPanel instanceof MultiRowsTabPanel) {
				sType = ((MultiRowsTabPanel) tabPanel).getTabBar()
						.getSelectedTab();
			} else { // Remove after Feature.MULTIROW_TAB_PROPERTIES will be
						// released
				sType = ((MyTabPanel) tabPanel).getTabBar().getSelectedTab();
			}
		}
		setOptionPanel(type, sType);
	}

	@Override
	protected void setObjectsToolTip() {
		Log.debug("=============== PropertiesViewW.setObjectsToolTip() : TODO");
		// styleBar.setObjectsToolTip();
	}

	@Override
	protected void setSelectedTab(OptionType type) {
		switch (type) {
		case EUCLIDIAN:
			euclidianPanel.setSelectedTab(selectedTab);
			break;
		case EUCLIDIAN2:
			euclidianPanel2.setSelectedTab(selectedTab);
			break;
		}
	}

	@Override
	protected void updateObjectPanelSelection(ArrayList<GeoElement> geos) {
		if (objectPanel == null) {
			return;
		}
		((OptionsObjectW) objectPanel).updateSelection(geos);
		updateTitleBar();
		setObjectsToolTip();
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		optionType = type;
		contentsPanel.clear();
		optionPanel = getOptionPanel(type, subType);
		updateAVvisible(true);
		Widget wPanel = optionPanel.getWrappedPanel();
		notImplemented.setText(getTypeString(type) + " - Not implemented");
		contentsPanel.add(wPanel != null ? wPanel: notImplemented);
		if(wPanel != null) {
			onResize();
		}
		this.styleBar.selectButton(type);
	}

	/**
	 * @return selected option type
	 */
	public OptionType getOptionType() {
		return optionType;
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (objectPanel == null) {
			return;
		}
		objectPanel.forgetGeoAdded();
	}


	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		if (geos.size() != 0 && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		updatePropertiesGUI();
		Log.debug("updateSelection(geos)");
	}

	private void updatePropertiesGUI() {
		OptionsObjectW panel = getObjectPanel();
		if (panel != null) {
			panel.updateGUI();
			if (optionType == OptionType.OBJECTS) {
				if (!panel.getWrappedPanel().isVisible()) {
					setOptionPanel(OptionType.EUCLIDIAN);
				}
			}
		}

		//		   if (optionType == OptionType.OBJECTS)  {
		// Log.debug("selecting tab 2");
		//			   getObjectPanel().selectTab(2);
		//		   }

		if (styleBar != null) {
			styleBar.updateGUI();
		}	


	}

	@Override
	protected void updateTitleBar() {
		updatePropertiesGUI();

	}

	@Override
	public void attachView() {
		if (attached){
			Log.debug("already attached");
			return;
		}

		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		app.getKernel().getAnimatonManager().stopAnimation();
		attached = true;
	}

	@Override
	public void detachView() {
		kernel.detach(this);
		clearView();
		app.getKernel().getAnimatonManager().startAnimation();
		attached = false;
	}

	@Override
	public void updatePropertiesView() {
		updatePropertiesGUI();
		Log.debug("updatePropertiesView");
	}


	public boolean isShowing() {
		Log.debug("isShowing");
		return false;
	}

	/**
	 * 
	 * @return GWT panel of this view
	 */
	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	/**
	 * Rebuild GUI for the new font size
	 */
	public void updateFonts(){
		updatePropertiesGUI();
	}

    public void onResize() {
    	//-34px for width of stylebar
    	int width = getWrappedPanel().getOffsetWidth() - 37;
    	int height = getWrappedPanel().getOffsetHeight();
    	//contentsPanel.setHeight(getWrappedPanel().getOffsetHeight() + "px");
    	
    	if(height > 0 && width > 0) {
    		contentsPanel.setWidth(width + "px");
    		
			if (!app.has(Feature.MULTIROW_TAB_PROPERTIES)) {
				// -30px for Tabs, -27px for padding, -26px for paddings
				optionPanel.onResize((height - 30 - 27),
						Math.max(0, width - 26));
			}
    	}
    }
    
	public boolean suggestRepaint(){
		return false;
	}

	@Override
    public void setLabels() {
		if (euclidianPanel != null) {
			euclidianPanel.setLabels();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.setLabels();
		}
		if (euclidianPanel3D != null) {
			euclidianPanel3D.setLabels();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.setLabels();
		}
		if (casPanel != null) {
			casPanel.setLabels();
		}

		if (algebraPanel != null) {
			algebraPanel.setLabels();
		}
    }

	@Override
	public void updateStyleBar() {

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}
}
