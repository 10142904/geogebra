package geogebra.web.gui.menubar;

import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.laf.MainMenuI;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.GuiManagerW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackPanel;

/**
 * Sidebar menu for SMART
 * 
 * 
 */

public class MainMenu extends FlowPanel implements MainMenuI, EventRenderable {
	
	/**
	 * Appw app
	 */
	/*private MenuItem signIn;
	private SignedInMenuW signedIn;
	private MenuItem signedInMenu;*/
	
	private AppW app;
	
	private StackPanel menuPanel;
	private ViewMenuW viewMenu;
	private FileMenuW fileMenu;
	private HelpMenuW helpMenu;
	private OptionsMenuW optionsMenu;
	private ToolsMenuW toolsMenu;
	private EditMenuW editMenu;
	private PerspectivesMenuW perspectivesMenu;
	private GMenuBar[] menus;
	private GMenuBar userMenu;
	private GMenuBar signInMenu = new GMenuBar(true);

	/**
	 * Constructs the menubar
	 * 
	 * @param app
	 *            application
	 */
	public MainMenu(AppW app) {
		this.addStyleName("menubarSMART");
		this.app = app;
		init();
	}

	private void init() {
		this.app.getLoginOperation().getView().add(this);
		this.createFileMenu();
		this.createPerspectivesMenu();
		this.createEditMenu();
		this.createViewMenu();
		this.createOptionsMenu();
		this.createToolsMenu();
		this.createHelpMenu();
		this.createUserMenu();
		if(app.isPrerelease()){
			this.menus = new GMenuBar[]{fileMenu,editMenu,perspectivesMenu,viewMenu, optionsMenu, toolsMenu, helpMenu};
		}else{
			this.menus = new GMenuBar[]{fileMenu,editMenu,perspectivesMenu,viewMenu, optionsMenu, helpMenu};
		}
		for(int i=0; i<menus.length; i++){
			final int next = (i+1)%menus.length;
			final int previous = (i-1+menus.length)%menus.length;
			final int index = i;
		this.menus[i].addDomHandler(new KeyDownHandler(){
			
			@Override
            public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				//First / last below are not intuitive -- note that default handler of
				//down skipped already from last to first
				if(keyCode == KeyCodes.KEY_DOWN){
					if(menus[index].isFirstItemSelected()){
						menuPanel.showStack(next);
						menus[next].focus();
					}
					
				}
				if(keyCode == KeyCodes.KEY_UP){
					if(menus[index].isLastItemSelected()){
						menuPanel.showStack(previous);
						menus[previous].focus();
					}
				}
				if(keyCode == KeyCodes.KEY_ESCAPE){
					app.toggleMenu();
					((GuiManagerW)app.getGuiManager()).getToolbarPanel().selectMenuButton(-1);
				}
	            
            }}, KeyDownEvent.getType());
		}
		this.menuPanel = new StackPanel(){
			@Override
			public void showStack(int index) {
				super.showStack(index);
				app.getGuiManager().setDraggingViews(index == 3 || index == 2, false);
			}

			@Override
			public void onBrowserEvent(Event event) {
				
				if (DOM.eventGetType(event) == Event.ONCLICK) {
					Element target = DOM.eventGetTarget(event);
					int index = findDividerIndex(target);
					//check if SignIn was clicked
					if (!app.getLoginOperation().isLoggedIn() && index == menuPanel.getWidgetCount()-1) {
						app.getDialogManager().showLogInDialog();
						app.toggleMenu();
						return;
					}
					if (index != -1) {
						showStack(index);
					}
				}
				super.onBrowserEvent(event);
			}
			
			  private int findDividerIndex(Element elem) {
				    while (elem != null && elem != getElement()) {
				      String expando = elem.getPropertyString("__index");
				      if (expando != null) {
				        // Make sure it belongs to me!
				        int ownerHash = elem.getPropertyInt("__owner");
				        if (ownerHash == hashCode()) {
				          // Yes, it's mine.
				          return Integer.parseInt(expando);
				        } else {
				          // It must belong to some nested StackPanel.
				          return -1;
				        }
				      }
				      elem = DOM.getParent(elem);
				    }
				    return -1;
				  }

		};
		this.menuPanel.addStyleName("menuPanel");
		
