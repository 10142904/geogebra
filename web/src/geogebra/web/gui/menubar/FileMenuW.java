package geogebra.web.gui.menubar;

import geogebra.common.main.App;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.GgbAPI;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;

public class FileMenuW extends MenuBar {
	
	private App app;
	
	public FileMenuW(App app, boolean enabled) {
	    super(true);
	    this.app = app;
	    addStyleName("GeoGebraMenuBar");
	    initActions(enabled);
		update();
	}

	public FileMenuW(App app) {
		this(app, true);
    }

	private void update() {
	    // TODO Auto-generated method stub
	    
    }

	private void initActions(boolean enabled) {

		// this is enabled always
		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),app.getMenu("New")),true,new Command() {

			public void execute() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		});

	    /*addItem(GeoGebraMenubar.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(), app.getMenu("Load")), true, new Command() {
			
			public void execute() {
				
			}
				
		});*/
	    
		// this is enabled always
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_open().getSafeUri().asString(),app.getMenu("OpenWebpage")),true,new Command() {
	    	public void execute() {
	    		app.getGuiManager().openURL();
	    	}
	    });
		
		if (enabled)
			addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_save().getSafeUri().asString(), app.getMenu("SaveAs")),true,new Command() {
			
				public void execute() {
					app.getGuiManager().save();
				}
			});
		else
			addItem(GeoGebraMenubarW.getMenuBarHtmlGrayout(AppResources.INSTANCE.document_save().getSafeUri().asString(), app.getMenu("SaveAs")),true,new Command() {
				public void execute() {	}
			});
			
//		addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.document_save().getSafeUri().asString(), app.getMenu("Download") ),true,new Command() {
//			
//			public void execute() {
//				//((GgbAPI) app.getGgbApi()).getGGB(true);
//				((GuiManagerW)(app.getGuiManager())).downloadGGB();
//			}
//		});
			
			

		// this is enabled always
	    addItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.export_small().getSafeUri().asString(),app.getMenu("Share")),true,new Command() {
	    	public void execute() {
	    		app.uploadToGeoGebraTube();
	    	}
	    });
    }

}
