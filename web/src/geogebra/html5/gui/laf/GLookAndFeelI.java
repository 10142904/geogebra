package geogebra.html5.gui.laf;

import geogebra.common.main.App;
import geogebra.html5.euclidian.EuclidianControllerW;
import geogebra.html5.main.AppW;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface GLookAndFeelI {
	public static final int COMMAND_LINE_HEIGHT = 43;
	public static final int TOOLBAR_HEIGHT = 53;
	boolean isSmart();

	String getType();

	Button getSignInButton(App app);

	boolean undoRedoSupported();

	MainMenuI getMenuBar(AppW app);

	void addWindowClosingHandler(AppW app);
	
	void removeWindowClosingHandler();

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean registerHandlers(Widget evPanel, EuclidianControllerW euclidiancontroller);

	boolean autosaveSupported();

	boolean exportSupported();

	boolean externalDriveSupported();
}
