package org.geogebra.web.web.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.sliderPanel.SliderPanelW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.PenPreview;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pen/Eraser/Color submenu for MOWToolbar.
 * 
 * @author Laszlo Gal
 *
 */
public class PenSubMenu extends SubMenuPanel {
	private static final int MAX_PEN_SIZE = 12;
	private static final int MAX_ERASER_SIZE = 100;
	private static final int PEN_STEP = 1;
	private static final int ERASER_STEP = 20;
	private static final int BLACK = 0;
	private StandardButton pen;
	private StandardButton eraser;
	private StandardButton move;
	private StandardButton select;
	private FlowPanel penPanel;
	private FlowPanel colorPanel;
	private FlowPanel sizePanel;
	private Label btnColor[];
	private GColor penColor[];
	private SliderPanelW slider;
	private StandardButton btnCustomColor;
	private PenPreview preview;
	/** whether colors are enabled */
	boolean colorsEnabled;
	// preset colors black, green, teal,blue, purple,magenta, red, carrot,
	// yellow
	private final static String hexColors[] = { "000000", "2E7D32", "00A8A8",
			"1565C0", "6557D2", "CC0099", "D32F2F", "DB6114", "FFCC00" };
	private GColor lastSelectedColor = null;

	/**
	 * 
	 * @param app
	 *            ggb app.
	 */
	public PenSubMenu(AppW app) {
		super(app/* , false */);
		addStyleName("penSubMenu");
	}

	private void createPenPanel() {
		penPanel = new FlowPanel();
		penPanel.addStyleName("penPanel");
		pen = createButton(EuclidianConstants.MODE_PEN);
		// pen gets a separate icon here so it can show the selected color
		ToolbarResources pr = ImageFactory.getToolbarResources();
		NoDragImage im = new NoDragImage(ImgResourceHelper.safeURI(pr.mode_pen_white_32()), 32);
		im.addStyleName("opacityFixForOldIcons");
		pen.getUpFace().setImage(im);
		pen.addStyleName("plusMarginLeft");
		eraser = createButton(EuclidianConstants.MODE_ERASER);
		eraser.addStyleName("plusMarginLeft");
		move = createButton(EuclidianConstants.MODE_MOVE);
		select = createButton(EuclidianConstants.MODE_SELECT);
		penPanel.add(LayoutUtilW.panelRow(move, pen, select, eraser));
	}

