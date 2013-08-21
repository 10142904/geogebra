package geogebra.web.factories;

import geogebra.common.factories.UtilFactory;
import geogebra.common.util.HttpRequest;
import geogebra.common.util.Prover;
import geogebra.common.util.URLEncoder;
import geogebra.common.util.debug.Log;

/**
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 * Web implementations for various utils
 */
public class UtilFactoryW extends UtilFactory {

	@Override
    public HttpRequest newHttpRequest() {
		return new geogebra.web.util.HttpRequest();
	}

	@Override
    public URLEncoder newURLEncoder() {
	    return new geogebra.web.util.URLEncoder();
    }

	@Override
    public Log newGeoGebraLogger() {
		return new geogebra.html5.util.debug.GeoGebraLogger();
    }
	
	@Override
    public Prover newProver() {
		return new geogebra.web.util.Prover();
    }
}
