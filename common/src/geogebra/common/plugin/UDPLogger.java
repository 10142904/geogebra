package geogebra.common.plugin;

import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;

public interface UDPLogger {

	void stopLogging();

	boolean startLogging();

	void registerGeo(String text, GeoNumeric number);

	void registerGeoList(String text, GeoList list);

}
