package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.view.spreadsheet.SpreadsheetView;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class SpreadsheetPanel extends Composite implements RequiresResize {

	App application = null;

	private static SpreadsheetPanelUiBinder uiBinder = GWT
	        .create(SpreadsheetPanelUiBinder.class);

	interface SpreadsheetPanelUiBinder extends UiBinder<AbsolutePanel, SpreadsheetPanel> {
	}

	@UiField AbsolutePanel tempsheet;
	SpreadsheetView spreadsheet = null;

	public SpreadsheetPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void onResize() {
		App.debug("resized");
    }

	public void attachApp(App app) {
	   this.application = app;

	   // get the spreadsheet from the app
	   spreadsheet = ((AppW)app).getGuiManager().getSpreadsheetView();

	   tempsheet.add(spreadsheet);
	   spreadsheet.getScrollPanel().setWidth(this.getOffsetWidth()+"px");
	   spreadsheet.getScrollPanel().setHeight(this.getOffsetHeight()+"px");
	}
}
