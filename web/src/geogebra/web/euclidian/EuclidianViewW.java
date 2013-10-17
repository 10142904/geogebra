package geogebra.web.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.javax.swing.GBox;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.util.debug.Log;
import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.javax.swing.GBoxW;
import geogebra.html5.util.ImageLoadCallback;
import geogebra.html5.util.ImageWrapper;
import geogebra.web.gui.tooltip.ToolTipManagerW;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

public class EuclidianViewW extends EuclidianViewWeb {
	
	
	
	public boolean isInFocus = false;

	private AppW app = (AppW) super.app;

	protected ImageElement resetImage, playImage, pauseImage, upArrowImage,
	downArrowImage;
	
	protected EuclidianPanelWAbstract EVPanel;

	public EuclidianViewW(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, int evNo, EuclidianSettings settings) {
		super(euclidiancontroller, settings);
		Canvas canvas = euclidianViewPanel.getCanvas();
		if (evNo == 2) {
			canvas.getElement().setId("View_"+ App.VIEW_EUCLIDIAN2);
		} else {
			canvas.getElement().setId("View_"+ App.VIEW_EUCLIDIAN);
		}
		this.evNo = evNo;
	    // TODO Auto-generated constructor stub
		this.g2p = new geogebra.html5.awt.GGraphics2DW(canvas);

		updateFonts();
		initView(true);
		attachView();
	
		((EuclidianControllerW)euclidiancontroller).setView(this);
//		canvas.addClickHandler((EuclidianController)euclidiancontroller);	
//		canvas.addMouseMoveHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseOverHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseOutHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseDownHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseUpHandler((EuclidianController)euclidiancontroller);
//		canvas.addMouseWheelHandler((EuclidianController)euclidiancontroller);
//		
//		canvas.addTouchStartHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchEndHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchMoveHandler((EuclidianController)euclidiancontroller);
//		canvas.addTouchCancelHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureStartHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureChangeHandler((EuclidianController)euclidiancontroller);
//		canvas.addGestureEndHandler((EuclidianController)euclidiancontroller);

		canvas.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent be) {
				focusLost();
			}
		});
		canvas.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent fe) {
				focusGained();
			}
		});
		canvas.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		canvas.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, ClickEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, DoubleClickEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseMoveEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseOverEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseOutEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseDownEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseUpEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, MouseWheelEvent.getType());
		
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, TouchStartEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, TouchEndEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, TouchMoveEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, TouchCancelEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, GestureStartEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, GestureChangeEvent.getType());
		euclidianViewPanel.getAbsolutePanel().addDomHandler((EuclidianControllerW)euclidiancontroller, GestureEndEvent.getType());
		
		//euclidianViewPanel.addDomHandler((EuclidianController)euclidiancontroller, KeyPressEvent.getType());
