package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Day {

    private int id;
    private String name;

    public Day() {
        this(-1, "");
    }

    public Day(int id, String name) {
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
        return name;
    }

    public static Day parse(LinkedTreeMap dayMap) {

        int id = ((Double) dayMap.get("id")).intValue();
        String name = (String) dayMap.get("name");

        return new Day(id, name);

    }

}
