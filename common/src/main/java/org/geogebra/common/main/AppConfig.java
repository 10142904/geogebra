package org.geogebra.common.main;

import org.geogebra.common.io.layout.DockPanelData;

public interface AppConfig {

	public void adjust(DockPanelData dp);

	public String getAVTitle();

	public int getLineDisplayStyle();

	public String getAppTitle();

	public String getAppName();

	public String getAppNameShort();

	public String getTutorialKey();

	public boolean showKeyboardHelpButton();

	public boolean showObjectSettingsFromAV();

	public boolean isSimpleMaterialPicker();

	public boolean hasPreviewPoints();

	public boolean allowsSuggestions();

	boolean shouldKeepRatioEuclidean();

}
