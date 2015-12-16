package org.geogebra.desktop.gui.menubar;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;

/**
 * The "Options" menu.
 */
public class OptionsMenuD extends BaseMenu implements ActionListener,
		MyActionListener, MenuInterface {
	private static final long serialVersionUID = -8032696074032177289L;

	Kernel kernel;
	private AbstractAction
	// drawingPadPropAction,
			showOptionsAction,
			saveSettings, restoreDefaultSettings;

	private JMenu menuPointCapturing, menuLabeling, menuAlgebraStyle;

	public OptionsMenuD(AppD app) {
		super(app, app.getMenu("Options"));

		kernel = app.getKernel();
		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);
	}

	/**
	 * Initialize the menu items.
	 * 
	 * @param flag
	 */
	void initItems(ImageIcon flag) {
		final JMenu submenu;
		int pos;

		// G.Sturr 2009-10-18
		// Algebra description: show value or definition of objects
		getOptionsMenu().addAlgebraDescriptionMenu(this);
		getOptionsMenu().addDecimalPlacesMenu(this);
		addSeparator();
		// Labeling
		getOptionsMenu().addLabelingMenu(this);

		// add(drawingPadPropAction);

		addSeparator();

		getOptionsMenu().addFontSizeMenu(this);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); add(menuFontName); updateMenuFontName();
		 */

		// addSeparator();
		// Language
		if (app.getLocalization().propertiesFilesPresent()) {

			ImageIcon flagIcon;
			final String flagName = app.getFlagName();

			if (flag != null) {
				flagIcon = flag;
			} else {
				App.debug("using flag: " + flagName);
				flagIcon = app.getScaledFlagIcon(flagName);

			}

			LanguageActionListener langListener = new LanguageActionListener(
					app);
			final JMenu submenuLang = new JMenu(app.getMenu("Language"));
			// submenu.setIcon(app.getImageIcon("globe.png"));
			submenuLang.setIcon(flagIcon);
			addLanguageMenuItems(app, submenuLang, langListener);
			add(submenuLang);

			// check
			if (flag == null) {
				new Thread(new Runnable() {
					public void run() {

						String geoIPflagname = app.getFlagName();

						// fake for testing
						// geoIPflagname = "wales.png";

						if (!geoIPflagname.equals(flagName)) {
							App.debug("updating flag to: " + geoIPflagname);

							// rebuild menu with new flag
							removeAll();
							initItems(app.getFlagIcon(geoIPflagname));
						}
					}
				}).start();
			}

		}

		addSeparator();

		// advanced properties
		add(showOptionsAction);

		// doesn't work in applets
		if (!app.isApplet()) {
			addSeparator();

			// save settings
			add(saveSettings);

			// restore default settings
			add(restoreDefaultSettings);
		}

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			} else {
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
						app.getGUIFontSize()));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	/**
	 * Create a list with all languages which can be selected.
	 * 
	 * @param menu
	 * @param al
	 */
	public static void addLanguageMenuItems(AppD app, JComponent menu,
			ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		boolean rtl = app.getLocalization().isRightToLeftReadingOrder();
		JMenu submenu1 = new JMenu(rtl ? "D - A" : "A - D");
		JMenu submenu2 = new JMenu(rtl ? "I - E" : "E - I");
		JMenu submenu3 = new JMenu(rtl ? "Q - J" : "J - Q");
		JMenu submenu4 = new JMenu(rtl ? "Z - R" : "R - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		menu.add(submenu3);
		menu.add(submenu4);

		String currentLocale = app.getLocale().toString();

		// change en_GB into enGB
		currentLocale = currentLocale.replaceAll("_", "");
		StringBuilder sb = new StringBuilder(20);

		for (Language loc : Language.values()) {

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = loc.name;

			char ch = text.charAt(0);

			if (ch == Unicode.LeftToRightMark || ch == Unicode.RightToLeftMark) {
				ch = text.charAt(1);
			} else {
				// make sure brackets are correct in Arabic, ie not )US)
				sb.setLength(0);
				sb.append(Unicode.LeftToRightMark);
				sb.append(text);
				sb.append(Unicode.LeftToRightMark);
				text = sb.toString();
			}

			mi = new LanguageRadioButtonMenuItem(text);

			// make sure eg Malayalam, Georgian drawn OK (not in standard Java
			// font)
			mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
					app.getGUIFontSize()));

			if (loc.locale.equals(currentLocale)) {
				mi.setSelected(true);
			}
			mi.setActionCommand(loc.locale);
			mi.addActionListener(al);
			bg.add(mi);

			if (ch <= 'D')
				submenu1.add(mi);
			else if (ch <= 'I')
				submenu2.add(mi);
			else if (ch <= 'Q')
				submenu3.add(mi);
			else
				submenu4.add(mi);
		}
	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		// display the options dialog
		showOptionsAction = new AbstractAction(
				app.getMenu("Advanced") + " ...",
				app.getMenuIcon("view-properties16.png")) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(
						OptionType.ADVANCED, null);
			}
		};

		if (!app.isApplet()) {
			// save settings
			saveSettings = new AbstractAction(app.getMenu("Settings.Save"),
					app.getMenuIcon("document-save.png")) {
				@SuppressWarnings("hiding")
				public static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferencesD.getPref().saveXMLPreferences(app);
				}
			};

			// restore default settings
			restoreDefaultSettings = new AbstractAction(
					app.getMenu("Settings.ResetDefault"), app.getEmptyIcon()) {
				@SuppressWarnings("hiding")
				public static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {

					// set checkbox size to new default
					app.getEuclidianView1().setBooleanSize(
							EuclidianConstants.DEFAULT_CHECKBOX_SIZE);
					if (app.hasEuclidianView2(1)) {
						app.getEuclidianView2(1).setBooleanSize(
								EuclidianConstants.DEFAULT_CHECKBOX_SIZE);
					}

					// set sliders to new styling
					TreeSet<GeoElement> geos = app.getKernel().getConstruction()
							.getGeoSetConstructionOrder();
					Iterator<GeoElement> it = geos.iterator();
					while (it.hasNext()) {
						GeoElement geo = it.next();
						if (geo instanceof GeoNumeric
								&& ((GeoNumeric) geo).isSlider()) {
							GeoNumeric slider = (GeoNumeric) geo;
							slider.setAlphaValue(
									ConstructionDefaults.DEFAULT_NUMBER_ALPHA);
							slider.setLineThickness(
									GeoNumeric.DEFAULT_SLIDER_THICKNESS);
							slider.setSliderWidth(
									GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL);
							slider.updateRepaint();
						}
					}

					GeoGebraPreferencesD.getPref().clearPreferences(app);
					boolean oldAxisX = app.getSettings().getEuclidian(1)
							.getShowAxis(0);
					boolean oldAxisY = app.getSettings().getEuclidian(1)
							.getShowAxis(1);
					// reset defaults for GUI, views etc
					// this has to be called before load XML preferences,
					// in order to avoid overwrite
					app.getSettings().resetSettings();

					// for geoelement defaults, this will do nothing, so it is
					// OK here
					GeoGebraPreferencesD.getPref().loadXMLPreferences(app);
					app.getSettings().getEuclidian(1)
							.setShowAxes(oldAxisX, oldAxisY);
					// reset default line thickness etc
					app.getKernel().getConstruction().getConstructionDefaults()
							.resetDefaults();

					// reset defaults for geoelements; this will create brand
					// new objects
					// so the options defaults dialog should be reset later
					app.getKernel().getConstruction().getConstructionDefaults()
							.createDefaultGeoElements();
					app.setInputPositon(InputPositon.algebraView, false);
					// reset the stylebar defaultGeo
					if (app.getEuclidianView1().hasStyleBar())
						app.getEuclidianView1().getStyleBar()
								.restoreDefaultGeo();
					if (app.hasEuclidianView2EitherShowingOrNot(1))
						if (app.getEuclidianView2(1).hasStyleBar())
							app.getEuclidianView2(1).getStyleBar()
									.restoreDefaultGeo();
					app.getKernel().updateConstruction();
					// set default layout options
					app.setToolbarPosition(SwingConstants.NORTH, false);
					app.setShowToolBarNoUpdate(true);
					app.setShowToolBarHelpNoUpdate(false);
					app.setShowDockBar(true, false);
					app.setDockBarEast(true);
					app.updateContentPane();

				}
			};
		}
	}

	@Override
	public void update() {
		getOptionsMenu().update();
	}

	/**
	 * Update algebra style description (switch between value / definition /
	 * command).
	 */
	private void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			((JRadioButtonMenuItem) menuAlgebraStyle.getMenuComponent(kernel
					.getAlgebraStyle())).setSelected(true);
		}
	}

	/**
	 * Update the point capturing menu.
	 */
	private void updateMenuPointCapturing() {
		if (menuPointCapturing == null)
			return;

		String pos = Integer.toString(app.getActiveEuclidianView()
				.getPointCapturingMode());
		for (int i = 0; i < 4; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	// /**
	// * Update the menu with all decimal places.
	// */
	// private void updateMenuDecimalPlaces() {
	// if (menuDecimalPlaces == null)
	// return;
	// int pos = -1;
	//
	// if (kernel.useSignificantFigures) {
	// int figures = kernel.getPrintFigures();
	// if (figures > 0 && figures < App.figuresLookup.length)
	// pos = App.figuresLookup[figures];
	// } else {
	// int decimals = kernel.getPrintDecimals();
	//
	// if (decimals > 0 && decimals < App.decimalsLookup.length)
	// pos = App.decimalsLookup[decimals];
	//
	// }
	//
	// try {
	// ((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
	// .setSelected(true);
	// } catch (Exception e) {
	// //
	// }
	//
	// }

	/**
	 * Execute a performed action.
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		getOptionsMenu().processActionPerformed(cmd);
	}

	@Override
	protected void initItems() {
		initItems(null);
	}

	public void actionPerformed(String command) {
		getOptionsMenu().processActionPerformed(command);
	}

	private OptionsMenu getOptionsMenu() {
		return app.getOptionsMenu(new MenuFactory() {

			@Override
			public RadioButtonMenuBar newSubmenu() {
				return new RadioButtonMenuBarD(app);
			}
		});
	}

}
