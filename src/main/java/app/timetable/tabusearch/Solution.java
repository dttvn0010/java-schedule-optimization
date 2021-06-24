package app.timetable.tabusearch;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import app.timetable.model.DataSet;
import app.timetable.model.Teacher;
import util.CombinationUtil;

public class Solution {

    public int[] encoded;
    private boolean soft;
    private DataSet dataSet;
    private Random rand = new Random();
    
    /**
     * Constructor
     * @param dataSet
     */
    public Solution(boolean soft, DataSet dataSet)
    {
        this.dataSet = dataSet;
        int N = dataSet.timeSlots.length * dataSet.rooms.length;
        int K = dataSet.classes.length;
        this.encoded = CombinationUtil.genCombination(N, K);
        this.soft = soft;
    }
    
    /**
     * Calculate fitness of the solution in hard constraint problem
     * @return: fitness
     */
    public double calcHardFitness() {
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
     * Calculate fitness of the solution in soft constraint problem
     * @return: fitness
     */    
    protected double calcSoftFitness() {
        int nTimeSlot = 0;
        int nNotPrefered = 0;
        
        for(Teacher teacher : dataSet.teachers) {
            Set<Integer> timeSlotIds = new HashSet<>();
            for(int i = 0; i < dataSet.classes.length; i++) {
                if(dataSet.classes[i].getTeacherId() == teacher.getId()) {
                    int roomId = encoded[i] / dataSet.timeSlots.length;
                    int timeSlotId = encoded[i] % dataSet.timeSlots.length;
                    if(dataSet.rooms[roomId].getCapacity() >= dataSet.classes[i].getNumberOfStudent()) {
                        timeSlotIds.add(timeSlotId);
                        
                        boolean preferedTimeSlot = false;
                        for(int preferedTimeSlotId : teacher.getPreferedTimeSlotIds()) {
                            if(preferedTimeSlotId == timeSlotId) {
                                preferedTimeSlot = true;
                                break;
                            }
                        }
                        
                        boolean preferedRoom = false;
                        for(int preferedRoomId : teacher.getPreferedRoomIds()) {
                            if(preferedRoomId == roomId) {
                                preferedRoom = true;
                            }
                        }
                        
                        if(!preferedTimeSlot || !preferedRoom) nNotPrefered += 1;                            
                    }
                }
            }
            nTimeSlot += timeSlotIds.size();
        }
        int nIllegal = dataSet.classes.length - nTimeSlot;
        return 1.0 / (nIllegal + (nNotPrefered+1.0)/(nNotPrefered+2.0));
    }        

    /**
     * Calculate fitness of the solution 
     * @return: fitness
     */
    double calcFitness() {
        if(soft) {
            return calcSoftFitness();
        }else {
            return calcHardFitness();
        }
    }

    /**
     * Constructor
     * @param solution
     **/
    public Solution(Solution solution)
    {
        this.dataSet = solution.dataSet;
        this.soft = solution.soft;
        this.encoded = new int[solution.encoded.length];
        for(int i = 0; i < solution.encoded.length; i++)
        {
            this.encoded[i] = solution.encoded[i];
        }
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
