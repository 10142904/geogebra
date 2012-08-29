package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class MySplitLayoutPanel extends SplitLayoutPanel {

	private GGWGraphicsView ggwGraphicView;
	private GGWViewWrapper ggwViewWrapper;

	//remove the comment to show the spreadsheet too (1)
	//TODO//private GGWSpreadsheetView ggwSpreadsheetView;

	private App application;

	public MySplitLayoutPanel(){
		super();
		addWest(ggwViewWrapper = new GGWViewWrapper(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);

		//remove the comment to show the spreadsheet too (2)
		//TODO//addEast(ggwSpreadsheetView = new GGWSpreadsheetView(), 400);

		add(ggwGraphicView = new GGWGraphicsView());
    }

	@Override
    public void onResize() {
		super.onResize();
		Element wrapper = getWidgetContainerElement(ggwGraphicView);
		((AppW) application).ggwGraphicsViewWidthChanged(wrapper.getOffsetWidth());
		
	}

	public SplitLayoutPanel getSplitLayoutPanel() {
	    return this;
    }

	public GGWGraphicsView getGGWGraphicsView() {
	    // TODO Auto-generated method stub
	    return ggwGraphicView;
    }

	public void attachApp(App app) {
	   this.application = app;
	   ggwViewWrapper.attachApp(app);
	   ggwGraphicView.attachApp(app);
    }

}
