package geogebra.common.kernel.prover;

import geogebra.common.main.AbstractApplication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This is a simple polynomial class for polynomials with arbitrary many
 * variables.
 * 
 * @author Simon Weitzhofer
 * 
 */
public class Polynomial implements Comparable<Polynomial> {
	private TreeMap<Term, Integer> terms;

	/**
	 * Creates the 0 polynomial
	 */
	public Polynomial() {
		terms = new TreeMap<Term, Integer>();
	}

	/**
	 * Copies a polynomial
	 * 
	 * @param poly
	 *            the polynomial to copy
	 */
	public Polynomial(final Polynomial poly) {
		terms = new TreeMap<Term, Integer>(poly.getTerms());
	}

	private Polynomial(final TreeMap<Term, Integer> terms) {
		this.terms = terms;
	}

	/**
	 * Getter for the map which contains the terms and the according
	 * coefficients.
	 * 
	 * @return the map
	 */
	public TreeMap<Term, Integer> getTerms() {
		return terms;
	}

	/**
	 * Creates a constant polynomial.
	 * 
	 * @param coeff
	 *            the constant
	 */
	public Polynomial(final int coeff) {
		this(coeff, new Term());
	}

	/**
	 * Creates a polynomial which contains only one variable
	 * 
	 * @param fv
	 *            the variable
	 */
	public Polynomial(final Variable fv) {
		this();
		terms.put(new Term(fv), 1);
	}

	/**
	 * Creates the polynomial coeff*variable
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param variable
	 *            the variable
	 */
	public Polynomial(final int coeff, final Variable variable) {
		this();
		terms.put(new Term(variable), coeff);
	}

	/**
	 * Creates the polynomial coeff*(variable^power)
	 * 
	 * @param coeff
	 *            The coefficient
	 * @param variable
	 *            the variable
	 * @param power
	 *            the exponent
	 */
	public Polynomial(final int coeff, final Variable variable,
			final int power) {
		this();
		terms.put(new Term(variable, power), coeff);
	}

	/**
	 * Creates the polynomial which contains only one term
	 * 
	 * @param t
	 *            the term
	 */
	public Polynomial(final Term t) {
		this();
		terms.put(t, 1);
	}

	/**
	 * Creates the polynomial coeff*t
	 * 
	 * @param coeff
	 *            the coefficient
	 * @param t
	 *            the term
	 */
	public Polynomial(final int coeff, final Term t) {
		this();
		terms.put(t, coeff);
	}

