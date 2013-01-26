package org.holistic.bactocom;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class MyParameters {
	public final static int N_SCALEFACTOR= 100;				// Super-individual scale-factor.
	
	
	public static String CONTEXT= "ConjugationKinetics-1";
	public static String GRID= "Grid";
	public static String RANDOM_DIVISION= "DivisionMassNormal";
	public static String RANDOM_NEIGHBORHOOD= "NeighborhoodUniform";
	public static String RANDOM_PLASMIDLOSS= "PlasmidLossUniform";
	public static String RANDOM_CONJUGATION= "ConjugationUniform";
	public static String RANDOM_PILUS= "PilusExpression";
	public static String RANDOM_REPRESSED= "Repressed";
	public static String RANDOM_DEREPRESSED= "DeRepressed";
	public static String VL_NUTRIENTS= "Nutrients";				// Value Layer nutrient key
	
	public static String RANDOM_SEED = "randomSeed";			// Random Seed
	public static String PENALTY_PLASMID= "fixed";				// Metabolic burden by plasmid harboring
	public static String PENALTY_CONJUGATION= "conjugation"; 	// Metabolic burden by conjugation event
	public static String PENALTY_DEREPRESSION= "derepression";	// Metabolic burden by T4SS expression
	public static String TAU= "GT";								// Generation time
	public static String ORIT= "orit";							// Self-transmissible plasmid, oriT=true
	public static String MIN_CELL_CYCLE= "CC";					// Minimal cell cycle to conjugate
	public static String DONOR_DENSITY= "donors";				// The initial density of donor cells
	public static String NUTRIENT= "nutrient";					// The initial nutrient particles
	public static String REPRESSED= "repressed";
	public static String RECOVERY_TIME= "recovery";
	public static String MATURATION_TIME= "maturation";
	public static String PLASMID_LOSS= "plasmidloss";
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
		return (Integer) parm.getValue(RANDOM_SEED);
	}
	
	/**
	 * @return the plasmidPenalty
	 */
	public static double getPlasmidPenalty() {
		return (Double) parm.getValue(PENALTY_PLASMID);
	}

	/**
	 * @return the conjugationPenalty
	 */
	public static double getConjugationPenalty() {
		return (Double) parm.getValue(PENALTY_CONJUGATION);
	}

	/**
	 * @return the derepressionPenalty
	 */
	public static double getDerepressionPenalty() {
		return (Double) parm.getValue(PENALTY_DEREPRESSION);
	}

	/**
	 * @return the generationTime
	 */
	public static int getGenerationTime() {
		return (Integer) parm.getValue(TAU);
	}

	/**
	 * @return the minCellCycle
	 */
	public static double getMinCellCycle() {
		return (Double) parm.getValue(MIN_CELL_CYCLE); 
	}

	/**
	 * @return the donorDensity
	 */
	public static int getDonorDensity() {
		return (Integer) parm.getValue(DONOR_DENSITY);
	}

	/**
	 * @return the oriT
	 */
	public static boolean isOriT() {
		return (Boolean) parm.getValue(ORIT);
	}
	
	/**
	 * @return the intial nutrient concentration
	 */
	public static double getNutrient() {
		return (Double) parm.getValue(NUTRIENT);
	}
	
	/*
	 *public static String CONSTITUTIVELY= "constitutively";
	 */
	
	/**
	 * @return plasmid is constitutively expressed
	 */
	public static boolean isRepressed() {
		return (Boolean) parm.getValue(REPRESSED);
	}
	
	/**
	 * @return the plasmid recovery time
	 */
	public static double getPlasmidRecoveryTime() {
		return (Double) parm.getValue(RECOVERY_TIME);
	}
	
	/**
	 * @return the plasmid maturation time
	 */
	public static double getPlasmidMaturationTime() {
		return (Double) parm.getValue(MATURATION_TIME);
	}
	
	/**
	 * @return the plasmid loss probability
	 */
	public static double getPlasmidLossProbability() {
		Object v= parm.getValue(PLASMID_LOSS);
		return (Double) (v != null ? v : 0.0001);
	}

	/**
	 * @return the height
	 */
	public static int getHeight() {
		Object v= parm.getValue(GRID_HEIGHT);
		return (Integer) (v != null ? v : 1000);
	}

	/**
	 * @return the width
	 */
	public static int getWidth() {
		Object v= parm.getValue(GRID_WIDTH);
		return (Integer) (v != null ? v : 1000);
	}
	
	public static int getN0() {
		return (int) (Math.sqrt(getHeight() * getWidth()))/20;
	}
			
	
}
