package org.geogebra.common.main.settings;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

public class AppConfigGraphing implements AppConfig {

	@Override
	public void adjust(DockPanelData dp) {
		if (dp.getViewId() == App.VIEW_ALGEBRA) {
			dp.makeVisible();
			dp.setLocation("3");
		}
		else if (dp.getViewId() == App.VIEW_EUCLIDIAN) {
			dp.makeVisible();
			dp.setLocation("1");
		}

	}

	@Override
	public String getAVTitle() {
		return "Algebra";
	}

	@Override
	public int getLineDisplayStyle() {
		return GeoLine.EQUATION_EXPLICIT;
	}

	@Override
	public String getAppTitle() {
		return "GraphingCalculator";
	}

}
