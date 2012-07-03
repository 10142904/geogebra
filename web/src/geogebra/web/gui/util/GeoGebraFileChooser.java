package geogebra.web.gui.util;

import sun.awt.HorizBagLayout;
import geogebra.common.main.App;
import geogebra.common.plugin.GgbAPI;
import geogebra.web.gui.menubar.FileMenuW;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GeoGebraFileChooser extends PopupPanel {
	
	App app;
	VerticalPanel p;
	TextBox fileName;
	TextArea description;
	Button save;
	Button cancel;
	GeoGebraFileChooser _this = this;

	public GeoGebraFileChooser(final App app) {
	    super();
	    this.app = app;
	    add(p = new VerticalPanel());
	    
	    HorizontalPanel fileNamePanel = new HorizontalPanel();
	    fileNamePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    fileNamePanel.add(new Label("Filename: "));
	    fileNamePanel.add(fileName = new TextBox());
	    fileNamePanel.addStyleName("fileNamePanel");
	    p.add(fileNamePanel);
	    
	    HorizontalPanel descriptionPanel = new HorizontalPanel();
	    descriptionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    descriptionPanel.add(new Label("Description: "));
	    descriptionPanel.add(description = new TextArea());
	    descriptionPanel.addStyleName("descriptionPanel");
	    p.add(descriptionPanel);
	    
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    buttonPanel.addStyleName("buttonPanel");
	    buttonPanel.add(cancel = new Button(app.getMenu("Cancel")));
	    buttonPanel.add(save = new Button(app.getMenu("SaveToGoogleDrive")));
	    p.add(buttonPanel);
	    addStyleName("GeoGebraFileChooser");
	    
	    cancel.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				app.setDefaultCursor();
				hide();
			}
		});
	    
	    save.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				if (fileName.getText() != "") {
					JavaScriptObject callback = MyGoogleApis.getPutFileCallback(fileName.getText(), description.getText(), _this);
					((geogebra.web.main.GgbAPI)app.getGgbApi()).getBase64(callback);
					//MyGoogleApis.putNewFileToGoogleDrive(fileName.getText(),description.getText(),FileMenu.temp_base64_BUNNY,_this);
				}
			}
				
		});
	    
	    addCloseHandler(new CloseHandler<PopupPanel>() {
			
			public void onClose(CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
			}
		});
	    center();
	    
	    
	    
	    
    }

	public void saveSuccess(String fName, String desc, String fileCont) {
	    ((AppW) app).refreshCurrentFileDescriptors(fName,desc,fileCont);
    }
	
	public void setFileName(String fName) {
		fileName.setText(fName);
	}
	
	public void setDescription(String ds) {
		description.setText(ds);
	}

}
