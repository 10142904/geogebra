package org.geogebra.desktop.awt;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GBufferedImageOp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GImage;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;

import com.kitfox.svg.SVGException;

/**
 * Desktop implementation of Graphics2D; wraps the java.awt.Graphics2D class
 * 
 * @author kondr
 * 
 */
public class GGraphics2DD implements GGraphics2D {
	private java.awt.Graphics2D impl;

	public GGraphics2DD(java.awt.Graphics2D g2Dtemp) {
		impl = g2Dtemp;
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		impl.draw3DRect(x, y, width, height, raised);

	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		impl.fill3DRect(x, y, width, height, raised);
	}

	public void drawString(String str, int x, int y) {
		impl.drawString(str, x, y);

	}

	public void drawString(String str, float x, float y) {
		impl.drawString(str, x, y);

	}

	public void setComposite(GComposite comp) {
		impl.setComposite(org.geogebra.desktop.awt.GCompositeD.getAwtComposite(comp));
	}

	public void setPaint(GPaint paint) {
		if (paint instanceof GColor) {
			impl.setPaint(GColorD.getAwtColor((GColor) paint));
		} else if (paint instanceof GGradientPaintD) {
			impl.setPaint(((GGradientPaintD) paint).getPaint());
			return;
		} else if (paint instanceof GTexturePaintD) {
			impl.setPaint(((GTexturePaintD) paint).getPaint());
			return;
		} else {
			Log.error("unknown paint type");
		}

	}

	private Key getAwtHintKey(int key) {

		switch (key) {
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.KEY_ANTIALIASING:
			return RenderingHints.KEY_ANTIALIASING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.KEY_RENDERING:
			return RenderingHints.KEY_RENDERING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.KEY_TEXT_ANTIALIASING:
			return RenderingHints.KEY_TEXT_ANTIALIASING;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.KEY_INTERPOLATION:
			return RenderingHints.KEY_INTERPOLATION;
		}

		return null;
	}

	private Object getAwtHintValue(int value) {

		switch (value) {
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_ANTIALIAS_ON:
			return RenderingHints.VALUE_ANTIALIAS_ON;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_RENDER_QUALITY:
			return RenderingHints.VALUE_RENDER_QUALITY;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_TEXT_ANTIALIAS_ON:
			return RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_INTERPOLATION_BILINEAR:
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR:
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		case com.himamis.retex.renderer.share.platform.graphics.RenderingHints.VALUE_INTERPOLATION_BICUBIC:
			return RenderingHints.VALUE_INTERPOLATION_BICUBIC;

		}

		return null;
	}

	public void setRenderingHint(int key, int value) {
		impl.setRenderingHint(getAwtHintKey(key), getAwtHintValue(value));
	}

	public void translate(int x, int y) {
		impl.translate(x, y);

	}

	public void translate(double tx, double ty) {
		impl.translate(tx, ty);

	}

