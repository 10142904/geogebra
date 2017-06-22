package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web dialog for angle
 */
public class InputDialogAngleFixedW extends AngleInputDialogW{
	private static String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;

	private GeoSegmentND[] segments;
	private GeoPointND[] points;

	private Kernel kernel;
	
	private EuclidianController ec;
		
	public InputDialogAngleFixedW(AppW app, String title, InputHandler handler,
			GeoSegmentND[] segments, GeoPointND[] points, Kernel kernel,
			EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), title,
				defaultRotateAngle, false, handler, false);
		
		this.segments = segments;
		this.points = points;
		this.kernel = kernel;
		
		this.ec = ec;
		
	}

	/**
	 * Handles button clicks for dialog.
	 */
	@Override
	public void onClick(ClickEvent e) {
		actionPerformed(e);
	}
	
	@Override
	protected void actionPerformed(DomEvent e){
		Object source = e.getSource();
		try {
			if (source == btOK || sourceShouldHandleOK(source)) {
				processInput();
				//setVisibleForTools(!processInput());
			//} else if (source == btApply) {
			//	processInput();
			} else if (source == btCancel) {
				//setVisibleForTools(false);
				wrappedPopup.hide();
				inputPanel.getTextComponent().hideTablePopup();
				app.getActiveEuclidianView().requestFocusInWindow();
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			//setVisibleForTools(false);
			wrappedPopup.hide();
			inputPanel.getTextComponent().hideTablePopup();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}
	
	private void processInput() {
		
		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		inputText = inputPanel.getText();
		
		// negative orientation ?
		if (rbClockWise.getValue()) {
			inputText = "-(" + inputText + ")";
		}
		
		getInputHandler().processInput(inputText, this,
				new AsyncOperation<Boolean>() {

					@Override
					public void callback(Boolean ok) {
						cons.setSuppressLabelCreation(oldVal);
						if (ok) {
							doProcesInput();

						}
						setVisibleForTools(ok);
						}
				});


		
	}

	/**
	 * Create angle if input was valid
	 */
	protected void doProcesInput() {
		String angleText = inputPanel.getText();
		// keep angle entered if it ends with 'degrees'
		if (angleText.endsWith(Unicode.DEGREE_STRING)) {
			defaultRotateAngle = angleText;
		} else {
			defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES_STRING;
		}

		DialogManager.doAngleFixed(kernel, segments, points,

				((NumberInputHandler) getInputHandler()).getNum(),
				rbClockWise.getValue(), ec);

	}

	protected void setVisibleForTools(boolean ok) {
		if (!ok) {
			// wrappedPopup.show();
			inputPanel.getTextComponent().hideTablePopup();
		} else {
			wrappedPopup.hide();
			inputPanel.getTextComponent().hideTablePopup();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
	}
/*
	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}*/

	/*
 	* auto-insert degree symbol when appropriate
 	*/
	//moved the operations from onKeyUp to onKeyPress,
	//because only the KeyPress event has getCharCode method
	@Override
	public void onKeyPress(KeyPressEvent event) {
		AutoCompleteTextFieldW tc = inputPanel.getTextComponent();
		String text = tc.getText();

		String input = StringUtil.addDegreeSignIfNumber(event.getCharCode(), text);

		int caretPos = tc.getCaretPosition();
		tc.setText(input);
		tc.setCaretPosition(caretPos);
	}

	@Override
	public void onKeyUp(KeyUpEvent e) {
		super.onKeyUp(e);
	}
}
