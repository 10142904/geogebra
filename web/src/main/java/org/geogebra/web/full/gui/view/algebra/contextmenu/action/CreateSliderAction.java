package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;

public class CreateSliderAction extends MenuAction<GeoElement> {

	private LabelController labelController;

	public CreateSliderAction() {
		super("CreateSlider");
		labelController = new LabelController();
	}

	@Override
	public void execute(GeoElement geo, AppWFull app) {
		if (!(geo instanceof GeoNumeric)) {
			return;
		}
		labelController.ensureHasLabel(geo);
		((GeoNumeric) geo).setShowExtendedAV(true);
		geo.getKernel().storeUndoInfo();
	}

	@Override
	public boolean isAvailable(GeoElement geo) {
		return geo instanceof GeoNumeric && !((GeoNumeric) geo).isShowingExtendedAV();
	}
}
