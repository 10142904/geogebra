package geogebra.touch.gui.elements;

import geogebra.touch.gui.algebra.events.FastClickHandler;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CustomButton;

/**
 * 
 * GWT Implementation influenced by Google's FastPressElement: <a
 * href=https://developers
 * .google.com/mobile/articles/fast_buttons>FastButtons</a>
 * 
 * Using Code examples and comments from: <a
 * href=http://stackoverflow.com/questions
 * /9596807/converting-gwt-click-events-to-touch-events>Converting GWT
 * ClickEvents to TouchEvents</a>
 * 
 * The FastButton is used to avoid the 300ms delay on mobile devices (Only do
 * this if you want to ignore the possibility of a double tap - The browser
 * waits to see if we actually want to double top)
 * 
 * The "press" event will occur significantly fast (around 300ms faster).
 * However the biggest improvement is from enabling fast consecutive touches.
 * 
 * If you try to rapidly touch one or more FastButtons, you will notice a MUCH
 * great improvement.
 * 
 * NOTE: Different browsers will handle quick swipe or long hold/drag touches
 * differently. This is an edge case if the user is long pressing or pressing
 * while dragging the finger slightly (but staying on the element) - The browser
 * may or may not fire the event. However, the browser will always fire the
 * regular tap/press very quickly.
 * 
 * 
 * @author ashton with changes from Matthias Meisinger
 * 
 */
public abstract class FastButton extends CustomButton {

	private boolean touchHandled = false;
	private boolean clickHandled = false;
	private boolean touchMoved = false;
	private int touchId;
	private boolean isActive;
	private List<FastClickHandler> handlers;

	public FastButton() {
		setStyleName("button");
		// Sink Click and Touch Events
		// I am not going to sink Mouse events since
		// I don't think we will gain anything

		sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS); // Event.TOUCHEVENTS adds
														// all (Start, End,
														// Cancel, Change)

		this.handlers = new ArrayList<FastClickHandler>();

	}

	public void setActive(boolean active) {
		if (active) {
			onEnablePressStyle();
		} else {
			onDisablePressStyle();
		}
		this.isActive = active;
	}

	/**
	 * Implement the handler for pressing but NOT releasing the button. Normally
	 * you just want to show some CSS style change to alert the user the element
	 * is active but not yet pressed
	 * 
	 * ONLY FOR STYLE CHANGE - Will briefly be called onClick
	 * 
	 * TIP: Don't make a dramatic style change. Take note that if a user is just
	 * trying to scroll, and start on the element and then scrolls off, we may
	 * not want to distract them too much. If a user does scroll off the
	 * element,
	 * 
	 */
	public abstract void onHoldPressDownStyle();

	/**
	 * Implement the handler for release of press. This should just be some CSS
	 * or Style change.
	 * 
	 * ONLY FOR STYLE CHANGE - Will briefly be called onClick
	 * 
	 * TIP: This should just go back to the normal style.
	 */
	public abstract void onHoldPressOffStyle();

	/**
	 * Change styling to disabled
	 */
	public abstract void onDisablePressStyle();

	/**
	 * Change styling to enabled
	 * 
	 * TIP:
	 */
	public abstract void onEnablePressStyle();

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONTOUCHSTART: {
			onTouchStart(event);
			break;
		}
		case Event.ONTOUCHEND: {
			onTouchEnd(event);
			break;
		}
		case Event.ONTOUCHMOVE: {
			onTouchMove(event);
			break;
		}
		case Event.ONMOUSEUP: {
			// because Event.ONCLICK always came twice on desktop browsers oO
			onClick(event);
			break;
		}
		default: {
			// Let parent handle event if not one of the above (?)
			super.onBrowserEvent(event);
		}
		}
	}

	private void onClick(Event event) {
		event.stopPropagation();
		event.preventDefault();

		if (this.touchHandled) {
			// if the touch is already handled, we are on a device that supports
			// touch (so you aren't in the desktop browser)

			this.touchHandled = false; // reset for next press
			this.clickHandled = true; // ignore future ClickEvents
		} else if (!this.clickHandled) {
			// Press not handled yet
			fireFastClickEvent();
		}

		super.onBrowserEvent(event);
	}

	private void onTouchStart(Event event) {

		onHoldPressDownStyle(); // Show style change

		// Stop the event from bubbling up
		event.stopPropagation();

		// Only handle if we have exactly one touch
		if (event.getTargetTouches().length() == 1) {
			Touch start = event.getTargetTouches().get(0);
			this.touchId = start.getIdentifier();
			this.touchMoved = false;
		}

	}

	/**
	 * Check to see if the touch has moved off of the element.
	 * 
	 * NOTE that in iOS the elasticScroll may make the touch/move cancel more
	 * difficult.
	 * 
	 * @param event
	 */
	private void onTouchMove(Event event) {

		if (!this.touchMoved) {
			Touch move = null;

			for (int i = 0; i < event.getChangedTouches().length(); i++) {
				if (event.getChangedTouches().get(i).getIdentifier() == this.touchId) {
					move = event.getChangedTouches().get(i);
				}
			}

			// Check to see if we moved off of the original element

			// Use Page coordinates since we compare with widget's absolute
			// coordinates
			if (move != null) {
				int yCord = move.getPageY();
				int xCord = move.getPageX();

				// is y above element
				boolean yTop = this.getAbsoluteTop() > yCord;
				boolean yBottom = (this.getAbsoluteTop() + this
						.getOffsetHeight()) < yCord; // y below

				// is x to the left of element
				boolean xLeft = this.getAbsoluteLeft() > xCord;
				boolean xRight = (this.getAbsoluteLeft() + this
						.getOffsetWidth()) < xCord; // x to the right

				if (yTop || yBottom || xLeft || xRight) {
					this.touchMoved = true;
					onHoldPressOffStyle();// Go back to normal style
				}
			}

		}

	}

	private void onTouchEnd(Event event) {
		if (!this.touchMoved) {
			this.touchHandled = true;
			fireFastClickEvent();
			event.preventDefault();
			onHoldPressOffStyle();// Change back the style
		}
	}

	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * Use this method in the same way you would use addClickHandler or
	 * addDomHandler
	 * 
	 */
	public void addFastClickHandler(FastClickHandler handler) {
		this.handlers.add(handler);
	}

	/**
	 * 
	 * @param event
	 */
	private void fireFastClickEvent() {

		for (FastClickHandler h : this.handlers) {
			h.onClick();
		}
	}
}
