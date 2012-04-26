package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.prover.FreeVariable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Simon Weitzhofer
 *         18th April 2012
 *
 */
public class AlgoAreCollinear extends AlgoElement implements SymbolicParametersAlgo{

	private GeoPoint2 inputPoint1; //input
	private GeoPoint2 inputPoint2; //input
	private GeoPoint2 inputPoint3; //input
	
    private GeoBoolean outputBoolean; //output	
	private Polynomial[] polynomials;

    /**
     * Creates a new AlgoAreCollinear function
     * @param cons the Construction
     * @param label the name of the boolean
     * @param inputPoint1 the first point
     * @param inputPoint2 the second point
     * @param inputPoint3 the third point
     */
    public AlgoAreCollinear(final Construction cons, final String label, final GeoPoint2 inputPoint1, final GeoPoint2 inputPoint2, final GeoPoint2 inputPoint3) {
        super(cons);
        this.inputPoint1=inputPoint1;
        this.inputPoint2=inputPoint2;
        this.inputPoint3=inputPoint3;
               
        outputBoolean = new GeoBoolean(cons);

        setInputOutput();
        compute();
        outputBoolean.setLabel(label);
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoAreCollinear;
    }

    @Override
	protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = inputPoint1;
        input[1] = inputPoint2;
        input[2] = inputPoint3;

        super.setOutputLength(1);
        super.setOutput(0, outputBoolean);
        setDependencies(); // done by AlgoElement
    }
    																								
    public GeoBoolean getResult() {
        return outputBoolean;
    }

    @Override
	public final void compute() {
        double det=
        	inputPoint1.getX()*inputPoint2.getY()*inputPoint3.getZ()+
	        inputPoint2.getX()*inputPoint3.getY()*inputPoint1.getZ()+
	        inputPoint3.getX()*inputPoint1.getY()*inputPoint2.getZ()-
	        inputPoint3.getX()*inputPoint2.getY()*inputPoint1.getZ()-
	        inputPoint2.getX()*inputPoint1.getY()*inputPoint3.getZ()-
	        inputPoint1.getX()*inputPoint3.getY()*inputPoint2.getZ();
        outputBoolean.setValue(Kernel.isZero(det));
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables)
			throws NoSymbolicParametersException {
		if (input[0] != null && input[1] != null && input[2] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo
				&& input[2] instanceof SymbolicParametersAlgo) {
			int[] degree1 = ((SymbolicParametersAlgo) input[0])
					.getFreeVariablesAndDegrees(freeVariables);
			int[] degree2 = ((SymbolicParametersAlgo) input[1])
					.getFreeVariablesAndDegrees(freeVariables);
			int[] degree3 = ((SymbolicParametersAlgo) input[2])
					.getFreeVariablesAndDegrees(freeVariables);
			int[] result =new int[1];
			result[0]=Math.max(degree1[0]+degree2[1]+degree3[2],
					Math.max(degree2[0]+degree3[1]+degree1[2],
					Math.max(degree3[0]+degree1[1]+degree2[2],
							
					Math.max(degree3[0]+degree2[1]+degree1[2],
					Math.max(degree2[0]+degree1[1]+degree3[2],
							degree1[0]+degree3[1]+degree2[2])))));
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(final HashMap<FreeVariable,BigInteger> values) {
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (input[0] != null && input[1] != null && input[2] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo
				&& input[2] instanceof SymbolicParametersAlgo) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			Polynomial[] coords3 = ((SymbolicParametersAlgo) input[2])
					.getPolynomials();
			polynomials = new Polynomial[1];
			polynomials[0] = coords1[0].multiply(coords2[1]).multiply(coords3[2]).add(
					coords2[0].multiply(coords3[1]).multiply(coords1[2])).add(
					coords3[0].multiply(coords1[1]).multiply(coords2[2])).subtract(
							
					coords3[0].multiply(coords2[1]).multiply(coords1[2]).add(
					coords2[0].multiply(coords1[1]).multiply(coords3[2])).add(
					coords1[0].multiply(coords3[1]).multiply(coords2[2])));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}
  
}
