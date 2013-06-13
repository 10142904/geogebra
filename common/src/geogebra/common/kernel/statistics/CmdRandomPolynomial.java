package geogebra.common.kernel.statistics;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.main.MyError;
/**
 * RandomPolynomial[degree, min, max]
 */
public class CmdRandomPolynomial extends CommandProcessor {

	/**
	 * @param kernel kernel
	 */
	public CmdRandomPolynomial(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		GeoElement[] args = resArgs(c);
		if(args.length!=3)
			throw argNumErr(app,c.getName(),args.length);
		for(int i=1;i<3;i++){
			if(!(args[i]  instanceof GeoNumberValue))
				throw argErr(app,c.getName(),args[i]);
		}
		AlgoRandomPolynomial algo = new AlgoRandomPolynomial(cons,c.getLabel(),(GeoNumberValue)args[0],
				(GeoNumberValue)args[1],(GeoNumberValue)args[2]);
		return new GeoElement[] { algo.getResult()} ;
	}

}
