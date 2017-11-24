package org.geogebra.web.web.gui.dialog;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.ColorChooserW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class ColorChooserDialog extends DialogBoxW
		implements SetLabels, ClickHandler {

	private ColorChooserW colorChooserW; 
	private Button btnOk;
	private Button btnCancel;
	private Button btnReset;
	private GColor selectedColor;
	private AppW app;
	private ColorChangeHandler handler;
	private GColor originalColor;
	
	public ColorChooserDialog(AppW app, final GColor originalColor, final ColorChangeHandler handler) {
		super(false, true, null, app.getPanel(), app);

		this.app = app;
		this.handler = handler;
		this.originalColor = originalColor;

		final GDimensionW colorIconSizeW = new GDimensionW(20, 20);
		colorChooserW = new ColorChooserW(app, 400, 210, colorIconSizeW, 4);
		colorChooserW.enableOpacity(false);
		colorChooserW.enableBackgroundColorPanel(false);
		colorChooserW.setSelectedColor(originalColor);
		setSelectedColor(originalColor);
		FlowPanel mainWidget = new FlowPanel();
		mainWidget.add(colorChooserW);
		FlowPanel btnPanel = new FlowPanel();
		btnOk = new Button();
		btnCancel = new Button();
		btnCancel.addStyleName("cancelBtn");
		btnReset = new Button();
		btnReset.addStyleName("resetBtn");
		btnPanel.addStyleName("DialogButtonPanel");
		btnPanel.add(btnOk);
		btnPanel.add(btnCancel);
		btnPanel.add(btnReset);
		mainWidget.add(btnPanel);
		
		btnOk.addClickHandler(this);
		btnCancel.addClickHandler(this);
		btnReset.addClickHandler(this);

		setLabels();

		setWidget(mainWidget);
		
		colorChooserW.addChangeHandler(new ColorChangeHandler() {
			
			@Override
			public void onForegroundSelected() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onColorChange(GColor color) {
				setSelectedColor(color);
			}
			
			@Override
			public void onClearBackground() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onBackgroundSelected() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAlphaChange() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBarSelected() {
				// TODO Auto-generated method stub

			}
		});
	}
	
	@Override
	public void setLabels() {
		this.getCaption().setText(localize("ChooseColor"));
		colorChooserW.setPaletteTitles(localize("RecentColor"), localize("Other"));
		colorChooserW.setPreviewTitle(localize("Preview"));
		btnOk.setText(localize("OK"));
		btnCancel.setText(localize("Cancel"));
		btnReset.setText(localize("Reset"));
    }

	private String localize(final String id) {
		return app.getLocalization().getMenu(id);
	}
	
	public GColor getSelectedColor() {
	    return selectedColor;
    }
	
	public void setSelectedColor(GColor selectedColor) {
	    this.selectedColor = selectedColor;
    }
	
	public ColorChangeHandler getHandler() {
	    return handler;
    }
	
	public void setHandler(ColorChangeHandler handler) {
	    this.handler = handler;
    }

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == btnOk) {
			handler.onColorChange(getSelectedColor());
			hide();
		} else if (source == btnCancel) {
			hide();
		} else if (source == btnReset) {
			reset();
		}
	}

	public void setOriginalColor(GColor color) {
		originalColor = color;
		reset();
	}

	private void reset() {
		setSelectedColor(originalColor);
		colorChooserW.setSelectedColor(originalColor);
		colorChooserW.update();
	}
}