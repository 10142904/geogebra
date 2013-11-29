package geogebra3D.kernel3D;

import geogebra.common.awt.GColor;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionElementCycle;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.Traceable;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasHeight;
import geogebra.common.kernel.kernelND.HasSegments;
import geogebra.common.kernel.kernelND.HasVolume;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author ggb3D
 * 
 *         Class describing a GeoPolyhedron
 * 
 */
public class GeoPolyhedron extends GeoElement3D 
implements HasSegments, HasVolume, Traceable, 
RotateableND, Translateable, MirrorableAtPlane, Transformable, Dilateable,
HasHeight
{// implements Path {

	public static final int TYPE_NONE = 0;
	public static final int TYPE_PYRAMID = 1;
	//public static final int TYPE_PSEUDO_PRISM = 2;
	public static final int TYPE_PRISM = 3;

	int type;

	/** vertices */
	// protected ArrayList<GeoPoint3D> points;

	/** edges index */
	protected TreeMap<ConstructionElementCycle, Long> segmentsIndex;

	/** max faces edges */
	protected long segmentsIndexMax = 0;

	/** edges */
	protected TreeMap<Long, GeoSegment3D> segments;

	/** edges linked (e.g basis of the prism -- WARNING: not always updated) */
	private TreeMap<ConstructionElementCycle, GeoSegmentND> segmentsLinked;

	/** faces index */
	protected TreeMap<ConstructionElementCycle, Integer> polygonsIndex;
	/** faces descriptions */
	protected ArrayList<ConstructionElementCycle> polygonsDescriptions;

	/** max faces index */
	protected int polygonsIndexMax = 0;

	/** faces */
	protected TreeMap<Integer, GeoPolygon3D> polygons;

	/** faces linked */
	protected TreeSet<GeoPolygon> polygonsLinked;
	
	/** points created by the algo */
	protected ArrayList<GeoPoint3D> pointsCreated;
	

	/** face currently constructed */
	private ConstructionElementCycle currentFace;

	/**
	 * constructor
	 * 
	 * @param c
	 *            construction
	 */
	public GeoPolyhedron(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		polygonsIndex = new TreeMap<ConstructionElementCycle, Integer>();
		polygonsDescriptions = new ArrayList<ConstructionElementCycle>();
		polygons = new TreeMap<Integer, GeoPolygon3D>();

		segmentsIndex = new TreeMap<ConstructionElementCycle, Long>();
		segments = new TreeMap<Long, GeoSegment3D>();


		segmentsLinked = new TreeMap<ConstructionElementCycle, GeoSegmentND>();
		polygonsLinked = new TreeSet<GeoPolygon>();

		pointsCreated = new ArrayList<GeoPoint3D>();
	}
	
	
	/**
	 * Update segments linked set with the polygon's segment
	 * @param polygon source polygon
	 */
	private void addSegmentsLinked(GeoPolygon polygon){
		if (polygon.getSegments() != null){
			for (GeoSegmentND segment: polygon.getSegments()){
				addSegmentLinked(segment);
			}
		}
	}
	
	/**
	 * update set of segments linked to this
	 */
	public void updateSegmentsLinked(){
		segmentsLinked.clear();
		for (GeoPolygon p : getPolygonsLinked()){
			addSegmentsLinked(p);
		}
	}

	/**
	 * 
	 * @return segments linked to the polyhedron (eg segments of the bottom)
	 */
	public Collection<GeoSegmentND> getSegmentsLinked() {
		return segmentsLinked.values();
	}

	/**
	 * 
	 * @return polygons linked to the polyhedron (eg the bottom)
	 */
	public Collection<GeoPolygon> getPolygonsLinked() {
		return polygonsLinked;
	}

	/**
	 * 
	 * @param polyhedron
	 */
	public GeoPolyhedron(GeoPolyhedron polyhedron) {
		this(polyhedron.getConstruction());
		set(polyhedron);
	}

	/**
	 * set the type of polyhedron
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @return the type of polyhedron
	 */
	public int getType() {
		return type;
	}

	/**
	 * start a new face
	 */
	public void startNewFace() {
		currentFace = new ConstructionElementCycle();
	}

	/**
	 * add the point to the current face and to the point list if it's a new one
	 * 
	 * @param point
	 */
	public void addPointToCurrentFace(GeoPointND point) {

		currentFace.add((GeoElement) point);
	}

	/**
	 * ends the current face and store it in the faces list
	 */
	public void endCurrentFace() {
		currentFace.setDirection();
		
		//Application.debug(polygonsIndexMax);

		//add to index
		polygonsIndex.put(currentFace, new Integer(polygonsIndexMax));
		polygonsDescriptions.add(currentFace);
		polygonsIndexMax++;
		
	}
	

	/**
	 * update the faces regarding vertices and faces description
	 * @deprecated since version 4.9.10.0
	 */
	@Deprecated
	public void updateFacesDeprecated() {
		
		App.debug("old file version");

		// create missing faces
		for (ConstructionElementCycle currentFace : polygonsIndex.keySet()) {

			// if a polygons already corresponds to the face description, then
			// pass it
			if (polygons.containsKey(polygonsIndex.get(currentFace)))
				continue;

			// vertices of the face
			GeoPointND[] p = new GeoPointND[currentFace.size()];

			// edges linked to the face
			GeoSegmentND[] s = new GeoSegmentND[currentFace.size()];

			Iterator<ConstructionElement> it2 = currentFace.iterator();
			GeoPointND endPoint = (GeoPointND) it2.next();
			int j = 0;
			p[j] = endPoint; // first point for the polygon
			GeoPointND firstPoint = endPoint;
			for (; it2.hasNext();) {
				// creates edges
				GeoPointND startPoint = endPoint;
				endPoint = (GeoPointND) it2.next();
				s[j] = createSegment(startPoint, endPoint);

				// points for the polygon
				j++;
				p[j] = endPoint;

			}
			// last segment
			s[j] = createSegment(endPoint, firstPoint);

			/*
			String st = "poly : ";
			for (int i = 0; i < p.length; i++)
				st += p[i].getLabel();
			Application.debug(st);
			*/

			GeoPolygon3D polygon = createPolygon(p);
			polygons.put(polygonsIndex.get(currentFace), polygon);
			polygon.setSegments(s);
		}
	}
	
	

	/**
	 * creates a polygon corresponding to the index
	 * @param index index of the polygon
	 * @return polygon corresponding
	 */
	public GeoPolygon3D createPolygon(int index) {
		
		currentFace = polygonsDescriptions.get(index);

		// vertices of the face
		GeoPointND[] p = new GeoPointND[currentFace.size()];

		// edges linked to the face
		GeoSegmentND[] s = new GeoSegmentND[currentFace.size()];
		
		GeoPointND endPoint = (GeoPointND) currentFace.get(0);
		p[0] = endPoint; // first point for the polygon
		GeoPointND firstPoint = endPoint;
		int j;
		for (j=1; j<currentFace.size(); j++) {
			// creates edges
			GeoPointND startPoint = endPoint;
			endPoint = (GeoPointND) currentFace.get(j);
			s[j-1] = createSegment(startPoint, endPoint);

			// points for the polygon
			p[j] = endPoint;

		}
		// last segment
		s[j-1] = createSegment(endPoint, firstPoint);


		GeoPolygon3D polygon = createPolygon(p);
		polygons.put(index, polygon);
		polygon.setSegments(s);
		
		return polygon;
	}
	
	/**
	 * update the faces
	 */
	public void createFaces() {
		for (int index = 0; index<polygonsDescriptions.size(); index++) {
			createPolygon(index);
		}
	}

	/**
	 * create a polygon joining the given points
	 * 
	 * @param points
	 *            vertices of the polygon
	 * @return the polygon
	 */
	public GeoPolygon3D createPolygon(GeoPointND[] points) {
		GeoPolygon3D polygon;

		AlgoPolygon3D algo = new AlgoPolygon3D(cons, points, false, this);
		cons.removeFromConstructionList(algo);

		polygon = (GeoPolygon3D) algo.getPoly();
		// refresh color to ensure segments have same color as polygon:
		polygon.setObjColor(getObjectColor());
		
		// force init labels called to avoid polygon to draw edges
		polygon.setInitLabelsCalled(true);
		
		return polygon;
	}
	
	

	/**
	 * add the polygon as a polygon linked to this (e.g basis of a prism)
	 * 
	 * @param polygon
	 */
	public void addPolygonLinked(GeoPolygon polygon) {
		polygonsLinked.add(polygon);
		addSegmentsLinked(polygon);
		polygon.addMeta(this);
		
		
	}
	

	/**
	 * add the point as created point (by algo)
	 * 
	 * @param point
	 */
	public void addPointCreated(GeoPoint3D point) {
		pointsCreated.add(point);
	}

	/**
	 * return a segment joining startPoint and endPoint if this segment already
	 * exists in segments, return the already stored one
	 * 
	 * @param startPoint
	 *            the start point
	 * @param endPoint
	 *            the end point
	 * @return the segment
	 */

	public GeoSegmentND createSegment(GeoPointND startPoint, GeoPointND endPoint) {

		//Application.debug(startPoint.getLabel() + endPoint.getLabel());

		ConstructionElementCycle key = ConstructionElementCycle
				.SegmentDescription((GeoElement) startPoint,
						(GeoElement) endPoint);

		// check if this segment is not already created
		if (segmentsIndex.containsKey(key)){
			//Application.debug(startPoint.getLabel() + endPoint.getLabel());
			return segments.get(segmentsIndex.get(key));
		}

		// check if this segment is not a segment linked
		if (segmentsLinked.containsKey(key))
			return segmentsLinked.get(key);

		GeoSegment3D segment;
		

		AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, startPoint,
				endPoint, this, GeoClass.SEGMENT3D);
		cons.removeFromConstructionList(algoSegment);

		segment = (GeoSegment3D) algoSegment.getCS();
		// refresh color to ensure segments have same color as polygon:
		segment.setObjColor(getObjectColor());

		Long index = new Long(segmentsIndexMax);
		segmentsIndex.put(key, index);
		segments.put(index, segment);
		segmentsIndexMax++;

		return segment;

	}
	
	public GeoSegmentND getSegment(GeoPointND startPoint, GeoPointND endPoint) {

		//Application.debug(startPoint.getLabel() + endPoint.getLabel());

		ConstructionElementCycle key = ConstructionElementCycle
				.SegmentDescription((GeoElement) startPoint,
						(GeoElement) endPoint);
		
		// check if this segment is already created
		if (segmentsIndex.containsKey(key))
			return segments.get(segmentsIndex.get(key));
		

		// check if this segment is a segment linked
		if (segmentsLinked.containsKey(key))
			return segmentsLinked.get(key);
		
		return null;
	}

	public void addSegmentLinked(GeoSegmentND segment) {
		ConstructionElementCycle key = ConstructionElementCycle
				.SegmentDescription(segment.getStartPointAsGeoElement(),
						segment.getEndPointAsGeoElement());

		segmentsLinked.put(key, segment);
	}

	public void defaultLabels(String[] labels) {

		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		if (labels == null || labels.length == 0)
			labels = new String[1];

		setLabel(labels[0]);

		defaultPolygonsLabels();
		defaultSegmentLabels();

	}
	
	private boolean allLabelsAreSet = false;

	/**
	 * Returns whether the method initLabels() was called for this polygon. This
	 * is important to know whether the segments have gotten labels.
	 * 
	 * @return true iff all labels (of created polygons, segments, points) are set.
	 */
	final public boolean allLabelsAreSet() {
		return allLabelsAreSet;
	}
	
	/**
	 * set init labels called 
	 * @param flag
	 */
	public void setAllLabelsAreSet(boolean flag){
		allLabelsAreSet = flag;
	}
	
	/**
	 * Inits the labels of this polyhedron, its faces and edges. labels[0] for
	 * polyhedron itself, labels[1..n] for faces and edges,
	 * 
	 * @param labels
	 */
	void initLabels(String[] labels) {

		// Application.printStacktrace("");

		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}
		
		setAllLabelsAreSet(true);

		if (labels == null || labels.length == 0) {
			labels = new String[1];
		}

		/*
		 * String s="labels:\n"; for (int i=0; i<labels.length; i++)
		 * s+=labels[i]+"\n";
		 * s+="points: "+pointsCreated.size()+"\npolygons: "+polygons
		 * .size()+"\nsegments: "+segments.size(); Application.debug(s);
		 */

		// first label for polyhedron itself
		setLabel(labels[0]);

		int index = 1;

		// labels for created points
		if (labels.length - index < pointsCreated.size()) {
			defaultPointsLabels();
			defaultPolygonsLabels();
			defaultSegmentLabels();
			return;
		}

		for (GeoPoint3D point : pointsCreated) {
			point.setLabel(labels[index]);
			index++;
		}

		// labels for polygons
		if (labels.length - index < polygons.size()) {
			defaultPolygonsLabels();
			defaultSegmentLabels();
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLabel(labels[index]);
			//Application.debug("labels["+index+"]="+labels[index]);
			index++;
		}

		// labels for segments
		if (labels.length - index < segments.size()) {
			defaultSegmentLabels();
			return;
		}

		// labels for segments
		for (GeoSegment3D segment : segments.values()) {
			segment.setLabel(labels[index]);
			//Application.debug("labels["+index+"]="+labels[index]+",\nsegment:"+segment.getParentAlgorithm());
			index++;
		}

	}
	
	private void defaultPointsLabels() {
		for (GeoPointND point : pointsCreated)
			point.setLabel(null);
	}

	private StringBuffer sb = new StringBuffer();

	/**
	 * 
	 * @param geo
	 * @return level of usability of the label
	 */
	private static int usableLabel(GeoElement geo) {
		if (!geo.isLabelSet())
			return 2; // not usable
		else if (geo.getLabel(StringTemplate.defaultTemplate).contains("_"))
			return 2; // not usable
		else
			return 0; // usable

	}

	private void defaultPolygonsLabels() {
		for (ConstructionElementCycle key : polygonsIndex.keySet()) {

			// stores points names and find the first
			String label = null;
			int labelUsability = 0;

			String[] points = new String[key.size()];
			int indexFirstPointName = 0;
			int i = 0;
			for (Iterator<ConstructionElement> it = key.iterator(); it
					.hasNext() && (labelUsability < 2);) {
				GeoElement p = (GeoElement) it.next();
				labelUsability += usableLabel(p);
				if (labelUsability < 2) {
					points[i] = p.getLabel(StringTemplate.defaultTemplate);
					if (points[i]
							.compareToIgnoreCase(points[indexFirstPointName]) < 0)
						indexFirstPointName = i;
					i++;
				}
			}

			if (labelUsability < 2) {

				sb.setLength(0);
				sb.append(loc.getPlain("Name.face"));

				// sets the direction to the next first name
				int indexSecondPointPlus = indexFirstPointName + 1;
				if (indexSecondPointPlus == points.length)
					indexSecondPointPlus = 0;
				int indexSecondPointMinus = indexFirstPointName - 1;
				if (indexSecondPointMinus == -1)
					indexSecondPointMinus = points.length - 1;

				if (points[indexSecondPointPlus]
						.compareToIgnoreCase(points[indexSecondPointMinus]) < 0) {
					for (int j = indexFirstPointName; j < points.length; j++)
						sb.append(points[j]);
					for (int j = 0; j < indexFirstPointName; j++)
						sb.append(points[j]);
				} else {
					for (int j = indexFirstPointName; j >= 0; j--)
						sb.append(points[j]);
					for (int j = points.length - 1; j > indexFirstPointName; j--)
						sb.append(points[j]);
				}

				label = sb.toString();
			}

			polygons.get(polygonsIndex.get(key)).setLabel(label);
		}
	}

	private void defaultSegmentLabels() {
		for (ConstructionElementCycle key : segmentsIndex.keySet()) {

			int labelUsability = 0;
			String label = null;

			String[] points = new String[2];
			int i = 0;
			for (Iterator<ConstructionElement> it = key.iterator(); it
					.hasNext() && (labelUsability < 2);) {
				GeoElement p = (GeoElement) it.next();
				labelUsability += usableLabel(p);
				if (labelUsability < 2) {
					points[i] = p.getLabel(StringTemplate.defaultTemplate);
					i++;
				}
			}

			if (labelUsability < 2) {

				sb.setLength(0);
				sb.append(loc.getPlain("Name.edge"));
				// sets the points names in order
				if (points[0].compareToIgnoreCase(points[1]) < 0) {
					sb.append(points[0]);
					sb.append(points[1]);
				} else {
					sb.append(points[1]);
					sb.append(points[0]);
				}

				label = sb.toString();
			}

			segments.get(segmentsIndex.get(key)).setLabel(label);
		}
	}

	public GeoSegmentND[] getSegments() {

		GeoSegmentND[] ret = new GeoSegmentND[segments.size()];
		int i = 0;
		for (GeoSegment3D segment : segments.values()) {
			ret[i] = segment;
			i++;
		}
		return ret;
	}
	
	public GeoSegment3D[] getSegments3D() {

		GeoSegment3D[] ret = new GeoSegment3D[segments.size()];
		int i = 0;
		for (GeoSegment3D segment : segments.values()) {
			ret[i] = segment;
			i++;
		}
		return ret;
	}

	public GeoPolygon3D[] getFaces() {
		GeoPolygon3D[] polygonsArray = new GeoPolygon3D[polygons.size()];
		int index = 0;
		for (GeoPolygon3D polygon : polygons.values()) {
			polygonsArray[index] = polygon;
			index++;
		}

		return polygonsArray;
	}
	
	public Collection<GeoPolygon3D> getFacesCollection(){
		return polygons.values();
	}
	
	public GeoPolygon3D getFace(int index){
		return polygons.get(index);
	}

	/**
	 * 
	 * @return collection of polygons created by this
	 */
	public Collection<GeoPolygon3D> getPolygons() {
		return polygons.values();
	}



	/**
	 * set all polygons to reverse normals (for 3D drawing)
	 */
	public void setReverseNormals() {
		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setReverseNormalForDrawing();
		}
	}

	@Override
	public void setEuclidianVisible(boolean visible) {

		super.setEuclidianVisible(visible);

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setEuclidianVisible(visible, false);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setEuclidianVisible(visible, false);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setEuclidianVisible(visible);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setEuclidianVisible(visible);
		}
	}

	@Override
	public void setObjColor(GColor color) {

		super.setObjColor(color);

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setObjColor(color);
			polygon.updateVisualStyle();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setObjColor(color);
			polygon.updateVisualStyle();
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setObjColor(color);
			segment.updateVisualStyle();
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setObjColor(color);
			segment.updateVisualStyle();
		}

		getKernel().notifyRepaint();
	}

	@Override
	public void setLineType(int type) {
		super.setLineType(type);

		if (polygons == null)
			return;

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineType(type, false);
			polygon.updateVisualStyle();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineType(type, false);
			polygon.updateVisualStyle();
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineType(type);
			segment.updateVisualStyle();
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLineType(type);
			segment.updateVisualStyle();
		}

	}

	@Override
	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);

		if (polygons == null)
			return;

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineTypeHidden(type, false);
			polygon.updateVisualStyle();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineTypeHidden(type, false);
			polygon.updateVisualStyle();
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineTypeHidden(type);
			segment.updateVisualStyle();
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLineTypeHidden(type);
			segment.updateVisualStyle();
		}

	}

	@Override
	public void setLineThickness(int th) {
		super.setLineThickness(th);

		if (polygons == null)
			return;

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineThickness(th, false);
			polygon.update();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineThickness(th, false);
			polygon.updateVisualStyle();
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineThickness(th);
			segment.updateVisualStyle();
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setLineThickness(th);
			segment.updateVisualStyle();
		}
	}

	@Override
	public void setAlphaValue(float alpha) {

		super.setAlphaValue(alpha);

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setAlphaValue(alpha);
			polygon.updateVisualStyle();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setAlphaValue(alpha);
			polygon.updateVisualStyle();
		}

		getKernel().notifyRepaint();

	}

	/*
	 * public void update() {
	 * 
	 * for (GeoPolygon3D polygon : polygons.values()){ polygon.update(); }
	 * 
	 * for (GeoSegment3D segment : segments.values()){ segment.update(); }
	 * 
	 * 
	 * }
	 */

	/*
	 * update the polygons and the segments from their parent algorithms
	 * 
	 * public void updatePolygonsAndSegmentsFromParentAlgorithms() {
	 * 
	 * for (GeoPolygon3D polygon : polygons.values()){
	 * //polygon.updateCoordSysAndPoints2D();
	 * polygon.getParentAlgorithm().update(); }
	 * 
	 * for (GeoSegment3D segment : segments.values()){
	 * segment.getParentAlgorithm().update(); }
	 * 
	 * 
	 * }
	 */

	@Override
	public GeoElement copy() {
		return new GeoPolyhedron(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POLYHEDRON;
	}

	@Override
	public String getTypeString() {
		switch(type){
		case TYPE_PRISM:
			return "Prism";
		case TYPE_PYRAMID:
			return "Pyramid";
		default:
			return "Polyhedron";
		}
	}

	@Override
	public boolean isDefined() {

		return isDefined;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		if (geo instanceof GeoPolyhedron) {
			GeoPolyhedron polyhedron = (GeoPolyhedron) geo;
			
			isDefined = polyhedron.isDefined;
			
			// global
			type = polyhedron.type;
			setVolume(polyhedron.getVolume());
			setOrientedHeight(polyhedron.getOrientedHeight());
			
			// set polygons
			//polygons.clear();
			int index = 0;
			for (GeoPolygon p : polyhedron.polygonsLinked){
				if(setPolygon(index, p)){
					index++;
				}
			}
			for (GeoPolygon p : polyhedron.polygons.values()){
				if(setPolygon(index, p)){
					index++;
				}
			}
			

			// set last polygons undefined
			if(!polygons.isEmpty()){
				for (int i = index; i < polygons.lastKey() ; i++){
					polygons.get(i).setUndefined();
				}
			}			


			// set segments
			//segments.clear();
			index = 0;
			for (GeoSegmentND s : polyhedron.segmentsLinked.values()){
				if(setSegment(index, s)){
					index++;
				}
			}
			for (GeoSegment3D s : polyhedron.segments.values()){
				if(setSegment(index, s)){
					index++;
				}
			}

			// set last segments undefined
			if (!segments.isEmpty()){
				for (int i = index; i < segments.lastKey() ; i++){
					segments.get((long) i).setUndefined();
				}
			}

	
		}
	}
	
	private boolean setPolygon(int index, GeoPolygon p){
		
		if (!p.isDefined()){
			return false;
		}
		
		GeoPolygon3D poly = polygons.get(index);
		if (poly == null){
			poly = new GeoPolygon3D(getConstruction());
			polygons.put(index, poly);
		}
		poly.set(p);
		return true;
	}
	
	private boolean setSegment(long index, GeoSegmentND s){
		
		if (!s.isDefined()){
			return false;
		}
		
		GeoSegment3D seg = segments.get(index);
		if (seg == null){
			seg = new GeoSegment3D(getConstruction());
			segments.put(index, seg);
		}
		seg.setSegment(s);
		
		return true;
	}

	private boolean isDefined = true;

	@Override
	public void setUndefined() {
		isDefined = false;
		
		volume = Double.NaN;

		/*
		 * for (GeoPolygon3D polygon : polygons.values()){
		 * polygon.setEuclidianVisible(visible,false); }
		 * 
		 * for (GeoPolygon polygon : polygonsLinked){
		 * polygon.setEuclidianVisible(visible,false); }
		 * 
		 * for (GeoSegment3D segment : segments.values()){
		 * segment.setEuclidianVisible(visible); }
		 * 
		 * for (GeoSegmentND segment : segmentsLinked.values()){
		 * segment.setEuclidianVisible(visible); }
		 */
	}

	public void setDefined() {
		isDefined = true;
	}

	@Override
	public boolean showInAlgebraView() {
		return isDefined();
	}

	@Override
	protected boolean showInEuclidianView() {

		return isDefined();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return kernel.format(getVolume(), tpl);
	}

	private StringBuilder sbToString = new StringBuilder(50);
	
	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getVolume(), tpl));
		return sbToString.toString();
	}

	@Override
	final public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getVolume()));
		return sbToString.toString();
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		super.getXMLtags(sb);
	}

	// /////////////////////////////////////////
	// GeoElement3DInterface

	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}

	// /////////////////////////////////////////
	// Path Interface

	/*
	 * public void pointChanged(GeoPointND PI) { // TODO Auto-generated method
	 * stub
	 * 
	 * }
	 * 
	 * 
	 * public void pathChanged(GeoPointND PI) { // TODO Auto-generated method
	 * stub
	 * 
	 * }
	 * 
	 * 
	 * public boolean isOnPath(GeoPointND PI, double eps) { // TODO
	 * Auto-generated method stub return false; }
	 * 
	 * 
	 * public double getMinParameter() { // TODO Auto-generated method stub
	 * return 0; }
	 * 
	 * 
	 * public double getMaxParameter() { // TODO Auto-generated method stub
	 * return 0; }
	 * 
	 * 
	 * public boolean isClosedPath() { // TODO Auto-generated method stub return
	 * false; }
	 * 
	 * 
	 * public PathMover createPathMover() { // TODO Auto-generated method stub
	 * return null; }
	 */

	@Override
	public boolean isPath() {
		return false;
	}

	@Override
	public void remove() {
		
		for (GeoPolygon polygon : polygonsLinked){
			polygon.removeMeta(this);
		}

		// prevent from removing this when redefine a prism (see
		// AlgoJoinPoints3D and AlgoPolygon)
		if (this != getConstruction().getKeepGeo())
			super.remove();
	}
	
	
	////////////////////////////
	// VOLUME
	////////////////////////////
	
	private double volume = Double.NaN;
	
	/**
	 * sets the volume
	 * @param volume volume
	 */
	public void setVolume(double volume){
		this.volume =  volume;
	}


	public double getVolume() {
		return volume;
	}


	public boolean hasFiniteVolume() {
		return isDefined();
	}

	
	
	//////////////////
	// TRACE
	//////////////////

	private boolean trace;	
	
	@Override
	public boolean isTraceable() {
		return true;
	}

	public boolean getTrace() {
		return trace;
	}
	
	
	public void setTrace(boolean trace) {
		
		this.trace = trace;

		if (polygons == null){
			return;
		}
		

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setTrace(trace);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setTrace(trace);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setTrace(trace);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((Traceable) segment).setTrace(trace);
		}

		getKernel().notifyRepaint();
	}
	
	
	
	//////////////////////////////////
	// TRANSFORM
	//////////////////////////////////


	public void rotate(NumberValue r, GeoPointND S) {
		for (GeoSegment3D seg: segments.values()){
			seg.rotate(r, S);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.rotate(r, S);
		}
	}


	public void rotate(NumberValue r) {
		
		for (GeoSegment3D seg: segments.values()){
			seg.rotate(r);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.rotate(r);
		}
		
	}


	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {
		for (GeoSegment3D seg: segments.values()){
			seg.rotate(r, S, orientation);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.rotate(r, S, orientation);
		}
	}


	public void rotate(NumberValue r, GeoLineND line) {
		for (GeoSegment3D seg: segments.values()){
			seg.rotate(r, line);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.rotate(r, line);
		}
		
	}

	
	@Override
	final public boolean isTranslateable() {
		return true;
	}

	public void translate(Coords v) {
		for (GeoSegment3D seg: segments.values()){
			seg.translate(v);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.translate(v);
		}	
	}
	
	
	
	////////////////////////
	// MIRROR
	////////////////////////
	
	public void mirror(Coords Q) {
		for (GeoSegment3D seg: segments.values()){
			seg.mirror(Q);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.mirror(Q);
		}	
	}

	public void mirror(GeoLineND g) {
		for (GeoSegment3D seg: segments.values()){
			seg.mirror(g);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.mirror(g);
		}	
	}
	
	public void mirror(GeoPlane3D plane) {
		for (GeoSegment3D seg: segments.values()){
			seg.mirror(plane);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.mirror(plane);
		}	
	}
	
	////////////////////////
	// DILATE
	////////////////////////


	public void dilate(NumberValue rval, Coords S) {
		
		for (GeoSegment3D seg: segments.values()){
			seg.dilate(rval,S);
		}
		
		for (GeoPolygon3D p : polygons.values()){
			p.dilate(rval,S);
		}	

		double r = Math.abs(rval.getDouble());		
		volume *= r*r*r;
		
	}


	/**
	 * oriented (positive or negative) height
	 */
	private double orientedHeight;
	
	/**
	 * set oriented (positive or negative) height
	 * @param height height
	 */
	public void setOrientedHeight(double height){
		orientedHeight = height;
	}
	
	public double getOrientedHeight() {
		return orientedHeight;
	}

	
	/**
	 * 
	 * @return bottom face (for pyramid & prism)
	 */
	public GeoPolygon getBottomFace(){
		if (polygonsLinked.isEmpty()){
			return polygons.get(0);
		}
		return polygonsLinked.first();
	}
	
	
	/**
	 * 
	 * @return top face (for prism)
	 */
	public GeoPolygon getTopFace(){
		return polygons.lastEntry().getValue();
	}
	
	

	
	/**
	 * 
	 * @return first side face (for prism)
	 */
	public GeoPolygon getFirstSideFace(){
		if (polygonsLinked.isEmpty()){
			return polygons.get(1);
		}
		return polygons.get(0);
	}
	
	

	/**
	 * 
	 * @return top point (for pyramid)
	 */
	public Coords getTopPoint() {
		GeoPolygon p = getFirstSideFace();		
		return p.getPoint3D(p.getPointsLength() - 1);
		
	}

	
}
