package geogebra.gui.dialog;

import geogebra.common.gui.InputHandler;
import geogebra.common.gui.dialog.handler.NumberInputHandler;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.main.DialogManager;
import geogebra.common.util.Unicode;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.text.JTextComponent;

public class InputDialogAngleFixed extends AngleInputDialog implements
		KeyListener {

	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	private GeoPoint geoPoint1;
	GeoSegment[] segments;
	GeoPoint[] points;
	GeoElement[] selGeos;

	private Kernel kernel;

	public InputDialogAngleFixed(AppD app, String title, InputHandler handler,
			GeoSegment[] segments, GeoPoint[] points, GeoElement[] selGeos,
			Kernel kernel) {
		super(app, app.getPlain("Angle"), title, defaultRotateAngle, false,
				handler, false);

		geoPoint1 = points[0];
		this.segments = segments;
		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;

		this.inputPanel.getTextComponent().addKeyListener(this);

	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
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

		inputText = inputPanel.getText();

		// negative orientation ?
		if (rbClockWise.isSelected()) {
			inputText = "-(" + inputText + ")";
		}

		boolean success = inputHandler.processInput(inputText);

		cons.setSuppressLabelCreation(oldVal);

		if (success) {
			String angleText = getText();
			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0")) {
				defaultRotateAngle = angleText;
			} else {
				defaultRotateAngle = "45" + "\u00b0";
			}
			DialogManager.doAngleFixed(kernel, segments, points, selGeos,
					((NumberInputHandler) inputHandler).getNum(),
					rbClockWise.isSelected());

			return true;
		}

		return false;

	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!wrappedDialog.isModal()) {
			app.setCurrentSelectionListener(null);
		}
		((GuiManagerD)app.getGuiManager()).setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	/*
	 * auto-insert degree symbol when appropriate
	 */
	public void keyReleased(KeyEvent e) {

		// return unless digit typed
		if (!Character.isDigit(e.getKeyChar()))
			return;

		JTextComponent tc = inputPanel.getTextComponent();
		String text = tc.getText();

		// if text already contains degree symbol or variable
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i)))
				return;
		}

		int caretPos = tc.getCaretPosition();

		tc.setText(tc.getText() + Unicode.degree);

		tc.setCaretPosition(caretPos);
	}
}
