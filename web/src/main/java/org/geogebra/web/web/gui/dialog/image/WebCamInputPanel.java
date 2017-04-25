package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for HTML5 webcam input
 */
public class WebCamInputPanel extends VerticalPanel {
	
	private SimplePanel inputWidget;
	private Element video;
	private JavaScriptObject stream;
	private int canvasWidth = 640, canvasHeight = 480;// overwritten by real
														// dimensions
	
	private AppW app;
	private static final int MAX_CANVAS_WIDTH = 640;

	/**
	 * @param app
	 *            application
	 */
	public WebCamInputPanel(AppW app) {
	    this.app = app;
	    initGUI();
    }

	private void initGUI() {		
		inputWidget = new SimplePanel();
		resetVideo();

		add(inputWidget);
	}

	private native Element populate(Element el, String message,
			String errorMessage) /*-{

		el.style.position = "relative";

		var ihtml = "<span style='position:absolute;width:213px;height:160px;text-align:center;'><br><br>"
				+ message + "</span>\n";
		ihtml += "<video width='213' height='160' autoplay><br><br>"
				+ errorMessage + "</video>";
		el.innerHTML = ihtml;
		var video = el.lastChild;

		$wnd.navigator.getMedia = ($wnd.navigator.getUserMedia
				|| $wnd.navigator.webkitGetUserMedia
				|| $wnd.navigator.mozGetUserMedia || $wnd.navigator.msGetUserMedia);

		$wnd.URL = $wnd.URL || $wnd.webkitURL || $wnd.msURL || $wnd.mozURL
				|| $wnd.oURL || null;
		var that = this;
		if ($wnd.navigator.getMedia) {
			try {
				$wnd.navigator
						.getMedia(
								{
									video : true
								},
								function(bs) {
									if ($wnd.URL && $wnd.URL.createObjectURL) {
										video.src = $wnd.URL
												.createObjectURL(bs);
										el.firstChild.style.display = "none";
									} else {
										video.src = bs;
										el.firstChild.style.display = "none";
									}
									that.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stream = bs;
								},

								function(err) {
									@org.geogebra.common.util.debug.Log::debug(Ljava/lang/String;)("Error from WebCam: "+err);
								});

				return video;
			} catch (e) {
				el.firstChild.innerHTML = "<br><br>" + errorMessage;
				return null;

			}
		} else {
			el.firstChild.innerHTML = "<br><br>" + errorMessage;
		}
		return null;
	}-*/;

	private native String shotcapture(Element video1) /*-{
		var canvas = $doc.createElement("canvas");
		canvas.width = Math
				.max(video1.videoWidth || 0,
						@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH);
		canvas.height = video1.videoHeight ? Math.round(canvas.width
				* video1.videoHeight / video1.videoWidth)
				: 0.75 * @org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::MAX_CANVAS_WIDTH;
		var ctx = canvas.getContext('2d');
		ctx.drawImage(video1, 0, 0);
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stopVideo()();
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::canvasWidth = canvas.width;
		this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::canvasHeight = canvas.height;
		return canvas.toDataURL('image/png');
	}-*/;

	/**
	 * Stop recording
	 */
	public native void stopVideo() /*-{
		var stream = this.@org.geogebra.web.web.gui.dialog.image.WebCamInputPanel::stream;
		if (stream == null) {
			return;
		}
		if (stream.stop) {
			stream.stop();
		} else {
			stream.getVideoTracks()[0].stop();
		}
		stream = null;
	}-*/;

	/**
	 * @return screenshot as data URL (png)
	 */
	public String getImageDataURL() {
		if (video == null) {
			return null;
		}
		return shotcapture(video);
	}

	/**
	 * Starts recording
	 */
	public void startVideo() {
		stopVideo();
		inputWidget.getElement().removeAllChildren();
		resetVideo();

	    
    }

	private void resetVideo() {
		Localization loc = app.getLocalization();
		String message;
		if (app.getVersion() == Versions.WEB_FOR_DESKTOP) {
			message = "";
		} else if (Browser.isFirefox()) {
			message = loc.getMenu("Webcam.Firefox"); 
		}else if(Browser.isEdge()){
			message = loc.getMenu("Webcam.Edge");
		}else{
			message = loc.getMenu("Webcam.Chrome");
		}
		video = populate(inputWidget.getElement(), message,
				loc.getMenu("Webcam.Problem"));

	}

	/**
	 * @return screenshot width
	 */
	public int getCanvasWidth() {
		return canvasWidth;
	}

	/**
	 * @return screenshot height
	 */
	public int getCanvasHeight() {
		return canvasHeight;
	}
}
