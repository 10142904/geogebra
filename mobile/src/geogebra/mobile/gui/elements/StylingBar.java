package geogebra.mobile.gui.elements;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.gui.elements.toolbar.ToolBarButton;
import geogebra.mobile.model.GuiModel;

import java.util.Arrays;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends RoundPanel
{
	private VerticalPanel base = new VerticalPanel();
	private ToolBarButton colorButton;
	private ToolBarButton labelButton;
	private ToolBarButton[] tempButtons = new ToolBarButton[0];

	public StylingBar(final GuiModel guiModel)
	{
		this.addStyleName("stylingbar");

		final ToolBarButton[] button = new ToolBarButton[3];
		button[0] = new ToolBarButton(
				CommonResources.INSTANCE.show_or_hide_the_axes(), guiModel);
		button[0].addStyleName("button-active");
		button[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("showAxes");
				if (button[0].getStyleName().endsWith("button-active"))
				{
					button[0].removeStyleName("button-active");
				} else
				{
					button[0].addStyleName("button-active");
				}
			}
		}, ClickEvent.getType());

		button[1] = new ToolBarButton(
				CommonResources.INSTANCE.show_or_hide_the_grid(), guiModel);
		button[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("showGrid");
				if (button[1].getStyleName().endsWith("button-active"))
				{
					button[1].removeStyleName("button-active");
				} else
				{
					button[1].addStyleName("button-active");
				}
			}
		}, ClickEvent.getType());

		button[2] = new ToolBarButton(
				CommonResources.INSTANCE.point_capturing(), guiModel);
		button[2].addStyleName("button-active");
		button[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				guiModel.processSource("pointCapture");
				if (button[2].getStyleName().endsWith("button-active"))
				{
					button[2].removeStyleName("button-active");
				} else
				{
					button[2].addStyleName("button-active");
				}
			}
		}, ClickEvent.getType());

		for (int i = 0; i < button.length; i++)
		{
			this.base.add(button[i]);
		}
		add(this.base);

		this.colorButton = new ToolBarButton(
				CommonResources.INSTANCE.colour(), guiModel);
		this.labelButton = new ToolBarButton(CommonResources.INSTANCE.label(),
				guiModel);
	}

	public void rebuild(ToolBarButton[] commands)
	{
		if (Arrays.equals(this.tempButtons, commands) && commands.length != 0)
		{
			return;
		}

		for (ToolBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}

		this.base.add(this.colorButton);
		this.tempButtons = commands;
		for (ToolBarButton b : this.tempButtons)
		{
			this.base.add(b);
		}
		this.base.add(this.labelButton);
	}

	@Override
	public void clear()
	{
		this.base.remove(this.colorButton);
		for (ToolBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}
		this.tempButtons = new ToolBarButton[0];
		this.base.remove(this.labelButton);
	}

	public void updateColour(String color)
	{
		this.colorButton.getElement().getStyle().setBackgroundColor(color);
	}
}
