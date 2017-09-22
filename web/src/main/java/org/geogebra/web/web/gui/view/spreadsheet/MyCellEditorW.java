package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.event.KeyEventsHandler;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.web.MathFieldW;

//import geogebra.web.gui.virtualkeyboard.VirtualKeyboard;

/**
 * Default cell editor for the spreadsheet, extends
 * DefaultCellEditor(JTextField)
 * 
 */
public class MyCellEditorW implements BaseCellEditor {

	protected Kernel kernel;
	protected AppW app;

	protected GeoElementND value;
	protected MyTableW table;
	AutoCompleteTextFieldW autoCompleteTextField;

	protected int column = -1;
	protected int row = -1;
	private boolean editing = false;

	private boolean allowProcessGeo = false;
	private boolean enableAutoComplete = false;

	private SpreadsheetCellEditorKeyListener keyListener;

	private boolean allowAutoEdit;

	private SpreadsheetController controller;
	// keep track of when <tab> was first pressed
	// so we can return to that column when <enter> pressed
	private int tabReturnCol = -1;

	public boolean allowProcessGeo() {
		return allowProcessGeo;
	}

	public void setAllowProcessGeo(boolean allowProcessGeo) {
		this.allowProcessGeo = allowProcessGeo;
	}

	public boolean isEnableAutoComplete() {
		return enableAutoComplete;
	}

	public void setEnableAutoComplete(boolean enableAutoComplete) {
		this.enableAutoComplete = enableAutoComplete;
		autoCompleteTextField.setAutoComplete(enableAutoComplete);
	}

	public MyCellEditorW(Kernel kernel,
			SimplePanel editorPanel, SpreadsheetController controller) {
		this.controller = controller;
		this.kernel = kernel;
		app = (AppW) kernel.getApplication();
		keyListener = new SpreadsheetCellEditorKeyListener(false);
		autoCompleteTextField = new AutoCompleteTextFieldW(0,
		        (AppW) kernel.getApplication(), false, keyListener, false);
		autoCompleteTextField.setAutoComplete(enableAutoComplete);
		autoCompleteTextField.setStyleName("SpreadsheetEditorCell");
		editorPanel.add(autoCompleteTextField);
	}

	public void setText(String text) {
		if (!autoCompleteTextField.hasFocus() && !table.isDragging) {
			autoCompleteTextField.setText(text);
		}

	}

	public Widget getTableCellEditorWidget(MyTableW table0, Object value0,
			boolean isSelected, int row0, int column0) {

		table = table0;

		if (value0 instanceof String) { // clicked to type
			value = null;
		} else {
			value = (GeoElement) value0;
		}

		column = column0;
		row = row0;
		String text = "";

		if (value != null) {
			text = controller.getEditorInitString(value);
			int index = text.indexOf("=");
			if ((!value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;
				}
			}
		}

		autoCompleteTextField.setText(text);
		// autoCompleteTextField.setFont(app.getFontCanDisplay(text));
		autoCompleteTextField.requestFocus();

		editing = true;

		return autoCompleteTextField;
	}

	/**
	 * set flag to require text start with "=" to activate autocomplete
	 */
	public void setEqualsRequired(boolean equalsRequired) {
		autoCompleteTextField.setEqualsRequired(equalsRequired);
	}

	/**
	 * returns flag that requires text start with "=" to activate autocomplete
	 */
	public boolean isEqualsRequired() {
		return autoCompleteTextField.isEqualsRequired();
	}

	public void setLabels() {
		autoCompleteTextField.setDictionary(false);
	}
	
	public boolean textStartsWithEquals() {
		String text = getEditingValue();
		return text.startsWith("=");
	}

	/**
	 * 
	 * @return true if the completion popup is open
	 */
	public boolean completionsPopupOpen() {
		return autoCompleteTextField.getCompletions() != null;
	}

	// =======================================================
	// In-cell Editing Methods
	// =======================================================

	public boolean isEditing() {
		return editing;
	}

	public int getCaretPosition() {
		return autoCompleteTextField.getCaretPosition();
	}

	/** Insert a geo label into current editor string. */
	public void addLabel(String label) {
		if (!editing) {
			return;
		}
		// String text = (String) delegate.getCellEditorValue();
		// delegate.setValue(text + label);
		autoCompleteTextField.insertString(" " + label + " ");
	}

	public void setLabel(String text) {
		if (!editing) {
			return;
		}
		autoCompleteTextField.setText(text);
	}

	public String getEditingValue() {
		return autoCompleteTextField.getText();
	}

	public Object getCellEditorValue() {
		return value;
	}

