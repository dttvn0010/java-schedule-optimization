package app.timetable;

import java.io.IOException;
import java.util.Random;

import app.timetable.model.Class_;
import app.timetable.model.DataSet;
import app.timetable.model.Room;
import app.timetable.model.Teacher;
import app.timetable.model.TimeSlot;
import util.CombinationUtil;

/**Data generator
 * Generate data for time table problems
 */
public class DataGenerator {
    
    public static TimeSlot[] timeSlots = {
        new TimeSlot(9, 0, 12, 0, 0), new TimeSlot(14, 0, 17, 0, 0),
        new TimeSlot(9, 0, 12, 0, 1), new TimeSlot(14, 0, 17, 0, 1),
        new TimeSlot(9, 0, 12, 0, 2), new TimeSlot(14, 0, 17, 0, 2),
        new TimeSlot(9, 0, 12, 0, 3), new TimeSlot(14, 0, 17, 0, 3),
        new TimeSlot(9, 0, 12, 0, 4), new TimeSlot(14, 0, 17, 0, 4)
        
    };
    
    /**
     * Generate a dataset for time table problem
     * @param size: Number of teacher. Each teacher teaches 4 classes, therefore number of classes is 4xsize
     * @param hard: true for hard constraint, false for hard constraint
     * @return: a dataset which contains: teachers, classes, time slots, rooms
     */
    
    public static DataSet generateDataset(int size, boolean hard) {
        Random rand = new Random();
        Teacher[] teachers = new Teacher[size];
        
        int roomSize = hard? (int)(size*0.4): size;
        Room[] rooms = new Room[roomSize];
        
        Class_[] classes = new Class_[size*4];
        
        for(int i = 0; i < size; i++) {
            int[] preferedTimeSlotIds = CombinationUtil.genCombination(timeSlots.length, 4, rand);
            int[] preferedRoomIds = CombinationUtil.genCombination(rooms.length, 4, rand);
            Teacher teacher = new Teacher(i+1, "Teacher " + (i+1), preferedTimeSlotIds, preferedRoomIds);
            teachers[i] = teacher;
            
            for(int j = 0; j < 4; j++) {
                int index = 4*i+j;
                Class_ cl = new Class_("C" +(index+1), i+1, 20 + rand.nextInt(25));
                classes[index] = cl;
            }
        }
        
        for(int i = 0; i < rooms.length; i++) {
            Room room = new Room("R" + (i+1), 30 + 10*(3*i/rooms.length));
            rooms[i] = room;
        }
        
        return new DataSet(teachers, timeSlots, rooms, classes);
    }
    
    /**
     * Program entry point
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {        
        generateDataset(40, false).save("src/main/resources/timetable/soft/160_classes.json");
        generateDataset(50, false).save("src/main/resources/timetable/soft/200_classes.json");
        
        generateDataset(40, true).save("src/main/resources/timetable/hard/160_classes.json");
        generateDataset(50, true).save("src/main/resources/timetable/hard/200_classes.json");
    }
}
