package org.holistic.bactocom;


import java.util.Date;

import cern.jet.random.Normal;
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
			double endAt= 4 * 60;	// 8 hours of simulation
			RunEnvironment.getInstance().endAt(endAt);
		} else {
			double endAt= 10 * 60;	// 6 hours of simulation
			RunEnvironment.getInstance().endAt(endAt);
		}
	}
	
	/**
	 * Random Number generators initializer
	 */
	private void initRandomStreams() {
		RandomHelper.setSeed(MyParameters.getRandomSeed());
		Normal n = RandomHelper.createNormal(0, 1);
		RandomHelper.registerDistribution(MyParameters.RANDOM_DIVISION, n);
		RandomHelper.registerDistribution(MyParameters.RANDOM_NEIGHBORHOOD, RandomHelper.createUniform());
		RandomHelper.registerDistribution(MyParameters.RANDOM_PLASMIDLOSS, RandomHelper.createUniform());
		RandomHelper.registerDistribution(MyParameters.RANDOM_CONJUGATION, RandomHelper.createUniform());
		RandomHelper.registerDistribution(MyParameters.RANDOM_PILUS, RandomHelper.createUniform());
		RandomHelper.registerDistribution(MyParameters.RANDOM_REPRESSED, RandomHelper.createPoisson(0.001));
		RandomHelper.registerDistribution(MyParameters.RANDOM_DEREPRESSED, RandomHelper.createPoisson(0.4));
		
	}
	
	/**
	 * Creates the initial population of D and R cells 
	 * and distribute it randomly across the grid.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initPopulation(Context<Object> context, Grid grid) {
		MyPopulationBookkeeper.getInstance().reset();
		// Population initialization
		int N= MyParameters.getN0();
		int donors= N * MyParameters.getDonorDensity()/100;
		int recipient= N - donors;

		double G= MyParameters.getGenerationTime();
		boolean oriT= MyParameters.isOriT();
		boolean R= MyParameters.isRepressed();
		
		for (int i = 0; i < donors ; i++) {
			context.add(new Bacterium(RandomHelper.nextDoubleFromTo(120,220), Bacterium.State.D, G, R, oriT));
		}
				
		for (int i = 0; i < recipient ; i++) {
			context.add(new Bacterium(RandomHelper.nextDoubleFromTo(120,220), Bacterium.State.R, G, R, oriT));
		}
				
		double rc= Math.sqrt(N) * 1.50;
			GridPoint pt= null;
			for ( Object obj : context ) {
				do { 
					double a= 2 * Math.PI * RandomHelper.getUniform().nextDouble();
					double r= Math.sqrt(RandomHelper.getUniform().nextDouble());
					int x= (int) ((int) ( rc * r) * Math.cos(a) + width/2);
					int y= (int) ((int) ( rc * r) * Math.sin(a) + height/2);
					pt= new GridPoint(x, y);
				} while(!grid.moveTo(obj , (int)pt.getX(), (int )pt.getY()));
			}
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
