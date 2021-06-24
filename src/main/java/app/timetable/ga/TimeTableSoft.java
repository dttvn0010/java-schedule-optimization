package app.timetable.ga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import api.Chromosome;
import api.GAOptimizer;
import api.GAOptimizer.CrossOverType;
import api.GAOptimizer.MutationType;
import api.GAOptimizer.SelectionType;
import app.timetable.model.DataSet;
import app.timetable.model.Teacher;
import util.CombinationUtil;


/**
 * Time table with hard constraint problem
 */

public class TimeTableSoft {

    static DataSet dataSet;
    
    static int N, K;
    /**
     * Chromosome for Time table with hard constraint
     */
    static class TimeTableChromosome extends Chromosome {        
        
    	/**
    	 * Constructor
    	 * @param encoded: encoded value of the solution
    	 */
        public TimeTableChromosome(int[] encoded) {
            super(encoded);
        }

        /**
    	 * Constructor
    	 * @param rand: random generator
    	 */
        public TimeTableChromosome(Random rand) {
            super(rand);
        }
        
        /**
    	 * Calculate fitness of the solution
    	 * @return: fitness
    	 */
        @Override
        protected void randomInit(Random rand) {
            encoded = CombinationUtil.genCombination(N, K, rand);
            
        }

        /**
         * Create a chromosome from encoded value
         * @param encoded: encoded value
         */
        @Override
        protected Chromosome fromEncoded(int[] encoded) {
            return new TimeTableChromosome(encoded);
        }
        
        /**
    	 * Calculate fitness of the solution
    	 * @return: fitness
    	 */
        @Override
        protected double calcFitness() {
            int nTimeSlot = 0;
            int nNotPrefered = 0;
            
            for(Teacher teacher : dataSet.teachers) {
                Set<Integer> timeSlotIds = new HashSet<>();
                for(int i = 0; i < K; i++) {
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
    }
    
    /**
     * Running all benchmarks 
     */
    public static void run(String dataSetPath, double mutationRate) throws IOException {
        System.out.println("============================" + dataSetPath);
        
        dataSet =  new DataSet(dataSetPath);
        N = dataSet.timeSlots.length * dataSet.rooms.length;
        K = dataSet.classes.length;
        
        
        Map<String, Object> params = new HashMap<>();
        params.put("maxIndex", N);
        params.put("tournamentThresh", 0.9);
        
        for(SelectionType selectionType : new SelectionType[] {SelectionType.TOURNAMENT, SelectionType.ROULETTE, SelectionType.REFINED}) {
            for(CrossOverType crossOverType: new CrossOverType[] { CrossOverType.ONE_POINT, CrossOverType.UNI_ONE_POINT, CrossOverType.UNI_THREE_PARENT}) {
                System.out.println(String.format("Selection type: %s, crossovertype: %s", selectionType, crossOverType));
                
                for(int k = 0; k < 8; k++) {
                    System.out.println("Run " + (k+1));
                    List<Chromosome> initialPopulation = new ArrayList<>();
                    Random rand = new Random();
                    for(int i = 0; i < 500; i++) {
                        initialPopulation.add(new TimeTableChromosome(rand));
                    }
                    
                    GAOptimizer gaOptimizer = new GAOptimizer(initialPopulation, 
                                                    50, 250, mutationRate,
                                                    selectionType,
                                                    crossOverType, 
                                                    MutationType.MUTATE_POINT,
                                                    true, params);
                    gaOptimizer.run(10000);
                    double score = gaOptimizer.getPopulations()[0].getFitness();
                    if(score >= 1.0) {
                        int nUnprefered = (int)( 1/(score-1) - 1);
                        System.out.println("Unprefered classes:" + nUnprefered);
                    }
                }
            }            
        }
    }
    
    /**
     * Program entry point
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        run("timetable/soft/200_classes.json", 0.25);
        run("timetable/soft/160_classes.json", 0.35);
    }
}
