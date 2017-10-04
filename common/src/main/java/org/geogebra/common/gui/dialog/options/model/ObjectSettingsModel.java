package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * This class represents a model for the object properties
 */

abstract public class ObjectSettingsModel {

    /**
     * Application
     */
    protected App app;
    private GeoElement geoElement;
    private ArrayList<GeoElement> geoElementsList;

    /**
     * @param app
     *         Application
     */
    public ObjectSettingsModel(App app) {
        this.app = app;
        geoElementsList = new ArrayList<GeoElement>();
    }

    /**
     * @return the color of the geoElement
     */
    public GColor getColor() {
        return geoElement != null ? geoElement.getObjectColor() : GColor.BLACK;
    }

    /**
     * @param color
     *         GColor which should be set for all selected geoElements
     */
    public void setColor(GColor color) {
        if (geoElement == null) {
            return;
        }

        if (!hasFurtherStyle()) {
            EuclidianStyleBarStatic.applyTextColor(geoElementsList, color);
        } else {
            EuclidianStyleBarStatic.applyColor(geoElementsList, color, geoElement.getAlphaValue(), app);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return if the label of the geoElement is visible or not
     */
    public boolean isLabelShown() {
        return geoElement != null && geoElement.isLabelVisible();
    }

    /**
     * @param show
     *         the label of the geoElement should be shown or not
     */
    public void showLabel(boolean show) {
        for (GeoElement geo : geoElementsList) {
            geo.setLabelVisible(show);
            geo.updateRepaint();
        }
    }

    /**
     * @return the label mode of the geoElement, default is LABEL_NAME
     */
    public int getLabelStyle() {
        return geoElement != null ? geoElement.getLabelMode() : GeoElement.LABEL_NAME;
    }

    /**
     * @param mode
     *         set it as label mode for all selected geoElement
     */
    public void setLabelStyle(int mode) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setLabelMode(mode);
        }

        showLabel(true);
        app.setPropertiesOccured();
    }

    /**
     * @return the label string of the current label mode of the geoElement or with empty string if it is null
     */
    public String getLabelString() {
        if (geoElement == null) {
            return "";
        }

        if (!geoElement.isLabelVisible()) {
            return App.getLabelStyleName(app, -1);
        }

        return App.getLabelStyleName(app, geoElement.getLabelMode());
    }

    /**
     * @return the current line style of the geoElement or with DEFAULT_LINE_TYPE if it is null
     */
    public int getLineStyle() {
        return geoElement != null ? geoElement.getLineType() : EuclidianStyleConstants.DEFAULT_LINE_TYPE;
    }

    /**
     * @param style
     *         set the line style for all selected geoElements
     */
    public void setLineStyle(int style) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setLineType(style);
            geo.updateVisualStyleRepaint(GProperty.LINE_STYLE);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the current point style of the geoElement, or with POINT_STYLE_DOT if it is null
     */
    public int getPointStyle() {
		if (!(geoElement instanceof PointProperties)) {
            return EuclidianStyleConstants.POINT_STYLE_DOT;
        }

        return ((PointProperties) geoElement).getPointStyle();
    }

