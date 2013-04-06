package org.holistic.bactocom;

import java.util.concurrent.atomic.AtomicInteger;

public class MyPopulationBookkeeper {
	private AtomicInteger D;
	private AtomicInteger R;
	private AtomicInteger T;
	private AtomicInteger Cd;		// Conjugative transfers performed by Donor cells
	private AtomicInteger Ct;		// Conjugative transfers performed by Transconjugant cells
	
	private static MyPopulationBookkeeper instance= new MyPopulationBookkeeper();
	
	
	private MyPopulationBookkeeper() {
		D= new AtomicInteger(0);
		R= new AtomicInteger(0);
		T= new AtomicInteger(0);
		Cd= new AtomicInteger(0);
		Ct= new AtomicInteger(0);
	}

	public static MyPopulationBookkeeper getInstance() {
		return instance;
	}
	
	public void reset() {
		D.set(0);
		R.set(0);
		T.set(0);
		Cd.set(0);
		Ct.set(0);
	}
	
	public double getD() {
		return (double) D.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementD() {
		D.incrementAndGet();
	}
	
	public void decrementD() {
		D.decrementAndGet();
	}

	public double getR() {
		return (double) R.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementR() {
		R.incrementAndGet();
	}
	
	public void decrementR() {
		R.decrementAndGet();
	}

	public double getT() {
		return (double) T.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementT() {
		T.incrementAndGet();
	}
	
	public void decrementT() {
		T.decrementAndGet();
	}
	
	public double getAll() {
		return(getD() + getR() + getT());
	}

	public double getN0() {
		return MyParameters.getN0() * MyParameters.N_SCALEFACTOR;
	}
	
	public double getCd() {
		return (double) Cd.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementCd() {
		Cd.incrementAndGet();
	}
	
	public void decrementCd() {
		Cd.decrementAndGet();
	}
	
	public double getCt() {
		return (double) Ct.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementCt() {
		Ct.incrementAndGet();
	}
	
	public void decrementCt() {
		Ct.decrementAndGet();
	}
	
}
