package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

public class Group {

    private int id;
    private Course course;
    private String letter;

    public Group() {
        this(-1, null, "");
    }

    public Group(int id, Course course, String letter) {
        this.id = id;
        this.course = course;
        this.letter = letter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return course.toString() + " " + letter;
    }

    public static Group parse(LinkedTreeMap subjectMap) {

        int id = ((Double) subjectMap.get("id")).intValue();
        Course course = Course.parse((LinkedTreeMap) subjectMap.get("course"));
        String letter = (String) subjectMap.get("letter");

        return new Group(id, course, letter);

    }

}
