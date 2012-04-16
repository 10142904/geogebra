package geogebra.common.factories;

import geogebra.common.util.GeoGebraLogger;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.URLEncoder;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Various util-like factories for common usage
 */
public abstract class UtilFactory {
	/**
	 * UtilFactory prototype for various common utils
	 */
	public static UtilFactory prototype;

	/**
	 * @return HttpRequest object
	 * Creates a HttpRequest object
	 */
	public abstract HttpRequest newHttpRequest();
	/**
	 * @return URLEncoder object
	 * Creates a URLEncoder object
	 */
	public abstract URLEncoder newURLEncoder();
	/**
	 * @return GeoGebraLogger
	 * Creates a GeoGebraLogger object
	 */
	public abstract GeoGebraLogger newGeoGebraLogger();
}
