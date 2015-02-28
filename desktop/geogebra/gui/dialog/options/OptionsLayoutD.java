package geogebra.gui.dialog.options;

import geogebra.common.gui.SetLabels;
import geogebra.common.main.settings.Settings;
import geogebra.gui.util.FullWidthLayout;
import geogebra.gui.util.LayoutUtil;
import geogebra.gui.util.LayoutUtil.TitlePanel;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Layout options for the options dialog.
 */
public class OptionsLayoutD extends
		geogebra.common.gui.dialog.options.OptionsLayout implements
		OptionPanelD, ActionListener, FocusListener, SetLabels {

	private AppD app;
	private Settings settings;

	/** */
	private JPanel sideBarPanel, inputBarPanel, toolbarPanel, navbarPanel,
			perspectivesPanel;

	/**	 */
	private JCheckBox ckShowInputHelp, ckIgnoreDocumentLayout, ckShowTitleBar,
			ckAllowStyleBar, ckShowInputBar, ckShowToolbar, ckShowToolHelp,
			ckShowMenuBar,
			ckShowSideBar;

	private JToggleButton rbToolbarNorth, rbToolbarSouth, rbToolbarEast,
			rbToolbarWest, rbSidebarWest, rbSidebarEast;
	
	private JRadioButton rbPespectiveSidebar,
	rbButtonSidebar;
	
	private JToggleButton rbInputBarSouth, rbInputBarNorth;

	private JLabel lblInputBarPosition, lblSidebarPosition;

	/** */
	private ButtonGroup inputbarPosGroup, toolBarPosGroup;

	private TitlePanel menuBarPanel;

	private JPanel wrappedPanel;

	/**
	 * Construct layout option panel.
	 * 
	 * @param app
	 */
	public OptionsLayoutD(AppD app) {
		this.wrappedPanel = new JPanel(new BorderLayout());

		this.app = app;
		this.settings = app.getSettings();

		initGUI();
		updateGUI();
		setLabels();

	}

	public void reinit() {
		wrappedPanel.removeAll();
		initGUI();
		updateGUI();
		setLabels();

	}
	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of
	 *         calling setLabels()
	 */
	private void initGUI() {

		initInputbarPanel();
		initPerspectivesPanel();
		initToolBarPanel();
		initMenuBarPanel();
		initSideBarPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());
		panel.add(inputBarPanel);
		panel.add(toolbarPanel);
		panel.add(perspectivesPanel);
		//panel.add(menuBarPanel);
		panel.add(sideBarPanel);

		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(4);
		// scrollPane.setBorder(BorderFactory.createEmptyBorder());

		wrappedPanel.add(scrollPane, BorderLayout.CENTER);

	}

	/**
	 * Initialize the perspectives (view) panel.
	 */
	private void initPerspectivesPanel() {

		perspectivesPanel = new JPanel();
		perspectivesPanel.setLayout(new BoxLayout(perspectivesPanel,
				BoxLayout.Y_AXIS));

		ckShowTitleBar = new JCheckBox();
		ckShowTitleBar.addActionListener(this);
		perspectivesPanel.add(LayoutUtil.flowPanel(ckShowTitleBar));

		ckAllowStyleBar = new JCheckBox();
		ckAllowStyleBar.addActionListener(this);
		perspectivesPanel.add(LayoutUtil.flowPanel(ckAllowStyleBar));

	}


	/**
	 * Initialize the input bar panel.
	 */
	private void initInputbarPanel() {

		inputBarPanel = new JPanel();
		inputBarPanel.setLayout(new BoxLayout(inputBarPanel, BoxLayout.Y_AXIS));

		ckShowInputBar = new JCheckBox();
		ckShowInputBar.addActionListener(this);

		int tab = 20;

		inputbarPosGroup = new ButtonGroup();

		rbInputBarNorth = new JToggleButton(
				app.getScaledIcon("layout_north.png"));
		rbInputBarNorth.addActionListener(this);
		inputbarPosGroup.add(rbInputBarNorth);

		rbInputBarSouth = new JToggleButton(
				app.getScaledIcon("layout_south.png"));
		rbInputBarSouth.addActionListener(this);
		inputbarPosGroup.add(rbInputBarSouth);

		lblInputBarPosition = new JLabel();

		inputBarPanel
				.add(LayoutUtil.flowPanel(ckShowInputBar,
						Box.createHorizontalStrut(5), rbInputBarNorth,
						rbInputBarSouth));

		ckShowInputHelp = new JCheckBox();
		ckShowInputHelp.addActionListener(this);
		inputBarPanel.add(LayoutUtil.flowPanel(tab, ckShowInputHelp));

	}

	/**
	 * Initialize the sidebar panel.
	 */
	private void initSideBarPanel() {

		sideBarPanel = new JPanel();
		sideBarPanel.setLayout(new BoxLayout(sideBarPanel, BoxLayout.Y_AXIS));

		ckShowSideBar = new JCheckBox();
		ckShowSideBar.addActionListener(this);

		int tab = 20;

		ButtonGroup grp = new ButtonGroup();
		rbSidebarWest = new JToggleButton(app.getScaledIcon("layout_west.png"));
		rbSidebarWest.addActionListener(this);
		grp.add(rbSidebarWest);
		rbSidebarEast = new JToggleButton(app.getScaledIcon("layout_east.png"));
		rbSidebarEast.setSelected(true);
		rbSidebarEast.addActionListener(this);
		grp.add(rbSidebarEast);

		ButtonGroup grp2 = new ButtonGroup();
		rbPespectiveSidebar = new JRadioButton();
		rbPespectiveSidebar.addActionListener(this);
		rbPespectiveSidebar.setSelected(true);
		grp2.add(rbPespectiveSidebar);
		rbButtonSidebar = new JRadioButton();
		rbButtonSidebar.addActionListener(this);
		grp2.add(rbButtonSidebar);

		lblSidebarPosition = new JLabel();

		sideBarPanel.add(LayoutUtil.flowPanel(ckShowSideBar,
				Box.createHorizontalStrut(5), rbSidebarWest, rbSidebarEast));
	
		// Don't show perspective/viewButton option (saved for 5.0 development)
		//sideBarPanel.add(OptionsUtil.flowPanel(tab, rbPespectiveSidebar,
			//	rbButtonSidebar));
	}

	/**
	 * Initialize the menu bar panel.
	 */
	private void initMenuBarPanel() {

		menuBarPanel = new LayoutUtil.TitlePanel();

		ckShowMenuBar = new JCheckBox();
		ckShowMenuBar.addActionListener(this);
		menuBarPanel.add(LayoutUtil.flowPanel(ckShowMenuBar));
	}

	/**
	 * Initialize the tool bar panel.
	 */
	private void initToolBarPanel() {

		toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));

		ckShowToolbar = new JCheckBox();
		ckShowToolbar.addActionListener(this);

		ckShowToolHelp = new JCheckBox();
		ckShowToolHelp.addActionListener(this);

		// TODO need to create a method to set a flag for this in the gui
		// manager
		ckShowToolHelp.setSelected(true);

		int tab = 20;

		toolBarPosGroup = new ButtonGroup();

		rbToolbarNorth = new JToggleButton(
				app.getScaledIcon("layout_north.png"));
		rbToolbarNorth.addActionListener(this);
		toolBarPosGroup.add(rbToolbarNorth);

		rbToolbarSouth = new JToggleButton(
				app.getScaledIcon("layout_south.png"));
		rbToolbarSouth.addActionListener(this);
		toolBarPosGroup.add(rbToolbarSouth);

		rbToolbarEast = new JToggleButton(app.getScaledIcon("layout_east.png"));
		rbToolbarEast.addActionListener(this);
		toolBarPosGroup.add(rbToolbarEast);

		rbToolbarWest = new JToggleButton(app.getScaledIcon("layout_west.png"));
		rbToolbarWest.addActionListener(this);
		toolBarPosGroup.add(rbToolbarWest);

		lblInputBarPosition = new JLabel();
		toolbarPanel.add(LayoutUtil.flowPanel(ckShowToolbar,
				Box.createHorizontalStrut(5), rbToolbarNorth,
				Box.createHorizontalStrut(5), rbToolbarSouth,
				Box.createHorizontalStrut(5), rbToolbarWest,
				Box.createHorizontalStrut(5), rbToolbarEast));
		toolbarPanel.add(LayoutUtil.flowPanel(tab, ckShowToolHelp));

	}

	private void updateIcons() {
		rbToolbarNorth.setIcon(app.getScaledIcon("layout_north.png"));
		rbToolbarSouth.setIcon(app.getScaledIcon("layout_south.png"));
		rbToolbarEast.setIcon(app.getScaledIcon("layout_east.png"));
		rbToolbarWest.setIcon(app.getScaledIcon("layout_west.png"));

	}
	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {

		ckShowInputBar.setSelected(app.showAlgebraInput());

		ckShowInputHelp.setSelected(app.showInputHelpToggle());

		rbToolbarNorth
				.setSelected(app.getToolbarPosition() == SwingConstants.NORTH);
		rbToolbarSouth
				.setSelected(app.getToolbarPosition() == SwingConstants.SOUTH);
		rbToolbarWest
				.setSelected(app.getToolbarPosition() == SwingConstants.WEST);
		rbToolbarEast
				.setSelected(app.getToolbarPosition() == SwingConstants.EAST);

		rbInputBarNorth.setSelected(app.showInputTop());
		rbInputBarSouth.setSelected(!app.showInputTop());
		ckShowToolbar.setSelected(app.showToolBar());
		ckShowToolHelp.setSelected(app.showToolBarHelp());

		rbToolbarNorth.setEnabled(ckShowToolbar.isSelected());
		rbToolbarSouth.setEnabled(ckShowToolbar.isSelected());
		rbToolbarEast.setEnabled(ckShowToolbar.isSelected());
		rbToolbarWest.setEnabled(ckShowToolbar.isSelected());

		rbInputBarNorth.setEnabled(ckShowInputBar.isSelected());
		rbInputBarSouth.setEnabled(ckShowInputBar.isSelected());


		// ckIgnoreDocumentLayout.setSelected(settings.getLayout()
		// .isIgnoringDocumentLayout());
		ckShowTitleBar.setSelected(settings.getLayout().showTitleBar());
		ckAllowStyleBar.setSelected(settings.getLayout().isAllowingStyleBar());

		ckShowMenuBar.setSelected(app.showMenuBar());

		ckShowSideBar.removeActionListener(this);
		ckShowSideBar.setSelected(app.isShowDockBar());
		ckShowSideBar.addActionListener(this);

		rbSidebarEast.removeActionListener(this);
		rbSidebarWest.removeActionListener(this);
		rbButtonSidebar.removeActionListener(this);
		rbPespectiveSidebar.removeActionListener(this);
		
		rbSidebarEast.setSelected(app.getDockBar().isEastOrientation());
		rbButtonSidebar.setSelected(app.getDockBar().isShowButtonBar());

		rbSidebarEast.addActionListener(this);
		rbSidebarWest.addActionListener(this);
		rbButtonSidebar.addActionListener(this);
		rbPespectiveSidebar.addActionListener(this);
		revalidate();

	}


	// needed updating things on the reset defaults button
	public void updateAfterReset() {

	}

	/**
	 * Values changed.
	 */
	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		// dock panel (perspective) settings
		if (source == ckShowTitleBar) {
			settings.getLayout().setShowTitleBar(ckShowTitleBar.isSelected());
		} else if (source == ckIgnoreDocumentLayout) {
			settings.getLayout().setIgnoreDocumentLayout(
					ckIgnoreDocumentLayout.isSelected());
		} else if (source == ckAllowStyleBar) {
			settings.getLayout().setAllowStyleBar(ckAllowStyleBar.isSelected());

			// tool bar settings
		} else if (source == ckShowToolbar || source == ckShowToolHelp) {
			app.setShowToolBar(ckShowToolbar.isSelected(),
					ckShowToolHelp.isSelected());
			app.updateApplicationLayout();
			app.updateToolBarLayout();
			app.getGuiManager().updateToolbar();
		} else if (source == rbToolbarNorth) {
			app.setToolbarPosition(SwingConstants.NORTH, true);
		} else if (source == rbToolbarSouth) {
			app.setToolbarPosition(SwingConstants.SOUTH, true);
		} else if (source == rbToolbarEast) {
			app.setToolbarPosition(SwingConstants.EAST, true);
		} else if (source == rbToolbarWest) {
			app.setToolbarPosition(SwingConstants.WEST, true);
		}

		// input bar settings
		else if (source == rbInputBarNorth) {
			app.setShowInputTop(true, true);
		} else if (source == rbInputBarSouth) {
			app.setShowInputTop(false, true);
		} else if (source == ckShowInputBar) {
			app.setShowAlgebraInput(ckShowInputBar.isSelected(), true);
		} else if (source == ckShowInputHelp) {
			app.setShowInputHelpToggle(ckShowInputHelp.isSelected());
		}

		// menubar settings
		else if (source == ckShowMenuBar) {
			app.setShowMenuBar(ckShowMenuBar.isSelected());
			app.getGuiManager().updateMenuBarLayout();
		}

		// sidebar settings
		else if (source == ckShowSideBar) {
			app.setShowDockBar(ckShowSideBar.isSelected());
		}
		else if (source == rbButtonSidebar || source == rbPespectiveSidebar) {
			app.getDockBar().setShowButtonBar(rbButtonSidebar.isSelected());
		}
		else if (source == rbSidebarEast || source == rbSidebarWest) {
			app.setDockBarEast(rbSidebarEast.isSelected());
			app.setShowDockBar(ckShowSideBar.isSelected());
		}
		
		

		wrappedPanel.requestFocus();
		updateGUI();

	}

	/**
	 * Not implemented.
	 */
	public void focusGained(FocusEvent e) {
	}

	/**
	 * Apply textfield changes.
	 */
	public void focusLost(FocusEvent e) {

	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {

		// input bar panel
		inputBarPanel.setBorder(LayoutUtil.titleBorder(app
				.getMenu("InputField")));
		ckShowInputBar.setText(app.getMenu("Show"));
		ckShowInputHelp.setText(app.getMenu("CmdList"));
		lblInputBarPosition.setText(app.getMenu("Position"));

		// tool bar panel
		toolbarPanel.setBorder(LayoutUtil.titleBorder(app.getMenu("Toolbar")));
		ckShowToolbar.setText(app.getMenu("Show"));
		ckShowToolHelp.setText(app.getMenu("ShowToolBarHelp"));

		// perspectives panel
		perspectivesPanel
				.setBorder(LayoutUtil.titleBorder(app.getMenu("View")));
		// ckIgnoreDocumentLayout.setText(app.getPlain("IgnoreDocumentLayout"));
		ckShowTitleBar.setText(app.getPlain("ShowTitleBar"));
		ckAllowStyleBar.setText(app.getPlain("AllowStyleBar"));


		// menu bar panel
		menuBarPanel.setTitle(app.getPlain("Miscellaneous"));
		ckShowMenuBar.setText(app.getMenu("ShowMenuBar"));

		// side bar panel
		sideBarPanel
				.setBorder(LayoutUtil.titleBorder(app.getMenu("Sidebar")));
		ckShowSideBar.setText(app.getMenu("ShowSidebar"));
		rbPespectiveSidebar.setText(app.getMenu("PerspectivePanel"));
		rbButtonSidebar.setText(app.getMenu("ViewPanel"));

	}

	public JPanel getWrappedPanel() {
		return this.wrappedPanel;
	}

	public void revalidate() {
		getWrappedPanel().revalidate();

	}

	public void setBorder(Border border) {
		this.wrappedPanel.setBorder(border);
	}

	public void applyModifications() {
		// override this method to make the properties view apply modifications
		// when panel changes
	}

	public void updateFont() {

		Font font = app.getPlainFont();

		// input bar panel
		inputBarPanel.setFont(font);
		ckShowInputBar.setFont(font);
		ckShowInputHelp.setFont(font);
		lblInputBarPosition.setFont(font);

		// tool bar panel
		toolbarPanel.setFont(font);
		ckShowToolbar.setFont(font);
		ckShowToolHelp.setFont(font);

		// perspectives panel
		perspectivesPanel.setFont(font);
		ckShowTitleBar.setFont(font);
		ckAllowStyleBar.setFont(font);


		// menu bar panel
		menuBarPanel.setFont(font);
		ckShowMenuBar.setFont(font);

		// sidebar panel
		sideBarPanel.setFont(font);
		ckShowSideBar.setFont(font);

		reinit();
	}

	public void setSelected(boolean flag) {
		// see OptionsEuclidianD for possible implementation
	}
}
