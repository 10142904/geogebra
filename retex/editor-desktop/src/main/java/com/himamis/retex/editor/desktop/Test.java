/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
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
 */
package com.himamis.retex.editor.desktop;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class Test {

	static {
		FactoryProvider.setInstance(new FactoryProviderDesktop());
	}

	public static void main(String[] args) {
		final MathFieldD mathField = new MathFieldD();
		
		JFrame frame = new JFrame();
		frame.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(mathField);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		// mathField.insertString("ggbmatrix(3,3)");

		// these two should both end up inserting a single char \uB458
		mathField.insertString("\u3137\u315c\u3139 ");
		mathField.insertString("\u1103\u116e\u11af ");
		// sector command (short format)
		mathField.insertString("\ubd80\ucc44\uaf34 ");
		// sector command (long format)
		mathField.insertString("\u1107\u116E\u110E\u1162\u1101\u1169\u11AF ");

		// sector command (compatibility format)
		mathField.insertString("\u3142\u315c\u314a\u3150\u3132\u3157\u3139 ");

		// mathField.insertString("Midpoint(<Point>, <Point>)");

	}
}
