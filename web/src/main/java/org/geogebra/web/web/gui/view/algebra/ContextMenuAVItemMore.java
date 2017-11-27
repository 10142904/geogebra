package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.AriaMenuItem;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels {
	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	protected Localization loc;
	private AppW app;
	/** parent item */
	RadioTreeItem item;
	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuAVItemMore(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
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
		addDuplicateItem();
		addDeleteItem();
		// wrappedPopup.addSeparator();
		addPropertiesItem();
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
	}

	private void addDuplicateItem() {
		String img = MaterialDesignResources.INSTANCE.duplicate_black()
				.getSafeUri()
				.asString();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
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
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
				loc.getMenu("Delete"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						item.geo.remove();
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addPropertiesItem() {
		String img = MaterialDesignResources.INSTANCE.gear()
				.getSafeUri()
				.asString();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
				loc.getMenu("Settings"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						openSettings();
					}
				});

		wrappedPopup.addItem(mi);
	}

	/**
	 * OPen object settings
	 */
	protected void openSettings() {
		ArrayList<GeoElement> list = new ArrayList<GeoElement>();
		list.add(item.geo);
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, list);

	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}
}

