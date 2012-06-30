package geogebra.web.euclidian;

import java.util.LinkedList;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianPen;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.web.euclidian.event.HasOffsets;
import geogebra.web.euclidian.event.MouseEvent;
import geogebra.web.euclidian.event.TouchEvent;
import geogebra.web.main.Application;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;

public class EuclidianControllerW extends geogebra.common.euclidian.EuclidianController implements MouseDownHandler, MouseUpHandler, 
MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseWheelHandler, ClickHandler, DoubleClickHandler, TouchStartHandler, TouchEndHandler, 
TouchMoveHandler, TouchCancelHandler, GestureStartHandler, GestureEndHandler, GestureChangeHandler, HasOffsets {

	/**
	 * @return offset to get correct getX() in mouseEvents
	 */
	public int getXoffset(){
		return EuclidianViewXOffset;
	}
	private int EuclidianViewXOffset;
	
	private int EuclidianViewYOffset;
	/**
	 * @return offset to get correct getY() in mouseEvents
	 */
	public int getYoffset(){
		return EuclidianViewYOffset;
	}

	private boolean EuclidianOffsetsInited = false;
	
	public boolean isOffsetsUpToDate(){
		return EuclidianOffsetsInited;
	}
	
	public void updateOffsets(){
		EuclidianViewXOffset = ((EuclidianViewW) view).getAbsoluteLeft() + Window.getScrollLeft();
		EuclidianViewYOffset = ((EuclidianViewW) view).getAbsoluteTop() + Window.getScrollTop();	
	}
	
	public EuclidianControllerW(Kernel kernel) {
		setKernel(kernel);
		setApplication(kernel.getApplication());
		
		tempNum = new MyDouble(kernel);
	}
	
	
	public void setApplication(AbstractApplication app) {
		this.app = app;
	}
	
	public  void setView(EuclidianView view) {
		this.view = view;
	}

	public void onGestureChange(GestureChangeEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onGestureEnd(GestureEndEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onGestureStart(GestureStartEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onTouchCancel(TouchCancelEvent event) {
		 //AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(event.getNativeEvent());
		 Application.console(event.getAssociatedType().getName());
	}

	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i),this);
			wrapMouseDragged(e);
			e.release();
		}
		//to not move the canvas (later some sophisticated handling must be find out)
		event.preventDefault();
		event.stopPropagation();
	}

	public void onTouchEnd(TouchEndEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			 AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i),this);
			 e.release();
			 //should be substracted the event just ended, and call mouseevent for that.
			 //later :-)
		}
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}

	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		for (int i = 0; i < targets.length(); i++) {
			AbstractEvent e = geogebra.web.euclidian.event.TouchEvent.wrapEvent(targets.get(i),this);
			wrapMousePressed(e);
			e.release();
		}
		//to not move the canvas (later some sophisticated handling must be find out)
				event.preventDefault();
				event.stopPropagation();
	}
	
	private boolean DRAGMODE_MUST_BE_SELECTED = false;

	public void onDoubleClick(DoubleClickEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		 wrapMouseclicked(e);
		 e.release();
	}

	public void onClick(ClickEvent event) {
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		 wrapMouseclicked(e);
		 e.release();
	}

	public void onMouseWheel(MouseWheelEvent event) {
		//don't want to roll the scrollbar
		 event.preventDefault();
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),event.getDeltaY(),this);
		 wrapMouseWheelMoved(e);
		 e.release();
	}

	public void onMouseOver(MouseOverEvent event) {
		 wrapMouseEntered();
	}

	public void onMouseOut(MouseOutEvent event) {
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMouseExited(e);
		e.release();
	}

	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
		 AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		 if (!DRAGMODE_MUST_BE_SELECTED) {
			 wrapMouseMoved(e);
		 } else {
			 wrapMouseDragged(e);
		 }
		 e.release();
	}

	public void onMouseUp(MouseUpEvent event) {
		DRAGMODE_MUST_BE_SELECTED = false;
		event.preventDefault();		 

		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMouseReleased(e);
		e.release();
	}

	public void onMouseDown(MouseDownEvent event) {
		DRAGMODE_MUST_BE_SELECTED = true;
		if(!textfieldHasFocus) event.preventDefault();
			
		AbstractEvent e = geogebra.web.euclidian.event.MouseEvent.wrapEvent(event.getNativeEvent(),this);
		wrapMousePressed(e);
		//hide PopUp if no hits was found.
		if (view.getHits().isEmpty()) {
			if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
				EuclidianStyleBarW.CURRENT_POP_UP.hide();
			}
		}
		e.release();
	}
	
	@Override
	protected void initToolTipManager() {
		AbstractApplication.debug("initToolTipManager: implementation needed really"); // TODO Auto-generated
		
	}

	@Override
	protected GeoElement[] createCircle2ForPoints3D(GeoPointND p0, GeoPointND p1) {
		AbstractApplication.debug("implementation needed for 3D"); // TODO Auto-generated
		return null;
	}

	@Override
	protected void resetToolTipManager() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean hitResetIcon() {
		return app.showResetIcon()
				&& ((mouseLoc.y < 20) && (mouseLoc.x > (view.getViewWidth() - 18)));
	}
	private LinkedList<MouseEvent> mousePool = new LinkedList<MouseEvent>();
	public LinkedList<MouseEvent> getMouseEventPool() {
	    return mousePool;
    }
	private LinkedList<TouchEvent> touchPool = new LinkedList<TouchEvent>();
	public LinkedList<TouchEvent> getTouchEventPool() {
	    return touchPool;
    }


}
