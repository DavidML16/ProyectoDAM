package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class TimeZone {

    private int id;
    private Day day;
    private Hour hour;

    /**
     * Empty constructor of TimeZone
     */
    public TimeZone() {
        this(-1, null, null);
    }

    /**
     * Create a new instance of TimeZone with given params
     * @param id
     * @param day
     * @param hour
     */
    public TimeZone(int id, Day day, Hour hour) {
        this.id = id;
        this.day = day;
        this.hour = hour;
    }

    /**
     * Get id of the timeZone
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the timeZone
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get associated day of the timeZone
     * @return day
     */
    public Day getDay() {
        return day;
    }

    /**
     * Set associated day of the timeZone
     * @param day
     */
    public void setDay(Day day) {
        this.day = day;
    }

    /**
     * Get associated hour of the timeZone
     * @return hour
     */
    public Hour getHour() {
        return hour;
    }

    /**
     * Set associated hour of the timeZone
     * @param hour
     */
    public void setHour(Hour hour) {
        this.hour = hour;
    }

    /**
     * Convert the received TreeMap to a TimeZone object
     * @param timeZoneMap
     * @return timeZone object
     */
    public static TimeZone parse(LinkedTreeMap timeZoneMap) {

        int id = ((Double) timeZoneMap.get("id")).intValue();

        LinkedTreeMap dayMap = (LinkedTreeMap) timeZoneMap.get("day");
        Day day = Day.parse(dayMap);

        LinkedTreeMap hourMap = (LinkedTreeMap) timeZoneMap.get("hour");
        Hour hour = Hour.parse(hourMap);

        return new TimeZone(id, day, hour);

    }

    @Override
    public String toString() {
        return day.getName() + ", " + hour.getName();
    }

}
