package org.geogebra.web.geogebra3D.web.gui.view.properties;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.properties.PropertiesStyleBarW;
import org.geogebra.web.web.gui.properties.PropertiesViewW;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

import com.google.gwt.core.shared.GWT;

/**
 * Style bar for properties view (in 3D)
 * 
 * @author mathieu
 *
 */
public class PropertiesStyleBar3DW extends PropertiesStyleBarW {

	/**
	 * constructor
	 * 
	 * @param propertiesView
	 *            properties view
	 * @param app
	 *            application
	 */
	public PropertiesStyleBar3DW(PropertiesViewW propertiesView, AppW app) {
		super(propertiesView, app);
	}

	@Override
	protected void setIcon(OptionType type, PopupMenuButtonW btn) {
		switch (type) {
		case EUCLIDIAN3D :
			PerspectiveResources pr = ((ImageFactory) GWT
			        .create(ImageFactory.class)).getPerspectiveResources();
			ImgResourceHelper.setIcon(pr.menu_icon_graphics3D(), btn);
			break;
		case EUCLIDIAN_FOR_PLANE :
			pr = ((ImageFactory) GWT
			        .create(ImageFactory.class)).getPerspectiveResources();
			ImgResourceHelper.setIcon(pr.menu_icon_graphics_extra(), btn);
			break;
		default:
			super.setIcon(type, btn);
			break;
		}
	}

	@Override
	protected String getTypeIcon(OptionType type) {
		switch (type) {
		case EUCLIDIAN3D:
			PerspectiveResources pr = ((ImageFactory) GWT
			        .create(ImageFactory.class)).getPerspectiveResources();
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics3D());
		case EUCLIDIAN_FOR_PLANE:
			pr = ((ImageFactory) GWT.create(ImageFactory.class))
					.getPerspectiveResources();
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics_extra());
		}
		return super.getTypeIcon(type);
	}

	@Override
	public void updateGUI() {

		super.updateGUI();

		setButtonVisible(OptionType.EUCLIDIAN3D,
		        app.getGuiManager().showView(App.VIEW_EUCLIDIAN3D));

		setButtonVisible(OptionType.EUCLIDIAN_FOR_PLANE,
				app.hasEuclidianViewForPlaneVisible());

	}

	@Override
	protected boolean typeAvailable(OptionType type) {
		return true;
	}

}
