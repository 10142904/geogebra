package geogebra.common.geogebra3D.kernel3D.geos;

import geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.RegionParameters;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.CoordMatrix4x4;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.Functional2Var;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.Dilateable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Transformable;
import geogebra.common.kernel.geos.Translateable;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.kernelND.HasVolume;
import geogebra.common.kernel.kernelND.Region3D;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;

/**
 * class describing quadric for 3D space
 * 
 * @author matthieu
 * 
 *                  ( A[0] A[4] A[5] A[7]) 
 *         matrix = ( A[4] A[1] A[6] A[8]) 
 *                  ( A[5] A[6] A[2] A[9]) 
 *                  ( A[7] A[8] A[9] A[3])
 * 
 */
public class GeoQuadric3D extends GeoQuadricND implements
		Functional2Var, Region3D, 
		Translateable, RotateableND, MirrorableAtPlane, Transformable, Dilateable,
		HasVolume,
		GeoQuadric3DInterface{

	private static String[] vars3D = { "x\u00b2", "y\u00b2", "z\u00b2", "x y",
			"x z", "y z", "x", "y", "z" };

	private CoordMatrix4x4 eigenMatrix = CoordMatrix4x4.Identity();

	public GeoQuadric3D(Construction c) {
		super(c, 3);

		// TODO merge with 2D eigenvec
		eigenvecND = new Coords[3];
		for (int i = 0; i < 3; i++) {
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i + 1, 1);
		}

		// diagonal (diagonalized matrix)
		diagonal = new double[4];

	}
	
	
	/**
	 * Creates new GeoQuadric3D 
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param coeffs
	 *            coefficients
	 */
	public GeoQuadric3D(Construction c, String label, double[] coeffs) {

		this(c);
		setMatrix(coeffs);
		setLabel(label);
	}
	
	/**
	 *  sets quadric's matrix from coefficients of equation
	 *  from array
	 *  @param coeffs Array of coefficients
	 */  
	final public void setMatrix(double[] coeffs) {
		
		for (int i = 0 ; i < 10 ; i++){
			matrix[i] = coeffs[i];
		}
		
		classifyQuadric();
	}
	
	
	/**
	 * Update conic type and properties
	 */
	protected void classifyQuadric() {
		classifyQuadric(false);
	}
	
	private double detS;
	
	/**
	 * @param degenerate true to allow classification as degenerate
	 */
	public void classifyQuadric(boolean degenerate) {	
		
		defined = degenerate || checkDefined();		
		if (!defined)
			return;
		
		// det of S lets us distinguish between
		// parabolic and midpoint quadrics
		// det(S) = A[0] * A[1] - A[3] * A[3]
		detS = matrix[0]*matrix[1]*matrix[2] 
				    - matrix[0]*matrix[6]*matrix[6]
					- matrix[1]*matrix[5]*matrix[5] 
					- matrix[2]*matrix[4]*matrix[4] 
				  + 2*matrix[4]*matrix[5]*matrix[6];
		if (Kernel.isZero(detS)) {
			type = QUADRIC_NOT_CLASSIFIED; // TODO
		} else {		
			classifyMidpointQuadric(degenerate);
		}	
		
		//setAffineTransform();		
		
	}
	
	private void classifyMidpointQuadric(boolean degenerate) {	
		
		// set midpoint
		double x = (-matrix[1] *matrix[2] *matrix[7] + matrix[1] *matrix[5] *matrix[9] + matrix[2] *matrix[4] *matrix[8] - matrix[4] *matrix[6] *matrix[9] - matrix[5] *matrix[6] *matrix[8] + matrix[6] *matrix[6] *matrix[7])/detS;
		double y = (-matrix[0] *matrix[2] *matrix[8] + matrix[0] *matrix[6] *matrix[9] + matrix[2] *matrix[4] *matrix[7] - matrix[4] *matrix[5] *matrix[9] + matrix[5] *matrix[5] *matrix[8] - matrix[5] *matrix[6] *matrix[7])/detS;
		double z = (-matrix[0] *matrix[1] *matrix[9] + matrix[0] *matrix[6] *matrix[8] + matrix[1] *matrix[5] *matrix[7] + matrix[4] *matrix[4] *matrix[9] - matrix[4] *matrix[5] *matrix[8] - matrix[4] *matrix[6] *matrix[7])/detS;
		double[] coords = {x,y,z,1};
		setMidpoint(coords);
		
		//App.debug("\nmidpoint = "+x+","+y+","+z);
		

		// set eigenvalues
		eigenval[0] = detS;
		eigenval[1] = -matrix[0]*matrix[1]-matrix[1]*matrix[2]-matrix[2]*matrix[0] +matrix[4]*matrix[4]+matrix[5]*matrix[5]+matrix[6]*matrix[6];
		eigenval[2] = matrix[0]+matrix[1]+matrix[2];
		eigenval[3] = -1;
		
		int nRoots = cons.getKernel().getEquationSolver().solveCubic(eigenval, eigenval, Kernel.STANDARD_PRECISION);
		
		if (nRoots == 1){
			eigenval[1] = eigenval[0];
			nRoots++;
		}
		
		if (nRoots == 2){
			eigenval[2] = eigenval[1];
		}
		
		//App.debug("\nnRoots = "+nRoots+"\n"+eigenval[0]+","+eigenval[1]+","+eigenval[2]);
		
		// degenerate ?
		double beta = matrix[7] * x + matrix[8] * y + matrix[9] * z + matrix[3];
		
		//App.debug("beta = "+beta);

		if (degenerate || Kernel.isZero(beta)) {
			//TODO : degenerate
			type = QUADRIC_NOT_CLASSIFIED;
		}else{
			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			mu[2] = -eigenval[2] / beta;			
			if (detS < 0) {
				//TODO : hyperboloid
				type = QUADRIC_NOT_CLASSIFIED;
			} else {
				if (mu[0] > 0 && mu[1] > 0 && mu[2] > 0) {
					ellipsoid();
				} else {
					empty();
				}
			}
		}
		
		
		
		
	}
	
	private void ellipsoid(){
		
		// sphere 
		if (Kernel.isEqual(mu[0]/mu[1],1.0) && Kernel.isEqual(mu[0]/mu[2],1.0)) {

			double r = Math.sqrt(1.0d / mu[0]);

			// set halfAxes = radius	
			for (int i=0;i<3;i++){
				halfAxes[i] = r;
			}
			
			// set type
			type = QUADRIC_SPHERE;
			linearEccentricity = 0.0d;
			eccentricity = 0.0d;

			volume = 4*Math.PI*getHalfAxis(0)*getHalfAxis(1)*getHalfAxis(2)/3;

			// set the diagonal values
			diagonal[0] = 1;
			diagonal[1] = 1;
			diagonal[2] = 1;
			diagonal[3] = -r * r;
			
			// eigen matrix
			eigenMatrix.setOrigin(getMidpoint3D());
			for (int i = 1; i <= 3 ; i++){
				eigenMatrix.set(i, i, getHalfAxis(i-1));
			}
			
			
		} else { // ellipsoid
			//TODO
			type = QUADRIC_NOT_CLASSIFIED;
		}
		
	}
	
	
	/**
	 * returns false if quadric's matrix is the zero matrix
	 * or has infinite or NaN values
	 */
	final private boolean checkDefined() {
		
		/*
		boolean allZero = true;
		double maxCoeffAbs = 0;		
		
		for (int i = 0; i < 6; i++) {
			if (Double.isNaN(matrix[i]) || Double.isInfinite(matrix[i])) {
				return false;
			}
				
			double abs = Math.abs(matrix[i]);			
			if (abs > Kernel.STANDARD_PRECISION) allZero = false;
			if ((i == 0 || i == 1 || i == 3) && maxCoeffAbs < abs) { // check max only on coeffs x*x, y*y, x*y
				maxCoeffAbs = abs;
			}		
		}
		if (allZero) {
			return false;		
		}
		
		// huge or tiny coefficients?
		double factor = 1.0;
		if (maxCoeffAbs < MIN_COEFFICIENT_SIZE) {
			factor = 2;
			while (maxCoeffAbs * factor < MIN_COEFFICIENT_SIZE) factor *= 2;					
		}		
		else if (maxCoeffAbs > MAX_COEFFICIENT_SIZE) {			
			factor = 0.5;
			while (maxCoeffAbs * factor > MAX_COEFFICIENT_SIZE) factor *= 0.5;					
		}	
		
		// multiply matrix with factor to avoid huge and tiny coefficients
		if (factor != 1.0) {
			maxCoeffAbs *= factor;
			for (int i=0; i < 6; i++) {
				matrix[i] *= factor;
			}
		}
		*/
		return true;
	}


	public GeoQuadric3D(GeoQuadric3D quadric) {
		this(quadric.getConstruction());
		set(quadric);
	}

	public Coords getMidpointND() {
		return getMidpoint3D();
	}

	// //////////////////////////////
	// SPHERE
	
	private double volume = Double.NaN;

	@Override
	protected void setSphereNDMatrix(Coords M, double r) {
		super.setSphereNDMatrix(M, r);
		
		volume = 4*Math.PI*getHalfAxis(0)*getHalfAxis(1)*getHalfAxis(2)/3;

		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 1;
		diagonal[3] = -r * r;
		
		// eigen matrix
		eigenMatrix.setOrigin(getMidpoint3D());
		for (int i = 1; i <= 3 ; i++){
			eigenMatrix.set(i, i, getHalfAxis(i-1));
		}
	}

	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO
	}

	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO do this in GeoQuadricND, implement degenerate cases
		setSphereNDMatrix(M.getInhomCoordsInD(3), M.distance(P));
	}

	// //////////////////////////////
	// CONE

	public void setCone(GeoPointND origin, GeoVectorND direction, double angle) {

		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite();

		// check direction

		// check angle
		double r;
		double c = Math.cos(angle);
		double s = Math.sin(angle);

		if (c < 0 || s < 0)
			defined = false;
		else if (Kernel.isZero(c))
			defined = false;// TODO if c=0 then draws a plane
		else if (Kernel.isZero(s))
			defined = false;// TODO if s=0 then draws a line
		else {
			r = s / c;
			setCone(origin.getInhomCoordsInD(3), direction.getCoordsInD(3), r);
		}

	}

	public void setCone(Coords origin, Coords direction, double r) {

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

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);
		
		// set type
		type = QUADRIC_CONE;
	}

	// //////////////////////////////
	// CONE

	public void setCylinder(GeoPointND origin, Coords direction, double r) {

		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite();

		// check direction

		// check radius
		if (Kernel.isZero(r)) {
			r = 0;
		} else if (r < 0) {
			defined = false;
		}

		if (defined) {
			setCylinder(origin.getInhomCoordsInD(3), direction, r);
		}

	}

	public void setCylinder(Coords origin, Coords direction, double r) {

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

		// eigen matrix
		setEigenMatrix(halfAxes[0], halfAxes[1], 1);

		// set type
		type = QUADRIC_CYLINDER;
	}
	
	/**
	 * set the eigen matrix
	 * @param x x half-axis
	 * @param y y half-axis
	 * @param z z half-axis
	 */
	private void setEigenMatrix(double x, double y, double z){

		eigenMatrix.setOrigin(getMidpoint3D());

		eigenMatrix.setVx(eigenvecND[0].mul(x));
		eigenMatrix.setVy(eigenvecND[1].mul(y));
		eigenMatrix.setVz(eigenvecND[2].mul(z));

	}

	// /////////////////////////////
	// GeoElement

	@Override
	public GeoElement copy() {
		return new GeoQuadric3D(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.QUADRIC;
	}

	@Override
	public String getTypeString() {
		switch (type) {
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
			return "Sphere";
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			return "Cylinder";
		case GeoQuadricNDConstants.QUADRIC_CONE:
			return "Cone";
		default:
			return "Quadric";
		}
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		return false;
	}

	@Override
	public void set(GeoElement geo) {

		GeoQuadric3D quadric = (GeoQuadric3D) geo;

		// copy everything
		toStringMode = quadric.toStringMode;
		type = quadric.type;
		for (int i = 0; i < 10; i++)
			matrix[i] = quadric.matrix[i]; // flat matrix A

		for (int i = 0; i < 3; i++) {
			eigenvecND[i].set(quadric.eigenvecND[i]);
			halfAxes[i] = quadric.halfAxes[i];
		}

		for (int i = 0; i < 4; i++) {
			diagonal[i] = quadric.diagonal[i];
		}

		setMidpoint(quadric.getMidpoint().get());

		eigenMatrix.set(quadric.eigenMatrix);

		defined = quadric.defined;
		volume = quadric.volume;
		
		super.set(geo);
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	protected StringBuilder buildValueString(StringTemplate tpl) {

		StringBuilder sbToValueString=new StringBuilder();

		switch (type) {
		case QUADRIC_SPHERE:
			buildSphereNDString(sbToValueString,tpl);
			break;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			double[] coeffs = new double[10];
			coeffs[0] = matrix[0]; // x²
			coeffs[1] = matrix[1]; // y²
			coeffs[2] = matrix[2]; // z²
			coeffs[9] = matrix[3]; // constant

			coeffs[3] = 2 * matrix[4]; // xy
			coeffs[4] = 2 * matrix[5]; // xz
			coeffs[5] = 2 * matrix[6]; // yz
			coeffs[6] = 2 * matrix[7]; // x
			coeffs[7] = 2 * matrix[8]; // y
			coeffs[8] = 2 * matrix[9]; // z

			return kernel.buildImplicitEquation(coeffs, vars3D, false, true,
					'=',tpl);
		}

		return sbToValueString;
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	// ///////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	// ///////////////////////////////////////

	public Coords evaluatePoint(double u, double v) {

		Coords eigenRet;

		switch (type) {
		case QUADRIC_SPHERE:
			eigenRet = new Coords(Math.cos(u) * Math.cos(v), Math.sin(u)
					* Math.cos(v), Math.sin(v), 1);
			break;
		case QUADRIC_CONE:
			double v2 = Math.abs(v);
			eigenRet = new Coords(Math.cos(u) * v2, Math.sin(u) * v2, v, 1);
			break;
		case QUADRIC_CYLINDER:
			eigenRet = new Coords(Math.cos(u), Math.sin(u), v, 1);
			break;
		case QUADRIC_SINGLE_POINT:
			return getMidpoint3D();
			
		default:
			eigenRet = null;
			App.error(this+" has wrong type : "+type);
			break;
		}

		return eigenMatrix.mul(eigenRet);
	}

	public Coords evaluateNormal(double u, double v) {

		Coords n;

		switch (type) {
		case QUADRIC_SPHERE:
			return new Coords(Math.cos(u) * Math.cos(v), Math.sin(u)
					* Math.cos(v), Math.sin(v), 0);

		case QUADRIC_CONE:

			double r = getHalfAxis(0);
			double r2 = Math.sqrt(1 + r * r);
			if (v < 0)
				r = -r;

			n = getEigenvec3D(1).mul(Math.sin(u) / r2).add(
					getEigenvec3D(0).mul(Math.cos(u) / r2).add(
							getEigenvec3D(2).mul(-r / r2)));

			return n;

		case QUADRIC_CYLINDER:

			n = getEigenvec3D(1).mul(Math.sin(u)).add(
					getEigenvec3D(0).mul(Math.cos(u)));

			return n;

		default:
			return null;
		}

	}

	public double getMinParameter(int index) {

		switch (type) {
		case QUADRIC_SPHERE:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return -Math.PI / 2;
			}
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return 0;
			case 1: // v
				return Double.NEGATIVE_INFINITY;
			}

		default:
			return 0;
		}

	}

	public double getMaxParameter(int index) {

		switch (type) {
		case QUADRIC_SPHERE:
			switch (index) {
			case 0: // u
			default:
				return 2 * Math.PI;
			case 1: // v
				return Math.PI / 2;
			}

		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			switch (index) {
			case 0: // u
			default:
				return 2 * Math.PI;
			case 1: // v
				return Double.POSITIVE_INFINITY;
			}
		default:
			return 0;
		}
	}

	// /////////////////////////////////////////
	// GEOELEMENT3D INTERFACE
	// /////////////////////////////////////////


	@Override
	public Coords getLabelPosition() {
		return new Coords(4); // TODO
	}

	@Override
	public Coords getMainDirection() {
		// TODO create with parameter coord where is looked at
		return Coords.VZ;
	}

	

	// /////////////////////////////////////////////////
	// REGION 3D INTERFACE
	// /////////////////////////////////////////////////

	@Override
	public boolean isRegion() {
		return true;
	}

	protected Coords getNormalProjectionParameters(Coords coords) {

		Coords eigenCoords = eigenMatrix.solve(coords);
		double x = eigenCoords.getX();
		double y = eigenCoords.getY();
		double z = eigenCoords.getZ();

		double u, v, r;
		Coords parameters;

		switch (getType()) {
		case QUADRIC_SPHERE:
			u = Math.atan2(y, x);
			r = Math.sqrt(x * x + y * y);
			v = Math.atan2(z, r);

			parameters = new Coords(u, v);
			return parameters;

		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			u = Math.atan2(y, x);
			parameters = new Coords(u, z);
			return parameters;

		default:
			App.printStacktrace("TODO -- type: " + getType());
			return null;
		}
	}

	public Coords[] getNormalProjection(Coords coords) {

		Coords parameters = getNormalProjectionParameters(coords);

		if (parameters == null)
			return null;
		else
			return new Coords[] {
					getPoint(parameters.getX(), parameters.getY()), parameters };

	}

	public Coords getPoint(double u, double v) {
		return evaluatePoint(u, v);
	}

	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isInRegion(GeoPointND P) {

		return isInRegion(P.getCoordsInD(3));
	}
	
	/**
	 * 
	 * @param coords coords
	 * @return true if these coords lies on region
	 */
	public boolean isInRegion(Coords coords){
		// calc tP.S.P
		return Kernel.isZero(coords.transposeCopy()
				.mul(getSymetricMatrix().mul(coords)).get(1, 1));
	}

	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 * @param p
	 * @return direction from p to center (midpoint, or axis for cone,
	 *         cylinder...)
	 */
	private Coords getDirectionToCenter(Coords p) {
		switch (getType()) {
		case QUADRIC_SPHERE:
			return getMidpoint3D().sub(p);
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			Coords eigenCoords = eigenMatrix.solve(p);
			// project on eigen xOy plane
			Coords eigenDir = new Coords(eigenCoords.getX(),
					eigenCoords.getY(), 0, 0);
			return eigenMatrix.mul(eigenDir).normalized().mul(-1);
		default:
			return null;
		}
	}

	public void pointChangedForRegion(GeoPointND P) {
		
		GeoPoint3D p = (GeoPoint3D) P;
		
		if (type == QUADRIC_SINGLE_POINT){
			p.setCoords(getMidpoint3D(), false);
			p.updateCoords();
			return;
		}

		Coords willingCoords = p.getWillingCoords();
		if (willingCoords == null)
			willingCoords = P.getCoordsInD(3);
		else
			p.setWillingCoords(null);

		Coords willingDirection = p.getWillingDirection();
		if (willingDirection == null)
			willingDirection = getDirectionToCenter(willingCoords);
		else {
			// willingDirection = willingDirection.mul(-1); //to get the point
			// closest to the eye
			p.setWillingDirection(null);
		}
		// Application.debug("direction=\n"+willingDirection+"\ncoords=\n"+willingCoords);

		// compute intersection
		CoordMatrix qm = getSymetricMatrix();
		//App.debug("qm=\n"+qm);
		CoordMatrix pm = new CoordMatrix(4, 2);
		pm.setVx(willingDirection);
		pm.setOrigin(willingCoords);
		CoordMatrix pmt = pm.transposeCopy();

		// sets the solution matrix from line and quadric matrix
		CoordMatrix sm = pmt.mul(qm).mul(pm);

		//App.debug("sm=\n"+sm);
		double a = sm.get(1, 1);
		double b = sm.get(1, 2);
		double c = sm.get(2, 2);
		double Delta = b * b - a * c;

		double t;
		if (Delta >= 0) {
			double t1 = (-b - Math.sqrt(Delta)) / a;
			double t2 = (-b + Math.sqrt(Delta)) / a;
			t = Math.min(t1, t2);// gets the point closer to the willing coords

		} else {
			t = -b / a; // get closer point (in some "eigen coord sys")
		}

		Coords[] coords = getNormalProjection(willingCoords
				.add(willingDirection.mul(t)));

		RegionParameters rp = p.getRegionParameters();
		rp.setT1(coords[1].get(1));
		rp.setT2(coords[1].get(2));
		rp.setNormal(evaluateNormal(coords[1].get(1), coords[1].get(2)));
		p.setCoords(coords[0], false);
		p.updateCoords();

	}

	public void regionChanged(GeoPointND P) {
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(P)){
			pointChangedForRegion(P);
			return;
		}
		

		GeoPoint3D p = (GeoPoint3D) P;
		
		if (type == QUADRIC_SINGLE_POINT){
			p.setCoords(getMidpoint3D(), false);
			p.updateCoords();
			return;
		}

		RegionParameters rp = p.getRegionParameters();
		Coords coords = getPoint(rp.getT1(), rp.getT2());
		p.setCoords(coords, false);
		p.updateCoords();

	}

	// ///////////////////////////////////
	// TRANSFORMATIONS
	// ///////////////////////////////////

	public void translate(Coords v) {
		Coords m = getMidpoint3D();
		m.addInside(v);
		setMidpoint(m.get());
		
		// current symetric matrix
		CoordMatrix sm = getSymetricMatrix();
		// transformation matrix
		CoordMatrix tm = CoordMatrix.Identity(4);
		tm.setOrigin(m);
		// set new symetric matrix
		setMatrix((tm.transposeCopy()).mul(sm).mul(tm));

		// eigen matrix
		eigenMatrix.setOrigin(m);
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}
	
	
	
	

	public void rotate(NumberValue r, GeoPointND S) {
			
		rotate(CoordMatrix4x4.Rotation4x4(r.getDouble(), S.getInhomCoordsInD(3)));		
	}

	public void rotate(NumberValue r) {
		
		rotate(CoordMatrix4x4.Rotation4x4(r.getDouble()));
	}

	private void rotate(CoordMatrix4x4 tm) {
	
		// eigen matrix 
		eigenMatrix = tm.mul(eigenMatrix);

		// midpoint
		setMidpoint(eigenMatrix.getOrigin().get());

		// eigen vectors		
		for (int i = 0; i<3; i++){
			eigenvecND[i] = tm.mul(eigenvecND[i]);
		}

		// symetric matrix
		setMatrix((tm.transposeCopy()).mul(getSymetricMatrix()).mul(tm));
		
	}

	public void rotate(NumberValue r, GeoPointND S, GeoDirectionND orientation) {
		
		rotate(CoordMatrix4x4.Rotation4x4(orientation.getDirectionInD3().normalized(), r.getDouble(), S.getInhomCoordsInD(3)));	
	}

	public void rotate(NumberValue r, GeoLineND line) {

		rotate(CoordMatrix4x4.Rotation4x4(line.getDirectionInD3().normalized(), r.getDouble(), line.getStartInhomCoords()));	
		
	}

	
	
	
	////////////////////////
	// MIRROR
	////////////////////////
	
	public void mirror(Coords point) {

		// eigen matrix 
		eigenMatrix.mulInside(-1);
		eigenMatrix.addToOrigin(point.mul(2));

		// midpoint
		setMidpoint(eigenMatrix.getOrigin().get());

		// eigen vectors		
		for (int i = 0; i<3; i++){
			eigenvecND[i].mulInside(-1);
		}

		// symetric matrix
		setMatrixFromEigen();
	}

	public void mirror(GeoLineND line) {
		
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();
		
		// midpoint
		Coords mp = getMidpoint3D();
		Coords o1 = mp.projectLine(point, direction)[0]; 
		mp.mulInside(-1);
		mp.addInside(o1.mul(2));
		setMidpoint(mp.get());

		

		// eigen vectors		
		for (int i = 0; i<3; i++){
			Coords v = eigenvecND[i];
			double a = 2*v.dotproduct(direction);
			v.mulInside(-1);
			v.addInside(direction.mul(a));
		}

		// symetric matrix
		setMatrixFromEigen();
		
		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));
		
		
	}
	
	public void mirror(GeoPlane3D plane) {
		
		Coords vn = plane.getDirectionInD3().normalized();
		
		// midpoint
		Coords mp = getMidpoint3D();
		Coords o1 = mp.projectPlane(plane.getCoordSys().getMatrixOrthonormal())[0];
		mp.mulInside(-1);
		mp.addInside(o1.mul(2));
		setMidpoint(mp.get());

		

		// eigen vectors		
		for (int i = 0; i<3; i++){
			Coords v = eigenvecND[i];
			double a = -2*v.dotproduct(vn);
			v.addInside(vn.mul(a));
		}

		// symetric matrix
		setMatrixFromEigen();
		
		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));
		
		
	}
	
	////////////////////////
	// DILATE
	////////////////////////


	public void dilate(NumberValue rval, Coords S) {

		double r = rval.getDouble();


		// midpoint
		Coords mp = getMidpoint3D();
		mp.mulInside(r);
		mp.addInside(S.mul(1-r));
		setMidpoint(mp.get());
		
		if (r<0){
			// eigen vectors		
			for (int i = 0; i<3; i++){
				eigenvecND[i].mulInside(-1);
			}
			
			r = -r;
		}
		
		// half axis
		for (int i = 0; i<3; i++){
			halfAxes[i] *= r;
		}
		
		// diagonal
		switch (getType()) {
		case QUADRIC_SPHERE:
		case QUADRIC_CYLINDER:
			diagonal[3] *= r*r;
			break;
		case QUADRIC_CONE:
			diagonal[2] *= r*r;
			break;
		}

		// symetric matrix
		setMatrixFromEigen();
		
		// set eigen matrix
		setEigenMatrix(getHalfAxis(0), getHalfAxis(1), getHalfAxis(2));

		// volume
		volume *= r*r*r;
		
	}
	
	
	
	// ///////////////////////////////////
	// VOLUME
	// ///////////////////////////////////

	
	
	public double getVolume(){
		switch (getType()) {
		case QUADRIC_SPHERE:
			return volume;
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			//return Double.POSITIVE_INFINITY; //TODO ? (0 or infinity)
		default:
			return Double.NaN;
		}
	}
	
	public boolean hasFiniteVolume(){
		switch (getType()) {
		case QUADRIC_SPHERE:
			return isDefined();
		default:
			return false;
		}
	}
	
	@Override
	public void setUndefined() {
		super.setUndefined();
		volume = Double.NaN;
	}
	
	
	
	@Override
	final protected void singlePoint() {
		type = GeoQuadricNDConstants.QUADRIC_SINGLE_POINT;
		
	}
	
	
	@Override
	public boolean isGeoQuadric() {
		return true;
	}
	
	
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		/*
		sb.append("\t<eigenvectors ");
		sb.append(" x0=\"" + eigenvec[0].getX() + "\"");
		sb.append(" y0=\"" + eigenvec[0].getY() + "\"");
		sb.append(" z0=\"1.0\"");
		sb.append(" x1=\"" + eigenvec[1].getX() + "\"");
		sb.append(" y1=\"" + eigenvec[1].getY() + "\"");
		sb.append(" z1=\"1.0\"");
		sb.append("/>\n");
		*/

		// matrix must be saved after eigenvectors
		// as only <matrix> will cause a call to classifyConic()
		// see geogebra.io.MyXMLHandler: handleMatrix() and handleEigenvectors()
		sb.append("\t<matrix");
		for (int i = 0; i < 10; i++)
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		sb.append("/>\n");

		// implicit or specific mode
		/*
		switch (toStringMode) {
			case GeoConicND.EQUATION_SPECIFIC :
				sb.append("\t<eqnStyle style=\"specific\"/>\n");
				break;

			case GeoConicND.EQUATION_EXPLICIT :
				sb.append("\t<eqnStyle style=\"explicit\"/>\n");
				break;
				
			default :
				sb.append("\t<eqnStyle style=\"implicit\"/>\n");
		}
		*/

	}


}
