package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;

/**
 * Execute[<list of commands>]
 */
public class CmdExecute extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExecute(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n > 10)
			throw argNumErr(app, c.getName(), n);
		if (arg[0].isGeoList() && ((GeoList) arg[0]).size()==0 || !arg[0].isDefined())
			return new GeoElement[] {};
		if ((!arg[0].isGeoList())
				|| (!((GeoList) arg[0]).getGeoElementForPropertiesDialog()
						.isGeoText()))
			throw argErr(app, c.getName(), arg[0]);
		GeoList list = (GeoList) arg[0];
		for (int i = 0; i < list.size(); i++) {
			try {
				String cmdText = ((GeoText) list.get(i)).getTextString();
				for(int k=1;k<n;k++)
					cmdText = cmdText.replace("%"+k, arg[k].getLabel(StringTemplate.maxPrecision));
				kernelA.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(cmdText
								, false,
								false, true);
			} catch (MyError e) {
				app.showError(e);
				break;
			} catch (Exception e) {
				app.showError(e.getLocalizedMessage());
				e.printStackTrace();
				break;
			}
		}
		app.storeUndoInfo();
		return new GeoElement[] {};

	}
}
