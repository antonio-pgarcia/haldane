package org.holistic.bactocom;

import java.util.List;

import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
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
	// Internal state variables
	protected double C0= 0;
		
	/**
	 * Constructs the cell agent
	 * 
	 * @param m	The initial cell mass
	 * @param s The initial cell state
	 * @param G	The Generation Time
	 * @param R A boolean flag indicating the plasmid has the Tra genes repressed.
	 * @param b True if plasmid is a mobilizable plasmid.
	 */
	public Bacterium(double m, State s, double G, boolean R, boolean b) {
		super(m, s, G, R, b);
		// TODO Auto-generated constructor stub
		C0= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
				
	}
	
	/**
	 * Constructs the cell agent using more state variables
	 * 
	 * @param m	The initial cell mass
	 * @param s The initial cell state
	 * @param G	The Generation Time
	 * @param R A boolean flag indicating the plasmid has the Tra genes repressed.
	 * @param b True if plasmid is a mobilizable plasmid.
	 * @param pili The pili counter
	 */
	public Bacterium(double m, State s, double G, boolean R, boolean b, int pili) {
		super(m, s, G, R, b);
		// TODO Auto-generated constructor stub
		setPili(pili);
		C0= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
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
		GridValueLayer vl = (GridValueLayer)ContextUtils.getContext(this).getValueLayer("Nutrients");
		
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
	
	/**
	 * This method implements the Cellular Growth. 
	 * 
	 */
	public void UptakeAndGrowth() {
		// Required uptake estimation
		int N= getIndividuals();
		//double r= BacteriumEquations.eqnLinearUptake(getMass(),getMass0(), getAverageUptake(), getEstimatedDivisionMass());
		double r= BacteriumEquations.eqnSigmoidUptake(getMass(),getMass0(), getAverageUptake(), getEstimatedDivisionMass());
		//double r= BacteriumEquations.eqnUptake(getMass(),getAverageUptake(), getEstimatedDivisionMass());
		
		double v= gridUptake(r * N);
		if(v == 0)
			v= gridDiffusion(r * N);
		
		v= v/N;
		
		// Metabolic penalization due to plasmid infection.
		if(isDonor() || isTransconjugant()) v= v - v * (MyParameters.getPlasmidPenalty()/100);

		setStarved((v <= 0));
		
		// Cell viability check
		if(isViable()) {
			double m0= getMass();
			setMass((v > 0 ? BacteriumEquations.eqnGrowth(m0, v, METABOLIC_EFFICIENCY) : BacteriumEquations.eqnDecay(m0, r, METABOLIC_EFFICIENCY)));
			setLength(BacteriumEquations.eqnLength(getLength(), getWidth(), getWidth(), m0, getMass()));
		}

	}
	
	/*
	 * Division
	 */
	public void Division() {
		// Sample a random variable for cell division decision rule
		double Z= RandomHelper.getDistribution(MyParameters.RANDOM_DIVISION).nextDouble();
		double Zm= BacteriumEquations.eqnEstimateZm(M, sigmaM, Z);
		
		// Division mass threshold
		if(getMass() >= Zm || getLength() > MAX_LENGTH) {
			//System.out.println("G=" + (RunEnvironment.getInstance().getCurrentSchedule().getTickCount() - C0) + " V=" + BacteriumEquations.eqnUptake(getMass(),getAverageUptake(), getEstimatedDivisionMass()));
			C0= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			List<GridPoint> emptySites = MyNeighborhood.getEmptyMooreNeighborhood(this,1);
			if (emptySites.size() > 0) {
				Bacterium d= getDaughterCell();
				int x= emptySites.get(0).getX();
				int y= emptySites.get(0).getY();	
				addToGrid(d, x, y);
			} else {
				List<GridPoint> sites = MyNeighborhood.getMooreNeighborhood(this,1,true);
				Bacterium d= getDaughterCell();
				int x= sites.get(0).getX();
				int y= sites.get(0).getY();	
				addToGrid(d, x, y);
			}
		}
	}
	
	/**
	 * Creates a new cell and adjust the parameters of current cell
	 * @return A new born cell
	 */
	public Bacterium getDaughterCell() {
		
		// Evaluate plasmid loss probability
		Uniform u= (Uniform) RandomHelper.getDistribution(MyParameters.RANDOM_PLASMIDLOSS);
		State s= (u.nextDouble() > (1 - MyParameters.getPlasmidLossProbability()) ? State.R : getState());

		double mass= getMass()/2;
		int p= getPili()/2;
		
		Bacterium daughter= new Bacterium(mass,s,getGenerationTime(), isRepressed(), isOnlyOriT(), p);
		setCellState(mass, getState(), p);
		return daughter;
	}
	
	
	/**
	 * Add a bacterial cell to a grid position
	 * @param daughter
	 * @param x
	 * @param y
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addToGrid(Bacterium daughter, int x, int y) {
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid) context.getProjection("Grid");
		context.add(daughter);
		boolean moved= grid.moveTo(daughter,x,y);
		if(!moved) {
			System.out.println("Error moving in division!!");
			context.remove(daughter);
		}
	}

	/**
	 * This method handle the logic of pili expression for both constitutivelly expressed
	 * and repressed plasmids. It is not completelly understood, but the pili expression, in 
	 * F like plasmids, seems to be positively regulated by the traJ gene product which in turn
	 * is controled by the FinO/FinP RNA anti-sense. 
	 * Here we use a simple probabilistic scheme for pilus expression 
	 *   
	 * 
	 */
	public void PiliExpression() {
		if(getState() == State.R || (getState() == State.T && isOnlyOriT()) ) return;
		
		Poisson poisson= (Poisson) (isRepressed() ? RandomHelper.getDistribution(MyParameters.RANDOM_REPRESSED) : RandomHelper.getDistribution(MyParameters.RANDOM_DEREPRESSED) );
		if(poisson.nextDouble() > 0 && getPili() < T4SS_MAXPILI) {
			Uniform u= (Uniform) RandomHelper.getDistribution(MyParameters.RANDOM_PILUS);
			int p= u.nextIntFromTo((getPili() > 0 ? getPili() : 1),T4SS_MAXPILI);
			if(p == getPili()) return;
			double b= getEstimatedDivisionMass() * (p/T4SS_MAXPILI * MyParameters.getDerepressionPenalty()/100);
			double m= getMass();
			setMass((m-b > MIN_VIABLE_MASS ? m - b : MIN_VIABLE_MASS));
			setLength(BacteriumEquations.cellLengthFromWetMass(getMass(),CYTOPLASM_DENSITY));
			setPili((int) p);
		}
	}
		
	
	public boolean isReadyForConjugate() {
		// Sample a random variable for cell division decision rule
		double Z= RandomHelper.getDistribution(MyParameters.RANDOM_DIVISION).nextDouble();
		double Zm= BacteriumEquations.eqnEstimateZm(M, sigmaM, Z);
					
		// Decision rule for conjugation
		return (getMass() >= Zm * (MyParameters.getMinCellCycle()/100));
	}
	
	/**
	 * Conjugation process.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void Conjugation() {
		if(getState() == State.D || (getState() == State.T && !isOnlyOriT()) ) {
			
			if(!isReadyForConjugate()) return;
			
			// Evaluate the probability of mating-pair formation as function of pili number
			Uniform u= (Uniform) RandomHelper.getDistribution(MyParameters.RANDOM_CONJUGATION);
			if( u.nextDouble() < BacteriumEquations.eqnConjugationProbability(getPili(), T4SS_MAXPILI)) return;
				
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
				if(b.getState() == State.R && b.isReadyForConjugate()) {
			 		b.setState(State.T);
					
			 		// Cell is penalized due to plasmid transfer burden
					setMass(getMass() - getEstimatedDivisionMass() * (MyParameters.getConjugationPenalty()/100));
					incrementConjugations();
					break;
				}
			}	
		}
	}
	
	/**
	 * Shoving relaxation process. Adapted from BacSim.
	 * "BacSim, a simulator for individual-based modelling of bacterial colony growth"
	 * 1998, Jan-Ulrich Kreft, Ginger Booth and Julian W. T. Wimpenny
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void Shoving() {
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid) context.getProjection("Grid");
		GridPoint mypt= grid.getLocation(this);

		double k= 1.3; // Maximun overlap parameter
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
			newLocation[i]= (int) Math.round((mypt.getCoord(i) + shoving[i]));
		}
		
		boolean moved= grid.moveTo((Object) this, newLocation);
		if(!moved) 
			context.remove(this);
	}
		
	@ScheduledMethod(start=1,interval=1,shuffle=true)
	public void step() {
		if(!isViable()) return;
		Shoving();
		UptakeAndGrowth();
		Division();
		PiliExpression();
		Conjugation();
	}

}
