package geogebra.touch.gui.elements.ggt;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.View;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends HorizontalPanel
{
	public static final int PANEL_HEIGHT = 100;

	private SimplePanel image, likeIcon;
	private VerticalPanel infos, links;
	private HorizontalPanel sharedPanel, likesPanel;
	private Label title, date, sharedBy, author, likes;
	private Button open;

	private VerticalMaterialPanel vmp;

	private Button delete;

	private Material material;

	public MaterialListElement(final Material m, final AppWeb app, final FileManagerM fm,
			VerticalMaterialPanel vmp)
	{
		// TODO set infos alignment
		this.image = new SimplePanel();
		this.infos = new VerticalPanel();
		this.infos.setSpacing(5);
		this.sharedPanel = new HorizontalPanel();
		this.likesPanel = new HorizontalPanel();
		this.links = new VerticalPanel();
		this.vmp = vmp;
		
		this.material = m;

		// TODO Change to icon
		this.open = new Button("OPEN");
		this.delete = new Button("DELETE");

		this.setHeight(PANEL_HEIGHT + "px");
		this.setWidth((Window.getClientWidth() - 100)/2 + "px");
		this.markUnSelected();
		/*this.getElement().getStyle().setBackgroundColor(GeoGebraTubeStyle.InfoBackground);
		this.getElement().getStyle().setBorderColor(GeoGebraTubeStyle.BorderColor);
		this.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		this.getElement().getStyle().setBorderWidth(GeoGebraTubeStyle.BorderWidth, Unit.PX);
		this.getElement().getStyle().setMarginTop(5, Unit.PX);
		this.getElement().getStyle().setMarginBottom(5, Unit.PX);*/

		this.add(this.image);
		if(m.getId()>0){
			this.image.getElement().getStyle().setBackgroundImage("url(http:" + m.getThumbnail() + ")");
		}else{
			this.image.getElement().getStyle().setBackgroundImage("url(" + fm.getThumbnailDataUrl(m.getURL()) + ")");
		}
		this.image.getElement().getStyle().setMarginRight(5, Unit.PX);
		this.image.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		this.image.setSize("100px", "100px");

		this.title = new Label(m.getTitle());
		this.infos.add(this.title);
		this.title.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.title.getElement().getStyle().setColor(GeoGebraTubeStyle.TitleColor);

		this.date = new Label(m.getDate());
		this.infos.add(this.date);
		this.date.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.sharedBy = new Label(GeoGebraTubeStyle.SharedBy);
		this.sharedPanel.add(this.sharedBy);
		this.sharedBy.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.sharedBy.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);

		this.author = new Label(m.getAuthor());
		this.sharedPanel.add(this.author);
		this.author.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);
		this.infos.add(this.sharedPanel);

		this.likeIcon = new SimplePanel();
		this.likeIcon.getElement().getStyle().setBackgroundImage("url(http://www.geogebratube.org/images/like-neutral-small.png)");
		this.likesPanel.add(this.likeIcon);
		this.likesPanel.setCellVerticalAlignment(this.likeIcon, HasVerticalAlignment.ALIGN_MIDDLE);
		this.likesPanel.setSpacing(5);
		this.likeIcon.setSize("12px", "11px");

		this.likes = new Label(String.valueOf(m.getLikes()));
		this.likesPanel.add(this.likes);
		this.likes.getElement().getStyle().setColor(GeoGebraTubeStyle.TextColor);
		this.infos.add(this.likesPanel);
		this.add(this.infos);

		this.links.add(this.open);
		this.links.add(this.delete);
		
		this.links.getElement().setAttribute("align", "right");
		this.open.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				if(m.getId()>0){
					//remote material
					new View(RootPanel.getBodyElement(),app).processFileName("http://www.geogebratube.org/files/material-"+m.getId()+".ggb");
				}else{
					fm.getFile(m.getURL(),app);
				}
				TouchEntryPoint.showTabletGUI();
			}
		}, ClickEvent.getType());
		this.delete.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				if(m.getId()>0){
					//remote material should not have this visible					
				}else{
					fm.delete(m.getURL());
				}
			}
		}, ClickEvent.getType());
		this.add(this.links);

		this.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				MaterialListElement.this.markSelected();
			}
		}, ClickEvent.getType());
	}

	protected void markSelected() {
		this.vmp.unselectMaterials();
		setStyleName("browserSelectedFile");
		this.open.setVisible(true);
		if(this.material.getId() == 0){
			this.delete.setVisible(true);
		}
		this.vmp.rememberSelected(this);
	}
	
	protected void markUnSelected() {
		setStyleName("browserDefaultFile");
		this.open.setVisible(false);
		this.delete.setVisible(false);
	}
}
