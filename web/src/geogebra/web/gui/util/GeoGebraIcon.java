package geogebra.web.gui.util;


import geogebra.common.awt.GColor;
import geogebra.common.awt.GRenderingHints;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.html5.awt.GArc2DW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.awt.GLine2DW;
import geogebra.html5.awt.GeneralPath;
import geogebra.html5.gui.util.BasicIcons;
import geogebra.html5.openjdk.awt.geom.Polygon;

import java.util.HashMap;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class GeoGebraIcon extends BasicIcons{



	/** creates LineStyle icon
	 * @param dashStyle
	 * @param thickness
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return Canvas with icon drawn
	 */
	public static ImageData createLineStyleIcon(int dashStyle, int thickness, GDimensionW iconSize, GColor fgColor, GColor bgColor) {
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w, h);
	    GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// draw dashed line
		g2.setPaint(fgColor);
		g2.setStroke(EuclidianStatic.getStroke(thickness, dashStyle));
		//g2.getCanvas().getContext2d().setLineWidth(thickness);
		int mid = h / 2;
		g2.drawLine(4, mid, w - 4, mid);

		return g2.getImageData(0, 0, w, h);
    }
	
	public static ImageData createEmptyIcon(int width, int height){

		Canvas image =	getTmpCanvas(width, height);
		image.setWidth(width+"px");
		image.setHeight(height+"px");
		image.setCoordinateSpaceHeight(height);
		image.setCoordinateSpaceWidth(width);
		return image.getContext2d().getImageData(0, 0, width, height);
	}

	/**
	 * @param pointStyle
	 * @param pointSize
	 * @param iconSize
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static ImageData createPointStyleIcon(int pointStyle, int pointSize, GDimensionW iconSize, GColor fgColor, GColor bgColor) {
		GeoGebraIcon g = new GeoGebraIcon();
		PointStyleImage image = new PointStyleImage(iconSize, pointStyle, pointSize,  fgColor,  bgColor);
		return image.getCanvas().getContext2d().getImageData(0, 0, image.getCanvas().getCanvasElement().getWidth(), image.getCanvas().getCanvasElement().getHeight());
    }
	
	public static ImageData createColorSwatchIcon(float alpha, GDimensionW iconSize, GColor fgColor, GColor bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int offset = 2;
		float thickness = 3;

		// if fgColor is null then make it a transparent white
		if(fgColor == null)
			fgColor = geogebra.common.factories.AwtFactory.prototype.newColor(255,255,255,255);
		
		Canvas c = getTmpCanvas(w,h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		--h;
		--w;

		if(bgColor != null){
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// interior fill color using alpha level

		float[] rgb = new float[3];
		fgColor.getRGBColorComponents(rgb);
		g2.setPaint(geogebra.common.factories.AwtFactory.prototype.newColor( rgb[0], rgb[1], rgb[2], alpha));
		g2.fillRect(offset, offset, w-2*offset, h-2*offset);
		
		g2.setPaint(fgColor);
		g2.getCanvas().getContext2d().setLineWidth(thickness);
		g2.drawRect(offset, offset, w-2*offset, h-2*offset);

		return g2.getImageData(0, 0, iconSize.getWidth(), iconSize.getHeight());
	}

	public static ImageData createTextSymbolIcon(String symbol,GFontW font, GDimensionW iconSize, GColor fgColor, GColor bgColor){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w, h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor (fgColor);
		g2.setFont (new GFontW (font.getFontFamily()));

		//fm = g2.getFontMetrics ();
		int symbolWidth = (int) g2.getCanvas().getContext2d().measureText(symbol).getWidth();
		//int ascent = fm.getMaxAscent ();
		//int descent= fm.getMaxDescent ();
		int msg_x = w/2 - symbolWidth/2;
		int msg_y = h/2; //- descent/2 + ascent/2;

		g2.drawString (symbol, msg_x, msg_y-2);
		g2.fillRect(1, h-5, w-1, 3);

		/*ImageIcon ic = new ImageIcon(image);
		//ensureIconSize(ic, iconSize);

		return ic;*/
		return g2.getImageData(0, 0, w, h);
	}
	
	public static ImageData createNullSymbolIcon(int width, int height){

		Canvas c = getTmpCanvas(width, height);

		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(GColor.GRAY);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width-6, height-6);
		int k = 7;
		g2.drawLine(k, k, width-k, height-k);
		g2.drawLine(k, height-k, width-k, k );
		return g2.getImageData(0, 0, width, height);
	}

	public static ImageData createDownTriangleIcon() {
		int h = 18;
		int w = 12;
		
		Canvas icon = getTmpCanvas(w,h);
	    icon.setCoordinateSpaceHeight(h);
	    icon.setCoordinateSpaceWidth(w);
	    GGraphics2DW g2 = new GGraphics2DW(icon);
	    g2.setColor(GColor.WHITE);
	    g2.fillRect(0, 0, g2.getCanvas().getCoordinateSpaceWidth(), g2.getCanvas().getCoordinateSpaceHeight());
	    g2.setColor(GColor.GRAY);
	    
	    int midx = w/2;
		int midy = h/2;
		
		Polygon p = new Polygon();
		p.addPoint(midx-3,midy-3);
		p.addPoint(midx+3,midy-3);
		p.addPoint(midx,midy+3);

		g2.fillPolygon(p);
	    return g2.getImageData(0, 0, w, h);
    }

	public static ImageData createStringIcon(String str, GFontW font, boolean isBold, boolean isItalic, 
			boolean isCentered, GDimensionW iconSize, GColor fgColor, GColor bgColor){
		int h = iconSize.getHeight();
		int w = iconSize.getWidth();

		Canvas c = getTmpCanvas(w,h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		if(bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor (fgColor);
		//font = font.deriveFont((h-6)*1.0f);
		if(isBold)
			font = (GFontW) font.deriveFont(GFontW.BOLD);
		if(isItalic)
			font = (GFontW) font.deriveFont(geogebra.common.awt.GFont.ITALIC);
		g2.setFont (font);


		//FontMetrics fm = g2.getFontMetrics ();
		TextMetrics fm = g2.getCanvas().getContext2d().measureText(str);
		double symbolWidth = fm.getWidth();
		//int ascent = fm.getMaxAscent ();
		//int descent= fm.getMaxDescent ();
		double x = (isCentered) ? w/2 - symbolWidth/2 : 1;
		double mid_y = 0; // there is not easy way to check the height of the text now h/2 - descent/2 + ascent/2 - 1;

		g2.drawString (str,(int) x, (int) mid_y);
		
		return g2.getImageData(0, 0, w, h);
    }


	public static ImageData createFileImageIcon(App app, String url, float alpha, GDimensionW iconSize){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		Canvas c = getTmpCanvas(w,h);
		c.setCoordinateSpaceWidth(w);
		c.setCoordinateSpaceHeight(h);
		Image img = new Image(url);
		int x = (w - img.getWidth()) / 2;
		int y = (h - img.getHeight()) / 2;
		
		c.getContext2d().drawImage(ImageElement.as(img.getElement()), x, y);
		
		return c.getContext2d().getImageData(0, 0, w, h);
	}
	
	public static ImageData ensureIconSize(ImageData icon, GDimensionW iconSize){

		int h = iconSize.getHeight();
		int w = iconSize.getWidth();
		int h2 = icon.getHeight();
		int w2 = icon.getWidth();
		if(h2 == h && w2 == w) 
			return icon;

		int wInset = (w - w2) > 0 ? (w-w2)/2 : 0;
		int hInset = (h - h2) > 0 ? (h-h2)/2 : 0;
		

		GGraphics2DW g2 = new GGraphics2DW(getTmpCanvas(w, h));
		g2.setRenderingHint(GRenderingHints.KEY_ANTIALIASING, GRenderingHints.VALUE_ANTIALIAS_ON);

		try {	
			if(icon !=null){
				g2.putImageData(icon, wInset, hInset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return g2.getImageData(0, 0, w, h);
	}
	
	private static HashMap<String, ImageElement> rightIcons = new HashMap<String, ImageElement>();

	public static ImageData joinIcons(ImageData leftIcon,
            ImageResource rightIcon) {
		int w1 = leftIcon.getWidth();
		int w2 = rightIcon.getWidth();
		int h1 = leftIcon.getHeight();
		int h2 = rightIcon.getHeight();
		int h = Math.max(h1, h2);
		int mid = h/2;
		Canvas c = getTmpCanvas(w1 + w2, h);
		GGraphics2DW g2 = new GGraphics2DW(c);
		g2.putImageData(leftIcon, 0, mid - h1/2);
		String url = rightIcon.getSafeUri().asString();
		if (!rightIcons.containsKey(rightIcon.getSafeUri().asString())) {
			rightIcons.put(url,ImageElement.as(new Image(url).getElement()));
		}
		g2.getCanvas().getContext2d().drawImage(rightIcons.get(url), w1, mid - h2 / 2, w2, h2);

		return g2.getImageData(0, 0, w1 +  w2, h);
    }

	public static ImageData createResourceImageIcon(App app,
            ImageResource res, float alpha, GDimensionW dim) {
	    return createFileImageIcon(app, res.getSafeUri().asString(), alpha, dim);
    }

	private static void drawTick(GLine2DW tick, double angle){
		tick.setLine(13+37*Math.cos(angle),
				27-37*Math.sin(angle),
				13+43*Math.cos(angle),
				27-43*Math.sin(angle));
	}

	public static ImageData createDecorAngleIcon(int id, GDimensionW iconSize){
		int w = iconSize.getWidth();
		int h = iconSize.getHeight();
		GLine2DW tick =new GLine2DW();
		GArc2DW arc =new GArc2DW();
	    GeneralPath polygon = new GeneralPath(); // Michael Borcherds 2007-10-28
		
		
		Canvas c = getTmpCanvas(w, h);
		GGraphics2DW g2 = new GGraphics2DW(c);
	//	if (getBackground()==Color.LIGHT_GRAY) g2.setColor(Color.LIGHT_GRAY); else g2.setColor(Color.WHITE); 
//		 Michael Borcherds 2007-10-13 END
		g2.fillRect(0,0, w, h);
		g2.setColor(GColor.BLACK);
		g2.drawLine(13,27, 67, 27);
		g2.drawLine(13,27,67,3);
		arc.setArcByCenter(13,27,40,0,24,GArc2DW.OPEN);
		g2.draw(arc);
		switch(id){
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				arc.setArcByCenter(13,27,35,0,24,GArc2DW.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				arc.setArcByCenter(13,27,35,0,24,GArc2DW.OPEN);
				g2.draw(arc);
				arc.setArcByCenter(13,27,45,0,24,GArc2DW.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				drawTick(tick, Math.toRadians(12));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				drawTick(tick, Math.toRadians(9.6));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(14.4));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				drawTick(tick, Math.toRadians(12));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(7));
				g2.draw(tick);
				drawTick(tick, Math.toRadians(16));
				g2.draw(tick);
			break;			
//			 Michael Borcherds 2007-11-19 BEGIN
			case GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				polygon.reset();
			    polygon.moveTo(56,15);
			    polygon.lineTo(48,19);
			    polygon.lineTo(50,10);
			    polygon.lineTo(56,15);
			    polygon.closePath();
				g2.fill(polygon);
			break;
			case GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE:
				polygon.reset();
			    polygon.moveTo(54,27);
			    polygon.lineTo(48,20);
			    polygon.lineTo(56,18);
			    polygon.lineTo(54,27);
			    polygon.closePath();
				g2.fill(polygon);
			break;
//			 Michael Borcherds 2007-11-19 END
		}
		return g2.getImageData(0, 0, w, h);
	}
}