	// =======================================================
	// Stop/Cancel Editing
	// =======================================================

	@Override
	public void cancelCellEditing() {
		editing = false;
		
		if (table != null) { // ?
			table.finishEditing(false);
		}
	}

	public boolean stopCellEditing() {


		if (autoCompleteTextField.hasDummyCursor()) {
			autoCompleteTextField.removeDummyCursor();
		}

		// try to redefine or create the cell geo with the current editing
		// string
		if (!processGeo()) {
			return false;
		}

		editing = false;
		boolean success = true;// TODO super.stopCellEditing();

		return success;
	}

	boolean stopCellEditing(int colOff, int rowOff, boolean editNext) {
		allowProcessGeo = true;
		boolean success = stopCellEditing();
		moveSelectedCell(colOff, rowOff);
		allowProcessGeo = false;
		table.finishEditing(editNext); // don't finish, we
		if (editNext) {
			table.setAllowEditing(true);
			table.editCellAt(row + rowOff, column + colOff);
			table.setAllowEditing(false);
			// this should be deferred so that browser cannot steal focus from
			// SS
			autoCompleteTextField.getTextField().setFocus(true);


		}

		return success;
	}

	private void moveSelectedCell(int colOff, int rowOff) {
		int nextRow = Math.min(row + rowOff, table.getRowCount());
		int nextColumn = Math.min(column + colOff, table.getColumnCount());
		table.setSelection(nextColumn, nextRow);
	}

