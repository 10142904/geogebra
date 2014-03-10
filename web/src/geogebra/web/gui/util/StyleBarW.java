package geogebra.web.gui.util;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author G. Sturr
 * 
 */
public abstract class StyleBarW extends HorizontalPanel {

	/**
	 * Constructor
	 */
	public StyleBarW() {
		setStyleName("StyleBar");
	}

	
	protected void addSeparator(){
		VerticalSeparator s = new VerticalSeparator(10,25);
		add(s);
	}


	public abstract void setOpen(boolean showStyleBar);
	
	
}
