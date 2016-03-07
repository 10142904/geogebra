package org.geogebra.desktop.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.JSONParserGGT;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.move.ggtapi.models.json.JSONTokener;
import org.geogebra.common.util.debug.Log;

/**
 * API Interface for GeoGebraTube requests and responses
 * 
 * @author stefan
 * 
 */
public class GeoGebraTubeAPID extends
		org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI {


	@Override
	protected org.geogebra.common.util.HttpRequest createHttpRequest() {
		return new org.geogebra.desktop.util.HttpRequestD();
	}

	/**
	 * Get Singleton GeogebraTubeAPI
	 * 
	 * @return GeogebraTubeAPI singleton
	 */
	public GeoGebraTubeAPID(boolean beta, ClientInfo client) {
		super(beta);
		JSONParserGGT.prototype = new JSONParserGGTD();
		this.client = client;
	}

	@Override
	public boolean parseUserDataFromResponse(GeoGebraTubeUser user,
			String responseStr) {
		try {
			JSONTokener tokener = new JSONTokener(responseStr);
			JSONObject response = new JSONObject(tokener);

			// Check if an error occurred
			if (response.has("error")) {
				return false;
			}
			JSONArray responseArray = response.getJSONObject("responses")
					.getJSONArray("response");
			JSONObject userinfo = ((JSONObject) responseArray.get(0))
					.getJSONObject("userinfo");
			user.setUserId(userinfo.getInt("user_id"));
			user.setUserName(userinfo.getString("username"));
			user.setRealName(userinfo.getString("realname"));
			user.setIdentifier(userinfo.get("identifier").toString());

			// Further fields are not parsed yet, because they are not needed
			// This is the complete response with all available fields:
			/*
			 * <responses> <response> <userinfo> <user_id>4711</user_id>
			 * <username>johndoe</username>
			 * <ggt_profile_url>http://tube.geogebra.org/user/profile/id/4711
			 * </ggt_profile_url> <group>user</group>
			 * <date_created>2012-09-18</date_created> <lang_ui>en</lang_ui>
			 * <lang_content>en,en_US,it</lang_content>
			 * <timezone>America/New_York</timezone> <materials>31</materials>
			 * <favorites>4</favorites> <collections>2</collections>
			 * <identifier>forum:0815</identifier> <realname>John Doe</realname>
			 * <occupation>Maths teacher</occupation> <location>New
			 * York</location> <website>www.thisisme.com</website>
			 * <profilemessage>Any text</profilemessage> </userinfo> </response>
			 * </responses>
			 */

			// user.setGGTProfileURL(userinfo.getString("ggt_profile_url"));
			// user.setGroup(userinfo.getString("group"));
			// user.setDateCreated(userinfo.getString("date_created"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Builds the request to check if the login token of a user is valid. This
	 * request will send detailed user information as response.
	 * 
	 * @param user
	 *            The user that should be logged in
	 * @return The JSONObject that contains the request.
	 */
	@Override
	protected String buildTokenLoginRequest(String token, String cookie) {
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

	@Override
	protected String getToken() {
		return client.getModel().getLoginToken();
	}

}
