package geogebra.mobile.gui.elements.stylingbar;

import geogebra.mobile.gui.CommonResources;
import geogebra.mobile.model.GuiModel;
import geogebra.mobile.model.MobileModel;
import geogebra.mobile.utils.StylingBarEntries;

import java.util.ArrayList;
import java.util.Arrays;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.widget.RoundPanel;

/**
 * The StylingBar includes the standard buttons (grid, axes, point capturing)
 * and the optional buttons, which are only shown if needed (e.g. color, lineStyle).
 * 
 * @author Thomas Krismayer
 * 
 */
public class StylingBar extends RoundPanel
{
	private VerticalPanel base = new VerticalPanel();
	private StylingBarButton[] tempButtons = new StylingBarButton[0];
	private StylingBarButton[] option;
	final GuiModel guiModel;
	MobileModel mobileModel; 
	StylingBarButton[] button;
	StylingBarButton colorButton;
	
	/**
	 * Initializes the {@link StylingBarButton StylingBarButtons}.
	 * @param guiModel
	 */
	public StylingBar(final GuiModel guiModel, MobileModel mobileModel)
	{
		this.addStyleName("stylingbar");
		this.guiModel = guiModel;
		this.mobileModel = mobileModel; 
		
		createStandardButtons();
		createOptionalButtons();
	}

	/**
	 * Initializes the standardButtons which are always shown
	 * (ShowGrid, ShowAxes & PointCapture).
	 */
	private void createStandardButtons()
  {
		this.button = new StylingBarButton[3];
		
		this.button[0] = createStyleBarButton("showAxes", CommonResources.INSTANCE.show_or_hide_the_axes(), 0);
		this.button[1] = createStyleBarButton("showGrid", CommonResources.INSTANCE.show_or_hide_the_grid(), 1);
		this.button[2] = createStyleBarButton("pointCapture", CommonResources.INSTANCE.point_capturing(), 2);
		
		//set button showAxes and pointCapture to (default) active
		this.button[0].addStyleName("button-active");
		this.button[2].addStyleName("button-active");
		
		//add the standardButtons to the verticalPanel
		for (int i = 0; i < this.button.length; i++)
		{
			this.base.add(this.button[i]);
		}
		add(this.base);
  }

	/**
	 * Initializes the optional buttons, which are only shown if its necessary.
	 */
	private void createOptionalButtons()
  {
	  // default: close ToolBarOptions on click
		ClickHandler defaultHandler = new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				StylingBar.this.guiModel.closeOptions();
			}
		};

		// optional buttons
		this.option = new StylingBarButton[2];
		this.option[0] = new StylingBarButton(CommonResources.INSTANCE.label(), defaultHandler);
		this.option[1] = new StylingBarButton(CommonResources.INSTANCE.properties_defaults(), defaultHandler);
		
		this.colorButton = addColorBarButton();
  }

	/**
	 * 
	 * @param process
	 * @param svg
	 * @param number
	 * @return a new StylingBarButton with an ClickHandler
	 */
	private StylingBarButton createStyleBarButton(final String process, SVGResource svg, final int number)
  {
	  return new StylingBarButton(svg, new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				StylingBar.this.guiModel.closeOptions();
				StylingBar.this.guiModel.processSource(process);
				if (StylingBar.this.button[number].getStyleName().endsWith("button-active"))
				{
					StylingBar.this.button[number].removeStyleName("button-active");
				}
				else
				{
					StylingBar.this.button[number].addStyleName("button-active");
				}
			}
		});
  }

	/**
	 * Initializes the colorButton with an ClickHandler to open and close
	 * the color-choice-wheel.
	 * @return
	 */
	private StylingBarButton addColorBarButton()
  {
	  return new StylingBarButton(CommonResources.INSTANCE.colour(), new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (StylingBar.this.guiModel.getColorBarShown())
				{
					StylingBar.this.guiModel.closeOptions();
				}
				else
				{
					ColorBarBackground colorBar = new ColorBarBackground(StylingBar.this, StylingBar.this.mobileModel);
					StylingBar.this.guiModel.showColorBar(colorBar);
				}
			}
		});
  }

	
	/**
	 * 
	 * @param commands
	 *          Buttons to add to the StylingBar; in case of an empty Array only
	 *          the ColorButton will be added; in case of null no further Button
	 *          will be added at all
	 */
	public void rebuild(StylingBarButton[] commands)
	{
		if (commands == null)
		{
			clear();
			return;
		}

		if (Arrays.equals(this.tempButtons, commands) && commands.length != 0)
		{
			return;
		}

		for (StylingBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}

		this.base.add(this.colorButton);
		this.tempButtons = commands;
		for (StylingBarButton b : this.tempButtons)
		{
			this.base.add(b);
		}
	}

	public void rebuild(SVGResource[] resource)
	{
		ArrayList<StylingBarButton> buttons = new ArrayList<StylingBarButton>();
		for (SVGResource svg : resource)
		{
			for (StylingBarButton b : this.option)
			{
				if (svg.equals(b.getIcon()))
				{
					buttons.add(b);
				}
			}
		}
		rebuild(buttons.toArray(new StylingBarButton[buttons.size()]));
	}

	/**
	 * 
	 * @param entries
	 *          the
	 */
	public void rebuild(StylingBarEntries entries)
	{
		if (entries == null)
		{
			clear();
		}
		else
		{
			rebuild(entries.getResources());
			updateColor(entries.getColor().toString());
		}
	}

	@Override
	public void clear()
	{
		this.base.remove(this.colorButton);
		for (StylingBarButton b : this.tempButtons)
		{
			this.base.remove(b);
		}
		this.tempButtons = new StylingBarButton[0];
	}

	public void updateColor(String color)
	{
		this.colorButton.getElement().getStyle().setBackgroundColor(color);
	}
}
