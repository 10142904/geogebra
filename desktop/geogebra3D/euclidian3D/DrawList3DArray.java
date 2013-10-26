package geogebra3D.euclidian3D;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.draw.DrawListArray;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Class for storing 3D drawables includes in a DrawList
 * 
 * @author matthieu
 *
 */
public class DrawList3DArray extends DrawListArray {

	private static final long serialVersionUID = 1L;

	/**
	 * common constructor
	 * @param view
	 */
	public DrawList3DArray(EuclidianViewInterfaceCommon view) {
		super(view);
	}
	

    @Override
	protected void update(DrawableND d){
		d.setWaitForUpdate();
		if (d.createdByDrawList())
			d.setCreatedByDrawListVisible(true);
    }
	
	
    @Override
	protected DrawableND createDrawableND(GeoElement listElement){
    	//Application.debug(listElement.toString());
    	DrawableND d = super.createDrawableND(listElement);
    	((EuclidianView3D) view).addToDrawable3DLists((Drawable3D) d);
    	return d;  
    }
    
	@Override
	protected DrawableND getDrawable(DrawableND oldDrawable, GeoElement listElement, DrawableND drawList) {
		((EuclidianView3D) view).remove((Drawable3D) oldDrawable);
		return super.getDrawable(oldDrawable, listElement, drawList);
	}

}
