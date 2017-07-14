/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package org.geogebra.common.kernel.kernelND;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgorithmSet;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.algos.DrawInformationAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.script.Script;
import org.geogebra.common.util.LaTeXCache;

/**
 * Common interface for all interfaces that represent GeoElements
 * 
 * @author Zbynek
 *
 */
public interface GeoElementND extends ExpressionValue {
	/**
	 * @param string
	 *            new label
	 */
	void setLabel(String string);

	/**
	 * Updates this geo
	 */
	void update();

	/**
	 * @param objectColor
	 *            object color
	 */
	void setObjColor(GColor objectColor);

	/**
	 * @param visible
	 *            whether should be visible in EV
	 */
	void setEuclidianVisible(boolean visible);

	/**
	 * @return true if this is visible in EV
	 */
	boolean isEuclidianVisible();

	/**
	 * @return true if label is visible
	 */
	boolean isLabelVisible();

	/**
	 * @return true if label was set
	 */
	public boolean isLabelSet();

	/**
	 * @param tpl
	 *            template
	 * @return label or definition command
	 */
	public String getLabel(StringTemplate tpl);

	/**
	 * @return true for infinite numbers / points
	 */
	public boolean isInfinite();

	/**
	 * Update visual style and notify kernel
	 * 
	 * @param prop
	 *            property being changed
	 */
	public void updateVisualStyle(GProperty prop);

	/**
	 * Remove this from construction
	 */
	public void remove();

	/**
	 * @return true if tracing to spreadsheet
	 */
	public boolean getSpreadsheetTrace();

	/**
	 * @param cons
	 *            construction
	 * @return copy of this element in given construction
	 */
	public GeoElement copyInternal(Construction cons);

	/**
	 * @return a copy of this geo
	 */
	public GeoElementND copy();

	/**
	 * @return true if this is free geo (noparent algo)
	 */
	public boolean isIndependent();

	/**
	 * @return parent algorithm
	 */
	public AlgoElement getParentAlgorithm();

	/**
	 * @return true if this is defined
	 */
	public boolean isDefined();

	/**
	 * Makes this geo undefined
	 */
	public void setUndefined();

	/**
	 * @param type
	 *            line type
	 */
	public void setLineType(int type);

	/**
	 * @param th
	 *            line thickness
	 */
	public void setLineThickness(int th);

	/**
	 * @return true if it has a line opacity value between 0 and 255
	 */
	public boolean hasLineOpacity();

	/**
	 * Sets the line opacity for this {@code GeoElement}. </br>
	 * 
	 * @param opacity
	 *            opacity value between 0 - 255
	 */
	public void setLineOpacity(int opacity);

	/**
	 * @return The value for the line opacity (0 - 255). </br>
	 *         The default value is 255 (opaque)
	 */
	public int getLineOpacity();

	/**
	 * @param b
	 *            true to make label visible
	 */
	public void setLabelVisible(boolean b);

	/**
	 * Returns whether this GeoElement is a point on a path.
	 * 
	 * @return true for points on path
	 */
	public boolean isPointOnPath();

	/**
	 * Returns whether this GeoElement is a point in a region
	 * 
	 * @return true for points on path
	 */
	public boolean isPointInRegion();

	/**
	 * @param p
	 *            point
	 * @return distance from point
	 */
	public double distance(GeoPointND p);

	/**
	 * Update this geo and all its descendants
	 */
	void updateCascade();

	/**
	 * Update and repaint this geo
	 */
	void updateRepaint();

	/**
	 * @return line type
	 */
	int getLineType();

	/**
	 * @return line thickness
	 */
	int getLineThickness();

	/**
	 * @return whether the complement should be filled
	 */
	boolean isInverseFill();

	/**
	 * @return animation step as double
	 */
	public double getAnimationStep();

	/**
	 * @return construction index
	 */
	int getConstructionIndex();

	/**
	 * @return set of algos that depend on this geo
	 */
	AlgorithmSet getAlgoUpdateSet();

	/**
	 * @return list of directly dependent algos
	 */
	ArrayList<AlgoElement> getAlgorithmList();

	/**
	 * @return whether the update set
	 */
	boolean hasAlgoUpdateSet();

	/**
	 * @return whether this is instance of GeoElement3D
	 */
	public boolean isGeoElement3D();

	/**
	 * 
	 * @return true if is region that produces 3D points
	 */
	public boolean isRegion3D();

	/**
	 * @return whether this is instance of GeoText
	 */
	public boolean isGeoText();

	/**
	 * @return label mode, may be GeoElement.LABEL_NAME, LABEL_VALUE etc
	 */
	int getLabelMode();

