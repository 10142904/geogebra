package geogebra.common.cas.giac;

import java.util.Map;
import java.util.TreeMap;

/***
 * IMPORTANT: Every time this file is changed a robot will automatically
 * create a new version of giac.js and modify kickstart.xml for the web.
 */

/***
 * # Command translation table from GeoGebra to giac # e.g. Factor[ 2(x+3) ]
 * is translated to factor( 2*(x+3) ) ###
 */

public class Ggb2giac {
	private static Map<String, String> commandMap = new TreeMap<String, String>();

	/**
	 * @param signature GeoGebra command signature (i.e. "Element.2")
	 * @param casSyntax CAS syntax, parameters as %0,%1
	 */
	public static void p(String signature, String casSyntax) {

		// replace _ with \_ to make sure it's not replaced with "unicode95u"

		commandMap.put(signature, casSyntax.replace("_",  "\\_"));
	}

	/**
	 * @return map signature => syntax
	 */
	public static Map<String,String> getMap() {
		p("Append.2",
				"append(%0,%1)");
		// simplify() to make sure Binomial[n,1] gives n
		p("Binomial.2",
				"simplify(binomial(%0,%1))");
		p("BinomialDist.4",
				"if %3=true then binomial_cdf(%0,%1,%2) else binomial(%0,%1,%2) fi");
		p("Cauchy.3", "normal(1/2+1/pi*atan(((%2)-(%1))/(%0)))");

		// factor over complex rationals
		// [ggbans:=%0] first in case something goes wrong, eg  CFactor[sqrt(21) - 2sqrt(7) x ί + 3sqrt(3) x² ί + 6x³]
		p("CFactor.1","[with_sqrt(0),[ggbans:=%0],[ggbans:=cfactor(ggbans)],with_sqrt(1),ggbans][4]");
		p("CFactor.2","[with_sqrt(0),[ggbans:=%0],[ggbans:=cfactor(ggbans,%1)],with_sqrt(1),ggbans][4]");

		// factor over complex irrationals
		p("CIFactor.1","[with_sqrt(1),[ggbans:=%0],[ggbans:=cfactor(ggbans)],ggbans][3]");
		p("CIFactor.2","[with_sqrt(1),[ggbans:=%0],[ggbans:=cfactor(ggbans,%1)],ggbans][3]");

		p("ChiSquared.2", 
				//"chisquare_cdf(%0,%1)");
				"igamma(%0/2,%1/2,1)");
		p("Coefficients.1",
				"when(is_polynomial(%0),"+
						"coeffs(%0),"+
				"{})");

		p("Coefficients.2", "coeffs(%0,%1)");
		p("CompleteSquare.1",
				"canonical_form(%0)");
		p("CommonDenominator.2", "lcm(denom(%0),denom(%1))");
		p("Covariance.2",
				"covariance(%0,%1)");
		p("Covariance.1",
				"normal(covariance(%0))");
		p("Cross.2", "when(%0[0]='pnt' && size(%0[1])==3,point(cross(%0,%1)),cross(%0,%1))");
		p("ComplexRoot.1", "normal(cZeros(%0,x))");
		p("CSolutions.1",
				"ggbsort([[[ggbans:=0/0],[ggbans:=%0],[ggbvars:=lname(ggbans)]],"+
				"normal(cZeros(%0,when(size(ggbvars)==1,ggbvars[0],x)))][1])");
		p("CSolutions.2",
				"ggbsort(normal(cZeros(%0,%1)))");
		p("CSolve.1",
				"ggbsort([[[ggbans:=0/0],[ggbans:=%0],[ggbvars:=lname(ggbans)]],"+
				"normal(csolve(%0,when(size(ggbvars)==1,ggbvars[0],x)))][1])");
		
		p("CSolve.2", "ggbsort(normal(csolve(%0,%1)))");
		p("Degree.1",
				"degree(%0)");
		p("Degree.2", "degree(%0,%1)");
		p("Denominator.1", "denom(%0)");
		
		// this chooses x if it's in the expression
		// otherwise the first variable alphabetcially
		//when(count_eq(x,lname(%0))==0,lname(%0)[0],x)
		
		p("Derivative.1",
				"regroup(diff(%0, when(count_eq(x,lname(%0))==0,lname(%0)[0],x)))");
		p("Derivative.2", 
				"when(type(%1)==DOM_INT,"+
						"regroup(diff(%0,when(count_eq(x,lname(%0))==0,lname(%0)[0],x),%1))"+
						","+
						"regroup(diff(%0,%1))"+
				")");

		p("Derivative.3", 
				"regroup(diff(%0,%1,%2))");
		p("Determinant.1", "det(%0)");
		p("Dimension.1", "when(%0[0]=='pnt' && size(%0[1])>1,size(%0[1]),dim(%0))");
		p("Div.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then iquo(%0,%1) else quo(%0,%1,x) fi");
		p("Division.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then iquorem(%0,%1) else quorem(%0,%1,x) fi");
		p("Divisors.1",
				"dim(idivis(%0))");
		p("DivisorsList.1",
				"idivis(%0)");
		p("DivisorsSum.1",
				"sum(idivis(%0))");
		p("Dot.2", "regroup(dot(%0,%1))");
		// GeoGebra indexes lists from 1, giac from 0

		// equations:
		// (4x-3y=2x+1)[0] ='='
		// (4x-3y=2x+1)[1] = left side
		// (4x-3y=2x+1)[2] = right side

		// expressions:
		// (4x+3y-1)[0] = '+' -- no way to handle in GGB, return ?
		// (4x+3y-1)[1] = 4x
		// (4x+3y-1)[2] = 3y
		// (4x+3y-1)[3] = -1
		p("Element.2", "when(type(%0)==DOM_LIST,(%0)[%1-1],when(%1>0,(%0)[%1],?))");

		//if %0[0]=='=' then %0[%1] else when(...) fi;

		// GeoGebra indexes lists from 1, giac from 0
		p("Element.3",
				"(%0)[%1 - 1,%2 - 1]");
		
		p("Eliminate.2", "eliminate(%0,%1)");

		// used in regular mode
		// Giac doesn't auto-simplify
		// normal so f(x):=(x^2-1)/(x-1) -> x+1 (consistent with Reduce)
		// regroup so that r*r^n -> r^(n+1)
		// regroup/normal swapped for improved variable order eg x^2 + a*x + b
		p("Evaluate.1", "regroup(normal(%0))");
		//p("Evaluate.1", "%0");

		p("Expand.1",
				"normal(%0)");
		p("Exponential.2", "1-exp(-(%0)*(%1))");

		// factor over rationals
		// add x so that Factor[(-k x² + 4k x + x³)] gives a nicer answer
		p("Factor.1",
				"[with_sqrt(0),[ggbans:=%0],[if type(ggbans)==DOM_INT then ggbans:=ifactor(ggbans); else ggbans:=factor(ggbans,x); fi],with_sqrt(1),ggbans][4]");
		p("Factor.2",
				"[with_sqrt(0),[ggbans:=%0],[ggbans:=factor(ggbans,%1)],with_sqrt(1),ggbans][4]");

		// factor over irrationals
		p("IFactor.1",
				"[with_sqrt(1),[ggbans:=%0],[if type(ggbans)==DOM_INT then ggbans:=ifactor(ggbans); else ggbans:=factor(ggbans,x); fi],ggbans][3]");
		p("IFactor.2",
				"[with_sqrt(1),[ggbans:=%0],[ggbans:=factor(ggbans,%1)],ggbans][3]");

		// convert {x-1,1,x+1,1} to {{x-1,1},{x+1,1}}
		p("Factors.1",
				//"factors(%0)");
				"[[ggbans:=%0],[if type(ggbans)==DOM_INT then calc_mode(0); ggbans:=ifactors(ggbans); calc_mode(1); else ggbans:=factors(ggbans); fi],matrix(dim(ggbans)/2,2,ggbans)][2]");
		p("FDistribution.3",
				"fisher_cdf(%0,%1,%2)");
		// alternative for exact answers
		// "Beta(exact(%0)/2,%1/2,%0*%2/(%0*%2+%1),1)");
		p("Flatten.1", "flatten(%0)");

		p("First.1", "{when(type(%0)==DOM_LIST,(%0)[0],(%0)[1])}");
		p("First.2", "when(type(%0)==DOM_LIST,(%0)[0..%1-1],seq((%0)[j],j,1,%1))");

		// These implementations follow the one in GeoGebra
		p("FitExp.1",
				"[[ggbans:=%0],[ggbans:=exponential_regression(ggbans)],evalf(ggbans[1])*exp(ln(evalf(ggbans[0]))*x)][2]");
		p("FitLog.1",
				"[[ggbans:=%0],[ggbans:=logarithmic_regression(%0)],evalf(ggbans[0])*ln(x)+evalf(ggbans[1])][2]");
		p("FitPoly.2",
				"normal(evalf(horner(polynomial_regression(%0,%1),x)))");
		p("FitPow.1",
				"[[ggbans:=%0],[ggbans:=power_regression(ggbans)],evalf(ggbans[1])*x^evalf(ggbans[0])][2]");
		
		// Function[sin(x),0, 2 pi]
		// Function[sin(p),0, 2 pi]
		// Function[5,0, 1]
		p("Function.3", "[[ggbvars:=lname(%0)],[ggbvar:=when(size(ggbvars)==0,x,ggbvars[0])], when(ggbvar>=%1 && ggbvar<=%2, %0, undef)][2]");

		p("Gamma.3", "igamma((%0),(%2)/(%1),1)");
		p("GCD.2",
				"gcd(%0,%1)");
		p("GCD.1",
				"lgcd(%0)");
		// GetPrecision.1

		// Groebner basis related commands.
		// See http://en.wikipedia.org/wiki/Gr%C3%B6bner_basis#Monomial_ordering to learn more about the following.
		// Also http://en.wikipedia.org/wiki/Monomial_order is very helpful.
		// Naming convention follows the (first) Wikipedia article, however, other pieces of software
		// (like Sage) may have different names. There is no common scientific naming for the orderings.
		// 1. (Pure) lexicographical ordering (original, "classical" method):
		p("GroebnerLex.1", "gbasis(%0,indets(%0),plex)");
		p("GroebnerLex.2", "gbasis(%0,%1,plex)");
		// We will not use the former "Groebner" command since for educational purposes it is crucial
		// to make an emphasis on the monomial ordering.
		// 2. Total degree reverse lexicographical ordering (best method), also called as "grevlex":
		p("GroebnerDegRevLex.1", "gbasis(%0,indets(%0),revlex)");
		p("GroebnerDegRevLex.2", "gbasis(%0,%1,revlex)");
		// 3. Total degree lexicographical ordering (useful for elimination), also called as "grlex":
		p("GroebnerLexDeg.1", "gbasis(%0,indets(%0),tdeg)");
		p("GroebnerLexDeg.2", "gbasis(%0,%1,tdeg)");
		
		p("HyperGeometric.5",
				"[[m:=%1],[ng:=%0],[n:=%2],[kk:=%3],if %4=true then sum(binomial(m,k)*binomial((ng-m),(n-k))/binomial(ng,n),k,0,floor(kk)) " +
				"else binomial(m,kk)*binomial((ng-m),(n-kk))/binomial(ng,n) fi][4]");
		p("Identity.1", "identity(round(%0))");
		p("If.2", "when(%0,%1,undef)");
		p("If.3", "when(%0,%1,%2)");

		// normal(regroup()) so that ImplicitDerivative[x^2 + y^2, y, x] gives a nice answer
		// the danger is that this could multiply something out eg (x+1)^100 (unlikely)
		p("ImplicitDerivative.3", "normal(regroup(-diff(%0,%2)/diff(%0,%1)))");
		p("ImplicitDerivative.1", "normal(regroup(-diff(%0,x)/diff(%0,y)))");

		p("Integral.1",
				"regroup(integrate(%0))");
		p("Integral.2",
				"regroup(integrate(%0,%1))");

		// The symbolic value of the integral is checked against a numeric evaluation of the integral
		// if they return different answers then a list with both values is returned.
		// get the first element of the list to ignore the warning
		p("Integral.3","[[[ggbans:=0/0],[ggbans:=integrate(%0,%1,%2)]]," +
				"normal(when(type(ggbans)==DOM_LIST,ggbans[0],ggbans))][1]");
		p("Integral.4","[[[ggbans:=0/0],[ggbans:=integrate(%0,%1,%2,%3)]]," +
				"normal(when(type(ggbans)==DOM_LIST,ggbans[0],ggbans))][1]");
		p("IntegralBetween.4","[[[ggbans:=0/0],[ggbans:=int(%0-(%1),x,%2,%3)]]," +
				"normal(when(type(ggbans)==DOM_LIST,ggbans[0],ggbans))][1]");
		p("IntegralBetween.5","[[[ggbans:=0/0],[ggbans:=int(%0-(%1),%2,%3,%4)]]," +
				"normal(when(type(ggbans)==DOM_LIST,ggbans[0],ggbans))][1]");

		// need to wrap in coordinates() for Intersect[Curve[t,t^2,t,-10,10],Curve[t2,1-t2,t2,-10,10] ]
		// but not for Intersect[x^2,x^3]
		// ggbans:=0/0 to make sure if there's an error, we don't output previous answer
		// add y= so that Intersect[(((2)*(x))+(1))/((x)-(5)),y=2] ie Intersect[f,a] works
		p("Intersect.2",
				"[[ggbans:=0/0],"+
		
				"[ggbarg0:=%0],"+
				"[ggbarg1:=%1],"+
				"[ggbans:=normal(inter(when(ggbarg0[0]=='=',ggbarg0,y=ggbarg0),when(ggbarg1[0]=='=',ggbarg1,y=ggbarg1)))],[ggbans:=when(type(ggbans[0])==DOM_LIST,ggbans,coordinates(ggbans))],ggbans][5]");

		// Giac currently uses approximation for this
		//p("Conic.5", "equation(conic((%0),(%1),(%2),(%3),(%4)))");
		
		
		// http://www.had2know.com/academics/conic-section-through-five-points.html
		// exact method
		p("Conic.5",
				"[[[M:=0/0],[A:=(0/0,0/0)],[B:=(0/0,0/0)],[C:=(0/0,0/0)],[D:=(0/0,0/0)],[E:=(0/0,0/0)]],"+
						//"[[A:=%0],[B:=%1],[C:=%2],[D:=%3],[E:=%4]],"+
						"[[A:=(real(%0[1]),im(%0[1]))],[B:=(real(%1[1]),im(%1[1]))],[C:=(real(%2[1]),im(%2[1]))],[D:=(real(%3[1]),im(%3[1]))],[E:=(real(%4[1]),im(%4[1]))],],"+
						"[M:={{x^2,x*y,y^2,x,y,1},{A[0]^2,A[0]*A[1],A[1]^2,A[0],A[1],1},{B[0]^2,B[0]*B[1],B[1]^2,B[0],B[1],1},{C[0]^2,C[0]*C[1],C[1]^2,C[0],C[1],1},{D[0]^2,D[0]*D[1],D[1]^2,D[0],D[1],1},{E[0]^2,E[0]*E[1],E[1]^2,E[0],E[1],1}}],"+
						"[M:=det(M)],"+
						
						// eg Conic[(5,0),(-5,0),(0,5),(0,-5),(4,1)]
						// simplify to x² +2 x y + y² - 25 from 10000x² + 20000x y + 10000y² - 250000
						"[hcf:=factors(M)[0]],"+
						"when(type(hcf)==DOM_INT,normal(M/hcf)=0,M=0)"+
						"][5]"
				);
		
		// version using Giac's internal commands: slower and not robust (converts to parametric form as an intermediate step)
		// Ellipse[point, point, point/number]
		//p("Ellipse.3", "equation(ellipse(%0,%1,%2))");
		// Hyperbola[point, point, point/number]
		//p("Hyperbola.3", "equation(hyperbola(%0,%1,%2))");

		
		// adapted from GeoConicND.setEllipseHyperbola()
		final String ellipseHyperbola1 = "[[[a:=0/0],"+
				"[b1:=0/0],"+
				"[b2:=0/0],"+
				"[c1:=0/0],"+
				"[c2:=0/0],"+				
				"[a:=%2],"+
				"[b1:=real(%0[1])],"+
				"[b2:=im(%0[1])],"+
				"[c1:=real(%1[1])],"+
				"[c2:=im(%1[1])],"+
				// AlgoEllipseFociPoint, AlgoHyperbolaFociPoint
				"[a := when(%2[0]=='pnt',(sqrt((b1-real(a[1]))^2+(b2-im(a[1]))^2) ";

		final String ellipseHyperbola2 = "sqrt((c1-real(a[1]))^2+(c2-im(a[1]))^2))/2,a)],"+
				"[diff1 := b1 - c1],"+
				"[diff2 := b2 - c2],"+
				"[sqsumb := b1 * b1 + b2 * b2],"+
				"[sqsumc := c1 * c1 + c2 * c2],"+
				"[sqsumdiff := sqsumb - sqsumc],"+
				"[a2 := 2 * a],"+
				"[asq4 := a2 * a2],"+
				"[asq := a * a],"+
				"[afo := asq * asq],"+
				"[ggbans:=simplify(4 * (a2 - diff1) * (a2 + diff1) * x^2 -8 * diff1 * diff2 * x * y + 4 * (a2 - diff2) * (a2 + diff2)* y^2 -4 * (asq4 * (b1 + c1) - diff1 * sqsumdiff)*x - 4 * (asq4 * (b2 + c2) - diff2 * sqsumdiff)*y-16 * afo - sqsumdiff * sqsumdiff + 8 * asq * (sqsumb + sqsumc))]],"+
				// simplify (...)/1000 = 0
				"when(type(denom(ggbans))==DOM_INT,numer(ggbans)=0,ggbans=0)][1]";

		
		
		// simplify (...)/1000 = 0
		//"[ggbans:=numer(ggbans)],"+
		// simplify eg 28x² - 24x y - 160x + 60y² - 96y + 256 = 0
		//"[hcf:=factors(ggbans)[0]]],"+
		//"when(type(hcf)==DOM_INT,normal(ggbans/hcf)=0,ggbans=0)][1]";

		
		p("Ellipse.3", 
				ellipseHyperbola1 +
				"+" +
				ellipseHyperbola2);
		
		p("Hyperbola.3", 
				ellipseHyperbola1 +
				"-" +
				ellipseHyperbola2);
		p("Intersection.2", "%0 intersect %1");
		p("Iteration.3",
				"(unapply(%0,x)@@%2)(%1)");
		p("IterationList.3",
				"[[ggbans(f,x0,n):=begin local l,k; l:=[x0]; for k from 1 to n do l[k]:=f(l[k-1]); od; l; end],ggbans(unapply(%0,x),%1,%2)][1]");
		p("PointList.1",
				"flatten(coordinates(%0))");
		p("RootList.1",
				"apply(x->convert([x,0],25),%0)");
		p("Invert.1", "[[ggbans:=0/0], [ggbarg:=%0], [ggbans:=when(type(ggbarg)!=DOM_LIST,"+
				// invert function (answer is function, not mapping)
				"subst(right(revlist([op(solve(tmpvar=ggbarg,lname(ggbarg)[0]))])[0]),tmpvar,lname(ggbarg)[0])"+
				","+
				// invert matrix
				"inv(ggbarg))"+
				"],ggbans][3]");

		p("IsPrime.1", "isprime(%0)");
		p("Join.N","flatten(%)");
		
		//p("Last.1",
		//		"{%0[dim(%0)-1]}");
		//p("Last.2",
		//		"%0[size(%0)-%1..size(%0)-1]");
		
		p("Laplace.1", "when(lname(%0)[0]=ggbtmpvart, laplace(%0, ggbtmpvart, ggbtmpvars), laplace(%0, lname(%0)[0]))");
		p("Laplace.2", "laplace(%0, %1)");
		p("Laplace.3", "laplace(%0, %1, %2)");
		p("InverseLaplace.1", "when(lname(%0)[0]=ggbtmpvars, ilaplace(%0, ggbtmpvars, ggbtmpvart), ilaplace(%0, lname(%0)[0]))");
		p("InverseLaplace.2", "ilaplace(%0, %1)");
		p("InverseLaplace.3", "ilaplace(%0, %1, %2)");
		
		p("Last.1", "{when(type(%0)==DOM_LIST,(%0)[dim(%0)-1],(%0)[dim(%0)])}");
		p("Last.2", "when(type(%0)==DOM_LIST,(%0)[size(%0)-%1..size(%0)-1],seq((%0)[j],j,dim(%0)-%1+1,dim(%0)))");

		
		p("LCM.1",
				"lcm(%0)");
		p("LCM.2",
				"lcm(%0,%1)");
		p("LeftSide.1",
				"when(type(%0)==DOM_LIST,map(%0,left),left(%0))");
		p("LeftSide.2",
				"left(%0[%1-1])");
		p("Length.1",
				"[[ggbv:=%0],regroup(when(ggbv[0]=='pnt', l2norm(ggbv),size(ggbv)))][1]");
		p("Length.3",
				"arcLen(%0,%1,%2)");
		p("Length.4", "arcLen(%0,%1,%2,%3)");

		// regroup so that exp(1)^2 is simplified
		// regroup(inf) doesn't work, so extra check needed
		p("Limit.2",
				"[[ggbans:=?],[ggbans:=limit(%0,%1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("Limit.3",
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitAbove.2",
				"[[ggbans:=?],[ggbans:=limit(%0,x,%1,1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitAbove.3", 
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2,1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitBelow.2",
				"[[ggbans:=?],[ggbans:=limit(%0,x,%1,-1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");
		p("LimitBelow.3", 
				"[[ggbans:=?],[ggbans:=limit(%0,%1,%2,-1)], [ggbans:=when(ggbans==inf || ggbans==-inf || ggbans==undef,ggbans,regroup(ggbans))],ggbans][3]");

		p("Max.N", "when(type(%)==DOM_LIST, when(type((%)[0])==DOM_LIST, ?, max(%)), ?)");
		p("MatrixRank.1", "rank(%0)");
		p("Mean.1",
				"mean(%0)");
		p("Median.1",
				"median(%0)");
		p("Min.N", "when(type(%)==DOM_LIST, when(type((%)[0])==DOM_LIST, ?, min(%)), ?)");
		p("MixedNumber.1",
				"propfrac(%0)");
		p("Mod.2",
				"if type(%0)==DOM_INT && type(%1)==DOM_INT then irem(%0,%1) else rem(%0,%1,x) fi");
		p("NextPrime.1", "nextprime(%0)");
		p("NIntegral.3",
				"romberg(%0,%1,%2)");
		p("NIntegral.4",
				"romberg(%0,%1,%2,%3)");
		p("Normal.3",
				"normald_cdf(%0,%1,%2)");
		p("Normal.4",
				"if %3=true then normald_cdf(%0,%1,%2) else (1/sqrt(2*pi*((%1)^2))) * exp(-((%2-(%0))^2) / (2*((%1)^2))) fi");
		p("nPr.2", "perm(%0,%1)");

		p("NSolve.1",
				"ggbsort([[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)}]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[irem(j,dim(ggbans))]=ggbans[j],j,0,dim(ggbans)-1)][3],"+
						// eg NSolve[a^4 + 34a^3 = 34]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars[0])],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[0]=ggbans[j],j,0,dim(ggbans)-1)][3])],"+
				"ggbans][2])");

		p("NSolve.2",
				"ggbsort([[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)},{x=1,y=1}]
						// eg NSolve[{π / x = cos(x - 2y), 2 y - π = sin(x)},{x,y}]
						"[[ggbvars:=seq(left(%1[j]),j,0,dim(%1))],[ggbans:=fsolve(%0,%1)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars[irem(j,dim(ggbans))]=ggbans[j],j,0,dim(ggbans)-1)][3],"+
						// eg NSolve[a^4 + 34a^3 = 34, a=3]
						// eg NSolve[a^4 + 34a^3 = 34, a]
						"[[ggbvars:=when(type(%1)==DOM_LIST,left(%1[0]),left(%1))],[ggbans:=fsolve(%0,%1)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],seq(ggbvars=ggbans[j],j,0,dim(ggbans)-1)][3])],"+
				"ggbans][2])");

		// fsolve starts at x=0 if no initial value is specified and if the search is not successful
		// it will try a few random starting points.

		p("NSolutions.1",
				"ggbsort([[ggbans:=%0],[ggbans:=when(type(ggbans)==DOM_LIST,"+
						// eg NSolutions[{π / x = cos(x - 2y), 2 y - π = sin(x)}]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars)],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],ggbans][3],"+
						// eg NSolutions[a^4 + 34a^3 = 34]
						"[[ggbvars:=lname(ggbans)],[ggbans:=fsolve(%0,ggbvars[0])],[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,[ggbans])],ggbans][3])],"+
				"ggbans][2])");

		p("NSolutions.2",
				"ggbsort([[ggbans:=fsolve(%0,%1)],when(type(ggbans)==DOM_LIST,ggbans,[ggbans])][1])");

		p("Numerator.1", "numer(%0)");

		p("Numeric.1",
				"[[ggbans:=%0],when(dim(lname(ggbans))==0 || count_eq(unicode0176u,lname(ggbans))>0,"+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbans)"+
						","+
						// #4537
						"evalf(regroup(normal(ggbans)))"+
				")][1]");

