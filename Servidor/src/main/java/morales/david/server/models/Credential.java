package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Credential {

    private int id;
    private String username;
    private String password;
    private String role;

    private Teacher teacher;

    public Credential() {
        this(-1, "", "", "", null);
    }

    public Credential(int id, String username, String password, String role, Teacher teacher) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.teacher = teacher;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", teacher=" + teacher +
                '}';
    }

    public static Credential parse(LinkedTreeMap credentialMap) {

        int id = ((Double) credentialMap.get("id")).intValue();
        String username = (String) credentialMap.get("username");
        String password = (String) credentialMap.get("password");
        String role = (String) credentialMap.get("role");

        Teacher teacher = null;

        if(credentialMap.get("teacher") != null) {
            LinkedTreeMap teacherMap = (LinkedTreeMap) credentialMap.get("teacher");
            teacher = Teacher.parse(teacherMap);
        }

        return new Credential(id, username, password, role, teacher);

    }

}
