package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.GPaintSVG;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.GeneralPath;
import org.geogebra.ggbjdk.java.awt.geom.Path2D;
import org.geogebra.ggbjdk.java.awt.geom.Shape;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.Repetition;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class GGraphics2DW implements GGraphics2D {

	protected final Canvas canvas;
	private final JLMContext2d context;

	private GFontW currentFont = new GFontW("normal");
	private GColor color = GColor.newColor(255, 255, 255, 255);

	GPaint currentPaint = GColor.newColor(255, 255, 255, 255);

	private double[] dash_array = null;
	private boolean nativeDashUsed = false;
	private JsArrayNumber jsarrn;

	private int canvasWidth;
	private int canvasHeight;

	private View view;

	private boolean lastDebugOk = false;
	private boolean lastDebugException = false;

	/**
	 * the pixel ratio of the canvas.
	 */
	public double devicePixelRatio = 1;

	private double[] coords = new double[6];

	/**
	 * @param canvas
	 */
	public GGraphics2DW(Canvas canvas) {
		this.canvas = canvas;
		setDirection();
		this.context = (JLMContext2d) canvas.getContext2d();
		this.context.initTransform();
		preventContextMenu(canvas.getElement());
	}

	/*
	 * GGB-1780 special method for SVG export this.canvas not set
	 */
	public GGraphics2DW(Context2d ctx) {
		// HACK
		this.canvas = null;
		// could also try this if necessary
		// this.canvas = Canvas.createIfSupported();

		this.context = (JLMContext2d) ctx.cast();
		this.context.initTransform();
	}

	/**
	 * @param view
	 *            The view associated with this instance of GGraphics2DW
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * @return The view associated with this instance of GGraphics2DW, null if
	 *         no view has been set
	 */
	public View getView() {
		return view;
	}

	public void setImageInterpolation(boolean b) {
		// canvas.getContext2d() doesn't work with canvas2svg.js
		try {
			setImageInterpolationNative(canvas.getContext2d(), b);
		} catch (Exception e) {
			// do nothing
		}
	}

	private native void setImageInterpolationNative(Context2d ctx, boolean b) /*-{

		ctx['imageSmoothingEnabled'] = b;
		ctx['mozImageSmoothingEnabled'] = b;
		// IE11+ only
		ctx['msImageSmoothingEnabled'] = b;
		ctx['oImageSmoothingEnabled'] = b;
		// deprecated
		// https://groups.google.com/a/chromium.org/forum/#!msg/blink-dev/Ud3cV1mj35s/Ssat21OeRqYJ
		// ctx['webkitImageSmoothingEnabled'] = b;

	}-*/;

	/**
	 * If we allow right-to left direction * checkboxes have their labels to the
	 * right * labels are drawn to the right, hence the check to fit in screen
	 * will probably fail * labels are malformed, eg )A=(1,2
	 */
	private void setDirection() {
		this.canvas.getElement().setDir("ltr");
	}

	public GGraphics2DW(Canvas canvas, boolean resetColor) {
		this(canvas);
		if (resetColor) {
			updateCanvasColor();
		}
	}

	private native void preventContextMenu(Element canvas) /*-{
		canvas.addEventListener("contextmenu", function(e) {
			e.preventDefault();
			e.stopPropagation();
			return false;
		});
	}-*/;

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		int width = (int) context.getLineWidth();
		context.beginPath();

		if (MyDouble.isOdd(width)) {
			context.moveTo(Math.floor(x1) + 0.5, Math.floor(y1) + 0.5);
			context.lineTo(Math.floor(x2) + 0.5, Math.floor(y2) + 0.5);
		} else {
			context.moveTo(Math.round(x1), Math.round(y1));
			context.lineTo(Math.round(x2), Math.round(y2));
		}

		context.stroke();
	}

	@Override
	public void startGeneralPath() {
		context.beginPath();
	}

	@Override
	public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
		int width = (int) context.getLineWidth();
		if (MyDouble.isOdd(width)) {
			context.moveTo(Math.floor(x1) + 0.5, Math.floor(y1) + 0.5);
			context.lineTo(Math.floor(x2) + 0.5, Math.floor(y2) + 0.5);
		} else {
			context.moveTo(Math.round(x1), Math.round(y1));
			context.lineTo(Math.round(x2), Math.round(y2));
		}
	}

	@Override
	public void endAndDrawGeneralPath() {
		context.stroke();
	}

	protected void doDrawShape(Shape shape) {
		context.beginPath();
		GPathIterator it = shape.getPathIterator(null);

		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			default:
				// do nothing
				break;
			case GPathIterator.SEG_MOVETO:
				context.moveTo(coords[0], coords[1]);
				break;
			case GPathIterator.SEG_LINETO:
				context.lineTo(coords[0], coords[1]);
				break;
			case GPathIterator.SEG_CUBICTO:
				context.bezierCurveTo(coords[0], coords[1], coords[2],
				        coords[3], coords[4], coords[5]);
				break;
			case GPathIterator.SEG_QUADTO:
				context.quadraticCurveTo(coords[0], coords[1], coords[2],
				        coords[3]);
				break;
			case GPathIterator.SEG_CLOSE:
				context.closePath();
			}
			it.next();
		}
		// this.closePath();
	}

	@Override
	public void drawString(String str, int x, int y) {
		context.fillText(str, x, y);
	}

	@Override
	public void drawString(String str, double x, double y) {
		context.fillText(str, x, y);
	}

	@Override
	public void drawStringStroke(String str, double x, double y) {
		context.strokeText(str, x, y);
	}

	@Override
	public void setStrokeLineWidth(double w) {
		context.setLineWidth(w);
	}

	@Override
	public void setComposite(GComposite comp) {
		context.setGlobalAlpha(((GAlphaCompositeW) comp).getAlpha());
	}

	public void setPaintSVG(final GPaintSVG svgPaint, double lineWidth) {

		CanvasPattern ptr = context.createPatternSVG(
				svgPaint.getPath(), svgPaint.getStyle(), svgPaint.getWidth(),
				svgPaint.getHeight(),
				Math.round(svgPaint.getAngle() * 180 / Math.PI),
				svgPaint.getFill());
		// "stroke:black; stroke-width:1", 69.2820323028, 120);

		context.setFillStyle(ptr);

	}

	@Override
	public void setPaint(final GPaint paint) {

		if (paint instanceof GPaintSVG) {
			setPaintSVG((GPaintSVG) paint, context.getLineWidth());
			return;
		}

		if (paint instanceof GColor) {
			setColor((GColor) paint);
		} else if (paint instanceof GGradientPaintW) {
			context.setFillStyle(((GGradientPaintW) paint).getGradient(context));
			currentPaint = new GGradientPaintW((GGradientPaintW) paint);
			color = null;
		} else if (paint instanceof GTexturePaintW) {
			try {// bug in Firefox
				 // https://groups.google.com/forum/#!msg/craftyjs/3qRwn_cW1gs/DdPTaCD81ikJ
				 // NS_ERROR_NOT_AVAILABLE: Component is not available
				 // https://bugzilla.mozilla.org/show_bug.cgi?id=574330
				final GBufferedImageW bi = ((GTexturePaintW) paint).getImg();
				CanvasPattern ptr;
				if (bi.hasCanvas()) {
					currentPaint = new GTexturePaintW((GTexturePaintW) paint);
					ptr = context.createPattern(bi.getCanvas()
					        .getCanvasElement(), Repetition.REPEAT);
					context.setFillStyle(ptr);
					color = null;
				} else if (bi.isLoaded()) {
					currentPaint = new GTexturePaintW((GTexturePaintW) paint);
					ptr = context.createPattern(bi.getImageElement(),
					        Repetition.REPEAT);
					context.setFillStyle(ptr);
					color = null;
				} else {
					ImageWrapper.nativeon(bi.getImageElement(), "load",
					        new ImageLoadCallback() {
						        @Override
								public void onLoad() {
							        currentPaint = new GTexturePaintW(
							                (GTexturePaintW) paint);
									CanvasPattern ptr1 = context.createPattern(
							                bi.getImageElement(),
							                Repetition.REPEAT);
									context.setFillStyle(ptr1);
							        color = null;
						        }
					        });
				}
			} catch (Throwable e) {
				Log.error(e.getMessage());
			}
		} else {
			Log.error("unknown paint type");
		}
	}

	@Override
	public void setStroke(GBasicStroke stroke) {
		if (stroke != null) {
			context.setLineWidth(stroke.getLineWidth());
			context.setLineCap(((GBasicStrokeW) stroke).getEndCapString());
			context.setLineJoin(((GBasicStrokeW) stroke).getLineJoinString());

			double[] dasharr = stroke.getDashArray();
			if (dasharr != null) {
				jsarrn = JavaScriptObject.createArray().cast();
				jsarrn.setLength(dasharr.length);
				for (int i = 0; i < dasharr.length; i++) {
					jsarrn.set(i, dasharr[i]);
				}
				setStrokeDash(context, jsarrn);
			} else {
				setStrokeDash(context, null);
			}
			dash_array = dasharr;
		}
	}

	public native void setStrokeDash(Context2d ctx, JsArrayNumber dasharray) /*-{
		if (dasharray === undefined || dasharray === null) {
			dasharray = [];
		}

		if (typeof ctx.setLineDash === 'function') {
			ctx.setLineDash(dasharray);
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		} else if (typeof ctx.mozDash !== 'undefined') {
			ctx.mozDash = dasharray;
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		} else if (typeof ctx.webkitLineDash !== 'undefined') {
			ctx.webkitLineDash = dasharray;
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		}

	}-*/;

	@Override
	public void setRenderingHint(int hintKey, int hintValue) {
		// nothing to do

	}

	@Override
	public void translate(double tx, double ty) {
		context.translate2(tx, ty);

	}

	@Override
	public void scale(double sx, double sy) {
		context.scale2(sx, sy);
	}

	@Override
	public void transform(GAffineTransform Tx) {
		context.transform2(Tx.getScaleX(), Tx.getShearY(), Tx.getShearX(),
				Tx.getScaleY(), Tx.getTranslateX(),
				Tx.getTranslateY());
	}

	private void setTransform(GAffineTransform Tx) {

		context.setDevicePixelRatio(devicePixelRatio);

		context.setTransform2(devicePixelRatio * Tx.getScaleX(),
				devicePixelRatio * Tx.getShearY(),
				devicePixelRatio * Tx.getShearX(),
				devicePixelRatio * Tx.getScaleY(),
				devicePixelRatio * Tx.getTranslateX(),
				devicePixelRatio * Tx.getTranslateY());

	}

	@Override
	public GComposite getComposite() {
		return new GAlphaCompositeW(context.getGlobalAlpha());
	}

	@Override
	public GColor getBackground() {
		return GColor.WHITE;
	}

	@Override
	public GBasicStroke getStroke() {

		return new GBasicStrokeW(context.getLineWidth(),
		        GBasicStrokeW.getCap(context.getLineCap()),
				GBasicStrokeW.getJoin(context.getLineJoin()), 0, dash_array);
	}

	@Override
	public GFontRenderContext getFontRenderContext() {
		return new GFontRenderContextW(context);
	}

	@Override
	public GColor getColor() {
		return color;
	}

	@Override
	public GFontW getFont() {
		return currentFont;
	}

	private int physicalPX(int logicalPX) {
		return (int) (logicalPX * devicePixelRatio);
	}
	public void setCoordinateSpaceSize(int width, int height) {
		canvas.setCoordinateSpaceWidth(physicalPX(width));
		canvas.setCoordinateSpaceHeight(physicalPX(height));

		context.resetTransform(devicePixelRatio);

		setWidth(width);
		setHeight(height);
		this.updateCanvasColor();
	}

	public void setCoordinateSpaceSizeNoTransformNoColor(int width, int height) {
		canvas.setCoordinateSpaceWidth(physicalPX(width));
		canvas.setCoordinateSpaceHeight(physicalPX(height));
		setWidth(width);
		setHeight(height);
	}

	public int getOffsetWidth() {
		int width = canvas.getOffsetWidth();
		return width == 0 ? canvasWidth : width;
	}

	public int getOffsetHeight() {
		int height = canvas.getOffsetHeight();
		return height == 0 ? canvasHeight : height;
	}

	public int getCoordinateSpaceWidth() {
		return canvas.getCoordinateSpaceWidth();
	}

	public int getCoordinateSpaceHeight() {
		return canvas.getCoordinateSpaceHeight();
	}

	public int getAbsoluteTop() {
		return canvas.getAbsoluteTop();
	}

	public int getAbsoluteLeft() {
		return canvas.getAbsoluteLeft();
	}

	@Override
	public void setFont(GFont font) {
		if (font instanceof GFontW) {
			currentFont = (GFontW) font;
			try {
				context.setFont(currentFont.getFullFontString());
			} catch (Throwable t) {
				Log.error("problem setting font: "
				        + currentFont.getFullFontString());
			}
		}

	}

	@Override
	public void setColor(GColor fillColor) {
		// checking for the same color here speeds up axis drawing by 25%
		if (fillColor.equals(color)) {
			return;
		}
		// but it seems that setColor is not only for setting "color",
		// but also for setFillStyle and setStrokeStyle,
		// and it seems that this is necessary to run,
		// until a better solution is found - see ticket #4291

		this.color = fillColor;
		updateCanvasColor();
		this.currentPaint = fillColor;
	}

	@Override
	public void updateCanvasColor() {
		if (color == null || context == null) {
			return;
		}
		String colorStr = "rgba(" + color.getRed() + "," + color.getGreen()
		        + "," + color.getBlue() + "," + (color.getAlpha() / 255d) + ")";
		context.setStrokeStyle(colorStr);
		context.setFillStyle(colorStr);

	}

	@Override
	public void fillRect(int x, int y, int w, int h) {
		context.fillRect(x, y, w, h);
	}

	@Override
	public void clearRect(int x, int y, int w, int h) {
		context.saveTransform();
		context.setTransform2(1, 0, 0, 1, 0, 0);
		context.clearRect(x, y, w, h);
		context.restoreTransform();
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {

		/*
		 * TODO: there is some differences between the result of
		 * geogebra.awt.Graphics.drawLine(...) function. Here is an attempt to
		 * make longer the vertical and horizontal lines:
		 * 
		 * int x_1 = Math.min(x1,x2); int y_1 = Math.min(y1,y2); int x_2 =
		 * Math.max(x1,x2); int y_2 = Math.max(y1,y2);
		 * 
		 * if(x1==x2){ y_1--; y_2++; } else if(y1==y2){ x_1--; x_2++; }
		 * 
		 * context.beginPath(); context.moveTo(x_1, y_1); context.lineTo(x_2,
		 * y_2); context.closePath(); context.stroke();
		 */
		context.beginPath();
		context.moveTo(x1, y1);
		context.lineTo(x2, y2);
		context.closePath();
		context.stroke();

	}

	@Override
	public void setClip(GShape shape) {
		if (shape == null) {
			Log.warn("Set clip should not be called with null, use resetClip instead");
			resetClip();
			return;
		}
		Shape shape2 = (Shape) shape;

		doDrawShape(shape2);
		// we should call this only if no clip was set or just after another
		// clip to overwrite
		// in this case we don't want to double-clip something so let's
		// restore the context
		// context.restoreTransform();
		// context.saveTransform();
		context.clip();
	}

	@Override
	public void draw(GShape shape) {
		if (shape == null) {
			Log.error("Error in EuclidianView.draw");
			return;
		}
		if (shape instanceof GeneralPathClipped) {
			doDrawShape((Shape) ((GeneralPathClipped) shape).getGeneralPath());
		} else {
			doDrawShape((Shape) shape);
		}
		context.stroke();
	}

	@Override
	public void fill(GShape gshape) {
		if (gshape == null) {
			Log.error("Error in EuclidianView.draw");
			return;
		}
		Shape shape;
		if (gshape instanceof GeneralPathClipped) {
			shape = (Shape) ((GeneralPathClipped) gshape).getGeneralPath();
		} else {
			shape = (Shape) gshape;
		}

		doDrawShape(shape);

		/*
		 * App.debug((shape instanceof GeneralPath)+""); App.debug((shape
		 * instanceof GeneralPathClipped)+"");
		 * App.debug((shape.getClass().toString())+"");
		 */

		// default winding rule changed for ggb50 (for Polygons) #3983
		if (shape instanceof GeneralPath) {
			GeneralPath gp = (GeneralPath) shape;
			int rule = gp.getWindingRule();
			if (rule == Path2D.WIND_EVEN_ODD) {
				context.fill("evenodd");
			} else {
				// context.fill("") differs between browsers
				context.fill();
			}
		} else {
			context.fill();
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		context.beginPath();
		context.rect(x, y, width, height);
		context.stroke();

	}

	@Override
	public void setClip(int x, int y, int width, int height) {

		double[] dash_array_save = dash_array;
		dash_array = null;
		GShape sh = AwtFactory.getPrototype().newRectangle(x, y,
		        width, height);
		setClip(sh);
		dash_array = dash_array_save;

		/*
		 * alternative: makes clipping bad, see #3212
		 * 
		 * //context.save(); context.beginPath(); context.moveTo(x, y);
		 * context.lineTo(x + width, y); context.lineTo(x + width, y + height);
		 * context.lineTo(x , y + height); context.lineTo(x , y);
		 * //context.closePath(); context.clip();
		 */
	}

	public void setWidth(int w) {
		this.canvasWidth = w;
		canvas.setWidth(w + "px");
	}

	public void setHeight(int h) {
		this.canvasHeight = h;
		canvas.setHeight(h + "px");
	}

	public void setPreferredSize(GDimension preferredSize) {
		setWidth(Math.max(0, preferredSize.getWidth()));
		setHeight(Math.max(0, preferredSize.getHeight()));

		// do not use getOffsetWidth here,
		// as it is prepared by the browser and not yet ready...
		// if preferredSize can be negative, have a check for it instead
		setCoordinateSpaceSize(
				(preferredSize.getWidth() >= 0) ? preferredSize.getWidth() : 0,
				(preferredSize.getHeight() >= 0) ? preferredSize.getHeight()
						: 0);
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight) {
		roundRect(x, y, width, height, arcHeight - arcHeight / 2);
		context.stroke();

	}

	/**
	 * Using arc, because arc to has buggy implementation in some browsers
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param r
	 */
	private void roundRect(int x, int y, int w, int h, int r) {
		context.beginPath();
		int ey = y + h;
		int ex = x + w;
		double r2d = Math.PI / 180;
		context.moveTo(x + r, y);
		context.lineTo(ex - r, y);
		context.arc(ex - r, y + r, r, r2d * 270, r2d * 360, false);
		context.lineTo(ex, ey - r);
		context.arc(ex - r, ey - r, r, r2d * 0, r2d * 90, false);
		context.lineTo(x + r, ey);
		context.arc(x + r, ey - r, r, r2d * 90, r2d * 180, false);
		context.lineTo(x, y + r);
		context.arc(x + r, y + r, r, r2d * 180, r2d * 270, false);

		context.closePath();
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight) {
		roundRect(x, y, width, height, arcHeight - arcHeight / 2);
		context.fill("evenodd");

	}

	public ImageData getImageData(int x, int y, int width, int height) {
		return context.getImageData(x, y, width, height);
	}

	/**
	 * @param data
	 *            Imagedata to put on the canvas
	 */
	public void putImageData(ImageData data, double x, double y) {
		context.putImageData(data, x, y);
	}

	@Override
	public void setAntialiasing() {
		// not needed
	}

	@Override
	public void setTransparent() {
		setComposite(GAlphaCompositeW.Src);
	}

	public void fillWith(GColor color) {
		this.setColor(color);
		this.fillRect(0, 0, getOffsetWidth(), getOffsetHeight());
	}

	public void debug() {
		// TODO Auto-generated method stub
		String physical = context.getFillStyle().toString().toUpperCase();
		String logical = "null";
		if (color != null) {
			logical = color.getAlpha() < 255 ? "RGBA(" + color.getRed() + ", "
			        + color.getGreen() + ", " + color.getBlue() + ", 0."
			        + (int) (1000000 * color.getAlpha() / 255d) + ")" : "#"
			        + StringUtil.toHexString(color).toUpperCase();
		}
		if (color == null && physical.contains("OBJ")) {
			System.out.println(hashCode() + ": not colors");
			lastDebugOk = false;
			lastDebugException = false;
		} else if (!logical.equals(physical)) {
			if (!lastDebugException) {
				Log.printStacktrace(
						hashCode() + ": "
				        + logical.replace(".0", "") + " / "
				        + physical.replace(".0", ""));
			}
			lastDebugOk = false;
			lastDebugException = true;
		} else if (!lastDebugOk) {
			System.out.println(hashCode() + ": ok");
			lastDebugOk = true;
			lastDebugException = false;
		}
	}

	public double getScale() {
		return devicePixelRatio;
	}

	public JLMContext2d getContext() {
		return context;
	}

	@Override
	public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
		this.setImageInterpolation(needsInterpolationRenderingHint);
		return null;
	}

	@Override
	public void resetInterpolationHint(Object oldInterpolationHint) {
		this.setImageInterpolation(true);
		this.color = null;
	}

	@Override
	public void drawImage(GBufferedImage img, int x, int y) {
		GBufferedImageW bi = (GBufferedImageW) img;
		if (bi == null) {
			return;
		}
		try {
			if (bi.hasCanvas()) {
				if (bi.getCanvas().getCoordinateSpaceWidth() > 0) {
					context.drawImage(bi.getCanvas().getCanvasElement(), 0, 0,
						bi.getCanvas().getCoordinateSpaceWidth(),
						bi.getCanvas().getCoordinateSpaceHeight(), x, y,
							this.getOffsetWidth(), this.getOffsetHeight());
				}
				// zero width canvas throws error in FF
			} else {
				context.drawImage(bi.getImageElement(), 0, 0, bi.getWidth(),
						bi.getHeight(), x, y, this.getOffsetWidth(),
						this.getOffsetHeight());
			}
		} catch (Exception e) {
			Log.error("error in context.drawImage.4 method");
		}
	}

	public void drawImage(ImageElement img, int x, int y) {
		try {
			context.drawImage(img, x, y);
		} catch (Exception e) {
			Log.error("error in context.drawImage.3 method");
		}
	}

	@Override
	public void drawImage(MyImage img, int x, int y) {
		context.drawImage(((MyImageW) img).getImage(), x, y);

	}

	@Override
	public void saveTransform() {
		context.saveTransform();
	}

	@Override
	public void restoreTransform() {
		context.restoreTransform();
	}

	public boolean setAltText(String altStr) {
		boolean ret = !(canvas.getElement().getInnerText() + "").equals(altStr);
		canvas.getElement().setInnerText(altStr);
		return ret;

	}

	public String getAltText() {
		return canvas.getElement().getInnerText();
	}

	public void forceResize() {
		int width = canvas.getOffsetWidth();
		int height = canvas.getOffsetHeight();
		if (width > 0) {
			setCoordinateSpaceSize(width - 1, height);
			setCoordinateSpaceSize(width, height);
		}
	}

	@Override
	public void resetClip() {
		context.restoreTransform();
	}


}
