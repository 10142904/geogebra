package geogebra.common.euclidian;

import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoAttachCopyToView;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import geogebra.common.kernel.geos.Furniture;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;

import java.util.ArrayList;

public class EuclidianStyleBarStatic {

	
	public  final static String[] bracketArray = { "\u00D8", "{ }", "( )", "[ ]", "| |",
	"|| ||" };
	public final static String[] bracketArray2 = { "\u00D8", "{ }", "( )", "[ ]",
	"||", "||||" };

	public static boolean applyFixPosition(ArrayList<GeoElement> geos, boolean flag, EuclidianViewInterfaceCommon ev) {
		
		AbsoluteScreenLocateable geoASL;
		
		AbstractApplication app = geos.get(0).getKernel().getApplication();
		
		// workaround to make sure pin icon disappears
		// see applyFixPosition() called with a geo with label not set below
		app.clearSelectedGeos();

		for (int i = 0; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);

			// problem with ghost geos
			if (!geo.isLabelSet()) {
				AbstractApplication.warn("applyFixPosition() called with a geo with label not set: "+geo.getLabelSimple());
				continue;
				
			}
			
			if (geo.isGeoSegment()) {
				if (geo.getParentAlgorithm() != null && geo.getParentAlgorithm().getInput().length == 3) {
					// segment is output from a Polygon
					//AbstractApplication.warn("segment from poly");
					continue;
				}
			}
			
			if (geo.getParentAlgorithm() instanceof AlgoAttachCopyToView) {
				
				AlgoAttachCopyToView algo = (AlgoAttachCopyToView)geo.getParentAlgorithm();
				
				if (!flag) {
					
					redefineGeo(geo, getDefinitonString(algo.getInput()[0]));

				} else {
					algo.setEV(ev.getEuclidianViewNo()); // 1 or 2
				}
				
				geo.updateRepaint();
				
			} else if (geo instanceof AbsoluteScreenLocateable) {
				geoASL = (AbsoluteScreenLocateable) geo;
				if (flag) {
					// convert real world to screen coords
					int x = ev.toScreenCoordX(geoASL.getRealWorldLocX());
					int y = ev.toScreenCoordY(geoASL.getRealWorldLocY());
					if (!geoASL.isAbsoluteScreenLocActive())
						geoASL.setAbsoluteScreenLoc(x, y);
				} else {
					// convert screen coords to real world
					double x = ev.toRealWorldCoordX(geoASL.getAbsoluteScreenLocX());
					double y = ev.toRealWorldCoordY(geoASL.getAbsoluteScreenLocY());
					if (geoASL.isAbsoluteScreenLocActive())
						geoASL.setRealWorldLoc(x, y);
				}
				geoASL.setAbsoluteScreenLocActive(flag);
				geo.updateRepaint();
				
			} else if (!(geo instanceof Furniture) && !geo.isGeoBoolean()) {
				Kernel kernelA = app.getKernel();
				
				GeoPoint2 corner1 = new GeoPoint2(kernelA.getConstruction());
				GeoPoint2 corner3 = new GeoPoint2(kernelA.getConstruction());
				GeoPoint2 screenCorner1 = new GeoPoint2(kernelA.getConstruction());
				GeoPoint2 screenCorner3 = new GeoPoint2(kernelA.getConstruction());
				if(ev!=null){
					corner1.setCoords(ev.getXmin(), ev.getYmin(), 1);
					corner3.setCoords(ev.getXmax(), ev.getYmax(), 1);
					screenCorner1.setCoords(0, ev.getHeight(), 1);
					screenCorner3.setCoords(ev.getWidth(), 0, 1);
				}
				
						
				// "false" here so that pinning works for eg polygons
				redefineGeo(geo, "AttachCopyToView["+ getDefinitonString(geo) +"," + ev.getEuclidianViewNo() + "]");
				
			} else {
				// can't pin
				AbstractApplication.debug("not pinnable");
				return false;
			}
			
		}
		
