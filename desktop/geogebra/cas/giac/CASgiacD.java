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

	@SuppressWarnings("javadoc")
	AppD app;

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

	/**
	 * Giac's context
	 */
	context C;

	// whether to use thread (JNI only)
	final private static boolean useThread = false;

	public String evaluate(String input) throws Throwable {

		// don't need to replace Unicode when sending to JNI
		String exp = casParser.replaceIndices(input, false);

		String ret;
		Object jsRet = null;

		App.debug("giac  input: "+exp);	

		threadResult = null;
		Thread thread;

		if (app.isApplet() && (!AppD.hasFullPermissions() || !giacLoaded)) {
			App.setCASVersionString("Giac/JS");

			// can't load DLLs in unsigned applet
			// so use JavaScript version instead

			if (!giacSetToGeoGebraMode) {
				app.getApplet().evalJS("_ggbCallGiac('" + initString + "');");
				giacSetToGeoGebraMode = true;
			}

			// set timeout (in seconds)
			app.getApplet().evalJS("_ggbCallGiac('timeout " + (timeoutMillis/1000) + "')");

			// reset Giac
			app.getApplet().evalJS("_ggbCallGiac('" + specialFunctions + "');");

			StringBuilder sb = new StringBuilder(exp.length() + 20);
			sb.append("_ggbCallGiac('");
			sb.append(exp);
			sb.append("');");

			threadResult = app.getApplet().evalJS(sb.toString());        

		} else {
			initialize();


			if (useThread) {
				// send expression to CAS
				thread = new GiacJNIThread(exp);


				thread.start();
				long startTime = System.currentTimeMillis();

				int wait = 1;

				// wait for result from thread
				while (threadResult == null && System.currentTimeMillis() < startTime + timeoutMillis) {
					Thread.sleep(wait);
					wait = wait * 2;
					//App.debug(System.currentTimeMillis() + " "+ (startTime + timeoutMillis));
				}

				//App.debug("took: "+(System.currentTimeMillis() - startTime)+"ms");

				thread.interrupt();
				// thread.interrupt() doesn't seem to stop it, so add this for good measure:
				thread.stop();

				// if we haven't got a result, CAS took too long to return
				// eg Solve[sin(5/4 π+x)-cos(x-3/4 π)=sqrt(6) * cos(x)-sqrt(2)]
				if (threadResult == null) {
					throw new geogebra.common.cas.error.TimeoutException("Timeout from Giac");
				}
			} else {
				gen g = new gen(exp, C);
				g = giac._eval(g, C);
				threadResult = g.print(C);

			}
		}


		ret = postProcess(threadResult);

		App.debug("giac output: " + ret);		

		return ret;
	}

	@Override
	public String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		if (C == null) {
			C = new context();
			gen g;

			if (!giacSetToGeoGebraMode) {

				g = new gen(initString, C);
				g = giac._eval(g, C);
				App.debug(g.print(C));


				giacSetToGeoGebraMode = true;
			}

			g = new gen(specialFunctions, C);
			g = giac._eval(g, C);
			App.debug(g.print(C));


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


	/**
	 * store result from Thread here
	 */
	String threadResult;

	/**
	 * @author michael
	 *
	 */
	class GiacJNIThread extends Thread {
		private String exp;
		/**
		 * @param exp Expression to send to Giac
		 */
		public GiacJNIThread(String exp) {
			this.exp = exp;
		}
		@Override
		public void run() {
			App.debug("thread starting: " + exp);

			try {
				gen g = new gen(exp, C);
				g = giac._eval(g, C);
				threadResult = g.print(C);
				App.debug("message from thread: " + threadResult);
			} catch (Throwable t) {
				App.debug("problem from JNI Giac: "+t.toString());
				// force error in GeoGebra
				threadResult = "(";
			}
		}
	}


}
