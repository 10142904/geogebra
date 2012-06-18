package geogebra.common.kernel.cas;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * Interface for CAS parser
 */
public interface CASParserInterface {

	ValidExpression parseGeoGebraCASInputAndResolveDummyVars(String inValue);

	ValidExpression parseGeoGebraCASInput(String result);

	/**
	 * Replace variables with dummy objects
	 * @param outputVe value to process
	 */
	void resolveVariablesForCAS(ExpressionValue outputVe);

	String getTranslatedCASCommand(String string);

}
