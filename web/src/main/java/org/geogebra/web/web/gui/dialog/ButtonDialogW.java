package org.geogebra.web.web.gui.dialog;

import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.gui.dialog.ButtonDialogModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ScriptArea;
import org.geogebra.web.web.gui.view.algebra.InputPanelW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ButtonDialogW extends DialogBoxW implements ClickHandler, HasKeyboardPopup {

	AutoCompleteTextFieldW tfCaption;
	private FlowPanel btPanel;
	private ButtonDialogModel model;
	private Button btOK, btCancel;
	private FlowPanel optionPane;
	AppW app;
	ScriptArea tfScript;
	private Localization loc;
	
	public ButtonDialogW(final AppW app, int x, int y, boolean textField) {
		super(false, true, null, app.getPanel(), app);
		if (app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
			setOverlapFeature(true);
		}
		this.app = app;
		this.loc = app.getLocalization();
		model = new ButtonDialogModel(app, x, y, textField);
		addStyleName("GeoGebraPopup");
		createGUI();	
		this.setGlassEnabled(true);
		this.setVisible(true);
		center();
		if (app.has(Feature.KEYBOARD_BEHAVIOUR)) {
			app.registerPopup(this);
		}
		if (app.has(Feature.DIALOGS_OVERLAP_KEYBOARD)) {
			this.addCloseHandler(new CloseHandler<GPopupPanel>() {
				@Override
				public void onClose(CloseEvent<GPopupPanel> event) {
					app.unregisterPopup(ButtonDialogW.this);
					app.hideKeyboard();
				}
			});
		}
	}

	private void createGUI() {
		if (model.isTextField()) {
			this.getCaption().setText(loc.getMenu("TextFieldAction"));
		}
		else {
			this.getCaption().setText(loc.getMenu("ButtonAction"));
		}
		
		// create caption panel
		Label captionLabel = new Label(
				app.has(Feature.DIALOG_DESIGN) ? loc.getMenu("Button.Caption")
						: loc.getMenu("Button.Caption") + ":");
		if (app.has(Feature.DIALOG_DESIGN)) {
			captionLabel.addStyleName("coloredLabel");
		}
		
		String initString = model.getInitString();
		InputPanelW ip = new InputPanelW(initString, app, 1, 25, true);
		tfCaption = ip.getTextComponent();
		if (tfCaption != null) {
			tfCaption.setAutoComplete(false);
			tfCaption.enableGGBKeyboard();
		}

		VerticalPanel captionPanel = new VerticalPanel();
		captionPanel.add(captionLabel);
		captionPanel.add(ip);
		captionPanel.addStyleName("captionPanel");
		//captionLabel.getElement().getParentElement().addClassName("tdForCaptionLabel");
		//captionLabel.getElement().getParentElement().setAttribute("style","vertical-align: middle");
				
		// combo box to link GeoElement to TextField
//		comboModel = new DefaultComboBoxModel();
		TreeSet<GeoElement> sortedSet = app.getKernel().getConstruction().
									getGeoSetNameDescriptionOrder();			
		
		final ListBox cbAdd = new ListBox();
		cbAdd.addItem("");
		
		if (model.isTextField()) {
			// lists for combo boxes to select input and output objects
			// fill combobox models
			Iterator<GeoElement> it = sortedSet.iterator();

			while (it.hasNext()) {
				GeoElement geo = it.next();				
				if (!geo.isGeoImage() && !(geo.isGeoButton()) && !(geo.isGeoBoolean())) {
//					comboModel.addElement(geo);
					String str = geo.toString(StringTemplate.defaultTemplate);
					cbAdd.addItem(str);
//					if (width < fm.stringWidth(str))
//						width = fm.stringWidth(str);
				}
			}	

			if (cbAdd.getItemCount() > 1) {
				cbAdd.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						String text = cbAdd.getItemText(cbAdd.getSelectedIndex());
						if("".equals(text.trim())){
							model.setLinkedGeo(null);
						}
						GeoElement geo = getGeo(text);
						if (geo==null) {
							return;
						}
						model.setLinkedGeo(geo);
                    }
					
					public GeoElement getGeo(String text) {
						TreeSet<GeoElement> sortedSet1 = app.getKernel()
						        .getConstruction()
						        .getGeoSetNameDescriptionOrder();
						Iterator<GeoElement> it1 = sortedSet1.iterator();
						while (it1.hasNext()) {
							GeoElement geo = it1.next();
							if (text.equals(geo
							        .toString(StringTemplate.defaultTemplate))) {
								return geo;
							}

						} 
						return null;
					}
					
				});
			}
		}

		Label scriptLabel = new Label(app.has(Feature.DIALOG_DESIGN)
				? loc.getMenu("Script") : loc.getMenu("Script") + ":");
		if (app.has(Feature.DIALOG_DESIGN)) {
			scriptLabel.addStyleName("coloredLabel");
		}
		tfScript = new ScriptArea();
		
		tfScript.enableGGBKeyboard(app);

		FlowPanel scriptPanel = new FlowPanel();
		scriptPanel.add(scriptLabel);
		scriptPanel.add(tfScript);

		VerticalPanel linkedPanel = new VerticalPanel();
		Label linkedLabel = new Label(
				app.has(Feature.DIALOG_DESIGN) ? loc.getMenu("LinkedObject")
						: loc.getMenu("LinkedObject") + ":");
		if (app.has(Feature.DIALOG_DESIGN)) {
			linkedLabel.addStyleName("coloredLabel");
		}
		linkedPanel.add(linkedLabel);
		linkedPanel.add(cbAdd);
		
		// buttons
		btOK = new Button(loc.getMenu("OK"));
		btOK.getElement().setAttribute("action", "OK");
		btOK.addClickHandler(this);
		btCancel = new Button(loc.getMenu("Cancel"));
		btCancel.getElement().setAttribute("action","Cancel");
		btCancel.addClickHandler(this);
		btCancel.addStyleName("cancelBtn");
		btPanel = new FlowPanel();
		btPanel.add(btOK);
		btPanel.add(btCancel);
		btPanel.addStyleName("DialogButtonPanel");
			
		optionPane = new FlowPanel();
		
		// create object list
		optionPane.add(captionPanel);

		if (model.isTextField()) {
			optionPane.add(linkedPanel);
		}
		else {
			optionPane.add(scriptPanel);
		}
		
		optionPane.add(btPanel);	
		//Make this dialog display it.	
		setWidget(optionPane);
		this.addStyleName("buttonDialog");
		//this.getElement().getElementsByTagName("table").getItem(0).setAttribute("cellpadding", "5px");
    }

	@Override
	public void onClick(ClickEvent event) {
//	    AbstractApplication.debug(((Widget) event.getSource()).getElement().getAttribute("action"));
	
		Object source = event.getSource();				
		if (source == btOK) {
			model.apply(tfCaption.getText(), tfScript.getText());
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		else if (source == btCancel) {
			model.cancel();
			hide();
			app.getActiveEuclidianView().requestFocusInWindow();
		} 
		
		
    }

}
