package morales.david.server.clients;

public class ClientSession {

    private int id;
    private String name;
    private String role;

    /**
     * Empty constructor of ClientSession
     */
    public ClientSession() {
        this(0, "", "");
    }

    /**
     * Create a new instance of ClientSession with given params
     * @param id
     * @param name
     * @param role
     */
    public ClientSession(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    /**
     * Get id of the session
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id of the session
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name of the session
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of the session
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get role of the session
     * @return role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set role of the session
     * @param role
     */
    public void setRole(String role) {
        this.role = role;
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
        return "ClientSession{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

}
