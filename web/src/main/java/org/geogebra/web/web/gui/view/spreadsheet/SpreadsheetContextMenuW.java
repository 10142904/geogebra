package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Subclass of SpreadsheetContextMenu, implements the spreadsheet context menu
 * for web.
 * 
 * @author G. Sturr
 * 
 */
public class SpreadsheetContextMenuW extends SpreadsheetContextMenu {

	private GPopupMenuW popup;

	/**
	 * Constructor
	 * 
	 * @param table
	 */
	public SpreadsheetContextMenuW(MyTable table) {
		super(table);
	}

	@Override
	public Object getMenuContainer() {
		return popup;
	}

	@Override
	public void createGUI() {
		popup = new GPopupMenuW((AppW) app);
		popup.getPopupPanel().addStyleName("geogebraweb-popup-spreadsheet");
		initMenu();
		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			popup.getPopupPanel().addStyleName("contextMenu");
		} else if (app.isUnbundled()) {
			popup.getPopupPanel().addStyleName("matMenu");
		}
	}

	@Override
	public void setTitle(String str) {

		MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(
		        AppResources.INSTANCE.empty().getSafeUri().asString(), str),
		        true, new Command() {
			        @Override
					public void execute() {
				        popup.setVisible(false);
			        }
		        });
		if (app.isUnbundled()) {
			title.addStyleName("no-hover");
		} else {
			title.addStyleName("menuTitle");
		}
		popup.addItem(title);
	}

	/**
	 * @return true if has mow context menu feature
	 */
	protected boolean hasWhiteboardContextMenu() {
		return app.has(Feature.WHITEBOARD_APP)
				&& app.has(Feature.MOW_CONTEXT_MENU);
	}

	@Override
	protected void addEditItems() {
		if (hasWhiteboardContextMenu()) {
			addSeparator();

			addCut();
			addCopy();
			addDuplicate();
			addPaste();
			addDelete();
		} else {
			super.addEditItems();
		}
	}

	@Override
	protected void addShowObject(GeoElement geo) {
		// Show object item is skipped in spreadsheet
	}

	private void addDuplicate() {
		String cmdString = MenuCommand.Duplicate.toString();
		addMenuItem(cmdString, app.getLocalization().getMenu(cmdString),
				!isEmptySelection());
	}

	@Override
	public void addMenuItem(final String cmdString, String text, boolean enabled) {
		String html;
		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		MenuItem mi;
		mi = new MenuItem(html, true, getCommand(cmdString));
		if (!hasWhiteboardContextMenu()) {
			mi.addStyleName("mi_with_image");
		}
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String nonSelected,
			String selected,
			boolean isSelected) {

		String html;

		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString, isSelected),
					"");
		} else {
			html = MainMenu.getMenuBarHtml(
					getIconUrl(cmdString, app.isUnbundled()), "");
		}

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html, selected,
				nonSelected,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected);
		popup.addItem(cbItem);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
	        boolean isSelected) {

		String html;
		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected);
		popup.addItem(cbItem);
	}

	@Override
	public MenuItem addSubMenu(String text, String cmdString) {

		String html;
		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(
					getIconUrl(cmdString, app.isUnbundled()), text);
		}

		MenuBar subMenu = new MenuBar(true);
		MenuItem menuItem = new MenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		if (app.isUnbundled()) {

		}
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		String html;
		if (hasWhiteboardContextMenu() && !app.isUnbundled()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(
					getIconUrl(cmdString, app.isUnbundled()),
					text);
		}

		MenuItem mi = new MenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
		mi.setEnabled(enabled);

		((MenuItem) menu).getSubMenu().addItem(mi);

	}

	@Override
	public void addSeparator() {
		if (!app.isUnbundled()) {
			popup.addSeparator();
		}
	}

	private Command getCommand(final String cmdString) {
		Command cmd = new Command() {
			@Override
			public void execute() {
				doCommand(cmdString);
			}
		};
		return cmd;
	}

	private static String getIconUrl(String cmdString, boolean isNewDesign) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		switch (MenuCommand.valueOf(cmdString)) {

		case ShowObject:
			im = AppResources.INSTANCE.mode_showhideobject_16();
			break;
		case ShowLabel:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.label_black();
			} else {
				im = AppResources.INSTANCE.mode_showhidelabel_16();
			}
			break;
		case Copy:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.copy_black();
			} else {
				im = AppResources.INSTANCE.edit_copy();
			}
			break;
		case Cut:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.cut_black();
			} else {
				im = AppResources.INSTANCE.edit_cut();
			}
			break;
		case Paste:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.paste_black();
			} else {
				im = AppResources.INSTANCE.edit_paste();
			}
			break;
		case Duplicate:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.duplicate_black();
			} else {
				im = AppResources.INSTANCE.duplicate20();
			}
			break;
		case Delete:
		case DeleteObjects:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.delete_black();
			} else {
				im = AppResources.INSTANCE.delete_small();
			}
			break;
		case RecordToSpreadsheet:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE
						.record_to_spreadsheet_black();
			} else {
				im = AppResources.INSTANCE.spreadsheettrace();
			}
			break;
		case Properties:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.gere().getSafeUri(), 0,
						0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.view_properties16();
			}
			break;
		case SpreadsheetOptions:
			if (isNewDesign) {
				im = MaterialDesignResources.INSTANCE.settings_black();
			} else {
				im = AppResources.INSTANCE.view_properties16();
			}
			break;
		case Create:
			if (isNewDesign) {
				im = new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.add_black()
						.getSafeUri(),
						0, 0, 24, 24, false, false);
			} else {
				im = AppResources.INSTANCE.empty();
			}
			break;
		default:
			im = AppResources.INSTANCE.empty();
		}
		return im.getSafeUri().asString();
	}

	private static String getIconUrlNew(String cmdString, boolean isSelected) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		if (MenuCommand.valueOf(cmdString) == MenuCommand.ShowLabel) {
			if (isSelected) {
				im = AppResources.INSTANCE.label_off20();
			} else {
				im = AppResources.INSTANCE.label20();
			}
			return im.getSafeUri().asString();
		}
		return getIconUrlNew(cmdString);
	}

	private static String getIconUrlNew(String cmdString) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		switch (MenuCommand.valueOf(cmdString)) {

		case ShowObject:
			im = AppResources.INSTANCE.mode_showhideobject_16();
			break;
		case ShowLabel:
			im = AppResources.INSTANCE.label20();
			break;
		case Copy:
			im = AppResources.INSTANCE.copy20();
			break;
		case Cut:
			im = AppResources.INSTANCE.cut20();
			break;
		case Paste:
			im = AppResources.INSTANCE.paste20();
			break;
		case Duplicate:
			im = AppResources.INSTANCE.duplicate20();
			break;
		case Delete:
		case DeleteObjects:
			im = AppResources.INSTANCE.delete20();
			break;
		case RecordToSpreadsheet:
			im = AppResources.INSTANCE.record_to_spreadsheet20();
			break;
		case Properties:
			im = AppResources.INSTANCE.properties20();
			break;
		case SpreadsheetOptions:
			im = AppResources.INSTANCE.properties20();
			break;
		default:
			im = AppResources.INSTANCE.empty();
		}
		return im.getSafeUri().asString();
	}

}
