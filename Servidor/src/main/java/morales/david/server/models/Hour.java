package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Hour {

    private int id;
    private String name;

    public Hour() {
        this(-1, "");
    }

    public Hour(int id, String name) {
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

    public static Hour parse(LinkedTreeMap hourMap) {

        int id = ((Double) hourMap.get("id")).intValue();
        String name = (String) hourMap.get("name");

        return new Hour(id, name);

    }

}
