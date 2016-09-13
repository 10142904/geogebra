package com.himamis.retex.editor.share.event;

public interface MathFieldListener {
	public void onEnter();

	public void onKeyTyped();

	public void onCursorMove();

	public String alt(char unicodeKeyChar, boolean shift);
}
