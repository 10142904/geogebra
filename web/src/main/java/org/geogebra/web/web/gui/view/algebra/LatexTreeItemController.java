package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.inputfield.InputSuggestions;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Timer;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.web.MathFieldW;


/**
 * @author Laszlo
 *
 */
public class LatexTreeItemController extends RadioTreeItemController
		implements MathFieldListener, BlurHandler {

	private InputSuggestions sug;
	private RetexKeyboardListener retexListener;
	/** whether blur listener is disabled */
	boolean preventBlur = false;

	/**
	 * @param item
	 *            AV item
	 */
	public LatexTreeItemController(RadioTreeItem item) {
		super(item);
	}

	@Override
	protected void startEdit(boolean substituteNumbers) {
		if (item.isInputTreeItem() && item.onEditStart()) {
			setOnScreenKeyboardTextField();
		} else {
			super.startEdit(substituteNumbers);
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (preventBlur) {
			return;
		}


		item.onEnter(false);
		if (item.isEmpty() && item.isInputTreeItem()) {
			item.addDummyLabel();
			item.setItemWidth(item.getAV().getFullWidth());

		}

		if (item.getAV().isNodeTableEmpty()) {
			// #5245#comment:8, cases B and C excluded
			item.updateGUIfocus(event == null ? this : event.getSource(), true);
		}
	}

	/**
	 * @param keepFocus
	 *            whether focus should stay
	 * @param createSliders
	 *            whether to create sliders
	 */
	public void onEnter(final boolean keepFocus, boolean createSliders) {
		if (item.isInputTreeItem() && item.isEmpty()) {
			item.styleEditor();
			item.addDummyLabel();
			return;
		}
		item.setShowInputHelpPanel(false);
		if (item.geo == null) {
			if (StringUtil.empty(item.getText())) {
				return;
			}
			item.getAV().setLaTeXLoaded();
			createGeoFromInput(keepFocus, createSliders);
			return;
		}
		if (!isEditing()) {
			return;
		}

		item.stopEditing(item.getText(), new AsyncOperation<GeoElementND>() {

			@Override
			public void callback(GeoElementND obj) {
				if (obj != null && !keepFocus) {
					if (app.has(Feature.AUTOSCROLLING_SPREADSHEET)) {
						app.setScrollToShow(true);
					}
					obj.update();
				}
			}
		}, keepFocus);
	}
	@Override
	public void onEnter() {
		if (isSuggesting()) {
			sug.needsEnterForSuggestion();
			return;
		}
		onEnter(true, false);
		item.getAV().clearActiveItem();
	}

	@Override
	public void onKeyTyped() {
		item.onKeyTyped();
	}

	@Override
	public void onCursorMove() {
		item.onCursorMove();
	}

	@Override
	public void onUpKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyUp();
		}

	}


	@Override
	public void onDownKeyPressed() {
		if (isSuggesting()) {
			sug.onKeyDown();
		}
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		getMathField().setFormula(
				GeoGebraSerializer.reparse(getMathField().getFormula()));

	}

	/**
	 * @return whether suggestions are open
	 */
	public boolean isSuggesting() {
		return sug != null && sug.isSuggesting();
	}

	/**
	 * @param keepFocus
	 *            whether the focus should stay afterwards
	 */
	public void createGeoFromInput(final boolean keepFocus) {
		createGeoFromInput(keepFocus, false);
	}

	/**
	 * @param keepFocus
	 *            whether the focus should stay afterwards
	 * @param withSliders
	 *            whether to create sliders
	 */
	public void createGeoFromInput(final boolean keepFocus,
			boolean withSliders) {
		String newValue = item.getText();
		final String rawInput = app.getKernel().getInputPreviewHelper()
				.getInput(newValue);
		boolean textInput = isInputAsText();
		final String input = textInput ? "\"" + rawInput + "\"": rawInput;
		
		setInputAsText(false);
		
		final boolean valid = input.equals(newValue);

		app.setScrollToShow(true);
		final int oldStep = app.getKernel().getConstructionStep();
		AsyncOperation<GeoElementND[]> callback = new AsyncOperation<GeoElementND[]>() {

			@Override
			public void callback(GeoElementND[] geos) {

				if (geos == null) {
					// inputField.getTextBox().setFocus(true);
					setFocus(true);
					return;
				}

				// need label if we type just eg
				// lnx
				if (geos.length == 1 && !geos[0].isLabelSet()) {
					geos[0].setLabel(geos[0].getDefaultLabel());
				}

				if (app.has(Feature.AV_TEXT_ITEM) && geos.length == 1
						&& geos[0].isGeoText()) {
					geos[0].setEuclidianVisible(false);
				}
				InputHelper.updateProperties(geos,
						app.getActiveEuclidianView(), oldStep);
				app.setScrollToShow(false);
				/**
				 * if (!valid) { addToHistory(input, null);
				 * addToHistory(newValueF, latexx); } else { addToHistory(input,
				 * latexx); }
				 */

				Scheduler.get()
						.scheduleDeferred(new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								item.scrollIntoView();
								if (keepFocus) {
									setFocus(true);
								} else {
									item.setFocus(false, true);
								}

							}
						});

				item.setText("");
				item.removeOutput();
				item.runSuggestionCallbacks(geos[0]);

			}

		};
		// keepFocus==false: this was called from blur, don't use modal slider
		// dialog
		ErrorHandler err = null;
		if (!textInput) {
			err = item.getErrorHandler(valid, keepFocus, withSliders);
			err.resetError();
		}
		EvalInfo info = new EvalInfo(true, true).withSliders(true)
				.withFractions(true);
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(input, true, err,
						info, callback);
		if (!keepFocus) {
			item.setFocus(false, false);
		}

	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		GuiManagerW.makeKeyboardListener(retexListener, app).insertString(text);

	}

	/**
	 * @return keyboard listener
	 */
	public RetexKeyboardListener getRetexListener() {
		return retexListener;
	}

	/**
	 * @param retexListener
	 *            keyboard listener
	 */
	public void setRetexListener(RetexKeyboardListener retexListener) {
		this.retexListener = retexListener;
	}

	/**
	 * Coneect keyboard listener to keyboard
	 */
	public void setOnScreenKeyboardTextField() {
		app.getGuiManager().setOnScreenKeyboardTextField(getRetexListener());
		// prevent that keyboard is closed on clicks (changing
		// cursor position)
		CancelEventTimer.keyboardSetVisible();
	}

	@Override
	public void showKeyboard() {
		app.showKeyboard(retexListener);

	}
	
	/**
	 * @param show
	 *            whether to show keyboard
	 */
	public void initAndShowKeyboard(boolean show) {
		retexListener = new RetexKeyboardListener(item.canvas, getMathField());
		if (show) {
			app.getAppletFrame().showKeyBoard(true, retexListener, false);
		}

	}

	private MathFieldW getMathField() {
		return item.getMathField();
	}

	/**
	 * @return input suggestion model (lazy load)
	 */
	InputSuggestions getInputSuggestions() {
		if (sug == null) {
			sug = new InputSuggestions(app, item);
		}
		return sug;
	}

	/**
	 * Prevent blur in the next 200ms
	 */
	public void preventBlur() {
		this.preventBlur = true;
		Timer t = new Timer(){

			@Override
			public void run() {
				preventBlur=false;
				
			}			
		};
		t.schedule(200);

	}

	@Override
	public boolean onEscape() {
		if (item.geo != null || StringUtil.empty(item.getText())) {
			item.cancelEditing();
			return true;
		}
		return false;
	}
}