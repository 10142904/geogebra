package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.html5.css.GuiResources;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.util.StyleBarW;
import geogebra.web.gui.view.consprotocol.ConstructionProtocolViewW;
import geogebra.web.main.AppW;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public class ConstructionProtocolDockPanelW extends DockPanelW{

	private static final long serialVersionUID = 1L;
	private StyleBarW cpStyleBar;

	/**
	 * @param app
	 */
	public ConstructionProtocolDockPanelW(AppW app) {
		super(
			App.VIEW_CONSTRUCTION_PROTOCOL, 	// view id
			"ConstructionProtocol", 					// view title phrase 
			null,	// toolbar string
			true,					// style bar?
			7,						// menu order
			'L' // ctrl-shift-L
		);
		

		this.app = app;
		this.setShowStyleBar(true);
		this.setEmbeddedSize(300);
	}

	@Override
	protected Widget loadComponent() {
		setViewImage(GuiResources.INSTANCE.styleBar_ConstructionProtocol());
		return ((ConstructionProtocolViewW) app.getGuiManager().getConstructionProtocolView()).getCpPanel();
	}

	@Override
	protected Widget loadStyleBar() {
		if (cpStyleBar == null) {
			cpStyleBar = ((ConstructionProtocolViewW) app.getGuiManager().getConstructionProtocolView()).getStyleBar();
		}
		return cpStyleBar; 
		//return ((ConstructionProtocolView)app.getGuiManager().getConstructionProtocolView()).getStyleBar();
	}
	
	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub
	    
    }
	
	@Override
    public ImageResource getIcon() {
		return AppResources.INSTANCE.view_constructionprotocol24();
	}
	
}
