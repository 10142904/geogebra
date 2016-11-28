/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.PointRotateable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;

/**
 *
 * @author Markus
 */
public class AlgoRotatePoint extends AlgoTransformation implements
		SymbolicParametersBotanaAlgo {

	private GeoPointND Q;
	private PointRotateable out;
	private NumberValue angle;
	private GeoElement inGeo, outGeo, angleGeo;

	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

	/**
	 * Creates new point rotation algo
	 */
	AlgoRotatePoint(Construction cons, String label, GeoElement A,
			GeoNumberValue angle, GeoPointND Q) {
		this(cons, A, angle, Q);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new unlabeled point rotation algo
	 */
	public AlgoRotatePoint(Construction cons, GeoElement A,
			GeoNumberValue angle,
			GeoPointND Q) {
		super(cons);
		this.angle = angle;
		this.Q = Q;

		angleGeo = angle.toGeoElement();
		inGeo = A;

		outGeo = getResultTemplate(inGeo);
		if (outGeo instanceof PointRotateable)
			out = (PointRotateable) outGeo;

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction())
			cons.registerEuclidianViewCE(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.Rotate;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ROTATE_BY_ANGLE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = inGeo;
		input[1] = angleGeo;
		input[2] = (GeoElement) Q;

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the rotated point
	 * 
	 * @return rotated point
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	// calc rotated point
	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo instanceof GeoFunction) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else
			outGeo.set(inGeo);
		if (!outGeo.isDefined()) {
			return;
		}
		out.rotate(angle, Q);
		if (inGeo.isLimitedPath())
			this.transformLimitedPath(inGeo, outGeo);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-25
		// simplified to allow better Chinese translation
		return getLoc().getPlain("ARotatedByAngleB", inGeo.getLabel(tpl),
				angleGeo.getLabel(tpl));
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList))
			out = (PointRotateable) outGeo;
	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction)
			return new GeoCurveCartesian(cons);
		return super.getResultTemplate(geo);
	}

	@Override
	public double getAreaScaleFactor() {
		return 1;
	}

	public Variable[] getBotanaVars(GeoElementND geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		// point to rotate
		GeoPoint A = (GeoPoint) Q;
		// point around the rotation is processed
		GeoPoint B = (GeoPoint) input[0];

		if (A != null && B != null) {
			Variable[] vA = A.getBotanaVars(A);
			Variable[] vB = B.getBotanaVars(B);

			if (botanaVars == null) {
				botanaVars = new Variable[8];
				// A' - rotation point
				botanaVars[0] = new Variable();
				botanaVars[1] = new Variable();
				// A - point around the rotation is processed
				botanaVars[2] = vA[0];
				botanaVars[3] = vA[1];
				// B - point to rotate
				botanaVars[4] = vB[0];
				botanaVars[5] = vB[1];
				// t1 = sqrt(3)
				botanaVars[6] = new Variable();
				// t2 = sqrt(2)
				botanaVars[7] = new Variable();
			}

			double angleDoubleVal = angle.getDouble();

			Polynomial a1 = new Polynomial(vA[0]);
			Polynomial a2 = new Polynomial(vA[1]);
			Polynomial b1 = new Polynomial(vB[0]);
			Polynomial b2 = new Polynomial(vB[1]);
			Polynomial a_1 = new Polynomial(botanaVars[0]);
			Polynomial a_2 = new Polynomial(botanaVars[1]);
			Polynomial t1 = new Polynomial(botanaVars[6]);
			Polynomial t2 = new Polynomial(botanaVars[7]);

			// rotate by 0 degrees
			if (Kernel.isEqual(angleDoubleVal, 0)) {
				botanaPolynomials = new Polynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b1).add(a1);
				botanaPolynomials[1] = a_2.subtract(a2).subtract(b2).add(a2);
				return botanaPolynomials;
			}
			// rotate by 180 or -180 degrees
			else if (Kernel.isEqual(angleDoubleVal, Math.PI)
					|| Kernel.isEqual(angleDoubleVal, -Math.PI)) {
				botanaPolynomials = new Polynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).add(b1).subtract(a1);
				botanaPolynomials[1] = a_2.subtract(a2).add(b2).subtract(a2);
				return botanaPolynomials;
			}
			// rotate by 90 degrees
			else if (Kernel.isEqual(angleDoubleVal, Math.PI / 2)) {
				botanaPolynomials = new Polynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = a_2.subtract(a2).add(b1).subtract(a1);
				return botanaPolynomials;
			}
			// rotate by -90 degrees
			else if (Kernel.isEqual(angleDoubleVal, -Math.PI / 2)) {
				botanaPolynomials = new Polynomial[2];
				botanaPolynomials[0] = a_1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[1] = a_2.subtract(a2).subtract(b1).add(a1);
				return botanaPolynomials;
			}
			// rotate by 30 degrees
			else if (Kernel.isEqual(angleDoubleVal, Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b2)
						.add(a2);
				Polynomial p2 = b1.subtract(a1);
				Polynomial p3 = t1.multiply(p2);
				botanaPolynomials[1] = p1.subtract(p3);
				Polynomial p4 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b1)
						.subtract(a1);
				Polynomial p5 = b2.subtract(a2);
				Polynomial p6 = t1.multiply(p5);
				botanaPolynomials[2] = p4.subtract(p6);
				return botanaPolynomials;
			}
			// rotate by -30 degrees
			else if (Kernel.isEqual(angleDoubleVal, -Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1)
						.subtract(new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b2)
						.subtract(a2);
				Polynomial p2 = b1.subtract(a1);
				Polynomial p3 = t1.multiply(p2);
				botanaPolynomials[1] = p1.subtract(p3);
				Polynomial p4 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b1)
						.add(a1);
				Polynomial p5 = b2.subtract(a2);
				Polynomial p6 = t1.multiply(p5);
				botanaPolynomials[2] = p4.subtract(p6);
				return botanaPolynomials;
			}
			// rotate by -45 degrees
			else if (Kernel.isEqual(angleDoubleVal, -Math.PI / 4)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t2.multiply(t2).subtract(new Polynomial(2));
				Polynomial p1 = new Polynomial(2).multiply(a_1).subtract(
						new Polynomial(2).multiply(a1));
				Polynomial p2 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[1] = p1.subtract(
						t2.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2).subtract(
						new Polynomial(2).multiply(a2));
				Polynomial p4 = b2.subtract(a2).subtract(b1).add(a1);
				botanaPolynomials[2] = p3.subtract(
						t2.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 45 degrees
			else if (Kernel.isEqual(angleDoubleVal, Math.PI / 4)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t2.multiply(t2).subtract(new Polynomial(2));
				Polynomial p1 = new Polynomial(2).multiply(a_1).subtract(
						new Polynomial(2).multiply(a1));
				Polynomial p2 = b1.subtract(a1).subtract(b2).add(a2);
				botanaPolynomials[1] = p1.subtract(
						t2.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2).subtract(
						new Polynomial(2).multiply(a2));
				Polynomial p4 = b1.subtract(a1).add(b2).subtract(a2);
				botanaPolynomials[2] = p3.subtract(t2.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 60 degrees
			else if (Kernel.isEqual(angleDoubleVal, Math.PI / 3)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b1)
						.add(a1);
				Polynomial p2 = b2.subtract(a2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b2)
						.add(a2);
				Polynomial p4 = a1.subtract(b1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by -60 degrees
			else if (Kernel.isEqual(angleDoubleVal, -Math.PI / 3)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b1)
						.add(a1);
				Polynomial p2 = a2.subtract(b2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b2)
						.add(a2);
				Polynomial p4 = b1.subtract(a1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 120 degrees
			else if (Kernel.isEqual(angleDoubleVal, 2 * Math.PI / 3)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b1)
						.subtract(a1);
				Polynomial p2 = b2.subtract(a2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b2)
						.subtract(a2);
				Polynomial p4 = a1.subtract(b1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by -120 degrees
			else if (Kernel.isEqual(angleDoubleVal, -2 * Math.PI / 3)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b1)
						.subtract(a1);
				Polynomial p2 = a2.subtract(b2);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b2)
						.subtract(a2);
				Polynomial p4 = b1.subtract(a1);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 150 degrees
			else if (Kernel.isEqual(angleDoubleVal, 5 * Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b2)
						.add(a2);
				Polynomial p2 = a1.subtract(b1);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b1)
						.subtract(a1);
				Polynomial p4 = a2.subtract(b2);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by -150 degrees
			else if (Kernel.isEqual(angleDoubleVal, -5 * Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b2)
						.subtract(a2);
				Polynomial p2 = a1.subtract(b1);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b1)
						.add(a1);
				Polynomial p4 = a2.subtract(b2);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by 210 degrees
			else if (Kernel.isEqual(angleDoubleVal, 7 * Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).add(b2)
						.subtract(a2);
				Polynomial p2 = a1.subtract(b1);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).subtract(b1)
						.add(a1);
				Polynomial p4 = a2.subtract(b2);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// rotate by -210 degrees
			else if (Kernel.isEqual(angleDoubleVal, -7 * Math.PI / 6)) {
				botanaPolynomials = new Polynomial[3];
				botanaPolynomials[0] = t1.multiply(t1).subtract(
						new Polynomial(3));
				Polynomial p1 = new Polynomial(2).multiply(a_1)
						.subtract(new Polynomial(2).multiply(a1)).subtract(b2)
						.add(a2);
				Polynomial p2 = a1.subtract(b1);
				botanaPolynomials[1] = p1.subtract(t1.multiply(p2));
				Polynomial p3 = new Polynomial(2).multiply(a_2)
						.subtract(new Polynomial(2).multiply(a2)).add(b1)
						.subtract(a1);
				Polynomial p4 = a2.subtract(b2);
				botanaPolynomials[2] = p3.subtract(t1.multiply(p4));
				return botanaPolynomials;
			}
			// unhandled angle
			throw new NoSymbolicParametersException();

		}
		throw new NoSymbolicParametersException();
	}

	

}
