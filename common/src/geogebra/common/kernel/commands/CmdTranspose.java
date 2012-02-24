package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Transpose[ <List> ]
 * Michael Borcherds 
 */
public class CmdTranspose extends CmdOneListFunction {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdTranspose(Kernel kernel) {
		super(kernel);
	}

	@Override
	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernelA.Transpose(a, b);
	}

}
