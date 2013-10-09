/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.dialog;

import geogebra.awt.GColorD;
import geogebra.awt.GFontD;
import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.gui.dialog.options.model.AuxObjectModel;
import geogebra.common.gui.dialog.options.model.AuxObjectModel.IAuxObjectListener;
import geogebra.common.gui.dialog.options.model.BackgroundImageModel;
import geogebra.common.gui.dialog.options.model.BackgroundImageModel.IBackroundImageListener;
import geogebra.common.gui.dialog.options.model.ColorObjectModel;
import geogebra.common.gui.dialog.options.model.ColorObjectModel.IColorObjectListener;
import geogebra.common.gui.dialog.options.model.FixObjectModel;
import geogebra.common.gui.dialog.options.model.FixObjectModel.IFixObjectListener;
import geogebra.common.gui.dialog.options.model.ListAsComboModel;
import geogebra.common.gui.dialog.options.model.ListAsComboModel.IListAsComboListener;
import geogebra.common.gui.dialog.options.model.ObjectNameModel;
import geogebra.common.gui.dialog.options.model.ObjectNameModel.IObjectNameListener;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import geogebra.common.gui.dialog.options.model.ReflexAngleModel.IReflexAngleListener;
import geogebra.common.gui.dialog.options.model.ShowConditionModel;
import geogebra.common.gui.dialog.options.model.ShowConditionModel.IShowConditionListener;
import geogebra.common.gui.dialog.options.model.ShowLabelModel;
import geogebra.common.gui.dialog.options.model.ShowLabelModel.IShowLabelListener;
import geogebra.common.gui.dialog.options.model.ShowObjectModel;
import geogebra.common.gui.dialog.options.model.ShowObjectModel.IShowObjectListener;
import geogebra.common.gui.dialog.options.model.TraceModel;
import geogebra.common.gui.dialog.options.model.TraceModel.ITraceListener;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Locateable;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoBarChart;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoIntersectAbstract;
import geogebra.common.kernel.algos.AlgoSlope;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.AngleProperties;
import geogebra.common.kernel.geos.Furniture;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoCanvasImage;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement.FillType;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoTextField;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.geos.LimitedPath;
import geogebra.common.kernel.geos.PointProperties;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.kernelND.CoordStyle;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoLevelOfDetail;
import geogebra.common.kernel.kernelND.GeoPlaneND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.common.main.Localization;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.euclidian.EuclidianViewD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.color.GeoGebraColorChooser;
import geogebra.gui.inputfield.AutoCompleteTextFieldD;
import geogebra.gui.inputfield.GeoGebraComboBoxEditor;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.properties.AnimationSpeedPanel;
import geogebra.gui.properties.AnimationStepPanel;
import geogebra.gui.properties.SliderPanel;
import geogebra.gui.properties.UpdateablePropertiesPanel;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.util.SpringUtilities;
import geogebra.gui.view.algebra.InputPanelD;
import geogebra.gui.view.spreadsheet.MyTableD;
import geogebra.main.AppD;
import geogebra.main.LocalizationD;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PropertiesPanel for displaying all gui elements for changing properties of
 * currently selected GeoElements.
 * 
 * @see #update(Graphics) PropertiesPanel
 * @author Markus Hohenwarter
 */
public class PropertiesPanelD extends JPanel implements SetLabels, UpdateFonts {
	private static final int MAX_COMBOBOX_ENTRIES = 200;

	AppD app;
	LocalizationD loc;
	private Kernel kernel;
	private GeoGebraColorChooser colChooser;

	private static final long serialVersionUID = 1L;
	private NamePanel namePanel;
	private ShowObjectPanel showObjectPanel;
	private SelectionAllowedPanel selectionAllowed;
	private ShowTrimmedIntersectionLines showTrimmedIntersectionLines;
	private ColorPanel colorPanel;
	private LabelPanel labelPanel;
	private TooltipPanel tooltipPanel;
	private LayerPanel layerPanel; // Michael Borcherds 2008-02-26
	private CoordPanel coordPanel;
	private LineEqnPanel lineEqnPanel;
	private ConicEqnPanel conicEqnPanel;
	private PointSizePanel pointSizePanel;
	private PointStylePanel pointStylePanel; // Florian Sonner 2008-07-17
	private TextOptionsPanel textOptionsPanel;
	private ArcSizePanel arcSizePanel;
	private LineStylePanel lineStylePanel;
	private LineStyleHiddenPanel lineStylePanelHidden;
	// added by Loic BEGIN
	private DecoSegmentPanel decoSegmentPanel;
	private DecoAnglePanel decoAnglePanel;
	private RightAnglePanel rightAnglePanel;
	// END

	FillingPanel fillingPanel;
	private FadingPanel fadingPanel;
	private LodPanel lodPanel;
	private CheckBoxInterpolateImage checkBoxInterpolateImage;
	private TracePanel tracePanel;
	private AnimatingPanel animatingPanel;
	private FixPanel fixPanel;
	private IneqStylePanel ineqStylePanel;
	private CheckBoxFixPanel checkBoxFixPanel;
	private AllowReflexAnglePanel allowReflexAnglePanel;
	private AllowOutlyingIntersectionsPanel allowOutlyingIntersectionsPanel;
	private AuxiliaryObjectPanel auxPanel;
	private AnimationStepPanel animStepPanel;
	private TextfieldSizePanel textFieldSizePanel;
	private AnimationSpeedPanel animSpeedPanel;
	private SliderPanel sliderPanel;
	private SlopeTriangleSizePanel slopeTriangleSizePanel;
	private StartPointPanel startPointPanel;
	private CornerPointsPanel cornerPointsPanel;
	private TextEditPanel textEditPanel;
	private ScriptEditPanel scriptEditPanel;
	private BackgroundImagePanel bgImagePanel;
	private AbsoluteScreenLocationPanel absScreenLocPanel;
	private ListsAsComboBoxPanel comboBoxPanel;
	// private ShowView2D showView2D;
	private ShowConditionPanel showConditionPanel;
	private ColorFunctionPanel colorFunctionPanel;

	private GraphicsViewLocationPanel graphicsViewLocationPanel;
	private ButtonSizePanel buttonSizePanel;
	// private CoordinateFunctionPanel coordinateFunctionPanel;

	private TabPanel basicTab;
	private TabPanel colorTab;
	private TabPanel styleTab;
	private TabPanel lineStyleTab;
	private TabPanel sliderTab;
	private TabPanel textTab;
	private TabPanel positionTab;
	private TabPanel algebraTab;
	private TabPanel scriptTab;
	private TabPanel advancedTab;

	/**
	 * If just panels should be displayed which are used if the user modifies
	 * the default properties of an object type.
	 */
	boolean isDefaults;

	private JTabbedPane tabs;

