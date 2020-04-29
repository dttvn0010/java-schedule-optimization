package app;

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
import util.CombinationUtil;

public class TimeTable {

    static class Teacher {
        int id;
        String name;
        public Teacher(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    static class TimeSlot {
        int id;
        int startHour;
        int startMinute;
        int endHour;
        int endMinute;
        int dayOfWeek;
        
        public TimeSlot(int startHour, int startMinute, int endHour, int endMinute, int dayOfWeek) {
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.dayOfWeek = dayOfWeek;
        }
    }
    
    static class Class_ {
        String name;
        int teacherId;
        int numberOfStudent;
        
        public Class_(String name, int teacherId, int numberOfStudent) {
            this.name = name;
            this.teacherId = teacherId;
            this.numberOfStudent = numberOfStudent;
        }
    }
    
    static class Room {
        String name;
        int capacity;
        
        public Room(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }
    }
    
    static Teacher[] teachers = {
            new Teacher(1, "T1"), new Teacher(2, "T2"),
            new Teacher(3, "T3"), new Teacher(4, "T4"),
            new Teacher(5, "T5"), new Teacher(6, "T6"),
            new Teacher(7, "T7"), new Teacher(8, "T8"),
            new Teacher(9, "T9"), new Teacher(10, "T10"),
    };
    
    static TimeSlot[] timeSlots = {
        new TimeSlot(9, 0, 12, 0, 0), new TimeSlot(14, 0, 17, 0, 0),
        new TimeSlot(9, 0, 12, 0, 1), new TimeSlot(14, 0, 17, 0, 1),
        new TimeSlot(9, 0, 12, 0, 2), new TimeSlot(14, 0, 17, 0, 2),
        new TimeSlot(9, 0, 12, 0, 3), new TimeSlot(14, 0, 17, 0, 3),
        new TimeSlot(9, 0, 12, 0, 4), new TimeSlot(14, 0, 17, 0, 4)
        
    };
    
    static Room[] rooms = {
        new Room("R01", 40), new Room("R02", 40), new Room("R03", 40), new Room("R04", 40),
        new Room("R05", 50), new Room("R06", 50), new Room("R07", 50), new Room("R08", 50),
        new Room("R09", 60), new Room("R10", 60), new Room("R11", 60), new Room("R12", 60)    
    };
    
    static Class_[] classes = {
        new Class_("C1", 1, 45), new Class_("C2", 1, 36), new Class_("C3", 1, 54), new Class_("C4", 1, 34),
        new Class_("C5", 2, 43), new Class_("C6", 2, 24), new Class_("C7", 2, 56), new Class_("C8", 2, 57),
        new Class_("C9", 3, 23), new Class_("C10", 3, 28), new Class_("C11", 3, 44), new Class_("C12", 3, 44),
        new Class_("C13", 4, 54), new Class_("C14", 4, 32), new Class_("C15", 4, 46), new Class_("C16", 4, 43),
        new Class_("C17", 5, 19), new Class_("C18", 5, 30), new Class_("C19", 5, 35), new Class_("C20", 5, 25),
        new Class_("C21", 6, 32), new Class_("C22", 6, 25), new Class_("C23", 6, 48), new Class_("C24", 6, 35),
        new Class_("C25", 7, 49), new Class_("C26", 7, 48), new Class_("C27", 7, 38), new Class_("C28", 7, 32),
        new Class_("C29", 8, 23), new Class_("C30", 8, 34), new Class_("C31", 8, 45), new Class_("C32", 8, 39),
        new Class_("C33", 9, 28), new Class_("C34", 9, 22), new Class_("C35", 9, 47), new Class_("C36", 9, 47),
        new Class_("C37", 10, 52), new Class_("C36", 10, 26), new Class_("C37", 10, 59), new Class_("C40", 10, 56),
    };
    
    static int N = timeSlots.length * rooms.length;
    static int k = classes.length;
    
    static class TimeTableChromosome extends Chromosome {        
        
        public TimeTableChromosome(int[] encoded) {
            super(encoded);
        }

        public TimeTableChromosome(Random rand) {
            super(rand);
        }
        
        @Override
        protected void randomInit(Random rand) {
            encoded = CombinationUtil.genCombination(N, k, rand);
            
        }

        @Override
        protected Chromosome fromEncoded(int[] encoded) {
            return new TimeTableChromosome(encoded);
        }
        
        @Override
        protected double calcFitness() {
            double score = 0;
            for(Teacher teacher : teachers) {
                Set<Integer> timeSlotIds = new HashSet<>();
                for(int i = 0; i < k; i++) {
                    if(classes[i].teacherId == teacher.id) {
                        int roomId = encoded[i] / timeSlots.length;
                        int timeSlotId = encoded[i] % timeSlots.length;
                        if(rooms[roomId].capacity >= classes[i].numberOfStudent) {
                            timeSlotIds.add(timeSlotId);
                        }
                    }
                }
                score += timeSlotIds.size();
            }
            return score;
        }        
    }
    
    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        params.put("maxIndex", N);
        params.put("bitMutationRate", 0.2);
        
        List<Chromosome> initialPopulation = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
        	initialPopulation.add(new TimeTableChromosome(rand));
        }
        
        GAOptimizer gaOptimizer = new GAOptimizer(initialPopulation, 
        								20, 50, 0.1,
                                        SelectionType.ROULETTE,
                                        CrossOverType.UNIFORM, 
                                        MutationType.MUTATE_POINT,
                                        true, params);
        gaOptimizer.run(1000);
    }
}
