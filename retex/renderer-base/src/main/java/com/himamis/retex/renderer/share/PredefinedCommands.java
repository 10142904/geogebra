/* PredefinedCommands.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2011 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

package com.himamis.retex.renderer.share;

import com.himamis.retex.renderer.share.Cancel.Type;
import com.himamis.retex.renderer.share.XArrowAtom.Kind;

final class PredefinedCommands {

	static {

		for (LaTeXCommand command : LaTeXCommand.values()) {

			switch (command.numArgs) {
			case 1:
				MacroInfo.Commands.put(command.getName(), new MacroInfo(command, command.arg1));
				break;
			case 2:
				MacroInfo.Commands.put(command.getName(), new MacroInfo(command, command.arg1, command.arg2));
				break;
			}

		}

	}

	public enum LaTeXCommand implements Macro {

		newcommand(2, 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.newcommand_macro(tp, args);
			}
		},

		renewcommand(2, 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.renewcommand_macro(tp, args);
			}
		},
		rule(2, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.rule_macro(args);
			}
		},

		hspace(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hvspace_macro(args);
			}
		},

		vspace(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hvspace_macro(args);
			}
		},
		llap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.clrlap_macro(tp, args);
			}
		},
		rlap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.clrlap_macro(tp, args);
			}
		},
		clap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.clrlap_macro(tp, args);
			}
		},
		mathllap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathclrlap_macro(tp, args);
			}
		},
		mathrlap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathclrlap_macro(tp, args);
			}
		},
		mathclap(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathclrlap_macro(tp, args);
			}
		},
		// includegraphics(1, 1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.includegraphics_macro(tp, args);
		// }
		// },
		cfrac(2, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.cfrac_macro(tp, args);
			}
		},
		frac(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.frac_macro(tp, args);
			}
		},
		sfrac(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sfrac_macro(tp, args);
			}
		},
		genfrac(6) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.genfrac_macro(tp, args);
			}
		},
		over(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.over_macro(tp);
			}
		},
		overwithdelims(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overwithdelims_macro(tp, args);
			}
		},
		atop(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.atop_macro(tp);
			}
		},
		atopwithdelims(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.atopwithdelims_macro(tp, args);
			}
		},
		choose(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.choose_macro(tp);
			}
		},
		bangle(0) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.bangle_macro(tp);
			}
		},
		brace(0) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.brace_macro(tp);
			}
		},
		brack(0) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.brack_macro(tp);
			}
		},
		underscore(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underscore_macro();
			}
		},
		mbox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mbox_macro(tp, args);
			}
		},
		text(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.text_macro(tp, args);
			}
		},
		ce(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.mhchem_ce_macro(tp, args);
			}
		},
		intertext(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.intertext_macro(tp, args);
			}
		},
		binom(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.binom_macro(tp, args);
			}
		},
		mathbf(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathbf_macro(tp, args);
			}
		},
		bf(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.bf_macro(tp);
			}
		},
		mathbb(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		mathcal(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		cal(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		mathit(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathit_macro(tp, args);
			}
		},
		it(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.it_macro(tp);
			}
		},
		mathrm(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathrm_macro(tp, args);
			}
		},
		rm(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.rm_macro(tp);
			}
		},
		mathscr(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		mathsf(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathsf_macro(tp, args);
			}
		},
		sf(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sf_macro(tp);
			}
		},
		mathtt(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathtt_macro(tp, args);
			}
		},
		tt(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.tt_macro(tp);
			}
		},
		mathfrak(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		mathds(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		frak(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		Bbb(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		oldstylenums(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		bold(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		},
		_circumflex("^", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_apostrophe("'", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_quote("\"", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_backtick("`", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_equals("=", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_period(".", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		_tilda("~", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		u(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		v(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		H(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		r(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		U(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		T(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.T_macro(tp, args);
			}
		},
		t(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentbis_macros(tp, args);
			}
		},
		accent(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macro(tp, args);
			}
		},
		grkaccent(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.grkaccent_macro(tp, args);
			}
		},
		hat(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		widehat(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		tilde(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		acute(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		grave(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		ddot(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		cyrddot(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		mathring(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		bar(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		breve(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		check(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		vec(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		dot(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		widetilde(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accent_macros(tp, args);
			}
		},
		nbsp(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.nbsp_macro();
			}
		},
		smallmatrix__env("smallmatrix@@env", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.smallmatrixATATenv_macro(tp, args);
			}
		},
		matrix__env("matrix@@env", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.matrixATATenv_macro(tp, args);
			}
		},
		overrightarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overrightarrow_macro(tp, args);
			}
		},
		overleftarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overleftarrow_macro(tp, args);
			}
		},
		overleftrightarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overleftrightarrow_macro(tp, args);
			}
		},
		underrightarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underrightarrow_macro(tp, args);
			}
		},
		underleftarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underleftarrow_macro(tp, args);
			}
		},
		underleftrightarrow(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underleftrightarrow_macro(tp, args);
			}
		},
		xleftarrow(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.Left);
			}
		},
		xrightarrow(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.Right);
			}
		},
		xleftandrightarrow(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.LeftAndRight);
			}
		},
		xrightandleft(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.RightAndLeft);
			}
		},
		xleftharpoondown(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.LeftHarpoonDown);
			}
		},
		xleftharpoonup(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.LeftHarpoonUp);
			}
		},
		xleftrightharpoons(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.LeftRightHarpoons);
			}
		},
		xlr(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.LR);
			}
		},
		xrightharpoondown(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.RightHarpoonDown);
			}
		},
		xrightharpoonup(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args, Kind.RightHarpoonUp);
			}
		},
		xrightleftharpoons(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.RightLeftHarpoons);
			}
		},
		xrightsmallleftharpoons(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.RightSmallLeftHarpoons);
			}
		},
		xsmallrightleftharpoons(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.xarrow_macro(tp, args,
						Kind.SmallRightLeftHarpoons);
			}
		},
		underbrace(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underbrace_macro(tp, args);
			}
		},
		overbrace(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overbrace_macro(tp, args);
			}
		},
		underbrack(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underbrack_macro(tp, args);
			}
		},
		overbrack(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overbrack_macro(tp, args);
			}
		},
		underparen(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underparen_macro(tp, args);
			}
		},
		overparen(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overparen_macro(tp, args);
			}
		},
		sqrt(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sqrt_macro(tp, args);
			}
		},
		sqrtsign(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sqrt_macro(tp, args);
			}
		},
		overline(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overline_macro(tp, args);
			}
		},
		underline(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underline_macro(tp, args);
			}
		},
		mathop(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathop_macro(tp, args);
			}
		},
		mathpunct(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathpunct_macro(tp, args);
			}
		},
		mathord(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathord_macro(tp, args);
			}
		},
		mathrel(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathrel_macro(tp, args);
			}
		},
		mathinner(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathinner_macro(tp, args);
			}
		},
		mathbin(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathbin_macro(tp, args);
			}
		},
		mathopen(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathopen_macro(tp, args);
			}
		},
		mathclose(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.mathclose_macro(tp, args);
			}
		},
		joinrel(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.joinrel_macro();
			}
		},
		smash(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.smash_macro(tp, args);
			}
		},
		vdots(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.vdots_macro();
			}
		},
		ddots(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.ddots_macro();
			}
		},
		iddots(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.iddots_macro();
			}
		},
		nolimits(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.nolimits_macro(tp);
			}
		},
		limits(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.limits_macro(tp);
			}
		},
		normal(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.normal_macro(tp);
			}
		},
		_parenthesis("(", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.leftparenthesis_macro(tp);
			}
		},
		_squarebracket("[", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.leftbracket_macro(tp);
			}
		},
		left(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.left_macro(tp, args);
			}
		},
		middle(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.middle_macro(tp, args);
			}
		},
		cr(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.cr_macro(tp);
			}
		},
		multicolumn(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.multicolumn_macro(tp, args);
			}
		},
		hdotsfor(1, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hdotsfor_macro(tp, args);
			}
		},
		array__env("array@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.arrayATATenv_macro(tp, args);
			}
		},
		align__env("align@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.alignATATenv_macro(tp, args);
			}
		},
		aligned__env("aligned@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.alignedATATenv_macro(tp, args);
			}
		},
		flalign__env("flalign@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.flalignATATenv_macro(tp, args);
			}
		},
		alignat__env("alignat@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.alignatATATenv_macro(tp, args);
			}
		},
		alignedat__env("alignedat@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.alignatATATenv_macro(tp, args);
			}
		},
		multline__env("multline@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.multlineATATenv_macro(tp, args);
			}
		},
		gather__env("gather@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.gatherATATenv_macro(tp, args);
			}
		},
		gathered__env("gathered@@env", 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.gatheredATATenv_macro(tp, args);
			}
		},
		shoveright(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.shoveright_macro(tp, args);
			}
		},
		shoveleft(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.shoveleft_macro(tp, args);
			}
		},
		_backslash("\\", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.backslashcr_macro(tp);
			}
		},
		newenvironment(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.newenvironment_macro(args);
			}
		},
		renewenvironment(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.renewenvironment_macro(args);
			}
		},
		makeatletter(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.makeatletter_macro(tp);
			}
		},
		makeatother(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.makeatother_macro(tp);
			}
		},
		fbox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.fbox_macro(tp, args);
			}
		},
		boxed(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.fbox_macro(tp, args);
			}
		},
		stackrel(2, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.stackrel_macro(tp, args);
			}
		},
		stackbin(2, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.stackbin_macro(tp, args);
			}
		},
		accentset(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.accentset_macro(tp, args);
			}
		},
		underaccent(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underaccent_macro(tp, args);
			}
		},
		undertilde(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.undertilde_macro(tp, args);
			}
		},
		overset(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.overset_macro(tp, args);
			}
		},
		Braket(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Braket_macro(tp, args);
			}
		},
		Set(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Set_macro(tp, args);
			}
		},
		underset(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.underset_macro(tp, args);
			}
		},
		boldsymbol(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.boldsymbol_macro(tp, args);
			}
		},
		LaTeX(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.LaTeX_macro();
			}
		},
		// GeoGebra(0) {
		// @Override
		// public Object handle(final TeXParser tp, final String[] args) {
		// return PredefMacros.GeoGebra_macro(tp, args);
		// }
		// },
		big(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.big_macro(tp, args);
			}
		},
		Big(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Big_macro(tp, args);
			}
		},
		bigg(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.bigg_macro(tp, args);
			}
		},
		Bigg(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Bigg_macro(tp, args);
			}
		},
		bigl(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.bigl_macro(tp, args);
			}
		},
		Bigl(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Bigl_macro(tp, args);
			}
		},
		biggl(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.biggl_macro(tp, args);
			}
		},
		Biggl(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Biggl_macro(tp, args);
			}
		},
		bigr(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.bigr_macro(tp, args);
			}
		},
		Bigr(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Bigr_macro(tp, args);
			}
		},
		biggr(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.biggr_macro(tp, args);
			}
		},
		Biggr(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Biggr_macro(tp, args);
			}
		},
		displaystyle(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.displaystyle_macro(tp);
			}
		},
		textstyle(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textstyle_macro(tp);
			}
		},
		scriptstyle(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.scriptstyle_macro(tp);
			}
		},
		scriptscriptstyle(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.scriptscriptstyle_macro(tp);
			}
		},
		sideset(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sideset_macro(tp, args);
			}
		},
		prescript(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.prescript_macro(tp, args);
			}
		},
		rotatebox(2, 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.rotatebox_macro(tp, args);
			}
		},
		reflectbox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.reflectbox_macro(tp, args);
			}
		},
		scalebox(2, 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.scalebox_macro(tp, args);
			}
		},
		resizebox(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.resizebox_macro(tp, args);
			}
		},
		raisebox(2, 2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.raisebox_macro(tp, args);
			}
		},
		shadowbox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.shadowbox_macro(tp, args);
			}
		},
		ovalbox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.ovalbox_macro(tp, args);
			}
		},
		doublebox(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.doublebox_macro(tp, args);
			}
		},
		phantom(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.phantom_macro(tp, args);
			}
		},
		hphantom(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hphantom_macro(tp, args);
			}
		},
		vphantom(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.vphantom_macro(tp, args);
			}
		},
		sp_breve("sp@breve", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.spATbreve_macro();
			}
		},
		sp_hat("sp@hat", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.spAThat_macro();
			}
		},
		definecolor(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.definecolor_macro(args);
			}
		},
		textcolor(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textcolor_macro(tp, args);
			}
		},
		fgcolor(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.fgcolor_macro(tp, args);
			}
		},
		bgcolor(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.bgcolor_macro(tp, args);
			}
		},
		cellcolor(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.cellcolor_macro(tp, args);
			}
		},
		jlmselection(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.selection_macro(tp, args);
			}
		},
		jlmcursor(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.cursor_macro(args);
			}
		},
		colorbox(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.colorbox_macro(tp, args);
			}
		},
		// * eg \cancel{10} */
		cancel(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.cancel_macro(tp, args, Type.SLASH);
			}
		},
		// * eg \bcancel{10} */
		bcancel(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.cancel_macro(tp, args, Type.BACKSLASH);
			}
		},
		// * eg \xcancel{10} */
		xcancel(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.cancel_macro(tp, args, Type.CROSS);
			}
		},
		fcolorbox(3) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.fcolorbox_macro(tp, args);
			}
		},
		c(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.cedilla_macro(tp, args);
			}
		},
		IJ(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.IJ_macro(args);
			}
		},
		ij(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.IJ_macro(args);
			}
		},
		TStroke(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.TStroke_macro(args);
			}
		},
		tStroke(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.TStroke_macro(args);
			}
		},
		Lcaron(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.LCaron_macro(args);
			}
		},
		tcaron(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.tcaron_macro();
			}
		},
		lcaron(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.LCaron_macro(args);
			}
		},
		k(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.ogonek_macro(tp, args);
			}
		},
		cong(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.cong_macro();
			}
		},
		doteq(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.doteq_macro();
			}
		},
		// jlmDynamic(1,1) {
		// @Override
		// public Object handle(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmDynamic_macro(tp, args);
		// }
		// },
		// jlmExternalFont(1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmExternalFont_macro(args);
		// }
		// },
		// jlmText(1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmText_macro(args);
		// }
		// },
		// jlmTextit(1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmTextit_macro(args);
		// }
		// },
		// jlmTextbf(1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmTextbf_macro(args);
		// }
		// },
		// jlmTextitbf(1) {
		// @Override
		// public Object executeMacro(final TeXParser tp, final String[] args) {
		// return PredefMacros.jlmTextitbf_macro(args);
		// }
		// },
