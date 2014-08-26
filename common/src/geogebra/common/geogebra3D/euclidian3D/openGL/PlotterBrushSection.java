package geogebra.common.geogebra3D.euclidian3D.openGL;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;



/** class describing the section of the brush
 * 
 * @author mathieu
 *
 */
public class PlotterBrushSection {


	/** center and clock vectors */
	Coords center;

	private Coords clockU;

	private Coords clockV;
	
	/** direction from last point */
	private Coords direction;
	
	double length;
	
	/** normal (for caps) */
	private Coords normal = null;
	
	/** normal deviation along direction */
	private double normalDevD = 0;
	private double normalDevN = 1;
	
	/** thickness = radius of the section */
	private float thickness;
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 */
	public PlotterBrushSection(Coords point, float thickness){
		this(point, thickness, null, null);
	}
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param clockU 
	 * @param clockV 
	 */
	public PlotterBrushSection(Coords point, float thickness, Coords clockU, Coords clockV){
		this.center = point;
		this.thickness = thickness;
		this.clockU = clockU;
		this.clockV = clockV;
	}	
	
	/**
	 * second section constructor
	 * @param s
	 * @param point
	 * @param thickness
	 * @param updateClock 
	 */
	public PlotterBrushSection(PlotterBrushSection s, Coords point, float thickness, boolean updateClock){
		this(point,thickness);
		
		direction = center.sub(s.center);

		if (center.equalsForKernel(s.center, Kernel.STANDARD_PRECISION)){
			if (this.thickness<s.thickness)
				normal = s.direction;
			else 
				normal = s.direction.mul(-1);
			s.normal = normal;
			//keep last direction
			direction = s.direction;
		}else{
			//calc normal deviation
			double dt = this.thickness-s.thickness;
			if (dt!=0){
				double l = direction.norm();
				double h = Math.sqrt(l*l+dt*dt);
				normalDevD = -dt/h;
				normalDevN = l/h;
			
				//normalDevD = 0.0000; normalDevN = 1;
				
				s.normalDevD = normalDevD;
				s.normalDevN = normalDevN;
				//Application.debug("dt="+dt+",normalDev="+normalDevD+","+normalDevN);
			}
			
			direction.normalize();
			s.direction = direction;
			normal = null;
			s.normal = null;
			
			//calc new clocks				
			if (updateClock){
				Coords[] vn = direction.completeOrthonormal();
				s.clockU = vn[0]; s.clockV = vn[1];
			}

		}
		clockU = s.clockU; clockV = s.clockV;
		
		//Application.debug("direction=\n"+direction.toString());
	}
	
	private Coords tmpCoords = new Coords(3);
	
	/**
	 * return the normal vector for parameters u,v
	 * @param u
	 * @param v
	 * @return the normal vector
	 */
	public void getNormalAndPosition(double u, double v, Coords vn, Coords pos){
		
		//vn.set(clockV.mul(v).add(clockU.mul(u)));
		clockU.mul(u, vn);
		clockV.mul(v, tmpCoords);
		vn.add(tmpCoords, vn);
		
		//pos.set(vn.mul(thickness).add(center));
		vn.mul(thickness, pos);
		pos.add(center, pos);
		
		
		if (normal!=null){
			vn.setValues(normal, 3);
		}else if (normalDevD!=0){
			//Application.debug("normalDev="+normalDevD+","+normalDevN);
			//vn.set(vn.mul(normalDevN).add(direction.mul(normalDevD)));	
			vn.mul(normalDevN, vn);
			direction.mul(normalDevD, tmpCoords);
			vn.add(tmpCoords, vn);
		}

	}
	
	
	/**
	 * @return the center of the section
	 */
	public Coords getCenter(){
		return center;
	}
	
	
	////////////////////////////////////
	// FOR 3D CURVE
	////////////////////////////////////
	
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param direction
	 */
	public PlotterBrushSection(Coords point, Coords direction, float thickness){
		this.center = point;
		this.thickness = thickness;
		this.direction = direction;
		Coords[] vn = direction.completeOrthonormal();
		clockU = vn[0]; clockV = vn[1];
		
	}
	
	/**
	 * first section constructor
	 * @param s 
	 * @param point
	 * @param thickness
	 */
	public PlotterBrushSection(PlotterBrushSection s, Coords point, float thickness){
		this.center = point;
		this.thickness = thickness;
		this.direction=this.center.sub(s.getCenter());
		length = direction.norm();
		direction = direction.mul(1/length);
		
		if (s.clockU == null){
			Coords[] vn = direction.completeOrthonormal();
			s.clockU = vn[0]; s.clockV = vn[1];
		}
		
		clockV = direction.crossProduct(s.clockU).normalized(); 
		//normalize it to avoid little errors propagation
		// TODO truncate ?
		clockU = clockV.crossProduct(direction).normalized();
	}
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param direction
	 */
	public PlotterBrushSection(PlotterBrushSection s, Coords point, Coords direction, float thickness){
		
		this.center = point;
		this.thickness = thickness;
		this.direction = direction;
		
		clockV = direction.crossProduct(s.clockU).normalized(); 
		//normalize it to avoid little errors propagation
		// TODO truncate ?
		clockU = clockV.crossProduct(direction).normalized();
		
	}	
	
	
//	/**
//	 * set this equal to pbs
//	 * @param pbs 
//	 */
//	public void set(PlotterBrushSection pbs){
//		center.set(pbs.center);
//
//		clockU.set(pbs.clockU);
//		clockV.set(pbs.clockV);
//
//		direction.set(pbs.direction);
//
//		length = pbs.length;
//
//		if (pbs.normal != null){
//			if (normal != null){
//				normal.set(pbs.normal);
//			}else{
//				normal = pbs.normal.copyVector();
//			}
//		}else{
//			normal = null;
//		}
//
//		normalDevD = pbs.normalDevD;
//		normalDevN = pbs.normalDevN;
//
//		thickness = pbs.thickness;
//	}
	
}
