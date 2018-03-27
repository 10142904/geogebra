package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Generates simple option and input dialogs modeled after the dialogs generated
 * by the JOptionPane class.
 * 
 */
public class GOptionPaneW extends GDialogBox
		implements ClickHandler, HasKeyboardPopup {

	private App mApp;
	private String title;
	private String mMessage;
	private String initialSelectionValue;
	private Button btnOK;
	private Button btnCancel;
	private Button[] optionButtons;
	private String[] optionNames;
	private int optionType;
	private int messageType;
	private boolean requiresReturnValue;
	private Localization loc;
	private AsyncOperation<String[]> returnHandler;

	private AutoCompleteTextFieldW inputField;
	private FlowPanel mainPanel;
	private FlowPanel buttonPanel;
	private ScrollPanel scrollPanel;
	private int returnOption;
	private String returnValue;
	private VerticalPanel messageTextPanel;

	private Image mIcon;
	private HorizontalPanel messagePanel;
	private String okLabel = null;
	private int enterOption;
	private boolean keyDown;

	private FocusWidget caller;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 */
	public GOptionPaneW(Panel root, App app) {
		super(false, true, root, app);
		this.mApp = app;
		createGUI();
	}

	private void showDialog(boolean autoComplete) {
		loc = mApp.getLocalization();
		updateGUI();
		center();
		if (inputField != null) {
			inputField.setAutoComplete(autoComplete);
		}
		show();
	}

	/**
	 * Close the popup.
	 */
	protected void close() {
		// if hide is called before the callback the callback can create another
		// Message (without being hidden instantaneously)!
		hide();

		if (requiresReturnValue) {
			if (returnOption == GOptionPane.CANCEL_OPTION) {
				returnValue = initialSelectionValue;
			} else {
				returnValue = inputField.getText();
			}
		}

		if (returnHandler != null) {
			Log.debug("option: " + returnOption + "  value: " + returnValue);
			String[] dialogResult = { returnOption + "", returnValue };
			returnHandler.callback(dialogResult);

		}

		// return the focus to the input field calling this dialog
		if (caller != null) {
			caller.setFocus(true);
		}
		caller = null;
	}

	public void setCaller(FocusWidget c) {
		caller = c;
	}

	@Override
	public void setGlassEnabled(boolean enabled) {
		super.setGlassEnabled(enabled);
	}

	private void createGUI() {

		setGlassEnabled(true);
		if (mApp.has(Feature.DIALOG_DESIGN)) {
			addStyleName("MaterialDialogBox");
		} else {
			addStyleName("DialogBox");
		}

		btnOK = new Button();
		btnOK.addClickHandler(this);

		btnCancel = new Button();
		btnCancel.addClickHandler(this);
		btnCancel.addStyleName("cancelBtn");

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		messagePanel = new HorizontalPanel();
		messagePanel.addStyleName("Dialog-messagePanel");
		messagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		messageTextPanel = new VerticalPanel();

		mainPanel = new FlowPanel();
		mainPanel.addStyleName("Dialog-content");

	}

	private void updateGUI() {

		mainPanel.clear();

		updateMessagePanel();
		mainPanel.add(messagePanel);

		if (requiresReturnValue) {
			updateInputField();
			mainPanel.add(inputField);
		}

		updateButtonPanel();
		mainPanel.add(buttonPanel);

		clear();
		add(mainPanel);
		setText(title);
	}

	private void updateButtonPanel() {

		buttonPanel.clear();

		switch (optionType) {
		case GOptionPane.CUSTOM_OPTION:
			optionButtons = new Button[optionNames.length];
			for (int i = optionNames.length - 1; i >= 0; i--) {
				optionButtons[i] = new Button(optionNames[i]);
				optionButtons[i].addClickHandler(this);

				// Styling of cancel button should be different
				if (optionNames[i]
						.equals(mApp.getLocalization().getMenu("Cancel"))) {
					optionButtons[i].addStyleName("cancelBtn");
				}

				buttonPanel.add(optionButtons[i]);
			}
			break;

		case GOptionPane.OK_OPTION:
		case GOptionPane.DEFAULT_OPTION:
			buttonPanel.add(btnOK);
			setLabels();
			break;

		case GOptionPane.OK_CANCEL_OPTION:
			buttonPanel.add(btnOK);
			buttonPanel.add(btnCancel);
			setLabels();
			break;

		default:
			buttonPanel.add(btnOK);
			setLabels();
		}

	}

	private void updateMessagePanel() {

		messagePanel.clear();
		messageTextPanel.clear();

		updateIcon();
		if (mIcon != null) {
			messagePanel.add(mIcon);
		}
		if (scrollPanel != null) {
			messagePanel.add(scrollPanel);
			messagePanel.addStyleName("examMessagePanel");
		} else {
			String[] lines = mMessage.split("\n");
			for (String item : lines) {
				messageTextPanel.add(new Label(item));
			}
			messagePanel.add(messageTextPanel);
		}
	}

	private void setLabels() {
		btnOK.setText(okLabel == null ? loc.getMenu("OK") : okLabel);
		btnCancel.setText(loc.getMenu("Cancel"));
	}

	private void updateInputField() {

		if (inputField == null) {
			inputField = new AutoCompleteTextFieldW(mApp);
		}
		inputField.setText(initialSelectionValue);

	}

	private void updateIcon() {

		if (mIcon != null) {
			return;
		}

		switch (messageType) {

		case GOptionPane.ERROR_MESSAGE:
			mIcon = new Image(
					GuiResourcesSimple.INSTANCE.dialog_error().getSafeUri());
			break;
		case GOptionPane.INFORMATION_MESSAGE:
			mIcon = new Image(
					GuiResourcesSimple.INSTANCE.dialog_info().getSafeUri());
			break;
		case GOptionPane.WARNING_MESSAGE:
			mIcon = new Image(
					GuiResourcesSimple.INSTANCE.dialog_warning().getSafeUri());
			break;
		case GOptionPane.QUESTION_MESSAGE:
			mIcon = new Image(
					GuiResourcesSimple.INSTANCE.dialog_question().getSafeUri());
			break;
		default:
		case GOptionPane.PLAIN_MESSAGE:
			mIcon = null;
			break;
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		Log.debug("source is: " + source);

		if (source == btnOK) {
			Log.debug("btnOk");
			returnOption = GOptionPane.OK_OPTION;
			close();
		}

		if (source == btnCancel) {
			Log.debug("btnCancel");
			returnOption = GOptionPane.CANCEL_OPTION;
			close();
		}

		if (optionButtons == null) {
			return;
		}

		for (int i = 0; i < optionButtons.length; i++) {
			if (source == optionButtons[i]) {
				returnOption = i;
				close();
			}
		}

	}

	/**
	 * Close the dialog on key events ENTER or ESC.
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);

		if (!isShowing()) {
			return;
		}
		if (event.getTypeInt() == Event.ONKEYDOWN) {
			keyDown = true;
		}
		if (event.getTypeInt() == Event.ONKEYUP && keyDown) {
			keyDown = false;
			int keyCode = event.getNativeEvent().getKeyCode();
			if (keyCode == KeyCodes.KEY_ESCAPE) {
				returnOption = GOptionPane.CANCEL_OPTION;
				close();

			} else if (keyCode == KeyCodes.KEY_ENTER) {
				returnOption = optionType == GOptionPane.CUSTOM_OPTION
						? enterOption : GOptionPane.OK_OPTION;
				close();
			}
		}
	}

	// ===================================================================
	// Dialog Launching Methods
	// ===================================================================

	/**
	 * Launches a confirm dialog.
	 */
	public void showConfirmDialog(App app, String message, String title,
			int optionType, int messageType, Image icon) {

		this.mApp = app;
		this.mMessage = message;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.mIcon = icon;

		this.okLabel = null;

		this.returnHandler = null;
		requiresReturnValue = false;

		showDialog(true);
		showDialog(true);
	}

	/**
	 * @param app
	 *            application
	 * @param scrollPanel
	 *            content scroll pane
	 * @param title
	 *            title
	 * @param optionType
	 *            options
	 * @param messageType
	 *            message type
	 * @param okLabel
	 *            label for OK button
	 * @param icon
	 *            icon
	 * @param returnHandler
	 *            handler for clicked buttons
	 */
	public void showConfirmDialog(App app, ScrollPanel scrollPanel,
			String title,
			int optionType, int messageType, String okLabel, Image icon,
			AsyncOperation<String[]> returnHandler) {

		this.mApp = app;
		this.scrollPanel = scrollPanel;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.mIcon = icon;

		this.okLabel = okLabel;
		this.returnHandler = returnHandler;

		// this.returnHandler = null;
		requiresReturnValue = false;

		this.addStyleName("centerDialog");
		showDialog(true);
		showDialog(true);
	}

	/**
	 * Launches a customizable option dialog.
	 * 
	 * The dialog result is returned in the parameter of the handler callback
	 * function as an array of two strings: <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue <br>
	 * 
	 * (Note that returnValue is meaningless here.)
	 */
	public void showOptionDialog(App app, String message, String title,
			int enterOption, int messageType, Object icon, String[] optionNames,
			AsyncOperation<String[]> handler) {

		this.mApp = app;
		this.mMessage = message;
		this.title = title;
		this.optionType = GOptionPane.CUSTOM_OPTION;
		this.enterOption = enterOption;
		this.messageType = messageType;
		this.mIcon = (Image) icon;
		this.scrollPanel = null;

		this.optionNames = optionNames;
		this.returnHandler = handler;
		requiresReturnValue = false;

		showDialog(true);

	}

	/**
	 * Launches a simple input dialog. The dialog result is returned in the
	 * parameter of the handler callback function as an array of two strings:
	 * <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue
	 * 
	 */
	public void showInputDialog(App app, String message,
			String initialSelectionValue, Object icon,
			AsyncOperation<String[]> handler) {

		this.mApp = app;
		this.mMessage = message;
		this.title = null;
		this.optionType = GOptionPane.OK_CANCEL_OPTION;
		this.messageType = GOptionPane.PLAIN_MESSAGE;
		this.mIcon = (Image) icon;

		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(true);
	}

	/**
	 * Simple download as dialog.
	 * 
	 * @param app
	 *            application
	 * @param title
	 *            title
	 * @param initialSelectionValue
	 *            initial input value (filename)
	 * @param icon
	 *            icon
	 * @param handler
	 *            button handler
	 * @param okLabel
	 *            label for OK
	 */
	public void showSaveDialog(App app, String title,
			String initialSelectionValue, Object icon,
			AsyncOperation<String[]> handler, String okLabel) {

		this.mApp = app;
		this.mMessage = "";
		this.title = title;
		this.okLabel = okLabel;
		this.optionType = GOptionPane.OK_CANCEL_OPTION;
		this.messageType = GOptionPane.PLAIN_MESSAGE;
		this.mIcon = (Image) icon;

		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(false);

	}

	/**
	 * Launches a customizable input dialog. The dialog result is returned in
	 * the parameter of the handler callback function as an array of two
	 * strings: <br>
	 * 
	 * dialogResult[0] = returnOption <br>
	 * dialogResult[1] = returnValue
	 * 
	 */
	public void showInputDialog(App app, String message, String title,
			String initialSelectionValue, int optionType, int messageType,
			Object icon, String[] optionNames,
			AsyncOperation<String[]> handler) {

		this.mApp = app;
		this.mMessage = message;
		this.title = title;
		this.optionType = optionType;
		this.messageType = messageType;
		this.mIcon = (Image) icon;

		this.optionNames = optionNames;
		this.initialSelectionValue = initialSelectionValue;
		this.returnHandler = handler;
		requiresReturnValue = true;

		showDialog(true);

	}

}
