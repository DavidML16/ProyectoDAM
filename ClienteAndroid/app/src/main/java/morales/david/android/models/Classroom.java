package morales.david.android.models;

import com.google.gson.internal.LinkedTreeMap;

public class Classroom {

    private int id;
    private String name;

    /**
     * Empty constructor of Classroom
     */
    public Classroom() {
        this(-1, "");
    }

    /**
     * Create a new instance of Classroom with given params
     * @param id
     * @param name
     */
    public Classroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Get id of the classroom
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the classroom
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of the classroom
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the classroom
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Convert the received TreeMap to a Classroom object
     * @param teacherMap
     * @return classroom object
     */
    public static Classroom parse(LinkedTreeMap teacherMap) {

        int id = ((Double) teacherMap.get("id")).intValue();
        String name = (String) teacherMap.get("name");

        return new Classroom(id, name);

    }

}
