package org.geogebra.common.kernel.scripting;

import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * HideLayer
 */
public class CmdShowHideLayer extends CmdScripting {

	private boolean show;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param show
	 *            whether to show or hide
	 */
	public CmdShowHideLayer(Kernel kernel, boolean show) {
		super(kernel);
		this.show = show;
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0] instanceof NumberValue) {
				int layer = (int) arg[0].evaluateDouble();
				if (layer < 0 || layer > EuclidianStyleConstants.MAX_LAYERS) {
					return arg;
				}
				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(show);
						geo.updateCascade();
					}
				}
				kernel.notifyRepaint();
				return arg;

			}
			throw argErr(app, c, null);

		default:
			throw argNumErr(c);
		}
	}
}
