package geogebra.kernel.statistics;

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoList;


/** 
 * Fits a*x^b  to a list of points.
 * Adapted from AlgoFitLine and AlgoPolynomialFromCoordinates
 * (Borcherds)
 * @author Hans-Petter Ulven
 * @version 24.04.08
 */
public class AlgoFitPow extends AlgoElement{

    private static final long serialVersionUID  =   1L;
    private GeoList         geolist;                        //input
    private GeoFunction     geofunction;                    //output

    
    public AlgoFitPow(Construction cons, String label, GeoList geolist) {
        this(cons, geolist);
        geofunction.setLabel(label);
    }//Constructor
    
    public AlgoFitPow(Construction cons, GeoList geolist) {
        super(cons);
        this.geolist=geolist;
        geofunction=new GeoFunction(cons);
        setInputOutput();
        compute();
    }//Constructor
    
    public String getClassName() {return "AlgoFitPow";}
        
    protected void setInputOutput(){
        input=new GeoElement[1];
        input[0]=geolist;
        output=new GeoElement[1];
        output[0]=geofunction;
        setDependencies();
    }//setInputOutput()
    
    public GeoFunction getFitPow() {return geofunction;}
    
    public final void compute() {
        int size=geolist.size();
        boolean regok=true;
        double a,b;
        if(!geolist.isDefined() || (size<2) ) {	//24.04.08: 2
            geofunction.setUndefined();
            return;
        }else{
        	RegressionMath regMath = ((Kernel) kernel).getRegressionMath();
            regok=regMath.doPow(geolist);
            if(regok){
                a=regMath.getP1();
                b=regMath.getP2();
                MyDouble A=new MyDouble(kernel,a);
                MyDouble B=new MyDouble(kernel,b);
                FunctionVariable X=new FunctionVariable(kernel);
                ExpressionValue expr=new ExpressionNode(kernel,X,Operation.POWER,B);
                ExpressionNode node=new ExpressionNode(kernel,A,Operation.MULTIPLY,expr);
                Function f=new Function(node,X);
                geofunction.setFunction(f); 
                geofunction.setDefined(true);
            }else{
                geofunction.setUndefined();
                return;  
            }//if error in regression   
        }//if error in parameters
    }//compute()
    
}// class AlgoFitPow
