package org.holistic.bactocom;

import org.nfunk.jep.JEP;

public class FitnessFunction {
	private JEP parser;
	
	public FitnessFunction(String s) {
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
	
	public Double getValue(Double x) {
	     parser.setVarValue("x", x);
	     double v= parser.getValue();
         return v;
	}
	
	public JEP getParser(){
		return parser;
	}
	
}
