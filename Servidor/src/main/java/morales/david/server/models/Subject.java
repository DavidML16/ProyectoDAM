package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    private int id;
    private int number;
    private String abreviation;
    private String name;
    private String color;

    private List<Course> courses;

    /**
     * Empty constructor of Subject
     */
    public Subject() {
        this(-1, -1, "", "", "FFFFFF", new ArrayList<>());
    }

    /**
     * Create a new instance of Subject with given params
     * @param id
     * @param number
     * @param abreviation
     * @param name
     * @param color
     * @param courses
     */
    public Subject(int id, int number, String abreviation, String name, String color, List<Course> courses) {
        this.id = id;
        this.number = number;
        this.abreviation = abreviation;
        this.name = name;
        this.color = color;
        this.courses = courses;
    }

    /**
     * Get id of the subject
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the subject
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get number of the subject
     * @return number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set number of the subject
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get abreviation of the subject
     * @return abreviation
     */
    public String getAbreviation() {
        return abreviation;
    }

    /**
     * Set abreviation of the subject
     * @param abreviation
     */
    public void setAbreviation(String abreviation) {
        this.abreviation = abreviation;
    }

    /**
     * Get name of the subject
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the subject
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get associated courses list of the subject
     * @return courses list
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Set associated courses list of the subject
     * @param courses
     */
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * Get color of the subject
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * Set color of the subject
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", number=" + number +
                ", abreviation='" + abreviation + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", courses=" + courses +
                '}';
    }

    /**
     * Convert the received TreeMap to a Subject object
     * @param subjectMap
     * @return subject object
     */
    public static Subject parse(LinkedTreeMap subjectMap) {

        int id = ((Double) subjectMap.get("id")).intValue();
        int number = ((Double) subjectMap.get("number")).intValue();
        String abreviation = (String) subjectMap.get("abreviation");
        String name = (String) subjectMap.get("name");
        String color = (String) subjectMap.get("color");

        List<LinkedTreeMap> crs = (List<LinkedTreeMap>) subjectMap.get("courses");

        List<Course> courses = new ArrayList<>();

        for(LinkedTreeMap courseMap : crs)
            courses.add(Course.parse(courseMap));

        return new Subject(id, number, abreviation, name, color, courses);

    }

}
