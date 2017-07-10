package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class ContextMenuMore implements SetLabels {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	private AppW app;
	private RadioTreeItem item;
	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuMore(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		wrappedPopup = new GPopupMenuW(app);
		if (app.has(Feature.NEW_TOOLBAR)) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addDuplicateItem();
		addDeleteItem();
		// wrappedPopup.addSeparator();
		addPropertiesItem();
	}
	
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	private void addDuplicateItem() {
		String img = MaterialDesignResources.INSTANCE.duplicate_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Duplicate"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						RadioTreeItem input = item.getAV().getInputTreeItem();
						
						String dup = "";
						if ("".equals(item.geo.getDefinition(StringTemplate.defaultTemplate))) {
							dup = item.geo.getValueForInputBar();
						} else {
							dup = item.geo.getDefinitionNoLabel(
									StringTemplate.editorTemplate);
						}
						item.selectItem(false);
						input.setText(dup);
						input.setFocus(true, true);
					
					}
				});
		mi.setEnabled(item.geo.isAlgebraDuplicateable());
		wrappedPopup.addItem(mi);
	}
		
	private void addDeleteItem() {
		String img = MaterialDesignResources.INSTANCE.delete_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getPlain("Delete"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.geo.remove();
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addPropertiesItem() {
		String img = MaterialDesignResources.INSTANCE.settings_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Settings"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						ArrayList<GeoElement> list = new ArrayList<GeoElement>();
						list.add(item.geo);
						app.getDialogManager()
								.showPropertiesDialog(OptionType.OBJECTS,
								list);
					}
				});

		wrappedPopup.addItem(mi);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
}

