package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.interfaces.ScheduleSearcheable;

public class Group implements ScheduleSearcheable {

    private int id;
    private Course course;
    private String letter;

    /**
     * Empty constructor of Group
     */
    public Group() {
        this(-1, null, "");
    }

    /**
     * Create a new instance of Group with given params
     * @param id
     * @param course
     * @param letter
     */
    public Group(int id, Course course, String letter) {
        this.id = id;
        this.course = course;
        this.letter = letter;
    }

    /**
     * Get id of the group
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the group
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get associated course of the group
     * @return course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Set associated course of the group
     * @param course
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * Get letter of the group
     * @return letter
     */
    public String getLetter() {
        return letter;
    }

    /**
     * Set letter of the group
     * @param letter
     */
    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return course.toString() + " " + letter;
    }

    /**
     * Convert the received TreeMap to a Group object
     * @param subjectMap
     * @return group object
     */
    public static Group parse(LinkedTreeMap subjectMap) {

        int id = ((Double) subjectMap.get("id")).intValue();
        Course course = Course.parse((LinkedTreeMap) subjectMap.get("course"));
        String letter = (String) subjectMap.get("letter");

        return new Group(id, course, letter);

    }

}
