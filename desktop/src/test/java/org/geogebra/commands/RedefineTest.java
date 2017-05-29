package org.geogebra.commands;

import java.util.Locale;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedefineTest extends Assert {
	static AppDNoGui app;
	static AlgebraProcessor ap;

	@Before
	public void resetSyntaxes() {
		app.getKernel().clearConstruction(true);
	}

	@BeforeClass
	public static void setupApp() {
		app = new AppDNoGui(new LocalizationD(3), false);
		app.setLanguage(Locale.US);
		ap = app.getKernel().getAlgebraProcessor();
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
	}
	private static void t(String input, String expected) {
		CommandsTest.testSyntax(input, new String[] { expected }, app, ap,
				StringTemplate.xmlTemplate);
	}

	public static void t(String s, String[] expected) {
		CommandsTest.testSyntax(s, expected, app, ap,
				StringTemplate.xmlTemplate);
	}

	public void checkError(String s, String msg) {
		ErrorAccumulator errorStore = new ErrorAccumulator();

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(s, false, errorStore,
						false, null);

		assertTrue(msg.equals(errorStore.getErrors()));

	}

	@Test
	public void breakingTypeChangeShouldRaiseException() {
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		t("a", "1"); // polygon side
		app.getKernel().setUndoActive(true);
		app.getKernel().initUndoInfo();
		app.storeUndoInfo();
		checkError("A(x)=x", "Redefinition failed");
		t("A", "(1, 1)");
		t("poly1", "1");
		t("a", "1");
	}

	@Test
	public void curlyBracketsShouldNotAffectRedefine() {
		t("r=1", "1");
		t("r_2=2*r", "2");
		t("r_3=3*r_2", "6");
		t("r_{2}=3*r", "3");

		t("a=7", "7");
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		Kernel kernel = app.getKernel();
		assertEquals("a_1: Segment[A, B, poly1]",
				kernel.lookupLabel("a_1").getDefinitionForEditor());
		t("a_{1}: Segment[A, B, poly1]", new String[0]);
		kernel.getAlgebraProcessor().changeGeoElement(kernel.lookupLabel("a_1"),
				"a_{1}: Segment[A, B, poly1]", true, true,
				new TestErrorHandler(), null);
	}
}
