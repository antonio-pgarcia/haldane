package org.holistic.bactocom;


public class Genotype {
	private String genome= null;
	
	public Genotype() {
		createGenome();
	}
	
	public Genotype(String v) {
		setGenome(v);
	}

	public void createGenome() {
		setGenome(java.util.UUID.randomUUID().toString());
	}
	
	public void setGenome(String v) {
		genome= v;
	}
	
	public String getGenome() {
		return genome;
	}
	
	public boolean isEqual(Genotype o) {
		return getGenome().equalsIgnoreCase(o.getGenome());
	}

}
