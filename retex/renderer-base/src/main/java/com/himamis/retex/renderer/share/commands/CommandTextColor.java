package com.himamis.retex.renderer.share.commands;

import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.ColorAtom;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.graphics.Color;

public class CommandTextColor extends Command1A {

	Color fg;

	public CommandTextColor() {
		//
	}

	public CommandTextColor(Color fg2) {
		this.fg = fg;
	}

	@Override
	public boolean init(TeXParser tp) {
		fg = CommandDefinecolor.getColor(tp);
		return true;
	}

	@Override
	public Atom newI(TeXParser tp, Atom a) {
		return new ColorAtom(a, null, fg);
	}

	@Override
	public Command duplicate() {
		return new CommandTextColor(fg);
	}

}
