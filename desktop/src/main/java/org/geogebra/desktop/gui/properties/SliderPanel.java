package org.geogebra.desktop.gui.properties;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.SliderModel;
import org.geogebra.common.gui.dialog.options.model.SliderModel.ISliderOptionsListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.AngleTextField;
import org.geogebra.desktop.gui.dialog.PropertiesPanelD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

/**
 * panel for numeric slider
 * 
 * @author Markus Hohenwarter
 */
public class SliderPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, ISliderOptionsListener {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private SliderModel model;
	private AngleTextField tfMin, tfMax;
	private JTextField tfWidth;
	private JTextField[] tfields;
	private JLabel[] tLabels;
	private JLabel lbWidthUnit;
	private JCheckBox cbSliderFixed, cbRandom;
	private JComboBox coSliderHorizontal;

	private AppD app;
	private AnimationStepPanel stepPanel;
	private AnimationSpeedPanel speedPanel;
	private Kernel kernel;
	private PropertiesPanelD propPanel;
	private JPanel intervalPanel, sliderPanel, animationPanel;
	private boolean useTabbedPane;
	private boolean actionPerforming;

	private final Localization loc;

	public SliderPanel(AppD app, PropertiesPanelD propPanel,
			boolean useTabbedPane, boolean includeRandom) {
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		model = new SliderModel(app, this);
		this.propPanel = propPanel;
		this.useTabbedPane = useTabbedPane;
		model.setIncludeRandom(includeRandom);

		intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		sliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		animationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		cbSliderFixed = new JCheckBox("", true);
		cbSliderFixed.addActionListener(this);
		sliderPanel.add(cbSliderFixed);

		cbRandom = new JCheckBox();
		cbRandom.addActionListener(this);
		sliderPanel.add(cbRandom);

		coSliderHorizontal = new JComboBox();
		coSliderHorizontal.addActionListener(this);
		sliderPanel.add(coSliderHorizontal);

		tfMin = new AngleTextField(6, app);
		tfMax = new AngleTextField(6, app);
		tfWidth = new MyTextFieldD(app, 4);
		lbWidthUnit = new JLabel("");
		tfields = new MyTextFieldD[3];
		tLabels = new JLabel[3];
		tfields[0] = tfMin;
		tfields[1] = tfMax;
		tfields[2] = tfWidth;
		int numPairs = tLabels.length;

		// add textfields
		for (int i = 0; i < numPairs; i++) {
			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			tLabels[i] = new JLabel("", SwingConstants.LEADING);
			p.add(tLabels[i]);
			JTextField textField = tfields[i];
			tLabels[i].setLabelFor(textField);
			textField.addActionListener(this);
			textField.addFocusListener(this);
			p.add(textField);
			if (i == 2) {
				p.add(lbWidthUnit);
			}
			p.setAlignmentX(Component.LEFT_ALIGNMENT);

			if (i < 2) {
				intervalPanel.add(p);
			} else {
				sliderPanel.add(p);
			}
		}

		// add increment to intervalPanel
		stepPanel = new AnimationStepPanel(app);
		stepPanel.setPartOfSliderPanel();
		intervalPanel.add(stepPanel);

		speedPanel = new AnimationSpeedPanel(app);
		speedPanel.setPartOfSliderPanel();
		animationPanel.add(speedPanel);

		setLabels();
	}

	private void initPanels() {
		removeAll();
		// put together interval, slider options, animation panels
		if (useTabbedPane) {
			setLayout(new FlowLayout());
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			tabbedPane.addTab(loc.getMenu("Interval"), intervalPanel);
			tabbedPane.addTab(loc.getMenu("Slider"), sliderPanel);
			tabbedPane.addTab(loc.getMenu("Animation"), animationPanel);
			add(tabbedPane);
		} else { // no tabs
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			intervalPanel.setBorder(
					BorderFactory.createTitledBorder(loc.getMenu("Interval")));
			sliderPanel.setBorder(
					BorderFactory.createTitledBorder(loc.getMenu("Slider")));
			animationPanel.setBorder(BorderFactory
					.createTitledBorder(loc.getMenu("Animation")));
			add(intervalPanel);
			add(Box.createVerticalStrut(5));
			add(sliderPanel);
			add(Box.createVerticalStrut(5));
			add(animationPanel);
		}
	}