	/**
	 * @param labelMode
	 *            label mode, may be GeoElement.LABEL_NAME, LABEL_VALUE etc
	 */
	void setLabelMode(int labelMode);

	// public Kernel getKernel();
	/**
	 * @return get the label if set; do not fallback to definition (unlike
	 *         {@link #getLabel(StringTemplate)})
	 */
	public String getLabelSimple();

	/**
	 * Update value and basic properties from other geo
	 * 
	 * @param geo
	 *            other geo
	 */
	public void set(GeoElementND geo);

	/**
	 * Sets visibility if not given by condition to show object
	 * 
	 * @param visible
	 *            whether it should be visible
	 */
	public void setEuclidianVisibleIfNoConditionToShowObject(boolean visible);

	/**
	 * @return whether this is a point
	 */
	boolean isGeoPoint();

	/**
	 * @return whether this is a number
	 */
	boolean isGeoNumeric();

	/**
	 * @return whether this is a button
	 */
	boolean isGeoButton();

	/**
	 * @return caption template including %v, %n, ...
	 */
	String getRawCaption();

	/**
	 * @return parent construction
	 */
	Construction getConstruction();

	/**
	 * @return whether this is a polyhedron
	 */
	boolean isGeoPolyhedron();

	/**
	 * @return IDs of views that contain this geo
	 */
	List<Integer> getViewSet();

	/**
	 * @return whether this is a segment
	 */
	boolean isGeoSegment();

	/**
	 * @return whether this is a ray
	 */
	boolean isGeoPolygon();

	/**
	 * @return whether this is a ray
	 */
	boolean isGeoRay();

	/**
	 * @return whether this is a conic arc
	 */
	boolean isGeoConicPart();

	/**
	 * @return whether this is a vector
	 */
	boolean isGeoVector();

	/**
	 * @return geo type
	 */
	GeoClass getGeoClassType();

	/**
	 * @param auxilliary
	 *            whether this is auxiliary object (not shown in AV by default)
	 */
	void setAuxiliaryObject(boolean auxilliary);

	/**
	 * @param fix
	 *            whether this should be prevented from moving
	 */
	void setFixed(boolean fix);

	/**
	 * @param wanted
	 *            whether label is needed
	 */
	void setLabelWanted(boolean wanted);

	/**
	 * @param colorSpace
	 *            color space of dynamic color
	 */
	void setColorSpace(int colorSpace);

	/**
	 * @param colorFunction
	 *            dynamic color
	 */
	void setColorFunction(GeoList colorFunction);

	/**
	 * @param hatchingDistance
	 *            hatching distance in pixels
	 */
	void setHatchingDistance(int hatchingDistance);

	/**
	 * @return (lowercase) class name for XML
	 */
	String getXMLtypeString();

	/**
	 * Copy 3D visibility from other element
	 * 
	 * @param geo
	 *            other element
	 */
	void setVisibleInView3D(GeoElement geo);

	/**
	 * @param viewSet
	 *            set of views where this may appear
	 */
	void setViewFlags(List<Integer> viewSet);

	/**
	 * Copy plane visibility from other element
	 * 
	 * @param geo
	 *            other element
	 */
	void setVisibleInViewForPlane(GeoElement geo);

	boolean isDrawable();

	/**
	 * @return defining expression
	 */
	ExpressionNode getDefinition();

	/**
	 * @param def
	 *            defining expression
	 */
	void setDefinition(ExpressionNode def);

	/**
	 * Returns whether geo depends on this object.
	 * 
	 * @param geo
	 *            other geo
	 * @return true if geo depends on this object.
	 */
	public boolean isParentOf(final GeoElementND geo);

	void doRemove();

	boolean hasChildren();

	boolean isVisibleInView3D();

	/**
	 * @param geo
	 *            other geo
	 * @return whether the elements are equal in geometric sense (for congruency
	 *         use isCongruent)
	 */
	public boolean isEqual(GeoElementND geo);

	Kernel getKernel();

	boolean doHighlighting();

	double getAlphaValue();

	AlgoElement getDrawAlgorithm();

	GPaint getFillColor();

	GColor getBackgroundColor();

	FillType getFillType();

	GColor getLabelColor();

	String getLabelDescription();

	GColor getObjectColor();

	String getImageFileName();

	Object getLaTeXdescription();

	GColor getSelColor();

	boolean isHatchingEnabled();

	void setHatchingAngle(int hatchingAngle);

	void setAlphaValue(double alpha);

	String getCaption(StringTemplate defaulttemplate);

