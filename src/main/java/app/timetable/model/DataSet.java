package app.timetable.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Dataset for time table problem
 */
public class DataSet {

    public Teacher[] teachers;
    public TimeSlot[] timeSlots;
    public Room[] rooms;
    public  Class_[] classes;
    
    /**
     * Constructor
     * @param teachers
     * @param timeSlots
     * @param rooms
     * @param classes
     */
    public DataSet(Teacher[] teachers, TimeSlot[] timeSlots, Room[] rooms, Class_[] classes) {
        this.teachers = teachers;
        this.timeSlots = timeSlots;
        this.rooms = rooms;
        this.classes = classes;
    }
    
    /**
     * Convert dataset to json
     **/    
    public String toJson() throws JsonProcessingException {
        Map<String, Object> obj = new HashMap<>();
        obj.put("teachers", Arrays.stream(teachers).map(x -> x.dump()).collect(Collectors.toList()));
        obj.put("timeSlots", Arrays.stream(timeSlots).map(x -> x.dump()).collect(Collectors.toList()));
        obj.put("rooms", Arrays.stream(rooms).map(x -> x.dump()).collect(Collectors.toList()));
        obj.put("classes", Arrays.stream(classes).map(x -> x.dump()).collect(Collectors.toList()));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);        
    }
    
    /**
     * Save dataset to file
     **/
    public void save(String fileName) throws IOException {
        String json = toJson();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName));
        writer.write(json);
        writer.close();
    }
    
    /**
     * Load dataset from file
     **/
    @SuppressWarnings("unchecked")
    public DataSet(String resourceName) throws IOException {
        String fn = DataSet.class.getClassLoader().getResource(resourceName).getFile();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> obj = mapper.readValue(new File(fn), Map.class);
        
        List<Map<String, Object>> teachersObjList = (List<Map<String, Object>>) obj.get("teachers");
        List<Teacher> teacherList = teachersObjList.stream().map(x -> new Teacher(x)).collect(Collectors.toList());
        this.teachers = teacherList.toArray(new Teacher[0]);
        
        List<Map<String, Object>> timeSlotsObjList = (List<Map<String, Object>>) obj.get("timeSlots");
        List<TimeSlot> timeSlotList = timeSlotsObjList.stream().map(x -> new TimeSlot(x)).collect(Collectors.toList());
        this.timeSlots = timeSlotList.toArray(new TimeSlot[0]);
        
        List<Map<String, Object>> roomsObjList = (List<Map<String, Object>>) obj.get("rooms");
        List<Room> roomList = roomsObjList.stream().map(x -> new Room(x)).collect(Collectors.toList());
        this.rooms = roomList.toArray(new Room[0]);
        
        List<Map<String, Object>> classesObjList = (List<Map<String, Object>>) obj.get("classes");
        List<Class_> classList = classesObjList.stream().map(x -> new Class_(x)).collect(Collectors.toList());
        this.classes = classList.toArray(new Class_[0]);
    }
}
