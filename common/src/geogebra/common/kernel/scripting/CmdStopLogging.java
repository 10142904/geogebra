package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.plugin.UDPLogger;

public class CmdStopLogging extends CmdScripting {
	public CmdStopLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {

		UDPLogger logger = app.getUDPLogger();
		if (logger != null) {
			logger.stopLogging();
		} else {
			// no need for error
		}

	}

	

}
