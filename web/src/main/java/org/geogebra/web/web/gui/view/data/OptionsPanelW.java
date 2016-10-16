package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.DataDisplayModel;
import org.geogebra.common.gui.view.data.DataDisplayModel.PlotType;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.gui.view.data.StatPanelSettings;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * JPanel to display settings options for a ComboStatPanel
 * 
 * @author G. Sturr
 * 
 */
public class OptionsPanelW extends FlowPanel implements ClickHandler, BlurHandler,
	StatPanelInterfaceW {

	private AppW app;
	private DataAnalysisViewW statDialog;
	private StatPanelSettings settings;

	// histogram panel GUI
	private CheckBox ckCumulative, ckManual, ckOverlayNormal,
			ckOverlayPolygon, ckShowFrequencyTable, ckShowHistogram;
	private RadioButton rbRelative, rbNormalized, rbFreq, rbLeftRule,
			rbRightRule;
	private Label lblFreqType, lblOverlay, lblClassRule;
	private FlowPanel freqPanel, showPanel, dimPanel;
	private Label lbClassTitle, lbFreqTitle, lbShowTitle, lbDimTitle;
	// graph panel GUI
	private CheckBox ckAutoWindow, ckShowGrid;
	private Label lblXMin, lblXMax, lblYMin, lblYMax, lblXInterval,
			lblYInterval;
	AutoCompleteTextFieldW fldXMin, fldXMax;

	private AutoCompleteTextFieldW fldYMin, fldYMax, fldXInterval, fldYInterval;
	private boolean showYAxisSettings = true;

	// bar chart panel GUI
	private Label lblBarWidth;
	private AutoCompleteTextFieldW fldBarWidth;
	private CheckBox ckAutoBarWidth;
	private FlowPanel barChartWidthPanel;

	// box plot panel GUI
	private CheckBox ckShowOutliers;

	// scatterplot panel GUI
	private CheckBox ckShowLines;

	// panels
	private FlowPanel histogramPanel, graphPanel, classesPanel, scatterplotPanel,
			barChartPanel, boxPlotPanel;
	private FlowPanel mainPanel;
	private TabPanel tabPanel;

	// misc fields
	private static final int tab = 5;
	private boolean isUpdating = false;

	private DataAnalysisModel daModel;

	private DataDisplayModel dyModel;

	private ScrollPanel spHistogram;

	private ScrollPanel spGraph;
	private ListBox cbLogAxes;

	private final static int fieldWidth = 8;

	private class PropertyChangeHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
	       actionPerformed(event.getSource());
        }
		
	}
	
	private class PropertyKeyHandler implements KeyHandler {
		private Object source;
		public PropertyKeyHandler(Object source) {
			this.source = source;
		}
		public void keyReleased(KeyEvent e) {
	        if (e.isEnterKey()) {
	        	actionPerformed(source);
	        }
        }
	}
	/************************************************************
	 * Constructs an OptionPanel
	 * 
	 * @param app
	 *            App
	 * @param settings
	 * @param statDialog
	 *            statDialog
	 * @param settings
	 *            settings
	 */
	public OptionsPanelW(AppW app, DataAnalysisModel model,
			DataDisplayModel dyModel) {

		this.app = app;
		this.daModel = model;
		this.dyModel = dyModel;
		this.settings = dyModel.getSettings();

		// create option panels
		createHistogramPanel();
		createGraphPanel();
		createScatterplotPanel();
		createBarChartPanel();
		createBoxPlotPanel();

		mainPanel = new FlowPanel();
		mainPanel.add(histogramPanel);
		mainPanel.add(scatterplotPanel);
		mainPanel.add(barChartPanel);
		mainPanel.add(boxPlotPanel);

		tabPanel = new TabPanel();
		tabPanel.setStyleName("daOptionsTabPanel");
		add(tabPanel);

		// update
		setLabels();
		updateGUI();
	}

	public void setPanel(PlotType plotType) {

		tabPanel.clear();
		this.setVisible(true);

		// add plot-specific tab
		String tabTitle = plotType.getTranslatedKey(app);
		spHistogram = new ScrollPanel();
		mainPanel.setStyleName("daScrollPanel");
		spHistogram.add(mainPanel);
		tabPanel.add(spHistogram, tabTitle);
		classesPanel.setVisible(false);
		histogramPanel.setVisible(false);
		scatterplotPanel.setVisible(false);
		barChartPanel.setVisible(false);
		boxPlotPanel.setVisible(false);

		rbNormalized.setVisible(false);
		ckOverlayNormal.setVisible(false);
		ckShowHistogram.setVisible(false);
		ckCumulative.setVisible(false);
		ckOverlayPolygon.setVisible(false);

		// add graph tab
		spGraph = new ScrollPanel();
		spGraph.setStyleName("daScrollPanel");
		spGraph.add(graphPanel);
		tabPanel.add(spGraph, app.getLocalization().getMenu("Graph"));
		graphPanel.setVisible(true);
		showYAxisSettings = true;

		// set visibility for plot-specific panels
		switch (plotType) {

		case HISTOGRAM:
			classesPanel.setVisible(true);
			histogramPanel.setVisible(true);
			rbNormalized.setVisible(true);
			ckOverlayNormal.setVisible(true);
			ckShowHistogram.setVisible(true);
			ckCumulative.setVisible(true);
			ckOverlayPolygon.setVisible(true);

			layoutHistogramPanel();

			break;

		case BOXPLOT:
		case MULTIBOXPLOT:
			boxPlotPanel.setVisible(true);
			break;

		case BARCHART:
			barChartPanel.setVisible(true);
			layoutBarChartPanel();
			break;

		case SCATTERPLOT:
			scatterplotPanel.setVisible(true);
			break;

		// graph tab only
		case DOTPLOT:
		case NORMALQUANTILE:
		case RESIDUAL:
			tabPanel.remove(0);
			break;

		case STEMPLOT:
			this.setVisible(false);
			break;

		}

		tabPanel.selectTab(0);
		setLabels();
		updateGUI();
	}

	private void createHistogramPanel() {
		histogramPanel = new FlowPanel();
		// create components
		ckCumulative = new CheckBox();
		
		lblFreqType = new Label();
		
		lbClassTitle = new Label();
		lbClassTitle.setStyleName("panelTitle");
		
		lbFreqTitle = new Label();
		lbFreqTitle.setStyleName("panelTitle");
	
		lbShowTitle = new Label();
		lbShowTitle.setStyleName("panelTitle");
	
		lbDimTitle = new Label();
		lbDimTitle.setStyleName("panelTitle");
		
		rbFreq = new RadioButton("group1");

		rbNormalized = new RadioButton("group1");

		rbRelative = new RadioButton("group1");
		lblOverlay = new Label();
		ckOverlayNormal = new CheckBox();
		
		ckOverlayPolygon = new CheckBox();
		
		ckShowFrequencyTable = new CheckBox();
		
		ckShowHistogram = new CheckBox();
		
		ckManual = new CheckBox();
		
		lblClassRule = new Label();
		rbLeftRule = new RadioButton("rule");
		rbRightRule = new RadioButton("rule");

		// create frequency type panel
		freqPanel = new FlowPanel();
		freqPanel.add(lbFreqTitle);
		freqPanel.add(ckCumulative);
		freqPanel.add(LayoutUtilW.panelRowIndent(rbFreq));
		freqPanel.add(LayoutUtilW.panelRowIndent(rbRelative));
		freqPanel.add(LayoutUtilW.panelRowIndent(rbNormalized));

		// create show panel
		showPanel = new FlowPanel();
		showPanel.add(lbShowTitle);
		showPanel.add(LayoutUtilW.panelRowIndent(ckShowHistogram));
		showPanel.add(LayoutUtilW.panelRowIndent(ckShowFrequencyTable));
		showPanel.add(LayoutUtilW.panelRowIndent(ckOverlayPolygon));
		showPanel.add(LayoutUtilW.panelRowIndent(ckOverlayNormal));

		// create classes panel
		classesPanel = new FlowPanel();
		classesPanel.setStyleName("daOptionsGroup");
		classesPanel.add(lbClassTitle);
		classesPanel.add(LayoutUtilW.panelRowIndent(ckManual));
		classesPanel.add(lblClassRule);
		classesPanel.add(LayoutUtilW.panelRowIndent(rbLeftRule));
		classesPanel.add(LayoutUtilW.panelRowIndent(rbRightRule));
		layoutHistogramPanel();

		PropertyChangeHandler handler = new PropertyChangeHandler();
		ckManual.addClickHandler(handler);
		ckCumulative.addClickHandler(handler);
		ckShowHistogram.addClickHandler(handler);
		ckOverlayPolygon.addClickHandler(handler);
		ckOverlayNormal.addClickHandler(handler);
		ckShowFrequencyTable.addClickHandler(handler);
		rbFreq.addClickHandler(handler);
		rbRelative.addClickHandler(handler);
		rbNormalized.addClickHandler(handler);
		rbLeftRule.addClickHandler(handler);
		rbRightRule.addClickHandler(handler);

	}

	private void layoutHistogramPanel() {
		if (histogramPanel == null) {
			histogramPanel = new FlowPanel();
		}

		FlowPanel p = new FlowPanel();
		p.add(classesPanel);
		p.add(freqPanel);
		p.add(showPanel);
		histogramPanel.add(p);
	}

	private void layoutBarChartPanel() {
		if (barChartPanel == null) {
			barChartPanel = new FlowPanel();
		}
		barChartPanel.clear();
		barChartPanel.add(barChartWidthPanel);
		// barChartPanel.add(freqPanel);
		barChartPanel.add(showPanel);
	}

	private void createBarChartPanel() {

		// create components
		ckAutoBarWidth = new CheckBox();
		ckAutoBarWidth.addClickHandler(this);
		lblBarWidth = new Label();
		fldBarWidth = new AutoCompleteTextFieldW(fieldWidth, app);
		fldBarWidth.setEditable(true);
		fldBarWidth.addKeyHandler(new PropertyKeyHandler(fldBarWidth));
		fldBarWidth.addBlurHandler(this);

		// barChartWidthPanel
		barChartWidthPanel = new FlowPanel();
		barChartWidthPanel.add(ckAutoBarWidth);
		barChartWidthPanel.add(LayoutUtilW.panelRow(lblBarWidth,
				fldBarWidth));

		layoutBarChartPanel();

	}

	private void createBoxPlotPanel() {

		// create components
		ckShowOutliers = new CheckBox();
		ckShowOutliers.addClickHandler(this);

		// layout
		
		boxPlotPanel = new FlowPanel();
		boxPlotPanel.add(ckShowOutliers);

	}

	private void createScatterplotPanel() {

		// create components
		ckShowLines = new CheckBox();
		ckShowLines.addClickHandler(this);

		scatterplotPanel = new FlowPanel();
		scatterplotPanel.add(ckShowLines);
	}

	private void createGraphPanel() {

		// create components
		ckAutoWindow = new CheckBox();
		ckAutoWindow.addClickHandler(this);

		ckShowGrid = new CheckBox();
		ckShowGrid.addClickHandler(this);

		lblXMin = new Label();
		fldXMin = InputPanelW.newTextComponent(app);
		fldXMin.setEditable(true);
		fldXMin.addKeyHandler(new KeyHandler() {
			
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldXMin);
				}
			}
		});
		fldXMin.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				actionPerformed(fldXMin);
			}
		});

		lblXMax = new Label();
		fldXMax = InputPanelW.newTextComponent(app);
		fldXMax.addKeyHandler(new KeyHandler() {
			
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					actionPerformed(fldXMax);
				}
			}
		});
		fldXMax.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				actionPerformed(fldXMax);
			}
		});

		lblYMin = new Label();
		fldYMin = InputPanelW.newTextComponent(app);
		fldYMin.addKeyHandler(new PropertyKeyHandler(fldYMin));
		fldYMin.addBlurHandler(this);

		lblYMax = new Label();
		fldYMax = InputPanelW.newTextComponent(app);
		fldYMax.addKeyHandler(new PropertyKeyHandler(fldYMax));
		fldYMax.addBlurHandler(this);

		lblXInterval = new Label();
		fldXInterval = new AutoCompleteTextFieldW(fieldWidth, app);
		fldXInterval.addKeyHandler(new PropertyKeyHandler(fldXInterval));
		fldXInterval.addBlurHandler(this);

		lblYInterval = new Label();
		fldYInterval = new AutoCompleteTextFieldW(fieldWidth, app);
		fldYInterval.addKeyHandler(new PropertyKeyHandler(fldYInterval));
		fldYInterval.addBlurHandler(this);

		// create graph options panel
		FlowPanel graphOptionsPanel = new FlowPanel();
		graphOptionsPanel.add(ckShowGrid);
		graphOptionsPanel.add(ckAutoWindow);

		// create window dimensions panel
		dimPanel = new FlowPanel();
		dimPanel.add(LayoutUtilW.panelRow(lblXMin, fldXMin));
		dimPanel.add(LayoutUtilW.panelRow(lblXMax, fldXMax));
		dimPanel.add(LayoutUtilW.panelRow(lblXInterval, fldXInterval));

		// y dimensions
		dimPanel.add(LayoutUtilW.panelRow(lblYMin, fldYMin));
		dimPanel.add(LayoutUtilW.panelRow(lblYMax, fldYMax));
		dimPanel.add(LayoutUtilW.panelRow(lblYInterval, fldYInterval));


		cbLogAxes = new ListBox();


		ckAutoWindow.addClickHandler(this);
		// put the sub-panels together
		graphPanel = new FlowPanel();
		graphPanel.add(graphOptionsPanel);
		graphPanel.add(dimPanel);
		if (app.has(Feature.LOG_AXES)) {
			cbLogAxes.addItem("Standard To Standard");
			cbLogAxes.addItem("Logarithmic To Standard");
			cbLogAxes.addItem("Standard To Logarithmic");
			cbLogAxes.addItem("Logarithmic To Logarithmic");
			cbLogAxes.addChangeHandler(new ChangeHandler() {

				public void onChange(ChangeEvent event) {
					onComboBoxChange();

				}
			});
			FlowPanel modePanel = new FlowPanel();
			modePanel.add(cbLogAxes);
			graphPanel.add(modePanel);
		}
	}


	protected void onComboBoxChange() {
		int index = cbLogAxes.getSelectedIndex();
		settings.setCoordMode(StatPanelSettings.CoordMode.values()[index]);
		this.firePropertyChange("settings", true, false);
		updateGUI();

	}

	public void setLabels() {
		Localization loc = app.getLocalization();
		// titled borders
		lbClassTitle.setText(loc.getMenu("Classes"));
		lbShowTitle.setText(loc.getMenu("Show"));
		lbFreqTitle.setText(loc.getMenu("FrequencyType"));
		lbDimTitle.setText(loc.getMenu("Dimensions"));

		// histogram options
		ckManual.setText(loc.getMenu("SetClasssesManually"));
		lblFreqType.setText(loc.getMenu("FrequencyType") + ":");

		rbFreq.setText(loc.getMenu("Count"));
		rbNormalized.setText(loc.getMenu("Normalized"));
		rbRelative.setText(loc.getMenu("Relative"));

		ckCumulative.setText(loc.getMenu("Cumulative"));
		lblOverlay.setText(loc.getMenu("Overlay"));
		ckOverlayNormal.setText(loc.getMenu("NormalCurve"));
		ckOverlayPolygon.setText(loc.getMenu("FrequencyPolygon"));
		ckShowFrequencyTable.setText(loc.getMenu("FrequencyTable"));
		ckShowHistogram.setText(loc.getMenu("Histogram"));

		lblClassRule.setText(loc.getMenu("ClassRule") + ":");
		rbRightRule.setText(loc.getMenu("RightClassRule"));
		rbLeftRule.setText(loc.getMenu("LeftClassRule"));

		// bar chart
		lblBarWidth.setText(loc.getMenu("Width"));
		ckAutoBarWidth.setText(loc.getMenu("AutoDimension"));

		// graph options
		ckAutoWindow.setText(loc.getMenu("AutoDimension"));
		ckShowGrid.setText(loc.getMenu("ShowGrid"));
		lblXMin.setText(loc.getMenu("xmin") + ":");
		lblXMax.setText(loc.getMenu("xmax") + ":");
		lblYMin.setText(loc.getMenu("ymin") + ":");
		lblYMax.setText(loc.getMenu("ymax") + ":");

		lblXInterval.setText(loc.getMenu("xstep") + ":");
		lblYInterval.setText(loc.getMenu("ystep") + ":");

		// scatterplot options
		ckShowLines.setText(loc.getMenu("LineGraph"));

		// boxplot options
		ckShowOutliers.setText(loc.getMenu("ShowOutliers"));

	}

	private void updateGUI() {
		// set updating flag so we don't have to add/remove action listeners
		isUpdating = true;

		// histogram/barchart
		ckManual.setValue(settings.isUseManualClasses());
		rbFreq.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_COUNT);
		rbRelative
				.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_RELATIVE);
		rbNormalized
				.setValue(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);
		rbLeftRule.setValue(settings.isLeftRule());
		ckCumulative.setValue(settings.isCumulative());
		ckOverlayNormal.setValue(settings.isHasOverlayNormal());
		ckOverlayPolygon.setValue(settings.isHasOverlayPolygon());
		ckShowGrid.setValue(settings.showGrid);
		ckAutoWindow.setValue(settings.isAutomaticWindow());
		ckShowFrequencyTable.setValue(settings.isShowFrequencyTable());
		ckShowHistogram.setValue(settings.isShowHistogram());

		if (settings.dataSource != null) {
			ckManual.setVisible(settings.getDataSource().getGroupType() != GroupType.CLASS);
			freqPanel
					.setVisible(settings.getDataSource().getGroupType() == GroupType.RAWDATA);
		}
		// normal overlay
		ckOverlayNormal
				.setEnabled(settings.getFrequencyType() == StatPanelSettings.TYPE_NORMALIZED);

		// bar chart width
		ckAutoBarWidth.setValue(settings.isAutomaticBarWidth());
		fldBarWidth.setText("" + settings.getBarWidth());
		fldBarWidth.setEditable(!ckAutoBarWidth.getValue());

		// window dimension
		lblYMin.setVisible(showYAxisSettings);
		fldYMin.setVisible(showYAxisSettings);
		lblYMax.setVisible(showYAxisSettings);
		fldYMax.setVisible(showYAxisSettings);
		lblYInterval.setVisible(showYAxisSettings);
		fldYInterval.setVisible(showYAxisSettings);

		fldXMin.setEditable(!ckAutoWindow.getValue());
		fldXMax.setEditable(!ckAutoWindow.getValue());
		fldXInterval.setEditable(!ckAutoWindow.getValue());
		fldYMin.setEditable(!ckAutoWindow.getValue());
		fldYMax.setEditable(!ckAutoWindow.getValue());
		fldYInterval.setEditable(!ckAutoWindow.getValue());


		// update automatic dimensions
		fldXMin.setText("" + daModel.format(settings.xMin));
		fldXMax.setText("" + daModel.format(settings.xMax));
		fldXInterval.setText("" + daModel.format(settings.xAxesInterval));

		fldYMin.setText("" + daModel.format(settings.yMin));
		fldYMax.setText("" + daModel.format(settings.yMax));
		fldYInterval.setText("" + daModel.format(settings.yAxesInterval));

		// show outliers
		ckShowOutliers.setValue(settings.isShowOutliers());

		isUpdating = false;
	}


	public void actionPerformed(Object source) {

		if (isUpdating)
			return;

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == ckManual) {
			settings.setUseManualClasses(ckManual.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckCumulative) {
			settings.setCumulative(ckCumulative.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == rbFreq) {
			settings.setFrequencyType(StatPanelSettings.TYPE_COUNT);
			firePropertyChange("settings", true, false);
		} else if (source == rbRelative) {
			settings.setFrequencyType(StatPanelSettings.TYPE_RELATIVE);
			firePropertyChange("settings", true, false);
		} else if (source == rbNormalized) {
			settings.setFrequencyType(StatPanelSettings.TYPE_NORMALIZED);
			firePropertyChange("settings", true, false);
		} else if (source == ckOverlayNormal) {
			settings.setHasOverlayNormal(ckOverlayNormal.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckOverlayPolygon) {
			settings.setHasOverlayPolygon(ckOverlayPolygon.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowGrid) {
			settings.showGrid = ckShowGrid.getValue();
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoWindow) {
			settings.setAutomaticWindow(ckAutoWindow.getValue());
			settings.xAxesIntervalAuto = ckAutoWindow.getValue();
			settings.yAxesIntervalAuto = ckAutoWindow.getValue();
			firePropertyChange("settings", true, false);
		} else if (source == ckShowFrequencyTable) {
			settings.setShowFrequencyTable(ckShowFrequencyTable.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowHistogram) {
			settings.setShowHistogram(ckShowHistogram.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == rbLeftRule || source == rbRightRule) {
			settings.setLeftRule(rbLeftRule.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowLines) {
			settings.setShowScatterplotLine(ckShowLines.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckShowOutliers) {
			settings.setShowOutliers(ckShowOutliers.getValue());
			firePropertyChange("settings", true, false);
		} else if (source == ckAutoBarWidth) {
			settings.setAutomaticBarWidth(ckAutoBarWidth.getValue());
			firePropertyChange("settings", true, false);
		} else {
			firePropertyChange("settings", true, false);
		}

		updateGUI();
	}


	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isUpdating)
			return;
		try {
			String inputText = source.getText().trim();
			NumberValue nv;
			nv = app.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			double value = nv.getDouble();

			// TODO better validation

			if (source == fldXMin) {
				settings.xMin = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldXMax) {
				settings.xMax = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYMax) {
				settings.yMax = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYMin) {
				settings.yMin = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldXInterval && value >= 0) {
				settings.xAxesInterval = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldYInterval && value >= 0) {
				settings.yAxesInterval = value;
				firePropertyChange("settings", true, false);
			} else if (source == fldBarWidth && value >= 0) {
				settings.setBarWidth(value);
				firePropertyChange("settings", true, false);
			}
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

    }

	private void firePropertyChange(String string, boolean b, boolean c) {
	    dyModel.updatePlot(true);
    }

	public void updatePanel() {
		
		// TODO Auto-generated method stub

	}

	public void onBlur(BlurEvent event) {
	       actionPerformed(event.getSource());
	    
    }

	public void onClick(ClickEvent event) {
	       actionPerformed(event.getSource());

    }

	public void resize(int height) {
	    spHistogram.setHeight(height + "px");
	    spGraph.setHeight(height + "px");
    }

}
