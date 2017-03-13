package org.geogebra.web.web.util;

import org.geogebra.common.cas.view.CASTableCellEditor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.cas.view.CASLaTeXEditor;
import org.geogebra.web.web.cas.view.CASTableControllerW;
import org.geogebra.web.web.cas.view.CASTableW;
import org.geogebra.web.web.gui.view.algebra.CheckboxTreeItem;
import org.geogebra.web.web.gui.view.algebra.LatexTreeItem;
import org.geogebra.web.web.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.web.gui.view.algebra.SliderTreeItemRetex;

/**
 * Factory class for ReTeX based editor
 */
public class ReTeXHelper extends LaTeXHelper {


	@Override
	public CASTableCellEditor getCASEditor(CASTableW table, AppW app,
			CASTableControllerW ml) {
		return new CASLaTeXEditor(table, app, ml);
	}

	@Override
	public RadioTreeItem getAVItem(GeoElement ob) {
		return new LatexTreeItem(ob);
	}

	@Override
	public RadioTreeItem getAVInput(Kernel kernel) {
		return new LatexTreeItem(kernel);
	}

	@Override
	public boolean supportsAV() {
		return false;
	}

	@Override
	public RadioTreeItem getSliderItem(GeoElement ob) {
		return new SliderTreeItemRetex(ob);
	}

	@Override
	public RadioTreeItem getCheckboxItem(GeoElement ob) {
		return new CheckboxTreeItem(ob);
	}

}
