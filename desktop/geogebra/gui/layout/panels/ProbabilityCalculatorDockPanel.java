package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.gui.layout.DockPanel;
import geogebra.main.AppD;

import javax.swing.JComponent;

/**
 * Dock panel for the probability calculator.
 */
public class ProbabilityCalculatorDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private AppD app;
	
	/**
	 * @param app
	 */
	public ProbabilityCalculatorDockPanel(AppD app) {
		super(
			App.VIEW_PROBABILITY_CALCULATOR, 		// view id
			"ProbabilityCalculator", 			// view title phrase
			null,								// toolbar string
			true,								// style bar?
			-1, 									// menu order
			'P'									// menu shortcut
		);
		
		this.app = app;
	}

	@Override
	protected JComponent loadComponent() {
		return app.getGuiManager().getProbabilityCalculator();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return app.getGuiManager().getProbabilityCalculator().getStyleBar();
	}
	
}
