package geogebra.html5.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

/**
 * Web implementation of Giac CAS
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac implements geogebra.common.cas.Evaluate {
	
	private static boolean asyncstarted = false;
	private Kernel kernel;
	private Evaluate giac;
	
	/**
	 * Creates new CAS
	 * @param casParser parser
	 * @param parserTools scientific notation convertor
	 * @param kernel kernel
	 */
	public CASgiacW(CASparser casParser, CasParserTools parserTools, Kernel kernel) {
		super(casParser);
		this.parserTools = parserTools;
		this.kernel = kernel;

	
		App.setCASVersionString("Giac/JS");
}
	
	@Override
	public String evaluateCAS(String exp) {
		try {
			// replace Unicode when sending to JavaScript
			// (encoding problem)
			String processedExp = casParser.replaceIndices(exp, true);
			String ret = evaluateRaw(processedExp);

			return postProcess(ret);
			
		//} catch (TimeoutException toe) {
		//	throw new Error(toe.getMessage());
		} catch (Throwable e) {
			App.debug("evaluateGiac: " + e.getMessage());
			return "?";
		}
	}

	/*
	 * called from JavaScript when the CAS is loaded
	 * (non-Javadoc)
	 * @see geogebra.common.kernel.cas.CASGenericInterface#initCAS()
	 */
	public void initCAS() {
		// not called?
	}
	
	public synchronized String evaluate(String s) {

		if (!giacSetToGeoGebraMode) {
			nativeEvaluateRaw(initString, true);
			giacSetToGeoGebraMode = true;
		}
		nativeEvaluateRaw(specialFunctions, false);
		
		App.debug("giac  input:"+s);
		String ret = nativeEvaluateRaw(s, true);
		App.debug("giac output:"+ret);
		
		return ret;
	}

	private native String nativeEvaluateRaw(String s, boolean showOutput) /*-{
		
		if (showOutput) {
			$wnd.console.log("js giac  input:"+s);
		}
		
		caseval = $wnd.Module.cwrap('_ZN4giac7casevalEPKc', 'string', ['string']);  
		
		var ret = caseval(s);
		
		if (showOutput) {
			$wnd.console.log("js giac output:"+ret);
		}
		
		return ret
	}-*/;

	public void initialize() {
		// not called?
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public String evaluate(String exp, long timeoutMilliseconds) {
	    return evaluate(exp);
    }
	

}
