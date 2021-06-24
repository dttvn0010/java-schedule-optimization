package app.timetable.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Room  
 */
public class Room {

    private String name;
    private int capacity;
    
    /**
     * Constructor
     * @param name
     * @param capacity
     */
    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
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
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }    
    
    /**
     * dump object into a hashmap
     */
    public Map<String, Object> dump() {
        Map<String, Object> obj = new HashMap<>();        
        obj.put("name", name);
        obj.put("capacity", capacity);
        return obj;
    }
    
    /**
     * new object from a hashmap
     */
    public Room(Map<String, Object> obj) {
        this.name = (String) obj.get("name");
        this.capacity = (Integer) obj.get("capacity");
    }
}
