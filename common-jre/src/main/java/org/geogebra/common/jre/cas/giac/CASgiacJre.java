package org.geogebra.common.jre.cas.giac;

import java.util.LinkedList;
import java.util.List;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.error.TimeoutException;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import javagiac.context;
import javagiac.gen;
import javagiac.giac;

public abstract class CASgiacJre extends CASgiac {

	/**
	 * application
	 */
	protected App app;

	/**
	 * @param casParser
	 *            casParser
	 * @param k
	 *            Kernel
	 */
	public CASgiacJre(CASparser casParser, Kernel k) {
		super(casParser);

		this.app = k.getApplication();


	}

	/**
	 * store result from Thread here
	 */
	String threadResult;

	@Override
	final public void clearResult() {
		this.threadResult = null;
	}

	/**
	 * @param exp0
	 *            String to send to Giac
	 * @param timeoutMilliseconds
	 *            timeout in milliseconds
	 * @return String from Giac
	 */
	final String evalRaw(String exp0, long timeoutMilliseconds) {

		// #5439
		// reset Giac before each call
		init(exp0, timeoutMilliseconds);

		String exp = wrapInevalfa(exp0);

		gen g = new gen("caseval(" + exp + ")", C);
		Log.debug("giac evalRaw input: " + StringUtil.toJavaString(exp));
		g = g.eval(1, C);
		String ret = g.print(C);
		Log.debug("giac evalRaw output: " + ret);

		if (ret != null && ret.startsWith("\"") && ret.endsWith("\"")) {
			ret = ret.substring(1, ret.length() - 1);
		}

		return ret;
	}

	private void init(String exp, long timeoutMilliseconds) {
		gen g = new gen(initString, C);
		g.eval(1, C);

		InitFunctions[] init = InitFunctions.values();

		// Log.debug("exp = " + exp);

		for (int i = 0; i < init.length; i++) {
			InitFunctions function = init[i];

			// send only necessary init commands
			if (function.functionName == null
					|| exp.indexOf(function.functionName) > -1) {
				g = new gen(function.definitionString, C);
				giac._eval(g, C);
				// Log.debug("sending " + function);
			} else {
				// Log.error("not sending " + function + " "
				// + function.functionName);
			}

			// Log.error(function.functionName + " " +
			// function.definitionString);
		}

		g = new gen("\"timeout " + (timeoutMilliseconds / 1000) + "\"", C);
		giac._eval(g, C);

		// make sure we don't always get the same value!
		int seed = rand.nextInt(Integer.MAX_VALUE);
		g = new gen("srand(" + seed + ")", C);
		giac._eval(g, C);

	}

	/**
	 * Giac's context
	 */
	context C;

	/**
	 * create context
	 */
	final protected void createContext() {
		try {
			C = new context();
		} catch (UnsatisfiedLinkError e) {
			Log.error("CAS not available: " + e.getMessage());
		}
	}

	@Override
	public String evaluateCAS(String input) {

		// don't need to replace Unicode when sending to JNI
		String exp = casParser.replaceIndices(input, false);

		try {
			return evaluate(exp, timeoutMillis);
		} catch (TimeoutException te) {
			throw te;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @return whether to use thread (JNI only)
	 */
	abstract protected boolean useThread();

	@Override
	protected String evaluate(String exp, long timeoutMillis0)
			throws Throwable {

		String ret;
		// Log.debug("giac input: " + exp);

		threadResult = null;
		Thread thread;

		if (useThread()) {
			// send expression to CAS
			thread = new GiacJNIThread(exp);

			thread.start();
			thread.join(timeoutMillis0);
			thread.interrupt();
			// thread.interrupt() doesn't seem to stop it, so add this for
			// good measure:
			stopThread(thread);
			// in fact, stop will do nothing (never implemented)
			// Log.debug("giac: after interrupt/stop");

			// if we haven't got a result, CAS took too long to return
			// eg Solve[sin(5/4 pi+x)-cos(x-3/4 pi)=sqrt(6) *
			// cos(x)-sqrt(2)]
			if (threadResult == null) {
				Log.debug("Thread timeout from Giac");
				throw new TimeoutException("Thread timeout from Giac");
			}
		} else {
			threadResult = evalRaw(exp, timeoutMillis0);
		}

		ret = postProcess(threadResult);

		// Log.debug("giac output: " + ret);
		if (ret.contains("user interruption")) {
			Log.debug("Standard timeout from Giac");
			throw new TimeoutException("Standard timeout from Giac");
		}

		return ret;
	}

	/**
	 * stop thread. TODO remove it?
	 * 
	 * @param thread
	 *            thread
	 */
	abstract protected void stopThread(Thread thread);

	/**
	 * Queue of asynchronous commands that are waiting for update
	 */
	List<AsynchronousCommand> queue = new LinkedList<AsynchronousCommand>();

	private Thread casThread;

	@Override
	@SuppressWarnings("unused")
	public void evaluateGeoGebraCASAsync(final AsynchronousCommand cmd) {
		Log.debug("about to start thread");
		if (!queue.contains(cmd)) {
			queue.add(cmd);
		}
		final GeoCasCell cell = null;
		if (casThread == null || !casThread.isAlive()) {
			casThread = new Thread() {
				@Override
				public void run() {
					Log.debug("thread is starting");
					while (queue.size() > 0) {
						AsynchronousCommand command = queue.get(0);
						String input = command.getCasInput();
						String result;
						ValidExpression inVE = null;
						// remove before evaluating to ensure we don't ignore
						// new requests meanwhile
						if (queue.size() > 0) {
							queue.remove(0);
						}
						try {
							inVE = casParser.parseGeoGebraCASInput(input, null);
							// TODO: arbconst()
							result = evaluateGeoGebraCAS(inVE,
									new MyArbitraryConstant(
											(ConstructionElement) command),
									StringTemplate.defaultTemplate, cell,
									// take kernel from cmd, in case macro
									// kernel matters (?)
									cmd.getKernel());
							CASAsyncFinished(inVE, result, null, command, input,
									cell);
						} catch (Throwable exception) {
							Log.debug("exception handling ...");
							exception.printStackTrace();
							result = "";
							CASAsyncFinished(inVE, result, exception, command,
									input, cell);
						}
					}
					Log.debug("thread is quitting");
				}
			};
		}
		if (AsynchronousCommand.USE_ASYNCHRONOUS && !casThread.isAlive()) {
			casThread.start();
		} else {
			casThread.run();
		}
	}

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
			// Log.debug("thread starting: " + exp);

			try {
				threadResult = evalRaw(exp, timeoutMillis);

				// Log.debug("message from thread: " + threadResult);
			} catch (Throwable t) {
				Log.debug("problem from JNI Giac: " + t.toString());
				// force error in GeoGebra
				threadResult = "(";
			}
		}
	}

}
