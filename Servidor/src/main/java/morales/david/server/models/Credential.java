package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

public class Credential {

    private int id;
    private String username;
    private String password;
    private String role;

    private Teacher teacher;

    /**
     * Empty constructor of Credential
     */
    public Credential() {
        this(-1, "", "", "", null);
    }

    /**
     * Create a new instance of Credential with given params
     * @param id
     * @param username
     * @param password
     * @param role
     * @param teacher
     */
    public Credential(int id, String username, String password, String role, Teacher teacher) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.teacher = teacher;
    }

    /**
     * Get id of the credential
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the credential
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get username of the credential
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username of the credential
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password of the credential
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password of the credential
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get role of the credential
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set role of the credential
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Get associated teacher of the crential
     * @return teacher
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Set associated teacher of the credential
     * @param teacher
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    /**
     * Returns if the role is teacher
     * @return have role of teacher
     */
    public boolean isTeacherRole() {
        return getRole().equalsIgnoreCase("profesor");
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

    /**
     * Convert the received TreeMap to a Credential object
     * @param credentialMap
     * @return credential object
     */
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
