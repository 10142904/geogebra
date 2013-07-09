package geogebra.touch.model;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianStyleBarStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.stylingbar.StyleBarStatic;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBarButton;
import geogebra.touch.utils.OptionType;
import geogebra.touch.utils.ToolBarCommand;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Organizes the visibility of the additional {@link OptionsBar toolBar}
 * according to the {@link ToolBarButton active button}.
 * 
 * @author Thomas Krismayer
 * 
 */
public class GuiModel
{

	private TouchModel touchModel;
	private ToolBarButton activeButton;
	private StylingBar stylingBar;
	private PopupPanel option;

	private OptionType styleBarOptionShown = OptionType.Non;

	private GColor color;
	private float alpha = -1f;
	private int lineStyle = -1;
	private int lineSize = -1;
	private int captionMode = -1;

	/**
	 * @param model
	 *          if it is not intended to use a TouchModel, model can be null
	 */
	public GuiModel(TouchModel model)
	{
		this.touchModel = model;
	}

	public ToolBarCommand getCommand()
	{
		return this.activeButton == null ? null : this.activeButton.getCmd();
	}

	public void buttonClicked(ToolBarButton tbb)
	{
		closeOptions();
		setActive(tbb);

		if (this.touchModel != null)
		{
			this.touchModel.resetSelection();
			this.touchModel.repaint();
		}
	}

	public void updateStylingBar()
	{
		if (this.stylingBar != null)
		{
			this.stylingBar.rebuild();
		}
	}

	/**
	 * 
	 * @return true if anything was closed, false otherwise
	 */
	public boolean closeOptions()
	{
		if (this.styleBarOptionShown == OptionType.Non)
		{
			System.out.println("false");
			return false;
		}

		if (this.option != null)
		{
			if (this.styleBarOptionShown != OptionType.Dialog)
			{
				this.option.hide();
			}

			if (this.touchModel != null)
			{
				this.touchModel.optionsClosed();
			}
		}

		this.styleBarOptionShown = OptionType.Non;

		System.out.println("true");
		
		return true;

		// activeButton looses style otherwise
		// this.activeButton.addStyleDependentName("active");
	}

	public void setActive(ToolBarButton toolBarButton)
	{
		if (this.activeButton != null && this.activeButton != toolBarButton)
		{
			// transparent
			this.activeButton.setActive(false);
			this.activeButton.removeStyleName("active");
		}
		this.activeButton = toolBarButton;
		this.activeButton.setActive(true);
		this.activeButton.addStyleName("active");

		if (this.touchModel != null)
		{
			this.touchModel.setCommand(toolBarButton.getCmd());
		}

		this.stylingBar.rebuild();
	}

	/**
	 * 
	 * @param newOption
	 *          the PopupPanel to be shown
	 * @param type
	 *          the OptionsType of the PopupPanel
	 * @param parent
	 *          the button that was clicked, null in case of a Dialog
	 *          (OptionsType.Dialog)
	 */
	public void showOption(PopupPanel newOption, OptionType type, StandardImageButton parent)
	{
		closeOptions();
		this.option = newOption;
		if (type != OptionType.Dialog)
		{
			newOption.showRelativeTo(parent);
		}
		this.styleBarOptionShown = type;
	}

	public OptionType getOptionTypeShown()
	{
		return this.styleBarOptionShown;
	}

	public void setStylingBar(StylingBar bar)
	{
		this.stylingBar = bar;
	}

	public void resetStyle()
	{
		this.color = null;
		this.alpha = -1f;
		this.lineStyle = -1;
		this.lineSize = -1;
		this.captionMode = -1;
	}

	public void appendStyle(ArrayList<GeoElement> elements)
	{
		if (this.color != null)
		{
			StyleBarStatic.applyColor(elements, this.color);
		}
		if (this.alpha >= 0) // != -1f
		{
			StyleBarStatic.applyAlpha(elements, this.alpha);
		}
		if (this.lineStyle != -1)
		{
			StyleBarStatic.applyLineStyle(elements, this.lineStyle);
		}
		if (this.lineSize != -1)
		{
			StyleBarStatic.applyLineSize(elements, this.lineSize);
		}
		if (this.captionMode != -1)
		{
			EuclidianStyleBarStatic.applyCaptionStyle(elements, -1, this.captionMode);
			// second argument (-1): anything other than 0
		}
	}

	public void setColor(GColor c)
	{
		this.color = c;
	}

	public void setAlpha(float a)
	{
		this.alpha = a;
	}

	public void setLineStyle(int i)
	{
		this.lineStyle = i;
	}

	public void setLineSize(int i)
	{
		this.lineSize = i;
	}

	public void setCaptionMode(int i)
	{
		this.captionMode = i;
	}
}