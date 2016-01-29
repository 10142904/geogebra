package org.geogebra.common.main;

import java.util.Date;
import java.util.LinkedList;

import org.geogebra.common.kernel.commands.CmdGetTime;
import org.geogebra.common.kernel.commands.Commands;

//import com.google.gwt.i18n.client.DateTimeFormat;

public class ExamEnvironment {
	private boolean supports3D, supportsCAS;
	long examStartTime = -1;
	private LinkedList<Long> cheatingTimes = null;
	private LinkedList<Boolean> cheatingEvents = null;
	private long closed = -1;
	private long maybeCheating = -1;

	public long getStart() {
		return examStartTime;
	}
	public boolean is3DAllowed() {
		return supports3D;
	}

	public void set3DAllowed(boolean supports3d) {
		supports3D = supports3d;
	}

	public boolean isCASAllowed() {
		return supportsCAS;
	}

	public void setCASAllowed(boolean supportsCAS) {
		this.supportsCAS = supportsCAS;
	}

	public void setStart(long time) {
		examStartTime = time;

	}

	public void startCheating() {
		maybeCheating = System.currentTimeMillis();
	}

	public void checkCheating() {
		if (maybeCheating > 0
				&& maybeCheating < System.currentTimeMillis() - 100) {

			maybeCheating = -1;
			if (getStart() > 0) {
				initLists();
				if (cheatingEvents.size() == 0
						|| !cheatingEvents.get(cheatingEvents.size() - 1)
								.booleanValue()) {
					cheatingTimes.add(System.currentTimeMillis());
					cheatingEvents.add(true);
				}
				App.debug("STARTED CHEATING");
			}
		}
	}

	public void stopCheating() {
		maybeCheating = -1;
		if (cheatingTimes == null || getStart() < 0) {
			return;
		}

		if (cheatingEvents.size() > 0
				&& cheatingEvents.get(cheatingEvents.size() - 1).booleanValue()) {
			cheatingTimes.add(System.currentTimeMillis());
			cheatingEvents.add(false);
			App.debug("STOPPED CHEATING");
		}
	}

	private void initLists() {
		if (cheatingTimes == null) {
			cheatingTimes = new LinkedList<Long>();
			cheatingEvents = new LinkedList<Boolean>();
		}

	}
	public boolean isCheating() {
		return cheatingTimes != null;
	}

	private String getLocalizedTime(Localization loc, long time) {
		// eg "Fri 23rd October 2015 14:08:48"
		return CmdGetTime.buildLocalizedDate("\\D \\j\\S \\F \\Y \\H:\\i:\\s",
				new Date(time), loc);
	}
	
	private String getLocalizedTimeOnly(Localization loc, long time) {
		// eg "14:08:48"
		return CmdGetTime.buildLocalizedDate("\\H:\\i:\\s",
				new Date(time), loc);
	}
	
	private String getLocalizedDateOnly(Localization loc, long time) {
		// eg "Fri 23rd October 2015"
		return CmdGetTime.buildLocalizedDate("\\D \\j\\S \\F \\Y",
				new Date(time), loc);
	}

	/*public String getLog(Localization loc) {
		StringBuilder sb = new StringBuilder();
		sb.append("Exam started");
		sb.append(' ');
		sb.append(getLocalizedTime(loc, examStartTime));
		sb.append("\n");
		if(cheatingTimes != null){
			for(int i = 0; i < cheatingTimes.size(); i++){
				sb.append(timeToString(cheatingTimes.get(i)));
				sb.append(' ');
				sb.append(cheatingEvents.get(i) ? "CHEATING ALERT: exam left"
						: "exam active again");
				sb.append("\n");
			}
		}
		if (closed > 0) {
			sb.append("Exam finished");
			sb.append(' ');
			sb.append(getLocalizedTime(loc, closed));
		}
		return sb.toString();
	}
	*/

	
	/**
	 * NEW LOG DIALOG
	 * (Alicia)
	 */
	public String getLog(Localization loc) {
		StringBuilder sb = new StringBuilder();
		
		// Deactivated Views
		if(supportsCAS == false || supports3D == false ){
			sb.append(loc.getMenu("exam_views_deactivated")+":");
			sb.append(' ');
			}
		if (supportsCAS == false) {
			sb.append("CAS");
		}
		if (supportsCAS == false && supports3D == false) {
			sb.append("," + ' ');
		}
		if (supports3D == false) {
			sb.append("3D Graphics");
		}
		sb.append("\n");
		
		// Exam Start Date
		sb.append(loc.getMenu("exam_start_date")+":"); 
		sb.append(' ');
		sb.append(getLocalizedDateOnly(loc, examStartTime));
		sb.append("\n");
		
		// Exam Start Time
		sb.append(loc.getMenu("exam_start_time")+":"); 
		sb.append(' ');
		sb.append(getLocalizedTimeOnly(loc, examStartTime));
		sb.append("\n");
		
		// Exam End Time
		if (closed > 0) {
			sb.append(loc.getMenu("exam_end_time")+":"); 
			sb.append(' ');
			sb.append(getLocalizedTimeOnly(loc, closed));
			sb.append("\n");
		}
		
		sb.append("-------------");
		sb.append("\n");
		
		// Log times
		sb.append("0:00");
		sb.append(' ');
		sb.append(loc.getMenu("exam_started"));
		sb.append("\n");
		
		if(cheatingTimes != null){
			for(int i = 0; i < cheatingTimes.size(); i++){
				sb.append(timeToString(cheatingTimes.get(i)));
				sb.append(' ');
				sb.append(cheatingEvents.get(i) ? loc.getMenu("exam_log_window_left") //CHEATING ALERT: exam left
						: loc.getMenu("exam_log_window_entered")); //exam active again
				sb.append("\n");
			}
		}
		if (closed > 0) {
			sb.append(timeToString(closed)); //  get exit timestamp
			sb.append(' ');
			sb.append(loc.getMenu("exam_ended"));
		}
		return sb.toString();
	}
	
	public String timeToString(long timestamp) {
		if (examStartTime < 0) {
			return "0:00";
		}
		int secs = (int) ((timestamp - examStartTime) / 1000);
		int mins = secs / 60;
		secs -= mins * 60;
		String secsS = secs + "";
		if (secs < 10) {
			secsS = "0" + secsS;
		}
		return mins + ":" + secsS;
	}

	public void exit() {
		this.closed = System.currentTimeMillis();
	}

	public String getSyntax(String cmdInt, Localization loc) {
		if(supportsCAS){
			return loc.getCommandSyntax(cmdInt);
		}
		Commands cmd = null;
		try {
			cmd = Commands.valueOf(cmdInt);

		} catch (Exception e) {
			// macro or error
		}
		if (cmd == null) {
			return loc.getCommandSyntax(cmdInt);
		}
		// IntegralBetween gives all syntaxes. Typing Integral or NIntegral
		// gives suggestions for NIntegral
		switch (cmd) {
		case Integral:
		case NIntegral:
			return loc.getCommandSyntaxCAS("NIntegral");
		case LocusEquation:
		case Envelope:
		case TrigSimplify:
		case Expand:
		case Factor:
		case IFactor:
		case Simplify:
		case SurdText:
		case ParametricDerivative:
		case Derivative:
		case TrigExpand:
		case TrigCombine:
		case Limit:
		case LimitBelow:
		case LimitAbove:
		case Degree:
		case Coefficients:
		case PartialFractions:
		case SolveODE:
		case ImplicitDerivative:
		case NextPrime:
		case PreviousPrime:
			return null;
		default:
			return loc.getCommandSyntax(cmdInt);
		}

	}

}
