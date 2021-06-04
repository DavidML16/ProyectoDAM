package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Course {

    private int id;
    private String level;
    private String name;

    /**
     * Empty constructor of Course
     */
    public Course() {
        this(-1, "", "");
    }

    /**
     * Create a new instance of Course with given params
     * @param id
     * @param name
     */
    public Course(int id, String level, String name) {
        this.id = id;
        this.level = level;
        this.name = name;
    }

    /**
     * Get id of the course
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the course
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get level of the course
     * @return level
     */
    public String getLevel() {
        return level;
    }

    /**
     * Set level of the course
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Get name of the course
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the course
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return level + "º " + name;
    }

    /**
     * Convert the received TreeMap to a Course object
     * @param courseMap
     * @return course object
     */
    public static Course parse(LinkedTreeMap courseMap) {

        int id = ((Double) courseMap.get("id")).intValue();
        String level = ((String) courseMap.get("level"));
        String name = (String) courseMap.get("name");

        return new Course(id, level, name);

    }

}
