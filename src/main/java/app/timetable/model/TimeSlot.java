package app.timetable.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Time slot in a week
 */
public class TimeSlot {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int dayOfWeek;
    
    /**
     * Constructor
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @param dayOfWeek 
     */
    public TimeSlot(int startHour, int startMinute, int endHour, int endMinute, int dayOfWeek) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * getter
     * @return startHour
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * getter
     * @return startMinute
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * getter
     * @return endHour
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * getter
     * @return endMinute
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * getter
     * @return dayOfWeek
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    
    /**
     * dump object into a hashmap
     */
    public Map<String, Object> dump() {
        Map<String, Object> obj = new HashMap<>();        
        obj.put("startHour", startHour);
        obj.put("startMinute", startMinute);
        obj.put("endHour", endHour);
        obj.put("endMinute", endMinute);
        obj.put("dayOfWeek", dayOfWeek);
        return obj;
    }
    
    /**
     * new object from a hashmap
     */
    public TimeSlot(Map<String, Object> obj) {
        this.startHour = (Integer) obj.get("startHour");
        this.startMinute = (Integer) obj.get("startMinute");
        this.endHour = (Integer) obj.get("endHour");
        this.endMinute = (Integer) obj.get("endMinute");        
        this.dayOfWeek = (Integer) obj.get("dayOfWeek");        
    }
        
}
