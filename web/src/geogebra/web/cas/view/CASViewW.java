package geogebra.web.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.cas.view.CASView;
import geogebra.common.main.App;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.SelectionHandler;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.ui.Widget;



public class CASViewW extends CASView {

	private CASComponentW component;
	private AppW app;
	private CASTableW consoleTable;
	private CASStylebarW styleBar;
	private CASSubDialogW subDialog;

	public CASViewW(AppW app){
		component = new CASComponentW();
		kernel = app.getKernel();
		this.app = app;
		
		getCAS();	

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();
		
		// CAS input/output cells
		CASTableControllerW ml = new CASTableControllerW(this,app);
		consoleTable = new CASTableW(app,ml,this);	
		component.add(consoleTable);
		SelectionHandler.disableTextSelectInternal(component.getElement(), true);
		// input handler
		casInputHandler = new CASInputHandler(this);
		
		component.addDomHandler(ml, MouseDownEvent.getType());
		component.addDomHandler(ml, MouseUpEvent.getType());
		component.addDomHandler(ml, MouseMoveEvent.getType());
		component.addDomHandler(ml, TouchStartEvent.getType());
		component.addDomHandler(ml, TouchMoveEvent.getType());
		component.addDomHandler(ml, TouchEndEvent.getType());
		
		getCAS().initCurrentCAS();
		getCAS().getCurrentCAS().reset();
				
	}
	public void repaintView() {
	    // TODO Auto-generated method stub
	    
    }

	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public CASTableW getConsoleTable() {
	    return consoleTable;
    }

	@Override
    public AppW getApp() {
	    return app;
    }

	@Override
	public void showSubstituteDialog(String prefix, String evalText,
	        String postfix, int selRow) {
		App.debug("Before creation");
		if (subDialog != null && subDialog.getDialog().isShowing())
			return;
		CASSubDialogW d = new CASSubDialogW(this, prefix, evalText,
		        postfix, selRow);
		d.getDialog().center();
		d.getDialog().show();
		App.debug("CASSubDialogCreated");
		subDialog = d;

    }
	
	public Widget getComponent() {
	    return component;
    }
	public CASStylebarW getCASStyleBar() {
		if (styleBar == null) {
			styleBar = newCASStyleBar();
		}
		return styleBar;
    }
	private CASStylebarW newCASStyleBar() {
		return new CASStylebarW(this, app);
    }
	
	public boolean suggestRepaint(){
		// not used for this view
		return false;
	}

}
