package geogebra.web.main;

import geogebra.common.kernel.commands.CmdGetTime;
import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.ggtapi.models.MaterialFilter;
import geogebra.common.move.ggtapi.models.SyncEvent;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.FileManagerI;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.util.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class FileManager implements FileManagerI {
	private AppW app;
	private Provider provider;

	public static final String AUTO_SAVE_KEY = "autosave";
	public static final String FILE_PREFIX = "file_";
	public static final String reservedCharacters = "*/:<>?\\|+,.;=[]";

	/**
	 * @param matID
	 *            local ID of material
	 * @param title
	 *            of material
	 * @return creates a key (String) for the stockStore
	 */
	public static String createKeyString(int matID, String title) {
		StringBuilder sb = new StringBuilder(title.length()+12);
		sb.append(FILE_PREFIX);
		sb.append(matID);
		sb.append('_');
		for(int i = 0; i < title.length(); i++){
			if (reservedCharacters.indexOf(title.charAt(i)) == -1) {
				sb.append(title.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String getFileKey(Material mat) {
		return createKeyString(mat.getLocalID(), mat.getTitle());
	}

	/**
	 * returns the ID from the given key. (key is of form "file_ID_fileName")
	 * 
	 * @param key
	 *            String
	 * @return int ID
	 */
	public static int getIDFromKey(String key) {
		return Integer.parseInt(key.substring(FILE_PREFIX.length(),
		        key.indexOf("_", FILE_PREFIX.length())));
	}
	public FileManager(final AppW app) {
		this.app = app;
	}

	public abstract void delete(final Material mat);

	/**
	 * 
	 * @param base64
	 *            only a hint, we can send null and it will be resolved
	 * @param cb
	 */
	public abstract void saveFile(String base64, long modified,
	        final SaveCallback cb);


	protected abstract void getFiles(MaterialFilter materialFilter);

	/**
	 * Overwritten for phone
	 * 
	 * @param material
	 *            {@link Material}
	 */
	public void removeFile(final Material material) {
		app.getGuiManager().getBrowseView().removeMaterial(material);
	}

	/**
	 * Overwritten for phone
	 * 
	 * @param material
	 *            {@link Material}
	 */
	public void addMaterial(final Material material) {
		app.getGuiManager().getBrowseView().addMaterial(material);
	}

	public Material createMaterial(final String base64, long modified) {
		final Material mat = new Material(0, MaterialType.ggb);

		// TODO check if we need to set timestamp / modified
		mat.setModified(modified);

		if (app.getTubeId() != 0) {
			mat.setId(app.getTubeId());
			App.debug("create material" + app.getSyncStamp());
			mat.setSyncStamp(app.getSyncStamp());
		}

		mat.setBase64(base64);
		mat.setTitle(app.getKernel().getConstruction().getTitle());
		mat.setDescription(app.getKernel().getConstruction()
		        .getWorksheetText(0));
		mat.setThumbnail(app.getEuclidianView1()
		        .getCanvasBase64WithTypeString());
		mat.setAuthor(app.getLoginOperation().getUserName());
		return mat;
	}

	/**
	 * @param query
	 *            String
	 */
	public void search(final String query) {
		getFiles(MaterialFilter.getSearchFilter(query));
	}

	/**
	 * adds the files from the current user to the {@link BrowseGUI}
	 */
	public void getUsersMaterials() {
		getFiles(MaterialFilter.getUniversalFilter());
		// getFiles(MaterialFilter.getAuthorFilter(app.getLoginOperation().getUserName()));
	}

	private int notSyncedFileCount;

	public void setNotSyncedFileCount(int count, ArrayList<SyncEvent> events) {
		this.notSyncedFileCount = count;
		checkMaterialsToDownload(events);
	}

	public void ignoreNotSyncedFile(ArrayList<SyncEvent> events) {
		this.notSyncedFileCount--;
		checkMaterialsToDownload(events);
	}

	public void sync(final Material mat, ArrayList<SyncEvent> events) {
		if (mat.getId() == 0) {
			upload(mat);
		} else {
			for (SyncEvent event : events) {
				if (event.getID() == mat.getId()) {
					sync(mat, event);
					event.setZapped(true);
					break;
				}
			}
		}

		this.notSyncedFileCount--;
		App.debug("SYNC remains " + this.notSyncedFileCount);
		checkMaterialsToDownload(events);
		sync(mat, new SyncEvent(0, 0));
	}

	private void checkMaterialsToDownload(ArrayList<SyncEvent> events) {
		if (notSyncedFileCount == 0) {
			for (SyncEvent event : events) {
				App.debug("SYNCDNLD" + event.isFavorite() + ","
				        + event.isZapped() + "," + event.getID());
				if (event.isFavorite() && !event.isZapped()) {
					getFromTube(event.getID());
				}
			}
		}
	}

	private void sync(final Material mat, SyncEvent event) {
		long tubeTimestamp = event.getTimestamp();

		if (event.isDelete()) {
			delete(mat);
		}
 else if (event.isUnfavorite() && mat.isFromAnotherDevice()) {
			// remove from local device
		}
 else if (tubeTimestamp != 0 && tubeTimestamp > mat.getSyncStamp()) {

				
			
				getFromTube(mat);


		} else {
			// no changes in Tube
			if (mat.getId() > 0 && mat.getModified() <= mat.getSyncStamp()) {
				App.debug("SYNC material up to date" + mat.getId());
			} else {
				App.debug("SYNC outgoing changes:" + mat.getId());
				upload(mat);
			}
		}

	}

	private void getFromTube(final int id) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(id + "", new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {

				        // edited on Tube, not edited locally
				        if (parseResponse.size() == 1) {
					        App.debug("SYNC downloading file:" + id);
					        FileManager.this.updateFile(
null,
				                parseResponse.get(0).getModified(),
				                parseResponse.get(0));
				        }
			        }

			        @Override
			        public void onError(final Throwable exception) {
				        App.debug("SYNC error loading from tube" + id);
			        }
		        });

	}

	private void getFromTube(final Material mat) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(mat.getId() + "", new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {

				        // edited on Tube, not edited locally
				        if (mat.getModified() <= mat.getSyncStamp()) {
					        App.debug("SYNC incomming changes:" + mat.getId());
					        FileManager.this.updateFile(getFileKey(mat),
					                parseResponse.get(0).getModified(),
					                parseResponse.get(0));
				        } else {
					        ToolTipManagerW.sharedInstance().showBottomMessage(
					                app.getLocalization().getPlain(
					                        "SeveralVersionsOfA",
					                        parseResponse.get(0).getTitle()),
					                true);
					        App.debug("SYNC fork: " + mat.getId());
					        final String format = app.getLocalization()
					                .isRightToLeftReadingOrder() ? "\\Y "
					                + Unicode.LeftToRightMark + "\\F"
					                + Unicode.LeftToRightMark + " \\j"
					                : "\\j \\F \\Y";
					        mat.setTitle(mat.getTitle()
					                + " ("
					                + CmdGetTime.buildLocalizedDate(format,
					                        new Date(), app.getLocalization())
					                + ")");
					        mat.setId(0);
					        upload(mat);

				        }

			        }

			        @Override
			        public void onError(final Throwable exception) {
				        App.debug("SYNC error loading from tube" + mat.getId());
			        }
		        });

	}

	protected abstract void updateFile(String title, long modified,
	        Material material);

	/**
	 * uploads the material and removes it from localStorage
	 * 
	 * @param mat
	 *            {@link Material}
	 */
	public void upload(final Material mat) {
		final String localKey = getFileKey(mat);
		mat.setTitle(getTitleFromKey(mat.getTitle()));
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .uploadLocalMaterial(app, mat, new MaterialCallback() {

			        @Override
			        public void onLoaded(final List<Material> parseResponse) {
				        if (parseResponse.size() == 1) {
					        mat.setTitle(getTitleFromKey(mat.getTitle()));
					        mat.setLocalID(FileManager.getIDFromKey(localKey));
					        final Material newMat = parseResponse.get(0);
					        newMat.setThumbnail(mat.getThumbnail());
					        newMat.setSyncStamp(newMat.getModified());
					        if (!FileManager.this.shouldKeep(mat.getId())) {
						        delete(mat);
					        } else {
						        // Meta may have changed (tube ID), sync
								// timestamp needs changing always
							        FileManager.this
							                .setTubeID(localKey, newMat);

					        }

					        app.getGuiManager().getBrowseView().refreshMaterial(newMat, false);
				        }
			        }

			        @Override
			        public void onError(final Throwable exception) {
				        // TODO
			        }
		        });
	}

	public abstract void setTubeID(String localKey, Material mat);

	public boolean shouldKeep(int id) {
		return true;
	}

	/**
	 * key is of form "file_ID_title"
	 * 
	 * @param key
	 *            file key
	 * @return the title
	 */
	public static String getTitleFromKey(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	public void setFileProvider(Provider google) {
		this.provider = google;
	}

	public Provider getFileProvider() {
		return this.provider;
	}





	@Override
	public void openMaterial(final Material material) {
		try {
			final String base64 = material.getBase64();
			if (base64 == null) {
				return;
			}
			app.getGgbApi().setBase64(base64);
		} catch (final Throwable t) {
			app.showError(app.getLocalization().getError("LoadFileFailed"));
			t.printStackTrace();
		}
	}

	/**
	 * only for FileManagerT and FileManagerW
	 * 
	 * @return {@link AppW}
	 */
	public AppW getApp() {
		return this.app;
	}

	public final boolean save(AppW app) {
		// not logged in and can't log in
		if (!app.getLoginOperation().isLoggedIn()
		        && (!app.getNetworkOperation().isOnline() || !app
		                .getLoginOperation().mayLogIn())) {
			saveLoggedOut(app);
			// not logged in and possible to log in
		} else if (!app.getLoginOperation().isLoggedIn()) {
			app.getGuiManager().listenToLogin();
			((SignInButton) app.getLAF().getSignInButton(app)).login();
			// logged in
		} else {
			((DialogManagerW) app.getDialogManager()).showSaveDialog();
		}
		return true;
	}

}
