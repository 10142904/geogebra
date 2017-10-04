package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.TubeAvailabilityCheckEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.DeleteRequest;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.ShareRequest;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.move.ggtapi.requests.SyncRequest;
import org.geogebra.common.move.ggtapi.requests.UploadRequest;
import org.geogebra.common.util.HttpRequest;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor Common base for GeoGebraTubeApi
 */
public abstract class GeoGebraTubeAPI {

	/**
	 * The Standard Result Quantity
	 */
	public static final int STANDARD_RESULT_QUANTITY = 30;

	/** Public url DO NOT CHANGE!s */
	public static final String url = "https://www.geogebra.org/api/json.php";
	/** For beta version */
	public static final String urlBeta = "https://beta.geogebra.org/api/json.php";
	/** Public login url DO NOT CHANGE! */
	public static final String login_url = "https://accounts.geogebra.org/api/index.php";
	/** Login for beta */
	public static final String login_urlBeta = "https://accounts-beta.geogebra.org/api/index.php";

	static public final int LOGIN_TOKEN_VALID = 0;
	static public final int LOGIN_TOKEN_INVALID = 1;
	static public final int LOGIN_REQUEST_FAILED = -2;

	protected boolean available = true;
	protected boolean availabilityCheckDone = false;
	protected ClientInfo client;
	private String materialsURL;

	private String loginURL;

	/**
	 * @param beta
	 *            true if beta
	 */
	public GeoGebraTubeAPI(boolean beta) {
		this.materialsURL = beta ? urlBeta : url;
		this.loginURL = beta ? login_urlBeta : login_url;
	}

	public void setURL(String url1) {
		this.materialsURL = url1;
	}

	public void setLoginURL(String loginAPIurl) {
		this.loginURL = loginAPIurl;

	}

	/**
	 * Private method performing the request given by requestString
	 * 
	 * @param requestString
	 *            JSON request String for the GeoGebraTubeAPI
	 */
	protected final void performRequest(String requestString, boolean login,
			AjaxCallback callback) {
		String postUrl = login ? getLoginUrl() : getUrl();
		if ("null".equals(postUrl)) {
			return;
		}
		HttpRequest request = createHttpRequest();
		request.sendRequestPost(postUrl, requestString,
				callback);
	}

	public final String getLoginUrl() {
		return loginURL;
	}

	protected final String getUrl() {
		return materialsURL;
	}

	/**
	 * Creates a new Http request
	 * 
	 * @return The new http request
	 */
	protected abstract HttpRequest createHttpRequest();

	protected abstract boolean parseUserDataFromResponse(GeoGebraTubeUser user,
			String response);