		this.menuPanel.add(fileMenu, setHTML(GuiResources.INSTANCE.menu_icon_file(), "File"), true);
		this.menuPanel.add(editMenu, setHTML(GuiResources.INSTANCE.menu_icon_edit(), "Edit"), true);
		this.menuPanel.add(perspectivesMenu, setHTML(GuiResources.INSTANCE.menu_icon_perspectives(), "Perspectives"), true);
		this.menuPanel.add(viewMenu, setHTML(GuiResources.INSTANCE.menu_icon_view(), "View"), true);
		this.menuPanel.add(optionsMenu, setHTML(GuiResources.INSTANCE.menu_icon_options(), "Options"), true);
		if (app.isPrerelease()) {
			this.menuPanel.add(toolsMenu, setHTML(GuiResources.INSTANCE.menu_icon_tools(), "Tools"), true);
		}
		this.menuPanel.add(helpMenu, setHTML(GuiResources.INSTANCE.menu_icon_help(), "Help"), true);
		if (app.getLoginOperation().isLoggedIn()) {
			addUserMenu();
		} else {
			addSignInMenu();
		}
	    this.add(menuPanel);	    
	}

	private void createUserMenu() {
	    this.userMenu = new GMenuBar(true);	
	    this.userMenu.addStyleName("GeoGebraMenuBar");
	    this.userMenu.addItem(getMenuBarHtml(GuiResources.INSTANCE.menu_icon_sign_out().getSafeUri().asString(), app.getMenu("SignOut"), true), true, new MenuCommand(app) {

			@Override
            public void doExecute() {
				app.getLoginOperation().performLogOut();
			}
		});
    }

	private String setHTML(ImageResource img, String s){
		//return  "<img src=\""+img.getSafeUri().asString()+"\" /><span style= \"font-size:80% \"  >" + s + "</span>";
		return  "<img src=\""+img.getSafeUri().asString()+"\" /><span>" + app.getMenu(s) + "</span>";
	}
	
	private void createFileMenu() {
		fileMenu = new FileMenuW(app, null);
	}

	private void createPerspectivesMenu() {
		perspectivesMenu = new PerspectivesMenuW(app);
	}

	private void createEditMenu() {
		editMenu = new EditMenuW(app);
	}
	
	private void createViewMenu() {

		viewMenu = new ViewMenuW(app);
	}
	
	private void createHelpMenu() {
		helpMenu = new HelpMenuW(app);
	}

	private void createOptionsMenu() {
		optionsMenu = new OptionsMenuW(app);
	}

	private void createToolsMenu() {
		toolsMenu = new ToolsMenuW(app);
	}

	private EditMenuW getEditMenu() {
	    return editMenu;
    }

	public void updateMenubar() {
		if(app.hasOptionsMenu()){
			app.getOptionsMenu(null).update();
		}
		if(viewMenu != null){
			viewMenu.update();
		}
    }
	
	public void updateSelection() {
		if(this.getEditMenu()!=null){
			getEditMenu().initActions();
		}
	}

    public MenuItem getSignIn() {
		return null;
    }

	public void focus(){
		int index= Math.max(menuPanel.getSelectedIndex(),0);
		if(this.menus[index]!=null){
			this.menus[index].focus();
		}
	}
	
	public static void addSubmenuArrow(AppW app,MenuBar w) {
			w.addStyleName("subMenuLeftSide");
			FlowPanel arrowSubmenu = new FlowPanel();
			arrowSubmenu.addStyleName("arrowSubmenu");
			Image arrow = new Image(GuiResources.INSTANCE.arrow_submenu_right());
			arrowSubmenu.add(arrow);
		    w.getElement().appendChild(arrowSubmenu.getElement());
    }

	public static String getMenuBarHtml(String url, String str, boolean enabled) {
		String text2 = str.replace("\"", "'");
		String text3 = (enabled) ? text2 :  "<span style=\"color:gray;\">"+text2+"</span>";
		return  "<img class=\"GeoGebraMenuImage\" alt=\""+text2+"\" src=\""+url+"\" />"+" "+ text3;
    }

	public static String getMenuBarHtml(String url, String str) {
		String text = str.replace("\"", "'");
		return "<img width=\"16\" height=\"16\" alt=\""+text+"\" src=\""+url+"\" />"+" "+text;
    }

	public static void setMenuSelected(MenuItem m, boolean visible) {
		if (visible) {
			m.addStyleName("checked");
		} else {
			m.removeStyleName("checked");
		}
	}
	
	/**
	 * sets the height of the menu
	 * @param height int
	 */
	public void updateHeight(int height) {
		this.setHeight(height + "px");
    }

	@Override
	public void renderEvent(final BaseEvent event) {
		if (event instanceof LoginEvent && ((LoginEvent) event).isSuccessful()) {
			this.menuPanel.remove(this.signInMenu);
			addUserMenu();
			this.userMenu.setVisible(false);
		} else if (event instanceof LogOutEvent) {
			this.menuPanel.remove(this.userMenu);
			addSignInMenu();
			this.signInMenu.setVisible(false);
		}
	}

    private void addSignInMenu() {
	    this.menuPanel.add(this.signInMenu, setHTML(GuiResources.INSTANCE.menu_icon_sign_in(), app.getMenu("SignIn")), true);
    }

    private void addUserMenu() {
	    this.menuPanel.add(this.userMenu, setHTML(GuiResources.INSTANCE.menu_icon_signed_in_f(), app.getLoginOperation().getUserName()), true);
    }
}
