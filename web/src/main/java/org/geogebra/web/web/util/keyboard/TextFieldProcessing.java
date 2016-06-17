package org.geogebra.web.web.util.keyboard;

import java.util.HashSet;

import org.geogebra.common.main.GWTKeycodes;
import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.keyboard.OnScreenKeyBoard;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.gui.view.algebra.InputTreeItem;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing implements KeyboardListener {

	protected MathKeyboardListener field;
	protected State state = State.empty;
	protected HashSet<String> needsLeftParenthesis = new HashSet<String>();

	public TextFieldProcessing(MathKeyboardListener field) {
		initNeedsLeftParenthesis();
		setField(field);
	}

	/**
	 * add the default Strings
	 */
	private void initNeedsLeftParenthesis() {
		needsLeftParenthesis.add("sin");
		needsLeftParenthesis.add("cos");
		needsLeftParenthesis.add("tan");
		needsLeftParenthesis.add("ln");
		needsLeftParenthesis.add("sinh");
		needsLeftParenthesis.add("cosh");
		needsLeftParenthesis.add("tanh");
		needsLeftParenthesis.add("arcsin");
		needsLeftParenthesis.add("arccos");
		needsLeftParenthesis.add("arctan");

		// my on-screen keyboard has these! Hungarian
		// TODO: fix this by calling updateForNewLanguage!
		needsLeftParenthesis.add("tg");
		needsLeftParenthesis.add("sh");
		needsLeftParenthesis.add("ch");
		needsLeftParenthesis.add("th");
	}

	/**
	 * change language specific notations
	 * 
	 * @param loc
	 */
	public void updateForNewLanguage(KeyboardLocale loc) {
		needsLeftParenthesis.clear();
		initNeedsLeftParenthesis();

		needsLeftParenthesis.add(loc.getFunction("sin"));
		needsLeftParenthesis.add(loc.getFunction("cos"));
		needsLeftParenthesis.add(loc.getFunction("tan"));
		needsLeftParenthesis.add(loc.getFunction("sinh"));
		needsLeftParenthesis.add(loc.getFunction("cosh"));
		needsLeftParenthesis.add(loc.getFunction("tanh"));
	}

	/**
	 * @param field
	 *            the field that should receive all actions
	 */

	private void setField(MathKeyboardListener field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof GTextBox) {
			state = State.gTextBox;
		} else if (field instanceof InputTreeItem) {
			state = State.inputTreeItem;
		} else if (field instanceof EquationEditorListener) {
			state = State.equationEditorListener;
		} else {
			state = State.other;
		}
		if (field != null && field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		}
	}

	/**
	 * Focus/Blur the text field
	 * 
	 * @param focus
	 *            true: focus; false: blur
	 */
	@Override
	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setFocus(focus);
		} else {
			if (field == null) {
				return;
			}

			switch (state) {
			case gTextBox:
				((GTextBox) field).setFocus(focus);
				break;
			case equationEditorListener:
			case inputTreeItem:
				if (focus) {
					((EquationEditorListener) field).setFocus(true, false);
				}
				break;
			}
		}
	}

	public void setKeyBoardModeText(boolean b) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setKeyBoardModeText(b);
		}
	}

	/**
	 * simulates an enter key event
	 */
	@Override
	public void onEnter() {
		if (state == State.autoCompleteTextField) {
			NativeEvent event = Document.get().createKeyDownEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false, false, false,
					false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false, false, false,
					ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));
		} else {
			switch (state) {
			case gTextBox:
				NativeEvent event2 = Document.get().createKeyDownEvent(false,
						false, false, false, ENTER);
				((GTextBox) field).onBrowserEvent(Event.as(event2));
				break;
			case equationEditorListener:
			case inputTreeItem:
				((EquationEditorListener) field).keyup(ENTER, false, false,
						false);
				break;
			}
		}
	}

	@Override
	public boolean resetAfterEnter() {
		return state == State.equationEditorListener
				&& ((EquationEditorListener) field).resetAfterEnter();
	}

	/**
	 * simulates a backspace key event
	 */
	@Override
	public void onBackSpace() {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).onBackSpace();
		} else {
			switch (state) {
			case gTextBox:
				int start = ((GTextBox) field).getCursorPos();
				int end = start + ((GTextBox) field).getSelectionLength();

				if (((GTextBox) field).getSelectionLength() < 1) {
					// nothing selected -> delete character before cursor
					end = start;
					start--;
				}

				if (start > 0) {
					// cursor not at the beginning of text -> delete something
					String oldText = ((GTextBox) field).getText();
					String newText = oldText.substring(0, start)
							+ oldText.substring(end);
					((GTextBox) field).setText(newText);
					((GTextBox) field).setCursorPos(start);
				}
				break;
			case equationEditorListener:
			case inputTreeItem:
				((EquationEditorListener) field).keydown(BACKSPACE, false,
						false, false);
				break;
			}
		}
	}

	/**
	 * simulates arrow events
	 * 
	 * @param type
	 *            {@link ArrowType}
	 */
	@Override
	public void onArrow(ArrowType type) {
		if (state == State.autoCompleteTextField) {
			int caretPos = ((AutoCompleteTextFieldW) field).getCaretPosition();
			switch (type) {
			case left:
				if (caretPos > 0)
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos - 1);
				break;
			case right:
				if (caretPos < ((AutoCompleteTextFieldW) field).getText()
						.length()) {
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos + 1);
				}
				break;
			}
		} else {
			switch (state) {
			case gTextBox:
				int cursorPos = ((GTextBox) field).getCursorPos();
				switch (type) {
				case left:
					if (cursorPos > 0)
						((GTextBox) field).setCursorPos(cursorPos - 1);
					break;
				case right:
					if (cursorPos < ((GTextBox) field).getText().length()) {
						((GTextBox) field).setCursorPos(cursorPos + 1);
					}
					break;
				}
				break;
			case equationEditorListener:
			case inputTreeItem:
				switch (type) {
				case left:
					((EquationEditorListener) field).keydown(
							GWTKeycodes.KEY_LEFT, false, false, false);
					break;
				case right:
					((EquationEditorListener) field).keydown(
							GWTKeycodes.KEY_RIGHT, false, false, false);
					break;
				}
				break;
			}
		}
	}

	/**
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	@Override
	public void insertString(String text) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).insertString(text);
			if (text.startsWith("(")) {
				// moves inside the brackets
				onArrow(ArrowType.left);
			} else if (text.equals(KeyboardConstants.A_POWER_X)) {
				((AutoCompleteTextFieldW) field).insertString("^");
			} else if (text.equals("nroot")) {
				((AutoCompleteTextFieldW) field).insertString("()");
				onArrow(ArrowType.left);
			}
		} else {
			switch (state) {
			case gTextBox:
				insertAtEnd(text);
				break;
			case equationEditorListener:
			case inputTreeItem:
				if (text.equals(KeyboardConstants.A_POWER_X)) {
					if (((EquationEditorListener) field).getText().length() == 0) {
						return;
					}
					((EquationEditorListener) field).keypress('^', false,
							false, false, false);
				} else if (text.startsWith(Unicode.EULER_STRING)) {
					// this should be like this, in order to avoid confusion
					// with a possible variable name called "e"
					((EquationEditorListener) field)
							.insertString(Unicode.EULER_STRING);
					// inserts: ^{}
					((EquationEditorListener) field).keypress('^', false,
							false, false, false);
				} else if (needsLeftParenthesis.contains(text)) {
					((EquationEditorListener) field).insertString(text);
					// inserts: () in Math mode, ( in Quotations
					// ((EquationEditorListener) field).keypress('(', false,
					// false,
					// false);
					// for Quotations, we need an additional ')' and backspace
					// but the timing of these events might makes these things
					// more indeterministic! so instead, preparing custom code
					// just for this use case...

					// some parameter should be added to mean also '(', ')' AND
					// a left key effect (without keydown event triggering)
					// instead of overriding ALT, CTRL, SHIFT, it is more clean
					// to add another parameter "more" so we can add custom code
					// then
					((EquationEditorListener) field).keypress('(', false,
							false, false, true);
					// the last true parameter means that this '(',
					// when executed, shall also make a ')' and a left key

					// if there is only one event happening, then we will
					// probably not have issues for indeterministic behaviour
					// that's why this is probably better than entering
					// "(" + ")" by keypress events...
				} else if (text.equals("nroot")) {
					((EquationEditorListener) field).insertString("nroo");
					((EquationEditorListener) field).keypress('t', false,
							false, true, false);
				} else if (text.equals("log")) {
					((EquationEditorListener) field).insertString("log_{10}");
					((EquationEditorListener) field).keypress('(', false,
							false, false, true);
				} else if (text.equals(KeyboardConstants.A_SQUARE)) {
					((EquationEditorListener) field)
							.insertString(Unicode.Superscript_2 + "");
				} else if (keyPressNeeded(text)) {
					((EquationEditorListener) field).keypress(text.charAt(0),
							false, false, false,
							text.startsWith("(") || text.startsWith("|"));
				} else if (text.equals("abs")) {
					((EquationEditorListener) field).keypress('|', false,
							false, false, true);
				} else if (text.equals("quotes")) {
					((EquationEditorListener) field).keypress('"', false,
							false, false, true);
				} else {
					// if (text.length() == 1) {
					// ((EquationEditorListener) field).keypress(text.charAt(0),
					// false, false, false);
					// } else {
					((EquationEditorListener) field).insertString(text);
					// }
					// in case of keypress, we shall wait until the keypress
					// event
					// is really effective and only check for show suggestions
					// then...
					// but this is non-trivial unless we deal with it in the
					// keypress
					// event, not sure it's worth the work when we can also use
					// insertString in this case as well...
					((EquationEditorListener) field).showOrHideSuggestions();
				}
				break;
			}
		}
	}

	/**
	 * @param text
	 *            to insert
	 * @return {@code true} if the RadioButtonTreeItem needs a keyPress event.
	 */
	private static boolean keyPressNeeded(String text) {
		return text.equals("/") || text.equals("_") || text.equals("$")
				|| text.equals(" ") || text.equals("|") || text.equals(",")
				|| text.equals("*") || text.startsWith("(") || text.equals(")")
				|| text.equals("[") || text.equals("]") || text.equals("{")
				|| text.equals("}") || text.equals(Unicode.SQUARE_ROOT + "")
				// allowing both syntaxes for * and / here
				|| text.equals(Unicode.MULTIPLY + "")
				|| text.equals(Unicode.DIVIDE);
	}

	/**
	 * only for {@link GTextBox}
	 * 
	 * @param text
	 */
	private void insertAtEnd(String text) {
		String oldText = ((GTextBox) field).getText();
		int caretPos = ((GTextBox) field).getCursorPos();

		String newText = oldText.substring(0, caretPos) + text
				+ oldText.substring(caretPos);
		((GTextBox) field).setText(newText);
		((GTextBox) field).setCursorPos(caretPos + text.length());
	}

	/**
	 * Method just used for RadioButtonTreeItem for now
	 */
	@Override
	public void scrollCursorIntoView() {
		switch (state) {
		case inputTreeItem:
		case equationEditorListener:
			((EquationEditorListener) field).scrollCursorIntoView();
			break;
		default:
			break;
		}
	}
}
