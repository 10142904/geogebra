package org.geogebra.web.web.gui.view.spreadsheet;

import org.geogebra.common.gui.view.spreadsheet.MyTable;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetContextMenu;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.resources.client.ImageResource;
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
		if (isWhiteboard()) {
			popup.getPopupPanel().addStyleName("contextMenu");
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
		title.addStyleName("menuTitle");
		popup.addItem(title);
	}

	protected boolean isWhiteboard() {
		return app.has(Feature.WHITEBOARD_APP) && app.has(Feature.CONTEXT_MENU);
	}

	@Override
	protected void addEditItems() {
		if (isWhiteboard()) {
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
		if (isWhiteboard()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		}

		MenuItem mi;
		mi = new MenuItem(html, true, getCommand(cmdString));
		if (!isWhiteboard()) {
			mi.addStyleName("mi_with_image");
		}
		mi.setEnabled(enabled);

		popup.addItem(mi);
	}

	@Override
	public void addCheckBoxMenuItem(final String cmdString, String text,
	        boolean isSelected) {

		String html;
		if (isWhiteboard()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		}

		GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(html,
				getCommand(cmdString), true, app);
		cbItem.setSelected(isSelected);
		popup.addItem(cbItem);
	}

	@Override
	public Object addSubMenu(String text, String cmdString) {

		String html;
		if (isWhiteboard()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		}

		MenuBar subMenu = new MenuBar(true);
		MenuItem menuItem = new MenuItem(html, true, subMenu);

		popup.addItem(menuItem);
		return menuItem;
	}

	@Override
	public void addSubMenuItem(Object menu, final String cmdString,
	        String text, boolean enabled) {

		String html;
		if (isWhiteboard()) {
			html = MainMenu.getMenuBarHtml(getIconUrlNew(cmdString), text);
		} else {
			html = MainMenu.getMenuBarHtml(getIconUrl(cmdString), text);
		}

		MenuItem mi = new MenuItem(html, true, getCommand(cmdString));
		mi.addStyleName("mi_with_image");
		mi.setEnabled(enabled);

		((MenuItem) menu).getSubMenu().addItem(mi);
	}

	@Override
	public void addSeparator() {
		popup.addSeparator();
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

	private static String getIconUrl(String cmdString) {

		if (cmdString == null) {
			return AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		ImageResource im = null;

		switch (MenuCommand.valueOf(cmdString)) {

		case ShowObject:
			im = AppResources.INSTANCE.mode_showhideobject_16();
			break;
		case ShowLabel:
			im = AppResources.INSTANCE.mode_showhidelabel_16();
			break;
		case Copy:
			im = AppResources.INSTANCE.edit_copy();
			break;
		case Cut:
			im = AppResources.INSTANCE.edit_cut();
			break;
		case Paste:
			im = AppResources.INSTANCE.edit_paste();
			break;
		case Delete:
		case DeleteObjects:
			im = AppResources.INSTANCE.delete_small();
			break;
		case RecordToSpreadsheet:
			im = AppResources.INSTANCE.spreadsheettrace();
			break;
		case Properties:
			im = AppResources.INSTANCE.view_properties16();
			break;
		case SpreadsheetOptions:
			im = AppResources.INSTANCE.view_properties16();
			break;
		default:
			im = AppResources.INSTANCE.empty();
		}
		return im.getSafeUri().asString();
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
