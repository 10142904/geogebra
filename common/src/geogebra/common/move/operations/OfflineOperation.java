package geogebra.common.move.operations;

import geogebra.common.move.views.OfflineView;

/**
 * @author gabor
 * 
 * Operational (logic) class for Offline functionality
 *
 */
public class OfflineOperation extends NetworkOperation {
	
	/**
	 * Creates a new offlineOperation class for Offline functionality
	 * @param network The implementation of the Network interface
	 */
	public OfflineOperation(Network network) {
		this.online = network.onLine();
	}
	
	@Override
	public OfflineView getView() {
		return (OfflineView) view;
	}
	
	

}
