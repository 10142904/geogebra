package geogebra.html5.event;

import geogebra.common.euclidian.event.ActionListener;
import geogebra.common.euclidian.event.ActionListenerI;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

public class ActionListenerW extends ActionListener implements EventListener,
        ChangeHandler {

	public ActionListenerW(ActionListenerI listener) {
		setListenerClass(listener);
	}

	public void onBrowserEvent(Event e) {
		ActionEventW event = ActionEventW.wrapEvent(e);
		wrapActionPerformed(event);
	}

	public void onChange(ChangeEvent e) {
		ChangeEventW event = ChangeEventW.wrapEvent(e);
		wrapActionPerformed(event);

	}

}
