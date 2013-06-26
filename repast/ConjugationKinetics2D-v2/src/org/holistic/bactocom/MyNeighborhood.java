package org.holistic.bactocom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cern.jet.random.Uniform;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

/**
 * Simple Moore's neighborhood for model developed for FdlC group paper 
 * "Experimental validation of a kinetic numerical model of bacterial conjugation"   
 * 
 * 
 * @author APG, ARPA
 *
 */
public class MyNeighborhood {

	public static List<GridPoint> getMooreNeighborhood(GridPoint pt, int size, boolean c) {
		List<GridPoint> neighbors = new ArrayList<GridPoint>();
		for(int x= -size; x<= size; x++) {
			for(int y= size; y >= -size; y--) {
				if(!c && x == 0 && y == 0) continue;
				neighbors.add(new GridPoint(pt.getX() + x,pt.getY() + y));
            }
		}
		Collections.shuffle(neighbors);
		return neighbors;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<GridPoint> getMooreNeighborhood(Object o, int size, boolean c) {
		Context context = ContextUtils.getContext(o);
		Grid grid = (Grid)context.getProjection("Grid");
		GridPoint pt = grid.getLocation(o);
		return getMooreNeighborhood(pt, size, c);
	}
	
	public static List<GridPoint> getMooreNeighborhood(Object o, GridPoint pt, int size) {
		List<GridPoint> sites = new ArrayList<GridPoint>();
		
		for(int x= -size; x<= size; x++) {
			for(int y= size; y >= -size; y--) {
				sites.add(new GridPoint(pt.getX() + x,pt.getY() + y));
            }
		}
        
		Collections.shuffle(sites);
		return sites;
	}
	
		
	@SuppressWarnings("rawtypes")
	public static List<GridPoint> getEmptyMooreNeighborhood(Object o, GridPoint pt, int size) {
		List<GridPoint> emptySites = new ArrayList<GridPoint>();
		Context context = ContextUtils.getContext(o);
		Grid grid = (Grid)context.getProjection("Grid");
		
		for(int x= -size; x<= size; x++) {
			for(int y= size; y >= -size; y--) {
				if(x == 0 && y == 0) continue;
            	if(!grid.getObjectsAt(pt.getX() + x,pt.getY() + y).iterator().hasNext())	
            		emptySites.add(new GridPoint(pt.getX() + x,pt.getY() + y));
            }
		}
        
		Collections.shuffle(emptySites);
		return emptySites;
	}
	
	/**
	 * Provides a list of adjacent (unoccupied) sites in the cell's Moore 
	 * neighborhood.  The list of sites is shuffled.
	 * 
	 * @return the list of adjacent sites.
	 */
	@SuppressWarnings("rawtypes")
	public static List<GridPoint> getEmptyMooreNeighborhood(Object o, int size) {
		List<GridPoint> emptySites = new ArrayList<GridPoint>();
		Context context = ContextUtils.getContext(o);
		Grid grid = (Grid)context.getProjection("Grid");
		GridPoint pt = grid.getLocation(o);

		for(int x= -size; x<= size; x++) {
			for(int y= size; y >= -size; y--) {
				if(x == 0 && y == 0) continue;
            	if(!grid.getObjectsAt(pt.getX() + x,pt.getY() + y).iterator().hasNext())	
            		emptySites.add(new GridPoint(pt.getX() + x,pt.getY() + y));
            }
		}
        
		Collections.shuffle(emptySites);
		
		return emptySites;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List ListFromIterator(Iterator i) {
		List individuals= new ArrayList();
		while(i.hasNext()) {
			individuals.add(i.next());
		}
		SimUtilities.shuffle(individuals, (Uniform) RandomHelper.getDistribution(MyParameters.RANDOM_NEIGHBORHOOD));
		return(individuals);
	}
	
	@SuppressWarnings("rawtypes")
	public static double[] getUnitVector(Object o, GridPoint pt, GridPoint other) {
		Context context = ContextUtils.getContext(o);
		Grid grid = (Grid) context.getProjection("Grid");
		
		double d= grid.getDistance(pt, other);
		double[] vector = new double[pt.dimensionCount()];
		
		for(int i= 0; i< pt.dimensionCount(); i++) {
			 vector[i]= (d != 0 ? (pt.getCoord(i) - other.getCoord(i))/d : 0);
		}
	    
		return(vector);
	}
}
