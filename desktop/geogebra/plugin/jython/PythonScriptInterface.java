package geogebra.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.Application;

/**
 * @author arno
 * pyggb.Interface implements this interface so that its methods can be called
 * from PythonBridge (see PythonBridge implementation).
 */
public interface PythonScriptInterface {
	
	/**
	 * @param app the application object
	 */
	public void init(Application app);
	
	/**
	 * This method is called every time an event is triggered on a GeoElement
	 * @param eventType string describing the type of the event
	 * @param eventTarget geo that is the target of the event
	 */
	public void handleEvent(String eventType, GeoElement eventTarget);
	
	/**
	 * This method is called every time a geo is selected
	 * @param geo the selected geo
	 * @param addToSelection true if it was added to the current selection
	 */
	public void notifySelected(GeoElement geo, boolean addToSelection);
	
	/**
	 * Open / close the Python window
	 */
	public void toggleWindow();
	
	/**
	 * Check the visibility of the Python window
	 * @return true if the Python window is currently visible
	 */
	public boolean isWindowVisible();
	
	/**
	 * Run a Python script
	 * @param script script to execute
	 */
	public void execute(String script);

	/**
	 * Set Python event listener
	 * @param geo target of the event listener
	 * @param evtType event type ("update", "click"...)
	 * @param code Python code to execute
	 */
	public void setEventListener(GeoElement geo, String evtType, String code);
}
