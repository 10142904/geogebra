package geogebra.html5.event;

import geogebra.common.main.App;

import java.util.LinkedList;

public class KeyEvent extends geogebra.common.euclidian.event.KeyEvent{

	public static LinkedList<KeyEvent> pool = new LinkedList<KeyEvent>();
	private com.google.gwt.event.dom.client.KeyPressEvent event;

	private KeyEvent(com.google.gwt.event.dom.client.KeyPressEvent e) {
		App.debug("possible missing release()");
		this.event = e;
	}
	
	public static KeyEvent wrapEvent(com.google.gwt.event.dom.client.KeyPressEvent e) {
		if(!pool.isEmpty()){
			KeyEvent wrap = pool.getLast();
			wrap.event = e;
			pool.removeLast();
			return wrap;
		}
		return new KeyEvent(e);
	}
	
	public void release() {
		KeyEvent.pool.add(this);
	}	
	
	
	@Override
    public boolean isEnterKey() {
	    return event.getNativeEvent().getKeyCode() == 13 || event.getNativeEvent().getKeyCode() == 10;
    }

	@Override
    public boolean isCtrlDown() {
	    return event.isControlKeyDown();
    }

	@Override
    public boolean isAltDown() {
	    return event.isAltKeyDown();
    }

}