//		euclidianViewPanel.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
//		euclidianViewPanel.addKeyPressHandler(this.app.getGlobalKeyDispatcher());
		
		if ((evNo == 1) || (evNo == 2)) {
			EuclidianSettings es = this.app.getSettings().getEuclidian(evNo);
			settingsChanged(es);
			es.addListener(this);
		}
		EVPanel = euclidianViewPanel;
    }

	// STROKES
	protected static MyBasicStrokeW standardStroke = new MyBasicStrokeW(1.0f);

	protected static MyBasicStrokeW selStroke = new MyBasicStrokeW(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	protected boolean unitAxesRatio;

	static public MyBasicStrokeW getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStrokeW getDefaultSelectionStroke() {
		return selStroke;
	}
	
	
	/* Code for dashed lines removed in r23713*/

	
	
	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2p.getOffsetWidth();
	}
	
	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2p.getOffsetHeight();
	}
	
	public int getAbsoluteTop() {
		return g2p.getAbsoluteTop();
	}
	
	public int getAbsoluteLeft() {
		return g2p.getAbsoluteLeft();
	}

	public EuclidianControllerW getEuclidianController() {
		return (EuclidianControllerW)euclidianController;
	}

	@Override
    protected void initCursor() {
		setDefaultCursor();
    }

	@Override
    protected void setStyleBarMode(int mode) {
		if (hasStyleBar()) {
			getStyleBar().setMode(mode);
		}
    }


	private ImageElement getResetImage() {
		if (resetImage == null) {
			resetImage = this.app.getRefreshViewImage();
		}
		return resetImage;
	}

	private ImageElement getPlayImage() {
		if (playImage == null) {
			playImage = this.app.getPlayImage();
		}
		return playImage;
	}

	private ImageElement getPauseImage() {
		if (pauseImage == null) {
			pauseImage = this.app.getPauseImage();
		}
		return pauseImage;
	}

	public boolean hitAnimationButton(int x, int y) {
		// draw button in focused EV only
				if (!drawPlayButtonInThisView()) {
					return false;
				}

				return kernel.needToShowAnimationButton() && (x <= 20)
						&& (y >= (getHeight() - 20));
    }

	@Override
    public boolean requestFocusInWindow() {
		g2p.getCanvas().getCanvasElement().focus();	
		focusGained();
		return true;
    }

	public void focusLost() {
		if (isInFocus) {
			this.isInFocus = false;
			this.app.focusLost();
		}
	}

	public void focusGained() {
		if (!isInFocus && !App.isFullAppGui()) {
			this.isInFocus = true;
			this.app.focusGained();
		}
	}

	public void setDefaultCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_default");
    }

	public void setHitCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_hit");
    }
	
	public geogebra.common.euclidian.EuclidianStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = new EuclidianStyleBarW(this);
		}

		return styleBar;
	}
	
	@Override
    protected boolean drawPlayButtonInThisView() {
		return true;
	}
	
	@Override
	final protected void drawAnimationButtons(final geogebra.common.awt.GGraphics2D g2) {

		// draw button in focused EV only
		if (!drawPlayButtonInThisView()) {
			return;
		}

		final int x = 6;
		final int y = getHeight() - 22;

		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			g2.setColor(geogebra.common.awt.GColor.darkGray);
		} else {
			g2.setColor(geogebra.common.awt.GColor.lightGray);
		}

		g2.setStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());

		// draw pause or play button
		g2.drawRect(x - 2, y - 2, 18, 18);
		final ImageElement img = kernel.isAnimationRunning() ? getPauseImage()
				: getPlayImage();
		if (img.getPropertyBoolean("complete")) {
			g2.drawImage(new geogebra.html5.awt.GBufferedImageW(img), null, x, y);
		} else {
			ImageWrapper.nativeon(img,
				"load",
				new ImageLoadCallback() {
					public void onLoad() {
						g2.drawImage(new geogebra.html5.awt.GBufferedImageW(img), null, x, y);
					}
				}
			);
		}
	}

	

	@Override
  public void setPreferredSize(GDimension preferredSize) {
    g2p.setPreferredSize(preferredSize);
    updateSize();
    setReIniting(false);
  }

	/**
	 * Updates the size of the canvas and coordinate system
	 * @param width the new width (in pixel)
	 * @param height the new height (in pixel)
	 */
	public void setPreferredSize(int width, int height) {
		setPreferredSize(new geogebra.html5.awt.GDimensionW(width, height));
	}

	public void setDragCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		if (this.app.useTransparentCursorWhenDragging) {
			g2p.getCanvas().addStyleName("cursor_transparent");
		} else {
			g2p.getCanvas().addStyleName("cursor_drag");
		}
    }

	public void setToolTipText(String plainTooltip) {
	    ToolTipManagerW.sharedInstance().showToolTip(plainTooltip);    
    }

	public void setResizeXAxisCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeXAxis");
    }

	public void setResizeYAxisCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_resizeYAxis");
    }
	

	public void setMoveCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_move");
    }

	@Override
    public void setTransparentCursor() {
		this.app.resetCursor();
		g2p.getCanvas().setStyleName("");
		g2p.getCanvas().addStyleName("cursor_transparent");
    }

	@Override
    public void setEraserCursor() {
		Log.warn("setEraserCursor() unimplemented");	    
    }

	public boolean hasFocus() {
	    // changed to return true, otherwise Arrow keys don't work to pan the view, see GlobalKeyDispatcher
		//return isInFocus;
		return true;
    }

	@Override
    public void add(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().add(
	    		GBoxW.getImpl(box),
	    		(int)box.getBounds().getX(), (int)box.getBounds().getY());
    }

	@Override
    public void remove(GBox box) {
		if (EVPanel != null)
			EVPanel.getAbsolutePanel().remove(
	    		GBoxW.getImpl(box));
	}

	@Override
	protected void drawResetIcon(geogebra.common.awt.GGraphics2D g){
		int w = getWidth() + 2;
		((GGraphics2DW)g).getCanvas().getContext2d().drawImage(
			getResetImage(), w - 18, 2);
	}


	public void synCanvasSizeWithApp(int canvasWidth, int canvasHeight) {
		g2p.setWidth(canvasWidth);
		g2p.setHeight(canvasHeight);
		setCoordinateSpaceSize(g2p.getOffsetWidth(), g2p.getOffsetHeight());
    }

	@Override
    protected void doDrawPoints(GeoImage gi, List<GPoint> penPoints2,
            GColor penColor, int penLineStyle, int penSize) {
	    App.debug("doDrawPoints() unimplemented");
	    
    }
	
	/*needed because set the id of canvas*/
	@Override
    public void setEuclidianViewNo(int evNo) {
		if (evNo >= 2) {
			this.evNo = evNo;
			this.g2p.getCanvas().getElement().setId("View_"+App.VIEW_EUCLIDIAN2);
		}
	}


	public void requestFocus() {
	    App.debug("unimplemented");
    }

	@Override
	public void updateVisualStyle(GeoElement geo) {
		super.updateVisualStyle(geo);

		if (styleBar!=null)
			styleBar.updateVisualStyle(geo);
	}
}
