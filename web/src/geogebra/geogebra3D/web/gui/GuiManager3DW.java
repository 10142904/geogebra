package geogebra.geogebra3D.web.gui;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import geogebra.geogebra3D.web.gui.view.properties.PropertiesView3DW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.ContextMenuGeoElementW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.properties.PropertiesViewW;
import geogebra.web.main.GDevice;

import com.google.gwt.user.client.Command;

/**
 * web gui manager for 3D
 * 
 * @author mathieu
 *
 */
public class GuiManager3DW extends GuiManagerW {

	private DockPanelW euclidian3Dpanel;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 */
	public GuiManager3DW(AppW app, GDevice device) {
		super(app, device);
	}

	@Override
	protected boolean initLayoutPanels() {

		if (super.initLayoutPanels()) {
			this.euclidian3Dpanel = new EuclidianDockPanel3DW(app);
			layout.registerPanel(this.euclidian3Dpanel);
			return true;
		}

		return false;

	}

	public DockPanelW getEuclidian3DPanel() {
		return this.euclidian3Dpanel;
	}

	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
	        geogebra.common.awt.GPoint p) {

		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		getDrawingPadpopupMenu3D(p.x, p.y).show(
		        ((EuclidianView3DW) view).g2p.getCanvas(), p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadpopupMenu3D(int x, int y) {
		currentPopup = new ContextMenuGraphicsWindow3DW((AppW) app, x, y);
		return (ContextMenuGeoElementW) currentPopup;
	}

	/**
	 * 
	 * @return command to show/hide 3D axis
	 */
	public Command getShowAxes3DAction() {
		return new Command() {

			public void execute() {
				// toggle axes
				((EuclidianView3DW) getApp().getEuclidianView3D()).toggleAxis();
				// getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
			}
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D grid
	 */
	public Command getShowGrid3DAction() {
		return new Command() {

			public void execute() {
				// toggle axes
				((EuclidianView3DW) getApp().getEuclidianView3D()).toggleGrid();
				// getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
			}
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D plane
	 */
	public Command getShowPlane3DAction() {
		return new Command() {

			public void execute() {
				// toggle axes
				((EuclidianView3DW) getApp().getEuclidianView3D())
				        .getSettings().togglePlane();
				// getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
			}
		};
	}

	@Override
	protected PropertiesViewW newPropertiesViewW(AppW app) {
		return new PropertiesView3DW(app);
	}

}
