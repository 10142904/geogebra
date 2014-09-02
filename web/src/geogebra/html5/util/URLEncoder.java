package geogebra.html5.util;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Implements URL encoding for web
 */
public class URLEncoder extends geogebra.common.util.URLEncoder {

	@Override
    public String encode(String decodedURL) {
		return com.google.gwt.http.client.URL.encode(decodedURL);
	}

}