	@Override
	public void setLabels() {
		initPanels();

		cbSliderFixed.setText(loc.getMenu("fixed"));
		cbRandom.setText(loc.getMenu("Random"));

		String[] comboStr = { loc.getMenu("horizontal"),
				loc.getMenu("vertical") };

		int selectedIndex = coSliderHorizontal.getSelectedIndex();
		coSliderHorizontal.removeActionListener(this);
		coSliderHorizontal.removeAllItems();

		for (int i = 0; i < comboStr.length; ++i) {
			coSliderHorizontal.addItem(comboStr[i]);
		}

		coSliderHorizontal.setSelectedIndex(selectedIndex);
		coSliderHorizontal.addActionListener(this);

		tLabels[0].setText(loc.getMenu("min") + ":");
		tLabels[1].setText(loc.getMenu("max") + ":");
		tLabels[2].setText(loc.getMenu("Width") + ":");

		model.setLabelForWidthUnit();

		stepPanel.setLabels();
		speedPanel.setLabels();
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		stepPanel.updatePanel(geos);
		speedPanel.updatePanel(geos);

		model.setGeos(geos);
		if (!model.checkGeos()) {
			return null;
		}

		for (int i = 0; i < tfields.length; i++) {
			tfields[i].removeActionListener(this);
		}

		coSliderHorizontal.removeActionListener(this);
		cbSliderFixed.removeActionListener(this);
		cbRandom.removeActionListener(this);

		model.updateProperties();

		for (int i = 0; i < tfields.length; i++) {
			tfields[i].addActionListener(this);
		}

		coSliderHorizontal.addActionListener(this);
		cbSliderFixed.addActionListener(this);
		cbRandom.addActionListener(this);

		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == cbSliderFixed) {
			doCheckBoxActionPerformed((JCheckBox) source);
		} else if (source == cbRandom) {
			doRandomActionPerformed((JCheckBox) source);
		} else if (source == coSliderHorizontal) {
			doComboBoxActionPerformed((JComboBox) source);
		} else {
			doTextFieldActionPerformed((JTextField) e.getSource());
		}
	}

	private void doCheckBoxActionPerformed(JCheckBox source) {
		model.applyFixed(source.isSelected());
		updatePanel(model.getGeos());
	}

	private void doRandomActionPerformed(JCheckBox source) {
		model.applyRandom(source.isSelected());
		updatePanel(model.getGeos());
	}

	private void doComboBoxActionPerformed(JComboBox source) {
		model.applyDirection(source.getSelectedIndex());
		updatePanel(model.getGeos());
	}

	private void doTextFieldActionPerformed(JTextField source) {
		actionPerforming = true;
		String inputText = source.getText().trim();
		boolean emptyString = "".equals(inputText);

		NumberValue value = new MyDouble(kernel, Double.NaN);
		if (!emptyString) {
			value = kernel.getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
		}

		if (source == tfMin) {
			model.applyMin(value);
		} else if (source == tfMax) {
			model.applyMax(value);
		} else if (source == tfWidth) {
			model.applyWidth(value.getDouble());
		}

		if (propPanel != null) {
			propPanel.updateSelection(model.getGeos());
		} else {
			updatePanel(model.getGeos());
		}
		actionPerforming = false;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// only handle focus lost
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (!actionPerforming) {
			doTextFieldActionPerformed((JTextField) e.getSource());
		}
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		cbSliderFixed.setFont(font);
		cbRandom.setFont(font);
		coSliderHorizontal.setFont(font);

		for (int i = 0; i < tLabels.length; ++i) {
			tLabels[i].setFont(font);
		}

		tfMin.setFont(font);
		tfMax.setFont(font);
		tfWidth.setFont(font);

		for (int i = 0; i < tfields.length; ++i) {
			tfields[i].setFont(font);
		}

		stepPanel.updateFonts();
		speedPanel.updateFonts();

	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMinText(String text) {
		tfMin.setText(text);
	}

	@Override
	public void setMaxText(String text) {
		tfMax.setText(text);
	}

	@Override
	public void setWidthText(String text) {
		tfWidth.setText(text);
	}

	@Override
	public void selectFixed(boolean value) {
		cbSliderFixed.setSelected(value);
	}

	@Override
	public void selectRandom(boolean value) {
		cbRandom.setSelected(value);
	}

	@Override
	public void setRandomVisible(boolean value) {
		cbRandom.setVisible(value);
	}

	@Override
	public void setSliderDirection(int index) {
		coSliderHorizontal.setSelectedIndex(index);
	}

	@Override
	public void setWidthUnitText(String text) {
		lbWidthUnit.setText(text);
	}
}