	/**
	 * @param app
	 * @param colChooser
	 * @param isDefaults
	 */
 	public PropertiesPanelD(AppD app, GeoGebraColorChooser colChooser,
			boolean isDefaults) {
		this.isDefaults = isDefaults;

		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.colChooser = colChooser;

		// load panels which are hidden for the defaults dialog
		if (!isDefaults) {
			namePanel = new NamePanel(app);
			labelPanel = new LabelPanel();
			tooltipPanel = new TooltipPanel();
			layerPanel = new LayerPanel(); // Michael Borcherds 2008-02-26
			animatingPanel = new AnimatingPanel();
			scriptEditPanel = new ScriptEditPanel();
			textEditPanel = new TextEditPanel();
			startPointPanel = new StartPointPanel();
			cornerPointsPanel = new CornerPointsPanel();
			bgImagePanel = new BackgroundImagePanel();
			showConditionPanel = new ShowConditionPanel(app, this);
			colorFunctionPanel = new ColorFunctionPanel(app, this);

			graphicsViewLocationPanel = new GraphicsViewLocationPanel(app, this);
		}
			
		allowReflexAnglePanel = new AllowReflexAnglePanel();

		

		sliderPanel = new SliderPanel(app, this, false, true);
		showObjectPanel = new ShowObjectPanel();
		selectionAllowed = new SelectionAllowedPanel();
		showTrimmedIntersectionLines = new ShowTrimmedIntersectionLines();
		colorPanel = new ColorPanel(colChooser);
		coordPanel = new CoordPanel();
		lineEqnPanel = new LineEqnPanel();
		conicEqnPanel = new ConicEqnPanel();
		pointSizePanel = new PointSizePanel();
		pointStylePanel = new PointStylePanel(); // Florian Sonner 2008-07-12
		ineqStylePanel = new IneqStylePanel(this);
		textOptionsPanel = new TextOptionsPanel();
		arcSizePanel = new ArcSizePanel();
		slopeTriangleSizePanel = new SlopeTriangleSizePanel();
		lineStylePanel = new LineStylePanel();
		lineStylePanelHidden = new LineStyleHiddenPanel();
		// added by Loic BEGIN
		decoSegmentPanel = new DecoSegmentPanel();
		decoAnglePanel = new DecoAnglePanel();
		rightAnglePanel = new RightAnglePanel();
		// END
		fillingPanel = new FillingPanel();
		fadingPanel = new FadingPanel();
		lodPanel = new LodPanel();
		checkBoxInterpolateImage = new CheckBoxInterpolateImage();
		tracePanel = new TracePanel();
		animatingPanel = new AnimatingPanel();
		fixPanel = new FixPanel();
		checkBoxFixPanel = new CheckBoxFixPanel();
		absScreenLocPanel = new AbsoluteScreenLocationPanel();
		comboBoxPanel = new ListsAsComboBoxPanel();
		// showView2D = new ShowView2D();
		auxPanel = new AuxiliaryObjectPanel();
		animStepPanel = new AnimationStepPanel(app);
		textFieldSizePanel = new TextfieldSizePanel(app);
		animSpeedPanel = new AnimationSpeedPanel(app);
		allowOutlyingIntersectionsPanel = new AllowOutlyingIntersectionsPanel();
		buttonSizePanel = new ButtonSizePanel(app, loc);
		// tabbed pane for properties
		tabs = new JTabbedPane();
		initTabs();

		tabs.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				applyModifications();

			}
		});

		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	/**
	 * apply tabs modifications (text edit panel, etc.)
	 */
	public void applyModifications() {
		if (textEditPanel != null)
			textEditPanel.applyModifications();
		if (scriptEditPanel != null)
			scriptEditPanel.applyModifications();
	}

	public void showSliderTab() {
		tabs.setSelectedIndex(1);
	}

	// added by Loic BEGIN
	public void setSliderMinValue() {
		arcSizePanel.setMinValue();
	}

	// END

	/**
	 * A list of the tab panels
	 */
	private ArrayList<TabPanel> tabPanelList;

	/**
	 * Initialize the tabs
	 */
	private void initTabs() {
		tabPanelList = new ArrayList<TabPanel>();

		// basic tab
		ArrayList<JPanel> basicTabList = new ArrayList<JPanel>();

		if (!isDefaults)
			basicTabList.add(namePanel);

		basicTabList.add(showObjectPanel);

		if (!isDefaults)
			basicTabList.add(labelPanel);

		basicTabList.add(tracePanel);

		if (!isDefaults)
			basicTabList.add(animatingPanel);

		basicTabList.add(fixPanel);
		basicTabList.add(auxPanel);
		basicTabList.add(checkBoxFixPanel);

		if (!isDefaults)
			basicTabList.add(bgImagePanel);

		basicTabList.add(comboBoxPanel);
		//if (!isDefaults)
			basicTabList.add(allowReflexAnglePanel);
		basicTabList.add(rightAnglePanel);
		basicTabList.add(allowOutlyingIntersectionsPanel);
		basicTabList.add(showTrimmedIntersectionLines);

		// basicTabList.add(showView2D);
		basicTab = new TabPanel(basicTabList);
		tabPanelList.add(basicTab);

		// text tab
		ArrayList<JPanel> textTabList = new ArrayList<JPanel>();
		textTabList.add(textOptionsPanel);

		if (!isDefaults) {
			textTabList.add(textEditPanel);
			textOptionsPanel.setEditPanel(textEditPanel);
		} else {
			textOptionsPanel.setEditPanel(null);
		}

		textTab = new TabPanel(textTabList);
		tabPanelList.add(textTab);

		// slider tab
		// if(!isDefaults)
		{
			ArrayList<JPanel> sliderTabList = new ArrayList<JPanel>();
			sliderTabList.add(sliderPanel);
			sliderTab = new TabPanel(sliderTabList);
			tabPanelList.add(sliderTab);
		}

		// color tab
		ArrayList<JPanel> colorTabList = new ArrayList<JPanel>();
		colorTabList.add(colorPanel);
		colorTab = new TabPanel(colorTabList);
		tabPanelList.add(colorTab);

		// style tab
		ArrayList<JPanel> styleTabList = new ArrayList<JPanel>();
		styleTabList.add(slopeTriangleSizePanel);
		styleTabList.add(pointSizePanel);
		styleTabList.add(pointStylePanel);
		styleTabList.add(lineStylePanel);
		styleTabList.add(ineqStylePanel);
		styleTabList.add(lineStylePanelHidden);
		styleTabList.add(arcSizePanel);
		styleTabList.add(buttonSizePanel);
		styleTabList.add(fillingPanel);
		styleTabList.add(fadingPanel);
		styleTabList.add(lodPanel);
		styleTabList.add(checkBoxInterpolateImage);
		styleTabList.add(textFieldSizePanel);
		styleTab = new TabPanel(styleTabList);
		tabPanelList.add(styleTab);

		// decoration
		ArrayList<JPanel> decorationTabList = new ArrayList<JPanel>();
		decorationTabList.add(decoAnglePanel);
		decorationTabList.add(decoSegmentPanel);
		lineStyleTab = new TabPanel(decorationTabList);
		tabPanelList.add(lineStyleTab);

		// filling style
		// ArrayList fillingTabList = new ArrayList();
		// fillingTabList.add(fillingPanel);
		// TabPanel fillingTab = new TabPanel(app.getPlain("Filling"),
		// fillingTabList);
		// fillingTab.addToTabbedPane(tabs);

		// position
		if (!isDefaults) {
			ArrayList<JPanel> positionTabList = new ArrayList<JPanel>();

			positionTabList.add(startPointPanel);
			positionTabList.add(cornerPointsPanel);

			positionTabList.add(absScreenLocPanel);

			positionTab = new TabPanel(positionTabList);
			tabPanelList.add(positionTab);

		}

		// algebra tab
		ArrayList<JPanel> algebraTabList = new ArrayList<JPanel>();
		algebraTabList.add(coordPanel);
		algebraTabList.add(lineEqnPanel);
		algebraTabList.add(conicEqnPanel);
		algebraTabList.add(animStepPanel);
		algebraTabList.add(animSpeedPanel);
		algebraTab = new TabPanel(algebraTabList);
		tabPanelList.add(algebraTab);

		// advanced tab
		if (!isDefaults) {
			ArrayList<JPanel> advancedTabList = new ArrayList<JPanel>();

			advancedTabList.add(showConditionPanel);
			advancedTabList.add(colorFunctionPanel);

			// advancedTabList.add(coordinateFunctionPanel);
			advancedTabList.add(layerPanel); // Michael Borcherds 2008-02-26

			advancedTabList.add(tooltipPanel);

			advancedTabList.add(selectionAllowed);

			// =================================================
			// add location panel
			advancedTabList.add(graphicsViewLocationPanel);
			// ===================================================

			advancedTab = new TabPanel(advancedTabList);
			tabPanelList.add(advancedTab);
		}

		// javascript tab
		if (!isDefaults) {
			ArrayList<JPanel> scriptTabList = new ArrayList<JPanel>();
			// scriptTabList.add(scriptOptionsPanel);

			scriptTabList.add(scriptEditPanel);

			scriptTab = new TabPanel(scriptTabList);
			tabPanelList.add(scriptTab);
		}

		setLabels();
	}

	/**
	 * Update the labels of this panel in case the user language was changed.
	 */
	public void setLabels() {

		// update labels of tabs
		// TODO change label for script tab
		basicTab.setTitle(app.getMenu("Properties.Basic"));
		colorTab.setTitle(app.getPlain("Color"));
		styleTab.setTitle(app.getMenu("Properties.Style"));
		lineStyleTab.setTitle(app.getPlain("Decoration"));
		textTab.setTitle(app.getPlain("Text"));
		algebraTab.setTitle(app.getMenu("Properties.Algebra"));
		sliderTab.setTitle(app.getPlain("Slider"));

		if (!isDefaults) {
			positionTab.setTitle(app.getMenu("Properties.Position"));
			scriptTab.setTitle(app.getPlain("Scripting"));
			advancedTab.setTitle(app.getMenu("Advanced"));
		}

		// update the labels of the panels
		showObjectPanel.setLabels();
		selectionAllowed.setLabels();
		showTrimmedIntersectionLines.setLabels();
		colChooser.setLabels();
		colorPanel.setLabels();
		coordPanel.setLabels();
		lineEqnPanel.setLabels();
		conicEqnPanel.setLabels();
		pointSizePanel.setLabels();
		pointStylePanel.setLabels();
		textOptionsPanel.setLabels();
		arcSizePanel.setLabels();
		lineStylePanel.setLabels();
		ineqStylePanel.setLabels();
		lineStylePanelHidden.setLabels();
		decoSegmentPanel.setLabels();
		decoAnglePanel.setLabels();
		rightAnglePanel.setLabels();
		fillingPanel.setLabels();
		fadingPanel.setLabels();
		lodPanel.setLabels();
		checkBoxInterpolateImage.setLabels();
		tracePanel.setLabels();
		fixPanel.setLabels();
		checkBoxFixPanel.setLabels();
		allowOutlyingIntersectionsPanel.setLabels();
		auxPanel.setLabels();
		animStepPanel.setLabels();
		animSpeedPanel.setLabels();
		slopeTriangleSizePanel.setLabels();
		absScreenLocPanel.setLabels();
		comboBoxPanel.setLabels();
		// showView2D.setLabels();
		sliderPanel.setLabels();
		buttonSizePanel.setLabels();
		if (!isDefaults) {
			namePanel.setLabels();
			labelPanel.setLabels();
			tooltipPanel.setLabels();
			layerPanel.setLabels();
			animatingPanel.setLabels();
			scriptEditPanel.setLabels();
			textEditPanel.setLabels();
			startPointPanel.setLabels();
			cornerPointsPanel.setLabels();
			bgImagePanel.setLabels();
			showConditionPanel.setLabels();
			colorFunctionPanel.setLabels();
			graphicsViewLocationPanel.setLabels();
		}
		

		allowReflexAnglePanel.setLabels();

		// remember selected tab
		Component selectedTab = tabs.getSelectedComponent();

		// update tab labels
		tabs.removeAll();
		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			tp.addToTabbedPane(tabs);
		}

		// switch back to previously selected tab
		if (tabs.getTabCount() > 0) {
			int index = tabs.indexOfComponent(selectedTab);
			tabs.setSelectedIndex(Math.max(0, index));
			// tabs.setVisible(true);
		} else {
			// tabs.setVisible(false);
		}
	}

	public void updateFonts() {

		Font font = app.getPlainFont();

		tabs.setFont(font);

		// update the labels of the panels
		showObjectPanel.updateFonts();
		selectionAllowed.updateFonts();
		showTrimmedIntersectionLines.updateFonts();
		colorPanel.updateFonts();
		colChooser.updateFonts();
		coordPanel.updateFonts();
		lineEqnPanel.updateFonts();
		conicEqnPanel.updateFonts();
		pointSizePanel.updateFonts();
		pointStylePanel.updateFonts();
		textOptionsPanel.updateFonts();
		arcSizePanel.updateFonts();
		lineStylePanel.updateFonts();
		ineqStylePanel.updateFonts();
		lineStylePanelHidden.updateFonts();
		decoSegmentPanel.updateFonts();
		decoAnglePanel.updateFonts();
		rightAnglePanel.updateFonts();
		fillingPanel.updateFonts();
		fadingPanel.updateFonts();
		lodPanel.updateFonts();
		checkBoxInterpolateImage.updateFonts();
		tracePanel.updateFonts();
		fixPanel.updateFonts();
		checkBoxFixPanel.updateFonts();
		allowOutlyingIntersectionsPanel.updateFonts();
		auxPanel.updateFonts();
		animStepPanel.updateFonts();
		animSpeedPanel.updateFonts();
		slopeTriangleSizePanel.updateFonts();
		absScreenLocPanel.updateFonts();
		comboBoxPanel.updateFonts();
		// showView2D.updateFonts();
		sliderPanel.updateFonts();
		buttonSizePanel.updateFonts();
		if (!isDefaults) {
			namePanel.updateFonts();
			labelPanel.updateFonts();
			tooltipPanel.updateFonts();
			layerPanel.updateFonts();
			animatingPanel.updateFonts();
			scriptEditPanel.updateFonts();
			textEditPanel.updateFonts();
			startPointPanel.updateFonts();
			cornerPointsPanel.updateFonts();
			bgImagePanel.updateFonts();
			showConditionPanel.updateFonts();
			colorFunctionPanel.updateFonts();
			graphicsViewLocationPanel.updateFonts();
		}
		
		allowReflexAnglePanel.updateFonts();


	}

	/**
	 * Update all tabs after new GeoElements were selected.
	 * 
	 * @param geos
	 */
	private void updateTabs(Object[] geos) {
		if (geos.length == 0) {
			tabs.setVisible(false);
			return;
		}

		// remember selected tab
		Component selectedTab = tabs.getSelectedComponent();

		tabs.removeAll();
		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			tp.update(geos);
			tp.addToTabbedPane(tabs);
		}

		// switch back to previously selected tab
		if (tabs.getTabCount() > 0) {
			int index = tabs.indexOfComponent(selectedTab);
			tabs.setSelectedIndex(Math.max(0, index));
			tabs.setVisible(true);
		} else
			tabs.setVisible(false);
	}

	private static boolean updateTabPanel(TabPanel tabPanel,
			ArrayList<JPanel> tabList, Object[] geos) {
		// update all panels and their visibility
		boolean oneVisible = false;
		int size = tabList.size();
		for (int i = 0; i < size; i++) {
			UpdateablePropertiesPanel up = (UpdateablePropertiesPanel) tabList
					.get(i);
			boolean show = (up.update(geos) != null);
			up.setVisible(show);
			if (show)
				oneVisible = true;
		}

		return oneVisible;
	}

	public void updateSelection(Object[] geos) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		updateTabs(geos);
	}

	public void updateVisualStyle(GeoElement geo) {

		for (int i = 0; i < tabPanelList.size(); i++) {
			TabPanel tp = tabPanelList.get(i);
			if (tp != null)
				tp.updateVisualStyle(geo);
		}

	}

	public void updateSelection(GeoElement geo) {
		// if (geos == oldSelGeos) return;
		// oldSelGeos = geos;

		updateSelection(new GeoElement[] { geo });
	}

	/**
	 * Update just definition of one geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateOneGeoDefinition(GeoElement geo) {
		namePanel.updateDef(geo);
	}

	/**
	 * Update just name of one geo
	 * 
	 * @param geo
	 *            geo
	 */
	public void updateOneGeoName(GeoElement geo) {
		namePanel.updateName(geo);
	}

	private class TabPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private String title;
		private ArrayList<JPanel> panelList;
		private boolean makeVisible = true;

		public TabPanel(ArrayList<JPanel> pVec) {
			panelList = pVec;

			setLayout(new BorderLayout());

			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			panel.setLayout(new FullWidthLayout());

			for (int i = 0; i < pVec.size(); i++) {
				panel.add(pVec.get(i));
			}

			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
			add(scrollPane, BorderLayout.CENTER);
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void update(Object[] geos) {
			makeVisible = updateTabPanel(this, panelList, geos);
		}

		public void updateVisualStyle(GeoElement geo) {

			for (int i = 0; i < panelList.size(); i++) {
				UpdateablePropertiesPanel up = (UpdateablePropertiesPanel) panelList
						.get(i);
				up.updateVisualStyle(geo);
			}

		}

		public void addToTabbedPane(JTabbedPane tabs) {
			if (makeVisible) {
				tabs.addTab(title, this);
			}
		}
	}

	/**
	 * panel with show/hide object checkbox
	 */
	private class ShowObjectPanel extends JPanel implements ItemListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, IShowObjectListener {

		private static final long serialVersionUID = 1L;
		private JCheckBox showObjectCB;
		private ShowObjectModel model;

		public ShowObjectPanel() {
			app.setFlowLayoutOrientation(this);
			model = new ShowObjectModel(this);
			// check box for show object
			showObjectCB = new JCheckBox();
			showObjectCB.addItemListener(this);
			add(showObjectCB);
		}

		public void setLabels() {
			showObjectCB.setText(app.getPlain("ShowObject"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			showObjectCB.removeItemListener(this);

			model.updateProperties();
			
			showObjectCB.addItemListener(this);
			return this;
		}

		public void updateCheckbox(boolean equalObjectVal,  boolean showObjectCondition) {
			if (equalObjectVal) {
				GeoElement geo0 = (GeoElement)model.getGeos()[0];
				showObjectCB.setSelected(geo0.isEuclidianVisible());
			}
			else {
				showObjectCB.setSelected(false);
			}
		
			showObjectCB.setEnabled(!showObjectCondition);
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == showObjectCB) {
				model.applyChanges(showObjectCB.isSelected());
			}
			updateSelection(model.getGeos());
		}

		public void updateFonts() {
			Font font = app.getPlainFont();
			showObjectCB.setFont(font);

		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // ShowObjectPanel

	/**
	 * panel with show/hide object checkbox
	 */
	private class SelectionAllowedPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox selectionAllowedCB;

		public SelectionAllowedPanel() {
			setLayout(new FlowLayout(FlowLayout.LEFT));

			// check box for show object
			selectionAllowedCB = new JCheckBox();
			selectionAllowedCB.addItemListener(this);
			add(selectionAllowedCB);
		}

		public void setLabels() {
			selectionAllowedCB.setText(app.getPlain("SelectionAllowed"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			selectionAllowedCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalObjectVal = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isSelectionAllowed() != temp.isSelectionAllowed()) {
					equalObjectVal = false;
					break;
				}

			}

			// set object visible checkbox
			if (equalObjectVal)
				selectionAllowedCB.setSelected(geo0.isSelectionAllowed());
			else
				selectionAllowedCB.setSelected(false);

			selectionAllowedCB.setEnabled(true);

			selectionAllowedCB.addItemListener(this);
			return this;
		}

		// show everything
		private boolean checkGeos(Object[] geos) {
			return true;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == selectionAllowedCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setSelectionAllowed(selectionAllowedCB.isSelected());
					geo.updateRepaint();
				}
			}
			updateSelection(geos);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			selectionAllowedCB.setFont(font);

		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // SelectionAllowedPanel

	/**
	 * panel with show/hide trimmed intersection lines
	 */
	private class ShowTrimmedIntersectionLines extends JPanel implements
			ItemListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showTrimmedLinesCB;

		public ShowTrimmedIntersectionLines() {
			setLayout(new FlowLayout(FlowLayout.LEFT));

			// check box for show object
			showTrimmedLinesCB = new JCheckBox();
			showTrimmedLinesCB.addItemListener(this);
			add(showTrimmedLinesCB);
		}

		public void setLabels() {
			showTrimmedLinesCB.setText(app.getPlain("ShowTrimmed"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showTrimmedLinesCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalObjectVal = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.getShowTrimmedIntersectionLines() != temp
						.getShowTrimmedIntersectionLines()) {
					equalObjectVal = false;
					break;
				}

			}

			// set object visible checkbox
			if (equalObjectVal)
				showTrimmedLinesCB.setSelected(geo0
						.getShowTrimmedIntersectionLines());
			else
				showTrimmedLinesCB.setSelected(false);

			showTrimmedLinesCB.setEnabled(true);

			showTrimmedLinesCB.addItemListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.getParentAlgorithm() instanceof AlgoIntersectAbstract)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == showTrimmedLinesCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setShowTrimmedIntersectionLines(showTrimmedLinesCB
							.isSelected());
					geo.getParentAlgorithm().getInput()[0]
							.setEuclidianVisible(!showTrimmedLinesCB
									.isSelected());
					geo.getParentAlgorithm().getInput()[1]
							.setEuclidianVisible(!showTrimmedLinesCB
									.isSelected());
					geo.getParentAlgorithm().getInput()[0].updateRepaint();
					geo.getParentAlgorithm().getInput()[1].updateRepaint();
					geo.updateRepaint();
				}
			}
			updateSelection(geos);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showTrimmedLinesCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // ShowObjectPanel

	/**
	 * panel to fix checkbox (boolean object)
	 */
	private class CheckBoxFixPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox checkboxFixCB;

		public CheckBoxFixPanel() {
			super();
			app.setFlowLayoutOrientation(this);

			checkboxFixCB = new JCheckBox();
			checkboxFixCB.addItemListener(this);
			add(checkboxFixCB);
		}

		public void setLabels() {
			checkboxFixCB.setText(app.getPlain("FixCheckbox"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			checkboxFixCB.removeItemListener(this);

			// check if properties have same values
			GeoBoolean temp, geo0 = (GeoBoolean) geos[0];
			boolean equalObjectVal = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoBoolean) geos[i];
				// same object visible value
				if (geo0.isCheckboxFixed() != temp.isCheckboxFixed()) {
					equalObjectVal = false;
					break;
				}
			}

			// set object visible checkbox
			if (equalObjectVal)
				checkboxFixCB.setSelected(geo0.isCheckboxFixed());
			else
				checkboxFixCB.setSelected(false);

			checkboxFixCB.addItemListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof GeoBoolean) {
					GeoBoolean bool = (GeoBoolean) geos[i];
					if (!bool.isIndependent()) {
						return false;
					}
				} else
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == checkboxFixCB) {
				for (int i = 0; i < geos.length; i++) {
					GeoBoolean bool = (GeoBoolean) geos[i];
					bool.setCheckboxFixed(checkboxFixCB.isSelected());
					bool.updateRepaint();
				}
			}
			updateSelection(geos);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			checkboxFixCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // CheckBoxFixPanel

	/**
	 * panel color chooser and preview panel
	 */
	private class ColorPanel extends JPanel implements ActionListener,
			UpdateablePropertiesPanel, ChangeListener, SetLabels, UpdateFonts, 	IColorObjectListener {

		private static final long serialVersionUID = 1L;
		private ColorObjectModel model;
		private JLabel previewLabel, currentColorLabel;
		private PreviewPanel previewPanel;
		private JPanel opacityPanel, colorChooserContainer;
		private JRadioButton rbtnForegroundColor, rbtnBackgroundColor;
		private JButton btnClearBackground;

		private JSlider opacitySlider;
		private JPanel previewMetaPanel;

		private Color selectedColor;
		private JPanel southPanel;
		
		// For handle single bar
		private JToggleButton[] selectionBarButtons;
		private int selectedBarButton;
		private JPanel barsPanel;
		private boolean isBarChart = false;

		public ColorPanel(GeoGebraColorChooser colChooser) {
			model = new ColorObjectModel(app, this);
			previewPanel = new PreviewPanel();
			previewLabel = new JLabel();
			currentColorLabel = new JLabel();

			// prepare color chooser
			colChooser.setLocale(app.getLocale());
			colChooser.getSelectionModel().addChangeListener(this);

			// get the color chooser panel
			AbstractColorChooserPanel colorChooserPanel = colChooser
					.getChooserPanels()[0];

			// create opacity slider
			opacitySlider = new JSlider(0, 100);
			opacitySlider.setMajorTickSpacing(25);
			opacitySlider.setMinorTickSpacing(5);
			opacitySlider.setPaintTicks(true);
			opacitySlider.setPaintLabels(true);
			opacitySlider.setSnapToTicks(true);

			updateSliderFonts();

			rbtnForegroundColor = new JRadioButton();
			rbtnBackgroundColor = new JRadioButton();
			ButtonGroup group = new ButtonGroup();
			group.add(rbtnForegroundColor);
			group.add(rbtnBackgroundColor);
			rbtnForegroundColor.setSelected(true);
			rbtnBackgroundColor.addActionListener(this);
			rbtnForegroundColor.addActionListener(this);

			btnClearBackground = new JButton(
					app.getImageIcon("delete_small.gif"));
			btnClearBackground.setFocusPainted(false);
			btnClearBackground.addActionListener(this);

			// panel to hold color chooser
			colorChooserContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
			colorChooserContainer.add(colorChooserPanel);

			// panel to hold opacity slider
			opacityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			opacityPanel.add(opacitySlider);
			// panel to hold preview
			previewMetaPanel = new JPanel(new FlowLayout());
			previewMetaPanel.add(previewLabel);
			previewMetaPanel.add(previewPanel);
			previewMetaPanel.add(currentColorLabel);

			// vertical box panel that stacks the preview and opacity slider
			// together
			southPanel = new JPanel();
			southPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.gridx = 0;
			southPanel.add(previewMetaPanel, c);
			southPanel.add(opacityPanel, c);
			c.gridwidth = 2;
			southPanel.add(rbtnForegroundColor, c);
			c.gridwidth = 1;
			southPanel.add(rbtnBackgroundColor, c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(0, 30, 0, 0);
			southPanel.add(btnClearBackground, c);

			// put the sub-panels together
			setLayout(new BorderLayout());
			add(colorChooserContainer, BorderLayout.NORTH);
			add(southPanel, loc.borderWest());
		}
		/**
		 * Extended JPanel that draws a preview rectangle filled with the color
		 * of the currently selected GeoElement(s). If the geo is fillable the
		 * panel paints a transparent rectangle using the geo's alpha value. An
		 * opaque 2 pixel border is drawn around the transparent interior.
		 * 
		 */
		protected class PreviewPanel extends JPanel {

			private static final long serialVersionUID = 1L;

			private Color alphaFillColor;

			public PreviewPanel() {
				setPreferredSize(new Dimension(80, app.getGUIFontSize() + 16));
				setMaximumSize(this.getPreferredSize());
				this.setBorder(BorderFactory.createEmptyBorder());
				this.setBackground(null);
				this.setOpaque(true);
			}

			/**
			 * Sets the preview colors.
			 * 
			 * @param color
			 * @param alpha
			 */
			public void setPreview(Color color, float alpha) {

				if (color == null) {
					alphaFillColor = getBackground();
					setForeground(getBackground());
				} else {
					float[] rgb = new float[3];
					color.getRGBColorComponents(rgb);
					alphaFillColor = new Color(rgb[0], rgb[1], rgb[2], alpha);
					setForeground(new Color(rgb[0], rgb[1], rgb[2], 1f));
				}
				this.repaint();
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				Graphics2D g2 = (Graphics2D) g;
				Insets insets = getInsets();
				int w = this.getWidth() - insets.left - insets.right;
				int h = this.getHeight() - insets.top - insets.bottom;

				g2.setPaint(Color.WHITE);
				g.fillRect(insets.left, insets.top, w, h);

				g2.setPaint(alphaFillColor);
				g.fillRect(insets.left, insets.top, w, h);

				g2.setPaint(getForeground());
				g2.setStroke(new BasicStroke(3));
				g.drawRect(insets.left + 3, insets.top + 3, w - 7, h - 7);

				g2.setPaint(Color.LIGHT_GRAY);
				g2.setStroke(new BasicStroke(1));
				g.drawRect(insets.left, insets.top, w - 1, h - 1);

				g2.setPaint(Color.WHITE);
				g2.setStroke(new BasicStroke(1));
				g2.drawRect(insets.left + 1, insets.top + 1, w - 3, h - 3);

			}
		}

		public void setLabels() {
			previewLabel.setText(app.getMenu("Preview") + ": ");
			opacityPanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Opacity")));
			colChooser.setLocale(app.getLocale());
			rbtnBackgroundColor.setText(app.getMenu("BackgroundColor"));
			rbtnForegroundColor.setText(app.getMenu("ForegroundColor"));
			btnClearBackground.setToolTipText(app.getPlain("Remove"));

			updateToolTipText();
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			addSelectionBar();
			return update();
		}

		public JPanel update() {

			if (!model.checkGeos())
				return null;

			model.updateProperties();
		

			rbtnBackgroundColor.setVisible(model.hasBackground());
			rbtnForegroundColor.setVisible(model.hasBackground());
			btnClearBackground.setVisible(rbtnBackgroundColor.isSelected());
			btnClearBackground.setEnabled(rbtnBackgroundColor.isSelected());
			// hide the color chooser and preview if we have an image
			colorChooserContainer.setVisible(!model.hasImageGeo());
			previewMetaPanel.setVisible(!model.hasImageGeo());
			
			return this;
		}
		
		// Methods that set value for single bar if single bar is selected
		// and bar has tag for value
		
		private void setPreview(GeoElement geo, float alpha) {
			AlgoBarChart algo=(AlgoBarChart) geo.getParentAlgorithm(); 
			if (selectedBarButton != 0
					&& (algo.getBarAlpha(selectedBarButton) != -1 )) {
				alpha=algo.getBarAlpha(selectedBarButton);
			}
			previewPanel.setPreview(selectedColor,alpha );
		}

		private void setOpacitySlider(GeoElement geo, float alpha) {
			/*AlgoBarChart algo=(AlgoBarChart) geo.getParentAlgorithm(); 
			if (selectedBarButton != 0
					&& algo.getBarAlpha(selectedBarButton) != -1) {
				alpha = algo.getBarAlpha(selectedBarButton);
			}
			opacitySlider.setValue(Math.round(alpha * 100));*/
		}

		private void setChooser(GeoElement geo0) {
			if (geo0.getParentAlgorithm() instanceof AlgoBarChart){
			AlgoBarChart algo=(AlgoBarChart) geo0.getParentAlgorithm(); 
			if (selectedBarButton != 0
					&& algo.getBarColor(selectedBarButton) != null) {
				GColor color=algo.getBarColor(selectedBarButton);
				selectedColor = new Color(color.getRed(),
						color.getGreen(),
						color.getBlue(),
						color.getAlpha());
			}
			}
			colChooser.getSelectionModel().setSelectedColor(selectedColor);
		}

		private void updateToolTipText() {
			// set the preview tool tip and color label text for the chosen
			// color
			if (selectedColor == null)
				previewPanel.setToolTipText("");
			else
				previewPanel.setToolTipText(getToolTipText(selectedColor));
			currentColorLabel.setText(previewPanel.getToolTipText());
		}

		/**
		 * Sets the tooltip string for a given color
		 * 
		 * @param color
		 * @return
		 */
		public String getToolTipText(Color color) {
			String name = GeoGebraColorConstants.getGeogebraColorName(app,
					new geogebra.awt.GColorD(color));
			String rgbStr = color.getRed() + ", " + color.getGreen() + ", "
					+ color.getBlue();
			if (name != null) {
				return name + "  " + rgbStr;
			}
			return rgbStr;
		}


		// Add tag for color and alpha or remove if selected all bars
		private void updateBarsColorAndAlpha(GeoElement geo, Color col,
				float alpha, boolean updateAlphaOnly) {
			AlgoBarChart algo=(AlgoBarChart)geo.getParentAlgorithm();
			if (selectedBarButton == 0) {
				for (int i = 1; i < selectionBarButtons.length; i++) {
					algo.setBarColor(null,i);
					algo.setBarAlpha(-1, i);
				}
				geo.setAlphaValue(alpha);
				if (!updateAlphaOnly) {
					geo.setObjColor(new geogebra.awt.GColorD(col));
				}
				algo.setBarAlpha(alpha, selectedBarButton);
				return;
			}
			if (!updateAlphaOnly) {
				algo.setBarColor(new geogebra.awt.GColorD(col), selectedBarButton);
			}
			algo.setBarAlpha(alpha, selectedBarButton);
			// For barchart opacity color  and
			// opacity image have same value if there is a tag
			fillingPanel.fillingSlider.removeChangeListener(fillingPanel);
			fillingPanel.fillingSlider.setValue(Math.round(alpha * 100));
			fillingPanel.fillingSlider.addChangeListener(fillingPanel);
		}

		//Add panel for single bar if is a BarChart
		private void addSelectionBar() {
			if (barsPanel != null) {
				remove(barsPanel);
			} 
			AlgoElement algo = model.getGeoAt(0).getParentAlgorithm();
			if (algo instanceof AlgoBarChart) {
				int numBar = ((AlgoBarChart) algo).getIntervals();
				isBarChart = true;
				selectionBarButtons = new JToggleButton[numBar+1];
				ButtonGroup group=new ButtonGroup();
				barsPanel = new JPanel(new GridLayout(0, 3 ,5,5));
				barsPanel.setBorder(new TitledBorder(app.getPlain("SelectedBar")));
				for (int i = 0; i < numBar + 1; i++) {
					selectionBarButtons[i] = new JToggleButton(app.getPlain("Bar") +" "+ i);
					selectionBarButtons[i].setSelected(false);
					selectionBarButtons[i].setActionCommand(""+i);
					selectionBarButtons[i].addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent arg0) {
							selectedBarButton=Integer.parseInt(((JToggleButton)arg0.getSource()).getActionCommand());
							ColorPanel.this.update();
						}
						
					});
					barsPanel.add(selectionBarButtons[i]);
					group.add(selectionBarButtons[i]);
				}
				selectionBarButtons[0].setText(app.getPlain("AllBars"));
				selectionBarButtons[selectedBarButton].setSelected(true);
				add(barsPanel, loc.borderEast());
			}
		}


		/**
		 * Listens for color chooser state changes
		 */
		public void stateChanged(ChangeEvent e) {

			float alpha = opacitySlider.getValue() / 100.0f;
			GColor color = new geogebra.awt.GColorD(colChooser.getColor());
			if (e.getSource() == opacitySlider)
				model.applyChanges(color, alpha, true);
			else
				model.applyChanges(color, alpha, false);

		}

		/**
		 * action listener implementation for label mode combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == rbtnBackgroundColor || source == rbtnForegroundColor) {
				addSelectionBar();
				update();
			}

			if (source == btnClearBackground) {
				model.clearBackgroundColor();
				addSelectionBar();
				update();
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			previewLabel.setFont(font);
			currentColorLabel.setFont(font);
			opacityPanel.setFont(font);
			// colChooser.setFont(font);
			rbtnBackgroundColor.setFont(font);
			rbtnForegroundColor.setFont(font);
			btnClearBackground.setFont(font);

			updateSliderFonts();

		}

		private void updateSliderFonts() {
			// set slider label font
			Dictionary<?, ?> labelTable = opacitySlider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
		}

		public void updateVisualStyle(GeoElement geo) {
			if (model.getGeos() == null)
				return;
			update();
		}

		public void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground) {
			// initialize selected color and opacity
			selectedColor = null;
			Color selectedBGColor = null;
			float alpha = 1;
			GeoElement geo0 = model.getGeoAt(0);
			if (equalObjColorBackground) {
				selectedBGColor = geogebra.awt.GColorD.getAwtColor(geo0
						.getBackgroundColor());
			}

			if (isBackgroundColorSelected()) {
				selectedColor = selectedBGColor;
			}				
			else {
				// set selectedColor if all selected geos have the same color
				if (equalObjColor) {
					if (allFillable) {
						selectedColor = geogebra.awt.GColorD.getAwtColor(geo0
								.getFillColor());
						alpha = geo0.getAlphaValue();
					} else {
						selectedColor = geogebra.awt.GColorD.getAwtColor(geo0
								.getObjectColor());
					}
				}
			}

			updateToolTipText();

			// set the chooser color
			colChooser.getSelectionModel().removeChangeListener(this);
			if (isBarChart){
				setChooser(geo0);
			} else {
				colChooser.getSelectionModel().setSelectedColor(selectedColor);
			}
			colChooser.getSelectionModel().addChangeListener(this);

			// set the opacity
			opacitySlider.removeChangeListener(this);
			if (allFillable) { // show opacity slider and set to first geo's
								// alpha value
				opacityPanel.setVisible(true);
				alpha = geo0.getAlphaValue();
				if (isBarChart){
					setOpacitySlider(geo0, alpha);
				} else {
					opacitySlider.setValue(Math.round(alpha * 100));
				}
			} else { // hide opacity slider and set alpha = 1
				opacityPanel.setVisible(false);
				alpha = 1;
				opacitySlider.setValue(Math.round(alpha * 100));
			}
			opacitySlider.addChangeListener(this);

			// set the preview panel (do this after the alpha level is set
			// above)
			if (geo0.getParentAlgorithm() instanceof AlgoBarChart){
				isBarChart=true;
				setPreview(geo0, alpha);
			} else {
				isBarChart=false;
				previewPanel.setPreview(selectedColor, alpha);
			}

			
		}

		public void updatePreview(GColor col, float alpha) {
			// update preview panel
			Color color = GColorD.getAwtColor(col);
			previewPanel.setPreview(color, alpha);
			previewPanel.setToolTipText(getToolTipText(color));
			currentColorLabel.setText(previewPanel.getToolTipText());

		}

		public boolean isBackgroundColorSelected() {
			return rbtnBackgroundColor.isSelected();
		}


		public void updateNoBackground(GeoElement geo, GColor col, float alpha,
				boolean updateAlphaOnly, boolean allFillable) {

			Color color = GColorD.getAwtColor(col);
			if (!updateAlphaOnly){
				if (isBarChart){
					updateBarsColorAndAlpha(geo,color,alpha,updateAlphaOnly);
				} else {
					geo.setObjColor(col);
				}
			}
			if (allFillable){
				if (isBarChart){
					updateBarsColorAndAlpha(geo,color,alpha,updateAlphaOnly);
				} else {
					geo.setAlphaValue(alpha);
				}
			}

		}

	} // ColorPanel

	/**
	 * panel with label properties
	 */
	private class LabelPanel extends JPanel implements ItemListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts, IShowLabelListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JCheckBox showLabelCB;
		private JComboBox labelModeCB;
		private ShowLabelModel model;

		public LabelPanel() {
			super();
			model = new ShowLabelModel(app, this);
			// check boxes for show object, show label
			showLabelCB = new JCheckBox();
			showLabelCB.addItemListener(this);

			// combo box for label mode: name or algebra
			labelModeCB = new JComboBox();
			labelModeCB.addActionListener(this);

			// labelPanel with show checkbox
			app.setFlowLayoutOrientation(this);
			add(showLabelCB);
			add(labelModeCB);
		}

		public void setLabels() {
			showLabelCB.setText(app.getPlain("ShowLabel") + ":");

			int selectedIndex = labelModeCB.getSelectedIndex();
			labelModeCB.removeActionListener(this);

			labelModeCB.removeAllItems();
			labelModeCB.addItem(app.getPlain("Name")); // index 0
			labelModeCB.addItem(app.getPlain("NameAndValue")); // index 1
			labelModeCB.addItem(app.getPlain("Value")); // index 2
			labelModeCB.addItem(app.getPlain("Caption")); // index 3 Michael
															// Borcherds

			labelModeCB.setSelectedIndex(selectedIndex);
			labelModeCB.addActionListener(this);

			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			updateShowLabel();
			
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (model.getGeos() == null)
				return;
			update();
		}

		public JPanel update() {
			if (!model.checkGeos())
				return null;

			showLabelCB.removeItemListener(this);
			labelModeCB.removeActionListener(this);

			model.updateProperties();
		
			showLabelCB.addItemListener(this);
			labelModeCB.addActionListener(this);
			
			return this;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show label value changed
			if (source == showLabelCB) {
				model.applyShowChanges(showLabelCB.isSelected());
			}
		}

		/**
		 * action listener implementation for label mode combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == labelModeCB) {
				model.applyModeChanges(labelModeCB.getSelectedIndex());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showLabelCB.setFont(font);
			labelModeCB.setFont(font);

		}
		
		private void updateShowLabel() {
			if (!model.isNameValueShown()) {
				showLabelCB.setText(app.getPlain("ShowLabel"));
			} else {
				showLabelCB.setText(app.getPlain("ShowLabel") + ":");
			}

		}
		
		public void update(boolean isEqualVal, boolean isEqualMode) {
			// change "Show Label:" to "Show Label" if there's no menu
			// Michael Borcherds 2008-02-18
			updateShowLabel();

			GeoElement geo0 = model.getGeoAt(0);
			// set label visible checkbox
			if (isEqualVal) {
				showLabelCB.setSelected(geo0.isLabelVisible());
				labelModeCB.setEnabled(geo0.isLabelVisible());
			} else {
				showLabelCB.setSelected(false);
				labelModeCB.setEnabled(false);
			}

			// set label visible checkbox
			if (isEqualMode)
				labelModeCB.setSelectedIndex(geo0.getLabelMode());
			else
				labelModeCB.setSelectedItem(null);

			// locus in selection
			labelModeCB.setVisible(model.isNameValueShown());

		}

	} // LabelPanel

	/**
	 * panel with label properties
	 */
	private class TooltipPanel extends JPanel implements ItemListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox tooltipModeCB;
		JLabel label;

		public TooltipPanel() {

			label = new JLabel();
			label.setLabelFor(tooltipModeCB);

			// combo box for label mode: name or algebra
			tooltipModeCB = new JComboBox();
			tooltipModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(tooltipModeCB);
		}

		public void setLabels() {

			label.setText(app.getMenu("Tooltip") + ":");

			int selectedIndex = tooltipModeCB.getSelectedIndex();
			tooltipModeCB.removeActionListener(this);

			tooltipModeCB.removeAllItems();
			tooltipModeCB.addItem(app.getMenu("Labeling.automatic")); // index 0
			tooltipModeCB.addItem(app.getMenu("on")); // index 1
			tooltipModeCB.addItem(app.getMenu("off")); // index 2
			tooltipModeCB.addItem(app.getPlain("Caption")); // index 3
			tooltipModeCB.addItem(app.getPlain("NextCell")); // index 4 Michael
																// Borcherds

			tooltipModeCB.setSelectedIndex(selectedIndex);
			tooltipModeCB.addActionListener(this);

		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			tooltipModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLabelMode = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];

				// same tooltip mode
				if (geo0.getLabelMode() != temp.getTooltipMode())
					equalLabelMode = false;

			}

			// set label visible checkbox
			if (equalLabelMode)
				tooltipModeCB.setSelectedIndex(geo0.getTooltipMode());
			else
				tooltipModeCB.setSelectedItem(null);

			// locus in selection
			tooltipModeCB.addActionListener(this);
			return this;
		}

		// show everything but numbers (note: drawable angles are shown)
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!geo.isDrawable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		/**
		 * action listener implementation for label mode combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == tooltipModeCB) {
				GeoElement geo;
				int mode = tooltipModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setTooltipMode(mode);
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
			tooltipModeCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // TooltipPanel

	/*
	 * panel with layers properties Michael Borcherds
	 */
	private class LayerPanel extends JPanel implements ItemListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JComboBox layerModeCB;
		private JLabel layerLabel;

		public LayerPanel() {
			layerLabel = new JLabel();
			layerLabel.setLabelFor(layerModeCB);

			// combo box for label mode: name or algebra
			layerModeCB = new JComboBox();

			for (int layer = 0; layer <= EuclidianStyleConstants.MAX_LAYERS; ++layer) {
				layerModeCB.addItem(" " + layer);
			}

			layerModeCB.addActionListener(this);

			// labelPanel with show checkbox
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(layerLabel);
			add(layerModeCB);
		}

		public void setLabels() {
			layerLabel.setText(app.getPlain("Layer") + ":");
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			layerModeCB.removeActionListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalLayer = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same label visible value
				if (geo0.getLayer() != temp.getLayer())
					equalLayer = false;
			}

			if (equalLayer)
				layerModeCB.setSelectedIndex(geo0.getLayer());
			else
				layerModeCB.setSelectedItem(null);

			// locus in selection
			layerModeCB.addActionListener(this);
			return this;
		}

		// show everything that's drawable
		// don't want layers for dependent numbers as we want to
		// minimise the XML for such objects to keep the spreadsheet fast
		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement) geos[i]).isDrawable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
		}

		/**
		 * action listener implementation for label mode combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == layerModeCB) {
				GeoElement geo;
				int layer = layerModeCB.getSelectedIndex();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLayer(layer);
					geo.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			layerLabel.setFont(font);
			layerModeCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // LayersPanel

	/**
	 * panel for trace
	 * 
	 * @author Markus Hohenwarter
	 */
	private class TracePanel extends JPanel implements ItemListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, ITraceListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showTraceCB;
		private TraceModel model;
		
		public TracePanel() {
			super();
			app.setFlowLayoutOrientation(this);
			model = new TraceModel(this);
			
			// check boxes for show trace
			showTraceCB = new JCheckBox();
			showTraceCB.addItemListener(this);
			add(showTraceCB);
		}

		public void setLabels() {
			showTraceCB.setText(app.getPlain("ShowTrace"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			showTraceCB.removeItemListener(this);

			model.updateProperties();
			
			showTraceCB.addItemListener(this);
			return this;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showTraceCB) {
				model.applyChanges(showTraceCB.isSelected());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showTraceCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateCheckbox(boolean isEqual) {
			// set trace visible checkbox
			if (isEqual) {
				Traceable geo0 = (Traceable) model.getGeoAt(0);
				showTraceCB.setSelected(geo0.getTrace());
			}
			else {
				showTraceCB.setSelected(false);
			}
						
		}
	}

	/**
	 * panel for trace
	 * 
	 * @author adapted from TracePanel
	 */
	private class AnimatingPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox showAnimatingCB;

		public AnimatingPanel() {
			super();
			app.setFlowLayoutOrientation(this);

			// check boxes for animating
			showAnimatingCB = new JCheckBox();
			showAnimatingCB.addItemListener(this);
			add(showAnimatingCB);
		}

		public void setLabels() {
			showAnimatingCB.setText(app.getPlain("Animating"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			showAnimatingCB.removeItemListener(this);

			// check if properties have same values
			GeoElement temp, geo0 = (GeoElement) geos[0];
			boolean equalAnimating = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same object visible value
				if (geo0.isAnimating() != temp.isAnimating())
					equalAnimating = false;
			}

			// set animating checkbox
			if (equalAnimating)
				showAnimatingCB.setSelected(geo0.isAnimating());
			else
				showAnimatingCB.setSelected(false);

			showAnimatingCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement) geos[i]).isAnimatable()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			GeoElement geo;
			Object source = e.getItemSelectable();

			// animating value changed
			if (source == showAnimatingCB) {
				boolean animate = showAnimatingCB.isSelected();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setAnimating(animate);
					geo.updateRepaint();
				}

				// make sure that we are animating
				if (animate)
					kernel.getAnimatonManager().startAnimation();
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showAnimatingCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to say if an image is to be interpolated
	 */
	private class CheckBoxInterpolateImage extends JPanel implements
			ItemListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox checkbox;
		public CheckBoxInterpolateImage() {
			super(new FlowLayout(FlowLayout.LEFT));
			checkbox = new JCheckBox();
			checkbox.addItemListener(this);
			add(checkbox);
		}

		public void setLabels() {
			checkbox.setText(app.getPlain("Interpolate"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;
			if (((GeoElement) geos[0]) instanceof GeoCanvasImage) {				
					checkbox.setVisible(true);
			} else {
				checkbox.setVisible(true);
			}
			checkbox.removeItemListener(this);

			// check if properties have same values
			GeoImage temp, geo0 = (GeoImage) geos[0];
			boolean equalObjectVal = true;

			for (int i = 1; i < geos.length; i++) {
				temp = (GeoImage) geos[i];
				// same object visible value
				if (geo0.isInterpolate() != temp.isInterpolate()) {
					equalObjectVal = false;
					break;
				}
			}

			// set object visible checkbox
			if (equalObjectVal)
				checkbox.setSelected(geo0.isInterpolate());
			else
				checkbox.setSelected(false);

			checkbox.addItemListener(this);
			return this;
		}

		// only images
		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoImage))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets object and label visible state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show object value changed
			if (source == checkbox) {
				for (int i = 0; i < geos.length; i++) {
					GeoImage image = (GeoImage) geos[i];
					image.setInterpolate(checkbox.isSelected());
					image.updateRepaint();
				}
			}
			updateSelection(geos);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			checkbox.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	} // CheckBoxInterpolateImage

	/**
	 * panel for fixing an object
	 * 
	 * @author Markus Hohenwarter
	 */
	private class FixPanel extends JPanel implements ItemListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel, IFixObjectListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private FixObjectModel model;
		private JCheckBox showFixCB;

		public FixPanel() {
			super();
			model = new FixObjectModel(this);
			app.setFlowLayoutOrientation(this);

			// check boxes for show trace
			showFixCB = new JCheckBox();
			showFixCB.addItemListener(this);
			add(showFixCB);
		}

		public void setLabels() {
			showFixCB.setText(app.getPlain("FixObject"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			showFixCB.removeItemListener(this);

			model.updateProperties();

			showFixCB.addItemListener(this);
			return this;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == showFixCB) {
					model.applyChanges(showFixCB.isSelected());
				}
		
			updateSelection(model.getGeos());
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			showFixCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateCheckbox(boolean equalObjectVal) {
			if (equalObjectVal) {
				showFixCB.setSelected(model.getGeoAt(0).isFixed());
			}
			else {
				showFixCB.setSelected(false);
			}
		}
			
	}

	/**
	 * panel to set object's absoluteScreenLocation flag
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AbsoluteScreenLocationPanel extends JPanel implements
			ItemListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox cbAbsScreenLoc;

		public AbsoluteScreenLocationPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// check boxes for show trace
			cbAbsScreenLoc = new JCheckBox();
			cbAbsScreenLoc.addItemListener(this);

			// put it all together
			add(cbAbsScreenLoc);
		}

		public void setLabels() {
			cbAbsScreenLoc.setText(app.getPlain("AbsoluteScreenLocation"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbAbsScreenLoc.removeItemListener(this);

			// check if properties have same values
			boolean pinned = ((GeoElement) geos[0]).isPinned();
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				// same object visible value
				if (((GeoElement) geos[i]).isPinned() != pinned)
					equalVal = false;
			}

			// set checkbox
			if (equalVal)
				cbAbsScreenLoc.setSelected(((GeoElement) geos[0]).isPinned());
			else
				cbAbsScreenLoc.setSelected(false);

			cbAbsScreenLoc.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo instanceof AbsoluteScreenLocateable) {
					AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geo;
					if (!absLoc.isAbsoluteScreenLocateable()
							|| geo.isGeoBoolean() || geo instanceof Furniture)
						return false;
				} else if (!geo.isPinnable()) {
					return false;
				}
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			AbsoluteScreenLocateable geo;
			Object source = e.getItemSelectable();

			// absolute screen location flag changed
			if (source == cbAbsScreenLoc) {
				boolean flag = cbAbsScreenLoc.isSelected();
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				for (int i = 0; i < geos.length; i++) {

					if (geos[i] instanceof AbsoluteScreenLocateable) {

						geo = (AbsoluteScreenLocateable) geos[i];
						if (flag) {
							// convert real world to screen coords
							int x = ev.toScreenCoordX(geo.getRealWorldLocX());
							int y = ev.toScreenCoordY(geo.getRealWorldLocY());
							if (!geo.isAbsoluteScreenLocActive())
								geo.setAbsoluteScreenLoc(x, y);
						} else {
							// convert screen coords to real world
							double x = ev.toRealWorldCoordX(geo
									.getAbsoluteScreenLocX());
							double y = ev.toRealWorldCoordY(geo
									.getAbsoluteScreenLocY());
							if (geo.isAbsoluteScreenLocActive())
								geo.setRealWorldLoc(x, y);
						}
						geo.setAbsoluteScreenLocActive(flag);
						geo.toGeoElement().updateRepaint();
					} else if (((GeoElement) geos[i]).isPinnable()) {
						ArrayList<GeoElement> al = new ArrayList<GeoElement>();
						al.add((GeoElement) geos[i]);

						// geo could be redefined, so need to change geos[i] to
						// new geo
						geos[i] = EuclidianStyleBarStatic.applyFixPosition(al,
								flag, app.getActiveEuclidianView());
					}
				}

				updateSelection(geos);
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			cbAbsScreenLoc.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to set whether GeoLists are drawn as ComboBoxes
	 * 
	 * @author Michael
	 */
	private class ListsAsComboBoxPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, IListAsComboListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ListAsComboModel model;
		private JCheckBox cbComboBox;

		public ListsAsComboBoxPanel() {
			super();
			app.setFlowLayoutOrientation(this);

			model = new ListAsComboModel(this);
			
			// check boxes for show trace
			cbComboBox = new JCheckBox();
			cbComboBox.addItemListener(this);

			// put it all together
			add(cbComboBox);
		}

		public void setLabels() {
			cbComboBox.setText(app.getPlain("DrawAsDropDownList"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			cbComboBox.removeItemListener(this);

			model.updateProperties();

			cbComboBox.addItemListener(this);
			return this;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// absolute screen location flag changed
			if (source == cbComboBox) {
				model.applyChanges(cbComboBox.isSelected());
				app.refreshViews();
				
				updateSelection(model.getGeos());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			cbComboBox.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateComboBox(boolean isEqual) {
			if (isEqual) {
				GeoList geo0 = (GeoList)model.getGeoAt(0);
				cbComboBox.setSelected(geo0.drawAsComboBox());
			}
			else {
				cbComboBox.setSelected(false);
			}			
		}

		public void drawListAsComboBox(GeoList geo, boolean value) {
			app.getActiveEuclidianView().drawListAsComboBox(geo, value);
			
		}
	}

	/**
	 * panel for angles to set whether reflex angles are allowed
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AllowReflexAnglePanel extends JPanel implements
			ActionListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel,
			IReflexAngleListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos

		private JLabel intervalLabel;
		private JComboBox intervalCombo;
		private ReflexAngleModel model;
		private boolean hasOrientation = true;
		private boolean isDrawable = true;

		public AllowReflexAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			model = new ReflexAngleModel(this, app, isDefaults);
			
			intervalLabel = new JLabel();
			intervalCombo = new JComboBox();
			
			add(intervalLabel);
			add(intervalCombo);

		}

		public void setLabels() {
			intervalLabel.setText(app.getPlain("AngleBetween"));

			setComboLabels();
		}

		@SuppressWarnings("unchecked")
		public void setComboLabels() {
			intervalCombo.removeActionListener(this);
			intervalCombo.removeAllItems();
			
			model.fillCombo();
			
			intervalCombo.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}

			intervalCombo.removeActionListener(this);
			model.updateProperties();
			intervalCombo.addActionListener(this);
			return this;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == intervalCombo) {
				model.applyChanges(getIndex());
			}
		}

		private int getIndex() {
			if (hasOrientation) {
				return intervalCombo.getSelectedIndex();
			}
			
			// first interval disabled
			return intervalCombo.getSelectedIndex() + 1;
		}

		public void setSelectedIndex(int index) {
			if (hasOrientation) {
				
				if (index >= intervalCombo.getItemCount()) {
					intervalCombo.setSelectedIndex(0);					
				} else {
					intervalCombo.setSelectedIndex(index);
				}
			} else {
				// first interval disabled
				intervalCombo.setSelectedIndex(index - 1);
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			intervalLabel.setFont(font);
			intervalCombo.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}


		public void updateRepaint(AngleProperties geo) {
			((GeoElementND) geo).updateRepaint();
		}

		public void addComboItem(String item) {
			intervalCombo.addItem(item);
		}

	}

	/**
	 * panel for limted paths to set whether outlying intersection points are
	 * allowed
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AllowOutlyingIntersectionsPanel extends JPanel implements
			ItemListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox outlyingIntersectionsCB;

		public AllowOutlyingIntersectionsPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// check boxes for show trace
			outlyingIntersectionsCB = new JCheckBox();
			outlyingIntersectionsCB.addItemListener(this);

			add(outlyingIntersectionsCB);
		}

		public void setLabels() {
			outlyingIntersectionsCB.setText(app
					.getPlain("allowOutlyingIntersections"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			outlyingIntersectionsCB.removeItemListener(this);

			// check if properties have same values
			LimitedPath temp, geo0 = (LimitedPath) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (LimitedPath) geos[i];
				// same value?
				if (geo0.allowOutlyingIntersections() != temp
						.allowOutlyingIntersections())
					equalVal = false;
			}

			// set trace visible checkbox
			if (equalVal)
				outlyingIntersectionsCB.setSelected(geo0
						.allowOutlyingIntersections());
			else
				outlyingIntersectionsCB.setSelected(false);

			outlyingIntersectionsCB.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof LimitedPath))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			LimitedPath geo;
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == outlyingIntersectionsCB) {
				for (int i = 0; i < geos.length; i++) {
					geo = (LimitedPath) geos[i];
					geo.setAllowOutlyingIntersections(outlyingIntersectionsCB
							.isSelected());
					geo.toGeoElement().updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			outlyingIntersectionsCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to set a background image (only one checkbox)
	 * 
	 * @author Markus Hohenwarter
	 */
	private class BackgroundImagePanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, IBackroundImageListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BackgroundImageModel model;
		private JCheckBox isBGimage;

		public BackgroundImagePanel() {
			super();
			app.setFlowLayoutOrientation(this);
			model = new BackgroundImageModel(this);
			// check boxes for show trace
			isBGimage = new JCheckBox();
			isBGimage.addItemListener(this);
			add(isBGimage);
		}

		public void setLabels() {
			isBGimage.setText(app.getPlain("BackgroundImage"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos()) {
				return null;
			}
			isBGimage.removeItemListener(this);
			model.updateProperties();
			// set trace visible checkbox
		
			isBGimage.addItemListener(this);
			return this;
		}


		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == isBGimage) {
				model.applyChanges(isBGimage.isSelected());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			isBGimage.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateCheckbox(boolean equalIsBGimage) {

			GeoImage geo0 = (GeoImage)model.getGeoAt(0);
			if (equalIsBGimage)
				isBGimage.setSelected(geo0.isInBackground());
			else
				isBGimage.setSelected(false);
	
		}
	}

	/**
	 * panel for making an object auxiliary
	 * 
	 * @author Markus Hohenwarter
	 */
	private class AuxiliaryObjectPanel extends JPanel implements ItemListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, IAuxObjectListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private AuxObjectModel model;
		private JCheckBox auxCB;

		public AuxiliaryObjectPanel() {
			super();
			model = new AuxObjectModel(this);
			app.setFlowLayoutOrientation(this);

			// check boxes for show trace
			auxCB = new JCheckBox();
			auxCB.addItemListener(this);
			add(auxCB);
		}

		public void setLabels() {
			auxCB.setText(app.getPlain("AuxiliaryObject"));
			app.setComponentOrientation(this);
		}

		public JPanel update(Object[] geos) {
			model.setGeos(geos);
			if (!model.checkGeos())
				return null;

			auxCB.removeItemListener(this);

			model.updateProperties();
			
			auxCB.addItemListener(this);
			return this;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			// show trace value changed
			if (source == auxCB) {
				model.applyChanges(auxCB.isSelected());
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			auxCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		public void updateCheckbox(boolean equalObjectVal) {
			// set trace visible checkbox
			if (equalObjectVal)
				auxCB.setSelected(model.getGeoAt(0).isAuxiliaryObject());
			else
				auxCB.setSelected(false);

		}
	}

	/**
	 * panel for location of vectors and text
	 */
	private class StartPointPanel extends JPanel implements ActionListener,
			FocusListener, SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JLabel label;
		private JComboBox cbLocation;
		private DefaultComboBoxModel cbModel;

		public StartPointPanel() {
			// textfield for animation step
			label = new JLabel();
			cbLocation = new JComboBox();
			cbLocation.setEditable(true);
			cbModel = new DefaultComboBoxModel();
			cbLocation.setModel(cbModel);
			label.setLabelFor(cbLocation);
			cbLocation.addActionListener(this);
			cbLocation.addFocusListener(this);
			cbLocation.setEditor(new GeoGebraComboBoxEditor(app, 10));
			// put it all together
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(label);
			add(cbLocation);
		}

		public void setLabels() {
			label.setText(app.getPlain("StartingPoint") + ": ");
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cbLocation.removeActionListener(this);

			// repopulate model with names of points from the geoList's model
			// take all points from construction
			// TreeSet points =
			// kernel.getConstruction().getGeoSetLabelOrder(GeoElement.GEO_CLASS_POINT);
			TreeSet<GeoElement> points = kernel.getPointSet();
			if (points.size() != cbModel.getSize() - 1) {
				cbModel.removeAllElements();
				cbModel.addElement(null);
				Iterator<GeoElement> it = points.iterator();
				int count = 0;
				while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
					GeoElement p = it.next();
					cbModel.addElement(p.getLabel(StringTemplate.editTemplate));
				}
			}

			// check if properties have same values
			Locateable temp, geo0 = (Locateable) geos[0];
			boolean equalLocation = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (Locateable) geos[i];
				// same object visible value
				if (geo0.getStartPoint() != temp.getStartPoint()) {
					equalLocation = false;
					break;
				}

			}

			// set location textfield
			GeoElement p = (GeoElement) geo0.getStartPoint();
			if (equalLocation && p != null) {
				cbLocation.setSelectedItem(p
						.getLabel(StringTemplate.editTemplate));
			} else
				cbLocation.setSelectedItem(null);

			cbLocation.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof Locateable && !((Locateable) geo)
						.isAlwaysFixed()) || geo.isGeoImage()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == cbLocation)
				doActionPerformed();
		}

		private void doActionPerformed() {
			String strLoc = (String) cbLocation.getSelectedItem();
			GeoPointND newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				// newLoc = null;
			} else {
				newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
						true, true);
			}

			for (int i = 0; i < geos.length; i++) {
				Locateable l = (Locateable) geos[i];
				try {
					l.setStartPoint(newLoc);
					l.toGeoElement().updateRepaint();
				} catch (CircularDefinitionException e) {
					app.showError("CircularDefinition");
				}
			}

			updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed();
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			label.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel for three corner points of an image (A, B and D)
	 */
	private class CornerPointsPanel extends JPanel implements ActionListener,
			FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JLabel[] labelLocation;
		private JComboBox[] cbLocation;
		private DefaultComboBoxModel[] cbModel;

		public CornerPointsPanel() {
			labelLocation = new JLabel[3];
			cbLocation = new JComboBox[3];
			cbModel = new DefaultComboBoxModel[3];

			// put it all together
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			// textfield for animation step
			for (int i = 0; i < 3; i++) {
				labelLocation[i] = new JLabel();
				cbLocation[i] = new JComboBox();
				cbLocation[i].setEditable(true);
				cbModel[i] = new DefaultComboBoxModel();
				cbLocation[i].setModel(cbModel[i]);
				labelLocation[i].setLabelFor(cbLocation[i]);
				cbLocation[i].addActionListener(this);
				cbLocation[i].addFocusListener(this);
				cbLocation[i].setEditor(new GeoGebraComboBoxEditor(app, 10));

				JPanel locPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				locPanel.add(labelLocation[i]);
				locPanel.add(cbLocation[i]);
				add(locPanel);
			}

			labelLocation[0].setIcon(app.getImageIcon("corner1.png"));
			labelLocation[1].setIcon(app.getImageIcon("corner2.png"));
			labelLocation[2].setIcon(app.getImageIcon("corner4.png"));
			

		}

		public void setLabels() {
			String strLabelStart = app.getPlain("CornerPoint");

			for (int i = 0; i < 3; i++) {
				int pointNumber = i < 2 ? (i + 1) : (i + 2);
				labelLocation[i].setText(strLabelStart + " " + pointNumber
						+ ":");
			}
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			for (int k = 0; k < 3; k++) {
				cbLocation[k].removeActionListener(this);
			}

			// repopulate model with names of points from the geoList's model
			// take all points from construction
			TreeSet<GeoElement> points = kernel.getConstruction()
					.getGeoSetLabelOrder(GeoClass.POINT);
			if (points.size() != cbModel[0].getSize() - 1) {
				// clear models
				for (int k = 0; k < 3; k++) {
					cbModel[k].removeAllElements();
					cbModel[k].addElement(null);
				}

				// insert points
				Iterator<GeoElement> it = points.iterator();
				int count = 0;
				while (it.hasNext() || ++count > MAX_COMBOBOX_ENTRIES) {
					GeoPointND p = (GeoPointND) it.next();

					for (int k = 0; k < 3; k++) {
						cbModel[k].addElement(p
								.getLabel(StringTemplate.defaultTemplate));
					}
				}
			}

			for (int k = 0; k < 3; k++) {
				// check if properties have same values
				GeoImage temp, geo0 = (GeoImage) geos[0];
				boolean equalLocation = true;

				for (int i = 0; i < geos.length; i++) {
					temp = (GeoImage) geos[i];
					// same object visible value
					if (geo0.getCorner(k) != temp.getCorner(k)) {
						equalLocation = false;
						break;
					}
				}

				// set location textfield
				GeoPoint p = geo0.getCorner(k);
				if (equalLocation && p != null) {
					cbLocation[k].setSelectedItem(p
							.getLabel(StringTemplate.defaultTemplate));
				} else
					cbLocation[k].setSelectedItem(null);

				cbLocation[k].addActionListener(this);
			}
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo instanceof GeoImage) {
					GeoImage img = (GeoImage) geo;
					if (img.isAbsoluteScreenLocActive() || !img.isIndependent())
						return false;
				} else
					return false;
			}
			return true;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			doActionPerformed(e.getSource());
		}

		private void doActionPerformed(Object source) {
			int number = 0;
			if (source == cbLocation[1])
				number = 1;
			else if (source == cbLocation[2])
				number = 2;

			String strLoc = (String) cbLocation[number].getSelectedItem();
			GeoPointND newLoc = null;

			if (strLoc == null || strLoc.trim().length() == 0) {
				// newLoc = null;
			} else {
				newLoc = kernel.getAlgebraProcessor().evaluateToPoint(strLoc,
						true, true);
			}

			for (int i = 0; i < geos.length; i++) {
				GeoImage im = (GeoImage) geos[i];
				im.setCorner((GeoPoint) newLoc, number);
				im.updateRepaint();
			}

			updateSelection(geos);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent e) {
			doActionPerformed(e.getSource());
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			for (int i = 0; i < 3; i++) {
				labelLocation[i].setFont(font);
			}
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel for text editing
	 */
	public class TextEditPanel extends JPanel implements ActionListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private TextInputDialog td;
		private JPanel editPanel;

		public TextEditPanel() {
			td = new TextInputDialog(app, app.getPlain("Text"), null, null, 30,
					5, false);
			setLayout(new BorderLayout());

			editPanel = new JPanel(new BorderLayout(0, 0));
			editPanel.add(td.getInputPanel(), BorderLayout.CENTER);
			editPanel.add(td.getToolBar(), BorderLayout.SOUTH);
			editPanel.setBorder(BorderFactory.createEtchedBorder());

			JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					editPanel, td.getPreviewPanel());
			sp.setResizeWeight(0.5);
			sp.setBorder(BorderFactory.createEmptyBorder());

			add(sp, BorderLayout.CENTER);
			// add(td.getPreviewPanel(), BorderLayout.NORTH);
			add(td.getButtonPanel(), BorderLayout.SOUTH);

		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			td.applyModifications();
		}

		public void setLabels() {
			// editPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Edit")));
			// td.getPreviewPanel().setBorder(BorderFactory.createTitledBorder(app.getMenu("Preview")));
			td.setLabels(app.getPlain("Text"));
		}

		public JPanel update(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos))
				return null;

			GeoText text = (GeoText) geos[0];
			td.setGeoText(text);
			td.updateRecentSymbolTable();

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			return geos.length == 1 && geos[0] instanceof GeoText
					&& !((GeoText) geos[0]).isTextCommand()
					&& !((GeoText) geos[0]).isFixed();
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			// if (e.getSource() == btEdit)
			// app.showTextDialog((GeoText) geos[0]);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			editPanel.setFont(font);
			td.updateFonts();
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel for script editing
	 */
	private class ScriptEditPanel extends JPanel implements ActionListener,
			UpdateablePropertiesPanel, SetLabels, UpdateFonts {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ScriptInputDialog clickDialog, updateDialog, globalDialog;
		private JTabbedPane tabbedPane;
		private JPanel clickScriptPanel, updateScriptPanel, globalScriptPanel;

		public ScriptEditPanel() {
			super(new BorderLayout());

			int row = 35;
			int column = 15;

			tabbedPane = new JTabbedPane();

			clickDialog = new ScriptInputDialog(app, app.getPlain("Script"),
					null, row, column, false, false);
			updateDialog = new ScriptInputDialog(app,
					app.getPlain("JavaScript"), null, row, column, true, false);
			globalDialog = new ScriptInputDialog(app,
					app.getPlain("GlobalJavaScript"), null, row, column, false,
					true);
			setLayout(new BorderLayout());
			// add(td.getInputPanel(), BorderLayout.NORTH);
			// add(td2.getInputPanel(), BorderLayout.CENTER);
			clickScriptPanel = new JPanel(new BorderLayout(0, 0));
			clickScriptPanel.add(clickDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			clickScriptPanel
					.add(clickDialog.getButtonPanel(), loc.borderEast());

			updateScriptPanel = new JPanel(new BorderLayout(0, 0));
			updateScriptPanel.add(
					updateDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			updateScriptPanel.add(updateDialog.getButtonPanel(),
					loc.borderEast());

			globalScriptPanel = new JPanel(new BorderLayout(0, 0));
			globalScriptPanel.add(
					globalDialog.getInputPanel(row, column, true),
					BorderLayout.NORTH);
			globalScriptPanel.add(globalDialog.getButtonPanel(),
					loc.borderEast());

			add(tabbedPane, BorderLayout.CENTER);

			tabbedPane.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					applyModifications();

				}
			});

		}

		/**
		 * apply edit modifications
		 */
		public void applyModifications() {
			clickDialog.applyModifications();
			updateDialog.applyModifications();
			globalDialog.applyModifications();
		}

		public void setLabels() {
			// setBorder(BorderFactory.createTitledBorder(app.getPlain("JavaScript")));
			clickDialog.setLabels(app.getPlain("OnClick"));
			updateDialog.setLabels(app.getPlain("OnUpdate"));
			globalDialog.setLabels(app.getPlain("GlobalJavaScript"));
		}

		public JPanel update(Object[] geos) {
			if (geos.length != 1 || !checkGeos(geos))
				return null;

			// remember selected tab
			Component selectedTab = tabbedPane.getSelectedComponent();

			GeoElement button = (GeoElement) geos[0];
			clickDialog.setGeo(button);
			updateDialog.setGeo(button);
			globalDialog.setGlobal();
			tabbedPane.removeAll();
			if (button.canHaveClickScript())
				tabbedPane.addTab(app.getPlain("OnClick"), clickScriptPanel);
			if (button.canHaveUpdateScript())
				tabbedPane.addTab(app.getPlain("OnUpdate"), updateScriptPanel);
			tabbedPane.addTab(app.getPlain("GlobalJavaScript"),
					globalScriptPanel);

			// select tab as before
			tabbedPane.setSelectedIndex(Math.max(0,
					tabbedPane.indexOfComponent(selectedTab)));

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			// return geos.length == 1 && geos[0] instanceof
			// GeoJavaScriptButton;
			return geos.length == 1;
		}

		/**
		 * handle textfield changes
		 */
		public void actionPerformed(ActionEvent e) {
			// if (e.getSource() == btEdit)
			// app.showTextDialog((GeoText) geos[0]);
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			tabbedPane.setFont(font);
			clickScriptPanel.setFont(font);
			updateScriptPanel.setFont(font);
			globalScriptPanel.setFont(font);

			clickDialog.updateFonts();
			updateDialog.updateFonts();
			globalDialog.updateFonts();

		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the kind of coordinates (cartesian or polar) for GeoPoint
	 * and GeoVector
	 * 
	 * @author Markus Hohenwarter
	 */
	private class CoordPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JLabel coordLabel;
		private JComboBox coordCB;

		public CoordPanel() {
			coordLabel = new JLabel();
			coordCB = new JComboBox();

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(coordLabel);
			add(coordCB);
		}

		public void setLabels() {
			coordLabel.setText(app.getPlain("Coordinates") + ":");

			int selectedIndex = coordCB.getSelectedIndex();
			coordCB.removeActionListener(this);

			coordCB.removeAllItems();
			coordCB.addItem(app.getPlain("CartesianCoords")); // index 0
			coordCB.addItem(app.getPlain("PolarCoords")); // index 1
			coordCB.addItem(app.getPlain("ComplexNumber")); // index 2
			coordCB.addItem(app.getPlain("CartesianCoords3D")); // index 3

			coordCB.setSelectedIndex(selectedIndex);
			coordCB.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			coordCB.removeActionListener(this);

			// check if properties have same values
			CoordStyle geo0 = (CoordStyle) geos[0];
			boolean equalMode = true;

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
			case Kernel.COORD_CARTESIAN:
				coordCB.setSelectedIndex(0);
				break;
			case Kernel.COORD_POLAR:
				coordCB.setSelectedIndex(1);
				break;
			case Kernel.COORD_COMPLEX:
				coordCB.setSelectedIndex(2);
				break;
			case Kernel.COORD_CARTESIAN_3D:
				coordCB.setSelectedIndex(3);
				break;
			default:
				coordCB.setSelectedItem(null);
			}

			coordCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			// boolean allPoints = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(((GeoElement) geos[i]).isGeoPoint() || geos[i] instanceof GeoVector)) {
					geosOK = false;
				}

				// check if fixed
				if (((GeoElement) geos[i]).isFixed())
					geosOK = false;

				// if (!(geos[i] instanceof GeoPoint)) allPoints = false;
			}

			// remove ComplexNumber option if any vectors are in list
			// if (!allPoints && coordCB.getItemCount() == 3)
			// coordCB.removeItemAt(2);

			return geosOK;
		}

		/**
		 * action listener implementation for coord combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == coordCB) {
				CoordStyle geo;
				switch (coordCB.getSelectedIndex()) {
				case 0: // Kernel.CARTESIAN
					for (int i = 0; i < geos.length; i++) {
						geo = (CoordStyle) geos[i];
						geo.setMode(Kernel.COORD_CARTESIAN);
						((GeoElement) geo).updateRepaint();
					}
					break;

				case 1: // Kernel.POLAR
					for (int i = 0; i < geos.length; i++) {
						geo = (CoordStyle) geos[i];
						geo.setMode(Kernel.COORD_POLAR);
						((GeoElement) geo).updateRepaint();
					}
					break;
				case 2: // Kernel.COMPLEX
					for (int i = 0; i < geos.length; i++) {
						geo = (CoordStyle) geos[i];
						geo.setMode(Kernel.COORD_COMPLEX);
						((GeoElement) geo).updateRepaint();
					}
					break;
				case 3: // Kernel.COORD_CARTESIAN_3D
					for (int i = 0; i < geos.length; i++) {
						geo = (CoordStyle) geos[i];
						geo.setMode(Kernel.COORD_CARTESIAN_3D);
						((GeoElement) geo).updateRepaint();
					}
					break;
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			coordLabel.setFont(font);
			coordCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the kind of line equation for GeoLine
	 * 
	 * @author Markus Hohenwarter
	 */
	private class LineEqnPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JComboBox eqnCB;
		private JLabel eqnLabel;

		public LineEqnPanel() {
			eqnLabel = new JLabel();
			eqnCB = new JComboBox();
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}

		public void setLabels() {
			eqnLabel.setText(app.getPlain("Equation") + ":");

			int selectedIndex = eqnCB.getSelectedIndex();
			eqnCB.removeActionListener(this);

			eqnCB.removeAllItems();
			eqnCB.addItem(app.getPlain("ImplicitLineEquation"));
			// index 0
			eqnCB.addItem(app.getPlain("ExplicitLineEquation"));
			// index 1
			eqnCB.addItem(app.getPlain("ParametricForm")); // index 2

			eqnCB.setSelectedIndex(selectedIndex);
			eqnCB.addActionListener(this);
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			// check if properties have same values
			GeoLine temp, geo0 = (GeoLine) geos[0];
			boolean equalMode = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoLine) geos[i];
				// same mode?
				if (geo0.getMode() != temp.getMode())
					equalMode = false;
			}

			int mode;
			if (equalMode)
				mode = geo0.getMode();
			else
				mode = -1;
			switch (mode) {
			case GeoLine.EQUATION_IMPLICIT:
				eqnCB.setSelectedIndex(0);
				break;
			case GeoLine.EQUATION_EXPLICIT:
				eqnCB.setSelectedIndex(1);
				break;
			case GeoLine.PARAMETRIC:
				eqnCB.setSelectedIndex(2);
				break;
			default:
				eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoLine)
						|| geos[i] instanceof GeoSegment) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * action listener implementation for coord combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoLine geo;
				switch (eqnCB.getSelectedIndex()) {
				case 0: // GeoLine.EQUATION_IMPLICIT
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoLine) geos[i];
						geo.setMode(GeoLine.EQUATION_IMPLICIT);
						geo.updateRepaint();
					}
					break;

				case 1: // GeoLine.EQUATION_EXPLICIT
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoLine) geos[i];
						geo.setMode(GeoLine.EQUATION_EXPLICIT);
						geo.updateRepaint();
					}
					break;

				case 2: // GeoLine.PARAMETRIC
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoLine) geos[i];
						geo.setMode(GeoLine.PARAMETRIC);
						geo.updateRepaint();
					}
					break;
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			eqnLabel.setFont(font);
			eqnCB.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the kind of conic equation for GeoConic
	 * 
	 * @author Markus Hohenwarter
	 */
	private class ConicEqnPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private DefaultComboBoxModel eqnCBmodel;
		private JComboBox eqnCB;
		private JLabel eqnLabel;
		int implicitIndex, explicitIndex, specificIndex;

		public ConicEqnPanel() {
			eqnLabel = new JLabel();
			eqnCB = new JComboBox();
			eqnCBmodel = new DefaultComboBoxModel();
			eqnCB.setModel(eqnCBmodel);
			eqnCB.addActionListener(this);

			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(eqnLabel);
			add(eqnCB);
		}

		public void setLabels() {
			eqnLabel.setText(app.getPlain("Equation") + ":");

			if (geos != null)
				update(geos);

			// TODO: Anything else required? (F.S.)
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			eqnCB.removeActionListener(this);

			// check if all conics have same type and mode
			// and if specific, explicit is possible
			GeoConic temp, geo0 = (GeoConic) geos[0];
			boolean equalType = true;
			boolean equalMode = true;
			boolean specificPossible = geo0.isSpecificPossible();
			boolean explicitPossible = geo0.isExplicitPossible();
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoConic) geos[i];
				// same type?
				if (geo0.getType() != temp.getType())
					equalType = false;
				// same mode?
				if (geo0.getToStringMode() != temp.getToStringMode())
					equalMode = false;
				// specific equation possible?
				if (!temp.isSpecificPossible())
					specificPossible = false;
				// explicit equation possible?
				if (!temp.isExplicitPossible())
					explicitPossible = false;
			}

			// specific can't be shown because there are different types
			if (!equalType)
				specificPossible = false;

			specificIndex = -1;
			explicitIndex = -1;
			implicitIndex = -1;
			int counter = -1;
			eqnCBmodel.removeAllElements();
			if (specificPossible) {
				eqnCBmodel.addElement(geo0.getSpecificEquation());
				specificIndex = ++counter;
			}
			if (explicitPossible) {
				eqnCBmodel.addElement(app.getPlain("ExplicitConicEquation"));
				explicitIndex = ++counter;
			}
			implicitIndex = ++counter;
			eqnCBmodel.addElement(app.getPlain("ImplicitConicEquation"));

			int mode;
			if (equalMode)
				mode = geo0.getToStringMode();
			else
				mode = -1;
			switch (mode) {
			case GeoConicND.EQUATION_SPECIFIC:
				if (specificIndex > -1)
					eqnCB.setSelectedIndex(specificIndex);
				break;

			case GeoConicND.EQUATION_EXPLICIT:
				if (explicitIndex > -1)
					eqnCB.setSelectedIndex(explicitIndex);
				break;

			case GeoConicND.EQUATION_IMPLICIT:
				eqnCB.setSelectedIndex(implicitIndex);
				break;

			default:
				eqnCB.setSelectedItem(null);
			}

			eqnCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i].getClass() != GeoConic.class) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * action listener implementation for coord combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == eqnCB) {
				GeoConic geo;
				int selIndex = eqnCB.getSelectedIndex();
				if (selIndex == specificIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToSpecific();
						geo.updateRepaint();
					}
				} else if (selIndex == explicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToExplicit();
						geo.updateRepaint();
					}
				} else if (selIndex == implicitIndex) {
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoConic) geos[i];
						geo.setToImplicit();
						geo.updateRepaint();
					}
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			eqnLabel.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the size of a GeoPoint
	 * 
	 * @author Markus Hohenwarter
	 */
	private class PointSizePanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public PointSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");

			slider = new JSlider(1, 9);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			slider.addChangeListener(this);

			add(slider);
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getPlain("PointSize")));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!checkGeos(geos))
				return null;

			slider.removeChangeListener(this);

			// set value to first point's size
			PointProperties geo0 = (PointProperties) geos[0];
			slider.setValue(geo0.getPointSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
						&& (!(geo.isGeoList() && ((GeoList) geo)
								.showPointProperties()))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				PointProperties point;
				for (int i = 0; i < geos.length; i++) {
					point = (PointProperties) geos[i];
					point.setPointSize(size);
					point.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		private void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());
		}

	}

	/**
	 * panel to change the point style
	 * 
	 * @author Florian Sonner
	 * @version 2008-07-17
	 */
	private class PointStylePanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, ActionListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JComboBox cbStyle; // G.Sturr 2010-1-24

		public PointStylePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// G.STURR 2010-1-24
			// Point styles were previously displayed with fonts,
			// but not all point styles had font equivalents. This is
			// now replaced by a comboBox holding rendered point styles
			// and radio buttons to select default or custom point style.

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18, 18));
			cbStyle = new JComboBox(EuclidianViewD.getPointStyles());
			cbStyle.setRenderer(renderer);
			cbStyle.setMaximumRowCount(EuclidianStyleConstants.MAX_POINT_STYLE + 1);
			cbStyle.setBackground(getBackground());
			cbStyle.addActionListener(this);
			add(cbStyle);

			/*
			 * ----- old code ButtonGroup buttonGroup = new ButtonGroup();
			 * 
			 * String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716" };
			 * String[] strPointStyleAC = { "0", "2", "1" }; buttons = new
			 * JRadioButton[strPointStyle.length];
			 * 
			 * for(int i = 0; i < strPointStyle.length; ++i) { buttons[i] = new
			 * JRadioButton(strPointStyle[i]);
			 * buttons[i].setActionCommand(strPointStyleAC[i]);
			 * buttons[i].addActionListener(this);
			 * buttons[i].setFont(app.getSmallFont());
			 * 
			 * if(!strPointStyleAC[i].equals("-1"))
			 * buttons[i].setFont(app.getSmallFont());
			 * 
			 * buttonGroup.add(buttons[i]); add(buttons[i]); }
			 */

			// END G.STURR

		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getMenu("PointStyle")));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!checkGeos(geos))
				return null;

			// set value to first point's style
			PointProperties geo0 = (PointProperties) geos[0];

			// G.STURR 2010-1-24:
			// update comboBox and radio buttons
			cbStyle.removeActionListener(this);
			if (geo0.getPointStyle() == -1) {
				// select default button
				cbStyle.setSelectedIndex(EuclidianStyleConstants.POINT_STYLE_DOT);
			} else {
				// select custom button and set combo box selection
				cbStyle.setSelectedIndex(geo0.getPointStyle());
			}
			cbStyle.addActionListener(this);

			/*
			 * ----- old code to update radio button group for(int i = 0; i <
			 * buttons.length; ++i) {
			 * if(Integer.parseInt(buttons[i].getActionCommand()) ==
			 * geo0.getPointStyle()) buttons[i].setSelected(true); else
			 * buttons[i].setSelected(false); }
			 */

			// END G.STURR

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (geo.isGeoElement3D()
						|| // TODO add point style to 3D points
						(!geo.getGeoElementForPropertiesDialog().isGeoPoint() && (!(geo
								.isGeoList() && ((GeoList) geo)
								.showPointProperties())))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {

			// G.STURR 2010-1-24:
			// Handle comboBox and radio button clicks

			// int style = Integer.parseInt(e.getActionCommand());
			int style = -1;
			// comboBox click
			if (e.getSource() == cbStyle) {
				style = cbStyle.getSelectedIndex();
			}
			// END G.STURR

			PointProperties point;
			for (int i = 0; i < geos.length; i++) {
				point = (PointProperties) geos[i];
				point.setPointStyle(style);
				point.updateRepaint();
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);
		}

	}

	/**
	 * panel to select the size of a GeoText
	 * 
	 * @author Markus Hohenwarter
	 */
	private class TextOptionsPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel, FocusListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;

		private JLabel decimalLabel;
		private JComboBox cbFont, cbSize, cbDecimalPlaces;
		private JToggleButton btBold, btItalic;

		private JPanel secondLine;
		private boolean secondLineVisible = false;
		private boolean justDisplayFontSize = true;
		private TextEditPanel editPanel;
		private String[] fontSizes = app.getLocalization().getFontSizeStrings();

		public TextOptionsPanel() {
			// font: serif, sans serif
			String[] fonts = { "Sans Serif", "Serif" };
			cbFont = new JComboBox(fonts);
			cbFont.addActionListener(this);

			// font size
			// TODO require font phrases F.S.
			cbSize = new JComboBox(fontSizes);
			cbSize.addActionListener(this);
			cbFont.addFocusListener(this);
			// toggle buttons for bold and italic
			btBold = new JToggleButton();
			btBold.setFont(app.getBoldFont());
			btBold.addActionListener(this);
			btItalic = new JToggleButton();
			btItalic.setFont(app.getPlainFont().deriveFont(Font.ITALIC));
			btItalic.addActionListener(this);

			// decimal places
			ComboBoxRenderer renderer = new ComboBoxRenderer();
			cbDecimalPlaces = new JComboBox(loc.getRoundingMenu());
			cbDecimalPlaces.setRenderer(renderer);
			cbDecimalPlaces.addActionListener(this);

			// font, size
			JPanel firstLine = new JPanel();
			firstLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

			firstLine.add(cbFont);
			firstLine.add(cbSize);
			firstLine.add(btBold);
			firstLine.add(btItalic);

			// bold, italic
			secondLine = new JPanel();
			secondLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
			decimalLabel = new JLabel();
			secondLine.add(decimalLabel);
			secondLine.add(cbDecimalPlaces);

			setLayout(new BorderLayout(5, 5));
			add(firstLine, BorderLayout.NORTH);
			add(secondLine, BorderLayout.SOUTH);
			secondLineVisible = true;
		}

		public void setEditPanel(TextEditPanel tep) {
			this.editPanel = tep;
		}

		public void setLabels() {
			String[] fontSizes = app.getLocalization().getFontSizeStrings();

			int selectedIndex = cbSize.getSelectedIndex();
			cbSize.removeActionListener(this);
			cbSize.removeAllItems();

			for (int i = 0; i < fontSizes.length; ++i) {
				cbSize.addItem(fontSizes[i]);
			}

			cbSize.addItem(app.getMenu("Custom") + "...");

			cbSize.setSelectedIndex(selectedIndex);
			cbSize.addActionListener(this);

			btItalic.setText(app.getPlain("Italic").substring(0, 1));
			btBold.setText(app.getPlain("Bold").substring(0, 1));

			decimalLabel.setText(app.getMenu("Rounding") + ":");
		}

		class ComboBoxRenderer extends JLabel implements ListCellRenderer {
			JSeparator separator;

			public ComboBoxRenderer() {
				setOpaque(true);
				setBorder(new EmptyBorder(1, 1, 1, 1));
				separator = new JSeparator(SwingConstants.HORIZONTAL);
			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String str = (value == null) ? "" : value.toString();
				if ("---".equals(str)) {
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

		public JPanel update(Object[] geos) {

			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!checkGeos(geos))
				return null;

			// hide most options for Textfields
			cbFont.setVisible(!justDisplayFontSize);
			btBold.setVisible(!justDisplayFontSize);
			btItalic.setVisible(!justDisplayFontSize);
			secondLine.setVisible(!justDisplayFontSize);
			secondLineVisible = !justDisplayFontSize;

			if (((GeoElement) geos[0]).isGeoButton()) {
				secondLine.setVisible(justDisplayFontSize);
				secondLineVisible = justDisplayFontSize;
			}

			cbSize.removeActionListener(this);
			cbFont.removeActionListener(this);
			cbDecimalPlaces.removeActionListener(this);

			// set value to first text's size and style
			TextProperties geo0 = (TextProperties) geos[0];

			cbSize.setSelectedIndex(GeoText.getFontSizeIndex(geo0
					.getFontSizeMultiplier())); // font
			// size
			// ranges
			// from
			// -6
			// to
			// 6,
			// transform
			// this
			// to
			// 0,1,..,6
			cbFont.setSelectedIndex(geo0.isSerifFont() ? 1 : 0);
			int selItem = -1;

			int decimals = geo0.getPrintDecimals();
			if (decimals > 0 && decimals < AppD.decimalsLookup.length
					&& !geo0.useSignificantFigures())
				selItem = AppD.decimalsLookup[decimals];

			int figures = geo0.getPrintFigures();
			if (figures > 0 && figures < AppD.figuresLookup.length
					&& geo0.useSignificantFigures())
				selItem = AppD.figuresLookup[figures];

			cbDecimalPlaces.setSelectedIndex(selItem);

			if (((GeoElement) geo0).isIndependent()
					|| (geo0 instanceof GeoList)) { // don't want rounding
													// option for lists of
													// texts?
				if (secondLineVisible) {
					secondLineVisible = false;
				}
			} else {
				if (!secondLineVisible) {
					secondLineVisible = true;
				}

				secondLine.setVisible(secondLineVisible);
			}

			int style = geo0.getFontStyle();
			btBold.setSelected(style == Font.BOLD
					|| style == (Font.BOLD + Font.ITALIC));
			btItalic.setSelected(style == Font.ITALIC
					|| style == (Font.BOLD + Font.ITALIC));

			cbSize.addActionListener(this);
			cbFont.addActionListener(this);
			cbDecimalPlaces.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			justDisplayFontSize = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];

				if ((geo instanceof TextProperties && !((TextProperties) geo)
						.justFontSize()) || geo.isGeoButton()) {
					justDisplayFontSize = false;
				}

				if (!(geo.getGeoElementForPropertiesDialog().isGeoText())) {
					if (!((GeoElement) geos[i]).isGeoButton()) {
						geosOK = false;
						break;
					}
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == cbSize) {

				double multiplier;

				if (cbSize.getSelectedIndex() == 7) {
					String percentStr = JOptionPane.showInputDialog(
							app.getFrame(),
							app.getPlain("EnterPercentage"),
							Math.round(((TextProperties) geos[0])
									.getFontSizeMultiplier() * 100) + "%");

					if (percentStr == null) {
						// Cancel
						return;
					}
					percentStr = percentStr.replaceAll("%", "");

					try {
						multiplier = StringUtil.parseDouble(percentStr) / 100;

						if (multiplier < 0.01) {
							multiplier = 0.01;
						} else if (multiplier > 100) {
							multiplier = 100;
						}
					} catch (NumberFormatException e2) {
						app.showError("InvalidInput");
						return;
					}

				} else {
					// transform indices to a multiplier
					multiplier = GeoText.getRelativeFontSize(cbSize
							.getSelectedIndex());
				}
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setFontSizeMultiplier(multiplier);
					((GeoElement) text).updateVisualStyleRepaint();
				}
				// update preview panel
				if (textEditPanel != null)
					textEditPanel.td.handleDocumentEvent();
			} else if (source == cbFont) {
				boolean serif = cbFont.getSelectedIndex() == 1;
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setSerifFont(serif);
					((GeoElement) text).updateVisualStyleRepaint();

					// update preview panel
					if (textEditPanel != null)
						textEditPanel.td.handleDocumentEvent();
				}
			} else if (source == cbDecimalPlaces) {
				int decimals = cbDecimalPlaces.getSelectedIndex();
				// Application.debug(decimals+"");
				// Application.debug(roundingMenuLookup[decimals]+"");
				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					if (decimals < 8) // decimal places
					{
						// Application.debug("decimals"+roundingMenuLookup[decimals]+"");
						text.setPrintDecimals(
								AppD.roundingMenuLookup[decimals], true);
					} else // significant figures
					{
						// Application.debug("figures"+roundingMenuLookup[decimals]+"");
						text.setPrintFigures(AppD.roundingMenuLookup[decimals],
								true);
					}
					((GeoElement) text).updateRepaint();

					// update preview panel
					if (textEditPanel != null)
						textEditPanel.td.handleDocumentEvent();
				}
			} else if (source == btBold || source == btItalic) {
				int style = 0;
				if (btBold.isSelected())
					style += 1;
				if (btItalic.isSelected())
					style += 2;

				TextProperties text;
				for (int i = 0; i < geos.length; i++) {
					text = (TextProperties) geos[i];
					text.setFontStyle(style);
					((GeoElement) text).updateVisualStyleRepaint();
				}

				// update preview panel
				if (textEditPanel != null)
					textEditPanel.td.handleDocumentEvent();
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			cbFont.setFont(font);
			cbSize.setFont(font);
			cbDecimalPlaces.setFont(font);

			btItalic.setFont(font);
			btBold.setFont(font);

			decimalLabel.setFont(font);

			editPanel.setFont(font);
		}

		public void focusGained(FocusEvent arg0) {
			TextProperties geo0 = (TextProperties) geos[0];
			cbSize.setSelectedIndex(GeoText.getFontSizeIndex(geo0
					.getFontSizeMultiplier()));
		}

		public void focusLost(FocusEvent arg0) {

		}
	}

	/**
	 * panel to select the size of a GeoPoint
	 * 
	 * @author Markus Hohenwarter
	 */
	private class SlopeTriangleSizePanel extends JPanel implements
			ChangeListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public SlopeTriangleSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");
			slider = new JSlider(1, 10);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			// slider.setFont(app.getSmallFont());
			slider.addChangeListener(this);

			/*
			 * setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			 * sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //setBorder(BorderFactory
			 * .createCompoundBorder(BorderFactory.createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * add(Box.createRigidArea(new Dimension(5,0))); add(sizeLabel);
			 */
			add(slider);
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			// set value to first point's size
			GeoNumeric geo0 = (GeoNumeric) geos[0];
			slider.setValue(geo0.getSlopeTriangleSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof GeoNumeric && geo.getParentAlgorithm() instanceof AlgoSlope)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoNumeric num;
				for (int i = 0; i < geos.length; i++) {
					num = (GeoNumeric) geos[i];
					num.setSlopeTriangleSize(size);
					num.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();

		}

		private void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the size of a GeoAngle's arc
	 * 
	 * @author Markus Hohenwarter
	 */
	private class ArcSizePanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public ArcSizePanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			// JLabel sizeLabel = new JLabel(app.getPlain("Size") + ":");
			slider = new JSlider(10, 100);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			/*
			 * //slider.setFont(app.getSmallFont());
			 * slider.addChangeListener(this); setLayout(new BoxLayout(this,
			 * BoxLayout.X_AXIS));
			 * sizeLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //setBorder(BorderFactory
			 * .createCompoundBorder(BorderFactory.createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * add(Box.createRigidArea(new Dimension(5,0))); add(sizeLabel);
			 */
			add(slider);
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Size")));
		}

		// added by Loic BEGIN
		public void setMinValue() {
			slider.setValue(20);
		}

		// END

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			// set value to first point's size
			AngleProperties geo0 = (AngleProperties) geos[0];
			slider.setValue(geo0.getArcSize());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (geos[i] instanceof AngleProperties) {
					AngleProperties angle = (AngleProperties) geos[i];
					if (angle.isIndependent() || !angle.isDrawable()) {
						geosOK = false;
						break;
					}
				} else {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				AngleProperties angle;
				for (int i = 0; i < geos.length; i++) {
					angle = (AngleProperties) geos[i];
					// addded by Loic BEGIN
					// check if decoration could be drawn
					if (size < 20
							&& (angle.getDecorationType() == GeoElement.DECORATION_ANGLE_THREE_ARCS || angle.getDecorationType() == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
						angle.setArcSize(20);
						int selected = ((AngleProperties) geos[0]).getDecorationType();
						if (selected == GeoElement.DECORATION_ANGLE_THREE_ARCS
								|| selected == GeoElement.DECORATION_ANGLE_TWO_ARCS) {
							slider.setValue(20);
						}
					}
					// END
					else
						angle.setArcSize(size);
					angle.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		private void updateSliderFonts() {
			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the filling of a polygon or conic section
	 * 
	 * @author Markus Hohenwarter
	 */
	class FillingPanel extends JPanel implements ChangeListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel, ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;

		private FillingPanel fillingPanel;

		private JSlider fillingSlider;
		private JSlider angleSlider;
		private JSlider distanceSlider;
		private JComboBox cbFillType;
		private JCheckBox cbFillInverse;

		private JPanel transparencyPanel, hatchFillPanel, imagePanel,
				anglePanel, distancePanel;
		private JLabel lblFillType;
		private JLabel lblSelectedSymbol;
		private JLabel lblMsgSelected;
		private JButton btnOpenFile;

		private PopupMenuButton btnImage;
		private JLabel lblFillInverse;
		private JLabel lblSymbols;
		private boolean hasGeoButton = false;
		private ArrayList<String> imgFileNameList;
		private PopupMenuButton btInsertUnicode;
		private FillType fillType;
		
		//For handle single bar
		private JPanel barsPanel;
		private boolean isBarChart;
		private JToggleButton[] selectionBarButtons;
		private int selectedBarButton;
		
		public FillingPanel() {

			// For filling whit unicode char
			btInsertUnicode = new PopupMenuButton(app);
			buildInsertUnicodeButton();
			btInsertUnicode.addActionListener(this);
			btInsertUnicode.setVisible(false);
			lblMsgSelected = new JLabel(loc.getMenu("Filling.CurrentSymbol") + ":"); 
			lblMsgSelected.setVisible(false);
			fillingPanel = this;
			lblSymbols = new JLabel(app.getMenu("Filling.Symbol") + ":"); 
			lblSymbols.setVisible(false);
			lblSelectedSymbol = new JLabel();
			lblSelectedSymbol.setFont(new Font("SansSerif", Font.PLAIN, 24));

			// JLabel sizeLabel = new JLabel(app.getPlain("Filling") + ":");
			fillingSlider = new JSlider(0, 100);
			fillingSlider.setMajorTickSpacing(25);
			fillingSlider.setMinorTickSpacing(5);
			fillingSlider.setPaintTicks(true);
			fillingSlider.setPaintLabels(true);
			fillingSlider.setSnapToTicks(true);

			angleSlider = new JSlider(0, 180);
			// angleSlider.setPreferredSize(new Dimension(150,50));
			angleSlider.setMajorTickSpacing(45);
			angleSlider.setMinorTickSpacing(5);
			angleSlider.setPaintTicks(true);
			angleSlider.setPaintLabels(true);
			angleSlider.setSnapToTicks(true);

			// Create the label table
			Hashtable<Integer, JLabel> labelHash = new Hashtable<Integer, JLabel>();
			labelHash.put(new Integer(0), new JLabel("0\u00b0"));
			labelHash.put(new Integer(45), new JLabel("45\u00b0"));
			labelHash.put(new Integer(90), new JLabel("90\u00b0"));
			labelHash.put(new Integer(135), new JLabel("135\u00b0"));
			labelHash.put(new Integer(180), new JLabel("180\u00b0"));
			angleSlider.setLabelTable(labelHash);

			distanceSlider = new JSlider(5, 50);
			// distanceSlider.setPreferredSize(new Dimension(150,50));
			distanceSlider.setMajorTickSpacing(10);
			distanceSlider.setMinorTickSpacing(5);
			distanceSlider.setPaintTicks(true);
			distanceSlider.setPaintLabels(true);
			distanceSlider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			// set label font
			Dictionary<?, ?> labelTable = fillingSlider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			labelTable = angleSlider.getLabelTable();
			en = labelTable.elements();
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			labelTable = distanceSlider.getLabelTable();
			en = labelTable.elements();
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			// ========================================
			// create sub panels

			// panel for the fill type combobox
			cbFillType = new JComboBox();
			JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JPanel syPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			lblFillType = new JLabel(app.getPlain("Filling") + ":");
			cbFillInverse = new JCheckBox();
			lblFillInverse = new JLabel(app.getPlain("InverseFilling"));
			cbPanel.add(lblFillType);
			cbPanel.add(cbFillType);
			cbPanel.add(cbFillInverse);
			cbPanel.add(lblFillInverse);
			syPanel.add(lblSymbols);
			syPanel.add(btInsertUnicode);
			syPanel.add(lblMsgSelected);
			lblSelectedSymbol.setAlignmentX(CENTER_ALIGNMENT);
			lblSelectedSymbol.setAlignmentY(CENTER_ALIGNMENT);
			lblSelectedSymbol.setVisible(false);
			syPanel.add(lblSelectedSymbol);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(cbPanel);
			panel.add(syPanel);
			// panels to hold sliders
			transparencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			transparencyPanel.add(fillingSlider);

			anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			anglePanel.add(angleSlider);

			distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			distancePanel.add(distanceSlider);

			// hatchfill panel: only shown when hatch fill option is selected
			hatchFillPanel = new JPanel();
			hatchFillPanel.setLayout(new BoxLayout(hatchFillPanel,
					BoxLayout.X_AXIS));
			hatchFillPanel.add(anglePanel);
			hatchFillPanel.add(distancePanel);
			hatchFillPanel.setVisible(false);

			// image panel: only shown when image fill option is selected
			createImagePanel();
			imagePanel.setVisible(false);

			// ===========================================================
			// put all the sub panels together

			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panel);
			this.add(transparencyPanel);
			this.add(hatchFillPanel);
			this.add(imagePanel);
		}

		public void setAllEnabled(boolean b) {
			Component[] c = this.getComponents();
			for (int i = 0; i < c.length; i++) {
				Component[] subc = ((JPanel) c[i]).getComponents();
				for (int j = 0; j < subc.length; j++) {
					subc[j].setEnabled(b);
				}
			}
		}

		public void setLabels() {

			// setBorder(BorderFactory.createTitledBorder(app.getPlain("Filling")));

			transparencyPanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Opacity")));
			anglePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Angle")));
			distancePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Spacing")));
			imagePanel.setBorder(BorderFactory.createTitledBorder(app
					.getMenu("Images")));

			btnOpenFile.setText(app.getMenu("ChooseFromFile") + "...");

			// fill type combobox
			lblFillType.setText(app.getMenu("Filling") + ":");

			int selectedIndex = cbFillType.getSelectedIndex();
			cbFillType.removeActionListener(this);
			cbFillType.removeAllItems();

			cbFillType.addItem(app.getMenu("Filling.Standard")); // index 0
			cbFillType.addItem(app.getMenu("Filling.Hatch")); // index 1
			cbFillType.addItem(app.getMenu("Filling.Crosshatch")); // index 2
			cbFillType.addItem(app.getMenu("Filling.Chessboard")); // index 3
			cbFillType.addItem(app.getMenu("Filling.Dotted")); // index 4
			cbFillType.addItem(app.getMenu("Filling.Honeycomb"));// index 5
			cbFillType.addItem(app.getMenu("Filling.Brick"));// index 6
			cbFillType.addItem(app.getMenu("Filling.Symbol"));// index 7
			cbFillType.addItem(app.getMenu("Filling.Image")); // index 8

			cbFillType.setSelectedIndex(selectedIndex);
			cbFillType.addActionListener(this);

		}

		private JPanel createImagePanel() {

			// =============================================
			// create array of image files from toolbar icons
			// for testing only ...

			imgFileNameList = new ArrayList<String>();
			String imagePath = "/geogebra/gui/images/";

			imgFileNameList.add(""); // for delete
			imgFileNameList.add(imagePath + "go-down.png");
			imgFileNameList.add(imagePath + "go-up.png");
			imgFileNameList.add(imagePath + "go-previous.png");
			imgFileNameList.add(imagePath + "go-next.png");
			imgFileNameList.add(imagePath + "nav_fastforward.png");
			imgFileNameList.add(imagePath + "nav_rewind.png");
			imgFileNameList.add(imagePath + "nav_skipback.png");
			imgFileNameList.add(imagePath + "nav_skipforward.png");
			imgFileNameList.add("/geogebra/main/nav_play.png");
			imgFileNameList.add("/geogebra/main/nav_pause.png");

			imgFileNameList.add(imagePath + "exit.png");

			ImageIcon[] iconArray = new ImageIcon[imgFileNameList.size()];
			iconArray[0] = GeoGebraIcon.createNullSymbolIcon(24, 24);
			for (int i = 1; i < iconArray.length; i++) {
				iconArray[i] = GeoGebraIcon.createFileImageIcon(app,
						imgFileNameList.get(i), 1.0f, new Dimension(32, 32));
			}
			// ============================================

			// panel for button to open external file

			btnImage = new PopupMenuButton(app, iconArray, -1, 4,
					new Dimension(32, 32),
					geogebra.common.gui.util.SelectionTable.MODE_ICON);
			btnImage.setSelectedIndex(1);
			btnImage.setStandardButton(true);
			btnImage.setKeepVisible(false);
			btnImage.addActionListener(this);

			btnOpenFile = new JButton();
			btnOpenFile.addActionListener(this);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			btnPanel.add(btnImage);
			btnPanel.add(btnOpenFile);

			// =====================================
			// put all sub panels together

			imagePanel = new JPanel(new BorderLayout());
			imagePanel.add(btnPanel, BorderLayout.CENTER);
			return imagePanel;
		}

		private void updateFillTypePanel(FillType fillType) {

			switch (fillType) {

			case STANDARD:
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(false);
				imagePanel.setVisible(false);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				break;
			case HATCH:
				distanceSlider.removeChangeListener(this);
				distanceSlider.setMinimum(5);
				distanceSlider.addChangeListener(this);
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				anglePanel.setVisible(true);
				angleSlider.removeChangeListener(this);
				angleSlider.setMaximum(180);
				angleSlider.setMinorTickSpacing(5);
				angleSlider.addChangeListener(this);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				break;
			case CROSSHATCHED:
			case CHESSBOARD:
				distanceSlider.removeChangeListener(this);
				distanceSlider.setMinimum(5);
				distanceSlider.addChangeListener(this);
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				anglePanel.setVisible(true);
				// Only at 0, 45 and 90 degrees texturepaint not have mismatches
				angleSlider.removeChangeListener(this);
				angleSlider.setMaximum(45);
				angleSlider.setMinorTickSpacing(45);
				angleSlider.addChangeListener(this);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				break;
			case BRICK:
				distanceSlider.removeChangeListener(this);
				distanceSlider.setMinimum(5);
				distanceSlider.addChangeListener(this);
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				anglePanel.setVisible(true);
				angleSlider.removeChangeListener(this);
				angleSlider.setMaximum(180);
				angleSlider.setMinorTickSpacing(45);
				angleSlider.addChangeListener(this);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				break;
			case SYMBOLS:
				distanceSlider.removeChangeListener(this);
				distanceSlider.setMinimum(10);
				distanceSlider.addChangeListener(this);
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				// for dotted angle is useless
				anglePanel.setVisible(false);
				lblSymbols.setVisible(true);
				lblSelectedSymbol.setVisible(true);
				btInsertUnicode.setVisible(true);
				break;
			case HONEYCOMB:
			case DOTTED:
				distanceSlider.removeChangeListener(this);
				distanceSlider.setMinimum(5);
				distanceSlider.addChangeListener(this);
				transparencyPanel.setVisible(false);
				hatchFillPanel.setVisible(true);
				imagePanel.setVisible(false);
				// for dotted angle is useless
				anglePanel.setVisible(false);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				break;
			case IMAGE:
				transparencyPanel.setVisible(true);
				hatchFillPanel.setVisible(false);
				imagePanel.setVisible(true);
				lblSymbols.setVisible(false);
				lblSelectedSymbol.setVisible(false);
				btInsertUnicode.setVisible(false);
				this.btnImage.setVisible(true);

				// for GeoButtons only show the image file button
				if (hasGeoButton) {
					transparencyPanel.setVisible(false);
					lblFillType.setVisible(false);
					cbFillType.setVisible(false);
					this.btnImage.setVisible(true);
				}
				break;

			}
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			
			cbFillType.removeActionListener(this);
			// set selected fill type to first geo's fill type
			if (isBarChart){
				setFillType((GeoElement)geos[0]);
			} else {
				cbFillType.setSelectedIndex(((GeoElement) geos[0]).getFillType()
						.ordinal());
			}
			cbFillType.addActionListener(this);

			cbFillInverse.removeActionListener(this);
			// set selected fill type to first geo's fill type
			cbFillInverse.setSelected(((GeoElement) geos[0]).isInverseFill());
			cbFillInverse.addActionListener(this);
			if (((GeoElement)geos[0]).getParentAlgorithm() instanceof AlgoBarChart){
				isBarChart=true;
				updateBarFillTypePanel((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm());
			} else {
				isBarChart=false;
				updateFillTypePanel(((GeoElement) geos[0]).getFillType());
			}
			this.geos = geos;
			fillingSlider.removeChangeListener(this);
			angleSlider.removeChangeListener(this);
			distanceSlider.removeChangeListener(this);

			// set value to first geo's alpha value
			double alpha = ((GeoElement) geos[0]).getAlphaValue();
			if (isBarChart){
				setAlpha((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm(),alpha);
			} else {
				fillingSlider.setValue((int) Math.round(alpha * 100));
			}
			double angle = ((GeoElement) geos[0]).getHatchingAngle();
			if (isBarChart){
				setAngle((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm(),angle);
			} else {
				angleSlider.setValue((int) angle);
			}
			int distance = ((GeoElement) geos[0]).getHatchingDistance();
			if (isBarChart){
				setDistance((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm(),distance);
			} else {
				distanceSlider.setValue(distance);
			}
			fillingSlider.addChangeListener(this);
			angleSlider.addChangeListener(this);
			distanceSlider.addChangeListener(this);
			if (isBarChart){
				lblSelectedSymbol.setText(((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm()).getBarSymbol(selectedBarButton));
				//setSymbol((AlgoBarChart) ((GeoElement)geos[0]).getParentAlgorithm());
			} else {
				if (((GeoElement) geos[0]).getFillSymbol()!=null 
						&& !((GeoElement) geos[0]).getFillSymbol().trim().equals("") ){
					lblSelectedSymbol.setText(((GeoElement) geos[0]).getFillSymbol());
				}
			}
			// set selected image to first geo image
			if (hasGeoButton) {
				int index = imgFileNameList.lastIndexOf(((GeoElement) geos[0])
						.getImageFileName());
				btnImage.setSelectedIndex(index > 0 ? index : 0);
			} else {
				btnImage.setSelectedIndex(0);
			}
			addSelectionBar();
			return this;
		}
		
		// Methods that set value for single bar if single bar is selected
		// and bar has tag for value
		
		private void setAlpha(AlgoBarChart algo, double alpha) {
			if (selectedBarButton!=0){
				if (algo.getBarAlpha(selectedBarButton)!=-1){
					alpha=algo.getBarAlpha(selectedBarButton);
				}
			}
			fillingSlider.setValue((int) Math.round(alpha * 100));
		}

		private void setSymbol(AlgoBarChart algo) {
			String symbol=null;
				if (algo.getBarSymbol(selectedBarButton) !=null
						&& !algo.getBarSymbol(selectedBarButton).equals("")){
					symbol=algo.getBarSymbol(selectedBarButton);
				}
			lblSelectedSymbol.setText(symbol);
		}

		private void updateBarFillTypePanel(AlgoBarChart algo) {
			FillType type=FillType.STANDARD;
			if (algo.getBarFillType(selectedBarButton)!=FillType.STANDARD){
				type=FillType.values()[algo.getBarFillType(selectedBarButton).ordinal()];
			}
			fillType=type;
			updateFillTypePanel(type);
		}

		private void setDistance(AlgoBarChart algo, int distance) {
			if (selectedBarButton!=0){
				if (algo.getBarHatchDistance(selectedBarButton)!=-1){
					distance =algo.getBarHatchDistance(selectedBarButton);
				}
			}
			distanceSlider.setValue(distance);
		}

		private void setAngle(AlgoBarChart algo, double angle) {
			if (selectedBarButton!=0){
				if (algo.getBarHatchAngle(selectedBarButton)!=-1){
					angle=algo.getBarHatchAngle(selectedBarButton);
				}
			}
			angleSlider.setValue((int) angle);
		}

		private void setFillType(GeoElement geo) {
			if (selectedBarButton==0){
				cbFillType.setSelectedIndex(geo.getFillType().ordinal());
			} else {
				AlgoBarChart algo=(AlgoBarChart) geo.getParentAlgorithm();
				if (algo.getBarFillType(selectedBarButton)!=null){
					cbFillType.setSelectedIndex(algo.getBarFillType(selectedBarButton).ordinal());
				}
			}
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			hasGeoButton = false;
			cbFillInverse.setVisible(true);
			lblFillInverse.setVisible(true);
			lblFillType.setVisible(true); // TODO remove this (see below)
			cbFillType.setVisible(true); // TODO remove this (see below)
			for (int i = 0; i < geos.length; i++) {
				hasGeoButton = ((GeoElement) geos[i]).isGeoButton();
				if (!(((GeoElement) geos[i]).isInverseFillable())
				// transformed objects copy inverse filling from parents, so
				// users can't change this
						|| (((GeoElement) geos[i]).getParentAlgorithm() instanceof AlgoTransformation)) {
					cbFillInverse.setVisible(false);
					lblFillInverse.setVisible(false);
				}
				if (!((GeoElement) geos[i]).isFillable()) {
					geosOK = false;
					break;
				}

				// TODO add fill type for 3D elements
				if (((GeoElement) geos[i]).isGeoElement3D()
						|| ((GeoElement) geos[i]).isGeoImage()) {
					lblFillType.setVisible(false);
					cbFillType.setVisible(false);
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			// For barchart opacity color  and
			// opacity image have same value if there is a tag
			if (e.getSource()==fillingSlider){
				if (isBarChart){
					for (int i=0;i<geos.length;i++){
						updateBarsFillType((GeoElement) geos[i],4,null);
						((GeoElement)geos[i]).updateVisualStyle();
					}
					
				}
				kernel.notifyRepaint();
				return;
			}
			if (!angleSlider.getValueIsAdjusting()
					&& !distanceSlider.getValueIsAdjusting()) {
				int angle = angleSlider.getValue();
				int distance = distanceSlider.getValue();
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					if (isBarChart){
						if (!updateBarsFillType(geo,1,null)){
							geo.setHatchingAngle(angle);
							geo.setHatchingDistance(distance);
						}
					} else {
						geo.setHatchingAngle(angle);
						geo.setHatchingDistance(distance);
					}
					geo.updateVisualStyle();
				}
				kernel.notifyRepaint();
			}
		}

		/**
		 * action listener for fill type combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			GeoElement geo;

			// handle change in fill type
			if (source == cbFillType) {
				fillType = FillType.values()[cbFillType.getSelectedIndex()];
				// set selected image to first geo image
				if (fillType == FillType.IMAGE
						&& ((GeoElement) geos[0]).getFillImage() != null) {
					btnImage.setSelectedIndex(this.imgFileNameList
							.lastIndexOf(((GeoElement) geos[0])
									.getImageFileName()));	
				} else {
					btnImage.setSelectedIndex(-1);
				}
				if (fillType == fillType.SYMBOLS) {
					btInsertUnicode.setVisible(true);
					lblSymbols.setVisible(true);
					lblSelectedSymbol.setVisible(true);
					lblMsgSelected.setVisible(true);
				} else {
					lblSymbols.setVisible(false);
					btInsertUnicode.setVisible(false);
					lblMsgSelected.setVisible(false);
					lblSelectedSymbol.setVisible(false);
					lblSelectedSymbol.setText("");
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];						
						if(isBarChart){
							if (!updateBarsFillType(geo,1,null)){
								geo.setFillType(fillType);
							}
						} else {
							geo.setFillType(fillType);
						}
						geo.updateRepaint();
					}
				}

				fillingPanel.updateFillTypePanel(fillType);
				
			} else if (source == cbFillInverse) {

				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setInverseFill(cbFillInverse.isSelected());
					geo.updateRepaint();
				}

			}
			// handle image button selection
			else if (source == this.btnImage) {
				String fileName = null;
				if (btnImage.getSelectedIndex() == 0) {
					fileName = "";
				} else {
					fileName = imgFileNameList.get(btnImage.getSelectedIndex());
				}
				if (fileName != null)
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];						
						if(isBarChart){							
							if (!updateBarsFillType(geo,2,fileName)){
								geo.setImageFileName(fileName);
							}
						} else {
							geo.setImageFileName(fileName);
						}
						geo.updateRepaint();
					}
			}

			// handle load image file
			else if (source == btnOpenFile) {
				String fileName = ((GuiManagerD) app.getGuiManager())
						.getImageFromFile();
				if (fileName != null)
					for (int i = 0; i < geos.length; i++) {
						geo = (GeoElement) geos[i];
						if(isBarChart){
							if (!updateBarsFillType(geo,2,fileName)){
								geo.setImageFileName(fileName);
							}
						} else {
							geo.setImageFileName(fileName);
						}
						geo.updateRepaint();
					}
			} else if (source == btInsertUnicode) {
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					if (!"".equals(lblSelectedSymbol.getText())) {
						if(isBarChart){
							if (!updateBarsFillType(geo,3,null)){
								geo.setFillType(fillType);
								geo.setFillSymbol(lblSelectedSymbol.getText());
							}
						} else {
							geo.setFillType(fillType);
							geo.setFillSymbol(lblSelectedSymbol.getText());
						}
						geo.updateRepaint();
					}
					
				}
			}
		}
		
		// Add tags or remove if selected all bars
		private boolean updateBarsFillType(GeoElement geo, int type, String fileName) {
			AlgoBarChart algo=(AlgoBarChart)geo.getParentAlgorithm();
			if (selectedBarButton == 0){
				for ( int i=1; i<selectionBarButtons.length;i++){
					algo.setBarFillType(null, i);
					algo.setBarHatchDistance(-1,i);
					algo.setBarHatchAngle(-1, i);
					algo.setBarSymbol(null,i);
					algo.setBarImage(null, i);
				}
				return false;
			}
			switch (type) {
			case 1:
				algo.setBarFillType(fillType,selectedBarButton);
				algo.setBarHatchDistance(distanceSlider.getValue(),selectedBarButton);
				algo.setBarHatchAngle(angleSlider.getValue(), selectedBarButton);
				algo.setBarImage(null, selectedBarButton);
				if (FillType.values()[cbFillType.getSelectedIndex()]==FillType.SYMBOLS){
					if (lblSelectedSymbol.getText() != null
							&& !"".equals(lblSelectedSymbol.getText())){
						algo.setBarHatchAngle(-1, selectedBarButton);
						algo.setBarSymbol(lblSelectedSymbol.getText(), selectedBarButton);						
					}else{
						algo.setBarFillType(FillType.STANDARD, selectedBarButton);
						algo.setBarSymbol(null , selectedBarButton);
					}					
				} else {
					algo.setBarSymbol(null , selectedBarButton);
				}
				break;
			case 4:
				algo.setBarAlpha(fillingSlider.getValue()/100f, selectedBarButton);
				break;
			case 2:
				algo.setBarFillType(null,selectedBarButton);
				algo.setBarHatchDistance(-1 , selectedBarButton);
				algo.setBarHatchAngle(-1, selectedBarButton);
				algo.setBarSymbol(null , selectedBarButton);
				algo.setBarImage(fileName, selectedBarButton);
				algo.setBarFillType(FillType.IMAGE, selectedBarButton);
				break;
			case 3:
				if (lblSelectedSymbol.getText() != null
						&& !"".equals(lblSelectedSymbol.getText())) {
					algo.setBarFillType(FillType.SYMBOLS,selectedBarButton);
					algo.setBarHatchAngle(-1, selectedBarButton);
					algo.setBarImage(null, selectedBarButton);
					algo.setBarSymbol(lblSelectedSymbol.getText() , selectedBarButton);
				} else {
					algo.setBarSymbol(null , selectedBarButton);
				}
				break;
			}
			return true;
		}
		
		public void updateFonts() {
			Font font = app.getPlainFont();

			transparencyPanel.setFont(font);
			anglePanel.setFont(font);
			distancePanel.setFont(font);
			imagePanel.setFont(font);

			btnOpenFile.setFont(font);
			lblFillType.setFont(font);
			cbFillType.setFont(font);

			lblFillInverse.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

		private void addSelectionBar() {
			if (barsPanel != null) {
				remove(barsPanel);
			}
			AlgoElement algo = ((GeoElement) geos[0]).getParentAlgorithm();
			if (algo instanceof AlgoBarChart) {
				int numBar = ((AlgoBarChart) algo).getIntervals();
				isBarChart = true;
				selectionBarButtons = new JToggleButton[numBar+1];
				ButtonGroup group=new ButtonGroup();
				barsPanel = new JPanel(new GridLayout(0, 5 ,5,5));
				barsPanel.setBorder(new TitledBorder(app.getPlain("SelectedBar")));
				for (int i = 0; i < numBar + 1; i++) {
					selectionBarButtons[i] = new JToggleButton(app.getPlain("Bar") +" "+ i);
					selectionBarButtons[i].setSelected(false);
					selectionBarButtons[i].setActionCommand(""+i);
					selectionBarButtons[i].addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent arg0) {
							selectedBarButton=Integer.parseInt(((JToggleButton)arg0.getSource()).getActionCommand());
							FillingPanel.this.update(geos);
						}
						
					});		
					group.add(selectionBarButtons[i]);
					barsPanel.add(selectionBarButtons[i]);
				}
				selectionBarButtons[0].setText(app.getPlain("All"));
				selectionBarButtons[selectedBarButton].setSelected(true);
				add(barsPanel);
			}
		}
		private void buildInsertUnicodeButton() {
			if (btInsertUnicode != null)
				btInsertUnicode.removeAllMenuItems();

			btInsertUnicode.setKeepVisible(false);
			btInsertUnicode.setStandardButton(true);
			btInsertUnicode.setFixedIcon(GeoGebraIcon
					.createDownTriangleIcon(10));

			JMenu menu = new JMenu(app.getMenu("Properties.Basic"));

			// Suits and music
			String[] fancy = Unicode.getSetOfSymbols(0x2660, 16);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));
			
			// Chess
			fancy = Unicode.getSetOfSymbols(0x2654, 12);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));
			
			// Stars
			fancy = Unicode.getSetOfSymbols(0x2725, 3);
			String[] fancy2 = Unicode.getSetOfSymbols(0x2729, 23);
			String[] union = new String[26];
			System.arraycopy(fancy, 0, union, 0, 3);
			System.arraycopy(fancy2, 0, union, 3, 23);
			btInsertUnicode.addPopupMenuItem(createMenuItem(union, -1, 4));
			
			// Squares
			fancy = Unicode.getSetOfSymbols(0x2b12, 8);
			btInsertUnicode.addPopupMenuItem(createMenuItem(fancy, -1, 4));
			app.setComponentOrientation(menu);

		}
		private JMenu createMenuItem(String[] table, int rows, int columns) {

			StringBuilder sb = new StringBuilder(7);
			sb.append(table[0]);
			sb.append(' ');
			sb.append(table[1]);
			sb.append(' ');
			sb.append(table[2]);
			sb.append("  ");

			JMenu menu = new JMenu(sb.toString());
			menu.add(new LatexTableFill(app, this, btInsertUnicode, table,
					rows, columns,
					geogebra.common.gui.util.SelectionTable.MODE_TEXT));

			menu.setFont(GFontD.getAwtFont(app.getFontCanDisplay(sb.toString())));

			return menu;
		}

		class LatexTableFill extends SelectionTable implements MenuElement {

			private Object[] latexArray;
			private PopupMenuButton popupButton;
			private geogebra.common.gui.util.SelectionTable mode;
			FillingPanel panel;

			public LatexTableFill(AppD app, FillingPanel panel,
					PopupMenuButton popupButton, Object[] data, int rows,
					int columns, geogebra.common.gui.util.SelectionTable mode) {
				super(app, data, rows, columns, new Dimension(24, 24), mode);
				this.panel = panel;
				this.latexArray = data;
				this.popupButton = popupButton;
				this.mode = mode;
				setHorizontalAlignment(SwingConstants.CENTER);
				setSelectedIndex(0);
				this.setShowGrid(true);
				this.setGridColor(geogebra.awt.GColorD
						.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR));
				this.setBorder(BorderFactory
						.createLineBorder(MyTableD.TABLE_GRID_COLOR));
				this.setShowSelection(false);
			}

			public Component getComponent() {
				return this;
			}

			public MenuElement[] getSubElements() {
				return new MenuElement[0];
			}

			public void menuSelectionChanged(boolean arg0) {
			}

			public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1,
					MenuSelectionManager arg2) {
			}

			public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1,
					MenuSelectionManager arg2) {

				if (this.getSelectedIndex() >= latexArray.length)
					return;

				if (arg0.getID() == MouseEvent.MOUSE_RELEASED) {

					// get the selected string
					String s = (String) latexArray[this.getSelectedIndex()];
					// if LaTeX string, adjust the string to include selected
					// text within braces

					if (s != null) {
						lblSelectedSymbol.setText(s);
						lblSelectedSymbol.setFont(GFontD.getAwtFont(app
								.getFontCanDisplay(s)));
					} 
					popupButton.handlePopupActionEvent();
				}
			}
		}
	}

	/**
	 * panel to select thickness and style (dashing) of a GeoLine
	 * 
	 * @author Markus Hohenwarter
	 */
	private class LineStylePanel extends JPanel implements ChangeListener,
			ActionListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;
		private JPanel thicknessPanel;
		private JLabel dashLabel;
		private JComboBox dashCB;

		public LineStylePanel() {
			// thickness slider
			slider = new JSlider(1, GeoElement.MAX_LINE_WIDTH);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			/*
			 * Dimension dim = slider.getPreferredSize(); dim.width =
			 * SLIDER_MAX_WIDTH; slider.setMaximumSize(dim);
			 * slider.setPreferredSize(dim);
			 */

			updateSliderFonts();

			slider.addChangeListener(this);

			// line style combobox (dashing)
			DashListRenderer renderer = new DashListRenderer();
			renderer.setPreferredSize(new Dimension(130,
					app.getGUIFontSize() + 6));
			dashCB = new JComboBox(EuclidianViewD.getLineTypes());
			dashCB.setRenderer(renderer);
			dashCB.addActionListener(this);

			// line style panel
			JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dashLabel = new JLabel();
			dashPanel.add(dashLabel);
			dashPanel.add(dashCB);

			// thickness panel
			thicknessPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			/*
			 * JLabel thicknessLabel = new JLabel(app.getPlain("Thickness") +
			 * ":"); thicknessPanel.setLayout(new BoxLayout(thicknessPanel,
			 * BoxLayout.X_AXIS));
			 * thicknessLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			 * slider.setAlignmentY(Component.TOP_ALIGNMENT);
			 * //thicknessPanel.setBorder
			 * (BorderFactory.createCompoundBorder(BorderFactory
			 * .createEtchedBorder(), //
			 * BorderFactory.createEmptyBorder(3,5,0,5)));
			 * thicknessPanel.add(Box.createRigidArea(new Dimension(5,0)));
			 * thicknessPanel.add(thicknessLabel);
			 */
			thicknessPanel.add(slider);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			thicknessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			dashPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(thicknessPanel);
			add(dashPanel);
		}

		public void setLabels() {
			thicknessPanel.setBorder(BorderFactory.createTitledBorder(app
					.getPlain("Thickness")));

			dashLabel.setText(app.getPlain("LineStyle") + ":");
		}

		private int maxMinimumThickness(Object[] geos) {

			if (geos == null || geos.length == 0)
				return 1;

			for (int i = 0; i < geos.length; i++) {
				GeoElement testGeo = ((GeoElement) geos[i])
						.getGeoElementForPropertiesDialog();
				if (testGeo.getMinimumLineThickness() == 1)
					return 1;
			}

			return 0;

		}

		public JPanel update(Object[] geos) {

			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!checkGeos(geos))
				return null;

			slider.removeChangeListener(this);
			dashCB.removeActionListener(this);

			// set slider value to first geo's thickness
			GeoElement temp, geo0 = (GeoElement) geos[0];
			slider.setValue(geo0.getLineThickness());

			// allow polygons to have thickness 0
			slider.setMinimum(maxMinimumThickness(geos));

			// check if geos have same line style
			boolean equalStyle = true;
			for (int i = 1; i < geos.length; i++) {
				temp = (GeoElement) geos[i];
				// same style?
				if (geo0.getLineType() != temp.getLineType())
					equalStyle = false;
			}

			// select common line style
			if (equalStyle) {
				int type = geo0.getLineType();
				for (int i = 0; i < dashCB.getItemCount(); i++) {
					if (type == ((Integer) dashCB.getItemAt(i)).intValue()) {
						dashCB.setSelectedIndex(i);
						break;
					}
				}
			} else
				dashCB.setSelectedItem(null);

			slider.addChangeListener(this);
			dashCB.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = ((GeoElement) geos[i])
						.getGeoElementForPropertiesDialog();
				if (!(geo.isPath()
						|| (geo.isGeoList() && ((GeoList) geo)
								.showLineProperties())
						|| (geo.isGeoNumeric() && (((GeoNumeric) geo)
								.isDrawable() || isDefaults)) || ((geo instanceof GeoFunctionNVar) && ((GeoFunctionNVar) geo)
						.isInequality()))) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoElement geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineThickness(size);
					geo.updateVisualStyleRepaint();
				}
			}
		}

		/**
		 * action listener implementation for coord combobox
		 */
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == dashCB) {
				GeoElement geo;
				int type = ((Integer) dashCB.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoElement) geos[i];
					geo.setLineType(type);
					geo.updateVisualStyleRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			thicknessPanel.setFont(font);
			dashLabel.setFont(font);

			updateSliderFonts();
		}

		public void updateSliderFonts() {
			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			// slider.setFont(app.getSmallFont());
		}
	}

	/**
	 * select dash style for hidden parts.
	 * 
	 * @author matthieu
	 * 
	 */
	private class LineStyleHiddenPanel extends JPanel implements
			UpdateablePropertiesPanel, SetLabels, UpdateFonts, ActionListener {
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JRadioButton[] buttons;

		public LineStyleHiddenPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			PointStyleListRenderer renderer = new PointStyleListRenderer();
			renderer.setPreferredSize(new Dimension(18, 18));

			buttons = new JRadioButton[3];

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE] = new JRadioButton(
					app.getMenu("Hidden.Invisible"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE]
					.setActionCommand("none");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED] = new JRadioButton(
					app.getMenu("Hidden.Dashed"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED]
					.setActionCommand("dashed");

			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN] = new JRadioButton(
					app.getMenu("Hidden.Unchanged"));
			buttons[EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN]
					.setActionCommand("asNotHidden");

			ButtonGroup buttonGroup = new ButtonGroup();
			for (int i = 0; i < 3; i++) {
				buttons[i].addActionListener(this);
				add(buttons[i]);
				buttonGroup.add(buttons[i]);
			}

		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getMenu("HiddenLineStyle")));
		}

		public JPanel update(Object[] geos) {

			this.geos = geos;
			return update();
		}

		public void updateVisualStyle(GeoElement geo) {
			if (geos == null)
				return;
			update();
		}

		public JPanel update() {
			// check geos
			if (!checkGeos(geos))
				return null;

			// set value to first line's style
			GeoElement geo0 = (GeoElement) geos[0];

			// update radio buttons
			buttons[geo0.getLineTypeHidden()].setSelected(true);

			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.isPath()) || !(geo.isGeoElement3D())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {

			int type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_NONE;

			if (e.getActionCommand() == "dashed") {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_DASHED;
			} else if (e.getActionCommand() == "asNotHidden") {
				type = EuclidianStyleConstants.LINE_TYPE_HIDDEN_AS_NOT_HIDDEN;
			}

			GeoElement geo;
			for (int i = 0; i < geos.length; i++) {
				geo = (GeoElement) geos[i];
				geo.setLineTypeHidden(type);
				geo.updateRepaint();
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);
		}

	}

	/**
	 * panel to select the fading for endings of a surface
	 * 
	 * @author mathieu
	 */
	private class FadingPanel extends JPanel implements ChangeListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public FadingPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			slider = new JSlider(0, 50);
			slider.setMajorTickSpacing(25);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			updateSliderFonts();

			slider.addChangeListener(this);

			add(slider);
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app.getPlain("Fading")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			// set value to first point's size
			GeoPlaneND geo0 = (GeoPlaneND) geos[0];
			slider.setValue((int) (100 * geo0.getFading()));

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo.isGeoPlane())) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int size = slider.getValue();
				GeoPlaneND plane;
				for (int i = 0; i < geos.length; i++) {
					plane = (GeoPlaneND) geos[i];
					plane.setFading((float) size / 100);
					((GeoElement) plane).updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		public void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to select the level of detail of surfaces
	 * 
	 * @author mathieu
	 */
	private class LodPanel extends JPanel implements ChangeListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos;
		private JSlider slider;

		public LodPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			slider = new JSlider(0, 10);
			slider.setMajorTickSpacing(1);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);

			updateSliderFonts();

			slider.addChangeListener(this);

			add(slider);
		}

		public void setLabels() {
			setBorder(BorderFactory.createTitledBorder(app
					.getPlain("LevelOfDetail")));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;

			this.geos = geos;
			slider.removeChangeListener(this);

			// set value to first point's size
			GeoLevelOfDetail geo0 = (GeoLevelOfDetail) geos[0];
			slider.setValue(geo0.getLevelOfDetail().getValue());

			slider.addChangeListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!((GeoElement) geos[i]).hasLevelOfDetail()) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		/**
		 * change listener implementation for slider
		 */
		public void stateChanged(ChangeEvent e) {
			if (!slider.getValueIsAdjusting()) {
				int lod = slider.getValue();
				GeoLevelOfDetail geo;
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoLevelOfDetail) geos[i];
					geo.getLevelOfDetail().setValue(lod);
					((GeoElement) geo).updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			setFont(font);

			updateSliderFonts();
		}

		public void updateSliderFonts() {

			// set label font
			Dictionary<?, ?> labelTable = slider.getLabelTable();
			Enumeration<?> en = labelTable.elements();
			JLabel label;
			while (en.hasMoreElements()) {
				label = (JLabel) en.nextElement();
				label.setFont(app.getSmallFont());
			}

			slider.setFont(app.getSmallFont());
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * panel to show a 2D view from a plane, polygon, etc.
	 * 
	 * @author mathieu
	 */
	private class ShowView2D extends JPanel implements ItemListener, SetLabels,
			UpdateFonts, UpdateablePropertiesPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object[] geos; // currently selected geos
		private JCheckBox cb2DView;

		public ShowView2D() {
			super(new FlowLayout(FlowLayout.LEFT));

			// check boxes for show trace
			cb2DView = new JCheckBox();
			cb2DView.addItemListener(this);

			// put it all together
			add(cb2DView);
		}

		public void setLabels() {
			cb2DView.setText(app.getPlain("ViewFrom"));
		}

		public JPanel update(Object[] geos) {
			this.geos = geos;
			if (!checkGeos(geos))
				return null;

			cb2DView.removeItemListener(this);

			// check if properties have same values
			ViewCreator temp, geo0 = (ViewCreator) geos[0];
			boolean equalVal = true;

			for (int i = 0; i < geos.length; i++) {
				temp = (ViewCreator) geos[i];
				// same object visible value
				if (geo0.hasView2DVisible() != temp.hasView2DVisible())
					equalVal = false;
			}

			// set checkbox
			if (equalVal)
				cb2DView.setSelected(geo0.hasView2DVisible());
			else
				cb2DView.setSelected(false);

			cb2DView.addItemListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (!(geo instanceof ViewCreator))
					return false;
			}
			return true;
		}

		/**
		 * listens to checkboxes and sets trace state
		 */
		public void itemStateChanged(ItemEvent e) {
			ViewCreator geo;
			Object source = e.getItemSelectable();

			// absolute screen location flag changed
			if (source == cb2DView) {
				boolean flag = cb2DView.isSelected();
				EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
				for (int i = 0; i < geos.length; i++) {
					geo = (ViewCreator) geos[i];
					geo.setView2DVisible(flag);
				}

				updateSelection(geos);
			}
		}

		public void updateFonts() {
			// TODO Auto-generated method stub

		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * Panel for segment decoration
	 * 
	 * @author Loic
	 */
	private class DecoSegmentPanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		private static final long serialVersionUID = 1L;
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private Object[] geos;

		DecoSegmentPanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			// deco combobox
			DecorationListRenderer renderer = new DecorationListRenderer();
			renderer.setPreferredSize(new Dimension(130,
					app.getGUIFontSize() + 6));
			decoCombo = new JComboBox(GeoSegment.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);

			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}

		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			decoCombo.removeActionListener(this);

			// set slider value to first geo's thickness
			GeoSegment geo0 = (GeoSegment) geos[0];
			decoCombo.setSelectedIndex(geo0.decorationType);

			decoCombo.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof GeoSegment)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				GeoSegment geo;
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (GeoSegment) geos[i];
					// Michael Borcherds 2007-11-20 BEGIN
					// geo.decorationType = type;
					geo.setDecorationType(type);
					// Michael Borcherds 2007-11-20 END
					geo.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	private class DecoAnglePanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		private JComboBox decoCombo;
		private JLabel decoLabel;
		private Object[] geos;

		DecoAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			// deco combobox
			DecorationAngleListRenderer renderer = new DecorationAngleListRenderer();
			renderer.setPreferredSize(new Dimension(80, 30));
			decoCombo = new JComboBox(GeoAngle.getDecoTypes());
			decoCombo.setRenderer(renderer);
			decoCombo.addActionListener(this);
			decoLabel = new JLabel();
			add(decoLabel);
			add(decoCombo);
		}

		public void setLabels() {
			decoLabel.setText(app.getPlain("Decoration") + ":");
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			decoCombo.removeActionListener(this);

			// set slider value to first geo's decoration
			AngleProperties geo0 = (AngleProperties) geos[0];
			decoCombo.setSelectedIndex(geo0.getDecorationType());
			decoCombo.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof AngleProperties)) {
					geosOK = false;
					break;
				}
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == decoCombo) {
				AngleProperties geo;
				int type = ((Integer) decoCombo.getSelectedItem()).intValue();
				for (int i = 0; i < geos.length; i++) {
					geo = (AngleProperties) geos[i];
					geo.setDecorationType(type);
					// addded by Loic BEGIN
					// check if decoration could be drawn
					if (geo.getArcSize() < 20
							&& (geo.getDecorationType() == GeoElement.DECORATION_ANGLE_THREE_ARCS || geo.getDecorationType() == GeoElement.DECORATION_ANGLE_TWO_ARCS)) {
						geo.setArcSize(20);
						setSliderMinValue();
					}
					// END
					geo.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			decoLabel.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}
	}

	// added 3/11/06
	private class RightAnglePanel extends JPanel implements ActionListener,
			SetLabels, UpdateFonts, UpdateablePropertiesPanel {
		private JCheckBox emphasizeRightAngle;
		private Object[] geos;

		RightAnglePanel() {
			super(new FlowLayout(FlowLayout.LEFT));
			emphasizeRightAngle = new JCheckBox();
			emphasizeRightAngle.addActionListener(this);
			add(emphasizeRightAngle);
		}

		public void setLabels() {
			emphasizeRightAngle.setText(app.getPlain("EmphasizeRightAngle"));
		}

		public JPanel update(Object[] geos) {
			// check geos
			if (!checkGeos(geos))
				return null;
			this.geos = geos;
			emphasizeRightAngle.removeActionListener(this);

			// set JcheckBox value to first geo's decoration
			AngleProperties geo0 = (AngleProperties) geos[0];
			emphasizeRightAngle.setSelected(geo0.isEmphasizeRightAngle());
			emphasizeRightAngle.addActionListener(this);
			return this;
		}

		private boolean checkGeos(Object[] geos) {
			boolean geosOK = true;
			for (int i = 0; i < geos.length; i++) {
				if (!(geos[i] instanceof AngleProperties)) {
					geosOK = false;
					break;
				}
				/*
				 * // If it isn't a right angle else if
				 * (!Kernel.isEqual(((GeoAngle)geos[i]).getValue(),
				 * Kernel.PI_HALF)){ geosOK=false; break; }
				 */
			}
			return geosOK;
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == emphasizeRightAngle) {
				AngleProperties geo;
				boolean b = emphasizeRightAngle.isSelected();
				for (int i = 0; i < geos.length; i++) {
					geo = (AngleProperties) geos[i];
					geo.setEmphasizeRightAngle(b);
					geo.updateRepaint();
				}
			}
		}

		public void updateFonts() {
			Font font = app.getPlainFont();

			emphasizeRightAngle.setFont(font);
		}

		public void updateVisualStyle(GeoElement geo) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * allows using a single = in condition to show object and dynamic color
	 * 
	 * @param strCond
	 *            Condition to be processed
	 * @return processed condition
	 */
	public static String replaceEqualsSigns(String strCond) {
		// needed to make next replace easier
		strCond = strCond.replaceAll(">=",
				ExpressionNodeConstants.strGREATER_EQUAL);
		strCond = strCond.replaceAll("<=",
				ExpressionNodeConstants.strLESS_EQUAL);
		strCond = strCond.replaceAll("==",
				ExpressionNodeConstants.strEQUAL_BOOLEAN);
		strCond = strCond
				.replaceAll("!=", ExpressionNodeConstants.strNOT_EQUAL);

		// allow A=B as well as A==B
		// also stops A=B doing an assignment of B to A :)
		return strCond
				.replaceAll("=", ExpressionNodeConstants.strEQUAL_BOOLEAN);

	}

} // PropertiesPanel

