package org.geogebra.web.web.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.ContextMenuGeoElement;
import org.geogebra.common.gui.dialog.options.model.AngleArcSizeModel;
import org.geogebra.common.gui.dialog.options.model.ReflexAngleModel;
import org.geogebra.common.gui.dialog.options.model.ShowLabelModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.geos.Animatable;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.html5.AttachedToDOM;
import org.geogebra.web.web.javax.swing.GCheckBoxMenuItem;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author gabor
 * 
 *         ContextMenuGeoElement for Web
 *
 */
public class ContextMenuGeoElementW extends ContextMenuGeoElement
		implements AttachedToDOM {

	/**
	 * popup menu
	 */
	protected GPopupMenuW wrappedPopup;
	/**
	 * localization
	 */
	protected Localization loc;
	// private MenuItem mnuCopy;
	private MenuItem mnuCut;
	// private MenuItem mnuDuplicate;
	private MenuItem mnuPaste;
	private MenuItem mnuDelete;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuGeoElementW(AppW app) {
		super(app);
		this.app = app;
		this.loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
	}

	/**
	 * Creates new MyPopupMenu for GeoElement
	 * 
	 * @param app
	 *            application
	 * @param geos
	 *            selected elements
	 */
	public ContextMenuGeoElementW(AppW app, ArrayList<GeoElement> geos) {
		this(app);
		initPopup(geos);
	}

	/**
	 * @param geos
	 *            list of geos
	 */
	public void initPopup(ArrayList<GeoElement> geos) {
		wrappedPopup.clearItems();
		if (geos == null || geos.size() == 0) {
			return;
		}
		this.setGeos(geos);
		setGeo(geos.get(0));


		String title;

		if (geos.size() == 1) {
			title = getDescription(getGeo(), false);
		} else {
			title = loc.getMenu("Selection");
		}
		setTitle(title);

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		}
	}

	/**
	 * add oither items like special for lines and conics
	 */
	public void addOtherItems() {
		if (app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
			addCoordsModeItems();
			if (app.getSettings().getCasSettings().isEnabled()) {
				addLineItems();
				addConicItems();
				addNumberItems();
				addUserInputItem();
			}

		}

		// TODO remove the condition when ggb version >= 5
		if (app.getKernel().getManager3D() != null) {
			addPlaneItems();
		}

		if (wrappedPopup.getComponentCount() > 2 && !app.isWhiteboardActive()) {
			wrappedPopup.addSeparator();
		}
		addForAllItems();
	}

	/**
	 * @return true if has mow context menu feature
	 */
	protected boolean hasWhiteboardContextMenu() {
		return app.has(Feature.MOW_CONTEXT_MENU);
	}

	private void addForAllItems() {
		if (hasWhiteboardContextMenu()) {
			addRename();
			addEditItems();
			addObjectPropertiesMenu();
			addPinAndFixObject();
		}

		if (getGeo() == null) {
			return;
		}
		// SHOW, HIDE

		// G.Sturr 2010-5-14: allow menu to show spreadsheet trace for
		// non-drawables
		if (getGeo().isDrawable() || (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET))) {

			addShowObjectItem();
			addShowLabelItem();
			addTraceItem();
			addSpreadsheetTraceItem();
			addAnimationItem();
			addAuxiliaryItem();

			if (!hasWhiteboardContextMenu()) {
				addLock();
				addPin();
			}
			if (!app.isWhiteboardActive()) {
				wrappedPopup.addSeparator();
			}
		}

		if (!hasWhiteboardContextMenu()) {
			addRename();
		}

		// DELETE
		addDeleteItem();
		addPropertiesItem();
	}

	private void addPropertiesItem() {
		// if (isWhiteboard()) {
		// wrappedPopup.addSeparator();
		// addSelect();
		// addOrder();
		// }
		// Object properties menuitem
		if (app.showMenuBar() && app.letShowPropertiesDialog()
				&& getGeo().hasProperties()) {
			if (!hasWhiteboardContextMenu()) {
				wrappedPopup.addSeparator();
			}

			String img;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.gere().getSafeUri()
						.asString();
			} else {
				img = AppResources.INSTANCE.view_properties16().getSafeUri()
						.asString();
			}

			// open properties dialog
			addAction(new Command() {

				@Override
				public void execute() {
					openPropertiesDialogCmd();
				}
			}, MainMenu.getMenuBarHtml(img,
					app.isUnbundled() || hasWhiteboardContextMenu()
							? loc.getMenu("Settings")
							: loc.getMenu("Properties")),
					loc.getMenu("Properties"));
		}

	}

	private void addDeleteItem() {
		if (app.letDelete() && !getGeo().isProtected(EventType.REMOVE)
				&& !hasWhiteboardContextMenu()) {

			String img;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.delete_black()
						.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.delete_small().getSafeUri()
						.asString();
			}

			addAction(new Command() {

				@Override
				public void execute() {
					deleteCmd(false);
				}
			}, MainMenu.getMenuBarHtml(img, loc.getMenu("Delete")),
					loc.getMenu("Delete"));
		}

	}

	private void addAnimationItem() {
		if (getGeo().isAnimatable()) {
			GCheckBoxMenuItem cbItem;
			String img;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.play_black().getSafeUri()
						.asString();
			} else {
				img = AppResources.INSTANCE.empty().getSafeUri().asString();
			}

			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtml(img, loc.getMenu("Animation")),
					new Command() {

						@Override
						public void execute() {
							animationCmd();
						}
					}, true, app);
			cbItem.setSelected(((Animatable) getGeo()).isAnimating()
					&& app.getKernel().getAnimatonManager().isRunning());
			wrappedPopup.addItem(cbItem);
		}

	}

	private void addAuxiliaryItem() {
		GCheckBoxMenuItem cbItem;
		if (app.getGuiManager() != null
				&& app.getGuiManager().showView(App.VIEW_ALGEBRA)
				&& app.showAuxiliaryObjects() && getGeo().isAlgebraShowable()) {
			cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(
					AppResources.INSTANCE.aux_folder().getSafeUri().asString(),
					loc.getMenu("AuxiliaryObject")), new Command() {

						@Override
						public void execute() {
							showObjectAuxiliaryCmd();
						}
					}, true, app);
			cbItem.setSelected(getGeo().isAuxiliaryObject());
			wrappedPopup.addItem(cbItem);

		}

	}

	private void addSpreadsheetTraceItem() {
		if (getGeo().isSpreadsheetTraceable()
				&& app.getGuiManager().showView(App.VIEW_SPREADSHEET)) {
			GCheckBoxMenuItem cbItem;
			boolean showRecordToSpreadsheet = true;
			// check if other geos are recordable
			for (int i = 1; i < getGeos().size()
					&& showRecordToSpreadsheet; i++) {
				showRecordToSpreadsheet &= getGeos().get(i)
						.isSpreadsheetTraceable();
			}

			if (showRecordToSpreadsheet) {

				String img;

				if (app.isUnbundled() || hasWhiteboardContextMenu()) {
					img = MaterialDesignResources.INSTANCE
							.record_to_spreadsheet_black().getSafeUri()
							.asString();
				} else {
					img = AppResources.INSTANCE.spreadsheettrace().getSafeUri()
							.asString();
				}

				if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
						|| app.isUnbundled()) {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtml(img, ""),
							loc.getMenu("DontRecordToSpreadsheet"),
							loc.getMenu("RecordToSpreadsheet"), new Command() {
								@Override
								public void execute() {
									recordToSpreadSheetCmd();
								}
							}, true, app);
				} else {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtml(img,
									loc.getMenu("RecordToSpreadsheet")),
							new Command() {

								@Override
								public void execute() {
									recordToSpreadSheetCmd();
								}
							}, true, app);
				}
				cbItem.setSelected(getGeo().getSpreadsheetTrace());
				wrappedPopup.addItem(cbItem);
			}
		}

	}

	private void addTraceItem() {
		GCheckBoxMenuItem cbItem;
		if (getGeo().isTraceable()) {

			String img;

			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.trace_black()
						.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.trace_on().getSafeUri().asString();
			}

			if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU) && !app.isUnbundled()
					&& !hasWhiteboardContextMenu()) {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, "", true),
						loc.getMenu("HideTrace"), loc.getMenu("ShowTrace"),
						new Command() {

							@Override
							public void execute() {
								traceCmd();
							}
						}, true, app);
				cbItem.setSelected(isTracing());
				wrappedPopup.addItem(cbItem);
			} else if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("ShowTrace")),
						MaterialDesignResources.INSTANCE.check_black()
								.getSafeUri().asString(),
						isTracing());
				Command cmdTrace = new Command() {

					@Override
					public void execute() {
						traceCmd();
						cmItem.setChecked(isTracing());
					}
				};
				cmItem.setCommand(cmdTrace);
				wrappedPopup.addItem(cmItem);
			} else {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img,
						loc.getMenu("ShowTrace"), true), new Command() {

							@Override
							public void execute() {
								traceCmd();
							}
						}, true, app);
				cbItem.setSelected(((Traceable) getGeo()).getTrace());
				wrappedPopup.addItem(cbItem);
			}
		}

	}

	private void addShowLabelItem() {
		GCheckBoxMenuItem cbItem;
		if (!hasWhiteboardContextMenu() && getGeo().isLabelShowable()) {
			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtml(
							AppResources.INSTANCE.mode_showhidelabel_16()
									.getSafeUri().asString(),
							loc.getMenu("ShowLabel")),
					new Command() {

						@Override
						public void execute() {
							showLabelCmd();
						}
					}, true, app);
			cbItem.setSelected(isLabelShown());
			wrappedPopup.addItem(cbItem);
		}

	}

	private void addShowObjectItem() {
		GCheckBoxMenuItem cbItem;
		if (!app.isWhiteboardActive() && !app.isUnbundled()
				&& getGeo().isEuclidianShowable()
				&& getGeo().getShowObjectCondition() == null
				&& (!getGeo().isGeoBoolean() || getGeo().isIndependent())) {
			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtml(
							AppResources.INSTANCE.mode_showhideobject_16()
									.getSafeUri().asString(),
							loc.getMenu("ShowObject")),
					new Command() {

						@Override
						public void execute() {
							showObjectCmd();
						}
					}, true, app);
			cbItem.setSelected(getGeo().isSetEuclidianVisible());
			wrappedPopup.addItem(cbItem);

		}

	}

	private void addLock() {
		GCheckBoxMenuItem cbItem;
		if (getGeo().isFixable() && (getGeo().isGeoText()
				|| getGeo().isGeoImage() || getGeo().isGeoButton())) {

			String img = AppResources.INSTANCE.objectFixed().getSafeUri()
					.asString();

			if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
					|| app.isUnbundled()) {
				cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnlockObject"), loc.getMenu("LockObject"),
						new Command() {

							@Override
							public void execute() {
								fixObjectCmd();
							}
						}, true, app);
			} else {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("LockObject")),
						new Command() {

							@Override
							public void execute() {
								fixObjectCmd();
							}
						}, true, app);
			}

			cbItem.setSelected(getGeo().isLocked());
			wrappedPopup.addItem(cbItem);
		} else if (getGeo().isGeoNumeric()) {
			final GeoNumeric num = (GeoNumeric) getGeo();
			if (num.isSlider()) {

				String img = AppResources.INSTANCE.objectFixed().getSafeUri()
						.asString();

				if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
						|| app.isUnbundled()) {
					cbItem = new GCheckBoxMenuItem(
							MainMenu.getMenuBarHtml(img, ""),
							loc.getMenu("UnlockObject"),
							loc.getMenu("LockObject"), new Command() {

								@Override
								public void execute() {
									fixObjectNumericCmd(num);
								}
							}, true, app);
				} else {
					cbItem = new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(img,
							loc.getMenu("LockObject")), new Command() {

								@Override
								public void execute() {
									fixObjectNumericCmd(num);
								}
							}, true, app);
				}

				cbItem.setSelected(num.isSliderFixed());
				wrappedPopup.addItem(cbItem);
			}
		} else if (getGeo().isGeoBoolean()) {

			String img = AppResources.INSTANCE.objectFixed().getSafeUri()
					.asString();

			cbItem = new GCheckBoxMenuItem(
					MainMenu.getMenuBarHtml(img, loc.getMenu("FixCheckbox")),
					new Command() {

						@Override
						public void execute() {
							fixCheckboxCmd();
						}
					}, true, app);
			cbItem.setSelected(((GeoBoolean) getGeo()).isCheckboxFixed());
			wrappedPopup.addItem(cbItem);
		}

	}

	private void addRename() {
		if (getGeos() == null || !(getGeos().size() == 1 && app.letRename()
				&& getGeo().isRenameable()) || app.isUnbundled()
				|| hasWhiteboardContextMenu()) {
			return;
		}

		String img;
		if (!app.isUnbundled()) {
			img = AppResources.INSTANCE.rename20().getSafeUri().asString();
		} else if (app.isUnbundled()) {
			img = MaterialDesignResources.INSTANCE.rename_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.rename().getSafeUri().asString();
		}

		addAction(new Command() {

			@Override
			public void execute() {
				renameCmd();
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Rename")),
				loc.getMenu("Rename"));

		if (getGeos().size() == 1 && getGeo() instanceof TextValue
				&& !getGeo().isTextCommand()
				&& !getGeo().isProtected(EventType.UPDATE)) {

			String img2;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img2 = MaterialDesignResources.INSTANCE.edit_black()
						.getSafeUri().asString();
			} else {
				img2 = AppResources.INSTANCE.edit().getSafeUri().asString();
			}

			addAction(new Command() {

				@Override
				public void execute() {
					editCmd();
				}
			}, MainMenu.getMenuBarHtml(img2, loc.getMenu("Edit")),
					loc.getMenu("Edit"));
		}

	}

	private void addObjectPropertiesMenu() {
		if (!hasWhiteboardContextMenu()) {
			return;
		}

		GeoElement geo = getGeo();

		boolean showLabel = ShowLabelModel.match(geo);
		boolean angle = AngleArcSizeModel.match(geo);

		if (!(showLabel || angle)) {
			return;
		}
		if (!app.isWhiteboardActive()) {
		wrappedPopup.addSeparator();
		}

		// Label
		if (showLabel && !app.isUnbundled() && !hasWhiteboardContextMenu()) {

			String img;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.label_black()
						.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.mode_showhidelabel_16().getSafeUri()
						.asString();
			}
			addSubmenuAction(MainMenu.getMenuBarHtml(img, loc.getMenu("Label")),
					loc.getMenu("Label"), getLabelSubMenu());
		}

		// Angle
		if (angle) {

			String img;
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.angle_black()
						.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.stylingbar_angle_interval()
						.getSafeUri().asString();
			}
			addSubmenuAction(MainMenu.getMenuBarHtml(img, loc.getMenu("Angle")),
					loc.getMenu("Angle"), getAngleSubMenu());

		}
		// wrappedPopup.addSeparator();

	}

	private void addPinAndFixObject() {
		if (!hasWhiteboardContextMenu()) {
			return;
		}

		final GeoElement geo = getGeo();
		boolean pinnable = geo.isPinnable();
		boolean fixable = geo.isFixable();
		if (!(pinnable || fixable)) {
			return;
		}

		if (!app.isWhiteboardActive()) {
			wrappedPopup.addSeparator();
		}

		if (pinnable) {

			String img;
			final boolean pinned = geo.isPinned();

			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.pin_black().getSafeUri()
						.asString();
			} else {
				img = AppResources.INSTANCE.pin().getSafeUri().asString();
			}

			if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
					&& !app.isUnbundled() && !hasWhiteboardContextMenu()) {
				GCheckBoxMenuItem cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnpinFromScreen"),
						loc.getMenu("PinToScreen"), new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);
				cbItem.setSelected(pinned);
				wrappedPopup.addItem(cbItem);
			} else if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
						MainMenu.getMenuBarHtml(img,
								loc.getMenu("PinToScreen")),
						MaterialDesignResources.INSTANCE.check_black()
								.getSafeUri().asString(),
						pinned);
				Command cmdPin = new Command() {
					
					@Override
					public void execute() {
						pinCmd(pinned);
						cmItem.setChecked(pinned);
					}
				};
				cmItem.setCommand(cmdPin);
				wrappedPopup.addItem(cmItem);
			} else {
				addAction(new Command() {

					@Override
					public void execute() {
						pinCmd(pinned);
					}
				}, MainMenu.getMenuBarHtml(img, loc.getMenu("PinToScreen")),
						loc.getMenu("PinToScreen"));
			}
		}

		Command cmd = null;
		// change back to old name-> Fix instead of Lock
		String label = loc.getMenu("FixObject");
		if (fixable) {
			cmd = new Command() {

				@Override
				public void execute() {
					ArrayList<GeoElement> geoArray = new ArrayList<GeoElement>();
					geoArray.add(geo);
					EuclidianStyleBarStatic.applyFixObject(geoArray,
							!geo.isLocked(), app.getActiveEuclidianView());
				}
			};
		}

		if (cmd != null) {

			String img = "";
			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.lock_black().getSafeUri()
						.asString();
				final GCheckmarkMenuItem cmItem = new GCheckmarkMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("FixObject")),
						MaterialDesignResources.INSTANCE.check_black()
								.getSafeUri().asString(),
						geo.isLocked());
				Command cmdLock = new Command() {

					@Override
					public void execute() {
						ArrayList<GeoElement> geoArray = new ArrayList<GeoElement>();
						geoArray.add(geo);
						EuclidianStyleBarStatic.applyFixObject(geoArray,
								!geo.isLocked(), app.getActiveEuclidianView());
						cmItem.setChecked(geo.isLocked());
					}
				};
				cmItem.setCommand(cmdLock);
				wrappedPopup.addItem(cmItem);
			} else {
				img = AppResources.INSTANCE.objectFixed().getSafeUri()
						.asString();
			}

			if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
					&& !app.isWhiteboardActive()) {
				GCheckBoxMenuItem mi = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnfixObject"), loc.getMenu("FixObject"),
						cmd, true, app);
				mi.setSelected(getGeo().isLocked());
				wrappedPopup.addItem(mi);
			} else if (!app.isUnbundled() && !app.isWhiteboardActive()) {
				addAction(cmd, MainMenu.getMenuBarHtml(img, label), label);
			}
		}
		// wrappedPopup.addSeparator();
	}

	private void addEditItems() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {

			String img3;

			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img3 = MaterialDesignResources.INSTANCE.duplicate_black()
						.getSafeUri().asString();
			} else {
				img3 = AppResources.INSTANCE.empty().getSafeUri().asString();
			}
			addAction(new Command() {

				@Override
				public void execute() {
					app.setWaitCursor();
					duplicateCmd();
					app.setDefaultCursor();

				}
			}, MainMenu.getMenuBarHtml(img3, loc.getMenu("Duplicate"), true),
					loc.getMenu("Duplicate"));
			return;
		}

		if (!hasWhiteboardContextMenu()) {
			return;
		}

		if (!app.isWhiteboardActive()) {
			wrappedPopup.addSeparator();
		}

		final SelectionManager selection = app.getSelectionManager();

		String img;

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img = MaterialDesignResources.INSTANCE.cut_black().getSafeUri()
					.asString();
		} else {
			img = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		mnuCut = addAction(new Command() {

			@Override
			public void execute() {
				app.setWaitCursor();
				cutCmd();
				app.setDefaultCursor();
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Cut"), true),
				loc.getMenu("Cut"));

		String img2;

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img2 = MaterialDesignResources.INSTANCE.copy_black().getSafeUri()
					.asString();
		} else {
			img2 = GuiResources.INSTANCE.menu_icon_edit_copy().getSafeUri()
					.asString();
		}

		addAction(new Command() {

			@Override
			public void execute() {
				if (!selection.getSelectedGeos().isEmpty()) {
					app.setWaitCursor();
					app.getCopyPaste().copyToXML(app,
							selection.getSelectedGeos(), false);
					// initActions(); // app.updateMenubar(); - it's needn't to
					// // update the all menubar here
					app.setDefaultCursor();
				}
			}
		}, MainMenu.getMenuBarHtml(img2, loc.getMenu("Copy"), true),
				loc.getMenu("Copy"));

		String img3;
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img3 = MaterialDesignResources.INSTANCE.duplicate_black()
					.getSafeUri().asString();
		} else {
			img3 = AppResources.INSTANCE.empty().getSafeUri().asString();
		}

		addAction(new Command() {

			@Override
			public void execute() {
				app.setWaitCursor();
				duplicateCmd();
				app.setDefaultCursor();

			}
		}, MainMenu.getMenuBarHtml(img3, loc.getMenu("Duplicate"), true),
				loc.getMenu("Duplicate"));

		addPasteItem();

		String img4;

		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			img4 = MaterialDesignResources.INSTANCE.delete_black().getSafeUri()
					.asString();
		} else {
			img4 = AppResources.INSTANCE.delete_small().getSafeUri().asString();
		}

		mnuDelete = addAction(new Command() {

			@Override
			public void execute() {
				deleteCmd(false);
			}
		}, MainMenu.getMenuBarHtml(img4, loc.getMenu("Delete"), true),
				loc.getMenu("Delete"));

		updateEditItems();
	}

	/**
	 * add paste menu item
	 */
	protected void addPasteItem() {
		if (app.isUnbundled()) {
			return;
		}

		String img;

		if (hasWhiteboardContextMenu()) {
			img = MaterialDesignResources.INSTANCE.paste_black().getSafeUri()
					.asString();
		} else {
			img = GuiResources.INSTANCE.menu_icon_edit_paste().getSafeUri()
					.asString();
		}

		mnuPaste = addAction(new Command() {

			@Override
			public void execute() {
				if (!app.getCopyPaste().isEmpty()) {
					app.setWaitCursor();
					app.getCopyPaste().pasteFromXML(app, false);
					app.setDefaultCursor();
				}
			}
		}, MainMenu.getMenuBarHtml(img, loc.getMenu("Paste"), true),
				loc.getMenu("Paste"));
	}

	/**
	 * update paste menu item
	 */
	protected void updatePasteItem() {
		if (!app.isUnbundled() && mnuPaste != null) {
			mnuPaste.setEnabled(!app.getCopyPaste().isEmpty());
		}
	}

	/**
	 * update edit menu item
	 */
	protected void updateEditItems() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}
		if (!hasWhiteboardContextMenu()) {
			return;
		}

		boolean canDelete = app.letDelete()
				&& !getGeo().isProtected(EventType.REMOVE);
		mnuCut.setEnabled(canDelete);
		updatePasteItem();
		mnuDelete.setEnabled(canDelete);
	}

	private void addPin() {
		if (getGeo().isPinnable()) {

			String img;
			final boolean pinned = getGeo().isPinned();

			if (app.isUnbundled() || hasWhiteboardContextMenu()) {
				img = MaterialDesignResources.INSTANCE.pin_black()
								.getSafeUri().asString();
			} else {
				img = AppResources.INSTANCE.pin().getSafeUri().asString();
			}

			GCheckBoxMenuItem cbItem;
			
			if (app.has(Feature.MOW_IMPROVE_CONTEXT_MENU)
					|| app.isUnbundled()) {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, ""),
						loc.getMenu("UnpinFromScreen"),
						loc.getMenu("PinToScreen"),
						new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);				
			} else {
				cbItem = new GCheckBoxMenuItem(
						MainMenu.getMenuBarHtml(img, loc.getMenu("PinToScreen")),
						new Command() {

							@Override
							public void execute() {
								pinCmd(pinned);
							}
						}, true, app);
			}

			cbItem.setSelected(pinned);

			wrappedPopup.addItem(cbItem);

		}
	}

	private void addPlaneItems() {

		if (!(getGeo() instanceof ViewCreator)) {
			return;
		}

		Log.debug("==================== addPlaneItems");

		final ViewCreator plane = (ViewCreator) getGeo();

		Command action = new Command() {

			@Override
			public void execute() {
				plane.setView2DVisible(true);
				Log.debug("set plane visible : " + plane);
			}
		};
		addAction(action, null, app.getLocalization().getPlain("ShowAas2DView",
				getGeo().getLabelSimple()));

	}

	private void addUserInputItem() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}
		if (getGeo() instanceof GeoImplicit) {
			final GeoImplicit inputElement = (GeoImplicit) getGeo();
			if (inputElement.isValidInputForm()) {
				Command action;
				if (inputElement.isInputForm()) {
					action = new Command() {

						@Override
						public void execute() {
							extendedFormCmd(inputElement);
						}
					};
					addAction(action, null, loc.getMenu("ExtendedForm"));
				} else {
					action = new Command() {

						@Override
						public void execute() {
							inputFormCmd(inputElement);
						}
					};
					addAction(action, null, loc.getMenu("InputForm"));
				}

			}
		}

	}

	private void addNumberItems() {
		// no items
	}

	private void addConicItems() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}
		if (!(getGeo() instanceof GeoConic)) {
			return;
		}
		GeoConic conic = (GeoConic) getGeo();
		// there's no need to show implicit equation
		// if you can't select the specific equation
		boolean specificPossible = conic.isSpecificPossible();
		boolean explicitPossible = conic.isExplicitPossible();
		boolean vertexformPossible = conic.isVertexformPossible();
		boolean conicformPossible = conic.isConicformPossible();
		if (!(specificPossible || explicitPossible)) {
			return;
		}

		int mode = conic.getToStringMode();
		Command action;
		StringBuilder sb = new StringBuilder();

		if (mode != GeoConicND.EQUATION_IMPLICIT) {
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitConicEquation"));
			action = new Command() {

				@Override
				public void execute() {
					implicitConicEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (specificPossible && mode != GeoConicND.EQUATION_SPECIFIC) {
			// specific conic string
			String conicEqn = conic.getSpecificEquation();
			if (conicEqn != null) {
				sb.setLength(0);
				sb.append(loc.getMenu("Equation"));
				sb.append(' ');
				sb.append(conicEqn);
				action = new Command() {

					@Override
					public void execute() {
						equationConicEqnCmd();
					}
				};
				addAction(action, null, sb.toString());
			}
		}

		if (explicitPossible && mode != GeoConicND.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitConicEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationExplicitConicEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (vertexformPossible && mode != GeoConicND.EQUATION_VERTEX) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaVertexForm"));
			action = new Command() {

				@Override
				public void execute() {
					equationVertexEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (conicformPossible && mode != GeoConicND.EQUATION_CONICFORM) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ParabolaConicForm"));
			action = new Command() {

				@Override
				public void execute() {
					equationConicformEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}
	}

	private void addLineItems() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}
		if (!(getGeo() instanceof GeoLine)) {
			return;
		}
		if (getGeo() instanceof GeoSegment) {
			return;
		}

		GeoLine line = (GeoLine) getGeo();
		int mode = line.getMode();
		Command action;

		StringBuilder sb = new StringBuilder();

		if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ImplicitLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationImplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("ExplicitLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationExplicitEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

		if (mode != GeoLine.PARAMETRIC) {
			action = new Command() {

				@Override
				public void execute() {
					parametricFormCmd();
				}
			};
			addAction(action, null, loc.getMenu("ParametricForm"));
		}

		if (mode != GeoLine.EQUATION_GENERAL) {
			sb.setLength(0);
			sb.append(loc.getMenu("Equation"));
			sb.append(' ');
			sb.append(loc.getMenu("GeneralLineEquation"));
			action = new Command() {

				@Override
				public void execute() {
					equationGeneralLineEquationCmd();
				}
			};
			addAction(action, null, sb.toString());
		}

	}

	private void addCoordsModeItems() {
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			return;
		}

		if (!(getGeo() instanceof CoordStyle) || getGeo() instanceof GeoLine) {
			return;
		}

		if (getGeo().isProtected(EventType.UPDATE)) {
			return;
		}

		CoordStyle point = (CoordStyle) getGeo();
		int mode = point.getMode();
		Command action;

		switch (mode) {
		case Kernel.COORD_COMPLEX:
		default:
			return;

		// 2D coords styles
		case Kernel.COORD_POLAR:
			action = new Command() {

				@Override
				public void execute() {
					cartesianCoordsCmd();
				}
			};
			addAction(action, null, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN:
			action = new Command() {

				@Override
				public void execute() {
					polarCoorsCmd();
				}
			};
			addAction(action, null, loc.getMenu("PolarCoords"));
			break;

		// 3D coords styles
		case Kernel.COORD_SPHERICAL:
			action = new Command() {

				@Override
				public void execute() {
					cartesianCoords3dCmd();
				}
			};
			addAction(action, null, loc.getMenu("CartesianCoords"));
			break;

		case Kernel.COORD_CARTESIAN_3D:
			action = new Command() {

				@Override
				public void execute() {
					sphericalCoordsCmd();
				}
			};
			addAction(action, null, loc.getMenu("Spherical"));
			break;
		}

	}

	/**
	 * @param action
	 *            action to perform on click
	 * @param html
	 *            html string of menu item
	 * @param text
	 *            text of menu item
	 * @return new menu item
	 */
	protected MenuItem addAction(Command action, String html, String text) {
		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, action);
			if (!hasWhiteboardContextMenu()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new MenuItem(text, action);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		return mi; // TODO: need we this?
		// return wrappedPopup.add(action, html, text);
	}

	/**
	 * @param html
	 *            html string of superior menu item
	 * @param text
	 *            name of menu item
	 * @param subMenu
	 *            sub menu
	 */
	protected void addSubmenuAction(String html, String text, MenuBar subMenu) {
		MenuItem mi;
		if (html != null) {
			mi = new MenuItem(html, true, subMenu);
			if (!hasWhiteboardContextMenu()) {
				mi.addStyleName("mi_with_image"); // TEMP
			}
		} else {
			mi = new MenuItem(text, true, subMenu);
			mi.addStyleName("mi_no_image"); // TEMP
		}

		wrappedPopup.addItem(mi);
		// return mi; // TODO: need we this?
		// return wrappedPopup.add(action, html, text);
	}

	/**
	 * @param str
	 *            title of menu (first menu item)
	 */
	protected void setTitle(String str) {
		MenuItem title = new MenuItem(MainMenu.getMenuBarHtml(
				AppResources.INSTANCE.empty().getSafeUri().asString(), str),
				true, new Command() {

					@Override
					public void execute() {
						if (hasWhiteboardContextMenu()) {
							wrappedPopup.setVisible(true);
							wrappedPopup.setMenuShown(false);
						} else {
							wrappedPopup.setVisible(false);
						}
					}
				});
		if (app.isUnbundled() || hasWhiteboardContextMenu()) {
			title.addStyleName("no-hover");
		} else {
			title.addStyleName("menuTitle");
		}
		wrappedPopup.addItem(title);
		if (!hasWhiteboardContextMenu()) {
			wrappedPopup.addSeparator();
		}
	}

	/**
	 * @return popup
	 */
	public GPopupMenuW getWrappedPopup() {
		return wrappedPopup;
	}

	/**
	 * @param c
	 *            canvas
	 * @param x
	 *            coord
	 * @param y
	 *            coord
	 */
	public void show(Canvas c, int x, int y) {
		updateEditItems();
		wrappedPopup.show(c, x, y);
	}

	/**
	 * @param p
	 *            show in p's coord
	 */
	public void show(GPoint p) {
		updateEditItems();
		wrappedPopup.show(p);
	}

	// public void reInit(ArrayList<GeoElement> geos, GPoint location) {
	// initPopup((AppW) this.app, geos);
	// addOtherItems();
	// }

	@Override
	public void removeFromDOM() {
		getWrappedPopup().removeFromDOM();
	}

	private MenuBar getLabelSubMenu() {
		String[] labels = { loc.getMenu("stylebar.Hidden"), loc.getMenu("Name"),
				loc.getMenu("NameAndValue"), loc.getMenu("Value"),
				loc.getMenu("Caption") };

		MenuBar mnu = new MenuBar(true);
		// mnu.addStyleName("gwt-PopupPanel");
		// mnu.addStyleName("contextMenuSubmenu");
		GeoElement geos[] = { getGeo() };
		final ShowLabelModel model = new ShowLabelModel(app, null);
		model.setGeos(geos);
		for (int i = 0; i < labels.length; i++) {
			final int idx = i;
			MenuItem mi = new MenuItem(
					MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
							.getSafeUri().asString(), labels[i]),
					true, new Command() {

						@Override
						public void execute() {
							if (idx == 0) {
								model.applyModeChanges(GeoElement.LABEL_DEFAULT,
										false);
							} else {
								model.applyModeChanges(idx - 1, true);
							}
						}
					});
			mnu.addItem(mi);
		}
		return mnu;
	}

	private MenuBar getAngleSubMenu() {
		String[] angleIntervals = new String[GeoAngle.getIntervalMinListLength()
				- 1];
		for (int i = 0; i < GeoAngle.getIntervalMinListLength() - 1; i++) {
			angleIntervals[i] = app.getLocalization().getPlain(
					"AngleBetweenAB.short", GeoAngle.getIntervalMinList(i),
					GeoAngle.getIntervalMaxList(i));
		}

		MenuBar mnu = new MenuBar(true);
		// mnu.addStyleName("gwt-PopupPanel");
		// mnu.addStyleName("contextMenuSubmenu");
		GeoElement geos[] = { getGeo() };
		final ReflexAngleModel model = new ReflexAngleModel(app, false);
		model.setGeos(geos);

		for (int i = 0; i < angleIntervals.length; i++) {
			final int idx = i;
			MenuItem mi = new MenuItem(
					MainMenu.getMenuBarHtml(AppResources.INSTANCE.empty()
							.getSafeUri().asString(), angleIntervals[i]),
					true, new Command() {

						@Override
						public void execute() {
							model.applyChanges(idx);
						}
					});
			mnu.addItem(mi);
		}
		return mnu;
	}

	/**
	 * @return true if menu is shown
	 */
	public boolean isMenuShown() {
		return wrappedPopup.isMenuShown();
	}

	/**
	 * @param menuShown
	 *            true if menu is shown
	 */
	public void setMenuShown(boolean menuShown) {
		wrappedPopup.setMenuShown(menuShown);
	}

	/**
	 * update whole popup
	 */
	public void update() {
		initPopup(app.getActiveEuclidianView()
				.getEuclidianController().getAppSelectedGeos());
		addOtherItems();
	}
}
