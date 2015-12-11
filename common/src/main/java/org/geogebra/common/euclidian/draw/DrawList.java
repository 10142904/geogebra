/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.euclidian.event.ActionEvent;
import org.geogebra.common.euclidian.event.ActionListenerI;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.util.DropDownList;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.Unicode;

/**
 * Draw a GeoList containing drawable objects
 * 
 * @author Markus Hohenwarter
 */
public final class DrawList extends CanvasDrawable implements RemoveNeeded {
	private static final int OPTIONSBOX_MIN_FONTSIZE = 13;
	private static final int OPTIONSBOX_ITEM_GAP_EXTRA_SMALL = 15;
	private static final int OPTIONSBOX_ITEM_GAP_VERY_SMALL1 = 20;
	private static final int OPTIONSBOX_ITEM_GAP_VERY_SMALL2 = 25;
	private static final int OPTIONSBOX_ITEM_GAP_SMALL1 = 30;
	private static final int OPTIONSBOX_ITEM_GAP_SMALL2 = 35;
	private static final int OPTIONSBOX_ITEM_GAP_MEDIUM = 40;
	private static final int OPTIONSBOX_ITEM_GAP_BIG = 55;
	private static final int COMBO_TEXT_MARGIN = 5;

	private static final int OPTIONSBOX_ITEM_HGAP_EXTRA_SMALL = 2;
	private static final int OPTIONSBOX_ITEM_HGAP_VERY_SMALL1 = 4;
	private static final int OPTIONSBOX_ITEM_HGAP_VERY_SMALL2 = 5;
	private static final int OPTIONSBOX_ITEM_HGAP_SMALL1 = 8;
	private static final int OPTIONSBOX_ITEM_HGAP_SMALL2 = 10;
	private static final int OPTIONSBOX_ITEM_HGAP_MEDIUM = 12;
	private static final int OPTIONSBOX_ITEM_HGAP_BIG = 15;
	private static final int OPTIONBOX_COMBO_GAP = 5;
	private static final int LABEL_COMBO_GAP = 10;
	/** coresponding list as geo */
	GeoList geoList;
	private List<GRectangle> optionItems = new ArrayList<GRectangle>();
	private DrawListArray drawables;
	private boolean isVisible;
	private boolean optionsVisible = false;
	private String oldCaption = "";
	/** combobox */
	org.geogebra.common.javax.swing.AbstractJComboBox comboBox;
	private org.geogebra.common.javax.swing.GLabel label;
	private DropDownList dropDown = null;
	private int optionsHeight;
	private int optionsWidth;
	private String selectedText;
	private int selectedHeight;
	private GBox ctrlBox;
	private GRectangle ctrlRect;
	private GRectangle optionsRect;
	private GBox optionsBox;
	private int selectedOptionIndex;
	private GDimension selectedDimension;
	private int currentIdx;
	private float lastDescent;
	private float lastAscent;
	private boolean latexLabel;
	private GFont optionFont = null;
	private int colCount = 0;
	private int colWidth = 0;
	private int rowCount;
	private boolean recalculateFontSize = true;
	private int viewHeight = 0;
	private boolean allPlain;
	private int viewWidth = 0;

	/**
	 * Creates new drawable list
	 * 
	 * @param view
	 *            view
	 * @param geoList
	 *            list
	 */
	public DrawList(EuclidianView view, GeoList geoList) {
		this.view = view;
		this.geoList = geoList;
		geo = geoList;
		setDrawingOnCanvas(view.getApplication()
				.has(Feature.DRAW_DROPDOWNLISTS_TO_CANVAS));

		if (isDrawingOnCanvas()) {
			dropDown = view.getApplication().newDropDownList();
			ctrlBox = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			ctrlRect = ctrlBox.getBounds();
			optionsBox = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			optionsRect = optionsBox.getBounds();
		}
		reset();

		update();
	}

	private void resetComboBox() {
		if (!isDrawingOnCanvas() && label == null) {
			label = view.getApplication().getSwingFactory().newJLabel("Label",
					true);
			label.setVisible(true);
		}

		if (comboBox == null) {
			comboBox = geoList.getComboBox(view.getViewID());
			comboBox.setVisible(!isDrawingOnCanvas());
			comboBox.addActionListener(AwtFactory.prototype
					.newActionListener(new DrawList.ActionListener()));
		}

		if (box == null) {
			box = view.getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			if (!isDrawingOnCanvas()) {
				box.add(label);
			}
			box.add(comboBox);
		}
		view.add(box);
	}

	private void reset() {

		if (geoList.drawAsComboBox()) {
			resetComboBox();
		} else {

			if (drawables == null) {
				drawables = new DrawListArray(view);
			}
		}
	}

