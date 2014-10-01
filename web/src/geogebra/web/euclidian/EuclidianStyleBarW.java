package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Previewable;
import geogebra.common.gui.dialog.options.model.LineStyleModel;
import geogebra.common.gui.dialog.options.model.LineStyleModel.ILineStyleListener;
import geogebra.common.gui.dialog.options.model.PointStyleModel;
import geogebra.common.gui.util.SelectionTable;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.algos.AlgoTableText;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.TextProperties;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.SelectionManager;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GFontW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.color.ColorPopupMenuButton;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.gui.images.StyleBarResources;
import geogebra.web.gui.util.ButtonPopupMenu;
import geogebra.web.gui.util.GeoGebraIcon;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.LineStylePopup;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PointStylePopup;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.util.PopupMenuHandler;
import geogebra.web.gui.util.StyleBarW;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianStyleBarW extends StyleBarW
	implements geogebra.common.euclidian.EuclidianStyleBar, ValueChangeHandler<Boolean>, //ClickHandler,
	PopupMenuHandler{

	public static ButtonPopupMenu CURRENT_POP_UP = null;
	EuclidianController ec;
	protected EuclidianView ev;
	private Construction cons;

	protected HashMap<Integer, Integer> defaultGeoMap;
	private ArrayList<GeoElement> defaultGeos;
	private GeoElement oldDefaultGeo;

	// flags and constants
	protected int iconHeight = 24;
	//private Dimension iconDimension = new Dimension(16, iconHeight);
	public int mode = -1;
	private boolean isIniting;
	private boolean needUndo = false;
	private Integer oldDefaultMode;
	private boolean modeChanged = true;

	// button-specific fields
	// TODO: create button classes so these become internal
	AlgoTableText tableText;

	HashMap<Integer, Integer> lineStyleMap;

	HashMap<Integer, Integer> pointStyleMap;


	private class EuclidianLineStylePopup extends LineStylePopup implements ILineStyleListener {
		private LineStyleModel model;
		
		public EuclidianLineStylePopup(AppW app, ImageOrText[] data, Integer rows,
                Integer columns, SelectionTable mode,
                boolean hasTable, boolean hasSlider) {
	        super(app, data, rows, columns, mode, hasTable, hasSlider);
	        model = new LineStyleModel(this);
	        this.setKeepVisible(false);
	        getMySlider().addChangeHandler(new ChangeHandler(){

				public void onChange(ChangeEvent event) {
					model.applyThickness(getSliderValue());	                
                }});
        }

		
		@Override
		public void update(Object[] geos) {
			model.setGeos(geos);

			if (!model.hasGeos() ) {
				this.setVisible(false);
				return;
			}

			boolean geosOK = model.checkGeos(); 
			this.setVisible(geosOK);
			
			if (geosOK) {
				setFgColor((GColorW) geogebra.common.awt.GColor.black);
				model.updateProperties();
				GeoElement geo0 = model.getGeoAt(0);
				if (hasSlider()) {
					setSliderValue(geo0.getLineThickness());
				}
				selectLineType(geo0.getLineType());
				
			}
		}
		
		@Override
		public void handlePopupActionEvent(){
			model.applyLineTypeFromIndex(getSelectedIndex());
			getMyPopup().hide();
		}

		public void setThicknessSliderValue(int value) {
			getMySlider().setValue(value);

		}

		public void setThicknessSliderMinimum(int minimum) {
			getMySlider().setMinimum(minimum);

		}

		public void selectCommonLineStyle(boolean equalStyle, int type) {
			selectLineType(type);
		}


		public void setLineTypeVisible(boolean value) {
	        // TODO Auto-generated method stub
	        
        }


		public void setOpacitySliderValue(int value) {
	        // TODO Auto-generated method stub
	        
        }


		public void setLineOpacityVisible(boolean value) {
	        // TODO Auto-generated method stub
	        
        }	    

		
	}
	// buttons and lists of buttons
	private ColorPopupMenuButton btnColor, btnBgColor, btnTextColor;

	private PopupMenuButton btnPointStyle, btnTextSize, btnMode, btnShowGrid;
	private EuclidianLineStylePopup btnLineStyle;
	PopupMenuButton btnTableTextJustify;

	PopupMenuButton btnTableTextBracket;

	private PopupMenuButton btnLabelStyle;

	protected PopupMenuButton btnPointCapture;

	private MyToggleButton2[] btnDeleteSizes=new MyToggleButton2[3];

	private MyToggleButton2 btnCopyVisualStyle, btnPen, 
	btnShowAxes;
	protected MyToggleButton2 btnStandardView;

	MyToggleButton2 btnBold;

	MyToggleButton2 btnItalic;

	private MyToggleButton2 btnDelete;

	private MyToggleButton2 btnLabel;

	private MyToggleButton2 btnPenEraser;

	MyToggleButton2 btnHideShowLabel;

	private MyToggleButton2 btnTableTextLinesV;

	private MyToggleButton2 btnTableTextLinesH;

	private MyToggleButton2[] toggleBtnList;
	private PopupMenuButton[] popupBtnList;
	
	//private MyCJButton btnDeleteGeo;
	
	private enum StyleBarMethod {NONE, UPDATE, UPDATE_STYLE};
	
	private StyleBarMethod waitingOperation = StyleBarMethod.NONE;

	/**
	 * Toggle button that should be visible if no geos are selected or to be created
	 * and no special icons appear in stylebar (eg. delete mode)
	 */
	protected class MyToggleButtonForEV extends MyToggleButton2{
		/**
		 * @param img image
		 * @param iconHeight height in pixels
		 */
		public MyToggleButtonForEV(ImageResource img, int iconHeight) {
	        super(img, iconHeight);
        }

		@Override
		public void update(Object[] geos){
			this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(mode)
					&& mode != EuclidianConstants.MODE_DELETE);
		}
	}

	public EuclidianStyleBarW(EuclidianView ev) {
		super((AppW) ev.getApplication(), ev.getViewID());

		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController();
		cons = app.getKernel().getConstruction();

		// init handling of default geos
		createDefaultMap();
		defaultGeos = new ArrayList<GeoElement>();

		// toolbar display settings
		//setFloatable(false);
		//Dimension d = getPreferredSize();
		//d.height = iconHeight + 8;
		//setPreferredSize(d);

		// init button-specific fields
		// TODO: put these in button classes
		EuclidianStyleBarStatic.pointStyleArray = EuclidianView.getPointStyles();
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.pointStyleArray.length; i++)
			pointStyleMap.put(EuclidianStyleBarStatic.pointStyleArray[i], i);

		EuclidianStyleBarStatic.lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianStyleBarStatic.lineStyleArray.length; i++)
			lineStyleMap.put(EuclidianStyleBarStatic.lineStyleArray[i], i);

		initGUI();
		isIniting = false;		
		setMode(ev.getMode()); // this will also update the stylebar
		setToolTips();
		
	}
	
	
	/**
	 * create default map between default geos and modes
	 */
	protected void createDefaultMap(){
		defaultGeoMap = EuclidianStyleBarStatic.createDefaultMap();
	}
	
	/**
	 * 
	 * @return euclidian view attached
	 */
	public EuclidianView getView(){
		return ev;
	}

	public int getMode() {
		return mode;
	}

	public void updateButtonPointCapture(int mode) {
		if (mode == 3 || mode == 0)
			mode = 3 - mode; // swap 0 and 3
		btnPointCapture.setSelectedIndex(mode);
	}

	public void setMode(int mode) {

		if (this.mode == mode) {
			modeChanged = false;
			return;
		}
		modeChanged = true;
		this.mode = mode;

		// MODE_TEXT temporarily switches to MODE_SELECTION_LISTENER
		// so we need to ignore this.
		if (mode == EuclidianConstants.MODE_SELECTION_LISTENER) {
			modeChanged = false;
			return;
		}
		
		if (CURRENT_POP_UP != null) {
			CURRENT_POP_UP.hide();
		}

		updateStyleBar();

    }

	protected boolean isVisibleInThisView(GeoElement geo) {
		return geo.isVisibleInView(ev.getViewID());
	}

	public void restoreDefaultGeo() {
		if (oldDefaultGeo != null)
			oldDefaultGeo = cons.getConstructionDefaults().getDefaultGeo(
					oldDefaultMode);
	}

	public void setLabels() {
		
		initGUI();
		updateStyleBar();
	    setToolTips();
    }
	
	protected void setAxesAndGridToolTips(Localization loc){
		btnShowGrid.setToolTipText(loc.getPlainTooltip("stylebar.Grid"));
		btnShowAxes.setToolTipText(loc.getPlainTooltip("stylebar.Axes"));
	}
	
	/**
	 * set tool tips
	 */
	protected void setToolTips() {

	    Localization loc = app.getLocalization();

	    setAxesAndGridToolTips(loc);
		btnStandardView.setToolTipText(loc.getPlainTooltip("stylebar.ViewDefault"));
		btnLabelStyle.setToolTipText(loc.getPlainTooltip("stylebar.Label"));
		btnColor.setToolTipText(loc.getPlainTooltip("stylebar.Color"));
		btnBgColor.setToolTipText(loc.getPlainTooltip("stylebar.BgColor"));
		btnLineStyle.setToolTipText(loc.getPlainTooltip("stylebar.LineStyle"));
		btnPointStyle
				.setToolTipText(loc.getPlainTooltip("stylebar.PointStyle"));
		btnTextColor.setToolTipText(loc.getPlainTooltip("stylebar.TextColor"));
		btnTextSize.setToolTipText(loc.getPlainTooltip("stylebar.TextSize"));
		btnBold.setToolTipText(loc.getPlainTooltip("stylebar.Bold"));
		btnItalic.setToolTipText(loc.getPlainTooltip("stylebar.Italic"));
		
		/*
		btnTableTextJustify.setToolTipText(loc
		        .getPlainTooltip("stylebar.Align"));
		btnTableTextBracket.setToolTipText(loc
		        .getPlainTooltip("stylebar.Bracket"));
		btnTableTextLinesV.setToolTipText(loc
		        .getPlainTooltip("stylebar.VerticalLine"));
		btnTableTextLinesH.setToolTipText(loc
		        .getPlainTooltip("stylebar.HorizontalLine"));

		btnPen.setToolTipText(loc.getPlainTooltip("stylebar.Pen"));
		btnFixPosition.setToolTipText(loc
		        .getPlainTooltip("AbsoluteScreenLocation"));

		btnDeleteSize.setToolTipText(loc.getPlainTooltip("Size"));
		*/
	
    }
	
	private ArrayList<GeoElement> activeGeoList;
	private boolean visible;

	@Override
	public void setOpen(boolean visible) {
		this.visible = visible;
		if (visible) {
			switch (this.waitingOperation) {
			case UPDATE:
				updateStyleBar();
				break;
			case UPDATE_STYLE:
				updateButtons();
				break;
			}
			this.waitingOperation = StyleBarMethod.NONE;
		}
	}

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	public void updateStyleBar() {
		if(!visible){
			this.waitingOperation = StyleBarMethod.UPDATE;
			return;
		}

		// -----------------------------------------------------
		// Create activeGeoList, a list of geos the stylebar can adjust.
		// These are either the selected geos or the current default geo.
		// Each button uses this list to update its gui and set visibility
		// -----------------------------------------------------
		activeGeoList = new ArrayList<GeoElement>();
		
		// -----------------------------------------------------
		// MODE_MOVE case: load activeGeoList with all selected geos
		// -----------------------------------------------------
		if (mode == EuclidianConstants.MODE_MOVE) {

			boolean hasGeosInThisView = false;
			SelectionManager selection = ev.getApplication().getSelectionManager();
			for (GeoElement geo : selection
					.getSelectedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible() && !geo.isAxis()) {
					hasGeosInThisView = true;
					break;
				}
			}	
			for (GeoElement geo : ec.getJustCreatedGeos()) {
				if (isVisibleInThisView(geo) && geo.isEuclidianVisible()) {
					hasGeosInThisView = true;
					break;
				}
			}
			if (hasGeosInThisView) {
				activeGeoList = selection
						.getSelectedGeos();

				// we also update stylebars according to just created geos
				activeGeoList.addAll(ec.getJustCreatedGeos());
			}
		}

		// -----------------------------------------------------
		// MODE_PEN: for the pen mode the default construction is 
		// saved in EuclidianPen		
		// All other modes: load activeGeoList with current default geo
		// -----------------------------------------------------
		else if (defaultGeoMap.containsKey(mode) || mode == EuclidianConstants.MODE_PEN) {

			// Save the current default geo state in oldDefaultGeo.
			// Stylebar buttons can temporarily change a default geo, but this
			// default
			// geo is always restored to its previous state after a mode change.

			if (oldDefaultGeo != null && modeChanged) {
				// add oldDefaultGeo to the default map so that the old default
				// is restored
				cons.getConstructionDefaults().addDefaultGeo(oldDefaultMode,
						oldDefaultGeo);
				oldDefaultGeo = null;
				oldDefaultMode = null;
			}

			// get the current default geo
			GeoElement geo = mode == EuclidianConstants.MODE_PEN ? ec.getPen().DEFAULT_PEN_LINE : 
					cons.getConstructionDefaults().getDefaultGeo(defaultGeoMap.get(mode));
			if (geo != null)
				activeGeoList.add(geo);

			// update the defaultGeos field (needed elsewhere for adjusting
			// default geo state)
			defaultGeos = activeGeoList;

			// update oldDefaultGeo
			if (modeChanged) {
				if (defaultGeos.size() == 0) {
					oldDefaultGeo = null;
					oldDefaultMode = -1;
				} else {
					oldDefaultGeo = defaultGeos.get(0);
					oldDefaultMode = defaultGeoMap.get(mode);
				}
			}

			// we also update stylebars according to just created geos
			activeGeoList.addAll(ec.getJustCreatedGeos());
		}
		updateButtons();
		// show the pen delete button
		// TODO: handle pen mode in code above
		//btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		addButtons();
    }
	

	private void updateButtons(){
		// -----------------------------------------------------
		// update the buttons
		// note: this must always be done, even when activeGeoList is empty
		// -----------------------------------------------------
		if (activeGeoList == null){
			return;
		}
		double l = System.currentTimeMillis();
		Object[] geos = activeGeoList.toArray();
		tableText = EuclidianStyleBarStatic.updateTableText(geos, mode);
		App.debug("tabletext"+(System.currentTimeMillis()-l));
		for (int i = 0; i < popupBtnList.length; i++) {
			if (popupBtnList[i] != null){//null pointer fix until necessary
				popupBtnList[i].update(geos);
				App.debug(i+"popup"+(System.currentTimeMillis()-l));
			}
		}
		for (int i = 0; i < toggleBtnList.length; i++) {
			if (toggleBtnList[i] != null){//null pointer fix until necessary
				toggleBtnList[i].update(geos);
				App.debug(i+"toggle"+(System.currentTimeMillis()-l));
			}
		}

	}
	

	public void updateVisualStyle(GeoElement geo) {
		if(activeGeoList!=null && activeGeoList.contains(geo)){
			if(!visible){
				this.waitingOperation = StyleBarMethod.UPDATE_STYLE;
				return;
			}
			updateButtons();
		}
	}
	// =====================================================
	// Init GUI
	// =====================================================

	private void initGUI() {

		createButtons();
		createColorButton();
		createBgColorButton();
		createTextButtons();
		setActionCommands();

		addButtons();
	
		popupBtnList = newPopupBtnList();
		toggleBtnList = newToggleBtnList();
	}
	
	protected void setActionCommands(){
		setActionCommand(btnShowAxes, "showAxes");
		setActionCommand(btnStandardView, "standardView");
		setActionCommand(btnPointCapture, "pointCapture");
	}

	/**
	 * adds/removes buttons 
	 * (must be called on updates so that separators are drawn only when needed)
	 */
	private void addButtons() {

		clear();
		

		//--- order matters here

		// add graphics decoration buttons
		addGraphicsDecorationsButtons();
		addBtnPointCapture();

		// add color and style buttons
		
		add(btnColor);
		add(btnBgColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);
		
		// add text decoration buttons
		if(btnBold.isVisible())
			addSeparator();
		
		add(btnBold);
		add(btnItalic);
		add(btnTextSize);

		/*add(btnTableTextJustify);
		add(btnTableTextLinesV);
		add(btnTableTextLinesH);
		add(btnTableTextBracket);*/

		// add(btnPenEraser);
		// add(btnHideShowLabel);
		add(btnLabelStyle);
		
		// add(btnPointCapture);
		addBtnRotateView();
		// add(btnPenDelete);
		
		for(int i=0;i<3;i++){
			add(btnDeleteSizes[i]);
		}
		getViewButton();
	}
	
	protected void addBtnRotateView() {

	}


	/**
	 * add axes, grid, ... buttons
	 */
	protected void addGraphicsDecorationsButtons() {
		addAxesAndGridButtons();
		add(btnStandardView);
	}
	
	/**
	 * add axes and grid buttons
	 */
	protected void addAxesAndGridButtons(){
		add(btnShowAxes);
		add(btnShowGrid);		
	}



	protected void addBtnPointCapture() {
		add(btnPointCapture);
	}

	protected MyToggleButton2 getAxesOrGridToggleButton(){
		return btnShowAxes;
	}
	
	protected PopupMenuButton getAxesOrGridPopupMenuButton(){
		return btnShowGrid;
	}
	
	protected MyToggleButton2[] newToggleBtnList() {
		return new MyToggleButton2[] { btnCopyVisualStyle, btnPen, 
				getAxesOrGridToggleButton(), btnStandardView, btnBold, btnItalic, btnDelete, btnLabel,
				btnPenEraser, btnHideShowLabel, btnTableTextLinesV,
				btnTableTextLinesH, btnDeleteSizes[0],btnDeleteSizes[1],btnDeleteSizes[2] };
	}
	
	protected PopupMenuButton[] newPopupBtnList() {
		return new PopupMenuButton[] { getAxesOrGridPopupMenuButton(), btnColor, btnBgColor, btnTextColor,
		        btnLineStyle, btnPointStyle, btnTextSize, btnTableTextJustify,
		        btnTableTextBracket, btnLabelStyle, btnPointCapture
		         };
	}

	// =====================================================
	// Create Buttons
	// =====================================================
	
	protected void createAxesAndGridButtons(){
		// ========================================
		// show axes button
		btnShowAxes = new MyToggleButtonForEV(StyleBarResources.INSTANCE.axes(), iconHeight);
		// btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		// ========================================
		// show grid button
		ImageOrText[] grids = new ImageOrText[4];
		for (int i = 0; i < 4; i++)
			grids[i] = GeoGebraIcon.createGridStyleIcon(
					EuclidianStyleBarStatic.pointStyleArray[i]);
		btnShowGrid = new GridPopup(app, grids, -1, 4,
				geogebra.common.gui.util.SelectionTable.MODE_ICON,
				ev);
		// btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addPopupHandler(this);


	}

	protected void createButtons() {
		// TODO: fill in
		
		// ========================================
		// mode button
		
		ImageOrText [] modeArray = ImageOrText.convert(new ImageResource [] {
				AppResources.INSTANCE.cursor_arrow(),
				AppResources.INSTANCE.application_graphics(),
				AppResources.INSTANCE.delete_small(),
				AppResources.INSTANCE.mode_point_16(),
				AppResources.INSTANCE.mode_copyvisualstyle_16()
		});
		
		btnMode = new PopupMenuButton((AppW) ev.getApplication(),
				modeArray, -1, 1, new GDimensionW(20, iconHeight),
				geogebra.common.gui.util.SelectionTable.MODE_ICON);
//		btnMode.addActionListener(this);
		btnMode.setKeepVisible(false);
		// add(btnMode);
		
		// ========================================
		// delete button
		btnDelete = new MyToggleButton2(
				AppResources.INSTANCE.delete_small(),
				iconHeight) {

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)
						|| mode == EuclidianConstants.MODE_DELETE);
			}
		};
		btnDelete.addValueChangeHandler(this);
		// add(btnDelete);
		// ========================================
		// hide/show labels button
		btnLabel = new MyToggleButton2(
				AppResources.INSTANCE.mode_copyvisualstyle_16(),
				iconHeight) {

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)
						|| mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
			}
		};
		btnLabel.addValueChangeHandler(this);
		// add(btnLabel);
		
		// ========================================
		// visual style button

		btnCopyVisualStyle = new MyToggleButton2(
				AppResources.INSTANCE.mode_copyvisualstyle_16(),
				iconHeight) {

			@Override
			public void update(Object[] geos) {
				this.setVisible((geos.length > 0 && mode == EuclidianConstants.MODE_MOVE));
			}
		};
		btnCopyVisualStyle.addValueChangeHandler(this);
		// add(this.btnCopyVisualStyle);
		
		
		
		
		
		createAxesAndGridButtons();
		
		
		btnStandardView = new MyToggleButtonForEV(StyleBarResources.INSTANCE.standard_view(), iconHeight);
		btnStandardView.addValueChangeHandler(this);


		// ========================================
		// line style button

		// create line style icon array
		// create button

		LineStylePopup.setMode(mode);
		EuclidianLineStylePopup.fillData(iconHeight);
		btnLineStyle = new EuclidianLineStylePopup(app, LineStylePopup.getLineStyleIcons(), -1, 5,
				 geogebra.common.gui.util.SelectionTable.MODE_ICON,
				true, true);

		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(13);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);
