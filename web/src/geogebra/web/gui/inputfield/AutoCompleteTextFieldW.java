package geogebra.web.gui.inputfield;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.gui.VirtualKeyboardListener;
import geogebra.common.gui.inputfield.AltKeys;
import geogebra.common.gui.inputfield.AutoComplete;
import geogebra.common.gui.inputfield.MyTextField;
import geogebra.common.gui.inputfield.ValidateAutocompletionResult;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.commands.MyException;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.main.GWTKeycodes;
import geogebra.common.util.AutoCompleteDictionary;
import geogebra.common.util.Korean;
import geogebra.common.util.Unicode;
import geogebra.web.gui.autocompletion.CompletionsPopup;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;

public class AutoCompleteTextFieldW extends HorizontalPanel implements AutoComplete, geogebra.common.gui.inputfield.AutoCompleteTextField, KeyDownHandler, KeyUpHandler, KeyPressHandler, ValueChangeHandler<String>, SelectionHandler<Suggestion>, VirtualKeyboardListener {
	
	  private AppW app;
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
	  SuggestBox textField = null;

	  private DrawTextField drawTextField = null;
	  
	// symbol table popup fields
	private ToggleButton showSymbolButton = null;
	private SymbolTablePopupW tablePopup;
	private boolean showSymbolTableIcon = false;
	  
	  /**
	   * Flag to determine if text must start with "=" to activate autoComplete;
	   * used with spreadsheet cells
	   */
	  private boolean isEqualsRequired = false;
	  
	  /**
	   * Pattern to find an argument description as found in the syntax information
	   * of a command.
	   */
	  // private static Pattern syntaxArgPattern = Pattern.compile("[,\\[] *(?:<[\\(\\) \\-\\p{L}]*>|\\.\\.\\.) *(?=[,\\]])");
	  // Simplified to this as there are too many non-alphabetic character in parameter descriptions:
	  private static com.google.gwt.regexp.shared.RegExp syntaxArgPattern = com.google.gwt.regexp.shared.RegExp
	      .compile("[,\\[] *(?:<.*?>|\"<.*?>\"|\\.\\.\\.) *(?=[,\\]])");

	  /**
	   * Constructs a new AutoCompleteTextField that uses the dictionary of the
	   * given Application for autocomplete look up.
	   * A default model is created and the number of columns is 0.
	   * 
	   */
	  public AutoCompleteTextFieldW(int columns, App app) {
	    this(columns, (AppW) app, true);
	  }
	  
	  public AutoCompleteTextFieldW(int columns, AppW app,
		      boolean handleEscapeKey) {
		    this(columns, app, handleEscapeKey, app.getCommandDictionary());
		    // setDictionary(app.getAllCommandsDictionary());
	  }
	  
	  public AutoCompleteTextFieldW(int columns, App app,
		      Drawable drawTextField) {
		    this(columns, app);
		    this.drawTextField = (DrawTextField) drawTextField;
	  }

