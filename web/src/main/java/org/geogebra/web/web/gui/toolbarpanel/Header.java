package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.io.layout.PerspectiveDecoder;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.Persistable;
import org.geogebra.web.web.gui.layout.DockSplitPaneW;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.web.gui.toolbarpanel.ToolbarPanel.TabIds;
import org.geogebra.web.web.gui.util.PersistablePanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;


/**
 * header of toolbar
 *
 */
class Header extends FlowPanel implements KeyDownHandler, TabHandler {
	private PersistableToggleButton btnMenu;
	private MyToggleButton btnAlgebra;
	private MyToggleButton btnTools;
	private MyToggleButton btnClose;
	private boolean open = true;
	private Image imgClose;
	private Image imgOpen;
	private Image imgMenu;
	private FlowPanel contents;
	private FlowPanel center;
	private FlowPanel rightSide;
	/**
	 * panel containing undo and redo
	 */
	PersistablePanel undoRedoPanel;
	private MyToggleButton btnUndo;
	private MyToggleButton btnRedo;
	private boolean animating = false;
	private boolean lastOrientation;
	/**
	 * height in open state
	 */
	private static final int OPEN_HEIGHT = 56;
	/**
	 * application
	 */
	AppW app;
	/**
	 * Parent tool panel
	 */
	final ToolbarPanel toolbarPanel;
	private static final int PADDING = 12;

	private class PersistableToggleButton extends MyToggleButton
			implements Persistable {

		public PersistableToggleButton(Image image) {
			super(image, app);
		}

		@Override
		public void setTitle(String title) {
			AriaHelper.setTitle(this, title, app);
		}
	}

	
	/**
	 * @param toolbarPanel
	 *            - panel containing the toolbar
	 * @param app
	 *            - application
	 */
	public Header(ToolbarPanel toolbarPanel, AppW app) {
		this.app = app;
		this.toolbarPanel = toolbarPanel;
		contents = new FlowPanel();
		contents.addStyleName("contents");
		add(contents);
		if (app.getArticleElement().getDataParamShowMenuBar(false)) {
			createMenuButton();
		}
		createRightSide();
		createCenter();
		if (app.isUndoRedoEnabled()) {
			addUndoRedoButtons();
		}
		setLabels();
		ClickStartHandler.initDefaults(this, true, true);
		setTabIndexes();
		lastOrientation = app.isPortrait();
	}

