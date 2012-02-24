package geogebra.common.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.Kernel;

/**
 * 
 * FitLineY[list of points]
 * adapted from CmdLcm by Michael Borcherds 2008-01-14
 */
public class CmdFitLineY extends CmdOneListFunction {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitLineY(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.FitLineY(a, b);
	}

}
