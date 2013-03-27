package geogebra.touch.gui.elements.ggt;

import geogebra.touch.utils.ggtapi.Material;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalMaterialPanel implements IsWidget
{
	private LayoutPanel layoutPanel;
	ScrollPanel scrollPanel;
	private FlexTable contentPanel;

	public VerticalMaterialPanel()
	{
		this.layoutPanel = new LayoutPanel();
		this.scrollPanel = new ScrollPanel();
		this.contentPanel = new FlexTable();

		this.layoutPanel.setSize(Window.getClientWidth() + "px", "400px");

		this.scrollPanel.setWidget(this.contentPanel);
		this.layoutPanel.add(this.scrollPanel);
	}

	public void setMaterials(List<Material> materials)
	{
		this.contentPanel.clear();

		int i = 0;
		for (Material m : materials)
		{
			MaterialListElement preview = new MaterialListElement(m);
			this.contentPanel.setWidget(i, 0, preview);
			i++;
		}
	}

	@Override
	public Widget asWidget()
	{
		return this.layoutPanel;
	}
}
