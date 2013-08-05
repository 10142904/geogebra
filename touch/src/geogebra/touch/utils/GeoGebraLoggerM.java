package geogebra.touch.utils;

import geogebra.common.util.debug.GeoGebraLogger;
import geogebra.touch.gui.GeoGebraTouchGUI;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GeoGebraLoggerM extends GeoGebraLogger {
    private final Element el;

    public GeoGebraLoggerM(GeoGebraTouchGUI gui) {
	this.el = new AbsolutePanel().getElement();
	final Style st = this.el.getStyle();
	st.setZIndex(1000000);
	st.setPosition(Position.ABSOLUTE);
	this.el.setInnerHTML("");
	st.setHeight(300, Unit.PX);
	st.setOverflow(Overflow.SCROLL);
	DOM.appendChild(gui.getElement(), this.el);
    }

    @Override
    protected void print(String logEntry, Level level) {

	this.el.setInnerHTML(this.el.getInnerHTML() + "<br/>" + logEntry);
    }

}
