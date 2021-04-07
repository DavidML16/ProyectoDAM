package morales.david.server.models;

public class Teacher {

    private int id;
    private int number;
    private String name;
    private String abreviation;
    private int minDayHours;
    private int maxDayHours;
    private String department;

    public Teacher(int id, int number, String name, String abreviation, int minDayHours, int maxDayHours, String department) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.abreviation = abreviation;
        this.minDayHours = minDayHours;
        this.maxDayHours = maxDayHours;
        this.department = department;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbreviation() {
        return abreviation;
    }

    public void setAbreviation(String abreviation) {
        this.abreviation = abreviation;
    }

    public int getMinDayHours() {
        return minDayHours;
    }

    public void setMinDayHours(int minDayHours) {
        this.minDayHours = minDayHours;
    }

    public int getMaxDayHours() {
        return maxDayHours;
    }

    public void setMaxDayHours(int maxDayHours) {
        this.maxDayHours = maxDayHours;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
