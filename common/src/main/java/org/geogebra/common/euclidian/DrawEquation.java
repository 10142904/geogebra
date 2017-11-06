package org.geogebra.common.euclidian;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
//import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/**
 * Cross platform helper for equation rendering
 */
public abstract class DrawEquation {

	/**
	 * @param app
	 *            application
	 * @param geo
	 *            geo
	 * @param g2
	 *            graphics
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 * @param useCache
	 *            true to cache
	 * @return dimensions of result
	 */
	public abstract GDimension drawEquation(App app, GeoElementND geo,
			GGraphics2D g2, int x, int y, String text, GFont font,
			boolean serif, GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, Runnable callback);

	/**
	 * @return \newcommand definitions for GeoGebra specific commands
	 */
	public static StringBuilder getJLMCommands() {
		StringBuilder initJLM = new StringBuilder();

		// https://dev.geogebra.org/trac/changeset/47736
		initJLM.append("\\newcommand{\\pcdot}{\\space} ");

		HashMap<String, GColor> ggbCols = GeoGebraColorConstants
				.getGeoGebraColors();

		Iterator<Entry<String, GColor>> it = ggbCols.entrySet().iterator();

		// add commands eg \red{text}
		while (it.hasNext()) {
			Entry<String, GColor> colPair = it.next();

			String colStr = colPair.getKey();

			// can't have command eg \grey2
			if (!Character.isDigit(colStr.charAt(colStr.length() - 1))) {
				GColor col = colPair.getValue();

				// eg
				// initJLM.append("\\newcommand{\\red}[1]{\\textcolor{255,0,0}{#1}}
				// ");
				initJLM.append("\\newcommand{\\");
				initJLM.append(colStr);
				initJLM.append("}[1]{\\textcolor{");
				initJLM.append(col.getRed());
				initJLM.append(',');
				initJLM.append(col.getGreen());
				initJLM.append(',');
				initJLM.append(col.getBlue());
				initJLM.append("}{#1}} ");

			}
		}
		return initJLM;
	}

	/**
	 * Renders LaTeX equation using JLaTeXMath
	 * 
	 * @param app
	 *            application
	 * @param geo
	 *            geo
	 * @param g2
	 *            graphics
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 * @param useCache
	 *            true to cache
	 * @param maxWidth
	 * @param lineSpace
	 *            line space in centimeters
	 * @return dimensions of result
	 */
	final public GDimension drawEquation(final App app, final GeoElementND geo,
			final Graphics2DInterface g2, final int x, final int y,
			final String text, final GFont font, final boolean serif,
			final Color fgColor, final Color bgColor,
			final boolean useCache, final Integer maxWidth,
			final Double lineSpace) {
		// TODO uncomment when \- works
		// text=addPossibleBreaks(text);

		int width = -1;
		int height = -1;
		// int depth = 0;

		int style = font.getLaTeXStyle(serif);

		// if we're exporting, we want to draw it full resolution
		if (app.isExporting() || !useCache) {

			// Application.debug("creating new icon for: "+text);
			TeXIcon icon = createIcon(text, fgColor, font, style, maxWidth,
					lineSpace, app);

			HasForegroundColor fg = new HasForegroundColor() {

				@Override
				public Color getForegroundColor() {
					return fgColor;
				}

			};

			icon.paintIcon(fg, g2, x, y);

			return AwtFactory.getPrototype().newDimension(icon.getIconWidth(),
					icon.getIconHeight());

		}

		Image im = null;
		try {
			final int ret[] = new int[2];
			checkFirstCall(app);
			im = getCachedDimensions(text, geo, fgColor, font, style, ret);

			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View
			checkFirstCall(app);

			try {
				final TeXFormula formula = TeXFormula
						.getPartialTeXFormula(text);
				im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, convertColor(GColor.BLACK),
						convertColor(GColor.WHITE));

				Log.warn("latex syntax error\n" + text + "\n" + e.getMessage());

			} catch (Exception e2) {

				final TeXFormula formula = TeXFormula
						.getPartialTeXFormula("\\textcolor{red}{?}");
				im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, convertColor(GColor.BLACK),
						convertColor(GColor.WHITE));

				Log.error(
						"serious latex error\n" + text + "\n" + e.getMessage());

			}
		}

		g2.drawImage(im, x, y);

		if (width == -1) {
			width = im.getWidth();
		}
		if (height == -1) {
			height = im.getHeight();
		}

		return AwtFactory.getPrototype().newDimension(width, height);
	}

	public TeXIcon createIcon(String text, Color fgColor, GFont font, int style,
			Integer maxWidth, Double lineSpace, App app) {
		checkFirstCall(app);
		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			if (maxWidth == null) {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);
			} else {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, TeXConstants.UNIT_CM,
						maxWidth.intValue(), TeXConstants.ALIGN_LEFT,
						TeXConstants.UNIT_CM, lineSpace.doubleValue());
			}
		} catch (final MyError e) {
			// e.printStackTrace();
			// Application.debug("MyError LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, fgColor);

			formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
					TeXConstants.UNIT_CM, 4f, TeXConstants.ALIGN_LEFT,
					TeXConstants.UNIT_CM, 0.5f);

		} catch (final Exception e) {
			// e.printStackTrace();
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View
			try {
				formula = TeXFormula.getPartialTeXFormula(text);

				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);
			} catch (Exception e2) {
				Log.debug("LaTeX parse exception: " + e.getMessage() + "\n"
						+ text);
				formula = TeXFormula
						.getPartialTeXFormula(
								"\\text{"
										+ app.getLocalization().getError(
												"CAS.GeneralErrorMessage")
										+ "}");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);
			}
		}
		icon.setInsets(new Insets(1, 1, 1, 1));
		return icon;
	}

	protected abstract Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret);

	/**
	 * Initialize commands if this is the first run
	 * 
	 * @param app
	 *            application
	 */
	public abstract void checkFirstCall(App app);

	/**
	 * @param color
	 *            GeoGebra color
	 * @return LaTeX color
	 */
	public abstract Color convertColor(GColor color);

	/**
	 * @param app
	 *            application
	 * @param geo0
	 *            element
	 * @param text
	 * @param font
	 * @param serif
	 * @return
	 */
	public abstract GDimension measureEquation(App app, GeoElement geo0,
			String text, GFont font, boolean serif);

	/**
	 * @param app
	 *            application
	 * @param geo
	 *            geo
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param maxWidth
	 * @param lineSpace
	 *            line space in centimeters
	 * @return dimensions of result
	 */
	final public GDimension measureEquationJLaTeXMath(final App app,
			final GeoElement geo, final int x, final int y, final String text,
			final GFont font, final boolean serif, final Integer maxWidth,
			final Double lineSpace) {

		checkFirstCall(app);
		GColor fgColor = GColor.BLACK;
		int style = font.getLaTeXStyle(serif);

		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			if (maxWidth == null) {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, convertColor(fgColor));
			} else {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, TeXConstants.UNIT_CM,
						maxWidth.intValue(), TeXConstants.ALIGN_LEFT,
						TeXConstants.UNIT_CM, lineSpace.doubleValue());
			}
		} catch (final MyError e) {
			// e.printStackTrace();
			// Application.debug("MyError LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));

			formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
					TeXConstants.UNIT_CM, 4f, TeXConstants.ALIGN_LEFT,
					TeXConstants.UNIT_CM, 0.5f);

		} catch (final Exception e) {
			// e.printStackTrace();
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));

		}
		icon.setInsets(new Insets(1, 1, 1, 1));

		return AwtFactory.getPrototype().newDimension(icon.getIconWidth(),
				icon.getIconHeight());

	}
}
