package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.kernel.Kernel;

/**
 * @author michael
 *
 */
public class GeoGebraPreferencesXML {

	/**
	 * these can get changed by --screenDPI (and maybe by --screenResX,
	 * --screenResY)
	 */
	private static int defaultFontSize = 16;
	private static int defaultWindowX = 800;
	private static int defaultWindowY = 600;

	/**
	 * @param app
	 *            application
	 * @return defaults as XML
	 */
	public static String getXML(App app) {

		int rightAngleStyle = app.getLocalization().getRightAngleStyle();
		boolean xAxis = app.getSettings().getEuclidian(1).getShowAxis(0);
		boolean yAxis = app.getSettings().getEuclidian(1).getShowAxis(1);

		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<geogebra format=\"5.0\" xsi:noNamespaceSchemaLocation=\"http://www.geogebra.org/ggb.xsd\" xmlns=\"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
				+ "<gui>"

				+ "<window width=\""

				// dynamic bit!
				+ defaultWindowX + "\" height=\""

				// dynamic bit!
				+ defaultWindowY

				+ "\" />"
				+ "<settings ignoreDocument=\"false\" showTitleBar=\"true\" />"
				+ "<labelingStyle val=\""

				+ (app.isUnbundled()
						? (app.getSettings().getToolbarSettings()
												.getType() == AppType.GRAPHING_CALCULATOR
										? 1 : 0)
						: 0)
				+ "\"/>"
				+ "<font  size=\""

				// dynamic bit!
				+ defaultFontSize

				+ "\"/>" + "<menuFont size=\"-1\"/>"
				+ "<tooltipSettings language=\"\" timeout=\"0\"/>" + "</gui>"
				+ "<euclidianView>"

				+ "<size width=\"640\" height=\"480\"/>"
				+ "<coordSystem xZero=\"215.0\" yZero=\"315.0\" scale=\"50.0\" yscale=\"50.0\"/>"
				+ "<evSettings axes=\"true\" grid=\"" + xAxis
				+ "\" gridIsBold=\"false\" pointCapturing=\"3\" rightAngleStyle=\""

				// dynamic
				+ rightAngleStyle

				+ "\" checkboxSize=\"26\" gridType=\""
				+ +EuclidianView.GRID_CARTESIAN_WITH_SUBGRID + "\"/>"
				+ "<bgColor r=\"255\" g=\"255\" b=\"255\"/>"
				+ "<axesColor r=\"0\" g=\"0\" b=\"0\"/>"
				+ "<gridColor r=\"192\" g=\"192\" b=\"192\"/>"
				+ "<lineStyle axes=\"1\" grid=\"0\"/>"
				+ "<axis id=\"0\" show=\"" + xAxis
				+ "\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\""
				+ " axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "<axis id=\"1\" show=\"" + yAxis
				+ "\" label=\"\" unitLabel=\"\" tickStyle=\"1\" showNumbers=\"true\""
				+ " axisCross=\"0.0\" positiveAxis=\"false\"/>"
				+ "</euclidianView>"

				+ "<kernel>" + "<continuous val=\"false\"/>"

				+ "<decimals val=\""
				+ ((app.has(Feature.OBJECT_DEFAULTS_AND_COLOR)
						&& app.isUnbundledGeometry()) ? 1 : 2)
				+ "\"/>"
				+ "<angleUnit val=\"degree\"/>"

				+ "<algebraStyle val=\""

				// dynamic bit!
				+ (app.has(Feature.AV_DEFINITION_AND_VALUE)
						? Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
						: Kernel.ALGEBRA_STYLE_VALUE)

				+ "\"/>"

				+ "<coordStyle val=\"0\"/>"
				+ "<localization digits=\"false\" labels=\"true\"/>"
				+ "<angleFromInvTrig val=\"false\"/>"
				+ "<casSettings timeout=\"5\" expRoots=\"true\"/>"

				+ "</kernel>"

				+ "<scripting blocked=\"false\"/>" + "</geogebra>";
	}

	/**
	 * @param fontSize
	 *            font size
	 */
	public static void setDefaultFontSize(int fontSize) {
		defaultFontSize = fontSize;

	}

	/**
	 * @param width
	 *            window width
	 */
	public static void setDefaultWindowX(int width) {
		defaultWindowX = width;
	}

	/**
	 * @param height
	 *            whindow height
	 */
	public static void setDefaultWindowY(int height) {
		defaultWindowY = height;

	}

}
