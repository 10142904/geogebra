package geogebra.web.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.cas.view.CASTableCellEditor;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.MyTableW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class CASTableW extends Grid implements CASTable{

	public CASTableW(){
		super(1,1);
		setBorderWidth(1);
		getElement().getStyle().setBorderColor(MyTableW.TABLE_GRID_COLOR.toString());
		getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		
		insertRow(0,null, false);
	}
	
	public int getRowHeight(int i) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void setLabels() {
	    // TODO Auto-generated method stub
	    
    }

	public GeoCasCell getGeoCasCell(int n) {
	    // TODO Auto-generated method stub
	    return null;
    }

	public App getApplication() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void deleteAllRows() {
	    // TODO Auto-generated method stub
	    
    }

	public void insertRow(int rows, GeoCasCell casCell, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	public int[] getSelectedRows() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public int getSelectedRow() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void stopEditing() {
	    // TODO Auto-generated method stub
	    
    }

	public void startEditingRow(int selectedRow) {
	    // TODO Auto-generated method stub
	    
    }

	public CASTableCellEditor getEditor() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public boolean isRowEmpty(int i) {
	    // TODO Auto-generated method stub
	    return false;
    }

	public void insertRow(GeoCasCell casCell, boolean b) {
		int n = getRowCount();
		resize(n+1,1);
	    Widget retwidget = new CASTableCellW(casCell);
		
		this.setWidget(n, CASTable.COL_CAS_CELLS, retwidget);
	    
    }

	public void deleteRow(int rowNumber) {
	    // TODO Auto-generated method stub
	    
    }

	public void setRow(int rowNumber, GeoCasCell casCell) {
	    if(rowNumber>=this.getRowCount()){
	    	resize(rowNumber+1,1);
	    }
	    Widget retwidget = new CASTableCellW(casCell);
		
		this.setWidget(rowNumber, CASTable.COL_CAS_CELLS, retwidget);
    }

}
