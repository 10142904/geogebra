package geogebra.common.gui.layout;

import geogebra.common.euclidian.GetViewId;
import geogebra.common.gui.SetLabels;

public abstract class DockManager implements SetLabels{

	public abstract GetViewId getFocusedEuclidianPanel();

	public abstract boolean setFocusedPanel(int panel);
	
	public abstract void unRegisterPanel(DockPanel dockPanel);

	public abstract DockPanel getPanel(int ViewId);

	public void resizePanels() {
		// TODO Auto-generated method stub
		
	}
}
