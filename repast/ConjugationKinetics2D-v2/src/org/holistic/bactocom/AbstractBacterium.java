package org.holistic.bactocom;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple Bacterial agent skeleton  
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */

public abstract class AbstractBacterium {
	// Agent Parameters
	private BacteriumParameters parameters;
	private Genotype Id; 
	private Genotype genotype;
	
	
	// Vegetative state variables
	private double width= 0;
	private double length= 0;
	
	// Conjugative state variables
	private State state0= null;			// The initial cell state.
	private State state= null;			// The current cell state.
	
	// Super-individuals related variables
	private int individuals= 1;			// Number of individuals which this agent (super-individual) represents.
	
	// Account variables
	private List<Genotype> neighbors;	// List for for accounting D|T to R encounters
	private List<String> nn;			// List for for accounting D|T to R encounters
	private int encounters= 0;			// Number of D|T to R encounters
	private int conjugations= 0;		// Number of conjugative events performed by a single cell.
	
	private boolean starved= false;		// Cell is no longer growing due to nutrient exhaustion
	
	
	/**
	 * The abstract constructor
	 * 
	 * @param s The initial cell state
	 * @param p The agent parameter collection
	 * 
	 */
	public AbstractBacterium(State s, BacteriumParameters p) {
		setState(s);
		setParameters(p);
		setGenotype(new Genotype());
		getGenotype().createGenome();
		createId();
		neighbors= new ArrayList<Genotype>();
		nn= new ArrayList<String>();
	}
	
	public AbstractBacterium(State s, BacteriumParameters p, Genotype g) {
		setState(s);
		setParameters(p);
		setGenotype(g);
		createId();
		neighbors= new ArrayList<Genotype>();
		nn= new ArrayList<String>();
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
	private void setState0(State s) {
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
		MyPopulationBookkeeper.getInstance().populationAccount(getState0(), getState());
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
	 * @return the parameters
	 */
	public BacteriumParameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(BacteriumParameters parameters) {
		this.parameters = parameters;
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
	 * Increments the number of conjugative events
	 */
	public void incConjugations() {
		this.conjugations++;
	}
	
	public int getEncounters() {
		return encounters;
	}
	
	public void incEncounters() {
		this.encounters++;
	}
	
	public double getGammaL() {
		double v= ((double) getConjugations())/((double) getEncounters());
		v= Double.isNaN(v) ? 0 : v;
		return v;
	}
	
	/**
	 * Updates both local and global conjugation counter
	 * 
	 */
	public void updateConjugations() {
		
		// Updates the number of CELLS which had performed at least one conjugative transfer
		if(getConjugations() == 0)
			MyPopulationBookkeeper.getInstance().incrementNc();
		
		incConjugations();
		
		if(isDonor()) 
			MyPopulationBookkeeper.getInstance().incrementCd();
		else if(isTransconjugant())
			MyPopulationBookkeeper.getInstance().incrementCt();
	}
	
	/**
	 * Updates both local and global encounter account
	 * 
	 * @param b The neighbor cell
	 */
	public void updateEncounters(Bacterium b) {
		if(!neighbors.contains(b.getId())) {
			neighbors.add(b.getId());
			incEncounters();
			MyPopulationBookkeeper.getInstance().incrementE();
			switch(getState()) {
				case D: {
					MyPopulationBookkeeper.getInstance().incrementEd();
					break;
				}
				
				case T: {
					MyPopulationBookkeeper.getInstance().incrementEt();
					break;
				}
			}
		}
	}
	
	public void updateEncounters1(Bacterium b) {
		if(!nn.contains(b.getId().getGenome())) {
			nn.add(b.getId().getGenome());
			incEncounters();
			MyPopulationBookkeeper.getInstance().incrementE();
		} else {
			System.out.println("Already present= " + b.getId().getGenome());
		}
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
	 * @return the genotype
	 */
	public Genotype getGenotype() {
		return genotype;
	}

	/**
	 * @param genotype the genotype to set
	 */
	public void setGenotype(Genotype genotype) {
		this.genotype = genotype;
	}
	
	/**
	 * @return the Agent Id
	 */
	public Genotype getId() {
		return Id;
	}

	/**
	 * @param v the genotype to set id
	 */
	private void createId() {
		this.Id= new Genotype();
		this.Id.createGenome();
	}
	
}
