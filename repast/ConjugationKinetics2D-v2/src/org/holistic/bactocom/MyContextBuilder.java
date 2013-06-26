package org.holistic.bactocom;


import java.util.Date;

import cern.jet.random.Normal;
import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Repast context builder for model developed for FdlC group paper 
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */
public class MyContextBuilder implements ContextBuilder<Object> {
	private int width;
	private int height; 
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Context build(Context<Object> context) {
		GridFactory factory = null;
		Grid <Object> grid = null;
		GridBuilderParameters<Object> parameters= null;

		configSimulation();
		
		
		width = MyParameters.getWidth();
		height = MyParameters.getHeight();
		
		context.setId(MyParameters.CONTEXT);
		factory= GridFactoryFinder.createGridFactory(null);
		
		parameters= GridBuilderParameters.multiOccupancy2D(new RandomGridAdder(), new WrapAroundBorders(), width, height);	
		grid= factory.createGrid(MyParameters.GRID, context, parameters);
		
		initRandomStreams();
		initPopulation(context, grid);
		initNutrients(context);
				
		grid.setAdder(new SimpleGridAdder<Object>());
		return context;
	}
	

	/**
	 * Config most common simulation parameters 
	 */
	private void configSimulation() {
		System.out.println("Initializing/Configuring Simulation (time= " + ( new Date()).toString() + ")" );
		if(RunEnvironment.getInstance().isBatch()){
			double endAt= 10 * 60;	// 10 hours of simulation
			RunEnvironment.getInstance().endAt(endAt);
		} else {
			double endAt= 10 * 60;	// 10 hours of simulation
			RunEnvironment.getInstance().endAt(endAt);
		}
	}
	
	/**
	 * Random Number generators initializer
	 */
	private void initRandomStreams() {
		RandomHelper.setSeed(MyParameters.getRandomSeed());
		
		RandomHelper.registerDistribution(MyParameters.RANDOM_DIVISION, (Normal) RandomHelper.createNormal(0, 1));
		RandomHelper.registerDistribution(MyParameters.RANDOM_GAMMA0, (Normal) RandomHelper.createNormal(0, 1));
		RandomHelper.registerDistribution(MyParameters.RANDOM_NEIGHBORHOOD, (Uniform) RandomHelper.createUniform());
		RandomHelper.registerDistribution(MyParameters.RANDOM_CONJUGATION, (Poisson) RandomHelper.createPoisson(MyParameters.getIntrinsicConjugationRate()));
		RandomHelper.registerDistribution(MyParameters.RANDOM_TIME2CONJUGATE, (Uniform) RandomHelper.createUniform());
	}
	
	/**
	 * Creates the initial population of D and R cells 
	 * and distribute it randomly across the grid.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPopulation(Context<Object> context, Grid grid) {
		MyPopulationBookkeeper.getInstance().reset();
		// Population initialization
		//int N= MyParameters.getN0();
		
		InitialConcentration ic= new InitialConcentration(MyParameters.getM0());
		int N= (int) Math.round(ic.getScaledValue(width, height));
		int donors= (int) (N * MyParameters.getInitialDonorDensity());
		int recipient= N - donors;
		double mS= ic.getMinSurfacePerCell();
		
		MyPopulationBookkeeper.getInstance().setN0(N);
		
		// Create the bacteria parameter collection 
		BacteriumParameters p= BacteriumParamtersFactory.getParameters();
		p.setValue(BacteriumParameters.GAMMA, MyParameters.getIntrinsicConjugationRate());
		p.setValue(BacteriumParameters.Gr, MyParameters.getGr());
		p.setValue(BacteriumParameters.Gd, MyParameters.getGd());
		p.setValue(BacteriumParameters.Gt, MyParameters.getGt());
		p.setValue(BacteriumParameters.isOnlyOriT, MyParameters.isOnlyOriT());
		p.setValue(BacteriumParameters.EQUATION, MyParameters.getEquation());
		
		// Experimental curve initialization
		ModelRatesHelper.getInstance().setFitnessFunction(MyParameters.getEquation());

		
		for (int i = 0; i < donors ; i++) {
			context.add(new Bacterium(State.D, p));
		}
				
		for (int i = 0; i < recipient ; i++) {
			context.add(new Bacterium(State.R, p));
		}
				
		double xx= 0;
		double yy= 0;
		double mx= width;
		double my= height;
		//double rc= Math.sqrt(N) * 6;
		double rc= Math.sqrt( (N * mS)/Math.PI );
		GridPoint pt= null;
		for ( Object obj : context ) {
			do { 
				double a= 2 * Math.PI * RandomHelper.getUniform().nextDouble();
				double r= Math.sqrt(RandomHelper.getUniform().nextDouble());
				int x= (int) ((int) ( rc * r) * Math.cos(a) + width/2);
				int y= (int) ((int) ( rc * r) * Math.sin(a) + height/2);
				
				xx= Math.max(xx, x);
				yy= Math.max(yy, y);
				mx= Math.min(mx, x);
				my= Math.min(my, y);
				
				pt= new GridPoint(x, y);
			} while(!grid.moveTo(obj , (int)pt.getX(), (int )pt.getY()));
		}
		Double S= Math.PI * Math.pow(rc, 2);
		
		System.out.println("N= " + N + " rc= " + rc + " Min S/cell= " + ic.getMinSurfacePerCell() +  " S= " + S + " Avg radius= " + (((xx + yy)/2) - ((mx + my)/2))/2);
	}
	
	/**
	 * This method initialize the nutrient on the grid value layer  
	 * 
	 */
	private void initNutrients(Context<Object> context) {
		GridValueLayer vl = new GridValueLayer(MyParameters.VL_NUTRIENTS, true,new WrapAroundBorders(),width,height);
		context.addValueLayer(vl);
		double nutrient= MyParameters.getNutrient();
		for(int x= 0; x< width; x++) 
			for(int y= 0; y< height; y++)
				vl.set(nutrient,x,y);
		
	}

}
