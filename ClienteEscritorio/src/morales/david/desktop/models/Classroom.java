package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

public class Classroom {

    private int id;
    private String name;
    private int floor;

    public Classroom() {
        this(-1, "", -1);
    }

    public Classroom(int id, String name, int floor) {
        this.id = id;
        this.name = name;
        this.floor = floor;
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

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", floor=" + floor +
                '}';
    }

    public static Classroom parse(LinkedTreeMap teacherMap) {

        int id = ((Double) teacherMap.get("id")).intValue();
        String name = (String) teacherMap.get("name");
        int floor = ((Double) teacherMap.get("floor")).intValue();

        return new Classroom(id, name, floor);

    }

}
