package org.geogebra.web.web.gui.images;

import org.geogebra.web.web.gui.ImageFactory;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

@SuppressWarnings("javadoc")
public interface PerspectiveResources {
	final static PerspectiveResources INSTANCE = ((ImageFactory) GWT
			.create(ImageFactory.class)).getPerspectiveResources();
	
	ResourcePrototype menu_icon_algebra();

	ResourcePrototype menu_icon_algebra24();
	

	ResourcePrototype menu_icon_geometry();

	ResourcePrototype menu_icon_geometry24();
	
	ResourcePrototype menu_icon_cas();

	ResourcePrototype menu_icon_cas24();
	
	ResourcePrototype menu_icon_graphics();

	ResourcePrototype menu_icon_graphics24();
	
	ResourcePrototype menu_icon_graphics1();
	
	ResourcePrototype menu_icon_graphics2();

	ResourcePrototype menu_icon_graphics224();

	ResourcePrototype menu_icon_graphics_extra();

	ResourcePrototype menu_icon_graphics_extra24();

	ResourcePrototype menu_icon_spreadsheet();

	ResourcePrototype menu_icon_spreadsheet24();
	
	ResourcePrototype menu_icon_graphics3D();

	ResourcePrototype menu_icon_graphics3D24();
	
	ResourcePrototype menu_icon_construction_protocol();
	
	ResourcePrototype menu_icon_construction_protocol24();

	ResourcePrototype menu_icon_probability();

	ResourcePrototype menu_icon_probability24();
	
	ResourcePrototype menu_icon_whiteboard();

	ResourcePrototype menu_icon_whiteboard24();

	ResourcePrototype menu_icon_exam();

	ResourcePrototype menu_icon_exam24();

	// StyleBar
	ResourcePrototype settings();

	ResourcePrototype styleBar_algebraView();

	ResourcePrototype styleBar_graphicsView();

	ResourcePrototype styleBar_CASView();

	ResourcePrototype styleBar_ConstructionProtocol();

	ResourcePrototype styleBar_graphics3dView();

	ResourcePrototype styleBar_graphics_extra();

	ResourcePrototype styleBar_graphics2View();

	ResourcePrototype styleBar_spreadsheetView();

	ResourcePrototype back_right();
		
	ResourcePrototype menu_header_redo();

	ResourcePrototype menu_header_redo_hover();

	ResourcePrototype menu_header_undo();

	ResourcePrototype menu_header_undo_hover();

	ResourcePrototype menu_header_open_search();

	ResourcePrototype menu_header_open_search_hover();

	ResourcePrototype menu_header_open_menu();

	ResourcePrototype menu_header_open_menu_hover();

	ResourcePrototype menu_header_back();


}
