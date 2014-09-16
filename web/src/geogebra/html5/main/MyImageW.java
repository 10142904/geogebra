package geogebra.html5.main;

import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.MyImage;
import geogebra.html5.gawt.GBufferedImageW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

public class MyImageW implements MyImage {

	private ImageElement img;
	private Canvas canv;
	int width = 0, height = 0;
	private boolean isSVG;

	public MyImageW(ImageElement im, boolean isSVG) {
	    this.img = im;
	    this.isSVG = isSVG;
	    
	    width = img.getWidth();
	    height = img.getHeight();
	    
	    if (width == 0 || height == 0) {
	    	// hack for IE10/11/12
	    	// can't work out SVG height unless it's attached to the DOM
	    	Document.get().getBody().appendChild(img);
	    	width = img.getOffsetWidth();
	    	height = img.getOffsetHeight();
	    	Document.get().getBody().removeChild(img);
	    }
    }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isSVG() {
		return isSVG;
	}

	public GBufferedImage getSubimage(int x, int y, int width, int height) {
		Context2d ctx = getCanvas().getContext2d(); 
	    ImageData imageData = ctx.getImageData(x, y, width, height);
	    return new GBufferedImageW(imageData);
	}

	private Canvas getCanvas() {
		if (canv == null) {
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(img.getWidth());
			canv.setCoordinateSpaceHeight(img.getHeight());
			canv.setWidth(getWidth()+"px");
			canv.setHeight(getWidth()+"px");
			Context2d c2d = canv.getContext2d();
			c2d.drawImage(img,0,0);
		}
		return canv;
	}

	public GGraphics2D createGraphics() {
		return new geogebra.html5.awt.GGraphics2DW(getCanvas(), true);
	}

	public ImageElement getImage() {
	    return img;
    }

}
