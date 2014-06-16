package geogebra.web.move.googledrive.events;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 * 
 * Used for notify things if drive loaded
 *
 */
public class GoogleDriveLoadedEvent extends BaseEvent {

	public GoogleDriveLoadedEvent() {
	    super("Drive loaded");
    }

	@Override
	public void trigger() {
		// Do nothing
	}

}
