package geogebra.html5.kernel;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.UndoManager;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.util.CopyPaste;
import geogebra.html5.Browser;

import com.google.gwt.storage.client.Storage;

public class UndoManagerW extends UndoManager {

	private static final String TEMP_STORAGE_PREFIX = "GeoGebraUndoInfo";
	private static long nextKeyNum = 1;

	/**
	 * can be null (eg IE9 running locally)
	 */
	Storage storage;

	protected class AppStateWeb implements AppState {
		private String key;

		AppStateWeb(String xmls) {
			if (storage != null) {
				storage.setItem(key = TEMP_STORAGE_PREFIX + nextKeyNum++, xmls);
			}
		}

		public String getXML() {
			if (storage == null) {
				return null;
			}
			return storage.getItem(key);
		}

		public void delete() {
			if (storage != null) {
				storage.removeItem(key);
			}
		}
	}

	public UndoManagerW(Construction cons) {
		super(cons);
		App.debug("trying to init storage");
		if (Browser.supportsSessionStorage()) {
			storage = Storage.getSessionStorageIfSupported();
		}
		App.debug("storage done");
	}

	@Override
	public void processXML(String xml) throws Exception {
		construction.getXMLio().processXMLString(xml, true, false);
	}

	@Override
	public void storeUndoInfoAfterPasteOrAdd() {
		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction
		        .getCurrentUndoXML(true);

		// Thread undoSaverThread = new Thread() {
		// @Override
		// public void run() {
		doStoreUndoInfo(currentUndoXML);
		CopyPaste.INSTANCE.pastePutDownCallback(app);
		// }
		// };
		// undoSaverThread.start();
	}

	@Override
	public void storeUndoInfo(final boolean refresh) {

		// this can cause a java.lang.OutOfMemoryError for very large
		// constructions
		final StringBuilder currentUndoXML = construction
		        .getCurrentUndoXML(true);

		// Thread undoSaverThread = new Thread() {
		// @Override
		// public void run() {

		doStoreUndoInfo(currentUndoXML);
		if (refresh)
			restoreCurrentUndoInfo();

		// }
		// };
		// undoSaverThread.start();

	}

	/**
	 * Adds construction state to undo info list.
	 * 
	 * @param undoXML
	 *            string builder with construction XML
	 */
	synchronized void doStoreUndoInfo(final StringBuilder undoXML) {

		try {
			// insert undo info
			AppState appStateToAdd = new AppStateWeb(undoXML.toString());
			iterator.add(appStateToAdd);
			pruneStateList();
			app.getEventDispatcher().dispatchEvent(
			        new Event(EventType.STOREUNDO, null));

		} catch (Exception e) {
			App.debug("storeUndoInfo: " + e.toString());
			e.printStackTrace();
		} catch (Error err) {
			App.debug("UndoManager.storeUndoInfo: " + err.toString());
			err.printStackTrace();
		}
		updateUndoActions();
	}

	@Override
	protected void loadUndoInfo(final AppState info) {
		try {
			// load from file
			String tempXML = ((AppStateWeb) info).getXML();

			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// load undo info
			app.getScriptManager().disableListeners();
			processXML(tempXML);
			app.getScriptManager().enableListeners();

		} catch (Exception e) {
			System.err.println("setUndoInfo: " + e.toString());
			e.printStackTrace();
			restoreCurrentUndoInfo();
		} catch (Error err) {
			System.err.println("UndoManager.loadUndoInfo: " + err.toString());
		}
	}
}
