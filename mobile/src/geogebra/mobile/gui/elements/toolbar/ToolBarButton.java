package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;
import geogebra.mobile.utils.ToolBarMenu;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;

/**
 * 
 * A button for the main-toolBar
 * 
 * @author Thomas Krismayer
 * 
 */
public class ToolBarButton extends ToolButton implements ToolBarButtonInterface
{

	ToolBarCommand[] menuEntries;
	ToolBarInterface parent;

	/**
	 * 
	 * @param menu
	 *          : the button to be placed
	 * @param parent
	 *          : the ToolBar it is placed on
	 */
	public ToolBarButton(ToolBarMenu menu, ToolBarInterface parent)
	{
		super(menu.getCommand());

		menuEntries = menu.getEntries();
		this.parent = parent;

		this.addTapHandler(new TapHandler()
		{
			@Override
			public void onTap(TapEvent event)
			{
				showOptions();
			}
		});
	}

	private void showOptions()
	{
		parent.closeOptions();
		OptionsBarBackground options = new OptionsBarBackground(menuEntries, this);
		parent.setOptions(options);
		RootPanel.get().add(options);
	}

	@Override
	public void optionClicked(ToolBarCommand cmd)
	{
		parent.closeOptions();
		super.setCmd(cmd); 
	}

}
