package geogebra.html5.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.Previewable;
import geogebra.common.euclidian.event.PointerEventType;

import com.google.gwt.canvas.client.Canvas;

/**
 * interface for EuclidianViewW / EuclidianView3DW
 * 
 * @author mathieu
 *
 */
public interface EuclidianViewWInterface extends EuclidianViewInterfaceSlim {

	/**
	 * 
	 * @return canvas
	 */
	public Canvas getCanvas();

	public Hits getHits();

	public boolean hasStyleBar();

	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar();

	public int getViewWidth();

	/**
	 * @param p
	 *            event coords
	 * @return whether textfield was clicked
	 */
	public boolean textfieldClicked(int x, int y, PointerEventType type);

	public int getViewID();

	public double getXZero();

	public double getYZero();

	public double getXscale();

	public void setCoordSystem(double xZero, double yZero, double xscale,
	        double newRatioY);

	public double getYscale();

	public void rememberOrigins();

	public void translateCoordSystemInPixels(int i, int j, int k,
	        int modeTranslateview);

	public void setHits(GPoint gPoint, PointerEventType touch);

	public Previewable getPreviewDrawable();

	public void updatePreviewableForProcessMode();

}
