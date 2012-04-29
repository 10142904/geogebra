package geogebra3D.gui.dialogs;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.gui.dialog.DialogManagerDesktop;
import geogebra.gui.dialog.InputDialog;
import geogebra.gui.dialog.handler.NumberInputHandler;
import geogebra.gui.dialog.options.OptionsDialog;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.gui.OptionsDialog3D;

/**
 * 3D version of the dialog manager.
 */
public class DialogManager3D extends DialogManagerDesktop {
	/**
	 * Construct 3D dialog manager.
	 * 
	 * Use {@link Application3D} instead of {@link Application}
	 * 
	 * @param app Instance of the 3d application object
	 */
	public DialogManager3D(Application3D app) {
		super(app);
	}
	
	@Override
	public void showNumberInputDialogCirclePointRadius(String title, GeoPointND geoPoint1,  AbstractEuclidianView view) {
		if (((GeoElement) geoPoint1).isGeoElement3D() || (view instanceof EuclidianViewForPlane)) {
			//create a circle parallel to plane containing the view
			showNumberInputDialogCirclePointDirectionRadius(title, geoPoint1, view.getDirection());
		} else {
			//create 2D circle
			super.showNumberInputDialogCirclePointRadius(title, geoPoint1, view);
		}		
	}
	
	/**
	 * @param title 
	 * @param geoPoint 
	 * @param forAxis 
	 * 
	 */
	public void showNumberInputDialogCirclePointDirectionRadius(String title, GeoPointND geoPoint, GeoDirectionND forAxis) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialog id = new InputDialogCirclePointDirectionRadius((Application) app, title, handler, geoPoint, forAxis, app.getKernel());
		id.setVisible(true);
	}
	
	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	public void showNumberInputDialogSpherePointRadius(String title, GeoPointND geoPoint) {
		NumberInputHandler handler = new NumberInputHandler(app.getKernel().getAlgebraProcessor());
		InputDialog id = new InputDialogSpherePointRadius((Application) app, title, handler, geoPoint, app.getKernel());
		id.setVisible(true);
	}
	
	public static class Factory extends DialogManagerDesktop.Factory {
		@Override
		public DialogManagerDesktop create(Application app) {
			if(!(app instanceof Application3D)) {
				throw new IllegalArgumentException();
			}
			
			DialogManager3D dialogManager = new DialogManager3D((Application3D)app);
			dialogManager.setOptionsDialogFactory(new OptionsDialog3D.Factory());
			return dialogManager;
		}
	}
}
