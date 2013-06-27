package geogebra.html5.gui.view.algebra;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Algebra view marble to show or hide geos
 *
 */
public class Marble extends SimplePanel
{
	private SafeUri showUrl, hiddenUrl;
	private GeoContainer gc;
	/** whether the last switch was done using touch (ignore onclick in that case) */
	boolean touchUsed;
	
	/**
	 * Object providing the corresponding geo
	 */
	public interface GeoContainer {
		/**
		 * @return corresponding geo
		 */
		public GeoElement getGeo();
		
	}
	
	/**
	 * Toggle visibility of corresponding geo
	 */
	void toggleVisibility(){
		gc.getGeo().setEuclidianVisible(!gc.getGeo().isSetEuclidianVisible());
		gc.getGeo().updateVisualStyle();
		gc.getGeo().getKernel().getApplication().storeUndoInfo();
		gc.getGeo().getKernel().notifyRepaint();
		setChecked(gc.getGeo().isEuclidianVisible());
	}
	
	/**
	 * @param showUrl url of image for shown geos
	 * @param hiddenUrl url of image for hidden geos
	 * @param gc object providing the GeoElement
	 */
	public Marble(SafeUri showUrl,SafeUri hiddenUrl,final GeoContainer gc){
		this.showUrl = showUrl;
		this.hiddenUrl = hiddenUrl;
		this.gc = gc;
		addDomHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{	
				event.preventDefault();
				event.stopPropagation();
				App.debug("click");
				if(touchUsed){
					touchUsed = false;
				}else{
					toggleVisibility();
				}
			}
		}, ClickEvent.getType());
		//MouseDown triggers scrolling if the element is too long for AV
		addDomHandler(new MouseDownHandler()
		{
			public void onMouseDown(MouseDownEvent event)
			{	
				event.preventDefault();
				event.stopPropagation();
				App.debug("mouse down");
			}
			
		}, MouseDownEvent.getType());
		//let's also prevent default TouchStart; note that calling preventDefault may cause missing Click event
		addDomHandler(new TouchStartHandler()
		{
			public void onTouchStart(TouchStartEvent event)
			{	
				event.preventDefault();
				event.stopPropagation();
				toggleVisibility();
				touchUsed = true;
				App.debug("touch start");
			}
		}, TouchStartEvent.getType());

	}

	/**
	 * set background-images via HTML
	 * @param text URL of image as string
	 */
	public void setImage(String text)
	{
		//String html = "<img src=\"" + text + "\" style=\"height: 19px;margin-right: 5px;\">";
		String html = "<img src=\"" + text + "\" style=\"margin-right: 5px;\">";
		this.getElement().setInnerHTML(html);
	}

	/**
	 * @param value true tfor visible, false for invisible geo
	 */
	public void setChecked(boolean value)
	{
		if (value)
		{
			setImage(showUrl.asString());
		}
		else
		{
			setImage(hiddenUrl.asString());
		}
	}
	
	/**
	 * Enable or disable this control, NOT IMPLEMENTED
	 * @param euclidianShowable whether the geo may be shown/hidden
	 */
	public void setEnabled(boolean euclidianShowable) {
	    // TODO Auto-generated method stub
	    
    }

}
