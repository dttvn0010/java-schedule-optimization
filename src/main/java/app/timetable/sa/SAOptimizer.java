package app.timetable.sa;

import app.timetable.model.DataSet;

/**
 * Optimizer using simulated annealing
 **/
public class SAOptimizer 
{
	private double temperature, coolingRate;
	private Solution currentSolution, nextSolution, bestSolution;
	
	/**
     * Constructor
     * @param dataSet
     * @param temperature
     * @param coolingRate
     */
	public SAOptimizer(DataSet dataSet, double temperature, double coolingRate)
	{
		this.temperature = temperature;
		this.coolingRate = coolingRate;
		
		this.currentSolution = new Solution(dataSet);
		this.bestSolution = new Solution(this.currentSolution);
	}
	
	/**
	 * Run the optimizer
	 **/	
	public double run()
	{
		// Run algorithm
		while(this.temperature > 1e-3)
		{   
			// Get new path
			this.nextSolution = this.currentSolution.getNewSolution();
			
			// Calculate the fitness (distance)
			double currentEnergy = this.currentSolution.calcFitness();
			double newEnergy = this.nextSolution.calcFitness();
			//System.out.println(currentEnergy + "," + newEnergy);
			// Should we accept new path?
			if(this.acceptByProbability(currentEnergy, newEnergy))
			{
				this.currentSolution = this.nextSolution;
			}
			
			// Keep track of best solution
			if(this.currentSolution.calcFitness() > this.bestSolution.calcFitness())
			{
			    System.out.println("Fitness improved to:" + this.currentSolution.calcFitness());
				this.bestSolution = this.currentSolution;				
			}

			// Drop the temperature
			this.temperature *= (1 - coolingRate);
		}
		
		return this.bestSolution.calcFitness();
	}
	
	/**
	 * Check whether to accept a new solution
	 * @param currentFitness 
	 * @param newFitness
	 * @return whether accept the new solution
	 **/
	public boolean acceptByProbability(double currentFitness, double newFitness)
	{
		if(newFitness < currentFitness)
			return true;
		else
		{
			double delta = (newFitness - currentFitness);
			double probability = Math.exp(delta/this.temperature);
			return probability > Math.random();
		}
	}
}
