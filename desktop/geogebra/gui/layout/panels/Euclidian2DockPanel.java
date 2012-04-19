package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.main.Application;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Dock panel for the secondary euclidian view.
 */
public class Euclidian2DockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public Euclidian2DockPanel(Application app, String toolbar) {
		super(
			AbstractApplication.VIEW_EUCLIDIAN2, 	// view id
			"DrawingPad2", 					// view title phrase
			toolbar,							// toolbar string
			true,							// style bar?
			6,								// menu order
			'2'
		);
		
		this.app = app;
	}
	
//	public ImageIcon getIcon() {
//		return app.getImageIcon("document-properties.png");
//	}
//	
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) app.getEuclidianView2().getStyleBar();
	}
	
	@Override
	protected JComponent loadComponent() {
		return app.getEuclidianView2().getJPanel();
	}
	
	@Override
	public EuclidianViewND getEuclidianView() {
		return app.getEuclidianView2();
	}
	
	@Override
	public ImageIcon getIcon() { 
			return app.getImageIcon("view-graphics224.png");
	}
	
}
