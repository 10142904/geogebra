package geogebra.html5.move.ggtapi.operations;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import geogebra.common.move.ggtapi.operations.LogInOperation;
import geogebra.common.move.views.BaseEventView;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.AuthenticationModelW;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.util.URLEncoder;


/**
 * The web version of the login operation.
 * uses an own AuthenticationModel and an own implementation of the API
 * 
 * @author stefan
 */
public class LoginOperationW extends LogInOperation {
	
	private AppWeb app;




	/**
	 * Initializes the SignInOperation for Web by creating the corresponding model and view classes
	 * @param appWeb application
	 */
	public LoginOperationW(AppWeb appWeb) {
		super();
		this.app = appWeb;
		setView(new BaseEventView());
		setModel(new AuthenticationModelW(appWeb));
		
		iniNativeEvents();
	}
	
	private native void iniNativeEvents() /*-{
		var t = this;
	    $wnd.addEventListener("message",function(event) {
	    	var data;
	    	//later if event.origin....
	    	if (event.data) {
	    		data = $wnd.JSON.parse(event.data);
	    		if (data.action === "logintoken") {
	    			t.@geogebra.html5.move.ggtapi.operations.LoginOperationW::processToken(Ljava/lang/String;)(data.msg);
	    		}
	    	}
	    }, false);
    }-*/;

	@Override
	public GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return new GeoGebraTubeAPIW(GeoGebraTubeAPI.url, app.getClientInfo());

	}
	
	@Override
	protected String getURLLoginCaller() {
		return "web";
	}

	@Override
	protected String getURLClientInfo() {
		URLEncoder enc = new URLEncoder();
		return enc.encode("GeoGebra Web Application V" + GeoGebraConstants.VERSION_STRING);
	}
	
	
	
	
	//AG: JUST FOR TESTING!
	/*@Override
    public String getLoginURL(String languageCode) {
		return "http://tube-test.geogebra.org:8080/user/login" 
				+ "/caller/"+getURLLoginCaller()
				+"/expiration/"+getURLTokenExpirationMinutes()
				+"/clientinfo/"+getURLClientInfo()
				+"/?lang="+languageCode;
	}*/
	
	private void processToken(String token) {
		App.debug("LTOKEN send via message");
		performTokenLogin(token, false);
	}
}