/**
 * panel for textfield size
 * 
 * @author Michael
 */
class TextfieldSizePanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {

	private static final long serialVersionUID = 1L;

	private Object[] geos; // currently selected geos
	private JLabel label;
	private MyTextField tfTextfieldSize;

	private Kernel kernel;

	public TextfieldSizePanel(AppD app) {
		kernel = app.getKernel();

		// text field for textfield size
		label = new JLabel();
		tfTextfieldSize = new MyTextField(app, 5);
		label.setLabelFor(tfTextfieldSize);
		tfTextfieldSize.addActionListener(this);
		tfTextfieldSize.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfTextfieldSize);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);

		setLabels();
	}

	public void setLabels() {
		label.setText(kernel.getApplication().getPlain("TextfieldLength")
				+ ": ");
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfTextfieldSize.removeActionListener(this);

		// check if properties have same values
		GeoTextField temp, geo0 = (GeoTextField) geos[0];
		boolean equalSize = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoTextField) geos[i];
			// same object visible value
			if (geo0.getLength() != temp.getLength())
				equalSize = false;
		}

		if (equalSize) {
			tfTextfieldSize.setText(geo0.getLength() + "");
		} else
			tfTextfieldSize.setText("");

		tfTextfieldSize.addActionListener(this);
		return this;
	}

	private static boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!(geo instanceof GeoTextField)) {
				geosOK = false;
				break;
			}
		}

		return geosOK;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfTextfieldSize)
			doActionPerformed();
	}

	private void doActionPerformed() {
		NumberValue newVal = kernel.getAlgebraProcessor().evaluateToNumeric(
				tfTextfieldSize.getText(), true);
		if (newVal != null && !Double.isNaN(newVal.getDouble())) {
			for (int i = 0; i < geos.length; i++) {
				GeoTextField geo = (GeoTextField) geos[i];
				geo.setLength((int) newVal.getDouble());
				geo.updateRepaint();
			}
		}
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	public void updateFonts() {
		// TODO Auto-generated method stub

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}
}

