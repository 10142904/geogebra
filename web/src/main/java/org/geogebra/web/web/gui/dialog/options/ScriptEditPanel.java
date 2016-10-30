package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.ScriptInputPanelW;
import org.geogebra.web.web.gui.properties.OptionPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Scripting editor for Web
 */
class ScriptEditPanel extends OptionPanel {


	/**
	 * 
	 */
	private ScriptInputPanelW clickDialog, updateDialog, globalDialog;
	private TabPanel tabbedPane;
	private FlowPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;
	private Localization loc;

	/**
	 * 
	 * @param model0
	 *            model
	 * @param app
	 *            application
	 */
	public ScriptEditPanel(ScriptEditorModel model0, final AppW app) {
		this.loc = app.getLocalization();
		int row = 35;
		int column = 15;
		setModel(model0);
		model0.setListener(this);
		tabbedPane = new TabPanel();
		tabbedPane.setStyleName("scriptTabPanel");

		clickDialog = new ScriptInputPanelW(app,
				null, row, column, false, false);
		updateDialog = new ScriptInputPanelW(app,
				null, row, column, true, false);
		globalDialog = new ScriptInputPanelW(app, null, row, column, false,
				true);
		// add(td.getInputPanel(), BorderLayout.NORTH);
		// add(td2.getInputPanel(), BorderLayout.CENTER);
		clickScriptPanel = new FlowPanel();
		clickScriptPanel.add(clickDialog.getInputPanel(row, column, true));
		clickScriptPanel
		.add(clickDialog.getButtonPanel());

		updateScriptPanel = new FlowPanel();
		updateScriptPanel.add(
				updateDialog.getInputPanel(row, column, true));
		updateScriptPanel.add(updateDialog.getButtonPanel());

		globalScriptPanel = new FlowPanel();
		globalScriptPanel.add(globalDialog.getInputPanel(row, column, true));
		globalScriptPanel.add(globalDialog.getButtonPanel());
		setWidget(tabbedPane);

	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		clickDialog.applyModifications();
		updateDialog.applyModifications();
		globalDialog.applyModifications();
	}

	@Override
	public void setLabels() {
		// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));


	}

	@Override
	public OptionPanel updatePanel(Object[] geos) {
		if (geos.length != 1){
			return null;
		}

		// remember selected tab
		int idx = tabbedPane.getTabBar().getSelectedTab();

		GeoElement geo = (GeoElement) geos[0];
		clickDialog.setGeo(geo);
		updateDialog.setGeo(geo);
		globalDialog.setGlobal();
		tabbedPane.clear();
		if (geo.canHaveClickScript())
			tabbedPane.add(clickScriptPanel, loc.getPlain("OnClick"));
		if (geo.canHaveUpdateScript())
			tabbedPane.add(updateScriptPanel, loc.getPlain("OnUpdate"));
		tabbedPane.add(globalScriptPanel, loc.getPlain("GlobalJavaScript"));

		// select tab as before
		tabbedPane.selectTab(Math.max(0,	idx));
		return this;
	}

}