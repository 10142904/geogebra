package geogebra.common.util;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.algos.AlgoMacro;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.Test;
import geogebra.common.kernel.prover.AlgoAreEqual;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author Christoph
 * 
 */
public class Assignment {

	/**
	 * The Result of the Assignment
	 */
	public enum Result {
		/**
		 * The assignment is CORRECT
		 */
		CORRECT,
		/**
		 * The assignment is WRONG and we can't tell why
		 */
		WRONG,
		/**
		 * There are not enough input geos, so we cannot check
		 */
		NOT_ENOUGH_INPUTS,
		/**
		 * We have enough input geos, but one or more are of the wrong type
		 */
		WRONG_INPUT_TYPES,
		/**
		 * There is no output geo matching our macro
		 */
		WRONG_OUTPUT_TYPE,
		/**
		 * The assignment was correct in the first place but wrong after
		 * randomization
		 */
		WRONG_AFTER_RANDOMIZE,
		/**
		 * The assignment could not be checked
		 */
		UNKNOWN,
		/**
		 * There are to many independent Inputs
		 */
		INPUT_AMBIGUOUS
	}

	private Macro macro;
	private Kernel kernel;

	public Assignment(App app) {
		kernel = app.getKernel();
		ArrayList<Macro> appMacros = kernel.getAllMacros();
		if (!appMacros.isEmpty()) {
			macro = appMacros.get(0);
		}
	}

	/**
	 * Exhaustive Testing of the Assignment
	 * 
	 * TODO: There are some assumption on the construction which are not checked
	 * 
	 * @return {@link Result} of the check
	 */
	public Result checkAssignment() {
		Result res = Result.UNKNOWN;
		TreeSet<GeoElement> possibleInputGeos = new TreeSet<GeoElement>();
		TreeSet<GeoElement> possibleOutputGeos = new TreeSet<GeoElement>();
		Construction cons = kernel.getConstruction();

		// find all possible inputgeos and all outputgeos that match the type of
		// the macro
		TreeSet<GeoElement> sortedSet = cons.getGeoSetNameDescriptionOrder();
		Iterator<GeoElement> it = sortedSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isIndependent() || geo.hasChildren()) {
				for (GeoElement macroIn : macro.getMacroInput()) {
					if (geo.getClass().equals(macroIn.getClass())) {
						possibleInputGeos.add(geo);
					}
				}
			}
			if (!geo.isIndependent()) {
				for (GeoElement macroOut : macro.getMacroOutput()) {
					if (macroOut.getClass().equals(geo.getClass())) {
						possibleOutputGeos.add(geo);
					}
				}
			}
		}
		GeoElement[] inputGeos = new GeoElement[possibleInputGeos.size()];
		possibleInputGeos.toArray(inputGeos);

		boolean typesOK = true; // we assume that types are OK

		Test[] inputTypes = macro.getInputTypes();

		if (macro.getInputTypes().length > possibleInputGeos.size()) {
			res = Result.NOT_ENOUGH_INPUTS;
		} else if (possibleOutputGeos.isEmpty()) {
			res = Result.WRONG_OUTPUT_TYPE;
			// } else if (macro.getInputTypes().length <
			// possibleInputGeos.size()) {
			// res = Result.INPUT_AMBIGUOUS;
		} else { // if (inputTypes.length <= possibleInputGeos.size()) {
			GeoElement[] input = new GeoElement[inputTypes.length];

			PermutationOfGeOElementsUtil permutationUtil = new PermutationOfGeOElementsUtil(
					inputGeos, inputTypes.length);
			GeoElement[] inputNextPermutation = permutationUtil.next();
			while (inputNextPermutation != null
					&& (res == Result.WRONG || res == Result.UNKNOWN)) {
				// System.out.println(Arrays.toString(inputNextPermutation));
				int i = 0;
				// we assumed types are OK in the beginning
				typesOK = true;
				while (i < inputNextPermutation.length && typesOK) {
					if (inputTypes[i].check(inputNextPermutation[i])) {
						input[i] = inputNextPermutation[i];
						typesOK = true;
					} else {
						typesOK = false;
					}
					i++;
				}
				if (typesOK) {
					res = checkEqualityOfGeos(input, new ArrayList<GeoElement>(
							possibleOutputGeos), cons);
				}

				inputNextPermutation = permutationUtil.next();
			}

			// TODO: Prove!

			if (res == Result.UNKNOWN) {
				if (typesOK) {
					res = Result.WRONG;
				} else {
					res = Result.WRONG_INPUT_TYPES;
				}
			}
		}

		return res;
	}

	private Result checkEqualityOfGeos(GeoElement[] input,
			ArrayList<GeoElement> possibleOutputGeos, Construction cons) {
		Result solved = Result.UNKNOWN;
		// String[] label = { "" };
		AlgoMacro algoMacro = new AlgoMacro(cons, null, macro, input);
		GeoElement[] macroOutput = algoMacro.getOutput();

		GeoElement saveInput;

		// if (macroOutput[0] instanceof GeoPolygon) {
		int i = 0;
		while (i < possibleOutputGeos.size()
				&& (solved == Result.UNKNOWN || solved == Result.WRONG)) {
			AlgoAreEqual algoEqual = new AlgoAreEqual(cons, "", macroOutput[0],
					possibleOutputGeos.get(i));
			solved = algoEqual.getResult().getBoolean() ? Result.CORRECT
					: Result.WRONG;
			int j = 0;
			while (j < input.length && solved == Result.CORRECT) {
				if (input[j].isRandomizable()) {
					saveInput = input[j].copy();
					input[j].randomizeForProbabilisticChecking();
					input[j].updateCascade();
					solved = algoEqual.getResult().getBoolean() ? Result.CORRECT
							: Result.WRONG_AFTER_RANDOMIZE;
					input[j].set(saveInput);
					input[j].updateCascade();
				}
				j++;
			}
			i++;
		}
		algoMacro.remove();
		return solved;
	}
}

