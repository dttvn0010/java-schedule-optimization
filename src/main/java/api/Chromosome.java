package api;

import java.util.Random;

/**Chromosome class
 * Encode a solution to the problem
 */

public abstract class Chromosome {
    
	public int[] encoded;	
    private Double fitness = null;
    
    /**
     * Calculate the fitness of the chromosome
     * @return: fitness
     */
    abstract protected double calcFitness();
    
    /**
     * Randomly initialize a chromosome
     * @param rand: random generator
     **/    
    abstract protected void randomInit(Random rand);
    
    /**
     * Create a chromosome from an encoded value
     * @param encoded: encoded value of the solution
     * @return: Chromosome
     */
    abstract protected Chromosome fromEncoded(int[] encoded);
    
    /**
     * Constructor
     * @param encoded: encoded value of the solution 
     */
    public Chromosome(int[] encoded) {
        this.encoded = new int[encoded.length];
        for(int i = 0; i < encoded.length; i++) {
            this.encoded[i] = encoded[i];
        }
    }
    
    /**
     * Constructor
     * @param random: random generator
     */
    public Chromosome(Random rand) {
        randomInit(rand);
    }
    
    /**
     * Get fitness of the solution
     */
    public double getFitness() {
        
        if(fitness == null) {
            fitness = calcFitness();            
        }
        
        return fitness;
    }
    
    /**
     * Clear fitness from cache
     */
    public void clearFitness() {
        fitness = null;
    }
    
    /**
     * Convert object to string
     */
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
