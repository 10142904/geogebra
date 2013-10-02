package geogebra.common.gui.dialog.options.model;

import geogebra.common.gui.dialog.handler.RedefineInputHandler;
import geogebra.common.gui.dialog.handler.RenameInputHandler;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;

public class ObjectNameModel extends OptionsModel {
	public interface IObjectNameListener {
		void setNameText(final String text);
		void setCaptionText(final String text);
		void updateGUI(boolean showDefinition, boolean showCaption);
		void updateDefLabel();
		void updateCaption();
		void updateName(final String text);
	};
	
	private IObjectNameListener listener;
	private App app;
	private RenameInputHandler nameInputHandler;
	private RedefineInputHandler defInputHandler;
	private GeoElement currentGeo;
	private boolean redefinitionFailed;
	
	public ObjectNameModel(App app, IObjectNameListener listener) {
		this.app = app;
		this.listener = listener;
		setNameInputHandler(new RenameInputHandler(app, null, false));
		// DEFINITON PANEL
		// Michael Borcherds 2007-12-31 BEGIN added third argument
		setDefInputHandler(new RedefineInputHandler(app, null, null));
		// Michael Borcherds 2007-12-31 END

	}
	
	
	@Override
	public void updateProperties() {
		/*
		 * DON'T WORK : MAKE IT A TRY FOR 5.0 ? //apply textfields modification
		 * on previous geo before switching to new geo //skip this if label is
		 * not set (we re in the middle of redefinition) //skip this if action
		 * is performing if (currentGeo!=null && currentGeo.isLabelSet() &&
		 * !actionPerforming && (geos.length!=1 || geos[0]!=currentGeo)){
		 * 
		 * //App.printStacktrace("\n"+tfName.getText()+"\n"+currentGeo.getLabel(
		 * StringTemplate.defaultTemplate));
		 * 
		 * String strName = tfName.getText(); if (strName !=
		 * currentGeo.getLabel(StringTemplate.defaultTemplate))
		 * nameInputHandler.processInput(tfName.getText());
		 * 
		 * 
		 * String strDefinition = tfDefinition.getText(); if
		 * (strDefinition.length()>0 &&
		 * !strDefinition.equals(getDefText(currentGeo)))
		 * defInputHandler.processInput(strDefinition);
		 * 
		 * String strCaption = tfCaption.getText(); if
		 * (!strCaption.equals(currentGeo.getCaptionSimple())){
		 * currentGeo.setCaption(tfCaption.getText());
		 * currentGeo.updateVisualStyleRepaint(); } }
		 */

		// take name of first geo
		GeoElement geo0 = getGeoAt(0);
		listener.updateName(geo0.getLabel(StringTemplate.editTemplate));

		// if a focus lost is called in between, we keep the current definition text
		//redefinitionForFocusLost = tfDefinition.getText();
		setCurrentGeo(geo0);
		nameInputHandler.setGeoElement(geo0);
		defInputHandler.setGeoElement(geo0);

		// DEFINITION
		// boolean showDefinition = !(currentGeo.isGeoText() ||
		// currentGeo.isGeoImage());
		boolean showDefinition = getCurrentGeo().isGeoText() ? ((GeoText) getCurrentGeo())
				.isTextCommand() : !(((getCurrentGeo().isGeoImage() || getCurrentGeo()
				.isGeoButton()) && getCurrentGeo().isIndependent()));
				
		if (showDefinition) {
			listener.updateDefLabel();
		}
		// CAPTION
		boolean showCaption = !(getCurrentGeo() instanceof TextValue); // borcherds was
															// currentGeo.isGeoBoolean();
		if (showCaption) {
			listener.updateCaption();
		}
		// captionLabel.setVisible(showCaption);
		// inputPanelCap.setVisible(showCaption);

		listener.updateGUI(showDefinition, showCaption);

	}

	@Override
	public boolean checkGeos() {
		return (getGeosLength() == 1);
	}

	public void applyNameChange(final String name) {
		nameInputHandler.setGeoElement(currentGeo);
		nameInputHandler.processInput(name);

		// reset label if not successful
		final String strName = currentGeo.getLabel(StringTemplate.defaultTemplate);
		if (!strName.equals(name)) {
			listener.setNameText(strName);
		}
		currentGeo.updateRepaint();
	
	}
	
	public void applyDefinitionChange(final String definition) {
	
		if (!definition.equals(getDefText(currentGeo))) {

			if (defInputHandler.processInput(definition)) {
				// if succeeded, switch current geo
				currentGeo = defInputHandler.getGeoElement();
				app.getSelectionManager().addSelectedGeo(currentGeo);
			} else {
				redefinitionFailed = true;
			}
		}

	}
	
	public static String getDefText(GeoElement geo) {
			/*
			 * return geo.isIndependent() ? geo.toOutputValueString() :
			 * geo.getCommandDescription();
			 */
			return geo.getRedefineString(false, true);
		}

	public void applyCapitonChange(final String caption) {
			currentGeo.setCaption(caption);

		final String strCaption = currentGeo.getRawCaption();
		if (!strCaption.equals(caption.trim())) {
			listener.setCaptionText(strCaption);
		}
		currentGeo.updateVisualStyleRepaint();
	}

	

	public GeoElement getCurrentGeo() {
		return currentGeo;
	}


	public void setCurrentGeo(GeoElement currentGeo) {
		this.currentGeo = currentGeo;
	}


	public RenameInputHandler getNameInputHandler() {
		return nameInputHandler;
	}


	public void setNameInputHandler(RenameInputHandler nameInputHandler) {
		this.nameInputHandler = nameInputHandler;
	}


	public RedefineInputHandler getDefInputHandler() {
		return defInputHandler;
	}


	public void setDefInputHandler(RedefineInputHandler defInputHandler) {
		this.defInputHandler = defInputHandler;
	}

}
