package geogebra.web.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface StyleBarResources extends ClientBundle {
	StyleBarResources INSTANCE = GWT.create(StyleBarResources.class);
	
	//EUCLIDIAN STYLEBAR:
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_axes.png")
	ImageResource axes();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_grid.png")
	ImageResource grid();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_polar_grid.png")
	ImageResource polar_grid();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_show_or_hide_the_isometric_grid.png")
	ImageResource isometric_grid();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_standardview.png")
	ImageResource standard_view();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_point_capturing.png")
	ImageResource magnet();
	
	//DELETE STYLEBAR
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_small.png")
	ImageResource stylingbar_delete_small();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_middle.png")
	ImageResource stylingbar_delete_middle();
	
	@Source("icons/png/web/stylingbar/stylingbar_graphicsview_delete_big.png")
	ImageResource stylingbar_delete_big();
	
	//LINES
	
	@Source("icons/png/web/stylingbar/stylingbar_line-dash-dot.png")
	ImageResource line_dash_dot();
	
	@Source("icons/png/web/stylingbar/stylingbar_line-dashed-long.png")
	ImageResource line_dashed_long();

	@Source("icons/png/web/stylingbar/stylingbar_line-dashed-short.png")
	ImageResource line_dashed_short();

	@Source("icons/png/web/stylingbar/stylingbar_line-dotted.png")
	ImageResource line_dotted();

	@Source("icons/png/web/stylingbar/stylingbar_line-solid.png")
	ImageResource line_solid();
	
	//POINTS
	
	@Source("icons/png/web/stylingbar/stylingbar_point-full.png")
	ImageResource point_full();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-empty.png")
	ImageResource point_empty();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-cross.png")
	ImageResource point_cross();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-cross-diag.png")
	ImageResource point_cross_diag();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-diamond-full.png")
	ImageResource point_diamond();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-diamond-empty.png")
	ImageResource point_diamond_empty();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-up.png")
	ImageResource point_up();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-down.png")
	ImageResource point_down();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-left.png")
	ImageResource point_left();
	
	@Source("icons/png/web/stylingbar/stylingbar_point-right.png")
	ImageResource point_right();
	
	//TEXT
	
	@Source("icons/png/web/stylingbar/stylingbar_text_font_size.png")
	ImageResource font_size();
	
	
	
	//ALGEBRA STYLEBAR
	@Source("icons/png/web/stylingbar/stylingbar_algebraview_auxiliary_objects.png")
	ImageResource auxiliary();
	
}
