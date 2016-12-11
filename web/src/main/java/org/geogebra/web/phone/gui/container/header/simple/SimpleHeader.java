package org.geogebra.web.phone.gui.container.header.simple;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.phone.PhoneLookAndFeel;
import org.geogebra.web.phone.gui.container.header.Header;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.HeaderPanel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class SimpleHeader extends FlowPanel implements Header {


	private SimplePanel leftFloatPanel;
	private FlowPanel rightTabPanel;

	private List<SimpleHeaderView> views;
	
	private AbstractView activeView;

	public SimpleHeader() {
		setStyleName("PhoneHeader");

		onResize();

		createLeftFloatPanel();
		createRightTabPanel();

		views = new ArrayList<SimpleHeaderView>();
	}

	public void onResize() {
		setPixelSize(Window.getClientWidth(),
		        PhoneLookAndFeel.PHONE_HEADER_HEIGHT);
	}

	private void createLeftFloatPanel() {
		leftFloatPanel = new SimplePanel();
		leftFloatPanel.addStyleName("tabLeft");
		add(leftFloatPanel);
	}

	private void createRightTabPanel() {
		rightTabPanel = new FlowPanel();
		rightTabPanel.setStyleName("tabContainer");
		add(rightTabPanel);
	}

	public void addView(AbstractView view) {
		SimpleHeaderView headerView = new SimpleHeaderView(view);
		if (views.size() > 0) {
			views.get(views.size() - 1).removeStyleName("lastTab");
		}
		views.add(headerView);
		headerView.addStyleName("lastTab");
		rightTabPanel.add(headerView);
	}

	public void removeView(AbstractView view) {
		SimpleHeaderView headerView = getSimpleHeaderViewByView(view);
		if (headerView == null) {
			return;
		}
		headerView.removeStyleName("lastTab");
		views.remove(headerView);
		if (views.size() > 0) {
			views.get(views.size() - 1).addStyleName("lastTab");
		}
	}

	public void showView(AbstractView view) {
		if (view == activeView) {
			return;
		}
		SimpleHeaderView headerView = getSimpleHeaderViewByView(view);
		if (headerView == null) {
			return;
		}
		SimpleHeaderView activeViewHeader = getSimpleHeaderViewByView(activeView);
		if (activeViewHeader != null) {
			activeViewHeader.setActive(false);
		}
		headerView.setActive(true);
		activeView = view;
		leftFloatPanel.clear();
		HeaderPanel headerPanel = view.getHeaderPanel();
		if (headerPanel != null) {
			leftFloatPanel.setWidget(headerPanel);
		}
	}

	private SimpleHeaderView getSimpleHeaderViewByView(AbstractView view) {
		for (int i = 0; i < views.size(); i++) {
			SimpleHeaderView headerView = views.get(i);
			if (headerView.getView() == view) {
				return headerView;
			}
		}
		return null;
	}
}
