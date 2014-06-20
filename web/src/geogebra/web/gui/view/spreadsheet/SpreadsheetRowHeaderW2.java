package geogebra.web.gui.view.spreadsheet;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GPoint;
import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SpreadsheetRowHeaderW2 extends Grid implements MouseDownHandler,
        MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler
/*
 * extends JList implements MouseListener, MouseMotionListener, KeyListener,
 * ListSelectionListener
 */

{
	private static final long serialVersionUID = 1L;
	private AppW app;
	private SpreadsheetViewW view;
	private MyTableW table;

	// note: MyTable uses its own minSelectionRow and maxSelectionRow.
	// The selection listener keeps them in sync.
	private int minSelectionRow = -1;
	private int maxSelectionRow = -1;
	/*
	 * private ListSelectionModel selectionModel;
	 * 
	 * // fields for resizing rows private static Cursor resizeCursor = Cursor
	 * .getPredefinedCursor(Cursor.N_RESIZE_CURSOR); private Cursor otherCursor
	 * = resizeCursor;
	 */
	private int mouseYOffset, resizingRow = -1;
	private boolean doRowResize = false;

	protected int row0 = -1;

	private boolean isMouseDown = false;

	/***************************************************
	 * Constructor
	 */
	public SpreadsheetRowHeaderW2(AppW app, MyTableW table) {

		super(table.getModel().getRowCount(), 1);

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

	private void registerListeners() {

		addDomHandler(this, MouseDownEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, ClickEvent.getType());
		addDomHandler(this, DoubleClickEvent.getType());
	}

	private void prepareGUI() {

		setCellPadding(0);
		setCellSpacing(0);
		this.setHeight("0px");

		getElement().addClassName("geogebraweb-table-spreadsheet");

		getColumnFormatter().getElement(0).getStyle()
		        .setWidth(view.ROW_HEADER_WIDTH, Style.Unit.PX);

		for (int row = 0; row < getRowCount(); row++) {
			initializeCell(row);
		}
	}

	private void initializeCell(int rowIndex) {

		setText(rowIndex, 0, (rowIndex + 1) + "");

		int rowHeight = app.getSettings().getSpreadsheet().preferredRowHeight();
		setRowHeight(rowIndex, rowHeight);

		Element elm = getCellFormatter().getElement(rowIndex, 0);

		elm.addClassName("SVheader");
		elm.getStyle().setBackgroundColor(
		        MyTableW.BACKGROUND_COLOR_HEADER.toString());
	}

	public void updateRowCount() {

		if (getRowCount() >= table.getRowCount())
			return;

		int oldRowCount = getRowCount();
		resizeRows(table.getRowCount());

		for (int i = oldRowCount; i < table.getRowCount(); ++i) {
			initializeCell(i);
		}
	}
	
	public void setRowHeight(int rowIndex, int rowHeight){
		
		if(rowIndex >= getRowCount()){
			return;
		}
		
		getRowFormatter().getElement(rowIndex).getStyle()
		        .setHeight(rowHeight, Style.Unit.PX);
	}

	public void renderSelection() {

		String defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER.toString();
		String selectedBackground = MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
		        .toString();

		for (int rowIndex = 0; rowIndex < this.getRowCount(); rowIndex++) {
			Style s = getCellFormatter().getElement(rowIndex, 0).getStyle();

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

	// ===============================================
	// Renderer
	// ===============================================

	public class RowHeaderRenderer /*
									 * extends JLabel implements
									 * ListCellRenderer
									 */{

		private static final long serialVersionUID = 1L;

		// protected JList rowHeader;
		private GColor defaultBackground;

		public RowHeaderRenderer(/* JTable table, JList rowHeader */) {
			// super("", SwingConstants.CENTER);
			// setOpaque(true);
			defaultBackground = MyTableW.BACKGROUND_COLOR_HEADER;
			// this.rowHeader = rowHeader;
			// setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
			// MyTableW.HEADER_GRID_COLOR));
		}

		public Widget changeListCellRendererWidget(Widget retwidget,
		        Object value, int index, boolean isSelected,
		        boolean cellHasFocus) {
			String text = (value == null) ? "" : value.toString();

			// setFont(app.getPlainFont());//instead of this:
			/*
			 * GFont gf = app.getFontCanDisplay(text, GFont.PLAIN);
			 * ((Label)retwidget
			 * ).getElement().getStyle().setFontSize(gf.getSize(),
			 * Style.Unit.PX);
			 * ((Label)retwidget).getElement().getStyle().setFontStyle(
			 * gf.isItalic() ? Style.FontStyle.ITALIC : Style.FontStyle.NORMAL);
			 * ((Label)retwidget).getElement().getStyle().setFontWeight(
			 * gf.isBold() ? Style.FontWeight.BOLD : Style.FontWeight.NORMAL);
			 */

			// adjust row height to match spreadsheet table row height
			// TODO?//Dimension size = getPreferredSize();
			// TODO?//size.height = table.getRowHeight(index);
			// TODO?//setPreferredSize(size);

			if (text != "")
				((Label) retwidget).setText(text);
			else
				((Label) retwidget).setText("" + (char) 160);

			/*
			 * if (table.getSelectionType() == MyTable.COLUMN_SELECT) { //if
			 * (defaultBackground != null)
			 * ((Label)retwidget).getElement().getStyle
			 * ().setBackgroundColor(defaultBackground.toString()); } else { if
			 * (table.selectedRowSet.contains(index) || (index >=
			 * minSelectionRow && index <= maxSelectionRow)) {
			 * ((Label)retwidget)
			 * .getElement().getStyle().setBackgroundColor(MyTableW
			 * .SELECTED_BACKGROUND_COLOR_HEADER.toString()); } else { //if
			 * (defaultBackground != null)
			 * ((Label)retwidget).getElement().getStyle
			 * ().setBackgroundColor(defaultBackground.toString()); } }
			 */

			// retwidget.getElement().getStyle().setProperty("textAlign",
			// "center");
			// retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
			// retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			return retwidget;
		}

		public Widget getListCellRendererWidget(Object value, int index,
		        boolean isSelected, boolean cellHasFocus) {

			Widget retwidget = new Label();

			String text = (value == null) ? "" : value.toString();

			// setFont(app.getPlainFont());//instead of this:
			GFont gf = app.getFontCanDisplay(text, GFont.PLAIN);
			((Label) retwidget).getElement().getStyle()
			        .setFontSize(gf.getSize(), Style.Unit.PX);
			((Label) retwidget)
			        .getElement()
			        .getStyle()
			        .setFontStyle(
			                gf.isItalic() ? Style.FontStyle.ITALIC
			                        : Style.FontStyle.NORMAL);
			((Label) retwidget)
			        .getElement()
			        .getStyle()
			        .setFontWeight(
			                gf.isBold() ? Style.FontWeight.BOLD
			                        : Style.FontWeight.NORMAL);

			// adjust row height to match spreadsheet table row height
			// TODO?//Dimension size = getPreferredSize();
			// TODO?//size.height = table.getRowHeight(index);
			// TODO?//setPreferredSize(size);

			if (text != "")
				((Label) retwidget).setText(text);
			else
				((Label) retwidget).setText("" + (char) 160);

			if (table.getSelectionType() == MyTable.COLUMN_SELECT) {
				// if (defaultBackground != null)
				((Label) retwidget).getElement().getStyle()
				        .setBackgroundColor(defaultBackground.toString());
			} else {
				if (table.selectedRowSet.contains(index)
				        || (index >= minSelectionRow && index <= maxSelectionRow)) {
					((Label) retwidget)
					        .getElement()
					        .getStyle()
					        .setBackgroundColor(
					                MyTableW.SELECTED_BACKGROUND_COLOR_HEADER
					                        .toString());
				} else {
					// if (defaultBackground != null)
					((Label) retwidget).getElement().getStyle()
					        .setBackgroundColor(defaultBackground.toString());
				}
			}

			retwidget.getElement().getStyle()
			        .setProperty("textAlign", "center");
			retwidget.getElement().getStyle().setPadding(2, Style.Unit.PX);
			retwidget.getElement().getStyle().setHeight(100, Style.Unit.PCT);
			return retwidget;
		}
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

	// Returns index of row to be resized if mouse point P is
	// near a row boundary (within 3 pixels)
	/*
	 * private int getResizingRow(java.awt.Point p) { int resizeRow = -1; GPoint
	 * point = table.getIndexFromPixel(p.x, p.y); if (point != null) { // test
	 * if mouse is 3 pixels from row boundary int cellRow = point.getY(); if
	 * (cellRow >= 0) { Rectangle r = table.getCellRect(cellRow, 0, true); //
	 * near row bottom if (p.y < r.y + 3) { resizeRow = cellRow - 1; } // near
	 * row top if (p.y > r.y + r.height - 3) { resizeRow = cellRow; } } } return
	 * resizeRow; }
	 */

	// Cursor change for when mouse is over a row boundary
	/*
	 * private void swapCursor() { Cursor tmp = getCursor();
	 * setCursor(otherCursor); otherCursor = tmp; }
	 */

	// ===============================================
	// Mouse Listener Methods
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
		// ?//java.awt.Point p = e.getPoint();
		// ?//resizingRow = getResizingRow(p);
		// ?//mouseYOffset = p.y - table.getRowHeight(resizingRow);
		//

		// left click
		if (!rightClick) {

			if (resizingRow >= 0)
				return; // GSTURR 2010-1-9

			GPoint point = table.getIndexFromPixel(x, y);
			if (point != null) {
				// G.STURR 2010-1-29
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
			if (p.getY() < minSelectionRow || p.getY() > maxSelectionRow
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
			SpreadsheetContextMenuW popupMenu = ((GuiManagerW) app
			        .getGuiManager()).getSpreadsheetContextMenu(table,
			        e.isShiftKeyDown());
			popupMenu.show(view.getFocusPanel(), e.getX(), e.getY());
		}

		// If row resize has happened, resize all other selected rows
		/*
		 * ? if (doRowResize) { if (minSelectionRow != -1 && maxSelectionRow !=
		 * -1 && (maxSelectionRow - minSelectionRow > 1)) { if
		 * (table.isSelectAll())
		 * table.setRowHeight(table.getRowHeight(resizingRow)); else for (int
		 * row = minSelectionRow; row <= maxSelectionRow; row++) {
		 * table.setRowHeight(row, table.getRowHeight(resizingRow)); } }
		 * doRowResize = false; }
		 */
	}

	// ===============================================
	// MouseMotion Listener Methods
	// ===============================================

	public void onMouseMove(MouseMoveEvent e) {

		e.preventDefault();

		// Show resize cursor when mouse is over a row boundary
		// ?//if ((getResizingRow(e.getPoint()) >= 0) != (getCursor() ==
		// resizeCursor)) {
		// ?// swapCursor();
		// ?//}

		if (isMouseDown) {

			if (e.getNativeButton() == NativeEvent.BUTTON_RIGHT)
				return; // G.Sturr 2009-9-30

			// G.STURR 2010-1-9
			// On mouse drag either resize or select a row
			int x = SpreadsheetMouseListenerW.getAbsoluteX(e, app);
			int y = SpreadsheetMouseListenerW.getAbsoluteY(e, app);
			if (resizingRow >= 0) {
				// resize row
				int newHeight = y - mouseYOffset;
				if (newHeight > 0) {
					table.setRowHeight(resizingRow, newHeight);
					// set this flag to resize all selected rows on mouse
					// release
					doRowResize = true;
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

	// ===============================================
	// Key Listener Methods
	// ===============================================

	/*
	 * public void keyTyped(KeyEvent e) { }
	 * 
	 * public void keyPressed(KeyEvent e) {
	 * 
	 * int keyCode = e.getKeyCode();
	 * 
	 * boolean metaDown = AppD.isControlDown(e); boolean altDown =
	 * e.isAltDown(); boolean shiftDown = e.isShiftDown();
	 * 
	 * // Application.debug(keyCode); switch (keyCode) {
	 * 
	 * case KeyEvent.VK_UP:
	 * 
	 * if (shiftDown) { // extend the column selection int row =
	 * table.getSelectionModel().getLeadSelectionIndex();
	 * table.changeSelection(row - 1, -1, false, true); } else { // select
	 * topmost cell in first column to the left of the // selection if
	 * (table.minSelectionRow > 0) table.setSelection(0, table.minSelectionRow -
	 * 1); else table.setSelection(0, table.minSelectionRow);
	 * table.requestFocus(); } break;
	 * 
	 * case KeyEvent.VK_DOWN: if (shiftDown) { // extend the column selection
	 * int row = table.getSelectionModel().getLeadSelectionIndex();
	 * table.changeSelection(row + 1, -1, false, true); } else { // select
	 * topmost cell in first column to the left of the // selection if
	 * (table.minSelectionRow > 0) table.setSelection(0, table.minSelectionRow +
	 * 1); else table.setSelection(0, table.minSelectionRow);
	 * table.requestFocus(); } break;
	 * 
	 * case KeyEvent.VK_C: // control + c if (metaDown && minSelectionRow != -1
	 * && maxSelectionRow != -1) { table.copyPasteCut.copy(0, minSelectionRow,
	 * table.getModel() .getColumnCount() - 1, maxSelectionRow, altDown);
	 * e.consume(); } break; case KeyEvent.VK_V: // control + v if (metaDown &&
	 * minSelectionRow != -1 && maxSelectionRow != -1) { boolean storeUndo =
	 * table.copyPasteCut.paste(0, minSelectionRow,
	 * table.getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); e.consume(); } break; case KeyEvent.VK_X: // control
	 * + x if (metaDown && minSelectionRow != -1 && maxSelectionRow != -1) {
	 * table.copyPasteCut.copy(0, minSelectionRow, table.getModel()
	 * .getColumnCount() - 1, maxSelectionRow, altDown); e.consume(); } boolean
	 * storeUndo = table.copyPasteCut.delete(0, minSelectionRow,
	 * table.getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); break;
	 * 
	 * case KeyEvent.VK_DELETE: // delete case KeyEvent.VK_BACK_SPACE: // delete
	 * on MAC storeUndo = table.copyPasteCut.delete(0, minSelectionRow, table
	 * .getModel().getColumnCount() - 1, maxSelectionRow); if (storeUndo)
	 * app.storeUndoInfo(); break; } }
	 * 
	 * public void keyReleased(KeyEvent e) { }
	 */

}
