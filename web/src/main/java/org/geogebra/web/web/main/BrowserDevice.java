package org.geogebra.web.web.main;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.browser.BrowseGUI;
import org.geogebra.web.web.gui.browser.BrowseResources;
import org.geogebra.web.web.gui.dialog.image.ImageInputDialog;
import org.geogebra.web.web.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.web.gui.dialog.image.UploadImageWithoutDialog;
import org.geogebra.web.web.gui.dialog.image.WebcamInputDialog;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolViewW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Device class for case we are running in the browser (eg Chrome app)
 */
public class BrowserDevice implements GDevice {
	/**
	 * Button for opening local files
	 *
	 */
	public static class FileOpenButton extends FlowPanel {
		private Element input;

		/**
		 * New button
		 */
		public FileOpenButton() {
			super();
			this.setStyleName("button");
			final Image icon = new Image(
			        BrowseResources.INSTANCE.location_local());
			final Element span = DOM.createElement("span");
			span.setAttribute(
			        "style",
			        "position: absolute; width: 50px; height: 50px; padding: 10px; top: 0px; left: 0px; overflow: hidden;");
			span.setInnerHTML("<img src=\"" + icon.getUrl() + "\"/>");
			Element form = DOM.createElement("form");
			input = DOM.createElement("input");
			input.setAttribute("type", "file");
			input.setAttribute(
			        "style",
			        "width: 500px; height: 60px; font-size: 56px;"
			                + "opacity: 0; position: absolute; right: 0px; top: 0px; cursor: pointer;");
			form.appendChild(input);
			span.appendChild(form);

			DOM.insertChild(getElement(), span, 0);

		}

		/**
		 * @param bg
		 *            browsing gui
		 */
		public void setBrowseGUI(BrowseGUI bg) {
			addGgbChangeHandler(input, bg);
		}

		private native void addGgbChangeHandler(Element el, BrowseGUI bg) /*-{
			var dialog = this;
			//		el.setAttribute("accept", "application/vnd.geogebra.file, application/vnd.geogebra.tool");
			el.onchange = function(event) {
				var files = this.files;
				if (files.length) {
					bg.@org.geogebra.web.web.gui.browser.BrowseGUI::showLoading()();
					var fileToHandle = files[0];
					bg.@org.geogebra.web.web.gui.browser.BrowseGUI::openFile(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(fileToHandle);
				}
				el.parentElement.reset();
			};
		}-*/;
	}

	@Override
	public FileManager createFileManager(AppW app) {
		return new FileManagerW(app);
	}

	@Override
	public boolean isOffline(AppW app) {
		return !app.getNetworkOperation().isOnline();
	}

	@Override
	public UploadImageDialog getImageInputDialog(AppW app) {

		return new ImageInputDialog(app);
	}

	/**
	 * @param app
	 *            application
	 * @return WebcamInputDialog
	 */
	public WebcamInputDialog getWebcamInputDialog(AppW app) {
		return new WebcamInputDialog(app);
	}

	public UploadImageWithoutDialog getUploadImageWithoutDialog(AppW app) {
		return new UploadImageWithoutDialog(app);
	}

	@Override
	public BrowseGUI createBrowseView(AppW app) {
		FileOpenButton mb = new FileOpenButton();
		BrowseGUI bg = new BrowseGUI(app, mb);
		mb.setBrowseGUI(bg);
		return bg;
	}

	@Override
	public ConstructionProtocolView getConstructionProtocolView(AppW app) {
		return new ConstructionProtocolViewW(app);
	}

	@Override
	public void resizeView(int width0, int height0) {
		if (width0 > Browser.getScreenWidth()
				|| height0 > Browser.getScreenHeight()) {
			int width = Browser.getScreenWidth();
			int height = Browser.getScreenHeight();
			Window.moveTo(0, 0);
			Window.resizeTo(width, height);
		} else {
			Window.resizeTo(width0, height0);
		}

	}


}
