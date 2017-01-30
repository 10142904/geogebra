package org.geogebra.common.main;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.Provider;
import org.geogebra.common.move.ggtapi.models.SyncEvent;

public interface MaterialsManagerI {

	void openMaterial(Material material);

	void delete(Material material, boolean permanent, Runnable onSuccess);

	void uploadUsersMaterials(ArrayList<SyncEvent> events);

	void getUsersMaterials();

	void search(String query);

	void rename(String newTitle, Material mat, Runnable callback);

	void setFileProvider(Provider google);

	Provider getFileProvider();

	void autoSave(int counter);

	String getAutosaveJSON();

	public void restoreAutoSavedFile(String json);

	public void deleteAutoSavedFile();

	boolean save(App app);

	void saveLoggedOut(App app);

	boolean shouldKeep(int i);

	void getFromTube(int id, boolean fromAnotherDevice);

	boolean isSyncing();

	void export(App app);

	void exportImage(String url, String string);

	boolean hasBase64(Material material);

	void nativeShare(String s, String string);

	void showExportAsPictureDialog(String url, String filename, App app);

	void refreshAutosaveTimestamp();
}
