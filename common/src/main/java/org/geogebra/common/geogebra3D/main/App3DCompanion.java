package org.geogebra.common.geogebra3D.main;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.GeoFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;

/**
 * 
 * @author mathieu
 *
 *         Companion for 3D application
 */
public abstract class App3DCompanion extends AppCompanion {
	// id of the first view
	private int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	protected ArrayList<EuclidianViewForPlaneCompanion> euclidianViewForPlaneCompanionList;

	private EuclidianViewForPlaneCompanion euclidianViewForPlaneCompanion;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public App3DCompanion(App app) {
		super(app);
	}

	@Override
	public Kernel newKernel() {
		return new Kernel3D(app, new GeoFactory3D());
	}

	@Override
	protected boolean tableVisible(int table) {
		return !(table == CommandsConstants.TABLE_CAS
				|| table == CommandsConstants.TABLE_ENGLISH);
	}

	// ///////////////////////////////
	// EUCLIDIAN VIEW FOR PLANE
	// ///////////////////////////////

	/**
	 * add euclidian views for plane settings
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            save as preference flag
	 */
	public void addCompleteUserInterfaceXMLForPlane(StringBuilder sb,
			boolean asPreference) {
		if (euclidianViewForPlaneCompanionList != null) {
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
				vfpc.getView().getXML(sb, asPreference);
			}
		}
	}

	@Override
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		super.getEuclidianViewXML(sb, asPreference);

		if (app.isEuclidianView3Dinited()) {
			// TODO it would be cleaner to use EuclidianSettings here instead
			app.getEuclidianView3D().getXML(sb, asPreference);
		}

		if (euclidianViewForPlaneCompanionList != null) {
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
				vfpc.getView().getXML(sb, asPreference);
			}
		}

	}

	/**
	 * create new euclidian view for plane
	 * 
	 * @param plane
	 *            plane
	 * @param evSettings
	 *            settings
	 * @return view companion
	 */
	protected abstract EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, EuclidianSettings evSettings,
			boolean panelSettings);

	@Override
	public EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, boolean panelSettings) {
		// create new view for plane and controller
		Settings settings = app.getSettings();
		String name = ((GeoElement) plane).getLabelSimple();
		EuclidianSettings evSettings = settings.getEuclidianForPlane(name);
		if (evSettings == null) {
			evSettings = new EuclidianSettingsForPlane(app);
			evSettings.setShowGridSetting(false);
			evSettings.setShowAxes(false, false);
			settings.setEuclidianSettingsForPlane(name, evSettings);
		}
		euclidianViewForPlaneCompanion = createEuclidianViewForPlane(plane,
				evSettings, panelSettings);
		evSettings.addListener(euclidianViewForPlaneCompanion.getView());
		euclidianViewForPlaneCompanion.getView().updateFonts();
		euclidianViewForPlaneCompanion.addExistingGeos();

		// add it to list
		if (euclidianViewForPlaneCompanionList == null) {
			euclidianViewForPlaneCompanionList = new ArrayList<EuclidianViewForPlaneCompanion>();
		}
		euclidianViewForPlaneCompanionList.add(euclidianViewForPlaneCompanion);

		return euclidianViewForPlaneCompanion;
	}

	@Override
	public void resetFonts() {

		super.resetFonts();

		if (app.isEuclidianView3Dinited()) {
			((EuclidianView) app.getEuclidianView3D()).updateFonts();
		}

		if (euclidianViewForPlaneCompanion != null) {
			euclidianViewForPlaneCompanion.getView().updateFonts();
		}
	}

	/**
	 * remove the view from the list
	 * 
	 * @param vfpc
	 *            view for plane companion
	 */
	public void removeEuclidianViewForPlaneFromList(
			EuclidianViewForPlaneCompanion vfpc) {
		euclidianViewForPlaneCompanionList.remove(vfpc);
		app.getSettings().removeEuclidianSettingsForPlane(
				((GeoElement) vfpc.getPlane()).getLabelSimple());
	}

	/**
	 * remove all euclidian views for plane
	 */
	public void removeAllEuclidianViewForPlane() {

		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
			vfpc.removeFromGuiAndKernel();
		}

		euclidianViewForPlaneCompanionList.clear();
		app.getSettings().clearEuclidianSettingsForPlane();

	}

	@Override
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {

		GeoElement geo = app.getKernel().lookupLabel(plane);
		if (geo == null) {
			return null;
		}
		if (!(geo instanceof ViewCreator)) {
			return null;
		}

		ViewCreator vc = (ViewCreator) geo;// getViewCreator(id);
		vc.setEuclidianViewForPlane(createEuclidianViewForPlane(vc, false));
		return getPanelForPlane();
	}

	/**
	 * 
	 * @return current dockpanel for plane
	 */
	abstract public DockPanel getPanelForPlane();

	@Override
	public Settings newSettings() {
		return new Settings(app, 3);
	}

	@Override
	public boolean hasEuclidianViewForPlane() {
		return euclidianViewForPlaneCompanionList != null
				&& euclidianViewForPlaneCompanionList.size() > 0;
	}

	@Override
	public boolean hasEuclidianViewForPlaneVisible() {
		if (!hasEuclidianViewForPlane()) {
			return false;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {

			if (c.isPanelVisible()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public EuclidianView getViewForPlaneVisible() {
		if (!hasEuclidianViewForPlane()) {
			return null;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			if (c.getView().isShowing()) {
				return c.getView();
			}
		}

		return null;

	}

	@Override
	public void addToViewsForPlane(GeoElement geo) {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			c.getView().add(geo);
		}
	}

	@Override
	public void removeFromViewsForPlane(GeoElement geo) {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			c.getView().remove(geo);
		}
	}

	@Override
	public final void resetEuclidianViewForPlaneIds() {
		viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}

	public int incViewID() {
		return viewId++;
	}
	
	@Override
	public void setFlagForSCADexport() {
		app.getEuclidianView3D().setFlagForSCADexport();
	}

}
