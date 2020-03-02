package org.geogebra.web.full.gui.exam;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.GlobalHeader;

/**
 * @author csilla
 * 
 *         dialog to enter in graphing or cas calc exam mode
 *
 */
public class ExamStartDialog extends DialogBoxW
		implements SetLabels, FastClickHandler {
	private FlowPanel mainPanel;
	private Label startText;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton startBtn;

	/**
	 * @param app
	 *            application
	 */
	public ExamStartDialog(AppWFull app) {
		super(app.getPanel(), app);
		buildGUI();
	}

	private void buildGUI() {
		// init start dialog text
		mainPanel = new FlowPanel();
		startText = new Label("");
		startText.addStyleName("examStartText");
		// create button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = new StandardButton("", app);
		cancelBtn.addFastClickHandler(this);
		startBtn = new StandardButton("", app);
		startBtn.addFastClickHandler(this);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(startBtn);
		// build main panel
		mainPanel.add(startText);
		mainPanel.add(buttonPanel);
		mainPanel.addStyleName("examStartDialog");
		add(mainPanel);
		setGlassEnabled(true);
		setLabels();
	}

	@Override
	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("exam_menu_enter"));
		startText.setText(
				app.getLocalization().getMenu("exam_start_dialog_text"));
		startBtn.setLabel(app.getLocalization().getMenu("exam_start_button"));
		cancelBtn.setLabel(app.getLocalization().getMenu("Cancel"));
	}

	@Override
	public void show() {
		super.show();
		center();
	}

	@Override
	public void onClick(Widget source) {
		if (source == startBtn) {
			onStartBtnClick();
		} else if (source == cancelBtn) {
			((AppWFull) app).getLAF().toggleFullscreen(false);
		}
		hide();
	}

	private void onStartBtnClick() {
		AppWFull app = (AppWFull) this.app;

		// ensure fullscreen: we may have lost it when handling unsaved
		// changes
		app.getLAF().toggleFullscreen(true);
		app.setNewExam();
		app.startExam();
		if (app.getGuiManager() != null
				&& app.getGuiManager().getUnbundledToolbar() != null) {
			app.getGuiManager().setUnbundledHeaderStyle("examOk");
			app.getMenuViewController().setExamMenu();
			app.getGuiManager().resetMenu();
			GlobalHeader.INSTANCE.addExamTimer();
			new ExamUtil(app).visibilityEventMain();
			app.getGuiManager().initInfoBtnAction();
		}
	}
}
