package geogebra.common;
/**
 * Application-wide constants (version strings, URLs)
 */
public interface GeoGebraConstants {

	// GeoGebra version
	// DO NOT CHANGE the format of VERSION_STRING (or add commented out version)
	// as it is read by the build system
	// update lines below when this is updated
	public static final String BUILD_DATE = "04 May 2012";
	public static final String VERSION_STRING = "4.1.73.0"; // <- update lines
															// below when this
															// is updated
	// current 3D: "4.9.20.0"
	// current ggb42: "4.1.72.0"

	/** used by version checker, so that sys admins can disable version checking for *all* ggb versions with
	 * HKEY_LOCAL_MACHINE/Software/JavaSoft/Prefs/geogebra42/version_check_allow = false
	 * */
	public static final String PREFERENCES_ROOT_GLOBAL = "/geogebra";
	
	//********* start hacks TODO remove it when branched  *************
	/** eg HKEY_CURRENT_USER/Software/JavaSoft/Prefs/geogebra42/ */
	public static final String PREFERENCES_ROOT = VERSION_STRING
			.startsWith("4.9") ? "/geogebra50" : "/geogebra42";
	/** File format version */
	public static final String XML_FILE_FORMAT = VERSION_STRING
			.startsWith("4.9") ? "5.0" : "4.2";
	/* end hacks */
	/** Splash filename -- used for online */
	public static final String SPLASH_STRING = "splash42beta.png";
	// archive
	/** short version, for online archive */
	public static final String SHORT_VERSION_STRING = "4.2";
	/** true if CAS is enabled*/
	public static final boolean CAS_VIEW_ENABLED = true;
	/** true for beta versions/release candidates*/
	public static final boolean IS_PRE_RELEASE = true; // !VERSION_STRING.endsWith(".0");
	// File format versions
	/** XSD for ggb files*/
	public static final String GGB_XSD_FILENAME = "ggb.xsd"; 
	/** XSD for ggt (macro) files */
	public static final String GGT_XSD_FILENAME = "ggt.xsd";
	// URLs
	/** URL of GeoGebraWeb main js file */
	public static final String GEOGEBRA_HTML5_BASE = "http://www.geogebra.org/web/" + SHORT_VERSION_STRING +
			"/web/web.nocache.js";
	/** URL of GeoGebraWeb zip file */
	public static final String GEOGEBRAWEB_ZIP_URL = "http://dev.geogebra.org/download/web/GeoGebraWeb-latest.zip";
	/** Destination filename for GeoGebraWeb zip file */
	public static final String GEOGEBRAWEB_ZIP_LOCAL = "GeoGebraWeb-latest.zip";
	/** URL of GeoGebraWeb main js file (offline version) */
	public static final String GEOGEBRA_HTML5_BASE_OFFLINE = "web/web.nocache.js"; 
	/** URL of GeoGebra jars */
	public final static String GEOGEBRA_ONLINE_ARCHIVE_BASE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra Webstart (to check whether we are running webstart)*/
	public final static String GEOGEBRA_ONLINE_WEBSTART_BASE = "http://www.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of GeoGebra Webstart (to check whether we are running debug webstart)*/
	public final static String GEOGEBRA_ONLINE_WEBSTART_BASE_ALTERNATIVE = "http://jars.geogebra.org/webstart/"
			+ SHORT_VERSION_STRING + "/";
	/** URL of loading gif*/
	public static final String LOADING_GIF = "http://www.geogebra.org/webstart/loading.gif";
	/** GeoGebra URL*/
	public final static String GEOGEBRA_WEBSITE = "http://www.geogebra.org/";
	/** Help (wiki) URL */
	public final static String HELP_URL = GEOGEBRA_WEBSITE + "help";
	/** GeoGebraTube URL */
	public final static String GEOGEBRATUBE_WEBSITE = "http://www.geogebratube.org/";
	/** max possible heap space for applets in MB */
	public final static int MAX_HEAP_SPACE = 512;
	
	public static final String URL_PARAM_GGB_FILE = "ggb-file";
	public static final String URL_PARAM_PROXY = "url";
	public static final String PROXY_SERVING_LOCATION = "proxy";
	/** CSS class name for GeoGebraWeb &article> tag*/
	public static final String GGM_CLASS_NAME = "geogebraweb";
	
	/** relative path to mathml*/
	public static final String MATHML_URL = "js/mathml_concat.js";
	/** Splash timeout in miliseconds */
	public static final int SPLASH_DIALOG_DELAY = 1000;

}