//		DeclareMathSizes(4) {
//			@Override
//			public Object executeMacro(final TeXParser tp, final String[] args) {
//				return PredefMacros.DeclareMathSizes_macro(tp, args);
//			}
//		},
//		magnification(1) {
//			@Override
//			public Object executeMacro(final TeXParser tp, final String[] args) {
//				return PredefMacros.magnification_macro(tp, args);
//			}
//		},
		hline(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hline_macro(tp);
			}
		},
		tiny(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		scriptsize(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		footnotesize(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		small(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		normalsize(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		large(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		Large(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		LARGE(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		huge(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		Huge(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.size_macros(tp, args);
			}
		},
		jlatexmathcumsup(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.jlatexmathcumsup_macro(tp, args);
			}
		},
		jlatexmathcumsub(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.jlatexmathcumsub_macro(tp, args);
			}
		},
		hstrok(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.hstrok_macro(tp);
			}
		},
		Hstrok(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Hstrok_macro(tp);
			}
		},
		dstrok(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.dstrok_macro(tp);
			}
		},
		Dstrok(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.Dstrok_macro(tp);
			}
		},
		dotminus(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.dotminus_macro();
			}
		},
		ratio(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.ratio_macro();
			}
		},
		smallfrowneq(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.smallfrowneq_macro();
			}
		},
		geoprop(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.geoprop_macro();
			}
		},
		minuscolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.minuscolon_macro();
			}
		},
		minuscoloncolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.minuscoloncolon_macro();
			}
		},
		simcolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.minuscoloncolon_macro();
			}
		},
		simcoloncolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.simcoloncolon_macro();
			}
		},
		approxcolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.approxcolon_macro();
			}
		},
		approxcoloncolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.approxcoloncolon_macro();
			}
		},
		coloncolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.coloncolon_macro();
			}
		},
		equalscolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.equalscolon_macro();
			}
		},
		equalscoloncolon(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.equalscoloncolon_macro();
			}
		},
		colonminus(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.colonminus_macro();
			}
		},
		coloncolonminus(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.coloncolonminus_macro();
			}
		},
		colonequals(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.colonequals_macro();
			}
		},
		coloncolonequals(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.coloncolonequals_macro();
			}
		},
		colonsim(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.colonsim_macro();
			}
		},
		coloncolonsim(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.coloncolonsim_macro();
			}
		},
		colonapprox(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.colonapprox_macro();
			}
		},
		coloncolonapprox(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.coloncolonapprox_macro();
			}
		},
		kern(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.kern_macro(args);
			}
		},
		_char("char", 1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.char_macro(tp, args);
			}
		},
		roman(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.romannumeral_macro(args);
			}
		},
		Roman(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.romannumeral_macro(args);
			}
		},
		textcircled(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textcircled_macro(tp, args);
			}
		},
		textsc(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.textsc_macro(tp, args);
			}
		},
		sc(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.sc_macro(tp);
			}
		},
		_comma(",", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		_colon(":", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		_semicolon(";", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		thinspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		medspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		thickspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		_pling("!", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		negthinspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		negmedspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		negthickspace(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.muskip_macros(args);
			}
		},
		quad(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.quad_macro();
			}
		},
		qquad(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.qquad_macro();
			}
		},
		longdiv(2) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.longdiv_macro(tp, args);
			}
		},
		surd(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.surd_macro();
			}
		},
		iint(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.iint_macro();
			}
		},
		iiint(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.iiint_macro();
			}
		},
		iiiint(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.iiiint_macro();
			}
		},
		idotsint(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.idotsint_macro();
			}
		},
		_int("int", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.int_macro();
			}
		},
		oint(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.oint_macro();
			}
		},
		lmoustache(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.lmoustache_macro();
			}
		},
		rmoustache(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.rmoustache_macro();
			}
		},
		_dash("-", 0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.insertBreakMark_macro();
			}
		},
		jlmXML(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.jlmXML_macro(tp, args);
			}
		},
		above(0) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.above_macro(tp);
			}
		},
		abovewithdelims(2) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.abovewithdelims_macro(tp, args);
			}
		},
		st(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.st_macro(tp, args);
			}
		},
		fcscore(1) {
			@Override
			public Object executeMacro(final TeXParser tp, final String[] args) {
				return PredefMacros.fcscore_macro(args);
			}
		},
		imagebasesixtyfour(3) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.includegraphics_macro(tp, args);
			}
		},
		mathnormal(1) {
			@Override
			public Object executeMacro(final TeXParser tp,
					final String[] args) {
				return PredefMacros.textstyle_macros(tp, args);
			}
		};

		private int numArgs;
		private int arg1;
		private int arg2;
		private String name = null;

		public String getName() {
			return name == null ? name() : name;
		}

		LaTeXCommand(int arg1) {
			this.arg1 = arg1;
			numArgs = 1;
		}

		LaTeXCommand(int arg1, int arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			numArgs = 2;
		}

		LaTeXCommand(String name, int arg1) {
			this.arg1 = arg1;
			this.name = name;
			numArgs = 1;
		}

		LaTeXCommand(String name, int arg1, int arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.name = name;
			numArgs = 2;
		}

		@Override
		abstract public Object executeMacro(TeXParser tp, String[] args);

	}

}
