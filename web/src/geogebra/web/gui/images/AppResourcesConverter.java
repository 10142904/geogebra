package geogebra.web.gui.images;

import geogebra.web.gui.util.SelectionTable;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class AppResourcesConverter {
	
	private static Canvas tmpCanvas = null;
	private static int waitingForConvert = 0;
	static ImageData [] converted = null;
	private static SelectionTable sT;
	
	private static Canvas getTmpCanvas() {
		if (tmpCanvas == null) {
			tmpCanvas = Canvas.createIfSupported();
		}
		return tmpCanvas;
	}

	public static void convertImageResourceToImageData(Object[] data,
            SelectionTable selectionTable) {
		waitingForConvert = data.length;
		converted = new ImageData[waitingForConvert];
		for (int i = 0; i < waitingForConvert; i++ ) {
		   convertToImageData(data[i],i);
		}
		sT = selectionTable;
    }

	private static void convertToImageData(Object object, final int index) {
	   ImageResource is = (ImageResource) object;
	   final Image i = new Image(is.getSafeUri());
	   i.addLoadHandler(new LoadHandler() {
		
		public void onLoad(LoadEvent event) {
			Context2d c = getTmpCanvas().getContext2d();
			int w = i.getWidth();
			int h = i.getHeight();
			getTmpCanvas().setCoordinateSpaceWidth(w);
			getTmpCanvas().setCoordinateSpaceHeight(h);
			c.clearRect(0, 0, w, h);
			c.drawImage(ImageElement.as(i.getElement()), 0, 0);
			converted[index] = c.getImageData(0, 0, w, h);
			waitingForConvert--;
			checkIfCanCallCallback();
		}
	   });
	   i.setVisible(false);
	   RootPanel.get().add(i);
    }

	private static void checkIfCanCallCallback() {
		if (waitingForConvert == 0) {
			sT.populateModelCallback(converted);
		}
	}

}
