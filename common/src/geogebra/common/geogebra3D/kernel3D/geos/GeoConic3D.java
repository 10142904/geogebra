package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.plugin.GeoClass;

/**
 * @author ggb3D
 * 
 */
public class GeoConic3D extends GeoConicND 
implements RotateableND, MirrorableAtPlane {

	/** 2D coord sys where the conic exists */
	private CoordSys coordSys;



	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * 
	 * @param c
	 *            construction
	 * @param cs
	 *            2D coord sys
	 */
	public GeoConic3D(Construction c, CoordSys cs) {
		this(c);
		setCoordSys(cs);
	}

	public GeoConic3D(GeoConicND conic) {
		this(conic.getConstruction());
		set(conic);
	}

	/**
	 * Creates an empty 3D conic with 2D coord sys
	 * 
	 * @param c
	 *            construction
	 */
	public GeoConic3D(Construction c) {
		this(c, false);
	}

	/**
	 * 
	 * @param c construction
	 * @param isIntersection if this is an intersection curve
	 */
	public GeoConic3D(Construction c, boolean isIntersection) {
		super(c, 2, isIntersection);
	}

	// ///////////////////////////////////////
	// link with the 2D coord sys

	/**
	 * set the 2D coordinate system
	 * 
	 * @param cs
	 *            the 2D coordinate system
	 */
	public void setCoordSys(CoordSys cs) {

		// Application.printStacktrace(cs.getMatrixOrthonormal().toString());
		this.coordSys = cs;
	}

	@Override
	public CoordSys getCoordSys() {
		return coordSys;
	}

	/*
	 * private Coords midpoint2D;
	 * 
	 * /** sets the coords of the 2D midpoint
	 * 
	 * @param coords
	 * 
	 * public void setMidpoint2D(Coords coords){ midpoint2D=coords; }
	 * 
	 * public Coords getMidpoint2D(){ return midpoint2D; }
	 */

	

	

	@Override
	public Coords getMainDirection() {
		return coordSys.getNormal();
	}

	// ///////////////////////////////////////
	// GeoConicND

	/*
	 * public Coords getMidpoint2D(){ return
	 * coordSys.getPoint(super.getMidpoint2D());
	 * 
	 * }
	 */

	@Override
	public Coords getEigenvec3D(int i) {
		return coordSys.getVector(super.getEigenvec(i));
	}

	public Coords getMidpointND() {
		return getMidpoint3D();
	}

	@Override
	public Coords getMidpoint3D() {
		return coordSys.getPoint(super.getMidpoint2D());
	}
	


	@Override
	public Coords getDirection3D(int i) {
		return getCoordSys().getVector(lines[i].y, -lines[i].x);
	}

	@Override
	public Coords getOrigin3D(int i) {
		return getCoordSys().getPoint(startPoints[i].x, startPoints[i].y);
	}

	// ///////////////////////////////////////
	// GeoConic3D
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CONIC3D;
	}

	/**
	 * it's a 3D GeoElement.
	 * 
	 * @return true
	 */
	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public String toString(StringTemplate tpl) {

		StringBuilder sbToString = new StringBuilder();
		sbToString.setLength(0);
		sbToString.append(label);

		sbToString.append(": ");
		sbToString.append(buildValueString(tpl));
		return sbToString.toString();
	}

	@Override
	public boolean hasValueStringChangeableRegardingView() {
		return true;
	}
	
	private void buildValueStringMidpointConic(boolean plusMinusX, String s1, String s2, StringTemplate tpl, StringBuilder sbBuildValueString){
		
		Coords center = getMidpoint3D();
		GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, center.getX(), center.getY(), center.getZ(), sbBuildValueString);
		
		Coords ev1 = getEigenvec3D(0);
		Coords ev2 = getEigenvec3D(1);
		double r1 = getHalfAxis(0);
		double r2 = getHalfAxis(1);
		
		String separator = GeoPoint.buildValueStringSeparator(kernel, tpl);

		sbBuildValueString.append(" + (");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getX(), r2 * ev2.getX(), s1, s2, tpl, sbBuildValueString);
		
		sbBuildValueString.append(separator);
		sbBuildValueString.append(" ");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getY(), r2 * ev2.getY(), s1, s2, tpl, sbBuildValueString);
		
		sbBuildValueString.append(separator);
		sbBuildValueString.append(" ");

		kernel.appendTwoCoeffs(plusMinusX, r1 * ev1.getZ(), r2 * ev2.getZ(), s1, s2, tpl, sbBuildValueString);

		sbBuildValueString.append(')');
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {
		
		StringBuilder sbBuildValueString = new StringBuilder();
		if (!isDefined()) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}

		switch(getType()){
		case CONIC_CIRCLE:
		case CONIC_ELLIPSE:
			buildValueStringMidpointConic(false, "cos(t)", "sin(t)", tpl, sbBuildValueString);
			break;
			
		case CONIC_HYPERBOLA:
			buildValueStringMidpointConic(true, "cosh(t)", "sinh(t)", tpl, sbBuildValueString);
			break;
			
			
		default:
			sbBuildValueString.append("todo-GeoConic3D");
			break;
		}

	       
		return sbBuildValueString;

	}
	
	

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * set the conic as single point equal to m
	 * @param m point
	 */
	public void setSinglePoint(GeoPointND m){

		//coordSys.setSimpleCoordSysWithOrigin(m.getInhomCoordsInD(3));

		// set midpoint as projection of m on the current coord sys
		setMidpoint(coordSys.getNormalProjection(m.getInhomCoordsInD(3))[1].get());

		setSinglePointMatrix();

		singlePoint();


	}
	
	private void setSinglePointMatrix(){
		for (int i=0;i<matrix.length;i++)
			matrix[i] = 0;

		for (int i=0;i<3;i++)
			matrix[i] = 1.0d;
	}
	
	
	/**
	 * set this to sigle point at m location
	 * @param m point
	 */
	public void setSinglePoint(Coords m){
		
		coordSys.resetCoordSys();
		coordSys.addPoint(m);
		coordSys.completeCoordSys2D();
		coordSys.makeOrthoMatrix(false, false);
		
		setMidpoint(new double[] {0,0});
		
		setSinglePointMatrix();

		singlePoint();

	}

	@Override
	public GeoElement copy() {
		return new GeoConic3D(this);
	}

	/*
	 * protected String getTypeString() { switch (type) { case
	 * GeoConic.CONIC_CIRCLE: return "Circle"; default: return "Conic3D"; }
	 * 
	 * 
	 * }
	 */

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {

		if (geo instanceof GeoConicND) {
			super.set(geo);
			if (coordSys == null) // TODO remove that
				coordSys = new CoordSys(2);
			coordSys.set(((GeoConicND) geo).getCoordSys());
			setIsEndOfQuadric(((GeoConicND) geo).isEndOfQuadric());
		}

	}


	// //////////////////////////////////
	// XML
	// //////////////////////////////////

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// curve thickness and type
		getLineStyleXML(sb);

	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////

	@Override
	public Coords getPoint(double x2d, double y2d) {
		return getCoordSys().getPoint(x2d, y2d);
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return coords.projectPlane(getCoordSys().getMatrixOrthonormal());
	}

	public Coords[] getProjection(Coords coords, Coords willingDirection) {
		return coords.projectPlaneThruV(getCoordSys().getMatrixOrthonormal(),
				willingDirection);
	}

	// //////////////////////////////////
	// GeoCoordSys2D
	// //////////////////////////////////






	/**
	 * 
	 * @return true if is an intersection curve
	 */
	public boolean isIntersection() {
		return isIntersection;
	}

	@Override
	protected void doTranslate(Coords v) {
		coordSys.translate(v);
	}



	@Override
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		
		CoordMatrix4x4 m = CoordMatrix4x4.Identity();
		m.set(1,1, a00);
		m.set(1,2, a01);
		m.set(2,1, a10);
		m.set(2,2, a11);
		
		double[] ret = getCoordSys().matrixTransform(m);	
		
		super.matrixTransform(ret[0], ret[1], 0, ret[2]);
	}


	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		CoordMatrix4x4 m = CoordMatrix4x4.Identity();
		
		m.set(1,1, a00);
		m.set(1,2, a01);		
		m.set(1,3, a02);
		
		
		m.set(2,1, a10);
		m.set(2,2, a11);
		m.set(2,3, a12);
		
		
		m.set(3,1, a20);
		m.set(3,2, a21);		
		m.set(3,3, a22);
		
		
		double[] ret = getCoordSys().matrixTransform(m);	
		
		super.matrixTransform(ret[0], ret[1], 0, ret[2]);
		
	}
	
	@Override
	final public void rotate(NumberValue phiVal) {
		coordSys.rotate(phiVal.getDouble(), Coords.O);
	}	
	
	@Override
	final public void rotate(NumberValue phiVal, GeoPointND Q) {
		coordSys.rotate(phiVal.getDouble(), Q.getInhomCoordsInD(3));
	}

	public void rotate(NumberValue phiVal, GeoPointND Q, GeoDirectionND orientation) {
		
		rotate(phiVal, Q.getInhomCoordsInD(3), orientation.getDirectionInD3());
		
	}

	public void rotate(NumberValue phiVal, GeoLineND line) {
		
		rotate(phiVal, line.getStartInhomCoords(), line.getDirectionInD3());
		
	}
	
	final private void rotate(NumberValue phiVal, Coords center, Coords direction) {
		coordSys.rotate(phiVal.getDouble(), center, direction.normalized());
	}
	

	public Coords getDirectionInD3() {
		switch(type){
		case CONIC_LINE:
		case CONIC_EMPTY:
		case CONIC_SINGLE_POINT:
			return null;
		default:
			return getCoordSys().getVz();
		}
	}
	
	
	//////////////////////////////////////////////
	// TRANSLATE
	//////////////////////////////////////////////

	@Override
	public void translate(Coords v) {
		getCoordSys().translate(v);
	}
	
	

	////////////////////////
	// MIRROR
	////////////////////////
	
	public void mirror(Coords Q) {
		getCoordSys().mirror(Q);		
	}

	public void mirror(GeoLineND line) {
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		getCoordSys().mirror(point, direction);
		
	}
	


	public void mirror(GeoPlane3D plane) {

		getCoordSys().mirror(plane.getCoordSys());
	}
	
	
	////////////////////////
	// DILATE
	////////////////////////


	public void dilate(NumberValue rval, Coords S) {
		
		double r = rval.getDouble();
		
		getCoordSys().dilate(r,S);	
		
		if (r < 0){ //mirror was done in coord sys
			r = -r;
		}
		
		dilate(r);
		
	}

}
