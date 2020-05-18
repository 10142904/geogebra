package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Ignore;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ProductParserTest extends BaseUnitTest {

	@Test
	public void testPiRSquare() {
		add("r = 2");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testABCD() {
		add("a=1");
		add("b=2");
		add("c=2");
		add("d=2");
		shouldReparseAs("abcd", "a b c d");
	}

	@Test
	public void testAvarb() {
		add("a=1");
		add("f(var)=?");
		add("b=2");
		shouldReparseAs("avarb", "a var b");
	}

	@Test
	public void testFunctionalVarVar() {
		add("f(var)=?");
		shouldReparseAs("varvar", "var var");
	}

	@Test
	public void testNFunctionalUV() {
		add("f(u, v)=?");
		shouldReparseAs("uv", "u v");
		shouldReparseAs("vu", "v u");
	}

	@Test
	public void testPir() {
		add("r=2");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testXPlusBs() {
		add("b=1");
		shouldReparseAs("x+bb", "x + b b");
		shouldReparseAs("x+bbb", "x + b b b");
		shouldReparseAs("x+bbbb", "x + b b b b");
		shouldReparseAs("x+bbbbbx", "x + b b b b b x");
	}

	@Test
	public void testABX() {
		shouldReparseAs("xab", "x a b");
		shouldReparseAs("x + ab", "x + a b");
		shouldReparseAs("xxxxxxxxxx", "x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axxxxxxxxxx", "a x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axaxaxaxax", "a x a x a x a x a x");
	}

	@Test
	public void testAkka() {
		add("a=?");
		add("k=?");
		add("aa(x,y)=?");
		shouldReparseAs("kk", "k k");
		shouldReparseAs("kkk", "k k k");
		shouldReparseAs("kkkk", "k k k k");
		shouldReparseAs("akakak", "a k a k a k");
		shouldReparseAs("akka", "a k k a");
		shouldReparseAs("kkaa", "k k a a");
	}

	@Test
	public void testArctanIntegral() {
		shouldReparseAs("21xarctanx", "21x atand(x)");
	}

	@Ignore
	@Test
	public void testCost7() {
		shouldReparseAs("-tcost7t/7", "-t cos 7t 7");
	}

	@Test
	public void testNpi7() {
		shouldReparseAs("npi/7", "n " + Unicode.PI_STRING + " / 7");
	}

	@Test
	public void testLnX() {
		shouldReparseAs("xlnx", "x ln(x)");
		shouldReparseAs("xln2x", "x ln(2x)");
//		shouldReparseAs("xln2xabc", "x ln(2x a b c)");
	}


 	@Test
	public void testC_2Index() {
		shouldReparseAs("c_2e^(7x)", "c_2 " + Unicode.EULER_STRING + "^(7x)");
	}

	@Ignore
	@Test
	public void testsina() {
		shouldReparseAs("sina", "sin(a)");
	}

	@Test
	public void testx4() {
		shouldReparseAs("x4", "x 4");
	}

	@Test
	public void testk4() {
		shouldReparseAs("k4", "k 4");
	}

	@Test
	public void testAkakakaaa() {
		withGeos("a", "k", "aa(x)");
		shouldReparseAs("akakakaaa", "a k a k a k a a a");
	}

	@Test
	public void testImaginaryProduct() {
 		shouldReparseAs("i1", String.valueOf(Unicode.IMAGINARY));
	}

	@Test
	public void testPiSqrt() {
		shouldReparseAs("18pisqrt5", "18" + Unicode.PI_STRING + " sqrt(5)");
	}

	@Test
	public void testiSqrt() {
		shouldReparseAs("isqrt5", Unicode.IMAGINARY + " sqrt(5)");
	}

	private void withGeos(String... geos) {
		for (String string: geos) {
			add(string + "=?");
		}
	}

	@Test
	public void testIndex() {
		shouldReparseAs("B_{0}e^(2)", "B_{0} " + Unicode.EULER_STRING + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testTangent() {
		shouldReparseAs("2xtan8x", "2x tan(8x)");
	}

	@Test
	public void testFcosThetaSum() {
		shouldReparseAs("Fcosθx+Fsinθy", "F cos(x θ) + F sin(y θ)");
	}

	private void shouldReparseAs(String original, String parsed) {
		ParserTest.shouldReparseAs(getApp(), original, parsed);
	}
}
