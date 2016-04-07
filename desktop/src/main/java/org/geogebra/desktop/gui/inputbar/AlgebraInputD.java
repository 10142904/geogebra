/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.inputbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.inputfield.AutoCompleteTextFieldD;
import org.geogebra.desktop.gui.view.algebra.AlgebraInputDropTargetListener;
import org.geogebra.desktop.gui.view.algebra.InputPanelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * @author Markus Hohenwarter
 */
public class AlgebraInputD extends JPanel implements ActionListener,
		KeyListener, FocusListener, SetLabels, MouseListener {
	private static final long serialVersionUID = 1L;

	protected AppD app;

	// autocompletion text field
	protected AutoCompleteTextFieldD inputField;

	private JLabel inputLabel;
	private JToggleButton btnHelpToggle;
	private InputPanelD inputPanel;
	private LocalizationD loc;

	/***********************************************************
	 * creates new AlgebraInput
	 * 
	 * @param app
	 */
	public AlgebraInputD(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();

		app.removeTraversableKeys(this);

		initGUI();

		addMouseListener(this);
	}

	private void addPreviewListener() {
		inputPanel.getTextComponent().getDocument()
				.addDocumentListener(new DocumentListener() {

					public void changedUpdate(DocumentEvent e) {
						preview();
					}

					public void removeUpdate(DocumentEvent e) {
						preview();
					}

					public void insertUpdate(DocumentEvent e) {
						preview();
					}

					public void preview() {
						app.getKernel().getInputPreviewHelper()
								.updatePreviewFromInputBar(
										inputField.getText(),
										new AsyncOperation<Boolean>() {

											@Override
											public void callback(Boolean obj) {
												inputField.setBackground(
														obj ? Color.WHITE
																: Color.ORANGE);

											}
										});
					}
				});
	}

	public void initGUI() {
		removeAll();
		inputLabel = new JLabel();
		inputPanel = new InputPanelD(null, app, 30, true);

		if (app.has(Feature.INPUT_BAR_PREVIEW)) {
			addPreviewListener();
		}

		// create and set up the input field
		inputField = (AutoCompleteTextFieldD) inputPanel.getTextComponent();
		inputField.setEditable(true);
		inputField.addKeyListener(this);
		inputField.addFocusListener(this);

		// enable a history popup and embedded button
		inputField.addHistoryPopup(app.showInputTop());

		// enable drops
		inputField.setDragEnabled(true);
		inputField.setDropTarget(new DropTarget(this,
				new AlgebraInputDropTargetListener(app, inputField)));
		inputField.setColoringLabels(true);

		updateFonts();

		// create toggle button to hide/show the input help panel
		btnHelpToggle = new JToggleButton() {
			public Point getToolTipLocation(MouseEvent e) {
				// make sure tooltip doesn't cover button (when window
				// maximized)
				return new Point(0, (int) -this.getSize().getHeight() / 2);
			}
		};

		// btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_16x16.png"));
		// btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_16x16.png"));

		updateIcons();

		// btnHelpToggle.setIcon(app.getImageIcon("inputhelp_left_20x20.png"));
		// btnHelpToggle.setSelectedIcon(app.getImageIcon("inputhelp_right_20x20.png"));

		btnHelpToggle.addActionListener(this);
		btnHelpToggle.setFocusable(false);
		btnHelpToggle.setContentAreaFilled(false);
		btnHelpToggle.setBorderPainted(false);

		// create sub-panels
		JPanel labelPanel = new JPanel(new BorderLayout());

		labelPanel.add(inputLabel, loc.borderEast());

		JPanel eastPanel = new JPanel(new BorderLayout());
		if (app.showInputHelpToggle()) {
			eastPanel.add(btnHelpToggle, loc.borderWest());
		}

		labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 2));
		eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

		setLayout(new BorderLayout(0, 0));
		add(labelPanel, loc.borderWest());
		add(inputPanel, BorderLayout.CENTER);
		add(eastPanel, loc.borderEast());

		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				SystemColor.controlShadow));
		setLabels();
	}

	private void updateIcons() {
		if (btnHelpToggle == null) {
			return;
		}

		btnHelpToggle.setIcon(app.getScaledIcon("inputhelp_left_18x18.png"));
		btnHelpToggle.setSelectedIcon(app
				.getScaledIcon("inputhelp_right_18x18.png"));

	}

	@Override
	public boolean requestFocusInWindow() {
		return inputField.requestFocusInWindow();
	}

	@Override
	public void requestFocus() {
		requestFocusInWindow();
	}

	@Override
	public boolean hasFocus() {
		return inputField.hasFocus();
	}

	public void clear() {
		inputField.setText(null);
	}

	public AutoCompleteTextFieldD getTextField() {
		return inputField;
	}

	public void updateOrientation(boolean showInputTop) {
		inputField.setOpenSymbolTableUpwards(!showInputTop);
	}

	/**
	 * updates labels according to current locale
	 */
	public void setLabels() {
		if (inputLabel != null)
			inputLabel.setText(app.getPlain("InputLabel") + ":");

		if (btnHelpToggle != null)
			btnHelpToggle.setToolTipText(app.getMenu("InputHelp"));

		inputField.setDictionary(false);
		inputField.setLabels();
	}

	public void updateFonts() {
		inputField.setFont(app.getBoldFont());
		inputLabel.setFont(app.getPlainFont());
		inputField.setPopupsFont(app.getPlainFont());

		// update the help panel
		if (((GuiManagerD) app.getGuiManager()).hasInputHelpPanel()) {
			InputBarHelpPanelD helpPanel = (InputBarHelpPanelD) ((GuiManagerD) app
					.getGuiManager()).getInputHelpPanel();
			helpPanel.updateFonts();
		}

		updateIcons();

	}

	// /**
	// * Inserts string at current position of the input textfield and gives
	// focus
	// * to the input textfield.
	// * @param str: inserted string
	// */
	// public void insertString(String str) {
	// inputField.replaceSelection(str);
	// }

	/**
	 * Sets the content of the input textfield and gives focus to the input
	 * textfield.
	 * 
	 * @param str
	 */
	public void replaceString(String str) {
		inputField.setText(str);
	}

	// see actionPerformed
	public void insertCommand(String cmd) {
		if (cmd == null)
			return;

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = oldText.substring(0, pos) + cmd + "[]"
				+ oldText.substring(pos);

		inputField.setText(newText);
		inputField.setCaretPosition(pos + cmd.length() + 1);
		inputField.requestFocus();
	}

	public void insertString(String str) {
		if (str == null)
			return;

		int pos = inputField.getCaretPosition();
		String oldText = inputField.getText();
		String newText = oldText.substring(0, pos) + str
				+ oldText.substring(pos);

		inputField.setText(newText);
		inputField.setCaretPosition(pos + str.length());
		inputField.requestFocus();
	}

	/**
	 * action listener implementation for input help panel toggle button
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnHelpToggle) {

			// ==========================================
			// hidden switch to toggle native/crossPlatform LAF
			if (AppD.getControlDown() && AppD.getShiftDown()) {
				AppD.toggleCrossPlatformLAF();
				SwingUtilities.updateComponentTreeUI(app.getFrame());
				app.getFrame().pack();
				return;
			}
			// =========================================

			if (btnHelpToggle.isSelected()) {
				InputBarHelpPanelD helpPanel = (InputBarHelpPanelD) ((GuiManagerD) app
						.getGuiManager()).getInputHelpPanel();
				helpPanel.setLabels();
				helpPanel.setCommands();
				app.setShowInputHelpPanel(true);
			} else {
				app.setShowInputHelpPanel(false);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		// the input field may have consumed this event
		// for auto completion
		if (e.isConsumed())
			return;

		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_A:
		case KeyEvent.VK_C:
		case KeyEvent.VK_X:
		case KeyEvent.VK_V:
			// make sure eg Ctrl-A not passed on
			return;
		case KeyEvent.VK_ENTER:
			app.getKernel().clearJustCreatedGeosInViews();
			String input = app.getKernel().getInputPreviewHelper()
					.getInput(getTextField().getText());

			if (input == null || input.length() == 0) {
				app.getActiveEuclidianView().requestFocus(); // Michael
																// Borcherds
																// 2008-05-12
				return;
			}

			app.setScrollToShow(true);
			GeoElement[] geos;
			try {
				{
					geos = app
							.getKernel()
							.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(input,
									true, false, true, true);

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					// set first outputs (same geo class) as selected geos (for
					// properties view)
					if (geos.length > 0) {
						ArrayList<GeoElement> list = new ArrayList<GeoElement>();
						// add first output
						GeoElement geo = geos[0];
						list.add(geo);
						GeoClass c = geo.getGeoClassType();
						int i = 1;
						// add following outputs until geo class changes
						while (i < geos.length) {
							geo = geos[i];
							if (geo.getGeoClassType() == c) {
								list.add(geo);
								i++;
							} else {
								i = geos.length;
							}
						}
						app.getSelectionManager().setSelectedGeos(list);
					}

				}
			} catch (Exception ee) {
				inputField.addToHistory(getTextField().getText());
				app.showError(ee, inputField);
				return;
			} catch (MyError ee) {
				inputField.addToHistory(getTextField().getText());
				inputField.showError(ee);
				return;
			}

			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText()) {
				GeoText text = (GeoText) geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null) {

					Construction cons = text.getConstruction();
					EuclidianView ev = app.getActiveEuclidianView();

					boolean oldSuppressLabelsStatus = cons
							.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint p = new GeoPoint(text.getConstruction(), null,
							(ev.getXmin() + ev.getXmax()) / 2,
							(ev.getYmin() + ev.getYmax()) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try {
						text.setStartPoint(p);
						text.update();
					} catch (CircularDefinitionException e1) {
						e1.printStackTrace();
					}
				}
			}

			app.setScrollToShow(false);

			inputField.addToHistory(input);
			if (!getTextField().getText().equals(input)) {
				inputField.addToHistory(getTextField().getText());
			}
			inputField.setText(null);

			break;
		default:
			app.getGlobalKeyDispatcher()
					.handleGeneralKeys(e); // handle eg ctrl-tab
		}
	}




	public void keyReleased(KeyEvent e) {
		//
	}

	public void keyTyped(KeyEvent e) {
		//
	}

	public void focusGained(FocusEvent arg0) {
		// app.clearSelectedGeos();
	}

	public void focusLost(FocusEvent arg0) {
		//
	}

	public void mouseClicked(MouseEvent e) {
		//

	}

	public void mousePressed(MouseEvent e) {
		//

	}

	public void mouseReleased(MouseEvent e) {
		//

	}

	public void mouseEntered(MouseEvent e) {
		// make sure tooltips from Tool Bar don't get in the way
		setToolTipText("");
	}

	public void mouseExited(MouseEvent e) {
		//

	}
}