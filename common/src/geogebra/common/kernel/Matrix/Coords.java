/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

/**
 * 
 * A Ggb3DVector is composed of {x1,x2,...,xn} coordinates in double precision.
 * This class provides methods for basic linear algebra calculus.
 * 
 * @author ggb3D
 * 
 */
public class Coords {

	private double norm, sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;
	
	/** origin 3D vector */
	public static final Coords O = new Coords(0,0,0,1);
	/** vx 3D vector */
	public static final Coords VX = new Coords(1,0,0,0);
	/** vy 3D vector */
	public static final Coords VY = new Coords(0,1,0,0);
	/** vz 3D vector */
	public static final Coords VZ = new Coords(0,0,1,0);
	/** vz 3D vector, down orientation */
	public static final Coords VZm = new Coords(0,0,-1,0);
	/** undefined vector */
	public static final Coords UNDEFINED = new Coords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);
	
	public double[] val;
	
	private int rows;
	
	/**
	 * 
	 * @return (x,y,z,1) coords
	 */
	public static final Coords createInhomCoorsInD3(){
		Coords ret = new Coords(4);
		ret.setW(1);
		return ret;
	}



	// /////////////////////////////////////////////////:
	// Constructors

	/**
	 * creates a vector of the dimension specified by rows.
	 * 
	 * @param rows
	 *            number of rows
	 */
	public Coords(int rows) {

		this.rows = rows;
		//transpose = false;

		val = new double[rows];
		/*
		for (int i = 0; i < rows; i++) {
			val[i] = 0.0;
		}
		*/

	}

	/**
	 * creates a vector with values vals
	 * 
	 * @param vals
	 *            values {x1, x2, ...}
	 */
	public Coords(double[] vals) {

		this(vals.length);

		for (int i = 0; i < vals.length; i++)
			val[i] = vals[i];

	}
	
	/**
	 * creates a vector with same values as v
	 * @param v vector
	 */
	public Coords(Coords v){
		this(v.val);
	}

	/**
	 * creates a 2D vector with the specified values
	 * 
	 * @param u
	 * @param v
	 */
	public Coords(double u, double v) {
		this(2);
		val[0] = u;
		val[1] = v;
	}

	/**
	 * creates a 3D vector with the specified values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Coords(double x, double y, double z) {
		this(3);
		val[0] = x;
		val[1] = y;
		val[2] = z;
	}

	final static public Coords Coords4DLastEqualTo0(double x, double y, double z) {
		Coords ret = new Coords(4);
		ret.val[0] = x;
		ret.val[1] = y;
		ret.val[2] = z;
		return ret;
	}

	/**
	 * creates a 3D vector/point with the specified values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public Coords(double x, double y, double z, double w) {
		this(4);
		val[0] = x;
		val[1] = y;
		val[2] = z;
		val[3] = w;
	}

	// /////////////////////////////////////////////////:
	// setters and getters
	/**
	 * sets v(i) to val0
	 * 
	 * @param i
	 *            number of the row
	 * @param val0
	 *            value
	 */
	public void set(int i, double val0) {
		val[i - 1] = val0;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets v to vals0
	 * 
	 * @param vals0
	 *            values {x1, x2, ...}
	 */
	public void set(double[] vals0) {
		// Application.debug("-------------val.length = "+val.length+"\n-------------vals0.length = "+vals0.length);
		for (int i = 0; i < vals0.length; i++)
			val[i] = vals0[i];

		calcNorm = calcSqNorm = true;
	}
	
	/**
	 * set this values to v's
	 * @param v coords
	 * @param length length first values only are updated
	 */
	public void setValues(Coords v, int length){
		for (int i = 0; i < length; i++){
			val[i] = v.val[i];
		}
	}
	
	/**
	 * set values from v
	 * @param v coords
	 */
	public void set(Coords v) {
		set(v.val);
	}
	

	public void set(double val0) {
		for (int i = 0; i < rows; i++) {
			val[i] = val0;
		}
		norm = Math.sqrt(rows)*Math.abs(val0);
		calcNorm = calcSqNorm = true;
	}

	/**
	 * returns v(i)
	 * 
	 * @param i
	 *            number of the row
	 * @return value
	 */
	public double get(int i) {
		return val[i - 1];

	}
	
	/**
	 * @param ret copy of this
	 * 
	 */
	public void copy(double[] ret){
		for (int i = 0 ; i < rows ; i++){
			ret[i] = val[i];
		}
	}

	/**
	 * returns v "x-coord"
	 * 
	 * @return x-coord
	 */
	public double getX() {
		return val[0];
	}

	/**
	 * returns v "y-coord"
	 * 
	 * @return y-coord
	 */
	public double getY() {
		return val[1];
	}

	/**
	 * returns v "z-coord"
	 * 
	 * @return z-coord
	 */
	public double getZ() {
		if (val.length > 2) {
			return val[2];
		}
		return 0; // z coord for 2D points
	}

	/**
	 * returns v "w-coord"
	 * 
	 * @return w-coord
	 */
	public double getW() {
		return val[3];
	}

	/**
	 * returns v last coord
	 * 
	 * @return last coord
	 */
	public double getLast() {
		return val[rows - 1];
	}

	/**
	 * sets the "x-coord"
	 * 
	 * @param val
	 */
	public void setX(double val) {
		this.val[0] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "y-coord"
	 * 
	 * @param val
	 */
	public void setY(double val) {
		this.val[1] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "z-coord"
	 * 
	 * @param val
	 */
	public void setZ(double val) {
		this.val[2] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "w-coord"
	 * 
	 * @param val
	 */
	public void setW(double val) {
		this.val[3] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * returns number of rows of the vector
	 * 
	 * @return number of rows
	 */
	public int getLength() {

		return rows;

	}

	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	public Coords copyVector() {

		Coords result = new Coords(rows);
		for (int i = 0; i < rows; i++)
			result.val[i] = val[i];

		return result;

	}

	/**
	 * returns the start-end subvector
	 * 
	 * @param start
	 *            number of starting row
	 * @param end
	 *            number of end row
	 * @return vector with rows between start and end
	 */
	public Coords subVector(int start, int end) {
		int r = end - start + 1;
		Coords result = new Coords(r);

		for (int i = 0; i < r; i++)
			result.val[i] = val[start + i - 1];

		return result;

	}

	/**
	 * returns the subvector composed of this without the row number row
	 * 
	 * @param row
	 *            number of the row to remove
	 * @return vector composed of this without the row number row
	 */
	public Coords subVector(int row) {
		int r = rows;
		Coords result = new Coords(r - 1);

		int shift = 0;
		for (int i = 0; i < r; i++) {
			if (i == row)
				shift = 1;
			else
				result.val[i] = val[i + shift];
		}

		return result;

	}

	// /////////////////////////////////////////////////:
	// basic operations

	/**
	 * returns dot product this * v.
	 * <p>
	 * If this={x1,x2,...} and v={x'1,x'2,...}, the dot product is
	 * x1*x'1+x2*x'2+...
	 * 
	 * @param v
	 *            vector multiplied with
	 * @return value of the dot product
	 */
	public double dotproduct(Coords v) {
		int len = Math.min(getLength(), v.getLength());
		double res = 0;
		for (int i = 0; i < len; i++)
			res += val[i] * v.val[i];
		return res;
	}

	/**
	 * returns cross product this * v. Attempt that the two vectors are of
	 * dimension 3.
	 * <p>
	 * If this={x,y,z} and v={x',y',z'}, then cross
	 * product={yz'-y'z,zx'-z'x,xy'-yx'}
	 * 
	 * @param v
	 *            vector multiplied with
	 * @return vector resulting of the cross product
	 */
	final public Coords crossProduct(Coords v) {

		Coords ret = new Coords(3);

		ret.setCrossProduct(this, v);

		return ret;
	}

	/**
	 * 
	 * @param v
	 * @return 4-length vector equal to cross product this ^ v
	 */
	final public Coords crossProduct4(Coords v) {

		Coords ret = new Coords(4);

		ret.setCrossProduct(this, v);

		return ret;
	}

	/**
	 * set x,y,z values according to v1 ^ v2 cross product
	 * 
	 * @param v1
	 * @param v2
	 */
	final public void setCrossProduct(Coords v1, Coords v2) {
		val[0] = v1.val[1] * v2.val[2] - v1.val[2] * v2.val[1];
		val[1] = v1.val[2] * v2.val[0] - v1.val[0] * v2.val[2];
		val[2] = v1.val[0] * v2.val[1] - v1.val[1] * v2.val[0];
	}

	/**
	 * returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...). Same result as
	 * Math.sqrt(this.dotproduct(this))
	 * 
	 * @return the scalar norm
	 */
	public double norm() {
		if (calcNorm) {
			calcNorm();
			calcNorm = false;
		}
		return norm;
	}

	/**
	 * calc the norm
	 */
	public void calcNorm() {
		norm = Math.sqrt(this.dotproduct(this));
	}

	/**
	 * The norm must be already calculated by calcNorm()
	 * 
	 * @return the norm
	 */
	public double getNorm() {
		return norm;
	}

	/**
	 * returns the square of the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=x1*x1+x2*x2+... Same result as
	 * this.dotproduct(this)
	 * 
	 * @return the scalar norm
	 */
	public double squareNorm() {
		if (calcSqNorm) {
			sqNorm = this.dotproduct(this);
			calcSqNorm = false;
		}
		return sqNorm;
	}

	/**
	 * returns this normalized WARNING : recalc the norm
	 * 
	 * @return this normalized
	 */
	public Coords normalized() {
		return normalized(false);
	}

	/**
	 * returns this normalized WARNING : recalc the norm
	 * 
	 * @param checkOneDirection
	 *            check if one of the result coord is near to 1 (for Kernel)
	 * @return this normalized
	 */
	public Coords normalized(boolean checkOneDirection) {
		Coords ret = new Coords(getLength());
		calcNorm();
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++) {
			double v = val[i] * normInv;
			// check if v is near to be one direction vector
			if (checkOneDirection && Kernel.isEqual(Math.abs(v), 1)) {
				if (v < 0)
					ret.val[i] = -1;
				else
					ret.val[i] = 1;
				for (int j = 0; j < i; j++)
					ret.val[j] = 0;
				for (int j = i + 1; j < len; j++)
					ret.val[j] = 0;
				break;
			}
			ret.val[i] = v;
		}
		return ret;
	}
	
	/**
	 * put this normalized in ret (WARNING : recalc the norm)
	 * @param ret 
	 * 
	 */
	public void normalized(Coords ret) {
		calcNorm();
		double normInv = 1 / getNorm();
		for (int i = 0; i < ret.rows; i++) {
			double v = val[i] * normInv;
			ret.val[i] = v;
		}
	}

	/**
	 * WARNING : recalc the norm set this to norm=1
	 * 
	 * @return this normalized
	 */
	public Coords normalize() {

		normalize(true);

		return this;
	}

	/**
	 * 
	 * @param recalcNorm
	 *            says if the norm has to be recalculated
	 * @return this normalized
	 */
	public Coords normalize(boolean recalcNorm) {

		if (recalcNorm)
			calcNorm();
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++)
			val[i] *= normInv;

		norm = sqNorm = 1.0;

		return this;
	}

	/**
	 * returns the distance between this and v
	 * 
	 * @param v
	 *            second vector
	 * @return (this-v).norm()
	 */
	public double distance(Coords v) {

		return this.sub(v).norm();
	}
	
	/**
	 * 
	 * Calc square distance to v - only on x, y, z coords
	 * 
	 * @param v coords
	 * @return square distance
	 */
	public double squareDistance3(Coords v){
		
		double x = getX() - v.getX();
		double y = getY() - v.getY();
		double z = getZ() - v.getZ();
		
		return x*x + y*y + z*z;
		
	}

	/**
	 * returns the shortest vector between this and a 3D-line represented by the matrix
	 * {V O}
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return shortest vector between this and the line
	 */
	private Coords vectorToLine(Coords O, Coords V) {

		Coords OM = this.sub(O);
		Coords N = V.normalized();
		Coords OH = N.mul(OM.dotproduct(N)); // TODO optimize
		return OM.sub(OH);
		
	}

	/**
	 * returns the distance between this and a 3D-line represented by the matrix
	 * {V O}
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return distance between this and the line
	 */
	public double distLine(Coords O, Coords V) {

		return vectorToLine(O, V).norm();
	}
	
	/**
	 * returns the square distance between this and a 3D-line represented by the matrix
	 * {V O} (only computed on x, y, z)
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return distance between this and the line
	 */
	public double squareDistLine3(Coords O, Coords V) {

		Coords v = vectorToLine(O, V);
		return v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ();
	}
	
	
	
	/**
	 * 
	 * @param o point of the plane
	 * @param vn normal direction to the plane
	 * @return distance of this to the plane
	 */
	public double distPlane(Coords o, Coords vn){
		return Math.abs(distPlaneOriented(o, vn));
	}
	
	/**
	 * 
	 * @param o point of the plane
	 * @param vn normal direction to the plane
	 * @return oriented distance of this to the plane
	 */
	public double distPlaneOriented(Coords o, Coords vn){
		return this.sub(o).dotproduct(vn);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction). If direction is parallel to the plane, return
	 * infinite point (direction vector).
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 *         
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3 the direction used for projection
	 * 
	 */
	public void projectPlane(CoordMatrix m, Coords globalCoords, Coords inPlaneCoords) {
		

		Coords vx = m.getVx();
		Coords vy = m.getVy();
		Coords vz = m.getVz();
		Coords o = m.getOrigin();
		
		
		if (Kernel.isEqual(
				(vx.crossProduct(vy)).dotproduct(vz), 0,
				Kernel.STANDARD_PRECISION)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			inPlaneCoords.setX(0);
			inPlaneCoords.setY(0);
			inPlaneCoords.setZ(-1);
			inPlaneCoords.setW(0);
			globalCoords.set(m.getVz());
			return;
		}
		
		// direction is not parallel to the plane
		projectPlaneNoCheck(vx, vy, vz, o, globalCoords.val, inPlaneCoords.val);

	}
	
	
	public void projectPlaneInPlaneCoords(CoordMatrix m, Coords inPlaneCoords) {
		

		Coords vx = m.getVx();
		Coords vy = m.getVy();
		Coords vz = m.getVz();
		Coords o = m.getOrigin();
		
		
		if (Kernel.isEqual(
				(vx.crossProduct(vy)).dotproduct(vz), 0,
				Kernel.STANDARD_PRECISION)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			inPlaneCoords.setX(0);
			inPlaneCoords.setY(0);
			inPlaneCoords.setZ(-1);
			inPlaneCoords.setW(0);
			return;
		}
		
		// direction is not parallel to the plane
		projectPlaneNoCheckInPlaneCoords(vx, vy, vz, o, inPlaneCoords.val);

	}

	
	public void projectPlane(CoordMatrix m, Coords globalCoords) {
		

		Coords vx = m.getVx();
		Coords vy = m.getVy();
		Coords vz = m.getVz();
		Coords o = m.getOrigin();
		
		
		if (Kernel.isEqual(
				(vx.crossProduct(vy)).dotproduct(vz), 0,
				Kernel.STANDARD_PRECISION)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			globalCoords.set(m.getVz());
			return;
		}
		
		// direction is not parallel to the plane
		// we can use globalCoords twice as it will be set at this end
		projectPlaneNoCheck(vx, vy, vz, o, globalCoords.val, globalCoords.val);

	}
	
	/**
	 * project on plane with known inverse matrix
	 * @param m inverse matrix
	 * @return 3D point in plane coords (z = distance(point, plane))
	 */
	final public Coords projectPlaneWithInverseMatrix(CoordMatrix m) {
		return m.mul(this);
	}

	
	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), no check if direction is parallel to the plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx 
	 * @param vy 
	 * @param vz 
	 * @param o 
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 */
	public void projectPlaneNoCheck(Coords vx, Coords vy, Coords vz, Coords o, double[] globalCoords, double[] inPlaneCoords) {

		// project in plane coords
		projectPlaneNoCheckInPlaneCoords(vx, vy, vz, o, inPlaneCoords);

		// globalCoords=this-inPlaneCoords_z*plane_vz
		double coeff = -inPlaneCoords[2]; // inPlaneCoords may use globalCoords for memory
		vz.mul(coeff, globalCoords);
		this.add(globalCoords, globalCoords);

		//note : globalCoords must be set at the end (when dummy inPlaneCoords)
	}
	
	public void projectPlaneNoCheckInPlaneCoords(Coords vx, Coords vy, Coords vz, Coords o, double[] inPlaneCoords) {

		// m*inPlaneCoords=this
		CoordMatrix.solve(inPlaneCoords, this, vx, vy, vz, o);

	}


	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * Attempt this to be of dimension 4, the matrix to be of dimension 4*4, and
	 * the vector to be of dimension 4.
	 * 
	 * 	 
	 *  set two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 ?? o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection
	 */
	public void projectPlaneThruV(CoordMatrix m, Coords v, Coords globalCoords, Coords inPlaneCoords) {

		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlane(m1, globalCoords, inPlaneCoords);
	}
	
	public void projectPlaneThruV(CoordMatrix m, Coords v, Coords globalCoords) {

		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlane(m1, globalCoords);
	}
	
	public void projectPlaneThruVInPlaneCoords(CoordMatrix m, Coords v, Coords inPlaneCoords) {

		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlaneInPlaneCoords(m1, inPlaneCoords);
	}



	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, then plane third vector is used instead
	 * 
	 * 
	 * 	 set two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 *         
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 */
	public void projectPlaneThruVIfPossible(CoordMatrix m, Coords v, Coords globalCoords, Coords inPlaneCoords) {

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (Kernel.isEqual(v3.dotproduct(v), 0.0,
				Kernel.STANDARD_PRECISION)){
			projectPlane(m, globalCoords, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlane(m1, globalCoords, inPlaneCoords);
	}
	
	public void projectPlaneThruVIfPossible(CoordMatrix m, Coords v, Coords globalCoords) {
		// check if v is parallel to plane
				Coords v3 = m.getColumn(3);
				if (Kernel.isEqual(v3.dotproduct(v), 0.0,
						Kernel.STANDARD_PRECISION)){
					projectPlane(m, globalCoords);
					return;
				}

				// if not, use v for direction
				CoordMatrix m1 = new CoordMatrix(4, 4);
				m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

				projectPlane(m1, globalCoords);
	}
	
	public void projectPlaneThruVIfPossibleInPlaneCoords(CoordMatrix m, Coords v, Coords inPlaneCoords) {

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (Kernel.isEqual(v3.dotproduct(v), 0.0,
				Kernel.STANDARD_PRECISION)){
			projectPlaneInPlaneCoords(m, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlaneInPlaneCoords(m1, inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, first project old position of the line
	 * (this,v), then project the result using plane third vector
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param oldCoords
	 *            old position of this
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 */
	public void projectPlaneThruVIfPossible(CoordMatrix m,
			Coords oldCoords, Coords v, Coords globalCoords, Coords inPlaneCoords) {

		// Application.debug(this+"\nold=\n"+oldCoords);

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (Kernel.isZero(v3.dotproduct(v))) {
			Coords firstProjection = Coords.createInhomCoorsInD3();
			oldCoords.projectLine(this, v, firstProjection, null);
			firstProjection.projectPlane(m, globalCoords, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		projectPlane(m1, globalCoords, inPlaneCoords);
	}
	

	/**
	 * calculates projection of this on the 3D-line represented by the matrix {V
	 * O}.
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @param H point projected 
	 * @param parameters {parameter on the line, normalized parameter}
	 */
	public void projectLine(Coords O, Coords V, Coords H, double[] parameters) {
		
		this.sub(O, H); // OM
		Coords N = V.normalized();
		double parameter = H.dotproduct(N); // OM.N
		N.mul(parameter, H); // OH
		O.add(H, H); 
		
		if (parameters == null){
			return;
		}
		
		parameters[0] = parameter / V.norm();
		parameters[1] = parameter;
		
	}

	/**
	 * calculates projection of this as far as possible to the 3D-line
	 * represented by the matrix {V O} regarding V2 direction.
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @param V2
	 *            direction of projection
	 * @return point projected
	 */
	public void projectNearLine(Coords O, Coords V, Coords V2, Coords project) {

		Coords V3 = V.crossProduct(V2);

		if (Kernel.isEqual(V3.norm(), 0.0,
				Kernel.STANDARD_PRECISION)) {
			project.set(this);
			return;
		}
		CoordMatrix m = new CoordMatrix(4, 4);
		m.set(new Coords[] { V, V3, V2, O });
		projectPlane(m, project);
	}

	/**
	 * project this on the line (O,V) in the direction V2.
	 * <p>
	 * returns the point of (O,V) that is the nearest to line (this,V2).
	 * <p>
	 * if V and V2 are parallel, return O.
	 * 
	 * @param O
	 *            origin of the line where this is projected
	 * @param V
	 *            direction of the line where this is projected
	 * @param V2
	 *            direction of projection
	 * @return {point projected, {coord of the proj. point on the line, distance
	 *         between this and the proj. point}}
	 */
	public Coords[] projectOnLineWithDirection(Coords O, Coords V, Coords V2) {

		Coords V3 = V.crossProduct(V2);

		if (Kernel.isEqual(V3.norm(), 0.0,
				Kernel.STANDARD_PRECISION)) {
			return new Coords[] { O,
					new Coords(new double[] { 0, this.sub(O).norm() }) };
		}
		CoordMatrix m = new CoordMatrix(4, 4);
		m.set(new Coords[] { V2, V3, V, this });
		Coords[] result = new Coords[] { new Coords(4), new Coords(4)};
		O.projectPlane(m, result[0], result[1]);
		return new Coords[] {
				result[0],
				new Coords(new double[] { -result[1].get(3),
						this.sub(result[0]).norm() }) };
	}

	/**
	 * returns this-v
	 * 
	 * @param v
	 *            vector subtracted
	 * @return this-v
	 */
	public Coords sub(Coords v) {
		int i;
		Coords result = new Coords(rows);
		for (i = 0; i < rows; i++)
			result.val[i] = val[i] - v.val[i];

		return result;
	}

	/**
	 * 
	 * @param v vector
	 * @param result gets this - v
	 */
	public void sub(Coords v, Coords result) {
		for (int i = 0; i < result.rows; i++){
			result.val[i] = val[i] - v.val[i];
		}
	}

	/**
	 * returns n-1 length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,x(n-1)}
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn}
	 */
	public Coords getInhomCoords() {
		int r = rows;
		Coords result = new Coords(r - 1);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++)
			result.val[i] = val[i] * wdiv;

		return result;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn,1}
	 */
	public Coords getInhomCoordsInSameDimension() {

		int r = rows;

		if (Kernel.isEqual(val[r - 1], 1))
			return this;

		Coords result = new Coords(r);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++)
			result.val[i] = val[i] * wdiv;

		result.val[r - 1] = 1;

		return result;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,1}
	 * 
	 * @return {x1/xn,x2/xn,...,1}
	 */
	public Coords getCoordsLast1() {
		int len = getLength();
		Coords result = new Coords(len);
		double lastCoord = val[len - 1];
		if (lastCoord != 0.0) {
			double lastCoordInv = 1 / lastCoord;
			for (int i = 0; i < len; i++)
				result.val[i] = val[i] * lastCoordInv;
		} else
			result.set(this);
		return result;
	}

	/**
	 * 
	 * @return this with (n-1) coord removed
	 */
	public Coords projectInfDim() {
		int len = getLength();
		Coords result = new Coords(len - 1);
		for (int i = 0; i < len - 1; i++)
			result.val[i] = val[i];
		result.val[len - 2] = val[len - 1];
		return result;
	}

	/**
	 * Return true if this==v for the precision given (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @param precision
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(Coords v, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(val[i], v.val[i], precision))
				return false;

		return true;
	}
	
	/**
	 * Return true if this==v for the precision given (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(Coords v) {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(val[i], v.val[i]))
				return false;

		return true;
	}


	
	/**
	 * Return true if this==v for Kernel.STANDARD_PRECISION precision (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @return true if the vectors are equal
	 */
	public boolean isEqual(Coords v) {
		return equalsForKernel(v, Kernel.STANDARD_PRECISION);
	}

	/**
	 * check if all entries are zero
	 * 
	 * @param precision
	 * @return
	 */
	public boolean isZero() {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(val[i], 0, Kernel.STANDARD_PRECISION))
				return false;
		return true;
	}

	/**
	 * Return true if all coordinates are not different from val more than
	 * precision.
	 * 
	 * @param val
	 *            value compared with
	 * @param precision
	 * @return true if all coordinates are not different from val more than
	 *         precision.
	 */
	public boolean equalsForKernel(double val, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(this.val[i], val, precision))
				return false;

		return true;

	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method returns
	 * an array v of two vectors {v[0], v[1]} (rows=4) so that (this, v[0],
	 * v[1]) is a right-handed orthonormal system.
	 */
	public Coords[] completeOrthonormal() {
		Coords vn1 = new Coords(4);

		if (val[0] != 0) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
		}

		Coords vn2 = this.crossProduct4(vn1);
		vn2.normalize();

		return new Coords[] { vn1, vn2 };
	}

	// ///////////////////////////////////////////////////
	// BASIC OPERATIONS
	// ///////////////////////////////////////////////////

	public Coords add(Coords v) {

		Coords result = new Coords(rows);

		for (int i = 0; i < rows; i++) {
			result.val[i] = val[i] + v.val[i];
		}

		return result;
	}
	
	/**
	 * put this + v into result
	 * @param v vector
	 * @param result result
	 */
	public void add(Coords v, double[] result) {

		for (int i = 0 ; i < rows ; i++){
			result[i] = val[i] + v.val[i];
		}
	}
	
	/**
	 * put this + v into result
	 * @param v vector
	 * @param result result
	 */
	public void add(double[] v, double[] result) {

		for (int i = 0 ; i < rows ; i++){
			result[i] = val[i] + v[i];
		}
	}
	
	public Coords addSmaller(Coords v) {
		Coords result = new Coords(rows);

		for (int i = 0; i < v.rows; i++) {
			result.val[i] = val[i] + v.val[i];
		}

		return result;
	}
	
	/**
	 * add values of v inside this
	 * @param v vector
	 */
	public void addInside(Coords v){
		for (int i = 0 ; i < v.val.length ; i++){
			val[i] += v.val[i];
		}
	}

	public Coords mul(double val0) {

		Coords result = new Coords(rows);

		for (int i = 0; i < rows; i++) {
			result.val[i] = val[i] * val0;
		}

		return result;
	}

	/**
	 * 
	 * @param val0 factor
	 * @param res gets this * val0
	 */
	public void mul(double val0, Coords res){
		for (int i = 0 ; i < res.rows && i < rows ; i++){
			res.val[i] = val[i] * val0;
		}
	}
	
	/**
	 * 
	 * @param val0 factor
	 * @param res gets this * val0
	 */
	public void mul(double val0, double[] res){
		for (int i = 0 ; i < res.length && i < rows ; i++){
			res[i] = val[i] * val0;
		}
	}
	/**
	 * 
	 * @param v vector
	 * @param res gets this + v
	 */
	public void add(Coords v, Coords res){
		for (int i = 0 ; i < res.rows ; i++){
			res.val[i] = v.val[i] + val[i];
		}
	}
	
	// /////////////////////////////////////////////////:
	/** for testing the package */
	public static synchronized void main(String[] args) {

		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		App.debug("v1.v1 = " + v1.dotproduct(v1));
	}

	/**
	 * if the ND hom coords is in x-y plane, return this coords
	 * 
	 * @param coordsND
	 * @return
	 */
	public Coords getCoordsIn2DView() {

		int dim = rows - 1;
		switch (dim) {
		case 2:
			return new Coords(getX(), getY(), getZ());
		case -1:
		case 0:
			return new Coords(0, 0, getX());
		case 1:
			return new Coords(getX(), 0, getY());
		default:
			for (int i = 3; i <= dim; i++) {
				if (Double.isNaN(get(i)) || !Kernel.isZero(get(i)))
					return new Coords(Double.NaN, Double.NaN, Double.NaN);
			}
			// get(3) to get(dim) are all zero
			return new Coords(get(1), get(2), get(dim + 1));
		}
	}

	/**
	 * this=(r,g,b,...) color representation
	 * 
	 * @return gray scale intensity
	 */
	public double getGrayScale() {
		return 0.2989 * getX() + 0.5870 * getY() + 0.1140 * getZ();
	}

	public void convertToGrayScale() {
		double gray = getGrayScale();
		setX(gray);
		setY(gray);
		setZ(gray);
	}
	
	
		

	/**
	 * assume this is equal to (x,y,z,w)
	 * @return true if define a defined point
	 */
	public boolean isPointDefined(){
		
		if (Kernel.isZero(getW())){
			return false;
		}
		
		return isDefined();
	}
	
	
	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	public boolean isDefined() {


		for (int i = 0; i < rows ; i++) {
			if (Double.isNaN(val[i])){
				return false;
			}
		}

		return true;
	}
	
	/**
	 * 
	 * @return 3 floats array
	 */
	public float[] get3ForGL(){
		float[] ret = new float[3];
		
		for (int i = 0 ; i < 3 ; i++){
			ret[i] = (float) val[i];
		}
		
		return ret;
		
	}
	
	/**
	 * 
	 * @return 3 floats array
	 */
	public float[] get4ForGL(){
		float[] ret = new float[4];
		
		for (int i = 0 ; i < 4 ; i++){
			ret[i] = (float) val[i];
		}
		
		return ret;
		
	}
	
	/**
	 * check for first non-zero value ; reverse all values if this one is negative
	 */
	public void checkReverseForFirstValuePositive(){
		
		boolean zero = true;
		int i = 0;
		while (i < val.length && zero){
			if (!Kernel.isZero(val[i])){
				zero = false;
			}else{
				i++;
			}
		}
		
		if (!zero && val[i] < 0){
			while (i < val.length){
				val[i] *= -1;
				i++;
			}
		}
		
	}

	
	/**
	 * returns double[] describing the matrix for openGL
	 * 
	 * @return the matrix as a double[]
	 */
	public double[] get() {

		return val;
	}
	
	
	/** @return false if at least one value is infinite */
	public boolean isFinite() {

		for (int i = 0; i < rows; i++) {
			if (Double.isInfinite(val[i])){
				return false;
			}
		}

		return true;
	}
	
	/**
	 * multiply all values by v
	 * @param v factor
	 */
	public void mulInside(double v){
		for (int i = 0 ; i < val.length; i++){
			val[i] *= v;
		}
	}
	
	/**
	 * mul 3 first values by v
	 * @param v value
	 */
	public void mulInside3(double v){
		for (int i = 0 ; i < 3; i++){
			val[i] *= v;			
		}
	}
	
	
	@Override
	public String toString() {
		String s = "";

		for (int i = 0; i < val.length; i++) {
			s += ""+val[i]+"\n";
		}

		return s;
	}
	
}