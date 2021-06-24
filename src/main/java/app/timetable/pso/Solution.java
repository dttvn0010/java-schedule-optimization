package app.timetable.pso;

import java.util.HashSet;
import java.util.Set;

import app.timetable.model.DataSet;
import app.timetable.model.Teacher;

/**
 * A solution for the time table problem
 **/
public class Solution {

    public int[] encoded;
    private DataSet dataSet;
    
    public double fitness;
    
    public int segmentSize;
    
    /**
     * Constructor
     * @param dataSet
     * @param encoded
     */
    public Solution(DataSet dataSet, int[] encoded) {
        this.dataSet = dataSet;
        this.encoded = encoded;
        this.calcFitness();
        segmentSize = -1;
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
        this.fitness = score;
        return this.fitness;
    }
    
    /**
     * Clone the solution
     * @return: a new solution with same encoded value
     */
    public Solution clone() {
        int[] tmp = new int[encoded.length];
        System.arraycopy(encoded, 0, tmp, 0, tmp.length);
        return new Solution(dataSet, tmp);
    }
    
    /**
     * Copy internal values to another solution
     */
    public void copyTo(Solution target) {
        target.dataSet = dataSet;
        target.encoded = new int[encoded.length];
        System.arraycopy(encoded, 0, target.encoded, 0, encoded.length);
        target.fitness = fitness;
    }
    
    /**
     * Get max value for indexes in encoded array
     */
    public int getNmax() {
        return dataSet.rooms.length * dataSet.timeSlots.length;
    }
}
