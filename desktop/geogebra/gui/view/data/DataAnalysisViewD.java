package geogebra.gui.view.data;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.util.debug.Log;
import geogebra.export.PrintPreview;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.view.data.DataVariable.GroupType;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 * View to display plots and statistical analysis of data.
 * 
 * @author G. Sturr
 * 
 */
public class DataAnalysisViewD extends JPanel implements View, Printable,
		SetLabels {

	private static final long serialVersionUID = 1L;

	// ggb
	private AppD app;
	private Kernel kernel;
	private StatGeo statGeo;
	protected DataAnalysisControllerD daCtrl;
	private DataAnalysisStyleBar stylebar;

	public static final int MODE_ONEVAR = EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS;
	public static final int MODE_REGRESSION = EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS;
	public static final int MODE_MULTIVAR = EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS;
	private int mode = -1;

	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showDataDisplayPanel2 = false;
	protected boolean isIniting = true;

	// colors
	public static final Color TABLE_GRID_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR);
	public static final Color TABLE_HEADER_COLOR = new Color(240, 240, 240);
	public static final Color HISTOGRAM_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.BLUE);
	public static final Color BOXPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.CRIMSON);
	public static final Color BARCHART_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.DARKGREEN);

	public static final Color DOTPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color NQPLOT_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.GRAY5);
	public static final Color REGRESSION_COLOR = Color.RED;
	public static final Color OVERLAY_COLOR = geogebra.awt.GColorD
			.getAwtColor(GeoGebraColorConstants.DARKBLUE);

	public static final float opacityBarChart = 0.3f;
	public static final int thicknessCurve = 4;
	public static final int thicknessBarChart = 3;

	/**
	 * @author mrb
	 * 
	 *         Order determines order in Two Variable Regression Analysis menu
	 *         For each String, getMenu(s) must be defined
	 */
	public enum Regression {
		NONE("None"), LINEAR("Linear"), LOG("Log"), POLY("Polynomial"), POW(
				"Power"), EXP("Exponential"), GROWTH("Growth"), SIN("Sin"), LOGISTIC(
				"Logistic");

		// getMenu(label) must be defined
		private String label;

		Regression(String s) {
			this.label = s;
		}

		public String getLabel() {
			return label;
		}
	}

	// rounding constants for local number format
	private int printDecimals = 4, printFigures = -1;

	// public static final int regressionTypes = 9;
	private Regression regressionMode = Regression.NONE;
	private int regressionOrder = 2;

	// main GUI panels
	private DataPanel dataPanel;
	private StatisticsPanel statisticsPanel;
	private RegressionPanel regressionPanel;
	private DataDisplayPanel dataDisplayPanel1, dataDisplayPanel2;

	private JSplitPane statDataPanel, displayPanel, comboPanelSplit;
	private DataSourcePanel dataSourcePanel;
	private JPanel mainPanel;

	private int defaultDividerSize;

	final static String MainCard = "Card with main panel";
	final static String SourceCard = "Card with data type options";

	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisViewD(AppD app, int mode) {

		isIniting = true;
		this.app = app;
		this.kernel = app.getKernel();

		daCtrl = new DataAnalysisControllerD(app, this);

		dataDisplayPanel1 = new DataDisplayPanel(this);
		dataDisplayPanel2 = new DataDisplayPanel(this);

		setView(null, mode, true);
		isIniting = false;

	}

	/*************************************************
	 * END constructor
	 */

	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {

		daCtrl.setDataSource(dataSource);

		if (dataSource == null) {
			daCtrl.setValidData(false);
		} else {
			daCtrl.setValidData(true);
		}

		if (mode == MODE_ONEVAR) {
			if (showDataPanel == true
					&& dataSource.getGroupType() != GroupType.RAWDATA) {
				setShowDataPanel(false);
			}
		}

		// reinit the GUI if mode is changed
		if (this.mode != mode || forceModeUpdate) {

			this.mode = mode;
			dataPanel = null;
			buildStatisticsPanel();
			daCtrl.updateDataLists();
			setDataPlotPanels();
			updateLayout();

			// TODO: why do this here?
			daCtrl.updateDataAnalysisView();

		} else {
			// just update data source
			daCtrl.updateDataAnalysisView();
		}

		// TODO is this needed?
		daCtrl.setLeftToRight(true);

		updateFonts();
		setLabels();
		updateGUI();

		revalidate();

	}

	public JComponent getStyleBar() {
		if (stylebar == null) {
			stylebar = new DataAnalysisStyleBar(app, this);
		}
		return stylebar;
	}

	private void createGUI() {

		buildStatisticsPanel();

	}

	private void buildStatisticsPanel() {
		if (statisticsPanel != null) {
			// TODO handle any orphaned geo children of stat panel
			statisticsPanel = null;
		}

		statisticsPanel = new StatisticsPanel(app, this);
		statisticsPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2));
	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels() {

		switch (mode) {

		case MODE_ONEVAR:
			if (!isNumericData()) {
				dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.BARCHART,
						mode);
				dataDisplayPanel2.setPanel(DataDisplayPanel.PlotType.BARCHART,
						mode);

			} else if (groupType() == GroupType.RAWDATA) {
				dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.HISTOGRAM,
						mode);
				dataDisplayPanel2.setPanel(DataDisplayPanel.PlotType.BOXPLOT,
						mode);

			} else if (groupType() == GroupType.FREQUENCY) {
				dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.BARCHART,
						mode);
				dataDisplayPanel2.setPanel(DataDisplayPanel.PlotType.BOXPLOT,
						mode);

			} else if (groupType() == GroupType.CLASS) {
				dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.HISTOGRAM,
						mode);
				dataDisplayPanel2.setPanel(DataDisplayPanel.PlotType.HISTOGRAM,
						mode);
			}
			break;

		case MODE_REGRESSION:
			dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.SCATTERPLOT,
					mode);
			dataDisplayPanel2
					.setPanel(DataDisplayPanel.PlotType.RESIDUAL, mode);
			break;

		case MODE_MULTIVAR:
			dataDisplayPanel1.setPanel(DataDisplayPanel.PlotType.MULTIBOXPLOT,
					mode);
			showDataDisplayPanel2 = false;
			break;
		}
	}

	// Create DataPanel to display the current data set(s) and allow
	// temporary editing.
	protected DataPanel buildDataPanel() {

		if (dataPanel != null) {
			// TODO handle any orphaned data panel geos
			dataPanel = null;
		}
		if (mode != MODE_MULTIVAR) {
			dataPanel = new DataPanel(app, this);
			dataPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}

		return dataPanel;

	}

	public void loadDataTable(ArrayList<GeoElement> dataArray) {
		if (dataPanel == null) {
			buildDataPanel();
		}
		dataPanel.loadDataTable(dataArray);
	}

	protected DataPanel getDataPanel() {
		return dataPanel;
	}

	// =================================================
	// GUI
	// =================================================

	private void updateLayout() {

		this.removeAll();

		// ===========================================
		// statData panel

		if (mode != MODE_MULTIVAR) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setResizeWeight(0.5);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		if (mode == MODE_MULTIVAR) {
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statisticsPanel, null);
			statDataPanel.setDividerSize(0);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		// ===========================================
		// regression panel

		if (mode == MODE_REGRESSION) {
			regressionPanel = new RegressionPanel(app, this);
		}

		// ===========================================
		// plotComboPanel panel

		// create a splitPane to hold the two plotComboPanels
		comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				dataDisplayPanel1, dataDisplayPanel2);

		comboPanelSplit.setDividerLocation(0.5);
		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());

		// grab the default divider size
		defaultDividerSize = comboPanelSplit.getDividerSize();

		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);

		// display panel
		// ============================================
		if (mode != MODE_MULTIVAR) {
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statDataPanel, plotComboPanel);
			displayPanel.setResizeWeight(0.5);
		} else {
			displayPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					plotComboPanel, statDataPanel);
			displayPanel.setResizeWeight(1);

		}
		displayPanel.setBorder(BorderFactory.createEmptyBorder());

		// main panel
		// ============================================
		mainPanel = new JPanel(new BorderLayout());
		// mainPanel.add(getStyleBar(), BorderLayout.NORTH);
		mainPanel.add(displayPanel, BorderLayout.CENTER);

		if (mode == MODE_REGRESSION) {
			mainPanel.add(regressionPanel, BorderLayout.SOUTH);
		}

		// dataTypePanel = new DataViewSettingsPanel(app,
		// StatDialog.MODE_ONEVAR);
		JPanel p = new JPanel(new FullWidthLayout());
		// p.add(dataTypePanel);

		this.setLayout(new CardLayout());
		add(mainPanel, MainCard);
		add(p, SourceCard);
		showMainPanel();

		setShowComboPanel2(showDataDisplayPanel2);
		updateStatDataPanelVisibility();

	}

	public void showSourcePanel() {
		CardLayout c = (CardLayout) this.getLayout();
		c.show(this, SourceCard);
	}

	public void showMainPanel() {
		CardLayout c = (CardLayout) this.getLayout();
		c.show(this, MainCard);

	}

	// ======================================
	// Getters/setters
	// ======================================

	public DataAnalysisControllerD getDaCtrl() {
		return daCtrl;
	}

	public DataSource getDataSource() {
		return daCtrl.getDataSource();
	}

	public GroupType groupType() {
		return daCtrl.getDataSource().getGroupType();
	}

	public DataDisplayPanel getDataDisplayPanel1() {
		return dataDisplayPanel1;
	}

	public DataDisplayPanel getDataDisplayPanel2() {
		return dataDisplayPanel2;
	}

	public RegressionPanel getRegressionPanel() {
		return regressionPanel;
	}

	public StatisticsPanel getStatisticsPanel() {
		return statisticsPanel;
	}

	/**
	 * Component representation of this view
	 * 
	 * @return reference to self
	 */
	public JComponent getDataAnalysisViewComponent() {
		return this;
	}

	public boolean showDataDisplayPanel2() {
		return showDataDisplayPanel2;
	}

	public boolean showDataPanel() {
		return showDataPanel;
	}

	public void setShowDataPanel(boolean isVisible) {
		if (showDataPanel == isVisible) {
			return;
		}
		showDataPanel = isVisible;
		updateStatDataPanelVisibility();
	}

	public void setShowStatistics(boolean isVisible) {
		if (showStatPanel == isVisible) {
			return;
		}
		showStatPanel = isVisible;
		updateStatDataPanelVisibility();
	}

	public boolean showStatPanel() {
		return showStatPanel;
	}

	public DataAnalysisControllerD getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public StatGeo getStatGeo() {
		if (statGeo == null)
			statGeo = new StatGeo(app);
		return statGeo;
	}

	public int getRegressionOrder() {
		return regressionOrder;
	}

	public void setRegressionMode(int regressionMode) {

		for (Regression l : Regression.values()) {
			if (l.ordinal() == regressionMode) {
				this.regressionMode = l;

				daCtrl.setRegressionGeo();
				daCtrl.updateAllPanels(true);

				return;
			}
		}

		Log.warn("no mode set in setRegressionMode()");
		this.regressionMode = Regression.NONE;

	}

	public Regression getRegressionMode() {
		return regressionMode;
	}

	public void setRegressionOrder(int regressionOrder) {
		this.regressionOrder = regressionOrder;
	}

	public AppD getApp() {
		return app;
	}

	public int getMode() {
		return mode;
	}

	public void setShowDataOptionsDialog(boolean showDialog) {
		// if (showDialog) {
		// showSourcePanel();
		// this.dataTypePanel;
		// } else {
		// showMainPanel();
		// }

		app.getDialogManager().showDataSourceDialog(mode, false);

	}

	public boolean isNumericData() {
		if (daCtrl.getDataSource() == null) {
			return false;
		}
		return daCtrl.getDataSource().isNumericData();
	}

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	public void setShowComboPanel2(boolean showComboPanel2) {

		this.showDataDisplayPanel2 = showComboPanel2;

		if (showComboPanel2) {
			if (comboPanelSplit == null) {
				// Application.debug("splitpane null");
			}
			comboPanelSplit.setBottomComponent(dataDisplayPanel2);
			comboPanelSplit.setDividerLocation(200);
			comboPanelSplit.setDividerSize(4);
		} else {
			comboPanelSplit.setBottomComponent(null);
			comboPanelSplit.setLastDividerLocation(comboPanelSplit
					.getDividerLocation());
			comboPanelSplit.setDividerLocation(0);
			comboPanelSplit.setDividerSize(0);
		}

	}

	public void updateStatDataPanelVisibility() {

		if (statDataPanel == null)
			return;

		if (mode != MODE_MULTIVAR) {

			if (showDataPanel) {
				if (statDataPanel.getRightComponent() == null) {
					statDataPanel.setRightComponent(dataPanel);
					statDataPanel.resetToPreferredSizes();
				}
			} else {
				if (statDataPanel.getRightComponent() != null) {
					statDataPanel.setRightComponent(null);
					statDataPanel.resetToPreferredSizes();
				}
			}

			if (showStatPanel) {
				if (statDataPanel.getLeftComponent() == null) {
					statDataPanel.setLeftComponent(statisticsPanel);
					statDataPanel.resetToPreferredSizes();
				}
			} else {
				if (statDataPanel.getLeftComponent() != null) {
					statDataPanel.setLeftComponent(null);
					statDataPanel.resetToPreferredSizes();
				}
			}

			// hide/show divider
			if (showDataPanel && showStatPanel)
				statDataPanel.setDividerSize(defaultDividerSize);
			else
				statDataPanel.setDividerSize(0);

			// hide/show statData panel
			if (showDataPanel || showStatPanel) {
				if (displayPanel.getLeftComponent() == null) {
					displayPanel.setLeftComponent(statDataPanel);
					// displayPanel.resetToPreferredSizes();
					displayPanel.setDividerLocation(displayPanel
							.getLastDividerLocation());
					displayPanel.setDividerSize(defaultDividerSize);
				}

			} else { // statData panel is empty, so hide it
				displayPanel.setLastDividerLocation(displayPanel
						.getDividerLocation());
				displayPanel.setLeftComponent(null);
				displayPanel.setDividerSize(0);
			}

		} else { // handle multi-variable case

			if (showStatPanel) {
				if (displayPanel.getBottomComponent() == null) {
					displayPanel.setBottomComponent(statDataPanel);
					// displayPanel.resetToPreferredSizes();
					displayPanel.setDividerLocation(displayPanel
							.getLastDividerLocation());
					displayPanel.setDividerSize(defaultDividerSize);
				}
			} else {
				displayPanel.setLastDividerLocation(displayPanel
						.getDividerLocation());
				displayPanel.setBottomComponent(null);
				displayPanel.setDividerSize(0);

			}

		}

		setLabels();
		updateFonts();

		displayPanel.resetToPreferredSizes();
	}

	public void doPrint() {
		List<Printable> l = new ArrayList<Printable>();
		l.add(this);
		PrintPreview.get(app, App.VIEW_DATA_ANALYSIS, PageFormat.LANDSCAPE)
				.setVisible(true);
	}

	// =================================================
	// Event Handlers and Updates
	// =================================================

	public void updateGUI() {

		if (stylebar != null) {
			stylebar.updateGUI();
		}
		revalidate();
		repaint();
	}

	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this, font);

	}

	public void setFontRecursive(Container c, Font font) {
		Component[] components = c.getComponents();
		for (Component com : components) {
			com.setFont(font);
			if (com instanceof StatPanelInterface) {
				((StatPanelInterface) com).updateFonts(font);
			}
			if (com instanceof Container)
				setFontRecursive((Container) com, font);
		}
	}

	public void setLabels() {

		if (isIniting) {
			return;
		}

		// setTitle(app.getMenu("OneVariableStatistics"));

		if (mode == MODE_REGRESSION && regressionPanel != null) {
			regressionPanel.setLabels();
		}

		if (stylebar != null) {
			stylebar.setLabels();
		}

		// call setLabels() for all child panels
		setLabelsRecursive(this);

	}

	public void setLabelsRecursive(Container c) {

		Component[] components = c.getComponents();
		for (Component com : components) {
			if (com instanceof StatPanelInterface) {
				// System.out.println(c.getClass().getSimpleName());
				((StatPanelInterface) com).setLabels();
			} else if (com instanceof Container)
				setLabelsRecursive((Container) com);
		}
	}

	// =================================================
	// Number Format
	//
	// (use GeoGebra rounding settings unless decimals < 4)
	// =================================================

	/**
	 * Converts a double numeric value to formatted String
	 * 
	 * @param x
	 *            number to be converted
	 * @return formatted number string
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		// override the default decimal place setting if less than 4 decimals
		if (printDecimals >= 0) {
			int d = printDecimals < 4 ? 4 : printDecimals;
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					d, false);
		} else {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					printFigures, false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	/**
	 * Adjust local rounding constants to match global rounding constants and
	 * update GUI when needed
	 */
	private void updateRounding() {

		if (kernel.useSignificantFigures) {
			if (printFigures != kernel.getPrintFigures()) {
				printFigures = kernel.getPrintFigures();
				printDecimals = -1;
				updateGUI();
			}
		} else if (printDecimals != kernel.getPrintDecimals()) {
			printDecimals = kernel.getPrintDecimals();
			updateGUI();
		}
	}

	public int getPrintDecimals() {
		return printDecimals;
	}

	public int getPrintFigures() {
		return printFigures;
	}

	// =================================================
	// View Implementation
	// =================================================

	public void remove(GeoElement geo) {
		// Application.debug("removed geo: " + geo.toString());
		daCtrl.handleRemovedDataGeo(geo);
	}

	public void update(GeoElement geo) {

		updateRounding();

		// update the view if the geo is in the data source
		if (!isIniting && daCtrl.isInDataSource(geo)) {

			// use a runnable to allow spreadsheet table model to update
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					daCtrl.updateDataAnalysisView();
				}
			});
		}
	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void add(GeoElement geo) {
		// do nothing
	}

	public void clearView() {
		// do nothing
	}

	public void rename(GeoElement geo) {
		// do nothing
	}

	public void repaintView() {
		// do nothing
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// do nothing
	}

	public void reset() {
		// do nothing
	}

	public void setMode(int mode, ModeSetter m) {
		// do nothing
	}

	public void attachView() {
		// clearView();
		// kernel.notifyAddAll(this);
		kernel.attach(this);

		// attachView to plot panels
		dataDisplayPanel1.attachView();
		if (dataDisplayPanel2 != null)
			dataDisplayPanel2.attachView();
	}

	public void detachView() {

		dataDisplayPanel1.detachView();
		if (dataDisplayPanel2 != null)
			dataDisplayPanel2.detachView();
		daCtrl.removeStatGeos();

		kernel.detach(this);

		// clearView();
		// kernel.notifyRemoveAll(this);
	}

	public String[] getDataTitles() {
		return daCtrl.getDataTitles();
	}

	public void updateSelection() {
		// updateDialog(true);
	}

	// =================================================
	// Printing
	// =================================================

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// construction title
		int y = 0;
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
					app.getBoldFont().getSize() + 2);
			g2d.setFont(titleFont);
			g2d.setColor(Color.black);
			// Font fn = g2d.getFont();
			FontMetrics fm = g2d.getFontMetrics();
			y += fm.getAscent();
			g2d.drawString(title, 0, y);
		}

		// construction author and date
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + " - " + date;
		}

		if (line != null) {
			g2d.setFont(app.getPlainFont());
			g2d.setColor(Color.black);
			// Font fn = g2d.getFont();
			FontMetrics fm = g2d.getFontMetrics();
			y += fm.getHeight();
			g2d.drawString(line, 0, y);
		}
		if (y > 0) {
			g2d.translate(0, y + 20); // space between title and drawing
		}

		// scale the dialog so that it fits on one page.
		double xScale = pageFormat.getImageableWidth() / this.getWidth();
		double yScale = (pageFormat.getImageableHeight() - (y + 20))
				/ this.getHeight();
		double scale = Math.min(xScale, yScale);

		this.paint(g2d, scale);

		return (PAGE_EXISTS);
	}

	/**
	 * Paint the dialog with given scale factor (used for printing).
	 */
	public void paint(Graphics graphics, double scale) {

		Graphics2D g2 = (Graphics2D) graphics;
		g2.scale(scale, scale);
		super.paint(graphics);

	}

	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}

	public JPopupMenu getExportMenu() {
		return dataDisplayPanel1.getExportMenu();
	}

	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}
	
	public void suggestRepaint(){
		// only used in web for now
	}

}
