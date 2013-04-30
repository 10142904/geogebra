package geogebra.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.main.AppD;
import javagiac.context;
import javagiac.gen;
import javagiac.giac;
import netscape.javascript.JSObject;

public class CASgiacD extends CASgiac implements Evaluate {

	private AppD app;

	public CASgiacD(CASparser casParser, CasParserTools t, Kernel k) {
		super(casParser);

		this.app = (AppD) k.getApplication();

		App.setCASVersionString("Giac");
	}

	private static boolean giacLoaded = false;

	static {
		try {
			App.debug("Loading Giac dynamic library");
			String file;
			if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))) {
				file = "javagiac64";
			} else {
				file = "javagiac";
			}

			// "classic" method
			//System.loadLibrary(file);
			//giacLoaded = true;

			// load native libraries from a jar file
			MyClassPathLoader loader = new MyClassPathLoader();
			giacLoaded = loader.loadLibrary(file, false);
			//JNILibLoaderBase.setLoadingAction(loader);
			//NativeLibrary.disableLoading();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (giacLoaded) {
			App.debug("Giac dynamic library loaded");
		} else {
			App.debug("Failed to load Giac dynamic library");
		}
	}

	private context C;

	public String evaluate(String exp) throws Throwable {

		String ret;
		Object jsRet = null;

		App.debug("giac  input: "+exp);		

		if (app.isApplet() && (!AppD.hasFullPermissions() || !giacLoaded)) {
			// can't load DLLs in unsigned applet
			// so use JavaScript version instead

			JSObject window = JSObject.getWindow(app.getApplet().applet);

			// JavaScript command to send
			StringBuilder sb = new StringBuilder(exp.length() + 20);
			sb.append("_ggbCallGiac('");
			sb.append(exp);
			sb.append("');");

			// get an array from JavaScript and retrieve its contents
			JSObject JSarray = (JSObject) window.eval(sb.toString());
			if (JSarray != null) {
				jsRet = JSarray.getSlot(0);
			}

			if (jsRet instanceof String) {
				ret = (String) jsRet;
			} else {
				ret = "?";
				String type = (jsRet == null) ? "*null*" : jsRet.getClass()+"";
				App.debug("wrong type returned from JS: " + type);
			}

		} else {
			initialize();

			gen g = new gen(exp, C);
			g = giac._eval(g, C);
			ret = g.print(C);
		}

		App.debug("giac output: " + ret);		

		return ret;
	}

	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		if (C == null) {
			C = new context();
		}

	}

	public void initCAS() {
		App.error("unimplemented");

	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
		App.error("unimplemented");

	}

	@Override
	public String evaluateCAS(String exp) {
		try {
			return evaluate(exp);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}


}
