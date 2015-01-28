package geogebra.web.main;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.common.move.ggtapi.models.SyncEvent;
import geogebra.html5.main.AppW;
import geogebra.html5.main.StringHandler;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.web.util.SaveCallback;

import java.util.ArrayList;

import com.google.gwt.storage.client.Storage;

/**
 * Manager for files from {@link Storage localStorage}
 * 
 * JSON including the base64 and metadata is stored under
 * "file_<local id>_<title>" key. The id field inside JSON is for Tube id, is
 * not affected by local id. Local id can still be found inside title => we need
 * to extract title after we load file from LS.
 *
 */
public class FileManagerW extends FileManager {

	/** locale storage */
	Storage stockStore = Storage.getLocalStorageIfSupported();

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public FileManagerW(final AppW app) {
		super(app);
	}

	@Override
	public void delete(final Material mat, boolean permanent, Runnable onSuccess) {
		if (this.stockStore != null) {
			this.stockStore.removeItem(getFileKey(mat));
			removeFile(mat);
			onSuccess.run();
		}

	}

	@Override
	public void saveFile(String base64, long modified, final SaveCallback cb) {
		if (this.stockStore == null) {
			return;
		}

		final Material mat = createMaterial(base64, modified);
		int id;

		if (getApp().getLocalID() == -1) {
			id = createID();
			getApp().setLocalID(id);
		} else {
			id = getApp().getLocalID();
		}
		String key = createKeyString(id, getApp().getKernel().getConstruction()
		        .getTitle());
		mat.setLocalID(id);
		mat.setTitle(getApp().getKernel().getConstruction().getTitle());
		stockStore.setItem(key, mat.toJson().toString());
		cb.onSaved(mat, true);

	}

	/**
	 * creates a new ID
	 * 
	 * @return int ID
	 */
	int createID() {
		int nextFreeID = 1;
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FileManager.FILE_PREFIX)) {
				int fileID = FileManager.getIDFromKey(key);
				if (fileID >= nextFreeID) {
					nextFreeID = FileManager.getIDFromKey(key) + 1;
				}
			}
		}
		return nextFreeID;
	}

	@Override
	protected void getFiles(final MaterialFilter filter) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}

		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FileManager.FILE_PREFIX)) {
				Material mat = JSONparserGGT.parseMaterial(this.stockStore
				        .getItem(key));
				if (mat == null) {
					mat = new Material(0, MaterialType.ggb);
					mat.setTitle(getTitleFromKey(key));
				}
				if (filter.check(mat)) {
					addMaterial(mat);
				}
			}
		}
	}

	@Override
	public void uploadUsersMaterials(final ArrayList<SyncEvent> events) {
		if (this.stockStore == null || this.stockStore.getLength() <= 0) {
			return;
		}
		setNotSyncedFileCount(this.stockStore.getLength(), events);
		for (int i = 0; i < this.stockStore.getLength(); i++) {
			final String key = this.stockStore.key(i);
			if (key.startsWith(FILE_PREFIX)) {
				final Material mat = JSONparserGGT
				        .parseMaterial(this.stockStore.getItem(key));
				if (getApp().getLoginOperation().owns(mat)) {
						sync(mat, events);

				} else {
					ignoreNotSyncedFile(events);
				}
			} else {
				ignoreNotSyncedFile(events);
			}
		}
	}

	@Override
	public boolean shouldKeep(int id) {
		return false;
	}

	@Override
	public void rename(String newTitle, Material mat, Runnable callback) {
		if (this.stockStore == null) {
			return;
		}
		this.stockStore.removeItem(mat.getTitle());
		int newID = createID();
		mat.setLocalID(createID());
		mat.setTitle(newTitle);
		this.stockStore.setItem(FileManager.createKeyString(newID, newTitle),
		        mat.toJson().toString());
	}

	@Override
	public void autoSave() {
		if (this.stockStore == null) {
			return;
		}
		final StringHandler base64saver = new StringHandler() {
			@Override
			public void handle(final String s) {
				final Material mat = createMaterial(s,
				        System.currentTimeMillis() / 1000);
				stockStore.setItem(AUTO_SAVE_KEY, mat.toJson().toString());
			}
		};

		getApp().getGgbApi().getBase64(true, base64saver);
	}

	@Override
	public boolean isAutoSavedFileAvailable() {
		if (stockStore != null && stockStore.getItem(AUTO_SAVE_KEY) != null) {
			return true;
		}
		return false;
	}

	/**
	 * opens the auto-saved file. call first {@code isAutoSavedFileAvailable}
	 */
	@Override
	public void restoreAutoSavedFile() {
		Material autoSaved = JSONparserGGT.parseMaterial(stockStore
		        .getItem(AUTO_SAVE_KEY));
		// maybe another user restores the file, so reset
		// sensitive data
		autoSaved.setAuthor("");
		autoSaved.setAuthorId(0);
		autoSaved.setId(0);
		autoSaved.setGoogleID("");
		openMaterial(autoSaved);
	}

	@Override
	public void deleteAutoSavedFile() {
		if (this.stockStore == null) {
			return;
		}
		this.stockStore.removeItem(AUTO_SAVE_KEY);
	}

	public void saveLoggedOut(AppW app) {
		app.getGuiManager().openFilePicker();
	}

	@Override
	public void setTubeID(String localID, Material mat) {
		// implement this if we need offline cache in Chromeapp

	}

	@Override
	protected void updateFile(String title, long modified, Material material) {
		// TODO Auto-generated method stub

	}
}
