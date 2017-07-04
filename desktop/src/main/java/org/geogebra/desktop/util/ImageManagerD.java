/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.geogebra.common.main.App;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;

/**
 * An ImageManager provides methods for loading images and icons for a JFrame.
 * To save memory every image and icon will be loaded only once.
 * 
 * @author Markus Hohenwarter
 */
public class ImageManagerD extends ImageManager {

	private Hashtable<String, ImageIcon> iconTable = new Hashtable<String, ImageIcon>();
	private Hashtable<String, MyImageD> internalImageTable = new Hashtable<String, MyImageD>();
	private static Hashtable<String, MyImageD> externalImageTable = new Hashtable<String, MyImageD>();

	private Toolkit toolKit;
	private MediaTracker tracker;

	private int maxIconSize = 64;// DEFAULT_ICON_SIZE;

	/**
	 * Creates a new ImageManager for the given JFrame.
	 */
	public ImageManagerD(Component comp) {
		toolKit = Toolkit.getDefaultToolkit();
		tracker = new MediaTracker(comp);
	}

	public void clearAllImages() {
		iconTable.clear();
		internalImageTable.clear();
		externalImageTable.clear();
	}

	/**
	 * Gets the icon specified by fileName.
	 * 
	 * @return icon for fileName or null
	 */
	public ImageIcon getImageIcon(ImageResourceD fileName) {
		return getImageIcon(fileName, null);
	}

	/**
	 * 
	 * @param fileName
	 *            icon filename
	 * @param borderColor
	 *            if borderColor == null no border is added
	 * @return icon
	 */
	public ImageIcon getImageIcon(ImageResourceD fileName, Color borderColor) {
		return getImageIcon(fileName, borderColor, null);
	}

	public ImageIcon getImageIcon(ImageResourceD fileName, Color borderColor,
			Color background) {
		ImageIcon icon = iconTable.get(fileName.getFilename());
		if (icon == null) {
			// load the icon
			Image im = getImageResourceGeoGebra(fileName);
			if (im != null) {
				icon = new ImageIcon(addBorder(im, borderColor, background));
				iconTable.put(fileName.getFilename(), icon);
			}
		}
		return icon;
	}

