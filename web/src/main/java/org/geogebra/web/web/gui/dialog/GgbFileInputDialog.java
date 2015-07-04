package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;

public class GgbFileInputDialog extends FileInputDialog{

	public GgbFileInputDialog(AppW app, GeoPoint location) {
	    super(app, location);
	    createGUI();
    }
	
	protected void createGUI(){
		super.createGUI();
//		inputWidget.getElement().setPropertyString("accept", "application/zip");
//		inputWidget.getElement().setPropertyString("accept", "application/octet-stream");
		addGgbChangeHandler(getInputWidget().getElement(), app);
	}
	
	public native void addGgbChangeHandler(Element el, AppW appl) /*-{
		var dialog = this;
//		el.setAttribute("accept", "application/vnd.geogebra.file, application/vnd.geogebra.tool");
		el.onchange = function(event) {
			var files = this.files;
			if (files.length) {
				var fileToHandle = files[0];
				appl.@org.geogebra.web.html5.main.AppW::openFile(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle, dialog.@org.geogebra.web.web.gui.dialog.ImageFileInputDialog::getNativeHideAndFocus()());
			}

		};
	}-*/;

	
}
