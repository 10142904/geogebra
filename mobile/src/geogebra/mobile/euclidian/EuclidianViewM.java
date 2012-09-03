package geogebra.mobile.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.MyZoomer;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.factories.AwtFactory;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.settings.Settings;
import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.web.awt.GGraphics2DW;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class EuclidianViewM extends EuclidianView
{
	// set in setCanvas
	private GGraphics2DW g2p = null;
	private Canvas canvas;

	private GColor backgroundColor = GColor.white;
	private GGraphics2D g2dtemp;

	public EuclidianViewM(MobileEuclidianController ec)
	{
		super(ec, new Settings().getEuclidian(1));

		this.setAllowShowMouseCoords(false);
	}

	/**
	 * This method has to be called before using g2p.
	 * 
	 * @param c
	 *            : a new Canvas
	 * 
	 */
	public void initCanvas(Canvas c)
	{
		this.canvas = c;
		this.g2p = new GGraphics2DW(this.canvas);

		// this.canvas.addTouchStartHandler(new TouchStartHandler()
		// {
		// @Override
		// public void onTouchStart(TouchStartEvent event)
		// {
		// ((MobileEuclidianController) EuclidianViewM.this
		// .getEuclidianController()).onTouchStart(event);
		// }
		// });
		//
		// this.canvas.addTouchMoveHandler(new TouchMoveHandler()
		// {
		// @Override
		// public void onTouchMove(TouchMoveEvent event)
		// {
		// ((MobileEuclidianController) EuclidianViewM.this
		// .getEuclidianController()).onTouchMove(event);
		// }
		// });
		//
		// this.canvas.addTouchEndHandler(new TouchEndHandler()
		// {
		// @Override
		// public void onTouchEnd(TouchEndEvent event)
		// {
		// ((MobileEuclidianController) EuclidianViewM.this
		// .getEuclidianController()).onTouchEnd(event);
		// }
		// });

		this.canvas.addMouseDownHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(MouseDownEvent event)
			{
				event.preventDefault();
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseDown(event);
			}
		});

		this.canvas.addMouseMoveHandler(new MouseMoveHandler()
		{
			@Override
			public void onMouseMove(MouseMoveEvent event)
			{
				event.preventDefault();
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseMove(event);
			}
		});

		this.canvas.addMouseUpHandler(new MouseUpHandler()
		{
			@Override
			public void onMouseUp(MouseUpEvent event)
			{
				event.preventDefault();
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onMouseUp(event);
			}
		});

		this.canvas.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				((MobileEuclidianController) EuclidianViewM.this
						.getEuclidianController()).onClick(event);
			}
		});

		updateFonts();
		initView(true);
		attachView();

	}

	@Override
	public void repaint()
	{
		// TODO
		if(getAxesColor() == null){
			setAxesColor(geogebra.common.awt.GColor.black);
		}
		paint(this.g2p);
	}

	@Override
	public GColor getBackgroundCommon()
	{
		// TODO
		return this.backgroundColor;
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
	public int getWidth()
	{
		// TODO
		return this.g2p.getCoordinateSpaceWidth();
	}

	@Override
	public int getHeight()
	{
		// TODO
		return this.g2p.getCoordinateSpaceHeight();
	}

	@Override
	public EuclidianController getEuclidianController()
	{
		// TODO
		return this.euclidianController;
	}

	@Override
	public void clearView()
	{
		resetLists();
		updateBackgroundImage();
	}

	@Override
	public GGraphics2D getTempGraphics2D(GFont fontForGraphics)
	{
		// TODO
		if (this.g2dtemp == null)
			this.g2dtemp = new geogebra.web.awt.GGraphics2DW(
					Canvas.createIfSupported());
		this.g2dtemp.setFont(fontForGraphics);
		return this.g2dtemp;
	}

	@Override
	public GFont getFont()
	{
		return null;
	}

	@Override
	protected void setHeight(int h)
	{
	}

	@Override
	protected void setWidth(int h)
	{
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
		// TODO
		Window.enableScrolling(false);

		int width = Window.getClientWidth();
		int height = Window.getClientHeight();

		this.canvas.setSize(width + "px", height + "px");

		this.g2p.setCoordinateSpaceWidth(width);
		this.g2p.setCoordinateSpaceHeight(height);
	}

	@Override
	public boolean requestFocusInWindow()
	{
		return false;
	}

	@Override
	public void paintBackground(GGraphics2D g2)
	{
		// TODO
		((GGraphics2DW) g2).drawGraphics((GGraphics2DW) this.bgGraphics, 0, 0,
				null);
	}

	@Override
	protected void drawActionObjects(GGraphics2D g)
	{
	}

	@Override
	protected void setAntialiasing(GGraphics2D g2)
	{
		// TODO
	}

	@Override
	public void setBackground(GColor bgColor)
	{
		if (bgColor != null)
		{
			this.backgroundColor = AwtFactory.prototype.newColor(
					bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
					bgColor.getAlpha());
		}
	}

	@Override
	public void setPreferredSize(GDimension preferredSize)
	{
	}

	@Override
	protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
			GColor penColor, int penLineStyle, int penSize)
	{
	}

	@Override
	protected MyZoomer newZoomer()
	{
		return null;
	}

	@Override
	public void add(GBox box)
	{
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
	public GGraphics2D getGraphicsForPen()
	{
		return null;
	}

	@Override
	public boolean isShowing()
	{
		return false;
	}

}