// Eyal Schneider
// http://stackoverflow.com/a/2799190
/**
 * Utility Class to permute the array of GeoElements
 * 
 * @author Eyal Schneider, http://stackoverflow.com/a/2799190
 * @author Adaption: Christoph Reinisch
 */
class PermutationOfGeOElementsUtil {
	private GeoElement[] arr;
	private int[] permSwappings;

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr) {
		this(arr, arr.length);
	}

	/**
	 * @param arr
	 *            the Array with the Elements to be permuted
	 * @param permSize
	 *            the Elements k < arr.length of the array you need to permute
	 */
	public PermutationOfGeOElementsUtil(GeoElement[] arr, int permSize) {

//		this.arr = arr.clone();
		this.arr = new GeoElement[arr.length];
		System.arraycopy(arr, 0, this.arr, 0, arr.length);
		this.permSwappings = new int[permSize];
		for (int i = 0; i < permSwappings.length; i++) {
			permSwappings[i] = i;
		}
	}

	/**
	 * @return the next permutation of the array if exists, null otherwise
	 */
	public GeoElement[] next() {
		if (arr == null) {
			return null;
		}

		GeoElement[] res = new GeoElement[permSwappings.length];
		System.arraycopy(arr, 0, res, 0, permSwappings.length);
		// GeoElement[] res = Arrays.copyOf(arr, permSwappings.length);

		// Prepare next
		int i = permSwappings.length - 1;
		while (i >= 0 && permSwappings[i] == arr.length - 1) {
			swap(i, permSwappings[i]); // Undo the swap represented by
										// permSwappings[i]
			permSwappings[i] = i;
			i--;
		}

		if (i < 0) {
			arr = null;
		} else {
			int prev = permSwappings[i];
			swap(i, prev);
			int next = prev + 1;
			permSwappings[i] = next;
			swap(i, next);
		}

		return res;
	}

	private void swap(int i, int j) {
		GeoElement tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}