package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.dialog.InputDialog;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.OptionType;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class InputDialogW extends InputDialog implements ClickHandler,
        SetLabels {

	protected AppW app;

	public static final int DEFAULT_COLUMNS = 30;
	public static final int DEFAULT_ROWS = 10;

	protected InputPanelW inputPanel;

	protected Button btApply, btProperties, btCancel, btOK, btHelp;
	protected DialogBox wrappedPopup;

	protected GeoElement geo;

	private CheckBox checkBox;

	private String title;

	public InputDialogW(boolean modal) {

		wrappedPopup = new DialogBox(false, false);
		wrappedPopup.addStyleName("DialogBox");
	}

	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        boolean modal, boolean selectInitText, GeoElement geo) {

		this(app, message, title, initString, autoComplete, handler, modal,
		        selectInitText, geo, null, DialogType.GeoGebraEditor);
	}

	/**
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param autoComplete
	 * @param handler
	 * @param modal
	 * @param selectInitText
	 * @param geo
	 * @param checkBox
	 * @param type
	 */
	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        boolean modal, boolean selectInitText, GeoElement geo,
	        CheckBox checkBox, DialogType type) {

		this(modal);

		this.app = app;
		this.geo = geo;
		this.inputHandler = handler;
		this.initString = initString;
		this.checkBox = checkBox;

		createGUI(title, message, autoComplete, DEFAULT_COLUMNS, 1, true,
		        selectInitText, geo != null, geo != null, type);

		centerOnScreen();

	}

	public InputDialogW(AppW app, String message, String title,
	        String initString, boolean autoComplete, InputHandler handler,
	        GeoElement geo) {
		this(app, message, title, initString, autoComplete, handler, false,
		        false, geo);
	}

	private void centerOnScreen() {
		wrappedPopup.center();
	}

	/**
	 * @param title
	 * @param message
	 * @param autoComplete
	 * @param columns
	 * @param rows
	 * @param showSymbolPopupIcon
	 * @param selectInitText
	 * @param showProperties
	 * @param showApply
	 * @param type
	 */
	protected void createGUI(String title, String message,
	        boolean autoComplete, int columns, int rows,
	        boolean showSymbolPopupIcon, boolean selectInitText,
	        boolean showProperties, boolean showApply, DialogType type) {

		this.title = title;

		// Create components to be displayed
		inputPanel = new InputPanelW(initString, app, rows, columns,
		        showSymbolPopupIcon/* , type */);

		// create buttons
		btProperties = new Button();
		btProperties.addClickHandler(this);
		// btProperties.setActionCommand("OpenProperties");
		// btProperties.addActionListener(this);

		btOK = new Button();
		btOK.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btOK.addClickHandler(this);

		btCancel = new Button();
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		btApply = new Button();
		btApply.addClickHandler(this);
		// btApply.setActionCommand("Apply");
		// btApply.addActionListener(this);

		// create button panel
		FlowPanel btPanel = new FlowPanel();
		btPanel.addStyleName("DialogButtonPanel");
		btPanel.add(btOK);
		btPanel.add(btCancel);
		// just tmp.
		if (showApply) {
			btPanel.add(btApply);
		}
		// if (showProperties) {
		// btPanel.add(btProperties);
		// }

		setLabels();

		// =====================================================================
		// Create the optionPane: a panel with message label on top, button
		// panel on bottom. The center panel holds the inputPanel, which is
		// added later.
		// =====================================================================

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputPanel);
		centerPanel.add(btPanel);

		wrappedPopup.setWidget(centerPanel);

	}

	public void onClick(ClickEvent event) {
		Widget source = (Widget) event.getSource();
		if (source == btOK) {
			inputText = inputPanel.getText();
			setVisible(!processInputHandler());
		} else if (source == btApply) {
			inputText = inputPanel.getText();
			processInputHandler();
		} else if (source == btProperties && geo != null) {
			setVisible(false);
			tempArrayList.clear();
			tempArrayList.add(geo);
			app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
			        tempArrayList);
		} else if (source == btCancel) {
			setVisible(false);
		}
	}

	public void setVisible(boolean visible) {
		wrappedPopup.setVisible(visible);
		if (visible)
			inputPanel.getTextComponent().getTextBox().getElement().focus();
	}

	public void setLabels() {
		wrappedPopup.setText(title);
		btOK.setText(app.getPlain("OK"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getPlain("Cancel"));
		btProperties.setText(app.getPlain("OpenProperties"));
	}

}
