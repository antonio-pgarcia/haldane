package org.holistic.bactocom;

import java.util.concurrent.atomic.AtomicInteger;

import org.holistic.bactocom.State;

public class MyPopulationBookkeeper {
	private AtomicInteger N0;
	private AtomicInteger D;
	private AtomicInteger R;
	private AtomicInteger T;
	private AtomicInteger E;		// Number of D|T to R Encounters
	private AtomicInteger Ed;		// Number of D|T to R Encounters
	private AtomicInteger Et;		// Number of D|T to R Encounters
	private AtomicInteger Nc;		// Number of cells which have conjugated
	private AtomicInteger Cd;		// Conjugative transfers performed by Donor cells
	private AtomicInteger Ct;		// Conjugative transfers performed by Transconjugant cells
	
	private static MyPopulationBookkeeper instance= new MyPopulationBookkeeper();
	
	
	private MyPopulationBookkeeper() {
		N0= new AtomicInteger(0);
		D= new AtomicInteger(0);
		R= new AtomicInteger(0);
		T= new AtomicInteger(0);
		E= new AtomicInteger(0);
		Ed= new AtomicInteger(0);
		Et= new AtomicInteger(0);
		Nc= new AtomicInteger(0);
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
		E.set(0);
		Ed.set(0);
		Et.set(0);
		Nc.set(0);
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

	public void setN0(int v) {
		N0.set(v);
	}
	
	public double getN0() {
		return N0.get() * MyParameters.N_SCALEFACTOR;
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
	
	public double getNc() {
		return (double) Nc.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementNc() {
		Nc.incrementAndGet();
	}
	
	public void decrementNc() {
		Nc.decrementAndGet();
	}
	
	public double getE() {
		return (double) E.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementE() {
		E.incrementAndGet();
	}
	
	public void decrementE() {
		E.decrementAndGet();
	}
	
	public double getEd() {
		return (double) Ed.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementEd() {
		Ed.incrementAndGet();
	}
	
	public void decrementEd() {
		Ed.decrementAndGet();
	}
	
	public double getEt() {
		return (double) Et.get() * MyParameters.N_SCALEFACTOR;
	}

	public void incrementEt() {
		Et.incrementAndGet();
	}
	
	public void decrementEt() {
		Et.decrementAndGet();
	}
	
	/**
	 * This method keeps the track of number and the type of individuals in
	 * simulated population. @Url MyPopulationBookkeper
	 * 
	 * @param s0 The cell state at t0
	 * @param s The cell state at current time
	 */
	public void populationAccount(State s0, State s) {
		switch (s) {
		case D:
			incrementD();
			break;

		case R:
			incrementR();
			break;
		
		case T:
			incrementT();
			break;
		}
		if(s0 == s) return;
		if(s0 == State.R && s == State.T) {
			getInstance().decrementR();
		}
	}
}
