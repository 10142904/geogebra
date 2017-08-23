package org.geogebra.io.latex;

import java.text.Normalizer;

import org.geogebra.common.io.latex.BracketsAdapter;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.io.latex.TeXAtomSerializer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.FactoryProvider;


public class SerializeLaTeX {
	static Parser parser;
	private static GeoGebraSerializer serializer;

	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderDesktop());
		}
		MetaModel m = new MetaModel();
		parser = new Parser(m);
		serializer = new GeoGebraSerializer();
	}

	@Test
	public void testAtoms() {
		checkCannon("a", "a");
	}

	@Test
	public void testExpr() {
		checkCannon("1 * 2", "1*2");
		checkCannon("1 == 2", "1==2");
		checkCannon("1 " + Unicode.PARALLEL + " 2",
				"1" + Unicode.PARALLEL + "2");
		checkCannon("1 = 2", "1=2");
		checkCannon("(1 * 2)", "(1*2)");

	}

	@Test
	public void testSqrt() {
		checkCannon("sqrt(x + 1)", "sqrt(x+1)");
		checkCannon("x sqrt(x + 1)", "x sqrt(x+1)");
		checkCannon("f(x) = sqrt(x)", "f(x)= sqrt(x)");
		checkCannon("nroot(x + 1,3)", "nroot(x+1,3)");
		checkCannon("f(x) = nroot(x,3)", "f(x)= nroot(x,3)");

	}

	@Test
	public void testDiv() {
		checkCannon("1/2", "(1)/(2)");
		checkCannon("1/2+3", "(1)/(2)+3");
		checkCannon("1/ ( 2)", "(1)/(2)");
		checkCannon("1/ ( 2+3)", "(1)/(2+3)");
		checkCannon("1/ ((2+3)+4)", "(1)/((2+3)+4)");
		checkCannon("1/(2/3)", "(1)/((2)/(3))");
		checkCannon("x^2/ 3", "(x^(2))/(3)");
		checkCannon("x^2 / 2", "(x^(2))/(2)");
		checkCannon("2/cos(x)", "(2)/(cos(x))");
	}

	@Test
	public void testExponent() {
		checkCannon("exp(-30)", "exp(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30", Unicode.EULER_STRING
				+ "^(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30+1", Unicode.EULER_STRING
				+ "^(-30)+1");
		checkCannon(Unicode.EULER_STRING + Unicode.SUPERSCRIPT_MINUS
				+ Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0,
				Unicode.EULER_STRING + "^(-10)");

	}

	@Test
	public void testFloorCeil() {
		checkCannon("floor(x)", "floor(x)");
		checkCannon("ceil(x)", "ceil(x)");
		checkCannon(Unicode.LFLOOR + "x" + Unicode.RFLOOR, "floor(x)");
		checkCannon(Unicode.LCEIL + "x" + Unicode.RCEIL, "ceil(x)");

	}

	@Test
	public void testPower() {
		checkCannon("x ^ 2", "x^(2)");
		checkCannon("x ^ 2 + 1", "x^(2)+1");
		checkCannon("x" + Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_3,
				"x^(23)");
		checkCannon("x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "x^(-23)");
		checkCannon("1 + x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "1+x^(-23)");
		checkCannon("e^x*sin(x)", "e^(x)*sin(x)");
		checkCannon("e^(-10/x)*sin(x)", "e^(-(10)/(x))*sin(x)");

	}

	@Test
	public void testSubscript() {
		checkCannon("x_2", "x_{2}");
		checkCannon("x_2 = 7", "x_{2}=7");
		checkCannon("x_2 t", "x_{2}*t");
		checkCannon("x_2 sin(x)", "x_{2}*sin(x)");
		checkCannon("f_2(x)", "f_{2}(x)");
		checkCannon("f_2 (x)", "f_{2} (x)");

	}

	@Test
	public void testPoint() {
		checkCannon("(1, 2)", "(1,2)");
		checkCannon("(1; 2)", "(1;2)");
		checkCannon("(1, 2, 3)", "(1,2,3)");
		checkCannon("(1; 2; 3)", "(1;2;3)");
	}

	@Test
	public void testMultiply() {
		checkCannon("t (1,2)", "t (1,2)");
		checkCannon("x x x", "x*x*x");
	}

	@Test
	public void testCommand() {
		checkCannon("turtle1=Turtle[]", "turtle1=Turtle[]");
		checkCannon("Turtle[]", "Turtle[]");
		checkCannon("Turtle[1*3,7]", "Turtle[1*3,7]");
	}

	@Test
	public void testMatrix() {
 		checkCannon("{{1,2},{3,4}}", "{{1,2},{3,4}}");
		checkCannon("{{1 , 2} , { 3 , 4}}", "{{1,2},{3,4}}");
		checkCannon("{{1 , 2} , 3}", "{{1,2},3}");
		checkCannon("{{1,2},{3,4}}+1", "{{1,2},{3,4}}+1");
		checkCannon("{7,{{1,2},{3,4}}+2,4,5,6}", "{7,{{1,2},{3,4}}+2,4,5,6}");
	}

	@Test
	public void testList() {
		checkCannon("{x,1}", "{x,1}");
		checkCannon("{x, 1}", "{x,1}");
		checkCannon("{x ,1}", "{x,1}");
		checkCannon("{x , 1}", "{x,1}");

	}

	@Test
	public void testComma() {
		checkCannon("If[x<1/x,x/2,sqrt(x/2)]",
				"If[x<(1)/(x),(x)/(2),sqrt((x)/(2))]");
		checkCannon("(1;sqrt(2))", "(1;sqrt(2))");
		checkCannon("(t^n;t)", "(t^(n);t)");
	}

	@Test
	public void testParseLaTeX() {
		checkLaTeX("\\frac{x+y}{x-y}", "(x+y)/(x-y)");
		checkLaTeX("\\sqrt{x+y}", "sqrt(x+y)");
		checkLaTeX("\\sqrt{x}+2", "sqrt(x)+2");
		checkLaTeX("1-\\sqrt[3]{x}", "1-nroot(x,3)");
		checkLaTeX("X=\\left(x_0+2x_x,y_0+2x_y\\right)",
				"X=(x_0+2x_x,y_0+2x_y)");
		checkLaTeX("i=\\left[0,\\frac{6\\pi}{p}...24\\pi\\right]",
				"i=[0,(6pi)/(p)...24pi]");
		checkLaTeX(
				"\\left(\\left(1-t\\right)\\left(x_1\\right)+t\\left(x_1+R\\ f\\left(j\\right)\\right),\\left(1-t\\right)\\left(y_1\\right)+t\\left(y_1+Rg\\left(j\\right)\\right)\\right)",
				"((1-t)(x_1)+t(x_1+R f(j)),(1-t)(y_1)+t(y_1+Rg(j)))");
		checkLaTeX("\\frac{x^2}{m^2}+\\frac{y^2}{n^2}\\ge2",
				"(x^(2))/(m^(2))+(y^(2))/(n^(2))>=2");
		checkLaTeX("a\\leq b", "a<=b");
		checkLaTeX("f\\left(x\\right)=\\sin\\left(x\\right)", "f(x)=sin(x)");
		checkLaTeX("r\\ =\\ g^{\\theta}",
				"r = g^(" + Unicode.theta_STRING + ")");
		checkLaTeX("7\\cdot 6", "7*6");
		checkLaTeX("7\\times 6", "7*6");
	}

	@Test
	public void testParseLaTeXAdapter() {
		checkLaTeX("a=\\left[1,...,4\\right]", "a=(1...4)",
				new BracketsAdapter());
		checkLaTeX("a=\\left[0.8,1.2,...,4\\right]",
				"a=Sequence[0.8,4,1.2-(0.8)]", new BracketsAdapter());
	}

	@Test
	public void testBinaryOp() {
		for (char op : new char[] { Unicode.LESS_EQUAL,
				Unicode.GREATER_EQUAL, Unicode.IS_SUBSET_OF,
				Unicode.IS_ELEMENT_OF, Unicode.IS_SUBSET_OF_STRICT }) {
			checkCannon("5 " + op + " 3", "5" + op + "3");
			checkCannon("5 " + op + " (2/3*x+5/3)",
					"5" + op + " ((2)/(3)*x+(5)/(3))");
		}
	}

	@Test
	public void testKorean() {
		testEditor("\u314D\u3157\u314E", "\uD407");
		testEditor("\u3141\u3163\u3142\u315C\u3134",
				"\uBBF8\uBD84");
		testEditor("\u3145\u3145", "\u110A");
		testEditor("\u3147\u315C\u3163", "\uC704");

		testEditor("\u3131\u314F\u3142\u3145", "\uAC12");

		testEditor("\u314E\u314F\u3134\u3145\u3145\u314F\u3147",
				"\uD55C\uC30D");
		
		// tricky
		// tail doubled char needs to be undoubled and split across 2 chars
		testEditor("\u314E\u314F\u3145\u3145\u314F\u3147", "\uD56B\uC0C1");

		// small steps
		testEditor("\u314E\u314F", "\uD558");
		testEditor("\u314E\u314F\u3145", "\uD56B");
		testEditor("\u314E\u314F\u3145\u3145", "\uD56C");
		testEditor("\u314E\u314F\u3145\u3145\u314F", "\uD56B\uC0AC");

		testEditor("\u3131\u3161", "\uADF8");
		testEditor("\u3131\u3161\u3131", "\uADF9");
		testEditor("\u3131\u3161\u3131\u3131", "\uADFA");
		testEditor("\u3131\u3161\u3131\u3131\u314F", "\uADF9\uAC00");
		testEditor("\u3131\u3161\u3131\u3131\u314F\u3142", "\uADF9\uAC11");
		testEditor("\u3131\u3161\u3131\u3131\u314F\u3142\u3145",
				"\uADF9\uAC12");

		testEditor("\u314E\u314F\u3134\u3146\u314F\u3147", "\uD55C\uC30D");

		// type with explicit double
		// strictly should probably be \uD558\uC30D
		testEditor("\u314E\u314F\u3146\u314F\u3147", "\uD56B\uC0C1");
		
		testEditor("\u3134\u3153\u3139\u3142\u3147\u3163", "\uB113\uC774");
		testEditor("\u3147\u314F\u3134\u314E\u3137\u314F", "\uC54A\uB2E4");
		testEditor("\u3131\u314F\u3142\u3145\u3147\u3161\u3134", "\uAC12\uC740");

		testEditor("\u3131\u314F\u3142\u3145\u3145\u314F\u3134",
				"\uAC12\uC0B0");

		testEditor(Korean.flattenKorean("\uB098"), "\uB098");
		testEditor(Korean.flattenKorean("\uB108"), "\uB108");
		testEditor(Korean.flattenKorean("\uC6B0\uB9AC"),
				"\uC6B0\uB9AC");
		testEditor(Korean.flattenKorean("\uBBF8\uBD84"),
				"\uBBF8\uBD84");
		testEditor(Korean.flattenKorean("\uBCA1\uD130"),
				"\uBCA1\uD130");
		testEditor(Korean.flattenKorean("\uC0C1\uC218"),
				"\uC0C1\uC218");
		testEditor(Korean.flattenKorean("\uB2ED\uBA39\uC5B4"),
				"\uB2ED\uBA39\uC5B4");
		testEditor(Korean.flattenKorean("\uC6EC\uC77C"),
				"\uC6EC\uC77C");
		testEditor(Korean.flattenKorean("\uC801\uBD84"),
				"\uC801\uBD84");
		testEditor(Korean.flattenKorean("\uC288\uD37C\uB9E8"),
				"\uC288\uD37C\uB9E8");
		testEditor(Korean.flattenKorean("\u3137\u3137"),
				"\u1104");
		testEditor(Korean.flattenKorean("\uC778\uD14C\uADF8\uB784"),
				"\uC778\uD14C\uADF8\uB784");
		testEditor(Korean.flattenKorean("\u3137"), "\u1103");
		testEditor(Korean.flattenKorean("\u3131"), "\u1100");
		testEditor(Korean.flattenKorean("\u3134"), "\u1102");
		testEditor(Korean.flattenKorean("\uC8FC\uC778\uC7A5"),
				"\uC8FC\uC778\uC7A5");
		testEditor(Korean.flattenKorean("\uC774\uC81C\uC880\uC790\uC790"),
				"\uC774\uC81C\uC880\uC790\uC790");
		testEditor(Korean.flattenKorean("\uC544\uBAA8\uB974\uACA0\uB2E4"),
				"\uC544\uBAA8\uB974\uACA0\uB2E4");
		
		testEditor("\u3145\u3145\u1161\u11BC", "\uC30D");
		testEditor("\u110A\u1161\u11BC", "\uC30D");
		
		
		testEditor("\u3142\u315C", "\uBD80");
		testEditor("\u3142\u315C\u3154", "\uBDB8");
		testEditor("\u3142\u315C\u3154\u3139", "\uBDC0");
		testEditor("\u3142\u315C\u3154\u3139\u3131","\uBDC1");
		
		testEditor("\u3131\u3131", "\u1101");
		testEditor("\u3131\u3131\u314F", "\uAE4C");

	}

	@Test
	public void testKoreanNormalization() {
		
		testKorean("\uD4DB");

		// Hangul syllables range
		// https://en.wikipedia.org/wiki/Hangul_Syllables
		for (char ch = '\uac00'; ch < '\ud7a3'; ch++) {
			testKorean(ch + "");
		}

		testKorean2(Korean.flattenKorean(
				"\uD56D\uC131\uC740 \uD56D\uC0C1 \uD63C\uC790 \uC788\uB294 \uAC83\uC774 \uC544\uB2C8\uB77C, \uB450 \uAC1C \uC774\uC0C1\uC758"));

		for (char lead = '\u1100'; lead <= '\u1112'; lead++) {
			for (char vowel = '\u1161'; vowel <= '\u1175'; vowel++) {
				for (char tail = '\u11a8'; tail <= '\u11c2'; tail++) {
					// System.err.println(lead + " " + vowel + " " + tail);
					testKorean2(lead + "" + vowel + "" + tail);
				}

			}

		}

	}

	private void testKorean2(String s) {

		String s1 = Normalizer.normalize(s, Normalizer.Form.NFKC);
		String s2 = Korean.unflattenKorean(s).toString();

		Assert.assertEquals(s1, s2);
	}

	private void testKorean(String s) {
		Assert.assertEquals(Normalizer.normalize(s, Normalizer.Form.NFD),
				Korean.flattenKorean(s));

	}

	@Test
	public void testEditor() {
		testEditor("sqrt(x/2)",
				"sqrt(x/2)");
		testEditor("1+2+3-4", "1+2+3-4");
		testEditor("12345", "12345");
		testEditor("1/2/3/4", "1/2/3/4");
		testEditor("Segment[(1,2),(3,4)]", "Segment[(1,2),(3,4)]");

		// typing second | starts another abs() clause
		testEditor("3|x", "3*abs(x)");
		testEditor("3 |x", "3 *abs(x)");
		testEditor("3*|x", "3*abs(x)");
		testEditor("x|xx", "x*abs(xx)");
		testEditor("x |x x", "x *abs(x x)");
		testEditor("x*|x*x", "x*abs(x*x)");
		testEditor("x sqrt(x)", "x sqrt(x)");
		testEditor("x" + Unicode.SQUARE_ROOT + "x+1", "x*sqrt(x+1)");

	}

	public void testEditor(String input, String output) {
		final MathFieldD mathField = new MathFieldD();

		MathFieldInternal mathFieldInternal = mathField.getInternal();
		InputController inputController = mathFieldInternal
				.getInputController();
		EditorState editorState = mathFieldInternal.getEditorState();

		mathField.insertString(input);

		Assert.assertEquals(output,
				GeoGebraSerializer.serialize(editorState.getRootComponent()));

	}

	private void checkLaTeX(String string, String string2) {
		checkLaTeX(string, string2, null);

	}

	private void checkLaTeX(String string, String string2, BracketsAdapter ad) {
		TeXFormula tf = new TeXFormula();
		TeXParser tp = new TeXParser(string, tf);
		tp.parse();
		Assert.assertEquals(string2,
				new TeXAtomSerializer(ad).serialize(tf.root));
	}

	private static void checkCannon(String input, String output) {
		MathFormula mf = null;
		try {
			mf = parser.parse(input);
			checkLaTeXRender(mf);
		} catch (ParseException e) {
			Assert.assertNull(e);
		}
		Assert.assertNotNull(mf);
		Assert.assertEquals(mf.getRootComponent() + "", output,
				serializer.serialize(mf));
		try {
			mf = parser.parse(output);
			checkLaTeXRender(mf);
		} catch (ParseException e) {
			Assert.assertNull(e);
		}
		
	}

	private static void checkLaTeXRender(MathFormula mf) {
		String tex = TeXSerializer.serialize(mf.getRootComponent(),
				new MetaModel());
		TeXFormula tf = new TeXFormula();
		TeXParser tp = new TeXParser(tex, tf);
		tp.parse();

	}

}
