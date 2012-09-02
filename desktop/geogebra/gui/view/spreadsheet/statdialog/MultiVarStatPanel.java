package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.main.AppD;

import javax.swing.table.DefaultTableModel;

/**
 * Extension of BasicStatTable that displays summary statistics for multiple
 * data sets.
 * 
 * @author G. Sturr
 * 
 */
public class MultiVarStatPanel extends BasicStatTable {
	private static final long serialVersionUID = 1L;

	public MultiVarStatPanel(AppD app, DataAnalysisViewD statDialog) {
		super(app, statDialog, -1);
	}

	@Override
	public String[] getRowNames() {
		return statDialog.getDataTitles();
	}

	@Override
	public String[] getColumnNames() {

		String[][] cmdMap = getCmdMap();
		String[] names = new String[cmdMap.length];
		for (int i = 0; i < cmdMap.length; i++) {
			names[i] = cmdMap[i][0];
		}
		return names;
	}

	@Override
	public int getRowCount() {
		return getRowNames().length;
	}

	@Override
	public int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public void updatePanel() {
		GeoList dataList = statDialog.getController()
				.getDataSelected();
		DefaultTableModel model = statTable.getModel();
		String[] titles = statDialog.getDataTitles();
		String[][] cmdMap = getCmdMap();

		for (int row = 0; row < titles.length; row++) {
			// get the stats for this list
			for (int col = 0; col < cmdMap.length; col++) {

				AlgoElement algo = getStatMapAlgo(cmdMap[col][1],
						(GeoList) dataList.get(row), null);
				app.getKernel().getConstruction()
						.removeFromConstructionList(algo);
				model.setValueAt(statDialog.format(((GeoNumeric) algo
						.getGeoElements()[0]).getDouble()), row, col);
			}
		}
		statTable.repaint();
	}

	private String[][] getCmdMap() {
		String[][] map = { { app.getMenu("Length.short"), "Length" },
				{ app.getMenu("Mean"), "Mean" },
				{ app.getMenu("StandardDeviation.short"), "SD" },
				{ app.getMenu("SampleStandardDeviation.short"), "SampleSD" },
				{ app.getMenu("Minimum.short"), "Min" },
				{ app.getMenu("LowerQuartile.short"), "Q1" },
				{ app.getMenu("Median"), "Median" },
				{ app.getMenu("UpperQuartile.short"), "Q3" },
				{ app.getMenu("Maximum.short"), "Max" },
				{ app.getMenu("Sum"), "Sum" },
				{ app.getMenu("Sum2"), "SigmaXX" } };
		return map;
	}
}
