package geogebra.web.gui.app;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.browser.BrowseGUI;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.toolbar.images.MyIconResourceBundle;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GGWToolBar extends Composite {

	static private MyIconResourceBundle myIconResourceBundle = GWT
	        .create(MyIconResourceBundle.class);

	static public MyIconResourceBundle getMyIconResourceBundle() {
		return myIconResourceBundle;
	}

	private ArrayList<ToolBarW> toolbars;
	AppW app;
	private ToolBarW toolBar;
	//panel which contains the toolbar and undo-redo buttons.
	FlowPanel toolBarPanel;
	//panel for toolbar (without undo-redo buttons)
	FlowPanel toolBPanel;
	boolean inited = false;
	private Integer activeToolbar = -1;
	
	private FlowPanel rightButtonPanel;
	private StandardButton openSearchButton, openMenuButton;

	/**
	 * Create a new GGWToolBar object
	 */
	public GGWToolBar() {
		toolBarPanel = new FlowPanel();
		toolBarPanel.addStyleName("ggbtoolbarpanel");
		//this makes it draggable on SMART board
		toolBarPanel.addStyleName("smart-nb-draggable");
		initWidget(toolBarPanel);
	}

	public boolean isInited() {
		return inited;
	}
	
	public void setActiveToolbar(Integer viewID){
		if (activeToolbar == viewID){
			return;
		}
		activeToolbar = viewID;
		for(ToolBarW bar:toolbars){
			bar.setActiveView(viewID);
		}
	}

	/**
	 * Initialization of the GGWToolbar.
	 * 
	 * @param app1 application
	 */
	public void init(AppW app1) {

		this.inited = true;
		this.app = app1;
		toolbars = new ArrayList<ToolBarW>();
		toolBar = new ToolBarW(this);
		toolBPanel = new FlowPanel();
		toolBarPanel.add(toolBar);
		toolBarPanel.add(toolBPanel);
		toolBarPanel.addStyleName("toolbarPanel");
		toolBPanel.setStyleName("toolBPanel");
		
		//toolBarPanel.setSize("100%", "100%");
		toolBar.init(app1);
		addToolbar(toolBar);
		
		
		//Adds the Open and Options Button for SMART
		
		addRightButtonPanel();
		
	}

//	/**
//	 * Build the toolbar GUI
//	 */
//	public void buildGui() {
//
//	}

	//undo-redo buttons
	private void addUndoPanel(){

		Image redoImage = new Image(AppResources.INSTANCE.edit_redo());
		Button redoButton = new Button();
		redoButton.getElement().appendChild(redoImage.getElement());
		redoButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				app.getGuiManager().redo();
            }
		});
		redoButton.setStyleName("redoButton");
		redoButton.getElement().addClassName("button");
		redoButton.setTitle("Redo");
	
		Image undoImage = new Image(AppResources.INSTANCE.edit_undo());
		Button undoButton = new Button();
		undoButton.getElement().appendChild(undoImage.getElement());
		undoButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				app.getGuiManager().undo();
            }
		});
		undoButton.setStyleName("undoButton");
		undoButton.getElement().addClassName("button");
		undoButton.setTitle("Undo");
		//toolBarPanel.add(redoButton);
		
		
		rightButtonPanel.add(undoButton);
		rightButtonPanel.add(redoButton);
			
	}
	
	//Undo, redo, open, menu
	private void addRightButtonPanel(){
		this.rightButtonPanel = new FlowPanel();
		this.rightButtonPanel.setStyleName("smartButtonPanel");
		if(app.getLAF().undoRedoSupported()){
			addUndoPanel();
		}
		if(app.getArticleElement().getDataParamShowMenuBar() || 
				app.getArticleElement().getDataParamApp()){
		openMenuButton = new StandardButton(GuiResources.INSTANCE.button_open_menu());
		openMenuButton.addFastClickHandler(new FastClickHandler() {
			@Override
            public void onClick() {
	            GGWToolBar.this.app.toggleMenu();
            }	
		});
		openMenuButton.addDomHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
	            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
	            	GGWToolBar.this.app.toggleMenu();
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT){
	            	GGWToolBar.this.selectMenuButton(0);
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT){
	            	GGWToolBar.this.toolBar.selectMenu(0);
	            }
            }
		}, KeyUpEvent.getType());
		
		openSearchButton = new StandardButton(GuiResources.INSTANCE.button_open_search());
		openSearchButton.addFastClickHandler(new FastClickHandler() {
			@Override
            public void onClick() {
				// TODO: Zbynek please check if this is ok - Steffi
				BrowseGUI bg = new BrowseGUI(app);
				app.showBrowser(bg);
            }
		});
		
		openSearchButton.addDomHandler(new KeyUpHandler(){
			public void onKeyUp(KeyUpEvent event) {
	            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
	            	BrowseGUI bg = new BrowseGUI(app);
					app.showBrowser(bg);
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT){
	            	GGWToolBar.this.selectMenuButton(1);
	            }
	            if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT){
	            	GGWToolBar.this.toolBar.selectMenu(-1);
	            }
            }
		}, KeyUpEvent.getType());
		
		this.rightButtonPanel.add(openSearchButton);
		this.rightButtonPanel.add(openMenuButton);
		}
		toolBarPanel.add(rightButtonPanel);	
	}
	
	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
