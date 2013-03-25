package geogebra.touch.gui;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ggt.HorizontalMaterialPanel;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;
import geogebra.touch.utils.ggtapi.GeoGebraTubeAPI;
import geogebra.touch.utils.ggtapi.JSONparserGGT;
import geogebra.touch.utils.ggtapi.Material;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.MSearchBox;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TubeSearchGUI extends VerticalPanel
{
	Presenter listener;

	private MSearchBox searchBox;
	protected HorizontalMaterialPanel featuredMaterials;
	protected VerticalMaterialPanel resultsArea;
	private Button backButton;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TubeSearchGUI()
	{
		this.setWidth(Window.getClientWidth() + "px");
		this.setHeight(Window.getClientHeight() + "px");

		this.searchBox = new MSearchBox();
		this.featuredMaterials = new HorizontalMaterialPanel();
		this.resultsArea = new VerticalMaterialPanel();

		// FIXME needs a proper icon
		this.backButton = new Button("BACK");

		// Handle orientation changes
		MGWT.addOrientationChangeHandler(new OrientationChangeHandler()
		{
			@Override
			public void onOrientationChanged(OrientationChangeEvent event)
			{
				// TODO update whatever is shown right now
			}
		});

		this.searchBox.addValueChangeHandler(new ValueChangeHandler<String>()
		{

			@Override
			public void onValueChange(ValueChangeEvent<String> event)
			{
				GeoGebraTubeAPI.getInstance().search(event.getValue(), new RequestCallback()
				{
					@Override
					public void onResponseReceived(com.google.gwt.http.client.Request request, Response response)
					{
						List<Material> materialList = JSONparserGGT.parseResponse(response.getText());

						if (materialList != null)
						{
							TubeSearchGUI.this.resultsArea.setMaterials(materialList);
						}

					}

					@Override
					public void onError(com.google.gwt.http.client.Request request, Throwable exception)
					{
						// TODO Handle error!
						exception.printStackTrace();
					}
				});
			}
		});
		this.featuredMaterials.setMaterials(new ArrayList<Material>());
		

		this.backButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TouchEntryPoint.showTabletGUI();
			}
		}, ClickEvent.getType());

		this.add(this.searchBox);
		this.add(this.featuredMaterials);
		this.add(this.resultsArea);
		this.add(this.backButton);
		setCellVerticalAlignment(this.backButton, HasVerticalAlignment.ALIGN_BOTTOM);

	}
	/**
	 * Loads the featured materials
	 */
	public void loadFeatured() {
		GeoGebraTubeAPI.getInstance().getFeaturedMaterials(new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				List<Material> materialList = JSONparserGGT.parseResponse(response.getText());

				TubeSearchGUI.this.featuredMaterials.setMaterials(materialList);
			}

			@Override
			public void onError(Request request, Throwable exception)
			{
				// TODO Auto-generated method stub

			}
		});
		
	}
}
