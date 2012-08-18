package geogebra.gui.layout;

import geogebra.common.io.layout.Perspective;
import geogebra.gui.menubar.LanguageActionListener;
import geogebra.gui.menubar.OptionsMenuD;
import geogebra.main.AppD;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class PerspectivePanel extends JPopupMenu {

	private AppD app;
	private LayoutD layout;

	private JPanel btnPanel;

	private AbstractAction changePerspectiveAction, managePerspectivesAction,
			savePerspectiveAction;

	public PerspectivePanel(AppD app) {

		this.app = app;
		this.layout = app.getGuiManager().getLayout();
		setupFlagLabel();
		initActions();
		initItems();
		// registerListeners();
	}

	private void registerListeners() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				hidePopup();
			}
		});
	}

	protected void hidePopup() {
		this.setVisible(false);
	}

	boolean flag = true;
	private AbstractAction setLanguageAction;

	public void setVisible(boolean b) {
		super.setVisible(b);
		// super.setVisible(b || flag);
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems() {

		// add(Box.createVerticalStrut(10));
		JLabel title = new JLabel(app.getMenu("Perspectives"));
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		title.setFont(app.getBoldFont());

		add(Box.createVerticalStrut(10));
		add(title);
		add(Box.createVerticalStrut(10));
		//addSeparator();
		Perspective[] defaultPerspectives = geogebra.common.gui.Layout.defaultPerspectives;

		for (int i = 0; i < defaultPerspectives.length; ++i) {
			JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
			tmpItem.setText(app.getMenu("Perspective."
					+ defaultPerspectives[i].getId()));
			tmpItem.setIcon(app.getEmptyIcon());
			tmpItem.setActionCommand("d" + i);
			if (defaultPerspectives[i].getIconString() != null) {
				tmpItem.setIcon(app.getImageIcon(defaultPerspectives[i]
						.getIconString()));
			} else {
				tmpItem.setIcon(app.getImageIcon("options-large.png"));
			}

			Dimension d = tmpItem.getMaximumSize();
			d.height = tmpItem.getPreferredSize().height;
			tmpItem.setMaximumSize(d);

			tmpItem.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
			add(tmpItem);
		}

		// user perspectives
		Perspective[] perspectives = layout.getPerspectives();

		if (perspectives.length != 0) {
			addSeparator();
			for (int i = 0; i < perspectives.length; ++i) {
				JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
				tmpItem.setText(perspectives[i].getId());
				tmpItem.setIcon(app.getEmptyIcon());
				tmpItem.setActionCommand(Integer.toString(i));
				tmpItem.setIcon(app.getImageIcon("options-large.png"));

				Dimension d = tmpItem.getMaximumSize();
				d.height = tmpItem.getPreferredSize().height;
				tmpItem.setMaximumSize(d);

				tmpItem.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
				add(tmpItem);
			}
		}

		// JMenu subMenu = new JMenu(app.getMenuTooltip("Language"));
		// subMenu.setIcon(app.getFlagIcon(flagName));
		// subMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		// OptionsMenuD.addLanguageMenuItems(app, subMenu,
		// new LanguageActionListener(app));

		// add(subMenu);

		// add(Box.createVerticalGlue());

	}

	String flagName;

	JLabel languageLabel;

	private void setupFlagLabel() {

		flagName = app.getFlagName(false);

		languageLabel = new JLabel(app.getFlagIcon(flagName));
		languageLabel
				.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		AbstractAction setLanguageAction;
		languageLabel.setToolTipText(app.getMenuTooltip("Language"));
		languageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu myPopup = new JPopupMenu();
				OptionsMenuD.addLanguageMenuItems(app, myPopup,
						new LanguageActionListener(app));
				myPopup.setVisible(true);
				myPopup.show(languageLabel, 0, languageLabel.getHeight());
			}
		});
	}

	/**
	 * Initialize the actions.
	 */
	private void initActions() {

		final String flagName = app.getFlagName(false);

		setLanguageAction = new AbstractAction(app.getMenuTooltip("Language"),
				app.getFlagIcon(flagName)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		savePerspectiveAction = new AbstractAction(
				app.getMenu("SaveCurrentPerspective"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showSaveDialog();
			}
		};

		managePerspectivesAction = new AbstractAction(
				app.getMenu("ManagePerspectives"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				layout.showManageDialog();
			}
		};

		changePerspectiveAction = new AbstractAction() {
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				if (e.getActionCommand().startsWith("d")) {
					int index = Integer.parseInt(e.getActionCommand()
							.substring(1));
					layout.applyPerspective(geogebra.common.gui.Layout.defaultPerspectives[index]);
				} else {
					int index = Integer.parseInt(e.getActionCommand());
					layout.applyPerspective(layout.getPerspective(index));
				}

			}
		};
	}

}
