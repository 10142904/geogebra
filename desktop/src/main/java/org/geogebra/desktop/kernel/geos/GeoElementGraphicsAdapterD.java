package org.geogebra.desktop.kernel.geos;

import java.awt.Image;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.kernel.geos.GeoElementGraphicsAdapter;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.desktop.util.ImageResourceDImpl;

public class GeoElementGraphicsAdapterD extends GeoElementGraphicsAdapter {

	protected AppD app;

	public GeoElementGraphicsAdapterD(App appl) {
		app = (AppD) appl;
	}

	@Override
	public MyImage getFillImage() {
		if (image != null)
			return image;

		if ("".equals(imageFileName)) {
			return null;
		}

		if (imageFileName.startsWith("/geogebra")) {
			Image im = app.getImageManager()
					.getImageResource(new ImageResourceDImpl(imageFileName));
			if(im == null){
				Log.error(imageFileName + " does not exist");
				return null;
			}
			image = new MyImageD(ImageManagerD.toBufferedImage(im));
		} else {
			/*
			 * java.awt.image.BufferedImage extimg = app
			 * .getExternalImage(imageFileName); if (extimg == null) image =
			 * null; else image = new BufferedImage(extimg);
			 */

			image = app.getExternalImageAdapter(imageFileName, 0, 0);

		}

		return image;
	}

	@Override
	public void setImageFileName(String fileName) {

		if (fileName.equals(this.imageFileName)) {
			return;
		}

		setImageFileNameOnly(fileName);

		if (fileName.startsWith("/geogebra")) { // internal image
			Image im = ((ImageManagerD) ((App) app).getImageManager())
					.getImageResource(new ImageResourceDImpl(imageFileName));
			image = new MyImageD(ImageManagerD.toBufferedImage(im));

		} else {
			image = ((App) app).getExternalImageAdapter(fileName, 0, 0);
		}
	}

	@Override
	public void convertToSaveableFormat() {
		// all openable formats are saveable in Desktop
	}

}
