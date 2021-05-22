package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.ScheduleSearcheable;

public class Classroom implements ScheduleSearcheable {

    private int id;
    private String name;

    public Classroom() {
        this(-1, "");
    }

    public Classroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

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

    public static Classroom parse(LinkedTreeMap teacherMap) {

        int id = ((Double) teacherMap.get("id")).intValue();
        String name = (String) teacherMap.get("name");

        return new Classroom(id, name);

    }

}
