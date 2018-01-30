package org.geogebra.web.web.gui.inputbar;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.view.algebra.MarblePanel;

public final class WarningErrorHandler implements ErrorHandler {
	private static String undefinedVariables;
	private final App app2;
	private final HasHelpButton input;

	WarningErrorHandler(App app2, HasHelpButton input) {
		this.app2 = app2;
		this.input = input;
	}

	@Override
	public void showError(String msg) {
		input.setError(msg);
		input.getHelpToggle().getElement().setTitle(msg == null
				? app2.getLocalization().getMenu("InputHelp") : msg);
		if (app2.has(Feature.TOOLTIP_DESIGN) && !Browser.isMobile()
				&& input.getHelpToggle() instanceof MarblePanel) {
			((MarblePanel) input.getHelpToggle())
					.setTitle(msg == null ? app2.getLocalization()
							.getMenu("InputHelp") : msg);
		}
	}

	@Override
	public void resetError() {
		showError(null);
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		if (app2.has(Feature.INPUT_BAR_ADD_SLIDER)) {
			input.setUndefinedVariables(string);
		}
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {
		input.setCommandError(command);
		if (((GuiManagerW) app2.getGuiManager())
				.hasInputHelpPanel()) {
			InputBarHelpPanelW helpPanel = ((GuiManagerW) app2
					.getGuiManager()).getInputHelpPanel();
			helpPanel.focusCommand(
					app2.getLocalization().getCommand(command));
			input.getHelpToggle().getElement().setTitle(
					app2.getLocalization().getError("InvalidInput"));
		}
	}

	@Override
	public String getCurrentCommand() {
		return input.getCommand();
	}

	public static String getUndefinedValiables() {
		return undefinedVariables;
	}

	public static void setUndefinedValiables(String undefinedValiables) {
		WarningErrorHandler.undefinedVariables = undefinedValiables;
	}
}