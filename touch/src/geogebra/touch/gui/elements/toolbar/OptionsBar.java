package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.utils.ToolBarCommand;

import com.google.gwt.user.client.ui.HorizontalPanel;


/**
 * The base of the submenu.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 * 
 */
public class OptionsBar extends HorizontalPanel
{

	/**
	 * Initializes the OptionsBar.
	 * 
	 * @param entries
	 *          : the buttons in the submenu
	 * @param ancestor
	 *          : the button responsible for the submenu
	 */
	OptionsBar(ToolBarCommand[] entries, OptionsClickedListener ancestor)
	{
		OptionButton[] options = new OptionButton[entries.length];

		for (int i = 0; i < options.length; i++)
		{
			options[i] = new OptionButton(entries[i], ancestor);
			this.add(options[i]);
		}

	}
}