	private void createCenter() {
		SVGResource img;
		if (!app.showToolBar() || !app.enableGraphing()) {
			return;
		}
		if (!app.isUnbundledGeometry()) {
			img = MaterialDesignResources.INSTANCE.toolbar_algebra_graphing();
		} else {
			img = MaterialDesignResources.INSTANCE.toolbar_algebra_geometry();
		}
		btnAlgebra = new MyToggleButton(new Image(new ImageResourcePrototype(
				null, img.getSafeUri(), 0, 0, 24, 24, false, false)), app);
		btnAlgebra.addStyleName("tabButton");
		ClickStartHandler.init(btnAlgebra, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onAlgebraPressed();
			}
		});


			createToolsButton();


		if (app.has(Feature.TAB_ON_GUI)) {
			if (btnAlgebra != null) {
				btnAlgebra.ignoreTab();
			}
			if (btnTools != null) {
				btnTools.ignoreTab();
			}
		}

		center = new FlowPanel();
		center.addStyleName("center");
		center.addStyleName("indicatorLeft");

		center.add(btnAlgebra);
		if (btnTools != null) {
			Element indicator = DOM.createDiv();
			indicator.addClassName("indicator");
			center.getElement().insertFirst(indicator);
			center.add(btnTools);
		}else{
			center.addStyleName("singleButton");
		}
		contents.add(center);
	}

	private void createToolsButton() {
		btnTools = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE
						.toolbar_tools().getSafeUri(),
				0, 0, 24, 24, false, false)),
				app);

		btnTools.addStyleName("tabButton");
		ClickStartHandler.init(btnTools,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
							PointerEventType type) {
						onToolsPressed();
					}
				});
		if (app.has(Feature.TAB_ON_GUI)) {
			btnAlgebra.addKeyDownHandler(this);
			btnTools.addKeyDownHandler(this);
		}
	}

	/**
	 * Handler for Algebra button.
	 */
	protected void onAlgebraPressed() {
		if (!open) {
			toolbarPanel.setFadeTabs(false);
		}
		toolbarPanel.openAlgebra(open);
		toolbarPanel.setMoveMode();
		app.setKeyboardNeeded(true);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(true);
	}

	/**
	 * Handler for button.
	 */
	protected void onToolsPressed() {
		if (!open) {
			toolbarPanel.setFadeTabs(false);
		}
		app.setKeyboardNeeded(false);
		toolbarPanel.getFrame().keyBoardNeeded(false, null);
		toolbarPanel.getFrame().showKeyboardButton(false);
		toolbarPanel.openTools(open);
	}

	/**
	 * Handler for Close button.
	 */
	protected void onClosePressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		if (isOpen()) {
			onClose();
		} else {
			onOpen();
		}
		toolbarPanel.getFrame().showKeyBoard(false, null, true);
	}
	
	private void onClose() {
		setAnimating(true);
		removeOrientationStyles();
		Widget headerParent = toolbarPanel.header.getParent().getParent()
				.getParent();
		if (app.isPortrait()) {
			headerParent.addStyleName("closePortrait");
		} else {
			headerParent.addStyleName("closeLandscape");
			toolbarPanel.setLastOpenWidth(getOffsetWidth());
		}
		toolbarPanel.setMoveMode();
		toolbarPanel.setClosedByUser(true);
		setOpen(false);
	}

	private void onOpen() {
		removeOrientationStyles();
		if (toolbarPanel.isAlgebraViewActive()) {
			onAlgebraPressed();
		} else {
			onToolsPressed();
		}
		setOpen(true);
		updateStyle();
		toolbarPanel.setClosedByUser(false);
	}

	private void removeOrientationStyles() {
		Widget headerParent = toolbarPanel.header.getParent().getParent()
				.getParent();
		headerParent.removeStyleName("closePortrait");
		headerParent.removeStyleName("closeLandscape");
	}

	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		toolbarPanel.app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		if (app.isMenuShowing()) {
			app.toggleMenu();
		}
		toolbarPanel.app.getGuiManager().redo();
	}

	/**
	 * set labels
	 */
	void setLabels() {
		setTitle(btnMenu, "Menu");
		setTitle(btnTools,"Tools");
		setTitle(btnAlgebra, app.getConfig().getAVTitle());
		setTitle(btnClose, isOpen() ? "Close" : "Open");
		setTitle(btnUndo, "Undo");
		setTitle(btnRedo, "Redo");

		setAltTexts();
	}

	private void setTitle(Widget btn, String avTitle) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(avTitle));
		}
	}

	private void setAltTexts() {
		if (!app.has(Feature.TAB_ON_GUI)) {
			return;
		}

		imgMenu.setAltText(app.getLocalization().getMenu("Menu"));
		setAltText(btnAlgebra, app.getConfig().getAVTitle());
		setAltText(btnTools, "Tools");
		setAltText(btnUndo, "Undo");
		setAltText(btnRedo, "Redo");
	}

	private void setAltText(MyToggleButton btn, String string) {
		if (btn != null) {
			btn.setAltText(app.getLocalization().getMenu(string));
		}
	}

	/**
	 * Switch to algebra panel
	 */
	void selectAlgebra() {
		if (center == null) {
			return;
		}
		center.removeStyleName("indicatorRight");
		center.addStyleName("indicatorLeft");
		btnAlgebra.addStyleName("selected");
		btnTools.removeStyleName("selected");

		toolbarPanel.setSelectedTabId(TabIds.ALGEBRA);
	}

	/**
	 * Switch to tools panel
	 */
	void selectTools() {
		if (center == null) {
			return;
		}
		center.removeStyleName("indicatorLeft");
		center.addStyleName("indicatorRight");
		btnAlgebra.removeStyleName("selected");
		btnTools.addStyleName("selected");

		toolbarPanel.setSelectedTabId(TabIds.TOOLS);
	}

	private void createRightSide() {
		imgClose = new Image();
		imgOpen = new Image();
		imgMenu = new Image();
		updateButtonImages();
		btnClose = new MyToggleButton(app);
		btnClose.addStyleName("flatButton");
		btnClose.addStyleName("close");

		ClickStartHandler.init(btnClose, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onClosePressed();
			}
		});
		
		if (app.has(Feature.TAB_ON_GUI)) {
			btnClose.addKeyDownHandler(this);
		}

		rightSide = new FlowPanel();
		rightSide.add(btnClose);
		rightSide.addStyleName("rightSide");
		contents.add(rightSide);
	}

	private void updateButtonImages() {
		if (app.isPortrait()) {
			setResource(imgOpen,
					MaterialDesignResources.INSTANCE
							.toolbar_open_portrait_white());
			setResource(imgClose,
					MaterialDesignResources.INSTANCE
							.toolbar_close_portrait_white());
			setResource(imgMenu,
					MaterialDesignResources.INSTANCE.menu_black_border());
		} else {
			setResource(imgOpen,
					MaterialDesignResources.INSTANCE
							.toolbar_open_landscape_white());
			setResource(imgClose,
					MaterialDesignResources.INSTANCE
							.toolbar_close_landscape_white());
			setResource(imgMenu,
					MaterialDesignResources.INSTANCE.toolbar_menu_white());
		}

		imgOpen.setAltText(app.getLocalization().getMenu("Open"));
		imgClose.setAltText(app.getLocalization().getMenu("Close"));
	}

	private static void setResource(Image img, SVGResource svg) {
		if (img != null) {
			img.setResource(new ImageResourcePrototype(
				null, svg.getSafeUri(),
				0, 0, 24, 24, false, false));
		}
	}

	/**
	 * @param expanded
	 *            whether menu is expanded
	 */
	public void markMenuAsExpanded(boolean expanded) {
		if (btnMenu != null) {
			btnMenu.getElement().setAttribute("aria-expanded",
					String.valueOf(expanded));
			btnMenu.getElement().removeAttribute("aria-pressed");
		}
	}

	private void createMenuButton() {
		ImageResource menuImgRec = new ImageResourcePrototype(null,
				MaterialDesignResources.INSTANCE.toolbar_menu_black()
						.getSafeUri(),
				0, 0, 24, 24, false, false);
		btnMenu = new PersistableToggleButton(new Image(menuImgRec));
		btnMenu.addStyleName("flatButton");
		btnMenu.addStyleName("menu");

		toolbarPanel.getFrame().add(btnMenu);
		markMenuAsExpanded(false);
		ClickStartHandler.init(btnMenu, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toolbarPanel.toggleMenu();
			}
		});
		if (app.has(Feature.TAB_ON_GUI)) {
			btnMenu.addTabHandler(this);
			btnMenu.addKeyDownHandler(this);
		}
	}

	private void addUndoRedoButtons() {
		undoRedoPanel = new PersistablePanel();
		undoRedoPanel.addStyleName("undoRedoPanel");
		addUndoButton(undoRedoPanel);
		addRedoButton(undoRedoPanel);
		toolbarPanel.getFrame().add(undoRedoPanel);
	}

	/**
	 * update position of undo+redo panel
	 */
	public void updateUndoRedoPosition() {
		final EuclidianView ev = toolbarPanel.app.getActiveEuclidianView();
		if (ev != null && undoRedoPanel != null) {
			double evTop = (ev.getAbsoluteTop() - (int) app.getAbsTop())
					/ app.getArticleElement().getScaleY();
			double evLeft = (ev.getAbsoluteLeft() - (int) app.getAbsLeft())
					/ app.getArticleElement().getScaleX();
			if ((evLeft <= 0) && !app.isPortrait()) {
				return;
			}
			int move = app.isPortrait() && app.showMenuBar() ? 48 : 0;
			undoRedoPanel.getElement().getStyle().setTop(evTop, Unit.PX);
			undoRedoPanel.getElement().getStyle().setLeft(evLeft + move,
					Unit.PX);
		}
	}

	/**
	 * Show the undo/redo panel.
	 */
	public void showUndoRedoPanel() {
		if (undoRedoPanel != null) {
			undoRedoPanel.removeStyleName("hidden");
		}
	}


	/**
	 * Hide the entire undo/redo panel (eg. during animation).
	 */
	public void hideUndoRedoPanel() {
		if (undoRedoPanel != null) {
			undoRedoPanel.addStyleName("hidden");
		}
	}

	/**
	 * Show buttons (tabs, close) of the header.
	 */
	public void showButtons() {
		center.removeStyleName("hidden");
		rightSide.removeStyleName("hidden");
	}

	/**
	 * Hide buttons (eg. during animation).
	 */
	public void hideButons() {
		center.addStyleName("hidden");
		rightSide.addStyleName("hidden");
	}

	/**
	 * update style of undo+redo buttons
	 */
	public void updateUndoRedoActions() {
		if (undoRedoPanel == null) {
			if (app.isUndoRedoEnabled()) {
				addUndoRedoButtons();
			} else {
				return;
			}
		}
		if (toolbarPanel.app.getKernel().undoPossible()) {
			btnUndo.addStyleName("buttonActive");
			btnUndo.removeStyleName("buttonInactive");
		} else {
			btnUndo.removeStyleName("buttonActive");
			btnUndo.addStyleName("buttonInactive");
		}

		if (toolbarPanel.app.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			btnRedo.addStyleName("hideButton");
		}
	}

	private void addUndoButton(final FlowPanel panel) {
		btnUndo = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.undo_border()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
				app);
		btnUndo.setTitle(app.getLocalization().getMenu("Undo"));
		btnUndo.addStyleName("flatButton");
		btnUndo.addStyleName("undo");

		ClickStartHandler.init(btnUndo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onUndoPressed();
			}
		});

		if (app.has(Feature.TAB_ON_GUI)) {
			btnUndo.addKeyDownHandler(this);
		}

		panel.add(btnUndo);
	}

	private void addRedoButton(final FlowPanel panel) {
		btnRedo = new MyToggleButton(
				new Image(new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.redo_border()
								.getSafeUri(),
						0, 0, 24, 24, false, false)),
				app);
		btnRedo.setTitle(app.getLocalization().getMenu("Redo"));
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
		btnRedo.addStyleName("redo");

		ClickStartHandler.init(btnRedo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onRedoPressed();
			}
		});

		if (app.has(Feature.TAB_ON_GUI)) {
			btnRedo.addKeyDownHandler(this);
		}

		panel.add(btnRedo);
	}
	
	/**
	 * @return - true if toolbar is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param value
	 *            - true if toolbar should be open
	 */
	public void setOpen(boolean value) {
		this.open = value;
		updateDraggerStyle(value);
		
		if (app.isPortrait()) {
			toolbarPanel.updateHeight();
		} else {
			toolbarPanel.updateWidth();
		}

		toolbarPanel.showKeyboardButtonDeferred(
				isOpen() && toolbarPanel.getSelectedTabId() != TabIds.TOOLS);
	}

	private void updateDraggerStyle(boolean close) {
		DockSplitPaneW dockParent = getDockParent();
		if (dockParent != null) {
			if (app.isPortrait() && !close) {
				dockParent.removeStyleName("hide-Dragger");
				dockParent.addStyleName("moveUpDragger");
			} else {
				dockParent.removeStyleName("moveUpDragger");
				dockParent.addStyleName("hide-Dragger");
			}
		}
	}

	private DockSplitPaneW getDockParent() {
		ToolbarDockPanelW dockPanel = toolbarPanel.getToolbarDockPanel();
		return dockPanel != null ? dockPanel.getParentSplitPane() : null;
	}

	private void removeOpenStyles() {
		removeStyleName("header-open-portrait");
		removeStyleName("header-open-landscape");
	}

	private void removeCloseStyles() {
		removeStyleName("header-close-portrait");
		removeStyleName("header-close-landscape");
	}

	/**
	 * update style of toolbar
	 */
	public void updateStyle() {
		if (isAnimating()) {
			return;
		}

		updateButtonImages();
		String orientation = app.isPortrait() ? "portrait" : "landscape";
		if (open) {
			removeCloseStyles();
			addStyleName("header-open-" + orientation);
			btnClose.getUpFace().setImage(imgClose);
			btnClose.setTitle(app.getLocalization().getMenu("Close"));
			if (!app.isPortrait()) {
				clearHeight();
				clearWidth();
			}
		} else {
			removeOpenStyles();
			addStyleName("header-close-" + orientation);
			btnClose.getUpFace().setImage(imgOpen);
			btnClose.setTitle(app.getLocalization().getMenu("Open"));

		}

		updateMenuButtonStyle();

		updateUndoRedoPosition();
		updateUndoRedoActions();
		toolbarPanel.updateStyle();
	}

	private void updateMenuButtonStyle() {
		if (btnMenu == null) {
			return;
		}
		if (open) {
			btnMenu.removeStyleName("landscapeMenuBtn");
		} else {
			if (!app.isPortrait()) {
				btnMenu.addStyleName("landscapeMenuBtn");
			} else {
				btnMenu.removeStyleName("landscapeMenuBtn");
			}
		}
		if (app.isPortrait()) {
			btnMenu.addStyleName("portraitMenuBtn");
		} else {
			btnMenu.removeStyleName("portraitMenuBtn");
		}
		btnMenu.getUpFace().setImage(imgMenu);
	}

	/**
	 * update center posiotion by resize
	 */
	void updateCenterSize() {
		int h = 0;
		if (open) {
			h = OPEN_HEIGHT;
		} else {
			h = getOffsetHeight() - getMenuButtonHeight()
					- btnClose.getOffsetHeight() - 2 * PADDING;
		}

		if (h > 0 && center != null) {
			center.setHeight(h + "px");
		}
	}

	private int getMenuButtonHeight() {
		return btnMenu == null ? 0 : btnMenu.getOffsetHeight();
	}

	/**
	 * handle resize of toolbar
	 */
	public void resize() {
		if (isAnimating()) {
			return;
		}
		updateCenterSize();
		updateStyle();
	}

	/**
	 * @return true if animating
	 */
	public boolean isAnimating() {
		return animating;
	}

	/**
	 * @param b
	 *            - set if animating
	 */
	public void setAnimating(boolean b) {
		this.animating = b;
	}

	/**
	 * Shrinks header width by dx.
	 * 
	 * @param dx
	 *            the step of shinking.
	 */
	public void expandWidth(double dx) {
		getElement().getStyle().setWidth(dx, Unit.PX);
	}

	/**
	 * Resets toolbar.
	 */
	public void reset() {
		resize();
		updateUndoRedoPosition();
		getElement().getStyle().setHeight(OPEN_HEIGHT, Unit.PX);
	}

	/**
	 * Called when app changes orientation.
	 */
	public void onOrientationChange() {
		if (lastOrientation != app.isPortrait()) {
			removeOpenStyles();
			removeCloseStyles();
		} else if (open) {
			removeCloseStyles();
		} else {
			removeOpenStyles();
		}
		updateStyle();

		lastOrientation = app.isPortrait();

		if (app.isPortrait()) {
			clearWidth();
			clearHeight();
			updateStyle();
		} else {
			if (!isOpen()) {
				int width = app.getArticleElement().getDataParamWidth();
				if (app.getArticleElement().getDataParamFitToScreen()) {
					width = Window.getClientWidth();
				}
				toolbarPanel.setLastOpenWidth((int) (width
						* PerspectiveDecoder.landscapeRatio(app, width)));
			}
		}
	}

	private void clearWidth() {
		getElement().getStyle().clearWidth();
	}

	private void clearHeight() {
		getElement().getStyle().clearHeight();
	}

	/**
	 * Sets tab order for header buttons.
	 */
	public void setTabIndexes() {
		tabIndex(btnMenu, GUITabs.MENU);
		tabIndex(btnClose, GUITabs.HEADER_CLOSE);
		tabIndex(btnUndo, GUITabs.UNDO);
		tabIndex(btnRedo, GUITabs.REDO);

		setAltTexts();
	}

	private static void tabIndex(CustomButton btn, int index) {
		if (btn != null) {
			btn.setTabIndex(index);
		}

	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		if (key != GWTKeycodes.KEY_ENTER && key != GWTKeycodes.KEY_SPACE) {
			return;
		}
		Object source = event.getSource();
		if (source == null) {
			return;
		}
		if (source == btnMenu) {
			toolbarPanel.toggleMenu();
		} else if (source == btnAlgebra) {
			onAlgebraPressed();
		} else if (source == btnTools) {
			onToolsPressed();
		} else if (source == btnClose) {
			onClosePressed();
		} else if (source == btnUndo) {
			onUndoPressed();
		} else if (source == btnRedo) {
			onRedoPressed();
		}
	}

	/** Sets focus to Burger menu */
	public void focusMenu() {
		if (btnMenu != null) {
			btnMenu.getElement().focus();
		}
	}

	/**
	 * @param expandFrom
	 *            collapsed width
	 * @param expandTo
	 *            expanded width
	 */
	public void onLandscapeAnimationEnd(double expandFrom, double expandTo) {
		if (!isOpen()) {
			expandWidth(expandFrom);
			setHeight("100%");
			toolbarPanel.updateUndoRedoPosition();
		} else {
			expandWidth(expandTo);
			toolbarPanel.onOpen();
		}
		if (getDockParent() != null) {
			getDockParent().onResize();
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				updateCenterSize();
				showUndoRedoPanel();
				updateUndoRedoPosition();
				showButtons();
				resize();
			}
		});
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		if (source == btnMenu && shiftDown) {
			app.getAccessibilityManager().focusPrevious(btnMenu);
			return true;
		}
		return false;
	}
}