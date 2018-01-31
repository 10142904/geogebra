package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.main.Localization;

/**
 * @author gabor
 * 
 *         common superclass for ChiSquarePanel
 *
 */
public abstract class ChiSquarePanel {

	// ======================================
	// GeoGebra fields
	// ======================================
	protected Localization loc;
	protected StatisticsCalculator statCalc;
	private StatisticsCalculatorProcessor statProcessor;
	private StatisticsCollection sc;

	/**
	 * @param app
	 *            Application
	 * @param statCalc
	 *            SatisticCalculator
	 */
	public ChiSquarePanel(Localization loc, StatisticsCalculator statCalc) {
		this.loc = loc;
		this.statCalc = statCalc;
		this.statProcessor = statCalc.getStatProcessor();
		this.sc = statCalc.getStatististicsCollection();
		if (sc.chiSquareData == null) {
			sc.setChiSqData(3, 3);
		} else {
			sc.initComputation(sc.chiSquareData.length - 2,
					sc.chiSquareData.length > 0 ? sc.chiSquareData[0].length - 2
							: 0);
		}
	}


	public StatisticsCalculator getStatCalc() {
		return statCalc;
	}


	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getSc() {
		return sc;
	}

	/**
	 * added partly to kill findbugs warning about loc being unused
	 * 
	 * @param s
	 *            key
	 * @return translation
	 */
	protected String getMenu(String s) {
		return loc.getMenu(s);
	}



}
