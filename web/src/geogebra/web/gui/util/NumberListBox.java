package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.gui.advanced.client.datamodel.ListDataModel;

public abstract class NumberListBox extends ComboBoxW {
	private static final String PI_STRING = "\u03c0";
	private App app;
	private ListDataModel model;
	public NumberListBox(App app) {		
		this.app = app;
		model = getModel();
		model.add("1", "1"); //pi
		model.add(PI_STRING, PI_STRING); //pi
		model.add(PI_STRING + "/2", PI_STRING + "/2"); //pi/2
	}

	public double getDoubleValue() {
		final String text = getValue().trim();
		if (text.equals("")) return Double.NaN;
		return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);			
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
