package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    private int id;
    private int number;
    private String abreviation;
    private String name;

    private List<Course> courses;

    public Subject() {
        this(-1, -1, "", "", new ArrayList<>());
    }

    public Subject(int id, int number, String abreviation, String name, List<Course> courses) {
        this.id = id;
        this.number = number;
        this.abreviation = abreviation;
        this.name = name;
        this.courses = courses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getAbreviation() {
        return abreviation;
    }

    public void setAbreviation(String abreviation) {
        this.abreviation = abreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", number=" + number +
                ", abreviation='" + abreviation + '\'' +
                ", name='" + name + '\'' +
                ", courses=" + courses +
                '}';
    }

    public static Subject parse(LinkedTreeMap subjectMap) {

        int id = ((Double) subjectMap.get("id")).intValue();
        int number = ((Double) subjectMap.get("number")).intValue();
        String abreviation = (String) subjectMap.get("abreviation");
        String name = (String) subjectMap.get("name");

        List<LinkedTreeMap> crs = (List<LinkedTreeMap>) subjectMap.get("courses");

        List<Course> courses = new ArrayList<>();

        for(LinkedTreeMap courseMap : crs)
            courses.add(Course.parse(courseMap));

        return new Subject(id, number, abreviation, name, courses);

    }

}
