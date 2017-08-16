package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawLine;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoElementSelectionListener;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

/**
 * Dynamically positioned stylebar
 * 
 * @author Judit
 *
 */
public class DynamicStyleBar extends EuclidianStyleBarW {

	/**
	 * @param ev
	 *            parent view
	 */
	public DynamicStyleBar(EuclidianView ev) {
		super(ev, -1);
		if (!app.isUnbundled() && !app.isWhiteboardActive()) {
			addStyleName("DynamicStyleBar");
		} else {
			addStyleName("matDynStyleBar");
		}

		app.getSelectionManager()
				.addSelectionListener(new GeoElementSelectionListener() {
					@Override
					public void geoElementSelected(GeoElement geo,
							boolean addToSelection) {
						if (addToSelection) {
							return;
						}

						if (app.has(Feature.LOCKED_GEO_HAVE_DYNAMIC_STYLEBAR)) {
							// If the activeGeoList will be null or empty, this will
							// hide the dynamic stylebar.
							// If we clicked on a locked geo, the activeGeoList will
							// contain it, so in this case the dynamic stylebar will
							// be visible yet.
							DynamicStyleBar.this.updateStyleBar();
						} else {
							DynamicStyleBar.this.setVisible(addToSelection);
						}
					}
				});
		stopPointer(getElement());
	}

	private native void stopPointer(Element element) /*-{
		if ($wnd.PointerEvent) {
			var evts = [ "PointerDown", "PointerUp" ];
			for ( var k in evts) {
				element.addEventListener(evts[k].toLowerCase(), function(e) {
					e.stopPropagation()
				});
			}
		}

	}-*/;

	private GPoint calculatePosition(GRectangle2D gRectangle2D,
			boolean hasBoundingBox, boolean isPoint, boolean isFunction) {

		if (gRectangle2D == null && !isFunction) {
			return null;
		}

		int move = this.getContextMenuButton().getAbsoluteLeft()
				- this.getAbsoluteLeft();
		int height = this.getOffsetHeight();

		double left, top = -1;

		if (isFunction) {
			GPoint mouseLoc = this.getView().getEuclidianController()
					.getMouseLoc();
			if (mouseLoc == null) {
				return null;
			}
			top = mouseLoc.y + 10;
		} else if (!isPoint) {
			if (hasBoundingBox) {
				top = gRectangle2D.getMinY() - height - 10;
			} else { // line has no bounding box
				top = gRectangle2D.getMinY();
			}
		}

		// if there is no enough place on the top of bounding box, dynamic
		// stylebar will be visible at the bottom of bounding box,
		// stylebar of points will be bottom of point if possible.
		if (top < 0) {
			top = gRectangle2D.getMaxY() + 10;
		}

		int maxtop = app.getActiveEuclidianView().getHeight() - height - 5;
		if (top > maxtop) {
			if (isFunction) {
				top = maxtop;
			} else if (isPoint) {
				// if there is no enough place under the point
				// put the dyn. stylebar above the point
				top = gRectangle2D.getMinY() - height - 10;
			} else {
				top = maxtop;
			}
		}


		// get left position
		if (isFunction) {
			left = this.getView().getEuclidianController().getMouseLoc().x + 10;
		} else if (hasBoundingBox) {
			left = gRectangle2D.getMaxX() - move;
		} else { // line has no bounding box
			left = gRectangle2D.getMaxX();
		}
		if (left < 0) {
			left = 0;
		}
		if (left + this.getOffsetWidth() > app.getActiveEuclidianView().getWidth()) {
			left = app.getActiveEuclidianView().getWidth() - this.getOffsetWidth();
		}

		return new GPoint((int) left, (int) top);
	}

	@Override
	public void updateStyleBar() {
		if (!isVisible()) {
			return;
		}

		// make sure it reflects selected geos

		setMode(EuclidianConstants.MODE_MOVE);
		super.updateStyleBar();

		if (activeGeoList == null || activeGeoList.size() == 0) {
			this.setVisible(false);
			return;
		}
		
		this.getElement().getStyle().setTop(-10000, Unit.PX);

		if (app.has(Feature.DYNAMIC_STYLEBAR_SELECTION_TOOL)
				&& app.getMode() == EuclidianConstants.MODE_SELECT) {
			setPosition(calculatePosition(
					app.getActiveEuclidianView().getSelectionRectangle(), true,
					false, false));
			return;
		}

		GPoint newPos = null, nextPos;
		boolean hasVisibleGeo = false;

		for (int i = 0; i < (app
				.has(Feature.DYNAMIC_STYLEBAR_POSITION_MULTISELECT)
						? activeGeoList.size() : 1); i++) {
			GeoElement geo = activeGeoList.get(i);
			// it's possible if a non visible geo is in activeGeoList, if we
			// duplicate a geo, which has descendant.
			if (geo.isEuclidianVisible()) {
				hasVisibleGeo = true;
				if (app.has(Feature.FUNCTIONS_DYNAMIC_STYLEBAR_POSITION)
						&& geo instanceof GeoFunction) {
					if (getView().getHits().contains(geo)) {
						nextPos = calculatePosition(null, true, false, true);
					} else {
						nextPos = null;
					}
				} else {
					Drawable dr = (Drawable) ev.getDrawableND(geo);
					nextPos = calculatePosition(
							dr.getBoundsForStylebarPosition(),
							!(dr instanceof DrawLine),
							dr instanceof DrawPoint && activeGeoList.size() < 2,
							false);
				}

				if (newPos == null) {
					newPos = nextPos;
				} else if (nextPos != null) {
					newPos.x = Math.max(newPos.x, nextPos.x);
					newPos.y = Math.min(newPos.y, nextPos.y);
				}
			}
		}

		// Maybe more functions are selected, but not hitted - in this case
		// position of dynamic stylebar will be calculated accordintly of
		// mouse position.
		if (hasVisibleGeo && newPos == null) {
			newPos = calculatePosition(null, true, false, true);
		}

		setPosition(newPos);
	}

	/**
	 * Sets the position of dynamic style bar. for newPos
	 */
	private void setPosition(GPoint newPos) {
		if (newPos == null) {
			return;
		}
		this.getElement().getStyle().setLeft(newPos.x, Unit.PX);
		this.getElement().getStyle().setTop(newPos.y, Unit.PX);

	}

	@Override
	protected boolean isDynamicStylebar(){
		return true;
	}
	
	public void setVisible(boolean v) {
		// Close label popup if opened when dynamic stylebar visiblity changed
		if (app.has(Feature.CLOSE_LABEL_DIALOG_AT_ESC) && isVisible()) {
			closeLabelPopup();
		}
		super.setVisible(v);

	}

}
