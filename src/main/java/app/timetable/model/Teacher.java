package app.timetable.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Teacher
 */
public class Teacher {

    private int id;
    private String name;
    private int[] preferedTimeSlotIds;
    private int[] preferedRoomIds;
    
    /**
     * constructor
     * @param id
     * @param name
     * @param preferedTimeSlotIds
     * @param preferedRoomIds
     */
    public Teacher(int id, String name, int[] preferedTimeSlotIds, int[] preferedRoomIds) {
        this.id = id;
        this.name = name;
        this.preferedTimeSlotIds = preferedTimeSlotIds;
        this.preferedRoomIds = preferedRoomIds;
    }
    
    /**
     * getter
     * @return id
     */
    public int getId() {
        return id;
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
     * @return preferedTimeSlotIds
     */
    public int[] getPreferedTimeSlotIds() {
        return preferedTimeSlotIds;
    }

    /**
     * getter
     * @return preferedRoomIds
     */
    public int[] getPreferedRoomIds() {
        return preferedRoomIds;
    }
    
    /**
     * dump object into a hashmap
     */
    public Map<String, Object> dump() {
        Map<String, Object> obj = new HashMap<>();
        obj.put("id", id);
        obj.put("name", name);        
        
        obj.put("preferedTimeSlotIds", Arrays.stream(preferedTimeSlotIds)
                                        .mapToObj(x -> String.valueOf(x))
                                        .collect(Collectors.joining(",")));
        
        obj.put("preferedRoomIds", Arrays.stream(preferedRoomIds)
                                        .mapToObj(x -> String.valueOf(x))
                                        .collect(Collectors.joining(",")));
        return obj;
    }
    
    /**
     * new object from a hashmap
     */
    public Teacher(Map<String, Object> obj) {
        this.id = (Integer) obj.get("id");
        this.name = (String) obj.get("name");
        
        String[] arr = ((String) obj.get("preferedTimeSlotIds")).split(",");
        preferedTimeSlotIds = new int[arr.length];
        
        for(int i = 0; i < arr.length; i++) {
            preferedTimeSlotIds[i] = Integer.valueOf(arr[i]);
        }
        
        arr = ((String) obj.get("preferedRoomIds")).split(",");
        preferedRoomIds = new int[arr.length];
        
        for(int i = 0; i < arr.length; i++) {
            preferedRoomIds[i] = Integer.valueOf(arr[i]);
        }
    }
}
