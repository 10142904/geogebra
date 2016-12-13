package org.geogebra.web.web.javax.swing;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class GCheckBoxMenuItem {

	CheckBox checkBox;
	MenuItem menuItem;
	HorizontalPanel itemPanel;
	
	// public GCheckBoxMenuItem(SafeHtml html, final ScheduledCommand cmd) {
	// super(html, cmd);
	// checkBox = new CheckBox(html);
	// checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
	// public void onValueChange(ValueChangeEvent<Boolean> event) {
	// cmd.execute();
	// }});
	// setHTML(checkBox.toString());
	// }
	public GCheckBoxMenuItem(String text,
			boolean isHtml) {

		// It's didn't work, becase when I clicked on the label of the checkbox,
		// the command of menuitem didn't run, so I added the html-string for
		// the MenuItem
		// in an another way (see below)
		// checkBox = new CheckBox(html);

		checkBox = new CheckBox();
		itemPanel = new HorizontalPanel();
		itemPanel.add(checkBox);
		if (isHtml) {
			itemPanel.add(new HTML(text));
		} else {
			itemPanel.add(new Label(text));
		}

	}

	public GCheckBoxMenuItem(String text, final ScheduledCommand cmd,
			boolean isHtml) {
		this(text, isHtml);
		setCommand(cmd);
	}

	public void setCommand(ScheduledCommand cmd) {
		menuItem = new MenuItem(itemPanel.toString(), true, cmd);

	}
	public void setSelected(boolean sel) {
		checkBox.setValue(sel);
		menuItem.setHTML(itemPanel.toString());
	}

	/**
	 * 
	 * @return true if check box is checked
	 */
	public boolean isSelected() {
		return checkBox.getValue();
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

}
