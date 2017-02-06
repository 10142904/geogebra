package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItem;

public class HelpMenuW extends GMenuBar implements BooleanRenderable{
	private MenuItem tutorials, forum, manual, about, bug;
	
	public HelpMenuW(final App app)  {

		super(true, "help");
	    addStyleName("GeoGebraMenuBar");
	    
		Localization loc = app.getLocalization();
	    // TODO: This item has no localization entry yet.
	    //addItem("Version", new Command() {
		//	public void execute() {
	    //        Window.alert("GeoGebra " + GeoGebraConstants.VERSION_STRING + "\n"
	    //        		+ GeoGebraConstants.BUILD_DATE);       
        //    }
	    //});
	 // Tutorials
	    tutorials = addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("Tutorials"), true), true,
				new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        app.getGuiManager().openHelp(App.WIKI_TUTORIAL);
            }
	    });
	    
	    // Help
	    manual = addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_help().getSafeUri().asString(),
				loc.getMenu("Manual"), true), true,
				new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
		        app.getGuiManager().openHelp(App.WIKI_MANUAL);
				
            }
	    });
	    
	    forum = addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("GeoGebraForum"), true), true,
				new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
	    		((AppW) app).getFileManager().open(GeoGebraConstants.FORUM_URL, "_blank", "");
				
            }
	    });

	    addSeparator();
	    
	    // Report Bug
	    bug = addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty().getSafeUri().asString(),
				loc.getMenu("ReportBug"), true), true,
				new MenuCommand((AppW) app) {
			
	    	@Override
	    	public void doExecute() {
	    		((AppW) app).getFileManager().open(GeoGebraConstants.GEOGEBRA_REPORT_BUG_WEB + "&lang="+app.getLocalization().getLanguage(), "_blank","");
            }
	    });
	    
	    addSeparator();

	    about = addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_help_about().getSafeUri().asString(),
				loc.getMenu("AboutLicense"), true), true,
				new MenuCommand((AppW) app) {
	    	
					@Override
					public void doExecute() {
						((AppW) app).getFileManager().open(GeoGebraConstants.GGW_ABOUT_LICENSE_URL
								+ "&version=" + app.getVersionString()
								+ "&date=" + GeoGebraConstants.BUILD_DATE,
								"_blank",
								"width=720,height=600,scrollbars=yes,toolbar=no,location=no,directories=no,menubar=no,status=no,copyhistory=no");
					}
	    });
	    if(!((AppW)app).getNetworkOperation().isOnline()){
	    	render(false);
	    }
	    ((AppW)app).getNetworkOperation().getView().add(this);
	    // TODO: This item has no localization entry yet.
	    //addItem("About / Team", new Command() {
		//	public void execute() {
	    //       Window.open(GeoGebraConstants.GGW_ABOUT_TEAM_URL, "_blank", "");
        //    }
	    //});
	}

	@Override
    public void render(boolean b) {
	    about.setEnabled(b);
	    manual.setEnabled(b);
	    tutorials.setEnabled(b);
	    bug.setEnabled(b);
	    forum.setEnabled(b);
	    
    }
	
}
