package geogebra.web.gui.dialog;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.ColorChangeHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.gui.dialog.handler.RenameInputHandler;
import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.common.main.OptionType;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.EventRenderable;
import geogebra.common.util.AsyncOperation;
import geogebra.html5.css.GuiResources;
import geogebra.html5.util.WindowReference;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.util.GoogleFileDescriptors;
import geogebra.web.gui.util.SaveDialogW;
import geogebra.web.gui.view.functioninspector.FunctionInspectorW;
import geogebra.web.javax.swing.GOptionPaneW;
import geogebra.web.main.AppW;
import geogebra.web.move.googledrive.events.GoogleLoginEvent;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class DialogManagerW extends DialogManager implements EventRenderable {

	private FunctionInspectorW functionInspector;
	private SaveUnsavedChanges saveUnsavedDialog;

	public DialogManagerW(App app) {
		super(app);		
	}

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		App.debug("Show Function Inspector");

		boolean success = true;

		try {
			if (functionInspector == null) {
				functionInspector = new FunctionInspectorW(((AppW) app),
						geoFunction);
			} else {
				functionInspector.insertGeoElement(geoFunction);
			}

			// show the view
			((GuiManagerW)app.getGuiManager()).setShowView(true, App.VIEW_FUNCTION_INSPECTOR);
			functionInspector.setInspectorVisible(true);
	

		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	@Override
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		showPropertiesDialog(OptionType.OBJECTS, geos);
	}

	@Override
	public void showBooleanCheckboxCreationDialog(GPoint loc, GeoBoolean bool) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation callback) {
		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor(),callback, app, oldVal);
		InputDialogW id = new InputDialogW(((AppW) app), message, title,
				initText, false, handler, true, false, null);
		id.setVisible(true);
	}
	
	@Override
	public void showNumberInputDialogRegularPolygon(String title, EuclidianController ec, 
			GeoPointND geoPoint1, GeoPointND geoPoint2) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogW id = new InputDialogRegularPolygonW(((AppW) app), ec, title,
				handler, geoPoint1, geoPoint2);
		id.setVisible(true);

	}	
	
	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPoint1, EuclidianView view) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogW id = new InputDialogCirclePointRadiusW(((AppW) app), title,
				handler, (GeoPoint) geoPoint1, app.getKernel());
		id.setVisible(true);

	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation callback) {

		// avoid labeling of num
		Construction cons = app.getKernel().getConstruction();
		oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor(),callback, app, oldVal);
		AngleInputDialog id = new AngleInputDialog(((AppW) app), message, title,
				initText, false, handler, true);
		id.setVisible(true);
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		ButtonDialogW dialog = new ButtonDialogW(((AppW) app), x, y, textfield);
		dialog.setVisible(true);
		return true;
	}

	@Override
	protected String prompt(String message, String def) {
		return Window.prompt(message, def);
	}

	@Override
	protected boolean confirm(String string) {
		return Window.confirm(string);
	}

	@Override
	public void closeAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showRenameDialog(GeoElement geo, boolean storeUndo, String initText,
			boolean selectInitText) {
		if (!app.isRightClickEnabled())
			return;
		geo.setLabelVisible(true);
		geo.updateRepaint();

		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

		InputDialogW id = new InputDialogW((AppW) app, app.getLocalization().getPlain("NewNameForA", geo.getNameDescription()),
				app.getPlain("Rename"), initText, false, handler, false, selectInitText, null);

		id.setVisible(true);
	}

	public SaveUnsavedChanges getSaveUnsavedDialog() {
		if (this.saveUnsavedDialog == null) {
			this.saveUnsavedDialog = new SaveUnsavedChanges(app);
		}
		
		return this.saveUnsavedDialog;
	}

	@Override
	public void showPropertiesDialog() {
		App.debug("SHOWWWWWWWWWWWW");
	}

	@Override
	public void showToolbarConfigDialog() {
		// TODO Auto-generated method stub

	}


	@Override
	public NumberValue showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Creates a new slider at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		app.setWaitCursor();

		SliderDialog dialog = new SliderDialog(((AppW) app), x, y);
		dialog.center();

		app.setDefaultCursor();

		return true;
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos,
			EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogRotate id = new InputDialogRotate(((AppW) app), title, handler, polys,
				points, selGeos, app.getKernel(), ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogAngleFixed(String title,
			GeoSegmentND[] segments, GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogAngleFixed id = new InputDialogAngleFixed(((AppW) app), title, handler,
				segments, points, selGeos, app.getKernel(), ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogDilate(String title, GeoPolygon[] polys,
			GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogW id = new InputDialogDilateW(((AppW) app), title, handler,
				points, selGeos, app.getKernel(), ec);
		id.setVisible(true);

	}
	
	@Override
	public void showNumberInputDialogSegmentFixed(String title,
			GeoPointND geoPoint1) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogW id = new InputDialogSegmentFixedW(((AppW) app), title,
				handler, geoPoint1, app.getKernel());
		id.setVisible(true);

	}
	
	SaveDialogW saveDialog = null;	

	public void setSaveDialog(SaveDialogW fileChooser) {
		this.saveDialog = fileChooser;
	}
	
	public SaveDialogW getSaveDialog() {
		if (saveDialog == null) {
			saveDialog = new SaveDialogW(app);
		}
		return saveDialog;
	}

	private GoogleFileDescriptors googleFileDescriptors = null;

	public void refreshAndShowCurrentFileDescriptors(
			String driveBase64FileName, String driveBase64description) {
		if (googleFileDescriptors == null) {
			googleFileDescriptors = new GoogleFileDescriptors();
		}
		if (driveBase64FileName == null) {
			googleFileDescriptors.hide();
		} else {
			googleFileDescriptors.setFileName(driveBase64FileName);
			googleFileDescriptors.setDescription(driveBase64description);
			
			//Steffi: In SMART the getSignIn()-Method returns NULL
			MenuItem lg = ((AppW) app).getObjectPool().getGgwMenubar().getMenubar().getSignIn();
			final int top = lg.getElement().getOffsetTop();
			final int left = lg.getElement().getOffsetLeft();
			googleFileDescriptors.setPopupPositionAndShow(new PositionCallback() {

				public void setPosition(int offsetWidth, int offsetHeight) {
					googleFileDescriptors.setPopupPosition(left - offsetWidth, top);
					googleFileDescriptors.show();

				}
			});	
		}
	}

	@Override
	public void showPropertiesDialog(OptionType type, ArrayList<GeoElement> geos) {
		if (!((AppW) app).letShowPropertiesDialog() || app.getGuiManager() == null)
			return;

		// get PropertiesView
		PropertiesView pv = (PropertiesView) ((GuiManagerW)app.getGuiManager())
				.getPropertiesView();
		int subType = -1;
		// select geos
		if (geos != null) {
			if (app.getSelectionManager().getSelectedGeos().size() == 0) {
				app.getSelectionManager().addSelectedGeos(geos, true);
			}

			if (geos.size() == 1 && geos.get(0).isEuclidianVisible()
					&& geos.get(0) instanceof GeoNumeric)
				// AbstractApplication.debug("TODO : propPanel.showSliderTab()");
				subType = 2;
		}

		// set properties option type
		if (type != null) {
			App.debug("Viewing optionsPanel subtype " + subType);
			pv.setOptionPanel(type, subType);
		}

		// show the view
		((GuiManagerW)app.getGuiManager()).setShowView(true, App.VIEW_PROPERTIES);

	}

	@Override
	public void openToolHelp() {
		App.debug("openToolHelp: unimplemented");
	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		app.getGuiManager().setShowView(true, App.VIEW_DATA_ANALYSIS);
		app.setMoveMode();
	}

	
	/**
	 * Shows alert dialog.
	 * @param text Alert message
	 */
	public void showAlertDialog(String text) {		
		GOptionPaneW.INSTANCE.showConfirmDialog(app, text, "",
		        GOptionPane.OK_OPTION, GOptionPane.INFORMATION_MESSAGE, null);
	}

	private WindowReference signInDialog = null;

	@Override
    public void showLogInDialog() {
	    if (signInDialog == null || signInDialog.closed()) {
	    	signInDialog = WindowReference.createSignInWindow(app);
	    } else {
	    	signInDialog.close();
	    	signInDialog = null;
	    }
    }
	
	private WindowReference openFromGGT = null;


	@Override
    public void showLogOutDialog() {
	    // TODO Auto-generated method stub
	    
    }

    public void renderEvent(BaseEvent event) {
	    if (event instanceof GoogleLoginEvent) {
	    	if (!((GoogleLoginEvent) event).isSuccessFull()) {
	    		App.debug("Login to Google failed");
	    	}
	    }
	    
    }

    private PopupPanel loadingAnimation  = null;
    
	/**
	 * Shows a loading animation
	 */
	public void showLoadingAnimation() {
	    if (loadingAnimation == null) {
	    	loadingAnimation = createLoadingAnimation();
	    }
	    loadingAnimation.center();
	    loadingAnimation.show();
    }

	private PopupPanel createLoadingAnimation() {
	    PopupPanel anim = new PopupPanel();
	    anim.addStyleName("loadinganimation");
	    anim.add(new Image(GuiResources.INSTANCE.getGeoGebraWebSpinner()));
	    return anim;
    }

	/**
	 * Hides a loading animation
	 */
	public void hideLoadingAnimation() {
		 if (loadingAnimation != null) {
		    	loadingAnimation.hide();;
		    }
    }
	
	
	/**
	 * Update labels in the GUI.
	 */
	public void setLabels() {

		//if (functionInspector != null)
		//	functionInspector.setLabels();

		if (textInputDialog != null)
			((TextInputDialogW) textInputDialog).setLabels();

		if (saveDialog != null) {
			saveDialog.setLabels();
		}
		
		getSaveUnsavedDialog().setLabels();
		//if (fileChooser != null)
		//	updateJavaUILanguage();
		
		//if (dataSourceDialog != null)
		//	dataSourceDialog.setLabels();
		
	}
	
	public void showColorChooserDialog(GColor originalColor, ColorChangeHandler handler) {
		ColorChooserDialog dialog = new ColorChooserDialog((AppW)app,
				originalColor, handler);
		dialog.center();
	}
	
	/**
	 * Creates a new, custom color .
	 * @param origColor The original color for initial purposes and preview.
	 * @return whether a color was given or not
	 */


	public FunctionInspectorW getFunctionInspector() {
		return functionInspector;
	}
}
