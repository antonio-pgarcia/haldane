package org.holistic.bactocom;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;

@SuppressWarnings("rawtypes")
public class ModelRatesHelper {
	private double pN0= 0;
	private double D= 0;
	private double R= 0;
	private double T= 0;
	private double Cd= 0;
	private double Ct= 0;

	
	private final static ModelRatesHelper instance= new ModelRatesHelper();
	
	private ModelRatesHelper() {
	}
	
	public double getGammaEndPointAll(Context context) {
		double psi= getGrowthRateAll(context);
		double g= gammaEndPoint(psi,D+R+T, this.pN0,D,R,T);
		return g;
	}
	
	public double getGammaEndPointDonor(Context context) {
		double psi= getGrowthRateDonors(context);
		double g= gammaEndPoint(psi,D+R+T, this.pN0,D,R,T);
		return g;
	}
	
	public double getGammaEndPointTransconjugant(Context context) {
		double psi= getGrowthRateTransconjugant(context);
		double g= gammaEndPoint(psi,D+R+T, this.pN0,D,R,T);
		return g;
	}
	
	public double getRCv(Context context) {
		Double v= (T - (Cd + Ct))/T; 
		return( (!Double.isNaN(v) ? v : 0) );
	}
	
	public double getRCh(Context context) {
		Double v= (Cd + Ct)/T;
		return( (!Double.isNaN(v) ? v : 0) ); 
	}
	
	public double getRCt(Context context) {
		Double v= Ct/(Cd + Ct); 
		return( (!Double.isNaN(v) ? v : 0) );
	}
	
	public double getTransconjugantPerRecipientCell(Context context) {
		return T/(R+T);
	}
	
	public double getGrowthRateAll(Context context) {
		double N0= this.pN0;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGrowthRate(D+R+T, N0, t, 0);
	}
	
	public double getGrowthRateDonors(Context context) {
		double N0= this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGrowthRate(D, N0, t, 0);
	}
	
	public double getGrowthRateRecipient(Context context) {
		double N0= this.pN0 - this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGrowthRate(R, N0, t, 0);
	}
	
	public double getGrowthRateTransconjugant(Context context) {
		double N0= this.pN0 - this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGrowthRate(T, N0, t, 0);
	}
	
	public double getGenerationTimeAll(Context context) {
		double N0= this.pN0;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGenerationTimes(D+R+T, N0, t, 0);
	}
	
	public double getGenerationTimeDonors(Context context) {
		double N0= this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGenerationTimes(D, N0, t, 0);
	}
	
	public double getGenerationTimeRecipient(Context context) {
		double N0= this.pN0 - this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return getGenerationTimes(R, N0, t, 0);
	}
	
	public double getGenerationTimeTransconjugant(Context context) {
		double N0= this.pN0 - this.pN0 * MyParameters.getDonorDensity()/100;
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return (T == 0 || T <= N0 ? 0 : getGenerationTimes(T, N0, t, 0));
	}
	
	public double getGrowthRate(double N, double N0, double t, double t0) {
		if( N == 0 || N <= N0 || t == 0) return 0;
		double mu = ((Math.log10(N) - Math.log10(N0)) * 2.303) / ((t - t0)/60);
		return mu;
	}

	public double getGenerationTimes(double N, double N0, double t, double t0) {
		if(N == N0 || t == 0) return 0;
		double n= 3.3 * Math.log10( N/N0 );
		double G= t/n;
		double g= MyParameters.getGenerationTime();
		return (G < (g * 1.5) ? G  : 0 );
	}
	
	/**
	 * Estimate the conjugation rate using the end-point method
	 * 
	 * @param psi 	The growth rate 
	 * @param N		The current population
	 * @param N0	The initial population
	 * @param D		The current donor population
	 * @param R		The recipient population
	 * @param T		The transconjugant population
	 * 
	 * @return		The gama value (ml cell^-1 h^-1)
	 */
	public double gammaEndPoint(double psi, double N, double N0, double D, double R, double T) {
		double scaling=   1.0000e+009/(MyParameters.getHeight()*MyParameters.getWidth());
		double gamma= psi * 1/(N-N0) * Math.log(1+(T*N)/(D*R));
		gamma= (Double.isNaN(gamma) ? 0 : gamma); 
		return gamma/scaling;
	}

	public static ModelRatesHelper getInstance() {
		return instance;
	}
	
	public void gatherData() {
		pN0= MyPopulationBookkeeper.getInstance().getN0();
		D= MyPopulationBookkeeper.getInstance().getD();
		R= MyPopulationBookkeeper.getInstance().getR();
		T= MyPopulationBookkeeper.getInstance().getT();
		Cd= MyPopulationBookkeeper.getInstance().getCd();
		Ct= MyPopulationBookkeeper.getInstance().getCt();
	}

}
