package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoFocus3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFocus;
import geogebra.common.kernel.commands.CmdFocus;
import geogebra.common.kernel.kernelND.GeoConicND;

public class CmdFocus3D extends CmdFocus {

	public CmdFocus3D(Kernel kernel) {
		super(kernel);
	}

	protected AlgoFocus newAlgoFocus(Construction cons, String[] labels,
			GeoConicND c) {

		if (c.isGeoElement3D()) {
			return new AlgoFocus3D(cons, labels, c);
		}

		return super.newAlgoFocus(cons, labels, c);
	}

}
