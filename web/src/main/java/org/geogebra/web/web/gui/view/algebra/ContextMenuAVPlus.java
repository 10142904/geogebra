package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.keyboard.web.TabbedKeyboard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.StyleBarResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Class for Plus menu for AV Input to select input method (expression, text or
 * image) and get help.
 * 
 * @author Laszlo Gal
 *
 */
public class ContextMenuAVPlus implements SetLabels {
	/** The popup itself */
	protected GPopupMenuW wrappedPopup;
	/** Localization */
	protected Localization loc;
	/** Application */
	AppW app;
	/** The AV item associated the menu with */
	RadioTreeItem item;
	/** On-Screen Keyboard instance to switch tabs if needed */
	TabbedKeyboard kbd;

	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuAVPlus(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		kbd = (TabbedKeyboard)((GuiManagerW)app.getGuiManager()).getOnScreenKeyboard(item, null);
		wrappedPopup = new GPopupMenuW(app);
		if (app.isUnbundled()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		buildGUI();
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addExpressionItem();
		if (!app.getSettings().getToolbarSettings().is3D()) {
			addTextItem();
			addImageItem();
		}
		addHelpItem();
	}

	private void addExpressionItem() {
		String img = StyleBarResources.INSTANCE.description().getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Expression"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(false);
						item.ensureEditing();
						kbd.selectNumbers();
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addTextItem() {
		String img = MaterialDesignResources.INSTANCE.icon_quote_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Text"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.getController().setInputAsText(true);
						item.ensureEditing();
						kbd.selectAbc();
					}
				});
		wrappedPopup.addItem(mi);
	}
	
	private void addImageItem() {
		String img = MaterialDesignResources.INSTANCE.insert_photo_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Image"), true), true,
				new Command() {
					
					@Override
					public void execute() {

						item.getController().setInputAsText(false);
						app.getImageManager().setPreventAuxImage(true);
						
						((GuiManagerW)app.getGuiManager()).loadImage(null, null, false, app.getActiveEuclidianView());
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addHelpItem() {
		String img = MaterialDesignResources.INSTANCE.icon_help_black()
				.getSafeUri()
		.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img, loc.getMenu("Help"), true),
				true, new Command() {
					
					@Override
					public void execute() {
						showHelp();
					}
				});
		wrappedPopup.addItem(mi);
	}

	/**
	 * Show popup menu at a given point.
	 * 
	 * @param p
	 *            point to show the menu at.
	 */
	public void show(GPoint p) {
		wrappedPopup.show(p);
		focusDeferred();
	}

	/**
	 * Show popup menu at (x, y) screen coordinates.
	 * 
	 * @param x
	 *            y coordinate.
	 * @param y
	 *            y coordinate.
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
	}

	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
	/**
	 * Shows command help dialog for the item.
	 */
	void showHelp() {
		if (MarblePanel.checkError(item)) {
			return;
		}
		MarblePanel.showDeferred(item);
	}
}

