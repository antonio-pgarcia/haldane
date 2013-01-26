package org.holistic.bactocom;

import repast.simphony.random.RandomHelper;

/**
 * A simple Bacterial agent skeleton  
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */

public abstract class AbstractBacterium {
	// Public fields
	public static enum State { D, R, T };
	public final static double RODSHAPED_WIDTH= 0.5;		// Standard width for rod-shaped bacteria
	public final static double RODSHAPED_LENGHT= 0.6;		// Initial length value for rod-shaped bacteria
	public final static double METABOLIC_EFFICIENCY= 0.6;	// Amount of up-take converted in cell mass (0.5-0.6)
	public final static int T4SS_MAXPILI= 5;				// Average pili number per cell (E. coli 4-5)
	public final static int T4SS_REPRESSED_TIME= 1000;		// 
	public final static int T4SS_DEREPRESSED_TIME= 40;		//
	public final static double MIN_VIABLE_MASS= 120;
	public final static double MAX_LENGTH= 4;				// Maximal allowed rod-shaped length
	public final static double CYTOPLASM_DENSITY= 1200;		// Average cell density in kg/m^3
	
	// Parameters
	public final static double M= 1200;		// Average cell mass at division
	public final static double sigmaM= 120;	// Standard deviation of cell mass at division
		
	// Cell helper state variables
	private double averageUptake= Double.NaN;			// Average nutrient uptake
	private double estimatedDivisionMass= Double.NaN;	// Division mass
	
	// Super-individuals related variables
	private int individuals= 1;			// Number of individuals which this agent (super-individual) represents.
	
	// Cell vegetative state variables
	private double mass0= Double.NaN;	// The initial cell mass at t=0 in femtograms
	private double mass= Double.NaN;	// The current cell mass in femtograms.
	private double length;				// The length of cell from pole to pole.
	private double width;				// The width of cell. For rod shaped bacteria this is a fixed value of 0.5 micrometers
	
	// Conjugative state variables
	private State state0= null;			// The initial cell state.
	private State state= null;			// The current cell state.
	private int pili= 0;				// The number of conjugative pili on cell surface.
	private int piliTimer= 0;			// Time required to express the conjugative machinery.	
	private int conjugations= 0;		// Number of conjugative events performed by a single cell.
	private boolean starved= false;		// Cell is no longer growing due to nutrient exhaustion
	private boolean viable= true;		// Cell is no longer viable

	// Cell and plasmid configuration
	private double generationTime= 0;	// The bacterial generetion time G.
	private boolean onlyOriT= false;	// If true is a mobilizable non-conjugative plasmid.
	private boolean repressed= false;	// Tells if plasmid is repressed.
	
	
	/**
	 * The abstract constructor
	 * 
	 * @param m	The initial cell mass
	 * @param s The initial cell state
	 * @param G	The Generation Time
	 * @param R A boolean flag indicating the plasmid has the Tra genes repressed.
	 * @param b True if plasmid is a mobilizable plasmid.
	 */
	public AbstractBacterium(double m, State s, double G, boolean R, boolean b) {
		setOnlyOriT(b);
		setRepressed(R);
		setGenerationTime(G);
		setState(s);
		setMass(m);
		setCellSize(RODSHAPED_WIDTH, BacteriumEquations.cellLengthFromWetMass(getMass(),CYTOPLASM_DENSITY));
		
		// Estimation of division mass and the average uptake
		double Z= RandomHelper.getDistribution(MyParameters.RANDOM_DIVISION).nextDouble();
		setEstimatedDivisionMass(BacteriumEquations.eqnEstimateZm(M, sigmaM, Z));
		setAverageUptake(BacteriumEquations.eqnAverageUptake(getGenerationTime(), getMass0(), getEstimatedDivisionMass(), METABOLIC_EFFICIENCY));
		
		// Create initial T4SS conjugative pili
	}
	
	/**
	 * @return the state0
	 */
	public State getState0() {
		return state0;
	}

	/**
	 * @param state0 the state0 to set
	 */
	public void setState0(State s) {
		if(this.state0 == null)
			this.state0 = s;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State s) {
		if(this.state == null)
			setState0(s);
		this.state = s;
		populationAccount(getState0(), getState());
	}
	
	/**
	 * This method keeps the track of number and the type of individuals in
	 * simulated population. @Url MyPopulationBookkeper
	 * 
	 * @param s0 The cell state at t0
	 * @param s The cell state at current time
	 */
	private void populationAccount(State s0, State s) {
		switch (s) {
		case D:
			MyPopulationBookkeeper.getInstance().incrementD();
			break;

		case R:
			MyPopulationBookkeeper.getInstance().incrementR();
			break;
		
		case T:
			MyPopulationBookkeeper.getInstance().incrementT();
			break;
		}
		if(s0 == s) return;
		switch (s0) {
		case D:
			MyPopulationBookkeeper.getInstance().decrementD();
			break;

		case R:
			MyPopulationBookkeeper.getInstance().decrementR();
			break;
			
		case T:
			MyPopulationBookkeeper.getInstance().decrementT();
			break;
		}
	}
	
	/**
	 * @return the mass0
	 */
	public double getMass0() {
		return mass0;
	}

	/**
	 * @param mass0 the mass0 to set
	 */
	public void setMass0(double m) {
		if(Double.isNaN(this.mass0))
			this.mass0 = m;
	}

	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass the mass to set
	 */
	public void setMass(double m) {
		setMass0(m);
		this.mass = m;
	}


