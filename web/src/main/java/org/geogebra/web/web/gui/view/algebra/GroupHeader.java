package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.main.SelectionManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;

public class GroupHeader extends FlowPanel {
	
	protected GroupNameLabel il;
	Image img;
	protected OpenButton open;
	private String label;
	public GroupHeader(SelectionManager selection, TreeItem parent,
			String strlab, String key, SafeUri showUrl, SafeUri hiddenUrl) {
		
		this.setStyleName("elemHeading");
		this.label = key;
		
		add(open = new OpenButton(showUrl, hiddenUrl, parent));
		add(il = new GroupNameLabel(selection, parent, strlab));
	}
	
	public class OpenButton extends SimplePanel
	{
		private SafeUri showUrl, hiddenUrl;
		
		public OpenButton(SafeUri showUrl,SafeUri hiddenUrl,final TreeItem ti){
			this.showUrl = showUrl;
			this.hiddenUrl = hiddenUrl;
			
			addDomHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					boolean open = ti.getState();
					ti.setState(!open);
					setChecked(!open);
				}
			}, ClickEvent.getType());
			setChecked(true);
		}

		/**
		 * set background-images via HTML
		 */
		public void setImage(String text)
		{
			//String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
			if(img == null){
				img = new Image(text);
				this.add(img);
			}else{
				img.setUrl(text);
			}
		}

		public void setChecked(boolean value)
		{
			if (value)
			{
				setImage(showUrl.asString());
				this.setStyleName("arrowBottom");
			}
			else
			{
				setImage(hiddenUrl.asString());
				this.setStyleName("arrowLeft");
			}
			this.getElement().addClassName("algebraOpenButton");
		}

		public void setEnabled(boolean euclidianShowable) {
		    // TODO Auto-generated method stub
		    
	    }

	}

	public void setText(String string) {
	    il.setText(string);
	    
    }

	public void setChecked(boolean value) {
		open.setChecked(value);
	}

	public String getLabel() {
		return label;
	}
}
