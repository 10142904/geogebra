package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.Timer;

/**
 * Class used in view controllers to handle long touches.
 */
public class LongTouchTimer extends Timer {

	private static final int SHOW_CONTEXT_MENU_DELAY = 500;

	private static final int MOVE_THRESHOLD = 10;

	private LongTouchHandler handler;
	private int x;
	private int y;
	private boolean longTouchHappened = false;

	/**
	 * Interface for handling long touches.
	 */
	public interface LongTouchHandler {
		/**
		 * Handles the long touch event.
		 * 
		 * @param x
		 *            the x coordinate of the long touch
		 * @param y
		 *            the y coordinate of the long touch
		 */
		public void handleLongTouch(int x, int y);
	}

	public LongTouchTimer() {
		this(null);
	}

	/**
	 * @param handler
	 *            used when the timer elapsed.
	 */
	public LongTouchTimer(LongTouchHandler handler) {
		this.handler = handler;
		this.x = 0;
		this.y = 0;
		longTouchHappened = false;
	}

	@Override
	public void run() {
		if (handler == null) {
			return;
		}
		longTouchHappened = true;
		handler.handleLongTouch(x, y);
	}

	/**
	 * Schedules the timer with a default delay value.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 */
	public void schedule(LongTouchHandler handler, int x, int y) {
		longTouchHappened = false;
		schedule(handler, x, y, SHOW_CONTEXT_MENU_DELAY);
	}

	/**
	 * Schedules the timer with {@code delayMillis} ms.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 * @param delayMillis
	 *            how long to wait before the timer elapses, in milliseconds
	 */
	public void schedule(LongTouchHandler handler, int x, int y,
			int delayMillis) {
		this.handler = handler;
		this.x = x;
		this.y = y;
		longTouchHappened = false;
		schedule(delayMillis);
	}

	/**
	 * Reschedules the timer if it is running and the new mouse location is
	 * within boundaries, with a default delay value.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 */
	public void rescheduleIfRunning(LongTouchHandler handler, int x, int y) {
		rescheduleIfRunning(handler, x, y, SHOW_CONTEXT_MENU_DELAY, true);
	}

	/**
	 * Reschedules the timer if it is running, with a default delay value.
	 * 
	 * @param handler
	 *            the handler to use when the timer fires
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 * @param shouldCancel
	 *            if true, the timer will be cancelled if the mouse moved too
	 *            much
	 */
	public void rescheduleIfRunning(LongTouchHandler handler, int x, int y,
			boolean shouldCancel) {
		rescheduleIfRunning(handler, x, y, SHOW_CONTEXT_MENU_DELAY,
				shouldCancel);
	}

	/**
	 * Reschedules the timer if it is running, with {@code delayMillis} ms.
	 * 
	 * @param x
	 *            the x coordinate passed to the handler
	 * @param y
	 *            the y coordinate passed to the handler
	 * @param delayMillis
	 *            how long to wait before the timer elapses, in milliseconds
	 * @param shouldCancel
	 *            if true, the timer will be cancelled if the mouse moved too
	 *            much
	 */
	public void rescheduleIfRunning(LongTouchHandler handler, int x, int y,
			int delayMillis, boolean shouldCancel) {
		if (isRunning()) {
			longTouchHappened = false;
			cancel();
			if (!shouldCancel || pointWithinLimit(x, y)) {
				schedule(handler, x, y, delayMillis);
			}
		}
	}

	public void cancelTimer() {
		x = 0;
		y = 0;
		handler = null;
		longTouchHappened = false;
		cancel();
	}

	private boolean pointWithinLimit(int nx, int ny) {
		if (Math.abs(nx - x) < MOVE_THRESHOLD
				&& Math.abs(ny - y) < MOVE_THRESHOLD) {
			return true;
		}
		return false;
	}

	public boolean isLongTouchHappened() {
		return longTouchHappened;
	}

}
