package geogebra.html5.util;

import geogebra.common.gui.view.spreadsheet.MyTable;
import geogebra.common.main.App;
import geogebra.common.main.SpreadsheetTableModel;
import geogebra.html5.main.AppW;

import java.util.ArrayList;
//import geogebra.web.gui.view.spreadsheet.MyTableW;

public class SpreadsheetTableModelW extends SpreadsheetTableModel {

	public interface ChangeListener {
		public void dimensionChange();
		public void valueChange();
	}

	private MyTable table;
	
	ChangeListener listener = null;

	// try with one-dimension ArrayList to represent two dimensions
	private ArrayList<Object> defaultTableModel;

	// it is easier to store the rowNum and colNum than computing them
	int rowNum = 0;
	int colNum = 0;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 */
	public SpreadsheetTableModelW(AppW app, int rows, int columns) {
		super(app, rows, columns);
		rowNum = rows;
		colNum = columns;
		defaultTableModel = new ArrayList(rows * columns);
		for (int i = 0; i < rows * columns; i++)
			defaultTableModel.add(null);
		if (listener != null)
			listener.dimensionChange();
		attachView();
		isIniting=false;
	}

	/**
	 * Gets the JTable table model.
	 * 
	 * @return instance of Swing DefaultTableModel class
	 */
	public ArrayList<Object> getDefaultTableModel() {
		return defaultTableModel;
	}
	
	public void attachMyTable(MyTable table){
		this.table = table;
		Object value;
		if (table != null)
			for (int i = 0; i < rowNum; i++)
				for (int j = 0; j < colNum; j++)
					if ((value = getValueAt(i,j)) != null)
						table.updateTableCellValue(value, i, j);
	}
		
	@Override
	public int getRowCount() {
		return rowNum;
	}

	@Override
	public int getColumnCount() {
		return colNum;
	}

	@Override
	public void setRowCount(int rowCount) {
		if (rowNum == rowCount) {
			return;
		} else if (rowNum > rowCount) {
			for (int i = rowNum * colNum - 1; i >= rowCount * colNum; i--)
				defaultTableModel.remove(i);
		} else {
			defaultTableModel.ensureCapacity(rowCount * colNum);
			for (int i = rowNum * colNum; i < rowCount * colNum; i++)
				defaultTableModel.add(null);
		}
		rowNum = rowCount;
		if (listener != null)
			listener.dimensionChange();
	}

	@Override
	public void setColumnCount(int columnCount) {
		if (colNum == columnCount) {
			return;
		} else if (colNum > columnCount) {
			for (int i = rowNum - 1; i >= 0; i--)
				for (int j = colNum - 1; j >= columnCount; j--)
					defaultTableModel.remove(i*colNum + j);
		} else {
			defaultTableModel.ensureCapacity(rowNum * columnCount);
			for (int i = rowNum - 1; i >= 0; i--)
				for (int j = colNum; j < columnCount; j++)
					if (i*colNum + j >= defaultTableModel.size())
						defaultTableModel.add(null);
					else
						defaultTableModel.add(i*colNum + j, null);
		}
		colNum = columnCount;
		if (listener != null)
			listener.dimensionChange();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return defaultTableModel.get(row*colNum+column);
}

	@Override
	public void setValueAt(Object value, int row, int column) {
		// update column count if needed
		if (column >= getColumnCount()) {
			setColumnCount(column + 1);
			if (listener != null) {
				listener.dimensionChange();
			}
		}

		if (row >= getRowCount()) {
			setRowCount(row + 1);
			if (listener != null) {
				listener.dimensionChange();
			}
		}

		defaultTableModel.set(row * colNum + column, value);
		if (listener != null)
			listener.valueChange();

		if (table != null) {
			table.updateTableCellValue(value, row, column);
		}

	}

	public boolean hasFocus() {
		App.debug("unimplemented");
		return false;
	}

	public void repaint() {
		App.debug("unimplemented");
    }

	public boolean isShowing() {
		App.debug("unimplemented");
	    return false;
    }

	public void setChangeListener(ChangeListener cl) {
		listener = cl;
	}
	
	public boolean suggestRepaint(){
		//repaint not needed
		return false;
	}
}
