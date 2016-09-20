package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.html5.main.FileManagerI;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.export.PrintPreviewW;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.exam.ExamDialog;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.gui.util.ShareDialogW;
import org.geogebra.web.web.main.AppWFull;

import com.google.gwt.user.client.ui.MenuItem;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable {
	
	/** Application */
	AppW app;
	private MenuItem uploadToGGT;
	/** clear construction and reset GUI */
	Runnable newConstruction;
	private MenuItem printItem;
	private Localization loc;
	
	/**
	 * @param app application
	 */
	public FileMenuW(final AppW app) {
		super(true, "help");
	    this.app = app;
		this.loc = app.getLocalization();
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
				if (app.has(Feature.NEW_START_SCREEN)) {
					app.showPerspectivesPopup();
				}
			}
		};
	    addStyleName("GeoGebraMenuBar");
	    initActions();
	}

	
	/**
	 * @return whether native JS function for sharing is present
	 */
	public native static boolean nativeShareSupported()/*-{
		if ($wnd.android && $wnd.android.share) {
			return true;
		}
		return false;
	}-*/;


	

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		if (!ExamUtil.toggleFullscreen(false)) {
			app.getExam().exit();
			boolean examFile = app.getArticleElement()
					.hasDataParamEnableGraphing();
			String buttonText = examFile ? loc.getPlain("Restart")
					: null;
			AsyncOperation<String[]> handler = null;
			if (examFile) {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						ExamDialog.startExam(null, app);
					}
				};
			}
			app.showMessage(true, app.getExam().getLog(app.getLocalization(),
							app.getSettings()), loc.getMenu("exam_log_header"),
					buttonText, handler);
			if (examFile)
				return;
			app.setExam(null);
			Layout.initializeDefaultPerspectives(app, 0.2);
			app.getLAF().addWindowClosingHandler(app);
			app.fireViewsChangedEvent();
			app.getGuiManager().updateToolbarActions();
			app.getGuiManager().setGeneralToolBarDefinition(
					ToolBar.getAllToolsNoMacros(true, false));
			app.getGuiManager().resetMenu();

			if (app.has(Feature.NEW_START_SCREEN)) {
				app.setActivePerspective(0);
			}
		}
	}
	

	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_sign_out().getSafeUri().asString(),
					loc.getMenu("exam_menu_exit"), true), true,
					new MenuCommand(app) { // Close

				@Override
				public void doExecute() {
					// set Firefox dom.allow_scripts_to_close_windows in about:config to true to make this work
							String[] optionNames = { loc.getMenu("Cancel"),
									loc.getMenu("Exit") };

					app.getGuiManager()
							.getOptionPane()
							.showOptionDialog(app,
											loc.getMenu("exam_exit_confirmation"), // ExitExamConfirm
											loc.getMenu("exam_exit_header"), // ExitExamConfirmTitle
											1, GOptionPane.WARNING_MESSAGE,
											null,
											optionNames,
											new AsyncOperation<String[]>() {
										@Override
												public void callback(
														String[] obj) {
													if ("1".equals(obj[0])) {
												exitAndResetExam();
											}
										}
					        });
				}
			});

			return;
		}
		/*
		 * } else { if (app.isExam()) { return; } }
		 */
		
		

		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				.menu_icon_file_new().getSafeUri().asString(),
				loc.getMenu("New"), true), true, new MenuCommand(app) {

			@Override
			public void doExecute() {
						((DialogManagerW) app.getDialogManager()).getSaveDialog().showIfNeeded(newConstruction);

			}
		});

		// open menu is always visible in menu
		
		addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				.menu_icon_file_open().getSafeUri().asString(),
				loc.getMenu("Open"), true), true, new MenuCommand(app) {
    		
				@Override
				public void doExecute() {
			        app.openSearch(null);
				}
			});	
		
		
		if(app.getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_file_save().getSafeUri().asString(),
					loc.getMenu("Save"), true), true, new MenuCommand(app) {
		
				@Override
				public void doExecute() {
			        app.getGuiManager().save();
				}
			});			
		}

		addSeparator();

		if (app.has(Feature.WEB_SHARE_DIALOG)) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_file_share().getSafeUri().asString(),
					loc.getMenu("Share"), true), true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							if (!nativeShareSupported()) {
								showShareDialog();
							} else {
								app.getGgbApi().getBase64(true,
										getShareStringHandler(app));
							}


					}
			});

		} else {
		// this is enabled always

			uploadToGGT = addItem(
					MainMenu.getMenuBarHtml(GuiResources.INSTANCE.menu_icon_file_share().getSafeUri().asString(),
					loc.getMenu("Share"), true),
					true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							if (!nativeShareSupported()) {
								app.uploadToGeoGebraTube();
							} else {
						app.getGgbApi().getBase64(true,
								getShareStringHandler(app));
							}
						}
					});
		}

		if (app.getLAF().exportSupported()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
				        .menu_icons_file_export()
.getSafeUri().asString(),
					loc.getMenu("Export"), true),
			        true, new ExportMenuW(app));

		}


		if (app.getLAF().printSupported()) {
			Log.debug("new printItem");
			printItem = new MenuItem(MainMenu.getMenuBarHtml(
					GuiResources.INSTANCE
					.menu_icons_file_print().getSafeUri().asString(),
 loc.getMenu("PrintPreview"), true),
					true, new MenuCommand(
					app) {

				@Override
						public void doExecute() {
							if (app.getGuiManager()
									.showView(App.VIEW_EUCLIDIAN)
									|| app.getGuiManager().showView(
											App.VIEW_EUCLIDIAN2)
									|| app.has(Feature.WEB_PRINT_CP_VIEW)
									&& app.getGuiManager().showView(
											App.VIEW_CONSTRUCTION_PROTOCOL)) {
								new PrintPreviewW(app).show();
							}
						}
			});
			// updatePrintMenu();
			addItem(printItem);

		}
		if (!app.has(Feature.NEW_START_SCREEN)) {
			addSeparator();

			if (app.getLAF().examSupported(app.has(Feature.EXAM_TABLET))) {
				// reset cas and 3d settings for restart of exam
				app.getSettings().getCasSettings().resetEnabled();
				app.getSettings().getEuclidian(-1).resetEnabled();
				if (app.getArticleElement().getDataParamEnableCAS(false) || !app
						.getArticleElement().getDataParamEnableCAS(true)) {
					app.getSettings().getCasSettings().setEnabled(app
							.getArticleElement().getDataParamEnableCAS(false));
				}
				if (app.getArticleElement().getDataParamEnable3D(false) || !app
						.getArticleElement().getDataParamEnable3D(true)) {
					app.getSettings().getEuclidian(-1).setEnabled(app
							.getArticleElement().getDataParamEnable3D(false));
				}
				
				if (app.getArticleElement().getDataParamEnableGraphing(false)
						|| !app.getArticleElement().getDataParamEnableGraphing(
								true)) {
					app.getSettings()
							.getEuclidian(1)
							.setEnabled(
									app.getArticleElement()
											.getDataParamEnableGraphing(false));
					app.getSettings()
							.getEuclidian(2)
							.setEnabled(
									app.getArticleElement()
											.getDataParamEnableGraphing(false));
				}
				
				addItem(MainMenu.getMenuBarHtml(
						GuiResources.INSTANCE.menu_icons_exam_mode()
								.getSafeUri().asString(),
						loc.getMenu("exam_menu_enter"), true), // EnterExamMode
						true, new MenuCommand(app) {

							@Override
							public void doExecute() {
								((DialogManagerW) app.getDialogManager())
										.getSaveDialog()
										.showIfNeeded(getExamCallback());

							}
						});
			}
		}
		if (app.has(Feature.BACK_TO_GGB)) {
			addItem(MainMenu.getMenuBarHtml(
					GuiResourcesSimple.INSTANCE.icons_fillings_arrow_big_left()
							.getSafeUri().asString(),
					loc.getMenu("BackToGeoGebra"), true), // EnterExamMode
					true, new MenuCommand(app) {

						@Override
						public void doExecute() {
							backToGeoGebra();

						}
					});

		}
	    app.getNetworkOperation().getView().add(this);
	    
	    if (!app.getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}

	/**
	 * SHow the custom share dialog
	 */
	protected void showShareDialog() {
		Runnable shareCallback = new Runnable() {

			public void run() {
				ShareDialogW sd = new ShareDialogW(app);
				sd.setVisible(true);
				sd.center();

			}
		};
		if (app.getActiveMaterial() == null
				|| "P".equals(app.getActiveMaterial().getVisibility())) {
			if (!app.getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				app.getLoginOperation().getView().add(new EventRenderable() {

					public void renderEvent(BaseEvent event) {
						if (event instanceof LoginEvent
								&& ((LoginEvent) event).isSuccessful()) {
							showShareDialog();
						}

					}
				});
				((SignInButton) app.getLAF().getSignInButton(app)).login();
			} else {
				// not saved, logged in
				((DialogManagerW) app.getDialogManager()).getSaveDialog()
						.setDefaultVisibility(SaveDialogW.Visibility.Shared)
					.showIfNeeded(shareCallback, true);
			}
		} else {
			// saved
			shareCallback.run();
		}

	}

	/**
	 * Go to geogebra.org or close iframe if we are running in one
	 */
	protected native void backToGeoGebra() /*-{
		if ($wnd != $wnd.parent) {
			$wnd.parent.postMessage("{\"type\":\"closesingleton\"}",
					location.protocol + "//" + location.host);
		} else {
			$wnd.location.assign("/");
		}

	}-*/;

	private boolean printItemAdded = false;

	/**
	 * 
	 * @param app
	 *            application
	 * @return handler for native sharing
	 */
	public static StringHandler getShareStringHandler(final AppW app) {
		return new StringHandler(){
			@Override
			public void handle(String s) {
				String title = app.getKernel().getConstruction().getTitle();
				FileManagerI fm = app.getFileManager();
				fm.nativeShare(s, "".equals(title) ? "construction" : title);
			}
		};
	}

	/**
	 * @return callback that shows the exam welcom message and prepares Exam
	 *         (goes fullscreen)
	 */
	Runnable getExamCallback() {

		return new Runnable() {

			public void run() {
				if (app.getLAF().supportsFullscreen()) {
					ExamUtil.toggleFullscreen(true);
				}
				app.setExam(new ExamEnvironment());
				((AppWFull) app).examWelcome();

			}
		};
	}
	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	public void render(boolean online) {
	    uploadToGGT.setEnabled(online);
	    if (!online) {
			uploadToGGT.setTitle(loc.getMenu("YouAreOffline"));
		} else {
			uploadToGGT.setTitle("");
		}
    }
}
