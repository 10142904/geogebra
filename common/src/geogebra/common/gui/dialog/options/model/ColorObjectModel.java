package geogebra.common.gui.dialog.options.model;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoGebraColorConstants;

import java.awt.Color;


public class ColorObjectModel extends OptionsModel {
	public interface IColorObjectListener {

		void updateChooser(boolean equalObjColor,
				boolean equalObjColorBackground, boolean allFillable,
				boolean hasBackground,
				boolean hasAlpha);

		void updatePreview(GColor col, float alpha);

		boolean isBackgroundColorSelected();


		void updateNoBackground(GeoElement geo, GColor col, float alpha,
				boolean updateAlphaOnly, boolean allFillable);
	}

	private static final long serialVersionUID = 1L;
	private boolean allFillable;
	private boolean hasBackground;
	private boolean hasImageGeo;
	private IColorObjectListener listener;
	private Kernel kernel;
	private Color selectedColor;
	private static App app;
	public ColorObjectModel(App app, IColorObjectListener listener) {
		this.listener = listener;
		this.app = app;
		kernel = app.getKernel();
	}

	@Override
	public void updateProperties() {

		GeoElement geo0 = getGeoAt(0);


		// check geos for similar properties

		boolean equalObjColor = true;
		boolean equalObjColorBackground = true;
		hasImageGeo = geo0.isGeoImage();
		allFillable = geo0.isFillable();
		hasBackground = geo0.hasBackgroundColor();

		GeoElement temp;
		for (int i = 1; i < getGeosLength(); i++) {
			temp = getGeoAt(i);
			// same object color
			if (!geo0.getObjectColor().equals(temp.getObjectColor())) {
				equalObjColor = false;
			}
			// has fill color
			if (!temp.isFillable()) {
				allFillable = false;
			}
			// has background
			if (!temp.hasBackgroundColor()) {
				hasBackground = false;
			}
			// has image geo
			if (temp.isGeoImage()) {
				hasImageGeo = true;
			}
		}

		if (hasBackground) {
			equalObjColorBackground = true;

			if (geo0.getBackgroundColor() == null)
				// test for all null background color
				for (int i = 1; i < getGeosLength(); i++) {
					temp = getGeoAt(i);
					if (temp.getBackgroundColor() != null) {
						equalObjColorBackground = false;
						break;
					}
				}
			else
				// test for all same background color
				for (int i = 1; i < getGeosLength(); i++) {
					temp = getGeoAt(i);
					// same background color
					if (!geo0.getBackgroundColor().equals(
							temp.getBackgroundColor())) {
						equalObjColorBackground = false;
						break;
					}
				}
		}
		
		listener.updateChooser(equalObjColor, equalObjColorBackground, allFillable, hasBackground,
				hasOpacity(geo0));
	}

	protected boolean hasOpacity(GeoElement geo) {
		boolean hasOpacity = true;
		if (geo instanceof GeoButton) {
			hasOpacity = false;
		}
		return hasOpacity;
	}
	
	/**
	 * Sets color of selected GeoElements
	 */
	private void updateColor(GColor col, float alpha, boolean updateAlphaOnly) {
		if (col == null || getGeos() == null) {
			return;
		}	

		listener.updatePreview(col, alpha);
		
		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);

			if (hasBackground && listener.isBackgroundColorSelected()) {
				geo.setBackgroundColor(col);
			} else {
				listener.updateNoBackground(geo,col,alpha,updateAlphaOnly, allFillable);
			}
			
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
	}


	/**
	 * Sets the background color of selected GeoElements to null
	 */
	public void clearBackgroundColor() {

		GeoElement geo;
		for (int i = 0; i < getGeosLength(); i++) {
			geo = getGeoAt(i);
			geo.setBackgroundColor(null);
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
	}

	@Override
	public boolean checkGeos() {
		return true;
	}

	/**
	 * Listens for color chooser state changes
	 */
	public void applyChanges(GColor color, float alpha, boolean alphaOnly) {

		updateColor(color, alpha, alphaOnly);

	}

	public boolean hasImageGeo() {
		return hasImageGeo;
	}

	public boolean hasBackground() {
		return hasBackground;
	}

	@Override
	protected boolean isValidAt(int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static String toHex2(int num) {
		// GWT lacks of String.format() so it must be done old school
		if (num > 255) {
			return "-";
		}
		String chars = "0123456789ABCDEF";
		String result = "";
		int high = num / 16;
		int low = num % 16;
		if (high > 	0) {
			result += chars.charAt(high);
		} else {
			result = "0";
		}
		
		result += chars.charAt(low);
		return result;
	}

	public static String getColorAsString(GColor color) {
		String result = "";
		int blue = color.getBlue();
		String rgbDec = color.getRed() + ", " + color.getGreen() + ", " + blue;
		String name = GeoGebraColorConstants.getGeogebraColorName(app, color);
		if (name != null) {
			result = name + " " + rgbDec;
		} else {
			result = rgbDec;
		}
		
		result +=  " (#" + toHex2(color.getRed()) + toHex2(color.getGreen()) + toHex2(blue) + ")";
		return result;
	}
}
