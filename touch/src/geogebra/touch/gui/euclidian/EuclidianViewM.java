package geogebra.touch.gui.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.Settings;
import geogebra.touch.controller.TouchController;
import geogebra.web.awt.GGraphics2DW;
import geogebra.web.awt.GRectangleW;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.main.AppWeb;
import geogebra.web.main.DrawEquationWeb;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianViewWeb
{
	private Canvas canvas;
	public static int repaints, repaintTime, drags, dragTime;

	protected Hits hits;

	private static int SELECTION_DIAMETER_MIN = 25; // taken from
	                                                // geogebra.common.euclidian.draw.DrawPoint
	public static int moveEventsIgnored;

	// accepting range for hitting a point is multiplied with this factor
	// (for anything other see App)
	private int selectionFactor = 3;

	public EuclidianViewM(EuclidianViewPanel euclidianViewPanel, TouchController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		ec.setView(this);

		this.setAllowShowMouseCoords(false);

		this.hits = new Hits();
		init(euclidianViewPanel);
		//make sure we listen to the changes of settings, eg if file is loaded
		if ((this.evNo == 1) || (this.evNo == 2)) {
			EuclidianSettings es = this.app.getSettings().getEuclidian(this.evNo);
			settingsChanged(es);
			es.addListener(this);
		}
	}

	/**
	 * This method has to be called before using g2p.
	 * 
	 */
	private void init(EuclidianViewPanel euclidianViewPanel)
	{
		this.canvas = Canvas.createIfSupported();
		this.g2p = new GGraphics2DW(this.canvas);
		TouchEventController touchController = new TouchEventController((TouchController) this.getEuclidianController());

		euclidianViewPanel.addDomHandler(touchController, TouchStartEvent.getType());
		euclidianViewPanel.addDomHandler(touchController, TouchEndEvent.getType());
		euclidianViewPanel.addDomHandler(touchController, TouchMoveEvent.getType());

		// Listeners for Desktop
		euclidianViewPanel.addDomHandler(touchController, MouseDownEvent.getType());
		euclidianViewPanel.addDomHandler(touchController, MouseMoveEvent.getType());
		euclidianViewPanel.addDomHandler(touchController, MouseUpEvent.getType());
		euclidianViewPanel.addDomHandler(touchController, MouseWheelEvent.getType());

		updateFonts();
		initView(true);
		attachView();
		doRepaint();
	}

	/**
	 * this version also adds points that are very close to the hit point
	 */
	@Override
	public void setHits(GPoint p)
	{
		super.setHits(p);
		this.hits = super.getHits();

		if (this.hits.size() == 0)
		{
			GRectangleW rect = new GRectangleW();
			int size = SELECTION_DIAMETER_MIN * this.selectionFactor;
			rect.setBounds(p.x - (size / 2), p.y - (size / 2), size, size);
			this.setHits(rect);
			this.hits = super.getHits();
		}
	}

	@Override
	public Hits getHits()
	{
		return this.hits;
	}

	@Override
	public boolean hitAnimationButton(AbstractEvent event)
	{
		return false;
	}

	@Override
	public void setDefaultCursor()
	{
	}

	@Override
	public void setHitCursor()
	{
	}

	@Override
	public EuclidianStyleBar getStyleBar()
	{
		return null;
	}

	@Override
	public void setDragCursor()
	{
	}

	@Override
	public void setToolTipText(String plainTooltip)
	{
	}

	@Override
	public void setResizeXAxisCursor()
	{
	}

	@Override
	public void setResizeYAxisCursor()
	{
	}

	@Override
	public void setMoveCursor()
	{
	}

	@Override
	public boolean hasFocus()
	{
		return false;
	}

	@Override
	public void requestFocus()
	{
	}

	@Override
	public EuclidianController getEuclidianController()
	{
		// TODO
		return this.euclidianController;
	}

	@Override
	protected void initCursor()
	{
	}

	@Override
	protected void setStyleBarMode(int mode)
	{
	}

	@Override
	public void updateSize()
	{
		setRealWorldBounds();
	}

	@Override
	public boolean requestFocusInWindow()
	{
		return false;
	}

	@Override
	public void setPreferredSize(GDimension preferredSize)
	{
	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2, GColor penColor, int penLineStyle, int penSize)
	{
	}

	@Override
	public void add(GBox box)
	{
		/*
		 * TODO panel.add( GBoxW.getImpl((GBoxW) box), (int)box.getBounds().getX(),
		 * (int)box.getBounds().getY());
		 */
	}

	@Override
	public void remove(GBox box)
	{
	}

	@Override
	public void setTransparentCursor()
	{
	}

	@Override
	public void setEraserCursor()
	{
	}

	@Override
	protected void drawResetIcon(GGraphics2D g)
	{
		// TODO Auto-generated method stub

	}

	public Canvas getCanvas()
	{
		return this.canvas;
	}

	@Override
	public void doRepaint2()
	{
		((AppWeb) this.app).getTimerSystem().viewRepainting(this);
		if (getAxesColor() == null)
		{
			setAxesColor(geogebra.common.awt.GColor.black);
		}
		long l = System.currentTimeMillis();
		((DrawEquationWeb) this.app.getDrawEquation()).clearLaTeXes(this);
		paint(this.g2p);
		getEuclidianController().setCollectedRepaints(false);
		((AppWeb) this.app).getTimerSystem().viewRepainted(this);
		repaints++;
		repaintTime += (System.currentTimeMillis() - l);
		if (repaints % 100 == 0)
		{
			App.debug("Repaint:" + repaints + " x " + (repaintTime / repaints) + " = " + repaintTime);
			if(drags > 0){
				App.debug("Drag:" + drags + " x " + (dragTime / drags) + " = " + dragTime + "," + moveEventsIgnored + " ignored");
			}
			repaints = 0;
			drags = 0;
			repaintTime = 0;
			dragTime = 0;
			moveEventsIgnored = 0;
		}
	}

	public void onResize(ResizeEvent event)
	{
		setPixelSize(event.getWidth(), event.getHeight());
	}

	public void setPixelSize(int width, int height)
	{
		this.g2p.setCoordinateSpaceWidth(width);
		this.g2p.setCoordinateSpaceHeight(height);

		this.canvas.setPixelSize(width, height);
		updateSize();
		doRepaint2();
	}
}
