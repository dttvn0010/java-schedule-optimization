package app.timetable.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to be arranged
 */

public class Class_ {

    String name;
    int teacherId;
    int numberOfStudent;
    
    /**
     * Constructor
     * @param name
     * @param teacherId
     * @param numberOfStudent
     */
    public Class_(String name, int teacherId, int numberOfStudent) {
        this.name = name;
        this.teacherId = teacherId;
        this.numberOfStudent = numberOfStudent;
    }

    /**
     * getter
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     * @return teacherId
     */
    public int getTeacherId() {
        return teacherId;
    }

    /**
     * getter
     * @return numberOfStudent
     */
    public int getNumberOfStudent() {
        return numberOfStudent;
    }
    
    /**
     * dump object into a hashmap
     */
    public Map<String, Object> dump() {
        Map<String, Object> obj = new HashMap<>();        
        obj.put("name", name);
        obj.put("teacherId", teacherId);
        obj.put("numberOfStudent", numberOfStudent);
        return obj;
    }
    
    /**
     * new object from a hashmap
     */
    public Class_(Map<String, Object> obj) {
        this.name = (String) obj.get("name");
        this.teacherId = (Integer) obj.get("teacherId");
        this.numberOfStudent = (Integer) obj.get("numberOfStudent");
    }
    
}