/**
 * panel for condition to show object
 * 
 * @author Markus Hohenwarter
 */
class ShowConditionPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts, IShowConditionListener {

	private static final long serialVersionUID = 1L;

	private ShowConditionModel model;
	private JTextField tfCondition;

	private Kernel kernel;
	private PropertiesPanelD propPanel;

	public ShowConditionPanel(AppD app, PropertiesPanelD propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;
		model = new ShowConditionModel(app, this);
		// non auto complete input panel
		InputPanelD inputPanel = new InputPanelD(null, app, -1, false);
		tfCondition = (AutoCompleteTextFieldD) inputPanel.getTextComponent();

		tfCondition.addActionListener(this);
		tfCondition.addFocusListener(this);

		// put it all together
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.CENTER);

		setLabels();
	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(kernel.getApplication()
				.getMenu("Condition.ShowObject")));
	}

	public JPanel update(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos())
			return null;

		tfCondition.removeActionListener(this);

		model.updateProperties();
		
		tfCondition.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfCondition) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;
		model.applyChanges(tfCondition.getText());
	}

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
			doActionPerformed();
	}

	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);
		tfCondition.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setText(String text) {
		tfCondition.setText(text);	
	}

	public void updateSelection(Object[] geos) {
		propPanel.updateSelection(geos);
	}

	public String replaceEqualsSigns(String strCond) {
		return propPanel.replaceEqualsSigns(strCond);
	}
}