		p("Numeric.2",
				"[[ggbans:=%0],when(dim(lname(ggbans))==0 || lname(ggbans)==[unicode0176u],"+
						// normal() so that Numeric(x + x/2) works
						// check for unicode0176u so that Numeric[acos((-11.4^2+5.8^2+7.2^2)/(2 5.8 7.2))]
						// is better when returning degrees from inverse trig
						"evalf(ggbans,%1)"+
						","+
						// #4537
						"evalf(regroup(normal(ggbans)),%1)"+
				")][1]");

		//using sub twice in opposite directions seems to fix #2198, though it's sort of magic
		// with_sqrt(0) to factor over rationals
		p("PartialFractions.1",
				"partfrac(%0)");
		p("PartialFractions.2", "partfrac(%0,%1)");
		p("Pascal.4",
				"if %3=true then Beta(%0,1+floor(%2),%1,1) else (1-(%1))^(%2)*(%1)^(%0)*binomial(%0+%2-1,%0-1) fi");
		p("Poisson.3",
				"if %2=true then " +
						"exp(-(%0))*sum ((%0)^k/k!,k,0,floor(%1)) " +
				"else normal((%0)^(%1)/factorial(floor(%1))*exp(-%0)) fi");
		p("Polynomial.1",
				"[[[ggbans:=0/0], [ggbinput:=%0], [ggbinput:=coeffs(ggbinput,x)], " +
				"[ggbans:=add(seq(ggbinput[j]*x^(size(ggbinput)-1-j),j=0..size(ggbinput)-1))]],ggbans][1]");
		p("Polynomial.2",
				"[[[ggbans:=0/0], [ggbinput:=%0], [ggbvar:=%1], [ggbinput:=coeffs(ggbinput,ggbvar)], " +
				"[ggbans:=add(seq(ggbinput[j]*ggbvar^(size(ggbinput)-1-j),j=0..size(ggbinput)-1))]],ggbans][1]");
		p("PreviousPrime.1",
				"if (%0 > 2) then prevprime(%0) else 0/0 fi");
		p("PrimeFactors.1",
				"ifactors(%0)");
		// normal() makes sure answer is expanded
		// TODO: do we want this, or do it in a more general way
		p("Product.1",
				"normal(product(%0))");
		p("Product.4", "normal(product(%0,%1,%2,%3))");
		// p("Prog.1","<<%0>>");
		// p("Prog.2","<<begin scalar %0; return %1 end>>");

