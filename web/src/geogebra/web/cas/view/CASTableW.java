package geogebra.web.cas.view;

import geogebra.common.awt.GPoint;
import geogebra.common.cas.view.CASTable;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;

import java.util.TreeSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class CASTableW extends Grid implements CASTable {

	public static final int COL_CAS_CELLS_WEB = 1;
	public static final int COL_CAS_HEADER = 0;
	private CASTableCellEditorW editor;
	private CASTableCellW editing;
	private AppW app;
	private int[] selectedRows = new int[0];
	private CASTableControllerW ml;
	private CASViewW view;

	public CASTableW(AppW app, CASTableControllerW ml, CASViewW casViewW) {
		super(0, 2);
		this.app = app;
		this.ml = ml;
		
		addStyleName("CAS-table");
		insertRow(0, null, false);
		view = casViewW;
	}

	public void setLabels() {
		getEditor().setLabels();

	}

	public GeoCasCell getGeoCasCell(int n) {
		Widget w = getWidget(n, COL_CAS_CELLS_WEB);
		if (w instanceof CASTableCellW) {
			return ((CASTableCellW) w).getCASCell();
		}
		return null;
	}

	public App getApplication() {
		return app;
	}

	public void deleteAllRows() {
		resize(0, 2);
	}

	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
		int n = rows;
		if (n >= getRowCount())
			resize(n + 1, 2);
		else
			this.insertRow(n);
		Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(this, n + 1, casCell, (AppW)getApplication());
		setWidget(n, CASTableW.COL_CAS_HEADER, rowHeader);
		getCellFormatter().addStyleName(n, COL_CAS_HEADER, "cas_header");
		
		setWidget(n, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
		
		if (n < getRowCount()-1){
			//Let increase the labels below the n. row.
			resetRowNumbers(n+1);
			// tell construction about new GeoCasCell if it is not at the
			// end
			app.getKernel().getConstruction().setCasCellRow(casCell, rows);
		}
	}
	
	public void resetRowNumbers(int from){
		RowHeaderWidget nextHeader;
		for(int i=from; i<getRowCount(); i++){
			nextHeader = (RowHeaderWidget) this.getWidget(i, COL_CAS_HEADER);
			nextHeader.setLabel(i+1+"");
		}
	}

	public int[] getSelectedRows() {
		return selectedRows;
	}

	public int getSelectedRow() {
		if(selectedRows.length<1)
			return -1;
		return selectedRows[0];
	}

	public void stopEditing() {
		if (editing != null)
			editing.stopEditing();
		editing = null;

	}

	public void cancelEditing() {
		if (editing != null)
			editing.cancelEditing();
		editing = null;
	}

	public void startEditingRow(int n) {
		if (n == 0) {
			setFirstRowFront(true);
		}
		Widget w = getWidget(n, COL_CAS_CELLS_WEB);

		if (w == editing)
			return;
		setSelectedRows(n,n);
		//cancelEditing();
		stopEditing();
		if (w instanceof CASTableCellW) {
			editing = (CASTableCellW) w;
			editing.startEditing(getEditor().getWidget());
		}

	}
	
	public void setInput(){
		if (editing !=null) editing.setInput();
	}

	public CASTableCellEditorW getEditor() {
		if(editor == null){
			editor = new CASTableCellEditorW(this, app, ml);
		}
		return editor;
	}

	public void deleteRow(int rowNumber) {
		removeRow(rowNumber);
	}

	public void setRow(int rowNumber, GeoCasCell casCell) {
		if (rowNumber<0) return;
		if (rowNumber >= this.getRowCount()) {
			resize(rowNumber + 1, 2);
		}
		if (casCell.isUseAsText()) setInput();
		
		Widget cellWidget = new CASTableCellW(casCell);
		Widget rowHeader = new RowHeaderWidget(this, rowNumber + 1,casCell, (AppW) getApplication());
		
		setWidget(rowNumber, CASTableW.COL_CAS_HEADER, rowHeader);
		setWidget(rowNumber, CASTableW.COL_CAS_CELLS_WEB, cellWidget);
		if(casCell.isUseAsText()){
			((CASTableCellW)cellWidget).setFont();
			((CASTableCellW)cellWidget).setColor();
		}
	}
	
	private void setRowSelected(int rowNumber, boolean selected) {
		if(selected) {
			getCellFormatter().getElement(rowNumber, COL_CAS_HEADER).addClassName("selected");
		}
		else {
			getCellFormatter().getElement(rowNumber, COL_CAS_HEADER).removeClassName("selected");
		}
	}

	public boolean isEditing() {
		return editing != null;
	}

	public GPoint getPointForEvent(MouseEvent event) {
		Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
		if (td == null) {
			return null;
		}

		int row = TableRowElement.as(td.getParentElement())
		        .getSectionRowIndex();
		int column = TableCellElement.as(td).getCellIndex();
		return new GPoint(column, row);
	}

	public void setSelectedRows(int from, int to) {
		selectedRows = new int[0];
		addSelectedRows(from, to);
		view.getCASStyleBar().setSelectedRow(getGeoCasCell(from));
	}

	public void addSelectedRows(int a, int b) {
		int from = Math.min(a, b);
		int to = Math.max(a, b);
		if(from < 0)
			return;
		for(int i=0;i<getRowCount();i++){
			markRowSelected(i,false);
		}
		TreeSet<Integer> newSelectedRows = new TreeSet<Integer>();
		//add old rows 
		for (int i = 0; i < selectedRows.length; i++) {
			newSelectedRows.add(selectedRows[i]);
			markRowSelected(selectedRows[i],true);
		}
		//add new rows
		for (int i = from; i <= to; i++) {
			newSelectedRows.add(i);
			markRowSelected(i,true);
		}
		int j = 0;
		selectedRows = new int[newSelectedRows.size()];
		for(int row:newSelectedRows){
			selectedRows[j++]=row;
		}
	}

	private void markRowSelected(int rowNumber, boolean b) {
		setRowSelected(rowNumber, b);
    }

	public CASViewW getCASView() {
	    return view;
    }

	public boolean isSelectedIndex(int row) {
		for(Integer item : getSelectedRows()){
			if (item.equals(row)) return true;
		}
		return false;
    }

	public int getEditingRow() {
	    if (isEditing()){
	    	return getSelectedRows()[0];
	    }
	    return -1;
    }
	
	public void setFirstRowFront(boolean value) {
		CellFormatter cellFormatter = getCellFormatter();
		if (value) {
			cellFormatter.addStyleName(0, COL_CAS_CELLS_WEB, "CAS_table_first_row_selected");
		} else {
			cellFormatter.removeStyleName(0, COL_CAS_CELLS_WEB, "CAS_table_first_row_selected");
		}
	}

}
