package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTableCellController;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.common.main.GWTKeycodes;
import geogebra.html5.gui.GuiManagerInterfaceW;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.main.AppW;
import geogebra.html5.util.EventUtil;
import geogebra.web.gui.GuiManagerW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class CASTableControllerW extends CASTableCellController implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler,
        KeyHandler, BlurHandler, TouchStartHandler, TouchEndHandler,
        TouchMoveHandler, LongTouchHandler, KeyDownHandler {

	private CASViewW view;
	private AppW app;
	private int startSelectRow;
	
	private LongTouchManager longTouchManager;
	private boolean mouseDown;
	private boolean touchDown;
	
	private boolean contextOpened;

	public CASTableControllerW(CASViewW casViewW, AppW app) {
		view = casViewW;
		this.app = app;
		longTouchManager = LongTouchManager.getInstance();
	}
	
	public void handleLongTouch(int x, int y) {
		CASTableW table = view.getConsoleTable();
		if (!table.isSelectedIndex(startSelectRow)) {
			table.setSelectedRows(startSelectRow, startSelectRow);
		}
		if (table.getSelectedRows().length > 0) {
			RowHeaderPopupMenuW popupMenu = ((GuiManagerW) app
			        .getGuiManager()).getCASContextMenu(null, table);
			popupMenu.show(new GPoint(x, y));
			contextOpened = true;
		}
	}
	
	/**
	 * Sets the toolbar to CAS
	 */
	private void setActiveToolbar() {
		if (app.getToolbar() != null) {
			GuiManagerInterfaceW gm = app.getGuiManager();
			gm.setActiveToolbarId(App.VIEW_CAS);
		}
	}

	/**
	 * Copies the output of a cell into the cell being edited
	 * if there is an editing cell and a cell output was clicked.
	 * @param event event
	 * @return true if copying happened
	 */
	private boolean copyOutputIfSource(HumanInputEvent<?> event) {
		if (event.getSource() != view.getComponent()) {
			CASTableW table = view.getConsoleTable();
			CASTableCellW clickedCell = table.getCasCellForEvent(event);
			return copyOutputToEditingCell(clickedCell);
		}
		return false;
	}
	
	private boolean copyOutputToEditingCell(CASTableCellW clickedCell) {
		CASTableW table = view.getConsoleTable();
		CASTableCellW editingCell = table.getEditingCell();
		if (editingCell != null && clickedCell != null) {
			editingCell.insertInput(clickedCell.getOutputString());
			return true;
		}
		return false;
	}

	public void onMouseMove(MouseMoveEvent event) {
		handleMouseMoveSelection(event);
		event.stopPropagation();
	}

	public void onMouseUp(MouseUpEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseDown = false;
		onPointerUp(event);
		event.stopPropagation();
		
	}

	public void onMouseDown(MouseDownEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseDown = true;
		handleMouseDownSelection(event);
		onPointerDown();
		
		event.stopPropagation();
	}

	private void onPointerDown() {
		setActiveToolbar();
		((GuiManagerW) app.getGuiManager()).removePopup();
	}
	
	private void onPointerUp(HumanInputEvent<?> event) {
		if (copyOutputIfSource(event)) {
			event.stopPropagation();
			return;
		}
		CASTableW table = view.getConsoleTable();
		Cell cell = table.getCellForEvent(event);
		table.setFirstRowFront(false);
		if (cell == null) {
			return;
		}
		if (cell.getCellIndex() == CASTableW.COL_CAS_CELLS_WEB) {
			int rowIndex = cell.getRowIndex();
			table.startEditingRow(rowIndex);
		}
	}

	public void onTouchMove(TouchMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || startSelectRow < 0) {
			longTouchManager.cancelTimer();
			return;
		}
		longTouchManager.rescheduleTimerIfRunning(this, EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		handleTouchMoveSelection(event);
		CancelEventTimer.touchEventOccured();
	}

	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		touchDown = false;
		if (!contextOpened) {
			onPointerUp(event);
		} else {
			contextOpened = false;
		}
		CancelEventTimer.touchEventOccured();
	}

	public void onTouchStart(TouchStartEvent event) {
		handleTouchStartSelection(event);
		touchDown = true;
		longTouchManager.scheduleTimer(this, 
				EventUtil.getTouchOrClickClientX(event),
		        EventUtil.getTouchOrClickClientY(event));
		onPointerDown();
		CancelEventTimer.touchEventOccured();
	}

	private void handleMouseDownSelection(MouseDownEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || point.getX() != CASTableW.COL_CAS_HEADER) {
			startSelectRow = -1;
			return;
		}
		int currentRow = point.getY();
		if (event.getNativeButton() == Event.BUTTON_RIGHT && selectionContainsRow(currentRow)) {
			// do nothing
		} else if (event.isShiftKeyDown()) {
			table.setSelectedRows(startSelectRow, currentRow);
		} else if (event.isControlKeyDown()) {
			table.addSelectedRows(currentRow, currentRow);
		} else {
			startSelectRow = currentRow;
			table.setSelectedRows(currentRow, currentRow);
		}
	}
	
	private boolean selectionContainsRow(int row) {
		CASTableW table = view.getConsoleTable();
		for (Integer item : table.getSelectedRows()) {
			if (item.equals(row))
				return true;
		}
		return false;
	}

	private void handleMouseMoveSelection(MouseMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || point.getX() != CASTableW.COL_CAS_HEADER || startSelectRow < 0 || !mouseDown) {
			return;
		}
		int currentRow = point.getY();
		if (event.isControlKeyDown()) {
			table.setSelectedRows(startSelectRow, currentRow);
		} else {
			table.addSelectedRows(currentRow, currentRow);
		}
	}
	
	private void handleTouchStartSelection(TouchStartEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null) {
			this.startSelectRow = -1;
			return;
		}
		int currentRow = point.getY();
		startSelectRow = currentRow;
		table.setSelectedRows(currentRow, currentRow);
	}
	
	private void handleTouchMoveSelection(TouchMoveEvent event) {
		CASTableW table = view.getConsoleTable();
		GPoint point = table.getPointForEvent(event);
		if (point == null || startSelectRow < 0 || !touchDown) {
			return;
		}
		int currentRow = point.getY();
		table.setSelectedRows(startSelectRow, currentRow);
	}

	public void keyReleased(KeyEvent e) {
		char ch = e.getCharCode();
		CASTableW table = view.getConsoleTable();
		int editingRow = table.getEditingRow();
		if (editingRow < 0) {
			App.debug("No row is being edited.");
			return;
		}
		CASTableCellEditorW editor = table.getEditor();
		String text = editor.getInput();
		// if closing paranthesis is typed and there is no opening parenthesis
		// for it
		// add one in the beginning
		switch (ch) {
		case ' ':
		case '|':
			// insert output of previous row (not in parentheses)
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate) + " ");
				e.preventDefault();
			}
			break;

		case ')':
			// insert output of previous row in parentheses
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				String prevOutput = selCellValue
				        .getOutputRHS(StringTemplate.defaultTemplate);
				editor.setInput("(" + prevOutput + ")");
				e.preventDefault();
			}
			break;

		case '=':
			// insert input of previous row
			if (editingRow > 0 && text.length() == 0) {
				GeoCasCell selCellValue = view.getConsoleTable().getGeoCasCell(
				        editingRow - 1);
				editor.setInput(selCellValue
				        .getInput(StringTemplate.defaultTemplate));
				e.preventDefault();
			}
			break;
		}
		if (e.isEnterKey()) {
			this.handleEnterKey(e, app);
			e.preventDefault();
		}
	}

	public void onBlur(BlurEvent event) {
		view.getConsoleTable().stopEditing();
		view.getConsoleTable().setFirstRowFront(false);
	}
	
	private Cell getCellForEvent(HumanInputEvent<?> event) {
		CASTableW table = view.getConsoleTable();
		return table.getCellForEvent(event);
	}

	public void onKeyDown(KeyDownEvent event) {
	    if (event.isControlKeyDown() && event.getNativeKeyCode() == GWTKeycodes.KEY_A) {
	    	CASTableCellEditorW editor = view.getConsoleTable().getEditor();
	    	editor.selectAll();
	    }
    }
}
