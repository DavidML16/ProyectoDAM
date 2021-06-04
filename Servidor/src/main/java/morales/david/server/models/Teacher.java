package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.interfaces.ScheduleSearcheable;

public class Teacher implements ScheduleSearcheable {

    private int id;
    private int number;
    private String name;
    private String abreviation;
    private int minDayHours;
    private int maxDayHours;
    private String department;

    /**
     * Empty constructor of Teacher
     */
    public Teacher() {
        this(-1, -1, "", "", -1, -1, "");
    }

    /**
     * Create a new instance of Teacher with given params
     * @param id
     * @param number
     * @param name
     * @param abreviation
     * @param minDayHours
     * @param maxDayHours
     * @param department
     */
    public Teacher(int id, int number, String name, String abreviation, int minDayHours, int maxDayHours, String department) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.abreviation = abreviation;
        this.minDayHours = minDayHours;
        this.maxDayHours = maxDayHours;
        this.department = department;
    }

    /**
     * Get id of the teacher
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the teacher
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get number of the teacher
     * @return number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set number of the teacher
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get name of the teacher
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the teacher
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get abreviation of the teacher
     * @return abreviation
     */
    public String getAbreviation() {
        return abreviation;
    }

    /**
     * Set abreviation of the teacher
     * @param abreviation
     */
    public void setAbreviation(String abreviation) {
        this.abreviation = abreviation;
    }

    /**
     * Get minimum day hours of the teacher
     * @return minDayHours
     */
    public int getMinDayHours() {
        return minDayHours;
    }

    /**
     * Set minimum day hours of the teacher
     * @param minDayHours
     */
    public void setMinDayHours(int minDayHours) {
        this.minDayHours = minDayHours;
    }

    /**
     * Get maximum day hours of the teacher
     * @return maxDayHours
     */
    public int getMaxDayHours() {
        return maxDayHours;
    }

    /**
     * Set maximum day hours of the teacher
     * @param maxDayHours
     */
    public void setMaxDayHours(int maxDayHours) {
        this.maxDayHours = maxDayHours;
    }

    /**
     * Get department of the teacher
     * @return department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set department of the teacher
     * @param department
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", abreviation='" + abreviation + '\'' +
                ", minDayHours=" + minDayHours +
                ", maxDayHours=" + maxDayHours +
                ", department='" + department + '\'' +
                '}';
    }

    /**
     * Convert the received TreeMap to a Teacher object
     * @param teacherMap
     * @return teacher object
     */
    public static Teacher parse(LinkedTreeMap teacherMap) {

        int id = ((Double) teacherMap.get("id")).intValue();
        int number = ((Double) teacherMap.get("number")).intValue();
        String name = (String) teacherMap.get("name");
        String abreviation = (String) teacherMap.get("abreviation");
        int minDayHours = ((Double) teacherMap.get("minDayHours")).intValue();
        int maxDayHours = ((Double) teacherMap.get("maxDayHours")).intValue();
        String department = (String) teacherMap.get("department");

        return new Teacher(id, number, name, abreviation, minDayHours, maxDayHours, department);

    }

}
