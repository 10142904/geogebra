package geogebra.web.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.view.algebra.DialogType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.web.gui.InputDialogW;
import geogebra.web.main.AppW;

public class TextInputDialogW extends InputDialogW{

	private GeoText editGeo;
	private GeoPointND startPoint;

	public TextInputDialogW(AppW appW, String title, GeoText editGeo,
            GeoPointND startPoint, int cols, int rows, boolean isTextMode) {
	    // TODO Auto-generated constructor stub
		super(false);
		this.app = appW;
		this.startPoint = startPoint;
//		this.isTextMode = isTextMode;
//		this.editGeo = editGeo;
//		textInputDialog = this;
		inputHandler = new TextInputHandler();
//		isIniting = true;		
		
		createGUI(title, "", false, cols, rows, /*false*/ true, false, false, false,
				DialogType.DynamicText);
		
		wrappedPopup.center();
		wrappedPopup.show();
		
    }

	// =============================================================
	// TextInputHandler
	// =============================================================

	/**
	 * Handles creating or redefining GeoText using the current editor string.
	 * 
	 */
	private class TextInputHandler implements InputHandler {

		private Kernel kernel;

		private TextInputHandler() {
			kernel = app.getKernel();
		}

		public boolean processInput(String inputValue) {
			if (inputValue == null)
				return false;

			// no quotes?
			if (inputValue.indexOf('"') < 0) {
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text

				// ad (1) OBJECT LABEL
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
				}
				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}
			} else {
				// replace \n\" by \"\n, this is useful for e.g.:
				// "a = " + a +
				// "b = " + b
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}

			if (inputValue.equals("\"\""))
				return false;

			// create new text
			boolean createText = editGeo == null;
			if (createText) {
				GeoElement[] ret = kernel.getAlgebraProcessor()
						.processAlgebraCommand(inputValue, false);
				if (ret != null && ret[0].isTextValue()) {
					GeoText t = (GeoText) ret[0];
//					t.setLaTeX(isLaTeX, true);

					// make sure for new LaTeX texts we get nice "x"s
//					if (isLaTeX)
//						t.setSerifFont(true);

					if (startPoint.isLabelSet()) {
						try {
							t.setStartPoint(startPoint);
						} catch (Exception e) {
						}
					} else {

						// // Michael Borcherds 2008-04-27 changed to RealWorld
						// not absolute
						// startpoint contains mouse coords
						// t.setAbsoluteScreenLoc(euclidianView.toScreenCoordX(startPoint.inhomX),
						// euclidianView.toScreenCoordY(startPoint.inhomY));
						// t.setAbsoluteScreenLocActive(true);
						Coords coords = startPoint.getInhomCoordsInD(3);
						t.setRealWorldLoc(coords.getX(), coords.getY());
						t.setAbsoluteScreenLocActive(false);
					}

					// make sure (only) the output of the text tool is selected
					kernel.getApplication().getActiveEuclidianView()
							.getEuclidianController()
							.memorizeJustCreatedGeos(ret);

					t.updateRepaint();
					app.storeUndoInfo();
					return true;
				}
				return false;
			}

			// change existing text
			try {
				GeoText newText = (GeoText) kernel.getAlgebraProcessor()
						.changeGeoElement(editGeo, inputValue, true, true);

				// make sure newText is using correct LaTeX setting
//				newText.setLaTeX(isLaTeX, true);

				if (newText.getParentAlgorithm() != null)
					newText.getParentAlgorithm().update();
				else
					newText.updateRepaint();

				app.doAfterRedefine(newText);

				// make redefined text selected
				app.addSelectedGeo(newText);
				return true;
			} catch (Exception e) {
				app.showError("ReplaceFailed");
				return false;
			} catch (MyError err) {
				app.showError(err);
				return false;
			}
		}
	}
}
