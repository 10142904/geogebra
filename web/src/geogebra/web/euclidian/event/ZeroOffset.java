package geogebra.web.euclidian.event;

import java.util.LinkedList;

public class ZeroOffset implements HasOffsets {

	public int getXoffset() {
	    return 0;
    }

	public int getYoffset() {
	    return 0;
    }

	public boolean isOffsetsUpToDate() {
	    return true;
    }

	public void updateOffsets() {
	    // nothing to update
    }
	public static final ZeroOffset instance = new ZeroOffset();
	private LinkedList<MouseEvent> mousePool = new LinkedList<MouseEvent>();
	public LinkedList<MouseEvent> getMouseEventPool() {
	    return mousePool;
    }
	private LinkedList<TouchEvent> touchPool = new LinkedList<TouchEvent>();
	public LinkedList<TouchEvent> getTouchEventPool() {
	    return touchPool;
    }
}