//		btnLineStyle.addActionListener(this);
		btnLineStyle.addPopupHandler(this);

		// create button
		btnPointStyle = PointStylePopup.create(app, iconHeight, mode, true,
				new PointStyleModel(null));

		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);
//		btnPointStyle.addActionListener(this);
		btnPointStyle.addPopupHandler(this);

		// ========================================
		// eraser button
		btnPenEraser = new MyToggleButton2(AppResources.INSTANCE.delete_small(), iconHeight) {

			@Override
			public void update(Object[] geos) {
				this.setVisible(EuclidianView.isPenMode(mode));
			}
		};

		btnPenEraser.addValueChangeHandler(this);

		// ========================================
		// delete geo button
		// btnDeleteGeo = new MyCJButton(AppResources.INSTANCE.delete_small());
		// btnDeleteGeo.addClickHandler(this);
		// add(btnDeleteGeo);

		// ========================================
		// hide/show label button
		btnHideShowLabel = new MyToggleButton2(
				AppResources.INSTANCE.mode_showhidelabel_16(), iconHeight) {

			@Override
			public void update(Object[] geos) {
				// only show this button when handling selection, do not use it
				// for defaults
				if (mode != EuclidianConstants.MODE_MOVE) {
					this.setVisible(false);
					return;
				}
				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					if ((((GeoElement) geos[i])
							.getGeoElementForPropertiesDialog().isGeoText())) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);
				if (geosOK) {
					btnHideShowLabel.setSelected(((GeoElement) geos[0])
							.isLabelVisible());
				}
			}

		};

		btnHideShowLabel.addValueChangeHandler(this);

		// ========================================
		// caption style button

		ImageOrText[] captionArray = ImageOrText.convert(new String[] { app.getPlain("stylebar.Hidden"), // index
																				// 4
				app.getPlain("Name"), // index 0
				app.getPlain("NameAndValue"), // index 1
				app.getPlain("Value"), // index 2
				app.getPlain("Caption") // index 3
		});

		btnLabelStyle = new PopupMenuButton(app, captionArray, -1, 1,
				new GDimensionW(0, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {
				boolean geosOK = false;
				GeoElement geo = null;
				if (mode == EuclidianConstants.MODE_MOVE) {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								|| ((GeoElement) geos[i]).isGeoAngle()
								|| (((GeoElement) geos[i]).isGeoNumeric() ? ((GeoNumeric) geos[i])
										.isSliderFixed() : false)) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_OFF) {
					this.setVisible(false);
					return;
				} else if (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY) {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								&& ((GeoElement) geos[i]).isGeoPoint()) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				} else {
					for (int i = 0; i < geos.length; i++) {
						if (((GeoElement) geos[i]).isLabelShowable()
								|| ((GeoElement) geos[i]).isGeoAngle()
								|| (((GeoElement) geos[i]).isGeoNumeric() ? ((GeoNumeric) geos[i])
										.isSliderFixed() : false)) {
							geo = (GeoElement) geos[i];
							geosOK = true;
							break;
						}
					}
				}
				this.setVisible(geosOK);

				if (geosOK) {
					if (!geo.isLabelVisible())
						setSelectedIndex(0);
					else
						setSelectedIndex(geo.getLabelMode() + 1);

				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}
		};
		ImageResource ic = AppResources.INSTANCE.mode_showhidelabel_16();
		//must be done with callback btnLabelStyle.setIcon(ic);
		AppResourcesConverter.setIcon(ic, btnLabelStyle);
