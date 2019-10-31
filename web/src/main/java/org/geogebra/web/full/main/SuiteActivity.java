package org.geogebra.web.full.main;

import org.geogebra.common.main.settings.AppConfigSuite;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.main.activity.BaseActivity;
import org.geogebra.web.resources.SVGResource;

/**
 * Activity class for the GeoGebra Suite app
 */
public class SuiteActivity extends BaseActivity {

	/**
	 * New Suite activity
	 */
	public SuiteActivity() {
		super(new AppConfigSuite());
	}

	@Override
	public SVGResource getIcon() {
		return MaterialDesignResources.INSTANCE.graphing();
	}

}
