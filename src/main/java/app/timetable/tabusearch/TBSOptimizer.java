package app.timetable.tabusearch;

import java.util.ArrayList;
import java.util.List;

import app.timetable.model.DataSet;

public class TBSOptimizer {

    private int neigbourSize;
    private int stoppingTurn;
    private Solution bestSolution;
    private double bestFitness;
    
    /**
     * Constructor
     * @param dataSet
     * @param temperature
     * @param coolingRate
     */
    public TBSOptimizer(boolean soft, DataSet dataSet, int neigbourSize, int stoppingTurn)
    {
        this.neigbourSize = neigbourSize;
        this.stoppingTurn = stoppingTurn;        
        this.bestSolution = new Solution(soft, dataSet);
        this.bestFitness = this.bestSolution.calcFitness();
    }
    
    /**
     * Run the optimizer
     **/
    public void run() {
        
        boolean stop = false;
        int bestKeepTurn = 0;
        Solution bestCandidate = bestSolution;
        
        while(!stop) {
            List<Solution> neighbours = new ArrayList<>();
            for(int i = 0; i < neigbourSize; i++) {
                neighbours.add(bestCandidate.getNewSolution());
            }
            
            bestCandidate = neighbours.get(0);
            double bestCandidateFitness = bestCandidate.calcFitness();
            
            for(Solution candidate : neighbours) {
                double candidateFitness = candidate.calcFitness();
                if(candidateFitness > bestCandidateFitness) {
                    bestCandidate = candidate;
                    bestCandidateFitness = candidateFitness;
                }
            }
            
            if(bestCandidateFitness > bestFitness) {
                //System.out.println(String.format("Fitness improves from %f to %f", bestFitness, bestCandidateFitness));
                bestSolution = bestCandidate;
                bestFitness = bestCandidateFitness;
                bestKeepTurn = 0;
            }
            
            if(bestKeepTurn == stoppingTurn) {
                stop = true;
            }
            bestKeepTurn += 1;
        }
    }
    
    /**
     * Get the fitness of the best solution
     **/
    public double getBestFitness() {
        return bestFitness;                
    }
    
    /**
     * Get the best solution
     **/
    public Solution getBestSolution() {
        return bestSolution;
    }
}