//		btnLabelStyle.addActionListener(this);
		btnLabelStyle.addPopupHandler(this);
		btnLabelStyle.setKeepVisible(false);

		// ========================================
		// point capture button

		ImageOrText[] strPointCapturing = ImageOrText.convert(new String[]{ app.getMenu("Labeling.automatic"),
				app.getMenu("SnapToGrid"), app.getMenu("FixedToGrid"),
				app.getMenu("off") });

		btnPointCapture = new PopupMenuButton(app, strPointCapturing, -1, 1, 
				new GDimensionW(0, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {
				// same as axes
				this.setVisible(geos.length == 0  && !EuclidianView.isPenMode(mode)
						&& mode != EuclidianConstants.MODE_DELETE);
			}

			@Override
			public ImageOrText getButtonIcon() {
				return this.getIcon();
			}

		}; 

		//it is not needed, must be an Image preloaded like others. 
		ImageResource ptCaptureIcon = StyleBarResources.INSTANCE.magnet();
		//must be done in callback btnPointCapture.setIcon(ptCaptureIcon); 
		AppResourcesConverter.setIcon(ptCaptureIcon, btnPointCapture); 
		//btnPointCapture.addActionListener(this);
		btnPointCapture.addPopupHandler(this);
		btnPointCapture.setKeepVisible(false);

		// =====================================================
		// Delete Size Button
		ImageResource[] delBtns = new ImageResource[]{
				StyleBarResources.INSTANCE.stylingbar_delete_small(),
				StyleBarResources.INSTANCE.stylingbar_delete_medium(),
				StyleBarResources.INSTANCE.stylingbar_delete_large()
		};
		for(int i=0; i<3; i++){
			btnDeleteSizes[i]= new MyToggleButton2(delBtns[i]){
	
				@Override
	            public void update(Object[] geos) {
					// always show this button unless in pen mode
					this.setVisible(mode==EuclidianConstants.MODE_DELETE);
	            }
				
			};
			btnDeleteSizes[i].addValueChangeHandler(this);
		}
	}

	// ========================================
		// object color button (color for everything except text)

		private void createColorButton() {

			final GDimensionW colorIconSize = new GDimensionW(20, iconHeight);
			btnColor = new ColorPopupMenuButton(app, colorIconSize,
					ColorPopupMenuButton.COLORSET_DEFAULT, true) {

				@Override
				public void update(Object[] geos) {

					if (mode == EuclidianConstants.MODE_FREEHAND_SHAPE){//EuclidianView.isPenMode(mode)) {
						/*this.setVisible(true);

						setSelectedIndex(getColorIndex(ec.getPen().getPenColor()));

						setSliderValue(100);
						getMySlider().setVisible(false);*/
						App.debug("MODE_FREEHAND_SHAPE not working in StyleBar yet");

					} else {
						boolean geosOK = (geos.length > 0 || EuclidianView.isPenMode(mode));
						for (int i = 0; i < geos.length; i++) {
							GeoElement geo = ((GeoElement) geos[i])
									.getGeoElementForPropertiesDialog();
							if (geo instanceof GeoImage || geo instanceof GeoText
									|| geo instanceof GeoButton) {
								geosOK = false;
								break;
							}
						}

						setVisible(geosOK);

						if (geosOK) {
							// get color from first geo
							geogebra.common.awt.GColor geoColor;
							geoColor = ((GeoElement) geos[0]).getObjectColor();

							// check if selection contains a fillable geo
							// if true, then set slider to first fillable's alpha
							// value
							float alpha = 1.0f;
							boolean hasFillable = false;
							for (int i = 0; i < geos.length; i++) {
								if (((GeoElement) geos[i]).isFillable()) {
									hasFillable = true;
									alpha = ((GeoElement) geos[i]).getAlphaValue();
									break;
								}
							}
							
							if (hasFillable)
								setTitle(app
										.getPlain("stylebar.ColorTransparency"));
							else
								setTitle(app.getPlain("stylebar.Color"));
							setSliderVisible(hasFillable);
							
							setSliderValue(Math.round(alpha * 100));

							updateColorTable();

							// find the geoColor in the table and select it
							int index = this.getColorIndex(geoColor);
							setSelectedIndex(index);
							setDefaultColor(alpha, geoColor);

							this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
						}
					}
				}

			};

//			btnColor.addActionListener(this);
			btnColor.addPopupHandler(this);
		}
		
		private void createBgColorButton() {

			final GDimensionW bgColorIconSize = new GDimensionW(20, iconHeight);

			btnBgColor = new ColorPopupMenuButton(app, bgColorIconSize,
					ColorPopupMenuButton.COLORSET_BGCOLOR, false) {

				@Override
				public void update(Object[] geos) {

					boolean geosOK = (geos.length > 0);
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i])
								.getGeoElementForPropertiesDialog();
						if (!(geo instanceof GeoText)
								&& !(geo instanceof GeoButton)) {
							geosOK = false;
							break;
						}
					}

					setVisible(geosOK);

					if (geosOK) {
						// get color from first geo
						geogebra.common.awt.GColor geoColor;
						geoColor = ((GeoElement) geos[0]).getBackgroundColor();

						/*
						 * // check if selection contains a fillable geo // if true,
						 * then set slider to first fillable's alpha value float
						 * alpha = 1.0f; boolean hasFillable = false; for (int i =
						 * 0; i < geos.length; i++) { if (((GeoElement)
						 * geos[i]).isFillable()) { hasFillable = true; alpha =
						 * ((GeoElement) geos[i]).getAlphaValue(); break; } }
						 * getMySlider().setVisible(hasFillable);
						 * setSliderValue(Math.round(alpha * 100));
						 */
						float alpha = 1.0f;
						updateColorTable();

						// find the geoColor in the table and select it
						int index = getColorIndex(geoColor);
						setSelectedIndex(index);
						setDefaultColor(alpha, geoColor);

						// if nothing was selected, set the icon to show the
						// non-standard color
						if (index == -1) {
							this.setIcon(GeoGebraIcon.createColorSwatchIcon(alpha,
									bgColorIconSize,
									geoColor, null));
						}
					}
				}
			};
			btnBgColor.setKeepVisible(true);
