package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.probcalculator.ProbabilityCalculator;

import javax.swing.JComponent;

/**
 * Dock panel for the probability calculator.
 */
public class ProbabilityCalculatorDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private App app;

	/**
	 * @param app
	 */
	public ProbabilityCalculatorDockPanel(App app) {
		super(App.VIEW_PROBABILITY_CALCULATOR, // view id
				"ProbabilityCalculator", // view title phrase
				null, // toolbar string
				true, // style bar?
				-1, // menu order
				'P' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		
	}

	@Override
	protected JComponent loadComponent() {
		return ((ProbabilityCalculator) app.getGuiManager().getProbabilityCalculator()).getWrapperPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return ((GuiManagerD) app.getGuiManager()).getProbabilityCalculator().getStyleBar().getWrappedToolbar();
	}

}