/**
 * panel for condition to show object
 * 
 * @author Michael Borcherds 2008-04-01
 */
class ColorFunctionPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {

	private static final long serialVersionUID = 1L;

	private Object[] geos; // currently selected geos
	private JTextField tfRed, tfGreen, tfBlue, tfAlpha;
	private JButton btRemove;
	private JLabel nameLabelR, nameLabelG, nameLabelB, nameLabelA;

	private JComboBox cbColorSpace;
	private int colorSpace = GeoElement.COLORSPACE_RGB;
	// flag to prevent unneeded relabeling of the colorSpace comboBox
	private boolean allowSetComboBoxLabels = true;

	private String defaultR = "0", defaultG = "0", defaultB = "0",
			defaultA = "1";

	private Kernel kernel;
	private PropertiesPanelD propPanel;

	public ColorFunctionPanel(AppD app, PropertiesPanelD propPanel) {
		kernel = app.getKernel();
		this.propPanel = propPanel;

		// non auto complete input panel
		InputPanelD inputPanelR = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelG = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelB = new InputPanelD(null, app, 1, -1, true);
		InputPanelD inputPanelA = new InputPanelD(null, app, 1, -1, true);
		tfRed = (AutoCompleteTextFieldD) inputPanelR.getTextComponent();
		tfGreen = (AutoCompleteTextFieldD) inputPanelG.getTextComponent();
		tfBlue = (AutoCompleteTextFieldD) inputPanelB.getTextComponent();
		tfAlpha = (AutoCompleteTextFieldD) inputPanelA.getTextComponent();

		tfRed.addActionListener(this);
		tfRed.addFocusListener(this);
		tfGreen.addActionListener(this);
		tfGreen.addFocusListener(this);
		tfBlue.addActionListener(this);
		tfBlue.addFocusListener(this);
		tfAlpha.addActionListener(this);
		tfAlpha.addFocusListener(this);

		nameLabelR = new JLabel("", SwingConstants.TRAILING);
		nameLabelR.setLabelFor(inputPanelR);
		nameLabelG = new JLabel("", SwingConstants.TRAILING);
		nameLabelG.setLabelFor(inputPanelG);
		nameLabelB = new JLabel("", SwingConstants.TRAILING);
		nameLabelB.setLabelFor(inputPanelB);
		nameLabelA = new JLabel("", SwingConstants.TRAILING);
		nameLabelA.setLabelFor(inputPanelA);

		btRemove = new JButton("\u2718");
		btRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					geo.removeColorFunction();
					geo.setObjColor(geo.getObjectColor());
					geo.updateRepaint();
				}
				tfRed.setText("");
				tfGreen.setText("");
				tfBlue.setText("");
				tfAlpha.setText("");
			}
		});

		cbColorSpace = new JComboBox();
		cbColorSpace.addActionListener(this);

		setLayout(new BorderLayout());

		JPanel colorsPanel = new JPanel(new SpringLayout());
		colorsPanel.add(nameLabelR);
		colorsPanel.add(inputPanelR);
		colorsPanel.add(nameLabelG);
		colorsPanel.add(inputPanelG);
		colorsPanel.add(nameLabelB);
		colorsPanel.add(inputPanelB);
		colorsPanel.add(nameLabelA);
		colorsPanel.add(inputPanelA);

		SpringUtilities.makeCompactGrid(colorsPanel, 4, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(colorsPanel, BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel(new SpringLayout());

		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftPanel.add(cbColorSpace);
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPanel.add(btRemove);
		buttonsPanel.add(leftPanel);
		buttonsPanel.add(rightPanel);

		SpringUtilities.makeCompactGrid(buttonsPanel, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		add(buttonsPanel, BorderLayout.SOUTH);

		setLabels();
	}

	public void setLabels() {
		Localization loc = kernel.getLocalization();

		setBorder(BorderFactory
				.createTitledBorder(loc.getMenu("DynamicColors")));

		if (allowSetComboBoxLabels) {
			cbColorSpace.removeActionListener(this);
			cbColorSpace.removeAllItems();
			cbColorSpace.addItem(loc.getMenu("RGB"));
			cbColorSpace.addItem(loc.getMenu("HSV"));
			cbColorSpace.addItem(loc.getMenu("HSL"));
			cbColorSpace.addActionListener(this);
		}
		allowSetComboBoxLabels = true;

		switch (colorSpace) {
		case GeoElement.COLORSPACE_RGB:
			nameLabelR.setText(loc.getMenu("Red") + ":");
			nameLabelG.setText(loc.getMenu("Green") + ":");
			nameLabelB.setText(loc.getMenu("Blue") + ":");
			break;
		case GeoElement.COLORSPACE_HSB:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Value") + ":");
			break;
		case GeoElement.COLORSPACE_HSL:
			nameLabelR.setText(loc.getMenu("Hue") + ":");
			nameLabelG.setText(loc.getMenu("Saturation") + ":");
			nameLabelB.setText(loc.getMenu("Lightness") + ":");
			break;
		}

		nameLabelA.setText(loc.getMenu("Opacity") + ":");

		btRemove.setToolTipText(loc.getPlainTooltip("Remove"));
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		// check for fillable geos in the current selection
		boolean someFillable = false;
		for (int i = 0; i < geos.length; i++) {
			if (((GeoElement) geos[i]).isFillable()) {
				someFillable = true;
				continue;
			}
		}

		// if we have any fillables then show the opacity field
		tfAlpha.setVisible(someFillable);
		nameLabelA.setVisible(someFillable);

		// set default field values
		GeoElement geo = (GeoElement) geos[0];
		Color col = geogebra.awt.GColorD.getAwtColor(geo.getObjectColor());
		defaultR = "" + col.getRed() / 255.0;
		defaultG = "" + col.getGreen() / 255.0;
		defaultB = "" + col.getBlue() / 255.0;
		defaultA = "" + geo.getFillColor().getAlpha() / 255.0;

		// remove action listeners
		tfRed.removeActionListener(this);
		tfGreen.removeActionListener(this);
		tfBlue.removeActionListener(this);
		tfAlpha.removeActionListener(this);
		btRemove.removeActionListener(this);
		cbColorSpace.removeActionListener(this);

		// take condition of first geo
		String strRed = "";
		String strGreen = "";
		String strBlue = "";
		String strAlpha = "";
		GeoElement geo0 = (GeoElement) geos[0];
		GeoList colorList = geo0.getColorFunction();
		if (colorList != null) {
			strRed = colorList.get(0).getLabel(StringTemplate.editTemplate);
			strGreen = colorList.get(1).getLabel(StringTemplate.editTemplate);
			strBlue = colorList.get(2).getLabel(StringTemplate.editTemplate);
			if (colorList.size() == 4)
				strAlpha = colorList.get(3).getLabel(
						StringTemplate.editTemplate);
		}

		// set the selected color space and labels to match the first geo's
		// color space
		colorSpace = geo0.getColorSpace();
		cbColorSpace.setSelectedIndex(colorSpace);
		allowSetComboBoxLabels = false;
		setLabels();

		// compare first geo with other selected geos
		// if difference exists in a color then null it out
		for (int i = 0; i < geos.length; i++) {
			geo = (GeoElement) geos[i];
			GeoList colorListTemp = geo.getColorFunction();
			if (colorListTemp != null) {
				String strRedTemp = colorListTemp.get(0).getLabel(
						StringTemplate.editTemplate);
				String strGreenTemp = colorListTemp.get(1).getLabel(
						StringTemplate.editTemplate);
				String strBlueTemp = colorListTemp.get(2).getLabel(
						StringTemplate.editTemplate);
				String strAlphaTemp = "";
				if (colorListTemp.size() == 4)
					strAlphaTemp = colorListTemp.get(3).getLabel(
							StringTemplate.editTemplate);
				if (!strRed.equals(strRedTemp))
					strRed = "";
				if (!strGreen.equals(strGreenTemp))
					strGreen = "";
				if (!strBlue.equals(strBlueTemp))
					strBlue = "";
				if (!strAlpha.equals(strAlphaTemp))
					strAlpha = "";
			}
		}

		// set the color fields
		tfRed.setText(strRed);
		tfGreen.setText(strGreen);
		tfBlue.setText(strBlue);
		tfAlpha.setText(strAlpha);

		// restore action listeners
		tfRed.addActionListener(this);
		tfGreen.addActionListener(this);
		tfBlue.addActionListener(this);
		tfAlpha.addActionListener(this);
		cbColorSpace.addActionListener(this);

		return this;
	}

	// return true: want to be able to color all spreadsheet objects
	private static boolean checkGeos(Object[] geos) {
		return true;
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfRed || e.getSource() == tfGreen
				|| e.getSource() == tfBlue || e.getSource() == tfAlpha)
			doActionPerformed();
		if (e.getSource() == cbColorSpace) {
			colorSpace = cbColorSpace.getSelectedIndex();
			allowSetComboBoxLabels = false;
			setLabels();
			doActionPerformed();
		}
	}

	private void doActionPerformed() {
		processed = true;

		GeoList list = null;
		GeoList listAlpha = null;
		String strRed = tfRed.getText();
		String strGreen = tfGreen.getText();
		String strBlue = tfBlue.getText();
		String strAlpha = tfAlpha.getText();

		strRed = PropertiesPanelD.replaceEqualsSigns(strRed);
		strGreen = PropertiesPanelD.replaceEqualsSigns(strGreen);
		strBlue = PropertiesPanelD.replaceEqualsSigns(strBlue);
		strAlpha = PropertiesPanelD.replaceEqualsSigns(strAlpha);

		if ((strRed == null || strRed.trim().length() == 0)
				&& (strGreen == null || strGreen.trim().length() == 0)
				&& (strAlpha == null || strAlpha.trim().length() == 0)
				&& (strBlue == null || strBlue.trim().length() == 0)) {
			// num = null;
		} else {
			if (strRed == null || strRed.trim().length() == 0)
				strRed = defaultR;
			if (strGreen == null || strGreen.trim().length() == 0)
				strGreen = defaultG;
			if (strBlue == null || strBlue.trim().length() == 0)
				strBlue = defaultB;
			if (strAlpha == null || strAlpha.trim().length() == 0)
				strAlpha = defaultA;

			list = kernel.getAlgebraProcessor().evaluateToList(
					"{" + strRed + "," + strGreen + "," + strBlue + "}");

			listAlpha = kernel.getAlgebraProcessor().evaluateToList(
					"{" + strRed + "," + strGreen + "," + strBlue + ","
							+ strAlpha + "}");

		}

		// set condition
		// try {
		if (list != null) { //
			if (((list.get(0) instanceof NumberValue)) && // bugfix, enter "x"
															// for a color
					((list.get(1) instanceof NumberValue)) && //
					((list.get(2) instanceof NumberValue)) && //
					((list.size() == 3 || list.get(3) instanceof NumberValue))) //
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement) geos[i];
					if (geo.isFillable() && listAlpha != null) {
						geo.setColorFunction(listAlpha);
						list = listAlpha; // to have correct update
					} else
						geo.setColorFunction(list);
					geo.setColorSpace(colorSpace);
				}

			list.updateRepaint();

			// to update "showObject" as well
			propPanel.updateSelection(geos);
		} else {
			// put back faulty text (for editing)
			tfRed.setText(strRed);
			tfGreen.setText(strGreen);
			tfBlue.setText(strBlue);
			tfAlpha.setText(strAlpha);
		}

	}

	public void focusGained(FocusEvent arg0) {
		processed = false;
	}

	private boolean processed = false;

	public void focusLost(FocusEvent e) {
		if (!processed)
			doActionPerformed();
	}

	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		setFont(font);

		cbColorSpace.setFont(font);

		nameLabelR.setFont(font);
		nameLabelG.setFont(font);
		nameLabelB.setFont(font);
		nameLabelA.setFont(font);

		btRemove.setFont(font);

		tfRed.setFont(font);
		tfGreen.setFont(font);
		tfBlue.setFont(font);
		tfAlpha.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}
}