    /**
     * @param pointStyle
     *         set the point style for all selected geoElements
     */
    public void setPointStyle(int pointStyle) {
        for (GeoElement geo : geoElementsList) {
            ((PointProperties) geo).setPointStyle(pointStyle);
            geo.updateVisualStyleRepaint(GProperty.POINT_STYLE);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the point size or line thickness, depending on the geoElement's type
     */
    public int getSize() {
        if (geoElement == null) {
            return EuclidianStyleConstants.DEFAULT_LINE_THICKNESS;
        }

        if (geoElement instanceof PointProperties) {
            return ((PointProperties) geoElement).getPointSize();
        }
        return geoElement.getLineThickness();
    }

    /**
     * @param size
     *         set the size of the geoElement depending on if it is Point or Line
     */
    public void setSize(int size) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            if (geo instanceof PointProperties) {
                ((PointProperties) geo).setPointSize(size + 1);
            } else {
                geo.setLineThickness(size + getMinSize());
            }
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    /**
     * @param size
     *         point size to set
     */
    public void setPointSize(int size) {
        for (GeoElement geo : geoElementsList) {
            if (geo instanceof PointProperties) {
                ((PointProperties) geo).setPointSize(size + 1);
            }
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    /**
     * @param size
     *         line thickness to set
     */
    public void setLineThickness(int size) {
        for (GeoElement geo : geoElementsList) {
            geo.setLineThickness(size + geo.getMinimumLineThickness());
            geo.updateRepaint();
        }
        app.setPropertiesOccured();
    }

    /**
     * @return the minimum size of the point size or the line thickness depending of the geoElement's type
     */
    public int getMinSize() {
        if (geoElement == null || geoElement instanceof PointProperties) {
            return 1;
        }

        return geoElement.getMinimumLineThickness();
    }

    /**
     * @return max size
     */
    public int getMaxSize() {
        return 9;
    }

    /**
     * @return whether the geoElement is fixed or not
     */
    public boolean isObjectFixed() {
        return geoElement != null && geoElement.isLocked();
    }

    /**
     * @param objectFixed
     *         geoElement should be fixed or not
     */
    public void setObjectFixed(boolean objectFixed) {
        if (geoElement == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            geo.setFixed(objectFixed);
        }

        app.setPropertiesOccured();
    }

    /**
     * @param name
     *         new name of the geoElement
     */
    public void rename(String name) {
        if (geoElement != null) {
            GeoElement geo;
            if (geoElementsList.size() == 1) {
                geo = geoElementsList.get(0);
            } else {
                return; // should not happen...
            }

            try {
                String checked = geo.getKernel().getAlgebraProcessor().parseLabel(name);
                if (LabelManager.checkName(geo, checked)) {
                    geo.rename(checked);
                    geo.updateRepaint();
                    app.setPropertiesOccured();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @return name description of the geoElement or empty string if it is null
     */
    public String getNameDescription() {
        return geoElement != null ? geoElement.getNameDescription() : "";
    }

    /**
     * @return the name of the geoElement or empty string if it is null
     */
    public String getName() {
        return geoElement != null ? geoElement.getLabelSimple() : "";
    }

    /**
     * @return the minimum value of the slider, default is -5
     */
    public double getSliderMin() {
        return geoElement != null ? ((GeoNumeric) geoElement).getIntervalMin() : -5;
    }

    /**
     * @param min
     *         minimum value for the slider
     */
    public void setSliderMin(String min) {
        if (geoElement == null) {
            return;
        }
        GeoNumberValue num = app.getKernel().getAlgebraProcessor().evaluateToNumeric(min, false);
        if (num == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            ((GeoNumeric) geo).setIntervalMin(num);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the maximum value of the slider, default is 5
     */
    public double getSliderMax() {
        return geoElement != null ? ((GeoNumeric) geoElement).getIntervalMax() : 5;
    }

    /**
     * @param max
     *         maximum value for the slider
     */
    public void setSliderMax(String max) {
        if (geoElement == null) {
            return;
        }
        GeoNumberValue num = app.getKernel().getAlgebraProcessor().evaluateToNumeric(max, false);
        if (num == null) {
            return;
        }

        for (GeoElement geo : geoElementsList) {
            ((GeoNumeric) geo).setIntervalMax(num);
        }

        app.setPropertiesOccured();
    }

    /**
     * @return the step/increment value of the slider, default is 0.1
     */
    public double getSliderIncrement() {
        return geoElement != null ? geoElement.getAnimationStep() : 0.1;
    }

    /**
     * @param increment
     *         step value for the slider
     */
    public void setSliderIncrement(String increment) {
        if (geoElement == null) {
            return;
        }
        double step = app.getKernel().getAlgebraProcessor().evaluateToDouble(increment);
        boolean isNaN = Double.isNaN(step);

        for (GeoElement geo : geoElementsList) {
            geo.setAnimationStep(step);
            setSliderAutoStep(geo, isNaN);
        }

        app.setPropertiesOccured();
    }

    /**
     * @param geoElement
     *         a single geoElement
     * @param autoStep
     *         boolean, which defines whether the slider's autostep should be turned on or not
     */
    private void setSliderAutoStep(GeoElement geoElement, boolean autoStep) {
        if (geoElement instanceof GeoNumeric) {
            ((GeoNumeric) geoElement).setAutoStep(autoStep);
        }
    }

    /**
     * @return the current alpha value of the geoElement
     */
    public float getAlpha() {
        return geoElement != null ? (float) geoElement.getAlphaValue() : 1;
    }

    /**
     * @param alpha
     *         alpha value to be set for the geoElement, it should be between 0 and 100
     */
    public void setAlpha(float alpha) {
        if (geoElement == null) {
            return;
        }

        EuclidianStyleBarStatic.applyColor(geoElementsList, geoElement.getObjectColor(), alpha, app);

        app.setPropertiesOccured();
    }

    /**
     * @return whether the selected geoElements are all fillable or not
     */
    public boolean isFillable() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!geo.isFillable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return whether the geoElement is shown or not
     */
    public boolean isObjectShown() {
        return geoElement != null && geoElement.isEuclidianVisible();
    }

    /**
     * @param show
     *         whether the selected geoElements should be shown or not
     */
    public void showObject(boolean show) {
        if (geoElement != null) {
            for (GeoElement geo : geoElementsList) {
                geo.setEuclidianVisible(show);
                geo.updateRepaint();
            }
        }
    }

    /**
     * @return whether all selected geoElements has further style or not
     */
    public boolean hasFurtherStyle() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!hasFurtherStyle(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param geo
     *         a single geoElement
     * @return whether the geoElement has further style or not
     */
    private boolean hasFurtherStyle(GeoElement geo) {
        return !(geo instanceof GeoText || geo instanceof GeoInputBox);
    }

    /**
     * @return whether the tracing is turned on for the geoElement or not
     */
    public boolean isTraceOn() {
        return geoElement != null && geoElement.getTrace();
    }

    /**
     * @return whether all selected geoElements are traceable or not
     */
    public boolean isTraceable() {
        if (geoElement == null) {
            return false;
        }

        for (GeoElement geo : geoElementsList) {
            if (!geo.isTraceable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param checked
     *         whether the tracing should be turned on for the selected geoElements or not
     */
    public void setTrace(boolean checked) {
        if (geoElement != null && geoElement.isTraceable()) {
            for (GeoElement geo : geoElementsList) {
                ((Traceable) geo).setTrace(checked);
            }
        }
    }

    /**
     * @return whether the selected geoElements are all sliders or not
     */
    public boolean hasSliderProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!hasSliderProperties(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param geo
     *         the GeoElement to check if it is a slider or not
     * @return if the passed geo if slider or not
     */
    private boolean hasSliderProperties(GeoElement geo) {
        return geo instanceof GeoNumeric
                && ((GeoNumeric) geo).getIntervalMinObject() != null
                && geo.isIndependent();
    }

    /**
     * @return true if all the selected geoElement is an instance of the PointProperties
     */
    public boolean hasPointProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!PointStyleModel.match(geo)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @return true if all the selected geoElement is a GeoFunction
     */
    public boolean hasFunctionProperties() {
        for (GeoElement geo : geoElementsList) {
            if (geo instanceof GeoList) {
                GeoElement elementForProperties = geo.getGeoElementForPropertiesDialog();
                if (!(elementForProperties instanceof GeoFunction)) {
                    return false;
                }
            } else if (!(geo instanceof GeoFunction)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the all the selected geoElements have line properties
     */
    public boolean hasLineProperties() {
        for (GeoElement geo : geoElementsList) {
            if (!LineStyleModel.match(geo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return with the selected geoElement
     */
    public GeoElement getGeoElement() {
        return geoElement;
    }


    /**
     * @param geo
     *         initialize geoElement
     */
    public void setGeoElement(GeoElement geo) {
        geoElement = geo;
    }

    /**
     * @return with the the geoElementsList which are the selected geos
     */
    protected ArrayList<GeoElement> getGeoElementsList() {
        return geoElementsList;
    }

    /**
     * @param geoList
     *         initialize geoElementsList
     */
    public void setGeoElementsList(ArrayList<GeoElement> geoList) {
        geoElementsList = geoList;
    }

    /**
     * Delete the selected geoElements
     */
    public void deleteGeoElements() {
        if (geoElement == null) {
            return;
        }
        app.deleteSelectedObjects(false);
    }

    /**
     * @return the localized type string if one item is selected, anyway "Selection"
     */
    public String getTranslatedTypeString() {
        if (geoElement == null) {
            return "";
        }

        if (geoElementsList.size() > 1) {
            return app.getLocalization().getMenu("Selection");
        }
        return geoElementsList.get(0).translatedTypeString();
    }

    /**
     * @return the label of the geoElement if only one geoElement is selected
     */
    public String getLabel() {
        if (geoElement == null || geoElementsList.size() > 1) {
            return null;
        }

        return geoElementsList.get(0).getLabelSimple();
    }
}
