package org.geogebra.desktop.main;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.InputDialog;
import org.geogebra.common.gui.dialog.TextInputDialog;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SegmentHandler;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class DialogManagerNoGui extends DialogManager implements ErrorHandler {

	private String[] inputs;
	private int position = 0;

	public DialogManagerNoGui(App app, String[] inputs) {
		this.app = app;
		this.inputs = inputs;
	}
	@Override
	public boolean showFunctionInspector(GeoFunction geoFunction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showDataSourceDialog(int mode, boolean doAutoLoadSelectedGeos) {
		// TODO Auto-generated method stub
	}

	@Override
	public InputDialog newInputDialog(App app1, String message, String title,
			String initString, InputHandler handler, GeoElement geo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showNumberInputDialogSegmentFixed(String menu,
			final GeoPointND geoPoint2) {
		final NumberInputHandler handler = new NumberInputHandler(
				geoPoint2.getKernel().getAlgebraProcessor());
		new SegmentHandler(geoPoint2, geoPoint2.getKernel())
				.doSegmentFixedAsync(getInput(), handler, this,

				new AsyncOperation<Boolean>() {

					public void callback(Boolean obj) {

					}
				});

	}

	@Override
	public void showNumberInputDialogAngleFixed(String menu,
			GeoSegmentND[] selectedSegments, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean showSliderCreationDialog(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showNumberInputDialogRotate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showNumberInputDialogDilate(String menu,
			GeoPolygon[] selectedPolygons, GeoPointND[] selectedPoints,
			GeoElement[] selGeos, EuclidianController ec) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showNumberInputDialogRegularPolygon(String menu,
			EuclidianController ec, GeoPointND geoPoint1,
			GeoPointND geoPoint2) {
		DialogManager.makeRegularPolygon(app, ec, getInput(),
				geoPoint1, geoPoint2, this, new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {


					}
				});

	}

	@Override
	public void showBooleanCheckboxCreationDialog(GPoint corner,
			GeoBoolean bool) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showNumberInputDialogCirclePointRadius(String title,
			GeoPointND geoPointND, EuclidianView view) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText,
			AsyncOperation<GeoNumberValue> callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showAngleInputDialog(String title, String message,
			String initText, AsyncOperation<GeoNumberValue> callback) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeAll() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showRenameDialog(GeoElement geo, boolean b, String label,
			boolean c) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showPropertiesDialog(ArrayList<GeoElement> geos) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showPropertiesDialog(OptionType type,
			ArrayList<GeoElement> geos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openToolHelp() {
		// TODO Auto-generated method stub
	}

	@Override
	public TextInputDialog createTextDialog(GeoText text, GeoPointND startPoint,
			boolean rw) {
		// TODO Auto-generated method stub
		return null;
	}

	public void showError(String msg) {
		throw new RuntimeException(msg);
	}

	public void showCommandError(String command, String message) {
		// TODO Auto-generated method stub

	}

	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetError() {
		// TODO Auto-generated method stub
	}

	private String getInput() {
		return inputs[position++];
	}

}