/**
 * panel to set graphics view location
 * 
 * @author G.Sturr
 */
class GraphicsViewLocationPanel extends JPanel implements ActionListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts {

	private static final long serialVersionUID = 1L;

	private Object[] geos; // currently selected geos

	private JCheckBox cbGraphicsView, cbGraphicsView2;

	private Kernel kernel;
	private AppD app;
	private PropertiesPanelD propPanel;

	public GraphicsViewLocationPanel(AppD app, PropertiesPanelD propPanel) {
		this.app = app;
		kernel = app.getKernel();
		this.propPanel = propPanel;

		cbGraphicsView = new JCheckBox();
		cbGraphicsView2 = new JCheckBox();
		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(cbGraphicsView);
		add(cbGraphicsView2);

		setLabels();
	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(kernel.getApplication()
				.getMenu("Location")));
		cbGraphicsView.setText(app.getPlain("DrawingPad"));
		cbGraphicsView2.setText(app.getPlain("DrawingPad2"));

	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		cbGraphicsView.removeActionListener(this);
		cbGraphicsView2.removeActionListener(this);

		boolean isInEV = false;
		boolean isInEV2 = false;

		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN))
				isInEV = true;
			if (geo.isVisibleInView(App.VIEW_EUCLIDIAN2))
				isInEV2 = true;
		}

		cbGraphicsView.setSelected(isInEV);
		cbGraphicsView2.setSelected(isInEV2);

		cbGraphicsView.addActionListener(this);
		cbGraphicsView2.addActionListener(this);

		return this;
	}

	private boolean checkGeos(Object[] geos) {

		// always show this option, nothing to check

		return true;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == cbGraphicsView) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				if (cbGraphicsView.isSelected()) {
					// geo.addView(ev);
					// ev.add(geo);
					app.addToEuclidianView(geo);
				} else {
					// geo.removeView(ev);
					// ev.remove(geo);
					app.removeFromEuclidianView(geo);
				}
			}
		}

		if (e.getSource() == cbGraphicsView2) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];

				EuclidianView ev2 = app.getEuclidianView2();

				if (cbGraphicsView2.isSelected()) {
					geo.addView(App.VIEW_EUCLIDIAN2);
					ev2.add(geo);
				} else {
					geo.removeView(App.VIEW_EUCLIDIAN2);
					ev2.remove(geo);
				}
			}
		}

	}

	public void updateFonts() {
		Font font = app.getPlainFont();

		setFont(font);
		cbGraphicsView.setFont(font);
		cbGraphicsView2.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

}

