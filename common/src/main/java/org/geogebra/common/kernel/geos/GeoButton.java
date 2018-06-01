/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * @author Michael
 */
public class GeoButton extends GeoElement
		implements AbsoluteScreenLocateable, TextProperties, Furniture {

	private double fontSizeD = 1;
	private int fontStyle = GFont.PLAIN;

	private boolean serifFont = false;

	private boolean fixedSize = false;

	private int width = 40;
	private int height = 30;

	private Observer observer;
	private SliderPosition startPoint;

	// original positions and widths
	// set once (if null)

	/**
	 * Creates new button
	 * 
	 * @param c
	 *            construction
	 */
	public GeoButton(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		setEuclidianVisible(true);
		setAuxiliaryObject(true);
	}

	/**
	 * Creates new button
	 * 
	 * @param cons
	 *            construction
	 * @param labelOffsetX
	 *            x offset
	 * @param labelOffsetY
	 *            y offset
	 */
	public GeoButton(Construction cons, int labelOffsetX, int labelOffsetY) {
		this(cons);
		this.labelOffsetX = labelOffsetX;
		this.labelOffsetY = labelOffsetY;
	}

	/**
	 * @param cons
	 *            cons
	 * @param size
	 *            size multiplier, usually 2
	 */
	public GeoButton(Construction cons, double size) {
		this(cons);
		this.fontSizeD = size;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_BUTTON_ACTION;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.BUTTON;
	}

	@Override
	public GeoElement copy() {
		return new GeoButton(cons, labelOffsetX, labelOffsetY);
	}

	@Override
	public final boolean isGeoButton() {
		return true;
	}

	@Override
	public boolean showInEuclidianView() {
		return true;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoButton()) {
			return;
		}
		setCaption(geo.getRawCaption());
	}

	@Override
	final public void setUndefined() {
		// do nothing
	}

	@Override
	final public boolean isDefined() {
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return "";
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return label;
	}

	@Override
	public double getRealWorldLocX() {
		return startPoint == null ? 0 : startPoint.x;
	}

	@Override
	public double getRealWorldLocY() {
		return startPoint == null ? 0 : startPoint.y;
	}

	@Override
	public boolean isAbsoluteScreenLocActive() {
		return startPoint == null;
	}

	@Override
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;
		if (startPoint != null) {
			updateRelLocation(kernel.getApplication().getActiveEuclidianView());
		}
		if (!hasScreenLocation()) {
			setScreenLocation(x, y);
		}
	}

	@Override
	public int getAbsoluteScreenLocX() {
		return labelOffsetX;
	}

	@Override
	public int getAbsoluteScreenLocY() {
		return labelOffsetY;
	}

	@Override
	public void setAbsoluteScreenLocActive(boolean flag) {
		EuclidianView ev = kernel.getApplication().getActiveEuclidianView();
		if (flag && startPoint != null) {
			updateAbsLocation(ev);

			startPoint = null;
		}
		else if (!flag) {
			startPoint = new SliderPosition();
			updateRelLocation(ev);
		}
	}

	/**
	 * Update absolute location according to relative location in given view
	 * (when rel. position active)
	 * 
	 * @param ev
	 *            view
	 */
	public void updateAbsLocation(EuclidianView ev) {
		if (startPoint != null) {
			labelOffsetX = ev.toScreenCoordX(startPoint.x);
			labelOffsetY = ev.toScreenCoordY(startPoint.y);
		}
	}

	private void updateRelLocation(EuclidianView ev) {
		startPoint.x = ev.toRealWorldCoordX(labelOffsetX);
		startPoint.y = ev.toRealWorldCoordY(labelOffsetY);
	}

	@Override
	public void setRealWorldLoc(double x, double y) {
		startPoint = new SliderPosition();
		startPoint.x = x;
		startPoint.y = y;
	}

	/**
	 * @return true for textfields, false for buttons
	 */
	public boolean isTextField() {
		return false;
	}

	// Michael Borcherds 2008-04-30
	@Override
	final public boolean isEqual(GeoElementND geo) {
		return geo == this;
	}

	/**
	 * Returns whether the value (e.g. equation) should be shown as part of the
	 * label description
	 */
	@Override
	final public boolean isLabelValueShowable() {
		return false;
	}

	@Override
	public double getFontSizeMultiplier() {
		return fontSizeD;
	}

	@Override
	public void setFontSizeMultiplier(double d) {
		fontSizeD = d;
	}

	@Override
	public int getFontStyle() {
		return fontStyle;
	}

	@Override
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	@Override
	public int getPrintDecimals() {
		return 0;
	}

	@Override
	public int getPrintFigures() {
		return 0;
	}

	@Override
	public void setPrintDecimals(int printDecimals, boolean update) {
		// do nothing
	}

	@Override
	public void setPrintFigures(int printFigures, boolean update) {
		// do nothing
	}

	@Override
	public boolean isSerifFont() {
		return serifFont;
	}

	@Override
	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}

	@Override
	public boolean useSignificantFigures() {
		return false;
	}

	@Override
	public boolean justFontSize() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		// font settings
		GeoText.appendFontTag(sb, serifFont, fontSizeD, fontStyle, false,
				kernel.getApplication());

		// name of image file
		if (getFillImage() != null) {
			sb.append("\t<file name=\"");
			sb.append(StringUtil
					.encodeXML(this.getGraphicsAdapter().getImageFileName()));
			sb.append("\"/>\n");
		}
		if (isFixedSize()) {
			sb.append("\t<dimensions width=\"" + width + "\" height=\"" + height
					+ "\" />\n");
		}
		if (!isAbsoluteScreenLocActive()) {
			sb.append("\t<startPoint x=\"");
			sb.append(startPoint.x);
			sb.append("\" y=\"");
			sb.append(startPoint.y);
			sb.append("\" z=\"1\"/>");
		}
	}

	@Override
	public boolean hasBackgroundColor() {
		return true;
	}

	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	public FillType getFillType() {
		return FillType.IMAGE;
	}

	@Override
	public boolean isFurniture() {
		return true;
	}

	@Override
	public boolean isPinnable() {
		return true;
	}

	/**
	 * @return whther this button has fixed size (otherwise depends on text)
	 */
	public boolean isFixedSize() {
		return fixedSize;
	}

	/**
	 * @param fixedSize
	 *            whether this button should have fixed size
	 */
	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
		if (observer != null) {
			observer.notifySizeChanged();
		}
	}

	/**
	 * @return width in pixels (if it's fixed)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            width in pixels (used for fixed size buttons)
	 */
	public void setWidth(int width) {
		this.width = width;
		if (hasScreenLocation()) {
			getScreenLocation().initWidth(width);
		}
	}

	/**
	 * 
	 * @return height in pixels (if it's fixed)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            height in pixels (used for fixed size buttons)
	 */
	public void setHeight(int height) {
		this.height = height;
		if (hasScreenLocation()) {
			getScreenLocation().initHeight(height);
		}
	}

	/**
	 * @param observer
	 *            object watching size of this button
	 */
	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	/** Object watching size of a button */
	public interface Observer {
		/**
		 * This method is called when size is changed
		 */
		public void notifySizeChanged();
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return false;
	}

	/**
	 * @param cons
	 *            cons
	 * @return new button (with default size)
	 */
	public static GeoButton getNewButton(Construction cons) {
		return new GeoButton(cons, ConstructionDefaults.DEFAULT_BUTTON_SIZE);
	}

	@Override
	public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

	/**
	 * @return total screen width, overridden in GeoInputBox
	 */
	@Override
	public int getTotalWidth(EuclidianViewInterfaceCommon ev) {
		return getWidth();
	}

	@Override
	public int getTotalHeight(EuclidianViewInterfaceCommon ev) {
		return getHeight();
	}

	@Override
	final public boolean isAlgebraViewEditable() {
		return !isIndependent();
	}

	/**
	 * @return relative position
	 */
	public SliderPosition getStartPoint() {
		return startPoint;
	}

	@Override
	public GColor getBackgroundColor() {
		if (bgColor == null && colFunction == null) {
			return null;
		}

		// get dynamic Opacity from *foreground* color
		if (colFunction != null && colFunction.size() == 4) {
			double alpha = colFunction.get(3).evaluateDouble();
			int r = 255;
			int g = 255;
			int b = 255;
			if (bgColor != null) {
				r = bgColor.getRed();
				g = bgColor.getGreen();
				b = bgColor.getBlue();
			}

			return GColor.newColor(r, g, b, (int) (alpha * 255));
		}

		return bgColor;
	}

	@Override
	public String getScreenReaderText() {
		StringBuilder sb = new StringBuilder();
		Localization loc = getKernel().getApplication().getLocalization();
		sb.append(loc.getMenuDefault("Button", "button"));
		sb.append(getCaptionForScreenReader());
		sb.append(loc.getMenuDefault("Selected", "selected"));
		return sb.toString();
	}
}