	private void updateWidgets() {
		isVisible = geo.isEuclidianVisible() && geoList.size() != 0;
		int fontSize = (int) (view.getFontSize()
				* geoList.getFontSizeMultiplier());
		if (isDrawingOnCanvas()) {
			setLabelFontSize(fontSize);
			if (geo.doHighlighting() == false) {
				hideWidget();
			}
		}
		box.setVisible(isVisible);

		if (!isVisible) {
			return;
		}

		// eg size changed etc
		geoList.rebuildComboxBoxIfNecessary(comboBox);
		labelDesc = getLabelText();

		App app = view.getApplication();

		org.geogebra.common.awt.GFont vFont = view.getFont();
		org.geogebra.common.awt.GFont font = app.getFontCanDisplay(
				comboBox.getItemAt(0).toString(), false, vFont.getStyle(),
				fontSize);

		if (!isDrawingOnCanvas()) {
			label.setText(labelDesc);

			if (!geo.isLabelVisible()) {
				label.setText("");
			}
			label.setOpaque(false);
			label.setFont(font);
			label.setForeground(geo.getObjectColor());
		}

		comboBox.setFont(font);
		comboBox.setForeground(geo.getObjectColor());
		org.geogebra.common.awt.GColor bgCol = geo.getBackgroundColor();
		comboBox.setBackground(
				bgCol != null ? bgCol : view.getBackgroundCommon());

		comboBox.setFocusable(true);
		comboBox.setEditable(false);

		box.validate();

		xLabel = geo.labelOffsetX;
		yLabel = geo.labelOffsetY;
		org.geogebra.common.awt.GDimension prefSize = box.getPreferredSize();
		labelRectangle.setBounds(xLabel, yLabel, prefSize.getWidth(),
				prefSize.getHeight());
		box.setBounds(labelRectangle);

	}

	private String getLabelText() {
		// don't need to worry about labeling options, just check if caption
		// set or not

		if (geo.getRawCaption() != null) {
			String caption = geo.getCaption(StringTemplate.defaultTemplate);
			if (isDrawingOnCanvas()) {
				oldCaption = caption;
				return caption;

			} else if (!caption.equals(oldCaption)) {
				oldCaption = caption;
				labelDesc = GeoElement.indicesToHTML(caption, true);
			}
			return labelDesc;
		}

		// make sure there's something to drag
		return Unicode.NBSP + Unicode.NBSP + Unicode.NBSP;

	}

