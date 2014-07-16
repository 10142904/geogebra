package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.DialogManager;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 *	Dialog for "Segment with given length" tool
 */
public class InputDialogSegmentFixed extends InputDialogD {

	private GeoPointND geoPoint1;

	private Kernel kernel;

	public InputDialogSegmentFixed(AppD app, String title,
			InputHandler handler, GeoPointND point1, Kernel kernel) {
		super(app, app.getPlain("Length"), title, "", false, handler);

		geoPoint1 = point1;
		this.kernel = kernel;
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		App.debug("inputdialogsegmentfixed actionperformed");
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				setVisibleForTools(!processInput());
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisibleForTools(false);
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue
			setVisibleForTools(false);
		}
	}

	private boolean processInput() {

		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);

		if (ret) {
			DialogManager.doSegmentFixed(kernel, geoPoint1,
					((NumberInputHandler) inputHandler).getNum());
		}

		return ret;
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(this, true);
	}
}
