package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionSlider;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoTurtle;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.util.CSSAnimation;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.layout.GUITabs;
import org.geogebra.web.web.gui.view.algebra.AnimPanel.AnimPanelListener;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Item action bar
 *
 */
public class ItemControls extends FlowPanel
		implements AnimPanelListener, SetLabels {

	/**
	 * 
	 */
	final RadioTreeItem radioTreeItem;

	/** Deletes the whole item */
	protected PushButton btnDelete;

	/** opens context menu */
	protected MyToggleButton btnMore;

	/** animation controls */
	protected AnimPanel animPanel = null;

	private ContextMenuAVItemMore cmMore = null;
	private SuggestionBar suggestionBar;

	/**
	 * @param radioTreeItem
	 *            parent item
	 */
	public ItemControls(RadioTreeItem radioTreeItem) {
		this.radioTreeItem = radioTreeItem;
		addStyleName("AlgebraViewObjectStylebar");
		addStyleName("smallStylebar");
		if (radioTreeItem.getApplication().has(Feature.AV_ITEM_DESIGN)) {
			addStyleName("withContextMenu");
		}
		buildGUI();
		if (hasMoreMenu() && radioTreeItem.geo != null) {
			add(getMoreButton());
			btnMore.setTabIndex(GUITabs.NO_TAB);
		}
		getElement().setTabIndex(GUITabs.NO_TAB);
		setLabels();
	}

	/**
	 * Gets (and creates if there is not yet) the delete button which geo item
	 * can be removed with from AV.
	 * 
	 * @return The "X" button.
	 */
	public PushButton getDeleteButton() {
		if (btnDelete == null) {
			btnDelete = new PushButton(
					new Image(GuiResources.INSTANCE.algebra_delete()));
			btnDelete.getUpHoveringFace().setImage(
					new Image(GuiResources.INSTANCE.algebra_delete_hover()));
			btnDelete.addStyleName("XButton");
			btnDelete.addStyleName("shown");
			btnDelete.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
						return;
					}
					event.stopPropagation();
					getController().removeGeo();
				}
			});
		}
		return btnDelete;

	}

	/**
	 * 
	 * @return The more button which opens the context menu.
	 */
	public ToggleButton getMoreButton() {
		if (btnMore == null) {
			btnMore = new MyToggleButton(
					new Image(
					new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE.more_vert_black()
									.getSafeUri(),
									0, 0, 24, 24, false, false)),
					radioTreeItem.app);

			btnMore.ignoreTab();

			btnMore.getUpHoveringFace()
					.setImage(new Image(new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE.more_vert_purple()
									.getSafeUri(),
							0, 0, 24, 24, false, false)));
			btnMore.addStyleName("XButton");
			btnMore.addStyleName("shown");
			btnMore.addStyleName("more");
			ClickStartHandler.init(btnMore, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					showMoreContexMenu();
				}

			});
			AriaHelper.setHidden(btnMore, true);
		}
		return btnMore;

	}

	/**
	 * Show the More context menu
	 */
	protected void showMoreContexMenu() {
		if (radioTreeItem.getApplication().isUnbundled()
				&& radioTreeItem.getApplication().isMenuShowing()) {
			radioTreeItem.getApplication().toggleMenu();
		}
		if (cmMore == null) {
			cmMore = new ContextMenuAVItemMore(radioTreeItem);
		}
		radioTreeItem.cancelEditing();
		cmMore.show(btnMore.getAbsoluteLeft(), btnMore.getAbsoluteTop() - 8);

	}

	/**
	 * @param value
	 *            whether to show animation panel
	 */
	public void showAnimPanel(boolean value) {
		if (hasAnimPanel()) {
			animPanel.setVisible(value);
		}
	}

	private void buildGUI() {
		radioTreeItem.setFirst(radioTreeItem.first);
		clear();
		buildAnimPanel();

		if (!hasMoreMenu() && (radioTreeItem.app.isRightClickEnabled()
				|| radioTreeItem.app.showAlgebraInput())) {
			add(getDeleteButton());
		}
	}

	private void buildAnimPanel() {
		if (radioTreeItem.geo != null && radioTreeItem.geo.isAnimatable()
				&& animPanelFits(radioTreeItem.geo)) {
			if (animPanel == null) {
				createAnimPanel();
			}

			add(animPanel);
			reset();
			updateAnimPanel();
			showAnimPanel(true);
		} else {
			showAnimPanel(false);
		}

	}

	private boolean hasMoreMenu() {
		return radioTreeItem.app.has(Feature.AV_ITEM_DESIGN)
				&& radioTreeItem.app.showAlgebraInput();
	}

	/**
	 * 
	 */
	protected void createAnimPanel() {
		GeoElement geo = radioTreeItem.geo;
		if (geo.isAnimatable() && animPanelFits(geo)) {
			animPanel = new AnimPanel(radioTreeItem, this);
		} else {
			animPanel = null;
		}

	}

	/**
	 * @param geo
	 *            geo
	 * @return whether we have place for the animation panel: do not show it if
	 *         the user disabled AV slider for given number
	 */
	private static boolean animPanelFits(GeoElement geo) {
		if(geo instanceof GeoNumeric){
			return SliderTreeItemRetex.match(geo);
		}
		return geo.getKernel()
				.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
				&& !(geo instanceof GeoTurtle);

	}

	/**
	 * Update animation panel
	 */
	public void updateAnimPanel() {
		if (hasAnimPanel()) {
			animPanel.update();
		}
	}

	/**
	 * @return animation panel
	 */
	public AnimPanel getAnimPanel() {
		return animPanel;
	}

	/**
	 * @param showX
	 *            whether to show x button
	 * @return whether this is shown (when single geo selected)
	 */
	public boolean update(boolean showX) {
		radioTreeItem.setFirst(radioTreeItem.first);

		if (radioTreeItem.geo == null) {
			return false;
		}
		boolean ret = false;
		if (getController().selectionCtrl.isSingleGeo()
				|| getController().selectionCtrl.isEmpty()) {
			radioTreeItem.setFirst(radioTreeItem.first);
			clear();
			if (radioTreeItem.geo.isAnimatable()
					&& animPanelFits(radioTreeItem.geo)) {
				if (animPanel == null) {
					createAnimPanel();
				}

				add(animPanel);
			}

			updateSuggestions(radioTreeItem.geo);

			if (radioTreeItem.getPButton() != null) {
				add(radioTreeItem.getPButton());
			}
			if (showX) {
				add(hasMoreMenu() ? getMoreButton() : getDeleteButton());
			}

			setVisible(true);

			if (!getController().isEditing()) {
				radioTreeItem.maybeSetPButtonVisibility(false);
			}

			radioTreeItem.getAV().setActiveTreeItem(radioTreeItem);
			ret = true;
		} else {
			radioTreeItem.getAV().removeCloseButton();
		}

		updateAnimPanel();
		return ret;
	}

	/**
	 * Add or remove suggestion bar
	 * 
	 * @param geo
	 *            geo element (either from AV item or from preview)
	 */
	void updateSuggestions(GeoElement geo) {
		Suggestion sug = radioTreeItem.needsSuggestions(geo);
		boolean slider = sug instanceof SuggestionSlider;

		if ((sug != null && geo != null) || slider) {
			if (suggestionBar == null) {
				suggestionBar = new SuggestionBar(radioTreeItem);
			}
			if (!suggestionBar.getStyleName().contains("animating")) {
				suggestionBar.removeStyleName("removing");
				suggestionBar.addStyleName("animating");
			}
			// suggestionBar.addStyleName("add");
			// if (sug.hasMode()) {
			// suggestionBar.setSuggestion(sug, radioTreeItem.app);

			// } else {
				suggestionBar.setSuggestion(sug, radioTreeItem.loc);
			// }
			if (!suggestionBar.isAttached()) {
				add(suggestionBar);
			}
			if (sug instanceof SuggestionSolve) {
				radioTreeItem.getApplication().getKernel().getGeoGebraCAS()
					.initCurrentCAS();
			}
			radioTreeItem.toggleSuggestionStyle(true);
		} else if (suggestionBar != null) {
			radioTreeItem.toggleSuggestionStyle(false);
			suggestionBar.addStyleName("removing");
			suggestionBar.removeStyleName("animating");
			CSSAnimation.runOnAnimation(new Runnable() {

				@Override
				public void run() {
					removeSuggestions();
				}
			}, radioTreeItem.getContentElement(), "noSuggestions");

		}
	}

	/**
	 * Removes the suggestion bar
	 */
	protected void removeSuggestions() {
		remove(suggestionBar);

	}

	/**
	 * Remove animation panel
	 */
	public void removeAnimPanel() {
		if (hasAnimPanel()) {
			remove(animPanel);
		}
	}

	/**
	 * Reset animation panel
	 */
	public void reset() {
		if (hasAnimPanel()) {
			animPanel.reset();
		}
	}

	/**
	 * @return whether animation panel exists
	 */
	public boolean hasAnimPanel() {
		return animPanel != null;
	}

	@Override
	public void setVisible(boolean b) {
		if (getController().isEditing()) {
			return;
		}
		super.setVisible(b);
	}

	/**
	 * Called when item selected, shows the x button in edit mode
	 * 
	 * @param value
	 *            whether this is the only selected item
	 */
	public void show(boolean value) {
		if (radioTreeItem.app.has(Feature.AV_ITEM_DESIGN)) {
			super.setVisible(true);
			return;
		}

		boolean b = value || getController().isEditing();

		if (value && isVisible()) {
			return;
		}

		setVisible(b);

		if (value) {
			buildGUI();
		}
	}

	/**
	 * Update position
	 */
	public void reposition() {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				int right = 0;
				int itemWidth = radioTreeItem.getItemWidth();
				int avWidth = radioTreeItem.getAV().getOffsetWidth();
				if (avWidth < itemWidth) {
					right = itemWidth - avWidth;
				}
				getElement().getStyle().setRight(right, Unit.PX);
			}
		});
	}

	/**
	 * @return controller
	 */
	RadioTreeItemController getController() {
		return this.radioTreeItem.getController();
	}

	@Override
	public void onPlay(boolean show) {
		if (btnMore == null) {
			return;
		}
		if (show) {
			btnMore.removeStyleName("more");
			btnMore.addStyleName("more-hidden");
		} else {
			btnMore.removeStyleName("more-hidden");
			btnMore.addStyleName("more");
		}

	}

	@Override
	public void setLabels() {
		if (cmMore != null) {
			cmMore.setLabels();
		}
		if (btnMore != null) {
			btnMore.setAltText(this.radioTreeItem.loc.getMenu("Options"));
		}
		if (animPanel != null) {
			animPanel.setLabels(this.radioTreeItem.loc);
		}
	}
}