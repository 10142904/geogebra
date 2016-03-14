package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.export.pstricks.GeoGebraToAsymptoteD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPgfD;
import org.geogebra.desktop.export.pstricks.GeoGebraToPstricksD;
import org.geogebra.desktop.gui.app.GeoGebraFrame;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;

/**
 * The "File" menu.
 */
class FileMenuD extends BaseMenu implements EventRenderable {
	private static final long serialVersionUID = -5154067739481481835L;

	private AbstractAction newWindowAction, deleteAll, saveAction,
			saveAsAction, loadAction, loadURLAction, exportWorksheet,
			shareAction, exportGraphicAction, exportAnimationAction,
			exportPgfAction, exportPSTricksAction, exportAsymptoteAction;

	JMenuItem loadURLMenuItem;

	AbstractAction exportGeoGebraTubeAction;

	private AbstractAction drawingPadToClipboardAction;

	private AbstractAction printEuclidianViewAction;

	private AbstractAction exitAction;

	private AbstractAction exitAllAction;

	public FileMenuD(AppD app) {
		super(app, app.getMenu("File"));

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);

	}

	/**
	 * Initialize all items.
	 */
	@Override
	public void initItems() {
		if (!initialized) {
			return;
		}

		removeAll();

		JMenuItem mi;

		if (!app.isApplet()) {
			// "New" in application: new window
			mi = new JMenuItem(newWindowAction);
			setMenuShortCutAccelerator(mi, 'N');
			add(mi);
		}

		// "New": reset
		add(deleteAll);

		mi = add(loadAction);
		setMenuShortCutAccelerator(mi, 'O'); // open

		LoginOperationD signIn = (LoginOperationD) app.getLoginOperation();

		if (!app.isApplet()
				&& (signIn.isTubeAvailable() || !signIn.isTubeCheckDone())) {
			loadURLMenuItem = add(loadURLAction);

			// If GeoGebraTube is not available we disable the item and
			// listen to the event that tube becomes available
			if (!signIn.isTubeAvailable()) {
				loadURLAction.setEnabled(false);
				signIn.getView().add(this);
			}

		}

		// recent SubMenu
		JMenu submenuRecent = new JMenu(app.getMenu("Recent"));
		submenuRecent.setIcon(app.getEmptyIcon());
		add(submenuRecent);

		// Recent files list
		int size = AppD.getFileListSize();
		if (size > 0) {
			for (int i = 0; i < AppD.MAX_RECENT_FILES; i++) {
				File file = AppD.getFromFileList(i);
				if (file != null) {
					mi = new JMenuItem(file.getName());
					mi.setIcon(app.getMenuIcon("geogebra.png"));
					ActionListener al = new LoadFileListener(app, file);
					mi.addActionListener(al);
					submenuRecent.add(mi);
				}
			}
		}

		addSeparator();
		mi = add(saveAction);
		setMenuShortCutAccelerator(mi, 'S');
		mi = add(saveAsAction);
		addSeparator();

		mi = add(shareAction);
		mi.setIcon(app.getMenuIcon("export_small.png"));

		// export
		JMenu submenu = new JMenu(app.getMenu("Export"));
		submenu.setIcon(app.getEmptyIcon());
		add(submenu);

		mi = submenu.add(exportWorksheet);
		setMenuShortCutShiftAccelerator(mi, 'W');

		mi = submenu.add(exportGraphicAction);
		setMenuShortCutShiftAccelerator(mi, 'U');

		mi = submenu.add(exportAnimationAction);

		// Graphical clipboard is not working under Mac when Java == 7:
		if (!app.isMacOS() || !app.isJava7()) {
			mi = submenu.add(drawingPadToClipboardAction);
			setMenuShortCutShiftAccelerator(mi, 'C');
		}

		submenu.addSeparator();
		mi = submenu.add(exportPSTricksAction);
		setMenuShortCutShiftAccelerator(mi, 'T');

		mi = submenu.add(exportPgfAction);
		mi = submenu.add(exportAsymptoteAction);

		addSeparator();

		mi = add(printEuclidianViewAction);
		mi.setText(app.getMenu("PrintPreview"));
		mi.setIcon(app.getMenuIcon("document-print-preview.png"));
		setMenuShortCutAccelerator(mi, 'P');


		// End Export SubMenu

		// DONE HERE WHEN APPLET
		if (app.isApplet())
			return;

		// close
		addSeparator();
		mi = add(exitAction);
		if (AppD.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4,
					InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}

		// close all
		if (GeoGebraFrame.getInstanceCount() > 1) {
			add(exitAllAction);
		}

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Initialize all actions of this menu.
	 */
	@Override
	protected void initActions() {
		deleteAll = new AbstractAction(app.getMenu("New"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
			}
		};

		newWindowAction = new AbstractAction(app.getMenu("NewWindow"),
				app.getMenuIcon("document-new.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						app.createNewWindow();
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"),
				app.getMenuIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().save();
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().saveAs();
			}
		};

		shareAction = new AbstractAction(app.getMenu("Share") + "...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				exportGeoGebraTubeAction.actionPerformed(e);
			}
		};

		/*
		 * printProtocolAction = new AbstractAction(
		 * app.getPlain("ConstructionProtocol") + " ...") { private static final
		 * long serialVersionUID = 1L;
		 * 
		 * public void actionPerformed(ActionEvent e) { Thread runner = new
		 * Thread() { public void run() { ConstructionProtocol constProtocol =
		 * app.getConstructionProtocol(); if (constProtocol == null) {
		 * constProtocol = new ConstructionProtocol(app); }
		 * constProtocol.initProtocol();
		 * 
		 * try { new PrintPreview(app, constProtocol, PageFormat.PORTRAIT); }
		 * catch (Exception e) {
		 * Application.debug("Print preview not available"); } } };
		 * runner.start(); } };
		 */

		printEuclidianViewAction = new AbstractAction(
				app.getPlain("DrawingPad") + " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraMenuBar.showPrintPreview(app);
			}
		};

		exitAction = new AbstractAction(app.getMenu("Close"),
				app.getMenuIcon("exit.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exit();
			}
		};

		exitAllAction = new AbstractAction(app.getMenu("CloseAll"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.exitAll();
			}
		};

		loadAction = new AbstractAction(app.getMenu("Load") + " ...",
				app.getMenuIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().openFile();
			}
		};

		loadURLAction = new AbstractAction(app.getMenu("OpenFromGeoGebraTube")
				+ " ...", app.getMenuIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				// Check if javafx is available
				boolean javaFx22Available = false;
				try {
					this.getClass().getClassLoader()
							.loadClass("javafx.embed.swing.JFXPanel");
					javaFx22Available = true;
				} catch (ClassNotFoundException ex) {
					Log.error("JavaFX 2.2 not available");
				}

				// Open the Search dialog only when javafx is available.
				// The User can force opening the old 'Open URL' dialog by
				// pressing shift.
				if (javaFx22Available
						&& ((e.getModifiers() & ActionEvent.SHIFT_MASK) == 0)) {

					app.getGuiManager().openFromGGT();
				} else {

					// old File -> Open from Webpage by pressing <Shift>
					app.getGuiManager().openURL();
				}
			}
		};

		drawingPadToClipboardAction = new AbstractAction(
				app.getMenu("DrawingPadToClipboard"),
				app.getMenuIcon("menu-edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getSelectionManager().clearSelectedGeos(true, false);
				app.updateSelection(false);

				Thread runner = new Thread() {
					@Override
					public void run() {
						app.setWaitCursor();
						// copy drawing pad to the system clipboard
						app.copyGraphicsViewToClipboard();
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		/*
		 * updateAction = new AbstractAction(getMenu("Update"), getEmptyIcon())
		 * { private static final long serialVersionUID = 1L; public void
		 * actionPerformed(ActionEvent e) { Thread runner = new Thread() {
		 * public void run() { updateGeoGebra(); } }; runner.start(); } };
		 */

		exportGraphicAction = new AbstractAction(
				app.getPlain("DrawingPadAsPicture") + " (" + FileExtensions.PNG
						+ ", " + FileExtensions.EPS + ") ...",
				app.getMenuIcon("image-x-generic.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					Thread runner = new Thread() {
						@Override
						public void run() {
							app.setWaitCursor();
							try {

								app.getGuiManager().showGraphicExport();

							} catch (Exception e1) {
								Log.debug("GraphicExportDialog not available for 3D view yet");
								// for 3D View
								app.copyGraphicsViewToClipboard();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		// export slider as animation
		exportAnimationAction = new AbstractAction(
				app.getPlain("ExportAnimatedGIF") + " ...") {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					new org.geogebra.desktop.export.AnimationExportDialog(app);
				} catch (Exception ex) {
					Log.debug("AnimationExportDialog not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		exportPSTricksAction = new AbstractAction(
				app.getPlain("DrawingPadAsPSTricks") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					GeoGebraToPstricksD export = new GeoGebraToPstricksD(app);
					new org.geogebra.desktop.export.pstricks.PstricksFrame(export);
				} catch (Exception ex) {
					Log.debug("GeoGebraToPstricks not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
		// Added By Loic Le Coq
		exportPgfAction = new AbstractAction(app.getPlain("DrawingPagAsPGF")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					GeoGebraToPgfD export = new GeoGebraToPgfD(app);
					new org.geogebra.desktop.export.pstricks.PgfFrame(export);
				} catch (Exception ex) {
					Log.debug("GeoGebraToPGF not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		// Added by Andy Zhu; Asymptote export
		exportAsymptoteAction = new AbstractAction(
				app.getPlain("GraphicsViewAsAsymptote") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				try {
					org.geogebra.common.export.pstricks.GeoGebraToAsymptote export = new GeoGebraToAsymptoteD(
							app);
					new org.geogebra.desktop.export.pstricks.AsymptoteFrame(export);
				} catch (Exception ex) {
					Log.debug("GeoGebraToAsymptote not available");
				} catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		// End

		exportWorksheet = new AbstractAction(
				app.getPlain("DynamicWorksheetAsWebpage") + " ("
						+ FileExtensions.HTML + ") ...",
				app.getMenuIcon("text-html.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {

					Thread runner = new Thread() {
						@Override
						public void run() {

							app.setWaitCursor();
							try {
								app.getSelectionManager().clearSelectedGeos(
										true, false);
								app.updateSelection(false);
								org.geogebra.desktop.export.WorksheetExportDialog d = new org.geogebra.desktop.export.WorksheetExportDialog(
										app);

								d.setVisible(true);
							} catch (Exception e1) {
								Log.debug("WorksheetExportDialog not available");
								e1.printStackTrace();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};

		exportGeoGebraTubeAction = new AbstractAction(
				app.getMenu("UploadGeoGebraTube") + " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {

					Thread runner = new Thread() {
						@Override
						public void run() {

							app.setWaitCursor();
							try {
								app.getSelectionManager().clearSelectedGeos(
										true, false);
								app.updateSelection(false);

								// callback for 3D
								app.uploadToGeoGebraTubeOnCallback();

							} catch (Exception e1) {
								Log.debug("Uploading failed");
								e1.printStackTrace();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}

				catch (java.lang.NoClassDefFoundError ee) {
					app.showError("ExportJarMissing");
					ee.printStackTrace();
				}
			}
		};
	}

	@Override
	public void update() {
		//

	}

	public void renderEvent(BaseEvent event) {
		if (event instanceof TubeAvailabilityCheckEvent) {
			TubeAvailabilityCheckEvent checkEvent = (TubeAvailabilityCheckEvent) event;
			if (!checkEvent.isAvailable()) {
				remove(loadURLMenuItem);
			} else {
				loadURLAction.setEnabled(true);
			}
		}
	}

}