	  public AutoCompleteTextFieldW(int columns, AppW app,
		      boolean handleEscapeKey, AutoCompleteDictionary dict) {
		    //AG not MathTextField and Mytextfield exists yet super(app);
		    // allow dynamic width with columns = -1
		  textField = new SuggestBox(completionsPopup = new CompletionsPopup(),new TextBox(), new SuggestBox.DefaultSuggestionDisplay());
		    if (columns > 0) {
		      setColumns(columns);
		    }
		    setVerticalAlignment(ALIGN_MIDDLE);
		    
		    
		    textField.addStyleName("TextField");
		    
		    showSymbolButton = new ToggleButton();
		    showSymbolButton.setText(Unicode.alpha);
		    showSymbolButton.addStyleName("SymbolToggleButton");
		    showSymbolButton.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					if (showSymbolButton.isDown()) {
						getTablePopup().showRelativeTo(showSymbolButton);
					} else {
						getTablePopup().hide();
					}
				}
			});
		    
		    add(textField);
		    add(showSymbolButton);

		    this.app = app;
		    setAutoComplete(true);
		    this.handleEscapeKey = handleEscapeKey;
		    curWord = new StringBuilder();

		    historyIndex = 0;
		    history = new ArrayList<String>(50);

		    completions = null;

		    //CommandCompletionListCellRenderer cellRenderer = new CommandCompletionListCellRenderer();
		    completionsPopup.addTextField(this);
		    
		    // addKeyListener(this); now in MathTextField <==AG not mathtexfield exist yet
		    textField.getTextBox().addKeyDownHandler(this);
		    textField.getTextBox().addKeyUpHandler(this);
		    textField.getTextBox().addKeyPressHandler(this);
		    textField.addValueChangeHandler(this);
		    textField.addSelectionHandler(this);
		    
		   
		    init();
	}
	
	private void init(){
		textField.getTextBox().addMouseUpHandler(new MouseUpHandler(){
			public void onMouseUp(MouseUpEvent event) {
				//AG I dont understand thisAutoCompleteTextField tf = ((AutoCompleteTextField)event.getSource()); 
	            //AG tf.setFocus(true);
				textField.setFocus(true);
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
	   * Add a history popup list and an embedded popup button.
	   * See AlgebraInputBar
	   */
	  public void addHistoryPopup(boolean isDownPopup) {

	    if (historyPopup == null)
	      historyPopup = new HistoryPopupW(this);

	    historyPopup.setDownPopup(isDownPopup);

	    ClickHandler al = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				//AGString cmd = event.;
		        //AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
		        // TODO: should up/down orientation be tied to InputBar?
		        // show popup
		        historyPopup.showPopup();
				
			}
		};
	    setBorderButton(1, GeoGebraIcon.createUpDownTriangleIcon(false, true), al);
	    this.setBorderButtonVisible(1, false);
	  }
	
	private void setBorderButtonVisible(int i, boolean b) {
		   App.debug("implementation needed"); //TODO Auto-generated
    }

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
            ClickHandler al) {
		   App.debug("implementation needed"); //TODO Auto-generated
    }

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
	    App.debug("implementation needed"); //TODO Auto-generated
    }


	public void showPopupSymbolButton(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }
	
	/**
	   * Sets whether the component is currently performing autocomplete lookups as
	   * keystrokes are performed.
	   * 
	   * @param val
	   *          True or false.
	   */
	  public void setAutoComplete(boolean val) {
	    autoComplete = val && app.isAutoCompletePossible();

	    if (autoComplete)
	      app.initTranslatedCommands();

	  }
	  
	  private List<String> resetCompletions() {
		    String text = getText();
		    updateCurrentWord(false);
		    completions = null;
		    if (isEqualsRequired && !text.startsWith("="))
		      return null;

		    boolean korean = false; //AG app.getLocale().getLanguage().equals("ko");

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
		    cmdPrefix = curWord.toString();

		    if (korean)
		      completions = dict.getCompletionsKorean(cmdPrefix);
		    else
		      completions = dict.getCompletions(cmdPrefix);

		    completions = getSyntaxes(completions);
		    return completions;
		  }

		  /*
		   * Take a list of commands and return all possible syntaxes
		   * for these commands
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
		        syntaxString = app.getCommandSyntaxCAS(cmdInt);
		      } else {
		        syntaxString = app.getCommandSyntax(cmdInt);
		      }
		      if (syntaxString.endsWith(isCASInput ? AppW.syntaxCAS
		          : AppW.syntaxStr)) {

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
	  
	  public void startAutoCompletion() {
		    resetCompletions();
		    completionsPopup.showCompletions();
		  }

	public void cancelAutoCompletion() {
		    completions = null;
	}

	public void enableColoring(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setOpaque(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFont(GFont font) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setForeground(GColor color) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setBackground(GColor color) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setFocusable(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setEditable(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void requestFocus() {
		textField.setFocus(true);
    }

	public void setLabel(GLabel label) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setVisible(boolean b) {
	    App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public void setColumns(int length) {
	  //AG getTextBox().setWidth(length+"px");
		App.debug("AutoCompleteTextField.setColumns called");
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
		textField.getTextBox().addFocusHandler((geogebra.web.euclidian.event.FocusListener) listener);
		textField.getTextBox().addBlurHandler((geogebra.web.euclidian.event.FocusListener) listener);	    
    }

	public void addKeyListener(geogebra.common.euclidian.event.KeyListener listener) {
		textField.getTextBox().addKeyPressHandler((geogebra.web.euclidian.event.KeyListener) listener);
	}
	
	public void wrapSetText(String s) {
		App.debug("implementation needed"); //TODO Auto-generated
	    
    }

	public int getCaretPosition() {
		return textField.getTextBox().getCursorPos();
    }

	public void setCaretPosition(int caretPos) {
		textField.getTextBox().setCursorPos(caretPos);
    }

	public void setDictionary(AutoCompleteDictionary dict) {
	    this.dict = dict;
    }

	public AutoCompleteDictionary getDictionary() {
	    return dict;
    }
	
	// returns the word at position pos in text
	  public static String getWordAtPos(String text, int pos) {
	    // search to the left
	    int wordStart = pos - 1;
	    while (wordStart >= 0 && isLetterOrDigit(text.charAt(wordStart)))
	      --wordStart;
	    wordStart++;

	    // search to the right
	    int wordEnd = pos;
	    int length = text.length();
	    while (wordEnd < length && isLetterOrDigit(text.charAt(wordEnd)))
	      ++wordEnd;

	    if (wordStart >= 0 && wordEnd <= length) {
	      return text.substring(wordStart, wordEnd);
	    } else {
	      return null;
	    }
	  }
	
	 private static boolean isLetterOrDigit(char character) {
		    switch (character) {
		      case '_': // allow underscore as a valid letter in an autocompletion word
		        return true;

		      default:
		        return Character.isLetterOrDigit(character);
		    }
		  }
	 
	 /**
	   * shows dialog with syntax info
	   * 
	   * @param cmd
	   *          is the internal command name
	   */
	  private void showCommandHelp(String cmd) {
	    // show help for current command (current word)
	    String help = app.getCommandSyntax(cmd);

	    // show help if available
	    if (help != null) {
	      app.showError(new MyError(app, app.getPlain("Syntax") + ":\n" + help, cmd));
	    } else {
	      app.getGuiManager().openCommandHelp(null);
	    }
	  }
	  
	  private void clearSelection() {
		    int start = textField.getText().indexOf(textField.getTextBox().getSelectedText());
		    int end = start + textField.getTextBox().getSelectionLength();
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
	   * Updates curWord to word at current caret position.
	   * curWordStart, curWordEnd are set to this word's start and end position
	   */
	  public void updateCurrentWord(boolean searchRight) {
	    String text = getText();
	    if (text == null)
	      return;
	    int caretPos = getCaretPosition();

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
	        isLetterOrDigit(text.charAt(curWordStart))) {
	      --curWordStart;
	    }
	    curWordStart++;
	    // search to the right
	    int curWordEnd = caretPos;
	    int length = text.length();
	    while (curWordEnd < length && isLetterOrDigit(text.charAt(curWordEnd)))
	      ++curWordEnd;

	    curWord.setLength(0);
	    curWord.append(text.substring(curWordStart, curWordEnd));

	    // remove '[' at end
	    if (curWord.toString().endsWith("[")) {
	      curWord.setLength(curWord.length() - 1);
	    }
	  }

	public void showError(Exception e) {
		 if (e instanceof MyException) {
		      updateCurrentWord(true);
		      int err = ((MyException) e).getErrorType();
		      if (err == MyException.INVALID_INPUT) {
		        String command = app.getReverseCommand(getCurrentWord());
		        if (command != null) {

		          app.showError(new MyError(app, app.getError("InvalidInput") + "\n\n"
		              + app.getPlain("Syntax") + ":\n" + app.getCommandSyntax(command),
		              getCurrentWord()));
		          return;
		        }
		      }
		    }
		    // can't work out anything better, just show "Invalid Input"
		    app.showError(e.getLocalizedMessage());
    }
	
	 /*
	   * just show syntax error (already correctly formulated by CommandProcessor.argErr())
	   */
	  public void showError(MyError e) {
	    app.showError(e);
	  }

	public boolean getAutoComplete() {
		 return autoComplete && app.isAutoCompletePossible();
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
		    /*AG dont do that yet if (app.getLocale().getLanguage().equals("ko")) {
		      String text = getText();
		      int caretPos = getCaretPosition();
		      String mergeText = Korean.mergeDoubleCharacters(text);
		      int decrease = text.length() - mergeText.length();
		      if (decrease > 0) {
		        setText(mergeText);
		        setCaretPosition(caretPos - decrease);
		      }
		    }*/
		  App.debug("KoreanDoubles may be needed in AutocompleteTextField");
		  }
	  
	  private boolean moveToNextArgument(boolean find) {
		  	String text = getText();
		    int caretPos = getCaretPosition();

		    // make sure it works if caret is just after [
		    // if (caretPos > 0 && text.charAt(caretPos - 1) == '[') caretPos--;

		    //AGMatcher argMatcher = syntaxArgPattern.matcher(text);
		    MatchResult argMatcher = syntaxArgPattern.exec(text);
		    //boolean hasNextArgument = argMatcher.find(caretPos);
		    boolean hasNextArgument = syntaxArgPattern.test(text);
		    if (find && !hasNextArgument) {
		      //hasNextArgument = argMatcher.find();
		    	hasNextArgument = syntaxArgPattern.test(text);
		    }
		    //if (hasNextArgument && (find || argMatcher.start() == caretPos)) {
		    if (hasNextArgument && (find || argMatcher.getIndex() == caretPos)) {
		      //setCaretPosition(argMatcher.end();
		      //moveCaretPosition(argMatcher.start() + 1);
		      for (int i = 0; i < argMatcher.getGroupCount(); i++) {
		    	  String groupStr = argMatcher.getGroup(i);
		    	  textField.getTextBox().setSelectionRange(text.indexOf(groupStr)+1, groupStr.length()-1);
		      }
		      return true;
		    } else {
		      return false;
		    }
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
	
			// checking for isAltDown() because Alt+, prints another character on the PC
			// TODO make this more robust - perhaps it could go in a document change listener
		    if (ch == ',' && !e.isAltKeyDown()) {
		      if (caretPos < text.length() && text.charAt(caretPos) == ',') {
		        // User typed ',' just in ahead of an existing ',':
		        // We may be in the position of filling in the next argument of an autocompleted command
		        // Look for a pattern of the form ", < Argument Description > ," or ", < Argument Description > ]"
		        // If found, select the argument description so that it can easily be typed over with the value
		        // of the argument.
		        if (moveToNextArgument(false)) {
		          e.stopPropagation();
		          e.preventDefault();
		        }
		        return;
		      }
		    }
	
		    if (!(ch == '(' || ch == '{' || ch == '[' || ch == '}' || ch == ')' || ch == ']')) {
		      //super.keyTyped(e);
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
		        || MyTextField.isCloseBracketOrWhitespace(text.charAt(caretPos))) {
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
		   /*AG if (Application.MAC_OS && e.isControlKeyDown()) {
		      e.consume();
		    }*/

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

		        /*AG do this if we will have windows Component comp = SwingUtilities.getRoot(this);
		        if (comp instanceof JDialog) {
		          ((JDialog) comp).setVisible(false);
		          return;
		        }*/
		        SuggestBox.DefaultSuggestionDisplay display = (SuggestBox.DefaultSuggestionDisplay) textField.getSuggestionDisplay();
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
		        break;

		      case GWTKeycodes.KEY_F9:
		        // needed for applets
		        if (app.isApplet())
		          app.getGlobalKeyDispatcher().handleGeneralKeys(e);
		        break;

		      case GWTKeycodes.KEY_RIGHT:
		        if (moveToNextArgument(false)) {
		          e.stopPropagation();
		        }
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
		           /*AG not yet... int n = JOptionPane.showOptionDialog(app.getMainComponent(),
		                app.getPlain("InputFieldHelp"), app.getPlain("ApplicationName")
		                    + " - " + app.getMenu("Help"), JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
		                options, // the titles of buttons
		                options[0]); // default button title
                   
		            if (n == 1)
		              app.getGuiManager().openHelp(AbstractApplication.WIKI_MANUAL);
                   */
		          } else {
		            int pos = getCaretPosition();
		            while (pos > 0 && getText().charAt(pos - 1) == '[') {
		              pos--;
		            }
		            String word = getWordAtPos(getText(), pos);
		            String lowerCurWord = word.toLowerCase();
		            String closest = dict.lookup(lowerCurWord);

		            if (closest != null)// && lowerCurWord.equals(closest.toLowerCase()))
		              showCommandHelp(app.getInternalCommand(closest));
		            else
		              app.getGuiManager().openHelp(App.WIKI_MANUAL);

		          }
		        } else
		          app.getGuiManager().openHelp(App.WIKI_MANUAL);

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
		    	  /*Try handling here that is originaly in keyup
		    	   * 
		    	   */
		    	  boolean modifierKeyPressed = e.isControlKeyDown() || e
		    			  .isAltKeyDown();

		    	  // we don't want to act when AltGr is down
		    	  // as it is used eg for entering {[}] is some locales
		    	  // NB e.isAltGraphDown() doesn't work
		    	  if (e.isAltKeyDown() && e.isControlKeyDown())
		    		  modifierKeyPressed = false;

		    	  char charPressed = Character.valueOf((char) e.getNativeKeyCode());

		    		    if ((isLetterOrDigit(charPressed) || modifierKeyPressed)
		    		        && !(ctrlC)
		    		        && !(e.getNativeKeyCode() == GWTKeycodes.KEY_A)) {
		    		      clearSelection();
		    		    }

		    		    // handle alt-p etc
		    		    //super.keyReleased(e);

		    		    mergeKoreanDoubles();

		    		    if (getAutoComplete()) {
		    		      updateCurrentWord(false);
		    		      startAutoCompletion();
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
		SuggestBox.DefaultSuggestionDisplay display = (SuggestBox.DefaultSuggestionDisplay) textField.getSuggestionDisplay();
		return display.isSuggestionListShowing();
    }
	
	private boolean isSuggestionJustHappened = false;
	private boolean isSuggestionClickJustHappened = false;
	
	/**
	 * @return that suggestion is just happened (click or enter,
	 *  so we don't need to run the enter code again
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
	  
	  //textField.getTextBox().getElement().focus();
    }

	public void onSelection(SelectionEvent<Suggestion> event) {
	   isSuggestionJustHappened = true;
	   int index = completions.indexOf(event.getSelectedItem().getReplacementString());
	   validateAutoCompletion(index, getCompletions());
    }
	
	/**
	 * Inserts a string into the text at the current caret position
	 */
	public void insertString(String text) {
		int start = getSelectionStart();
		int end = getSelectionEnd();

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
			AutoCompleteTextFieldW tf = (AutoCompleteTextFieldW) this;
			tf.updateCurrentWord(false);
			tf.startAutoCompletion();
		}

		
		setCaretPosition(newPos);
		

		// TODO: tried to keep the Mac OS from auto-selecting the field by
		// resetting the
		// caret, but not working yet
		// setCaret(new DefaultCaret());
		// setCaretPosition(newPos);
	}

	private int getSelectionEnd() {
	   return getSelectionStart() + textField.getTextBox().getSelectionLength();
    }

	private int getSelectionStart() {
	   return getText().indexOf(textField.getTextBox().getSelectedText());
    }

	  /**
	   * Ticket #1167 Auto-completes input; It will keep already entered parameters<br>
	   * and merge them in order. If chosen command has less it will output:<br>
	   * command_name[&lt;original parameter list&gt;]<br>
	   * <br>
	   * e.g.:<br>
	   * Input: der <br>
	   * Choose: Derivative[ &lt;Function&gt; ]<br>
	   * Output: Derivative[ &lt;Function&gt; ]<br>
	   * <br>
	   * Input: derivative[x^2]<br>
	   * Choose: Derivative[ &lt;Function&gt; ]<br>
	   * Output: Derivative[x^2]<br>
	   * <br>
	   * Input: derivative[x^2]<br>
	   * Choose: Derivative[ &lt;Function>, &lt;Number&gt; ]<br>
	   * Output: Derivative[x^2, &lt;Number&gt; ]<br>
	   * <br>
	   * Input: derivative[x^2, &lt;Number&gt; ]<br>
	   * Choose: Derivative[ &lt;Function&gt;, &lt;Number&gt; ]<br>
	   * Output: Derivative[x^2, &lt;Number&gt; ]<br>
	   * <br>
	   * Input: inde[x, &lt;Number&gt; ]<br>
	   * Choose: IndexOf[ &lt;Object&gt;, &lt;List&gt;, &lt;StartIndex&gt; ]<br>
	   * Output: IndexOf[x, &lt;Number&gt; , &lt;StartIndex&gt; ]<br>
	   * <br>
	   * 
	   * @param index
	   *          index of the chosen command in the completions list
	   * @return false if completions list is null or index < 0 or index >
	   *         completions.size()
	   * @author Lucas Binter
	   */
	public boolean validateAutoCompletion(int index, List<String> completions) {
		ValidateAutocompletionResult ret = geogebra.common.gui.inputfield.MyTextField.commonValidateAutocompletion(index, completions,getText(),curWordStart);
		
		if (!ret.returnval) {
			return false;
		}
		
		setText(ret.sb);
		setCaretPosition(ret.carPos);
		
		moveToNextArgument(false);
		return true;
	}
	
	/**
	 * Sets a flag to show the symbol table icon when the field is focused
	 * 
	 * @param showSymbolTableIcon
	 */
	public void setShowSymbolTableIcon(boolean showSymbolTableIcon) {
		this.showSymbolTableIcon = showSymbolTableIcon;
	}

	private SymbolTablePopupW getTablePopup() {
		if (tablePopup == null)
			tablePopup = new SymbolTablePopupW(app, this);
		return tablePopup;
	}

	public void hideTablePopup() {
		if (tablePopup != null)
			tablePopup.hide();
	}

	public String getText() {
	    return textField.getText();
    }

	public void setText(String s) {
	   textField.setText(s);
    }

	public FocusWidget getTextBox() {
	    return textField.getTextBox();
    }

	public void toggleSymbolButton(boolean toggled) {
	    showSymbolButton.setDown(toggled);
    }

	public SuggestBox getTextField() {
		return textField;
	}
	
	public void removeSymbolButton(){
		remove(showSymbolButton);
	}
}
