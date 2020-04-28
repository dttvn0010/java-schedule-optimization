package api;

import java.util.Random;

public abstract class Chromosome {
	public int[] encoded;
	private Double fitness = null;
	abstract protected double calcFitness();
	abstract protected void randomInit(Random rand);
	abstract protected Chromosome fromEncoded(int[] encoded);
	
	public Chromosome(int[] encoded) {
		this.encoded = encoded;		
	}
	
	public Chromosome(Random rand) {
		randomInit(rand);
	}
	
	public double getFitness() {
		
		if(fitness == null) {
			fitness = calcFitness();			
		}
		
		return fitness;
	}
	
	public void clearFitness() {
		fitness = null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		if(encoded != null) {			
			builder.append("[");
			for(int i = 0; i < encoded.length; i++) {
				builder.append(encoded[i]);
				if(i < encoded.length - 1) builder.append(",");
			}
			builder.append("]");
		}
		
		return builder.toString();
	}
}
