package org.geogebra.common;

/**
 * Application-wide constants (version strings, URLs)
 */
public interface GeoGebraConstants {

	// GeoGebra version
	// DO NOT CHANGE the format of VERSION_STRING (or add commented out version)
	// as it is read by the build system
	// update lines below when this is updated
	/** last build date */
	public static final String BUILD_DATE = "20 February 2016";
	/** complete version string */
	public static final String VERSION_STRING = "5.0.206.0";

	/** true for beta versions/release candidates */
	public static final boolean IS_PRE_RELEASE = false;// VERSION_STRING.startsWith(XML_FILE_FORMAT);

	// proper noun, should NOT be translated / transliterated
	public static final String APPLICATION_NAME = "GeoGebra";

	/**
	 * used by version checker, so that sys admins can disable version checking
	 * for *all* ggb versions with
	 * HKEY_LOCAL_MACHINE/Software/JavaSoft/Prefs/geogebra/version_check_allow =
	 * false
	 * */
	public static final String PREFERENCES_ROOT_GLOBAL = "/geogebra";

	/** eg HKEY_CURRENT_USER/Software/JavaSoft/Prefs/geogebra42/ */
	/** root preferences node */
	public static final String PREFERENCES_ROOT = "/geogebra50";
	/** File format version */
	public static final String XML_FILE_FORMAT = "5.0";

	// This is used for checking if a minor update exists (on each run):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL_MINOR = "http://www.geogebra.org/download/version50.txt";

	// This is used for checking whether a major update exists (monthly):
	// DON'T change to https (causes problems)
	public static final String VERSION_URL = "http://www.geogebra.org/download/version.txt";

	public static final String INSTALLERS_URL = "https://www.geogebra.org/installers";

	/** Splash filename -- used for online */
	public static final String SPLASH_STRING = "splash.png";
	// archive
	/** short version, for online archive */
	public static final String SHORT_VERSION_STRING = "5.0";
	/** true if CAS is enabled */
	public static final boolean CAS_VIEW_ENABLED = true;
	// File format versions
	/** XSD for ggb files */
	public static final String GGB_XSD_FILENAME = "ggb.xsd";
	/** XSD for ggt (macro) files */
	public static final String GGT_XSD_FILENAME = "ggt.xsd";
	// URLs
	/** URL of GeoGebraWeb main js file */
	public static final String GEOGEBRA_HTML5_BASE = "https://www.geogebra.org/web/"
			+ SHORT_VERSION_STRING + "/web/web.nocache.js";
	/** URL of GeoGebraWeb zip file */
	// public static final String GEOGEBRAWEB_ZIP_URL =
	// "http://dev.geogebra.org/download/web/GeoGebraWeb-latest.zip";
	/** Destination filename for GeoGebraWeb zip file */
	public static final String GEOGEBRAWEB_ZIP_LOCAL = "GeoGebraWeb-latest.zip";
	/** URL of GeoGebraWeb main js file (offline version) */
	// public static final String GEOGEBRA_HTML5_BASE_OFFLINE =
	// "web/web.nocache.js";
	/** URL of GeoGebra jars */
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra jars, zipped */
	public static final String GEOGEBRA_ONLINE_JARS_ZIP = GEOGEBRA_ONLINE_ARCHIVE_BASE
			+ "geogebra-jars.zip";
	/** update directory, typically on Windows */
	public static final String GEOGEBRA_JARS_UPDATE_DIR = "\\GeoGebra 5.0\\jars\\update";

	/** update directory, typically on Windows */
	public static final String GEOGEBRA_THIRD_PARTY_UPDATE_DIR = "\\GeoGebra 5.0\\thirdparty\\update";

	/** GeoGebra URL */
	public final static String GEOGEBRA_WEBSITE = "https://www.geogebra.org/";
	/** 4.2 desktop bug reports */
	public final static String GEOGEBRA_REPORT_BUG_DESKTOP = "https://www.geogebra.org/bugs/?v=5.0";
	/** web bug reports */
	public final static String GEOGEBRA_REPORT_BUG_WEB = "https://www.geogebra.org/bugs/?v=web";
	/** GeoGebraTube URL */
	public final static String GEOGEBRATUBE_WEBSITE = "https://tube.geogebra.org/";
	/** GeoGebraTube beta URL, used when Feature.TUBE_BETA == true */
	public final static String GEOGEBRATUBE_WEBSITE_BETA = "https://tube-beta.geogebra.org/";
	/** max possible heap space for applets in MB */
	public final static int MAX_HEAP_SPACE = 1024;

	public static final String URL_PARAM_GGB_FILE = "ggb-file";
	public static final String URL_PARAM_PROXY = "url";
	public static final String PROXY_SERVING_LOCATION = "proxy";
	/** CSS class name for GeoGebraWeb &article> tag */
	public static final String GGM_CLASS_NAME = "geogebraweb";
	/** mimetype of GGB files */
	public static final String GGW_MIME_TYPE = "application/vnd.geogebra.file";

	/** Splash timeout in miliseconds */
	public static final int SPLASH_DIALOG_DELAY = 1000;
	/** team page URL */
	public static final String GGW_ABOUT_TEAM_URL = "https://www.geogebra.org/team";

	/** license URL */
	public static final String GGW_ABOUT_LICENSE_URL = "https://www.geogebra.org/info/?action=AboutLicense";

	// //////////////////////////////////////////////////////////////////////////
	// AUTHENTICATING WITH GOOGLE
	// ///////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////

	/** google auth url */
	public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

	/**
	 * This app's personal client ID assigned by the Google APIs Console
	 * (http://code.google.com/apis/console).
	 */
	public static final String GOOGLE_CLIENT_ID = "656990710877.apps.googleusercontent.com";
	public static final String GOOGLE_TEST_CLIENT_ID = "300173001758.apps.googleusercontent.com";
	public static final String SKYDRIVE_CLIENT_ID = "00000000440E9093";

	// The auth scope being requested. This scope will allow the application to
	// identify who the authenticated user is.
	public static final String PLUS_ME_SCOPE = "https://www.googleapis.com/auth/plus.me";
	public static final String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly";
	public static final String USERINFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	public static final String USERINFO_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
	public static final String API_USERINFO = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=";

	public static final String FORUM_URL = "https://forum.geogebra.org/";
	public static final String APPLET_FOCUSED_CLASSNAME = "applet-focused";
	public static final String APPLET_UNFOCUSED_CLASSNAME = "applet-unfocused";
	public static final String WEB_CUSTOM_HTML_ELEMENT_NAME = "geogebra-web";
	public static final boolean SINGULARWS_ENABLED_BY_DEFAULT = false;

	public static final String DATA_LOGGING_WEBSOCKET_URL = "//data-logger.geogebra.org";
	public static final String DATA_LOGGING_WEBSOCKET_PORT = "80";
	public static final String DATA_LOGGING_WEBSOCKET_SECURE_PORT = "443";

	public static final String QUICKSTART_URL = "https://www.geogebra.org/tutorial/";

	public static int[] VALID_FONT_SIZES = { 12, 14, 16, 18, 20, 24, 28, 32, 48 };

}
