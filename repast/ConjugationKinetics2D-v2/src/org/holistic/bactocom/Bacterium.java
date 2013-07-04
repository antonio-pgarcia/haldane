package org.holistic.bactocom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgroups.blocks.UpdateException;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.valueLayer.GridValueLayer;


/**
 * The bacterial cell agent implementation for model developed for FdlC group paper 
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */
public class Bacterium extends AbstractBacterium {
	private boolean OVERLAPCELLS= false;
	// Internal state variables
	private double t0= 0;						// The t0 of inter-division time
	private double G= 0;						
	private double Zg;							// The generation time random variable
	private double Gc;							// Estimated point of cell cycle for division
	private int EEX= 0;							// Entry Exclusion generations settlement 						
	
	// Random generators
	private Poisson P= null;
	private Normal Z= null;
	private Normal ZGamma= null;
	private Uniform U= null;
	
	// Parameters 
	private double gamma0= 0;
	
	/**
	 * Construct an agent with a default genotype
	 * 
	 * @param s	Conjugative state
	 * @param p Parameter collection
	 */
	public Bacterium(State s, BacteriumParameters p) {
		super(s, p);
		init(s, p);
	}
	
	/**
	 * Construct an agent with a user provided genotype, used for division
	 * 
	 * @param s	Conjugative state
	 * @param p Parameter collection
	 */
	public Bacterium(State s, BacteriumParameters p, Genotype g) {
		super(s, p, g);
		init(s,p);		
	}
	
	/**
	 * Constructor helper method
	 * 
	 * @param s	Conjugative state
	 * @param p Parameter collection
	 */
	private void init(State s, BacteriumParameters p) {
		t0= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();

		switch(s) {
			case R:
				G= (Integer) p.getValue(BacteriumParameters.Gr);
				break;
			case D:
				G= (Integer) p.getValue(BacteriumParameters.Gd);
				break;
			case T:
				G= (Integer) p.getValue(BacteriumParameters.Gt);
				break;	
		}
		gamma0= (Double) p.getValue(BacteriumParameters.GAMMA);
		
		Z= (Normal) RandomHelper.getDistribution(MyParameters.RANDOM_DIVISION);
		ZGamma= (Normal) RandomHelper.getDistribution(MyParameters.RANDOM_GAMMA0);
		P= (Poisson) RandomHelper.getDistribution(MyParameters.RANDOM_CONJUGATION);
		U= (Uniform) RandomHelper.getDistribution(MyParameters.RANDOM_UNIFORM1);
		
		//Zg= BacteriumEquations.eqnZ(G, G * 0.35D, Z.nextDouble());
		Zg= BacteriumEquations.eqnZ(G, G * 0.25D, Z.nextDouble());
	}
	
	public double getGc() {
		Gc= Zg * U.nextDouble();
		return Gc;
	}
	
	public double getZg() {
		Zg= BacteriumEquations.eqnZ(G, G * 0.25D, Z.nextDouble());
		return Zg;
	}
	
	public double getZGamma() {
		double v= BacteriumEquations.eqnZ(gamma0, gamma0 * 0.1D, ZGamma.nextDouble());
		return v;
	}
	
	public double getC() {
		return(P.nextInt());
	}
	
	/**
	 * The uptake process
	 * 
	 * @return Nutrient particle
	 */
	public double procUptake() {
		double v= gridUptake(1);
		if(v == 0)
			v= gridDiffusion(1);
		return v;
	}
	
	/**
	 * 
	 */
	public void procLag() {
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		//double delta= t - t0;
		
		double lag= BacteriumEquations.getLagFromG(G);
		double u= (1 + (lag - 1)) * U.nextDouble();
		if( t <  u ) {
			t0= t;
		}
	}
	
	public void initEeX() {
		EEX= (int) ( (2 + (5 - 2)) * U.nextDouble() ); 
	}
	
	public int getEeX() {
		return EEX;
	}
	
	public void setEeX(int v) {
		EEX= v;
	}
	
