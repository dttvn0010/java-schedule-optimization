package app.timetable.sa;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import app.timetable.model.DataSet;
import app.timetable.model.Teacher;
import util.CombinationUtil;

/**
 * A solution for the time table problem
 **/

public class Solution 
{
    public int[] encoded;
    private DataSet dataSet;
    private Random rand = new Random();
    
    /**
     * Constructor
     * @param dataSet
     */
	public Solution(DataSet dataSet)
	{
	    this.dataSet = dataSet;
	    int N = dataSet.timeSlots.length * dataSet.rooms.length;
        int K = dataSet.classes.length;
		this.encoded = CombinationUtil.genCombination(N, K);
	}
	
	/**
	 * Constructor
	 * @param solution
	 **/
	public Solution(Solution solution)
	{
		this.dataSet = solution.dataSet;
		this.encoded = new int[solution.encoded.length];
		for(int i = 0; i < solution.encoded.length; i++)
		{
			this.encoded[i] = solution.encoded[i];
		}
	}
	
	/**
     * Calculate fitness of the solution
     * @return: fitness
     */
	public double calcFitness() {
        double score = 0;
        for(Teacher teacher : dataSet.teachers) {
            Set<Integer> timeSlotIds = new HashSet<>();
            for(int i = 0; i < dataSet.classes.length; i++) {
                if(dataSet.classes[i].getTeacherId() == teacher.getId()) {
                    int roomId = encoded[i] / dataSet.timeSlots.length;
                    int timeSlotId = encoded[i] % dataSet.timeSlots.length;
                    if(dataSet.rooms[roomId].getCapacity() >= dataSet.classes[i].getNumberOfStudent()) {
                        timeSlotIds.add(timeSlotId);
                    }
                }
            }
            score += timeSlotIds.size();
        }
        return score;
    }	
	
	/**
	 * Generating a new solution by mutating
	 **/
	public Solution getNewSolution()
	{	    
	    Solution newState = new Solution(this);
        
        int N = dataSet.timeSlots.length * dataSet.rooms.length;
        
        int i = (int)(rand.nextDouble() * newState.encoded.length);
        int index = (int)(rand.nextDouble() * N); 
        int j = -1;
        
        for(int k = 0; k < newState.encoded.length; k++) {
            if(newState.encoded[k] == index) {
                j = k;
                break;
            }
        }
        
        int tmp = newState.encoded[i];
        newState.encoded[i] = index;
        if(j >= 0) newState.encoded[j] = tmp;
                
        return newState;
	}
	
}
