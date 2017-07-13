package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ContextMenuGeoElementW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.MyCJButton;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * context menu
 */
public class ContextMenuPopup extends MyCJButton
		implements CloseHandler<GPopupPanel>, MouseOverHandler {

	private EuclidianController ec;
	private GPoint location;
	private boolean menuShown = false;
	private AppW app;
	/**
	 * context menu
	 */
	ContextMenuGeoElementW popup;

	/**
	 * @param app
	 *            - application
	 */
	public ContextMenuPopup(AppW app) {
		super();
		this.app = app;
		ImgResourceHelper.setIcon(app.has(Feature.NEW_TOOLBAR)
				? MaterialDesignResources.INSTANCE.more_vert_black()
				:AppResources.INSTANCE.dots(), this);
		ec = app.getActiveEuclidianView().getEuclidianController();
		location = new GPoint();
		updateLocation();
		createPopup();
		addStyleName("MyCanvasButton-borderless");

	}


	private void updateLocation() {
		int x = getAbsoluteLeft();
		int y = getAbsoluteTop() + getOffsetHeight();
		location.setLocation(x, y);
	}

	private void createPopup() {
		popup = ((GuiManagerW) app.getGuiManager())
				.getPopupMenu(ec.getAppSelectedGeos());
		popup.getWrappedPopup().getPopupPanel().addCloseHandler(this);
		// addClickHandler(this);
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isMenuShown()) {
					hideMenu();
				} else {
					showMenu();
				}

			}
		});
		ClickEndHandler.init(this, new ClickEndHandler(false, true) {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// only stop

			}
		});
		this.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				switchIcon(true);
			}
		});
		this.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				switchIcon(false);
			}
		});
	}

	/**
	 * switch img on hover
	 * 
	 * @param isActive
	 *            is hover
	 */
	protected void switchIcon(boolean isActive) {
		if (isMenuShown()) {
			return;
		}
		if (isActive) {
			ImgResourceHelper
					.setIcon(
							app.has(Feature.NEW_TOOLBAR)
									? MaterialDesignResources.INSTANCE
											.more_vert_purple()
									: AppResources.INSTANCE.dots_active(),
							this);
		} else {
			ImgResourceHelper.setIcon(app.has(Feature.NEW_TOOLBAR)
					? MaterialDesignResources.INSTANCE.more_vert_black()
					: AppResources.INSTANCE.dots(), this);
		}

	}

	/**
	 * @param handler
	 *            - mouse out
	 * @return handler
	 */
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * @param handler
	 *            - mouse over
	 * @return handler
	 */
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	/**
	 * show the menu
	 */
	public void showMenu() {
		updateLocation();
		popup.update();
		popup.show(location);
		ImgResourceHelper.setIcon(app.has(Feature.NEW_TOOLBAR)
				? MaterialDesignResources.INSTANCE.more_vert_purple()
				: AppResources.INSTANCE.dots_active(), this);
		this.addStyleName("noOpacity");
		menuShown = true;

	}

	/**
	 * hide the menu
	 */
	public void hideMenu() {
		menuShown = false;
		ImgResourceHelper.setIcon(app.has(Feature.NEW_TOOLBAR)
				? MaterialDesignResources.INSTANCE.more_vert_black()
				: AppResources.INSTANCE.dots(), this);
		this.removeStyleName("noOpacity");
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		// make context menu btn toggle button
		//hideMenu();
	}
	
	/**
	 * @return in the menu open
	 */
	public boolean isMenuShown() {
		return menuShown;
	}

	/**
	 * close popup
	 */
	public void close() {
		popup.getWrappedPopup().hide();
		hideMenu();
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		ImgResourceHelper.setIcon(app.has(Feature.NEW_TOOLBAR)
				? MaterialDesignResources.INSTANCE.more_vert_purple()
				: AppResources.INSTANCE.dots_active(), this);
	}

}
