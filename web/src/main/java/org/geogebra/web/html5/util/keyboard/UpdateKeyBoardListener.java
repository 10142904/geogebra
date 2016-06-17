package org.geogebra.web.html5.util.keyboard;

import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;

public interface UpdateKeyBoardListener {

	// public void showInputField();

	public void keyBoardNeeded(boolean show, MathKeyboardListener textField);

	public void doShowKeyBoard(boolean b, MathKeyboardListener textField);

}