	// draw a line around the image
	public static Image addBorder(Image im, Color borderColor,
			Color background) {
		if (borderColor == null) {
			return im;
		}

		BufferedImage bim = toBufferedImage(im);
		Graphics g = bim.getGraphics();
		if (background != null) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, bim.getWidth() - 1, bim.getHeight() - 1);
			g.drawImage(im, 0, 0, null);
		}
		g.setColor(borderColor);
		g.drawRect(0, 0, bim.getWidth() - 1, bim.getHeight() - 1);
		return bim;
	}

	/**
	 * Gets the image specified by fileName.
	 * 
	 * @return image for fileName or null
	 */
	public MyImageD getInternalImage(ImageResourceD fileName) {
		MyImageD img = null;
		MyImageD ob = internalImageTable.get(fileName.getFilename());
		if (ob != null) {
			// image already loaded
			img = ob;
		} else {
			// load the image from disk
			Image imgNative = getImageResourceGeoGebra(fileName);
			if (imgNative != null) {
				img = new MyImageD(imgNative);
				internalImageTable.put(fileName.getFilename(), img);
			}
		}
		return img;
	}

	public void addExternalImage(String fileName0, MyImageD img) {
		if (fileName0 != null && img != null) {
			String fileName = fileName0;
			// GIF saved as PNG in .ggb files so need to change extension
			FileExtensions ext = StringUtil.getFileExtension(fileName);
			if (!ext.isAllowedImage()) {
				fileName = StringUtil.changeFileExtension(fileName,
						FileExtensions.PNG);
			}

			fileName = fileName.replace(".GIF", ".png");
			Log.debug("storing " + fileName + " " + img.isSVG());
			externalImageTable.put(fileName, img);
		}
	}

	public static MyImageD getExternalImage(String fileName0) {
		String fileName = fileName0;
		// GIF saved as PNG in .ggb files so need to change extension
		FileExtensions ext = StringUtil.getFileExtension(fileName);
		if (!ext.isAllowedImage()) {
			fileName = StringUtil.changeFileExtension(fileName,
					FileExtensions.PNG);
		}

		// Log.debug("retrieving filename = " + fileName);
		MyImageD ret = externalImageTable.get(fileName);
		// Log.debug("(ret == null)" + (ret == null));
		return ret;
	}

	/*
	 * private class FileNamePair { File file; String name;
	 * 
	 * FileNamePair(File file, String name) { this.file = file; this.name =
	 * name; } }
	 */

	/**
	 * get image for icons and other automatically add "/org/geogebra/desktop"
	 * prefix
	 * 
	 * @param name
	 *            name of the image (without "/org/geogebra/desktop" prefix)
	 * @return the image
	 */
	public Image getImageResourceGeoGebra(ImageResourceD name) {
		Image img = getImageResource(name);
		if (img == null) {
			img = getImageResource(
					"/org/geogebra/desktop" + name.getFilename());
		}

		if (img == null) {
			Log.error("Image " + name.getFilename() + " not found");
		}

		return img;
	}

	public Image getImageResource(ImageResourceD name) {
		return getImageResource(name.getFilename());
	}

	/**
	 * return image from the full path name
	 * 
	 * @param name
	 * @return image from the full path name
	 */
	protected Image getImageResource(String name) {
		String path = name;
		if (!name.startsWith("/org")) {
			path = "/org/geogebra/desktop" + path;
		}
		Image img = null;

		try {
			java.net.URL url = ImageManagerD.class.getResource(path);
			if (url != null) {
				img = toolKit.getImage(url);
				tracker.addImage(img, 0);
				try {
					tracker.waitForAll();
				} catch (InterruptedException e) {
					Log.debug("Interrupted while loading Image: " + path);
				}
				tracker.removeImage(img);
			}
		} catch (Exception e) {
			Log.debug(e.toString());
		}

		return img;
	}

	// This method returns a buffered image with the contents of an image
	public static BufferedImage toBufferedImage(Image image) {
		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		boolean hasAlpha = hasAlpha(image);

		if (hasAlpha) {
			return toBufferedImage(image, Transparency.BITMASK);
		}
		return toBufferedImage(image, Transparency.OPAQUE);

	}

	public static BufferedImage toBufferedImage(Image image0,
			int transparency) {
		if (image0 instanceof BufferedImage) {
			return (BufferedImage) image0;
		}

		// This code ensures that all the pixels in the image are loaded
		Image image = new ImageIcon(image0).getImage();

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type;
			if (transparency == Transparency.OPAQUE) {
				type = BufferedImage.TYPE_INT_RGB;
			} else {
				type = BufferedImage.TYPE_INT_ARGB;
			}

			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	// This method returns true if the specified image has transparent pixels
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();

		if (cm == null) {
			return false;
		}

		return cm.hasAlpha();
	}

	public static ImageIcon getScaledIcon(ImageIcon icon, int width,
			int height) {
		if (icon.getIconWidth() == width && icon.getIconHeight() == height) {
			return icon;
		}
		Image scaledImage = getScaledImage(icon.getImage(), width, height);
		return new ImageIcon(scaledImage);
	}

	public static Image getScaledImage(Image img, int width, int height) {
		// scale image
		BufferedImage scaledImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(img, 0, 0, width, height, null);
		graphics2D.dispose();
		return scaledImage;
	}

	public String createImage(ImageResourceD res, App app) {
		Image im = getImageResource(res);
		BufferedImage image = ImageManagerD.toBufferedImage(im);
		String fileName = ((AppD) app).createImage(new MyImageD(image),
				"tool.png");
		return fileName;
	}

	public ImageResourceD getToolImageResource(String modeText) {
		String filename = "mode_" + StringUtil.toLowerCaseUS(modeText) + ".png";
		String path = getToolbarIconPath() + filename;
		return new ImageResourceDImpl(path);
	}

	public ImageIcon getFlagIcon(String filename) {
		return getImageIcon(
				new ImageResourceDImpl("/gui/menubar/images/" + filename),
				null);
	}

	public String getToolbarIconPath() {
		if (getMaxIconSize() <= 32) {
			return "/org/geogebra/common/icons_toolbar/p32/";
		}

		return "/org/geogebra/common/icons_toolbar/p64/";

	}

	public void setMaxIconSizeAsPt(int points) {
		setMaxIconSize(Math.max(32, points * 2));
	}

	/**
	 * Sets the maximum pixel size (width and height) of all icons in the user
	 * interface. Larger icons are scaled down.
	 * 
	 * @param pixel
	 *            max icon size between 16 and 32 pixels
	 */
	public void setMaxIconSize(int pixel) {
		maxIconSize = Math.min(64, Math.max(16, pixel));
	}

	public int getMaxIconSize() {
		return maxIconSize;
	}
}
