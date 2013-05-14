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

public class CASgiacD extends CASgiac implements Evaluate {

	private AppD app;

	public CASgiacD(CASparser casParser, CasParserTools t, Kernel k) {
		super(casParser);

		this.app = (AppD) k.getApplication();

		this.parserTools = t;

	}

	private static boolean giacLoaded = false;

	static {
		try {
			App.debug("Loading Giac dynamic library");

			String file;

			// System.getenv("PROCESSOR_ARCHITECTURE") can return null (seems to happen on linux)

			if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))
					|| "amd64".equals(System.getProperty("os.arch"))) {
				file = "javagiac64";
			} else {
				file = "javagiac";
			}


			// When running from local jars we can load the library files from inside a jar like this 
			MyClassPathLoader loader = new MyClassPathLoader();
			giacLoaded = loader.loadLibrary(file);


			if (!giacLoaded) {
				// "classic" method
				// for Webstart, eg loading 
				// javagiac.dll from javagiac-win32.jar
				// javagiac64.dll from javagiac-win64.jar
				// libjavagiac.so from javagiac-linux32.jar
				// libjavagiac64.so from javagiac-linux64.jar
				// libjavagiac.jnilib from javagiac-mac.jar

				App.debug("Trying to load Giac library (alternative method)");
				System.loadLibrary(file);
				giacLoaded = true;



			}

		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (giacLoaded) {
			App.debug("Giac dynamic library loaded");
			App.setCASVersionString("Giac/JNI");
		} else {
			App.debug("Failed to load Giac dynamic library");
			App.setCASVersionString("Giac");
		}
	}

	private context C;

	public String evaluate(String input) throws Throwable {

		// don't need to replace Unicode when sending to JNI
		String exp = casParser.replaceIndices(input, false);

		String ret;
		Object jsRet = null;

		App.debug("giac  input: "+exp);		

		if (app.isApplet() && (!AppD.hasFullPermissions() || !giacLoaded)) {
			App.setCASVersionString("Giac/JS");

			// can't load DLLs in unsigned applet
			// so use JavaScript version instead

			if (!specialFunctionsInitialized) {
				app.getApplet().evalJS("_ggbCallGiac('" + initString + "');");
				app.getApplet().evalJS("_ggbCallGiac('" + specialFunctions + "');");
				specialFunctionsInitialized = true;
			}


			// JavaScript command to send
			StringBuilder sb = new StringBuilder(exp.length() + 20);
			sb.append("_ggbCallGiac('");
			sb.append(exp);
			sb.append("');");

			jsRet = app.getApplet().evalJS(sb.toString());

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

		if (ret.trim().startsWith("\"")) {
			// eg "Index outside range : 5, vector size is 3, syntax compatibility mode xcas Error: Invalid dimension"
			// assume error
			App.debug("message from giac (assuming error) "+ret);
			// force error? TODO: Needs testing
			return "(";
		}


		if (ret.indexOf("c_") > -1) {
			App.debug("replacing arbitrary constants in "+ret);
			ret = ret.replaceAll("c_([0-9])*", "arbconst($1)");
		}

		if (ret.indexOf("n_") > -1) {
			App.debug("replacing arbitrary integers in "+ret);
			ret = ret.replaceAll("n_([0-9])*", "arbint($1)");
		}

		App.debug("giac output: " + ret);		

		// convert Giac's scientific notation from e.g. 3.24e-4 to
		// 3.2E-4
		ret = parserTools.convertScientificFloatNotation(ret);

		ret = casParser.insertSpecialChars(ret); // undo special character handling

		return ret;
	}

	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		if (C == null) {
			C = new context();

			if (!specialFunctionsInitialized) {

				gen g = new gen(initString, C);
				g = giac._eval(g, C);
				App.debug(g.print(C));

				g = new gen(specialFunctions, C);
				g = giac._eval(g, C);
				App.debug(g.print(C));

				specialFunctionsInitialized = true;
			}

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
