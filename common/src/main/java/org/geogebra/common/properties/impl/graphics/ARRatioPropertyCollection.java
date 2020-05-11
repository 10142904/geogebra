package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.PropertiesList;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyCollection;

import java.util.ArrayList;

public class ARRatioPropertyCollection extends AbstractProperty implements PropertyCollection {

    private PropertiesList collection;

    /**
     * Constructs a ar ratio property collection.
     * @param app
     *            application
     *
     * @param localization
     *            localization for the title
     */
    public ARRatioPropertyCollection(App app, Localization localization) {
        super(localization, "AR Ratio");

        EuclidianView3D euclidianView3D = (EuclidianView3D) app.getActiveEuclidianView();
        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new ARRatioProperty(localization, euclidianView3D));
        properties.add(new RatioUnitProperty(localization,
                euclidianView3D.getRenderer().getARManager()));

        collection = new PropertiesList(properties);
    }

    @Override
    public PropertiesList getProperties() {
        return collection;
    }
}
