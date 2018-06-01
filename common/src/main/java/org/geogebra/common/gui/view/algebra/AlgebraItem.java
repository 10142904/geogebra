package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.IndexLaTeXBuilder;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Utitlity class for AV items
 */
public class AlgebraItem {

	/**
	 * Changes the symbolic flag of a geo or its parent algo
	 * 
	 * @param geo
	 *            element that we want to change
	 * @return whether it's symbolic after toggle
	 */
	public static boolean toggleSymbolic(GeoElement geo) {

		if (geo instanceof HasSymbolicMode) {
			if (geo.getParentAlgorithm() instanceof AlgoSolve) {
				return !((AlgoSolve) geo.getParentAlgorithm()).toggleNumeric();
			}
			((HasSymbolicMode) geo).setSymbolicMode(
					!((HasSymbolicMode) geo).isSymbolicMode(), true);
			geo.updateRepaint();
			return ((HasSymbolicMode) geo).isSymbolicMode();

		}
		return false;
	}

	/**
	 * @param geo
	 *            element
	 * @return arrow or approx, depending on symbolic/numeric nature of the
	 *         element
	 */
	public static String getOutputPrefix(GeoElement geo) {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymbolicMode()) {
			if (!(geo.getParentAlgorithm() instanceof AlgoSolve)
					|| ((AlgoSolve) geo.getParentAlgorithm())
							.getClassName() == Commands.NSolve) {
				return Unicode.CAS_OUTPUT_NUMERIC + "";
			}
		}

