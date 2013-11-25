package geogebra.html5.util;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.util.AbstractImageManager;
import geogebra.common.util.StringUtil;
import geogebra.html5.io.MyXMLioW;
import geogebra.html5.main.AppWeb;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

public class ImageManager extends AbstractImageManager {
	
	private HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();
	private HashMap<String, String> externalImageSrcs = new HashMap<String, String>();

	public void reset() {
		externalImageTable = new HashMap<String, ImageElement>();
		externalImageSrcs = new HashMap<String, String>();
		imagesLoaded = 0;
		construction = null;
		myXMLio = null;
		app = null;
	}

	@Override
	public String createImage(String filename, App app) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected int imagesLoaded = 0;
	
	ImageLoadCallback callBack = new ImageLoadCallback() {
		
		public void onLoad() {
			imagesLoaded++;
			checkIfAllLoaded();
		}
	};

	private String construction;
	private MyXMLioW myXMLio;
	private AppWeb app = null;

	public void addExternalImage(String fileName, String src) {
	   if (fileName != null && src != null) {
		   String fn = StringUtil.removeLeadingSlash(fileName);
		   ImageElement img = Document.get().createImageElement();
		   externalImageSrcs.put(fn, src);
		   externalImageTable.put(fn, img);
	   }
    }
	
	public String getExternalImageSrc(String fileName){
		return externalImageSrcs.get(StringUtil.removeLeadingSlash(fileName));
	}
	
	protected void checkIfAllLoaded() {
		imagesLoaded++;
		if (imagesLoaded == externalImageSrcs.size()) {
			try {
				App.debug("images loaded");
				myXMLio.processXMLString(construction, true, false);
				app.afterLoadFileAppOrNot();
				imagesLoaded=0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	public ImageElement getExternalImage(String fileName) {
		ImageElement match = externalImageTable.get(StringUtil.removeLeadingSlash(fileName));
		int md5length = app.getMD5folderLength(fileName);
		//FIXME this is a bit hacky: if we did not get precise match, assume encoding problem and rely on MD5
		if(match == null && fileName.length() > md5length){
			String md5 = fileName.substring(0,md5length);
			for(String s:externalImageTable.keySet()){
				if(md5.equals(s.substring(0, md5length))){
					return externalImageTable.get(s);
				}
			}
		}
		return match;
	}

	public static GBufferedImage toBufferedImage(ImageElement im) {
	    return new geogebra.html5.awt.GBufferedImageW(im);
    }

	class ImageLoadCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		public ImageLoadCallback2(GeoImage gi2) {
			this.gi = gi2;
		}
		public void onLoad() {
			gi.updateRepaint();
		}
	}

	class ImageErrorCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		public ImageErrorCallback2(GeoImage gi2) {
			this.gi = gi2;
		}
		public void onLoad() {
			// Image onerror and onabort actually
			gi.getCorner(0).remove();
			gi.getCorner(1).remove();
			gi.remove();
			app.getKernel().notifyRepaint();
		}
	}

	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		ImageElement img = getExternalImage(imageFileName);
		ImageWrapper.nativeon(img, "load", new ImageLoadCallback2(geoi));
		ImageErrorCallback2 i2 = new ImageErrorCallback2(geoi);
		ImageWrapper.nativeon(img, "error", i2);
		ImageWrapper.nativeon(img, "abort", i2);
		img.setSrc(externalImageSrcs.get(imageFileName));
	}

	public void triggerImageLoading(String construction, MyXMLioW myXMLio, AppWeb app) {
		this.construction = construction;
		this.myXMLio = myXMLio;	
		this.app = app;
		if (externalImageSrcs.entrySet() != null) {
			for (Entry<String, String> imgSrc : externalImageSrcs.entrySet()) {
				ImageWrapper img = new ImageWrapper(getExternalImage(imgSrc.getKey())); 
				img.attachNativeLoadHandler(this);	
				img.getElement().setSrc(imgSrc.getValue());
			}
		}
	}
	
	
	/**
	 * @return has images
	 * because of async call of geogebra.xml if images exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	public ImageElement getInternalImage(ImageResource resource) {
	    ImageElement img = Document.get().createImageElement();
	    img.setSrc(resource.getSafeUri().asString());
	    return img;
    }
}
