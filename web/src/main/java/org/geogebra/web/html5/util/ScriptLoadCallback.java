package org.geogebra.web.html5.util;

public interface ScriptLoadCallback {

	public void onLoad();

	public void onError();

	public void cancel();

}