	/**
	 * The cellular division process
	 */
	public void procDivision() {
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		double delta= t - t0;
		//System.out.println("donors= " + MyPopulationBookkeeper.getInstance().getD());
		if( delta >= getZg() ) {
			this.t0= t;
			List<GridPoint> emptySites= null;
		
			for(int i= 1; i<=3; i++) {
				emptySites= MyNeighborhood.getEmptyMooreNeighborhood(this,1);
			
				if (emptySites.size() > 0) {
					if(procUptake() <= 0) {
						setStarved(true);
						return;
					}
					Bacterium d= getDaughter();
					int x= emptySites.get(0).getX();
					int y= emptySites.get(0).getY();
					addToGrid(d, x, y);
					break;
				} else {
					if(OVERLAPCELLS) {
						if(procUptake() <= 0) {
							setStarved(true);
							return;
						}
						List<GridPoint> sites = MyNeighborhood.getMooreNeighborhood(this,1,true);
						Bacterium d= getDaughter();
						int x= sites.get(0).getX();
						int y= sites.get(0).getY();	
						addToGrid(d, x, y);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Check whether this cell is a conjugative one
	 * 
	 * @return true for conjugative cells, false for recipient cells 
	 */
	public boolean isConjugativeHost() {
		return(getState() == State.D || (getState() == State.T && !(Boolean) getParameters().getValue(BacteriumParameters.isOnlyOriT)));
	}
	
	public boolean isDonor() {
		return(getState() == State.D);
	}
	
	public boolean isConjugativeT() {
		return((getState() == State.T && !(Boolean) getParameters().getValue(BacteriumParameters.isOnlyOriT)));
	}
	
	public double getTime() {
		double v= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return v;
	}
	
	public double getDelta() {
		double v= getTime() - t0;
		return v;
	}
	
	/**
	 * This is the simplest decision rule. Just check the local gamma and compare it
	 * to the desired gamma0 parameter.
	 * 
	 */
	public void procConjugation0() {

		if(isConjugativeHost() ) {
			Bacterium b= pickRandomNeighbor(State.R);
			if(b == null) return;
			updateEncounters(b);

			if(getGammaL() > this.gamma0) return;
			if(isGreaterThanExperimental()) return;
			
			b.setState(State.T);
			updateConjugations();

		}
	}
	
	/**
	 * The basic decision rule to which a minimum cell cycle has been added.
	 * 
	 */
	public void procConjugation1() {

		if(isConjugativeHost() ) {
			Bacterium b= pickRandomNeighbor(State.R);
			if(b == null) return;
			updateEncounters(b);
	
			if(getGammaL() > getZGamma()) return;
			//if( getDelta() < (getZg() * 0.7) ) return;
			if(isGreaterThanExperimental()) return;
			b.setState(State.T);
			updateConjugations();
		}
	}
	
	/**
	 * The basic decision rule to which a minimum cell cycle has been added.
	 * 
	 */
	public void procConjugation2() {

		if(isConjugativeHost() ) {
			Bacterium b= pickRandomNeighbor(State.R);
			if(b == null) return;
			updateEncounters(b);
	
			
			if(isDonor() && getGammaL() > getZGamma()) return;
			if(isGreaterThanExperimental()) return;
			b.setState(State.T);
			b.initEeX();
			updateConjugations();
		}
	}
	
	
	public void procSuperInfection() {
		if(isConjugativeHost()) {
			if(getConjugations() > 3) return; 
			Bacterium b= pickRandomNeighbor(State.T);
			if(b == null) return;
			if(b.getEeX() <= 0) return;
			updateEncounters(b);
			if(isDonor() && getGammaL() > getZGamma()) return;
			//if(isGreaterThanExperimental()) return;
			b.setState(State.T);
			//System.out.println("Superinfections:: " + b.getEeX() + " C:: " + getConjugations());
			updateConjugations();
		}
	}
	
	/**
	 * Conjugation process.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void procConjugation10() {

		if( isConjugativeHost() ) {

			double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			double delta= t - t0;
			
			if(delta < (getZg() * 0.6)) return;
			
			// Conjugation code
			Grid grid = (Grid) ContextUtils.getContext(this).getProjection("Grid");
			List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, 1,false);
			for(GridPoint pt : neighbors) {
				Bacterium b;
				List<Bacterium> bl= MyNeighborhood.ListFromIterator(grid.getObjectsAt(pt.getX(),pt.getY()).iterator());
				if(bl.size() > 0)
					b= bl.get(0);
				else
					b= (Bacterium) grid.getObjectAt(pt.getX(),pt.getY());
				
				// No neighbor to plasmid transfer 
				if(b == null) continue;
				
				// All conditions are meet to plasmid transfer
				if(b.getState() == State.R) {
					
					updateEncounters(b);


					
					
					//if(getConjugations() > C) return;
					if(getConjugations() > P.nextInt()) return;
					
			 		
					b.setState(State.T);
					updateConjugations();
					break;
				}
			}	
			
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Bacterium pickRandomNeighbor(State s) {
		Bacterium b= null;
	
		Grid grid = (Grid) ContextUtils.getContext(this).getProjection("Grid");
		
		List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, 1,false);
		for(GridPoint pt : neighbors) {
		
			List<Bacterium> bl= MyNeighborhood.ListFromIterator(grid.getObjectsAt(pt.getX(),pt.getY()).iterator());
			if(bl.size() > 0)
				b= bl.get(0);
			else
				b= (Bacterium) grid.getObjectAt(pt.getX(),pt.getY());
			
			// No neighbor  
			if(b == null || b.getState() != s) continue;
		}
		b= (b == null || b.getState() != s ? null : b);
		return b;
	}
	
	public boolean isGreaterThanExperimental() {
		boolean v= false;
		if(MyParameters.isAutoFit()) {
			ModelRatesHelper.getInstance().gatherData();
			double error= ModelRatesHelper.getInstance().getFitnessError();
			v= (error < 0 ? true : false);
		}
		return v;
	}
	
	/**
	 * Shoving relaxation process. Adapted from BacSim.
	 * 
	 * "BacSim, a simulator for individual-based modelling of bacterial colony growth"
	 * 1998, Jan-Ulrich Kreft, Ginger Booth and Julian W. T. Wimpenny
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void procShoving() {
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid) context.getProjection("Grid");
		GridPoint mypt= grid.getLocation(this);

		double k= 1.30D; // Maximun overlap parameter
		double md= 3;
		double ss= 0D;
		double[] u= new double[mypt.dimensionCount()];
		double[] shoving= new double[mypt.dimensionCount()];
		
		// Initialization of shoving relaxation vector
		for(int i= 0; i< mypt.dimensionCount(); i++) {
			shoving[i]= 0D;
		}
		
		List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, 1, false);
		for(GridPoint pt : neighbors) {
			double d= grid.getDistance(mypt, pt);
			List<Bacterium> bl= MyNeighborhood.ListFromIterator(grid.getObjectsAt(pt.getX(),pt.getY()).iterator());
			Bacterium b = (bl.size() > 0 ? b= bl.get(0) : (Bacterium) grid.getObjectAt(pt.getX(),pt.getY()));
			if(b == null) continue;
			double r= getLength();
			double r1= b.getLength();
			
			if( (k*r + r1) < d) return;
			ss= (k*r + r1 - d)/2;
			u= MyNeighborhood.getUnitVector(this, mypt, pt);
			
			for(int i= 0; i< mypt.dimensionCount(); i++) {
				shoving[i]+= ss * u[i];
			}
		}
		
		int[] newLocation= new int[mypt.dimensionCount()];
		for(int i= 0; i< mypt.dimensionCount(); i++) {
			if(Math.abs(shoving[i]) > md)
				return;
			newLocation[i]= (int) Math.round((mypt.getCoord(i) + shoving[i]));
		}
		
		boolean moved= grid.moveTo((Object) this, newLocation);
		if(!moved) 
			context.remove(this);
	}
	
	/**
	 * The getLength method estimates the bacterial length parameter
	 * as function of the current bacterial cycle point. It is actually an 
	 * linear approximation based on generation time (G). 
	 * 
	 * @return l the estimated length 
	 */
	public double getLength() {
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		double delta= t - t0;
		double l= BacteriumEquations.eqnSimpleLinearFit(delta, getZg(), BacteriumParameters.MIN_LENGTH/2, BacteriumParameters.MAX_LENGTH/2);
		return l;
	}
	
	/**
	 * This method implements the uptake on the agent's current grid cell.
	 * 
	 * @param r the required calculated uptake value
	 * @return 	the real uptake which depends on nutrient availability on current grid cell
	 */
	@SuppressWarnings("rawtypes")
	public double gridUptake(double r) {
		double v= 0;
		GridValueLayer vl = (GridValueLayer)ContextUtils.getContext(this).getValueLayer("Nutrients");
		Grid grid = (Grid)ContextUtils.getContext(this).getProjection("Grid");
		GridPoint pt= grid.getLocation(this); 
		
		double c= vl.get(pt.getX(),pt.getY());
		if(c > 0) {
			v=(c >= r ? r : c);
			vl.set(c-v, pt.toIntArray(null));
		} 
		return v;
	}
	
	/**
	 * This is an adaptation of the diffusion method as described in the paper:
	 * "Modeling the spatial dynamics of plasmid transfer and persistence."
	 * Krone, S.M., R. Lu, R. Fox, H. Suzuki, and E.M. Top. 2007. Microbiology 153: 2803-2816.  
	 * 
	 * @param r	the required calculated uptake  
	 * @return	the real uptake which depends on nutrient availability on grid 
	 */
	public double gridDiffusion(double r) {
		double v= 0;
		GridValueLayer vl = (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Nutrients");
		
		for(int i= 1; i<= 3; i++) {
			List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, i, false);
			for(GridPoint p : neighbors) {
				double c= vl.get(p.getX(),p.getY());
				if(c > 0) {
					v=(c >= r ? r : c);
					vl.set(c-v, p.toIntArray(null));
					break;
				} 
			}
		}
		return(v);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int countByState(State s) {
		int c= 0;
		
		Grid grid = (Grid) ContextUtils.getContext(this).getProjection("Grid");
		List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, 1,false);
		for(GridPoint pt : neighbors) {
			Bacterium b;
			List<Bacterium> bl= MyNeighborhood.ListFromIterator(grid.getObjectsAt(pt.getX(),pt.getY()).iterator());
			b= (bl.size() > 0 ? bl.get(0) : (Bacterium) grid.getObjectAt(pt.getX(),pt.getY()));  
			// No neighbor cells 
			if(b == null) continue;
			if(b.getState() == s)
				c++;
		}
		return c;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int countUnrelated() {
		int v= 0;
		
		Grid grid = (Grid) ContextUtils.getContext(this).getProjection("Grid");
		List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, 1,false);
		for(GridPoint pt : neighbors) {
			Bacterium b;
			List<Bacterium> bl= MyNeighborhood.ListFromIterator(grid.getObjectsAt(pt.getX(),pt.getY()).iterator());

			b= (bl.size() > 0 ? bl.get(0) : (Bacterium) grid.getObjectAt(pt.getX(),pt.getY()));  
			// No neighbor cells 
			if(b == null) continue;
			
			if( !getGenotype().isEqual(b.getGenotype()) ) {
				v++;
			}
		}
		return v;
	}
	
	public boolean isNutrientAvailable() {
		boolean b= false;
		GridValueLayer vl = (GridValueLayer) ContextUtils.getContext(this).getValueLayer("Nutrients");
		
		for(int i= 1; i<= 4; i++) {
			List<GridPoint> neighbors = MyNeighborhood.getMooreNeighborhood(this, i, true);
			for(GridPoint p : neighbors) {
				double c= vl.get(p.getX(),p.getY());
				if(c > 0) {
					b= true;
					break;
				}
			}
		}
		return b;
	}
	
	/**
	 * Create a genetically identical cell
	 * 
	 * @return daughter A new Bacterium instance
	 */
	private Bacterium getDaughter() {
		EEX--;
		Bacterium daughter= new Bacterium(getState(), getParameters(), getGenotype());
		daughter.setEeX(getEeX());
		return daughter;
	}
	
	/**
	 * Places a new cell into the simulation lattice
	 * 
	 * @param daughter
	 * @param x
	 * @param y
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addToGrid(Bacterium daughter, int x, int y) {
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid) context.getProjection(MyParameters.GRID);
		context.add(daughter);
		boolean moved= grid.moveTo(daughter,x,y);
		if(!moved) {
			System.out.println("Error moving in division!!");
			context.remove(daughter);
		}
	}
		
	/**
	 * The stepDivision method performs the cellular division logic  
	 */
	@ScheduledMethod(start=1,interval=1,shuffle=true)
	public void stepDivision() {
		if(isStarved()) return;
		//procLag();
		procDivision();
	}
	
	/**
	 * The stepShoving method performs the shoving relaxation logic  
	 */
	@ScheduledMethod(start=1,interval=1,shuffle=true)
	public void stepShoving() {
		if(isStarved()) return;
		procShoving();
	}
	
	/**
	 * The stepConjugation method performs the cell to cell encounters 
	 * and the conjugative logic.  
	 *   
	 */
	@ScheduledMethod(start=1,interval=1,shuffle=true)
	public void stepConjugation() {
		if(isStarved()) return;
		procConjugation2();
	}
	
	@ScheduledMethod(start=1,interval=1,shuffle=true)
	public void stepSuperInfection() {
		if(isStarved()) return;
		procSuperInfection();
	}

}
