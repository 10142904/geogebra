package geogebra.web;


import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCirclePointRadius;
import geogebra.common.kernel.algos.AlgoDependentPoint;
import geogebra.common.kernel.algos.AlgoDistancePoints;
import geogebra.common.kernel.algos.AlgoIntersectLineConic;
import geogebra.common.kernel.algos.AlgoIntersectSingle;
import geogebra.common.kernel.algos.AlgoJoinPointsSegment;
import geogebra.common.kernel.algos.AlgoMidpoint;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.algos.AlgoPolygonRegular;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.MyVecNode;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.Operation;
import geogebra.common.util.debug.GeoGebraProfiler;
import geogebra.common.util.debug.Log;
import geogebra.html5.css.GuiResourcesSimple;
import geogebra.html5.gui.GeoGebraFrameSimple;
import geogebra.html5.js.JavaScriptInjector;
import geogebra.html5.main.AppWsimple;
import geogebra.html5.util.ArticleElement;
import geogebra.html5.util.Dom;
import geogebra.html5.util.debug.GeoGebraLogger;
import geogebra.web.util.debug.GeoGebraProfilerW;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PerformanceTest implements EntryPoint {

	private static ArrayList<ArticleElement> getGeoGebraMobileTags() {
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		ArrayList<ArticleElement> articleNodes = new ArrayList<ArticleElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Date creationDate = new Date();
			nodes.getItem(i).setId(GeoGebraConstants.GGM_CLASS_NAME+i+creationDate.getTime());
			articleNodes.add(ArticleElement.as(nodes.getItem(i)));
		}
		return articleNodes;
	}

	/**
	 * set true if Google Api Js loaded
	 */

	public void onModuleLoad() {
		GeoGebraProfiler.init(new GeoGebraProfilerW());
		GeoGebraProfiler.getInstance().profile();
		NodeList<Element> nodes = Dom.getElementsByClassName(GeoGebraConstants.GGM_CLASS_NAME);
		Log.logger = new GeoGebraLogger();
		ArticleElement ae = ArticleElement.as(nodes.getItem(0));
		GeoGebraFrameSimple gfs = new GeoGebraFrameSimple();
		ae.setId("ggbPerfTest");
		gfs.setComputedWidth(800);
		gfs.setComputedHeight(600);
		RootPanel.get(ae.getId()).add(gfs);
		JavaScriptInjector.inject(GuiResourcesSimple.INSTANCE.propertiesKeysJS());
		AppWsimple app = new AppWsimple(ae, gfs, false);
		gfs.setApplication(app);
		gfs.setWidth(800);
		gfs.setHeight(600);
		Kernel kernel = app.getKernel();
		app.setLabelingStyle(2);
		Construction cons = kernel.getConstruction();
		
		/** Construction start */
		GeoPoint A = new GeoPoint(cons,0,0,1);
		A.setLabel("A");
		
		ExpressionNode exB = new MyVecNode(kernel,A.wrap().apply(Operation.XCOORD).wrap().plus(5),A.wrap().apply(Operation.YCOORD)).wrap();
		GeoPoint B = new AlgoDependentPoint(cons, "B", exB, false).getPoint();
		
		ExpressionNode exC = new MyVecNode(kernel,B.wrap().apply(Operation.XCOORD).wrap(),B.wrap().apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint C = new AlgoDependentPoint(cons, "C", exC, false).getPoint();
		
		ExpressionNode exD = new MyVecNode(kernel,A.wrap().apply(Operation.XCOORD).wrap(),A.wrap().apply(Operation.YCOORD).wrap().plus(3)).wrap();
		GeoPoint D = new AlgoDependentPoint(cons,"D", exD, false).getPoint();
		
		GeoPoint E = new AlgoMidpoint(cons,"E",B,C).getPoint();
		
		GeoSegment a = new AlgoJoinPointsSegment(cons,"a", E,C).getSegment();

		GeoPointND F = new AlgoPointOnPath(cons,"F",a,5,2).getP();
		
		AlgoPolygonRegular regPoly1 = new AlgoPolygonRegular(cons,new String[]{"poly1","f","c","g","h","G","H"},F,C,new GeoNumeric(cons,4));
		GeoPoint G = regPoly1.getPoly().getPoint(2);
		GeoPoint H = regPoly1.getPoly().getPoint(3);
		
		GeoSegment b = new AlgoJoinPointsSegment(cons,"b",D,A).getSegment();
		
		GeoSegment d = new AlgoJoinPointsSegment(cons,"d",A,B).getSegment();
		
		GeoSegment e = new AlgoJoinPointsSegment(cons,"e",B,C).getSegment();
		
		GeoSegment i = new AlgoJoinPointsSegment(cons,"i",C,D).getSegment();
		
		GeoConic k = new AlgoCirclePointRadius(cons,"k",D,new AlgoDistancePoints(cons,C,F).getDistance()).getCircle();
		
		GeoConic p = new AlgoCirclePointRadius(cons,"p",A,new AlgoDistancePoints(cons,C,F).getDistance()).getCircle();
		
		GeoConic q = new AlgoCirclePointRadius(cons,"q",B,new AlgoDistancePoints(cons,C,F).getDistance()).getCircle();
		
		GeoPoint L = new AlgoIntersectSingle("L",new AlgoIntersectLineConic(cons,i,k),0).getPoint();
		
		GeoPoint M = new AlgoIntersectSingle("M",new AlgoIntersectLineConic(cons,d,p),0).getPoint();
		
		GeoPoint N = new AlgoIntersectSingle("L",new AlgoIntersectLineConic(cons,e,q),0).getPoint();
		
		AlgoPolygonRegular regPoly2 = new AlgoPolygonRegular(cons,new String[]{"poly2","j","l","o","n","O","P"},L,D,new GeoNumeric(cons,4));
		GeoPoint O = regPoly2.getPoly().getPoint(2);
		GeoPoint P = regPoly2.getPoly().getPoint(3);
		
		AlgoPolygonRegular regPoly3 = new AlgoPolygonRegular(cons,new String[]{"poly3","m","r","s","t","Q","R"},A,M,new GeoNumeric(cons,4));
		GeoPoint Q = regPoly3.getPoly().getPoint(2);
		GeoPoint R = regPoly3.getPoly().getPoint(3);
		
		AlgoPolygonRegular regPoly4 = new AlgoPolygonRegular(cons,new String[]{"poly4","a1","b1","c1","d1","S","T"},B,N,new GeoNumeric(cons,4));
		GeoPoint S = regPoly4.getPoly().getPoint(2);
		GeoPoint T = regPoly4.getPoly().getPoint(3);
		
		AlgoPolygon ptPoly5 = new AlgoPolygon(cons, null, new GeoPointND[]{Q,M,T,S});
		
		AlgoPolygon ptPoly6 = new AlgoPolygon(cons, null, new GeoPointND[]{S,N,F,H});
		
		AlgoPolygon ptPoly7 = new AlgoPolygon(cons, null, new GeoPointND[]{H,G,L,P});
		
		AlgoPolygon ptPoly8 = new AlgoPolygon(cons, null, new GeoPointND[]{O,R,Q,P});
		
		AlgoPolygon ptPoly9 = new AlgoPolygon(cons, null, new GeoPointND[]{Q,S,H,P});
		
		GeoSegment e1 = new AlgoJoinPointsSegment(cons,"e1",Q,M).getSegment();
		
		GeoSegment f1 = new AlgoJoinPointsSegment(cons,"f1",M,T).getSegment();
		
		GeoSegment g1 = new AlgoJoinPointsSegment(cons,"g1",T,S).getSegment();
		
		GeoSegment h1 = new AlgoJoinPointsSegment(cons,"h1",A,M).getSegment();
		
		GeoSegment i1 = new AlgoJoinPointsSegment(cons,"i1",A,R).getSegment();
		
		GeoSegment j1 = new AlgoJoinPointsSegment(cons,"j1",T,B).getSegment();
		
		GeoSegment k1 = new AlgoJoinPointsSegment(cons,"k1",B,N).getSegment();
		
		GeoSegment l1 = new AlgoJoinPointsSegment(cons,"l1",(GeoPoint)F, C).getSegment();
		
		GeoSegment m1 = new AlgoJoinPointsSegment(cons,"m1",C,G).getSegment();
		
		GeoSegment n1 = new AlgoJoinPointsSegment(cons,"n1",L,D).getSegment();
		
		GeoSegment p1 = new AlgoJoinPointsSegment(cons,"p1",D,O).getSegment();
		
		
		/** Construction end*/
		app.getEuclidianView1().getGraphicsForPen().setCoordinateSpaceSize(800, 600);
		app.afterLoadFileAppOrNot();
		GeoGebraProfiler.getInstance().profileEnd();
		//use GeoGebraProfilerW if you want to profile, SilentProfiler  for production
		//GeoGebraProfiler.init(new GeoGebraProfilerW());
	}

	public static void loadAppletAsync() {
	    GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				startGeoGebra(getGeoGebraMobileTags());
			}
			
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub
				
			}
		});
    }

	static void startGeoGebra(ArrayList<ArticleElement> geoGebraMobileTags) {
	 	
		geogebra.html5.gui.GeoGebraFrameSimple.main(geoGebraMobileTags);
	    
    }
	
	private native void exportGGBElementRenderer() /*-{
 		$wnd.renderGGBElement = $entry(@geogebra.html5.gui.GeoGebraFrameSimple::renderArticleElement(Lcom/google/gwt/dom/client/Element;Lcom/google/gwt/core/client/JavaScriptObject;))
	}-*/;

}
