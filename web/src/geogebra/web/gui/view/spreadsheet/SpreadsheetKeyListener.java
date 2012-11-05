package geogebra.web.gui.view.spreadsheet;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GWTKeycodes;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public class SpreadsheetKeyListener implements KeyDownHandler
{

	private AppW app;
	private SpreadsheetView view;
	private Kernel kernel;
	private MyTableW table;
	private SpreadsheetTableModelW model;
	private MyCellEditorW editor;


	public SpreadsheetKeyListener(AppW app, MyTableW table){

		this.app = app;
		this.kernel = app.getKernel();
		this.table = table;
		this.view = (SpreadsheetView)table.getView();
		this.model = (SpreadsheetTableModelW) table.getModel();  
		this.editor = table.editor;

	}

	/*public void keyTyped(KeyEvent e) {

	}*/

	public void onKeyDown(KeyDownEvent e) {

		e.stopPropagation();
		e.preventDefault();

		int keyCode = e.getNativeKeyCode();//.getKeyCode();
		//Application.debug(keyCode+"");
		//boolean shiftDown = e.isShiftDown(); 	 
		boolean altDown = e.isAltKeyDown(); 	 
		boolean ctrlDown = e.isControlKeyDown(); //AppW.isControlDown(e) // Windows ctrl/Mac Meta
		//|| e.isControlDown(); // Fudge (Mac ctrl key)	

		int row = table.getSelectedRow();
		int column = table.getSelectedColumn();

		switch (keyCode) {

		case KeyCodes.KEY_UP://KeyEvent.VK_UP:
			if (e.isControlKeyDown()) {
				//AppW.isControlDown(e)) {
				if (model.getValueAt(row, column) != null) {
					// move to top of current "block"
					// if shift pressed, select cells too
					while ( row > 0 && model.getValueAt(row - 1, column) != null) row--;
					table.changeSelection(row, column, false, e.isShiftKeyDown());
				} else {
					// move up to next defined cell
					while ( row > 0 && model.getValueAt(row - 1, column) == null) row--;
					table.changeSelection(Math.max(0, row - 1), column, false, false);
					
				}
				//e.consume();
			}
			// copy description into input bar when a cell is entered
//			GeoElement geo = (GeoElement) getModel().getValueAt(table.getSelectedRow() - 1, table.getSelectedColumn());
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			
			break;
			
		case KeyCodes.KEY_LEFT://VK_LEFT:
			if (e.isControlKeyDown()) {
				//AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to left of current "block"
					// if shift pressed, select cells too
					while ( column > 0 && model.getValueAt(row, column - 1) != null) column--;
					table.changeSelection(row, column, false, e.isShiftKeyDown());
				} else {
					// move left to next defined cell
					while ( column > 0 && model.getValueAt(row, column - 1) == null) column--;
					table.changeSelection(row, Math.max(0, column - 1), false, false);						
				}
				
				//e.consume();
			}
//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn() - 1);
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			break;

		
		case KeyCodes.KEY_DOWN://VK_DOWN:
			// auto increase spreadsheet size when you go off the bottom	
			if (table.getSelectedRow() + 1 >= table.getRowCount()-1 && table.getSelectedRow() < Kernel.MAX_SPREADSHEET_ROWS) {
				model.setRowCount(table.getRowCount());
				
				//getView().getRowHeader().revalidate();   //G.STURR 2010-1-9
			}
			
			else if (e.isControlKeyDown()) {
				//AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
				
					// move to bottom of current "block"
					// if shift pressed, select cells too
					while ( row < table.getRowCount()-1 && model.getValueAt(row + 1, column) != null) row++;
					table.changeSelection(row, column, false, e.isShiftKeyDown());
				} else {
					// move down to next selected cell
					while ( row < table.getRowCount()-1 && model.getValueAt(row + 1, column) == null) row++;
					table.changeSelection(Math.min(table.getRowCount() - 1, row + 1), column, false, false);
					
				}
				
				//e.consume();
			}


//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow()+1, table.getSelectedColumn());
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}

			
			break;
			
		case KeyCodes.KEY_HOME://.VK_HOME:

			// if shift pressed, select cells too
			if (e.isControlKeyDown()) {
				// AppD.isControlDown(e)) {
				
				// move to top left of spreadsheet
				table.changeSelection(0, 0, false, e.isShiftKeyDown());
			}
			else {
				// move to left of current row
				table.changeSelection(row, 0, false, e.isShiftKeyDown());
			}
			
			//e.consume();
			break;
			
		case KeyCodes.KEY_END://.VK_END:

			// move to bottom right of spreadsheet
			// if shift pressed, select cells too
			
			// find rectangle that will contain all cells 
			for (int c = 0 ; c <table.getColumnCount()-1 ; c++)
			for (int r = 0 ; r < table.getRowCount()-1 ; r++)
				if ((r > row || c > column) && model.getValueAt(r, c) != null) {
					if (r > row) row = r;
					if (c > column) column = c;
				}
			table.changeSelection(row, column, false, e.isShiftKeyDown());
			
			//e.consume();
			break;

		case KeyCodes.KEY_RIGHT: //Event.VK_RIGHT:
			// auto increase spreadsheet size when you go off the right
			
			if (table.getSelectedColumn() + 1 >= table.getColumnCount()-1 && table.getSelectedColumn() < Kernel.MAX_SPREADSHEET_COLUMNS) {
				model.setColumnCount(table.getColumnCount());		
				view.columnHeaderRevalidate();

				// these two lines are a workaround for Java 6
				// (Java bug?)
				table.changeSelection(row, column + 1, false, false);
				//e.consume();
			}
			else if (e.isControlKeyDown()) {
				//AppD.isControlDown(e)) {

				if (model.getValueAt(row, column) != null) {
					// move to bottom of current "block"
					// if shift pressed, select cells too
					while ( column < table.getColumnCount() - 1 && model.getValueAt(row, column + 1) != null) column++;
					table.changeSelection(row, column, false, e.isShiftKeyDown());
				} else {
					// move right to next defined cell
					while ( column < table.getColumnCount() - 1 && model.getValueAt(row, column + 1) == null) column++;
					table.changeSelection(row, Math.min(table.getColumnCount() - 1, column + 1), false, false);
					
				}
				//e.consume();
			}

