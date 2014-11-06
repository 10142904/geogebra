package geogebra.html5.util;


import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

public class ImageWrapper {
	
	ImageElement img;

	public ImageWrapper(ImageElement imageElement) {
		this.img = imageElement;
    }
	
	/**
	 * @param imageManager
	 * 
	 * Images in gwt event system not loaded when src added, only when attached to dom.
	 * So we must hack it.
	 */
	public void attachNativeLoadHandler(ImageManager imageManager) {
		addNativeLoadHandler(img,imageManager);
	}

	private native void addNativeLoadHandler(Element img, ImageManager imageManager) /*-{
		img.addEventListener("load",function() {
			imageManager.@geogebra.html5.util.ImageManager::checkIfAllLoaded()();
		});
		img.addEventListener("error",function() {
			imageManager.@geogebra.html5.util.ImageManager::checkIfAllLoaded()();
		});
	}-*/;
	
	public ImageElement getElement() {
		return img;
	}
	
	
	public static native void nativeon(ImageElement img, String event, ImageLoadCallback callback) /*-{
		img.addEventListener(event,function() {
			callback.@geogebra.html5.util.ImageLoadCallback::onLoad()();
		});	
	}-*/;
	
	/**
	 * Native event handler
	 * @param event
	 * @param callback
	 */
	public void on(String event, ImageLoadCallback callback) {
		nativeon(getElement(), event, callback);
	}

}
