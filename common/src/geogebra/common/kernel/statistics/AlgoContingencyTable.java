/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

/**
 * ContingencyTable[] algorithm
 * 
 * @author G. Sturr
 * 
 */
public class AlgoContingencyTable extends AlgoElement {

	private GeoList list1, list2, rowList, colList, freqMatrix; // input
	private GeoText args; // input

	private GeoText table; // output

	// for compute
	private AlgoFrequency freq;
	private StringBuilder sb = new StringBuilder();
	private boolean isRawData;

	private String[] rowValues;
	private String[] colValues;
	private int[][] freqValues;
	private double[][] expected;
	private double[][] chiCont;
	private int[] rowSum;
	private int[] colSum;
	private int totalSum;

	// display option flags
	private boolean showRowPercent, showColPercent, showTotalPercent, showChi,
			showExpected, showTest;
	private int rowCount;
	private int colCount;
	private int lastRow;

	/**************************************************
	 * Constructs a contingency table from raw data
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param args
	 * 
	 */
	public AlgoContingencyTable(Construction cons, String label, GeoList list1,
			GeoList list2, GeoText args) {

		super(cons);

		isRawData = true;

		this.list1 = list1;
		this.list2 = list2;
		this.args = args;

		freq = new AlgoFrequency(cons, list1, list2, true);
		cons.removeFromConstructionList(freq);

		table = new GeoText(cons);

		setInputOutput();
		//must set isLaTex before computing, #3846
		table.isTextCommand = true;
		table.setLaTeX(true, false);

		compute();
		table.setLabel(label);

	}

	/***************************************************
	 * Constructs a contingency table from a given frequency table
	 * 
	 * @param cons
	 * @param label
	 * @param rowList
	 * @param colList
	 * @param freqMatrix
	 * @param args
	 */
	public AlgoContingencyTable(Construction cons, String label,
			GeoList rowList, GeoList colList, GeoList freqMatrix, GeoText args) {

		super(cons);

		isRawData = false;

		this.rowList = rowList;
		this.colList = colList;
		this.freqMatrix = freqMatrix;
		this.args = args;

		table = new GeoText(cons);

		setInputOutput();
		//must set isLaTex before computing, #3846
		table.isTextCommand = true;
		table.setLaTeX(true, false);
		compute();

		
		table.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.ContingencyTable;
	}

	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> outList = new ArrayList<GeoElement>();

		if (list1 != null)
			outList.add(list1);
		if (list2 != null)
			outList.add(list2);

		if (rowList != null)
			outList.add(rowList);
		if (colList != null)
			outList.add(colList);
		if (freqMatrix != null)
			outList.add(freqMatrix);
		if (args != null)
			outList.add(args);

		input = new GeoElement[outList.size()];
		input = outList.toArray(input);

