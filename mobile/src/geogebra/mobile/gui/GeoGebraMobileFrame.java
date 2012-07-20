package geogebra.mobile.gui;

import geogebra.common.main.App;
import geogebra.mobile.MobileApp;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

public class GeoGebraMobileFrame extends GeoGebraFrame
{
	private MobileApp app;
	private ArticleElement element;

	private RootPanel root;

	private HeaderPanel headerPanel;
	private ButtonBar toolBar;

	public GeoGebraMobileFrame()
	{
		root = RootPanel.get();

		headerPanel = new HeaderPanel();
		toolBar = new ButtonBar();
	}

	public void start()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		headerPanel.addStyleName("headerpanel");

		headerPanel.setTitle("Title");
		headerPanel.setCenter("Title");

		toolBar.addStyleName("toolbar");

		ButtonBarButtonBase[] b = new ButtonBarButtonBase[10];
		for (int i = 0; i < 10; i++)
		{
			b[i] = new ButtonBarButtonBase(Resources.INSTANCE.logo());

			b[i].setTitle("bla" + i);
			b[i].addStyleName("toolbutton" + i);
			toolBar.add(b[i]);
		}

		// get the article element from Mobile.html)
		element = ArticleElement.as(Dom.querySelector("geogebraweb"));

		// Initialize the AppW app
		app = new MobileApp(element, this);

		root.add(headerPanel);
		root.add(app.getEuclidianViewpanel());
		root.add(toolBar);

		Window.addResizeHandler(new ResizeHandler()
		{

			@Override
			public void onResize(ResizeEvent event)
			{
				app.resizeToParent(event.getWidth(), event.getHeight());
			}
		});

		App.debug("I'm here!");

	}
}
