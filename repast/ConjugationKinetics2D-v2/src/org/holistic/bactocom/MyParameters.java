package org.holistic.bactocom;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class MyParameters {
	public final static int N_SCALEFACTOR= 100;				// Super-individual scale-factor.
	
	public static String CONTEXT= "ConjugationKinetics2D-v2";
	public static String GRID= "Grid";
	
	public static String RANDOM_DIVISION= "DivisionMassNormal";
	public static String RANDOM_GAMMA0= "Gamma0Normal";
	public static String RANDOM_NEIGHBORHOOD= "NeighborhoodUniform";
	public static String RANDOM_CONJUGATION= "ConjugationUniform";
	public static String RANDOM_TIME2CONJUGATE= "ConjugationUniformTime";
	
	public static String VL_NUTRIENTS= "Nutrients";				// Value Layer nutrient key
	
	public static String PN_RANDOM_SEED = "randomSeed";			// Random Seed
	
	public static String PN_INTRINSIC= "TI";					// The intrinsic conjugation rate
	public static String PN_GR= "GR";							// Generation time of recipient cells
	public static String PN_GD= "GD";							// Generation time of donor cells
	public static String PN_GT= "GT";							// Generation time of transconjugant cells
	public static String PN_ORIT= "isOnlyOriT";					// Flag indicating a MOB plasmid
	public static String PN_DENSITY_D= "densityD";				// Initial donor density
	public static String PN_DENSITY_T0= "density0";				// Initial lattice occupation
	public static String PN_EQUATION= "equation";				// The experimental equation
	public static String PN_PLASMID= "pName";					// The plasmid name 
	public static String PN_AUTOFIT= "fitAuto";					// The plasmid name
	
	
	
	public static String GRID_HEIGHT= "height";					// The desired grid height				
	public static String GRID_WIDTH= "width";					// The desired grid width
	
	public static double SI_MICROMETERS= 1.0000e-006;
	public static double SI_FEMTOGRAMS= 1.00e-018; 
	
	
	private final static Parameters parm;
	static {
		parm = RunEnvironment.getInstance().getParameters();
	}
	
	/**
	 * @return the simulation random seed
	 */
	public static int getRandomSeed() {
		return (Integer) parm.getValue(PN_RANDOM_SEED);
	}
	
	/**
	 * @return Intrinsic conjugation rate, Gamma(intrinsic)
	 */
	public static double getIntrinsicConjugationRate() {
		return (Double) parm.getValue(PN_INTRINSIC);
	}
	
	/**
	 * @return Recipient Generation time, G(R)
	 */
	public static int getGr() {
		return (Integer) parm.getValue(PN_GR);
	}
	
	/**
	 * @return Donor Generation time, G(D)
	 */
	public static int getGd() {
		return (Integer) parm.getValue(PN_GD);
	}

	/**
	 * @return Transconjugant Generation time, G(T)
	 */
	public static int getGt() {
		return (Integer) parm.getValue(PN_GT);
	}
	
	/**
	 * @return true whether plasmid is a MOB one.
	 */
	public static boolean isOnlyOriT() {
		return (Boolean) parm.getValue(PN_ORIT);
	}
	
	/**
	 * @return The equation fitted to experimental data
	 */
	public static String getEquation() {
		return (String) parm.getValue(PN_EQUATION);
	}
	
	/**
	 * @return the height
	 */
	public static int getHeight() {
		return (Integer) 500;
	}
	
	/**
	 * @return the width
	 */
	public static int getWidth() {
		return (Integer) 500;
	}

	/**
	 * @return Initial donor density
	 */
	public static double getInitialDonorDensity() {
		return (Double) parm.getValue(PN_DENSITY_D)/100D;
	}
	
	public static int getNutrient() {
		return (Integer) 2; //4 p 3,6
	}
	
	/**
	 * The initial cell density
	 * 
	 * @return The initial population at t=0 
	 */
	/*public static int getN0() {
		return (int) (getHeight() * getWidth() * getInitialLatticeOcupation());
	}*/
	
	public static String getM0() {
		return (String) parm.getValue(PN_DENSITY_T0);
	}
	
	public static String getPlasmidName() {
		return (String) parm.getValue(PN_PLASMID);
	}
	
	public static boolean isAutoFit() {
		return (Boolean) parm.getValue(PN_AUTOFIT);
	}
	
	/**
	 * 
	 * @return
	 */
	//public static double getInitialLatticeOcupation() {
	//	return (Double) parm.getValue(PN_DENSITY_T0)/100D;
	//}
}