		setOutputLength(1);
		setOutput(0, table);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return table;
	}

	private void parseArgs() {

		// set defaults
		showRowPercent = false;
		showColPercent = false;
		showTotalPercent = false;
		showChi = false;
		showExpected = false;
		showTest = false;
		lastRow = 0;
		if (args != null) {
			String optionsStr = args.getTextString();
			if (optionsStr.indexOf("_") > -1){
				showRowPercent = true;
				lastRow = 1;
			}
			if (optionsStr.indexOf("|") > -1){
				showColPercent = true;
				lastRow = 2;
			}
			if (optionsStr.indexOf("+") > -1){
				showTotalPercent = true;
				lastRow = 3;
			}
			if (optionsStr.indexOf("e") > -1){
				showExpected = true;
				lastRow = 4;
			}
			if (optionsStr.indexOf("k") > -1){
				showChi = true;
				lastRow = 5;
			}
			if (optionsStr.indexOf("=") > -1){
				showTest = true;
			}
		}
	}

	/**
	 * Loads raw data from GeoLists into arrays
	 */
	private boolean loadRawDataValues() {

		if (!freq.getResult().isDefined()) {
			return false;
		}

		rowValues = freq.getContingencyRowValues();
		colValues = freq.getContingencyColumnValues();
		GeoList fr = freq.getResult();

		rowSum = new int[rowValues.length];
		colSum = new int[colValues.length];
		totalSum = 0;

		freqValues = new int[rowValues.length][colValues.length];

		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			GeoList rowGeo = (GeoList) fr.get(rowIndex);
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				freqValues[rowIndex][colIndex] = (int) ((GeoNumeric) rowGeo
						.get(colIndex)).getDouble();
				rowSum[rowIndex] += freqValues[rowIndex][colIndex];
				colSum[colIndex] += freqValues[rowIndex][colIndex];
				totalSum += freqValues[rowIndex][colIndex];
			}
		}

		return true;
	}

	/**
	 * Loads prepared frequencies and values from GeoLists into arrays
	 */
	private boolean loadPreparedDataValues() {

		GeoElement geo;

		if (rowList == null || colList == null || freqMatrix == null
				|| !rowList.isDefined() || !colList.isDefined()
				|| !freqMatrix.isDefined() || !freqMatrix.isMatrix()) {
			table.setUndefined();
			return false;
		}

		// TODO: reuse value arrays

		rowCount = rowList.size();
		if(freqMatrix.size()!=rowCount){
			table.setUndefined();
			return false;
		}
		colCount = colList.size();
		rowValues = new String[rowCount];
		colValues = new String[colCount];
		rowSum = new int[rowCount];
		colSum = new int[colCount];

		for (int i = 0; i < rowCount; i++) {
			geo = rowList.get(i);
			if (!geo.isGeoText())
				return false;
			rowValues[i] = ((GeoText) geo).getTextString();
		}

		for (int i = 0; i < colCount; i++) {
			geo = colList.get(i);
			if (!geo.isGeoText())
				return false;

			colValues[i] = ((GeoText) geo).getTextString();
		}

		freqValues = new int[rowSum.length][colValues.length];
		
		totalSum = 0;

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			// row element
			GeoList rowGeo = (GeoList) freqMatrix.get(rowIndex);
			for (int colIndex = 0; colIndex < colCount; colIndex++) {
				// geo element
				geo = rowGeo.get(colIndex);
				if (!geo.isGeoNumeric())
					return false;

				freqValues[rowIndex][colIndex] = (int) ((GeoNumeric) rowGeo
						.get(colIndex)).getDouble();
				rowSum[rowIndex] += freqValues[rowIndex][colIndex];
				colSum[colIndex] += freqValues[rowIndex][colIndex];
				totalSum += freqValues[rowIndex][colIndex];
			}

		}
		return true;
	}

	/**
	 * Computes expected counts and chi-square contributions
	 */
	private void computeChiTestValues() {

		expected = new double[rowValues.length][colValues.length];
		chiCont = new double[rowValues.length][colValues.length];

		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {
			for (int colIndex = 0; colIndex < colValues.length; colIndex++) {
				expected[rowIndex][colIndex] = 1.0 * rowSum[rowIndex]
						* colSum[colIndex] / totalSum;
				chiCont[rowIndex][colIndex] = (freqValues[rowIndex][colIndex] - expected[rowIndex][colIndex]);
				chiCont[rowIndex][colIndex] = chiCont[rowIndex][colIndex]
						* chiCont[rowIndex][colIndex]
						/ expected[rowIndex][colIndex];
			}
		}
	}

	@Override
	public final void compute() {

		boolean dataLoaded;
		if (isRawData) {
			dataLoaded = loadRawDataValues();
		} else {
			dataLoaded = loadPreparedDataValues();
		}

		if (!dataLoaded) {
			table.setUndefined();
			return;
		}

		parseArgs();
		computeChiTestValues();

		sb.setLength(0);

		// prepare array
		beginTable();

		// table header
		addTableRow(sb, 0, handleSpecialChar(loc.getMenu("Frequency")),
				"colValue", lastRow == 0);

		if (showRowPercent)
			addTableRow(sb, 0, handleSpecialChar(loc.getPlain("RowPercent")),
					"blank", lastRow == 1);
		if (showColPercent)
			addTableRow(sb, 0,
					handleSpecialChar(loc.getPlain("ColumnPercent")), "blank", lastRow == 2);
		if (showTotalPercent)
			addTableRow(sb, 0, handleSpecialChar(loc.getPlain("TotalPercent")),
					"blank",lastRow == 3);
		if (showExpected)
			addTableRow(sb, 0,
					handleSpecialChar(loc.getPlain("ExpectedCount")), "blank",lastRow == 4);
		if (showChi)
			addTableRow(sb, 0,
					handleSpecialChar(loc.getPlain("ChiSquaredContribution")),
					"blank",lastRow == 5);

		

		// remaining rows
		for (int rowIndex = 0; rowIndex < rowValues.length; rowIndex++) {

			addTableRow(sb, rowIndex, rowValues[rowIndex], "count", lastRow == 0);
			if (showRowPercent)
				addTableRow(sb, rowIndex, null, "_", lastRow == 1);
			if (showColPercent)
				addTableRow(sb, rowIndex, null, "|", lastRow == 2);
			if (showTotalPercent)
				addTableRow(sb, rowIndex, null, "+", lastRow == 3);
			if (showExpected)
				addTableRow(sb, rowIndex, null, "e", lastRow == 4);
			if (showChi)
				addTableRow(sb, rowIndex, null, "k", lastRow == 5);

			
		}
		sb.append("\\hline ");

		// table footer
		addTableRow(sb, 0, loc.getMenu("Total"), "tableFooter", !showRowPercent);
		if (showRowPercent)
			addTableRow(sb, 0, null, "rowPercentFooter", true);
		endTable(sb);

		if (showTest) {
			addChiTest(sb);

		}

		table.setTextString(sb.toString());
	}

	private void endTable(StringBuilder sb2) {
		sb.append("\\end{array}");
	}

	private void addChiTest(StringBuilder sb) {


		AlgoChiSquaredTest test;
		if (isRawData) {
			test = new AlgoChiSquaredTest(cons, freq.getResult(), null);
		} else {
			test = new AlgoChiSquaredTest(cons, freqMatrix, null);
		}
		cons.removeFromConstructionList(test);
		GeoList result = test.getResult();

		sb.append("\\\\");
		sb.append(loc.getMenu("ChiSquaredTest"));
		sb.append("\\\\");
		sb.append("\\begin{array}{| | | | |}");
		sb.append(" \\\\ \\hline ");
		sb.append(loc.getMenu("DegreesOfFreedom.short") + "&" + Unicode.chi
				+ Unicode.Superscript_2 + "&" + loc.getMenu("PValue"));
		sb.append("\\\\");
		sb.append("\\hline ");
		sb.append(kernel.format(
				(rowValues.length - 1) * (colValues.length - 1),
				StringTemplate.numericDefault));
		sb.append("&");
		sb.append(result.get(1)
				.toValueString(StringTemplate.numericDefault));
		sb.append("&");
		sb.append(result.get(0)
				.toValueString(StringTemplate.numericDefault));
		sb.append("\\\\");
		sb.append("\\hline ");
		sb.append("\\end{array}");
	}

	private void beginTable() {
		sb.append("\\begin{array}{|l");
		for (int i = 0; i < colValues.length - 1; i++) {
			sb.append("| ");
		}
		sb.append("| || |}"); // extra column for margin
		sb.append(" \\\\ \\hline ");
	}

	private void addTableRow(StringBuilder sb, int rowIndex, String header,
			String type, boolean lineBelow) {

		double x;

		// row header
		if (header == null) {
			sb.append("\\;");
		} else {
			sb.append(header);
		}
		endCell(sb);

		// row elements
		for (int colIndex = 0; colIndex < colValues.length; colIndex++) {

			if (type.equals("blank")) {
				sb.append("\\;");

			} else if (type.equals("colValue")) {
				sb.append(colValues[colIndex]);

			} else if (type.equals("count")) {
				sb.append(freqValues[rowIndex][colIndex]);

			} else if (type.equals("_")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / rowSum[rowIndex];
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("|")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / colSum[colIndex];
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("+")) {
				x = 100.0 * freqValues[rowIndex][colIndex] / totalSum;
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("e")) {
				x = expected[rowIndex][colIndex];
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("k")) {
				x = chiCont[rowIndex][colIndex];
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));

			} else if (type.equals("tableFooter")) {
				sb.append(colSum[colIndex]);

			} else if (type.equals("rowPercentFooter")) {
				x = 100.0 * colSum[colIndex] / totalSum;
				sb.append(kernel.format(x,
						StringTemplate.numericDefault));
			}

			endCell(sb);
		}

		// margin
		if (type.equals("count")) {
			sb.append(rowSum[rowIndex]);

		} else if (type.equals("colValue")) {
			sb.append(loc.getMenu("Total"));

		} else if (type.equals("|")) {
			x = 100.0 * rowSum[rowIndex] / totalSum;
			sb.append(kernel.format(x, StringTemplate.numericDefault));

		} else if (type.equals("tableFooter")) {
			sb.append(totalSum);

		} else {
			sb.append("\\;");
		}
		endRow(sb, lineBelow);
	}

	private void endRow(StringBuilder sb2, boolean lineBelow) {
		
		sb.append("\\\\");
		if(lineBelow){
			sb.append("\\hline ");
		}
	}

	private void endCell(StringBuilder sb) {
		if(kernel.getApplication().isHTML5Applet()){
			sb.append("}\\ggbtd{");
		}else{
			sb.append("&");
		}
	}

	private String handleSpecialChar(String s) {
		return s.replaceAll(" ", "\\\\;");
	}



}
