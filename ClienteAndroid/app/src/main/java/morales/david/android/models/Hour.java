package morales.david.android.models;

import com.google.gson.internal.LinkedTreeMap;

public class Hour {

    private int id;
    private String name;

    /**
     * Empty constructor of Hour
     */
    public Hour() {
        this(-1, "");
    }

    /**
     * Create a new instance of Hour with given params
     * @param id
     * @param name
     */
    public Hour(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get id of the hour
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the hour
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of the hour
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the hour
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
     * Convert the received TreeMap to a Hour object
     * @param hourMap
     * @return hour object
     */
    public static Hour parse(LinkedTreeMap hourMap) {

        int id = ((Double) hourMap.get("id")).intValue();
        String name = (String) hourMap.get("name");

        return new Hour(id, name);

    }

}