	MyImage getFillImage();

	String getFillSymbol();

	void setFillType(FillType fillType);

	int getHatchingDistance();

	void setFillSymbol(String symbol);

	boolean isFillable();

	boolean isGeoFunction();

	boolean isTraceable();

	double getHatchingAngle();

	void setImageFileName(String fileName);

	boolean getShowTrimmedIntersectionLines();

	boolean isVisible();

	public LaTeXCache getLaTeXCache();

	public void updateVisualStyleRepaint(GProperty prop);

	void setVisualStyle(GeoElement geoElement);

	boolean isParametric();

	void setLabelSimple(String labelSimple);

	GeoBoolean getShowObjectCondition();

	GeoList getColorFunction();

	/**
	 * @return color space of dynamic color
	 */
	int getColorSpace();

	boolean isSetEuclidianVisible();

	void setAdvancedVisualStyleCopy(GeoElementND macroGeo);

	void setDrawAlgorithm(DrawInformationAlgo copy);

	/**
	 * @return whether this can be moved
	 */
	public boolean isMoveable();

	String getDefinitionForInputBar();

	String getDefinition(StringTemplate tpl);

	GeoElement toGeoElement();

	/**
	 * @return whether this is output of random() or randomizable algo
	 */
	boolean isRandomGeo();

	/**
	 * Randomize this and update parent algo (no cascade)
	 */
	void updateRandomGeo();

	void addAlgorithm(AlgoElement algoElement);

	String getFreeLabel(String label);

	void removeOrSetUndefinedIfHasFixedDescendent();

	void removeAlgorithm(AlgoElement algoAttachCopyToView);

	boolean addToUpdateSets(AlgoElement algorithm);

	boolean removeFromUpdateSets(AlgoElement algorithm);

	void addToUpdateSetOnly(AlgoElement algoElement);

	boolean canBeRemovedAsInput();

	boolean isGeoCasCell();

	int getMinConstructionIndex();

	boolean setCaption(String object);

	boolean isGeoConic();

	void addToAlgorithmListOnly(AlgoElement algoElement);

	boolean isVisibleInputForMacro();

	String getNameDescription();

	Script getScript(EventType type);

	String getDefaultLabel();

	String getLongDescription();

	GColor getAlgebraColor();

	boolean isGeoPolyLine();

	Object getOldLabel();

	void setSelected(boolean b);

	TreeSet<GeoElement> getAllChildren();

	void setSelectionAllowed(boolean b);

	int getLayer();

	void setTooltipMode(int tooltipOff);

	void setLayer(int i);

	void addView(int viewEuclidian);

	void removeView(int viewEuclidian2);

	void setVisualStyleForTransformations(GeoElement topHit);

	public void resetDefinition();

	DescriptionMode needToShowBothRowsInAV();

	boolean isGeoFunctionable();

	boolean isLaTeXDrawableGeo();

	String getIndexLabel(String labelPrefix);

	boolean isGeoCurveCartesian();

	boolean isChildOf(GeoElementND autoCreateGeo);

	void setAllVisualProperties(GeoElement value, boolean b);

	void setShowObjectCondition(GeoBoolean newConditionToShowObject)
			throws CircularDefinitionException;

	String getRedefineString(boolean b, boolean c);

	boolean isAuxiliaryObject();

	String getFormulaString(StringTemplate latextemplate, boolean b);

	String getValueForInputBar();

	boolean isGeoAngle();

	boolean isGeoLine();

	boolean rename(String newLabel);

	boolean isGeoImage();

	void setLoadedLabel(String label);

	void setScripting(GeoElement value);

	boolean isGeoList();

	boolean isGeoBoolean();

	boolean hasChangeableCoordParentNumbers();

	boolean isGeoPlane();

	Coords getMainDirection();

	public ArrayList<GeoPointND> getFreeInputPoints(
			final EuclidianViewInterfaceSlim view);

	boolean isTranslateable();

	boolean isMoveable(EuclidianViewInterfaceSlim view);

	boolean isGeoInputBox();

	void recordChangeableCoordParentNumbers(EuclidianView view);

	boolean hasMoveableInputPoints(EuclidianViewInterfaceSlim view);

	boolean isChangeable();


	boolean isGeoImplicitCurve();

	boolean hasIndexLabel();

	boolean isLimitedPath();

	long getID();

	int compareTo(ConstructionElement cycleNext);

	boolean isVisibleInView(int viewID);

	boolean isVisibleInViewForPlane();

	boolean isAlgebraViewEditable();

	boolean isColorSet();

	boolean hasDrawable3D();

}