		return getSymbolicPrefix(geo.getKernel());
	}

	/**
	 * @param geo
	 *            element
	 * @return whether changing symbolic/numeric for this geo will have any
	 *         effect
	 */
	public static boolean isSymbolicDiffers(GeoElement geo) {
		if (!(geo instanceof HasSymbolicMode)) {
			return false;
		}

		if (geo.getParentAlgorithm() instanceof AlgoSolve) {
			return !allRHSareIntegers((GeoList) geo);
		}

		HasSymbolicMode sm = (HasSymbolicMode) geo;
		boolean orig = sm.isSymbolicMode();
		String text1 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);
		sm.setSymbolicMode(!orig, false);
		String text2 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		sm.setSymbolicMode(orig, false);
		if (text1 == null) {
			return true;
		}

		return !text1.equals(text2);
	}

	private static boolean allRHSareIntegers(GeoList geo) {
		for (int i = 0; i < geo.size(); i++) {
			if (geo.get(i) instanceof GeoLine
					&& !DoubleUtil.isInteger(((GeoLine) geo.get(i)).getZ())) {
				return false;
			}
			if (geo.get(i) instanceof GeoPlaneND
					&& !DoubleUtil.isInteger(((GeoPlaneND) geo.get(i)).getCoordSys()
							.getEquationVector().getW())) {
				return false;
			}
			if (geo.get(i) instanceof GeoList
					&& !allRHSareIntegers(((GeoList) geo.get(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is a numeric that can be written as a fraction
	 */
	public static boolean isGeoFraction(GeoElement geo) {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isFraction();
	}

	/**
	 * @param geo
	 *            element
	 * @return most relevant suggestion
	 */
	public static Suggestion getSuggestions(GeoElement geo) {
		if (geo == null || geo.getKernel()
				.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE) {
			return null;
		}
		Suggestion sug = null;
		boolean casEnabled = geo.getKernel().getApplication().getSettings()
				.getCasSettings().isEnabled();
		if (casEnabled) {
			sug = SuggestionSolve.get(geo);
			if (sug != null) {
				return sug;
			}

		}
		if (geo.getKernel().getApplication().has(Feature.SHOW_STEPS)
				&& casEnabled) {
			sug = SuggestionSteps.get(geo);

			if (sug != null) {
				return sug;
			}
		}

		sug = SuggestionRootExtremum.get(geo);
		if (sug != null) {
			return sug;
		}

		return null;
	}

	/**
	 * @param geo
	 *            element
	 * @param undefinedVariables
	 *            undefined variables
	 * @return most relevant suggestion
	 */
	public static Suggestion getSuggestions(GeoElement geo,
			String undefinedVariables) {
		Suggestion sug = null;
		if (undefinedVariables != null) {
			sug = SuggestionSlider.get();
			if (sug != null) {
				return sug;
			}
		}
		return getSuggestions(geo);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return symbolic prefix (depends on RTL/LTR)
	 */
	public static String getSymbolicPrefix(Kernel kernel) {
		return kernel.getLocalization().rightToLeftReadingOrder
				? Unicode.CAS_OUTPUT_PREFIX_RTL + ""
				: Unicode.CAS_OUTPUT_PREFIX + "";
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is part of packed output (including header)
	 */
	public static boolean needsPacking(GeoElement geo) {
		return geo != null && geo.getPackedIndex() >= 0;
	}

	/**
	 * @param element
	 *            element
	 * @return whether element is part of packed output; exclude header
	 */
	public static boolean isCompactItem(GeoElement element) {
		return element != null && element.getPackedIndex() > 0;
	}

	/**
	 * @param element
	 *            element
	 * @return formula for "Duplicate"
	 */
	public static String getDuplicateFormulaForGeoElement(GeoElement element) {
		String duplicate = "";
		if ("".equals(element.getDefinition(StringTemplate.defaultTemplate))) {
			duplicate = element.getValueForInputBar();
		} else {
			duplicate = element
					.getDefinitionNoLabel(StringTemplate.editorTemplate);
		}

		return duplicate;
	}

	/**
	 * @param element
	 *            element
	 * @return output text (LaTex or plain)
	 */
	public static String getOutputTextForGeoElement(GeoElement element) {
		String outputText = "";
		if (element.isLaTeXDrawableGeo()
				|| AlgebraItem.isGeoFraction(element)) {
			outputText = element.getLaTeXDescriptionRHS(true,
					StringTemplate.latexTemplate);
		} else {
			if (needsPacking(element)) {
				outputText = element.getAlgebraDescriptionLaTeX();
			} else {
				outputText = element.getAlgebraDescriptionRHSLaTeX();
			}
		}

		return outputText;
	}

	/**
	 * @param geo1
	 *            element
	 * @param builder
	 *            index builder
	 * @return whether we did append something to the index builder
	 */
	public static boolean buildPlainTextItemSimple(GeoElement geo1,
			IndexHTMLBuilder builder) {
		int avStyle = geo1.getKernel().getAlgebraStyle();
		if (geo1.isIndependent() && geo1.isGeoPoint()
				&& avStyle == Kernel.ALGEBRA_STYLE_DESCRIPTION) {
			builder.clear();
			builder.append(((GeoPointND) geo1)
					.toStringDescription(StringTemplate.defaultTemplate));
			return true;
		}
		if (geo1.isIndependent() && geo1.getDefinition() == null) {
			geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			return true;
		}
		switch (avStyle) {
		case Kernel.ALGEBRA_STYLE_VALUE:
			geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			return true;

		case Kernel.ALGEBRA_STYLE_DESCRIPTION:
			geo1.addLabelTextOrHTML(geo1.getDefinitionDescription(
					StringTemplate.defaultTemplate), builder);
			return true;

		case Kernel.ALGEBRA_STYLE_DEFINITION:
			geo1.addLabelTextOrHTML(
					geo1.getDefinition(StringTemplate.defaultTemplate),
					builder);
			return true;
		default:
		case Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE:

			return false;
		}
	}

	/**
	 * @param geoElement
	 *            element
	 * @param style
	 *            Kenel.ALGEBRA_STYLE_*
	 * @param sb
	 *            builder
	 */
	public static void getDefinitionText(GeoElement geoElement, int style, IndexHTMLBuilder sb) {
		if (style == Kernel.ALGEBRA_STYLE_DESCRIPTION && needsPacking(geoElement)) {
			String value = geoElement.getDefinitionDescription(StringTemplate.editorTemplate);
			sb.clear();
			sb.append(value);
		} else {
			buildPlainTextItemSimple(geoElement, sb);
		}
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element should be represented by simple text item
	 */
	public static boolean isTextItem(GeoElement geo) {
		return geo instanceof GeoText && !((GeoText) geo).isLaTeX()
				&& !(geo).isTextCommand();
	}

	/**
	 * @param geo
	 *            element
	 * @return whether we should show symbolic switch for the geo
	 */
	public static boolean shouldShowSymbolicOutputButton(GeoElement geo) {
		return isSymbolicDiffers(geo) && !isTextItem(geo);
	}

	/**
	 * add geo to selection with its special points.
	 * 
	 * @param geo
	 *            The geo element to add.
	 * @param app
	 *            application
	 */
	public static void addSelectedGeoWithSpecialPoints(GeoElementND geo,
			App app) {
		if (!app.has(Feature.PREVIEW_POINTS)) {
			return;
		}
		app.getSelectionManager().clearSelectedGeos(false, false);
		app.getSelectionManager().addSelectedGeo(geo, false, false);
	}

	/**
	 * @param geoElement
	 *            about we should decide if the outputrow should be shown or not
	 * @param style
	 *            current algebrastyle
	 * @return whether the output should be shown or not
	 */
	public static DescriptionMode getDescriptionModeForGeo(
			GeoElement geoElement, int style) {
		switch (style) {
		case Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE:
			return geoElement.needToShowBothRowsInAV();

		case Kernel.ALGEBRA_STYLE_DESCRIPTION:
			if (geoElement.getPackedIndex() == 0) {
				return DescriptionMode.DEFINITION_VALUE;
			}
			if (geoElement.getPackedIndex() > 0) {
				return DescriptionMode.VALUE;
			}
			return geoElement instanceof GeoNumeric
					&& (!geoElement.isIndependent() || (geoElement
							.needToShowBothRowsInAV() == DescriptionMode.DEFINITION_VALUE
							&& geoElement.getParentAlgorithm() == null))
									? DescriptionMode.DEFINITION_VALUE
									: DescriptionMode.DEFINITION;
		case Kernel.ALGEBRA_STYLE_DEFINITION:
			return DescriptionMode.DEFINITION;
		default:
		case Kernel.ALGEBRA_STYLE_VALUE:
			return DescriptionMode.VALUE;
		}
	}
	/**
	 * @param geoElement
	 *            about we should decide if the outputrow should be shown or not
	 * @param style
	 *            current algebrastyle
	 * @return whether the output should be shown or not
	 */
	public static boolean shouldShowOutputRowForAlgebraStyle(GeoElement geoElement, int style) {
		if (style == Kernel.ALGEBRA_STYLE_DESCRIPTION) {
			return getDescriptionModeForGeo(geoElement, style) != DescriptionMode.DEFINITION;
		}
		return style != Kernel.ALGEBRA_STYLE_VALUE && style != Kernel.ALGEBRA_STYLE_DEFINITION;
	}

	/**
	 * Tells whether AV should show two rows for a geo element.
	 *
	 * @param element the element
	 * @param style the algebra style
	 * @return true if both rows should be shown.
	 */
	public static boolean shouldShowBothRows(GeoElement element, int style) {
		return ((element.needToShowBothRowsInAV() == DescriptionMode.DEFINITION_VALUE ||
				(AlgebraItem.isTextItem(element) && !element.isIndependent())) &&
				shouldShowOutputRowForAlgebraStyle(element, style));
	}

	/**
	 *
	 * @param element geo
	 * @param style AV style
	 * @return description string for element to show in AV row; null if element prefers showing definition
	 */
	public static String getDescriptionString(GeoElement element, int style) {
		if (element.mayShowDescriptionInsteadOfDefinition()) {
			IndexLaTeXBuilder builder = new IndexLaTeXBuilder();
			AlgebraItem.getDefinitionText(element, style, builder);
			return getLatexText(builder.toString().replace("^", "\\^{\\;}"));
		}
		return null;
	}

	private static String getLatexText(String text) {
		return "\\text{" + text + '}';
	}
}