//			// copy description into input bar when a cell is entered
//			geo = (GeoElement) getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn() + 1);
//			if (geo != null) {
//				AlgebraInput ai = (AlgebraInput)(app.getGuiManager().getAlgebraInput());
//				ai.setString(geo);
//			}
			break;
			
		case KeyCodes.KEY_SHIFT://.VK_SHIFT:
		case KeyCodes.KEY_CTRL://Event.VK_CONTROL:
		case KeyCodes.KEY_ALT://Event.VK_ALT:
		//case KeyEvent.VK_META: //MAC_OS Meta
			//e.consume(); // stops editing start
			break;

		case GWTKeycodes.KEY_F9://  Event.VK_F9:
			kernel.updateConstruction();
			//e.consume(); // stops editing start
			break;

		case GWTKeycodes.KEY_R://KeyEvent.VK_R:
			if (e.isControlKeyDown()) {
				//AppD.isControlDown(e)) {
				kernel.updateConstruction();
				//e.consume();
			}
			else letterOrDigitTyped();
			break;

			// needs to be here to stop keypress starting a cell edit after the undo
		case GWTKeycodes.KEY_Z://KeyEvent.VK_Z: //undo
			if (ctrlDown) {
				//Application.debug("undo");
				app.getGuiManager().undo();
				//e.consume();
			}
			else letterOrDigitTyped();
			break;

			// needs to be here to stop keypress starting a cell edit after the redo
		case GWTKeycodes.KEY_Y://KeyEvent.VK_Y: //redo
			if (ctrlDown) {
				//Application.debug("redo");
				app.getGuiManager().redo();
				//e.consume();
			}
			else letterOrDigitTyped();
			break;


		case GWTKeycodes.KEY_C://KeyEvent.VK_C:
		case GWTKeycodes.KEY_V://KeyEvent.VK_V:
		case GWTKeycodes.KEY_X://KeyEvent.VK_X:
		case GWTKeycodes.KEY_DELETE://KeyEvent.VK_DELETE: 	                         
		case GWTKeycodes.KEY_BACKSPACE://KeyEvent.VK_BACK_SPACE:
			if (! editor.isEditing()) {
				if (Character.isLetterOrDigit(
					Character.toChars(e.getNativeEvent().getCharCode())[0]
					) &&
						!editor.isEditing() && !(ctrlDown || e.isAltKeyDown())) {
					letterOrDigitTyped();
				} else	if (ctrlDown) {
					//e.consume();

					if (keyCode == GWTKeycodes.KEY_C) {
							//KeyEvent.VK_C) {
						table.copy(altDown);
					}
					else if (keyCode == GWTKeycodes.KEY_V) {
							//KeyEvent.VK_V) {
						boolean storeUndo = table.paste();
						view.rowHeaderRevalidate();
						if (storeUndo)
							app.storeUndoInfo();
					}
					else if (keyCode == GWTKeycodes.KEY_X) {
							//KeyEvent.VK_X) {
						boolean storeUndo = table.cut();
						if (storeUndo)
							app.storeUndoInfo();
					}
				}
				if (keyCode == GWTKeycodes.KEY_DELETE ||
						//KeyEvent.VK_DELETE || 	                                         
						keyCode == GWTKeycodes.KEY_BACKSPACE
						//KeyEvent.VK_BACK_SPACE
						) {
					//e.consume();
					//Application.debug("deleting...");
					boolean storeUndo = table.delete();
					if (storeUndo)
						app.storeUndoInfo();
				}
				return;
			}
			break;		
			
		//case KeyEvent.VK_ENTER:	
		case GWTKeycodes.KEY_F2://KeyEvent.VK_F2:	//FIXME
			if (!editor.isEditing()) {
				table.setAllowEditing(true);
				table.editCellAt(table.getSelectedRow()+1, table.getSelectedColumn()+1);
				 //?//final JTextComponent f = (JTextComponent)table.getEditorComponent();
		         //?//   f.requestFocus();
		         //?//   f.getCaret().setVisible(true);
				table.setAllowEditing(false);
			}
			//e.consume();
			break;	
			
		case KeyCodes.KEY_ENTER://KeyEvent.VK_ENTER:	
			/*? if (MyCellEditor.tabReturnCol > -1) {
				table.changeSelection(row , MyCellEditor.tabReturnCol, false, false);
				MyCellEditor.tabReturnCol = -1;
			}*/
			
			// fall through
		case GWTKeycodes.KEY_PAGEDOWN://KeyEvent.VK_PAGE_DOWN:	
		case GWTKeycodes.KEY_PAGEUP://KeyEvent.VK_PAGE_UP:	
			// stop cell being erased before moving
			break;
			
			// stop TAB erasing cell before moving
		case KeyCodes.KEY_TAB://KeyEvent.VK_TAB:
			// disable shift-tab in column A
			if (table.getSelectedColumn() == 0 && e.isShiftKeyDown()) ;
				//e.consume();
			break;

		case GWTKeycodes.KEY_A://KeyEvent.VK_A:
			if (e.isControlKeyDown()) {
				//AppD.isControlDown(e)) {

				// select all cells
				
				row = 0;
				column = 0;
				// find rectangle that will contain all defined cells 
				for (int c = 0 ; c < table.getColumnCount()-1 ; c++)
				for (int r = 0 ; r < table.getRowCount()-1 ; r++)
					if ((r > row || c > column) && model.getValueAt(r, c) != null) {
						if (r > row) row = r;
						if (c > column) column = c;
					}
				table.changeSelection(0, 0, false, false);
				table.changeSelection(row, column, false, true);

				
				//e.consume();
				
			}
			// no break, fall through
		default:
			if (/*? !Character.isIdentifierIgnorable(
					Character.toChars(e.getNativeEvent().getCharCode())[0]
					//e.getKeyChar()
				) && */
					!editor.isEditing() && !(ctrlDown || e.isAltKeyDown())) {
				letterOrDigitTyped();
			} else
				//e.consume();
		break;
			
		}
			
		/*
		if (keyCode >= 37 && keyCode <= 40) {
			if (editor.isEditing())	return;			
		}

		for (int i = 0; i < defaultKeyListeners.length; ++ i) {
			if (e.isConsumed()) break;
			defaultKeyListeners[i].keyPressed(e);			
		}
		 */
	}
	
	public void letterOrDigitTyped() {
		table.setAllowEditing(true);
		table.repaint();  //G.Sturr 2009-10-10: cleanup when keypress edit begins
		
		// check if cell fixed
		Object o = model.getValueAt(table.getSelectedRow(), table.getSelectedColumn());			
		if ( o != null && o instanceof GeoElement) {
			GeoElement geo = (GeoElement)o;
			if (geo.isFixed()) return;
		}
	
		model.setValueAt(null, table.getSelectedRow(), table.getSelectedColumn());
		table.editCellAt(table.getSelectedRow()+1, table.getSelectedColumn()+1); 
		// workaround, see
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4192625				
        //?//final JTextComponent f = (JTextComponent)table.getEditorComponent();
        //?//f.requestFocus();
        //?//f.getCaret().setVisible(true);
        
        // workaround for Mac OS X 10.5 problem (first character typed deleted)
        /*? if (AppD.MAC_OS)
            SwingUtilities.invokeLater( new Runnable(){ public void
            	run() { f.setSelectionStart(1);
	            f.setSelectionEnd(1);} });*/

        table.setAllowEditing(false);
		
	}

	/*public void keyReleased(KeyEvent e) {
		
	}*/

}