class ButtonSizePanel extends JPanel implements ChangeListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts {

	private static final long serialVersionUID = 1L;
	private Object[] geos; // currently selected geos

	private MyTextField tfButtonWidth;
	private MyTextField tfButtonHeight;
	private JLabel labelWidth;
	private JLabel labelHeight;
	private JLabel labelPixelW;
	private JLabel labelPixelH;
	private Localization loc;
	private JCheckBox cbUseFixedSize;
	private int precHeight;
	private int precWidth;

	public ButtonSizePanel(AppD app, Localization loc) {
		this.loc = loc;
		labelWidth = new JLabel(loc.getPlain("Width"));
		labelHeight = new JLabel(loc.getPlain("Height"));
		labelPixelW = new JLabel(loc.getMenu("Pixels.short"));
		labelPixelH = new JLabel(loc.getMenu("Pixels.short"));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		cbUseFixedSize = new JCheckBox(loc.getPlain("Fixed"));
		tfButtonWidth = new MyTextField(app, 3);
		tfButtonHeight = new MyTextField(app, 3);
		tfButtonHeight.setInputVerifier(new SizeVerify());
		tfButtonWidth.setInputVerifier(new SizeVerify());
		cbUseFixedSize.addChangeListener(this);
		tfButtonHeight.setEnabled(cbUseFixedSize.isSelected());
		tfButtonWidth.setEnabled(cbUseFixedSize.isSelected());
		tfButtonHeight.addFocusListener(this);
		tfButtonWidth.addFocusListener(this);
		add(cbUseFixedSize);
		add(labelWidth);
		add(tfButtonWidth);
		add(labelPixelW);
		add(labelHeight);
		add(tfButtonHeight);
		add(labelPixelH);
	}