	/**
	 * In super-individual scheme every agent in the computational domain stands for several real
	 * individuals in the concrete domain.
	 * 
	 * @return The actual number of real bacterial cells 
	 */
	public int getIndividuals() {
		return individuals;
	}

	/**
	 * Sets the number of individuals 
	 * 
	 * @param v Number of individuals
	 */
	public void setIndividuals(int v) {
		this.individuals = v;
	}
	
	/**
	 * Increment by the individual number
	 * 
	 */
	public void incrementIndividuals() {
		this.individuals++;
	}
	
	/**
	 * Accounting the number of conjugative events performed by each cell.
	 * 
	 * @return The number of conjugative events.
	 */
	public int getConjugations() {
		return conjugations;
	}

	/**
	 * Sets the value of conjugative events which cell had performed.
	 * 
	 * @param v Number of conjugative events.
	 */
	public void setConjugations(int v) {
		this.conjugations = v;
	}
	
	/**
	 * Increment the number of conjugative events.
	 */
	public void incrementConjugations() {
		this.conjugations++;
	}
	
	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * A simple wrapper to set both cell dimensions
	 * @param w	The cell width
	 * @param l	The cell length
	 */
	public void setCellSize(double w, double l) {
		setWidth(w);
		setLength(l);
	}
	
	/**
	 * A simple wrapper to set several cell state variables at once
	 * @param m	The cell mass
	 * @param w	The cell width
	 * @param l	The cell length
	 * @param s	The cell state
	 * @param p	The number of conjugative pili on cell surface
	 * @param t	The timer for the expression of a new pilus
	 */
	public void setCellState(double m, double w, double l, State s,int p, int t) {
		setMass(m);
		setCellSize(RODSHAPED_WIDTH, BacteriumEquations.cellLengthFromWetMass(getMass(),CYTOPLASM_DENSITY));
		setPili(p);
		setPiliTimer(t);
		if(getState() != s) setState(s);
	}
	
	public void setCellState(double m, State s, int p ) {
		setMass(m);
		setCellSize(RODSHAPED_WIDTH, BacteriumEquations.cellLengthFromWetMass(getMass(),CYTOPLASM_DENSITY));
		setPili(p);
		if(getState() != s) setState(s);
	}
	
	/**
	 * @return the starved
	 */
	public boolean isStarved() {
		return starved;
	}

	/**
	 * @param starved the starved to set
	 */
	public void setStarved(boolean starved) {
		this.starved = starved;
	}

	/**
	 * @return the viability state
	 */
	public boolean isViable() {
		setViable(!(getMass() < MIN_VIABLE_MASS));
		return viable;
	}

	/**
	 * @param starved the cell viability state
	 */
	public void setViable(boolean v) {
		this.viable= v;
	}
	
	/**
	 * @return the pili
	 */
	public int getPili() {
		return pili;
	}

	/**
	 * @param pili the pili to set
	 */
	public void setPili(int pili) {
		this.pili = pili;
	}

	/**
	 * Helper method to evaluate the cell state
	 * 
	 * @return true is this is a plasmid Donor
	 */
	public boolean isDonor() {
		return (getState() == State.D);
	} 
	
	/**
	 * Helper method to evaluate the cell state
	 * 
	 * @return true is this is a plasmid Recipient
	 */
	public boolean isRecipient() {
		return (getState()== State.R);
	}
	
	/**
	 * Helper method to evaluate the cell state
	 * 
	 * @return true is this is a plasmid Transconjugant
	 */
	public boolean isTransconjugant() {
		return (getState() == State.T);
	}
	
	public int getDonorConjugations() {
		return (isDonor() ? getConjugations() : 0);
	}
	
	public int getTransconjugantConjugations() {
		return (isTransconjugant() ? getConjugations() : 0);
	}

	/**
	 * @return the averageUptake
	 */
	public double getAverageUptake() {
		return averageUptake;
	}

	/**
	 * @param averageUptake the averageUptake to set
	 */
	public void setAverageUptake(double v) {
		if(Double.isNaN(this.averageUptake))
		this.averageUptake= v;
	}

	/**
	 * @return the estimatedDivisionMass
	 */
	public double getEstimatedDivisionMass() {
		return estimatedDivisionMass;
	}

	/**
	 * @param estimatedDivisionMass the estimatedDivisionMass to set
	 */
	public void setEstimatedDivisionMass(double v) {
		if(Double.isNaN(this.estimatedDivisionMass))
			this.estimatedDivisionMass= v;
	}

	/**
	 * @return the generationTime
	 */
	public double getGenerationTime() {
		return generationTime;
	}

	/**
	 * @param generationTime the generationTime to set
	 */
	public void setGenerationTime(double v) {
		this.generationTime= v;
	}

	/**
	 * @return the onlyOriT
	 */
	public boolean isOnlyOriT() {
		return onlyOriT;
	}

	/**
	 * @param onlyOriT the onlyOriT to set
	 */
	public void setOnlyOriT(boolean v) {
		this.onlyOriT= v;
	}

	/**
	 * @return the repressed
	 */
	public boolean isRepressed() {
		return repressed;
	}

	/**
	 * @param repressed the repressed to set
	 */
	public void setRepressed(boolean v) {
		this.repressed= v;
	}
	

	/**
	 * @return the piliTimer
	 */
	public int getPiliTimer() {
		return piliTimer;
	}

	/**
	 * @param piliTimer the piliTimer to set
	 */
	public void setPiliTimer(int t) {
		this.piliTimer = t;
	}

	
}
