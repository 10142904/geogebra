package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianSimplePanelW extends AbsolutePanel implements
        EuclidianPanelWAbstract, RequiresResize {

	AppW app;
	int oldHeight = 0;
	int oldWidth = 0;

	Canvas eview1 = null;// static foreground

	/**
	 * This constructor is used by the Application and by the other constructor
	 * 
	 * @param stylebar
	 *            (is there stylebar?)
	 */
	public EuclidianSimplePanelW(boolean stylebar) {
		super();

		loadComponent();
	}

	/**
	 * This constructor is used by the applet
	 * 
	 * @param application
	 * @param stylebar
	 */
	public EuclidianSimplePanelW(AppW application, boolean stylebar) {
		this(stylebar);
		app = application;
	}

	protected Widget loadComponent() {
		eview1 = Canvas.createIfSupported();
		eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		eview1.getElement().getStyle().setZIndex(0);
		getElement().getStyle().setOverflow(Overflow.VISIBLE);
		add(eview1);

		return this;
	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	@Override
	public Panel getEuclidianPanel() {
		return this;
	}

	public void attachApp(App app) {
		this.app = (AppW) app;
	}

	public EuclidianSimplePanelW getEuclidianView1Wrapper() {
		return this;
	}

	@Override
	public AbsolutePanel getAbsolutePanel() {
		return this;
	}

	@Override
	public EuclidianView getEuclidianView() {
		if (app != null) {
			return app.getEuclidianView1();
		}
		return null;
	}

	@Override
	public void onResize() {

		// This is probably not needed, but what if yes?

		if (app != null) {
			int h = getOffsetHeight();
			int w = getOffsetWidth();

			// exit if new size cannot be determined
			if (h <= 0 || w <= 0) {
				return;
			}

			if (h != oldHeight || w != oldWidth) {
				app.ggwGraphicsViewDimChanged(w, h);
				oldHeight = h;
				oldWidth = w;
			}
		}
	}

	@Override
	public void deferredOnResize() {

		// There is probably no need for deferred call here, but what if yes?

		Scheduler.get().scheduleDeferred(onResizeCmd);
		// onResize();
	}

	Scheduler.ScheduledCommand onResizeCmd = new Scheduler.ScheduledCommand() {
		@Override
		public void execute() {
			onResize();
		}
	};

	@Override
	public void updateNavigationBar() {
	}

	@Override
	public void setVisible(boolean sv) {
		super.setVisible(sv);
		if (getEuclidianView() != null) {
			((EuclidianViewW) getEuclidianView()).updateFirstAndLast(sv, false);
		}
	}
}
