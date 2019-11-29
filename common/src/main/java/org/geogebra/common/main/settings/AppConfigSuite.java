package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.arithmetic.filter.OperationArgumentFilter;

/**
 * Config for the Suite app (currently graphing before tool removal)
 */
public class AppConfigSuite extends AppConfigGraphing {

	@Override
	public String getAppCode() {
		return "suite";
	}

	@Override
	public OperationArgumentFilter createOperationArgumentFilter() {
		return null;
	}
}
