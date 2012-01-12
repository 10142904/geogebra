package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.kernel.algos.AlgoHistogram;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.inputfield.AutoCompleteTextField;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.main.Application;
import geogebra.util.Validation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
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
 * StatDialog class.
 * 
 * @author G.Sturr
 * 
 */
public class StatComboPanel extends JPanel implements ActionListener, FocusListener,
StatPanelInterface{

	// ggb fields
	private Application app;
	private StatDialog statDialog;
	private StatGeo statGeo;

	// stat dialog mode
	private int mode;

	// one variable plot types
	public static final int PLOT_HISTOGRAM = 0;
	public static final int PLOT_BOXPLOT = 1;
	public static final int PLOT_DOTPLOT = 2;
	public static final int PLOT_NORMALQUANTILE = 3;
	public static final int PLOT_STEMPLOT = 5;

	// two variable plot types
	public static final int PLOT_SCATTERPLOT = 30;
	public static final int PLOT_RESIDUAL = 31;

	// multi variable plot types
	public static final int PLOT_MULTIBOXPLOT = 50;

	// currently selected plot type
	private int selectedPlot;


	// plot reference vars
	protected static HashMap<Integer, String> plotMap;
	private HashMap<String, Integer> plotMapReverse;


	private StatPanelSettings settings;


	// geos
	private GeoList regressionAnalysisList;
	private ArrayList<GeoElement> plotGeoList;
	
	private GeoElement[] boxPlotTitles;
	private GeoElement histogram, dotPlot, frequencyPolygon, normalCurve, 
	scatterPlot, scatterPlotLine, residualPlot;
;
	

	// display panels 	
	private JPanel displayCardPanel;
	private JPanel metaPlotPanel, plotPanelNorth, plotPanelSouth;
	private PlotPanelEuclidianView plotPanel;


	private JLabel imageContainer;


	// control panel
	private JPanel controlPanel;
	private JPanel controlCards;
	private boolean hasControlPanel;
	private JComboBox cbDisplayType;


	// options button and sidebar panel
	private OptionsPanel optionsPanel; 
	private JToggleButton optionsButton;

	// numClasses panel 
	private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses; 

	// manual classes panel
	private JToolBar manualClassesPanel;
	private JLabel lblStart;
	private JLabel lblWidth;
	private AutoCompleteTextField fldStart;
	private AutoCompleteTextField fldWidth;
	private JLabel lblNumClasses;



	// stemplot adjustment panel
	private JToolBar stemAdjustPanel;
	private JLabel lblAdjust;
	private JButton minus;
	private JButton none;
	private JButton plus;
	private int stemPlotAdjustment = 0;
	private JPanel imagePanel;
	private JComboBox cbInferenceType;
	private JPanel inferencePanel;

	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	private FrequencyTable frequencyTable;
	




	/***************************************** 
	 * Constructs a ComboStatPanel
	 */
	public  StatComboPanel( StatDialog statDialog, int defaultPlotIndex, int mode, boolean hasControlPanel){

		this.statDialog = statDialog;
		this.app = statDialog.getApp();
		this.mode = mode;
		this.statGeo = statDialog.getStatGeo();
		this.selectedPlot = defaultPlotIndex;
		this.hasControlPanel=hasControlPanel;
		plotGeoList = new ArrayList<GeoElement>();

		createPlotMap();

		createGUI();
		setLabels();
		updatePlot(true);

	}



	//==============================================
	//              GUI
	//==============================================


	private void createGUI(){

		// create settings
		settings = new StatPanelSettings();


		// create options button
		optionsButton = new JToggleButton();
		optionsButton.setIcon(app.getImageIcon("document-properties.png"));
		optionsButton.setIcon(app.getImageIcon("inputhelp_left_18x18.png"));
		optionsButton.setSelectedIcon(app.getImageIcon("inputhelp_right_18x18.png"));
		optionsButton.setBorderPainted(false);
		optionsButton.setFocusPainted(false);
		optionsButton.setContentAreaFilled(false);
		optionsButton.addActionListener(this);



		// create control panel 
		if(hasControlPanel){

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
			controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(flowPanel(cbDisplayType),BorderLayout.WEST);
			controlPanel.add(controlCards,BorderLayout.CENTER);
			controlPanel.add(flowPanelRight(optionsButton),BorderLayout.EAST);
		}


		
		plotPanel = new PlotPanelEuclidianView(app.getKernel(), exportToEVAction);
		

		plotPanelNorth = new JPanel();
		plotPanelSouth = new JPanel();
		plotPanelNorth.setBackground(plotPanel.getBackground());
		plotPanelSouth.setBackground(plotPanel.getBackground());
		lblTitleX = new JLabel();
		lblTitleY = new JLabel();
		fldTitleX = new MyTextField(app,20);
		fldTitleY = new MyTextField(app,20);
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
		displayCardPanel.setBackground(plotPanel.getBackground());

		displayCardPanel.add("plotPanel", metaPlotPanel);
		displayCardPanel.add("imagePanel", new JScrollPane(imagePanel));


		// create options panel
		optionsPanel= new OptionsPanel(app, statDialog, settings);
		optionsPanel.addPropertyChangeListener("settings", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updatePlot(true);
			}
		});
		optionsPanel.setVisible(false);

		frequencyTable = new FrequencyTable(app, statDialog); 



		// =======================================
		// put all the panels together

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		if(hasControlPanel){
			mainPanel.add(controlPanel,BorderLayout.NORTH);
		}
		mainPanel.add(displayCardPanel,BorderLayout.CENTER);		
		mainPanel.add(optionsPanel,BorderLayout.EAST);


		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		//	this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.setBorder(BorderFactory.createEmptyBorder());
		controlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));

	}


	/**
	 * Sets the labels to the current language
	 */
	public void setLabels(){

		createPlotMap();
		createDisplayTypeComboBox();
		lblNumClasses.setText(app.getMenu("Classes") + ": ");
		lblStart.setText(app.getMenu("Start") + ": ");
		lblWidth.setText(app.getMenu("Width") + ": ");
		if(mode == statDialog.MODE_REGRESSION){
			lblTitleX.setText(app.getMenu("Column.X") + ": ");
			lblTitleY.setText(app.getMenu("Column.Y") + ": ");
		}
		lblAdjust.setText(app.getMenu("Adjustment")+ ": ");

		optionsPanel.setLabels();
		optionsButton.setToolTipText(app.getMenu("Options"));

	}



	/**
	 * Creates the JComboBox that selects display type
	 */
	private void createDisplayTypeComboBox(){

		if(cbDisplayType == null){
			cbDisplayType = new JComboBox();
			cbDisplayType.setRenderer(new MyRenderer());


		}else{
			cbDisplayType.removeActionListener(this);
			cbDisplayType.removeAllItems();
		}

		switch(mode){

		case StatDialog.MODE_ONEVAR:
			cbDisplayType.addItem(plotMap.get(PLOT_HISTOGRAM));
			cbDisplayType.addItem(plotMap.get(PLOT_BOXPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_DOTPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_STEMPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_NORMALQUANTILE));
			break;

		case StatDialog.MODE_REGRESSION:
			cbDisplayType.addItem(plotMap.get(PLOT_SCATTERPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_RESIDUAL));
			break;

		case StatDialog.MODE_MULTIVAR:
			cbDisplayType.addItem(plotMap.get(PLOT_MULTIBOXPLOT));
			break;
		}

		cbDisplayType.setSelectedItem(plotMap.get(selectedPlot));
		cbDisplayType.addActionListener(this);
		cbDisplayType.setMaximumRowCount(cbDisplayType.getItemCount());

	}



	/**
	 * Updates the plot panel. Adds/removes additional panels as needed for the
	 * current selected plot.
	 */
	private void updatePlotPanelLayout(){

		metaPlotPanel.removeAll();
		plotPanelSouth.removeAll();
		plotPanelNorth.removeAll();
		metaPlotPanel.add(plotPanel.getJPanel(), BorderLayout.CENTER);

		if(selectedPlot == this.PLOT_SCATTERPLOT){
			plotPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
			plotPanelSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			plotPanelSouth.add(lblTitleX);
			plotPanelSouth.add(fldTitleX);
			plotPanelNorth.add(lblTitleY);
			plotPanelNorth.add(fldTitleY);

			metaPlotPanel.add(plotPanelNorth, BorderLayout.NORTH);
			metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
		}

		else if(selectedPlot == this.PLOT_HISTOGRAM){

			//	plotPanelNorth.setLayout(new FlowLayout(FlowLayout.LEFT));
			//	plotPanelNorth.add(lblTitleY);
			//	plotPanelNorth.add(fldTitleY);
			//	metaPlotPanel.add(plotPanelNorth, BorderLayout.NORTH);

			if(settings.showFrequencyTable){
				plotPanelSouth.setLayout(new BorderLayout());
				plotPanelSouth.add(frequencyTable, BorderLayout.CENTER);
				metaPlotPanel.add(plotPanelSouth, BorderLayout.SOUTH);
			}
		}

		//plotPanelSouth.revalidate();
		//plotPanelNorth.revalidate();

	}


	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel(){

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
	private void createNumClassesPanel(){

		lblNumClasses = new JLabel();
		final JTextField fldNumClasses = new JTextField(""+numClasses);
		fldNumClasses.setEditable(false);
		fldNumClasses.setOpaque(true);
		fldNumClasses.setColumns(2);
		fldNumClasses.setHorizontalAlignment(SwingConstants.CENTER);
		fldNumClasses.setBackground(Color.WHITE);

		sliderNumClasses = new JSlider(SwingConstants.HORIZONTAL, 3, 20, numClasses);
		Dimension d = sliderNumClasses.getPreferredSize();
		d.width = 80;
		sliderNumClasses.setPreferredSize(d);
		sliderNumClasses.setMinimumSize(new Dimension(50,d.height));


		sliderNumClasses.setMajorTickSpacing(1);
		sliderNumClasses.setSnapToTicks(true);
		sliderNumClasses.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				numClasses = slider.getValue();
				fldNumClasses.setText(("" + numClasses));
				updatePlot(true);
				//btnClose.requestFocus();
			}
		});

		numClassesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numClassesPanel.add(sliderNumClasses);
		numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(fldNumClasses);

	}




	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel(){

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
	private void createManualClassesPanel(){

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new AutoCompleteTextField(4,app);
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + (int)settings.classStart);
		fldStart.addFocusListener(this);

		fldWidth = new AutoCompleteTextField(4,app);
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + (int)settings.classWidth);
		fldWidth.addFocusListener(this);
		
		manualClassesPanel = new JToolBar();
		manualClassesPanel.setFloatable(false);
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);

	}

	/*
	private void createTwoSampleSelectionPanel(){

		modelTitle1 = new DefaultComboBoxModel();
		modelTitle2 = new DefaultComboBoxModel();
		cbTitle1 = new JComboBox(modelTitle1);
		cbTitle2 = new JComboBox(modelTitle2);
		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		lblTitle1.setText("1: ");
		lblTitle2.setText("2: ");

		modelTitle1.removeAllElements();
		modelTitle2.removeAllElements();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				modelTitle1.addElement(dataTitles[i]);
				modelTitle2.addElement(dataTitles[i]);
			}
		}


		JToolBar p = new JToolBar();

		p.add(flowPanel(lblTitle1,cbTitle1));
		p.add(flowPanel(lblTitle2,cbTitle2));
		twoSampleSelectionPanel =new JPanel(new BorderLayout());
		twoSampleSelectionPanel.add(p);
	}
	 */


	/**
	 * Creates two hash maps for JComboBox selections, 
	 * 1) plotMap:  Key = integer display type, Value = JComboBox menu string  
	 * 2) plotMapReverse: Key = JComboBox menu string, Value = integer display type    
	 */
	private void createPlotMap(){
		if(plotMap == null)
			plotMap = new HashMap<Integer,String>();

		plotMap.clear();
		plotMap.put(PLOT_HISTOGRAM, app.getMenu("Histogram"));
		plotMap.put(PLOT_BOXPLOT, app.getMenu("Boxplot"));
		plotMap.put(PLOT_DOTPLOT, app.getMenu("DotPlot"));
		plotMap.put(PLOT_NORMALQUANTILE, app.getMenu("NormalQuantilePlot"));
		plotMap.put(PLOT_STEMPLOT, app.getMenu("StemPlot"));

		plotMap.put(PLOT_SCATTERPLOT, app.getMenu("Scatterplot"));
		plotMap.put(PLOT_RESIDUAL, app.getMenu("ResidualPlot"));

		plotMap.put(PLOT_MULTIBOXPLOT, app.getMenu("StackedBoxPlots"));

		// REVERSE PLOT MAP
		plotMapReverse = new HashMap<String, Integer>();
		for(Integer key: plotMap.keySet()){
			plotMapReverse.put(plotMap.get(key), key);
		}

	}



	//==============================================
	//              DISPLAY UPDATE
	//==============================================

	
	public void updatePlot(boolean doCreate){

		GeoList dataListSelected = statDialog.getStatDialogController().getDataSelected();
		
		
		//if(!statGeo.removeFromConstruction()){	
			//statDialog.getStatDialogController().setRegressionGeo();			
		//}
	
		
		GeoElement geo;
		if(hasControlPanel)
			((CardLayout)controlCards.getLayout()).show(controlCards, "blankPanel");	

		if(doCreate)
			clearPlotGeoList();

		optionsButton.setVisible(true);
		updatePlotPanelLayout();

		switch(selectedPlot){

		case PLOT_HISTOGRAM:			
			if(doCreate){
				if(histogram != null)
					histogram.remove();
				histogram = statGeo.createHistogram(dataListSelected, numClasses, settings, false);
				plotGeoList.add(histogram);

				if(frequencyPolygon != null)
					frequencyPolygon.remove();
				if(settings.hasOverlayPolygon){
					frequencyPolygon = statGeo.createHistogram( dataListSelected, numClasses, settings, true);
					plotGeoList.add(frequencyPolygon);
				}
				if(normalCurve != null)
					normalCurve.remove();
				if(settings.hasOverlayNormal){
					normalCurve = statGeo.createNormalCurveOverlay(dataListSelected);
					plotGeoList.add(normalCurve);
				}
			}

			// update the frequency table
			AlgoHistogram algo = (AlgoHistogram) histogram.getParentAlgorithm();
			frequencyTable.setTable(algo.getLeftBorder(), algo.getYValue(), settings);

			// update settings
			statGeo.getHistogramSettings( dataListSelected, histogram, settings);
			plotPanel.updateSettings(settings);

			if(hasControlPanel)
				if(settings.useManualClasses)
					((CardLayout)controlCards.getLayout()).show(controlCards, "manualClassesPanel");	
				else
					((CardLayout)controlCards.getLayout()).show(controlCards, "numClassesPanel");	


			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			break;	

		case PLOT_BOXPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createBoxPlot( dataListSelected));

			statGeo.getBoxPlotSettings( dataListSelected, settings);
			plotPanel.updateSettings(settings);
			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			break;

		case PLOT_DOTPLOT:
			if(doCreate){
				if(dotPlot != null)
					dotPlot.remove();
				dotPlot = statGeo.createDotPlot( dataListSelected);
				plotGeoList.add(dotPlot);
			}

			statGeo.updateDotPlot(dataListSelected, dotPlot, settings);
			plotPanel.updateSettings(settings);
			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			break;

		case PLOT_STEMPLOT:
			String latex = statGeo.getStemPlotLatex( dataListSelected, settings.stemAdjust);
			imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, latex, app.getPlainFont(), true, Color.BLACK, null));
			optionsButton.setVisible(false);
			if(hasControlPanel)
				((CardLayout)controlCards.getLayout()).show(controlCards, "stemAdjustPanel");

			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "imagePanel");
			break;


		case PLOT_NORMALQUANTILE:
			if(doCreate){
				plotGeoList.add(statGeo.createNormalQuantilePlot( dataListSelected));
			}
			statGeo.updateNormalQuantilePlot(dataListSelected, settings);
			plotPanel.updateSettings(settings);
			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			break;

		case PLOT_SCATTERPLOT:
			if(doCreate){
				scatterPlot = statGeo.createScatterPlot(dataListSelected);
				plotGeoList.add(scatterPlot);

				if(statDialog.getRegressionModel()!=null 
						&& statDialog.getRegressionMode() != statDialog.REG_NONE){
					plotGeoList.add(statDialog.getRegressionModel());  
				}

				if(settings.showScatterplotLine){
					scatterPlotLine = statGeo.createScatterPlotLine((GeoList) scatterPlot);
					plotGeoList.add(scatterPlotLine);
				}
			}

			// update xy title fields
			fldTitleX.setText(statDialog.getDataTitles()[0]);
			fldTitleY.setText(statDialog.getDataTitles()[1]);

			// update settings
			statGeo.getScatterPlotSettings(dataListSelected, settings);
			plotPanel.updateSettings(settings);

			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");

			break;


		case PLOT_RESIDUAL:
			if(doCreate){
				if(statDialog.getRegressionMode() != statDialog.REG_NONE){
					residualPlot = statGeo.createRegressionPlot(dataListSelected, statDialog.getRegressionMode(), 
							statDialog.getRegressionOrder(), true);
					plotGeoList.add(residualPlot);
					statGeo.getResidualPlotSettings(dataListSelected, residualPlot, settings);
					plotPanel.updateSettings(settings);
				} 
				else if(residualPlot != null){
					residualPlot.remove();
					residualPlot = null;
				}
			}

			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			break;


		case PLOT_MULTIBOXPLOT:
			if(doCreate){
				GeoElement[] boxPlots = statGeo.createMultipleBoxPlot( dataListSelected);
				for (int i = 0 ; i < boxPlots.length ; i++)
					plotGeoList.add(boxPlots[i]);
			}

			statGeo.getMultipleBoxPlotSettings(dataListSelected, settings);
			plotPanel.updateSettings(settings);
			boxPlotTitles = statGeo.createBoxPlotTitles(statDialog, settings);
			for (int i = 0 ; i < boxPlotTitles.length ; i++)
				plotGeoList.add(boxPlotTitles[i]);

			((CardLayout)displayCardPanel.getLayout()).show(displayCardPanel, "plotPanel");
			optionsButton.setVisible(false);
			break;


		default:

		}


		if(doCreate && statGeo.removeFromConstruction()){
			for(GeoElement listGeo:plotGeoList){
				// add the geo to our view and remove it from EV		
				listGeo.addView(plotPanel.getViewID());
				plotPanel.add(listGeo);
				listGeo.removeView(AbstractApplication.VIEW_EUCLIDIAN);
				app.getEuclidianView().remove(listGeo);
			}
		}
		
		if(histogram != null){
			histogram.setEuclidianVisible(settings.showHistogram);
		histogram.updateRepaint();
		}
		
		
	}




	//============================================================
	//     Event Handlers
	//============================================================


	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if(source instanceof JTextField)
			doTextFieldActionPerformed(source);

		else if(source == minus || source == plus || source == none){
			minus.setSelected(source == minus);
			none.setSelected(source == none);
			plus.setSelected(source == plus);
			if(source == minus) settings.stemAdjust=-1;
			if(source == none) settings.stemAdjust=0;
			if(source == plus) settings.stemAdjust=1;
			updatePlot(true);
		}

		else if(source == optionsButton){
			optionsPanel.setPanel(selectedPlot);
			optionsPanel.setVisible(optionsButton.isSelected());
		}

		else if(source == cbDisplayType){
			if(cbDisplayType.getSelectedItem().equals(MyRenderer.SEPARATOR)){
				cbDisplayType.setSelectedItem(plotMap.get(selectedPlot));
			}
			else{
				selectedPlot = plotMapReverse.get(cbDisplayType.getSelectedItem());
				updatePlot(true);
			}
			optionsPanel.setVisible(false);
			optionsButton.setSelected(false);
		}

	}

	private void doTextFieldActionPerformed(Object source){

		if(source == fldStart){
			settings.classStart = Validation.validateDouble(fldStart, settings.classStart);
		}
		else if(source == fldWidth){
			settings.classWidth = Validation.validateDoublePositive(fldWidth, settings.classWidth);			
		}
		updatePlot(true);
	}


	public void focusLost(FocusEvent e) {
		Object source = e.getSource();
		if(source instanceof JTextField)
			this.doTextFieldActionPerformed(source);
	}

	public void focusGained(FocusEvent e) { }

	

	public void clearPlotGeoList(){
		for(GeoElement geo : plotGeoList){
			if(geo != null){
				geo.remove();
				geo = null;
			}
		}
		plotGeoList.clear();
	}

	
	
	public void removeGeos(){
		clearPlotGeoList();
	}


	public void detachView(){
		//plotPanel.detachView();
	}

	public void updateFonts(){

	}

	public void attachView() {
		plotPanel.attachView();

	}



	//============================================================
	//            Utilities
	//============================================================

	private JPanel flowPanel(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}

	private JPanel flowPanelRight(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}


	private JPanel boxXPanel(JComponent... comp){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}


	public void updateFonts(Font font) { }

	public void updatePanel() { }






	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================

	private static class MyRenderer extends JLabel implements ListCellRenderer {

		public static final String SEPARATOR = "SEPARATOR";
		JSeparator separator;

		public MyRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
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
	//               Export
	// **********************************************
	
	/**
	 * Action to export all GeoElements that are currently displayed in this
	 * panel to a EuclidianView. The viewID for the target EuclidianView is
	 * stored as a property with key "euclidianViewID".
	 * 
	 * This action is passed as a parameter to plotPanel where it is used in
	 * the plotPanel context menu and the EuclidianView transfer handler
	 * when the plot panel is dragged into an EV.
	 */
	AbstractAction exportToEVAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent event){
			Integer euclidianViewID = (Integer) this.getValue("euclidianViewID");
			
			// if null ID then use EV1 unless shift is down, then use EV2
			if(euclidianViewID == null){
				euclidianViewID = app.getShiftDown()? app.getEuclidianView2().getViewID() : app.getEuclidianView().getViewID();
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
	public void exportGeosToEV(int euclidianViewID){

		app.setWaitCursor();
		AbstractEuclidianView targetEV  = (AbstractEuclidianView) app.getView(euclidianViewID);
		
		try {
			app.storeUndoInfo();

			// prepare the data list to display in the EV (e.g. set labels, auxiliary = false)
			GeoList dataListSelected = statDialog.getStatDialogController().getDataSelected();
			prepareGeoForEV(dataListSelected, euclidianViewID);
			

			// update the plot to get a new set of geos that exist in the construction
			statGeo.setRemoveFromConstruction(false);
			updatePlot(true);

			// remove the histogram from the plot geo list if it is not showing
			if(histogram != null && settings.showHistogram == false){
				plotGeoList.remove(histogram);
				histogram.remove();
				histogram = null;
			}

			// prepare the new geos to display in the EV (e.g. set labels, auxiliary = false)
			for(GeoElement geo: plotGeoList){
				prepareGeoForEV(geo, euclidianViewID);
			}
			
			GeoElement regressionCopy = null;
			if(statDialog.getMode() == StatDialog.MODE_REGRESSION 
					&& statDialog.getRegressionMode() != StatDialog.REG_NONE)
			{
				regressionCopy = statGeo.createRegressionPlot(dataListSelected,
						statDialog.getRegressionMode(), statDialog.getRegressionOrder(), false);
				prepareGeoForEV(regressionCopy, euclidianViewID);
			}

			// set the window dimensions of the target EV to match the plotPanel dimensions
			
			targetEV.setRealWorldCoordSystem(settings.xMin, settings.xMax, settings.yMin, settings.yMax);
			targetEV.setAutomaticAxesNumberingDistance(settings.xAxesIntervalAuto, 0);
			targetEV.setAutomaticAxesNumberingDistance(settings.yAxesIntervalAuto, 1);

			if(!settings.xAxesIntervalAuto){
				targetEV.setAxesNumberingDistance(settings.xAxesInterval, 0);
			}
			if(!settings.yAxesIntervalAuto){
				targetEV.setAxesNumberingDistance(settings.yAxesInterval, 1);
			}
			targetEV.updateBackground();			
						

			// null our display geos and clear the plotGeoList to unlink the new geos
			boxPlotTitles = null;
			histogram = null;
			dotPlot = null;
			frequencyPolygon = null;
			normalCurve = null; 
			scatterPlotLine = null;
			
			if(scatterPlot != null){
				scatterPlot.remove(); // dataListSelected already gives the scatterplot 
				scatterPlot = null;
			}
			
			if(residualPlot != null){
				residualPlot = null;
				regressionCopy.remove();
				dataListSelected.setEuclidianVisible(false); // hide the dataListSelected scatterplot 
				dataListSelected.updateRepaint();				
			}
			
			
			//TODO: in multivar mode create dynamic boxplots linked to separate lists
			if(statDialog.getMode() == StatDialog.MODE_MULTIVAR){
				dataListSelected.remove();
			}
			
			
			plotGeoList.clear();

			
			statDialog.getStatDialogController().removeRegressionGeo();
			statDialog.getStatDialogController().removeDataListSelected();
			statDialog.getStatDialogController().loadDataLists();
			
			//update the plot in removeFromConstruction mode to get a new set of geos for our plot
			statGeo.setRemoveFromConstruction(true);
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
	private static void prepareGeoForEV(GeoElement geo, int euclidianViewID){

		geo.setLabel(null);
		geo.setEuclidianVisible(true);
		geo.setAuxiliaryObject(false);
		if(euclidianViewID == AbstractApplication.VIEW_EUCLIDIAN){
			geo.addView(AbstractApplication.VIEW_EUCLIDIAN);
			geo.removeView(AbstractApplication.VIEW_EUCLIDIAN2);
			geo.update();
		}
		if(euclidianViewID == AbstractApplication.VIEW_EUCLIDIAN2){
			geo.addView(AbstractApplication.VIEW_EUCLIDIAN2);
			geo.removeView(AbstractApplication.VIEW_EUCLIDIAN);
			geo.update();
		}

	}


} 