	/**
	 * Attempts to create or redefine the cell geo using the current editing
	 * string
	 * 
	 * @return
	 */
	private boolean processGeo() {

		try {

			if (allowProcessGeo) {
				String text = autoCompleteTextField.getText();// ?// (String)
												  // delegate.getCellEditorValue();
				// get GeoElement of current cell
				value = kernel.lookupLabel(GeoElementSpreadsheet
				        .getSpreadsheetCellName(column, row));

				if ("".equals(text)) {
					if (value != null) {
						value.removeOrSetUndefinedIfHasFixedDescendent();
						value = null;
					}

				} else {
					GeoElementND newVal = RelativeCopy
					        .prepareAddingValueToTableNoStoringUndoInfo(kernel,
									app, text, value, column, row, false);
					if (newVal == null) {
						return false;
					}
					value = newVal;
				}

				if (value != null) {
					app.storeUndoInfo();
				}
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			// TODO super.stopCellEditing();
			editing = false;
			return false;
		}
		return true;
	}

	// =======================================================
	// Key and Focus Listeners
	// =======================================================

	public void sendKeyPressEvent(KeyPressEvent e) {
		autoCompleteTextField.getTextField().setFocus(true);
		keyListener.onKeyPress(e);
	}

	public void sendKeyDownEvent(KeyDownEvent e) {
		autoCompleteTextField.getTextField().setFocus(true);
		keyListener.onKeyDown(e);
	}

	public class SpreadsheetCellEditorKeyListener implements KeyEventsHandler {

		// boolean escape = false;
		boolean isFormulaBarListener;

		public SpreadsheetCellEditorKeyListener(boolean isFormulaBarListener) {
			this.isFormulaBarListener = isFormulaBarListener;
		}

		@Override
		public void onKeyDown(KeyDownEvent e) {

			// stopping propagation is needed to prevent duplicate events
			e.stopPropagation();

			checkCursorKeys(e);
			int keyCode = e.getNativeKeyCode();

			switch (keyCode) {
			default:
				// do nothing
				break;
			case KeyCodes.KEY_ESCAPE:
				e.preventDefault();
				GeoElement oldGeo = kernel.getGeoAt(column, row);
				cancelCellEditing();

				// restore old text in spreadsheet
				table.getModel().setValueAt(oldGeo, row, column);

				// stopCellEditing(0,0);
				// force nice redraw
				table.setSelection(column, row);

				// update the formula bar after escape
				// ?//table.getView().updateFormulaBar();

				break;

			}
		}

		@Override
		public void onKeyPress(KeyPressEvent e) {
			// iOS: we do receive the event but nothing is actually printed
			// because focus moved from dummy textarea into editor
			if (MathFieldW.checkCode(e.getNativeEvent(), "NumpadDecimal")) {
				autoCompleteTextField.insertString(".");
				e.preventDefault();
				e.stopPropagation();
				return;
			}
			final String charcode = e.getCharCode() + "";
			if (MyCellEditorW.this.allowAutoEdit) {
				app.invokeLater(new Runnable() {

					@Override
					public void run() {
						String text = autoCompleteTextField.getText();
						if (text == null || text.length() == 0) {
							autoCompleteTextField.setText(charcode);
						}
					}
				});
				MyCellEditorW.this.allowAutoEdit = false;
			}
			
			// stopping propagation is needed to prevent
			// the prevention of the default action at another place
			e.stopPropagation();
		}

		@Override
		public void onKeyUp(KeyUpEvent e) {
			// stopping propagation may be needed in strange browsers
			// this also makes sure no top-level action is done on keyUp
			// but the default action of the event should have already been
			// expired
			e.stopPropagation();
		}

		public void checkCursorKeys(KeyDownEvent e) {

			String text = autoCompleteTextField.getText();// ?// (String)
											  // delegate.getCellEditorValue();

			int keyCode = e.getNativeKeyCode();
			// Application.debug(e+"");
			switch (keyCode) {
			default:
				// do nothing
				break;
			case KeyCodes.KEY_UP:
			
				if(isSuggesting()){
					return;
				}
				if (isFormulaBarListener) {
					return;
				}

				// Application.debug("UP");
				stopCellEditing(0, -1, false);
				// ?//e.consume();
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_TAB:
				if (isFormulaBarListener) {
					return;
				}
				Log.debug(" tab");
				// Application.debug("RIGHT");
				// shift-tab moves left
				// tab moves right
				if (getTabReturnCol() == -1) {
					setTabReturnCol(column);
				}
				stopCellEditing(e.isShiftKeyDown() ? -1 : 1, 0, false);
				e.preventDefault();
				break;

			case KeyCodes.KEY_ENTER:
				
				if(isSuggesting()){
					return;
				}
				
				// if incomplete command entered, want to move the cursor to
				// between []
				int bracketsIndex = text.indexOf("[]");
				if (bracketsIndex == -1) {

					if (getTabReturnCol() != -1) {
						int colOffset = getTabReturnCol() - column;
						stopCellEditing(colOffset, 1, true);
					} else {

						// TODO: in desktop this works with column, row + 1
						String cellBelowStr = GeoElementSpreadsheet
						        .getSpreadsheetCellName(column, row + 1);
						GeoElement cellBelow = kernel.getConstruction()
						        .lookupLabel(cellBelowStr);

						boolean moveDown = cellBelow == null
								|| !cellBelow.isProtected(EventType.UPDATE);

						// don't move down to cell below after <Enter> if it's
						// fixed
						stopCellEditing(0, moveDown ? 1 : 0, moveDown);

					}
				} else {
					autoCompleteTextField.setCaretPosition(bracketsIndex + 1);
					// ?//e.consume();
				}

				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_DOWN:
				
				if(isSuggesting()){
					return;
				}
				
				if (isFormulaBarListener) {
					// ?//e.consume();
					return;
				}
				// Application.debug("DOWN");
				stopCellEditing(0, 1, false);
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_LEFT:
				if (isFormulaBarListener) {
					return;
				}
				// Application.debug("LEFT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == 0) {
					stopCellEditing(-1, 0, false);
				}
				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_RIGHT:
				if (isFormulaBarListener) {
					return;
				}
				// Application.debug("RIGHT");
				// Allow left/right keys to exit cell for easier data entry
				if (getCaretPosition() == text.length()) {
					stopCellEditing(1, 0, false);
				}

				setTabReturnCol(-1);
				break;

			case KeyCodes.KEY_PAGEDOWN:
			case KeyCodes.KEY_PAGEUP:
				e.preventDefault();
				// ?//e.consume();
				setTabReturnCol(-1);
				break;

			// An F1 keypress causes the focus to be lost, so we
			// need to set 'editing' to false to prevent the focusLost()
			// method from calling stopCellEditing()
			// ?//case KeyEvent.VK_F1:
			// ?// editing = false;
			// ?// break;

			}

		}

	}

	public Widget getTextfield() {
		return autoCompleteTextField;
	}
	
	boolean isSuggesting(){
		return autoCompleteTextField.isSuggesting();
	}

	public void allowAutoEdit() {
		this.allowAutoEdit = true;

	}

	protected int getTabReturnCol() {
		return tabReturnCol;
	}

	protected void setTabReturnCol(int tabReturnCol) {
		this.tabReturnCol = tabReturnCol;
	}

	public void onEnter() {
		if (tabReturnCol > -1) {
			table.changeSelection(row, tabReturnCol, false);
			setTabReturnCol(-1);
		}

	}

	/**
	 * Selects all the text in editor.
	 */
	public void selectAll() {
		autoCompleteTextField.getTextField().setFocus(true);
		autoCompleteTextField.selectAll();
	}
}
