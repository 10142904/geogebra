package geogebra.html5.event;

import geogebra.common.euclidian.event.PointerEventType;

import java.util.LinkedList;

public interface HasOffsets {
	public LinkedList<PointerEvent> getMouseEventPool();
	public LinkedList<PointerEvent> getTouchEventPool();
	public int mouseEventX(int clientX);
	public int mouseEventY(int clientY);
	public int touchEventX(int clientX);
	public int touchEventY(int clientY);
	public int getEvID();
	public PointerEventType getDefaultEventType();
	public void calculateEnvironment();
}