//		toolBarPanel.clear();
		toolBPanel.clear();
		for(Widget toolbar : toolbars) {
			if(toolbar != null) {
				((ToolBarW)toolbar).buildGui();
				//TODO
				//toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
				toolBPanel.add(toolbar);
			}
		}
		
		//TODO
		//toolbarPanel.show(Integer.toString(activeToolbar));

		toolBPanel.setVisible(true);
	}

	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the
	 * GUI after all toolbar changes were made.
	 * 
	 * @param toolbar
	 */
	public void addToolbar(ToolBarW toolbar) {
		toolbars.add(toolbar);
	}

	/**
	 * Removes a toolbar from this container. Use {@link #updateToolbarPanel()}
	 * to update the GUI after all toolbar changes were made. If the removed
	 * toolbar was the active toolbar as well the active toolbar is changed to
	 * the general (but again, {@link #updateToolbarPanel()} has to be called
	 * for a visible effect).
	 * 
	 * @param toolbar
	 */
	public void removeToolbar(ToolBarW toolbar) {
		toolbars.remove(toolbar);
		/*AGif(getViewId(toolbar) == activeToolbar) {
			activeToolbar = -1;
		}*/
	}

	/**
	 * Gets an HTML fragment that displays the image belonging to mode
	 * given in parameter
	 * 
	 * @param mode
	 * @return HTML fragment
	 */
	public String getImageHtml(int mode){
		String url = getImageURL(mode);
		return (url.length()>0) ? "<img src=\""+url+"\">" : "";
	}
	
	
	public String getImageURL(int mode) {
		

//		String modeText = app.getKernel().getModeText(mode);
//		// bugfix for Turkish locale added Locale.US
//		String iconName = "mode_" +StringUtil.toLowerCase(modeText)
//				+ "_32";
//

		// macro
		if (mode >= EuclidianConstants.MACRO_MODE_ID_OFFSET) {
			int macroID = mode - EuclidianConstants.MACRO_MODE_ID_OFFSET;
			try {
				Macro macro = app.getKernel().getMacro(macroID);
				String iconName = macro.getIconFileName();
				if (iconName == null || iconName.length()==0) {
					// default icon
					return myIconResourceBundle.mode_tool_32().getSafeUri().asString();
				}
				// use image as icon
				Image img = new Image(app.getImageManager().getExternalImageSrc(iconName));
				return img.getUrl();
			} catch (Exception e) {
				App.debug("macro does not exist: ID = " + macroID);
				return "";
			}
		}
		
		
		switch (mode) {

		case EuclidianConstants.MODE_ANGLE:
			return myIconResourceBundle.mode_angle_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ANGLE_FIXED:
			return myIconResourceBundle.mode_anglefixed_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ANGULAR_BISECTOR:
			return myIconResourceBundle.mode_angularbisector_32().getSafeUri().asString();

		case EuclidianConstants.MODE_AREA:
			return myIconResourceBundle.mode_area_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ATTACH_DETACH:
			return myIconResourceBundle.mode_attachdetachpoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_BUTTON_ACTION:
			return myIconResourceBundle.mode_buttonaction_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_TWO_POINTS:
			return myIconResourceBundle.mode_circle2_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			return myIconResourceBundle.mode_circle3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circlearc3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS:
			return myIconResourceBundle.mode_circlepointradius_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circlesector3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclearc3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			return myIconResourceBundle.mode_circumcirclesector3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_COMPASSES:
			return myIconResourceBundle.mode_compasses_32().getSafeUri().asString();

		case EuclidianConstants.MODE_COMPLEX_NUMBER:
			return myIconResourceBundle.mode_complexnumber_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CONIC_FIVE_POINTS:
			return myIconResourceBundle.mode_conic5_32().getSafeUri().asString();

		case EuclidianConstants.MODE_COPY_VISUAL_STYLE:
			return myIconResourceBundle.mode_copyvisualstyle_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_COUNT:
			return myIconResourceBundle.mode_countcells_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LIST:
			return myIconResourceBundle.mode_createlist_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CREATE_LIST:
			return myIconResourceBundle.mode_createlistgraphicsview_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_LISTOFPOINTS:
			return myIconResourceBundle.mode_createlistofpoints_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_MATRIX:
			return myIconResourceBundle.mode_creatematrix_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_POLYLINE:
			return myIconResourceBundle.mode_createpolyline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_CREATE_TABLETEXT:
			return myIconResourceBundle.mode_createtable_32().getSafeUri().asString();

		case EuclidianConstants.MODE_DELETE:
			return myIconResourceBundle.mode_delete_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_DERIVATIVE:
			return myIconResourceBundle.mode_derivative_32().getSafeUri().asString();

		case EuclidianConstants.MODE_DILATE_FROM_POINT:
			return myIconResourceBundle.mode_dilatefrompoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_DISTANCE:
			return myIconResourceBundle.mode_distance_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ELLIPSE_THREE_POINTS:
			return myIconResourceBundle.mode_ellipse3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_EVALUATE:
			return myIconResourceBundle.mode_evaluate_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_EXPAND:
			return myIconResourceBundle.mode_expand_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_FACTOR:
			return myIconResourceBundle.mode_factor_32().getSafeUri().asString();

		case EuclidianConstants.MODE_FITLINE:
			return myIconResourceBundle.mode_fitline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_FREEHAND_SHAPE:
			return myIconResourceBundle.mode_freehandshape_32().getSafeUri().asString();

		case EuclidianConstants.MODE_FUNCTION_INSPECTOR:
			return myIconResourceBundle.mode_functioninspector_32().getSafeUri().asString();

		case EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS:
			return myIconResourceBundle.mode_hyperbola3_32().getSafeUri().asString();

		case EuclidianConstants.MODE_IMAGE:
			return myIconResourceBundle.mode_image_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_INTEGRAL:
			return myIconResourceBundle.mode_integral_32().getSafeUri().asString();

		case EuclidianConstants.MODE_INTERSECT:
			return myIconResourceBundle.mode_intersect_32().getSafeUri().asString();

		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			return myIconResourceBundle.mode_intersectioncurve_32().getSafeUri().asString();

		case EuclidianConstants.MODE_JOIN:
			return myIconResourceBundle.mode_join_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			return myIconResourceBundle.mode_keepinput_32().getSafeUri().asString();

		case EuclidianConstants.MODE_LINE_BISECTOR:
			return myIconResourceBundle.mode_linebisector_32().getSafeUri().asString();

		case EuclidianConstants.MODE_LOCUS:
			return myIconResourceBundle.mode_locus_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MAX:
			return myIconResourceBundle.mode_maxcells_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_AVERAGE:
			return myIconResourceBundle.mode_meancells_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIDPOINT:
			return myIconResourceBundle.mode_midpoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MIN:
			return myIconResourceBundle.mode_mincells_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_CIRCLE:
			return myIconResourceBundle.mode_mirroratcircle_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_LINE:
			return myIconResourceBundle.mode_mirroratline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MIRROR_AT_POINT:
			return myIconResourceBundle.mode_mirroratpoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MOVE:
			return myIconResourceBundle.mode_move_32().getSafeUri().asString();

		case EuclidianConstants.MODE_MOVE_ROTATE:
			return myIconResourceBundle.mode_moverotate_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_MULTIVARSTATS:
			return myIconResourceBundle.mode_multivarstats_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_CAS_NUMERIC:
			return myIconResourceBundle.mode_numeric_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_CAS_NUMERICAL_SOLVE:
			return myIconResourceBundle.mode_nsolve_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_ONEVARSTATS:
			return myIconResourceBundle.mode_onevarstats_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ORTHOGONAL:
			return myIconResourceBundle.mode_orthogonal_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PARABOLA:
			return myIconResourceBundle.mode_parabola_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PARALLEL:
			return myIconResourceBundle.mode_parallel_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PEN:
			return myIconResourceBundle.mode_pen_32().getSafeUri().asString();

		case EuclidianConstants.MODE_POINT:
			return myIconResourceBundle.mode_point_32().getSafeUri().asString();

		case EuclidianConstants.MODE_POINT_ON_OBJECT:
			return myIconResourceBundle.mode_pointonobject_32().getSafeUri().asString();

		case EuclidianConstants.MODE_POLAR_DIAMETER:
			return myIconResourceBundle.mode_polardiameter_32().getSafeUri().asString();

		case EuclidianConstants.MODE_POLYGON:
			return myIconResourceBundle.mode_polygon_32().getSafeUri().asString();

		case EuclidianConstants.MODE_POLYLINE:
			return myIconResourceBundle.mode_polyline_32().getSafeUri().asString();

		case EuclidianConstants.MODE_PROBABILITY_CALCULATOR:
			return myIconResourceBundle.mode_probabilitycalculator_32().getSafeUri().asString();

		case EuclidianConstants.MODE_RAY:
			return myIconResourceBundle.mode_ray_32().getSafeUri().asString();

		case EuclidianConstants.MODE_RECORD_TO_SPREADSHEET:
			return myIconResourceBundle.mode_recordtospreadsheet_32().getSafeUri().asString();

		case EuclidianConstants.MODE_REGULAR_POLYGON:
			return myIconResourceBundle.mode_regularpolygon_32().getSafeUri().asString();

		case EuclidianConstants.MODE_RELATION:
			return myIconResourceBundle.mode_relation_32().getSafeUri().asString();

		case EuclidianConstants.MODE_RIGID_POLYGON:
			return myIconResourceBundle.mode_rigidpolygon_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ROTATE_BY_ANGLE:
			return myIconResourceBundle.mode_rotatebyangle_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SEGMENT:
			return myIconResourceBundle.mode_segment_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SEGMENT_FIXED:
			return myIconResourceBundle.mode_segmentfixed_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SEMICIRCLE:
			return myIconResourceBundle.mode_semicircle_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX:
			return myIconResourceBundle.mode_showcheckbox_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			return myIconResourceBundle.mode_showhidelabel_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SHOW_HIDE_OBJECT:
			return myIconResourceBundle.mode_showhideobject_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SLIDER:
			return myIconResourceBundle.mode_slider_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SLOPE:
			return myIconResourceBundle.mode_slope_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_SOLVE:
			return myIconResourceBundle.mode_solve_32().getSafeUri().asString();

		case EuclidianConstants.MODE_CAS_SUBSTITUTE:
			return myIconResourceBundle.mode_substitute_32().getSafeUri().asString();

		case EuclidianConstants.MODE_SPREADSHEET_SUM:
			return myIconResourceBundle.mode_sumcells_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TANGENTS:
			return myIconResourceBundle.mode_tangent_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TEXT:
			return myIconResourceBundle.mode_text_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TEXTFIELD_ACTION:
			return myIconResourceBundle.mode_textfieldaction_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TRANSLATE_BY_VECTOR:
			return myIconResourceBundle.mode_translatebyvector_32().getSafeUri().asString();

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			return myIconResourceBundle.mode_translateview_32().getSafeUri().asString();
			
		case EuclidianConstants.MODE_SPREADSHEET_TWOVARSTATS:
			return myIconResourceBundle.mode_twovarstats_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VECTOR:
			return myIconResourceBundle.mode_vector_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VECTOR_FROM_POINT:
			return myIconResourceBundle.mode_vectorfrompoint_32().getSafeUri().asString();

		case EuclidianConstants.MODE_VECTOR_POLYGON:
			return myIconResourceBundle.mode_vectorpolygon_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ZOOM_IN:
			return myIconResourceBundle.mode_zoomin_32().getSafeUri().asString();

		case EuclidianConstants.MODE_ZOOM_OUT:
			return myIconResourceBundle.mode_zoomout_32().getSafeUri().asString();

		default:
			return "";
		}

	}
	
	/**
	 * @return tool bar
	 */
	public ToolBarW getToolBar(){
		return toolBar;
	}
	
	/**
	 * Select a mode.
	 * 
	 * @param mode
	 *            new mode
	 * @return -1 //mode that was actually selected
	 */
	public int setMode(int mode) {
//		int ret = -1;
//		for (ToolBarW toolbar : toolbars) {
//			int tmp = toolbar.setMode(mode);
//			App.debug("tmp: " + tmp);
//
//			// this will be the actual mode set
//			if (getViewId(toolbar) == activeToolbar) {
//				ret = tmp;
//			}
//		}
//
////		updateHelpText();
//
//		return ret;
		return toolbars.get(0).setMode(mode);
    }
	
	/**
	 * @param toolbar
	 * @return The ID of the dock panel associated with the passed toolbar or -1
	 */
	private static int getViewId(ToolBarW toolbar) {
		return (toolbar.getDockPanel() != null ? toolbar.getDockPanel()
				.getViewId() : -1);
	}
	
	protected void onAttach(){
		super.onAttach();
		// gwt sets openSearcButton's tabindex to 0 at onAttach (see
		// FocusWidget.onAttach())
		// but we don't want to select openSearchButton with tab, so tabindex will
		// be set back to -1 after attach all time.
		if(this.openSearchButton != null){
			this.openSearchButton.setTabIndex(-1);
		}
		if(this.openMenuButton != null){
			this.openMenuButton.setTabIndex(-1);
		}
	}

	public void selectMenuButton(int index) {
		deselectButtons();
		StandardButton focused = index == 0 ? this.openSearchButton : this.openMenuButton;
		if(focused != null){
			focused.setFocus(true);
			focused.getElement().addClassName("selectedButton");
		}
	    
    }

	public void deselectButtons() {
		this.openSearchButton.getElement().removeClassName("selectedButton");
		this.openMenuButton.getElement().removeClassName("selectedButton");
    }
}
