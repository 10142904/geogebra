package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 */
public class SpreadsheetRowHeaderW implements MouseDownHandler, MouseUpHandler,
        MouseMoveHandler, ClickHandler, DoubleClickHandler, KeyDownHandler

{
	private static final long serialVersionUID = 1L;
	private AppW app;
	private SpreadsheetViewW view;
	private MyTableW table;
	private Grid grid;
	private FlowPanel container;

	private FocusPanel focusPanel;

	private int mouseYOffset, resizingRow = -1;
	private boolean doRowResize = false;

	protected int row0 = -1;

	private boolean isMouseDown = false;

	/**
	 * @param app
	 * @param table
	 */
	public SpreadsheetRowHeaderW(AppW app, MyTableW table) {

		this.app = app;
		this.table = table;
		this.view = (SpreadsheetViewW) table.getView();

		prepareGUI();
		registerListeners();

		/*
		 * setFocusable(true); setAutoscrolls(false); addMouseListener(this);
		 * addMouseMotionListener(this); addKeyListener(this);
		 * setFixedCellWidth(SpreadsheetView.ROW_HEADER_WIDTH);
		 * 
		 * setCellRenderer(new RowHeaderRenderer(table, this));
		 * 
		 * table.getSelectionModel().addListSelectionListener(this);
		 */

	}

	// ============================================
	// GUI handlers
	// ============================================

	private void registerListeners() {

		grid.addDomHandler(this, MouseDownEvent.getType());
		grid.addDomHandler(this, MouseUpEvent.getType());
		grid.addDomHandler(this, MouseMoveEvent.getType());
		grid.addDomHandler(this, ClickEvent.getType());
		grid.addDomHandler(this, DoubleClickEvent.getType());

	}

	private void prepareGUI() {

		grid = new Grid(table.getModel().getRowCount(), 1);

		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		grid.setHeight("0px");

		grid.getElement().addClassName("geogebraweb-table-spreadsheet");

		grid.getColumnFormatter().getElement(0).getStyle()
		        .setWidth(view.ROW_HEADER_WIDTH, Style.Unit.PX);

		for (int row = 0; row < grid.getRowCount(); row++) {
			initializeCell(row);
		}

		focusPanel = new FocusPanel();
		focusPanel.addKeyDownHandler(this);
		Style s = focusPanel.getElement().getStyle();
		// s.setDisplay(Style.Display.NONE);
		s.setPosition(Style.Position.ABSOLUTE);
		s.setTop(0, Unit.PX);
		s.setLeft(0, Unit.PX);

		container = new FlowPanel();
		container.add(grid);
		container.add(focusPanel);
	}

	private void initializeCell(int rowIndex) {

		grid.setText(rowIndex, 0, (rowIndex + 1) + "");

		int rowHeight = app.getSettings().getSpreadsheet().preferredRowHeight();
		setRowHeight(rowIndex, rowHeight);

		Element elm = grid.getCellFormatter().getElement(rowIndex, 0);

		elm.addClassName("SVheader");
		elm.getStyle().setBackgroundColor(
		        MyTableW.BACKGROUND_COLOR_HEADER.toString());
	}

	/**
	 * updates header row count to match table row count
	 */
	public void updateRowCount() {

		if (grid.getRowCount() >= table.getRowCount())
			return;

		int oldRowCount = grid.getRowCount();
		grid.resizeRows(table.getRowCount());

		for (int i = oldRowCount; i < table.getRowCount(); ++i) {
			initializeCell(i);
		}
	}

	// ============================================
	// Getters/Setters
	// ============================================

	public Widget getContainer() {
		return container;
	}

	private String getCursor() {
		return grid.getElement().getStyle().getCursor();
	}

	private void setRowResizeCursor() {
		grid.getElement().getStyle().setCursor(Style.Cursor.ROW_RESIZE);
	}

	private void setDefaultCursor() {
		grid.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
	}

	/**
	 * @param rowIndex
	 *            index of row to set height
	 * @param rowHeight
	 *            new row height
	 */
	public void setRowHeight(int rowIndex, int rowHeight) {

		if (rowIndex >= grid.getRowCount()) {
			return;
		}

		grid.getRowFormatter().getElement(rowIndex).getStyle()
		        .setHeight(rowHeight, Style.Unit.PX);
	}

	/**
	 * Renders selected and unselected rows
	 */
	public void renderSelection() {

		String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();

		for (int rowIndex = 0; rowIndex < grid.getRowCount(); rowIndex++) {
			Style s = grid.getCellFormatter().getElement(rowIndex, 0)
			        .getStyle();

			if (table.getSelectionType() == MyTable.COLUMN_SELECT) {
				setBgColorIfNeeded(s, defaultBackground);
			} else {
				if (table.selectedRowSet.contains(rowIndex)
				        || (rowIndex >= table.minSelectionRow && rowIndex <= table.maxSelectionRow)) {
					setBgColorIfNeeded(s, selectedBackground);
				} else {
					setBgColorIfNeeded(s, defaultBackground);
				}
			}
		}
	}

	private static void setBgColorIfNeeded(Style s, String bgColor) {
		if (!s.getBackgroundColor().equals(bgColor))
			s.setBackgroundColor(bgColor);
	}

	/**
	 * Update the rowHeader list when row selection changes in the table
	 */
	/*
	 * public void valueChanged(ListSelectionEvent e) { ListSelectionModel
	 * selectionModel = (ListSelectionModel) e.getSource(); minSelectionRow =
	 * selectionModel.getMinSelectionIndex(); maxSelectionRow =
	 * selectionModel.getMaxSelectionIndex(); repaint(); }
	 */

	/**
	 * @param p
	 *            location of mouse (in client area pixels)
	 * @return index of the row to be resized if mouse point p is near a row
	 *         boundary (within 3 pixels)
	 */
	private int getResizingRow(GPoint p) {
		int resizeRow = -1;
		GPoint point = table.getIndexFromPixel(0, p.y);
		if (point != null) {
			// test if mouse is 3 pixels from row boundary
			int cellRow = point.getY();

			if (cellRow >= 0) {
				GRectangle r = table.getCellRect(cellRow, 0, true);
				// App.debug("cell row = " + cellRow + " p.y = " + p.y +
				// "   r.y = " + r.getY() + "r.height = " + r.getHeight());
				// near row bottom ?
				if (p.y < r.getY() + 3) {
					resizeRow = cellRow - 1;
				}
				// near row top ?
				if (p.y > r.getY() + r.getHeight() - 3) {
					resizeRow = cellRow;
				}
			}
		}
		return resizeRow;
	}

	public static int getAbsoluteX(MouseEvent e, AppW app) {
		return (int) ((e.getClientX() + Window.getScrollLeft()) / app
		        .getArticleElement().getScaleX());
	}

	public int getAbsoluteX(MouseEvent e) {
		return getAbsoluteX(e, app);
	}

	public static int getAbsoluteY(MouseEvent e, AppW app) {
		return (int) ((e.getClientY() + Window.getScrollTop()) / app
		        .getArticleElement().getScaleY());
	}

	public int getAbsoluteY(MouseEvent e) {
		return getAbsoluteY(e, app);
	}

	// ===============================================
	// Mouse Listeners
	// ===============================================

	/*
	 * public void mouseClicked(MouseEvent e) {
	 * 
	 * // Double clicking on a row boundary auto-adjusts the // height of the
	 * row above the boundary (the resizingRow)
	 * 
	 * if (resizingRow >= 0 && !AppD.isRightClick(e) && e.getClickCount() == 2)
	 * { table.fitRow(resizingRow); e.consume(); } }
	 * 
	 * public void mouseEntered(MouseEvent e) { }
	 * 
	 * public void mouseExited(MouseEvent e) { }
	 */

	public void onMouseDown(MouseDownEvent e) {

		isMouseDown = true;
		e.preventDefault();

		requestFocus();

		boolean shiftPressed = e.isShiftKeyDown();
		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);

		int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
		int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);

		// ?//if (!view.hasViewFocus())
		// ?// ((LayoutW) app.getGuiManager().getLayout()).getDockManager()
		// ?// .setFocusedPanel(App.VIEW_SPREADSHEET);

		// Update resizingRow. If nonnegative, then mouse is over a boundary
		// and it gives the row to be resized (resizing is done in
		// mouseDragged).
		GPoint p = new GPoint(x, y);
		resizingRow = getResizingRow(p);
		if (resizingRow >= 0) {
			mouseYOffset = p.y - table.getRowHeight(resizingRow);
		}

		// left click
		if (!rightClick) {

			if (resizingRow >= 0)
				return;

			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {

				if (table.getSelectionType() != MyTable.ROW_SELECT) {
					table.setSelectionType(MyTable.ROW_SELECT);
					// ?//requestFocusInWindow();
				}

				if (shiftPressed) {
					if (row0 != -1) {
						int row = point.getY();
						table.setRowSelectionInterval(row0, row);
					}
				}

				// ctrl-select is handled in table

				else {
					row0 = point.getY();
					table.setRowSelectionInterval(row0, row0);
				}
				table.repaint();
				renderSelection();
			}
		}

	}

	public void onMouseUp(MouseUpEvent e) {

		isMouseDown = false;
		e.preventDefault();

		boolean rightClick = (e.getNativeButton() == NativeEvent.BUTTON_RIGHT);

		if (rightClick) {
			if (!app.letShowPopupMenu())
				return;

			GPoint p = table.getIndexFromPixel(
			        SpreadsheetMouseListenerW.getAbsoluteX(e, app),
			        SpreadsheetMouseListenerW.getAbsoluteY(e, app));
			if (p == null)
				return;

			// if click is outside current selection then change selection
			if (p.getY() < table.minSelectionRow
			        || p.getY() > table.maxSelectionRow
			        || p.getX() < table.minSelectionColumn
			        || p.getX() > table.maxSelectionColumn) {

				// switch to row selection mode and select row
				if (table.getSelectionType() != MyTable.ROW_SELECT) {
					table.setSelectionType(MyTable.ROW_SELECT);
				}

				table.setRowSelectionInterval(p.getY(), p.getY());
				renderSelection();
			}

			// show contextMenu
			SpreadsheetContextMenuW contextMenu = ((GuiManagerW) app
			        .getGuiManager()).getSpreadsheetContextMenu(table);
			GPopupMenuW popup = (GPopupMenuW) contextMenu.getMenuContainer();
			popup.show(view.getFocusPanel(), e.getX(), e.getY());
			
			
		}

		// If row resize has happened, resize all other selected rows
		if (doRowResize) {

			int rowHeight = table.getRowHeight(resizingRow);
			// App.debug("doRowResiz for selection: " + rowHeight);
			// App.debug("min/max " + table.minSelectionRow + " , " +
			// table.maxSelectionRow);
			if (table.minSelectionRow != -1 && table.maxSelectionRow != -1
			        && (table.maxSelectionRow - table.minSelectionRow > 0)) {
				if (table.isSelectAll())
					table.setRowHeight(rowHeight);
				else
					for (int row = table.minSelectionRow; row <= table.maxSelectionRow; row++) {
						// App.debug("set row height row/height: " + row + " / "
						// + rowHeight);
						table.setRowHeight(row, rowHeight);
					}
			}
			table.repaint();
			table.renderSelectionDeferred();
			doRowResize = false;
		}

	}

	public void onMouseMove(MouseMoveEvent e) {

		e.preventDefault();

		// Show resize cursor when mouse is over a row boundary
		GPoint p = new GPoint(e.getClientX(), e.getClientY());
		int r = this.getResizingRow(p);
		if (r >= 0 && !getCursor().equals(Style.Cursor.ROW_RESIZE)) {
			setRowResizeCursor();
		} else if (!getCursor().equals(Style.Cursor.DEFAULT)) {
			setDefaultCursor();
		}

		if (isMouseDown) {

			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT)
				return;

			// On mouse drag either resize or select a row
			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
			
			if (resizingRow >= 0) {
				// resize row
				int newHeight = y - mouseYOffset;
				if (newHeight > 0) {
					table.setRowHeight(resizingRow, newHeight);
					// flag to resize all selected rows on mouse release
					doRowResize = true;
					table.repaint();
					renderSelection();
				}
			} else { // select row
				GPoint point = table.getIndexFromPixel(x, y);
				if (point != null) {
					int row = point.getY();
					table.setRowSelectionInterval(row0, row);

					// G.Sturr 2010-4-4
					// keep the row header updated when drag selecting multiple
					// rows
					// ?//view.updateRowHeader();
					// ?//table.scrollRectToVisible(table.getCellRect(point.y,
					// point.x,
					// ?// true));
					table.repaint();
					renderSelection();
				}
			}
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	// transfer focus to the table
	// @Override
	public void requestFocus() {
		Scheduler.get().scheduleDeferred(requestFocusCommand);
	}

	Scheduler.ScheduledCommand requestFocusCommand = new Scheduler.ScheduledCommand() {
		public void execute() {
			focusPanel.setFocus(true);
		}
	};

	// ===============================================
	// Key Listeners
	// ===============================================

	public void onKeyDown(KeyDownEvent e) {
		//App.debug("row header key down");
		e.stopPropagation();
		int keyCode = e.getNativeKeyCode();

		boolean shiftDown = e.isShiftKeyDown();
		boolean altDown = e.isAltKeyDown();
		boolean ctrlDown = e.isControlKeyDown() || e.isMetaKeyDown();

		switch (keyCode) {

		case KeyCodes.KEY_UP:
			if (shiftDown) {
				// extend the column selection
				int row = table.getLeadSelectionRow();
				table.setSelectionType(MyTableW.ROW_SELECT);
				table.changeSelection(row - 1, -1, true);
			} else {
				// select topmost cell in first column left of the selection
				if (table.minSelectionRow > 0) {
					table.setSelection(0, table.minSelectionRow - 1);
				} else {
					table.setSelection(0, table.minSelectionRow);
					// table.requestFocus();
				}
			}
			break;

		case KeyCodes.KEY_DOWN:
			if (shiftDown) {
				// extend the row selection
				int row = table.getLeadSelectionRow();
				table.setSelectionType(MyTableW.ROW_SELECT);
				table.changeSelection(row + 1, -1, true);
			} else {
				// select topmost cell in first column left of the selection
				if (table.minSelectionRow >= 0)
					table.setSelection(0, table.minSelectionRow + 1);
				else
					table.setSelection(0, table.minSelectionRow);
				// table.requestFocus();
			}
			break;

		case KeyCodes.KEY_C:
			// control + c
			if (ctrlDown && table.minSelectionRow != -1
			        && table.maxSelectionRow != -1) {
				table.copyPasteCut.copy(0, table.minSelectionRow, table
				        .getModel().getColumnCount() - 1,
				        table.maxSelectionRow, altDown);
			}
			break;

		case KeyCodes.KEY_V: // control + v
			if (ctrlDown && table.minSelectionRow != -1
			        && table.maxSelectionRow != -1) {
				boolean storeUndo = table.copyPasteCut.paste(0,
				        table.minSelectionRow, table.getModel()
				                .getColumnCount() - 1, table.maxSelectionRow);
				if (storeUndo)
					app.storeUndoInfo();
			}
			break;

		case KeyCodes.KEY_X: // control + x
			if (ctrlDown && table.minSelectionRow != -1
			        && table.maxSelectionRow != -1) {
				table.copyPasteCut.copy(0, table.minSelectionRow, table
				        .getModel().getColumnCount() - 1,
				        table.maxSelectionRow, altDown);
			}
			boolean storeUndo = table.copyPasteCut.delete(0,
			        table.minSelectionRow,
			        table.getModel().getColumnCount() - 1,
			        table.maxSelectionRow);
			if (storeUndo)
				app.storeUndoInfo();
			break;

		case KeyCodes.KEY_DELETE: // delete
		case KeyCodes.KEY_BACKSPACE: // delete on MAC
			storeUndo = table.copyPasteCut.delete(0, table.minSelectionRow,
			        table.getModel().getColumnCount() - 1,
			        table.maxSelectionRow);
			if (storeUndo)
				app.storeUndoInfo();
			break;
		}
	}

	public int getOffsetWidth() {
		return getContainer().getOffsetWidth();
	}

	public void setTop(int top) {
		container.getElement().getStyle().setTop(top, Unit.PX);
	}

	public class MyFocusPanel extends SimplePanel implements Focusable {

		public MyFocusPanel(Widget widget) {
			super(widget);
			this.getElement().setPropertyInt("tabIndex", 0);
		}

		public int getTabIndex() {
			// TODO Auto-generated method stub
			return this.getElement().getPropertyInt("tabIndex");
		}

		public void setAccessKey(char key) {
			// TODO Auto-generated method stub

		}

		public void setFocus(boolean focused) {
			this.getElement().focus();

		}

		public void setTabIndex(int index) {
			this.getElement().setPropertyInt("tabIndex", index);
		}

	}

}
