package geogebra.gui.view.data;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.statistics.AlgoFrequencyTable;
import geogebra.common.main.App;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.data.DataAnalysisViewD.Regression;
import geogebra.gui.view.data.DataVariable.GroupType;
import geogebra.main.AppD;
import geogebra.main.LocalizationD;
import geogebra.util.Validation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayPanel extends JPanel implements ActionListener,
		FocusListener, StatPanelInterface {
	private static final long serialVersionUID = 1L;

	// ggb fields
	private AppD app;
	private final LocalizationD loc;
	private DataAnalysisViewD daView;
	private StatGeo statGeo;

	// data view mode
	private int mode;

	@SuppressWarnings("javadoc")
	// keys for these need to be in "menu" category of ggbtrans
	public enum PlotType {
		HISTOGRAM("Histogram"), BOXPLOT("Boxplot"), DOTPLOT("DotPlot"), NORMALQUANTILE(
				"NormalQuantilePlot"), STEMPLOT("StemPlot"), BARCHART(
				"BarChart"), SCATTERPLOT("Scatterplot"), RESIDUAL(
				"ResidualPlot"), MULTIBOXPLOT("StackedBoxPlots");

		/**
		 * the associated key from menu.properties app.getMenu(key) gives the
		 * translation (for the menu) in the current locale
		 */
		public String key;

		PlotType(String key) {
			this.key = key;

		}

		/**
		 * @param app
		 *            AppD
		 * @return translated key for the current locale eg "StemPlot" ->
		 *         "Stem and Leaf Diagram" in en_GB
		 */
		public String getTranslatedKey(AppD app) {
			return app.getMenu(key);
		}

	}

	// currently selected plot type
	private PlotType selectedPlot;

	private StatPanelSettings settings;

	private ArrayList<GeoElement> plotGeoList;

	private GeoElement[] boxPlotTitles;
	private GeoElement histogram, dotPlot, frequencyPolygon, normalCurve,
			scatterPlot, scatterPlotLine, residualPlot, nqPlot, boxPlot,
			barChart, freqTableGeo;

	// display panels
	private JPanel displayCardPanel;
	private JPanel metaPlotPanel, plotPanelNorth, plotPanelSouth;
	private PlotPanelEuclidianViewD plotPanel;

	private JLabel imageContainer;

	// control panel
	private JPanel controlPanel;
	private JPanel controlCards;
	private boolean hasControlPanel = true;
	private JComboBox cbDisplayType;

	// options button and sidebar panel
	private OptionsPanel optionsPanel;
	private JToggleButton btnOptions;

	// numClasses panel
	// private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses;

	// manual classes panel
	private JToolBar manualClassesPanel;
	private JLabel lblStart;
	private JLabel lblWidth;
	private AutoCompleteTextFieldD fldStart;
	private AutoCompleteTextFieldD fldWidth;

	// stemplot adjustment panel
	private JToolBar stemAdjustPanel;
	private JLabel lblAdjust;
	private JButton minus;
	private JButton none;
	private JButton plus;

	private JPanel imagePanel;

	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	private FrequencyTablePanel frequencyTable;
	private JToggleButton btnExport;
	private JTextField fldNumClasses;

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daView
	 *            daView
	 */
	public DataDisplayPanel(DataAnalysisViewD daView) {

		this.daView = daView;
		this.app = daView.getApp();
		this.loc = app.getLocalization();
		this.statGeo = daView.getStatGeo();
		plotGeoList = new ArrayList<GeoElement>();

		// create settings
		settings = new StatPanelSettings();
		settings.setDataSource(daView.getDataSource());

		// create the GUI
		createGUI();

	}

	/**
	 * Sets the plot to be displayed and the GUI corresponding to the given data
	 * analysis mode
	 * 
	 * @param plotIndex
	 *            the plot to be displayed
	 * @param mode
	 *            the data analysis mode
	 */
	public void setPanel(PlotType plotIndex, int mode) {

		this.mode = mode;
		this.selectedPlot = plotIndex;
		settings.setDataSource(daView.getDataSource());
		setLabels();
		updatePlot(true);
		optionsPanel.setVisible(false);
		btnOptions.setSelected(false);

	}

	// ==============================================
	// GUI
	// ==============================================

	private void createGUI() {

		// create options button
		btnOptions = new JToggleButton();
		// optionsButton.setIcon(app.getImageIcon("view-properties16.png"));
		btnOptions.setIcon(app.getImageIcon("inputhelp_left_18x18.png"));
		btnOptions.setSelectedIcon(app
				.getImageIcon("inputhelp_right_18x18.png"));
		btnOptions.setBorderPainted(false);
		btnOptions.setFocusPainted(false);
		btnOptions.setContentAreaFilled(false);
		// optionsButton.setPreferredSize(new
		// Dimension(optionsButton.getIcon().getIconWidth(),18));
		btnOptions.setMargin(new Insets(0, 0, 0, 0));
		btnOptions.addActionListener(this);

		// create export button
		btnExport = new JToggleButton();
		btnExport.setIcon(app.getImageIcon("export16.png"));
		// optionsButton.setSelectedIcon(app.getImageIcon("inputhelp_right_18x18.png"));
		btnExport.setBorderPainted(false);
		btnExport.setFocusPainted(false);
		btnExport.setContentAreaFilled(false);
		// btnExport.setPreferredSize(new
		// Dimension(btnExport.getIcon().getIconWidth(),18));
		btnExport.setMargin(new Insets(0, 0, 0, 0));
		btnExport.addActionListener(this);

		// create control panel
		if (hasControlPanel) {

			// create sub-control panels
			createDisplayTypeComboBox();
			createNumClassesPanel();
			createManualClassesPanel();
			createStemPlotAdjustmentPanel();
			JPanel emptyControl = new JPanel(new BorderLayout());
			emptyControl.add(new JLabel("  "));

			// put sub-control panels into a card layout
			controlCards = new JPanel(new CardLayout());
			controlCards.add("numClassesPanel", numClassesPanel);
			controlCards.add("manualClassesPanel", manualClassesPanel);
			controlCards.add("stemAdjustPanel", stemAdjustPanel);
			controlCards.add("blankPanel", emptyControl);

			// control panel
			controlPanel = new JPanel(new BorderLayout(0, 0));
			controlPanel.add(flowPanel(cbDisplayType), loc.borderWest());
			controlPanel.add(controlCards, BorderLayout.CENTER);
			controlPanel.add(flowPanelRight(btnOptions, btnExport),
					loc.borderEast());
		}

		plotPanel = new PlotPanelEuclidianViewD(app.getKernel(),
				exportToEVAction);

		plotPanelNorth = new JPanel();
		plotPanelSouth = new JPanel();
		Color bgColor = geogebra.awt.GColorD.getAwtColor(plotPanel
				.getBackgroundCommon());
		plotPanelNorth.setBackground(bgColor);
		plotPanelSouth.setBackground(bgColor);
		lblTitleX = new JLabel();
		lblTitleY = new JLabel();
		fldTitleX = new MyTextField(app, 20);
		fldTitleY = new MyTextField(app, 20);
		fldTitleX.setEditable(false);
		fldTitleX.setBorder(BorderFactory.createEmptyBorder());
		fldTitleY.setEditable(false);
		fldTitleY.setBorder(BorderFactory.createEmptyBorder());
		fldTitleX.setBackground(Color.white);
		fldTitleY.setBackground(Color.white);

		metaPlotPanel = new JPanel(new BorderLayout());
		metaPlotPanel.add(plotPanel.getJPanel(), BorderLayout.CENTER);

		createImagePanel();

		// put display panels into a card layout

		displayCardPanel = new JPanel(new CardLayout());
		displayCardPanel.setBackground(bgColor);

		displayCardPanel.add("plotPanel", metaPlotPanel);
		displayCardPanel.add("imagePanel", new JScrollPane(imagePanel));

		// create options panel
		optionsPanel = new OptionsPanel(app, daView, settings);
		optionsPanel.addPropertyChangeListener("settings",
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						updatePlot(true);
					}
				});
		optionsPanel.setVisible(false);

		frequencyTable = new FrequencyTablePanel(app, daView);

		// =======================================
		// put all the panels together

		JPanel mainPanel = new JPanel(new BorderLayout(0, 0));

		if (hasControlPanel) {
			mainPanel.add(controlPanel, BorderLayout.NORTH);
		}
		mainPanel.add(displayCardPanel, BorderLayout.CENTER);
		mainPanel.add(optionsPanel, loc.borderEast());

		this.setLayout(new BorderLayout(0, 0));
		this.add(mainPanel, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		controlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				SystemColor.controlShadow));

	}

	/**
	 * Sets the labels to the current language
	 */
	public void setLabels() {

		createDisplayTypeComboBox();
		sliderNumClasses.setToolTipText(loc.getMenu("Classes"));
		fldNumClasses.setToolTipText(loc.getMenu("Classes"));
		lblStart.setText(loc.getMenu("Start") + " ");
		lblWidth.setText(loc.getMenu("Width") + " ");
		if (mode == DataAnalysisViewD.MODE_REGRESSION) {
			lblTitleX.setText(loc.getMenu("Column.X") + ": ");
			lblTitleY.setText(loc.getMenu("Column.Y") + ": ");
		}
		lblAdjust.setText(loc.getMenu("Adjustment") + ": ");

		optionsPanel.setLabels();
		btnOptions.setToolTipText(loc.getMenu("Options"));

	}

	/**
	 * Creates the JComboBox that selects display type
	 */
	private void createDisplayTypeComboBox() {

		if (cbDisplayType == null) {
			cbDisplayType = new JComboBox();
			cbDisplayType.setRenderer(new MyRenderer(app));

		} else {
			cbDisplayType.removeActionListener(this);
			cbDisplayType.removeAllItems();
		}

		switch (mode) {

		case DataAnalysisViewD.MODE_ONEVAR:

			if (!daView.isNumericData()) {
				cbDisplayType.addItem(PlotType.BARCHART);
			}

			else if (settings.groupType() == GroupType.RAWDATA) {
				cbDisplayType.addItem(PlotType.HISTOGRAM);
				cbDisplayType.addItem(PlotType.BARCHART);
				cbDisplayType.addItem(PlotType.BOXPLOT);
				cbDisplayType.addItem(PlotType.DOTPLOT);
				cbDisplayType.addItem(PlotType.STEMPLOT);
				cbDisplayType.addItem(PlotType.NORMALQUANTILE);
			}

			else if (settings.groupType() == GroupType.FREQUENCY) {
				cbDisplayType.addItem(PlotType.HISTOGRAM);
				cbDisplayType.addItem(PlotType.BARCHART);
				cbDisplayType.addItem(PlotType.BOXPLOT);

			} else if (settings.groupType() == GroupType.CLASS) {
				cbDisplayType.addItem(PlotType.HISTOGRAM);
			}

			break;

		case DataAnalysisViewD.MODE_REGRESSION:
			cbDisplayType.addItem(PlotType.SCATTERPLOT);
			cbDisplayType.addItem(PlotType.RESIDUAL);
			break;

		case DataAnalysisViewD.MODE_MULTIVAR:
			cbDisplayType.addItem(PlotType.MULTIBOXPLOT);
			break;
		}

		cbDisplayType.setSelectedItem(selectedPlot);
		cbDisplayType.setFocusable(false);
		cbDisplayType.addActionListener(this);
		cbDisplayType.setMaximumRowCount(cbDisplayType.getItemCount());

	}

	/**
	 * Updates the plot panel. Adds/removes additional panels as needed for the
	 * current selected plot.
	 */
	private void updatePlotPanelLayout() {

		metaPlotPanel.removeAll();
		plotPanelSouth.removeAll();
		plotPanelNorth.removeAll();
		metaPlotPanel.add(plotPanel.getJPanel(), BorderLayout.CENTER);

		if (selectedPlot == PlotType.SCATTERPLOT) {
			plotPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
			plotPanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			plotPanelSouth.add(lblTitleX);
			plotPanelSouth.add(fldTitleX);
			plotPanelNorth.add(lblTitleY);
			plotPanelNorth.add(fldTitleY);

			metaPlotPanel.add(plotPanelNorth, BorderLayout.NORTH);
			metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
		}

		else if (selectedPlot == PlotType.HISTOGRAM
				|| selectedPlot == PlotType.BARCHART) {

			if (settings.showFrequencyTable) {
				plotPanelSouth.setLayout(new BorderLayout());
				plotPanelSouth.add(frequencyTable, BorderLayout.CENTER);
				metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
			}
		}

		metaPlotPanel.revalidate();
		plotPanelSouth.revalidate();
		plotPanelNorth.revalidate();

	}

	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel() {

		imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(BorderFactory.createEmptyBorder());
		imagePanel.setBackground(Color.WHITE);
		imageContainer = new JLabel();
		imagePanel.setAlignmentX(SwingConstants.CENTER);
		imagePanel.setAlignmentY(SwingConstants.CENTER);
		imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
		imagePanel.add(imageContainer, BorderLayout.CENTER);

	}

	/**
	 * Creates a control panel for adjusting the number of histogram classes
	 */
	private void createNumClassesPanel() {

		fldNumClasses = new JTextField("" + settings.numClasses);
		fldNumClasses.setEditable(false);
		fldNumClasses.setOpaque(true);
		fldNumClasses.setColumns(2);
		fldNumClasses.setHorizontalAlignment(SwingConstants.CENTER);
		fldNumClasses.setBackground(null);
		fldNumClasses.setBorder(BorderFactory.createEmptyBorder());
		fldNumClasses.setVisible(false);

		sliderNumClasses = new JSlider(SwingConstants.HORIZONTAL, 3, 20,
				settings.numClasses);
		Dimension d = sliderNumClasses.getPreferredSize();
		d.width = 80;
		sliderNumClasses.setPreferredSize(d);
		sliderNumClasses.setMinimumSize(new Dimension(50, d.height));

		sliderNumClasses.setMajorTickSpacing(1);
		sliderNumClasses.setSnapToTicks(true);
		sliderNumClasses.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				settings.numClasses = slider.getValue();
				fldNumClasses.setText(("" + settings.numClasses));
				updatePlot(true);
			}
		});

		sliderNumClasses.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				fldNumClasses.setVisible(true);
				fldNumClasses.revalidate();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				fldNumClasses.setVisible(false);
				fldNumClasses.revalidate();
			}
		});

		numClassesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		numClassesPanel.add(sliderNumClasses);
		// numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(fldNumClasses);

	}

	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel() {

		lblAdjust = new JLabel();
		minus = new JButton("-1");
		none = new JButton("0");
		plus = new JButton("+1");
		minus.addActionListener(this);
		none.addActionListener(this);
		plus.addActionListener(this);
		none.setSelected(true);
		stemAdjustPanel = new JToolBar();
		stemAdjustPanel.setFloatable(false);
		stemAdjustPanel.add(minus);
		stemAdjustPanel.add(none);
		stemAdjustPanel.add(plus);

	}

	/**
	 * Creates a control panel for manually setting classes
	 */
	private void createManualClassesPanel() {

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new AutoCompleteTextFieldD(4, app);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + (int) settings.classStart);
		fldStart.addFocusListener(this);

		fldWidth = new AutoCompleteTextFieldD(4, app);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + (int) settings.classWidth);
		fldWidth.addFocusListener(this);

		manualClassesPanel = new JToolBar();
		manualClassesPanel.setFloatable(false);
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(Box.createHorizontalStrut(4));
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);
	}

	public JPopupMenu getExportMenu() {
		return plotPanel.getContextMenu();
	}

	// ==============================================
	// DISPLAY UPDATE
	// ==============================================

	/**
	 * Update the plot.
	 * 
	 * @param doCreate
	 *            if true then the plot GeoElements are redefined
	 */
	public void updatePlot(boolean doCreate) {

		GeoList dataListSelected = daView.getController().getDataSelected();

		if (hasControlPanel)
			((CardLayout) controlCards.getLayout()).show(controlCards,
					"blankPanel");

		if (doCreate) {
			clearPlotGeoList();
		}

		btnOptions.setVisible(true);
		updatePlotPanelLayout();

		// if invalid data, show blank plot and exit
		if (!daView.getController().isValidData()) {
			showInvalidDataDisplay();
			return;
		}

		try {
			switch (selectedPlot) {

			case HISTOGRAM:

				if (doCreate) {
					if (histogram != null) {
						histogram.remove();
					}
					histogram = statGeo.createHistogram(dataListSelected,
							settings, false);
					plotGeoList.add(histogram);

					if (frequencyPolygon != null) {
						frequencyPolygon.remove();
					}
					if (settings.hasOverlayPolygon) {
						frequencyPolygon = statGeo.createHistogram(
								dataListSelected, settings, true);
						plotGeoList.add(frequencyPolygon);
					}

					if (normalCurve != null) {
						normalCurve.remove();
					}
					if (settings.hasOverlayNormal) {
						normalCurve = statGeo
								.createNormalCurveOverlay(dataListSelected);
						plotGeoList.add(normalCurve);
					}

					if (freqTableGeo != null) {
						freqTableGeo.remove();
					}
					if (settings.showFrequencyTable) {
						freqTableGeo = statGeo.createFrequencyTableGeo(
								(GeoNumeric) histogram, selectedPlot);
						plotGeoList.add(freqTableGeo);
					}
				}

				// update the frequency table
				if (settings.showFrequencyTable) {
					frequencyTable.setTableFromGeoFrequencyTable(
							(AlgoFrequencyTable) freqTableGeo
									.getParentAlgorithm(), true);
				}

				// update settings
				statGeo.getHistogramSettings(dataListSelected, histogram,
						settings);
				plotPanel.updateSettings(settings);

				if (hasControlPanel
						&& settings.getDataSource().getGroupType() != GroupType.CLASS)
					if (settings.useManualClasses)
						((CardLayout) controlCards.getLayout()).show(
								controlCards, "manualClassesPanel");
					else
						((CardLayout) controlCards.getLayout()).show(
								controlCards, "numClassesPanel");

				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case BOXPLOT:
				if (doCreate) {
					if (boxPlot != null)
						boxPlot.remove();
					boxPlot = statGeo.createBoxPlot(dataListSelected, settings);
					plotGeoList.add(boxPlot);
				}
				statGeo.getBoxPlotSettings(dataListSelected, settings);
				plotPanel.updateSettings(settings);
				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case BARCHART:
				if (doCreate) {
					if (barChart != null) {
						barChart.remove();
					}
					if (settings.isNumericData()) {
						barChart = statGeo.createBarChartNumeric(
								dataListSelected, settings);
						plotGeoList.add(barChart);
					} else {
						barChart = statGeo.createBarChartText(dataListSelected,
								settings);
					}
					plotGeoList.add(barChart);

					if (freqTableGeo != null) {
						freqTableGeo.remove();
					}
					if (settings.showFrequencyTable) {
						freqTableGeo = statGeo.createFrequencyTableGeo(
								(GeoNumeric) barChart, selectedPlot);
						plotGeoList.add(freqTableGeo);
					}
				}

				// update the frequency table
				if (settings.showFrequencyTable) {
					frequencyTable.setTableFromGeoFrequencyTable(
							(AlgoFrequencyTable) freqTableGeo
									.getParentAlgorithm(), false);
				}

				// update settings
				statGeo.getBarChartSettings(dataListSelected, settings,
						barChart);
				plotPanel.updateSettings(settings);
				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case DOTPLOT:
				if (doCreate) {
					if (dotPlot != null)
						dotPlot.remove();
					dotPlot = statGeo.createDotPlot(dataListSelected);
					plotGeoList.add(dotPlot);
				}

				statGeo.updateDotPlot(dataListSelected, dotPlot, settings);
				plotPanel.updateSettings(settings);
				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case STEMPLOT:
				String latex = statGeo.getStemPlotLatex(dataListSelected,
						settings.stemAdjust);
				imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, latex,
						app.getPlainFont(), true, Color.BLACK, null));
				btnOptions.setVisible(false);
				if (hasControlPanel)
					((CardLayout) controlCards.getLayout()).show(controlCards,
							"stemAdjustPanel");

				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "imagePanel");
				break;

			case NORMALQUANTILE:
				if (doCreate) {
					if (nqPlot != null)
						nqPlot.remove();
					nqPlot = statGeo.createNormalQuantilePlot(dataListSelected);
					plotGeoList.add(nqPlot);
				}
				statGeo.updateNormalQuantilePlot(dataListSelected, settings);
				plotPanel.updateSettings(settings);
				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case SCATTERPLOT:
				if (doCreate) {
					scatterPlot = statGeo.createScatterPlot(dataListSelected);
					plotGeoList.add(scatterPlot);

					if (daView.getRegressionModel() != null
							&& !daView.getRegressionMode().equals(
									Regression.NONE)) {
						plotGeoList.add(daView.getRegressionModel());
					}

					if (settings.showScatterplotLine) {
						scatterPlotLine = statGeo
								.createScatterPlotLine((GeoList) scatterPlot);
						plotGeoList.add(scatterPlotLine);
					}
				}

				// update xy title fields

				if (settings.isPointList()) {
					fldTitleX.setText(daView.getDataTitles()[0]);
					fldTitleY.setText(daView.getDataTitles()[0]);
				} else {
					if (daView.getDaCtrl().isLeftToRight()) {
						fldTitleX.setText(daView.getDataTitles()[0]);
						fldTitleY.setText(daView.getDataTitles()[1]);
					} else {
						fldTitleX.setText(daView.getDataTitles()[1]);
						fldTitleY.setText(daView.getDataTitles()[0]);
					}
				}

				// update settings
				statGeo.getScatterPlotSettings(dataListSelected, settings);
				plotPanel.updateSettings(settings);

				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");

				break;

			case RESIDUAL:
				if (doCreate) {
					if (!daView.getRegressionMode().equals(Regression.NONE)) {
						residualPlot = statGeo.createRegressionPlot(
								dataListSelected, daView.getRegressionMode(),
								daView.getRegressionOrder(), true);
						plotGeoList.add(residualPlot);
						statGeo.getResidualPlotSettings(dataListSelected,
								residualPlot, settings);
						plotPanel.updateSettings(settings);
					} else if (residualPlot != null) {
						residualPlot.remove();
						residualPlot = null;
					}
				}

				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			case MULTIBOXPLOT:
				if (doCreate) {
					GeoElement[] boxPlots = statGeo.createMultipleBoxPlot(
							dataListSelected, settings);
					for (int i = 0; i < boxPlots.length; i++)
						plotGeoList.add(boxPlots[i]);
				}

				statGeo.getMultipleBoxPlotSettings(dataListSelected, settings);
				plotPanel.updateSettings(settings);
				boxPlotTitles = statGeo.createBoxPlotTitles(daView, settings);
				for (int i = 0; i < boxPlotTitles.length; i++)
					plotGeoList.add(boxPlotTitles[i]);

				((CardLayout) displayCardPanel.getLayout()).show(
						displayCardPanel, "plotPanel");
				break;

			default:

			}

			// ==============================================
			// Prepare Geos for plot panel view
			// ==============================================

			if (doCreate && statGeo.removeFromConstruction()) {
				for (GeoElement listGeo : plotGeoList) {
					// add the geo to our view and remove it from EV
					listGeo.addView(plotPanel.getViewID());
					plotPanel.add(listGeo);
					listGeo.removeView(App.VIEW_EUCLIDIAN);
					app.getEuclidianView1().remove(listGeo);
				}
			}

			if (freqTableGeo != null) {
				freqTableGeo.setEuclidianVisible(false);
				freqTableGeo.updateRepaint();
			}

			if (histogram != null) {
				histogram.setEuclidianVisible(settings.showHistogram);
				histogram.updateRepaint();
			}

		} catch (Exception e) {
			daView.getDaCtrl().setValidData(false);
			showInvalidDataDisplay();
			e.printStackTrace();
		}

	}

	private void showInvalidDataDisplay() {
		imageContainer.setIcon(null);
		((CardLayout) displayCardPanel.getLayout()).show(displayCardPanel,
				"imagePanel");
	}

	// ============================================================
	// Event Handlers
	// ============================================================

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source instanceof JTextField)
			doTextFieldActionPerformed(source);

		else if (source == minus || source == plus || source == none) {
			minus.setSelected(source == minus);
			none.setSelected(source == none);
			plus.setSelected(source == plus);
			if (source == minus)
				settings.stemAdjust = -1;
			if (source == none)
				settings.stemAdjust = 0;
			if (source == plus)
				settings.stemAdjust = 1;
			updatePlot(true);
		}

		else if (source == btnOptions) {
			optionsPanel.setPanel(selectedPlot);
			optionsPanel.setVisible(btnOptions.isSelected());
		}

		else if (source == btnExport) {
			JPopupMenu menu = plotPanel.getContextMenu();
			menu.show(btnExport,
					-menu.getPreferredSize().width + btnExport.getWidth(),
					btnExport.getHeight());
		}

		else if (source == cbDisplayType) {
			if (cbDisplayType.getSelectedItem().equals(MyRenderer.SEPARATOR)) {
				cbDisplayType.setSelectedItem(selectedPlot);
			} else {
				selectedPlot = (PlotType) cbDisplayType.getSelectedItem();
				updatePlot(true);
			}

			if (optionsPanel.isVisible()) {
				optionsPanel.setPanel(selectedPlot);
			}
		}

	}

	private void doTextFieldActionPerformed(Object source) {

		if (source == fldStart) {
			settings.classStart = Validation.validateDouble(fldStart,
					settings.classStart);
		} else if (source == fldWidth) {
			settings.classWidth = Validation.validateDoublePositive(fldWidth,
					settings.classWidth);
		}
		updatePlot(true);
	}

	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField)
			this.doTextFieldActionPerformed(source);
	}

	public void focusGained(FocusEvent e) {
		//
	}

	public void clearPlotGeoList() {
		for (GeoElement geo : plotGeoList) {
			if (geo != null) {
				geo.remove();
				geo = null;
			}
		}
		plotGeoList.clear();
	}

	public void removeGeos() {
		clearPlotGeoList();
	}

	public void detachView() {
		// plotPanel.detachView();
	}

	
	public void attachView() {
		plotPanel.attachView();

	}

	// ============================================================
	// Utilities
	// ============================================================

	private static JPanel flowPanel(JComponent... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

	private static JPanel flowPanelRight(JComponent... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

	public void updateFonts(Font font) {
		plotPanel.updateFonts();
	}

	public void updatePanel() {
		//
	}

	// ============================================================
	// ComboBox Renderer with SEPARATOR
	// ============================================================

	private static class MyRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public static final String SEPARATOR = "SEPARATOR";
		JSeparator separator;

		private AppD app;

		public MyRenderer(AppD app) {
			this.app = app;
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = "";
			if (value instanceof PlotType) {
				str = app.getMenu(((PlotType) value).key);
			} else {
				App.error("wrong class");
			}
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}

	// **********************************************
	// Export
	// **********************************************

	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 * 
	 * This action is passed as a parameter to plotPanel where it is used in the
	 * plotPanel context menu and the EuclidianView transfer handler when the
	 * plot panel is dragged into an EV.
	 */
	AbstractAction exportToEVAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent event) {
			Integer euclidianViewID = (Integer) this
					.getValue("euclidianViewID");

			// if null ID then use EV1 unless shift is down, then use EV2
			if (euclidianViewID == null) {
				euclidianViewID = AppD.getShiftDown() ? app.getEuclidianView2()
						.getViewID() : app.getEuclidianView1().getViewID();
			}

			// do the export
			exportGeosToEV(euclidianViewID);

			// null out the ID property
			this.putValue("euclidianViewID", null);
		}
	};

	/**
	 * Exports all GeoElements that are currently displayed in this panel to a
	 * target EuclidianView.
	 * 
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	public void exportGeosToEV(int euclidianViewID) {

		// TODO:
		// in multivar mode create dynamic boxplots linked to separate lists

		app.setWaitCursor();
		// app.storeUndoInfo();
		GeoElement regressionCopy = null;
		EuclidianView targetEV = (EuclidianView) app.getView(euclidianViewID);

		try {

			// =================================================================
			// Step 1:
			// Update the plot geos with the reomoveFromConstruction
			// flag set to false. This ensures that the display geos have been
			// put in the construction list and will be saved to xml.
			// =================================================================
			daView.getController().loadDataLists(false); // load actual data
															// lists
			statGeo.setRemoveFromConstruction(false);
			updatePlot(true);

			// =================================================================
			// Step 2:
			// Prepare the geos for display in the currently active EV
			// (set labels, make visible, etc).
			// =================================================================

			// remove the histogram from the plot geo list if it is not showing
			if (histogram != null && settings.showHistogram == false) {
				plotGeoList.remove(histogram);
				histogram.remove();
				histogram = null;
			}

			// prepare all display geos to appear in the EV
			for (GeoElement geo : plotGeoList) {
				prepareGeoForEV(geo, euclidianViewID);
			}

			// the regression geo is maintained by the da view, so we create a
			// copy and prepare this for the EV
			if (daView.getMode() == DataAnalysisViewD.MODE_REGRESSION
					&& !daView.getRegressionMode().equals(Regression.NONE)) {

				regressionCopy = statGeo.createRegressionPlot(
						(GeoList) scatterPlot, daView.getRegressionMode(),
						daView.getRegressionOrder(), false);
				prepareGeoForEV(regressionCopy, euclidianViewID);
			}

			// =================================================================
			// Step 3:
			// Adjust the target EV window to match the plotPanel dimensions
			// =================================================================

			targetEV.setRealWorldCoordSystem(settings.xMin, settings.xMax,
					settings.yMin, settings.yMax);
			targetEV.setAutomaticAxesNumberingDistance(
					settings.xAxesIntervalAuto, 0);
			targetEV.setAutomaticAxesNumberingDistance(
					settings.yAxesIntervalAuto, 1);
			if (!settings.xAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(settings.xAxesInterval, 0);
			}
			if (!settings.yAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(settings.yAxesInterval, 1);
			}
			targetEV.updateBackground();

			// =================================================================
			// Step 4:
			// Dereference the geos from fields in this class and the
			// DataAnalysisView
			// =================================================================

			// null the display geos
			boxPlotTitles = null;
			histogram = null;
			dotPlot = null;
			frequencyPolygon = null;
			normalCurve = null;
			scatterPlotLine = null;
			scatterPlot = null;
			nqPlot = null;
			boxPlot = null;
			barChart = null;
			freqTableGeo = null;

			daView.getController().removeRegressionGeo();
			daView.getController().disposeDataListSelected();
			plotGeoList.clear();

			// =================================================================
			// Step 5:
			// Reload the data and create new display geos that are not
			// in the construction list.
			// =================================================================

			daView.getController().loadDataLists(true);
			statGeo.setRemoveFromConstruction(true);
			daView.getController().setRegressionGeo();
			updatePlot(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			app.setDefaultCursor();
		}

		app.setDefaultCursor();
		app.storeUndoInfo();
	}

	/**
	 * Prepares the specified GeoElement for visibility in a target
	 * EuclidianView.
	 * 
	 * @param geo
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	private static void prepareGeoForEV(GeoElement geo, int euclidianViewID) {

		geo.setLabel(null);
		geo.setEuclidianVisible(true);
		geo.setAuxiliaryObject(false);
		if (euclidianViewID == App.VIEW_EUCLIDIAN) {
			geo.addView(App.VIEW_EUCLIDIAN);
			geo.removeView(App.VIEW_EUCLIDIAN2);
			geo.update();
		}
		if (euclidianViewID == App.VIEW_EUCLIDIAN2) {
			geo.addView(App.VIEW_EUCLIDIAN2);
			geo.removeView(App.VIEW_EUCLIDIAN);
			geo.update();
		}

	}

}
