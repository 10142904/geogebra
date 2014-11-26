package geogebra.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.error.TimeoutException;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.util.LinkedList;
import java.util.List;

import javagiac.context;
import javagiac.gen;
import javagiac.giac;

/**
 * @author michael
 *
 */
public class CASgiacD extends CASgiac implements Evaluate {

	@SuppressWarnings("javadoc")
	AppD app;

	/**
	 * @param casParser
	 *            casParser
	 * @param t
	 *            CasParserTools
	 * @param k
	 *            Kernel
	 */
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

			if (AppD.MAC_OS) {
				// Architecture on OSX seems to be x86_64, but let's make sure
				file = "javagiac";
			} else if ("AMD64".equals(System.getenv("PROCESSOR_ARCHITECTURE"))
			// System.getenv("PROCESSOR_ARCHITECTURE") can return null (seems to
			// happen on linux)
					|| "amd64".equals(System.getProperty("os.arch"))) {
				file = "javagiac64";
			} else {
				file = "javagiac";
			}

			App.debug("Loading Giac dynamic library: " + file);

			// When running from local jars we can load the library files from
			// inside a jar like this
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
	final private static boolean useThread = !AppD.LINUX;

	@SuppressWarnings("deprecation")
	public String evaluate(String input) throws Throwable {

		// don't need to replace Unicode when sending to JNI
		String exp = casParser.replaceIndices(input, false);

		String ret;
		App.debug("giac input: " + exp);

		threadResult = null;
		Thread thread;

		if (app.isApplet() && (!AppD.hasFullPermissions() || !giacLoaded)) {
			App.setCASVersionString("Giac/JS");

			// can't load DLLs in unsigned applet
			// so use JavaScript version instead

			if (!giacSetToGeoGebraMode) {
				app.getApplet().evalJS(wrapJSString(initString));
				giacSetToGeoGebraMode = true;
			}

			// set timeout (in seconds)
			app.getApplet().evalJS(
					wrapJSString("timeout " + (timeoutMillis / 1000)));

			// reset Giac
			app.getApplet().evalJS(wrapJSString(specialFunctions));

			threadResult = app.getApplet().evalJS(wrapJSString(exp));

		} else {
			initialize();

			if (useThread) {
				// send expression to CAS
				thread = new GiacJNIThread(exp);

				thread.start();
				thread.join(timeoutMillis);
				thread.interrupt();
				// thread.interrupt() doesn't seem to stop it, so add this for
				// good measure:
				thread.stop();
				// in fact, stop will do nothing (never implemented)
				App.debug("giac: after interrupt/stop");

				// if we haven't got a result, CAS took too long to return
				// eg Solve[sin(5/4 π+x)-cos(x-3/4 π)=sqrt(6) * cos(x)-sqrt(2)]
				if (threadResult == null) {
					throw new geogebra.common.cas.error.TimeoutException(
							"Thread timeout from Giac");
				}
			} else {
				threadResult = evalRaw(exp);
			}
		}
		ret = postProcess(threadResult);

		App.debug("giac output: " + ret);
		if (ret.contains("user interruption")) {
			throw new geogebra.common.cas.error.TimeoutException(
					"Standard timeout from Giac");
		}

		return ret;
	}

	/**
	 * 
	 * wrap in _ggbCallGiac('...')
	 * 
	 * @param s
	 *            string to wrap
	 * @return wrapped string
	 */
	private static String wrapJSString(String s) {
		StringBuilder sb = new StringBuilder(s.length() + 20);

		// we will wrap string in '', so we need to escape any 's
		String str = s.replace("'", "\\'");

		sb.append("_ggbCallGiac('");
		sb.append(str);
		sb.append("');");

		return sb.toString();
	}

	@Override
	public String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable {
		return evaluate(exp);
	}

	public void initialize() throws Throwable {
		App.debug("giac: initialize");
		if (C == null) {
			C = new context();
			gen g;

			if (!giacSetToGeoGebraMode) {

				evalRaw(initString);
				evalRaw("\"timeout " + (timeoutMillis / 1000) + "\"");

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

	/**
	 * Queue of asynchronous commands that are waiting for update
	 */
	List<AsynchronousCommand> queue = new LinkedList<AsynchronousCommand>();

	private Thread casThread;

	@SuppressWarnings("unused")
	public void evaluateGeoGebraCASAsync(final AsynchronousCommand cmd) {
		App.debug("about to start thread");
		if (!queue.contains(cmd)) {
			queue.add(cmd);
		}

		if (casThread == null || !casThread.isAlive()) {
			casThread = new Thread() {
				@Override
				public void run() {
					App.debug("thread is starting");
					while (queue.size() > 0) {
						AsynchronousCommand command = queue.get(0);
						String input = command.getCasInput();
						String result;
						ValidExpression inVE = null;
						// remove before evaluating to ensure we don't ignore
						// new requests meanwhile
						if (queue.size() > 0)
							queue.remove(0);
						try {
							inVE = casParser.parseGeoGebraCASInput(input);
							// TODO: arbconst()
							result = evaluateGeoGebraCAS(inVE,
									new MyArbitraryConstant(
											(ConstructionElement) command),
									StringTemplate.defaultTemplate,
									app.getKernel());
							CASAsyncFinished(inVE, result, null, command, input);
						} catch (Throwable exception) {
							App.debug("exception handling ...");
							exception.printStackTrace();
							result = "";
							CASAsyncFinished(inVE, result, exception, command,
									input);
						}
					}
					App.debug("thread is quitting");
				}
			};
		}
		if (AsynchronousCommand.USE_ASYNCHRONOUS && !casThread.isAlive()) {
			casThread.start();
		} else {
			casThread.run();
		}
	}

	@Override
	public String evaluateCAS(String exp) {
		try {
			return evaluate(exp);
		} catch (TimeoutException te) {
			throw te;
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
		 * @param exp
		 *            Expression to send to Giac
		 */
		public GiacJNIThread(String exp) {
			this.exp = exp;
		}

		@Override
		public void run() {
			App.debug("thread starting: " + exp);

			try {
				threadResult = evalRaw(exp);

				App.debug("message from thread: " + threadResult);
			} catch (Throwable t) {
				App.debug("problem from JNI Giac: " + t.toString());
				// force error in GeoGebra
				threadResult = "(";
			}
		}
	}

	/**
	 * @param exp0
	 *            String to send to Giac
	 * @return String from Giac
	 */
	String evalRaw(String exp0) {

		String exp = wrapInevalfa(exp0);

		gen g = new gen("caseval(" + exp + ")", C);
		App.debug("giac evalRaw input: " + exp);
		g = g.eval(1, C);
		String ret = g.print(C);
		App.debug("giac evalRaw output: " + ret);

		if (ret != null && ret.startsWith("\"") && ret.endsWith("\"")) {
			ret = ret.substring(1, ret.length() - 1);
		}

		return ret;
	}

	public void clearResult() {
		this.threadResult = null;
	}
}
