package geogebra3D.gui;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.euclidian.EuclidianViewD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.ContextMenuChooseGeo;
import geogebra.gui.GuiManagerD;
import geogebra.gui.dialog.InputDialog;
import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.gui.layout.LayoutD;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3D;
import geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.gui.dialogs.DialogManager3D;
import geogebra3D.gui.dialogs.InputDialogCirclePointDirectionRadius;
import geogebra3D.gui.dialogs.InputDialogSpherePointRadius;
import geogebra3D.gui.layout.panels.EuclidianDockPanel3D;
import geogebra3D.gui.view.algebra.AlgebraView3D;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 *
 */
public class GuiManager3D extends GuiManagerD {

	
	private AbstractAction showAxes3DAction, showGrid3DAction, showPlaneAction;
	
	/** 
	 * default constructor
	 * @param app
	 */
	public GuiManager3D(Application app) {
		super(app);
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		dialogManagerFactory = new DialogManager3D.Factory();
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}
	
	/**
	 * Add 3D euclidian view to layout.
	 */
	@Override
	protected void initLayoutPanels() {
		super.initLayoutPanels();
		EuclidianDockPanel3D panel = new EuclidianDockPanel3D((Application)app);
		getLayout().registerPanel(panel);
	}
	
	
	//////////////////////////////
	// ACTIONS
	//////////////////////////////
	
	@Override
	protected boolean initActions() {
		
		if (!super.initActions())
			return false;
		showAxes3DAction = new AbstractAction(app.getMenu("Axes"),
				((Application)app).getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				((Application3D) app).toggleAxis3D();
				//app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};

		showGrid3DAction = new AbstractAction(app.getMenu("Grid"),
				((Application)app).getImageIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle grid
				((Application3D) app).toggleGrid3D();
				//app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};
		
		showPlaneAction = new AbstractAction(app.getMenu("Plane"),
				((Application)app).getImageIcon("plane.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle plane
				((Application3D) app).togglePlane();
				app.storeUndoInfo();
				app.updateMenubar();
			}
		};

		
		return true;
		
	}
	
	public AbstractAction getShowAxes3DAction() {
		initActions();
		return showAxes3DAction;
	}

	public AbstractAction getShowGrid3DAction() {
		initActions();
		return showGrid3DAction;
	}
	
	public AbstractAction getShowPlaneAction() {
		initActions();
		return showPlaneAction;
	}
	
	
	
	//////////////////////////////
	// POPUP MENU
	//////////////////////////////
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	/*
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views		
		app.getEuclidianView().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}
	*/
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view, geogebra.common.awt.GPoint p) {
		// clear highlighting and selections in views		
		((Application3D) app).getEuclidianView3D().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				(Application)app, p.x, p.y);
		popupMenu.show(((EuclidianViewND) view).getJPanel(), p.x, p.y);
	}
	
	
	
	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 * @param selectedGeos first geos
	 * @param geos list of geos
	 * @param view view calling
	 * @param p place to show the popup menue
	 */
	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewND view,
			geogebra.common.awt.GPoint p) {
		
		if (selectedGeos == null || selectedGeos.get(0) == null)
			return;

		// clear highlighting and selections in views
		app.getActiveEuclidianView().resetMode();
		
		Component invoker = view.getJPanel();

		Point screenPos = (invoker == null) ? new Point(0, 0) : invoker
				.getLocationOnScreen();
		screenPos.translate(p.x, p.y);


		ContextMenuChooseGeo popupMenu = new ContextMenuChooseGeo((Application)app, view, selectedGeos, geos, screenPos, p);
		popupMenu.show(invoker, p.x, p.y);


	}


	
	
	//////////////////////////////
	// ALGEBRA VIEW
	//////////////////////////////
	
	@Override
	protected AlgebraView newAlgebraView(AlgebraController algc){
		return new AlgebraView3D(algc);
	}
	
	@Override
	protected EuclidianViewD newEuclidianView(boolean[] showAxis, boolean showGrid, int id){
		return new EuclidianViewFor3D(new EuclidianControllerFor3D(kernel), showAxis, showGrid, id);
	}
}
