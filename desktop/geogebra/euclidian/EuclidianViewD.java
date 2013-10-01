/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.euclidian;

import geogebra.awt.GBasicStrokeD;
import geogebra.awt.GBufferedImageD;
import geogebra.awt.GColorD;
import geogebra.awt.GGraphics2DD;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.GetViewId;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;
import geogebra.main.GuiManagerInterfaceD;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.List;

/**
 * 
 * @author Markus Hohenwarter
 */
public class EuclidianViewD extends EuclidianViewND implements
		Printable {
	
	/**
	 * Rendering hints for the graphics
	 */
	protected static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);

		// This ensures fast image drawing. Note that DrawImage changes
		// this hint for scaled and sheared images to improve their quality
		defRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	}


	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	/** reset image in applets */
	protected Image resetImage; 
	/** play image for animations*/
	protected Image playImage;
	/** pause image for animations */
	protected Image pauseImage;


	// public Graphics2D lastGraphics2D;
	/** default mouse cursor */
	protected Cursor defaultCursor;

	// set EuclidianView no - 2 for 2nd EulidianView, 1 for 1st EuclidianView
	// and Applet
	// EVNO_GENERAL for others

	/**
	 * @param ec controller
	 * @param showAxes whether to show x-axis and y-axis
	 * @param showGrid whether to show grid
	 * @param settings settings
	 */
	public EuclidianViewD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, EuclidianSettings settings) {
		this(ec, showAxes, showGrid, 1, settings);
	}

	/**
	 * Creates EuclidianView
	 * 
	 * @param ec
	 *            controller
	 * @param showAxes
	 *            whether x-axis and y-axis should be shown
	 * @param showGrid
	 *            whether grid should be shown
	 * @param evno
	 *            number of this view
	 * @param settings euclidian settings
	 */
	public EuclidianViewD(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno, EuclidianSettings settings) {

		super(ec, settings);

		evNo = evno;
		setApplication(ec.getApplication());


		setShowAxis(0, showAxes[0], false);
		setShowAxis(1, showAxes[1], false);
		this.showGrid = showGrid;

		


		// algebra controller will take care of our key events

		((EuclidianControllerD)euclidianController).setView(this);

		attachView();

		
		initView(false);

		// updateRightAngleStyle(app.getLocale());


		EuclidianSettings es = null;
		if (settings!=null){
			es = settings;
		// settings from XML for EV1, EV2
		// not for eg probability calculator
		}else if ((evNo == 1) || (evNo == 2)) {
			es = getApplication().getSettings().getEuclidian(evNo);
		}
		
		if (es!=null){
			settingsChanged(es);
			es.addListener(this);
		}
	}

	@Override
	protected void initView(boolean repaint) {
		initPanel(repaint);
		super.initView(repaint);
	}

	/**
	 * @return whether preferred size is defined and greater than minimum
	 */
	public boolean hasPreferredSize() {
		Dimension prefSize = getPreferredSize();

		return (prefSize != null) && (prefSize.width > MIN_WIDTH)
				&& (prefSize.height > MIN_HEIGHT);
	}

	public void setDragCursor() {
		
		if(getMode() == EuclidianConstants.MODE_TRANSLATEVIEW){
			setGrabbingCursor();
		}

		else if (getApplication().useTransparentCursorWhenDragging) {
			setCursor(getApplication().getTransparentCursor());
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

	}

	@Override
	public void setTransparentCursor() {

		setCursor(getApplication().getTransparentCursor());
	}

	@Override
	public void setEraserCursor() {

		setCursor(getApplication().getEraserCursor());

	}

	public void setMoveCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	public void setResizeXAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
	}

	public void setResizeYAxisCursor() {
		setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
	}

	/**
	 * Set the cursor to grabbing hand
	 */
	public void setGrabbingCursor() {
		setCursor(getCursorForImage(getApplication()
				.getInternalImage("cursor_grabbing.gif")));
	}
	
	public void setHitCursor() {
		if (defaultCursor == null) {
			setCursor(Cursor.getDefaultCursor());
		} else {
			setCursor(defaultCursor);
		}
	}

	public void setDefaultCursor() {
		if (defaultCursor == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		} else {
			setCursor(defaultCursor);
		}
	}

	@Override
	protected void initCursor() {
		defaultCursor = null;

		switch (getMode()) {
		case EuclidianConstants.MODE_ZOOM_IN:
			defaultCursor = getCursorForImage(getApplication()
					.getInternalImage("cursor_zoomin.gif"));
			break;

		case EuclidianConstants.MODE_ZOOM_OUT:
			defaultCursor = getCursorForImage(getApplication()
					.getInternalImage("cursor_zoomout.gif"));
			break;

		case EuclidianConstants.MODE_TRANSLATEVIEW:
			defaultCursor = getCursorForImage(getApplication()
					.getInternalImage("cursor_grab.gif"));
			break;
		}

		setDefaultCursor();
	}

	/**
	 * @param image image file
	 * @return cursor created from image
	 */
	protected Cursor getCursorForImage(Image image) {
		if (image == null) {
			return null;
		}

		// Query for custom cursor support
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getBestCursorSize(32, 32);
		int colors = tk.getMaximumCursorColors();
		if (!d.equals(new Dimension(0, 0)) && (colors != 0)) {
			// load cursor image
			try {
					// Create custom cursor from the image
					Cursor cursor = tk.createCustomCursor(image,
						new java.awt.Point(16, 16), "custom cursor");
					return cursor;
				} catch (Exception exc) {
					// Catch exceptions so that we don't try to set a null
					// cursor
					App
							.debug("Unable to create custom cursor.");
			}
			
		}
		return null;
	}

	@Override
	public void setDefRenderingHints(geogebra.common.awt.GGraphics2D g2){
		g2.setRenderingHints(defRenderingHints);
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} 
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();

			g2d.translate(pageFormat.getImageableX(),
					pageFormat.getImageableY());

			// construction title
			int y = 0;
			Construction cons = kernel.getConstruction();
			String title = cons.getTitle();
			if (!title.equals("")) {
				GFont titleFont = getApplication().getBoldFontCommon().deriveFont(GFont.BOLD,
						getApplication().getBoldFont().getSize() + 2);
				g2d.setFont(geogebra.awt.GFontD.getAwtFont(titleFont));
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getAscent();
				g2d.drawString(title, 0, y);
			}

			// construction author and date
			String author = cons.getAuthor();
			String date = cons.getDate();
			String line = null;
			if (!author.equals("")) {
				line = author;
			}
			if (!date.equals("")) {
				if (line == null) {
					line = date;
				} else {
					line = line + " - " + date;
				}
			}

			// scale string:
			// Scale in cm: 1:1 (x), 1:2 (y)
			String scaleString = null;
			if (getApplication().isPrintScaleString()) {
				StringBuilder sb = new StringBuilder(
						getApplication().getPlain("ScaleInCentimeter"));
				if (printingScale <= 1) {
					sb.append(": 1:");
					sb.append(printScaleNF.format(1 / printingScale));
				} else {
					sb.append(": ");
					sb.append(printScaleNF.format(printingScale));
					sb.append(":1");
				}

				// add yAxis scale too?
				if (getScaleRatio() != 1.0) {
					sb.append(" (x), ");
					double yPrintScale = (printingScale * getYscale()) / getXscale();
					if (yPrintScale < 1) {
						sb.append("1:");
						sb.append(printScaleNF.format(1 / yPrintScale));
					} else {
						sb.append(printScaleNF.format(yPrintScale));
						sb.append(":1");
					}
					sb.append(" (y)");
				}
				scaleString = sb.toString();
			}

			if (scaleString != null) {
				if (line == null) {
					line = scaleString;
				} else {
					line = line + " - " + scaleString;
				}
			}

			if (line != null) {
				g2d.setFont(getApplication().getPlainFont());
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getHeight();
				g2d.drawString(line, 0, y);
			}
			if (y > 0) {
				g2d.translate(0, y + 20); // space between title and drawing
			}

			double scale = (PRINTER_PIXEL_PER_CM / getXscale()) * printingScale;
			exportPaint(g2d, scale);

			// clear page margins at bottom and right
			double pagewidth = pageFormat.getWidth();
			double pageheight = pageFormat.getHeight();
			double xmargin = pageFormat.getImageableX();
			double ymargin = pageFormat.getImageableY();

			g2d.setTransform(oldTransform);
			g2d.setClip(null);
			g2d.setPaint(Color.white);

			Rectangle2D.Double rect = new Rectangle2D.Double();
			rect.setFrame(0, pageheight - ymargin, pagewidth, ymargin);
			g2d.fill(rect);
			rect.setFrame(pagewidth - xmargin, 0, xmargin, pageheight);
			g2d.fill(rect);

			return (PAGE_EXISTS);
		
	}

	/**
	 * @param g2d graphics
	 * @param scale  ratio of desired size and current size of the graphics
	 */
	public void exportPaint(Graphics2D g2d, double scale) {
		exportPaint(new GGraphics2DD(g2d), scale, false);
	}

	/**
	 * Scales construction and draws it to g2d.
	 * 
	 * @param g2d
	 *            export graphics
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * 
	 * @param transparency
	 *            states if export should be optimized for eps. Note: if this is
	 *            set to false, no traces are drawn.
	 * 
	 */
	public void exportPaint(geogebra.common.awt.GGraphics2D g2d, double scale, boolean transparency) {
		getApplication().exporting = true;
		exportPaintPre(g2d, scale, transparency);
		drawObjects(g2d);
		getApplication().exporting = false;
	}

	/**
	 * @param g2d target graphics object
	 * @param scale ratio of desired size and current size of the graphics
	 */
	public void exportPaintPre(geogebra.common.awt.GGraphics2D g2d, double scale) {
		exportPaintPre(g2d, scale, false);
	}

	private void exportPaintPre(geogebra.common.awt.GGraphics2D g2d, double scale,
			boolean transparency) {
		g2d.scale(scale, scale);

		// clipping on selection rectangle
		if (getSelectionRectangle() != null) {
			GRectangle rect = getSelectionRectangle();
			g2d.setClip(0, 0, (int)rect.getWidth(), (int)rect.getHeight());
			g2d.translate(-rect.getX(), -rect.getY());
			// Application.debug(rect.x+" "+rect.y+" "+rect.width+" "+rect.height);
		} else {
			// use points Export_1 and Export_2 to define corner
			try {
				// Construction cons = kernel.getConstruction();
				GeoPoint export1 = (GeoPoint) kernel.lookupLabel(EXPORT1);
				GeoPoint export2 = (GeoPoint) kernel.lookupLabel(EXPORT2);
				double[] xy1 = new double[2];
				double[] xy2 = new double[2];
				export1.getInhomCoords(xy1);
				export2.getInhomCoords(xy2);
				double x1 = xy1[0];
				double x2 = xy2[0];
				double y1 = xy1[1];
				double y2 = xy2[1];
				x1 = (x1 / getInvXscale()) + getxZero();
				y1 = getyZero() - (y1 / getInvYscale());
				x2 = (x2 / getInvXscale()) + getxZero();
				y2 = getyZero() - (y2 / getInvYscale());
				int x = (int) Math.min(x1, x2);
				int y = (int) Math.min(y1, y2);
				int exportWidth = (int) Math.abs(x1 - x2) + 2;
				int exportHeight = (int) Math.abs(y1 - y2) + 2;

				g2d.setClip(0, 0, exportWidth, exportHeight);
				g2d.translate(-x, -y);
			} catch (Exception e) {
				// or take full euclidian view
				g2d.setClip(0, 0, getWidth(), getHeight());
			}
		}

		// DRAWING
		if (isTracing() || hasBackgroundImages()) {
			// draw background image to get the traces
			if (bgImage == null) {
				drawBackgroundWithImages(g2d, transparency);
			} else {
				GGraphics2DD.getAwtGraphics(g2d).drawImage(GBufferedImageD.getAwtBufferedImage(bgImage), 0, 0, getJPanel());
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		GGraphics2DD.getAwtGraphics(g2d).setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		setAntialiasing(g2d);
	}

	/**
	 * Returns image of drawing pad sized according to the given scale factor.
	 * 
	 * @param scale  ratio of desired size and current size of the graphics
	 * @return image of drawing pad sized according to the given scale factor.
	 * @throws OutOfMemoryError if the requested image is too big
	 */
	public BufferedImage getExportImage(double scale) throws OutOfMemoryError {
		return getExportImage(scale, false);
	}

	/**
	 * @param scale  ratio of desired size and current size of the graphics
	 * @param transparency true for transparent image
	 * @return image
	 * @throws OutOfMemoryError if the requested image is too big
	 */
	public BufferedImage getExportImage(double scale, boolean transparency)
			throws OutOfMemoryError {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);
		BufferedImage img = createBufferedImage(width, height, transparency);
		exportPaint(new GGraphics2DD(img.createGraphics()), scale, transparency);
		img.flush();
		return img;
	}
	/**
	 * @param width
	 * pixel width
	 * @param height
	 * pixel height
	 * @param transparency
	 * true for transparent
	 * @return image
	 * @throws OutOfMemoryError if the requested image is too big
	 */
	protected BufferedImage createBufferedImage(int width, int height,
			boolean transparency) throws OutOfMemoryError {

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice gs = ge.getDefaultScreenDevice();

		GraphicsConfiguration gc = gs.getDefaultConfiguration();
		BufferedImage bufImg = gc
				.createCompatibleImage(width, height,
						(transparency ? Transparency.TRANSLUCENT
								: Transparency.BITMASK));

		// Graphics2D g = (Graphics2D)bufImg.getGraphics();

		// g.setBackground(new Color(0,0,0,0));

		// g.clearRect(0,0,width,height);

		return bufImg;

	}

	




	@Override
	protected void drawResetIcon(geogebra.common.awt.GGraphics2D g){
		// need to use getApplet().width rather than width so that
					// it works with applet rescaling
					int w = getApplication().onlyGraphicsViewShowing() ? getApplication().getApplet().width
							: getWidth() + 2;
					GGraphics2DD.getAwtGraphics(g).drawImage(getResetImage(), w - 18, 2, null);
	}
	private Image getResetImage() {
		if (resetImage == null) {
			resetImage = getApplication().getRefreshViewImage();
		}
		return resetImage;
	}

	private Image getPlayImage() {
		if (playImage == null) {
			playImage = getApplication().getPlayImage();
		}
		return playImage;
	}

	private Image getPauseImage() {
		if (pauseImage == null) {
			pauseImage = getApplication().getPauseImage();
		}
		return pauseImage;
	}

	
	@Override
	final protected void drawAnimationButtons(geogebra.common.awt.GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		int x = 6;
		int y = getHeight() - 22;

		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(geogebra.common.awt.GColor.darkGray);
		} else {
			g2.setColor(geogebra.common.awt.GColor.lightGray);
		}

		g2.setStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());

		// draw pause or play button
		g2.drawRect(x - 2, y - 2, 18, 18);
		Image img = kernel.isAnimationRunning() ? getPauseImage()
				: getPlayImage();
		GGraphics2DD.getAwtGraphics(g2).drawImage(img, x, y, null);
	}

	public final boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return false;
		}

		return kernel.needToShowAnimationButton() && (x <= 20)
				&& (y >= (getHeight() - 20));
	}

	@Override
	protected boolean drawPlayButtonInThisView() {
		GuiManagerInterfaceD gui = getApplication().getGuiManager();
		// just one view
		if ( gui == null) {
			return true;
		}
		// eg ev1 just closed
		 GetViewId evp = ((GuiManagerD)gui).getLayout().getDockManager().getFocusedEuclidianPanel();
		if (evp == null) {
			return true;
		}

		return this.getViewID() != evp.getViewId();
	}



	@Override
	public EuclidianControllerD getEuclidianController() {
		return (EuclidianControllerD)euclidianController;
	}

	/**
	 * @return graphics of the underlying component
	 */
	@Override
	public geogebra.common.awt.GGraphics2D getGraphicsForPen() {
			return new GGraphics2DD((Graphics2D) evjpanel.getGraphics());

	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<geogebra.common.awt.GPoint> penPoints2, geogebra.common.awt.GColor penColor, int penLineStyle, int penSize) {
		PolyBezier pb = new PolyBezier(penPoints2);
		BufferedImage penImage2 = GBufferedImageD.getAwtBufferedImage(gi
						.getFillImage());
		boolean giNeedsInit = false;
		if (penImage2 == null) {
			giNeedsInit = true;
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			penImage2 = gc.createCompatibleImage(Math.max(300,getWidth()),
					Math.max(getHeight(),200), Transparency.BITMASK);
		}
		Graphics2D g2d = (Graphics2D) penImage2.getGraphics();

		EuclidianViewND.setAntialiasing(g2d);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		g2d.setStroke(GBasicStrokeD.getAwtStroke(geogebra.common.euclidian.EuclidianStatic.getStroke(2 * penSize, (penPoints2
					.size() <= 2) ? EuclidianStyleConstants.LINE_TYPE_FULL
							: penLineStyle)));
		g2d.setColor(GColorD.getAwtColor(penColor));

		g2d.draw(pb.gp);
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

		app.refreshViews(); // clear trace
		//TODO -- did we need the following line?
		//ev.getGraphics().drawImage(penImage2, penOffsetX, penOffsetY, null);

		if (giNeedsInit) {
			String fileName = ((AppD)app).createImage(penImage2, "penimage.png");
			// Application.debug(fileName);
			GeoImage geoImage = null;
			//if (gi == null)
			//	geoImage = new GeoImage(app.getKernel().getConstruction());
			//else
			geoImage = gi;
			geoImage.setImageFileName(fileName);
			geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);
			GeoPoint corner = (new GeoPoint(
					app.getKernel().getConstruction(), null,
					ev.toRealWorldCoordX(0),
					ev.toRealWorldCoordY(penImage2.getHeight()),
					1.0));
			GeoPoint corner2 = (new GeoPoint(app.getKernel()
					.getConstruction(), null, ev.toRealWorldCoordX(penImage2.getWidth()), ev.toRealWorldCoordY(penImage2.getHeight()), 1.0));
			corner.setLabelVisible(false);
			corner2.setLabelVisible(false);
			corner.update();
			corner2.update();
			//if (gi == null)
			//	geoImage.setLabel(null);
			geoImage.setCorner(corner, 0);
			geoImage.setCorner(corner2, 1);

			// need 3 corner points if axes ratio isn't 1:1
			if (!Kernel.isEqual(ev.getXscale(), ev.getYscale())) {
				GeoPoint corner4 = (new GeoPoint(app.getKernel()
						.getConstruction(), null,
						ev.toRealWorldCoordX(0),
						ev.toRealWorldCoordY(0), 1.0));
				corner4.setLabelVisible(false);
				corner4.update();
				geoImage.setCorner(corner4, 2);
			}

			geoImage.update();

			GeoImage.updateInstances();

		}

		// doesn't work as all changes are in the image not the XML
		// app.storeUndoInfo();
		app.setUnsaved();

	}

	@Override
	public void setBoldAxes(boolean bold) {
		// TODO Auto-generated method stub
		
	}

}
