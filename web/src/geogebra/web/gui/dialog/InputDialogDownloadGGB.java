package geogebra.web.gui.dialog;

import geogebra.common.gui.view.algebra.DialogType;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;

public class InputDialogDownloadGGB extends InputDialogW{
	
	public InputDialogDownloadGGB(AppW app){
		super(true);
		this.app = app;
		
		//createGUI(app.getMenu("Download..."), null, false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);		
		createGUI(app.getMenu("OpenWebpage"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, true, false, false, DialogType.TextArea);
		this.btOK.addStyleName("downloadButton");
		wrappedPopup.center();
		inputPanel.getTextComponent().getTextField().setFocus(true);
		addEventListenerForDownloadButton();
	}
	
	private native void addEventListenerForDownloadButton() /*-{
		console.log("addEventListenerForDownlaodButton starts");
		var downloadButton = document.getElementById("downloadButton");
		console.log("addEventListenerForDownloadButton 1");
        downloadButton.addEventListener("click", $wnd.downloadggb.tempfunction, false);
        console.log("addEventListenerForDownloadButton 2");
	}-*/;
	
	public void onClick(ClickEvent e) {
	    Object source = e.getSource();

		try {
//			if (source == btOK || source == inputPanel.getTextComponent()) {
//				if(processInput()) wrappedPopup.hide();
//				app.getActiveEuclidianView().requestFocusInWindow();
//		}else
			if (source == btCancel) {
				wrappedPopup.hide();
				app.getActiveEuclidianView().requestFocusInWindow();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			setVisible(false);
			app.setDefaultCursor();
			app.getActiveEuclidianView().requestFocusInWindow();
		}
		
    }

}
