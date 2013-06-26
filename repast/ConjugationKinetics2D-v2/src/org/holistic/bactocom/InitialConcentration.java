package org.holistic.bactocom;

import org.nfunk.jep.JEP;

public class InitialConcentration {
	private JEP parser;
	
	public InitialConcentration(String s) {
		parser= new JEP();
		parser.initSymTab();
        parser.addStandardConstants();
        parser.addStandardFunctions();
        parser.addComplex();
        //parser.setTraverse(true);
        parser.setAllowAssignment(true);
        parser.setAllowUndeclared(true);
        parser.parseExpression(s);
	}
	
	public Double getValue() {
	     double v= parser.getValue();
         return v;
	}
	
	public Double getMinSurfacePerCell() {
		double v= Math.pow(Math.pow(1/(getValue()/1.0e12),1D/3D),2);
		return v;
	}
	
	public Double getScaledValue(int w, int h) {
		double s= w * h;
		double v= (getValue()/1.0e12) * s;
		return v;
	}
	
	public JEP getParser(){
		return parser;
	}
	
}