	public void rotate(double theta) {
		impl.rotate(theta);

	}

	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);

	}

	public void scale(double sx, double sy) {
		impl.scale(sx, sy);

	}

	public void shear(double shx, double shy) {
		impl.shear(shx, shy);

	}

	public void transform(GAffineTransform Tx) {
		impl.transform(org.geogebra.desktop.awt.GAffineTransformD.getAwtAffineTransform(Tx));

	}

	public GPaint getPaint() {
		java.awt.Paint paint = impl.getPaint();
		if (paint instanceof java.awt.Color)
			return new org.geogebra.desktop.awt.GColorD((java.awt.Color) paint);
		else if (paint instanceof java.awt.GradientPaint)
			return new org.geogebra.desktop.awt.GGradientPaintD(
					(java.awt.GradientPaint) paint);

		// other types of paint are currently not used in setPaint
		return null;
	}

	public GComposite getComposite() {
		return new org.geogebra.desktop.awt.GCompositeD(impl.getComposite());
	}

	public void setBackground(GColor color) {
		impl.setBackground(org.geogebra.desktop.awt.GColorD.getAwtColor(color));
	}

	public GColor getBackground() {
		return new org.geogebra.desktop.awt.GColorD(impl.getBackground());
	}

	public GFontRenderContext getFontRenderContext() {
		return new org.geogebra.desktop.awt.GFontRenderContextD(impl.getFontRenderContext());
	}

	public GColor getColor() {
		return new org.geogebra.desktop.awt.GColorD(impl.getColor());
	}

	public GFont getFont() {
		return new org.geogebra.desktop.awt.GFontD(impl.getFont());
	}

	public static java.awt.Graphics2D getAwtGraphics(
			org.geogebra.common.awt.GGraphics2D g2) {
		return ((GGraphics2DD) g2).impl;
	}

	public void setFont(GFont font) {
		impl.setFont(org.geogebra.desktop.awt.GFontD.getAwtFont(font));

	}

	public void setStroke(GBasicStroke s) {
		impl.setStroke(org.geogebra.desktop.awt.GBasicStrokeD.getAwtStroke(s));

	}

	public void setColor(GColor selColor) {
		impl.setColor(org.geogebra.desktop.awt.GColorD.getAwtColor(selColor));

	}

	public GBasicStroke getStroke() {
		return (org.geogebra.desktop.awt.GBasicStrokeD) impl.getStroke();
	}

	public void clip(org.geogebra.common.awt.GShape shape) {
		impl.clip(((org.geogebra.desktop.awt.GShapeD) shape).getAwtShape());
	}

	public void drawImage(GBufferedImage img, GBufferedImageOp op, int x, int y) {
		impl.drawImage(org.geogebra.desktop.awt.GBufferedImageD.getAwtBufferedImage(img),
				(org.geogebra.desktop.awt.GBufferedImageOpD) op, x, y);
	}

	public void drawImage(MyImage img, GBufferedImageOp op, int x, int y) {
		impl.drawImage((BufferedImage) ((MyImageD) img).getImage(),
				(org.geogebra.desktop.awt.GBufferedImageOpD) op, x, y);
	}

	public void drawImage(GBufferedImage img, int x, int y) {
		impl.drawImage(GBufferedImageD.getAwtBufferedImage(img), x, y, null);
	}

	public void drawImage(MyImage img, int x, int y) {

		MyImageD imgD = (MyImageD) img;

		if (imgD.isSVG()) {
			try {
				// TODO: x, y
				imgD.getDiagram().render(impl);
			} catch (SVGException e) {
				e.printStackTrace();
			}
		} else {
			impl.drawImage(imgD.getImage(), x, y, null);
		}

	}

	public void drawImage(GImage img, int x, int y) {
		impl.drawImage(GGenericImageD.getAwtImage(img), x, y, null);

	}

	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	public void clearRect(int x, int y, int width, int height) {
		impl.clearRect(x, y, width, height);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		impl.drawLine(x1, y1, x2, y2);
	}

	public void setClip(org.geogebra.common.awt.GShape shape) {
		if (shape == null) {
			impl.setClip(null);
		} else if (shape instanceof org.geogebra.desktop.awt.GShapeD) {
			impl.setClip(org.geogebra.desktop.awt.GGenericShapeD.getAwtShape(shape));
		}
	}

	public void draw(org.geogebra.common.awt.GShape s) {
		if (s instanceof org.geogebra.desktop.awt.GShapeD)
			impl.draw(((org.geogebra.desktop.awt.GShapeD) s).getAwtShape());
		if (s instanceof GeneralPathClipped)
			impl.draw(org.geogebra.desktop.awt.GGeneralPathD
					.getAwtGeneralPath(((GeneralPathClipped) s)
							.getGeneralPath()));

	}

	public void fill(org.geogebra.common.awt.GShape s) {
		if (s instanceof org.geogebra.desktop.awt.GShapeD)
			impl.fill(((org.geogebra.desktop.awt.GShapeD) s).getAwtShape());
		if (s instanceof GeneralPathClipped)
			impl.fill(org.geogebra.desktop.awt.GGeneralPathD
					.getAwtGeneralPath(((GeneralPathClipped) s)
							.getGeneralPath()));
		if (s instanceof GPolygonD) {
			getAwtGraphics(this).fillPolygon(((GPolygonD) s).getPolygon());
		}
	}

	public GShape getClip() {
		return new GGenericShapeD(impl.getClip());
	}

	public void drawRect(int x, int y, int width, int height) {
		impl.drawRect(x, y, width, height);

	}

	public void setClip(int x, int y, int width, int height) {
		impl.setClip(x, y, width, height);

	}

	public void setImpl(java.awt.Graphics2D g) {
		impl = g;
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		impl.drawRoundRect(x, y, width, height, arcWidth, arcHeight);

	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		impl.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

	}

	public void setAntialiasing() {
		setAntialiasing(impl);
	}

	public static void setAntialiasing(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	public void setTransparent() {
		impl.setComposite(AlphaComposite.Src);

	}

	public void drawWithValueStrokePure(GShape shape) {
		Object oldHint = impl
				.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		impl.draw(org.geogebra.desktop.awt.GGenericShapeD.getAwtShape(shape));
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);

	}

	public void fillWithValueStrokePure(GShape shape) {
		Object oldHint = impl
				.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		impl.fill(org.geogebra.desktop.awt.GGenericShapeD.getAwtShape(shape));
		impl.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);

	}

	public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
		java.awt.Graphics2D g2 = impl;
		Object oldInterpolationHint = g2
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

		if (oldInterpolationHint == null)
			oldInterpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		if (needsInterpolationRenderingHint) {
			// improve rendering quality for transformed images
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		return oldInterpolationHint;
	}

	public void resetInterpolationHint(Object hint) {
		impl.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	}

	@Override
	public void updateCanvasColor() {
		// TODO Auto-generated method stub

	}

	private GLine2D line = AwtFactory.prototype.newLine2D();

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		line.setLine(x1, y1, x2, y2);
		this.draw(line);
	}

	public void dispose() {
		impl.dispose();
	}

	public void drawImage(MyImageD img, int x, int y, int width, int height,
			ImageObserver io) {
		if (img.isSVG()) {
			try {
				// TODO: scaling
				img.getDiagram().render(impl);
			} catch (SVGException e) {
				e.printStackTrace();
			}
		} else {
			impl.drawImage(img.getImage(), x, y, width, height, io);
		}

	}

	private AffineTransform affineTransform;

	@Override
	public void saveTransform() {
		affineTransform = impl.getTransform();
	}

	@Override
	public void restoreTransform() {
		if (affineTransform == null) {
			throw new RuntimeException("Save transform was not called!");
		}
		impl.setTransform(affineTransform);
		affineTransform = null;
	}

}
