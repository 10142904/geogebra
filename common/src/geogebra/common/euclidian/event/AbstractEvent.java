package geogebra.common.euclidian.event;

import geogebra.common.awt.Point;

public abstract class AbstractEvent {

	public abstract Point getPoint();

	public abstract boolean isAltDown();

	public abstract boolean isShiftDown();

	public abstract void release();
	
	public abstract int getID();

	public abstract int getX();
	
	public abstract int getY();

	public abstract boolean isRightClick();

	public abstract boolean isControlDown();

	public abstract int getClickCount();

	public abstract boolean isMetaDown();

	public abstract double getWheelRotation();

	public abstract boolean isMiddleClick();

	public abstract boolean isPopupTrigger();

}
