package geogebra.html5.euclidian;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.ZeroOffset;

import com.google.gwt.user.client.Element;

public class MsZoomer {

	private final IsEuclidianController tc;
	private HasOffsets off;

	private class MsOffset extends ZeroOffset {

		private EuclidianControllerW ec;

		public MsOffset(EuclidianControllerW ec) {
			this.ec = ec;
		}

		@Override
		public int mouseEventX(int clientX) {
			return Math.round(clientX + (zoom() - 1));
		}

		private native int zoom() /*-{
			return $wnd.screen.deviceXDPI / $wnd.screen.logicalXDPI;
		}-*/;

		@Override
		public int mouseEventY(int clientY) {
			return Math.round(clientY + (zoom() - 1));
		}

		@Override
		public int touchEventX(int clientX) {
			return mouseEventX(clientX);
		}

		@Override
		public int touchEventY(int clientY) {
			return mouseEventY(clientY);
		}

		@Override
		public int getEvID() {
			return ec.getViewID();
		}

	}

	public MsZoomer(IsEuclidianController tc) {
		this.tc = tc;
		// this.off = (HasOffsets)this.tc;
		this.off = new MsOffset((EuclidianControllerW) tc);
	}

	private void pointersUp() {
		this.tc.setExternalHandling(false);
	}

	private void twoPointersDown(double x1, double y1, double x2, double y2) {
		this.tc.setExternalHandling(true);
		this.tc.twoTouchStart(x1, y1, x2, y2);
	}

	private void twoPointersMove(double x1, double y1, double x2, double y2) {
		this.tc.twoTouchMove(x1, y1, x2, y2);
	}

	private void setPointerTypeTouch(boolean b) {
		this.tc.setDefaultEventType(b ? PointerEventType.TOUCH
		        : PointerEventType.MOUSE);
	}

	public native void reset()/*-{
		$wnd.first = {
			id : -1
		};
		$wnd.second = {
			id : -1
		};
	}-*/;

	public static native void attachTo(Element element, MsZoomer zoomer) /*-{
		$wnd.first = {
			id : -1
		};
		$wnd.second = {
			id : -1
		};

		element
				.addEventListener(
						"MSPointerMove",
						function(e) {
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								if ($wnd.second.id === e.pointerId) {
									$wnd.second.x = e.x;
									$wnd.second.y = e.y;
									zoomer
											.@geogebra.html5.euclidian.MsZoomer::twoPointersMove(
													DDDD)($wnd.first.x,
													$wnd.first.y,
													$wnd.second.x,
													$wnd.second.y);
								} else {
									$wnd.first.x = e.x;
									$wnd.first.y = e.y;
								}

							}
							zoomer.@geogebra.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2);
						});

		element
				.addEventListener(
						"MSPointerDown",
						function(e) {
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								return;
							}
							if ($wnd.first.id >= 0) {
								$wnd.second.id = e.pointerId;
								$wnd.second.x = e.x;
								$wnd.second.y = e.y;
							} else {
								$wnd.first.id = e.pointerId;
								$wnd.first.x = e.x;
								$wnd.first.y = e.y;
							}
							if ($wnd.first.id >= 0 && $wnd.second.id >= 0) {
								zoomer
										.@geogebra.html5.euclidian.MsZoomer::twoPointersDown(
												DDDD)($wnd.first.x,
												$wnd.first.y, $wnd.second.x,
												$wnd.second.y);
							}

							zoomer.@geogebra.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2);
						});

		element
				.addEventListener(
						"MSPointerUp",
						function(e) {
							if ($wnd.first.id == e.pointerId) {
								$wnd.first.id = -1;
							} else {
								$wnd.second.id = -1;
							}
							zoomer.@geogebra.html5.euclidian.MsZoomer::pointersUp()();
							zoomer.@geogebra.html5.euclidian.MsZoomer::setPointerTypeTouch(Z)(e.pointerType == 2);
						});
	}-*/;

}