		return true;
	}
	
	private static String getDefinitonString(GeoElement geo) {
		// needed for eg freehand functions
		String definitonStr = geo.getCommandDescription(StringTemplate.maxPrecision);
		
		// everything else
		if (definitonStr.equals("")) {
			definitonStr = geo.getFormulaString(StringTemplate.maxPrecision, false);
		}
		
		return definitonStr;

	}

	public static GeoElement redefineGeo(GeoElement geo, String cmdtext) {
		GeoElement newGeo = null;
		
		AbstractApplication app = geo.getKernel().getApplication();

		if (cmdtext == null)
			return newGeo;
		
		AbstractApplication.debug("redefining "+geo+" as "+cmdtext);

		try {
			newGeo = app.getKernel().getAlgebraProcessor()
					.changeGeoElement(geo, cmdtext, true, true);
			app.doAfterRedefine(newGeo);
			newGeo.updateRepaint();
			return newGeo;

		} catch (Exception e) {
			app.showError("ReplaceFailed");
		} catch (MyError err) {
			app.showError(err);
		}
		return newGeo;
	}
	
	public static void applyTableTextFormat(ArrayList<GeoElement> geos, int justifyIndex, boolean HisSelected, boolean VisSelected, int index, AbstractApplication app) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		String[] justifyArray = { "l", "c", "r" };
		//arg = justifyArray[btnTableTextJustify.getSelectedIndex()];
		arg = justifyArray[justifyIndex];
		//if (this.btnTableTextLinesH.isSelected())
		if (HisSelected)
			arg += "_";
		//if (this.btnTableTextLinesV.isSelected())
		if (VisSelected)
			arg += "|";
		if (index > 0)
			arg += bracketArray2[index];
		ArrayList<GeoElement> newGeos = new ArrayList<GeoElement>();

		StringBuilder cmdText = new StringBuilder();

		for (int i = 0; i < geos.size(); i++) {

			// get the TableText algo for this geo and its input
			geo = geos.get(i);
			algo = geo.getParentAlgorithm();
			input = algo.getInput();

			// create a new TableText cmd
			cmdText.setLength(0);
			cmdText.append("TableText[");
			cmdText.append(((GeoList) input[0]).getFormulaString(
					StringTemplate.defaultTemplate, false));
			cmdText.append(",\"");
			cmdText.append(arg);
			cmdText.append("\"]");

			// use the new cmd to redefine the geo and save it to a list.
			// (the list is needed to reselect the geo)
			newGeos.add(redefineGeo(geo, cmdText.toString()));
		}

		// reset the selection
		app.setSelectedGeos(newGeos);
	}
	
	public static boolean applyCaptionStyle(ArrayList<GeoElement> geos, int mode, int index) {
		
		boolean needUndo = false;
		
		AbstractApplication app = geos.get(0).getKernel().getApplication();
		
		for (int i = 0; i < geos.size(); i++) {
			GeoElement geo = geos.get(i);
			if ((mode == EuclidianConstants.MODE_MOVE && (geo.isLabelShowable()
					|| geo.isGeoAngle() || (geo.isGeoNumeric() ? ((GeoNumeric) geo)
					.isSliderFixed() : false)))
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY
							&& geo.isLabelShowable() && geo.isGeoPoint())
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON
							&& geo.isLabelShowable() || geo.isGeoAngle() || (geo
								.isGeoNumeric() ? ((GeoNumeric) geo)
							.isSliderFixed() : false))
					|| (app.getLabelingStyle() == ConstructionDefaults.LABEL_VISIBLE_AUTOMATIC
							&& geo.isLabelShowable() || geo.isGeoAngle() || (geo
								.isGeoNumeric() ? ((GeoNumeric) geo)
							.isSliderFixed() : false))) {
				if (index == 0) {
					if (mode == EuclidianConstants.MODE_MOVE
							|| app.getLabelingStyle() != ConstructionDefaults.LABEL_VISIBLE_ALWAYS_ON) {
						geo.setLabelVisible(false);
					}
				} else {
					geo.setLabelVisible(true);
					geo.setLabelMode(index - 1);
				}
			}
			geo.updateRepaint();
			needUndo = true;
		}
		
		return needUndo;
	}

	
}
