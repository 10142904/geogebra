package org.geogebra.io;
import java.util.Locale;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Test;

import com.google.gwt.regexp.shared.RegExp;
public class SerializationTest {
	
	@Test
	public void testSerializationSpeed(){
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), true);
		app.setLanguage(Locale.US);
		long l = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(1000);
		FunctionVariable fv = new FunctionVariable(app.getKernel());
		ExpressionNode n = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv);
		for(int i = 0;i<100000;i++){
			sb.append(n.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
		
		l = System.currentTimeMillis();
		StringBuilder sbm = new StringBuilder(1000);
		ExpressionNode nm = fv.wrap().subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv);
		for(int i = 0;i<100000;i++){
			sbm.append(nm.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
	}
	
	@Test
	public void testCannonicNumber(){
		Assert.assertEquals("0", StringUtil.cannonicNumber("0.0"));
		Assert.assertEquals("0", StringUtil.cannonicNumber(".0"));
		Assert.assertEquals("1.0E2", StringUtil.cannonicNumber("1.0E2"));
		Assert.assertEquals("1", StringUtil.cannonicNumber("1.00"));
	}

	@Test
	public void testInequality() {
		String[] testI = new String[] { "(x>=3) && (7>=x) && (10>=x)" };
		String[] test = new String[] { "aaa", "(a)+b", "3", "((a)+(b))+7" };
		String[] testFalse = new String[] { "3(", "(((7)))" };
		for (String t : test) {
			Assert.assertTrue(RegExp.compile("^" + CASgiac.expression + "$")
					.test(t));

		}
		for (String t : testFalse) {
			Assert.assertFalse(RegExp.compile("^" + CASgiac.expression + "$")
					.test(t));

		}
		for (String t : testI) {
			Assert.assertTrue(CASgiac.inequality.test(t));
		}
	}

}
