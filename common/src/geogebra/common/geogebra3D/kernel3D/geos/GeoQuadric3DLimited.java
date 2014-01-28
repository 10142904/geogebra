package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.awt.GColor;
import geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasHeight;
import geogebra.common.kernel.kernelND.HasVolume;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.plugin.GeoClass;


/**
 * Class for limited quadrics (e.g. limited cones, cylinders, ...)
 * 
 * @author mathieu
 * 
 */
public class GeoQuadric3DLimited extends GeoQuadricND 
implements GeoNumberValue, HasVolume, HasHeight, 
RotateableND, Translateable, MirrorableAtPlane, Transformable, Dilateable {

	/** side of the quadric */
	private GeoQuadric3DPart side;
	/** bottom and top of the quadric */
	private GeoConicND bottom;
	private GeoConic3D top;

	//private GeoPointND bottomPoint, topPoint;

	private double bottomParameter, topParameter;

	/*
	 * constructor
	 * 
	 * @param c
	 *
	public GeoQuadric3DLimited(Construction c) {
		this(c, null, null);
	}
	*/

	/**
	 * 
	 * @param c construction
	 */
	public GeoQuadric3DLimited(Construction c){//, GeoPointND bottomPoint, GeoPointND topPoint) {

		super(c, 3);

		//setPoints(bottomPoint, topPoint);

		// TODO merge with GeoQuadricND
		eigenvecND = new Coords[3];
		for (int i = 0; i < 3; i++) {
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i + 1, 1);
		}

		// diagonal (diagonalized matrix)
		diagonal = new double[4];

	}

	/*
	public void setPoints(GeoPointND bottomPoint, GeoPointND topPoint) {
		this.bottomPoint = bottomPoint;
		this.topPoint = topPoint;
	}
	*/


	public void setParts(GeoQuadric3DPart side, GeoConicND bottom2,
			GeoConic3D top) {
		this.side = side;
		this.bottom = bottom2;
		this.top = top;
	}

	public GeoQuadric3DLimited(GeoQuadric3DLimited quadric) {
		this(quadric.getConstruction());
		this.bottom = new GeoConic3D(quadric.getConstruction());
		this.top = new GeoConic3D(quadric.getConstruction());
		this.side = new GeoQuadric3DPart(quadric.getConstruction());
		set(quadric);
	}

	public GeoConicND getBottom() {
		return bottom;
	}

	public GeoConic3D getTop() {
		return top;
	}

	public GeoQuadric3DPart getSide() {
		return side;
	}

	public void updatePartsVisualStyle() {
		setObjColor(getObjectColor());
		setLineThickness(getLineThickness());
		setAlphaValue(getAlphaValue());
		setEuclidianVisible(isEuclidianVisible());

	}

	/**
	 * init the labels
	 * 
	 * @param labels
	 */
	public void initLabelsIncludingBottom(String[] labels) {

		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		if (labels == null || labels.length == 0) {
			labels = new String[1];
		}

		setLabel(labels[0]);

		if (labels.length < 3) {
			bottom.setLabel(null);
			if (!silentTop)
				top.setLabel(null);
			side.setLabel(null);
			return;
		} else if (labels.length == 3) {
			bottom.setLabel(labels[1]);
			side.setLabel(labels[2]);
		} else {
			bottom.setLabel(labels[1]);
			top.setLabel(labels[2]);
			side.setLabel(labels[3]);
		}

	}
	
	/**
	 * init the labels
	 * 
	 * @param labels
	 */
	public void initLabelsNoBottom(String[] labels) {

		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		if (labels == null || labels.length == 0) {
			labels = new String[1];
		}

		setLabel(labels[0]);

		if (labels.length < 3) {
			if (!silentTop)
				top.setLabel(null);
			side.setLabel(null);
			return;
		}
		// else
		top.setLabel(labels[1]);
		side.setLabel(labels[2]);

	}

	public double getBottomParameter() {
		return bottomParameter;
	}

	public double getTopParameter() {
		return topParameter;
	}

	// TODO merge in GeoQuadricND
	/**
	 * @param origin
	 * @param direction
	 * @param r
	 * @param bottomParameter
	 * @param topParameter
	 * 
	 */
	public void setCylinder(Coords origin, Coords direction, double r,
			double bottomParameter,
			double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);
		
		// set center
		setMidpoint(origin.get());

		// set direction
		eigenvecND[2] = direction;

		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];

		// set halfAxes = radius
		for (int i = 0; i < 2; i++)
			halfAxes[i] = r;
		
		halfAxes[2] = 1;


		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r * r;

		// set matrix
		setMatrixFromEigen();

		// set type
		setType(QUADRIC_CYLINDER);

	}
	
	/**
	 * sets the bottom and top values for limits
	 * 
	 * @param bottomParameter
	 * @param topParameter
	 */
	public void setLimits(double bottomParameter, double topParameter) {

			this.bottomParameter = bottomParameter;
			this.topParameter = topParameter;

	}

	public void setCone(Coords origin, Coords direction, double r, double bottomParameter,
			double topParameter) {

		// limits
		setLimits(bottomParameter, topParameter);
		
		// set center
		setMidpoint(origin.get());

		// set direction
		eigenvecND[2] = direction;

		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];

		// set halfAxes = radius
		for (int i = 0; i < 2; i++)
			halfAxes[i] = r;
		
		halfAxes[2] = 1;

		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = -r * r;
		diagonal[3] = 0;

		// set matrix
		setMatrixFromEigen();
		
		// set type
		type = QUADRIC_CONE;
	}

	public void set(Coords origin, Coords direction, double r, double min,
			double max) {

		switch (type) {
		case QUADRIC_CYLINDER:
			setCylinder(origin, direction, r, min, max);
			break;
		case QUADRIC_CONE:
			setCone(origin, direction, r, min, max);
			break;
		}
	}

	// ///////////////////////
	// GEOELEMENT
	// ///////////////////////

	@Override
	public void setObjColor(GColor color) {
		super.setObjColor(color);
		if (bottom == null)
			return;
		bottom.setObjColor(color);
		if (!silentTop)
			top.setObjColor(color);
		side.setObjColor(color);

	}
	
	
	@Override
	public void setTrace(boolean trace) {
		super.setTrace(trace);
		if (bottom == null)
			return;
		bottom.setTrace(trace);
		if (!silentTop)
			top.setTrace(trace);
		side.setTrace(trace);

	}
	
	
	

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		
		super.setEuclidianVisible(visible);
		bottom.setEuclidianVisible(visible);
		if (!silentTop)
			top.setEuclidianVisible(visible);
		side.setEuclidianVisible(visible);

	}
	
	@Override
	public void setLineType(int type) {
		super.setLineType(type);

		if (bottom == null)
			return;

		bottom.setLineType(type);
		bottom.update();

		if (!silentTop){
			top.setLineType(type);
			top.update();
		}

	}

	@Override
	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);

		if (bottom == null)
			return;

		bottom.setLineTypeHidden(type);
		bottom.update();

		if (!silentTop){
			top.setLineTypeHidden(type);
			top.update();
		}
	}

	@Override
	public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (bottom == null)
			return;
		
		bottom.setLineThickness(th);
		bottom.update();
		
		if (!silentTop){
			top.setLineThickness(th);
			top.update();
		}
	}

	@Override
	public void setAlphaValue(float alpha) {

		super.setAlphaValue(alpha);

		if (bottom == null)
			return;

		bottom.setAlphaValue(alpha);
		bottom.updateVisualStyle();
		if (!silentTop) {
			top.setAlphaValue(alpha);
			top.updateVisualStyle();
		}
		side.setAlphaValue(alpha);
		side.updateVisualStyle();

		getKernel().notifyRepaint();

	}

	@Override
	public GeoElement copy() {
		return new GeoQuadric3DLimited(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.QUADRIC_LIMITED;
	}

	@Override
	public String getTypeString() {
		return side.getTypeString();
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {

		if (geo instanceof GeoQuadric3DLimited) {
			GeoQuadric3DLimited quadric = (GeoQuadric3DLimited) geo;

			bottomParameter = quadric.bottomParameter;
			topParameter = quadric.topParameter;
			volume = quadric.volume;

			bottom.set(quadric.bottom);
			top.set(quadric.top);
			silentTop = quadric.silentTop;
			side.set(quadric.side);

			// TODO merge with GeoQuadric3D
			// copy everything
			toStringMode = quadric.toStringMode;
			type = quadric.type;
			for (int i = 0; i < 10; i++)
				matrix[i] = quadric.matrix[i]; // flat matrix A

			for (int i = 0; i < 3; i++) {
				eigenvecND[i].set(quadric.eigenvecND[i]);
				halfAxes[i] = quadric.halfAxes[i];
			}

			setMidpoint(quadric.getMidpoint().get());

			defined = quadric.defined;

			super.set(geo);
		}

	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	// ///////////////////////////////////
	// GEOQUADRICND
	// ///////////////////////////////////

	private double volume;

	public void calcVolume() {

		// Application.debug("ici");
		if (bottom == null) {
			volume = Double.NaN;
			return;
		}
		
		double radius = getHalfAxis(0);

		switch (type) {
		case QUADRIC_CYLINDER:
			volume = radius * radius * Math.PI * Math.abs(topParameter - bottomParameter);
			break;
		case QUADRIC_CONE:
			double h = Math.abs(topParameter - bottomParameter);
			radius *= h; // half-axis is the radius value for h = 1
			volume = radius * radius * Math.PI * h
					/ 3;
			break;
		// default:
		// volume=Double.NaN;
		}
		
	}

	public double getVolume() {
		if (defined)
			return volume;
		return Double.NaN;
	}
	
	public boolean hasFiniteVolume(){
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		switch (type) {
		case QUADRIC_CYLINDER:
		case QUADRIC_CONE:
			return kernel.format(volume,tpl);
		case QUADRIC_EMPTY:
			return kernel.format(0,tpl);
		}

		return "todo-GeoQuadric3DLimited";

	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		return new StringBuilder(toValueString(tpl));
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub

	}

	// ////////////////////////////////
	// NumberValue
	// ////////////////////////////////

	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	public double getDouble() {
		return getVolume();
	}

	@Override
	public boolean isNumberValue() {
		return true;
	}
	
	private boolean silentTop = false;

	/**
	 * set top as silent part (not in construction)
	 */
	public void setSilentTop() {
		
		silentTop = true;
		
	}
	



	public double getOrientedHeight() {
		return topParameter - bottomParameter;
	}
	
	
	@Override
	public boolean isGeoElement3D() {
		return true;
	}
	
	
	@Override
	protected void setColorVisualStyle(final GeoElement geo){
		setObjColor(geo.getObjectColor());
		setAlphaValue(geo.getAlphaValue());
	}

	

	public void rotate(NumberValue r, GeoPointND S) {

		bottom.rotate(r, S);
		top.rotate(r, S);
		side.rotate(r, S);

	}

	public void rotate(NumberValue r) {
		
		bottom.rotate(r);
		top.rotate(r);
		side.rotate(r);

	}

	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {
		
		((GeoConic3D) bottom).rotate(r, S, orientation);
		top.rotate(r, S, orientation);
		side.rotate(r, S, orientation);		
	}

	public void rotate(NumberValue r, GeoLineND line) {

		((GeoConic3D) bottom).rotate(r, line);
		top.rotate(r, line);
		side.rotate(r, line);		
		
	}
	
	@Override
	final public boolean isTranslateable() {
		return true;
	}

	public void translate(Coords v) {
		bottom.translate(v);
		top.translate(v);
		side.translate(v);
	}
	
	
	
	////////////////////////
	// MIRROR
	////////////////////////
	
	public void mirror(Coords Q) {
		bottom.mirror(Q);
		top.mirror(Q);
		side.mirror(Q);
	}

	public void mirror(GeoLineND g) {
		bottom.mirror(g);
		top.mirror(g);
		side.mirror(g);
	}
	
	public void mirror(GeoPlane3D plane) {
		((MirrorableAtPlane) bottom).mirror(plane);
		top.mirror(plane);
		side.mirror(plane);
	}
	
	
	////////////////////////
	// DILATE
	////////////////////////


	public void dilate(NumberValue rval, Coords S) {
		bottom.dilate(rval, S);
		top.dilate(rval, S);
		side.dilate(rval, S);
		
		double r = Math.abs(rval.getDouble());		
		volume *= r*r*r;

	}
	
	
	
	
	
	
	@Override
	final protected void singlePoint() {
		type = GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;
		
		//TODO more ?

	}

}
