package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Day {

    private int id;
    private String name;

    /**
     * Empty constructor of Day
     */
    public Day() {
        this(-1, "");
    }

    /**
     * Create a new instance of Day with given params
     * @param id
     * @param name
     */
    public Day(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get id of the day
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the day
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of the day
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the day
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Convert the received TreeMap to a Day object
     * @param dayMap
     * @return day object
     */
    public static Day parse(LinkedTreeMap dayMap) {

        int id = ((Double) dayMap.get("id")).intValue();
        String name = (String) dayMap.get("name");

        return new Day(id, name);

    }

}
