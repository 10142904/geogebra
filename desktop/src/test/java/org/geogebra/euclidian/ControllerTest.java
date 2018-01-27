package org.geogebra.euclidian;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.desktop.main.AppDNoGui;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ControllerTest {
	private static AppDNoGui app;
	private static EuclidianController ec;

	@BeforeClass
	public static void setup() {
		app = CommandsTest.createApp();
		ec = app.getActiveEuclidianView().getEuclidianController();
	}

	@Before
	public void clear() {
		app.getKernel().clearConstruction(true);
		app.getActiveEuclidianView().clearView();
	}
	
	@Test
	public void moveTool() {
		app.setMode(EuclidianConstants.MODE_MOVE); // TODO 0
	}

	@Test
	public void pointTool() {
		app.setMode(EuclidianConstants.MODE_POINT);
		click(10, 10);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}

	@Test
	public void joinTool() {
		app.setMode(EuclidianConstants.MODE_JOIN); // TODO 2
	}

	@Test
	public void parallelTool() {
		app.setMode(EuclidianConstants.MODE_PARALLEL); // TODO 3
	}

	@Test
	public void orthogonalTool() {
		app.setMode(EuclidianConstants.MODE_ORTHOGONAL); // TODO 4
	}

	@Test
	public void intersectTool() {
		app.setMode(EuclidianConstants.DEFAULT_PEN_SIZE); // TODO 5
	}

	@Test
	public void deleteTool() {
		app.setMode(EuclidianConstants.MODE_DELETE); // TODO 6
	}

	@Test
	public void vectorTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR); // TODO 7
	}

	@Test
	public void lineBisectorTool() {
		app.setMode(EuclidianConstants.SHOW_SYMBOLBUTTON_MINLENGTH); // TODO 8
	}

	@Test
	public void angularBisectorTool() {
		app.setMode(EuclidianConstants.MODE_ANGULAR_BISECTOR); // TODO 9
	}

	@Test
	public void circle2Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_TWO_POINTS); // TODO 10
	}

	@Test
	public void circle3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_THREE_POINTS); // TODO 11
	}

	@Test
	public void conic5Tool() {
		app.setMode(EuclidianConstants.MODE_CONIC_FIVE_POINTS); // TODO 12
	}

	@Test
	public void tangentTool() {
		app.setMode(EuclidianConstants.MODE_TANGENTS); // TODO 13
	}

	@Test
	public void relationTool() {
		app.setMode(EuclidianConstants.MODE_RELATION); // TODO 14
	}

	@Test
	public void segmentTool() {
		app.setMode(EuclidianConstants.MODE_SEGMENT);
		click(10, 10);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f = 2.82843");
	}

	@Test
	public void polygonTool() {
		app.setMode(EuclidianConstants.MODE_POLYGON);
		click(10, 10);
		click(100, 10);
		click(100, 100);
		click(10, 100);
		click(10, 10);
		checkContent("A = (0, 0)", "B = (2, 0)", "C = (2, -2)",
				"D = (0, -2)", "q1 = 4", "a = 2", "b = 2", "c = 2", "d = 2");
	}

	@Test
	public void textTool() {
		app.setMode(EuclidianConstants.MODE_TEXT); // TODO 17
	}

	@Test
	public void rayTool() {
		app.setMode(EuclidianConstants.MODE_RAY);
		click(10, 10);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: 2x + 2y = 0");

	}

	@Test
	public void midpointTool() {
		app.setMode(EuclidianConstants.MODE_MIDPOINT); // TODO 19
	}

	@Test
	public void circleArc3Tool() {
		app.setMode(EuclidianConstants.DEFAULT_ERASER_SIZE); // TODO 20
	}

	@Test
	public void circleSector3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS); // TODO
																			// 21
	}

	@Test
	public void circumcircleArc3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS); // TODO
																			// 22
	}

	@Test
	public void circumcircleSector3Tool() {
		app.setMode(EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS); // TODO
																				// 23
	}

	@Test
	public void semicircleTool() {
		app.setMode(EuclidianConstants.MODE_SEMICIRCLE); // TODO 24
	}

	@Test
	public void sliderTool() {
		app.setMode(EuclidianConstants.MODE_SLIDER); // TODO 25
	}

	@Test
	public void imageTool() {
		app.setMode(EuclidianConstants.DEFAULT_CHECKBOX_SIZE); // TODO 26
	}

	@Test
	public void showHideObjectTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_OBJECT); // TODO 27
	}

	@Test
	public void showHideLabelTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL); // TODO 28
	}

	@Test
	public void mirrorAtPointTool() {
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_POINT); // TODO 29
	}

	@Test
	public void mirrorAtLineTool() {
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_LINE); // TODO 30
	}

	@Test
	public void translateByVectorTool() {
		app.setMode(EuclidianConstants.MODE_TRANSLATE_BY_VECTOR); // TODO 31
	}

	@Test
	public void rotateByAngleTool() {
		app.setMode(EuclidianConstants.MODE_ROTATE_BY_ANGLE); // TODO 32
	}

	@Test
	public void dilateFromPointTool() {
		app.setMode(EuclidianConstants.MODE_DILATE_FROM_POINT); // TODO 33
	}

	@Test
	public void circlePointRadiusTool() {
		app.setMode(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS); // TODO 34
	}

	@Test
	public void copyVisualStyleTool() {
		app.setMode(EuclidianConstants.MODE_COPY_VISUAL_STYLE); // TODO 35
	}

	@Test
	public void angleTool() {
		app.setMode(EuclidianConstants.MODE_ANGLE); // TODO 36
	}

	@Test
	public void vectorFromPointTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR_FROM_POINT); // TODO 37
	}

	@Test
	public void distanceTool() {
		app.setMode(EuclidianConstants.MODE_DISTANCE); // TODO 38
	}

	@Test
	public void moveRotateTool() {
		app.setMode(EuclidianConstants.MODE_MOVE_ROTATE); // TODO 39
	}

	@Test
	public void translateViewTool() {
		app.setMode(EuclidianConstants.MODE_TRANSLATEVIEW); // TODO 40
	}

	@Test
	public void zoomInTool() {
		app.setMode(EuclidianConstants.MODE_ZOOM_IN); // TODO 41
	}

	@Test
	public void zoomOutTool() {
		app.setMode(EuclidianConstants.MODE_ZOOM_OUT); // TODO 42
	}

	@Test
	public void selectionListenerTool() {
		app.setMode(EuclidianConstants.MODE_SELECTION_LISTENER); // TODO 43
	}

	@Test
	public void polarDiameterTool() {
		app.setMode(EuclidianConstants.MODE_POLAR_DIAMETER); // TODO 44
	}

	@Test
	public void segmentFixedTool() {
		app.setMode(EuclidianConstants.MODE_SEGMENT_FIXED); // TODO 45
	}

	@Test
	public void angleFixedTool() {
		app.setMode(EuclidianConstants.MODE_ANGLE_FIXED); // TODO 46
	}

	@Test
	public void locusTool() {
		app.setMode(EuclidianConstants.MODE_LOCUS); // TODO 47
	}

	@Test
	public void areaTool() {
		app.setMode(EuclidianConstants.MODE_AREA); // TODO 49
	}

	@Test
	public void slopeTool() {
		app.setMode(EuclidianConstants.MODE_SLOPE); // TODO 50
	}

	@Test
	public void regularPolygonTool() {
		app.setMode(EuclidianConstants.MODE_REGULAR_POLYGON); // TODO 51
	}

	@Test
	public void showCheckBoxTool() {
		app.setMode(EuclidianConstants.MODE_SHOW_HIDE_CHECKBOX); // TODO 52
	}

	@Test
	public void compassesTool() {
		app.setMode(EuclidianConstants.MODE_COMPASSES); // TODO 53
	}

	@Test
	public void mirrorAtCircleTool() {
		app.setMode(EuclidianConstants.MODE_MIRROR_AT_CIRCLE); // TODO 54
	}

	@Test
	public void ellipse3Tool() {
		app.setMode(EuclidianConstants.MODE_ELLIPSE_THREE_POINTS); // TODO 55
	}

	@Test
	public void hyperbola3Tool() {
		app.setMode(EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS); // TODO 56
	}

	@Test
	public void parabolaTool() {
		app.setMode(EuclidianConstants.MODE_PARABOLA); // TODO 57
	}

	@Test
	public void fitLineTool() {
		app.setMode(EuclidianConstants.MODE_FITLINE); // TODO 58
	}

	@Test
	public void buttonActionTool() {
		app.setMode(EuclidianConstants.MODE_BUTTON_ACTION); // TODO 60
	}

	@Test
	public void textFieldActionTool() {
		app.setMode(EuclidianConstants.MODE_TEXTFIELD_ACTION); // TODO 61
	}

	@Test
	public void penTool() {
		app.setMode(EuclidianConstants.MODE_PEN); // TODO 62
	}

	@Test
	public void rigidPolygonTool() {
		app.setMode(EuclidianConstants.MODE_RIGID_POLYGON); // TODO 64
	}

	@Test
	public void polyLineTool() {
		app.setMode(EuclidianConstants.MODE_POLYLINE); // TODO 65
	}

	@Test
	public void probabilityCalculatorTool() {
		app.setMode(EuclidianConstants.MODE_PROBABILITY_CALCULATOR); // TODO 66
	}

	@Test
	public void attachDetachPointTool() {
		app.setMode(EuclidianConstants.MODE_ATTACH_DETACH); // TODO 67
	}

	@Test
	public void functionInspectorTool() {
		app.setMode(EuclidianConstants.MODE_FUNCTION_INSPECTOR); // TODO 68
	}

	@Test
	public void intersectionCurveTool() {
		app.setMode(EuclidianConstants.MODE_INTERSECTION_CURVE); // TODO 69
	}

	@Test
	public void vectorPolygonTool() {
		app.setMode(EuclidianConstants.MODE_VECTOR_POLYGON); // TODO 70
	}

	@Test
	public void createListTool() {
		app.setMode(EuclidianConstants.MODE_CREATE_LIST); // TODO 71
	}

	@Test
	public void complexNumberTool() {
		app.setMode(EuclidianConstants.MODE_COMPLEX_NUMBER); // TODO 72
	}

	@Test
	public void freehandShapeTool() {
		app.setMode(EuclidianConstants.MODE_FREEHAND_SHAPE); // TODO 73
	}

	@Test
	public void extremumTool() {
		app.setMode(EuclidianConstants.MODE_EXTREMUM); // TODO 75
	}

	@Test
	public void rootsTool() {
		app.setMode(EuclidianConstants.MODE_ROOTS); // TODO 76
	}

	@Test
	public void selectTool() {
		app.setMode(EuclidianConstants.MODE_SELECT); // TODO 77
	}

	@Test
	public void lineTool() {
		app.setMode(EuclidianConstants.MODE_JOIN);
		click(10, 10);
		click(100, 100);
		checkContent("A = (0, 0)", "B = (2, -2)", "f: x + y = 0");
	}

	@Test
	public void shapeTriangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_TRIANGLE); // TODO 102
	}

	@Test
	public void shapeSquareTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_SQUARE); // TODO 103
	}

	@Test
	public void shapeRectangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE); // TODO 104
	}

	@Test
	public void shapeRoundedRectangleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_RECTANGLE_ROUND_EDGES); // TODO
																			// 105
	}

	@Test
	public void shapePolygonTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_POLYGON); // TODO 106
	}

	@Test
	public void shapeFreeformTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_FREEFORM); // TODO 107
	}

	@Test
	public void circleTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_CIRCLE); // TODO 108
	}

	@Test
	public void ellipseTool() {
		app.setMode(EuclidianConstants.MODE_SHAPE_ELLIPSE); // TODO 109
	}

	@Test
	public void eraserTool() {
		app.setMode(EuclidianConstants.MODE_ERASER); // TODO 110
	}

	@Test
	public void highlighterTool() {
		app.setMode(EuclidianConstants.MODE_HIGHLIGHTER); // TODO 111
	}

	@Test
	public void penPanelTool() {
		app.setMode(EuclidianConstants.MODE_PEN_PANEL); // TODO 112
	}

	@Test
	public void toolsPanelTool() {
		app.setMode(EuclidianConstants.MODE_TOOLS_PANEL); // TODO 113
	}

	@Test
	public void mediaPanelTool() {
		app.setMode(EuclidianConstants.MODE_MEDIA_PANEL); // TODO 114
	}

	@Test
	public void videoTool() {
		app.setMode(EuclidianConstants.MODE_VIDEO); // TODO 115
	}

	@Test
	public void audioTool() {
		app.setMode(EuclidianConstants.MODE_AUDIO); // TODO 116
	}

	@Test
	public void geoGebraTool() {
		app.setMode(EuclidianConstants.MODE_GEOGEBRA); // TODO 117
	}

	@Test
	public void cameraTool() {
		app.setMode(EuclidianConstants.MODE_CAMERA); // TODO 118
	}


	private static void click(int x, int y) {
		ec.wrapMousePressed(new TestEvent(x, y));
		ec.wrapMouseReleased(new TestEvent(x, y));
	}

	private static void checkContent(String... desc) {
		int i = 0;
		for (String label : app.getGgbApi().getAllObjectNames()) {
			Assert.assertEquals(desc[i], app.getKernel().lookupLabel(label)
					.toString(StringTemplate.editTemplate));
			i++;
		}
	}
}
