package geogebra.common.kernel.locusequ;

import geogebra.common.kernel.algos.AlgoIntersectLines;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPoint2;

import java.util.Collection;
//import java.util.Hashtable;
import java.util.Map;

/**
 * @author sergio
 * Scope for points. Maps points to symbolic representation.
 */
public class EquationPointMap {

    /*
     * Because EquationPoint holds three coordinates but
     * for this project only two are needed, 
     * GEOGEBRA_DIMENSION is defined.
     */
    private static final int GEOGEBRA_DIMENSION = 2;
    private GeoLocus locus;
    private GeoPoint2 locusPoint, movingPoint; // TODO: Look into using GeoPointND
    private int curInd;
    private Map<GeoPoint2,EquationPoint> container;
    private Map<GeoPoint2,GeoPoint2> identifications;
    private EquationScope scope;
    
    /**
     * Creates a new EquationPointMap associated to a {@link EquationScope}.
     * @param scope associated.
     */
    public EquationPointMap(EquationScope scope) {
        this.curInd = 1;
//        this.container = new Hashtable<GeoPoint2,EquationPoint>();
//        this.identifications = new Hashtable<GeoPoint2, GeoPoint2>();
        this.scope = scope;
    }

    /**
     * @param locusPoint locusPoint from locus.
     * @param movingPoint movingPoint from locus.
     * @param scope scope.
     */
    public EquationPointMap(GeoPoint2 locusPoint, GeoPoint2 movingPoint, EquationScope scope){
        this(scope);
        
        this.locusPoint  = locusPoint;
        this.movingPoint = movingPoint;
    }
    
    /**
     * @param p The point whose {@link EquationPoint} you are looking for.
     * @return The {@link EquationPoint} for p. If p is not in the map, returns null.
     */
    public EquationPoint get(GeoPoint2 p) {
        return this.container.get(p);
    }
    
    /**
     * @param p The point whose {@link EquationPoint} you are looking for.
     * @return A {@link EquationPoint} for p. If there was none, it is created.
     */
    public EquationPoint getOrCreate(GeoPoint2 p) {
        
    	// get
    	EquationPoint res = this.get(p);
        if(res != null) {
            return res;
        }
        
        // or create
		res = this.constructEquationPoint(p);
		this.container.put(p, res);
		return res;
    }
    
    private EquationPoint constructEquationPoint(GeoPoint2 geoPoint) {
        EquationPoint point;
        
        if(mustTakeNumericCoordinates(geoPoint)) {
            point = new EquationFreePoint(geoPoint);
        } else if(geoPoint == this.locusPoint) {
            // there should be only one of this type
            point = new EquationSpecialSymbolicPoint(1, geoPoint);
        } else {
            point = new EquationDependentPoint(this.curInd, geoPoint);
            curInd +=  GEOGEBRA_DIMENSION;
        }
        return point;
    }
    
    /**
     * Restricts two different {@link GeoPoint2} to use the same
     * variables.
     * @param orig Original point.
     * @param target Target point. Its coordinates will be preserved.
     */
    public void identify(final GeoPoint2 orig, final GeoPoint2 target) {
    	this.identifications.put(orig, target);
        EquationPoint formerPoint = this.container.get(orig);
        EquationPoint newPoint = this.getOrCreate(target);
        this.container.put(orig, newPoint);

        // Just in case there is some reference hanging out there
        // alone.
        if(formerPoint != null) {
            formerPoint.getIndexesFrom(newPoint);
        }
    }
    
    /**
     * Checks if p must not take symbolic coordinates.
     * @param p point
     * @return true iff p symbolic representation will be still symbolic.
     */
    protected boolean mustTakeNumericCoordinates(final GeoPoint2 p) {
        return p.isIndependent() ||
               (isPointOnPath(p) &&
                       p != this.movingPoint &&
                       !hasMovingPointAsPredecessor(p)) ||
               isIntersectionOfAxis(p) ||
               isAuxiliarPointOnAPolygon(p);
    }
    
    /**
     * Checks if point's parent algorithm is AlgoPolygonRegular.
     * @param p
     * @return true iff point's been created by an AlgoPolygonRegular
     */
    private boolean isAuxiliarPointOnAPolygon(GeoPoint2 p) {
        return Algos.AlgoPolygonRegular == getParentAlgorithmName(p);
    }

    /**
     * Check if p is an intersection of any of the axis.
     * @param p point to check
     * @return true iff p is an intersection with an axis.
     */
    protected boolean isIntersectionOfAxis(GeoPoint2 p) {
        if(Algos.AlgoIntersectLines != getParentAlgorithmName(p)) {
            return false;
        }
        
        // Safe, because of previous if.
        //AlgoIntersectLines algo = (AlgoIntersectLines) p.getParentAlgorithm();
        
        // FIXME: wait for an email answer.
        return false; /*(algo.getg() instanceof GeoAxis &&
                algo.geth() instanceof GeoAxis);*/
    }
    
    /**
     * Check if current point is a point on path.
     * @param p point to check
     * @return true iff is a point on a path object.
     */
    protected boolean isPointOnPath(GeoPoint2 p) {
        return Algos.AlgoPointOnPath == getParentAlgorithmName(p);
    }
    
    /**
     * Checks if points contains current moving point as a predecessor.
     * @param p current point.
     * @return true iff p contains moving point as predecessor.
     */
    protected boolean hasMovingPointAsPredecessor(GeoPoint2 p) {
        return this.movingPoint != null &&
               p.getAllPredecessors().contains(this.movingPoint);
    }
    
    /**
     * @return all symbolic points
     */
    public Collection<EquationPoint> getAllPoints() {
        return this.container.values();
    }
    
    /**
     * Returns the parent algorithm's Algos for p.
     * @param p point
     * @return an {@link Algos}
     */
    protected Algos getParentAlgorithmName(final GeoPoint2 p) {
        return p.getParentAlgorithm() == null ? null : p.getParentAlgorithm().getClassName();
    }

    /**
     * Adds a map between {@link GeoPoint2} midpoint to {@link EquationPoint} m.
     * @param midpoint {@link GeoPoint2}
     * @param m {@link EquationPoint}
     */
    public void put(final GeoPoint2 midpoint, final EquationPoint m) {
        if(midpoint != null){
            this.container.put(midpoint, m);
        }
    }
    
    /**
     * Check if p is the exact same point object as the moving point.
     * Equality is checked with ==.
     * @param p point to check.
     * @return true iff p == this.movingPoint.
     */
    public boolean isMovingPoint(GeoPoint2 p) {
        return this.movingPoint == p; // Yes, it has to be the exact same point.
    }
    
    /**
     * Check if p is the exact same point object as the locus point.
     * @param p point to check.
     * @return true iff p == this.locusPoint.
     */
    public boolean isLocusPoint(GeoPoint2 p) {
        return this.locusPoint == p; // Yes, it has to be the exact same point.
    }
}
