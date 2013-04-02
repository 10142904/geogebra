package geogebra.common.util;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.prover.AbstractProverReciosMethod;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Prover package for GeoGebra.
 * Allows using multiple backends for theorem proving.
 */

public abstract class Prover {

	/**
	 * Enum list of supported prover backends for GeoGebra
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProverEngine {/**
	 * Tomas Recio's method
	 */
	RECIOS_PROVER, /**
	 * Francisco Botana's method
	 */
	BOTANAS_PROVER, /**
	 * OpenGeoProver (http://code.google.com/p/open-geo-prover/), Wu's method
	 */
	OPENGEOPROVER_WU, /**
	 * OpenGeoProver, Area method
	 */
	OPENGEOPROVER_AREA, /**
	 * pure symbolic prover (every object is calculated symbolically, also the statements)
	 */ 
	PURE_SYMBOLIC_PROVER,
	/**
	 * Default prover (GeoGebra decides internally)
	 */
	AUTO}
	
	/**
	 * Possible results of an attempted proof
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 *
	 */
	public enum ProofResult {/**
	 * The proof is completed, the statement is generally true (with some NDG conditions)
	 */
	TRUE, /**
	 * The proof is completed, the statement is generally false
	 */
	FALSE, /**
	 * The statement cannot be proved by using the current backed within the given timeout 
	 */
	UNKNOWN}
	
	/**
	 * Maximal time to be spent in the prover subsystem
	 */
	/* input */
	protected int timeout = 5;
	private ProverEngine engine = ProverEngine.AUTO;
	/**
	 * The full GeoGebra construction, containing all geos and algos.
	 */
	protected Construction construction;
	/**
	 * The statement to be prove
	 */
	protected GeoElement statement;
	
	protected static AbstractProverReciosMethod reciosProver;
		 
	/**
	 * Gives the current statement to prove
	 * @return the statement (usually a GeoBoolean)
	 */
	public GeoElement getStatement() {
		return statement;
	}

	/* output */
	private HashSet<NDGCondition> ndgConditions = new HashSet<NDGCondition>();
	/**
	 * The result of the proof
	 */
	protected ProofResult result;

	/**
	 * Should the prover return extra NDG conditions? If not, some computation time may be saved.
	 */
	private boolean returnExtraNDGs;
	
	/**
	 * @author Zoltan Kovacs <zoltan@geogebra.org>
	 * An object which contains a condition description (e.g. "AreCollinear")
	 * and an ordered list of GeoElement's (e.g. A, B, C)
	 */
	public static class NDGCondition {
		/**
		 * The condition String
		 */
		String condition;
		/**
		 * Array of GeoElements (parameters of the condition)
		 */
		GeoElement[] geos;
		/**
		 * A short textual description of the condition
		 * @return the condition
		 */
		public String getCondition() {
			return condition;
		}
		/**
		 * Sets a condition text
		 * @param condition the text, e.g. "AreCollinear"
		 */
		public void setCondition(String condition) {
			this.condition = condition;
		}
		/**
		 * Returns the GeoElements for a given condition
		 * @return the array of GeoElements
		 */
		public GeoElement[] getGeos() {
			return geos;
		}
		/**
		 * Sets the GeoElements for a given condition
		 * @param object the array of GeoElements
		 */
		public void setGeos(GeoElement[] object) {
			this.geos = object;
		}
	}
	
	/**
	 * Constructor for the package.
	 */
	public Prover() {
		 proveAutoOrder = new ArrayList<ProverEngine>();
		 // Order of Prove[] for the AUTO prover:
		 // Recio is the fastest.
		 proveAutoOrder.add(ProverEngine.RECIOS_PROVER);
		 // Botana's prover is also fast for general problems.
		 proveAutoOrder.add(ProverEngine.BOTANAS_PROVER);
		 // Wu may be a bit slower.
		 proveAutoOrder.add(ProverEngine.OPENGEOPROVER_WU);
		 // Area method is not polished yet.
		 proveAutoOrder.add(ProverEngine.OPENGEOPROVER_AREA);
		 
		 // Order of ProveDetails[] for the AUTO prover:
		 proveDetailsAutoOrder = new ArrayList<ProverEngine>();
		 // Wu's method does the best job.
		 proveDetailsAutoOrder.add(ProverEngine.OPENGEOPROVER_WU);
		 // Current version of Botana's presumes some good looking conditions, so maybe useful. 
		 proveDetailsAutoOrder.add(ProverEngine.BOTANAS_PROVER);
		 // Recio may a few NDGs for the denominator polynomial. 
		 proveDetailsAutoOrder.add(ProverEngine.RECIOS_PROVER);
		 // Area method seems to return {true} always, not useful.
		 proveDetailsAutoOrder.add(ProverEngine.OPENGEOPROVER_AREA);
	}

	/**
	 * Sets the maximal time spent in the Prover for the given proof.
	 * @param timeout The timeout in seconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Sets the prover engine.
	 * @param engine The engine subsystem
	 */
	public void setProverEngine(ProverEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Sets the GeoGebra construction as the set of the used objects
	 * in the proof. 
	 * @param construction The GeoGebra construction
	 */
	public void setConstruction(Construction construction) {
		this.construction = construction;
	}
	
	/**
	 * Sets the statement to be proven.
	 * @param root The statement to be proven
	 */
	public void setStatement(GeoElement root) {
		this.statement = root;
	}
	
	/**
	 * Adds a non-degeneracy condition to the prover object
	 * @param ndgc the condition itself
	 */
	public void addNDGcondition(NDGCondition ndgc) {
		ndgConditions.add(ndgc);
	}
	
	private List<ProverEngine> proveAutoOrder;
	private List<ProverEngine> proveDetailsAutoOrder;
	private boolean useAlternativeBotana;

		
	/**
	 * The real computation of decision of a statement.
	 * The statement is forwarded to an engine (or more engines).
	 */
	public void decideStatement() {
		// Step 1: Checking if the statement is null.
		if (statement == null) {
			App.error("No statement to prove");
			result = ProofResult.UNKNOWN;
			return;
		}

		// Step 2:
		// Maybe an already computed value is asked to be proven, e.g. Prove[1==1], i.e. Prove[true]
		AlgoElement algoParent = statement.getParentAlgorithm();
		if (algoParent == null) {
			if (statement.getValueForInputBar().equals("true"))
				result = ProofResult.TRUE; // Trust in kernel's wisdom
			else if (statement.getValueForInputBar().equals("false"))
				result = ProofResult.FALSE; // Trust in kernel's wisdom
			else
				result = ProofResult.UNKNOWN; // Not sure if this is executed at all, but for sure.
			return;
		}
		
		// Step 3: Non-AUTO provers
		if (engine != ProverEngine.AUTO) {
			callEngine(engine);
			return;
		}
		
		// Step 4: AUTO prover
		App.debug("Using " + engine);
		Iterator<ProverEngine> it;
		if (isReturnExtraNDGs())
			it = proveDetailsAutoOrder.iterator();
		else
			it = proveAutoOrder.iterator();
		result = ProofResult.UNKNOWN;
		while (result == ProofResult.UNKNOWN && it.hasNext()) {
			ProverEngine pe = it.next();
			callEngine(pe);
		}
	}

	private void callEngine(ProverEngine currentEngine) {
		App.debug("Using " + currentEngine);
		ndgConditions = new HashSet<NDGCondition>(); // reset
		if (currentEngine == ProverEngine.BOTANAS_PROVER) {
			// Botana's prover needs singularWS.
			// So don't try to use it if singularWS is not available:
			if (App.singularWS == null) {
				App.debug(currentEngine + " cannot be used, since singularWS is null");
				result = ProofResult.UNKNOWN;
				return;
			}
			if (!App.singularWS.isAvailable()) {
				App.debug(currentEngine + " cannot be used, since singularWS is unavailable");
				result = ProofResult.UNKNOWN;
				return;
			}
			if (useAlternativeBotana){
				result = geogebra.common.kernel.prover.ProverBotanasMethod.proveAlternative(this);
			} else {
				result = geogebra.common.kernel.prover.ProverBotanasMethod.prove(this);
			}
			return;
		} else if (currentEngine == ProverEngine.RECIOS_PROVER) {
			result = reciosProver.prove(this);
			return;
		} else if (currentEngine == ProverEngine.PURE_SYMBOLIC_PROVER) {
			result = geogebra.common.kernel.prover.ProverPureSymbolicMethod.prove(this);
			return;
		} else if (currentEngine == ProverEngine.OPENGEOPROVER_WU || 
				currentEngine == ProverEngine.OPENGEOPROVER_AREA ) {
			result = openGeoProver(currentEngine);
			return;
		}

	}
	
	/**
	 * Gets non-degeneracy conditions of the current proof.
	 * @return The XML output string of the NDG condition
	 */
	public HashSet<NDGCondition> getNDGConditions() {
		return ndgConditions;
	}
	
	/**
	 * Gets the proof result
	 * @return The result (TRUE, FALSE or UNKNOWN)
	 */
	public ProofResult getProofResult() {
		return result;
	}
	
	/**
	 * If the result of the proof can be expressed by a boolean value,
	 * then it returns that value. 
	 * @return The result of the proof (true, false or null)
	 */
	public Boolean getYesNoAnswer() {
		if (result != null)
		{
			if (result == Prover.ProofResult.TRUE)
				return true;
			if (result == Prover.ProofResult.FALSE)
				return false;
		}
		return null;
	}

	/**
	 * A minimal version of the construction XML. Only elements/commands are preserved,
	 * the rest is deleted. 
	 * @param cons The construction
	 * @return The simplified XML 
	 */
	// TODO: Cut even more unneeded parts to reduce unneeded traffic between OGP and GeoGebra.
	protected static String simplifiedXML(Construction cons, GeoElement statement) {
		StringBuilder sb = new StringBuilder();
		cons.getConstructionElementsXML_OGP(sb, statement);
		return "<construction>\n" + sb.toString() + "</construction>";
	}

	/**
	 * Does the real computation for the proof
	 */
	public void compute() {
		// Will be overridden by web and desktop
	}

	/**
	 * Calls OpenGeoProver
	 * @return the proof result
	 */
	protected abstract ProofResult openGeoProver(ProverEngine pe);

	/**
	 * Will the prover return extra NDGs? 
	 * @return yes or no
	 */
	public boolean isReturnExtraNDGs() {
		return returnExtraNDGs;
	}

	/**
	 * The prover may return extra NDGs
	 * @param returnExtraNDGs setting for the prover
	 */
	public void setReturnExtraNDGs(boolean returnExtraNDGs) {
		this.returnExtraNDGs = returnExtraNDGs;
	}

	/**
	 * Sets if the alternative botana method should be used to check
	 * if a statement is generically true.
	 * @param b True if the alternative method should be used, false if the traditional method should be used
	 */
	public void setAlternativeBotana(final boolean b) {
		useAlternativeBotana = b;
		
	}
	
}
