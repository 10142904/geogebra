package geogebra.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.HashMap;

//import org.concord.framework.data.stream.DataListener;
//import org.concord.framework.data.stream.DataStreamEvent;
//import org.concord.sensor.SensorDataProducer;
//import org.mozilla.javascript.Context;
//import org.mozilla.javascript.Scriptable;


public class ScriptManager extends geogebra.common.plugin.ScriptManagerCommon {
	
	
	// library of functions that is available to all JavaScript calls
	// init() is called when GeoGebra starts up (eg to start listeners)
	/*
	private String libraryScriptxxx ="function ggbOnInit() {}";
	private String libraryScriptxx ="function ggbOnInit() {"+
		"ggbApplet.evalCommand('A=(1,2)');" +
	//"ggbApplet.registerAddListener('listener');" +
	"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}" +
			"function listener() {//java.lang.System.out.println('add listener called');\n" +
			"var x = ggbApplet.getXcoord('A');" +
			"var y = ggbApplet.getYcoord('A');" +
			"var len = Math.sqrt(x*x + y*y);" +
			"if (len > 5) { x=x*5/len; y=y*5/len; }" +
			"" +
			"ggbApplet.unregisterObjectUpdateListener('A');" +
			"ggbApplet.setCoords('A',x,y);" +
			"ggbApplet.registerObjectUpdateListener('A','listener');" +
			"}";*/
	
	public ScriptManager(AbstractApplication app) {
		this.app = app;
		
		//evalScript("ggbOnInit();");
	}
	
	public void ggbOnInit() {
		
		try {
			((Application)app).getKernel();
			// call only if libraryJavaScript is not the default (ie do nothing)
			if (!((Application)app).getKernel().getLibraryJavaScript().equals(Kernel.defaultLibraryJavaScript))
				CallJavaScript.evalScript(((Application)app), "ggbOnInit();", null);
		} catch (Exception e) {
			AbstractApplication.debug("Error calling ggbOnInit(): "+e.getMessage());
		}
		
		// Python
		String libraryPythonScript = app.getKernel().getLibraryPythonScript();
		if (!libraryPythonScript.equals(Kernel.defaultLibraryPythonScript)) {
			// TODO ggbOnInit() is made optional here, in a hackish way
			((Application)app).getPythonBridge().eval(libraryPythonScript+ "\n"
					+ "try:\n"
					+ "    ggbOnInit()\n"
					+ "except NameError:\n"
					+ "    pass\n");
		}

	}
	



	
	public synchronized void initJavaScript() {
		
		if (app.isApplet()) {
			((Application)app).getApplet().initJavaScript();
		}
	}
	
	public void callJavaScript(String jsFunction, Object [] args) {		
		
		if (app.isApplet() && app.useBrowserForJavaScript()) {
			((Application)app).getApplet().callJavaScript(jsFunction, args);
		} else {

			
			StringBuilder sb = new StringBuilder();
			sb.append(jsFunction);
			sb.append("(");
			for (int i = 0 ; i < args.length ; i++) {
				sb.append('"');
				sb.append(args[i].toString());
				sb.append('"');
				if (i < args.length - 1) sb.append(",");
			}
			sb.append(");");
			
			AbstractApplication.debug(sb.toString());
			
			CallJavaScript.evalScript(app, sb.toString(), null);

		}
	}

	USBFunctions usb = null;
	
	public USBFunctions getUSBFunctions() {
		if (usb == null) usb = new USBFunctions(this);
		
		return usb;
	}

	public void notifyDraw(String label, double[] x, double[] y) {			
		if (!listenersEnabled || penListeners == null || penListeners.size() ==0) 
			return;
		int n = x.length;
		StringBuilder params = new StringBuilder("(\"");
		params.append(label);
		params.append("\",new Array(");
		for(int i =0;i<n;i++)
			params.append(x[i]+(i!=n-1?",":"),new Array("));
		for(int i =0;i<n;i++)
				params.append(y[i]+(i!=n-1?",":"))"));
		
		int size = penListeners.size();
		for (int i=0; i < size; i++) {							
			CallJavaScript.evalScript(app, (String) penListeners.get(i)+ 
					params.toString(),null);
			
		}		
		
		
	}


}
