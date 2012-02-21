package geogebra.common.kernel.geos;

import geogebra.common.awt.BufferedImage;

/**
 * Handles fill image of GeoElement
 * @author Arpad
 */
public abstract class GeoElementGraphicsAdapter {
	/** image filename */
	protected String imageFileName = "";
	/** fill image */
	protected BufferedImage image;

	/**
	 * @return fill image
	 */
	public BufferedImage getImageOnly() {
		return image;
	}

	/**
	 * @param ba new fill image
	 */
	public void setImageOnly(BufferedImage ba) {
		image = ba;
	}

	/**
	 * @param fn new filename
	 */
	public void setImageFileNameOnly(String fn) {
		imageFileName = fn;
	}

	/**
	 * @return filename of fill image
	 */
	public String getImageFileName() {
		return imageFileName;
	}
	
	/**
	 * @return fill image
	 */
	public abstract BufferedImage getFillImage();
	
	/**
	 * @param fileName filename
	 */
	public abstract void setImageFileName(String fileName);

	/**
	 * @param filename filename
	 */
	public void setFillImage(String filename) {
		imageFileName = filename;
		image = null;
	}
	
	
}
