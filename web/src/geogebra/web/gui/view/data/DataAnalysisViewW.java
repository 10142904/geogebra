package geogebra.web.gui.view.data;

import geogebra.common.awt.GColor;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.common.gui.view.data.DataAnalysisModel.IDataAnalysisListener;
import geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import geogebra.common.gui.view.data.DataSource;
import geogebra.common.gui.view.data.DataVariable.GroupType;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.html5.awt.GColorW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DataAnalysisViewW extends FlowPanel implements View, 
		SetLabels, IDataAnalysisListener {

	private static final long serialVersionUID = 1L;

	// ggb
	private AppW app;
	private Kernel kernel;
	private DataAnalysisModel model;
	protected DataAnalysisControllerW daCtrl;
	//private DataAnalysisStyleBarW stylebar;

	// colors
	public static final GColor TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
	public static final GColor TABLE_HEADER_COLOR = new GColorW(240, 240, 240);
	public static final GColor HISTOGRAM_COLOR = GeoGebraColorConstants.BLUE;
	public static final GColor BOXPLOT_COLOR = GeoGebraColorConstants.CRIMSON;
	public static final GColor BARCHART_COLOR = GeoGebraColorConstants.DARKGREEN;

	public static final GColor DOTPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor NQPLOT_COLOR = GeoGebraColorConstants.GRAY5;
	public static final GColor REGRESSION_COLOR = GColorW.RED;
	public static final GColor OVERLAY_COLOR = GeoGebraColorConstants.DARKBLUE;

	private GColor[] colors = { TABLE_GRID_COLOR, TABLE_HEADER_COLOR,
			HISTOGRAM_COLOR, BOXPLOT_COLOR, BARCHART_COLOR, DOTPLOT_COLOR,
			NQPLOT_COLOR, REGRESSION_COLOR, OVERLAY_COLOR, GColor.BLACK,
			GColor.WHITE };
	// main GUI panels
	private DataPanelW dataPanel;
	private StatisticsPanelW statisticsPanel;
	private RegressionPanelW regressionPanel;
	private DataDisplayPanelW dataDisplayPanel1, dataDisplayPanel2;

//	private JSplitPane statDataPanel, displayPanel, comboPanelSplit;
//	private DataSourcePanel dataSourcePanel;
	private FlowPanel mainPanel;

	private int defaultDividerSize;

	private static final String MainCard = "Card with main panel";
	private static final String SourceCard = "Card with data type options";

	/*************************************************
	 * Constructs the view.
	 * 
	 * @param app
	 * @param mode
	 */
	public DataAnalysisViewW(AppW app, int mode) {
		this.app = app;
		this.kernel = app.getKernel();

		daCtrl = new DataAnalysisControllerW(app, this);
		model = new DataAnalysisModel(app, mode, this, daCtrl);

		dataDisplayPanel1 = new DataDisplayPanelW(this);
		dataDisplayPanel2 = new DataDisplayPanelW(this);

		setView(null, mode, true);
		model.setIniting(false);

	}

	/*************************************************
	 * END constructor
	 */

	protected void setView(DataSource dataSource, int mode,
			boolean forceModeUpdate) {

		model.setView(dataSource, mode, forceModeUpdate);
//		updateFonts();
		setLabels();
		add(new Label("Ezezezezez"));
		updateGUI();

	}

	public Widget getStyleBar() {
//		if (stylebar == null) {
//			stylebar = new DataAnalysisStyleBar(app, this);
//		}
		return null;//stylebar;
	}

	private void createGUI() {

		buildStatisticsPanel();

	}

	private void buildStatisticsPanel() {
		if (statisticsPanel != null) {
			// TODO handle any orphaned geo children of stat panel
			statisticsPanel = null;
		}

		statisticsPanel = new StatisticsPanelW(app, this);
	}

	public void setPlotPanelOVNotNumeric(int mode) {
		dataDisplayPanel1.setPanel(PlotType.BARCHART, mode);
		dataDisplayPanel2.setPanel(PlotType.BARCHART, mode);

	}

	public void setPlotPanelOVRawData(int mode) {
		dataDisplayPanel1.setPanel(PlotType.HISTOGRAM, mode);
		dataDisplayPanel2.setPanel(PlotType.BOXPLOT, mode);

	}

	public void setPlotPanelOVFrequency(int mode) {
		dataDisplayPanel1.setPanel(PlotType.BARCHART, mode);
		dataDisplayPanel2.setPanel(PlotType.BOXPLOT, mode);

	}

	public void setPlotPanelOVClass(int mode) {
		dataDisplayPanel1.setPanel(PlotType.HISTOGRAM, mode);
		dataDisplayPanel2.setPanel(PlotType.HISTOGRAM, mode);

	}

	public void setPlotPanelRegression(int mode) {
		dataDisplayPanel1.setPanel(PlotType.SCATTERPLOT, mode);
		dataDisplayPanel2.setPanel(PlotType.RESIDUAL, mode);
	}

	public void setPlotPanelMultiVar(int mode) {
		dataDisplayPanel1.setPanel(PlotType.MULTIBOXPLOT, mode);

	}

	/**
	 * set the data plot panels with default plots
	 */
	public void setDataPlotPanels() {
		model.setDataPlotPanels();
	}

	// Create DataPanel to display the current data set(s) and allow
	// temporary editing.
	protected DataPanelW buildDataPanel() {

		if (dataPanel != null) {
			// TODO handle any orphaned data panel geos
			dataPanel = null;
		}
		if (!model.isMultiVar()) {
			dataPanel = new DataPanelW(app, this);
		}

		return dataPanel;

	}

	public void loadDataTable(ArrayList<GeoElement> dataArray) {
		if (dataPanel == null) {
			buildDataPanel();
		}
//		dataPanel.loadDataTable(dataArray);
	}

	protected DataPanelW getDataPanel() {
		return dataPanel;
	}

	// =================================================
	// GUI
	// =================================================

	private void updateLayout() {

	//	this.removeAll();

		// ===========================================
		// statData panel
//
//		if (!model.isMultiVar()) {
//			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//					statisticsPanel, null);
//			statDataPanel.setResizeWeight(0.5);
//			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
//		}
//		if (model.isMultiVar()) {
//			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//					statisticsPanel, null);
//			statDataPanel.setDividerSize(0);
//			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
//		}

		// ===========================================
		// regression panel

		if (model.isRegressionMode()) {
			regressionPanel = new RegressionPanelW(app, this);
		}

		// ===========================================
		// plotComboPanel panel

		// create a splitPane to hold the two plotComboPanels
//		comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//				dataDisplayPanel1, dataDisplayPanel2);
//
//		comboPanelSplit.setDividerLocation(0.5);
//		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());
//
//		// grab the default divider size
//		defaultDividerSize = comboPanelSplit.getDividerSize();
//
//		JPanel plotComboPanel = new JPanel(new BorderLayout());
//		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);
//
//		// display panel
//		// ============================================
//		if (!model.isMultiVar()) {
//			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//					statDataPanel, plotComboPanel);
//			displayPanel.setResizeWeight(0.5);
//		} else {
//			displayPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//					plotComboPanel, statDataPanel);
//			displayPanel.setResizeWeight(1);
//
//		}
//		displayPanel.setBorder(BorderFactory.createEmptyBorder());
//
//		// main panel
//		// ============================================
//		mainPanel = new JPanel(new BorderLayout());
//		// mainPanel.add(getStyleBar(), BorderLayout.NORTH);
//		mainPanel.add(displayPanel, BorderLayout.CENTER);
//
//		if (model.isRegressionMode()) {
//			mainPanel.add(regressionPanel, BorderLayout.SOUTH);
//		}
//
//		// dataTypePanel = new DataViewSettingsPanel(app,
//		// StatDialog.MODE_ONEVAR);
//		JPanel p = new JPanel(new FullWidthLayout());
//		// p.add(dataTypePanel);
//
//		this.setLayout(new CardLayout());
//		add(mainPanel, MainCard);
//		add(p, SourceCard);
		showMainPanel();

		model.setShowComboPanel2(model.showDataDisplayPanel2());
		updateStatDataPanelVisibility();

	}

	public void showSourcePanel() {
//		CardLayout c = (CardLayout) this.getLayout();
//		c.show(this, SourceCard);
	}

	public void showMainPanel() {
//		CardLayout c = (CardLayout) this.getLayout();
//		c.show(this, MainCard);

	}

	// ======================================
	// Getters/setters
	// ======================================

	public DataAnalysisControllerW getDaCtrl() {
		return daCtrl;
	}

	public DataSource getDataSource() {
		return model.getDataSource();
	}

	public GroupType groupType() {
		return daCtrl.getDataSource().getGroupType();
	}

	public DataDisplayPanelW getDataDisplayPanel1() {
		return dataDisplayPanel1;
	}

	public DataDisplayPanelW getDataDisplayPanel2() {
		return dataDisplayPanel2;
	}

	public RegressionPanelW getRegressionPanel() {
		return regressionPanel;
	}

	public StatisticsPanelW getStatisticsPanel() {
		return statisticsPanel;
	}

	/**
	 * Component representation of this view
	 * 
	 * @return reference to self
	 */
	public Widget getDataAnalysisViewComponent() {
		App.debug("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
		return new Label("WWWWWWWWWWWWWWWW");
	}

	public DataAnalysisControllerW getController() {
		return daCtrl;
	}

	public GeoElement getRegressionModel() {
		return daCtrl.getRegressionModel();
	}

	public AppW getApp() {
		return app;
	}

	// public int getMode() {
	// return mode;
	// }

	// =================================================
	// Handlers for Component Visibility
	// =================================================

	public void updateStatDataPanelVisibility() {

//		if (statDataPanel == null)
//			return;
//
//		if (!model.isMultiVar()) {
//
//			if (model.showDataPanel()) {
//				if (statDataPanel.getRightComponent() == null) {
//					statDataPanel.setRightComponent(dataPanel);
//					statDataPanel.resetToPreferredSizes();
//				}
//			} else {
//				if (statDataPanel.getRightComponent() != null) {
//					statDataPanel.setRightComponent(null);
//					statDataPanel.resetToPreferredSizes();
//				}
//			}
//
//			if (model.showStatPanel()) {
//				if (statDataPanel.getLeftComponent() == null) {
//					statDataPanel.setLeftComponent(statisticsPanel);
//					statDataPanel.resetToPreferredSizes();
//				}
//			} else {
//				if (statDataPanel.getLeftComponent() != null) {
//					statDataPanel.setLeftComponent(null);
//					statDataPanel.resetToPreferredSizes();
//				}
//			}
//
//			// hide/show divider
//			if (model.showDataPanel() && model.showStatPanel())
//				statDataPanel.setDividerSize(defaultDividerSize);
//			else
//				statDataPanel.setDividerSize(0);
//
//			// hide/show statData panel
//			if (model.showDataPanel() || model.showStatPanel()) {
//				if (displayPanel.getLeftComponent() == null) {
//					displayPanel.setLeftComponent(statDataPanel);
//					// displayPanel.resetToPreferredSizes();
//					displayPanel.setDividerLocation(displayPanel
//							.getLastDividerLocation());
//					displayPanel.setDividerSize(defaultDividerSize);
//				}
//
//			} else { // statData panel is empty, so hide it
//				displayPanel.setLastDividerLocation(displayPanel
//						.getDividerLocation());
//				displayPanel.setLeftComponent(null);
//				displayPanel.setDividerSize(0);
//			}
//
//		} else { // handle multi-variable case
//
//			if (model.showStatPanel()) {
//				if (displayPanel.getBottomComponent() == null) {
//					displayPanel.setBottomComponent(statDataPanel);
//					// displayPanel.resetToPreferredSizes();
//					displayPanel.setDividerLocation(displayPanel
//							.getLastDividerLocation());
//					displayPanel.setDividerSize(defaultDividerSize);
//				}
//			} else {
//				displayPanel.setLastDividerLocation(displayPanel
//						.getDividerLocation());
//				displayPanel.setBottomComponent(null);
//				displayPanel.setDividerSize(0);
//
//			}
//
//		}
//
//		setLabels();
//		updateFonts();
//
//		displayPanel.resetToPreferredSizes();
	}

	public void doPrint() {
//		List<Printable> l = new ArrayList<Printable>();
//		l.add(this);
//		PrintPreview.get(app, App.VIEW_DATA_ANALYSIS, PageFormat.LANDSCAPE)
//				.setVisible(true);
	}

	// =================================================
	// Event Handlers and Updates
	// =================================================

	public void updateGUI() {

//		if (stylebar != null) {
//			stylebar.updateGUI();
//		}
//		revalidate();
//		repaint();
	}

//	public void updateFonts() {
//		Font font = app.getPlainFont();
//		setFont(font);
//		setFontRecursive(this, font);
//
//	}
//
//	public void setFontRecursive(Container c, Font font) {
//		Component[] components = c.getComponents();
//		for (Component com : components) {
//			com.setFont(font);
//			if (com instanceof StatPanelInterfaceW) {
//				((StatPanelInterfaceW) com).updateFonts(font);
//			}
//			if (com instanceof Container)
//				setFontRecursive((Container) com, font);
//		}
//	}
//
	public void setLabels() {

		if (model.isIniting()) {
			return;
		}

		// setTitle(app.getMenu("OneVariableStatistics"));

		if (model.isRegressionMode() && regressionPanel != null) {
			regressionPanel.setLabels();
		}

//		if (stylebar != null) {
//			stylebar.setLabels();
//		}
//
//		// call setLabels() for all child panels
//		setLabelsRecursive(this);

	}

//	public void setLabelsRecursive(Container c) {
//
//		Component[] components = c.getComponents();
//		for (Component com : components) {
//			if (com instanceof StatPanelInterfaceW) {
//				// System.out.println(c.getClass().getSimpleName());
//				((StatPanelInterfaceW) com).setLabels();
//			} else if (com instanceof Container)
//				setLabelsRecursive((Container) com);
//		}
//	}

	// =================================================
	// Number Format
	//
	// (use GeoGebra rounding settings unless decimals < 4)
	// =================================================

	// =================================================
	// View Implementation
	// =================================================

	public void remove(GeoElement geo) {
		model.remove(geo);
	}

	public void update(GeoElement geo) {
		model.update(geo);
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

//	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
//		if (pageIndex > 0)
//			return (NO_SUCH_PAGE);
//
//		Graphics2D g2d = (Graphics2D) g;
//		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
//
//		// construction title
//		int y = 0;
//		Construction cons = kernel.getConstruction();
//		String title = cons.getTitle();
//		if (!title.equals("")) {
//			Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
//					app.getBoldFont().getSize() + 2);
//			g2d.setFont(titleFont);
//			g2d.setColor(Color.black);
//			// Font fn = g2d.getFont();
//			FontMetrics fm = g2d.getFontMetrics();
//			y += fm.getAscent();
//			g2d.drawString(title, 0, y);
//		}
//
//		// construction author and date
//		String author = cons.getAuthor();
//		String date = cons.getDate();
//		String line = null;
//		if (!author.equals("")) {
//			line = author;
//		}
//		if (!date.equals("")) {
//			if (line == null)
//				line = date;
//			else
//				line = line + " - " + date;
//		}
//
//		if (line != null) {
//			g2d.setFont(app.getPlainFont());
//			g2d.setColor(Color.black);
//			// Font fn = g2d.getFont();
//			FontMetrics fm = g2d.getFontMetrics();
//			y += fm.getHeight();
//			g2d.drawString(line, 0, y);
//		}
//		if (y > 0) {
//			g2d.translate(0, y + 20); // space between title and drawing
//		}
//
//		// scale the dialog so that it fits on one page.
//		double xScale = pageFormat.getImageableWidth() / this.getWidth();
//		double yScale = (pageFormat.getImageableHeight() - (y + 20))
//				/ this.getHeight();
//		double scale = Math.min(xScale, yScale);
//
//		this.paint(g2d, scale);
//
//		return (PAGE_EXISTS);
//	}
//
//	/**
//	 * Paint the dialog with given scale factor (used for printing).
//	 */
//	public void paint(Graphics graphics, double scale) {
//
//		Graphics2D g2 = (Graphics2D) graphics;
//		g2.scale(scale, scale);
//		super.paint(graphics);
//
//	}
//
	public int getViewID() {
		return App.VIEW_DATA_ANALYSIS;
	}
//
//	public JPopupMenu getExportMenu() {
//		return dataDisplayPanel1.getExportMenu();
//	}
//
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void suggestRepaint() {
		// only used in web for now
	}

	public DataAnalysisModel getModel() {
		return model;
	}

	public void setModel(DataAnalysisModel model) {
		this.model = model;
	}

	public void onModeChange() {
		dataPanel = null;
		buildStatisticsPanel();

		setDataPlotPanels();
		updateLayout();

	}

	public void showComboPanel2(boolean show) {
		if (show) {
//			if (comboPanelSplit == null) {
//				// Application.debug("splitpane null");
//			}
//			comboPanelSplit.setBottomComponent(dataDisplayPanel2);
//			comboPanelSplit.setDividerLocation(200);
//			comboPanelSplit.setDividerSize(4);
//		} else {
//			comboPanelSplit.setBottomComponent(null);
//			comboPanelSplit.setLastDividerLocation(comboPanelSplit
//					.getDividerLocation());
//			comboPanelSplit.setDividerLocation(0);
//			comboPanelSplit.setDividerSize(0);
		}

	}

	public String format(double value) {
		return model.format(value);
	}

	public GColor createColor(int idx) {
		GColor c = colors[idx];
		return new GColorW(c.getRed(), c.getGreen(), c.getBlue());
	}

	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
    }


}
