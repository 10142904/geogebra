package geogebra.gui;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.geos.FromMeta;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.main.AppD;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * Popup Menu for choosing a geo
 * 
 * @author mathieu
 *
 */
public class ContextMenuChooseGeoD extends ContextMenuGeoElementD {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


	
	/**
	 * 
	 */
	protected EuclidianView view;
	
	/**
	 * polygons/polyhedra parents of segments, polygons, ...
	 */
	private TreeSet<GeoElement> metas;
	
	private ArrayList<GeoElement> selectedGeos;
	private geogebra.common.awt.GPoint loc;
	
	private JMenu selectAnotherMenu;

	/**
	 * 
	 * @param app application
	 * @param view view
	 * @param selectedGeos selected geos
	 * @param geos geos
	 * @param location place to show
	 */
	public ContextMenuChooseGeoD(AppD app, EuclidianView view, 
			ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, Point location, geogebra.common.awt.GPoint invokerLocation) {

		super(app, selectedGeos, location);
		
		this.view = view;
		this.selectedGeos = selectedGeos;

		//return if just one geo, or if first geos more than one
		if (geos.size()<2 || selectedGeos.size()>1) {
			justOneGeo = false;
			return;
		}

		justOneGeo = true;

		//section to choose a geo
		//addSeparator();
		addSelectAnotherMenu();
		
		
		this.loc = invokerLocation;
		this.geos = geos;
		
		
		GeoElement geoSelected = selectedGeos.get(0);
		
		
		//add geos
		metas = new TreeSet<GeoElement>();
		
		for (GeoElement geo : geos){
			if (geo!=geoSelected){//don't add selected geo
				addGeo(geo);
			}

			if (geo.getMetasLength() > 0){
				for (GeoElement meta : ((FromMeta) geo).getMetas()){
					if (!metas.contains(meta)){
						addGeo(meta);
					}
				}
			}
		}
		
		//TODO: clear selection is not working from here
		this.getWrappedPopup().getSelectionModel().clearSelection();
		
	}
	
	
	
	private void addSelectAnotherMenu(){
		selectAnotherMenu = new JMenu(app.getMenu("SelectAnother") );
		selectAnotherMenu.setIcon(((AppD) app).getEmptyIcon());
		selectAnotherMenu.setBackground(getWrappedPopup().getBackground());
		selectAnotherMenu.setFont(((AppD) app).getItalicFont());
		
		// add the selection menu just under the title
		getWrappedPopup().add(selectAnotherMenu,1);     
		
	}
	
	
	/**
	 * 
	 */
	private void addGeo(GeoElement geo) {
		
		GeoAction chooser = new GeoAction(geo);
		JMenuItem mi = selectAnotherMenu.add(chooser);    
		mi.setBackground(bgColor);  
		mi.setText(getDescription(geo, true));			
		mi.addMouseListener(new MyMouseAdapter(geo));
	
		//prevent to add meta twice
		metas.add(geo);
			
		            
	}
	
	/**
	 * Action when select a geo
	 * @author mathieu
	 *
	 */
	private class GeoAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private GeoElement geo;
		
		
		/**
		 * Create chooser for this geo
		 * @param geo geo to choose
		 */
		public GeoAction(GeoElement geo){
			super();
			this.geo = geo;
				
		}
		
		public void actionPerformed(ActionEvent e) {
			geoActionCmd(this.geo,selectedGeos,geos,view, loc);
		}
		
	}
	
	
	private class MyMouseAdapter extends MouseAdapter{
		
		private GeoElement geo;
		
		public MyMouseAdapter(GeoElement geo){
			this.geo=geo;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			//AbstractApplication.debug(geo.getLabelSimple());
			/*
			geo.setHighlighted(true);
			app.getKernel().notifyRepaint();
			*/
			view.getEuclidianController().doSingleHighlighting(geo);
		}
		
		/*
		@Override
		public void mouseExited(MouseEvent e) {
			AbstractApplication.debug(geo.getLabelSimple());
			geo.setHighlighted(false);
			app.getKernel().notifyRepaint();
			
		}
		*/
	}
	
	

	@Override
	protected void setTitle(String str) {
		
		AbstractAction titleAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				if (selectedGeos.size() < 2){
					if (view.getMode() == EuclidianConstants.MODE_MOVE){ // change selection to geo clicked

						app.getSelectionManager().clearSelectedGeos(false); //repaint done next step
						app.getSelectionManager().addSelectedGeo(geo);


					}else{ // use geo clicked to process mode
						Hits hits = new Hits();
						hits.add(geo);
						view.getEuclidianController().processMode(hits, false);
					}
				}
				
			}
		};
		
		JMenuItem title = wrappedPopup.add(titleAction);	
		title.setText(str);
		title.setFont(((AppD) app).getBoldFont());                      
		title.setBackground(bgColor);
		title.setForeground(fgColor);
		
		title.setIcon(((AppD) app).getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 15)); 
		
		
		
	}
	
}