		p("Random.2", "%0+rand(%1-(%0)+1)"); // "RandomBetween"
		p("RandomBinomial.2",
				"binomial_icdf(%0,%1,rand(0,1))");
		p("RandomElement.1", "rand(1,%0)[0]");
		p("RandomPoisson.1",
				"poisson_icdf(%0,rand(0,1))"); // could also make the product of rand(0,1) until less than exp(-%0)
		p("RandomNormal.2",
				"randnorm(%0,%1)");
		p("RandomPolynomial.3",
				"randpoly(%0,x,%1,%2)");
		p("RandomPolynomial.4",
				"randpoly(%1,%0,%2,%3)");
		p("Rationalize.1", "if type(%0)==DOM_RAT then %0 else normal(exact(%0)) fi");
		p("Reverse.1","revlist(%0)");
		p("RightSide.1",
				"when(type(%0)==DOM_LIST,map(%0,right),right(%0))");
		p("RightSide.2",
				"right(%0[%1-1]) ");

		p("ReducedRowEchelonForm.1",
				"rref(%0)");
		p("Sample.2",
				"flatten(seq(rand(1,%0),j,1,%1))");
		p("Sample.3",
				"if %2=true then flatten(seq(rand(1,%0),j,1,%1)) else rand(%1,%0) fi");
		p("SampleVariance.1",
				" [[ggbans:=%0],[ggbans:=normal(variance(ggbans)*size(ggbans)/(size(ggbans)-1))],ggbans][2]");
		p("SampleSD.1",
				"normal(stddevp(%0))");
		p("Sequence.1", "seq(j,j,1,%0)");
		p("Sequence.4",
				"seq(%0,%1,%2,%3)");
		p("Sequence.5",
				"seq(%0,%1,%2,%3,%4)");	
		p("SD.1",
				"normal(stddev(%0))");
		
