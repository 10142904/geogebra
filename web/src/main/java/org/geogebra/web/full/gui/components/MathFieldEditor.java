package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.EventUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField capable editor widget for the web.
 *
 * @author Laszlo
 */
public class MathFieldEditor implements IsWidget {

	private static final int PADDING_LEFT = 2;
	private static final int PADDING_TOP = 8;

	private final Kernel kernel;
	private FlowPanel main;
	private MathFieldW mathField;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public MathFieldEditor(App app, MathFieldListener listener) {
		kernel = app.getKernel();
		createMathField(listener, app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION));
		initEventHandlers();
	}

	private void createMathField(MathFieldListener listener, boolean directFormulaConversion) {
		main = new FlowPanel();
		Canvas canvas = Canvas.createIfSupported();
		mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, listener,
				directFormulaConversion,
				null);
		main.add(mathField);
	}

	private void initEventHandlers() {
		EventUtil.stopPointer(main.getElement());

		ClickStartHandler.init(main,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
											 PointerEventType type) {
						editorClicked();
					}
				});
	}

	/**
	 * Called when editor was clicked.
	 */
	protected void editorClicked() {
		setFocus();
	}

	/**
	 * Focus the editor
	 */
	public void setFocus() {
		mathField.setFocus(true);
	}

	/**
	 * Scroll content horizontally if needed.
	 */
	public void scrollHorizontally() {
		MathFieldW.scrollParentHorizontally(main, PADDING_LEFT);
	}

	/**
	 * Scroll content vertically if needed.
	 */
	public void scrollVertically() {
		mathField.scrollParentVertically(main, PADDING_TOP);
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	/**
	 * Sets editor text.
	 * @param text to set.
	 */
	public void setText(String text) {
		mathField.setText(text, false);
	}

	/**
	 * Add style to the editor.
	 *
	 * @param style to add.
	 */
	public void addStyleName(String style) {
		main.addStyleName(style);
	}
}
