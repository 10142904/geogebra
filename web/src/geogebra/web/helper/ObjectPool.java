package geogebra.web.helper;

import geogebra.web.gui.app.GGWMenuBar;

public class ObjectPool {

	private GGWMenuBar ggwMenubar;

	public ObjectPool() {

	}

	public GGWMenuBar getGgwMenubar() {
		return ggwMenubar;
	}

	public void setGgwMenubar(GGWMenuBar ggwMenubar) {
		this.ggwMenubar = ggwMenubar;
	}

}
