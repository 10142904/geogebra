package geogebra.html5.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.euclidian.draw.DrawList;
import geogebra.common.factories.AwtFactory;
import geogebra.common.io.MyXMLio;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.html5.awt.GFontW;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.DrawEquationWeb;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;

public abstract class EuclidianViewWeb extends EuclidianView {

	public static int DELAY_UNTIL_MOVE_FINISH = 150;

	public static final int DELAY_BETWEEN_MOVE_EVENTS = 30;
	
	public geogebra.html5.awt.GGraphics2DW g2p = null;
	private GGraphics2D g2dtemp;
	public geogebra.html5.awt.GGraphics2DW g4copy = null;
	private geogebra.common.awt.GColor backgroundColor = GColor.white;
	
	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint2();
		}
	};

	private AnimationScheduler repaintScheduler = AnimationScheduler.get();
	
	public EuclidianViewWeb(EuclidianController ec, EuclidianSettings settings) {
	    super(ec, settings);
    }
	/**
	 * @param list
	 *            list
	 * @param b
	 *            whether the list should be drawn as combobox
	 */
	public void drawListAsComboBox(GeoList list, boolean b) {

		list.setDrawAsComboBox(b);

		DrawList d = (DrawList) getDrawable(list);
		d.resetDrawType();

	}
	@Override
	protected final void drawActionObjects(GGraphics2D g)
	{
		//not part of canvas, not needed
	}

	@Override
	protected final void setAntialiasing(GGraphics2D g2)
	{
		//always on
	}
	
	@Override
    public final GFont getFont() {
		return new GFontW(g2p.getFont());
    }


    public final GColor getBackgroundCommon() {
	    return backgroundColor ;
    }
	@Override
    public final void setBackground(GColor bgColor) {
		if (bgColor != null)
			backgroundColor = AwtFactory.prototype.newColor(
			bgColor.getRed(),
			bgColor.getGreen(),
			bgColor.getBlue(),
			bgColor.getAlpha());
    }
	
	@Override
	public final GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		// TODO
		if (this.g2dtemp == null)
			this.g2dtemp = new geogebra.html5.awt.GGraphics2DW(Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}
	
	@Override
    protected final MyZoomer newZoomer() {
	    return new MyZoomerW(this);
    }
	

	@Override
    public final void paintBackground(geogebra.common.awt.GGraphics2D g2) {
		if(this.isGridOrAxesShown() || this.hasBackgroundImages() || this.tracing
				|| app.showResetIcon() || kernel.needToShowAnimationButton()){
			((geogebra.html5.awt.GGraphics2DW)g2).drawGraphics(
				(geogebra.html5.awt.GGraphics2DW)bgGraphics, 0, 0, null);
		}else{
			((geogebra.html5.awt.GGraphics2DW)g2).fillWith(this.getBackgroundCommon());
		}
		
	}
	
	public void doRepaint() {
			repaintScheduler.requestAnimationFrame(repaintCallback);
	}
	
	/**
     * This doRepaint method should be used instead of repaintView in cases
     * when the repaint should be done immediately
     */
	public final void doRepaint2()
	{
		((AppWeb) this.app).getTimerSystem().viewRepainting(this);
		long l = System.currentTimeMillis();
		((DrawEquationWeb) this.app.getDrawEquation()).clearLaTeXes(this);
		this.updateBackgroundIfNecessary();
		paint(this.g2p);
		getEuclidianController().setCollectedRepaints(false);
		((AppWeb) this.app).getTimerSystem().viewRepainted(this);
		GeoGebraProfiler.addRepaint(l);
		
	}
	
	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	public int getWidth()
	{
		return this.g2p.getCoordinateSpaceWidth();
	}
	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	public int getHeight()
	{
		return this.g2p.getCoordinateSpaceHeight();
	}
	
	public void clearView() {
		resetLists();
		updateBackgroundImage(); // clear traces and images
		// resetMode();
		if(((AppWeb)app).getViewManager()!=null){
			((AppWeb)app).getViewManager().clearAbsolutePanels();
		}
    }
	
	@Override
	protected final void setHeight(int h)
	{
		//TODO: not clear what should we do
	}

	@Override
	protected final void setWidth(int h)
	{
		//TODO: not clear what should we do
	}
	
	@Override
    public final GGraphics2DW getGraphicsForPen() {
	    return g2p;
    }
	
	public final boolean isShowing() {
	  	return
	  			g2p != null &&
	  			g2p.getCanvas() != null &&
	  			g2p.getCanvas().isAttached() &&
	  			g2p.getCanvas().isVisible();
    }

	public String getExportImageDataUrl(double scale, boolean transparency) {
		int width = (int) Math.floor(getExportWidth() * scale);
		int height = (int) Math.floor(getExportHeight() * scale);

		Canvas c4 = Canvas.createIfSupported();
		c4.setCoordinateSpaceWidth(width);
		c4.setCoordinateSpaceHeight(height);
		c4.setWidth(width+"px");
		c4.setHeight(height+"px");
		g4copy = new geogebra.html5.awt.GGraphics2DW(c4);
		this.app.exporting = true;
		exportPaintPre(g4copy, scale, transparency);
		drawObjects(g4copy);
		this.app.exporting = false;
		return g4copy.getCanvas().toDataUrl();
	}
	
	public void exportPaintPre(geogebra.common.awt.GGraphics2D g2d, double scale,
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
				paintBackground(g2d);
			}
		} else {
			// just clear the background if transparency is disabled (clear =
			// draw background color)
			drawBackground(g2d, !transparency);
		}

		setAntialiasing(g2d);
	}
	
	/**
	 * repaintView just calls this method
	 */
    public final void repaint() {

    	// TODO: this is a temporary hack until the timer system can handle TextPreview view
    	// (or ignore timer system because text preview only draws one geo)
    	if(getViewID() == App.VIEW_TEXT_PREVIEW || getViewID() < 0){
    		doRepaint();
    		return;
    	}
    	if (getEuclidianController().isCollectingRepaints()){
    		getEuclidianController().setCollectedRepaints(true);
    		return;
    	}

    	//TODO: enable this code if this view can be detached
    	//if (!isShowing())
    	//	return;

    	((AppWeb)app).getTimerSystem().viewRepaint(this);
    }
    
    public void setCoordinateSpaceSize(int width, int height) {
    	int oldWidth = g2p.getCoordinateSpaceWidth();
    	int oldHeight = g2p.getCoordinateSpaceHeight();
		g2p.setCoordinateSpaceSize(width, height);
		try {
			((AppWeb)app).syncAppletPanelSize(width - oldWidth, height - oldHeight, evNo);

			// just resizing the AbsolutePanelSmart, not the whole of DockPanel
			g2p.getCanvas().getElement().getParentElement().getStyle().setWidth(width, Style.Unit.PX);
			g2p.getCanvas().getElement().getParentElement().getStyle().setHeight(height, Style.Unit.PX);
			getEuclidianController().calculateEnvironment();
		} catch (Exception exc) {
			App.debug("Problem with the parent element of the canvas");
		}
	}

	public void synCanvasSize() {
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
	}
	
	public String getCanvasBase64WithTypeString() {

		// TODO: make this more perfect, like in Desktop

		double ratio = g2p.getCoordinateSpaceWidth();
		ratio /= g2p.getCoordinateSpaceHeight() * 1.0;
		double thx = MyXMLio.THUMBNAIL_PIXELS_X;
		double thy = MyXMLio.THUMBNAIL_PIXELS_Y;
		if (ratio < 1)
			thx *= ratio;
		else if (ratio > 1)
			thy /= ratio;

		Canvas canv = Canvas.createIfSupported();
		canv.setCoordinateSpaceHeight((int)thy);
		canv.setCoordinateSpaceWidth((int)thx);
		canv.setWidth((int)thx+"px");
		canv.setHeight((int)thy+"px");
		Context2d c2 = canv.getContext2d();

		//g2p.getCanvas().getContext2d().drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);
		if(bgGraphics!=null)
			c2.drawImage(((GGraphics2DW)bgGraphics).getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);
		c2.drawImage(g2p.getCanvas().getCanvasElement(), 0, 0, (int)thx, (int)thy);

		return canv.toDataUrl();
	}

	@Override
	protected void updateSizeKeepDrawables() {
		if ((getWidth() <= 0) || (getHeight() <= 0)) {
			return;
		}

		// real world values
		setXYMinMaxForUpdateSize();
		setRealWorldBounds();

		try {
			createImage();
		} catch (Exception e) {
			bgImage = null;
			bgGraphics = null;
		}

		updateBackgroundImage();
	}
	
	private void createImage() {
		bgImage = new geogebra.html5.awt.GBufferedImageW(getWidth(), getHeight(), 0, false);
		bgGraphics = bgImage.createGraphics();
	}

	
	public double getMinSamplePoints() {
		return 40;
	}

	/*public double getMaxBendOfScreen() {
		return MAX_BEND_OFF_SCREEN;
	}*

	public double getMaxBend() {
		return MAX_BEND;
	}

	public int getMaxDefinedBisections() {
		return MAX_DEFINED_BISECTIONS;
	}*/
	
	public double getMinPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 0.5 : 1 ;
	}

	/*public int getMaxZeroCount() {
		return MAX_ZERO_COUNT;
	}*/

	public double getMaxPixelDistance() {
		return this.g2p == null || g2p.getScale() <= 1 ? 15 : 30 ;
	}

	public static void resetDelay() {
		DELAY_UNTIL_MOVE_FINISH = 150;
    }

	/*public int getMaxProblemBisections() {
		return MAX_PROBLEM_BISECTIONS;
	}*/

}
