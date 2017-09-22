package org.geogebra.web.html5.util;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;

public class ImageManagerW extends ImageManager {

	private HashMap<String, ImageElement> externalImageTable = new HashMap<String, ImageElement>();
	private HashMap<String, String> externalImageSrcs = new HashMap<String, String>();
	private boolean preventAuxImage;
	protected int imagesLoaded = 0;
	private String construction, defaults2d, defaults3d, macros;
	private MyXMLio myXMLio;

	public void reset() {
		externalImageTable = new HashMap<String, ImageElement>();
		externalImageSrcs = new HashMap<String, String>();
		imagesLoaded = 0;
		construction = null;
		macros = null;
		defaults2d = null;
		defaults3d = null;
		myXMLio = null;
	}

	public void addExternalImage(String fileName, String src) {
		if (fileName != null && src != null) {
			Log.debug("addExternalImage: " + fileName);
			String fn = StringUtil.removeLeadingSlash(fileName);
			ImageElement img = Document.get().createImageElement();
			externalImageSrcs.put(fn, src);
			externalImageTable.put(fn, img);
		}
	}

	public String getExternalImageSrc(String fileName) {
		return externalImageSrcs.get(StringUtil.removeLeadingSlash(fileName));
	}

	protected void checkIfAllLoaded(AppW app1) {
		imagesLoaded++;
		if (imagesLoaded == externalImageSrcs.size()) {
			try {
				Log.debug("images loaded");
				// Macros (optional)
				if (macros != null) {
					// macros = DataUtil.utf8Decode(macros);
					// //DataUtil.utf8Decode(macros);
					myXMLio.processXMLString(macros, true, true);
				}

				myXMLio.processXMLString(construction, true, false);
				// defaults (optional)
				if (defaults2d != null) {
					myXMLio.processXMLString(defaults2d, false, true);
				}
				if (defaults3d != null) {
					myXMLio.processXMLString(defaults3d, false, true);
				}
				app1.afterLoadFileAppOrNot();
				imagesLoaded = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ImageElement getExternalImage(String fileName, AppW app1) {
		ImageElement match = externalImageTable.get(StringUtil
		        .removeLeadingSlash(fileName));
		// FIXME this is a bit hacky: if we did not get precise match, assume
		// encoding problem and rely on MD5
		if (match == null
				&& fileName.length() > app1.getMD5folderLength(fileName)) {
			int md5length = app1.getMD5folderLength(fileName);
			String md5 = fileName.substring(0, md5length);
			for (Entry<String, ImageElement> entry : externalImageTable
					.entrySet()) {
				String s = entry.getKey();
				if (md5.equals(s.substring(0, md5length))) {
					return entry.getValue();
				}
			}
		}
		return match;
	}

	public static GBufferedImage toBufferedImage(ImageElement im) {
		return new GBufferedImageW(im);
	}

	static class ImageLoadCallback2 implements ImageLoadCallback {
		public GeoImage gi;

		public ImageLoadCallback2(GeoImage gi2) {
			this.gi = gi2;
		}

		@Override
		public void onLoad() {
			gi.updateRepaint();
		}
	}

	static class ImageErrorCallback2 implements ImageLoadCallback {
		public GeoImage gi;
		private AppW app;

		public ImageErrorCallback2(GeoImage gi2, AppW app) {
			this.gi = gi2;
			this.app = app;
		}

		@Override
		public void onLoad() {
			// Image onerror and onabort actually
			gi.getCorner(0).remove();
			gi.getCorner(1).remove();
			gi.remove();
			app.getKernel().notifyRepaint();
		}
	}

	public void triggerSingleImageLoading(String imageFileName, GeoImage geoi) {
		ImageElement img = getExternalImage(imageFileName, (AppW) geoi
				.getKernel().getApplication());
		ImageWrapper.nativeon(img, "load", new ImageLoadCallback2(geoi));
		ImageErrorCallback2 i2 = new ImageErrorCallback2(geoi, (AppW) geoi
				.getKernel().getApplication());
		ImageWrapper.nativeon(img, "error", i2);
		ImageWrapper.nativeon(img, "abort", i2);
		img.setSrc(externalImageSrcs.get(imageFileName));
	}

	public void triggerImageLoading(String construction, String defaults2d,
			String defaults3d, String macros, MyXMLio myXMLio, final AppW app) {
		this.construction = construction;
		this.defaults2d = defaults2d;
		this.defaults3d = defaults3d;
		this.macros = macros;
		this.myXMLio = myXMLio;

		if (externalImageSrcs.entrySet() != null) {
			for (Entry<String, String> imgSrc : externalImageSrcs.entrySet()) {
				ImageWrapper img = new ImageWrapper(
						getExternalImage(imgSrc.getKey(), app));
				img.attachNativeLoadHandler(this, new ImageLoadCallback() {

					@Override
					public void onLoad() {
						checkIfAllLoaded(app);

					}
				});
				img.getElement().setSrc(imgSrc.getValue());
			}
		}
	}

	/**
	 * @return has images because of async call of geogebra.xml if images
	 *         exists, but not loaded yet.
	 */
	public boolean hasImages() {
		return !externalImageTable.isEmpty();
	}

	public static ImageElement getInternalImage(ImageResource resource) {
		ImageElement img = Document.get().createImageElement();
		img.setSrc(resource.getSafeUri().asString());
		return img;
	}

	public void replace(String fileName, String newName) {
		if (fileName.equals(newName)) {
			return;
		}
		ImageElement el = this.externalImageTable.get(fileName);
		String src = this.externalImageSrcs.get(fileName);

		this.externalImageTable.put(newName, el);
		this.externalImageSrcs.put(newName, src);
	}

	public String getErrorURL() {
		return GuiResourcesSimple.INSTANCE.questionMark().getSafeUri()
		        .asString();
	}

	public boolean isPreventAuxImage() {
		if (preventAuxImage) {
			preventAuxImage = false;
			return true;
		}
		return false;
	}

	public void setPreventAuxImage(boolean value) {
		this.preventAuxImage = value;
	}
}