//			btnBgColor.addActionListener(this);
			btnBgColor.addPopupHandler(this);
		}
	
	private void createTextButtons() {
		// ========================
		// text color button
		final GDimensionW textColorIconSize = new GDimensionW(24, iconHeight);

		btnTextColor = new ColorPopupMenuButton(app, textColorIconSize,
				ColorPopupMenuButton.COLORSET_DEFAULT, false) {

			private geogebra.common.awt.GColor geoColor;

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					geoColor =	geo
							.getObjectColor();
					updateColorTable();

					// find the geoColor in the table and select it
					int index = this.getColorIndex(geoColor);
					setSelectedIndex(index);

					// if nothing was selected, set the icon to show the
					// non-standard color
					if (index == -1) {
						this.setIcon(getButtonIcon());
					}

					setFgColor((GColorW) geoColor);
					setFontStyle(((TextProperties) geo).getFontStyle());
				}
			}

			@Override
			public ImageOrText getButtonIcon() {
				return GeoGebraIcon.createTextSymbolIcon("A",
						(GFontW) app.getPlainFontCommon(), textColorIconSize,
						getSelectedColor(),
						null);
			}

		};
		btnTextColor.addStyleName("btnTextColor");
//		btnTextColor.addActionListener(this);
		btnTextColor.addPopupHandler(this);


		// ========================================
		// bold text button
		//ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold")
		//		.substring(0, 1), app.getPlainFont(), true, false, true,
		//		iconDimension, Color.black, null);
		btnBold = new MyToggleButton2(app.getPlain("Bold").substring(0, 1), iconHeight) {
				//AppResources.INSTANCE.format_text_bold(), iconHeight) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnBold.setValue((style & geogebra.common.awt.GFont.BOLD) != 0);
				}
			}
		};
		btnBold.addStyleName("btnBold");
		btnBold.addValueChangeHandler(this);

		// ========================================
		// italic text button
		//ImageIcon italicIcon = GeoGebraIcon.createStringIcon(
		//		app.getPlain("Italic").substring(0, 1), app.getPlainFont(),
		//		false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButton2(app.getPlain("Italic").substring(0, 1), iconHeight) {
				//AppResources.INSTANCE.format_text_italic(), iconHeight) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);
				this.setVisible(geosOK);
				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					int style = ((TextProperties) geo).getFontStyle();
					btnItalic.setValue((style & geogebra.common.awt.GFont.ITALIC) != 0);
				}
			}

		};
		btnItalic.addStyleName("btnItalic");
		btnItalic.addValueChangeHandler(this);
		
		
		// ========================================
		// text size button

		ImageOrText[] textSizeArray = ImageOrText.convert(app.getLocalization().getFontSizeStrings());

		btnTextSize = new PopupMenuButton(app, textSizeArray, -1, 1,
				new GDimensionW(-1, iconHeight), geogebra.common.gui.util.SelectionTable.MODE_TEXT) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = checkGeoText(geos);
				setVisible(geosOK);

				if (geosOK) {
					GeoElement geo = ((GeoElement) geos[0])
							.getGeoElementForPropertiesDialog();
					setSelectedIndex(GeoText
							.getFontSizeIndex(((TextProperties) geo)
									.getFontSizeMultiplier())); // font size ranges from
														// -4 to 4, transform
														// this to 0,1,..,4
				}
			}
		};
