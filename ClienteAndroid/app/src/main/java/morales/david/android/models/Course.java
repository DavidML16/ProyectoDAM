package morales.david.android.models;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;

public class Course implements Serializable {

    private int id;
    private String level;
    private String name;

    public Course() {
        this(-1, "", "");
    }

    public Course(int id, String level, String name) {
        this.id = id;
        this.level = level;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return level + "º " + name;
    }

    public static Course parse(LinkedTreeMap courseMap) {

        int id = ((Double) courseMap.get("id")).intValue();
        String level = ((String) courseMap.get("level"));
        String name = (String) courseMap.get("name");

        return new Course(id, level, name);

    }

}
