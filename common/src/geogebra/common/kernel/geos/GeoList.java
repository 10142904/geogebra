/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.awt.Color;
import geogebra.common.awt.Font;
import geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.PathMoverGeneric;
import geogebra.common.kernel.PathNormalizer;
import geogebra.common.kernel.PathOrPoint;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.algos.AlgoDependentListInterface;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoMacroInterface;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ListValue;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;

/**
 * List of GeoElements
 */
public class GeoList extends GeoElement implements ListValue, LineProperties,
		PointProperties, TextProperties, Traceable, Path, Transformable,
		SpreadsheetTraceable {

	public final static GeoClass ELEMENT_TYPE_MIXED = GeoClass.DEFAULT;

	public boolean trace;

	private static String STR_OPEN = "{";
	private static String STR_CLOSE = "}";

	// GeoElement list members
	private final ArrayList<GeoElement> geoList;

	// lists will often grow and shrink dynamically,
	// so we keep a cacheList of all old list elements
	private final ArrayList<GeoElement> cacheList;

	private boolean isDefined = true;
	private boolean isDrawable = true;
	private GeoClass elementType = ELEMENT_TYPE_MIXED;

	/**
	 * Whether this lists show all properties in the properties dialog. This is
	 * just recommended for the default GeoList in order to show all possible
	 * properties in the default configuration dialog.
	 */
	private boolean showAllProperties = false;

	private ArrayList<GeoElement> colorFunctionListener; // Michael Borcherds
															// 2008-04-02

	public GeoList(final Construction c) {
		this(c, 20);
	}

	private GeoList(final Construction c, final int size) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		geoList = new ArrayList<GeoElement>(size);
		cacheList = new ArrayList<GeoElement>(size);
		setEuclidianVisible(false);
	}

	@Override
	public void setParentAlgorithm(final AlgoElement algo) {
		super.setParentAlgorithm(algo);
		setEuclidianVisible(true);
	}

	public GeoList(final GeoList list) {
		this(list.cons, list.size());
		set(list);
	}

	@Override
	public String getClassName() {
		return "GeoList";
	}

	@Override
	public String getTypeString() {
		return "List";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.LIST;
	}

	/**
	 * Returns the element type of this list.
	 * 
	 * @return ELEMENT_TYPE_MIXED or GeoElement.GEO_CLASS_xx constant
	 */
	public GeoClass getElementType() {
		return elementType;
	}

	@Override
	public GeoElement copy() {
		return new GeoList(this);
	}

	@Override
	public void set(final GeoElement geo) {

		if (geo.isGeoNumeric()) { // eg SetValue[list, 2]
			// 1 -> first element
			selectedIndex = -1 + (int) ((GeoNumeric) geo).getDouble();
			isDefined = true;

			return;
		}
		final GeoList l = (GeoList) geo;

		if ((l.cons != cons) && isAlgoMacroOutput()) {
			// MACRO CASE
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in the list
			final AlgoMacroInterface algoMacro = (AlgoMacroInterface) getParentAlgorithm();
			algoMacro.initList(l, this);
		} else {
			// STANDARD CASE
			// copy geoList
			copyListElements(l);
		}

		isDefined = l.isDefined;
		elementType = l.elementType;
	}

	/**
	 * Set if the list should show all properties in the properties dialog. This
	 * is just recommended for the default list.
	 * 
	 * @param showAllProperties
	 */
	public void setShowAllProperties(final boolean showAllProperties) {
		this.showAllProperties = showAllProperties;
	}

	private void copyListElements(final GeoList otherList) {
		final int otherListSize = otherList.size();
		ensureCapacity(otherListSize);
		geoList.clear();

		for (int i = 0; i < otherListSize; i++) {
			final GeoElement otherElement = otherList.get(i);
			GeoElement thisElement = null;

			// try to reuse cached GeoElement
			if (i < cacheList.size()) {
				final GeoElement cachedGeo = cacheList.get(i);
				if (!cachedGeo.isLabelSet()
						&& (cachedGeo.getGeoClassType() == otherElement
								.getGeoClassType())) {
					// cached geo is unlabeled and has needed object type: use
					// it
					cachedGeo.set(otherElement);
					thisElement = cachedGeo;
				}
			}

			// could not use cached element -> get copy element
			if (thisElement == null) {
				thisElement = getCopyForList(otherElement);
			}

			// set list element
			add(thisElement);
		}
	}

	private GeoElement getCopyForList(final GeoElement geo) {
		if (geo.isLabelSet()) {
			// take original element
			return geo;
		} else {
			// create a copy of geo
			final GeoElement ret = geo.copyInternal(cons);
			ret.setParentAlgorithm(getParentAlgorithm());
			return ret;
		}
	}

	private void applyVisualStyle(final GeoElement geo) {

		if (!geo.isLabelSet()) {
			geo.setObjColor(getObjectColor());

			geo.setColorSpace(getColorSpace());

			// copy color function
			if (getColorFunction() != null) {
				geo.setColorFunction(getColorFunction());
			} else {
				geo.removeColorFunction();
			}

			geo.setLineThickness(getLineThickness());
			geo.setLineType(getLineType());

			if (geo instanceof PointProperties) {
				((PointProperties) geo).setPointSize(getPointSize());
				((PointProperties) geo).setPointStyle(getPointStyle());
			}

			if (geo instanceof TextProperties) {
				((TextProperties) geo).setFontSize(getFontSize());
				((TextProperties) geo).setFontStyle(getFontStyle());
				((TextProperties) geo).setSerifFont(isSerifFont());
				if (useSignificantFigures) {
					((TextProperties) geo).setPrintFigures(getPrintFigures(),
							false);
				} else {
					((TextProperties) geo).setPrintDecimals(getPrintDecimals(),
							false);
				}

			}

			geo.setFillType(fillType);
			geo.setHatchingAngle(hatchingAngle);
			geo.setHatchingDistance(hatchingDistance);
			geo.setImageFileName(getGraphicsAdapter().getImageFileName());
			geo.setAlphaValue(getAlphaValue());

			geo.setLayer(getLayer());

			// copy ShowObjectCondition, unless it generates a
			// CirclularDefinitionException
			try {
				geo.setShowObjectCondition(getShowObjectCondition());
			} catch (final Exception e) {
			}

			setElementEuclidianVisible(geo, isSetEuclidianVisible());
		}
	}

	@Override
	public final void removeColorFunction() {
		super.removeColorFunction();

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.removeColorFunction();
			}
		}
	}

	@Override
	public final void setColorFunction(final GeoList col) {
		super.setColorFunction(col);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setColorFunction(col);
			}
		}

	}

	@Override
	public final void setColorSpace(final int colorSpace) {
		super.setColorSpace(colorSpace);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setColorSpace(colorSpace);
			}
		}

	}

	/*
	 * we DON'T want to do this, as objects without label set eg the point in
	 * this {(1,1)} are drawn by the list public final void setLayer(int layer)
	 * { super.setLayer(layer);
	 * 
	 * if (geoList == null || geoList.size() == 0) return;
	 * 
	 * int size = geoList.size(); for (int i=0; i < size; i++) { GeoElement geo
	 * = (GeoElement)geoList.get(i); if (!geo.isLabelSet()) geo.setLayer(layer);
	 * }
	 * 
	 * }
	 */

	@Override
	public final void setShowObjectCondition(final GeoBoolean bool)
			throws CircularDefinitionException {
		super.setShowObjectCondition(bool);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setShowObjectCondition(bool);
			}
		}

	}

	@Override
	public void setVisualStyle(final GeoElement style) {
		super.setVisualStyle(style);

		// set point style
		if (style instanceof PointProperties) {
			setPointSize(((PointProperties) style).getPointSize());
			setPointStyle(((PointProperties) style).getPointStyle());
		}

		// set visual style
		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}
		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setVisualStyle(style);
			}
		}
	}

	@Override
	public void setObjColor(final Color color) {
		super.setObjColor(color);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			if (!geo.isLabelSet()) {
				geo.setObjColor(color);
			}
		}
	}

	@Override
	public void setBackgroundColor(final Color color) {
		super.setBackgroundColor(color);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			if (!geo.isLabelSet()) {
				geo.setBackgroundColor(color);
			}
		}
	}

	@Override
	public void setEuclidianVisible(final boolean visible) {
		super.setEuclidianVisible(visible);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		final int size = geoList.size();
		for (int i = 0; i < size; i++) {
			final GeoElement geo = get(i);
			setElementEuclidianVisible(geo, visible);
		}
	}

	private static void setElementEuclidianVisible(final GeoElement geo,
			final boolean visible) {
		if (!geo.isLabelSet() && !geo.isGeoNumeric()) {
			geo.setEuclidianVisible(visible);
		}
	}

	/**
	 * Returns this GeoList as a MyList object.
	 */
	public MyList getMyList() {
		final int size = geoList.size();
		final MyList myList = new MyList(kernel, size);

		for (int i = 0; i < size; i++) {
			myList.addListElement(new ExpressionNode(kernel, geoList.get(i)));
		}

		return myList;
	}

	@Override
	final public boolean isDefined() {
		return isDefined;
	}

	public void setDefined(final boolean flag) {
		isDefined = flag;

		if (!isDefined) {
			final int size = geoList.size();
			for (int i = 0; i < size; i++) {
				final GeoElement geo = geoList.get(i);
				if (!geo.isLabelSet()) {
					geo.setUndefined();
				}
			}
		}
	}

	@Override
	public void setUndefined() {
		setDefined(false);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined() && isDrawable();
	}

	@Override
	public boolean isDrawable() {
		return isDrawable;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	public final void clear() {
		geoList.clear();
	}

	/*
	 * free up memory and set undefined
	 */
	public final void clearCache() {
		if (cacheList.size() > 0) {
			for (int i = 0; i < cacheList.size(); i++) {
				final GeoElement geo = cacheList.get(i);
				if ((geo != null) && !geo.isLabelSet()) {
					geo.remove();
				}
			}
		}
		cacheList.clear();
		clear();
		setUndefined();
		System.gc();
	}

	public final void add(final GeoElement geoI) {
		// add geo to end of list
		final GeoElement geo = geoI;
		geoList.add(geo);

		/*
		 * // needed for Corner[Element[text // together with swapping these
		 * lines over in MyXMLio: //kernel.updateConstruction();
		 * //kernel.setNotifyViewsActive(oldVal);
		 * 
		 * if (geo.isGeoText()) {
		 * ((GeoText)geo).setNeedsUpdatedBoundingBox(true); }
		 */

		// add to cache
		final int pos = geoList.size() - 1;
		if (pos < cacheList.size()) {
			cacheList.set(pos, geo);
		} else {
			cacheList.add(geo);
		}

		// init element type
		if (pos == 0) {
			isDrawable = geo.isDrawable();
			elementType = geo.getGeoClassType();
		}
		// check element type
		else if (elementType != geo.getGeoClassType()) {
			elementType = ELEMENT_TYPE_MIXED;
		}
		isDrawable = isDrawable && geo.isDrawable();

		// set visual style of this list
		applyVisualStyle(geo);
		// if (!geo.isLabelSet())
		// geo.setVisualStyle(this);

	}

	/**
	 * Removes geo from this list. Note: geo is not removed from the
	 * construction.
	 * 
	 * @param geo
	 *            element to be removed
	 */
	public final void remove(final GeoElement geo) {
		geoList.remove(geo);
	}

	/**
	 * Removes i-th element from this list. Note: this element is not removed
	 * from the construction.
	 * 
	 * @param index
	 *            position of element to be removed
	 */
	public final void remove(final int index) {
		geoList.remove(index);
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *            element position
	 * @return the element at the specified position in this list.
	 */
	final public GeoElement get(final int index) {
		return geoList.get(index);
	}

	/**
	 * Returns the element at the specified position in this (2D) list.
	 * 
	 * @param index
	 *            element position -- row
	 * @param index2
	 *            element position -- column
	 * @return the element at the specified position in this (2D) list.
	 */
	final public GeoElement get(final int index, final int index2) {
		return ((GeoList) geoList.get(index)).get(index2);
	}

	/**
	 * Tries to return this list as an array of double values
	 * 
	 * @return array of double values from this list
	 */
	public double[] toDouble() {
		try {
			final double[] valueArray = new double[geoList.size()];
			for (int i = 0; i < valueArray.length; i++) {
				valueArray[i] = ((NumberValue) geoList.get(i)).getDouble();
			}
			return valueArray;
		} catch (final Exception e) {
			return null;
		}
	}

	final public void ensureCapacity(final int size) {
		geoList.ensureCapacity(size);
		cacheList.ensureCapacity(size);
	}

	final public int size() {
		return geoList.size();
	}

	final public int getCacheSize() {
		return cacheList.size();
	}

	/**
	 * Returns the cached element at the specified position in this list's
	 * cache.
	 * 
	 * @param index
	 *            element position
	 * @return cached alement at given position
	 */
	final public GeoElement getCached(final int index) {
		return cacheList.get(index);
	}

	@Override
	public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(buildValueString());
		return sbToString.toString();
	}

	@Override
	public String toStringMinimal() {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString.toString();
		}

		// first (n-1) elements
		final int lastIndex = geoList.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				final GeoElement geo = geoList.get(i);

				sbBuildValueString.append(geo.getAlgebraDescriptionRegrOut());
				sbBuildValueString.append(" ");
			}

			// last element
			final GeoElement geo = geoList.get(lastIndex);
			sbBuildValueString.append(geo.getAlgebraDescriptionRegrOut());
		}

		return sbBuildValueString.toString();
	}

	StringBuilder sbToString = new StringBuilder(50);

	@Override
	public String toValueString() {
		return buildValueString().toString();
	}

	private StringBuilder buildValueString() {
		sbBuildValueString.setLength(0);
		if (!isDefined) {
			sbBuildValueString.append("?");
			return sbBuildValueString;
		}

		if (kernel.getCASPrintForm().equals(StringType.LATEX)) {
			sbBuildValueString.append("\\left\\");
		}
		if (kernel.getCASPrintForm().equals(StringType.MPREDUCE)) {
			sbBuildValueString.append("list(");
		} else {
			sbBuildValueString.append(STR_OPEN);
		}
		// first (n-1) elements
		final int lastIndex = geoList.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				final GeoElement geo = geoList.get(i);
				sbBuildValueString.append(geo.toOutputValueString());
				sbBuildValueString.append(AbstractApplication.unicodeComma);
				sbBuildValueString.append(" ");
			}

			// last element
			final GeoElement geo = geoList.get(lastIndex);
			sbBuildValueString.append(geo.toOutputValueString());
		}
		if (kernel.getCASPrintForm().equals(StringType.LATEX)) {
			sbBuildValueString.append("\\right\\");
		}
		if (kernel.getCASPrintForm().equals(StringType.MPREDUCE)) {
			sbBuildValueString.append(")");
		} else {
			sbBuildValueString.append(STR_CLOSE);
		}
		return sbBuildValueString;
	}

	private final StringBuilder sbBuildValueString = new StringBuilder(50);

	@Override
	public boolean isGeoList() {
		return true;
	}

	@Override
	public boolean isListValue() {
		return true;
	}

	/**
	 * save object in XML format
	 */
	@Override
	public final void getXML(final StringBuilder sb) {

		// an independent list needs to add
		// its expression itself
		// e.g. {1,2,3}
		if (isIndependent() && (getDefaultGeoType() < 0)) {
			sb.append("<expression");
			sb.append(" label =\"");
			sb.append(StringUtil.encodeXML(label));
			sb.append("\" exp=\"");
			sb.append(StringUtil.encodeXML(toValueString()));
			sb.append("\"/>\n");
		}

		sb.append("<element");
		sb.append(" type=\"list\"");
		sb.append(" label=\"");
		sb.append(label);
		if (getDefaultGeoType() >= 0) {
			sb.append("\" default=\"");
			sb.append(getDefaultGeoType());
		}
		sb.append("\">\n");
		getXMLtags(sb);

		if (selectedIndex != 0) {
			sb.append("\t<selectedIndex val=\"");
			sb.append(selectedIndex);
			sb.append("\"/>\n");
		}

		// point style
		sb.append("\t<pointSize val=\"");
		sb.append(pointSize);
		sb.append("\"/>\n");

		sb.append("\t<pointStyle val=\"");
		sb.append(pointStyle);
		sb.append("\"/>\n");

		// font settings
		if (serifFont || (fontSize != 0) || (fontStyle != 0)) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			sb.append("\" size=\"");
			sb.append(fontSize);
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}

		// print decimals
		if ((printDecimals >= 0) && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}

		// print significant figures
		if ((printFigures >= 0) && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}
		sb.append("</element>\n");

	}

	/**
	 * Registers geo as a listener for updates of this boolean object. If this
	 * object is updated it calls geo.updateConditions()
	 * 
	 * @param geo
	 */
	public void registerColorFunctionListener(final GeoElement geo) {
		if (colorFunctionListener == null) {
			colorFunctionListener = new ArrayList<GeoElement>();
		}
		colorFunctionListener.add(geo);
	}

	public void unregisterColorFunctionListener(final GeoElement geo) {
		if (colorFunctionListener != null) {
			colorFunctionListener.remove(geo);
		}
	}

	/**
	 * Calls super.update() and update() for all registered condition listener
	 * geos. // Michael Borcherds 2008-04-02
	 */
	@Override
	public void update() {
		super.update();
		// update all registered locatables (they have this point as start
		// point)
		if (colorFunctionListener != null) {
			// Application.debug("GeoList update listeners");
			for (int i = 0; i < colorFunctionListener.size(); i++) {
				final GeoElement geo = colorFunctionListener.get(i);
				// kernel.notifyUpdate(geo);
				// geo.toGeoElement().updateCascade();
				geo.updateVisualStyle();
			}
			// kernel.notifyRepaint();
		}
	}

	/**
	 * Tells conidition listeners that their condition is removed and calls
	 * super.remove() // Michael Borcherds 2008-04-02
	 */
	@Override
	public void doRemove() {

		if (colorFunctionListener != null) {
			// copy conditionListeners into array
			final Object[] geos = colorFunctionListener.toArray();
			colorFunctionListener.clear();

			// tell all condition listeners
			for (int i = 0; i < geos.length; i++) {
				final GeoElement geo = (GeoElement) geos[i];
				geo.removeColorFunction();
				kernel.notifyUpdate(geo);
			}
		}

		super.doRemove();
	}

	/**
	 * return whether this list equals GeoList list Michael Borcherds 2008-04-12
	 */
	@Override
	final public boolean isEqual(final GeoElement geo) {

		if (!geo.isGeoList()) {
			return false;
		}

		final GeoList list = (GeoList) geo;

		// check sizes
		if (geoList.size() != list.size()) {
			return false;
		}

		// check each element
		for (int i = 0; i < list.geoList.size(); i++) {
			final GeoElement geoA = geoList.get(i);
			final GeoElement geoB = list.get(i);

			if (!geoA.isEqual(geoB)) {
				return false;
				/*
				 * if (geoA.isGeoNumeric() && geoB.isGeoNumeric()) { if
				 * (!((GeoNumeric)geoA).equals((GeoNumeric)geoB)) return false;
				 * } else if (geoA.isGeoConicPart() && geoB.isGeoConicPart()) {
				 * if (!((GeoConicPart)geoA).equals((GeoConicPart)geoB)) return
				 * false; } else if (geoA.isGeoConic() && geoB.isGeoConic()) {
				 * if (!((GeoConic)geoA).equals((GeoConic)geoB)) return false; }
				 * else if (geoA.isGeoAngle() && geoB.isGeoAngle()) { if
				 * (!((GeoAngle)geoA).equals((GeoAngle)geoB)) return false; }
				 * else if (geoA.isGeoPoint() && geoB.isGeoPoint()) { if
				 * (!((GeoPoint)geoA).equals((GeoPoint)geoB)) return false; }
				 * else if (geoA.isGeoPolygon() && geoB.isGeoPolygon()) { if
				 * (!((GeoPolygon)geoA).equals((GeoPolygon)geoB)) return false;
				 * } else if (geoA.isGeoSegment() && geoB.isGeoSegment()) { if
				 * (!((GeoSegment)geoA).equals((GeoSegment)geoB)) return false;
				 * } else if (geoA.isGeoList() && geoB.isGeoList()) { if
				 * (!((GeoList)geoA).equals((GeoList)geoB)) return false; } else
				 * if (!geoA.equals(geoB)) return false;
				 */
			}
		}

		// all list elements equal
		return true;
	}

	@Override
	public void setZero() {
		geoList.clear();
	}

	@Override
	public void setLineThickness(final int thickness) {

		super.setLineThickness(thickness);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineThickness(thickness);
			}
		}

		// Application.debug("GeoList.setLineThickness "+thickness);
	}

	@Override
	public int getLineThickness() {
		return super.getLineThickness();
	}

	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals
	 *         etc)
	 */
	@Override
	public int getMinimumLineThickness() {
		if ((geoList == null) || (geoList.size() == 0)) {
			return 1;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				if (geo.getMinimumLineThickness() == 1) {
					return 1;
				}
			}
		}

		return 0;
	}

	@Override
	public void setLineType(final int type) {

		super.setLineType(type);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setLineType(type);
			}
		}

		// Application.debug("GeoList.setLineType");

	}

	@Override
	public int getLineType() {
		return super.getLineType();
	}

	public int pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	private int pointStyle = -1; // use global option if -1

	public void setPointSize(final int size) {
		pointSize = size;

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet() && (geo instanceof PointProperties)) {
				((PointProperties) geo).setPointSize(size);
			}
		}
	}

	public int getPointSize() {
		return pointSize;
	}

	public void setPointStyle(final int style) {
		pointStyle = style;

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet() && (geo instanceof PointProperties)) {
				((PointProperties) geo).setPointStyle(style);
			}
		}
	}

	@Override
	public float getAlphaValue() {
		if (super.getAlphaValue() == -1) {
			// no alphaValue set
			// so we need to set it to that of the first element, if there is
			// one
			if ((geoList != null) && (geoList.size() > 0)) {

				// get alpha value of first element
				final float alpha = geoList.get(0).getAlphaValue();

				// Application.debug("setting list alpha to "+alpha);

				super.setAlphaValue(alpha);

				// set all the other elements in the list
				// if appropriate
				if (geoList.size() > 1) {
					for (int i = 1; i < geoList.size(); i++) {
						final GeoElement geo = geoList.get(i);
						if (!geo.isLabelSet()) {
							geo.setAlphaValue(alpha);
						}
					}

				}
			} else {
				return -1.0f;
			}
		}

		return super.getAlphaValue();
	}

	@Override
	public void setAlphaValue(final float alpha) {

		if (alpha == -1) {
			// wait until we have a GeoElement in the list to use
			// see getAlphaValue()
			alphaValue = -1;
			return;
		}

		super.setAlphaValue(alpha);

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setAlphaValue(alpha);
			}
		}

	}

	public int getPointStyle() {
		return pointStyle;
	}

	@Override
	public boolean isFillable() {
		if ((geoList == null) || (geoList.size() == 0)) {
			return false;
		}

		boolean someFillable = false;
		boolean allLabelsSet = true;

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (geo.isFillable()) {
				someFillable = true;
			}
			if (!geo.isLabelSet()) {
				allLabelsSet = false;
			}
		}

		return someFillable && !allLabelsSet;
	}

	@Override
	public GeoElement getGeoElementForPropertiesDialog() {
		if ((geoList.size() > 0) && (elementType != ELEMENT_TYPE_MIXED)) {
			return get(0).getGeoElementForPropertiesDialog(); // getGeoElementForPropertiesDialog()
																// to cope with
																// lists of
																// lists
		} else {
			return this;
		}
	}

	/*
	 * is this a list in the form { {1,2}, {3,4}, {5,6} } etc
	 */
	public boolean isMatrix() {

		if (!getElementType().equals(GeoClass.LIST) || (size() == 0)) {
			return false;
		}

		final GeoElement geo0 = get(0);
		if (geo0.isGeoList()) {
			final int length = ((GeoList) geo0).size();
			if (length == 0) {
				return false;
			} else {
				if (size() > 0) {
					for (int i = 0; i < size(); i++) {
						final GeoElement geoi = get(i);
						// Application.debug(((GeoList)geoi).get(0).getGeoClassType()+"");
						if (!get(i).isGeoList()
								|| (((GeoList) geoi).size() == 0)
								|| (((GeoList) geoi).size() != length)) {
							return false;
						} else {
							for (int j = 0; j < ((GeoList) geoi).size(); j++) {
								final GeoElement geoij = ((GeoList) geoi)
										.get(j);
								if (!geoij.getGeoClassType().equals(
										GeoClass.NUMERIC)
										&& !geoij.getGeoClassType().equals(
												GeoClass.FUNCTION)
										&& !geoij.getGeoClassType().equals(
												GeoClass.FUNCTION_NVAR)) {
									return false;
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	// font options
	private boolean serifFont = false;
	private int fontStyle = Font.PLAIN;
	private int fontSize = 0; // size relative to default font size
	private int printDecimals = -1;
	private int printFigures = -1;
	public boolean useSignificantFigures = false;

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(final int size) {
		fontSize = size;

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setFontSize(size);
			}
		}
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(final int fontStyle) {
		this.fontStyle = fontStyle;

		if ((geoList == null) || (geoList.size() == 0)) {
			return;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setFontStyle(fontStyle);
			}
		}
	}

	final public int getPrintDecimals() {
		return printDecimals;
	}

	final public int getPrintFigures() {
		return printFigures;
	}

	public void setPrintDecimals(final int printDecimals, final boolean update) {
		this.printDecimals = printDecimals;
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setPrintDecimals(printDecimals, update);
			}
		}
	}

	public void setPrintFigures(final int printFigures, final boolean update) {
		this.printFigures = printFigures;
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setPrintFigures(printFigures, update);
			}
		}
	}

	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}

	public boolean isSerifFont() {
		return serifFont;
	}

	public void setSerifFont(final boolean serifFont) {
		this.serifFont = serifFont;
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof TextProperties) && !geo.isLabelSet()) {
				((TextProperties) geo).setSerifFont(serifFont);
			}
		}
	}

	@Override
	public void setHatchingAngle(final int angle) {
		super.setHatchingAngle(angle);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setHatchingAngle(angle);
			}
		}
	}

	@Override
	public void setHatchingDistance(final int distance) {
		super.setHatchingDistance(distance);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setHatchingDistance(distance);
			}
		}
	}

	@Override
	public void setFillType(final int type) {
		super.setFillType(type);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setFillType(type);
			}
		}
	}

	@Override
	public void setFillImage(final String filename) {
		super.setFillImage(filename);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setFillImage(filename);
			}
		}
	}

	@Override
	public void setImageFileName(final String filename) {
		super.setImageFileName(filename);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (!geo.isLabelSet()) {
				geo.setImageFileName(filename);
			}
		}
	}

	/*
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the line properties
	 */
	public boolean showLineProperties() {
		if (showAllProperties) {
			return true;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof LineProperties) && !geo.isLabelSet()) {
				return true;
			}
		}

		return false;
	}

	/*
	 * for a list like this: {Circle[B, A], (x(A), y(A)), "text"} we want to be
	 * able to set the point properties
	 */
	public boolean showPointProperties() {
		if (showAllProperties) {
			return true;
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if ((geo instanceof PointProperties) && !geo.isLabelSet()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isVector3DValue() {
		return false;
	}

	@Override
	public String toLaTeXString(final boolean symbolic) {

		if (isMatrix()) {

			// int rows = size();
			final int cols = ((GeoList) get(0)).size();

			final StringBuilder sb = new StringBuilder();
			sb.append("\\left(\\begin{array}{");
			// eg rr
			for (int i = 0; i < cols; i++) {
				sb.append('r');
			}
			sb.append("}");
			for (int i = 0; i < size(); i++) {
				final GeoList geo = (GeoList) get(i);
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).toLaTeXString(symbolic));
					if (j < (geo.size() - 1)) {
						sb.append("&");
					}
				}
				sb.append("\\\\");
			}
			sb.append(" \\end{array}\\right)");
			return sb.toString();
			// return "\\begin{array}{ll}1&2 \\\\ 3&4 \\\\ \\end{array}";
		}

		return super.toLaTeXString(symbolic);

	}

	@Override
	protected void getXMLtags(final StringBuilder sb) {
		super.getXMLtags(sb);

		getLineStyleXML(sb);
		getScriptTags(sb);

	}

	/*
	 * for CmdSetLabelMode
	 * 
	 * removed: no point setting labelmode for objects with no labels and we
	 * don't want to set labelmode for objects with labels... public void
	 * setLabelMode(int mode) { super.setLabelMode(mode);
	 * 
	 * for (int i = 0; i < geoList.size(); i++) { GeoElement geo = (GeoElement)
	 * geoList.get(i); if (!geo.isLabelSet()) geo.setLabelMode(mode); }
	 * 
	 * }
	 */

	// G.Sturr 2010-6-12
	// Selection index for lists used in comboBoxes
	private int selectedIndex = 0;

	private int closestPointIndex;

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(final int selectedIndex0) {
		selectedIndex = selectedIndex0;
	}

	// END G.Sturr

	/*
	 * mathieu : for drawing 3D elements of the list
	 */
	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	/*
	 * when list is visible (as a combobox) this returns the element selected by
	 * the user or null if there's a problem
	 */
	public GeoElement getSelectedElement() {
		if ((selectedIndex > -1) && (selectedIndex < size())) {
			return get(selectedIndex);
		} else {
			return null;
		}
	}

	public void setTrace(final boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public boolean isLimitedPath() {
		return false;
	}

	@Override
	public boolean isPath() {
		return true;
	}

	/*
	 * adapted from GeoLocus
	 */
	public void pointChanged(final GeoPointND P) {
		// Application.debug("pointChanged",1);

		// GeoPoint P = (GeoPoint) PI;

		P.updateCoords();

		// update closestPointIndex
		getNearestPoint(P);

		final GeoElement geo = get(closestPointIndex);
		if (!(geo instanceof PathOrPoint)) {
			AbstractApplication.debug("TODO: " + geo.getClassName()
					+ " should implement PathOrPoint interface");
			return;
		}
		final PathOrPoint path = (PathOrPoint) get(closestPointIndex);

		path.pointChanged(P);

		final PathParameter pp = P.getPathParameter();

		// update path param
		// 0-1 for first obj
		// 1-2 for second
		// etc
		// Application.debug(pp.t+" "+path.getMinParameter()+" "+path.getMaxParameter());
		pp.t = closestPointIndex
				+ PathNormalizer.toNormalizedPathParameter(pp.t,
						path.getMinParameter(), path.getMaxParameter());
		// Application.debug(pp.t);

	}

	public void getNearestPoint(final GeoPointND p) {
		// Application.printStacktrace(p.inhomX+" "+p.inhomY);
		double distance = Double.POSITIVE_INFINITY;
		closestPointIndex = 0; // default - first object
		// double closestIndex = -1;
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			final double d = geo.distance(p);
			// Application.debug(i+" "+d+" "+distance+" "+getLabel());
			if (d < distance) {
				distance = d;
				closestPointIndex = i;
			}
		}

		// Application.debug("closestPointIndex="+closestPointIndex);

		// return get(closestPointIndex).getNearestPoint(p);
	}

	@Override
	public double distance(final GeoPoint2 p) {
		double distance = Double.POSITIVE_INFINITY;
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			final double d = geo.distance(p);
			if (d < distance) {
				distance = d;
			}
		}

		return distance;
	}

	public void pathChanged(final GeoPointND PI) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters()) {
			pointChanged(PI);
			return;
		}

		final PathParameter pp = PI.getPathParameter();

		double t = pp.getT();
		int n = (int) Math.floor(t);

		// check n is in a sensible range
		if ((n >= size()) || (n < 0)) {
			final double check = t - size();
			// t = size() when at very end of path
			// so check == 0 is OK, just need to set n = size() - 1
			if (check != 0.0) {
				AbstractApplication.debug("problem with path param "
						+ PI.getLabel());
			}
			n = (n < 0) ? 0 : size() - 1;
		}
		final PathOrPoint path = (PathOrPoint) get(n);

		pp.setT(PathNormalizer.toParentPathParameter(t - n,
				path.getMinParameter(), path.getMaxParameter()));

		// Application.debug("pathChanged "+n);

		path.pathChanged(PI);

		t = pp.getT();
		// Application.debug(PathNormalizer.toNormalizedPathParameter(t,
		// path.getMinParameter(), path.getMaxParameter()));
		pp.setT(PathNormalizer.toNormalizedPathParameter(t,
				path.getMinParameter(), path.getMaxParameter())
				+ n);

	}

	public boolean isOnPath(final GeoPointND PI, final double eps) {
		// Application.debug("isOnPath",1);
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (((PathOrPoint) geo).isOnPath(PI, eps)) {
				return true;
			}
		}
		return false;
	}

	public double getMinParameter() {
		return 0;
	}

	public double getMaxParameter() {
		return geoList.size();
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public boolean justFontSize() {
		return false;
	}

	@Override
	public boolean hasMoveableInputPoints(final EuclidianViewInterfaceSlim view) {
		// we don't want e.g. DotPlots to be dragged
		if (!((getParentAlgorithm() == null) || (getParentAlgorithm() instanceof AlgoDependentListInterface))) {
			return false;
		}
		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);

			if (geo.isGeoPoint()) {
				if (!geo.isMoveable()) {
					return false;
				}
			} else {
				// not point
				if (!geo.hasMoveableInputPoints(view)) {
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * allow lists like this to be dragged {Segment[A, B], Segment[B, C], (3.92,
	 * 4)}
	 */
	@Override
	public ArrayList<GeoPoint2> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view) {
		final ArrayList<GeoPoint2> al = new ArrayList<GeoPoint2>();

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);

			if (geo.isGeoPoint()) {
				final GeoPoint2 p = (GeoPoint2) geo;
				if (p.isMoveable() && !al.contains(p)) {
					al.add(p);
				}

			} else {
				final ArrayList<GeoPoint2> al2 = geo.getFreeInputPoints(view);

				if (al2 != null) {
					for (int j = 0; j < al2.size(); j++) {
						final GeoPoint2 p = al2.get(j);
						// make sure duplicates aren't added
						if (!al.contains(p)) {
							al.add(p);
						}
					}
				}
			}
		}
		return al;

	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public String getCASString(final boolean symbolic) {

		// isMatrix() is rather expensive, and we only need it
		// if we're using Maxima, so test for that first
		final StringType casPrinttype = kernel.getCASPrintForm();
		if ((!casPrinttype.equals(StringType.MAXIMA) && !casPrinttype
				.equals(StringType.MPREDUCE)) || !isMatrix()) {
			return super.getCASString(symbolic);
		}

		final StringBuilder sb = new StringBuilder();
		if (casPrinttype.equals(StringType.MAXIMA)) {
			sb.append("matrix(");
			for (int i = 0; i < size(); i++) {
				final GeoList geo = (GeoList) get(i);
				sb.append('[');
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(symbolic));
					if (j != (geo.size() - 1)) {
						sb.append(',');
					}
				}
				sb.append(']');
				if (i != (size() - 1)) {
					sb.append(',');
				}
			}
			sb.append(')');
		} else {
			sb.append("mat(");
			for (int i = 0; i < size(); i++) {
				final GeoList geo = (GeoList) get(i);
				sb.append("(");
				for (int j = 0; j < geo.size(); j++) {
					sb.append(geo.get(j).getCASString(symbolic));
					if (j != (geo.size() - 1)) {
						sb.append(',');
					}
				}
				sb.append(')');
				if (i != (size() - 1)) {
					sb.append(',');
				}
			}
			sb.append(')');
		}
		return sb.toString();
	}

	public boolean listContains(final GeoElement geo) {
		if (geoList == null) {
			return true;
		}
		return geoList.contains(geo);
	}

	@Override
	public boolean isLaTeXDrawableGeo(final String latexStr) {

		// check for matrix
		if (getElementType().equals(GeoClass.LIST)) {
			return true;
		}

		// don't check getGeoElementForPropertiesDialog
		// as we want matrices to use latex
		if (getElementType().equals(GeoClass.NUMERIC)) {
			return false;
		}

		return super.isLaTeXDrawableGeo(latexStr);
	}

	@Override
	public ArrayList<String> getColumnHeadings() {

		if (spreadsheetColumnHeadings == null) {
			spreadsheetColumnHeadings = new ArrayList<String>();
		} else {
			spreadsheetColumnHeadings.clear();
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (geo instanceof SpreadsheetTraceable) {
				final ArrayList<String> geoHead = geo.getColumnHeadings();
				for (int j = 0; j < geoHead.size(); j++) {
					spreadsheetColumnHeadings.add(geoHead.get(j));
				}
			}
		}

		return spreadsheetColumnHeadings;
	}

	/*
	 * default for elements implementing NumberValue interface eg GeoSegment,
	 * GeoPolygon
	 */
	@Override
	public ArrayList<GeoNumeric> getSpreadsheetTraceList() {

		if (spreadsheetTraceList == null) {
			spreadsheetTraceList = new ArrayList<GeoNumeric>();
		} else {
			spreadsheetTraceList.clear();
		}

		for (int i = 0; i < geoList.size(); i++) {
			final GeoElement geo = geoList.get(i);
			if (geo instanceof SpreadsheetTraceable) {
				final ArrayList<GeoNumeric> traces = ((SpreadsheetTraceable) geo)
						.getSpreadsheetTraceList();
				spreadsheetTraceList.addAll(traces);
			}
		}

		return spreadsheetTraceList;
	}

	public int performScriptActions() {
		int actions = 0;
		for(int i=0;i<size();i++){
			if(get(i) instanceof GeoScriptAction){
				((GeoScriptAction) get(i)).perform();
				actions++;
			}
			if(get(i) instanceof GeoList)
				actions+=((GeoList) get(i)).performScriptActions();
		}
		return actions;
	}

}