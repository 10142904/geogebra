package org.geogebra.common.kernel.prover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAngularBisectorPoints;
import org.geogebra.common.kernel.algos.AlgoCircleThreePoints;
import org.geogebra.common.kernel.algos.AlgoCircleTwoPoints;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoEllipseHyperbolaFociPoint;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgoAre;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.Inspecting.MinusChecker;
import org.geogebra.common.kernel.arithmetic.Inspecting.PlusChecker;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ProverSettings;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.Prover;
import org.geogebra.common.util.Prover.NDGCondition;
import org.geogebra.common.util.Prover.ProofResult;

/**
 * A prover which uses Francisco Botana's method to prove geometric theorems.
 * 
 * @author Zoltan Kovacs
 *
 */
public class ProverBotanasMethod {

	/**
	 * Inverse mapping of botanaVars.
	 */
	private static HashMap<List<Variable>, GeoElement> botanaVarsInv;

	private static void updateBotanaVarsInv(GeoElement statement) {
		if (botanaVarsInv == null)
			botanaVarsInv = new HashMap<List<Variable>, GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			Variable[] vars = ((SymbolicParametersBotanaAlgo) geo)
					.getBotanaVars(geo);
			if (vars != null) {
				List<Variable> varsList = Arrays.asList(vars);
				botanaVarsInv.put(varsList, geo);
			}
		}
	}

	protected static List<GeoElement> getFreePoints(GeoElement statement) {
		List<GeoElement> freePoints = new ArrayList<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) { // this
																		// is a
																		// free
																		// point
				freePoints.add(geo);
			}
		}
		return freePoints;
	}

	// We don't use this at the moment. It seemed to be useful to select the
	// best coordinates to fix from circle centers
	// but finally there is no test case for this at the moment to be convinced
	// if this really helps in speed.
	private static List<GeoElement> getCircleCenters(GeoElement statement) {
		List<GeoElement> circleCenters = new ArrayList<GeoElement>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoConic()) { // this is probably a circle
				if (geo.getParentAlgorithm() instanceof AlgoCircleTwoPoints
						|| geo.getParentAlgorithm() instanceof AlgoCircleThreePoints) {
					// Search for the center point.
					Variable[] vars = ((SymbolicParametersBotanaAlgo) geo)
							.getBotanaVars(geo);
					Variable[] center = new Variable[2];
					center[0] = vars[0];
					center[1] = vars[1];
					GeoElement centerGeo = botanaVarsInv.get(Arrays
							.asList(center));
					if (centerGeo != null) // it may be a virtual center (TODO:
											// handle somehow)
						circleCenters.add(centerGeo);
				}
			}
		}
		return circleCenters;
	}

	/**
	 * Creates those polynomials which describe that none of 3 free points can
	 * lie on the same line.
	 * 
	 * @return the NDG polynomials (in denial form)
	 */
	private static Polynomial[] create3FreePointsNeverCollinearNDG(Prover prover) {
		// Creating the set of free points first:
		List<GeoElement> freePoints = getFreePoints(prover.getStatement());
		int setSize = freePoints.size();

		// Creating NDGs:
		NDGCondition ndgc = new NDGCondition();
		if (setSize > 3)
			ndgc.setCondition("DegeneratePolygon");
		else
			ndgc.setCondition("AreCollinear");
		GeoElement[] geos = new GeoElement[setSize];
		int i = 0;
		Iterator<GeoElement> it = freePoints.iterator();
		while (it.hasNext()) {
			geos[i++] = it.next();
		}
		ndgc.setGeos(geos);
		Arrays.sort(ndgc.getGeos());
		prover.addNDGcondition(ndgc);

		// The output will contain $\binom{n}{3}$ elements:
		Polynomial[] ret = new Polynomial[setSize * (setSize - 1)
				* (setSize - 2) / 6];
		i = 0;
		// Creating the set of triplets:
		HashSet<HashSet<GeoElement>> triplets = new HashSet<HashSet<GeoElement>>();
		Iterator<GeoElement> it1 = freePoints.iterator();
		while (it1.hasNext()) {
			GeoElement geo1 = it1.next();
			Iterator<GeoElement> it2 = freePoints.iterator();
			while (it2.hasNext()) {
				GeoElement geo2 = it2.next();
				if (!geo1.isEqual(geo2)) {
					Iterator<GeoElement> it3 = freePoints.iterator();
					while (it3.hasNext()) {
						GeoElement geo3 = it3.next();
						if (!geo1.isEqual(geo3) && !geo2.isEqual(geo3)) {
							HashSet<GeoElement> triplet = new HashSet<GeoElement>();
							triplet.add(geo1);
							triplet.add(geo2);
							triplet.add(geo3);
							// Only the significantly new triplets will be
							// processed:
							if (!triplets.contains(triplet)) {
								triplets.add(triplet);
								Variable[] fv1 = ((SymbolicParametersBotanaAlgo) geo1)
										.getBotanaVars(geo1);
								Variable[] fv2 = ((SymbolicParametersBotanaAlgo) geo2)
										.getBotanaVars(geo2);
								Variable[] fv3 = ((SymbolicParametersBotanaAlgo) geo3)
										.getBotanaVars(geo3);
								// Creating the polynomial for collinearity:
								Polynomial p = Polynomial.collinear(fv1[0],
										fv1[1], fv2[0], fv2[1], fv3[0], fv3[1]);
								App.debug("Forcing non-collinearity for points "
										+ geo1.getLabelSimple()
										+ ", "
										+ geo2.getLabelSimple()
										+ " and "
										+ geo3.getLabelSimple());
								// Rabinowitsch trick for prohibiting
								// collinearity:
								ret[i] = p.multiply(
										new Polynomial(new Variable()))
										.subtract(new Polynomial(1));
								// FIXME: this always introduces an extra
								// variable, shouldn't do
								i++;
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Uses a minimal heuristics to fix the first four variables to certain
	 * "easy" numbers. The first two variables (usually the coordinates of the
	 * first point) are set to 0, and the second two variables (usually the
	 * coordinates of the second point) are set to 0 and 1.
	 * 
	 * @param prover
	 *            the input prover
	 * @param coords
	 *            number of fixed coordinates
	 * @return a HashMap, containing the substitutions
	 */
	static HashMap<Variable, Integer> fixValues(Prover prover, int coords) {

		int[] fixCoords = { 0, 0, 0, 1 };

		GeoElement statement = prover.getStatement();
		List<GeoElement> freePoints = getFreePoints(statement);
		List<GeoElement> fixedPoints = new ArrayList<GeoElement>();
		// Adding free points:
		for (GeoElement ge : freePoints) {
			fixedPoints.add(ge);
		}

		HashMap<Variable, Integer> ret = new HashMap<Variable, Integer>();

		Iterator<GeoElement> it = fixedPoints.iterator();
		GeoElement[] geos = new GeoElement[2];
		int i = 0, j = 0;
		while (it.hasNext() && i < 2 && j < coords) {
			GeoElement geo = it.next();
			Variable[] fv = ((SymbolicParametersBotanaAlgo) geo)
					.getBotanaVars(geo);
			geos[i] = geo;
			ret.put(fv[0], fixCoords[j]);
			++j;
			if (j < coords) {
				ret.put(fv[1], fixCoords[j]);
				++i;
				++j;
			}
		}

		// We implicitly assumed that the first two points are different:
		if (i == 2 && prover.isReturnExtraNDGs()) {
			NDGCondition ndgc = new NDGCondition();
			ndgc.setCondition("AreEqual");
			ndgc.setGeos(geos);
			Arrays.sort(ndgc.getGeos());
			prover.addNDGcondition(ndgc);
		}
		return ret;
	}

	// formulate draw in readable format, TODO: create translation keys
	private static String getTextFormat(GeoElement statement) {
		Localization loc = statement.getKernel().getLocalization();
		ArrayList<String> freePoints = new ArrayList<String>();
		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		StringBuilder hypotheses = new StringBuilder();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isGeoPoint() && geo.getParentAlgorithm() == null) {
				freePoints.add(geo.getLabelSimple());
			} else if (!(geo instanceof GeoNumeric)) {
				String definition = geo
						.getDefinitionDescription(StringTemplate.noLocalDefault);
				String textLocalized = loc.getPlain("LetABeB",
						geo.getLabelSimple(), definition);
				hypotheses.append(textLocalized).append(".\n");
			}
		}
		StringBuilder theoremText = new StringBuilder();
		StringBuilder freePointsText = new StringBuilder();

		for (String str : freePoints) {
			freePointsText.append(str);
			freePointsText.append(",");
		}
		freePointsText.deleteCharAt(freePointsText.length() - 1);
		theoremText.append(loc.getPlain("LetABeArbitraryPoints",
						freePointsText.toString())).append(".\n");

		theoremText.append(hypotheses);

		String toProveStr = String.valueOf(statement.getParentAlgorithm());
		theoremText.append(loc.getPlain("ProveThat", toProveStr)).append(".");
		return theoremText.toString();
	}

	/**
	 * Proves the statement by using Botana's method
	 * 
	 * @param prover
	 *            the prover input object
	 * @return if the statement is true
	 */
	public static ProofResult prove(org.geogebra.common.util.Prover prover) {
		GeoElement statement = prover.getStatement();

		// Decide quickly if proving this kind of statement is already implemented at all: 
		if (!(statement.getParentAlgorithm() instanceof SymbolicParametersBotanaAlgoAre)) {
			App.debug(statement.getParentAlgorithm() + " unimplemented");
			return ProofResult.UNKNOWN;
			// If not, let's not spend any time here, but give up immediately. 
		}
		
		// If Singular is not available, let's try Giac (mainly on web)
		if (App.singularWS == null || (!App.singularWS.isAvailable())) {
			ProverSettings.transcext = false;
			App.debug("Testing local CAS connection");
			GeoGebraCAS cas = (GeoGebraCAS) statement.getKernel()
					.getGeoGebraCAS();
			try {
				String output = cas.getCurrentCAS().evaluateRaw("1");
				App.debug("Local CAS evaluates 1 to " + output);
				if (!(output.equals("1"))) {
					App.debug("Switching to PROCESSING mode");
					return ProofResult.PROCESSING;
				}
			} catch (Throwable e) {
				App.debug("Exception, switching to PROCESSING mode");
				return ProofResult.PROCESSING;
			}
		}

		// Getting the hypotheses:
		Polynomial[] hypotheses = null;
		boolean interpretFalseAsUndefined = false;
		int maxFixcoords = -1;

		// write construction in readable format
		// App.debug(getTextFormat(statement));

		Iterator<GeoElement> it = statement.getAllPredecessors().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// AbstractApplication.debug(geo);
			if (geo instanceof SymbolicParametersBotanaAlgo) {
				try {
					App.debug("/* PROCESSING OBJECT " + geo.getLabelSimple()
							+ " */");
					if (ProverSettings.captionAlgebra) {
						geo.setCaption(null);
					}
					String command = geo
							.getCommandDescription(StringTemplate.noLocalDefault);
					if (!("".equals(command))) {
						App.debug("/* Command definition */");
						App.debug(geo.getLabelSimple()
								+ " = "
								+ geo.getCommandDescription(StringTemplate.noLocalDefault)
								+ " /* "
								+ geo.getDefinitionDescription(StringTemplate.noLocalDefault)
								+ " */");
					} else {
						String description = geo.getAlgebraDescriptionDefault();
						if (!description.startsWith("xOyPlane")) { // handling
																	// GeoGebra3D's
																	// definition
																	// for
																	// xy-plane
							App.debug(description + " /* free point */");
							Variable[] v = new Variable[2];
							v = ((SymbolicParametersBotanaAlgo) geo)
									.getBotanaVars(geo);
							if (ProverSettings.captionAlgebra) {
								geo.setCaptionBotanaVars("(" + v[0].toTeX()
										+ "," + v[1].toTeX() + ")");
							}
							App.debug("// Free point "
									+ geo.getLabelSimple() + "(" + v[0] + ","
									+ v[1] + ")");
						}
					}
					Polynomial[] geoPolys = ((SymbolicParametersBotanaAlgo) geo)
							.getBotanaPolynomials(geo);
					
					/* Check if the construction step could be reliably translated to
					 * an algebraic representation. This is the case for linear
					 * constructions (parallel, perpendicular etc.) but not for
					 * quadratic ones (intersection of conics etc.). In the latter case the equation system
					 * may be solvable even if geometrically, "seemingly" the statement is
					 * true. To avoid such confusing cases, it's better to report undefined
					 * instead of false.
					 */
					AlgoElement algo = geo.getParentAlgorithm();
					if (algo instanceof AlgoAngularBisectorPoints
							|| algo instanceof AlgoEllipseHyperbolaFociPoint
							|| (algo instanceof AlgoIntersectConics && ((AlgoIntersectConics) algo)
									.existingIntersections() != 1)
							|| (algo instanceof AlgoIntersectLineConic && ((AlgoIntersectLineConic) algo)
									.existingIntersections() != 1)) {
						interpretFalseAsUndefined = true;
						App.debug("Due to " + algo + " is not 1-1 algebraic mapping, FALSE will be interpreted as UNKNOWN");
					}

					/*
					 * Consider the following case: Let AB a segment and C a
					 * point on it. Move C to A. Now let's check if Prove[A==C]
					 * returns false. Since C is on a line and normally A=(0,0),
					 * B=(0,1), x(C)=0, but this last one should not be
					 * constrained, otherwise we will get true for a false
					 * statement! See Example 52 in Zoltan's diss on page
					 * 176---here we need to generalize B to avoid getting true.
					 */
					if (algo instanceof AlgoPointOnPath
							&& ProverSettings.transcext) {
						maxFixcoords = 2;
					}
				
					if (geoPolys != null) {
						if (geo instanceof GeoPoint) {
							Variable[] v = new Variable[2];
							v = ((SymbolicParametersBotanaAlgo) geo)
									.getBotanaVars(geo);
							App.debug("// Constrained point "
									+ geo.getLabelSimple() + "(" + v[0] + ","
									+ v[1] + ")");
							if (ProverSettings.captionAlgebra) {
								geo.setCaptionBotanaVars("(" + v[0].toTeX()
										+ "," + v[1].toTeX() + ")");
							}
						}
						int nHypotheses = 0;
						if (hypotheses != null)
							nHypotheses = hypotheses.length;
						Polynomial[] allPolys = new Polynomial[nHypotheses
								+ geoPolys.length];
						for (int i = 0; i < nHypotheses; ++i) {
							allPolys[i] = hypotheses[i];
						}

						App.debug("Hypotheses:");
						for (int i = 0; i < geoPolys.length; ++i) {
							App.debug((nHypotheses + i + 1) + ". "
									+ geoPolys[i]);
							allPolys[nHypotheses + i] = geoPolys[i];
							if (ProverSettings.captionAlgebra) {
								geo.addCaptionBotanaPolynomial(geoPolys[i]
										.toTeX());
							}
						}
						hypotheses = allPolys;

					}
				} catch (NoSymbolicParametersException e) {
					App.debug(geo.getParentAlgorithm()
							+ " is not fully implemented");
					return ProofResult.UNKNOWN;
				}
			} else {
				App.debug(geo.getParentAlgorithm() + " unimplemented");
				return ProofResult.UNKNOWN;
			}
		}
		App.debug("Hypotheses have been processed.");
		updateBotanaVarsInv(statement);
		try {
			boolean interpretTrueAsUndefined = false;
			// The sets of statement polynomials.
			// The last equation of each set will be negated.

			Polynomial[][] statements = ((SymbolicParametersBotanaAlgoAre) statement
					.getParentAlgorithm()).getBotanaPolynomials();

			// case input was an expression
			if (statements == null) {
				AlgoElement algo = statement.getParentAlgorithm();
				// get expression string for giac
				String strForGiac = ((AlgoDependentBoolean) algo)
						.getStrForGiac();
				String userStrForGiac = ((AlgoDependentBoolean) algo)
						.getUserGiacString();

				GeoGebraCAS cas = (GeoGebraCAS) statement.getKernel()
						.getGeoGebraCAS();
				try {
					// K
					String output = cas.getCurrentCAS().evaluateRaw(
							strForGiac.toString());
					// F
					String userOutput = cas.getCurrentCAS().evaluateRaw(
							userStrForGiac);
					// T = K/F
					String result = cas.getCurrentCAS().evaluateRaw(
							"simplify(" + output + "/" + userOutput + ")");
					// unhandled input expression
					if (output.contains("?") || userOutput.contains("?")
							|| result.contains("?")) {
						return ProofResult.UNKNOWN;
					}
					// T is not empty
					if (!(result.equals("{}"))) {
						ValidExpression resultVE = (statement.getKernel()
								.getGeoGebraCAS()).getCASparser()
								.parseGeoGebraCASInputAndResolveDummyVars(
										result, statement.getKernel(), null);
						if (resultVE instanceof ExpressionNode
								&& ((ExpressionNode) resultVE).getLeft() instanceof MyList) {
							ExpressionValue nodeToCheck = ((MyList) ((ExpressionNode) resultVE)
									.getLeft()).getListElement(0);
							Inspecting plusInsp = PlusChecker.INSTANCE;
							Inspecting minusInsp = MinusChecker.INSTANCE;
							// check if contains only plus
							boolean contPlus = plusInsp.check(nodeToCheck);
							// check if contains only minus
							boolean contMinus = minusInsp.check(nodeToCheck);
							// expression is trustable
							// if contains only plus or only minus
							if (contPlus || contMinus) {
								((AlgoDependentBoolean) algo)
										.setTrustable(true);
							}
						}
					}
					// giac output is not empty
					if (!(output.equals("{}"))) {
						// App.debug(output);
						ValidExpression validExpression = (statement
								.getKernel()
							.getGeoGebraCAS()).getCASparser()
							.parseGeoGebraCASInputAndResolveDummyVars(output,
									statement.getKernel(), null);
						// App.debug(validExpression
						// .toString(StringTemplate.defaultTemplate));
						PolynomialNode polyRoot = new PolynomialNode();
						ExpressionNode expNode = new ExpressionNode(
							statement.getKernel(),
							((ExpressionNode) validExpression).getLeft());
						MyList list = new MyList(statement.getKernel());
						ExpressionNode root = null;
						if (expNode.getLeft() instanceof MyList) {
							list = ((MyList) expNode.getLeft()).getMyList();
						}
						if (list.getListElement(0).isExpressionNode()) {
							root = (ExpressionNode) list.getListElement(0);
						}
						// case giac output has form A^2-B^2
						if (cas.getCurrentCAS()
								.evaluateRaw(
										"size("
												+ root.toString(StringTemplate.giacTemplate)
												+ ")").equals("2")) {
							// get variables of giac output
							HashSet<GeoElement> giacOutputVars = root
									.getVariables();
							if (giacOutputVars != null) {
								Iterator<GeoElement> giacVarIt = giacOutputVars
										.iterator();
								// reserve first var
								GeoElement geo = giacVarIt.next();
								// copy the expression of giac output
								ExpressionNode copyRoot = root
										.deepCopy(statement.getKernel());
								while (giacVarIt.hasNext()) {
									// iterate through vars
									GeoElement currGeo = giacVarIt.next();
									// replace current var with 1
									copyRoot.traverse(GeoDummyReplacer.getReplacer(
											currGeo.getLabel(StringTemplate.defaultTemplate),
											new ExpressionNode(statement
													.getKernel(), 1), true));
								}
								// get solution of expression with reserved var
								// parameter
								String solutionGeo = cas
										.getCurrentCAS()
										.evaluateRaw(
												"ggbsort(normal(zeros("
														+ copyRoot
																.toString(StringTemplate.giacTemplate)
														+ "=0,"
														+ geo.toString(StringTemplate.giacTemplate)
														+ ")))");
								// skip { and }
								solutionGeo = solutionGeo.substring(1,
										solutionGeo.length() - 1);
								// split solution list
								String[] solutionList = solutionGeo.split(",");
								// parse solutions into expression
								// needed e.g. 1/2 solution
								ValidExpression validSol1 = (statement
										.getKernel().getGeoGebraCAS())
										.getCASparser()
										.parseGeoGebraCASInputAndResolveDummyVars(
												solutionList[0],
												statement.getKernel(), null);
								ValidExpression validSol2 = (statement
										.getKernel().getGeoGebraCAS())
										.getCASparser()
										.parseGeoGebraCASInputAndResolveDummyVars(
												solutionList[1],
												statement.getKernel(), null);
								double geoSol1 = validSol1.evaluateDouble();
								double geoSol2 = validSol2.evaluateDouble();
								// make sure we use the positive root
								if (geoSol1 < 0) {
									geoSol1 = geoSol2;
								}
								// substitution list
								// e.g. [a,v7],[b,v8]
								// where a name of segment and v7 the variable
								ArrayList<Vector<String>> substitutions = ((AlgoDependentBoolean) algo)
										.getVarSubstListOfSegs();
								// copy input expression
								ExpressionNode statementRootCopy = (ExpressionNode) ((AlgoDependentBoolean) algo)
										.getExpression().deepCopy(
												statement.getKernel());
								// result after substitution of solutions into
								// input expression
								// using e.g. subst(2a=b,{a=1,b=2})
								String substOutput = new String();
								if (substitutions != null) {
									Iterator<Vector<String>> itSubst = substitutions.iterator();
									// list of substitutions
									StringBuilder substListStr = new StringBuilder();
									substListStr.append("{");
									while (itSubst.hasNext()) {
										Vector<String> currSubst = itSubst.next();
										// reserved var substitute with
										// calculated solution
										if (currSubst.get(1).equals(geo.toString(StringTemplate.defaultTemplate))) {
											substListStr.append("ggbtmpvar"+currSubst.get(0)+"="+geoSol1+",");
										} else {
											// everything else substitute with 1
											substListStr.append("ggbtmpvar"+currSubst.get(0)+"=1,");
										}
									}
									// cut unwanted ","
									substListStr.setLength(substListStr.length()-1);
									substListStr.append("}");
									// call giac substitution
									substOutput = cas
													.getCurrentCAS()
													.evaluateRaw(
															"subst("
																	+ statementRootCopy
																			.getLeftTree()
																			.toString(
																					StringTemplate.giacTemplate)
																	+ "="
																	+ statementRootCopy
																			.getRightTree()
																			.toString(
																					StringTemplate.giacTemplate)
																	+ ","
																	+ substListStr
																			.toString()
																	+ ")");
								}
								// check if equation is true
								// e.g. 2=2
								if (cas.getCurrentCAS()
										.evaluateRaw(
												"evalb(" + substOutput + ")")
										.equals("0")) {
									return ProofResult.FALSE;
								}
								((AlgoDependentBoolean) algo)
										.setTrustable(true);
							}
						}
						((AlgoDependentBoolean) algo).buildPolynomialTree(root,
							polyRoot);
						((AlgoDependentBoolean) algo)
								.expressionNodeToPolynomial(
							root, polyRoot);
						while (polyRoot.getPoly() == null) {
							((AlgoDependentBoolean) algo)
								.expressionNodeToPolynomial(root,
										polyRoot);
						}
						// get distance polynomials
						ArrayList<Polynomial> extraPolys = ((AlgoDependentBoolean) algo)
							.getExtraPolys();
						statements = new Polynomial[1][extraPolys.size() + 1];
						int index = 0;
						for (Polynomial p : extraPolys) {
							statements[0][index] = p;
							index++;
						}
						// clear polynomial list of distant conditions for the
						// next check
						extraPolys = new ArrayList<Polynomial>();
						// add input polynomial
						statements[0][index] = polyRoot.getPoly();
					}
					// case giac result was empty
					else {
						statements = new Polynomial[1][1];
						statements[0][0] = new Polynomial(0);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}

			}

			AlgoElement algo = statement
					.getParentAlgorithm();
			if (algo instanceof AlgoAreCongruent) {
				if (algo.input[0] instanceof GeoAngle
						&& algo.input[1] instanceof GeoAngle) {
					interpretTrueAsUndefined = true;
				}
			}
			if (algo instanceof AlgoDependentBoolean) {
				Operation operation = ((AlgoDependentBoolean) algo)
						.getOperation();
				boolean trustable = ((AlgoDependentBoolean) algo).isTrustable();
				if (operation == Operation.IS_ELEMENT_OF) {
					if (algo.input[0] instanceof GeoConic
							&& (((GeoConic) algo.input[0]).isEllipse() || ((GeoConic) algo.input[0])
									.isHyperbola())) {
						interpretTrueAsUndefined = true;
					} else if (algo.input[1] instanceof GeoConic
							&& (((GeoConic) algo.input[1]).isEllipse() || ((GeoConic) algo.input[1])
									.isHyperbola())) {
						interpretTrueAsUndefined = true;
					}
				} else if (operation == Operation.EQUAL_BOOLEAN) {
					if ((algo.input[0] instanceof GeoAngle && algo.input[1] instanceof GeoAngle)
							|| !trustable) {
						interpretTrueAsUndefined = true;
					}
				}
			}
			
			// The NDG conditions (automatically created):
			Polynomial[] ndgConditions = null;
			if (ProverSettings.freePointsNeverCollinear == null) {
				if (App.singularWS != null && App.singularWS.isAvailable()) {
					// SingularWS will use Cox' method
					ProverSettings.freePointsNeverCollinear = false;
				} else {
					ProverSettings.freePointsNeverCollinear = true;
				}
			}

			// Only for the Prove command makes sense to set up extra NDG
			// conditions
			if (ProverSettings.freePointsNeverCollinear
					&& !(prover.isReturnExtraNDGs())) {
				ndgConditions = create3FreePointsNeverCollinearNDG(prover);
			}
			HashMap<Variable, Integer> substitutions = null;
			int fixcoords = 0;
			if (prover.isReturnExtraNDGs())
				fixcoords = ProverSettings.useFixCoordinatesProveDetails;
			else
				fixcoords = ProverSettings.useFixCoordinatesProve;
			if (maxFixcoords >= 0 && maxFixcoords < fixcoords) {
				fixcoords = maxFixcoords;
			}
			if (fixcoords > 0) {
				substitutions = fixValues(prover, fixcoords);
				App.debug("substitutions: " + substitutions);
			}
			int nHypotheses = 0;
			int nNdgConditions = 0;
			int nStatements = 0;
			if (hypotheses != null)
				nHypotheses = hypotheses.length;
			if (ndgConditions != null)
				nNdgConditions = ndgConditions.length;
			if (statements != null)
				nStatements = statements.length;

			
			// Solving/manipulating the equation system:
			
			int nExtraPolysNonDenied = 0;
			for (int i = 0; i < nStatements; ++i) {
				nExtraPolysNonDenied += (statements[i].length - 1);
			}
			
			Polynomial[] eqSystem = new Polynomial[nHypotheses + nNdgConditions + nExtraPolysNonDenied + 1];
			// These polynomials will be in the equation system always:
			for (int j = 0; j < nHypotheses; ++j)
				eqSystem[j] = hypotheses[j];
			if (nNdgConditions > 0)
				App.debug("Extra NDGs:");
			for (int j = 0; j < nNdgConditions; ++j) {
				App.debug((j + nHypotheses + 1) + ". " + ndgConditions[j]);
				eqSystem[j + nHypotheses] = ndgConditions[j];
			}
			int k = nHypotheses + nNdgConditions;
			if (nExtraPolysNonDenied > 0)
				App.debug("Statement equations (non-denied parts):");
			for (int i = 0; i < nStatements; ++i) {
				for (int j = 0; j < statements[i].length - 1; ++j) {
					App.debug((k + 1) + ". " + statements[i][j]);
					eqSystem[k] = statements[i][j];
					++k;
				}
			}

			// Rabinowitsch trick for the last polynomials of the theses of the statement.
			// Here we use that NOT (A and B and C) == (NOT A) or (NOT b) or (NOT c),
			// and disjunctions can be algebraized by using products.
			App.debug("Thesis reductio ad absurdum (denied statement), product of factors:");
			Polynomial spoly = new Polynomial(1);
			Variable z = new Variable();
			// It is OK to use the same variable for each factor since it is enough
			// to find one counterexample only for one of the theses.
			// See http://link.springer.com/article/10.1007%2Fs10817-009-9133-x
			// Appendix, Proposition 6 and Corollary 2 to read more on this.
			// FIXME: this always introduces an extra variable, shouldn't do.
			for (int i = 0; i < nStatements; ++i) {
				Polynomial factor = (statements[i][statements[i].length - 1]);
				App.debug("(" + factor + ")*" + z + "-1");
				factor = factor.multiply(new Polynomial(z)).subtract(new Polynomial(1));
				spoly = spoly.multiply(factor);
			}
			eqSystem[k] = spoly;
			App.debug("that is,");
			App.debug((k + 1) + ". " + spoly);

			if (prover.isReturnExtraNDGs()) {

					Set<Set<Polynomial>> eliminationIdeal;
					NDGDetector ndgd = new NDGDetector(prover, substitutions);

					boolean found = false;
					int permutation = 0;
					int MAX_PERMUTATIONS = 1; // Giac cannot permute the
												// variables at the moment.
					if (App.singularWS != null && App.singularWS.isAvailable()) {
						// TODO: Limit MAX_PERMUTATIONS to
						// (#freevars-#substitutes)! to prevent unneeded
						// computations:
						MAX_PERMUTATIONS = 8; // intuitively set, see
												// Polynomial.java for more on
												// info (Pappus6 will work with
												// 7, too)
						// Pappus6 is at
						// http://www.tube.geogebra.org/student/m57255
					}
					while (!found && permutation < MAX_PERMUTATIONS) {

						eliminationIdeal = Polynomial.eliminate(eqSystem,
								substitutions, statement.getKernel(),
								permutation++);
						if (eliminationIdeal == null) {
							return ProofResult.UNKNOWN;
						}

						Iterator<Set<Polynomial>> ndgSet = eliminationIdeal
								.iterator();

						List<Set<GeoPoint>> xEqualSet = new ArrayList(
								new HashSet<GeoPoint>());
						List<Set<GeoPoint>> yEqualSet = new ArrayList(
								new HashSet<GeoPoint>());
						boolean xyRewrite = (eliminationIdeal.size() == 2);

						List<NDGCondition> bestNdgSet = new ArrayList<NDGCondition>();
						double bestScore = Double.POSITIVE_INFINITY;
						int ndgI = 0;
						while (ndgSet.hasNext()) {
							ndgI++;
							App.debug("Considering NDG " + ndgI + "...");
							List<NDGCondition> ndgcl = new ArrayList<NDGCondition>();
							double score = 0.0;
							// All NDGs must be translatable into human readable
							// form.
							boolean readable = true;
							Set<Polynomial> thisNdgSet = ndgSet.next();
							Iterator<Polynomial> ndg = thisNdgSet.iterator();
							while (ndg.hasNext() && readable) {
								Polynomial poly = ndg.next();
								if (poly.isZero()) {

									// Here we know that the statement is not generally true.
									App.debug("Statement is NOT GENERALLY TRUE");

									/*								
									// But it is possible that the statement is not generally false, either.
									// So we should check the negative statement also.
									App.debug("Checking the negative statement to decide if the statement is generally false or not:");
									
									// If there were more than one theses, we need to add 1 extra polynomial for each,
									// because we used the product of the last statement of each, but now we need to add all of
									// them to the system.
									if (nStatements != 1) {
										Polynomial[] eqSystem2 = new Polynomial[nHypotheses + nNdgConditions + nExtraPolysNonDenied + nStatements];
										for (int i = 0; i < nHypotheses + nNdgConditions + nExtraPolysNonDenied; ++i) {
											eqSystem2[i] = eqSystem[i];
											}
										eqSystem = eqSystem2;
										}
									int j = nHypotheses + nNdgConditions + nExtraPolysNonDenied;
									// Add the last (earlier: denied) theses to the system as simple equations (in non-denied form). 
									for (int i = 0; i < nStatements; ++i) {
										eqSystem[j + i] = statements[i][statements[i].length - 1];
									}
									// Computing elimination ideal. If this elimination is not zero, then the statement is generally false.
									eliminationIdeal = Polynomial.eliminate(eqSystem,
										substitutions, statement.getKernel(),
										permutation++);
									if (eliminationIdeal == null) {
											App.debug("Statement is NOT GENERALLY FALSE => UNKNOWN (1)");
											return ProofResult.UNKNOWN;
										}
									ndgSet = eliminationIdeal.iterator();
									while (ndgSet.hasNext()) {
										thisNdgSet = ndgSet.next();
										ndg = thisNdgSet.iterator();
										while (ndg.hasNext()) {
											poly = ndg.next();
											if (poly.isZero()) {
												App.debug("Statement is NOT GENERALLY FALSE either => UNKNOWN (2)");
												return ProofResult.UNKNOWN;
											}
										}
									}
								App.debug("Statement is GENERALLY FALSE");
								*/
									if (interpretFalseAsUndefined) {
										App.debug("Interpreting FALSE as UNKNOWN");
										return ProofResult.UNKNOWN;
									}
									return ProofResult.FALSE;
								}
									
							if (!poly.isConstant()) {
								NDGCondition ndgc = ndgd.detect(poly);
								if (ndgc == null)
									readable = false;
								else {
									// Check if this elimination ideal
									// equals to {xM-xN,yM-yN}:
									xyRewrite = (xyRewrite && thisNdgSet.size() == 1);
										// Note that in some cases the CAS may
										// return (xM-xN)*(-1) which
										// consists of two factors, so
										// thisNdgSet.size() == 1 will fail.
										// Until now there is no experience of
										// such behavior for such
										// simple ideals, so maybe this check is
										// OK.
									if (xyRewrite) {
										if (ndgc.getCondition().equals("xAreEqual")) {
											Set<GeoPoint> points = new HashSet<GeoPoint>();
											points.add((GeoPoint) ndgc.getGeos()[0]);
											points.add((GeoPoint) ndgc.getGeos()[1]);
											xEqualSet.add(points);
											}
										if (ndgc.getCondition().equals("yAreEqual")) {
											Set<GeoPoint> points = new HashSet<GeoPoint>();
											points.add((GeoPoint) ndgc.getGeos()[0]);
											points.add((GeoPoint) ndgc.getGeos()[1]);
											yEqualSet.add(points);
											}
										if (xEqualSet.size() == 1 && xEqualSet.equals(yEqualSet)) {
											// If yes, set the condition to
											// AreEqual(M,N) and readable
											// enough:
											ndgc.setCondition("AreEqual");
											ndgc.setReadability(0.5);
											}
										}

									ndgcl.add(ndgc);
									score += ndgc.getReadability();
								}
							}
						}
						// Now we take the set if the conditions are
						// readable and the set is the current best.
						// TODO: Here we should simplify the NDGs, i.e. if
						// one of them is a logical
						// consequence of others, then it should be
						// eliminated.
						if (readable && score < bestScore) {
							App.debug("Found a better NDG score (" + score
								+ ") than " + bestScore);
							bestScore = score;
							bestNdgSet = ndgcl;
							found = true;
						} else {
							if (readable) {
								App.debug("Not better than previous NDG score ("
									+ bestScore + "), this is " + score);
								} else {
								App.debug("...unreadable");
								}
							}
						}
					if (found) {
						Iterator<NDGCondition> ndgc = bestNdgSet.iterator();
						while (ndgc.hasNext()) {
							prover.addNDGcondition(ndgc.next());
							}
						}
					}
				// No readable proof was found, search for another
				// prover to make a better job:
				if (!found) {
					App.debug("Statement is TRUE but NDGs are UNREADABLE");
					return ProofResult.TRUE_NDG_UNREADABLE;
					}
				} else {
					Boolean solvable = Polynomial.solvable(eqSystem,
							substitutions, statement.getKernel(),
							ProverSettings.transcext);
					if (solvable == null) {
						// Prover returned with no success, search for another
						// prover:
						App.debug("Unsuccessful run, statement is UNKNOWN at the moment");
						return ProofResult.UNKNOWN;
					}
					if (solvable) {
						if (!ProverSettings.transcext) {
							// We cannot reliably tell if the statement is
							// really false:
							App.debug("No transcext support, system is solvable, statement is UNKNOWN");
							return ProofResult.UNKNOWN;
						}
						// Here we know that the statement is not generally true.
						// But it is possible that the statement is not generally false, either.
						// So we check the negative statement also.
						//spoly = lastpoly;
						App.debug("Statement is NOT GENERALLY TRUE");
						
						/*
						App.debug("Checking the negative statement to decide if the statement is generally false or not:");
						
						// If there were more than one theses, we need to add 1 extra polynomial for each,
						// because we used the product of the last statement of each, but now we need to add all of
						// them to the system.
						if (nStatements != 1) {
							Polynomial[] eqSystem2 = new Polynomial[nHypotheses + nNdgConditions + nExtraPolysNonDenied + nStatements];
							for (int i = 0; i < nHypotheses + nNdgConditions + nExtraPolysNonDenied; ++i) {
								eqSystem2[i] = eqSystem[i];
								}
							eqSystem = eqSystem2;
							}
						int j = nHypotheses + nNdgConditions + nExtraPolysNonDenied;
						// Add the last (earlier: denied) theses to the system as simple equations (in non-denied form). 
						for (int i = 0; i < nStatements; ++i) {
							eqSystem[j + i] = statements[i][statements[i].length - 1];
						}

						Boolean negsolvable = Polynomial.solvable(eqSystem,
								substitutions, statement.getKernel(),
								ProverSettings.transcext);
						if (negsolvable) {
							App.debug("Statement is NOT GENERALLY FALSE either => UNKNOWN");
							return ProofResult.UNKNOWN;
						}
						App.debug("Statement is GENERALLY FALSE");
						*/
					if (interpretFalseAsUndefined && !interpretTrueAsUndefined) {
							App.debug("Interpreting FALSE as UNKNOWN");
							return ProofResult.UNKNOWN;
						}
						return ProofResult.FALSE;
					}
				}
			if (interpretTrueAsUndefined) {
				App.debug("Interpreting TRUE as UNKNOWN");
				return ProofResult.UNKNOWN;
			}
			App.debug("Statement is GENERALLY TRUE");
			return ProofResult.TRUE;

		} catch (NoSymbolicParametersException e) {
			App.debug("Unsuccessful run, statement is UNKNOWN at the moment");
			return ProofResult.UNKNOWN;
		}
	}

}
