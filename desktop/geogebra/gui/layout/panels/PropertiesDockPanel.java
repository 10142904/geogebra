package geogebra.gui.layout.panels;

import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.properties.PropertiesViewD;
import geogebra.main.AppD;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Dock panel for the algebra view.
 */
public class PropertiesDockPanel extends DockPanel implements
		WindowFocusListener {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private PropertiesViewD view;

	JDialog dialog = null;

	/**
	 * @param app
	 */
	public PropertiesDockPanel(AppD app) {
		super(App.VIEW_PROPERTIES, // view id
				"Properties", // view title phrase
				null, // toolbar string
				true, // style bar?
				7, // menu order
				'E' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		// this.setShowStyleBar(true);
		super.setDialog(true);

	}

	private PropertiesViewD getPropertiesView() {
		return (PropertiesViewD) app.getGuiManager().getPropertiesView();
	}

	@Override
	protected JComponent loadComponent() {

		view = getPropertiesView();

		if (isOpenInFrame())
			view.windowPanel();
		else
			view.unwindowPanel();
		return view.getWrappedPanel();
	}

	@Override
	protected JComponent loadStyleBar() {
		return getPropertiesView().getStyleBar();
	}

	@Override
	protected void windowPanel() {
		super.windowPanel();
		getPropertiesView().windowPanel();
	}

	@Override
	protected void unwindowPanel() {
		super.unwindowPanel();
		getPropertiesView().unwindowPanel();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getImageIcon("view-properties24.png");
	}

	@Override
	public void createFrame() {

		super.createFrame();

		frame.addWindowFocusListener(this);
		frame.addWindowListener(this);

	}

	@Override
	public void updateLabels() {
		super.updateLabels();
		if (view != null) {
			titleLabel
					.setText(view.getTypeString(view.getSelectedOptionType()));
		}
	}

	/**
	 * Update all elements in the title bar.
	 */
	@Override
	public void updateTitleBar() {
		super.updateTitleBar();
		titleLabel.setVisible(true);
	}

	public void windowGainedFocus(WindowEvent arg0) {
		//

	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	/*
	 * Window Listener
	 */
	@Override
	public void windowActivated(WindowEvent e) {
		/*
		 * if (!isModal()) { geoTree.setSelected(null, false);
		 * //selectionChanged(); } repaint();
		 */
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// cancel();
		closeDialog();
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	public void closeDialog() {
		view.closeDialog();
	}

	@Override
	public void setFocus(boolean hasFocus, boolean updatePropertiesView) {

		// no action on properties view

		setFocus(hasFocus);
	}
	
	/**
	 * update menubar (and dockbar) on visibility changes 
	 */
	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);
		dockManager.getLayout().getApplication().updateMenubar();
	}

}
