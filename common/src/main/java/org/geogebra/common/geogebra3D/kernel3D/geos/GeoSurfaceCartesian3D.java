package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.TreeMap;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.Matrix.CoordsDouble3;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.Feature;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for cartesian curves in 3D
 * 
 * @author Mathieu
 * 
 */
public class GeoSurfaceCartesian3D extends GeoSurfaceCartesianND
		implements Functional2Var, Traceable, CasEvaluableFunction, Region,
		MirrorableAtPlane, RotateableND {
	private boolean isSurfaceOfRevolutionAroundOx = false;
	private CoordMatrix4x4 tmpMatrix4x4;
	/**
	 * empty constructor (for ConstructionDefaults3D)
	 * 
	 * @param c
	 *            construction
	 */
	public GeoSurfaceCartesian3D(Construction c) {
		super(c);
		isSurfaceOfRevolutionAroundOx = false;
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param point
	 *            expression defining the surface
	 * @param fun
	 *            functions
	 */
	public GeoSurfaceCartesian3D(Construction c, ExpressionNode point,
			FunctionNVar fun[]) {
		super(c, point, fun);
		isSurfaceOfRevolutionAroundOx = false;
	}

	/**
	 * 
	 * @param surface
	 *            Surface to be copied
	 */
	public GeoSurfaceCartesian3D(GeoSurfaceCartesian3D surface) {
		super(surface.cons);
		set(surface);
	}

	private double[] tmp = new double[2];

	@Override
	public void evaluatePoint(double u, double v, Coords3 p) {
		tmp[0] = u;
		tmp[1] = v;
		p.set(fun[0].evaluate(tmp), fun[1].evaluate(tmp), fun[2].evaluate(tmp));
	}

	private Coords der1 = new Coords(3), der2 = new Coords(3),
			normal = new Coords(3);
	private CoordsDouble3 p1 = new CoordsDouble3(), p2 = new CoordsDouble3();

	private boolean setNormalFromNeighbours(Coords3 p, double u, double v,
			Coords3 n) {

		evaluatePoint(u + SurfaceEvaluable.NUMERICAL_DELTA, v, p1);
		if (!p1.isDefined()) {
			return false;
		}
		evaluatePoint(u, v + SurfaceEvaluable.NUMERICAL_DELTA, p2);
		if (!p2.isDefined()) {
			return false;
		}

		der1.setX(p1.x - p.getXd());
		der1.setY(p1.y - p.getYd());
		der1.setZ(p1.z - p.getZd());
		der2.setX(p2.x - p.getXd());
		der2.setY(p2.y - p.getYd());
		der2.setZ(p2.z - p.getZd());

		normal.setCrossProduct(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v, Coords3 n) {
		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1evaluate(0, i, tmp);
			if (Double.isNaN(val)) {
				return setNormalFromNeighbours(p, u, v, n);
			}
			der1.set(i + 1, val);

			val = fun1evaluate(1, i, tmp);
			if (Double.isNaN(val)) {
				return setNormalFromNeighbours(p, u, v, n);
			}
			der2.set(i + 1, val);
		}

		normal.setCrossProduct(der1, der2);
		n.setNormalizedIfPossible(normal);

		return true;

	}



	/**
	 * set the jacobian matrix for bivariate newton method
	 * 
	 * @param uv
	 *            parameter values
	 * @param vx
	 *            direction x
	 * @param vy
	 *            direction y
	 * @param vz
	 *            direction z
	 * @param matrix
	 *            output matrix
	 */
	public void setJacobianForBivariate(double[] uv, double vx, double vy,
			double vz, CoordMatrix matrix) {

		final double dfxu = fun1evaluate(0, 0, uv);
		final double dfyu = fun1evaluate(0, 1, uv);
		final double dfzu = fun1evaluate(0, 2, uv);
		final double dfxv = fun1evaluate(1, 0, uv);
		final double dfyv = fun1evaluate(1, 1, uv);
		final double dfzv = fun1evaluate(1, 2, uv);

		matrix.set(1, 1, vz * dfyu - vy * dfzu);
		matrix.set(1, 2, vz * dfyv - vy * dfzv);

		matrix.set(2, 1, vx * dfzu - vz * dfxu);
		matrix.set(2, 2, vx * dfzv - vz * dfxv);

	}

	/**
	 * set vector for bivariate newton method ie
	 * 
	 * vector = this(u,v) (X) v + c
	 * 
	 * @param uv
	 *            parameters
	 * @param xyz
	 *            helper array
	 * @param vx
	 *            x(v)
	 * @param vy
	 *            y(v)
	 * @param vz
	 *            z(v)
	 * @param cx
	 *            x(c)
	 * @param cy
	 *            y(c)
	 * @param cz
	 *            z(c)
	 * @param vector
	 *            output vector
	 */
	public void setVectorForBivariate(double[] uv, double[] xyz, double vx,
			double vy, double vz, double cx, double cy, double cz,
			Coords vector) {

		xyz[0] = fun[0].evaluate(uv);
		xyz[1] = fun[1].evaluate(uv);
		xyz[2] = fun[2].evaluate(uv);

		vector.setX(vz * xyz[1] - vy * xyz[2] + cx);
		vector.setY(vx * xyz[2] - vz * xyz[0] + cy);
		vector.setZ(vy * xyz[0] - vx * xyz[1] + cz);
	}

	@Override
	public GeoElement copy() {
		return new GeoSurfaceCartesian3D(this);
	}

	@Override
	public boolean isEqual(GeoElementND Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		GeoSurfaceCartesian3D geoSurface = (GeoSurfaceCartesian3D) geo;

		fun = new FunctionNVar[3];
		for (int i = 0; i < 3; i++) {
			fun[i] = new FunctionNVar(geoSurface.fun[i], kernel);
			// Application.debug(fun[i].toString());
		}

		fun1 = null;
		fun2 = null;

		startParam = geoSurface.startParam;
		endParam = geoSurface.endParam;
		isDefined = geoSurface.isDefined;

		isSurfaceOfRevolutionAroundOx = geoSurface.isSurfaceOfRevolutionAroundOx;

		// macro OUTPUT
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
			if (!geo.isIndependent()) {
				// this object is an output object of AlgoMacro
				// we need to check the references to all geos in its function's
				// expression
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				for (int i = 0; i < 3; i++) {
					algoMacro.initFunction(fun[i]);
				}
			}
		}

		// distFun = new ParametricCurveDistanceFunction(this);

	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SURFACECARTESIAN3D;
	}

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	@Override
	public Coords getMainDirection() {
		return Coords.VZ; // TODO
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	// /////////////////////////
	// FUNCTIONAL2VAR


	/**
	 * evaluate point at parameters u,v
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param p
	 *            point
	 */
	public void evaluatePoint(double u, double v, Coords p) {
		tmp[0] = u;
		tmp[1] = v;
		for (int i = 0; i < 3; i++) {
			p.set(i + 1, fun[i].evaluate(tmp));
		}
	}

	@Override
	public Coords evaluateNormal(double u, double v) {

		Coords n = new Coords(4);

		tmp[0] = u;
		tmp[1] = v;

		double val;
		for (int i = 0; i < 3; i++) {
			val = fun1evaluate(0, i, tmp);
			der1.set(i + 1, val);

			val = fun1evaluate(1, i, tmp);
			der2.set(i + 1, val);
		}

		n.setCrossProduct(der1, der2);
		n.normalize();
		return n;
	}

	// /////////////////////////
	// SPECIFIC XML

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// line style
		getLineStyleXML(sb);

		// level of detail
		if (getLevelOfDetail() == LevelOfDetail.QUALITY) {
			sb.append("\t<levelOfDetailQuality val=\"true\"/>\n");
		}

	}

	// /////////////////////////
	// LEVEL OF DETAIL

	private LevelOfDetail levelOfDetail = LevelOfDetail.SPEED;

	@Override
	public LevelOfDetail getLevelOfDetail() {
		return levelOfDetail;
	}

	@Override
	public void setLevelOfDetail(LevelOfDetail lod) {
		levelOfDetail = lod;
	}

	@Override
	public boolean hasLevelOfDetail() {
		return true;
	}

	// ////////////////
	// TRACE
	// ////////////////

	private boolean trace;

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f,
			boolean symbolic, MyArbitraryConstant arbconst) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVarString(StringTemplate tpl) {
		return fun[0].getVarString(tpl);
	}

	@Override
	public FunctionVariable[] getFunctionVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCasEvalMap(String string) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return cartesian coords as functions
	 */
	public FunctionNVar[] getFunctions() {
		return fun;
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced);

		if (geo.hasLevelOfDetail()) {
			levelOfDetail = ((SurfaceEvaluable) geo).getLevelOfDetail();
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC3D;
	}

	@Override
	public ExpressionValue evaluateSurface(double u, double v) {
		tmp[0] = u;
		tmp[1] = v;
		return new Geo3DVec(kernel, fun[0].evaluate(tmp), fun[1].evaluate(tmp),
				fun[2].evaluate(tmp));
	}

	@Override
	public boolean isRegion() {
		return isRegion3D();
	}

	@Override
	public boolean isRegion3D() {
		return kernel.getApplication()
				.has(Feature.PARAMETRIC_SURFACE_IS_REGION);
	}

	private double[] xyzuv;

	@Override
	public void pointChangedForRegion(GeoPointND P) {

		GeoPoint3D p = (GeoPoint3D) P;

		// use last hit parameters if exist
		if (hasLastHitParameters()) {
			RegionParameters rp = P.getRegionParameters();
			rp.setT1(lastHitU);
			rp.setT2(lastHitV);
			Coords c = Coords.createInhomCoorsInD3();
			evaluatePoint(lastHitU, lastHitV, c);
			setDerivatives();
			Coords n = evaluateNormal(lastHitU, lastHitV);
			rp.setNormal(n);
			p.setCoords(c, false);
			p.updateCoords();
			p.setWillingCoordsUndefined();
			p.setWillingDirectionUndefined();
			resetLastHitParameters();
			return;
		}

		Coords coords, direction;
		if (p.hasWillingCoords()) { // use willing coords
			coords = p.getWillingCoords();
		} else {
			// use real coords
			coords = p.getInhomCoordsInD3();
		}

		if (xyzuv == null) {
			xyzuv = new double[5];
		}

		// use willing direction if exist
		if (p.hasWillingDirection()) {
			direction = p.getWillingDirection();
			RegionParameters rp = p.getRegionParameters();

			if (getClosestParameters(rp.getT1(), rp.getT2(), coords.getX(),
					coords.getY(), coords.getZ(), direction.getX(),
					direction.getY(), direction.getZ(), xyzuv)) {

				rp.setT1(xyzuv[3]);
				rp.setT2(xyzuv[4]);
				Coords n = evaluateNormal(xyzuv[3], xyzuv[4]);
				rp.setNormal(n);
				p.setCoords(new Coords(xyzuv[0], xyzuv[1], xyzuv[2], 1), false);
				p.updateCoords();
			}

			p.setWillingCoordsUndefined();
			p.setWillingDirectionUndefined();
			resetLastHitParameters();
			return;
		}

		// find closest point, looking for zero normal
		getClosestParameters(coords.getX(), coords.getY(), coords.getZ(),
				xyzuv);

		RegionParameters rp = p.getRegionParameters();
		rp.setT1(xyzuv[3]);
		rp.setT2(xyzuv[4]);
		Coords n = evaluateNormal(xyzuv[3], xyzuv[4]);
		rp.setNormal(n);
		p.setCoords(new Coords(xyzuv[0], xyzuv[1], xyzuv[2], 1), false);
		p.updateCoords();
		p.setWillingCoordsUndefined();
		p.setWillingDirectionUndefined();
		resetLastHitParameters();

	}



	/**
	 * calc closest point to line (x0,y0,z0) (vx,vy,vz) with (uold, vold) start
	 * parameters
	 * 
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param vx
	 * @param vy
	 * @param vz
	 * @param xyzuv1
	 * 
	 * @return true if found
	 */
	private boolean getClosestParameters(double uold, double vold, double x0,
			double y0, double z0, double vx, double vy, double vz,
			double[] xyzuv1) {

		// check (uold,vold) are correct starting parameters
		if (Double.isNaN(uold) || Double.isNaN(vold)) {
			return false;
		}

		// set derivatives if needed
		setSecondDerivatives();

		// create fields if needed
		if (xyz == null) {
			xyz = new double[3];
		}

		if (xyzDu == null) {
			xyzDu = new double[3];
			xyzDv = new double[3];
			xyzDuu = new double[3];
			xyzDuv = new double[3];
			xyzDvu = new double[3];
			xyzDvv = new double[3];
			uv = new double[2];
		}

		// init to no solution
		xyzuv1[0] = Double.NaN;

		// make several tries
		uv[0] = uold;
		uv[1] = vold;
		if (findMinimumDistanceGradient(x0, y0, z0, vx, vy, vz, uv)) {
			xyzuv1[0] = xyz[0];
			xyzuv1[1] = xyz[1];
			xyzuv1[2] = xyz[2];
			xyzuv1[3] = uv[0];
			xyzuv1[4] = uv[1];
			// Log.debug(">>> " + xyzuv[0] + "," + xyzuv[1] + "," + xyzuv[2]);
			return true;
		}

		return false;

	}

	private static final int GRADIENT_JUMPS = 100;

	// private static final int GRADIENT_SAMPLES = 8;

	private boolean findMinimumDistanceGradient(double x0, double y0, double z0,
			double vx, double vy, double vz, double[] uvOut) {

		for (int i = 0; i < GRADIENT_JUMPS; i++) {
			// calc current f(u,v) point
			xyz[0] = fun[0].evaluate(uvOut);
			xyz[1] = fun[1].evaluate(uvOut);
			xyz[2] = fun[2].evaluate(uvOut);

			// calculate derivatives values
			xyzDu[0] = fun1evaluate(0, 0, uvOut);
			xyzDu[1] = fun1evaluate(0, 1, uvOut);
			xyzDu[2] = fun1evaluate(0, 2, uvOut);

			xyzDv[0] = fun1evaluate(1, 0, uvOut);
			xyzDv[1] = fun1evaluate(1, 1, uvOut);
			xyzDv[2] = fun1evaluate(1, 2, uvOut);

			xyzDuu[0] = fun2evaluate(0, 0, 0, uvOut);
			xyzDuu[1] = fun2evaluate(0, 0, 1, uvOut);
			xyzDuu[2] = fun2evaluate(0, 0, 2, uvOut);

			xyzDuv[0] = fun2evaluate(1, 0, 0, uvOut);
			xyzDuv[1] = fun2evaluate(1, 0, 1, uvOut);
			xyzDuv[2] = fun2evaluate(1, 0, 2, uvOut);

			xyzDvu[0] = fun2evaluate(0, 1, 0, uvOut);
			xyzDvu[1] = fun2evaluate(0, 1, 1, uvOut);
			xyzDvu[2] = fun2evaluate(0, 1, 2, uvOut);

			xyzDvv[0] = fun2evaluate(1, 1, 0, uvOut);
			xyzDvv[1] = fun2evaluate(1, 1, 1, uvOut);
			xyzDvv[2] = fun2evaluate(1, 1, 2, uvOut);

			// we want to minimize (x,y,z)-to-line distance,
			// i.e. norm of vector:
			// (xyz[2] - z0) * vx - (xyz[0] - x0) * vz;
			// (xyz[0] - x0) * vy - (xyz[1] - y0) * vx;
			// (xyz[1] - y0) * vz - (xyz[2] - z0) * vy;

			// help values
			double nx = (xyz[2] - z0) * vx - (xyz[0] - x0) * vz;
			double ny = (xyz[0] - x0) * vy - (xyz[1] - y0) * vx;
			double nz = (xyz[1] - y0) * vz - (xyz[2] - z0) * vy;
			double nxDu = xyzDu[2] * vx - xyzDu[0] * vz;
			double nyDu = xyzDu[0] * vy - xyzDu[1] * vx;
			double nzDu = xyzDu[1] * vz - xyzDu[2] * vy;
			double nxDv = xyzDv[2] * vx - xyzDv[0] * vz;
			double nyDv = xyzDv[0] * vy - xyzDv[1] * vx;
			double nzDv = xyzDv[1] * vz - xyzDv[2] * vy;

			// calc gradient /2
			double gu = nxDu * nx // nx
					+ nyDu * ny // ny
					+ nzDu * nz; // nz
			double gv = nxDv * nx // nx
					+ nyDv * ny // ny
					+ nzDv * nz; // nz

			// calc Hessien /2
			double huu = (xyzDuu[2] * vx - xyzDuu[0] * vz) * nx // nx
					+ (xyzDuu[0] * vy - xyzDuu[1] * vx) * ny // ny
					+ (xyzDuu[1] * vz - xyzDuu[2] * vy) * nz // nz
					+ nxDu * nxDu // nx
					+ nyDu * nyDu // ny
					+ nzDu * nzDu; // nz
			double huv = (xyzDuv[2] * vx - xyzDuv[0] * vz) * nx // nx
					+ (xyzDuv[0] * vy - xyzDuv[1] * vx) * ny // ny
					+ (xyzDuv[1] * vz - xyzDuv[2] * vy) * nz // nz
					+ nxDu * nxDv // nx
					+ nyDu * nyDv // ny
					+ nzDu * nzDv; // nz
			double hvv = (xyzDvv[2] * vx - xyzDvv[0] * vz) * nx // nx
					+ (xyzDvv[0] * vy - xyzDvv[1] * vx) * ny // ny
					+ (xyzDvv[1] * vz - xyzDvv[2] * vy) * nz // nz
					+ nxDv * nxDv // nx
					+ nxDv * nyDv // ny
					+ nxDv * nzDv; // nz
			double hvu = (xyzDvu[2] * vx - xyzDvu[0] * vz) * nx // nx
					+ (xyzDvu[0] * vy - xyzDvu[1] * vx) * ny // ny
					+ (xyzDvu[1] * vz - xyzDvu[2] * vy) * nz // nz
					+ nxDu * nxDv // nx
					+ nyDu * nyDv // ny
					+ nzDu * nzDv; // nz

			// Hessien * gradient
			double Hgu = huu * gu + hvu * gv;
			double Hgv = huv * gu + hvv * gv;

			// best step: gradient*gradient/(gradient * (Hessien * gradient))
			double gnorm = gu * gu + gv * gv;
			double d = gnorm / (2 * (gu * Hgu + gv * Hgv));

			// new u,v
			double du = d * gu;
			double dv = d * gv;
			uvOut[0] -= du;
			uvOut[1] -= dv;

			// back to interval if needed
			if (uvOut[0] < getMinParameter(0)) {
				uvOut[0] = getMinParameter(0);
			} else if (uvOut[0] > getMaxParameter(0)) {
				uvOut[0] = getMaxParameter(0);
			}
			if (uvOut[1] < getMinParameter(1)) {
				uvOut[1] = getMinParameter(1);
			} else if (uvOut[1] > getMaxParameter(1)) {
				uvOut[1] = getMaxParameter(1);
			}

			if (Kernel.isZero(gnorm)) {
				return true;
			}

		}

		return false;

	}

	/**
	 * find best point on surface colinear to (x0,y0,z0) point in (vx,vy,vz)
	 * direction
	 * 
	 * @param x0
	 *            origin x
	 * @param xMax
	 *            max x value
	 * @param y0
	 *            origin y
	 * @param z0
	 *            origin z
	 * @param vx
	 *            vector x
	 * @param vy
	 *            vector y
	 * @param vz
	 *            vector z
	 * @param vSquareNorm
	 *            vector square norm
	 * @param xyzuvOut
	 *            (x,y,z,u,v) best point coords and parameters
	 * @return true if point found
	 */
	public boolean getBestColinear(double x0, double xMax, double y0, double z0,
			double vx, double vy, double vz, double vSquareNorm,
			double[] xyzuvOut) {
		if (jacobian == null) {
			jacobian = new CoordMatrix(2, 2);
			bivariateVector = new Coords(3);
			bivariateDelta = new Coords(2);
			uv = new double[2];
			xyz = new double[3];

		}

		// we use bivariate newton method:
		// A(x0,y0,z0) and B(x1,y1,z1) delimits the hitting segment
		// M(u,v) is a point on the surface
		// we want vector product AM*AB to equal 0, so A, B, M are colinear
		// we only check first and second values of AM*AB since third will
		// be a consequence

		double gxc = z0 * vy - vz * y0;
		double gyc = x0 * vz - vx * z0;
		double gzc = y0 * vx - vy * x0;

		double uMin, uMax;
		if (isSurfaceOfRevolutionAroundOx) {
			uMin = x0;
			uMax = xMax;
		} else {
			uMin = getMinParameter(0);
			uMax = getMaxParameter(0);
		}

		double vMin = getMinParameter(1);
		double vMax = getMaxParameter(1);

		double finalError = Double.NaN;
		double dotProduct = -1;

		// make several tries
		double du = (uMax - uMin) / BIVARIATE_SAMPLES;
		double dv = (vMax - vMin) / BIVARIATE_SAMPLES;
		for (int ui = 0; ui <= BIVARIATE_SAMPLES; ui++) {
			uv[0] = uMin + ui * du;
			for (int vi = 0; vi <= BIVARIATE_SAMPLES; vi++) {
				uv[1] = vMin + vi * dv;
				double error = findBivariateColinear(x0, y0, z0, vx, vy, vz,
						vSquareNorm, gxc, gyc, gzc, uv);
				if (!Double.isNaN(error)) {
					// check if the hit point is in the correct direction
					double d = (xyz[0] - x0) * vx + (xyz[1] - y0) * vy
							+ (xyz[2] - z0) * vz;
					if (d >= 0) {
						if (dotProduct < 0 || d < dotProduct) {
							dotProduct = d;
							finalError = error;
							xyzuvOut[0] = xyz[0];
							xyzuvOut[1] = xyz[1];
							xyzuvOut[2] = xyz[2];
							xyzuvOut[3] = uv[0];
							xyzuvOut[4] = uv[1];
						}
					}
				}

			}

		}

		return !Double.isNaN(finalError);
	}

	private double findBivariateColinear(final double x0, final double y0,
			final double z0, final double vx, final double vy, final double vz,
			final double vSquareNorm, final double gxc, final double gyc,
			final double gzc, double[] uvParams) {

		for (int i = 0; i < BIVARIATE_JUMPS; i++) {

			// calc angle vector between hitting direction and hitting
			// origin-point on surface
			setVectorForBivariate(uvParams, xyz, vx, vy, vz, gxc, gyc, gzc,
					bivariateVector);

			double dx = xyz[0] - x0;
			double dy = xyz[1] - y0;
			double dz = xyz[2] - z0;
			double d = dx * dx + dy * dy + dz * dz;
			double error = bivariateVector.dotproduct3(bivariateVector);

			// check if sin(angle)^2 is small enough, then stop
			if (error < Kernel.STANDARD_PRECISION * vSquareNorm * d) {
				return error;
			}

			// set jacobian matrix and solve it
			setJacobianForBivariate(uvParams, vx, vy, vz, jacobian);
			jacobian.pivotDegenerate(bivariateDelta, bivariateVector);

			// if no solution, dismiss
			if (!bivariateDelta.isDefined()) {
				return Double.NaN;
			}

			// calc new parameters
			uvParams[0] -= bivariateDelta.getX();
			uvParams[1] -= bivariateDelta.getY();

			// check bounds
			randomBackInIntervalsIfNeeded(uvParams);

		}

		return Double.NaN;
	}



	@Override
	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * reset last hitted parameters
	 */
	public void resetLastHitParameters() {
		hasLastHitParameters = false;
	}

	private boolean hasLastHitParameters = false;

	private boolean hasLastHitParameters() {
		return hasLastHitParameters;
	}

	private double lastHitU, lastHitV;

	/**
	 * set last hit u,v parameters
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 */
	public void setLastHitParameters(double u, double v) {
		lastHitU = u;
		lastHitV = v;
		hasLastHitParameters = true;
	}

	/**
	 * 
	 * @return true if surface of revolution around Ox by definition
	 */
	@Override
	public boolean isSurfaceOfRevolutionAroundOx() {
		return isSurfaceOfRevolutionAroundOx;
	}

	/**
	 * set this to be a surface of revolution around Ox
	 * 
	 * @param flag
	 *            flag
	 */
	public void setIsSurfaceOfRevolutionAroundOx(boolean flag) {
		isSurfaceOfRevolutionAroundOx = flag;
	}

	@Override
	public boolean showLineProperties() {
		return true;
	}

	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	@Override
	public void setLineThicknessOrVisibility(final int th) {
		setLineThickness(th);
	}

	@Override
	public void printCASEvalMapXML(StringBuilder sb) {
		// fun.printCASevalMapXML(sb);
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> map) {
		// TODO
	}

	/**
	 * @param fun
	 *            array of coordinate functions
	 */
	public void setFun(FunctionNVar[] fun) {
		this.fun = fun;
		this.fun1 = null;
		this.fun2 = null;

	}

	public void mirror(GeoLineND line) {
		SurfaceTransform.mirror(fun, kernel, line);

	}

	public void mirror(GeoCoordSys2D plane) {
		SurfaceTransform.mirror(fun, kernel, plane);

	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, tmpMatrix4x4);

	}

	@Override
	public void rotate(NumberValue r) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, tmpMatrix4x4);

	}

	@Override
	public void rotate(NumberValue r, GeoPointND S,
			GeoDirectionND orientation) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}
		SurfaceTransform.rotate(fun, kernel, r, S, orientation, tmpMatrix4x4);
	}

	// private void transform(CoordMatrix4x4 m) {
	//
	// SurfaceTransform.transform(fun, kernel, m);
	//
	// }

	@Override
	public void rotate(NumberValue r, GeoLineND line) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = new CoordMatrix4x4();
		}

		SurfaceTransform.rotate(fun, kernel, r, line, tmpMatrix4x4);

	}

}
