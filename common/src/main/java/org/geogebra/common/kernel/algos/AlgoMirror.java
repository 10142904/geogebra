/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMirrorPointPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.ConicMirrorable;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPoly;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.Mirrorable;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoMirror extends AlgoTransformation implements
		RestrictionAlgoForLocusEquation, SymbolicParametersBotanaAlgo {

	protected Mirrorable out;
	protected GeoElement inGeo;
	protected GeoElement outGeo;
	private GeoLineND mirrorLine;
	protected GeoPointND mirrorPoint;
	private GeoConic mirrorConic;
	protected GeoElement mirror;

	private GeoPoint transformedPoint;

	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

	/**
	 * Creates new "mirror at point" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param p
	 */
	protected AlgoMirror(Construction cons, String label, GeoElement in,
			GeoPointND p) {

		this(cons, in, p);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at point" algo
	 * 
	 * @param cons
	 * @param in
	 * @param p
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoPointND p) {

		this(cons);
		mirrorPoint = p;
		endOfConstruction(cons, in, (GeoElement) p);
	}

	/**
	 * Creates new "mirror at conic" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param c
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoConic c) {

		this(cons, in, c);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at conic" algo
	 * 
	 * @param cons
	 * @param in
	 * @param c
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoConic c) {

		this(cons);
		mirrorConic = c;
		endOfConstruction(cons, in, c);
	}

	/**
	 * Creates new "mirror at line" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param g
	 */
	AlgoMirror(Construction cons, String label, GeoElement in, GeoLineND g) {

		this(cons, in, g);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new "mirror at line" algo
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param g
	 */
	public AlgoMirror(Construction cons, GeoElement in, GeoLineND g) {

		this(cons);
		mirrorLine = g;
		endOfConstruction(cons, in, (GeoElement) g);
	}

	/**
	 * used for 3D
	 * 
	 * @param cons
	 *            cons
	 */
	protected AlgoMirror(Construction cons) {
		super(cons);
	}

	/**
	 * end of construction
	 * 
	 * @param cons
	 *            cons
	 * @param in
	 *            transformed geo
	 * @param mirror
	 *            mirror
	 */
	public void endOfConstruction(Construction cons, GeoElement in,
			GeoElement mirror) {

		this.mirror = mirror;

		inGeo = in;
		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof Mirrorable)
			out = (Mirrorable) outGeo;
		setInputOutput();

		cons.registerEuclidianViewCE(this);
		transformedPoint = new GeoPoint(cons);
		compute();
		if (inGeo.isGeoFunction())
			cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mirror;
	}

	@Override
	public int getRelatedModeID() {
		if (mirror.isGeoLine()) {
			return EuclidianConstants.MODE_MIRROR_AT_LINE;
		} else if (mirror.isGeoPoint()) {
			return EuclidianConstants.MODE_MIRROR_AT_POINT;
		} else {
			return EuclidianConstants.MODE_MIRROR_AT_CIRCLE;
		}

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = inGeo;
		input[1] = mirror;

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the transformed geo
	 * 
	 * @return transformed geo
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	public final void compute() {

		if (!mirror.isDefined()) {
			outGeo.setUndefined();
			return;
		}

		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}

		setOutGeo();
		if (!outGeo.isDefined()) {
			return;
		}

		if (inGeo.isRegion() && mirror == mirrorConic) {
			GeoVec2D v = mirrorConic.getTranslationVector();
			outGeo.setInverseFill(((Region) inGeo).isInRegion(v.getX(),
					v.getY())
					^ inGeo.isInverseFill());
		}

		computeRegardingMirror();

		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	/**
	 * compute regarding which mirror type is used
	 */
	protected void computeRegardingMirror() {
		if (mirror == mirrorLine) {
			if (mirrorLine.getStartPoint() == null) {
				mirrorLine.setStandardStartPoint();
			}
			out.mirror(mirrorLine);
		} else if (mirror == mirrorPoint) {
			if (outGeo.isGeoFunction()) {
				((GeoFunction) outGeo).mirror(getMirrorCoords());
			} else {
				out.mirror(getMirrorCoords());
			}
		} else
			((ConicMirrorable) out).mirror(mirrorConic);

	}

	/**
	 * set inGeo to outGeo
	 */
	protected void setOutGeo() {
		if (mirror instanceof GeoConic && inGeo instanceof GeoLine) {
			((GeoLine) inGeo).toGeoConic((GeoConic) outGeo);
		}
		/*
		 * else if(mirror instanceof GeoConic && geoIn instanceof GeoConic &&
		 * geoOut instanceof GeoCurveCartesian){
		 * ((GeoConic)geoIn).toGeoCurveCartesian((GeoCurveCartesian)geoOut); }
		 */
		else if (mirror instanceof GeoConic && inGeo instanceof GeoConic
				&& outGeo instanceof GeoImplicit) {
			((GeoConic) inGeo).toGeoImplicitPoly((GeoImplicit) outGeo);
		} else if (inGeo instanceof GeoFunction && mirror != mirrorPoint) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else if (inGeo instanceof GeoPoly && mirror == mirrorConic) {
			((GeoPoly) inGeo).toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else
			outGeo.set(inGeo);
	}

	/**
	 * 
	 * @return inhom coords for mirror point
	 */
	protected Coords getMirrorCoords() {
		return mirrorPoint.getInhomCoords();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlain("AMirroredAtB", inGeo.getLabel(tpl),
				mirror.getLabel(tpl));

	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList))
			out = (Mirrorable) outGeo;

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if ((geo instanceof GeoPoly) && mirror == mirrorConic)
			return new GeoCurveCartesian(cons);
		if ((geo instanceof GeoFunction) && mirror != mirrorPoint)
			return new GeoCurveCartesian(cons);
		if (geo.isLimitedPath() && mirror == mirrorConic)
			return new GeoConicPart(cons, GeoConicPart.CONIC_PART_ARC);
		if (mirror instanceof GeoConic && geo instanceof GeoLine) {
			return new GeoConic(cons);
		}
		if (mirror instanceof GeoConic
				&& geo instanceof GeoConic
				&& (!((GeoConic) geo).isCircle() || !((GeoConic) geo)
						.keepsType()))
			return new GeoImplicitPoly(cons);
		if (geo instanceof GeoPoly
				|| (geo.isLimitedPath() && mirror != mirrorConic))
			return copyInternal(cons, geo);
		if (geo.isGeoList())
			return new GeoList(cons);
		return copy(geo);
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (mirror != mirrorConic) {
			super.transformLimitedPath(a, b);
			return;
		}

		GeoConicPart arc = (GeoConicPart) b;
		arc.setParameters(0, 6.28, true);
		if (a instanceof GeoRay) {
			transformedPoint.removePath();
			setTransformedObject(((GeoRay) a).getStartPoint(), transformedPoint);
			compute();
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(arc.getPointParam(0.5), transformedPoint);
			compute();
			if (!((GeoRay) a).isOnPath(transformedPoint,
					Kernel.STANDARD_PRECISION))
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);

			setTransformedObject(a, b);
		} else if (a instanceof GeoSegment) {
			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(((GeoSegment) a).getStartPoint(),
					transformedPoint);
			compute();
			// if start point itself is on path, transformed point may have
			// wrong path param #2306
			transformedPoint.removePath();
			arc.pathChanged(transformedPoint);
			double d = transformedPoint.getPathParameter().getT();

			arc.setParameters(0, Kernel.PI_2, true);
			transformedPoint.removePath();
			setTransformedObject(((GeoSegment) a).getEndPoint(),
					transformedPoint);
			compute();

			arc.pathChanged(transformedPoint);
			double e = transformedPoint.getPathParameter().getT();
			arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, true);
			transformedPoint.removePath();
			transformedPoint.setCoords(mirrorConic.getTranslationVector());
			if (arc.isOnPath(transformedPoint, Kernel.STANDARD_PRECISION))
				arc.setParameters(d * Kernel.PI_2, e * Kernel.PI_2, false);
			setTransformedObject(a, b);
		}
		if (a instanceof GeoConicPart) {
			transformLimitedConic(a, b);
		}
	}

	@Override
	public boolean swapOrientation(GeoConicPartND arc) {
		if (arc == null) {
			return true;
		} else if (mirror != mirrorConic || !(arc instanceof GeoConicPart)) {
			return arc.positiveOrientation();
		}
		GeoVec2D arcCentre = ((GeoConicPart) arc).getTranslationVector();
		GeoVec2D mirrorCentre = mirrorConic.getTranslationVector();
		double dist = MyMath.length(arcCentre.getX() - mirrorCentre.getX(),
				arcCentre.getY() - mirrorCentre.getY());
		return !Kernel.isGreater(dist, ((GeoConicPart) arc).halfAxes[0]);
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}

	public EquationElementInterface buildEquationElementForGeo(GeoElement geo,
			EquationScopeInterface scope) {
		return LocusEquation.eqnMirror(geo, this, scope);
	}

	@Override
	public double getAreaScaleFactor() {
		return -1;
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_LINE) {

			GeoPoint P, Q;
			// if we want to mirror a line to a line
			if (inGeo.isGeoLine()) {
				P = ((GeoLine) inGeo).startPoint;
				Q = ((GeoLine) inGeo).endPoint;
				GeoLine l = (GeoLine) mirrorLine;

				if (P != null && Q != null && l != null) {
					Variable[] vP = P.getBotanaVars(P);
					Variable[] vQ = Q.getBotanaVars(Q);
					Variable[] vL = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[8];
						// P' - mirror of P
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// Q' - mirror of Q
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// V1 - auxiliary point
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// V2 - auxiliary point
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
					}

					botanaPolynomials = new Polynomial[8];

					// first we want to mirror P to line l

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial v1_1 = new Polynomial(botanaVars[4]);
					Polynomial v1_2 = new Polynomial(botanaVars[5]);
					Polynomial p_1 = new Polynomial(botanaVars[0]);
					Polynomial p_2 = new Polynomial(botanaVars[1]);

					// PV1 = V1P'
					botanaPolynomials[0] = v1_1.multiply(new Polynomial(2))
							.subtract(p1).subtract(p_1);
					botanaPolynomials[1] = v1_2.multiply(new Polynomial(2))
							.subtract(p2).subtract(p_2);

					Variable[] A = new Variable[2];
					// A - start point of mirrorLine
					A[0] = vL[0];
					A[1] = vL[1];
					Variable[] B = new Variable[2];
					// B - end point of mirrorLine
					B[0] = vL[2];
					B[1] = vL[3];

					// A, V1, B collinear
					botanaPolynomials[2] = Polynomial.collinear(A[0], A[1],
							botanaVars[4], botanaVars[5], B[0], B[1]);

					// PV1 orthogonal AB
					botanaPolynomials[3] = Polynomial.perpendicular(vP[0],
							vP[1], botanaVars[4], botanaVars[5], A[0], A[1],
							B[0], B[1]);

					// second we want to mirror Q to line l

					Polynomial q1 = new Polynomial(vQ[0]);
					Polynomial q2 = new Polynomial(vQ[1]);
					Polynomial v2_1 = new Polynomial(botanaVars[6]);
					Polynomial v2_2 = new Polynomial(botanaVars[7]);
					Polynomial q_1 = new Polynomial(botanaVars[2]);
					Polynomial q_2 = new Polynomial(botanaVars[3]);

					// QV2 = V2Q'
					botanaPolynomials[4] = v2_1.multiply(new Polynomial(2))
							.subtract(q1).subtract(q_1);
					botanaPolynomials[5] = v2_2.multiply(new Polynomial(2))
							.subtract(q2).subtract(q_2);

					// A, V2, B collinear
					botanaPolynomials[6] = Polynomial.collinear(A[0], A[1],
							botanaVars[6], botanaVars[7], B[0], B[1]);

					// QV2 orthogonal AB
					botanaPolynomials[7] = Polynomial.perpendicular(vQ[0],
							vQ[1], botanaVars[6], botanaVars[7], A[0], A[1],
							B[0], B[1]);

					return botanaPolynomials;
				}
			}
			// we want to mirror a point to a line
			else if (inGeo.isGeoPoint()) {
				P = (GeoPoint) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

				if (P != null && l != null) {
					Variable[] vP = P.getBotanaVars(P);
					Variable[] vL = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[6];
						// C'
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// V
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// N
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
					}

					botanaPolynomials = new Polynomial[6];

					Polynomial v1 = new Polynomial(botanaVars[2]);
					Polynomial v2 = new Polynomial(botanaVars[3]);
					Polynomial c1 = new Polynomial(vP[0]);
					Polynomial c2 = new Polynomial(vP[1]);
					Polynomial c_1 = new Polynomial(botanaVars[0]);
					Polynomial c_2 = new Polynomial(botanaVars[1]);

					// CV = VC'
					botanaPolynomials[0] = v1.multiply(new Polynomial(2))
						.subtract(c_1).subtract(c1);
					botanaPolynomials[1] = v2.multiply(new Polynomial(2))
						.subtract(c_2).subtract(c2);

					// points of mirrorLine
					Variable[] A = new Variable[2];
					A[0] = vL[0];
					A[1] = vL[1];
					Variable[] B = new Variable[2];
					B[0] = vL[2];
					B[1] = vL[3];

					// A,V,B collinear
					botanaPolynomials[2] = Polynomial.collinear(A[0], A[1],
							B[0], B[1], botanaVars[2], botanaVars[3]);

					Polynomial a1 = new Polynomial(A[0]);
					Polynomial a2 = new Polynomial(A[1]);
					Polynomial b1 = new Polynomial(B[0]);
					Polynomial b2 = new Polynomial(B[1]);
					Polynomial n1 = new Polynomial(botanaVars[4]);
					Polynomial n2 = new Polynomial(botanaVars[5]);

					// CV orthogonal AB
					botanaPolynomials[3] = b1.subtract(a1).add(c2).subtract(n2);
					botanaPolynomials[4] = c1.subtract(b2).add(a2).subtract(n1);

					// C',N,V collinear
					botanaPolynomials[5] = Polynomial.collinear(botanaVars[0],
						botanaVars[1], botanaVars[2], botanaVars[3],
						botanaVars[4], botanaVars[5]);

					return botanaPolynomials;
				}
			}
			// mirror circle to line
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoLine l = (GeoLine) mirrorLine;

				if (circle != null && l != null) {
					Variable[] vCircle = circle.getBotanaVars(circle);
					Variable[] vl = l.getBotanaVars(l);

					if (botanaVars == null) {
						botanaVars = new Variable[12];
						// mirror of circle
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// mirror of point on circle
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
						// V - midpoint of center and mirror of center
						botanaVars[4] = new Variable();
						botanaVars[5] = new Variable();
						// T - midpoint of point on circle and mirror of point
						// on circle
						botanaVars[6] = new Variable();
						botanaVars[7] = new Variable();
						// N1 - AN1 orthogonal CD
						botanaVars[8] = new Variable();
						botanaVars[9] = new Variable();
						// N2 - BN2 orthogonal CD
						botanaVars[10] = new Variable();
						botanaVars[11] = new Variable();
					}

					botanaPolynomials = new Polynomial[12];

					Polynomial v1 = new Polynomial(botanaVars[4]);
					Polynomial v2 = new Polynomial(botanaVars[5]);
					Polynomial a1 = new Polynomial(vCircle[0]);
					Polynomial a2 = new Polynomial(vCircle[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);

					// AV = VA'
					botanaPolynomials[0] = v1.multiply(new Polynomial(2))
							.subtract(a_1).subtract(a1);
					botanaPolynomials[1] = v2.multiply(new Polynomial(2))
							.subtract(a_2).subtract(a2);

					// C, V, D collinear
					botanaPolynomials[2] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[4], botanaVars[5], vl[2], vl[3]);

					Polynomial c1 = new Polynomial(vl[0]);
					Polynomial c2 = new Polynomial(vl[1]);
					Polynomial d1 = new Polynomial(vl[2]);
					Polynomial d2 = new Polynomial(vl[3]);
					Polynomial n1_1 = new Polynomial(botanaVars[8]);
					Polynomial n1_2 = new Polynomial(botanaVars[9]);

					// AV orthogonal CD
					botanaPolynomials[3] = d1.subtract(c1).add(a2)
							.subtract(n1_2);
					botanaPolynomials[4] = a1.subtract(d2).add(c2)
							.subtract(n1_1);

					// A', V, N1 collinear
					botanaPolynomials[5] = Polynomial.collinear(botanaVars[0],
							botanaVars[1], botanaVars[4], botanaVars[5],
							botanaVars[8], botanaVars[9]);

					Polynomial t1 = new Polynomial(botanaVars[6]);
					Polynomial t2 = new Polynomial(botanaVars[7]);
					Polynomial b1 = new Polynomial(vCircle[2]);
					Polynomial b2 = new Polynomial(vCircle[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// BT = TB'
					botanaPolynomials[6] = t1.multiply(new Polynomial(2))
							.subtract(b_1).subtract(b1);
					botanaPolynomials[7] = t2.multiply(new Polynomial(2))
							.subtract(b_2).subtract(b2);

					// C, T, D collinear
					botanaPolynomials[8] = Polynomial.collinear(vl[0], vl[1],
							botanaVars[6], botanaVars[7], vl[2], vl[3]);

					Polynomial n2_1 = new Polynomial(botanaVars[10]);
					Polynomial n2_2 = new Polynomial(botanaVars[11]);

					// BT orthogonal CD
					botanaPolynomials[9] = d1.subtract(c1).add(b2)
							.subtract(n2_2);
					botanaPolynomials[10] = b1.subtract(d2).add(c2)
							.subtract(n2_1);

					// B', T, N2 collinear
					botanaPolynomials[11] = Polynomial.collinear(botanaVars[1],
							botanaVars[2], botanaVars[6], botanaVars[7],
							botanaVars[10], botanaVars[11]);

					return botanaPolynomials;
				}
			}
			
		throw new NoSymbolicParametersException();
			
		}
		// case mirroring GeoElement about point
		else if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_POINT) {

			// mirror point about point
			if (inGeo.isGeoPoint()) {
				GeoPoint P1 = (GeoPoint) inGeo;
				GeoPoint P2 = (GeoPoint) mirrorPoint;

				if (P1 != null && P2 != null) {
					Variable[] vP1 = P1.getBotanaVars(P1);
					Variable[] vP2 = P2.getBotanaVars(P2);

					if (botanaVars == null) {
						botanaVars = new Variable[2];
						// P1'
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
					}

					botanaPolynomials = new Polynomial[2];
				
					Polynomial a1 = new Polynomial(vP1[0]);
					Polynomial a2 = new Polynomial(vP1[1]);
					Polynomial b1 = new Polynomial(vP2[0]);
					Polynomial b2 = new Polynomial(vP2[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);

					// AB = BA'
					botanaPolynomials[0] = b1.multiply(new Polynomial(2))
						.subtract(a1).subtract(a_1);
					botanaPolynomials[1] = b2.multiply(new Polynomial(2))
						.subtract(a2).subtract(a_2);

					return botanaPolynomials;
				}
			}
			// mirror line about point
			else if (inGeo.isGeoLine()) {
				GeoLine l = (GeoLine) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (l != null && P != null) {
					Variable[] vl = l.getBotanaVars(l);
					Variable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// mirror of start point
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// mirror of end point
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial a1 = new Polynomial(vl[0]);
					Polynomial a2 = new Polynomial(vl[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);
					Polynomial b1 = new Polynomial(vl[2]);
					Polynomial b2 = new Polynomial(vl[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1).subtract(
							a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2).subtract(
							a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1).subtract(
							b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2).subtract(
							b_2.subtract(p2));

					return botanaPolynomials;
				}
			} 
			// mirror circle about point
			else if (inGeo.isGeoConic() && ((GeoConic) inGeo).isCircle()) {
				GeoConic circle = (GeoConic) inGeo;
				GeoPoint P = (GeoPoint) mirrorPoint;

				if (circle != null && P != null) {
					Variable[] vCircle = circle.getBotanaVars(circle);
					Variable[] vP = P.getBotanaVars(P);

					if (botanaVars == null) {
						botanaVars = new Variable[4];
						// mirror of center
						botanaVars[0] = new Variable();
						botanaVars[1] = new Variable();
						// mirror of point on the circle
						botanaVars[2] = new Variable();
						botanaVars[3] = new Variable();
					}

					botanaPolynomials = new Polynomial[4];

					Polynomial p1 = new Polynomial(vP[0]);
					Polynomial p2 = new Polynomial(vP[1]);
					Polynomial a1 = new Polynomial(vCircle[0]);
					Polynomial a2 = new Polynomial(vCircle[1]);
					Polynomial a_1 = new Polynomial(botanaVars[0]);
					Polynomial a_2 = new Polynomial(botanaVars[1]);
					Polynomial b1 = new Polynomial(vCircle[2]);
					Polynomial b2 = new Polynomial(vCircle[3]);
					Polynomial b_1 = new Polynomial(botanaVars[2]);
					Polynomial b_2 = new Polynomial(botanaVars[3]);

					// AP vector = PA' vector
					botanaPolynomials[0] = p1.subtract(a1).subtract(
							a_1.subtract(p1));
					botanaPolynomials[1] = p2.subtract(a2).subtract(
							a_2.subtract(p2));

					// BP vector = PB' vector
					botanaPolynomials[2] = p1.subtract(b1).subtract(
							b_1.subtract(p1));
					botanaPolynomials[3] = p2.subtract(b2).subtract(
							b_2.subtract(p2));

					return botanaPolynomials;
				}

			}
			throw new NoSymbolicParametersException();

		} else if (getRelatedModeID() == EuclidianConstants.MODE_MIRROR_AT_CIRCLE) {

			GeoPoint P = (GeoPoint) inGeo;
			GeoConic c = (GeoConic) mirror;

			if (P != null && c != null) {
				Variable[] vP = P.getBotanaVars(P);
				Variable[] vc = c.getBotanaVars(c);

				if (botanaVars == null) {
					botanaVars = new Variable[8];
					// B'
					botanaVars[0] = new Variable();
					botanaVars[1] = new Variable();
					// B
					botanaVars[2] = vP[0];
					botanaVars[3] = vP[1];
					// O
					botanaVars[4] = vc[0];
					botanaVars[5] = vc[1];
					// A
					botanaVars[6] = vc[2];
					botanaVars[7] = vc[3];
				}

				botanaPolynomials = new Polynomial[2];

				Polynomial o1 = new Polynomial(vc[0]);
				Polynomial o2 = new Polynomial(vc[1]);
				Polynomial a1 = new Polynomial(vc[2]);
				Polynomial a2 = new Polynomial(vc[3]);
				Polynomial b1 = new Polynomial(vP[0]);
				Polynomial b2 = new Polynomial(vP[1]);
				Polynomial b_1 = new Polynomial(botanaVars[0]);
				Polynomial b_2 = new Polynomial(botanaVars[1]);

				// r^2
				Polynomial oa = (a1.subtract(o1)).multiply(a1.subtract(o1))
						.add((a2.subtract(o2)).multiply(a2.subtract(o2)));
				// (x-x_0)^2 + (y-y_0)^2
				Polynomial denominator = (b1.subtract(o1)).multiply(
						b1.subtract(o1)).add(
						(b2.subtract(o2)).multiply(b2.subtract(o2)));

				// formula for the coordinates of inverse point
				// from: http://mathworld.wolfram.com/Inversion.html
				botanaPolynomials[0] = oa.multiply(b1.subtract(o1)).add(
						(o1.subtract(b_1)).multiply(denominator));

				botanaPolynomials[1] = oa.multiply(b2.subtract(o2)).add(
						(o2.subtract(b_2)).multiply(denominator));

				return botanaPolynomials;

			}
			throw new NoSymbolicParametersException();

		} else {
			throw new NoSymbolicParametersException();
		}
	}
}
