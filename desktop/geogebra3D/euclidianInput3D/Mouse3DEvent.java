package geogebra3D.euclidianInput3D;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.euclidian.event.MouseEventND;

import java.awt.Component;

/**
 * Class for 3D mouse event
 * @author mathieu
 *
 */
public class Mouse3DEvent extends AbstractEvent implements MouseEventND{

	private GPoint point;
	
	private Component component;
	
	/**
	 * constructor
	 * @param point point
	 */
	public Mouse3DEvent(GPoint point, Component component){
		this.point = point;
		this.component = component;
	}
	
	@Override
	public GPoint getPoint() {
		return point;
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public boolean isShiftDown() {
		return false;
	}

	@Override
	public void release() {
		//nothing to do		
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return point.x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return point.y;
	}

	@Override
	public boolean isRightClick() {
		return false;
	}

	@Override
	public boolean isControlDown() {
		return false;
	}

	@Override
	public int getClickCount() {
		return 0;
	}

	@Override
	public boolean isMetaDown() {
		return false;
	}

	@Override
	public double getWheelRotation() {
		return 0;
	}

	@Override
	public boolean isMiddleClick() {
		return false;
	}

	@Override
	public boolean isPopupTrigger() {
		return false;
	}
	

	public java.awt.Component getComponent() {		
		return component;
	}


}
