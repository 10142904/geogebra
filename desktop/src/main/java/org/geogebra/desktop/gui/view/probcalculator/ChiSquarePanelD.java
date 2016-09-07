package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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
import javax.swing.JTextField;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.probcalculator.ChiSquareCell;
import org.geogebra.common.gui.view.probcalculator.ChiSquarePanel;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.main.AppD;

/**
 * Panel for Chi Square and Goodness of Fit Tests
 * 
 * @author G. Sturr
 * 
 */
public class ChiSquarePanelD extends ChiSquarePanel
		implements ActionListener, FocusListener {

	private static final long serialVersionUID = 1L;

	

	// ======================================
	// GUI components
	// ======================================
	private JPanel pnlCount, pnlControl;
	private JComboBox cbRows, cbColumns;
	private ChiSquareCellD[][] cell;
	private JCheckBox ckExpected, ckChiDiff, ckRowPercent, ckColPercent;
	private JLabel lblRows, lblColumns;

	private boolean showColumnMargin;



	private JPanel wrappedPanel;

	/**
	 * @param app
	 * @param statCalc
	 */
	public ChiSquarePanelD(AppD app, StatisticsCalculator statCalc) {
		super(app, statCalc);
		createGUI();
		setLabels();

	}

	public void setLabels() {
		Localization loc = app.getLocalization();
		lblRows.setText(loc.getMenu("Rows"));
		lblColumns.setText(loc.getMenu("Columns"));
		ckExpected.setText(loc.getMenu("ExpectedCount"));
		ckChiDiff.setText(loc.getMenu("ChiSquaredContribution"));
		ckRowPercent.setText(loc.getMenu("RowPercent"));
		ckColPercent.setText(loc.getMenu("ColumnPercent"));

		if (statCalc.getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setLabelText(0, loc.getMenu("ObservedCount"));
			cell[0][2].setLabelText(0, loc.getMenu("ExpectedCount"));
		}

	}

	private void createGUI() {
		
		this.wrappedPanel = new JPanel();

		createGUIElements();
		createCountPanel();
		createControlPanel();

		JPanel p = new JPanel(new BorderLayout());
		p.add(pnlCount, BorderLayout.NORTH);
		p.setBackground(null);

		wrappedPanel.setLayout(new BorderLayout());
		wrappedPanel.add(pnlControl, BorderLayout.NORTH);
		wrappedPanel.add(p, BorderLayout.CENTER);
		wrappedPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

	}

	private void createGUIElements() {

		lblRows = new JLabel();
		lblColumns = new JLabel();

		ckExpected = new JCheckBox();
		ckChiDiff = new JCheckBox();
		ckRowPercent = new JCheckBox();
		ckColPercent = new JCheckBox();

		ckExpected.addActionListener(this);
		ckChiDiff.addActionListener(this);
		ckRowPercent.addActionListener(this);
		ckColPercent.addActionListener(this);

		// drop down menu for rows/columns 2-12
		String[] num = new String[11];
		for (int i = 0; i < num.length; i++) {
			num[i] = "" + (i + 2);
		}
		cbRows = new JComboBox(num);
		cbRows.setSelectedItem("" + sc.rows);
		cbRows.addActionListener(this);
		cbRows.setMaximumRowCount(12);

		cbColumns = new JComboBox(num);
		cbColumns.setSelectedItem("" + sc.columns);
		cbColumns.addActionListener(this);
		cbColumns.setMaximumRowCount(12);

	}

	private void createControlPanel() {

		pnlControl = new JPanel();
		pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.Y_AXIS));
		pnlControl.add(wrappedPanel.add(LayoutUtil.flowPanel(lblRows, cbRows, lblColumns,
				cbColumns)));
		pnlControl.add(wrappedPanel.add(LayoutUtil.flowPanel(ckRowPercent, ckColPercent,
				ckExpected, ckChiDiff)));
	}

	private void createCountPanel() {

		if (pnlCount == null) {
			pnlCount = new JPanel();
		}
		pnlCount.removeAll();
		pnlCount.setLayout(new BoxLayout(pnlCount, BoxLayout.Y_AXIS));

		cell = new ChiSquareCellD[sc.rows + 2][sc.columns + 2];

		// create grid of cells
		for (int r = 0; r < sc.rows + 2; r++) {
			JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			for (int c = 0; c < sc.columns + 2; c++) {
				cell[r][c] = new ChiSquareCellD(sc, r, c);
				cell[r][c].getInputField().addActionListener(this);
				cell[r][c].getInputField().addFocusListener(this);

				// wider fields for the GOF test
				if (statCalc.getSelectedProcedure() == Procedure.GOF_TEST) {
					cell[r][c].setColumns(10);
				}

				row.add(cell[r][c].getWrappedPanel());
			}
			pnlCount.add(row);
		}

		// upper-right corner cell
		cell[0][0].setMarginCell(true);

		// column headers and margins
		for (int c = 1; c < sc.columns + 2; c++) {
			cell[0][c].setHeaderCell(true);
			cell[sc.rows + 1][c].setMarginCell(true);
		}

		// row headers and margins
		for (int r = 1; r < sc.rows + 1; r++) {
			cell[r][0].setHeaderCell(true);
			cell[r][sc.columns + 1].setMarginCell(true);
		}

		// clear other corners
		cell[sc.rows + 1][0].hideAll();
		cell[0][sc.columns + 1].hideAll();

		if (statCalc.getSelectedProcedure() == Procedure.GOF_TEST) {
			cell[0][1].setMarginCell(true);
			cell[0][2].setMarginCell(true);
		}

	}

	// ==========================================
	// Event handlers
	// ==========================================

	public void updateGUI() {

		if (statCalc.getSelectedProcedure() == Procedure.CHISQ_TEST) {
			cbColumns.setVisible(true);
			lblColumns.setVisible(true);
			ckRowPercent.setVisible(true);
			ckExpected.setVisible(true);
			ckChiDiff.setVisible(true);

		} else if (statCalc.getSelectedProcedure() == Procedure.GOF_TEST) {
			cbColumns.setVisible(false);
			lblColumns.setVisible(false);
			ckRowPercent.setVisible(false);
			ckExpected.setVisible(false);
			ckChiDiff.setVisible(false);

			// only two columns for GOF
			cbColumns.removeActionListener(this);
			cbColumns.setSelectedItem("2");
			cbColumns.addActionListener(this);

		}

		sc.setChiSqData(Integer.parseInt((String) cbRows.getSelectedItem()),
				Integer.parseInt((String) cbColumns.getSelectedItem()));

		createCountPanel();
		setLabels();
		wrappedPanel.revalidate();
		wrappedPanel.repaint();
	}

	private void updateVisibility() {
		for (int i = 1; i < sc.rows + 1; i++) {
			for (int j = 1; j < sc.columns + 1; j++) {
				cell[i][j].setLabelVisible(1, ckExpected.isSelected());
				cell[i][j].setLabelVisible(2, ckChiDiff.isSelected());
				cell[i][j].setLabelVisible(3, ckRowPercent.isSelected());
				cell[i][j].setLabelVisible(4, ckColPercent.isSelected());
			}
		}

		// column percent for bottom margin
		for (int r = 0; r < sc.rows; r++) {
			cell[r + 1][sc.columns + 1].setLabelVisible(3,
					ckColPercent.isSelected());
		}

		// row percent for right margin
		for (int c = 0; c < sc.columns; c++) {
			cell[sc.rows + 1][c + 1].setLabelVisible(4,
					ckRowPercent.isSelected());
		}

		updateCellContent();
	}

	private void updateCellContent() {

		statProcessor.doCalculate();

		for (int r = 0; r < sc.rows; r++) {
			for (int c = 0; c < sc.columns; c++) {
				if (ckExpected.isSelected()) {
					cell[r + 1][c + 1].setLabelText(1,
							statCalc.format(sc.expected[r][c]));
				}
				if (ckChiDiff.isSelected()) {
					cell[r + 1][c + 1].setLabelText(2,
							statCalc.format(sc.diff[r][c]));
				}
				if (ckRowPercent.isSelected()) {
					cell[r + 1][c + 1].setLabelText(
							3,
							statCalc.format(100 * sc.observed[r][c]
									/ sc.rowSum[r]));
				}
				if (ckColPercent.isSelected()) {
					cell[r + 1][c + 1].setLabelText(
							4,
							statCalc.format(100 * sc.observed[r][c]
									/ sc.columnSum[c]));
				}
			}
		}

		// column margin
		if (showColumnMargin) {
			for (int r = 0; r < sc.rows; r++) {
				cell[r + 1][sc.columns + 1].setLabelText(0,
						statCalc.format(sc.rowSum[r]));
				if (ckRowPercent.isSelected()) {
					cell[r + 1][sc.columns + 1].setLabelText(3,
							statCalc.format(100 * sc.rowSum[r] / sc.total));
				}
			}
		}

		// bottom margin
		for (int c = 0; c < sc.columns; c++) {
			cell[sc.rows + 1][c + 1].setLabelText(0,
					statCalc.format(sc.columnSum[c]));

			if (ckColPercent.isSelected()) {
				cell[sc.rows + 1][c + 1].setLabelText(4,
						statCalc.format(100 * sc.columnSum[c] / sc.total));
			}

		}

		// bottom right corner
		if (showColumnMargin) {
			cell[sc.rows + 1][sc.columns + 1].setLabelText(0,
					statCalc.format(sc.total));
		}

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}
		if (source == cbRows || source == cbColumns) {
			updateGUI();
		}

		if (source == ckExpected || source == ckChiDiff
				|| source == ckRowPercent || source == ckColPercent) {
			updateVisibility();
		}

	}

	public void doTextFieldActionPerformed(JTextField source) {

		updateCellContent();
	}

	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof MyTextFieldD) {
			((MyTextFieldD) e.getSource()).selectAll();
		}

	}

	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof MyTextFieldD)
			doTextFieldActionPerformed((MyTextFieldD) e.getSource());

	}

	/*****************************************************************
	 * 
	 * Class ChiSquareCell: extended JPanel to hold cell components
	 * 
	 ***************************************************************** 
	 */
	public class ChiSquareCellD extends ChiSquareCell implements ActionListener,
			FocusListener {

		private static final long serialVersionUID = 1L;

		
		

		private JPanel wrappedPanel;
		
		private MyTextFieldD fldInput;
		private JLabel[] label;

		/**
		 * Construct ChiSquareCell with given row, column
		 */
		public ChiSquareCellD(StatisticsCollection sc, int row, int column) {
			this(sc);
			this.row = row;
			this.column = column;
		}

		/**
		 * Construct ChiSquareCell
		 */
		public ChiSquareCellD(StatisticsCollection sc) {

			this.sc = sc;
			this.wrappedPanel = new JPanel();
			wrappedPanel.setOpaque(true);
			wrappedPanel.setLayout(new BoxLayout(this.wrappedPanel, BoxLayout.Y_AXIS));

			fldInput = new MyTextFieldD((AppD) app);
			fldInput.addActionListener(this);
			fldInput.addFocusListener(this);
			wrappedPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, fldInput));

			label = new JLabel[5];
			for (int i = 0; i < label.length; i++) {
				label[i] = new JLabel();
				label[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				wrappedPanel.add(LayoutUtil.flowPanelCenter(0, 0, 0, label[i]));
			}
			setColumns(4);
			setVisualStyle();
			hideAllLabels();

		}

		public void setColumns(int columns) {
			fldInput.setColumns(columns);

			// force a minimum width for margin cells
			wrappedPanel.add(Box.createHorizontalStrut(fldInput.getPreferredSize().width));

		}

		/**
		 * hide all labels
		 */
		public void hideAllLabels() {
			for (int i = 0; i < label.length; i++) {
				label[i].setVisible(false);
			}
		}

		/**
		 * hide all
		 */
		public void hideAll() {
			hideAllLabels();
			fldInput.setVisible(false);
			wrappedPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		/**
		 * @return input field
		 */
		public MyTextFieldD getInputField() {
			return fldInput;
		}

		/**
		 * @return label array
		 */
		public JLabel[] getLabel() {
			return label;
		}

		public void setLabelText(int index, String s) {
			label[index].setText(s);
		}

		public void setLabelVisible(int index, boolean isVisible) {
			label[index].setVisible(isVisible);
		}

		public void setMarginCell(boolean isMarginCell) {
			this.isMarginCell = isMarginCell;
			setVisualStyle();
		}

		public void setHeaderCell(boolean isHeaderCell) {
			this.isHeaderCell = isHeaderCell;
			setVisualStyle();
		}

		private void setVisualStyle() {
			wrappedPanel.setBackground(null);
			fldInput.setVisible(false);

			if (isMarginCell) {
				wrappedPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				setLabelVisible(0, true);

			} else if (isHeaderCell) {
				wrappedPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
				fldInput.setVisible(true);
				fldInput.setBackground(GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));

			} else {
				fldInput.setVisible(true);
				wrappedPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				fldInput.setBackground(GColorD
						.getAwtColor(GColor.WHITE));
			}

		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		private void updateCellData() {
			sc.chiSquareData[row][column] = fldInput.getText();
		}

		public void focusGained(FocusEvent e) {
			if (e.getSource() instanceof MyTextFieldD) {
				((MyTextFieldD) e.getSource()).selectAll();
			}
		}

		public void focusLost(FocusEvent e) {
			updateCellData();
			statCalc.updateResult();
		}

		public void actionPerformed(ActionEvent e) {
			updateCellData();
			statCalc.updateResult();

		}
		
		public JPanel getWrappedPanel() {
			return wrappedPanel;
		}
		
		

	}

	
	
	/**
	 * @return the GUI wrapper panel
	 */
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

}