	/**
	 * Create color buttons for selecting pen color
	 * 
	 * @param aColor
	 * @return
	 */
	private Label createColorButton(GColor aColor, final int colorIndex) {
		ImageOrText color = GeoGebraIconW.createColorSwatchIcon(1, null,
				aColor);
		Label label = new Label();
		color.applyToLabel(label);
		label.addStyleName("mowColorButton");
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {

				if (!colorsEnabled) {
					return;
				}

				selectColor(colorIndex);

			}
		});
		return label;
	}
	private void createColorPanel() {
		colorPanel = new FlowPanel();
		colorPanel.addStyleName("colorPanel");
		btnColor = new Label[hexColors.length];
		penColor = new GColor[hexColors.length];
		for (int i = 0; i < hexColors.length; i++) {
			penColor[i] = GColor
					.newColorRGB(Integer.parseInt(hexColors[i], 16));
			btnColor[i] = createColorButton(penColor[i], i);
		}
		btnCustomColor = new StandardButton(
				new ImageResourcePrototype(null,
						MaterialDesignResources.INSTANCE.add_black()
								.getSafeUri(),
						0, 0, 24, 24, false, false),
				app);
		btnCustomColor.addStyleName("mowColorButton");
		btnCustomColor.addStyleName("mowColorPlusButton");
		btnCustomColor.addFastClickHandler(this);
		colorPanel.add(LayoutUtilW.panelRow(btnColor[0], btnColor[1],
				btnColor[2], btnColor[3], btnColor[4], btnColor[5], btnColor[6], btnColor[7], btnColor[8],
				btnCustomColor));
	}

	/**
	 * Create panel with slider for pen and eraser size
	 */
	private void createSizePanel() {
		sizePanel = new FlowPanel();
		sizePanel.addStyleName("sizePanel");
		slider = new SliderPanelW(0, 20, app.getKernel(), false);
		slider.addStyleName("mowOptionsSlider");
		preview = new PenPreview(app, 50, 30);
		preview.addStyleName("preview");
		slider.add(preview);
		sizePanel.add(slider);
		slider.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				sliderValueChanged(event.getValue());
			}
		});
	}

	/**
	 * Sets the size of pen/eraser from the slider value.
	 * 
	 * @param value
	 *            value of slider
	 */
	void sliderValueChanged(double value) {
		if (colorsEnabled) {
			getPenGeo().setLineThickness((int) value);
			updatePreview();
		} else {
			app.getActiveEuclidianView().getSettings().setDeleteToolSize((int) value);
		}
		closeFloatingMenus();
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		createPenPanel();
		createColorPanel();
		createSizePanel();
		contentPanel.add(
				LayoutUtilW.panelRow(penPanel, colorPanel, sizePanel));
	}

	@Override
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		if (app.isModeValid(menu.get(0).intValue())) {
			panel.add(createButton(menu.get(0).intValue()));
		}
	}

	@Override
	public void onClick(Widget source) {
		if (source == pen) {
			app.setMode(EuclidianConstants.MODE_PEN);
		} else if (source == eraser) {
			app.setMode(EuclidianConstants.MODE_ERASER);
		} else if (source == move) {
			app.setMode(EuclidianConstants.MODE_MOVE);
		} else if (source == select) {
			app.setMode(EuclidianConstants.MODE_SELECT);
		} else if (source == btnCustomColor) {
			openColorDialog();
		}
		closeFloatingMenus();
	}



	private void doSelectPen() {
		pen.getElement().setAttribute("selected", "true");
		setColorsEnabled(true);
		if (lastSelectedColor == null) {
			selectColor(BLACK);
		} else {
			selectColor(lastSelectedColor);
		}
		slider.setMinimum(1, false);
		slider.setMaximum(MAX_PEN_SIZE, false);
		slider.setStep(PEN_STEP);
		slider.setValue((double) getPenGeo().getLineThickness());
		slider.getElement().setAttribute("disabled", "false");
		preview.setVisible(true);
		updatePreview();
	}

	private void doSelectEraser() {
		reset();
		eraser.getElement().setAttribute("selected", "true");
		setColorsEnabled(false);
		slider.setMinimum(1, false);
		slider.setMaximum(MAX_ERASER_SIZE, false);
		slider.setStep(ERASER_STEP);
		int delSize = app.getActiveEuclidianView().getSettings()
				.getDeleteToolSize();
		slider.setValue((double) delSize);
		slider.getElement().setAttribute("disabled", "false");
		preview.setVisible(false);
	}

	private void doSelectMove() {
		reset();
		move.getElement().setAttribute("selected", "true");
		slider.getElement().setAttribute("disabled", "true");
	}

	private void doSelectSelect() {
		reset();
		select.getElement().setAttribute("selected", "true");
		slider.getElement().setAttribute("disabled", "true");
	}

	/**
	 * Unselect all buttons and disable colors
	 */
	public void reset() {
		pen.getElement().setAttribute("selected", "false");
		eraser.getElement().setAttribute("selected", "false");
		move.getElement().setAttribute("selected", "false");
		select.getElement().setAttribute("selected", "false");
		setColorsEnabled(false);
	}

	/**
	 * @param colorStr
	 *            color string
	 */
	public void setPenIconColor(String colorStr) {
		// set background of pen icon to selected color
		pen.getElement().getFirstChildElement().getNextSiblingElement()
				.setAttribute("style", "background-color: " + colorStr);
	}

	/**
	 * @param idx
	 *            index
	 */
	public void selectColor(int idx) {
		for (int i = 0; i < btnColor.length; i++) {
			if (idx == i) {
				getPenGeo().setObjColor(penColor[i]);
				if (colorsEnabled) {
					lastSelectedColor = penColor[i];
					btnColor[i].addStyleName("mowColorButton-selected");
					setPenIconColor(penColor[i].toString());
				}
			} else {
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		updatePreview();
	}

	// remember and set a color that was picked from color chooser
	private void selectColor(GColor color) {
		getPenGeo().setObjColor(color);
		if (colorsEnabled) {
			setPenIconColor(color.toString());
		}
		updatePreview();
	}

	private void setColorsEnabled(boolean enable) {
		for (int i = 0; i < btnColor.length; i++) {
			if (enable) {
				btnColor[i].removeStyleName("disabled");
				if (penColor[i] == lastSelectedColor) {
					btnColor[i].addStyleName("mowColorButton-selected");
				}
			} else {
				btnColor[i].addStyleName("disabled");
				btnColor[i].removeStyleName("mowColorButton-selected");
			}
		}
		if (enable) {
			btnCustomColor.removeStyleName("disabled");
		} else {
			btnCustomColor.addStyleName("disabled");
		}
		colorsEnabled = enable;
	}

	private GeoElement getPenGeo() {
		return app.getActiveEuclidianView().getEuclidianController()
				.getPen().DEFAULT_PEN_LINE;
	}

	@Override
	public void setMode(int mode) {
		reset();
		if (mode == EuclidianConstants.MODE_MOVE) {
			doSelectMove();
		} else if (mode == EuclidianConstants.MODE_SELECT) {
			doSelectSelect();
		} else if (mode == EuclidianConstants.MODE_ERASER) {
			doSelectEraser();
		} else if (mode == EuclidianConstants.MODE_PEN) {
			doSelectPen();
		}
	}

	/**
	 * @return last selected color
	 */
	public GColor getLastSelectedColor() {
		return lastSelectedColor;
	}

	/**
	 * @param lastSelectedColor
	 *            update last selected color
	 */
	public void setLastSelectedColor(GColor lastSelectedColor) {
		this.lastSelectedColor = lastSelectedColor;
	}

	/**
	 * @return get preview of pen
	 */
	public PenPreview getPreview() {
		return preview;
	}

	@Override
	public int getFirstMode() {
		return EuclidianConstants.MODE_PEN;
	}

	private void updatePreview() {
		preview.update();
	}

	private void openColorDialog() {
		if (colorsEnabled) {
			final GeoElement penGeo = getPenGeo();
			DialogManagerW dm = (DialogManagerW) (app.getDialogManager());
			GColor originalColor = penGeo.getObjectColor();
			dm.showColorChooserDialog(originalColor, new ColorChangeHandler() {
				@Override
				public void onForegroundSelected() {
					// do nothing here
				}

				@Override
				public void onColorChange(GColor color) {
					penGeo.setObjColor(color);
					setPenIconColor(color.toString());
					setLastSelectedColor(color);
					selectColor(-1);
					getPreview().update();
				}

				@Override
				public void onClearBackground() {
					// do nothing
				}

				@Override
				public void onBarSelected() {
					// do nothing
				}

				@Override
				public void onBackgroundSelected() {
					// do nothing
				}

				@Override
				public void onAlphaChange() {
					// do nothing
				}
			});
		}
	}
}
