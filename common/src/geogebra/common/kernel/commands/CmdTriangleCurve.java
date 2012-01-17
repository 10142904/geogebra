package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

public class CmdTriangleCurve extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangleCurve(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			GeoNumeric ta=null,tb=null,tc=null;
			arg = new GeoElement[4];
			for(int i=0;i<3;i++)
				arg[i]=resArg(c,i);
			ta =new GeoNumeric(cons);
			tb =new GeoNumeric(cons);
			tc =new GeoNumeric(cons);
			cons.addLocalVariable("A", ta);
			cons.addLocalVariable("B", tb);
			cons.addLocalVariable("C", tc);
			arg[3] = resArg(c,3);

			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isGeoImplicitPoly())) {
				
				
				GeoElement[] ret = { kernelA.TriangleCubic(c.getLabel(),
						(GeoPoint2)arg[0], (GeoPoint2)arg[1], (GeoPoint2)arg[2],
						(GeoImplicitPoly) arg[3],ta,tb,tc)} ;
				cons.removeLocalVariable("A");
				cons.removeLocalVariable("B");
				cons.removeLocalVariable("C");
				return ret;
				
			}			
			else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				throw argErr(app, c.getName(), arg[3]);
			}
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
	protected final GeoElement resArg(Command c,int pos) throws MyError {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		

		
			// resolve variables in argument expression
			arg[pos].resolveVariables();

			// resolve i-th argument and get GeoElements
			// use only first resolved argument object for result
			GeoElement result = resArg(arg[pos])[0];
		

		cons.setSuppressLabelCreation(oldMacroMode);
		return result;
	}

}
