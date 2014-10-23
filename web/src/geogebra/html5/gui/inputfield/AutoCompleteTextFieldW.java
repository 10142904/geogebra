package geogebra.html5.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.VirtualKeyboardListener;
import geogebra.common.gui.inputfield.AltKeys;
import geogebra.common.gui.inputfield.AutoComplete;
import geogebra.common.gui.inputfield.MyTextField;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.main.App;
import geogebra.common.main.GWTKeycodes;
import geogebra.common.main.Localization;
import geogebra.common.main.MyError;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.Korean;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.html5.event.KeyEventsHandler;
import geogebra.html5.event.KeyListenerW;
import geogebra.html5.gui.util.BasicIcons;
import geogebra.html5.gui.view.autocompletion.CompletionsPopup;
import geogebra.html5.gui.view.autocompletion.ScrollableSuggestBox;
import geogebra.html5.main.AppW;
import geogebra.html5.util.keyboard.OnScreenKeyBoard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class AutoCompleteTextFieldW extends FlowPanel implements
        AutoComplete, geogebra.common.gui.inputfield.AutoCompleteTextField,
        KeyDownHandler, KeyUpHandler, KeyPressHandler,
        ValueChangeHandler<String>, SelectionHandler<Suggestion>,
        VirtualKeyboardListener {

	private AppW app;
	private Localization loc;
	private StringBuilder curWord;
	private int curWordStart;

	protected AutoCompleteDictionary dict;
	protected boolean isCASInput = false;
	protected boolean autoComplete;
	private int historyIndex;
	private ArrayList<String> history;

	private boolean handleEscapeKey = false;

	private List<String> completions;
	private String cmdPrefix;
	private static CompletionsPopup completionsPopup;

	private HistoryPopupW historyPopup;
	protected ScrollableSuggestBox textField = null;

	private DrawTextField drawTextField = null;

	// symbol table popup fields
	private ToggleButton showSymbolButton = null;
	private SymbolTablePopupW tablePopup;
	private boolean showSymbolTableIcon = false;
	public static boolean showSymbolButtonFocused = false;

	/**
	 * Flag to determine if text must start with "=" to activate autoComplete;
	 * used with spreadsheet cells
	 */
	private boolean isEqualsRequired = false;


	/**
	 * Flag to determine if Tab key should behave like usual or disabled.
	 */
	private boolean tabEnabled = true;
	private int columns = 0;
	private boolean forCAS;
	
	/**
	 * Pattern to find an argument description as found in the syntax
	 * information of a command.
	 */
	// private static Pattern syntaxArgPattern =
	// Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.) *(?=[,\\]])");
	// Simplified to this as there are too many non-alphabetic character in
	// parameter descriptions:
	private static com.google.gwt.regexp.shared.RegExp syntaxArgPattern = com.google.gwt.regexp.shared.RegExp
	        .compile("[,\\[\\(] *(<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]\\)])");

	boolean showOnScreenKeyBoard = false;
	private OnScreenKeyBoard keyboard;
	private int actualFontSize = 14;

	/**
	 * Constructs a new AutoCompleteTextField that uses the dictionary of the
	 * given Application for autocomplete look up. A default model is created
	 * and the number of columns is 0.
	 * 
	 */
	public AutoCompleteTextFieldW(int columns, App app) {
		this(columns, (AppW) app, true, null, false);
	}

	public AutoCompleteTextFieldW(int columns, App app, Drawable drawTextField) {
		this(columns, app);
		this.drawTextField = (DrawTextField) drawTextField;
		addStyleName("FromDrawTextFieldNew");
	}

	public AutoCompleteTextFieldW(int columns, AppW app,
	        boolean handleEscapeKey,
	        KeyEventsHandler keyHandler, boolean forCAS) {
		this.forCAS = forCAS;
		// AG not MathTextField and Mytextfield exists yet super(app);
		// allow dynamic width with columns = -1
		textField = new ScrollableSuggestBox(completionsPopup = new CompletionsPopup()){
			public void setText(String s){
				String oldText = getText();
				int pos = getValueBox().getCursorPos();
				StringBuilder sb = new StringBuilder();
				int wp = AutoCompleteTextFieldW.updateCurrentWord(false, new StringBuilder(), oldText, pos);
				/*if(wp <= 0){
					wp = pos;
				}*/
				sb.append(oldText.substring(0, wp));
				sb.append(s);
				sb.append(oldText.substring(pos));
				super.setText(sb.toString());
				//AutoCompleteTextFieldW.this.moveToNextArgument(false);
			}

			@Override
			public void setFocus(boolean focused) {
			    super.setFocus(focused && !showOnScreenKeyBoard);
			}
		};
		if (columns > 0) {
			setColumns(columns);
		}

		//setVerticalAlignment(ALIGN_MIDDLE);
		addStyleName("AutoCompleteTextFieldW");

		String id = DOM.createUniqueId();
		// App.debug(id);
		// id = id.substring(7);

		textField.addStyleName("TextField");
		textField.getElement().setId(id);

		showSymbolButton = new ToggleButton() {
			public void onBrowserEvent(Event event) {
				if (event.getTypeInt() == Event.ONMOUSEDOWN) {
					
					// set it as focused anyway, because it is needed
					// before the real focus and blur events take place
					showSymbolButton.addStyleName("ShowSymbolButtonFocused");
					showSymbolButtonFocused = true;
				}
				super.onBrowserEvent(event);
			}
		};
		showSymbolButton.getElement().setId(id + "_SymbolButton");
		showSymbolButton.getElement().setAttribute("data-visible", "false");
		//showSymbolButton.getElement().setAttribute("style", "display: none");
		showSymbolButton.setText(Unicode.alpha + "");
		showSymbolButton.addStyleName("SymbolToggleButton");
		showSymbolButton.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				showSymbolButton.removeStyleName("ShowSymbolButtonFocused");
				showSymbolButtonFocused = false;
				// TODO: make it disappear when blurred
				// to a place else than the textfield?
			}
		});
		showSymbolButton.addClickHandler(new ClickHandler(){

			public void onClick(ClickEvent event) {
				if (showSymbolButton.isDown()) {
					// when it is still down, it will be changed to up
					// when it is still up, it will be changed to down
					showTablePopupRelativeTo(showSymbolButton);
					
				} else {
					hideTablePopup();
				}
	            
            }});
		
		showSymbolButton.setFocus(false);
		add(textField);
		add(showSymbolButton);

		this.app = app;
		this.loc = app.getLocalization();
		setAutoComplete(true);
		this.handleEscapeKey = handleEscapeKey;
		curWord = new StringBuilder();

		historyIndex = 0;
		history = new ArrayList<String>(50);

		completions = null;

		// CommandCompletionListCellRenderer cellRenderer = new
		// CommandCompletionListCellRenderer();
		completionsPopup.addTextField(this);

		// addKeyListener(this); now in MathTextField <==AG not mathtexfield
		// exist yet
		if (keyHandler == null) {
			textField.getValueBox().addKeyDownHandler(this);
			textField.getValueBox().addKeyUpHandler(this);
			textField.getValueBox().addKeyPressHandler(this);
		} else {
			// This is currently used for
			// MyCellEditorW.SpreadsheetCellEditorKeyListener
			textField.getValueBox().addKeyDownHandler(keyHandler);
			textField.getValueBox().addKeyPressHandler(keyHandler);
			textField.getValueBox().addKeyUpHandler(keyHandler);
		}
		textField.addValueChangeHandler(this);
		textField.addSelectionHandler(this);

		init();
	}

	/**
	 * @param app creates new AutoCompleteTextField with app.
	 */
	public AutoCompleteTextFieldW(App app) {
	  this(0,app);
    }

	private void init() {
		textField.getValueBox().addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				// AG I dont understand thisAutoCompleteTextField tf =
				// ((AutoCompleteTextField)event.getSource());
				// AG tf.setFocus(true);
//				textField.setFocus(true);
				requestFocus();
			}
		});
	}

	public DrawTextField getDrawTextField() {
		return drawTextField;
	}

	public ArrayList<String> getHistory() {
		return history;
	}

	/**
	 * Add a history popup list and an embedded popup button. See
	 * AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup) {

		if (historyPopup == null)
			historyPopup = new HistoryPopupW(this);

		historyPopup.setDownPopup(isDownPopup);

		ClickHandler al = new ClickHandler() {

			public void onClick(ClickEvent event) {
				// AGString cmd = event.;
				// AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
				// TODO: should up/down orientation be tied to InputBar?
				// show popup
				historyPopup.showPopup();

			}
		};
		setBorderButton(1, BasicIcons.createUpDownTriangleIcon(false, true), al);
		this.setBorderButtonVisible(1, false);
	}

	private void setBorderButtonVisible(int i, boolean b) {
		App.debug("setBorderVisible() implementation needed"); // TODO Auto-generated
	}

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
	        ClickHandler al) {
		App.debug("setBorderButton() implementation needed"); // TODO Auto-generated
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		App.debug("geoElementSelected() implementation needed"); // TODO Auto-generated
	}

	public void showPopupSymbolButton(boolean b) {
		this.showSymbolTableIcon = b;
		if (showSymbolButton == null) {
			return;
		}
		// temp
		// TODO: don't fix the popup button here, but it should appear if mouse
		// clicked into the textfield.
		if ((showSymbolTableIcon) && app.isAllowedSymbolTables() && this.columns > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH) {
			showSymbolButton.getElement().addClassName("shown");
			showSymbolButton.getElement().setAttribute("data-persist", "true");
		} else {
			showSymbolButton.getElement().removeClassName("shown");
			showSymbolButton.getElement().removeAttribute("data-persist");
		}
	}

	/**
	 * Sets whether the component is currently performing autocomplete lookups
	 * as keystrokes are performed.
	 * 
	 * @param val
	 *            True or false.
	 */
	public void setAutoComplete(boolean val) {
		autoComplete = val && loc.isAutoCompletePossible();

		/*if (autoComplete)
			app.initTranslatedCommands();*/

	}

	public List<String> resetCompletions() {
		String text = getText();
		updateCurrentWord(false);
		completions = null;
		if (isEqualsRequired && !text.startsWith("="))
			return null;

		boolean korean = false; // AG
		                        // app.getLocale().getLanguage().equals("ko");

		// start autocompletion only for words with at least two characters
		if (korean) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				completions = null;
				return null;
			}
		} else if (curWord.length() < 2) {
			completions = null;
			return null;
		}
		// start autocompletion only if curWord is not a defined variable
		if (app.getKernel().lookupLabel(curWord.toString()) != null) {
			completions = null;
			return null;
		}
		cmdPrefix = curWord.toString();
		
		if (korean) {
			completions = getDictionary().getCompletionsKorean(cmdPrefix);
		} else {
			completions = getDictionary().getCompletions(cmdPrefix);
		}
		
		List<String> commandCompletions = getSyntaxes(completions);

		// Start with the built-in function completions
		completions = app.getParserFunctions().getCompletions(cmdPrefix);
		// Then add the command completions
		if (completions.isEmpty()) {
			completions = commandCompletions;
		} else if (commandCompletions != null) {
			completions.addAll(commandCompletions);
		}
		return completions;
	}

	/*
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 */
	private List<String> getSyntaxes(List<String> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<String> syntaxes = new ArrayList<String>();
		for (String cmd : commands) {

			String cmdInt = app.getInternalCommand(cmd);

			String syntaxString;
			if (isCASInput) {
				syntaxString = loc.getCommandSyntaxCAS(cmdInt);
			} else {
				syntaxString = loc.getCommandSyntax(cmdInt);
			}
			if (syntaxString.endsWith(isCASInput ? Localization.syntaxCAS
			        : Localization.syntaxStr)) {

				// command not found, check for macros
				Macro macro = isCASInput ? null : app.getKernel().getMacro(cmd);
				if (macro != null) {
					syntaxes.add(macro.toString());
				} else {
					// syntaxes.add(cmdInt + "[]");
					App.debug("Can't find syntax for: " + cmd);
				}

				continue;
			}
			for (String syntax : syntaxString.split("\\n")) {
				syntaxes.add(syntax);
			}
		}
		return syntaxes;
	}

	public void cancelAutoCompletion() {
		completions = null;
	}

	public void enableColoring(boolean b) {
		App.debug("enableColoring() implementation needed"); // TODO Auto-generated

	}

	public void setOpaque(boolean b) {
		App.debug("setOpaque() implementation needed"); // TODO Auto-generated

	}

	public void setFont(GFont font) {
		actualFontSize = font.getSize();
		textField.getElement().getStyle().setFontSize(font.getSize(), Unit.PX);

		if (showSymbolButton != null) {
			showSymbolButton.getElement().getStyle().setFontSize(font.getSize(), Unit.PX);
			showSymbolButton.getElement().getStyle().setLineHeight(font.getSize(), Unit.PX);
		}

		setColumns(this.columns);
	}

	public void setForeground(GColor color) {
		drawTextField.getLabel().setForeground(color);
		textField.getElement().getStyle().setColor(GColor.getColorString(color));
	}

	public void setBackground(GColor color) {
		textField.getElement().getStyle().setBackgroundColor(GColor.getColorString(color));
	}

	public void setFocusable(boolean b) {
		App.debug("setFocusable() implementation needed"); // TODO Auto-generated

	}

	public void setFocus(boolean b) {
		textField.setFocus(b && !showOnScreenKeyBoard);
	}

	public void setEditable(boolean b) {
		textField.setEnabled(b);
	}

	public void setLabel(GLabel label) {
		App.debug("setLabel() implementation needed"); // TODO Auto-generated

	}

	public void setColumns(int columns) {
		this.columns  = columns;
		if (showSymbolButton != null && 
				(this.columns > EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH || this.columns == -1)) {
			prepareShowSymbolButton(true);
		}

		int columnWidth = 11;
		switch (actualFontSize) {
		case 7: columnWidth = 6; break;
		case 9: columnWidth = 8; break;
		case 14: columnWidth = 11; break;
		case 18: columnWidth = 15; break;//18:15:educated guess
		case 19: columnWidth = 16; break;
		case 28: columnWidth = 24; break;
		case 56: columnWidth = 47; break;
		case 112: columnWidth = 95; break;
		//more precise for FitLine+FitLineX, but unreal
		//default: columnWidth = (int) Math.floor(0.83265 * actualFontSize + 0.4615); break;
		//default: columnWidth = (int) Math.round(0.832 * actualFontSize); break;
		// 20 length * 18 fontSize * 0.002 difference gives just less than 1 pixel anyway
		default: columnWidth = (int) Math.round(0.83 * actualFontSize); break;
		}

		// this is a way to emulate how Java does it in Desktop version,
		// but columnWidth is not always exact (+-1)
		getTextBox().setWidth((columns * columnWidth + 5) + "px");
		// the number 5 comes from experimental testing for small textfields (e.g. columns=1)
		// of course, this is not the most perfect, but at least works...
		// due to Greek letters popup, length should be lessened somewhere else

		// as the following solution was wrong, since em means vertical height:
		//getTextBox().setWidth(columns + "em");
	}

	public String getCurrentWord() {
		return curWord.toString();
	}

	public List<String> getCompletions() {
		return completions;
	}

	public int getCurrentWordStart() {
		return curWordStart;
	}

	public void addFocusListener(FocusListener listener) {
		textField.getValueBox().addFocusHandler(
		        (geogebra.html5.event.FocusListenerW) listener);
		textField.getValueBox().addBlurHandler(
		        (geogebra.html5.event.FocusListenerW) listener);
	}

	public void wrapSetText(String s) {
		App.debug("wrapSetText() implementation needed"); // TODO Auto-generated

	}

	public int getCaretPosition() {
		return textField.getValueBox().getCursorPos();
	}

	public void setCaretPosition(int caretPos) {
		textField.getValueBox().setCursorPos(caretPos);
	}

	public void setDictionary(boolean forCAS) {
		this.forCAS = forCAS;
		this.dict = null;
	}

	public AutoCompleteDictionary getDictionary() {
		if (this.dict == null) {
			this.dict = this.forCAS ? app.getCommandDictionaryCAS() : app
					.getCommandDictionary();
		}
		return dict;
	}

	// returns the word at position pos in text
	public static String getWordAtPos(String text, int pos) {
		// search to the left
		int wordStart = pos - 1;
		while (wordStart >= 0
		        && StringUtil.isLetterOrDigitOrUnderscore(text
		                .charAt(wordStart)))
			--wordStart;
		wordStart++;

		// search to the right
		int wordEnd = pos;
		int length = text.length();
		while (wordEnd < length
		        && StringUtil.isLetterOrDigitOrUnderscore(text.charAt(wordEnd)))
			++wordEnd;

		if (wordStart >= 0 && wordEnd <= length) {
			return text.substring(wordStart, wordEnd);
		}
		return null;
	}

	/**
	 * shows dialog with syntax info
	 * 
	 * @param cmd
	 *            is the internal command name
	 */
	private void showCommandHelp(String cmd) {
		// show help for current command (current word)
		String help = loc.getCommandSyntax(cmd);

		// show help if available
		if (help != null) {
			app.showError(new MyError(loc, loc.getPlain("Syntax") + ":\n"
			        + help, cmd));
		} else if (app.getGuiManager() != null) {
			app.getGuiManager().openCommandHelp(null);
		}
	}

	private void clearSelection() {
		int start = textField.getText().indexOf(
		        textField.getValueBox().getSelectedText());
		int end = start + textField.getValueBox().getSelectionLength();
		// clear selection if there is one
		if (start != end) {
			int pos = getCaretPosition();
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			if (pos < sb.length())
				setCaretPosition(pos);
		}
	}

	/**
	 * Updates curWord to word at current caret position. curWordStart,
	 * curWordEnd are set to this word's start and end position
	 */
	public void updateCurrentWord(boolean searchRight) {
		int next = updateCurrentWord(searchRight, this.curWord, getText(), getCaretPosition());
		if(next > -1){
			this.curWordStart = next;
		}
	}

	static int updateCurrentWord(boolean searchRight, StringBuilder curWord,
            String text, int caretPos) {
		int curWordStart;
		if (text == null)
			return -1;

		if (searchRight) {
			// search to right first to see if we are inside [ ]
			boolean insideBrackets = false;
			curWordStart = caretPos;

			while (curWordStart < text.length()) {
				char c = text.charAt(curWordStart);
				if (c == '[')
					break;
				if (c == ']')
					insideBrackets = true;
				curWordStart++;
			}

			// found [, so go back until we get a ]
			if (insideBrackets) {
				while (caretPos > 0 && text.charAt(caretPos) != '[')
					caretPos--;
			}
		}

		// search to the left
		curWordStart = caretPos - 1;
		while (curWordStart >= 0 &&
		// isLetterOrDigitOrOpenBracket so that F1 works
		        StringUtil.isLetterOrDigitOrUnderscore(text
		                .charAt(curWordStart))) {
			--curWordStart;
		}
		curWordStart++;
		// search to the right
		int curWordEnd = caretPos;
		int length = text.length();
		while (curWordEnd < length
		        && StringUtil.isLetterOrDigitOrUnderscore(text
		                .charAt(curWordEnd)))
			++curWordEnd;

		curWord.setLength(0);
		curWord.append(text.substring(curWordStart, curWordEnd));

		// remove '[' at end
		if (curWord.toString().endsWith("[")) {
			curWord.setLength(curWord.length() - 1);
		}
		return curWordStart;
    }

	/*
	 * just show syntax error (already correctly formulated by
	 * CommandProcessor.argErr())
	 */
	public void showError(MyError e) {
		app.showError(e);
	}

	public boolean getAutoComplete() {
		return autoComplete && loc.isAutoCompletePossible();
	}

	/**
	 * @return previous input from input textfield's history
	 */
	private String getPreviousInput() {
		if (history.size() == 0)
			return null;
		if (historyIndex > 0)
			--historyIndex;
		return history.get(historyIndex);
	}

	/**
	 * @return next input from input textfield's history
	 */
	private String getNextInput() {
		if (historyIndex < history.size())
			++historyIndex;
		if (historyIndex == history.size())
			return null;
		else
			return history.get(historyIndex);
	}

	public void mergeKoreanDoubles() {
		// avoid shift on Korean keyboards
		/*
		 * AG dont do that yet if (app.getLocale().getLanguage().equals("ko")) {
		 * String text = getText(); int caretPos = getCaretPosition(); String
		 * mergeText = Korean.mergeDoubleCharacters(text); int decrease =
		 * text.length() - mergeText.length(); if (decrease > 0) {
		 * setText(mergeText); setCaretPosition(caretPos - decrease); } }
		 */
		App.debug("KoreanDoubles may be needed in AutocompleteTextField");
	}

	private boolean moveToNextArgument(boolean find) {
		String text = getText();
		int caretPos = getCaretPosition();

		// make sure it works if caret is just after [
		if(caretPos > 0 && text.length() < caretPos && text.charAt(caretPos) != '[') caretPos--;
		String suffix = text.substring(caretPos);
		int index = -1;
		// AGMatcher argMatcher = syntaxArgPattern.matcher(text);
		MatchResult argMatcher = syntaxArgPattern.exec(suffix);
		// boolean hasNextArgument = argMatcher.find(caretPos);
		boolean hasNextArgument = syntaxArgPattern.test(suffix);
		if(hasNextArgument){
			index = argMatcher.getIndex() + caretPos;
		}

		if (find && !hasNextArgument) {
			// hasNextArgument = argMatcher.find();
			hasNextArgument = syntaxArgPattern.test(text);
			argMatcher = syntaxArgPattern.exec(text);
			if(hasNextArgument){
				index = argMatcher.getIndex();
			}
		}
		// if (hasNextArgument && (find || argMatcher.start() == caretPos)) {
		if (hasNextArgument && argMatcher.getGroup(1) != null &&(find || index == caretPos)) {
			// setCaretPosition(argMatcher.end();
			// moveCaretPosition(argMatcher.start() + 1);
				String groupStr = argMatcher.getGroup(1);
				textField.getValueBox().setSelectionRange(
				        index + 2, groupStr.length());
			
			return true;
		}		
		return false;
	}

	// ----------------------------------------------------------------------------
	// Protected methods ..why? :-)
	// ----------------------------------------------------------------------------

	boolean ctrlC = false;

	public void onKeyPress(KeyPressEvent e) {

		// only handle parentheses
		char ch = e.getCharCode();

		int caretPos = getCaretPosition();

		String text = getText();

		// checking for isAltDown() because Alt+, prints another character on
		// the PC
		// TODO make this more robust - perhaps it could go in a document change
		// listener
		if (ch == ',' && !e.isAltKeyDown()) {
			if (caretPos < text.length() && text.charAt(caretPos) == ',') {
				// User typed ',' just in ahead of an existing ',':
				// We may be in the position of filling in the next argument of
				// an autocompleted command
				// Look for a pattern of the form ", < Argument Description > ,"
				// or ", < Argument Description > ]"
				// If found, select the argument description so that it can
				// easily be typed over with the value
				// of the argument.
				if (moveToNextArgument(false)) {
					e.stopPropagation();
					e.preventDefault();
				}
				return;
			}
		}

		if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
			// super.keyTyped(e);
			App.debug("super.keyTyped needed in AutocompleteTextField");
			return;
		}

		clearSelection();
		caretPos = getCaretPosition();

		if (ch == '}' || ch == ')' || ch == ']') {

			// simple check if brackets match
			if (text.length() > caretPos && text.charAt(caretPos) == ch) {
				int count = 0;
				for (int i = 0; i < text.length(); i++) {
					char c = text.charAt(i);
					if (c == '{')
						count++;
					else if (c == '}')
						count--;
					else if (c == '(')
						count += 1E3;
					else if (c == ')')
						count -= 1E3;
					else if (c == '[')
						count += 1E6;
					else if (c == ']')
						count -= 1E6;
				}

				if (count == 0) {
					// if brackets match, just move the cursor forwards one
					e.preventDefault();
					caretPos++;
				}
			}

		}

		// auto-close parentheses
		if (caretPos == text.length()
		        || MyTextField
		                .isCloseBracketOrWhitespace(text.charAt(caretPos))) {
			switch (ch) {
			case '(':
				// opening parentheses: insert closing parenthesis automatically
				insertString(")");
				break;

			case '{':
				// opening braces: insert closing parenthesis automatically
				insertString("}");
				break;

			case '[':
				// opening bracket: insert closing parenthesis automatically
				insertString("]");
				break;
			}
		}

		// make sure we keep the previous caret position
		setCaretPosition(Math.min(text.length(), caretPos));
	}

	public void onKeyDown(KeyDownEvent e) {
		if (!isTabEnabled()) {
			return;
		}
		int keyCode = e.getNativeKeyCode();
		if (keyCode == GWTKeycodes.KEY_TAB) {
			e.preventDefault();
		}
	}

	public void onKeyUp(KeyUpEvent e) {
		int keyCode = e.getNativeKeyCode();

		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltKeyDown() && e.isControlKeyDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		/*
		 * AG if (Application.MAC_OS && e.isControlKeyDown()) { e.consume(); }
		 */

		ctrlC = false;

		switch (keyCode) {

		case GWTKeycodes.KEY_Z:
		case GWTKeycodes.KEY_Y:
			if (e.isControlKeyDown()) {
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
				e.stopPropagation();
			}
			break;
		case GWTKeycodes.KEY_C:
			if (e.isControlKeyDown()) // workaround for MAC_OS
			{
				ctrlC = true;
			}
			break;

		// process input

		case GWTKeycodes.KEY_ESCAPE:
			if (!handleEscapeKey) {
				break;
			}

			/*
			 * AG do this if we will have windows Component comp =
			 * SwingUtilities.getRoot(this); if (comp instanceof JDialog) {
			 * ((JDialog) comp).setVisible(false); return; }
			 */
			SuggestBox.DefaultSuggestionDisplay display = (SuggestBox.DefaultSuggestionDisplay) textField
			        .getSuggestionDisplay();
			display.hideSuggestions();
			break;

		case GWTKeycodes.KEY_UP:
			if (!isSuggesting()) {
				if (!handleEscapeKey) {
					break;
				}
				if (historyPopup == null) {
					String text = getPreviousInput();
					if (text != null)
						setText(text);
				} else if (!historyPopup.isDownPopup()) {
					historyPopup.showPopup();
				}
			}
			e.stopPropagation(); 
			break;

		case GWTKeycodes.KEY_DOWN:
			if (!handleEscapeKey) {
				break;
			}
			if (historyPopup != null && historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			} else {
				// Fix for Ticket #463
				if (getNextInput() != null) {
					setText(getNextInput());
				}
			}
			e.stopPropagation(); // prevent GlobalKeyDispatcherW to move the euclidian view
			break;

		case GWTKeycodes.KEY_F9:
			// needed for applets
			if (app.isApplet())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);
			break;
		case GWTKeycodes.KEY_LEFT:
			textField.hideSuggestions();
			e.stopPropagation();
			break;
		case GWTKeycodes.KEY_RIGHT:
			if (moveToNextArgument(false)) {
				e.stopPropagation();
				textField.hideSuggestions();
			}
			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_TAB:
			e.preventDefault();
			if (moveToNextArgument(true)) {
				e.stopPropagation();
			}
			break;

		case GWTKeycodes.KEY_F1:

			if (autoComplete) {
				if (getText().equals("")) {

					Object[] options = { app.getPlain("OK"),
					        app.getPlain("ShowOnlineHelp") };
					/*
					 * AG not yet... int n =
					 * JOptionPane.showOptionDialog(app.getMainComponent(),
					 * app.getPlain("InputFieldHelp"),
					 * GeoGebraConstants.APPLICATION_NAME) + " - " +
					 * app.getMenu("Help"), JOptionPane.YES_NO_OPTION,
					 * JOptionPane.QUESTION_MESSAGE, null, // do not use a
					 * custom Icon options, // the titles of buttons
					 * options[0]); // default button title
					 * 
					 * if (n == 1)
					 * app.getGuiManager().openHelp(AbstractApplication
					 * .WIKI_MANUAL);
					 */
				} else {
					int pos = getCaretPosition();
					while (pos > 0 && getText().charAt(pos - 1) == '[') {
						pos--;
					}
					String word = getWordAtPos(getText(), pos);
					String lowerCurWord = word.toLowerCase();
					String closest = getDictionary().lookup(lowerCurWord);

					if (closest != null)// &&
					                    // lowerCurWord.equals(closest.toLowerCase()))
						showCommandHelp(app.getInternalCommand(closest));
					else if (app.getGuiManager() != null) {
						app.getGuiManager().openHelp(App.WIKI_MANUAL);
					}

				}
			} else if (app.getGuiManager() != null) {
				app.getGuiManager().openHelp(App.WIKI_MANUAL);
			}

			e.stopPropagation();
			break;

		case GWTKeycodes.KEY_0:
		case GWTKeycodes.KEY_1:
		case GWTKeycodes.KEY_2:
		case GWTKeycodes.KEY_3:
		case GWTKeycodes.KEY_4:
		case GWTKeycodes.KEY_5:
		case GWTKeycodes.KEY_6:
		case GWTKeycodes.KEY_7:
		case GWTKeycodes.KEY_8:
		case GWTKeycodes.KEY_9:
			if (e.isControlKeyDown() && e.isShiftKeyDown())
				app.getGlobalKeyDispatcher().handleGeneralKeys(e);

			// fall through eg Alt-2 for squared

		default:

			// check for eg alt-a for alpha
			// check for eg alt-shift-a for upper case alpha
			if (e.isAltKeyDown()) {

				char c = (char) keyCode;

				String s;

				if (e.isShiftKeyDown()) {
					s = AltKeys.LookupUpper.get(c);
				} else {
					s = AltKeys.LookupLower.get(c);
				}

				if (s != null) {
					insertString(s);
					break;
				}
			}
			/*
			 * Try handling here that is originaly in keyup
			 */
			boolean modifierKeyPressed = e.isControlKeyDown()
			        || e.isAltKeyDown();

			// we don't want to act when AltGr is down
			// as it is used eg for entering {[}] is some locales
			// NB e.isAltGraphDown() doesn't work
			if (e.isAltKeyDown() && e.isControlKeyDown())
				modifierKeyPressed = false;

			char charPressed = Character.valueOf((char) e.getNativeKeyCode());

			if ((StringUtil.isLetterOrDigitOrUnderscore(charPressed) || modifierKeyPressed)
			        && !(ctrlC) && !(e.getNativeKeyCode() == GWTKeycodes.KEY_A)) {
				clearSelection();
			}

			// handle alt-p etc
			// super.keyReleased(e);

			mergeKoreanDoubles();

			if (getAutoComplete()) {
				updateCurrentWord(false);
			}

		}
	}

	public void addToHistory(String str) {
		// exit if the new string is the same as the last entered string
		if (!history.isEmpty() && str.equals(history.get(history.size() - 1)))
			return;

		history.add(str);
		historyIndex = history.size();
	}

	public boolean isSuggesting() {
		SuggestBox.DefaultSuggestionDisplay display = (SuggestBox.DefaultSuggestionDisplay) textField
		        .getSuggestionDisplay();
		return display.isSuggestionListShowing();
	}

	private boolean isSuggestionJustHappened = false;
	private boolean isSuggestionClickJustHappened = false;
	private GeoTextField geoUsedForInputBox;

	/**
	 * @return that suggestion is just happened (click or enter, so we don't
	 *         need to run the enter code again
	 */
	public boolean isSuggestionJustHappened() {
		return isSuggestionJustHappened && !isSuggestionClickJustHappened;
	}

	public void setIsSuggestionJustHappened(boolean b) {
		isSuggestionJustHappened = b;
		isSuggestionClickJustHappened = b;
	}

	/* Hopefully happens only on click */
	public void onValueChange(ValueChangeEvent<String> event) {
		isSuggestionClickJustHappened = true;

		// textField.getValueBox().getElement().focus();
	}

	public void onSelection(SelectionEvent<Suggestion> event) {
		isSuggestionJustHappened = true;
		int index = completions.indexOf(event.getSelectedItem()
		        .getReplacementString());
		validateAutoCompletion(index, getCompletions());
	}

	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		setText(start, end, text);
	}

	public void onBackSpace(){
		int start = getSelectionStart();
		int end = getSelectionEnd();
		
		if(end - start < 1){
			end = getCaretPosition();
			start = end - 1;
		}

		if(start >= 0){
			setText(start, end, "");
		}
	}

	private void setText(int start, int end, String text){
		// clear selection if there is one
		if (start != end) {
			String oldText = getText();
			StringBuilder sb = new StringBuilder();
			sb.append(oldText.substring(0, start));
			sb.append(oldText.substring(end));
			setText(sb.toString());
			setCaretPosition(start);
		}

		int pos = getCaretPosition();
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		sb.append(oldText.substring(0, pos));
		sb.append(text);
		sb.append(oldText.substring(pos));
		setText(sb.toString());

		// setCaretPosition(pos + text.length());
		final int newPos = pos + text.length();

		// make sure AutoComplete works
		if (this instanceof AutoCompleteTextFieldW) {
			AutoCompleteTextFieldW tf = this;
			tf.updateCurrentWord(false);
		}

		setCaretPosition(newPos);

		// TODO: tried to keep the Mac OS from auto-selecting the field by
		// resetting the
		// caret, but not working yet
		// setCaret(new DefaultCaret());
		// setCaretPosition(newPos);
	}

	private int getSelectionEnd() {
		return getSelectionStart()
		        + textField.getValueBox().getSelectionLength();
	}

	private int getSelectionStart() {
		return getText().indexOf(textField.getValueBox().getSelectedText());
	}

	public void removeSymbolTable(){
		if (showSymbolButton == null) {
			return;
		}
		this.showSymbolButton.removeFromParent();
		this.showSymbolButton = null;
	}

	void showTablePopupRelativeTo(Widget w) {
		if (tablePopup == null && this.showSymbolButton != null)
			tablePopup = new SymbolTablePopupW(app, this, showSymbolButton);
		if(this.tablePopup != null){
			tablePopup.showRelativeTo(w);
		}
	}

	public void hideTablePopup() {
		if (this.tablePopup != null){
			this.tablePopup.hide();
		}
	}

	public String getText() {
		return textField.getText();
	}

	public void setText(String s) {
		textField.getValueBox().setText(s);
	}

	public FocusWidget getTextBox() {
		return textField.getValueBox();
	}

	public void toggleSymbolButton(boolean toggled) {
		if (showSymbolButton == null) {
			return;
		}
		showSymbolButton.setDown(toggled);
	}

	public SuggestBox getTextField() {
		return textField;
	}

	/**
	 * Ticket #1167 Auto-completes input; <br>
	 * 
	 * @param index
	 *            index of the chosen command in the completions list
	 * @param completions
	 * @return false if completions list is null or index < 0 or index >
	 *         completions.size()
	 * @author Arnaud
	 */
	public boolean validateAutoCompletion(int index, List<String> completions) {
		if (completions == null || index < 0 || index >= completions.size()) {
			return false;
		}
		String command = completions.get(index);
		// String text = getText();
		// StringBuilder sb = new StringBuilder();
		// sb.append(text.substring(0, curWordStart));
		// sb.append(command);
		// sb.append(text.substring(curWordStart + curWord.length()));
		// setText(sb.toString());
		int bracketIndex = command.indexOf('[');// + 1;
		// Special case if the completion is a built-in function
		if (bracketIndex == -1) {
			bracketIndex = command.indexOf('(');
			bracketIndex++;
			/*setCaretPosition(curWordStart + bracketIndex + 1);
			return true;*/
		}
		setCaretPosition(curWordStart + bracketIndex);
		moveToNextArgument(false);
		return true;
	}

	/**
	 * This method inspects its first parameter - an Object, if there is a
	 * symbol button associated with that. If there is one, this method sets the
	 * visibility of the symbol button according to the second parameter.
	 * 
	 * @param source
	 *            the scanned object
	 * @param show
	 *            true, if the source's symbol button must be visible, false
	 *            otherwise.
	 */
	public static void showSymbolButtonIfExists(Object source, boolean show) {
		if (source instanceof Widget) {
			String id = ((Widget) source).getElement().getId();
			if (id != null) {
				Element element = Document.get().getElementById(id + "_SymbolButton");
				if (element != null && "true".equals(element.getAttribute("data-visible"))) {
					if (show) {
						element.addClassName("shown");
					} else {
						if (!"true".equals(element.getAttribute("data-persist"))) {
							element.removeClassName("shown");
						}
					}
				}
			}
		}
	}

	public void setUsedForInputBox(GeoTextField geo) {
		geoUsedForInputBox = geo;
	}

	public boolean usedForInputBox() {
		return geoUsedForInputBox != null;
	}

	public void requestFocus() {
		if(showOnScreenKeyBoard) {
			keyboard = OnScreenKeyBoard.getInstance(this);
			keyboard.show();
		}
		// if the onScreenKeyBoard is shown setFocus will blur textField
		textField.setFocus(true);
		if (geoUsedForInputBox != null && !geoUsedForInputBox.isSelected()) {
			app.getSelectionManager().clearSelectedGeos(false);
			app.getSelectionManager().addSelectedGeo(geoUsedForInputBox);
		}
	}

	public void setFocusTraversalKeysEnabled(boolean b) {
		// Dummy method
	}

	public boolean hasFocus() {
		App.debug("Unimplemented");
		return false;
	}

	/** returns if text must start with "=" to activate autocomplete */
	public boolean isEqualsRequired() {
		return isEqualsRequired;
	}

	/** sets flag to require text starts with "=" to activate autocomplete */
	public void setEqualsRequired(boolean isEqualsRequired) {
		this.isEqualsRequired = isEqualsRequired;
	}

	public void setCASInput(boolean b) {
		this.isCASInput = b;
	}

	public void addKeyHandler(KeyHandler handler) {
		textField.getValueBox().addKeyPressHandler(new KeyListenerW(handler));

	}
	
	/**
	 * @param hanlder Handler to key up events
	 * @return the handler
	 */
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textField.getValueBox().addKeyUpHandler(handler);
	}

	public void setWidth(int width) {
		if (width > 0) {
			textField.setWidth(width + "px");
			super.setWidth(width + "px");
		}
	}

	public String getCommand() {
		this.updateCurrentWord(true);
		return this.getCurrentWord();
	}

	/**
	 * @param hanlder
	 * Adds a focus handler to the wrapped textfield.
	 */
	public HandlerRegistration addFocusHandler(
            FocusHandler hanlder) {
	    return textField.getValueBox().addFocusHandler(hanlder);
	    
    }

	/**
	 * selects all text
	 */
	public void selectAll() {
		textField.getValueBox().selectAll();
	}

	/**
	 * @param handler Keypresshandler
	 * 
	 * Added to tetxtfield as handler
	 */
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
	    return textField.getValueBox().addKeyPressHandler(handler);
	    
    }

	/**
	 * @param handler Blurhandler attached to texbox
	 */
	public void addBlurHandler(
            BlurHandler handler) {
	    getTextBox().addBlurHandler(handler);

    }

	public boolean isTabEnabled() {
	    return tabEnabled;
    }

	public void setTabEnabled(boolean tabEnabled) {
	    this.tabEnabled = tabEnabled;
    }
	
	/**
	 * use this, if no explicit length set, but symbolbutton must be shown.
	 */
	public void requestToShowSymbolButton() {
		if (showSymbolButton == null) {
			return;
		}
		this.columns = EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH + 1;
		prepareShowSymbolButton(true);
	}

	public void prepareShowSymbolButton(boolean b) {
		if (showSymbolButton == null) {
			return;
		}
		if(b){
			showSymbolButton.getElement().setAttribute("data-visible", "true");
			addStyleName("SymbolCanBeShown");
		}else{
			showSymbolButton.getElement().setAttribute("data-visible", "false");
			removeStyleName("SymbolCanBeShown");
		}
    }

	public boolean isShowOnScreenKeyBoard() {
		return showOnScreenKeyBoard;
	}

	public void setShowOnScreenKeyBoard(boolean showOnScreenKeyBoard) {
		this.showOnScreenKeyBoard = showOnScreenKeyBoard;
	}
	
	public boolean showOnscreenKeyboard() {
		return false;
	}
}
