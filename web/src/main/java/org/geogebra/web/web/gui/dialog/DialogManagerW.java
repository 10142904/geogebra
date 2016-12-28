package org.geogebra.web.web.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.gui.dialog.handler.RenameInputHandler;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.GDialogBox;
import org.geogebra.web.html5.gui.LoadingApplication;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW.ToolTipLinkType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.export.AnimationExportDialogW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.gui.view.data.DataAnalysisViewW;
import org.geogebra.web.web.gui.view.functioninspector.FunctionInspectorW;
import org.geogebra.web.web.main.AppWFull;
import org.geogebra.web.web.main.AppWapplication;
import org.geogebra.web.web.main.GDevice;
import org.geogebra.web.web.move.googledrive.events.GoogleLoginEvent;
import org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class DialogManagerW extends DialogManager implements EventRenderable, LoadingApplication {

	private FunctionInspectorW functionInspector;
	protected SaveDialogW saveDialog = null;
	protected UploadImageDialog imageDialog;
	private RecoverAutoSavedDialog autoSavedDialog;
	
	public DialogManagerW(AppW app) {
		super(app);
		if (app.getGoogleDriveOperation() != null) {
			((GoogleDriveOperationW) app.getGoogleDriveOperation()).getView()
					.add(this);
		}
	}

	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		Log.debug("Show Function Inspector");

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
		CheckboxCreationDialogW dlg = new CheckboxCreationDialogW((AppW)app, loc, bool);
		dlg.show();
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// avoid labeling of num
		final Construction cons = app.getKernel().getConstruction();
		oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor(),callback, app, oldVal);
		InputDialogW id = new InputDialogW(((AppW) app), message, title,
		        initText, false, handler, true, false, null) {
			@Override
			protected void cancel() {
				cons.setSuppressLabelCreation(false);
				super.cancel();
			}
		};
		id.setVisible(true);
	}
	
	/**
	 * shows the {@link RecoverAutoSavedDialog}
	 * @param app2 {@link AppWapplication}
	 */
	public void showRecoverAutoSavedDialog(AppWFull app2, String json) {
		if (this.autoSavedDialog == null) {
			this.autoSavedDialog = new RecoverAutoSavedDialog(app2);
		}
		this.autoSavedDialog.setJSON(json);
		this.autoSavedDialog.show();
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
		AngleInputDialogW id = new AngleInputDialogW(((AppW) app), message, title,
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
		if (app.getGuiManager() != null) {
			app.getGuiManager().clearInputbar();
		}
		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

		InputDialogW id = new InputDialogW((AppW) app, app.getLocalization().getPlain("NewNameForA", geo.getNameDescription()),
				loc.getMenu("Rename"), initText, false, handler, false,
				selectInitText, null);

		id.setVisible(true);
	}
	
	
	/**
	 * @param loc {@link GeoPoint}
	 */
	public void showImageInputDialog(GeoPoint loc, GDevice device) {
		if (this.imageDialog == null) {
			this.imageDialog =  device.getImageInputDialog((AppW) app);
		}
		imageDialog.setLocation(loc);
		imageDialog.center();
		imageDialog.show();
	}

	@Override
	public void showPropertiesDialog() {
	}

	@Override
	public void showToolbarConfigDialog() {
		// TODO Auto-generated method stub

	}


	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {

		// avoid labeling of num
		NumberChangeSignInputHandler handler = new NumberChangeSignInputHandler(
				app.getKernel().getAlgebraProcessor(),
				callback, app, oldVal);
		NumberChangeSignInputDialogW id = new NumberChangeSignInputDialogW(
				((AppW) app), message, title, initText, handler, changingSign,
				checkBoxText);
		id.setVisible(true);

	}

	/**
	 * Creates a new slider at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		app.setWaitCursor();

		SliderDialogW dialog = new SliderDialogW(((AppW) app), x, y);
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
		InputDialogRotateW id = new InputDialogRotatePointW(((AppW) app), title, handler, polys,
				points, selGeos, ec);
		id.setVisible(true);

	}

	@Override
	public void showNumberInputDialogAngleFixed(String title,
			GeoSegmentND[] segments, GeoPointND[] points, GeoElement[] selGeos, EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(app.getKernel()
				.getAlgebraProcessor());
		InputDialogAngleFixedW id = new InputDialogAngleFixedW(((AppW) app), title, handler,
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
	
	/**
	 * 
	 * @return {@link SaveDialogW}
	 */
	public SaveDialogW getSaveDialog() {
		if (saveDialog == null) {
			saveDialog = new SaveDialogW((AppW) app);
		}
		// set default saveType
		saveDialog.setSaveType(MaterialType.ggb);
		return saveDialog;
	}
	
	/**
	 * shows the {@link SaveDialogW} centered on the screen
	 */
	public void showSaveDialog() {
		getSaveDialog().center();
	}



	@Override
	public void showPropertiesDialog(OptionType type, ArrayList<GeoElement> geos) {
		if (!((AppW) app).letShowPropertiesDialog() || app.getGuiManager() == null)
			return;

		// get PropertiesView
		PropertiesView pv = ((GuiManagerW) app.getGuiManager())
				.getPropertiesView(type);
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
			Log.debug("Viewing optionsPanel subtype " + subType);
			pv.setOptionPanel(type, subType);
		}

		// show the view
		((GuiManagerW)app.getGuiManager()).setShowView(true, App.VIEW_PROPERTIES);

	}

	@Override
	public void openToolHelp() {
		int mode = app.getMode();
		ToolTipManagerW.sharedInstance().showBottomInfoToolTip(
				app.getToolTooltipHTML(mode),
				((AppW) app).getGuiManager().getTooltipURL(mode),
				ToolTipLinkType.Help, (AppW) app,
				((AppW) app).getAppletFrame().isKeyboardShowing());
	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		if (mode == EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS 
				|| mode == EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS
				|| mode == EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS) {
	
			Log.debug("[DAMODE] about to show mode " + mode);
			DataAnalysisViewW da = (DataAnalysisViewW)app.getGuiManager().getDataAnalysisView();
			da.changeMode(mode);
			app.getGuiManager().setShowView(true, App.VIEW_DATA_ANALYSIS);
			}
	}

	
	/**
	 * Shows alert dialog.
	 * @param text Alert message
	 */
	public void showAlertDialog(String text) {		
		((AppW) app)
				.getGuiManager()
				.getOptionPane()
				.showConfirmDialog(app, text, "",
		        GOptionPane.OK_OPTION, GOptionPane.INFORMATION_MESSAGE, null);
	}

	

	@Override
    public void renderEvent(BaseEvent event) {
	    if (event instanceof GoogleLoginEvent) {
	    	if (!((GoogleLoginEvent) event).isSuccessFull()) {
				Log.debug("Login to Google failed");
	    	}
	    }
	    
    }

    private PopupPanel loadingAnimation  = null;
    
	/**
	 * Shows a loading animation
	 */
	@Override
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
		anim.add(
				new Image(GuiResourcesSimple.INSTANCE.getGeoGebraWebSpinner()));
	    return anim;
    }

	/**
	 * Hides a loading animation
	 */
	@Override
	public void hideLoadingAnimation() {
		 if (loadingAnimation != null) {
		    	loadingAnimation.hide();
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
		
		if (imageDialog != null) {
			imageDialog.setLabels();
		}

		if (this.autoSavedDialog != null) {
			this.autoSavedDialog.setLabels();
		}
		//if (fileChooser != null)
		//	updateJavaUILanguage();
		
		//if (dataSourceDialog != null)
		//	dataSourceDialog.setLabels();
		
	}
	
	/**
	 * Creates a new {@link ColorChooserDialog}.
	 * 
	 * @param {@link GColor originalColor}.
	 * @param {@link ColorChangeHandler handler}
	 */
	@Override
	public void showColorChooserDialog(GColor originalColor,
	        ColorChangeHandler handler) {
		ColorChooserDialog dialog = new ColorChooserDialog((AppW)app,
				originalColor, handler);
		dialog.center();
	}

	/**
	 * @return {@link FunktionInspectorW}
	 */
	public FunctionInspectorW getFunctionInspector() {
		return functionInspector;
	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint, boolean rw) {
		return new TextInputDialogW(app, loc.getMenu("Text"), text,
				startPoint, rw, 30,
	        6, app.getMode() == EuclidianConstants.MODE_TEXT);
	}


	@Override
	public InputDialog newInputDialog(App app, String message, String title, String initString, boolean autoComplete,
			InputHandler handler, GeoElement geo, boolean showApply) {
		return new InputDialogW((AppW) app, message, title, initString, autoComplete, handler, geo, showApply);
	}

	public void showAnimGifExportDialog() {
		GDialogBox dialog = new AnimationExportDialogW((AppW) app);
		dialog.center();
		dialog.show();
	}
}