		// removed, Shuffle[{1,2}] kills Giac
		p("Shuffle.1", "randperm(%0)");
		
		// regroup for r*r^n
		// tlin() removed, see #3956
		p("Simplify.1", "simplify(regroup(%0))");

		p("Solutions.1",
				"ggbsort(normal(zeros(%0,x)))");
		p("Solutions.2",
				"ggbsort(normal(zeros(%0,%1)))");

		// Root.1 and Solve.1 should be the same		
		String root1 = "ggbsort(normal([op(solve(%0))]))";
		p("Root.1", root1);
		p("Solve.1", root1);

		p("Solve.2",
				"ggbsort(normal([op(solve(%0,%1))]))");
		p("SolveODE.1",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0),x->y=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0),x->y=x)[0])"
						+")");
		p("SolveODE.2",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%1),x->y=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%1),x->y=x)[0])"
						+")");
		p("SolveODE.3",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%2,%1),(type(%1)==6)?(x->%1=x):(x->y=x))[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%2,%1),(type(%1)==6)?(x->%1=x):(x->y=x))[0])"
						+")");
		p("SolveODE.4",
				"when((%0)[0]=='=',"
						+"normal(map(desolve(%0,%2,%1,%3),x->%1=x)[0])"
						+","
						// add y'= if it's missing
						+"normal(map(desolve(y'=%0,%2,%1,%3),x->%1=x)[0])"
						+")");
		p("SolveODE.5",//SolveODE[y''=x,y,x,A,{B}]
				"normal(map(desolve(%0,%2,%1,%3,%4),x->%1=x)[0])");
		p("Substitute.2","subst(%0,%1)");
		p("Substitute.3","subst(%0,%1,%2)");
		// p("SubstituteParallel.2","if hold!!=0 then sub(%1,%0) else sub(%1,!*hold(%0))");

		// remove normal from Sum, otherwise
		// Sum[1/n*sqrt(1-(k/n)^2),k,1,n]
		// Sum[1/10*sqrt(1-(k/10)^2),k,1,10]
		// don't work
		p("Sum.1",
				"sum(%0)");
		p("Sum.4",
				"expand(subst(sum(subst(%0,%1,ggbsumvar@1),ggbsumvar@1,%2,%3), ggbsumvar@1, %1))");

		// GeoGebra counts elements from 1, giac from 0
		p("Take.3",
				"%0[%1-1..%2-1]");
		p("TaylorSeries.3",
				"convert(series(%0,x,%1,%2),polynom)");
		p("TaylorSeries.4",
				"convert(series(%0,%1,%2,%3),polynom)");
		p("TDistribution.2",
				"student_cdf(%0,%1)");
		// alternative for exact calculations, but Numeric[TDistribution[4,2],15] doesn't work with this
		// "1/2 + (Beta(%0 / 2, 1/2, 1, 1) - Beta(%0 / 2, 1/2, %0 / (%0 + (%1)^2 ) ,1) )* sign(%1) / 2");
		p("ToComplex.1",
				"[[ggbans:=?],[ggbans:=%0],[ggbtype:=type(evalf(ggbans))],"+
		// ToComplex[3.1]
				"when(ggbtype==DOM_INT || ggbtype==DOM_FLOAT,ggbans,"+
		// ToComplex[(3,4)]
				"when(ggbans[0]=='pnt',xcoord(%0)+i*ycoord(%0),"+
		// ToComplex[ln(i)], ToComplex[a]
				"real(ggbans)+i*im(ggbans)"+
				"))][3]");
		p("ToExponential.1",
				"rectangular2polar(%0)");
		p("ToPolar.1",
				"([[ggbans:=%0],[ggbans:=polar_coordinates(ggbans)],[ggbans:=convert([ggb_ang(ggbans[0],ggbans[1])],25)],ggbans])[3]");
		p("ToPoint.1",
				"point(convert(coordinates(%0),25))");
		p("Transpose.1", "transpose(%0)");
		// http://reduce-algebra.com/docs/trigsimp.pdf
		// possible Giac commands we can use:
		// halftan, tan2sincos2, tan2cossin2, sincos, trigtan, trigsin, trigcos
		// cos2sintan, sin2costan, tan2sincos, trigexpand, tlin, tcollect, trig2exp, exp2trig
		
		// eg tlin(halftan(csc(x) - cot(x) + csc(y) - cot(y))) ->  tan(x/2)+tan(y/2)
		p("TrigExpand.1",
				"tan2sincos(trigexpand(%0))");
		p("TrigExpand.2",
				"when((%1)[0]=='tan', trigexpand(%0),tan2sincos(trigexpand(%0)))");
		
		//subst(trigexpand(subst(sin(x),solve(tmpvar=x/2,lname(sin(x)))),tmpvar=x/2)
		// gives 2*cos(x/2)*sin(x/2)
		p("TrigExpand.3",
				"when(%1==tan(x),"+
				// if %1=tan(x), assume %2=x/2 
				"tlin(halftan(%0))"+
				","+
				"subst(trigexpand(subst(%0,solve(ggbtmp=%2,lname(%0)))),ggbtmp=%2)"+
				")");
		
		//subst(subst(trigexpand(subst(subst((sin(x))+(sin(y)),solve(tmpvar=(x)/(2),lname((sin(x))+(sin(y)))  )),solve(tmpvar2=(y)/(2),lname((sin(x))+(sin(y)))  ))),tmpvar=x/2),tmpvar2=y/2)
		//  2*cos(x/2)*sin(x/2)+2*cos(y/2)*sin(y/2)
		p("TrigExpand.4",
				"when(%1==tan(x),"+
						// if %1=tan(x), assume %2=x/2, %3=y/2
				"tlin(halftan(%0))"+
				","+
				"subst(subst(trigexpand(subst(subst(%0,solve(tmpvar=%2,lname(%0))),solve(tmpvar2=%3,lname(%0)))),tmpvar=%2),tmpvar2=%3)"+
				")");
		
		// calculate trigsin, trigcos, trigtan and check which is shortest (as a string)
		p("TrigSimplify.1",
				"[[[ggbarg:=%0], [ggbsin:=trigsin(ggbarg)], [ggbcos:=trigcos(ggbarg)], [ggbtan:=trigtan(ggbarg)], "+
		"[ggbsinlen:=length(\"\"+ggbsin)],[ggbcoslen:=length(\"\"+ggbcos)],[ggbtanlen:=length(\"\"+ggbtan)]],"+
		"when(ggbsinlen<=ggbcoslen && ggbsinlen<=ggbtanlen,ggbsin,when(ggbcoslen<=ggbtanlen,ggbcos,ggbtan))][1]");
		
		p("TrigCombine.1",
				"tcollect(normal(%0))");
		// eg TrigCombine[sin(x)+cos(x),sin(x)]
		p("TrigCombine.2",
				"when(%1[0]=='sin',tcollectsin(normal(%0)),tcollect(normal(%0)))");
		p("Union.2", "%0 union %1");
		p("Unique.1", "[op(set[op(%0)])]");
		p("Variance.1",
				"normal(variance(%0))");
		p("Weibull.3", "1-exp(-((%2)/(%1))^(%0))");
		p("Zipf.4", // %1=exponent
				"if %3=true then harmonic(%1,%2)/harmonic(%1,%0) else 1/((%2)^%1*harmonic(%1,%0)) fi");
		// TODO check if it's easier to implement with giac's zip command
		p("Zip.N",
				"[[ggbans(l):=begin local len,res,sbl,xpr,k,j;xpr:=l[0];len:=length(l[2]);res:={};" +
				"for k from 4 to length(l)-1 step +2 do len:=min(len,length(l[k])); od;" +
				"for k from 0 to len-1 do sbl:={};for j from 2 to length(l)-1 step +2 do" +
				"sbl:=append(sbl, l[j-1]=l[j][k]);od;res:=append(res, subst(xpr,sbl));od; res; end],ggbans(%)][1]");
		// SolveCubic[x^3+3x^2+x-1]
		// SolveCubic[x^3+3x^2+x-2]
		// SolveCubic[x^3+3x^2+x-3]

		// SolveCubic[x^3 + 6x^2 - 7*x - 2]
		// x³ - 6x² - 7x + 9

		// check with CSolve first, eg
		// f(x) = x³ - 9x² - 2x + 8
		
		// adapted from xcas example by Bernard Parisse
		p("SolveCubic.1", "["+
			"[j:=exp(2*i*pi/3)],"+
			"[V:=symb2poly(%0,x)],"+
			"[n:=size(V)],"+
			
			//if (n!=4){
			//  throw(afficher(P)+" n'est pas de degre 3");
			//}
			// Reduction de l'equation
			
			"[V:=V/V[0]],"+
			"[b:=V[1]],"+
			"[V:=ptayl(V,-b/3)],"+
			"[p:=V[2]],"+
			"[q:=V[3]],"+
			// on est ramen  x^3+p*x+q=0
			// x=u+v -> u^3+v^3+(3uv+p)(u+v)+q=0
			// On pose uv=-p/3 donc u^3+v^3=-q et u^3 et v^3 sont solutions
			// de u^3 v^3 = -p^3/27 et u^3+v^3=-q
			// donc de x^2+q*x-p^3/27=0
			"[d:=q^2/4+p^3/27],"+
			
			//if (d==0){
			//  // racine double
			//  return solve(P,x);
			//}
			"[d:=sqrt(d)],"+
			"[u:=(-q/2+d)^(1/3)],"+
			"[v:=-p/3/u],"+
			"[x1:=u+v-b/3],"+
			"[x2:=u*j+v*conj(j)-b/3],"+
			"[x3:=u*conj(j)+v*j-b/3],"+
			"[x1s:=simplify(x1)],"+
			"[x2s:=simplify(x2)],"+
			"[x3s:=simplify(x3)],"+
			//"[when(d==0,[solve(%0,x)],[when(x1s[1][0]=='rootof',x1,x1s),when(x2s[1][0]=='rootof',x2,x2s),when(x3s[1][0]=='rootof',x3,x3s)])]"+
			"[[x1,x2,x3]]"+
			"][18][0]");
		
		// SolveQuartic[2x^4+3x^3+x^2+1]
		// SolveQuartic[x^4+6x^2-60x+36] approx = {(-1.872136644123) - (3.810135336798 * ί), (-1.872136644123) + (3.810135336798 * ί), 0.6443988642267, 3.099874424019}
		// SolveQuartic[3x^4   + 6x^3   - 123x^2   - 126x + 1080]  =  {(-6), (-4), 3, 5}
		// SolveQuartic[x^(4) - (10 * x^(3)) + (35 * x^(2)) - (50 * x) + 24]  =  {1, 3, 2, 4}
		// SolveQuartic[x^4 +   2x^3   -   41x^2  -   42x   +   360]   =  {(-6), (-4), 3, 5}
		// SolveQuartic[ x^4 + 2x^2 + 6sqrt(10) x + 1] approx {(-2.396488591753), (-0.05300115102973), 1.224744871392 - (2.524476846043 * ί), 1.224744871392 + (2.524476846043 * ί)}
		// SolveQuartic[x^4 +   x^3   +   x   +   1] = {(-1), (-1), (((-ί) * sqrt(3)) + 1) / 2, ((ί * sqrt(3)) + 1) / 2}
		// SolveQuartic[x^(4) - (4 * x^(3)) + (6 * x^(2)) - (4 * x) + 1] = {1}
		// 3 repeated roots, S=0, SolveQuartic[ x⁴ - 5x³ + 9x² - 7x + 2 ] = {2,1}
		// SolveQuartic[x^(4) - (2 * x^(3)) - (7 * x^(2)) + (16 * x) - 5] = ((x^(2) - (3 * x) + 1) * (x^(2) + x - 5))
		// http://en.wikipedia.org/wiki/Quartic_function
		/*
		p("SolveQuartic.1", "["+
				"[ggbans:={}],"+
				"[ggbfun:=%0],"+
				"[ggbcoeffs:=coeffs(ggbfun)],"+
				"[a:=ggbcoeffs[0]],"+
				"[b:=ggbcoeffs[1]],"+
				"[c:=ggbcoeffs[2]],"+
				"[d:=ggbcoeffs[3]],"+
				"[ee:=ggbcoeffs[4]],"+
				// for checking, but unnecessary
				// "[delta:=256*a^3*ee^3-192*a^2*b*d*ee^2-128*a^2*c^2*ee^2+144*a^2*c*d^2*ee-27*a^2*d^4+144*a*b^2*c*ee^2-6*a*b^2*d^2*ee-80*a*b*c^2*d*ee+18*a*b*c*d^3+16*a*c^4*ee-4*a*c^3*d^2-27*b^4*ee^2+18*b^3*c*d*ee-4*b^3*d^3-4*b^2*c^3*ee+b^2*c^2*d^2],"+
				"[p:=(8*a*c-3*b*b)/(8*a*a)],"+
				"[q:=(b^3-4*a*b*c+8*a^2*d)/(8*a^3)],"+
				"[delta0:=c^2-3*b*d+12*a*ee],"+//OK
				"[delta1:=2*c^3-9*b*c*d+27*b^2*ee+27*a*d^2-72*a*c*ee],"+//OK
				
				"if (delta0 == 0 && delta1 == 0) then"+
				// giac's solve should give exact in this case
				" ggbans:=zeros(ggbfun) else " +
			
				" ["+
				"[minusdelta27:=delta1^2-4*delta0^3],"+//OK
				// use surd rather than cbrt so that simplify cbrt(27) works
				//"[Q:=simplify(surd((delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2,3))],"+
				// find all 3 cube-roots
				"[Qzeros:=czeros(x^3=(delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2)],"+
				// czeros can return an empty list eg czeros(x^(3) = (1150 + ((180 * i) * sqrt(35))) / 2)
				// from SolveQuartic[x^(4) - (2 * x^(3)) - (7 * x^(2)) + (16 * x) - 5]
				"[Qzeros:=when(length(Qzeros)==0,{cbrt((delta1 + when(delta0==0, delta1, sqrt(minusdelta27)))/2)},Qzeros)],"+
				"[Q:=Qzeros[0]],"+
				"[Q1:=when(length(Qzeros) > 1,Qzeros[1],Qzeros[0])],"+
				"[Q2:=when(length(Qzeros) > 2,Qzeros[2],Qzeros[0])],"+
				// pick a cube-root to make S non-zero
				// always possible unless quartic is in form (x+a)^4
				"[S:=sqrt(-2*p/3+(Q+delta0/Q)/(3*a))/2],"+
				"[S:=when(S!=0,S,sqrt(-2*p/3+(Q1+delta0/Q1)/(3*a))/2)],"+
				"[S:=when(S!=0,S,sqrt(-2*p/3+(Q2+delta0/Q2)/(3*a))/2)],"+
				
				// could use these for delta > 0 ie minusdelta27 < 0
				//"[phi:=acos(delta1/2/sqrt(delta0^3))],"+
				//"[Salt:=sqrt(-2*p/3+2/(3*a)*sqrt(delta0)*cos(phi/3))/2],"+
				
				"[ggbans:={simplify(-b/(4*a)-S-sqrt(-4*S^2-2*p+q/S)/2),simplify(-b/(4*a)-S+sqrt(-4*S^2-2*p+q/S)/2),simplify(-b/(4*a)+S-sqrt(-4*S^2-2*p-q/S)/2),simplify(-b/(4*a)+S+sqrt(-4*S^2-2*p-q/S)/2)}]"+
				"]" +
				"fi"+
				//")]" + 
				" ,ggbans][13]");
*/
		
		// Experimental Geometry commands. Giac only
		p("Radius.1", "normal(regroup(radius(%0)))"); 
		p("Center.1", "coordinates(center(%0))"); 
		p("Midpoint.2", "normal(regroup(coordinates(midpoint(%0,%1))))");

		// center-point:      point(%0),point(%1)
		// or center-radius:  point(%0),%1
		// regroup r*r -> r^2 without multiplying out
		// circle(2*(%0)-(%1),%1) to convert centre,point -> points on diameter
		p("Circle.2", "regroup(equation(when(%1[0]=='pnt',circle(2*(%0)-(%1),%1),circle(%0,%1))))");

		p("Area.1", "normal(regroup(area(circle(%0))))");
		p("Circumference.1", "normal(regroup(perimeter(%0)))");

		p("LineBisector.2", "equation(perpen_bisector(%0,%1))");
		p("AngularBisector.2", "[[B:=inter(%0,%1)],[cbx:=B[0][0]],[Ax1:=cbx-10],[eqa:=equation(%0)],[eqb:=equation(%1)],"
				+ "[Ay1:=right(subst(eqa,x=Ax1))],[Ax2:=cbx+10],[Ay2:=right(subst(eqa, x=Ax2))],"
				+ "[Cy:=right(subst(eqb, x=Ax1))],[C:=point(Ax1,Cy)],"
				+ "[equation(bisector(B,point(Ax1,Ay1),C)),equation(bisector(B,point(Ax2,Ay2),C))]][10]");
		p("AngularBisector.3", "equation(bisector(%1,%0,%2))");

		// can it be plotted in GeoGebra? Do we want it to be plotted?
		p("Angle.3", "normal(regroup(angle(%1,%0,%2)))");

		// eg distance((4,5),(0,3))
		// eg distance((2,3,4),(0,3,1))
		// eg distance(conic(y=x^2),(0,3))
		// don't want normal(), eg Distance[(a,b),(c,d)] 
		// bit do want it for Distance[(0.5,0.5),x^2+y^2=1]
		p("Distance.2", 
				"[[[ggbans:=0/0],[ggbans:=regroup(distance(%0,"+ 
						// #3907 add "y=" for functions but not points 
						"when(%1[0]!='pnt' && %1[0] != '=',y=%1,%1)"+ 
						"))]]," +				
				"when(lname(ggbans)=={},normal(ggbans),ggbans)][1]");

		// regroup: y = -2 a + b + 2x -> y = 2x - 2 a + b 
		// don't want normal(), eg Line[(a,b),(c,d)]
		p("Line.2","regroup(equation(line(%0,%1)))");
		
		//p("Midpoint.2", "[[ggbans:=factor((normal(convert(coordinates(midpoint(%0,%1)),25))))]," +
		//		"(ggbans[0],ggbans[1])][1]");

		// normal: nice form for Midpoint[(1/2,pi),(1,1)]
		// factor: nice form for Midpoint[(a,b),(c,d)]
		p("Midpoint.2", "convert(factor((normal(coordinates(midpoint(%0,%1))))),25)");

		p("OrthogonalLine.2", "equation(perpendicular(%0,line(%1)))");
		// TODO: return Segment() not equation
		p("Segment.2","equation(segment(%0,%1))");

		// TODO: needs to get back from Giac into GeoGebra as a parametric eqn
		//p("Curve.5", "equation(plotparam([%0,%1],%2,%3,%4))");
		//p("Polygon.N", "polygon(%)");
		//p("PolyLine.N", "open_polygon(%)");

		p("Tangent.2","when((%0)[0]=='pnt',"+
				"when((%1)[0]=='=',"+
				// Tangent[conic/implicit, point on curve]
				"equation(tangent(%1,%0)),"+
				// Tangent[point, function]
				// just use x-coordinate real(%0[1])
				"y=subst(diff(%1,x),x=real(%0[1]))*(x-real(%0[1]))+subst(%1,x=real(%0[1])))"+
				","+
				// Tangent[x-value, function]
				"y=subst(diff(%1,x),x=%0)*(x-(%0))+subst(%1,x=%0)"+
				")");
		p("TangentThroughPoint.2", 
				"[[ggbans:=?],[ggbans:=equation(tangent(when((%1)[0]=='=',%1,y=%1),%0))],"
				+ "[ggbans:=when(((ggbans)[0])=='=' && lhs(ggbans)==1 && rhs(ggbans)==0,?,ggbans)],"
				+ "[ggbans:=when(type(ggbans)==DOM_LIST,ggbans,{ggbans})],ggbans][4]");

		// see ToPoint.1 
		// eg Dot[Vector[(a,b)],Vector[(c,d)]] 
		p("Vector.1", 
				"point(convert(coordinates(%0),25))");
		
		// TODO: see GeoVector.buildValueString()
		// p("Vector.2", "%1-(%0)");
		p("OrthogonalVector.1",
				"convert([[0,-1],[1,0]]*(%0),25)");
		p("UnitOrthogonalVector.1",
				"convert(unitV([-im(%0[1]),real(%0[1])]),25)");
		p("UnitVector.1",
				"when(type(%0)==DOM_LIST,normalize(%0),when(%0[0]='pnt' && size(%0[1])==3,normal(unitV(%0)),convert(unitV([real(%0[1]),im(%0[1])]),25)))");
		
		return commandMap;
	}

}