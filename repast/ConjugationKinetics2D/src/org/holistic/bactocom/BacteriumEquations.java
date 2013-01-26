package org.holistic.bactocom;

/**
 * The equations describing the model evolution   
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */
public class BacteriumEquations {
	
	/**
	 * Estimates the possible value of division mass
	 * 
	 * @param M			The average cell division mass
	 * @param sigmaM	The standard deviation of cell division mass 
	 * @param Z			Sampled random variable from a standard normal distribution
	 * @return			The estimated division mass
	 */
	public static double eqnEstimateZm(double M, double sigmaM, double Z) {
		double Zm= sigmaM * Z + M;
		return Zm;
	}

	/**
	 * Calculate the average nutrient based on the estimated cell duration. This is a simple 
	 * linear fit based on the desired generation time parameter. 
	 * 
	 * @param G			The generation time
	 * @param m0		The mass at t=0
	 * @param e			The metabolic efficiency
	 * 
	 * @return			The average nutrient uptake			
	 */
	public static double eqnAverageUptake(double G, double m0, double Zm, double e) {
		//double V= (Zm - m0)/G * 1/e;
		double V= ((Zm - m0)/G) * (1+e);
		return V;
	}
	
	/**
	 * Calculate the uptake as function of current cell mass.
	 * 
	 * @param mass	The current cell mass 
	 * @param V 	The average nutrient uptake.
	 * @param Zm	The estimated mass at cell should divide.	 
	 * @return 		The required uptake.
	 */
	public static double eqnUptake(double mass, double V, double Zm) {
		double m = V/(Zm-mass);
		double b= V + V/2 - (m * Zm);
		double y= m * mass + b;
		return(Math.abs(y));
	}
	
	/**
	 * Calculate the the linear uptake as function of current cell mass.
	 * 
	 * @param mass	The current cell mass 
	 * @param m0 	The initial cell mass
	 * @param V 	The average nutrient uptake.
	 * @param Zm	The estimated mass at cell should divide.	 
	 * @return 		The required uptake.
	 */
	public static double eqnLinearUptake(double mass, double m0, double V, double Zm) {
		double m = V/(Zm-m0);
		double b= V + V/2 - (m * Zm);
		double y= m * mass + b;
		return(Math.abs(y));
	}
	
	/**
	 * This is a simple case of a logistic sigmoid uptake function.
	 * 
	 * @param mass	The current cell mass 
	 * @param m0 	The initial cell mass
	 * @param V 	The average nutrient uptake.
	 * @param Zm	The estimated mass at cell should divide.	 
	 * @return 		The required uptake.
	 */
	public static double eqnSigmoidUptake(double mass, double m0, double V, double Zm) {
		 double l= -6.0;
		 double u= 6.0;
		 double m= (u - l) / (Zm - m0);		// Slope
		 double b= u - (m * Zm);			// Intercept
		 double y= m * mass + b;			// Scaled value
		 double S= 1 / (1 + Math.exp(y));
		 double v= S * V; 
		 return v;
	}
	
	/**
	 * Calculate the increment in the cell length as function of cell mass. 
	 * 
	 * @param l0	current length
	 * @param w0	current width
	 * @param w1	new width (in rod shaped bacteria it is aproximatelly constant (0.5 micrometers)
	 * @param m0	previous cell mass
	 * @param m1	new cell mass
	 * @return		the new calculated cell length
	 */
	public static double eqnLength(double l0, double w0, double w1, double m0, double m1) {
		m0= FgToKg(m0);
		m1= FgToKg(m1);
		w0 = MuToMeters(w0);
		w1 = MuToMeters(w1);
		l0 = MuToMeters(l0);
		double l1= (Math.pow(w1, 3) * m1 - Math.pow(w0, 3) * m1 + 3 * Math.pow(w0, 2) * l0 * m1) / (3 * m0 * Math.pow(w1,2));
		return MetersToMu(l1);
	}
	
	/**
	 * Calculate how the cell mass should increase when nutrien are available
	 * 
	 * @param m Current mass
	 * @param v Effective uptake
	 * @param e Metabolic efficiency
	 * @return 	The new cellular mass
	 */
	public static  double eqnGrowth(double m, double v, double e) {
		return(m + v * e);
	}
	
	/**
	 * Calculate how the cell mass should decrease when nutrien are unavailable
	 * 
	 * @param m Current mass
	 * @param v Effective uptake
	 * @param e Metabolic efficiency
	 * @return 	The new cellular mass
	 */
	public static double eqnDecay(double m, double v, double e) {
		return(m - v * e);
	}
	
	/**
	 * Calculate the probability p of mate pair formation 
	 * 
	 * @param pili		actual number of t4ss pili on cell surface
	 * @param maxpili	average maximal number of conjugative pili on e. coli cells.
	 * @return			the probability of successful conjugative events
	 */
	public static double eqnConjugationProbability(double pili, double maxpili) {
		double p= (1 - pili/maxpili);
		return p;
	}
	
	/**
	 * Convert femtograms to kilograms
	 * 
	 * @param fg femtogram value
	 * @return The corresponding kg valu
	 */
	public static double FgToKg(double fg) {
		return fg * MyParameters.SI_FEMTOGRAMS;
	}
	
	/**
	 * Kilogram to Femtogram conversion method
	 * @param kg The value in Kilograms	
	 * @return The same value in femtograms
	 */
	public static double KgToFg(double kg) {
		return kg/MyParameters.SI_FEMTOGRAMS;
	}
	
	/**
	 * Micrometers to meters conversion method
	 * @param mu The value in micrometers
	 * @return The same value in meters
	 */
	public static double MuToMeters(double mu) {
		return mu * MyParameters.SI_MICROMETERS;
	}
	
	/**
	 * Meters to micrometers conversion method
	 * @param m The value in meters
	 * @return The same value in micrometers
	 */
	public static double MetersToMu(double m) {
		return m/MyParameters.SI_MICROMETERS;
	}
	
	public static double cellVolume(double w, double l) {
		w= MuToMeters(w);
		l= MuToMeters(l);
		double v= ( (Math.pow(w,2) * Math.PI/4) * (l-w) ) + (Math.PI * Math.pow(w,3)/6);
		return v;
	}
	
	public static double cellDensityFromWetMass(double m, double w, double l) {
		m= FgToKg(m);
		double v= cellVolume(w, l);
		double d= m / v;
		return d;
	}
	
	public static double cellLengthFromWetMass(double m, double d) {
		m= FgToKg(m);
		double l= ((m/d) + 3.2725e-020)/1.9635e-013;
		return MetersToMu(l);
	}
	
}
