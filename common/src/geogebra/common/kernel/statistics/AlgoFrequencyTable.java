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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.TableAlgo;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;

import java.util.ArrayList;

/**
 * FrequencyTable[] algorithm based on AlgoFrequency
 * 
 * @author Zbynek Konecny
 * 
 */
public class AlgoFrequencyTable extends AlgoElement implements TableAlgo {

	private enum inputType {
		STANDARD, HISTOGRAM, BARCHART
	};

	private inputType type;

	private GeoList dataList; // input
	private GeoList classList; // input
	private GeoBoolean isCumulative; // input
	private GeoBoolean useDensity; // input
	private GeoNumeric density; // input
	private GeoNumeric scale; // input
	private GeoNumeric chart; // input

	// private GeoList frequency; //output
	private GeoText table; // output
	// for compute
	private AlgoFrequency freq;

	String[] strHeader = null;
	String[] strValue = null;
	String[] strFrequency = null;

	private StringBuilder sb = new StringBuilder();

	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, label, isCumulative, classList, dataList, null, null, null);
	}

	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList, GeoNumeric scale) {
		this(cons, label, isCumulative, classList, dataList, null, null, scale);
	}

	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density) {
		this(cons, label, isCumulative, classList, dataList, useDensity, density, null);
	}
	
	public AlgoFrequencyTable(Construction cons, String label,
			GeoBoolean isCumulative, GeoList classList, GeoList dataList,
			GeoBoolean useDensity, GeoNumeric density, GeoNumeric scale) {
		this(cons, isCumulative, classList, dataList, useDensity, density, scale);
		table.setLabel(label);
	}

	public AlgoFrequencyTable(Construction cons, GeoBoolean isCumulative,
			GeoList classList, GeoList dataList, GeoBoolean useDensity,
			GeoNumeric density, GeoNumeric scale) {
		super(cons);

		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;
		this.scale = scale;
		freq = new AlgoFrequency(cons, isCumulative, classList, dataList,
				useDensity, density, scale);
		cons.removeFromConstructionList(freq);
		table = new GeoText(cons);

		type = inputType.STANDARD;
		setInputOutput();

		compute();
		table.isTextCommand = true;
		table.setLaTeX(true, false);

	}
	
    

	public AlgoFrequencyTable(Construction cons, String label, GeoNumeric chart) {
		this(cons, chart);
		table.setLabel(label);
	}

	public AlgoFrequencyTable(Construction cons, GeoNumeric chart) {
		super(cons);
		
		AlgoElement algo = chart.getParentAlgorithm();

		if (algo instanceof AlgoHistogram) {
			type = inputType.HISTOGRAM;
		} else {
			type = inputType.BARCHART;
		}

		this.chart = chart;

		table = new GeoText(cons);
		setInputOutput();
		compute();
		table.isTextCommand = true;
		table.setLaTeX(true, false);
	}

	@Override
	public Commands getClassName() {
		return Commands.FrequencyTable;
	}

	@Override
	protected void setInputOutput() {

		switch (type) {
		case HISTOGRAM:
		case BARCHART:
			input = new GeoElement[1];
			input[0] = chart;
			break;

		case STANDARD:

			ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

			if (isCumulative != null)
				tempList.add(isCumulative);

			if (classList != null)
				tempList.add(classList);

			tempList.add(dataList);

			if (useDensity != null)
				tempList.add(useDensity);

			if (density != null)
				tempList.add(density);

			if (scale != null)
				tempList.add(scale);

			input = new GeoElement[tempList.size()];
			input = tempList.toArray(input);
			break;
		}

		setOutputLength(1);
		setOutput(0, table);
		setDependencies(); // done by AlgoElement
	}
	

	public GeoText getResult() {
		return table;
	}

	public String[] getValueString() {
		if (!table.isDefined()) {
			return null;
		}
		return strValue;
	}

	public String[] getFrequencyString() {
		if (!table.isDefined()) {
			return null;
		}
		return strFrequency;
	}

	public String[] getHeaderString() {
		if (!table.isDefined()) {
			return null;
		}
		return strHeader;
	}
	
	private double scaleFactor;

	@Override
	public final void compute() {
		
		switch (type) {
		case HISTOGRAM:
			AlgoHistogram algoHistogram = (AlgoHistogram) chart
					.getParentAlgorithm();
			if (algoHistogram == null || algoHistogram.getLeftBorder() == null
					|| algoHistogram.getYValue() == null) {
				table.setUndefined();
				return;
			}

			strHeader = new String[2];
			strHeader[0] = loc.getMenu("Interval");
			if (algoHistogram.getUseDensityGeo() != null
					&& ((GeoBoolean) algoHistogram.getUseDensityGeo())
							.getBoolean()) {
				strHeader[1] = loc.getMenu("Frequency");
			} else {
				strHeader[1] = loc.getMenu("Count");
			}

			double[] leftBorder = algoHistogram.getLeftBorder();
			double[] f = algoHistogram.getYValue();
			strValue = new String[f.length];
			strFrequency = new String[f.length];
			for (int i = 0; i < f.length; i++) {
				strValue[i] = kernel.format(leftBorder[i],
						table.getStringTemplate());
				strFrequency[i] = kernel
						.format(f[i], table.getStringTemplate());
			}

			createLaTeXTable(true);
			break;

		case BARCHART:
			AlgoBarChart algoBarChart = (AlgoBarChart) chart
					.getParentAlgorithm();
			if (algoBarChart == null || algoBarChart.getValue() == null
					|| algoBarChart.getYValue() == null) {
				table.setUndefined();
				return;
			}
			strHeader = new String[2];
			strHeader[0] = loc.getMenu("Value");
			strHeader[1] = loc.getMenu("Count");

			strValue = algoBarChart.getValue();
			double[] f2 = algoBarChart.getYValue();
			strFrequency = new String[f2.length];
			for (int i = 0; i < f2.length; i++) {
				strFrequency[i] = kernel.format(f2[i],
						table.getStringTemplate());
			}

			createLaTeXTable(false);
			break;

		case STANDARD:

			// validate input arguments
			if (!freq.getResult().isDefined()) {
				table.setUndefined();
				return;
			}

			boolean useDens = useDensity != null && useDensity.getBoolean();
			GeoList fr = freq.getResult();
			int length = fr.size();

			// If classList does not exist,
			// get the unique value list and compute frequencies for this list
			if (classList == null) {

				if (scale != null){
					useDens = true; //we assume this will be used to compute frequencies
				}
				
				strHeader = new String[2];
				strHeader[0] = loc.getMenu("Value");
				if (useDens){
					if (scale != null){
						double scaleValue = scale.getDouble();
						if (Kernel.isEqual(scaleValue, 1.0)){
							strHeader[1] = loc.getMenu("FrequencyTable.Count");
						}else if (Kernel.isEqual(scaleValue * dataList.size(), 1)){
							strHeader[1] = loc.getMenu("FrequencyTable.RelativeFrequency");
						}else{
							strHeader[1] = loc.getMenu("FrequencyTable.Frequency");
						}
					}else{
						strHeader[1] = loc.getMenu("FrequencyTable.Frequency");
					}					
				}else{
					strHeader[1] = loc.getMenu("FrequencyTable.Count");
				}
				

				strValue = new String[length];
				strFrequency = new String[length];
				GeoList va = freq.getValue();

				for (int i = 0; i < length; i++) {
					strValue[i] = va.get(i).toValueString(
							table.getStringTemplate());
					strFrequency[i] = fr.get(i).toValueString(
								table.getStringTemplate());
				}
				createLaTeXTable(false);
			}

			// If classList exists, compute frequencies using the classList
			else {
				if (!classList.isDefined()) {
					table.setUndefined();
					return;
				}

				strHeader = new String[2];
				strHeader[0] = loc.getMenu("Interval");
				strHeader[1] = useDens ? loc.getMenu("Frequency") : loc
						.getMenu("Count");

				strValue = new String[length + 1];
				strFrequency = new String[length + 1];
				for (int i = 0; i < length; i++) {
					strValue[i] = classList.get(i).toValueString(
							table.getStringTemplate());
					strFrequency[i] = fr.get(i).toValueString(
							table.getStringTemplate());
				}
				// include final class limit
				strValue[length] = classList.get(length).toValueString(
						table.getStringTemplate());

				createLaTeXTable(true);
			}

			break;
		}

		table.setTextString(sb.toString());
	}

	private void createLaTeXTable(boolean useClassList) {
		if(kernel.getApplication().isHTML5Applet()){
			createLaTeXTableMQ(useClassList);
		}else{
			createLaTeXTableD(useClassList);
		}
	}
	private void createLaTeXTableD(boolean useClassList) {

		sb.setLength(0);
		sb.append("\\begin{array}{c|c}");

		sb.append(strHeader[0]);
		sb.append("&");
		sb.append(strHeader[1]);
		sb.append(" \\\\\\hline ");
		if (useClassList) {
			for (int i = 0; i < strFrequency.length - 1; i++) {
				sb.append(strValue[i]);
				sb.append("\\text{ -- }");
				sb.append(strValue[i + 1]);
				sb.append("&");
				sb.append(strFrequency[i]);
				sb.append("\\\\");
			}
		} else {
			for (int i = 0; i < strFrequency.length; i++) {
				sb.append(strValue[i]);
				sb.append("&");
				sb.append(strFrequency[i]);
				sb.append("\\\\");
			}
		}
		sb.append("\\end{array}");
	}
	
	private void createLaTeXTableMQ(boolean useClassList) {
//FrequencyTable[{1,2,3,4,5}]
		sb.setLength(0);
		sb.append("\\ggbtable{\\ggbtrlb{\\ggbtd{");

		sb.append(strHeader[0]);
		sb.append("}\\ggbtdll{");
		sb.append(strHeader[1]);
		sb.append("}}\\ggbtr{\\ggbtd{ ");
		if (useClassList) {
			for (int i = 0; i < strFrequency.length - 1; i++) {
				sb.append(strValue[i]);
				sb.append("\\text{ -- }");
				sb.append(strValue[i + 1]);
				sb.append("}\\ggbtdll{");
				sb.append(strFrequency[i]);
				sb.append("}\\ggbtr{\\ggbtd{");
			}
		} else {
			for (int i = 0; i < strFrequency.length; i++) {
				sb.append(strValue[i]);
				sb.append("}\\ggbtdll{");
				sb.append(strFrequency[i]);
				sb.append("}}\\ggbtr{\\ggbtd{");
			}
		}
		sb.append("}}}");
	}

	// TODO Consider locusequability

}
