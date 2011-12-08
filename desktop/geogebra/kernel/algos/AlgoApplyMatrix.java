/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoApplyMatrix.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.kernel.MatrixTransformable;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoConicPart;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoPolyLineInterface;

/**
 * 
 * @author Markus
 * @version
 */
public class AlgoApplyMatrix extends AlgoTransformation {

	private MatrixTransformable out;
	private GeoElement inGeo, outGeo;
	private final GeoList matrix;

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 * @param label
	 * @param in
	 * @param matrix
	 */
	public AlgoApplyMatrix(Construction cons, String label, GeoElement in,
			GeoList matrix) {
		this(cons, in, matrix);
		outGeo.setLabel(label);
	}

	/**
	 * Creates new apply matrix algorithm
	 * 
	 * @param cons
	 * @param in
	 * @param matrix
	 */
	public AlgoApplyMatrix(Construction cons, GeoElement in, GeoList matrix) {
		super(cons);

		this.matrix = matrix;

		inGeo = in.toGeoElement();
		if ((inGeo instanceof GeoPolyLineInterface) || inGeo.isLimitedPath()) {
			outGeo = in.copyInternal(cons);
			out = (MatrixTransformable) outGeo;
		} else if (inGeo.isGeoList()) {
			outGeo = new GeoList(cons);
		} else if (inGeo instanceof GeoFunction) {
			out = new GeoCurveCartesian(cons);
			outGeo = (GeoElement) out;
		} else {
			out = (MatrixTransformable) inGeo.copy();
			outGeo = out.toGeoElement();
		}

		setInputOutput();
		compute();
		if (inGeo.isGeoFunction()) {
			cons.registerEuclidianViewCE(this);
		}
	}

	@Override
	public String getClassName() {
		return "AlgoApplyMatrix";
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[1] = inGeo;
		input[0] = matrix;

		setOutputLength(1);
		setOutput(0, outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the resulting element
	 * 
	 * @return resulting element
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	public final void compute() {
		if (inGeo.isGeoList()) {
			transformList((GeoList) inGeo, (GeoList) outGeo);
			return;
		}
		if (inGeo.isGeoFunction()) {
			((GeoFunction) inGeo)
					.toGeoCurveCartesian((GeoCurveCartesian) outGeo);
		} else {
			outGeo.set(inGeo);
		}
		MyList list = matrix.getMyList();

		if ((list.getMatrixCols() != list.getMatrixRows())
				|| (list.getMatrixRows() < 2) || (list.getMatrixRows() > 3)) {
			outGeo.setUndefined();
			return;
		}

		double a, b, c, d, e, f, g, h, i;
		if (list.getMatrixRows() < 3) {
			a = ((NumberValue) (MyList.getCell(list, 0, 0).evaluate()))
					.getDouble();
			b = ((NumberValue) (MyList.getCell(list, 1, 0).evaluate()))
					.getDouble();
			c = ((NumberValue) (MyList.getCell(list, 0, 1).evaluate()))
					.getDouble();
			d = ((NumberValue) (MyList.getCell(list, 1, 1).evaluate()))
					.getDouble();
			out.matrixTransform(a, b, c, d);
		} else {
			a = ((NumberValue) (MyList.getCell(list, 0, 0).evaluate()))
					.getDouble();
			b = ((NumberValue) (MyList.getCell(list, 1, 0).evaluate()))
					.getDouble();
			c = ((NumberValue) (MyList.getCell(list, 2, 0).evaluate()))
					.getDouble();
			d = ((NumberValue) (MyList.getCell(list, 0, 1).evaluate()))
					.getDouble();
			e = ((NumberValue) (MyList.getCell(list, 1, 1).evaluate()))
					.getDouble();
			f = ((NumberValue) (MyList.getCell(list, 2, 1).evaluate()))
					.getDouble();
			g = ((NumberValue) (MyList.getCell(list, 0, 2).evaluate()))
					.getDouble();
			h = ((NumberValue) (MyList.getCell(list, 1, 2).evaluate()))
					.getDouble();
			i = ((NumberValue) (MyList.getCell(list, 2, 2).evaluate()))
					.getDouble();
			out.matrixTransform(a, b, c, d, e, f, g, h, i);
		}
		if (inGeo.isLimitedPath()) {
			this.transformLimitedPath(inGeo, outGeo);
		}

	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(out instanceof GeoList)
				&& (outGeo instanceof MatrixTransformable)) {
			out = (MatrixTransformable) outGeo;
		}

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction) {
			return new GeoCurveCartesian(cons);
		}
		return super.getResultTemplate(geo);
	}

	@Override
	public boolean swapOrientation(boolean posOrientation) {
		double a, b, c, d;
		a = ((NumberValue) (matrix.get(0, 0))).getDouble();
		b = ((NumberValue) (matrix.get(1, 0))).getDouble();
		c = ((NumberValue) (matrix.get(0, 1))).getDouble();
		d = ((NumberValue) (matrix.get(1, 1))).getDouble();
		return posOrientation ^ (((a * d) - (b * c)) < 0);
	}

	@Override
	protected void transformLimitedPath(GeoElement a, GeoElement b) {
		if (!(a instanceof GeoConicPart)) {
			super.transformLimitedPath(a, b);
		} else {
			super.transformLimitedConic(a, b);
		}

	}

}