//		btnTextSize.addActionListener(this);
		btnTextSize.addPopupHandler(this);
		btnTextSize.setKeepVisible(false);
		btnTextSize.setIcon(GeoGebraIcon.createResourceImageIcon(app, StyleBarResources.INSTANCE.font_size(), 1, null));

	}
	

	// =====================================================
	// Event Handlers
	// =====================================================

	public void updateGUI() {

		if (isIniting)
			return;
		
		btnPointCapture.removeActionListener(this);
		updateButtonPointCapture(ev.getPointCapturingMode());
//		btnPointCapture.addActionListener(this);

		btnMode.removeActionListener(this);
		switch (mode) {
		case EuclidianConstants.MODE_MOVE:
			btnMode.setSelectedIndex(0);
			break;
		case EuclidianConstants.MODE_PEN:
		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			btnMode.setSelectedIndex(1);
			break;
		case EuclidianConstants.MODE_DELETE:
			btnMode.setSelectedIndex(2);
			break;
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			btnMode.setSelectedIndex(3);
			break;
		}
//		btnMode.addActionListener(this);

		//btnPen.removeActionListener(this);
		//btnPen.setSelected(mode == EuclidianConstants.MODE_PEN);
		//btnPen.addActionListener(this);

		btnDelete.removeValueChangeHandler();
		btnDelete.setSelected(mode == EuclidianConstants.MODE_DELETE);
		btnDelete.addValueChangeHandler(this);

		btnLabel.removeValueChangeHandler();
		btnLabel.setSelected(mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		btnLabel.addValueChangeHandler(this);

		updateAxesAndGridGUI();

		btnStandardView.removeValueChangeHandler();
		btnStandardView.setSelected(false);
		btnStandardView.addValueChangeHandler(this);
	}

	protected void updateAxesAndGridGUI(){
		btnShowAxes.removeValueChangeHandler();
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addValueChangeHandler(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelectedIndex(gridIndex(ev));
//		btnShowGrid.addActionListener(this);

	}


	public void onValueChange(ValueChangeEvent event) {
			Object source = event.getSource();

			handleEventHandlers(source);
	}

	private void handleEventHandlers(Object source) {
	    needUndo = false;

	    ArrayList<GeoElement> targetGeos = new ArrayList<GeoElement>();
	    targetGeos.addAll(ec.getJustCreatedGeos());
	    if (mode != EuclidianConstants.MODE_MOVE){
	    	targetGeos.addAll(defaultGeos);
			Previewable p = ev.getPreviewDrawable();
			if (p!=null){
				GeoElement geo = p.getGeoElement();
				if (geo!=null){
					targetGeos.add(geo);
				}
			}
	    }else
	    	targetGeos.addAll(app.getSelectionManager().getSelectedGeos());

	    processSource(source, targetGeos);

	    if (needUndo) {
	    	app.storeUndoInfo();
	    	needUndo = false;
	    }

    }

	static boolean checkGeoText(Object[] geos) {
		boolean geosOK = (geos.length > 0);
		for (int i = 0; i < geos.length; i++) {
			if (!(((GeoElement) geos[i]).getGeoElementForPropertiesDialog() instanceof TextProperties)) {
				geosOK = false;
				break;
			}
		}
		return geosOK;
	}
	
	
	protected boolean processSourceForAxesAndGrid(Object source){
		if (source == btnShowGrid) {
			if (btnShowGrid.getSelectedValue() != null) {
				setGridType(ev, btnShowGrid.getSelectedIndex());

			}
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * process the action performed
	 * 
	 * @param source
	 * @param targetGeos
	 */
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos) {

		if ((source instanceof Widget)
				&& (EuclidianStyleBarStatic.processSourceCommon(
						getActionCommand((Widget) source), targetGeos, ev)))
			return;

		if (source == btnColor) {
//			if (mode == EuclidianConstants.MODE_PEN) {
//				ec.getPen().setPenColor((btnColor.getSelectedColor()));
//				btnColor.setDefaultColor(1, btnColor.getSelectedColor());
//				btnColor.updateColorTable();

				// App.debug("Not MODE_PEN in EuclidianStyleBar yet");
				/*ec.getPen().setPenColor(
						geogebra.awt.Color.getAwtColor(btnColor
								.getSelectedColor()));
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());*/
//			} else {
				GColor color = btnColor.getSelectedColor();
				float alpha = btnColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyColor(targetGeos, color, alpha, app);
				// btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
				// btnPointStyle.setFgColor((Color)btnColor.getSelectedValue());
//			}
		} else if (source == btnBgColor) {
			if (btnBgColor.getSelectedIndex() >= 0) {
				GColor color = btnBgColor.getSelectedColor();
				float alpha = btnBgColor.getSliderValue() / 100.0f;
				needUndo = EuclidianStyleBarStatic.applyBgColor(targetGeos, color, alpha);
			}
		}

		else if (source == btnTextColor) {
			if (btnTextColor.getSelectedIndex() >= 0) {
				GColor color = btnTextColor.getSelectedColor();
				needUndo = EuclidianStyleBarStatic.applyTextColor(targetGeos, color);
				// btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				// btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				// btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		} else if (source == btnLineStyle) {
			if (btnLineStyle.getSelectedValue() != null) {
				if (EuclidianView.isPenMode(mode)) {
					/*ec.getPen().setPenLineStyle(
							lineStyleArray[btnLineStyle.getSelectedIndex()]);
					ec.getPen().setPenSize(btnLineStyle.getSliderValue());*/
					// App.debug("Not MODE_PEN in EuclidianStyleBar yet");
				} else {
					// handled by the popup itself
			//		int lineSize = btnLineStyle.getSliderValue();
//					needUndo = EuclidianStyleBarStatic.applyLineStyle(targetGeos, selectedIndex, lineSize);
				}

			}
		}else if (processSourceForAxesAndGrid(source)) {
			// done in method
		} else if (source == btnPointStyle) {
			if (btnPointStyle.getSelectedValue() != null) {
				int pointStyleSelIndex = btnPointStyle.getSelectedIndex();
				int pointSize = btnPointStyle.getSliderValue();
				needUndo = EuclidianStyleBarStatic.applyPointStyle(targetGeos, pointStyleSelIndex, pointSize);
			}
		} else if (source == btnBold) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos, GFont.ITALIC, btnBold.isDown() ? GFont.BOLD : GFont.PLAIN );
		} else if (source == btnItalic) {
			needUndo = EuclidianStyleBarStatic.applyFontStyle(targetGeos, GFont.BOLD, btnItalic.isDown() ? GFont.ITALIC : GFont.PLAIN);
		} else if (source == btnTextSize) {
			needUndo = EuclidianStyleBarStatic.applyTextSize(targetGeos, btnTextSize.getSelectedIndex());
		} else if (source == btnHideShowLabel) {
			applyHideShowLabel(targetGeos);
			updateStyleBar();
		} else if (source == btnLabelStyle) {
			needUndo = EuclidianStyleBarStatic.applyCaptionStyle(targetGeos, mode, btnLabelStyle.getSelectedIndex());
		}

		else if (source == btnTableTextJustify || source == btnTableTextLinesH|| source == btnTableTextLinesV || source == btnTableTextBracket) {
			EuclidianStyleBarStatic.applyTableTextFormat(targetGeos, btnTableTextJustify.getSelectedIndex(), btnTableTextLinesH.isSelected(), btnTableTextLinesV.isSelected(), btnTableTextBracket.getSelectedIndex(), app);
		}

		else {
			for(int i=0;i < 3; i++){ 
				if(source == btnDeleteSizes[i]){
					setDelSize(i);
				}
			}
		}
	}
	
	public static void setGridType(EuclidianView ev, int val) {
		EuclidianSettings evs = ev.getSettings();
		boolean gridChanged = false;
	    if(val == 0){
	    	gridChanged = evs.showGrid(false);
	    }else{
	    	evs.beginBatch();
	    	gridChanged = evs.showGrid(true);
	    	switch(val){
	    	case 2:
	    	evs.setGridType(EuclidianView.GRID_POLAR);
		    	break ;
		    
	    	case 3:evs.setGridType(EuclidianView.GRID_ISOMETRIC);
		    	break;
		    default:evs.setGridType(EuclidianView.GRID_CARTESIAN);
	    	}
	    	evs.endBatch();
	    }
	    if(gridChanged){
	    	ev.getApplication().storeUndoInfo();
	    }
    }


	private void setDelSize(int s){
		ev.getSettings().setDeleteToolSize(EuclidianSettings.DELETE_SIZES[s]);
		for(int i =0; i<3;i++){
			btnDeleteSizes[i].setDown(i == s);
			btnDeleteSizes[i].setEnabled(i != s);
		}
	}
	// ==============================================
		// Apply Styles
		// ==============================================


		private void applyHideShowLabel(ArrayList<GeoElement> geos) {
			boolean visible = btnHideShowLabel.isSelected();
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				if (geo.isLabelVisible() != visible) {
					geo.setLabelVisible(visible);
					geo.updateRepaint();
					needUndo = true;
				}
			}
		}


//	public void onClick(ClickEvent event) {
//			Object source = event.getSource();
//			handleEventHandlers(source);
//    }

	/**
	 * @param actionButton runs programatically the action performed event.
	 */
	public void fireActionPerformed(PopupMenuButton actionButton) {
		handleEventHandlers(actionButton);
    }

	public int getPointCaptureSelectedIndex() {
		return btnPointCapture.getSelectedIndex();
	}

	protected void setActionCommand(Widget widget, String actionCommand){
		widget.getElement().setAttribute("actionCommand", actionCommand);
	}

	private String getActionCommand(Widget widget){
		return widget.getElement().getAttribute("actionCommand");
	}


	public static int gridIndex(EuclidianView ev) {
	    if(!ev.getShowGrid()){
	    	return 0;
	    }
	    if(ev.getGridType() == EuclidianView.GRID_POLAR){
	    	return 2;
	    }
	    if(ev.getGridType() == EuclidianView.GRID_ISOMETRIC){
	    	return 3;
	    }
	    return 1;
    }


	


	@Override
    public void hidePopups() {
		if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
			EuclidianStyleBarW.CURRENT_POP_UP.hide();
		}
    }
	
	private boolean firstPaint = true;
	
	public void resetFirstPaint(){
		firstPaint = true;
	}
	

	
	@Override
    public void onAttach(){
		
		if (firstPaint){
			firstPaint = false;
			updateGUI();		
		}
		
		super.onAttach();
	}

}