	/**
	 * Converts a String to a Polynomial
	 * @param s the input string 
     * @author Damien Desfontaines
	 */
	public Polynomial(String s) {
		AbstractApplication.debug("Constructing poly from " + s.length() + " length String");
        // s has the form "-2*x^2*y + 5*x^3 - 2"
        // Firstly, we remove all whitespace.
        s = s.replace(" ","");
        // We verify that s has a "good" form, to avoid bugs
        String regex = "((-?\\w+)(\\^[0-9]+)?(\\*(-?\\w+)(\\^[0-9]+)?)*)([\\+-]((-?\\w+)(\\^[0-9]+)?(\\*(-?\\w+)(\\^[0-9]+)?)*))*";
        if (! s.matches(regex)) {
            AbstractApplication.error("Polynomial of unexpected form : ".concat(s));
        }
        // Then, we transform all minus signs between terms into "+-", to
        // separate terms more easily.
        // The 4 following lines are ugly. There must be a better way to do this.
        s = s.replace("*-","*~");
        s = s.replace("+-","-");
        s = s.replace("-","+-");
        s = s.replace("~","-");
        // Avoid the "'-' in first position" bug
        if (s.length() != 0 && s.charAt(0) == '+') {
            s = s.substring(1);
        }
        // We can now separate our string into his terms
        String[] termsOfS = s.split("[+]");
        Polynomial sum = new Polynomial(0);
        for (int i = 0 ; i < termsOfS.length ; i++) {
            String[] factors = termsOfS[i].split("[*]");
            Polynomial product = new Polynomial(1);
            for (int j = 0 ; j < factors.length ; j++) {
                // If factors[j] is of form x^n
                if (factors[j].contains("^")) {
                    String[] factorMembers = factors[j].split("\\^");
                    String signedVar = factorMembers[0];
                    Variable variable;
                    int coeff = 1;
                    if (signedVar.charAt(0) == '-') {
                    	variable = new Variable(signedVar.substring(1));
                    	coeff = -1;
                    } else
                    	variable = new Variable(signedVar);
                    int exponent = Integer.parseInt(factorMembers[1]);
                    Polynomial factor = new Polynomial(coeff,variable,exponent);
                    product = product.multiply(factor);
                }
                // If factors[j] is a number
                else if (factors[j].matches("-?[0-9]+")) {
                    Polynomial factor = new Polynomial(Integer.parseInt(factors[j]));
                    product = product.multiply(factor);
                }
                // If factors[j] is a variable
                else if (factors[j].matches("\\w+")) {
                    Polynomial factor = new Polynomial(new Variable(factors[j]));
                    product = product.multiply(factor);
                }
                // If factors[j] is the negation of a variable
                else if (factors[j].matches("-\\w+")) {
                    Polynomial factor = new Polynomial(-1,new Variable(factors[j].substring(1)));
                    product = product.multiply(factor);
                }
                else {
                    AbstractApplication.error("Input of unexpected form : ".concat(factors[j]));
                    product = product.multiply(new Polynomial());
                }
            }
            sum = sum.add(product);
        }
		terms = new TreeMap<Term, Integer>(sum.getTerms());
	}
	
