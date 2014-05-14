package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.plugin.EuclidianStyleConstants;

public class LineStyleModel extends OptionsModel {
	public interface ILineStyleListener {
		void setValue(int value);

		void setMinimum(int minimum);

		void selectCommonLineStyle(boolean equalStyle, int type);

		void setLineTypeVisible(boolean value); 
	}

	private ILineStyleListener listener;
	private boolean lineTypeEnabled; 

	private static Integer[] lineStyleArray=null;

	public static void initStyleArray() {
		if (lineStyleArray == null) {
			lineStyleArray = getLineTypes();
		}

	}
	public LineStyleModel(ILineStyleListener listener) {
		this.listener = listener;

	}

	private static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(EuclidianStyleConstants.LINE_TYPE_FULL),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_LONG),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DOTTED),
				new Integer(EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}

	public static final Integer getStyleAt(int i) {
		initStyleArray();
		return lineStyleArray[i];
	}

	public static final Integer getStyleCount() {
		initStyleArray();
		return lineStyleArray.length;
	}
	private int maxMinimumThickness() {

		if (!hasGeos()) {
			return 1;
		}

		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement testGeo = getGeoAt(i)
					.getGeoElementForPropertiesDialog();
			if (testGeo.getMinimumLineThickness() == 1) {
				return 1;
			}
		}

		return 0;

	}


	@Override
	public void updateProperties() {
		GeoElement temp, geo0 = getGeoAt(0);
		if (listener != null) {
			listener.setValue(geo0.getLineThickness());
			// allow polygons to have thickness 0
			listener.setMinimum(maxMinimumThickness());

			listener.setLineTypeVisible(lineTypeEnabled); 
		}
		// check if geos have same line style
		if (lineTypeEnabled) { 
			boolean equalStyle = true; 
			int type0 = geo0.getLineType(); 
			for (int i = 1; i < getGeosLength(); i++) { 
				temp = getGeoAt(i); 
				// same style? 
				if (type0 != temp.getLineType()) 
					equalStyle = false; 
			} 

			if (listener != null) { 
				listener.selectCommonLineStyle(equalStyle, type0); 
			} 		
		}

	}

	public void applyThickness(int value) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineThickness(value);
			geo.updateVisualStyleRepaint();
		}
	}


	public void applyLineType(int type) {
		for (int i = 0; i < getGeosLength(); i++) {
			GeoElement geo = getGeoAt(i);
			geo.setLineType(type);
			geo.updateVisualStyleRepaint();
		}
	}

	public void applyLineTypeFromIndex(int index) {
		applyLineType(lineStyleArray[index]);
	}

	@Override
	public boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index)
				.getGeoElementForPropertiesDialog();
		return geo.showLineProperties() || geo.isNumberValue();
	}

	@Override
	public boolean checkGeos() {
		boolean geosOK = true;
		lineTypeEnabled = true;
		for (int i = 0; i < getGeosLength(); i++) {
			if (!isValidAt(i)) {
				geosOK = false;

				break;
			}
			
			GeoElement geo = getGeoAt(i);
			if ((geo instanceof GeoNumeric) && ((GeoNumeric)geo).isSlider() ) {
			lineTypeEnabled = false;
			}
		}
		return geosOK;
	}
}