	/**
	 * Sends a request to the GeoGebraTube API to check if the login token which
	 * is defined in the specified GeoGebraTubeUser is valid.
	 * 
	 * @param user
	 *            The user that should be authorized.
	 * @param op
	 *            login operation
	 * @param automatic
	 *            true if automatic
	 */
	public final void authorizeUser(final GeoGebraTubeUser user,
			final LogInOperation op, final boolean automatic) {
		performRequest(
				buildTokenLoginRequest(user.getLoginToken(), user.getCookie()),
				true, new AjaxCallback() {
					@Override
					public void onSuccess(String responseStr) {
						try {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;

							GeoGebraTubeAPI.this.available = true;

							// Parse the userdata from the response
							if (!parseUserDataFromResponse(user, responseStr)) {
								op.onEvent(new LoginEvent(user, false,
										automatic, responseStr));
								return;
							}

							op.onEvent(new LoginEvent(user, true, automatic,
									responseStr));

							// GeoGebraTubeAPID.this.available = false;
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onError(String error) {
						GeoGebraTubeAPI.this.availabilityCheckDone = true;
						GeoGebraTubeAPI.this.available = false;
						op.onEvent(
								new LoginEvent(user, false, automatic, null));
					}
				});

	}

	/**
	 * Builds the request to check if the login token of a user is valid. This
	 * request will send detailed user information as response.
	 * 
	 * @param token
	 *            The user that should be logged in
	 * @param cookie
	 *            cookie (for web)
	 * @return The JSONObject that contains the request.
	 */
	protected static final String buildTokenLoginRequest(String token,
			String cookie) {
		JSONObject requestJSON = new JSONObject();
		JSONObject apiJSON = new JSONObject();
		JSONObject loginJSON = new JSONObject();
		try {
			if (token != null) {
				loginJSON.put("token", token);
			} else {
				loginJSON.put("cookie", cookie);
			}
			loginJSON.put("getuserinfo", "true");
			apiJSON.put("login", loginJSON);
			apiJSON.put("api", "1.0.0");
			requestJSON.put("request", apiJSON);
		} catch (Exception e) {
			Log.debug("problem building request: " + e.getMessage());
			return null;
		}
		return requestJSON.toString();
	}

	public boolean checkAvailable(LogInOperation op) {
		if (this.availabilityCheckDone && op != null) {
			op.onEvent(new TubeAvailabilityCheckEvent(this.available));
		}
		checkIfAvailable(op, getClientInfo());
		return this.available;
	}

	public boolean isAvailable() {
		return this.available;
	}

	protected String getClientInfo() {
		return "";
	}

	/**
	 * Sends a test request to GeoGebraTube to check if it is available The
	 * result is stored in a boolean variable. Subsequent calls to isAvailable()
	 * will return the value of the stored variable and don't send the request
	 * again.
	 * 
	 * @return boolean if the request was successful.
	 */
	private boolean checkIfAvailable(final LogInOperation op,
			String clientInfo) {
		if (!this.availabilityCheckDone) {
			this.available = false;
		}
		this.availabilityCheckDone = false;
		try {
			performRequest(
					"{\"request\": {\"-api\": \"1.0.0\"," + clientInfo
							+ "\"task\": {\"-type\": \"info\"}}}",
					false, new AjaxCallback() {

						@Override
						public void onSuccess(String response) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = true;
							if (op != null) {
								op.onEvent(
										new TubeAvailabilityCheckEvent(true));
							}
						}

						@Override
						public void onError(String error) {
							GeoGebraTubeAPI.this.availabilityCheckDone = true;
							GeoGebraTubeAPI.this.available = false;
							if (op != null) {
								op.onEvent(
										new TubeAvailabilityCheckEvent(true));
							}

						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.available;
	}

	public void setUserLanguage(String lang, String token) {
		performRequest("{\"request\": {" + "\"api\":\"1.0.0\","
				+ "\"login\": {\"token\":\"" + token
				+ "\", \"getuserinfo\":\"false\"},"
				+ "\"task\": {\"type\":\"setuserlang\", \"lang\":\"" + lang
				+ "\"}}}", true, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				// yay, it worked
			}

			@Override
			public void onError(String error) {
				Log.error(error);

			}
		});
	}

	public void logout(String token) {
		performRequest("{\"request\": {" + "\"api\":\"1.0.0\","
				+ "\"logout\": {\"token\":\"" + token
				+ "\", \"getuserinfo\":\"false\"}}}", true, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				// yay, it worked
			}

			@Override
			public void onError(String error) {
				Log.error(error);

			}
		});
	}

	public void favorite(int id, boolean favorite) {
		performRequest("{\"request\": {" + "\"-api\":\"1.0.0\","
				+ "\"login\": {\"-token\":\"" + getToken() + "\"},"
				+ "\"task\": {\"-type\":\"favorite\", \"id\":\"" + id
				+ "\",\"favorite\":\"" + favorite + "\"}}}", false,
				new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
						// yay, it worked
					}

					@Override
					public void onError(String error) {
						Log.error(error);

					}
				});
	}

	/**
	 * to rename materials on ggt; TODO no use of base64
	 * 
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void uploadRenameMaterial(Material mat, final MaterialCallbackI cb) {
		performRequest(
				UploadRequest.getRequestElement(mat.getTitle(), mat.getId())
						.toJSONString(client),
				cb);
	}

	/**
	 * Uploads a local saved file (web - localStorage; touch - device) to ggt
	 * 
	 * @param mat
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void uploadLocalMaterial(final Material mat,
			final MaterialCallbackI cb) {
		performRequest(
				UploadRequest.getRequestElement(mat).toJSONString(client), cb);
	}

	/**
	 * @param material
	 *            {@link Material}
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void deleteMaterial(Material material, final MaterialCallbackI cb) {
		performRequest(
				DeleteRequest.getRequestElement(material).toJSONString(client),
				cb);
	}

	public void shareMaterial(Material material, String to, String message,
			final MaterialCallbackI cb) {
		performRequest(ShareRequest.getRequestElement(material, to, message)
				.toJSONString(client), cb);
	}

	/**
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void getUsersMaterials(MaterialCallbackI cb) {
		performRequest(
				MaterialRequest.forCurrentUser(client).toJSONString(client),
				cb);
	}

	public void getUsersOwnMaterials(MaterialCallbackI cb) {
		System.out.println(client.getModel().getUserId());
		performRequest(
				MaterialRequest.forUser(client.getModel().getUserId(), client)
						.toJSONString(client),
				cb);
	}

	/**
	 * @param requestString
	 *            json string representing the request
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	protected final void performRequest(String requestString,
			final MaterialCallbackI cb) {
		if ("null".equals(getUrl())) {
			return;
		}
		HttpRequest req = createHttpRequest();
		req.sendRequestPost(getUrl(), requestString, new AjaxCallback() {

			@Override
			public void onSuccess(String response) {
				ArrayList<Material> result = new ArrayList<Material>();
				ArrayList<Chapter> meta = JSONParserGGT.prototype
						.parseResponse(response, result);
				cb.onLoaded(result, meta);

			}

			@Override
			public void onError(String error) {
				cb.onError(new Exception(error));

			}
		});

	}

	/**
	 * @param id
	 *            int
	 * @param cb
	 *            {@link MaterialCallbackI}
	 */
	public void getBookItems(int id, MaterialCallbackI cb) {
		performRequest(MaterialRequest.forBook(id, client).toJSONString(client),
				cb);
	}

	public void getWorksheetItems(int id, MaterialCallbackI cb) {
		performRequest(
				MaterialRequest.forWorksheet(id, client).toJSONString(client),
				cb);
	}

	/**
	 * Uploads the actual opened application to ggt
	 * 
	 * @param tubeID
	 *            tube id
	 * @param visibility
	 *            visibility string
	 * 
	 * @param filename
	 *            String
	 * @param base64
	 *            base64 string
	 * @param cb
	 *            MaterialCallback
	 * @param type
	 *            material type
	 */
	public void uploadMaterial(int tubeID, String visibility,
			final String filename, String base64, final MaterialCallbackI cb,
			MaterialType type) {

		performRequest(UploadRequest
				.getRequestElement(tubeID, visibility, filename, base64, type)
				.toJSONString(client), cb);

	}

	/**
	 * Search for materials containing the String query
	 * 
	 * @param query
	 *            search String
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	public void search(String query, MaterialCallbackI callback) {
		performRequest(new MaterialRequest(query, client).toJSONString(client),
				callback);
	}

	/**
	 * Returns materials in the given amount and order
	 * 
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	public void getFeaturedMaterials(MaterialCallbackI callback) {
		performRequest(MaterialRequest.forFeatured(client).toJSONString(client),
				callback);
	}

	// /**
	// * Returns a String-Array of popular tags fetched from the GGT API
	// *
	// */
	// public String[] getPopularTags()
	// {
	// // TODO fetch popular tags from the API
	// return new String[] { "algebra", "dment", "pythagorean", "circle",
	// "triangle", "functions", "jerzy", "geometry", "trigonometry", "3d" };
	// }

	/**
	 * Return a specific Material by its ID
	 * 
	 * @param id
	 *            int
	 * @param callback
	 *            {@link MaterialCallbackI}
	 */
	public void getItem(String id, MaterialCallbackI callback) {
		performRequest(MaterialRequest.forId(id, client).toJSONString(client),
				callback);
	}

	public boolean isCheckDone() {
		return availabilityCheckDone;
	}

	public void sync(long timestamp, final SyncCallback cb) {
		this.performRequest(new SyncRequest(timestamp).toJSONString(client),
				false, new AjaxCallback() {

					@Override
					public void onSuccess(String response) {
						ArrayList<SyncEvent> events = new ArrayList<SyncEvent>();
						try {
							JSONTokener tok = new JSONTokener(response);
							JSONObject responseObj = (JSONObject) ((JSONObject) new JSONObject(
									tok).get("responses"))

											.get("response");
							Object items = responseObj.get("item");

							JSONParserGGT.prototype.addEvents(events, items);

							cb.onSync(events);
						} catch (Exception e) {
							Log.error("SYNC parse error" + e.getMessage());
						}

					}

					@Override
					public void onError(String error) {
						Log.error("SYNCE error" + error);

					}
				});
	}



	protected String getToken() {
		return client.getModel().getLoginToken();
	}

	public void setClient(ClientInfo clientInfo) {
		this.client = clientInfo;

	}
}