	/**
	 * Returns the sum of the polynomial plus another polynomial.
	 * 
	 * @param poly
	 *            the polynomial to add
	 * @return the sum
	 */
	public Polynomial add(final Polynomial poly) {
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>(terms);
		TreeMap<Term, Integer> terms2 = poly.getTerms();
		Iterator<Term> it = terms2.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			if (terms.containsKey(t)) {
				int coefficient = terms.get(t) + terms2.get(t);
				if (coefficient == 0) {
					result.remove(t);
				} else {
					result.put(t, terms.get(t) + terms2.get(t));
				}
			} else {
				result.put(t, terms2.get(t));
			}
		}
		return new Polynomial(result);
	}

	/**
	 * Calculates the additive inverse of the polynomial
	 * 
	 * @return the negation of the polynomial
	 */
	public Polynomial negate() {
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>();
		Iterator<Term> it = terms.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			result.put(t, 0 - terms.get(t));
		}
		return new Polynomial(result);
	}

	/**
	 * Subtracts another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is subtracted
	 * @return the difference
	 */
	public Polynomial subtract(final Polynomial poly) {
		return add(poly.negate());
	}

	/**
	 * Multiplies the polynomial with another polynomial
	 * 
	 * @param poly
	 *            the polynomial which is multiplied
	 * @return the product
	 */
	public Polynomial multiply(final Polynomial poly) {

		/*
		if (AbstractApplication.singularWS != null && AbstractApplication.singularWS.isAvailable()) {
			if (poly.toString().length()>100 && this.toString().length()>100) {
				String singularMultiplicationProgram = getSingularMultiplication("rr", poly, this);
				AbstractApplication.trace(singularMultiplicationProgram.length() + " bytes -> singular");
				String singularMultiplication = AbstractApplication.singularWS.directCommand(singularMultiplicationProgram);
				return new Polynomial(singularMultiplication);
			}
		}
		*/
		
		TreeMap<Term, Integer> result = new TreeMap<Term, Integer>();
		TreeMap<Term, Integer> terms2 = poly.getTerms();
		Iterator<Term> it1 = terms.keySet().iterator();
		while (it1.hasNext()) {
			Term t1 = it1.next();
			Iterator<Term> it2 = terms2.keySet().iterator();
			while (it2.hasNext()) {
				Term t2 = it2.next();
				Term product = t1.times(t2);
				int productCoefficient = terms.get(t1) * terms2.get(t2);
				if (result.containsKey(product)) {
					int sum = result.get(product) + productCoefficient;
					if (sum == 0) {
						result.remove(product);
					} else {
						result.put(product, result.get(product)
								+ productCoefficient);
					}
				} else {
					result.put(product, productCoefficient);
				}
			}

		}
		return new Polynomial(result);
	}

	public int compareTo(Polynomial poly) {
		if (this==poly){
			return 0;
		}

		TreeMap<Term, Integer> polyVars=poly.getTerms();
		if (polyVars.isEmpty()) {
			if (terms.isEmpty()) {
				return 0;
			}
			return 1;
		}
		if (terms.isEmpty()) {
			return -1;
		}
		
		Term termsLastKey=terms.lastKey(),
				polyVarsLastKey=polyVars.lastKey();

		int compare = termsLastKey.compareTo(polyVarsLastKey);

		if (compare == 0) {
			compare = terms.get(termsLastKey).compareTo(polyVars.get(polyVarsLastKey));
		}

		if (compare != 0) {
			return compare;
		}
		
		do {
			SortedMap<Term, Integer> termsSub = terms.headMap(termsLastKey);
			SortedMap<Term, Integer> oSub = polyVars.headMap(polyVarsLastKey);
			if (termsSub.isEmpty()) {
				if (oSub.isEmpty()) {
					return 0;
				}
				return -1;
			}
			if (oSub.isEmpty()) {
				return 1;
			}
			termsLastKey=termsSub.lastKey();
			polyVarsLastKey=oSub.lastKey();
			compare = termsLastKey.compareTo(polyVarsLastKey);
			if (compare == 0) {
				compare = termsSub.get(termsLastKey).compareTo(
						oSub.get(polyVarsLastKey));
			}
		} while (compare == 0);

		return compare;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Term> it = terms.keySet().iterator();
		if (!it.hasNext()) {
			return "0";
		}
		while (it.hasNext()) {
			Term t = it.next();
			int c = terms.get(t);
			if (!t.getTerm().isEmpty()) {
				if (c != 1)
					sb.append(c + "*");
				sb.append(t);
			}
			else
				sb.append(c);
			sb.append('+');
		}
		return sb.substring(0, sb.length() - 1); // removing closing "+"
	}
	
	/**
	 * The set of the variables in this polynomial
	 * @return the set of variables
	 */
	public HashSet<Variable> getVars() {
		HashSet<Variable> v = new HashSet<Variable>();
		Iterator<Term> it = terms.keySet().iterator();
		while (it.hasNext()) {
			Term t = it.next();
			v.addAll(t.getVars());
		}
		return v;
	}

	/**
	 * The set of the variables in the given polynomials
	 * @param polys the polynomials
	 * @return the set of variables
	 */
	public static HashSet<Variable> getVars(Polynomial[] polys) {
		HashSet<Variable> v = new HashSet<Variable>();
		int polysLength = 0;
		if (polys != null)
			polysLength = polys.length;
		for (int i=0; i<polysLength; ++i) {
			HashSet<Variable> vars = polys[i].getVars();
			if (vars != null)
				v.addAll(vars);
		}
		return v;
	}
	
	/**
	 * Creates a comma separated list of the variables in the given polynomials
	 * @param polys the polynomials
	 * @param extraVars (maybe) extra variables (typically substituted variables)
	 * @return the comma separated list
	 */
	public static String getVarsAsCommaSeparatedString(Polynomial[] polys, HashSet<Variable> extraVars) {
		StringBuilder sb = new StringBuilder();
		HashSet<Variable> vars = getVars(polys);
		if (extraVars != null)
			vars.addAll(extraVars);
		Iterator<Variable> it = vars.iterator();
		while (it.hasNext()) {
			Variable fv = it.next();
			sb.append("," + fv);
		}
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	
	/**
	 * Creates a comma separated list of the given polynomials
	 * @param polys the polynomials
	 * @return the comma separated list
	 */
	public static String getPolysAsCommaSeparatedString(Polynomial[] polys) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<polys.length; ++i)
			sb.append("," + polys[i].toString());
		if (sb.length()>0)
			return sb.substring(1); // removing first "," character
		return "";
	}
	
	/**
	 * Creates a Singular program for creating a ring to work with two
	 * polynomials, and multiply them; adds a closing ";" 
	 * @param ringVariable variable name for the ring in Singular
	 * @param p1 first polynomial
	 * @param p2 second polynomial
	 * @return the Singular program code
	 */
	public String getSingularMultiplication(String ringVariable, Polynomial p1, Polynomial p2) {
		String vars = getVarsAsCommaSeparatedString(new Polynomial[] {p1, p2}, null);
		if (vars != "")
			return "ring " + ringVariable + "=0,(" 
				+ vars
				+ "),dp;" // ring definition in Singular
				
				+ "short=0;" // switching off short output
				
				+ "(" + p1.toString() + ")"
				+ "*"
				+ "(" + p2.toString() + ");"; // the multiplication command
		return p1.toString() + "*" + p2.toString() + ";";
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * lying on the same line. 
	 * @param fv1 x-coordinate of the first point
	 * @param fv2 y-coordinate of the first point
	 * @param fv3 x-coordinate of the second point
	 * @param fv4 y-coordinate of the second point
	 * @param fv5 x-coordinate of the third point
	 * @param fv6 y-coordinate of the third point
	 * @return the polynomial
	 */
	public static Polynomial collinear(Variable fv1, Variable fv2, Variable fv3, 
			Variable fv4, Variable fv5, Variable fv6) {
		// a*d-b*c:
		Polynomial a = new Polynomial(fv1);
		Polynomial b = new Polynomial(fv2);
		Polynomial c = new Polynomial(fv3);
		Polynomial d = new Polynomial(fv4);
		Polynomial e = new Polynomial(fv5);
		Polynomial f = new Polynomial(fv6);
		
		Polynomial ret = a.multiply(d).subtract(b.multiply(c))
				// + e*(b-d)
				.add(e.multiply(b.subtract(d)))
				// - f*(a-c)
				.subtract(f.multiply(a.subtract(c)));
		return ret;
	}
	
	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are perpendicular, i.e. AB is perpendicular to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static Polynomial perpendicular(Variable v1, Variable v2, Variable v3, 
			Variable v4, Variable v5, Variable v6, Variable v7, Variable v8) {

		Polynomial a1 = new Polynomial(v1);
		Polynomial a2 = new Polynomial(v2);
		Polynomial b1 = new Polynomial(v3);
		Polynomial b2 = new Polynomial(v4);
		Polynomial c1 = new Polynomial(v5);
		Polynomial c2 = new Polynomial(v6);
		Polynomial d1 = new Polynomial(v7);
		Polynomial d2 = new Polynomial(v8);
		
		// (a1-b1)*(c1-d1)+(a2-b2)*(c2-d2)
		Polynomial ret = ((a1.subtract(b1)).multiply(c1.subtract(d1)))
				.add((a2.subtract(b2)).multiply(c2.subtract(d2)));
		return ret;
	}

	/**
	 * Creates a polynomial which describes the input coordinates as points
	 * are parallel, i.e. AB is parallel to CD. 
	 * @param v1 x-coordinate of the first point (A)
	 * @param v2 y-coordinate of the first point (A)
	 * @param v3 x-coordinate of the second point (B)
	 * @param v4 y-coordinate of the second point (B)
	 * @param v5 x-coordinate of the third point (C)
	 * @param v6 y-coordinate of the third point (C)
	 * @param v7 x-coordinate of the fourth point (D)
	 * @param v8 y-coordinate of the fourth point (D)
	 * @return the polynomial
	 */
	public static Polynomial parallel(Variable v1, Variable v2, Variable v3, 
			Variable v4, Variable v5, Variable v6, Variable v7, Variable v8) {

		Polynomial a1 = new Polynomial(v1);
		Polynomial a2 = new Polynomial(v2);
		Polynomial b1 = new Polynomial(v3);
		Polynomial b2 = new Polynomial(v4);
		Polynomial c1 = new Polynomial(v5);
		Polynomial c2 = new Polynomial(v6);
		Polynomial d1 = new Polynomial(v7);
		Polynomial d2 = new Polynomial(v8);

		// (a1-b1)*(c2-d2)-(a2-b2)*(c1-d1)
		Polynomial ret = ((a1.subtract(b1)).multiply(c2.subtract(d2)))
				.subtract((a2.subtract(b2)).multiply(c1.subtract(d1)));
		return ret;
	}
	
	/**
	 * Calculates the determinant of a 4 times 4 matrix
	 * @param matrix matrix
	 * @return the determinant
	 */
	public static Polynomial det4(final Polynomial[][] matrix){
		return matrix[0][3].multiply(matrix[1][2].multiply(matrix[2][1].multiply(matrix[3][0]))).subtract(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][0])).add(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][0])).subtract(
				matrix[0][3].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][2].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][2]).multiply(matrix[3][1])).subtract(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][3]).multiply(matrix[3][1])).add(
				matrix[0][3].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][1].multiply(matrix[1][3]).multiply(matrix[2][0]).multiply(matrix[3][2])).subtract(
				matrix[0][3].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][0].multiply(matrix[1][3]).multiply(matrix[2][1]).multiply(matrix[3][2])).add(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][3]).multiply(matrix[3][2])).subtract(
				matrix[0][2].multiply(matrix[1][1]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][1].multiply(matrix[1][2]).multiply(matrix[2][0]).multiply(matrix[3][3])).add(
				matrix[0][2].multiply(matrix[1][0]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][0].multiply(matrix[1][2]).multiply(matrix[2][1]).multiply(matrix[3][3])).subtract(
				matrix[0][1].multiply(matrix[1][0]).multiply(matrix[2][2]).multiply(matrix[3][3])).add(
				matrix[0][0].multiply(matrix[1][1]).multiply(matrix[2][2]).multiply(matrix[3][3]));
	}
	
	/**
	 * 
	 * Calculates the cross product of two vectors of dimension three.
	 * @param a the first vector
	 * @param b the second vector
	 * @return the cross product of the two vectors
	 */
	public static Polynomial[] crossProduct(Polynomial[] a,
			Polynomial[] b) {
		Polynomial[] result=new Polynomial[3];
		result[0]=(a[1].multiply(b[2])).subtract(a[2].multiply(b[1]));
		result[1]=(a[2].multiply(b[0])).subtract(a[0].multiply(b[2]));
		result[2]=(a[0].multiply(b[1])).subtract(a[1].multiply(b[0]));
		return result;
	}

	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Polynomial) {
			return this.compareTo((Polynomial) o) == 0;
		}
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}

	/**
	 * Tests if the Polynomial is the zero polynomial
	 * @return true if the polynomial is zero false otherwise
	 */
	public boolean isZero() {
		return terms.isEmpty();
	}

	/**
	 * Converts substitutions to Singular strings
	 * @param subst input as a HashMap
	 * @return the parameters for Singular (e.g. "v1,0,v2,0,v3,0,v4,1")
	 */
	static String substitutionsString(HashMap<Variable,Integer> subst) {
		String ret = "";
		Iterator<Variable> it = subst.keySet().iterator();
		while (it.hasNext()) {
			Variable v = it.next();
			ret += "," + v.toString() + "," + subst.get(v);
		}
		if (ret.length()>0)
			return ret.substring(1);
		return "";
	}
	
	/**
	 * Creates a Singular program for creating a ring to work with several
	 * polynomials, and returns if the equation system has a solution. Uses
	 * the Groebner basis w.r.t. the revgradlex order.
	 * @param ringVariable variable name for the ring in Singular
	 * @param idealVariable variable name for the ideal in Singular
	 * @param polys array of polynomials
	 * @param substitutions HashMap with variables and values, e.g. {v1->0},{v2->1}
	 * @return the Singular program code
	 */
	public static String getSingularGroebnerSolvable(String ringVariable, String idealVariable, Polynomial[] polys,
			HashMap<Variable,Integer> substitutions) {
		HashSet<Variable> substVars = null;
		String substCommand = "";
		if (substitutions != null) {
			substVars = new HashSet<Variable>(substitutions.keySet());
			String substParams = substitutionsString(substitutions);
			substCommand = idealVariable + "=subst(" + idealVariable + "," + substParams + ");";
		}
		String ret = "ring " + ringVariable + "=0,(" 
			+ getVarsAsCommaSeparatedString(polys, substVars)
			+ "),dp;" // ring definition in Singular
				
			+ "ideal " + idealVariable + "="
		 	+ getPolysAsCommaSeparatedString(polys) + ";"; // ideal definition in Singular

		ret += substCommand;

		ret += "groebner(" + idealVariable + ")!=1;"; // the Groebner basis calculation command
		return ret;
	}
	
	/**
	 * Decides if an array of polynomials (as a set) gives a solvable equation system
	 * on the field of the complex numbers.
	 * @param polys the array of polynomials
	 * @param substitutions some variables which are to be evaluated with exact numbers
	 * @return yes if solvable, no if no solutions, or null (if cannot decide)
	 */
	public static Boolean solvable(Polynomial[] polys, HashMap<Variable,Integer> substitutions) {
		if (AbstractApplication.singularWS != null && AbstractApplication.singularWS.isAvailable()) {
			String singularSolvableProgram = getSingularGroebnerSolvable("r", "i", polys, substitutions);
			if (singularSolvableProgram.length()>500)
				AbstractApplication.debug(singularSolvableProgram.length() + " bytes -> singular");
			else
				AbstractApplication.debug(singularSolvableProgram + " -> singular");
			String singularSolvable = AbstractApplication.singularWS.directCommand(singularSolvableProgram);
			if (singularSolvable.length()>500)
				AbstractApplication.debug("singular -> " + singularSolvable.length() + " bytes");
			else
				AbstractApplication.debug("singular -> " + singularSolvable);
			if ("0".equals(singularSolvable))
				return false; // no solution
			return true; // at least one solution exists
		}
		return null; // cannot decide
	}
	
	/** Returns the square of the input polynomial
	 * @param p input polynomial
	 * @return the square (p*p)
	 */
	public static Polynomial sqr(Polynomial p) {
		return p.multiply(p);
	}

	
	
	/**
	 * Returns the square of the distance of two points
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the square of the distance
	 */
	public static Polynomial sqrDistance(Variable a1, Variable a2, Variable b1, Variable b2) {
		return sqr(new Polynomial(a1).subtract(new Polynomial(b1)))
				.add(sqr(new Polynomial(a2).subtract(new Polynomial(b2))));
	}
	
	/**
	 * Returns if AO=OB, i.e. whether the AOB triangle is isosceles
	 * @param a1 first coordinate of A
	 * @param a2 second coordinate of A
	 * @param o1 first coordinate of O
	 * @param o2 second coordinate of O
	 * @param b1 first coordinate of B
	 * @param b2 second coordinate of B
	 * @return the 0 polynomial if AO=OB
	 */
	public static Polynomial equidistant(Variable a1, Variable a2,
			Variable o1, Variable o2, Variable b1,
			Variable b2) {
		return sqrDistance(a1,a2,o1,o2).subtract(sqrDistance(o1,o2,b1,b2));  
	}
	
}
