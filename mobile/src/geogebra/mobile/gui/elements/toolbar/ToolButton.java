package geogebra.mobile.gui.elements.toolbar;

import geogebra.mobile.utils.ToolBarCommand;

import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

/**
 * A Button for the ToolBar, allowing an SVG graphic to be set as background <br>
 * css-styling: {@code -webkit-background-size: 100%;} <br>
 * for the correct size of the SVG
 * 
 * @see ButtonBarButtonBase
 * @author Matthias Meisinger
 * 
 */
public class ToolButton extends ButtonBarButtonBase
{

	private ToolBarCommand cmd;

	public ToolButton(ToolBarCommand cmd)
	{
		super(null);
		this.addStyleName("toolbutton");

		this.cmd = cmd;
		super.getElement().getStyle().setBackgroundImage(cmd.getIconUrlAsString());

		// this.addTapHandler(new TapHandler()
		// {
		// @Override
		// public void onTap(TapEvent event)
		// {
		// Window.alert("Mode: " + cmd.getMode());
		// }
		// });

		this.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				clickStart();
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				clickEnd();
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
			}
		});

	}

	public ToolBarCommand getCmd()
	{
		return cmd;
	}

	public void setCmd(ToolBarCommand cmd)
	{
		this.cmd = cmd;
		super.getElement().getStyle().setBackgroundImage(cmd.getIconUrlAsString());
	}

	private void clickStart()
	{
		this.addStyleName("button-active");
	}

	private void clickEnd()
	{
		this.removeStyleName("button-active");
	}

}
