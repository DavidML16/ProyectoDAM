package morales.david.android.models;

import com.google.gson.internal.LinkedTreeMap;

public class Course {

    private int id;
    private int level;
    private String name;

    public Course() {
        this(-1, -1, "");
    }

    public Course(int id, int level, String name) {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
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
        int level = ((Double) courseMap.get("level")).intValue();
        String name = (String) courseMap.get("name");

        return new Course(id, level, name);

    }

}
