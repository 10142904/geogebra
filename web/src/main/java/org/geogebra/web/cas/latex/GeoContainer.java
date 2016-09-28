package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;

/**
 * Object providing the corresponding geo
 */
public interface GeoContainer {
	/**
	 * @return corresponding geo
	 */
	public GeoElement getGeo();

	public boolean hideSuggestions();

	public boolean stopNewFormulaCreation(String input, String latex,
			AsyncOperation<Object> callback);

	public boolean popupSuggestions();

	public void showOrHideSuggestions();

	public void stopEditing(String latex,
 AsyncOperation<GeoElementND> callback);

	public Element getElement();

	public void scrollIntoView();

	public void shuffleSuggestions(boolean down);

	public App getApplication();

	public void typing(boolean heuristic);

	public void onKeyPress(String s);

	public void onBlur(BlurEvent be);

	public void onFocus(FocusEvent fe);

	public Element getScrollElement();

}