	@Override
	final public void update() {

		if (geoList.drawAsComboBox()) {
			updateWidgets();

		} else {
			isVisible = geoList.isEuclidianVisible();
			if (!isVisible)
				return;

			// go through list elements and create and/or update drawables
			int size = geoList.size();
			drawables.ensureCapacity(size);
			int oldDrawableSize = drawables.size();

			int drawablePos = 0;
			for (int i = 0; i < size; i++) {
				GeoElement listElement = geoList.get(i);
				if (!listElement.isDrawable())
					continue;

				// add drawable for listElement
				// if (addToDrawableList(listElement, drawablePos,
				// oldDrawableSize))
				if (drawables.addToDrawableList(listElement, drawablePos,
						oldDrawableSize, this))
					drawablePos++;

			}

			// remove end of list
			for (int i = drawables.size() - 1; i >= drawablePos; i--) {
				view.remove(drawables.get(i).getGeoElement());
				drawables.remove(i);
			}

			// draw trace
			if (geoList.getTrace()) {
				isTracing = true;
				org.geogebra.common.awt.GGraphics2D g2 = view
						.getBackgroundGraphics();
				if (g2 != null)
					drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					// view.updateBackground();
				}
			}
		}

	}

	/**
	 * This method is necessary, for example when we set another construction
	 * step, and the sub-drawables of this list should be removed as well
	 */
	final public void remove() {

		if (geoList.drawAsComboBox()) {
			view.remove(box);
		} else {
			for (int i = drawables.size() - 1; i >= 0; i--) {
				GeoElement currentGeo = drawables.get(i).getGeoElement();
				if (!currentGeo.isLabelSet())
					view.remove(currentGeo);
			}
			drawables.clear();
		}
	}

	@Override
	protected final void drawTrace(org.geogebra.common.awt.GGraphics2D g2) {
		if (!geoList.drawAsComboBox()) {

			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			if (isVisible) {
				int size = drawables.size();
				for (int i = 0; i < size; i++) {
					Drawable d = (Drawable) drawables.get(i);
					// draw only those drawables that have been created by this
					// list;
					// if d belongs to another object, we don't want to mess
					// with it
					// here
					if (createdByDrawList()
							|| !d.getGeoElement().isLabelSet()) {
						d.draw(g2);
					}
				}
			}
		}
	}

	@Override
	final public void draw(org.geogebra.common.awt.GGraphics2D g2) {
		if (isVisible && geoList.drawAsComboBox()) {
			if (isDrawingOnCanvas()) {
				drawOnCanvas(g2, "");
				return;
			}

			if (isVisible) {
				if (geo.doHighlighting()) {
					label.setOpaque(true);
					label.setBackground(GColor.LIGHT_GRAY);

				} else {
					label.setOpaque(false);
				}
			}

		} else {
			if (isVisible) {
				boolean doHighlight = geoList.doHighlighting();

				int size = drawables.size();
				for (int i = 0; i < size; i++) {
					Drawable d = (Drawable) drawables.get(i);
					// draw only those drawables that have been created by this
					// list;
					// if d belongs to another object, we don't want to mess
					// with it
					// here
					if (createdByDrawList()
							|| !d.getGeoElement().isLabelSet()) {
						d.getGeoElement().setHighlighted(doHighlight);
						d.draw(g2);
					}
				}
			}
		}
	}

	/**
	 * Returns whether any one of the list items is at the given screen
	 * position.
	 */
	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		if (geoList.drawAsComboBox()) {
			DrawList dl = view.getOpenedComboBox();
			if (dl != null && dl != this) {
				return false;
			}

			return isDrawingOnCanvas()
					? super.hit(x, y, hitThreshold) || isControlHit(x, y)
							|| isOptionsHit(x, y)
					: box.getBounds().contains(x, y);
		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.hit(x, y, hitThreshold))
				return true;
		}
		return false;

	}

	@Override
	public boolean isInside(GRectangle rect) {
		if (geoList.drawAsComboBox()) {
			return super.isInside(rect);
		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (!d.isInside(rect))
				return false;
		}
		return size > 0;

	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (geoList.drawAsComboBox()) {
			return super.intersectsRectangle(rect);
		}

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			if (d.intersectsRectangle(rect))
				return true;
		}
		return false;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public org.geogebra.common.awt.GRectangle getBounds() {
		if (geoList.drawAsComboBox()) {
			return isDrawingOnCanvas() ? box.getBounds() : null;

		}

		if (!geo.isEuclidianVisible())
			return null;

		org.geogebra.common.awt.GRectangle result = null;

		int size = drawables.size();
		for (int i = 0; i < size; i++) {
			Drawable d = (Drawable) drawables.get(i);
			org.geogebra.common.awt.GRectangle bb = d.getBounds();
			if (bb != null) {
				if (result == null)
					result = org.geogebra.common.factories.AwtFactory.prototype
							.newRectangle(bb); // changed () to (bb) bugfix,
				// otherwise top-left of screen
				// is always included
				// add bounding box of list element
				result.add(bb);
			}
		}

		return result;

	}

	/**
	 * Listens to events in this combobox
	 * 
	 * @author Michael + Judit
	 */
	public class ActionListener
			extends org.geogebra.common.euclidian.event.ActionListener
			implements ActionListenerI {

		/**
		 * @param e
		 *            action event
		 */
		public void actionPerformed(ActionEvent e) {
			geoList.setSelectedIndex(comboBox.getSelectedIndex(), true);
		}

	}

	/**
	 * Resets the drawables when draw as combobox option is toggled
	 */
	public void resetDrawType() {

		if (geoList.drawAsComboBox()) {
			if (drawables != null) {
				for (int i = drawables.size() - 1; i >= 0; i--) {
					GeoElement currentGeo = drawables.get(i).getGeoElement();
					if (!currentGeo.isLabelSet()) {
						view.remove(currentGeo);
					}
				}
			drawables.clear();
			}
		} else {
			view.remove(box);
		}

		reset();

		update();
	}

	private boolean isAllPlain() {
		for (int i = 0; i < geoList.size(); i++) {
			String text = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);
			if (isLatexString(text)) {
				return false;
			}
		}
		return true;
	}
	private void updateOptionMetrics(GGraphics2D g2) {
		g2.setPaint(GColor.WHITE);
		// just measuring
		colWidth = 0;
		optionsHeight = 0;
		int origFontSize = optionFont.getSize();
		int minFontSize = 12;
		g2.setFont(optionFont);
		colCount = 1;
		int fontSize = optionFont.getSize();
		if (!isFontSizeUpdateNeeded(g2)) {
			drawOptionLines(g2, 0, 0, false);
			return;
		}

		drawOptionLines(g2, 0, 0, false);

		int plainHeight = estimatePlainHeight(g2, geoList.size());
		while ((plainHeight > view.getHeight() && fontSize < minFontSize)) {
			fontSize -= 1;
			optionFont = optionFont.deriveFont(GFont.PLAIN, fontSize);
			g2.setFont(optionFont);

//			if (fontSize < minFontSize) {
//				fontSize = origFontSize;
//				optionFont = optionFont.deriveFont(GFont.PLAIN, origFontSize);
//				colCount++;
//			}

			g2.setFont(optionFont);
			plainHeight = estimatePlainHeight(g2, geoList.size() / colCount);

		}

		drawOptionLines(g2, 0, 0, false);

		App.debug("[DROPDOWN] font size udated: " + origFontSize + " to "
				+ optionFont.getSize());
		recalculateFontSize = false;
	}

	private int getMaxCapacity(GGraphics2D g2) {
		g2.setFont(optionFont);
		GTextLayout layout = getLayout(g2, getWidthestPlainItem(), optionFont);
		int w = (int) (layout.getBounds().getWidth()
				+ 3 * getOptionsItemHGap());

		int h = getTextHeight(g2, getWidthestPlainItem()) + getOptionsItemGap();
		int cols = view.getViewWidth() / w;
		int rows = view.getViewHeight() / h;
		rowCount = rows;
		colCount = (geoList.size() / rowCount);
		if (cols * rows >= geoList.size()) {
			colCount++;
		}
		;

		return colCount * rowCount;
	}


	private void updateMetrics(GGraphics2D g2) {

		if (viewHeight != view.getHeight() || viewWidth != view.getHeight()) {
			optionFont = getLabelFont().deriveFont(GFont.PLAIN);
		}

		if (viewHeight != view.getHeight()) {
			viewHeight = view.getHeight();
		}

		if (viewWidth != view.getWidth()) {
			viewWidth = view.getWidth();


		}

		selectedText = geoList.get(geoList.getSelectedIndex())
				.toValueString(StringTemplate.defaultTemplate);
		selectedDimension = drawTextLine(g2, false, 0, 0, selectedText,
				getLabelFont(), isLatexString(selectedText), false);

		if (isOptionsVisible()) {
			int max = getMaxCapacity(g2);
			int fontSize = optionFont.getSize();

			while (max < geoList.size() && fontSize > OPTIONSBOX_MIN_FONTSIZE) {
				fontSize--;
				optionFont = optionFont.deriveFont(GFont.PLAIN, fontSize);
				max = getMaxCapacity(g2);
			}

			drawOptionLines(g2, 0, 0, false);

			App.debug("[DROPDOWN][CAPACITY] colCount: " + colCount
					+ " rowCount: " + rowCount + " max capacity: " + max
					+ " itemCount: " + geoList.size());

		}

		setPreferredSize(getPreferredSize());

		latexLabel = measureLabel(g2, geoList, getLabelText());

		labelRectangle.setBounds(boxLeft - 1, boxTop - 1, boxWidth, boxHeight);

	}

	private int estimatePlainHeight(GGraphics2D g2, int size) {
		int gap = getOptionsItemGap();
		int h = (getDefaultTextHeight(g2) + gap) * (size + 1);
		if (colCount == 1) {
			h += 1.5 * gap;
		}
		return h;
	}

	private String getWidthestPlainItem() {
		String result = "";
		for (int i = 0; i < geoList.size(); i++) {
			String text = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);
			if (!"".equals(text) && !isLatexString(text)
					&& text.length() > result.length()) {
				result = text;
			}
		}
		App.debug("[DROPDOWNS][METRICS] widthest item: " + result);
		return result;
	}

	private int estimatePlainWidth(GGraphics2D g2, int cols) {
		GTextLayout layout = getLayout(g2, getWidthestPlainItem(), optionFont);
		double w = (layout.getBounds().getWidth() + 2 * getOptionsItemHGap())
				* cols;
		App.debug("[DROPDOWNS][METRICS] est. width: " + w);
		return (int) w;
	}

	private boolean isFontSizeUpdateNeeded(GGraphics2D g2) {

		int h = estimatePlainHeight(g2, geoList.size());

		App.debug("[DROPDOWN] fontSize: " + optionFont.getSize() + " height: "
				+ h + " viewHeight: " + viewHeight);
		return recalculateFontSize || viewHeight < h;
	}

	@Override
	protected void drawWidget(GGraphics2D g2) {

		updateMetrics(g2);

		String labelText = getLabelText();
		int textLeft = boxLeft + COMBO_TEXT_MARGIN;
		int textBottom = boxTop + getTextBottom();
		GColor bgColor = geo.getBackgroundColor() != null
				? geo.getBackgroundColor() : view.getBackgroundCommon();

		dropDown.drawSelected(geoList, g2, bgColor, boxLeft, boxTop, boxWidth,
				boxHeight);

		g2.setPaint(GColor.LIGHT_GRAY);
		highlightLabel(g2, latexLabel);

		g2.setPaint(geo.getObjectColor());

		// Draw the selected line
		boolean latex = isLatexString(selectedText);
		if (latex) {
			textBottom = boxTop
					+ (boxHeight - selectedDimension.getHeight()) / 2;
		} else {
			textBottom = alignTextToBottom(g2, boxTop, boxHeight, selectedText);
		}

		drawTextLine(g2, false, textLeft, textBottom, selectedText,
				getLabelFont(), latex, true);

		drawControl(g2);

		if (geo.isLabelVisible()) {
			drawLabel(g2, geoList, labelText);
		}

		if (isOptionsVisible()) {
			drawOptions(g2);
		}


	}

	private int alignTextToBottom(GGraphics2D g2, int top, int height,
			String text) {
		int base = (height + getTextDescent(g2, text)) / 2;
		return top + base + (height - base) / 2;

	}

	@Override
	protected void drawLabel(GGraphics2D g2, GeoElement geo0, String text) {

		int textBottom = boxTop + getTextBottom();
		boolean latex = isLatexString(text);
		if (latex) {
			drawLatex(g2, geo0, getLabelFont(), text, xLabel,
					boxTop + (boxHeight - labelSize.y) / 2);

		} else {
			textBottom = boxTop
					+ (boxHeight + getMultipliedFontSize() - COMBO_TEXT_MARGIN)
							/ 2;
			g2.setPaint(geo.getObjectColor());
			g2.setFont(getLabelFont());
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					xLabel, textBottom, false, false);
		}

	}

	@Override
	protected void highlightLabel(GGraphics2D g2, boolean latex) {
		if (geo.isLabelVisible() && geo.doHighlighting() && latex) {
			g2.fillRect(xLabel, boxTop + (boxHeight - labelSize.y) / 2,
					labelSize.x, labelSize.y);

		} else {
			super.highlightLabel(g2, latex);
		}

	}

	private void drawControl(GGraphics2D g2) {
		g2.setPaint(GColor.BLACK);
		int left = boxLeft + boxWidth - boxHeight;

		ctrlRect.setBounds(boxLeft, boxTop, boxWidth, boxHeight);
		dropDown.drawControl(g2, left, boxTop, boxHeight, boxHeight,
				geo.getBackgroundColor(), isOptionsVisible());

	}

	@Override
	protected void calculateBoxBounds(boolean latex) {
		boxLeft = xLabel + labelSize.x + LABEL_COMBO_GAP;
		boxTop = latex
				? yLabel + (labelSize.y - getPreferredSize().getHeight()) / 2
				: yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight() + COMBO_TEXT_MARGIN;
	}

	@Override
	protected void calculateBoxBounds() {
		boxLeft = xLabel + LABEL_COMBO_GAP;
		boxTop = yLabel;
		boxWidth = getPreferredSize().getWidth();
		boxHeight = getPreferredSize().getHeight();
	}

	@Override
	protected int getTextBottom() {

		return isLatexString(selectedText) ? boxHeight - selectedHeight / 2
				: (getPreferredSize().getHeight() + getMultipliedFontSize())
						/ 2;
	}

	private void drawOptions(GGraphics2D g2) {

		g2.setPaint(geoList.getBackgroundColor());
		int optTop = boxTop + boxHeight + OPTIONBOX_COMBO_GAP;
		int viewBottom = view.getViewHeight();

		if (optTop + optionsHeight > viewBottom) {
			optTop = viewBottom - optionsHeight - OPTIONBOX_COMBO_GAP;
			int gap = getOptionsItemGap() / 2;
			if (optTop < gap) {
				optTop = gap;
			}
		}

		int rowTop = optTop;
		int optLeft = boxLeft;
		int estimatedWidth = estimatePlainWidth(g2, colCount);
		if (optLeft + estimatedWidth > view.getViewWidth()) {
			optLeft = view.getViewWidth() - estimatedWidth;
		}

		if (optLeft < 0) {
			optLeft = 0;
		}

		drawOptionLines(g2, optLeft, rowTop, true);

		int size = optionItems.size();
		if (size > 1) {
			// int rowCount = colCount == 1 ? size
			// : size / colCount + (size % colCount == 0 ? 0 : 1);
			GRectangle rUpLeft = optionItems.get(0).getBounds();

			int upRigthIdx = rowCount * (colCount - 1);
			if (upRigthIdx >= size) {
				upRigthIdx = size - 1;
			}

			int left = (int) rUpLeft.getX();
			int top = (int) rUpLeft.getY();

			int width = (int) (colCount > 0 ? colCount * rUpLeft.getWidth()
					: rUpLeft.getWidth());
			int height = (int) (rowCount * rUpLeft.getHeight());
			optionsRect.setBounds(left, top, width, height);

			App.debug("[DROPDOWNS][METRICS] real width: " + width);

			g2.setPaint(geoList.getBackgroundColor());
			g2.fillRect(left, top, width, height);

			g2.setPaint(GColor.LIGHT_GRAY);

			g2.drawRect(left, top, width, height);

			g2.setPaint(geo.getObjectColor());

			// fillRect(g2, rUpLeft, GColor.RED);
			// fillRect(g2, rDownLeft, GColor.GREEN);
			// fillRect(g2, rUpRight, GColor.BLUE);

		}

		drawOptionLines(g2, optLeft, rowTop, true);

	}

	private void fillRect(GGraphics2D g2, GRectangle r, GColor color) {
		g2.setPaint(color);
		g2.fillRect((int) (r.getX()), (int) (r.getY()), (int) (r.getWidth()),
				(int) (r.getHeight()));
	}

	private GDimension drawTextLine(GGraphics2D g2, boolean center,
			int textLeft, int top, String text, GFont font, boolean latex,
			boolean draw) {

		int left = textLeft;

		if (latex) {
			GDimension d = null;
			if (center) {
				g2.setPaint(GColor.WHITE);
				d = measureLatex(g2, geoList, getLabelFont(), text);
				left += (colWidth - d.getWidth()) / 2;
				g2.setPaint(geo.getObjectColor());

			}

			d = draw ? drawLatex(g2, geoList, font, text, left, top)
					: measureLatex(g2, geoList, font, text);

			return d;
		}
		g2.setFont(font);

		GTextLayout layout = getLayout(g2, text, font);

		final int w = (int) layout.getBounds().getWidth();
		if (center) {
			left += (colWidth - w) / 2;
		}

		if (draw) {
			EuclidianStatic.drawIndexedString(view.getApplication(), g2, text,
					left, top, false, false);
		}

		lastDescent = layout.getDescent();
		lastAscent = layout.getAscent();

		return AwtFactory.prototype.newDimension(w,
				Math.round(lastAscent + lastDescent));
	}

	private int drawOptionLines(GGraphics2D g2, int left, int top,
			boolean draw) {
		optionItems.clear();
		int itemCount = geoList.size();

		if (colCount == 1) {
			colWidth = boxWidth;
			GDimension d = drawOptionColumn(g2, top, left, 0, itemCount, draw);
			optionsWidth = d.getWidth() + 2 * COMBO_TEXT_MARGIN
					+ getTriangleControlWidth();
			optionsHeight = d.getHeight();
		} else {
			drawOptionsMultiColumn(g2, top, left, draw);
		}

		return optionsHeight;
	}

	private void drawOptionsMultiColumn(GGraphics2D g2, int top, int left0,
			boolean draw) {
		int startIdx = 0;
		int size = geoList.size();
		int width = 0;
		int height = 0;
		int left = left0 > 0 ? left0 : 0;
		while (startIdx < geoList.size()) {
			int endIdx = startIdx + rowCount;
			if (endIdx > size) {
				endIdx = size;
			}

			GDimension d = drawOptionColumn(g2, top, left, startIdx, endIdx,
					draw);

			if (!draw) {
				// measuring the biggest element
				if (width < d.getWidth()) {
					width = d.getWidth();
				}
				if (height < d.getHeight()) {
					height = d.getHeight();
				}

			}

			startIdx = endIdx;
			left += colWidth + 2 * getOptionsItemHGap();
		}

		if (!draw) {
			colWidth = width;
			optionsHeight = height;
		}

		optionsWidth = colWidth * colCount;

		if (colCount != size) {
			// no gap needed at the end if all elements in one row.
			optionsHeight += getOptionsItemGap();
		}

	}

	private GDimension drawOptionColumn(GGraphics2D g2, int top, int left,
			int itemsFrom, int itemsTo, boolean draw) {
		int dW = 0;
		int dH = 0;

		int rowTop = top;
		boolean allLatex = true;

		int standardGap = getOptionsItemGap();
		GFont font = g2.getFont();
		g2.setFont(optionFont);
		for (int i = itemsFrom; i < itemsTo; i++) {
			GBox b = geo.getKernel().getApplication().getSwingFactory()
					.createHorizontalBox(view.getEuclidianController());
			GRectangle itemRect = b.getBounds();

			String text = geoList.get(i)
					.toValueString(StringTemplate.defaultTemplate);

			boolean latex = isLatexString(text);

			allLatex = allLatex && latex;
			allPlain = allPlain && !latex;

			boolean latexNext = i < itemsTo - 1
					? isLatexString(geoList.get(i + 1)
							.toValueString(StringTemplate.defaultTemplate))
					: allLatex;

			boolean hovered = i == selectedOptionIndex;

			if (i == itemsFrom && !latex) {

				if (rowTop > boxTop + boxHeight) {
					rowTop += standardGap / 2;
				}

				rowTop += getFullTextHeight(g2, text);
			}

			GDimension d = drawTextLine(g2, true, left, rowTop, text,
					optionFont, latex, draw);

			int h = d.getHeight();

			int w = getOptionsItemHGap();
			if (latex) {
				itemRect.setBounds(left - w, rowTop, colWidth + 2 * w, h);
			} else {
				itemRect.setBounds(left - w, rowTop - h - standardGap / 2,
						colWidth + 2 * w,
						(int) (h + lastDescent + standardGap));

			}
			optionItems.add(itemRect);

			if (draw && hovered && itemsTo > i) {
				int rx = (int) (itemRect.getX());
				int ry = (int) (itemRect.getY());
				int rw = (int) (itemRect.getWidth());
				int rh = (int) (itemRect.getHeight());

				g2.setPaint(GColor.LIGHT_GRAY);

				g2.fillRoundRect(rx, ry, rw, rh, 4, 4);

				g2.setPaint(GColor.GRAY);

				g2.drawRoundRect(rx, ry, rw, rh, 4, 4);

				g2.setPaint(geoList.getObjectColor());
				drawTextLine(g2, true, left, rowTop, text, optionFont, latex,
						draw);
			}

			if (i == geoList.getSelectedIndex()) {
				selectedText = text;
			}

			if (dW < d.getWidth()) {
				dW = d.getWidth();
			}

			if (!latex && latexNext) {
				rowTop += standardGap;
				dH += standardGap;

			} else if (latex && !latexNext) {
				rowTop += itemRect.getHeight() + 1.5 * standardGap;
			} else {
				rowTop += itemRect.getHeight();
			}

		}
		GRectangle lastRect = optionItems.get(itemsTo - 1);
		dH = (int) ((lastRect.getY() + lastRect.getHeight())
				- optionItems.get(itemsFrom).getY());
		g2.setFont(font);

		return AwtFactory.prototype.newDimension(dW, dH);
	}

	private int getOptionsItemGap() {

		// switch (view.getApplication().getFontSize()) {
		switch (optionFont.getSize()) {
		case 8:
		case 9:
		case 10:
			return OPTIONSBOX_ITEM_GAP_EXTRA_SMALL;
		case 11:
		case 12:
		case 13:
		case 14:
			return OPTIONSBOX_ITEM_GAP_VERY_SMALL1;
		case 15:
		case 16:
		case 17:
		case 18:
			return OPTIONSBOX_ITEM_GAP_VERY_SMALL2;
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
			return OPTIONSBOX_ITEM_GAP_SMALL1;
		case 25:
		case 26:
		case 27:
		case 28:
			return OPTIONSBOX_ITEM_GAP_SMALL2;
		case 29:
		case 30:
		case 31:
			return OPTIONSBOX_ITEM_GAP_MEDIUM;
		case 32:
		case 48:
			return OPTIONSBOX_ITEM_GAP_BIG;

		}
		return OPTIONSBOX_ITEM_GAP_SMALL1;
	}

	private int getOptionsItemHGap() {

		// switch (view.getApplication().getFontSize()) {
		switch (optionFont.getSize()) {
		case 8:
		case 9:
		case 10:
			return OPTIONSBOX_ITEM_HGAP_EXTRA_SMALL;
		case 11:
		case 12:
		case 13:
		case 14:
			return OPTIONSBOX_ITEM_HGAP_VERY_SMALL1;
		case 15:
		case 16:
		case 17:
		case 18:
			return OPTIONSBOX_ITEM_HGAP_VERY_SMALL2;
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
			return OPTIONSBOX_ITEM_HGAP_SMALL1;
		case 25:
		case 26:
		case 27:
		case 28:
			return OPTIONSBOX_ITEM_HGAP_SMALL2;
		case 29:
		case 30:
		case 31:
			return OPTIONSBOX_ITEM_HGAP_MEDIUM;
		case 32:
		case 48:
			return OPTIONSBOX_ITEM_HGAP_BIG;

		}
		return OPTIONSBOX_ITEM_HGAP_SMALL1;
	}

	private int getTriangleControlWidth() {
		return selectedDimension.getHeight();
	}

	private int getMultipliedFontSize() {
		return (int) Math.round(
				((getLabelFont().getSize() * geoList.getFontSizeMultiplier())));
	}

	/**
	 * @return preferred width of dropdown
	 */
	int getPreferredWidth() {
		return isOptionsVisible() ? optionsWidth
				: selectedDimension.getWidth()
						+ (isLatexString(selectedText) ? 0
								: 2 * COMBO_TEXT_MARGIN)
						+ getTriangleControlWidth();
	}

	@Override
	public GDimension getPreferredSize() {
		if (selectedDimension == null) {
			return AwtFactory.prototype.newDimension(0, 0);
		}

		return AwtFactory.prototype.newDimension(getPreferredWidth(),
				selectedDimension.getHeight() + COMBO_TEXT_MARGIN);

	}

	@Override
	protected void showWidget() {
		// no widget
	}

	@Override
	protected void hideWidget() {
		// no widget
	}

	//
	// private void debugOptionItems() {
	// App.debug("[OPTRECT] optionItems size: " + optionItems.size());
	// for (GRectangle rect : optionItems) {
	// App.debug("[OPTRECT] (" + rect.getX() + ", " + rect.getY() + ", "
	// + (rect.getX() + rect.getWidth()) + ", "
	// + (rect.getY() + rect.getHeight()) + ")");
	// }
	//
	// }
	private int getOptionAt(int x, int y) {
		int idx = 0;
		for (GRectangle rect : optionItems) {
			boolean inside = rect != null && rect.contains(x, y);
			if (inside) {
				return idx;
			}
			idx++;

		}
		return -1;
	}

	/**
	 * Returns if mouse is hit the options or not.
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return true if options rectangle hit by mouse.
	 */
	public boolean isOptionsHit(int x, int y) {
		return optionsVisible && optionsRect.contains(x, y);
	}

	/**
	 * Called when mouse is over options to highlight item.
	 * 
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 */
	public void onOptionOver(int x, int y) {
		if (!isOptionsHit(x, y)) {
			return;
		}

		currentIdx = getOptionAt(x, y);

		if (currentIdx != -1 && currentIdx != selectedOptionIndex) {
			selectedOptionIndex = currentIdx;
			geoList.updateRepaint();
		}

	}

	/**
	 * Called when user presses down the mouse on the widget.
	 * 
	 * @param x
	 *            Mouse x coordinate.
	 * @param y
	 *            Mouse y coordinate.
	 */
	public void onMouseDown(int x, int y) {
		if (!isDrawingOnCanvas()) {
			return;
		}

		boolean optionHandled = false;
		if (isOptionsVisible()) {
			optionHandled = onOptionDown(x, y);
		}

		if (!optionHandled && isControlHit(x, y)) {
			setOptionsVisible(!isOptionsVisible());
		}
	}

	/**
	 * Called when user presses mouse on dropdown list
	 * 
	 * @param x
	 *            Mouse x coordinate.
	 * @param y
	 *            Mouse y coordinate.
	 */
	public boolean onOptionDown(int x, int y) {
		if (!isDrawingOnCanvas()) {
			return false;
		}
		if (optionsRect.contains(x, y)
				|| optionsRect.getBounds().contains(x, y)) {
			currentIdx = getOptionAt(x, y);
			selectItem();
			return true;
		}
		return false;
	}

	private void selectItem() {
		if (currentIdx != -1) {
			geoList.setSelectedIndex(currentIdx, true);

		}
		closeOptions();

	}

	/**
	 * Open dropdown
	 */
	public void openOptions() {
		setOptionsVisible(false);
	}

	/**
	 * Close dropdown
	 */
	public void closeOptions() {
		setOptionsVisible(false);
	}

	/**
	 * @param x
	 *            mouse x-coord
	 * @param y
	 *            mouse y-coord
	 * @return whether control rectangle was hit
	 */
	public boolean isControlHit(int x, int y) {
		return ctrlRect != null && ctrlRect.contains(x, y);
	}

	/**
	 * @return whether dropdown is visible
	 */
	public boolean isOptionsVisible() {
		return optionsVisible;
	}

	/**
	 * @param optionsVisible
	 *            change visibility of dropdown items
	 */
	private void setOptionsVisible(boolean optionsVisible) {
		this.optionsVisible = optionsVisible;
		if (optionsVisible) {
			currentIdx = 0;
			view.setOpenedComboBox(this);
		} else {
			view.setOpenedComboBox(null);
		}
	}

	/**
	 * toggle visibility of dropdown items
	 */
	public void toggleOptions() {
		if (!isDrawingOnCanvas()) {
			return;
		}
		App.debug("[DROPDOWN] toggle");
		optionsVisible = !optionsVisible;
		geo.updateRepaint();
	}

	/**
	 * Sync selected index to GeoList
	 */
	public void selectCurrentItem() {
		if (!isOptionsVisible()) {
			return;
		}
		recalculateFontSize = false;
		selectItem();
		geo.updateRepaint();
		recalculateFontSize = true;
	}

	/**
	 * Gets DrawList for geo. No type check.
	 * 
	 * @param app
	 *            The current application.
	 * @param geo
	 *            The geo we like to get the DrawList for.
	 * @return The DrawList for the geo element;
	 * 
	 */
	public static DrawList asDrawable(App app, GeoElement geo) {
		return (DrawList) app.getActiveEuclidianView().getDrawableFor(geo);
	}

	/**
	 * Moves dropdown selection indicator up or down by one item.
	 * 
	 * @param down
	 *            Sets if selection indicator should move down or up.
	 */
	public void moveSelectionVertical(boolean down) {
		if (down) {
			if (currentIdx < optionItems.size() - 1) {
				currentIdx++;
			}
		} else {
			if (currentIdx > 0) {
				currentIdx--;
			}

		}
		selectedOptionIndex = currentIdx;
		geo.updateRepaint();
	}

	public void moveSelectionHorizontal(boolean left) {
		int itemInRow = (geoList.size() / colCount) + 1;
		if (left) {
			if (currentIdx < optionItems.size() - itemInRow) {
				currentIdx += itemInRow;

			}
		} else {
			if (currentIdx > itemInRow - 1) {
				currentIdx -= itemInRow;

			}

		}
		selectedOptionIndex = currentIdx;
		geo.updateRepaint();
	}

	/**
	 * 
	 * @return if the dropdown displays its items in multiple columns.
	 */
	public boolean isMultiColumn() {
		return colCount > 1;
	}

	/**
	 * 
	 * @return if list when draw as combo, is selected.
	 */
	public boolean isSelected() {
		return geo.doHighlighting();
	}

}