	public void updateFonts() {

	}

	public void setLabels() {
		setBorder(BorderFactory.createTitledBorder(loc.getPlain("ButtonSize")));
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;
		for (int i = 0; i < geos.length; i++) {
			GeoButton geo = (GeoButton) geos[i];
			cbUseFixedSize.removeChangeListener(this);
			cbUseFixedSize.setSelected(geo.isFixedSize());
			tfButtonHeight.setText("" + geo.getHeight());
			tfButtonWidth.setText("" + geo.getWidth());
			tfButtonHeight.setEnabled(geo.isFixedSize());
			tfButtonWidth.setEnabled(geo.isFixedSize());
			cbUseFixedSize.addChangeListener(this);
		}
		return this;
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent arg0) {
		for (int i = 0; i < geos.length; i++) {
			GeoButton geo = (GeoButton) geos[i];
			geo.setFixedSize(cbUseFixedSize.isSelected());
			if (cbUseFixedSize.isSelected()) {
				geo.setHeight(Integer.parseInt(tfButtonHeight.getText()));
				geo.setWidth(Integer.parseInt(tfButtonWidth.getText()));
			} else {
				geo.setFixedSize(false);
			}
		}
	}

	private boolean checkGeos(Object[] geos) {
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isGeoButton())
				return false;
		}
		return true;

	}

	public void stateChanged(ChangeEvent arg0) {
		if (arg0.getSource() == cbUseFixedSize) {
			JCheckBox check = (JCheckBox) arg0.getSource();
			for (int i = 0; i < geos.length; i++) {
				GeoButton geo = (GeoButton) geos[i];
				geo.setFixedSize(check.isSelected());
				tfButtonHeight.setEnabled(check.isSelected());
				tfButtonWidth.setEnabled(check.isSelected());
				tfButtonHeight.setText("" + geo.getHeight());
				tfButtonWidth.setText("" + geo.getWidth());
			}
		}
	}

	class SizeVerify extends InputVerifier {
		public boolean verify(JComponent input) {
			MyTextField tf = (MyTextField) input;
			String s = tf.getText();
			if (!s.matches("\\d{2,3}"))
				return false;
			if (Integer.parseInt(s) < 24 || Integer.parseInt(s) > 500)
				return false;
			return true;
		}
	}
}

/**
 * panel for name of object
 * 
 * @author Markus Hohenwarter
 */
class NamePanel extends JPanel implements ActionListener, FocusListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts, IObjectNameListener {

	private static final long serialVersionUID = 1L;

	private ObjectNameModel model;
	private AutoCompleteTextFieldD tfName, tfDefinition, tfCaption;

	private Runnable doActionStopped = new Runnable() {
		public void run() {
			model.setBusy(false);
		}
	};
	private JLabel nameLabel, defLabel, captionLabel;
	private InputPanelD inputPanelName, inputPanelDef, inputPanelCap;

	private AppD app;
	private Localization loc;

	public NamePanel(AppD app) {
		this.app = app;
		this.loc = app.getLocalization();
		model = new ObjectNameModel(app, this);
		// NAME PANEL
	

		// non auto complete input panel
		inputPanelName = new InputPanelD(null, app, 1, -1, true);
		tfName = (AutoCompleteTextFieldD) inputPanelName.getTextComponent();
		tfName.setAutoComplete(false);
		tfName.addActionListener(this);
		tfName.addFocusListener(this);

	
		// definition field: non auto complete input panel
		inputPanelDef = new InputPanelD(null, app, 1, -1, true);
		tfDefinition = (AutoCompleteTextFieldD) inputPanelDef
				.getTextComponent();
		tfDefinition.setAutoComplete(false);
		tfDefinition.addActionListener(this);
		tfDefinition.addFocusListener(this);

		// caption field: non auto complete input panel
		inputPanelCap = new InputPanelD(null, app, 1, -1, true);
		tfCaption = (AutoCompleteTextFieldD) inputPanelCap.getTextComponent();
		tfCaption.setAutoComplete(false);
		tfCaption.addActionListener(this);
		tfCaption.addFocusListener(this);

		// name panel
		nameLabel = new JLabel();
		nameLabel.setLabelFor(inputPanelName);

		// definition panel
		defLabel = new JLabel();
		defLabel.setLabelFor(inputPanelDef);

		// caption panel
		captionLabel = new JLabel();
		captionLabel.setLabelFor(inputPanelCap);

		setLabels();
		updateGUI(true, true);
	}

	public void setLabels() {
		nameLabel.setText(loc.getPlain("Name") + ":");
		defLabel.setText(loc.getPlain("Definition") + ":");
		captionLabel.setText(loc.getMenu("Button.Caption") + ":");
	}

	public void updateGUI(boolean showDefinition, boolean showCaption) {
		int rows = 1;
		removeAll();

		if (loc.isRightToLeftReadingOrder()) {
			add(inputPanelName);
			add(nameLabel);
		} else {
			add(nameLabel);
			add(inputPanelName);
		}

		if (showDefinition) {
			rows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelDef);
				add(defLabel);
			} else {
				add(defLabel);
				add(inputPanelDef);
			}
		}

		if (showCaption) {
			rows++;
			if (loc.isRightToLeftReadingOrder()) {
				add(inputPanelCap);
				add(captionLabel);
			} else {
				add(captionLabel);
				add(inputPanelCap);
			}
		}

		app.setComponentOrientation(this);

		this.rows = rows;
		setLayout();

	}

	private int rows;

	private void setLayout() {
		// Lay out the panel
		setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(this, rows, 2, // rows, cols
				5, 5, // initX, initY
				5, 5); // xPad, yPad
	}
	
	/**
	 * current geo on which focus lost shouls apply
	 * (may be different to current geo, due to threads)
	 */
	private GeoElement currentGeoForFocusLost = null;

	public JPanel update(Object[] geos) {

		/*
		 * DON'T WORK : MAKE IT A TRY FOR 5.0 ? //apply textfields modification
		 * on previous geo before switching to new geo //skip this if label is
		 * not set (we re in the middle of redefinition) //skip this if action
		 * is performing if (currentGeo!=null && currentGeo.isLabelSet() &&
		 * !actionPerforming && (geos.length!=1 || geos[0]!=currentGeo)){
		 * 
		 * //App.printStacktrace("\n"+tfName.getText()+"\n"+currentGeo.getLabel(
		 * StringTemplate.defaultTemplate));
		 * 
		 * String strName = tfName.getText(); if (strName !=
		 * currentGeo.getLabel(StringTemplate.defaultTemplate))
		 * nameInputHandler.processInput(tfName.getText());
		 * 
		 * 
		 * String strDefinition = tfDefinition.getText(); if
		 * (strDefinition.length()>0 &&
		 * !strDefinition.equals(getDefText(currentGeo)))
		 * defInputHandler.processInput(strDefinition);
		 * 
		 * String strCaption = tfCaption.getText(); if
		 * (!strCaption.equals(currentGeo.getCaptionSimple())){
		 * currentGeo.setCaption(tfCaption.getText());
		 * currentGeo.updateVisualStyleRepaint(); } }
		 */

		model.setGeos(geos);
		if (!model.checkGeos()) {
			// currentGeo=null;
			return null;
		}
		

		model.updateProperties();

		return this;
	}

	private String redefinitionForFocusLost = "";
	
	public void updateDef(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy())
			return;
		
		tfDefinition.removeActionListener(this);
		model.getDefInputHandler().setGeoElement(geo);
		tfDefinition.setText(model.getDefText(geo));
		tfDefinition.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	public void updateName(GeoElement geo) {

		// do nothing if called by doActionPerformed
		if (model.isBusy())
			return;

		tfName.removeActionListener(this);
		model.getNameInputHandler().setGeoElement(geo);
		tfName.setText(geo.getLabel(StringTemplate.editTemplate));
		tfName.addActionListener(this);

		// App.printStacktrace(""+geo);
	}

	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (model.isBusy()) {
			return;
		}
		
		doActionPerformed(e.getSource());
	}

	private synchronized void doActionPerformed(Object source) {
		

		model.setBusy(true);

		if (source == tfName) {
			// rename
			model.applyNameChange(tfName.getText());
				
		} else if (source == tfDefinition) {
		
			model.applyDefinitionChange(tfDefinition.getText());
			tfDefinition.requestFocusInWindow();
			
		} else if (source == tfCaption) {
			model.applyCaptionChange(tfCaption.getText());
		}

		SwingUtilities.invokeLater(doActionStopped);
	}

	public void focusGained(FocusEvent arg0) {
		//started to type something : store current geo if focus lost
		currentGeoForFocusLost = model.getCurrentGeo();
	}

	public void focusLost(FocusEvent e) {
		
		if (model.isBusy()) {
			return;
		}

		Object source = e.getSource();

		if (source == tfDefinition) {
			model.redefineCurrentGeo(currentGeoForFocusLost, tfDefinition.getText(),
						redefinitionForFocusLost);
		
			SwingUtilities.invokeLater(doActionStopped);
		
		} else {
				doActionPerformed(source);
			}
}

	
	public void updateFonts() {
		Font font = app.getPlainFont();

		nameLabel.setFont(font);
		defLabel.setFont(font);
		captionLabel.setFont(font);

		inputPanelName.updateFonts();
		inputPanelDef.updateFonts();
		inputPanelCap.updateFonts();

		setLayout();

	}

	public void updateVisualStyle(GeoElement geo) {
		// NOTHING SHOULD BE DONE HERE (ENDLESS CALL WITH UPDATE)

	}

	public void setNameText(final String text) {
		tfName.setText(text);
		tfName.requestFocus();
	}

	public void setDefinitionText(final String text) {
		tfDefinition.setText(text);
	}

	public void setCaptionText(final String text) {
		tfCaption.setText(text);
		tfCaption.requestFocus();
	}
	public void updateCaption() {
		tfCaption.removeActionListener(this);
		tfCaption.setText(model.getCurrentGeo().getRawCaption());
		tfCaption.addActionListener(this);
		
	}

	public void updateDefLabel() {
		updateDef(model.getCurrentGeo());

		if (model.getCurrentGeo().isIndependent()) {
			defLabel.setText(app.getPlain("Value") + ":");
		} else {
			defLabel.setText(app.getPlain("Definition") + ":");
		}
	}

	public void updateName(String text) {
		tfName.removeActionListener(this);
		tfName.setText(text);

		// if a focus lost is called in between, we keep the current definition text
		redefinitionForFocusLost = tfDefinition.getText();
		tfName.addActionListener(this);

		
	}

}