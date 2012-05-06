package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *SetLabelMode
 */
public class CmdSetLabelMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLabelMode(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = arg[0];

				geo.setLabelMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				
				return;
			}
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
