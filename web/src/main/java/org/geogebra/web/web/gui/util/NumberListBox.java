package org.geogebra.web.web.gui.util;

import org.geogebra.common.main.App;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.advanced.client.datamodel.ListDataModel;

public abstract class NumberListBox extends ComboBoxW {
	private ListDataModel model;

	public NumberListBox(App app) {
		super((AppW) app);
		model = getModel();
		model.add("1", "1"); //pi
		model.add(Unicode.PI_STRING, Unicode.PI_STRING); // pi
		model.add(Unicode.PI_STRING + "/2", Unicode.PI_STRING + "/2"); // pi/2
	}
	
	public void setDoubleValue(Double value) {
		String valStr = value.toString();
		for (int idx = 0; idx < getItemCount(); idx++) {
			if (getModel().get(idx).equals(valStr)) {
				setSelectedIndex(idx);
				break;
			}
		}
	}

}
