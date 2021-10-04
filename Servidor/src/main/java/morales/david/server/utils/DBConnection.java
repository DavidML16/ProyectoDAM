package morales.david.server.utils;

import morales.david.server.interfaces.ScheduleSearcheable;
import morales.david.server.clients.ClientSession;
import morales.david.server.models.*;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    private Connection connection;

    public DBConnection() {
        this.connection = null;
    }

    /**
     * Open mysql connection if its not oppened
     */
    public void open() {
        try {
            if(connection != null && !connection.isClosed())
                return;
            String url = "jdbc:mysql://" + DBConstants.DB_IP + ":" + DBConstants.DB_PORT + "/" + DBConstants.DB_DATABASE;
            connection = DriverManager.getConnection(url, DBConstants.DB_USER, DBConstants.DB_PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close mysql connection if its open
     */
    public void close() {
        try {
            if(connection != null && connection.isClosed())
                return;
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean testConnection(String ip, String user, String password) {

        boolean valid = false;

        try {

            String url = "jdbc:mysql://" + ip + ":" + DBConstants.DB_PORT + "/";
            connection = DriverManager.getConnection(url, user, password);

            if(connection != null)
                valid = true;

        } catch (SQLException e) {

            valid = false;

        } finally {

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        return valid;

    }

    public boolean checkDatabaseExists(String ip, String user, String password, String dbName) {

        boolean exists = false;

        ResultSet rs = null;

        try {

            String url = "jdbc:mysql://" + ip + ":" + DBConstants.DB_PORT + "/";
            connection = DriverManager.getConnection(url, user, password);

            if(connection == null)
                exists = false;

            rs = connection.getMetaData().getCatalogs();

            while (rs.next()) {

                String database = rs.getString(1);

                if(database.equalsIgnoreCase(dbName))
                    exists = true;

            }

        } catch (SQLException e) {

            exists = false;

        } finally {

            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        return exists;

    }

    public boolean createDatabase(String ip, String user, String password, String database) {

        try {

            String url = "jdbc:mysql://" + ip + ":" + DBConstants.DB_PORT + "/";
            connection = DriverManager.getConnection(url, user, password);

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);

            stmt.close();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

        } finally {

            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        return false;

    }

    /**
     * Get connection instance
     * @return connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Check if exists credential provided username and password
     * @param username
     * @param password
     * @return credential exists
     */
    public boolean existsCredential(String username, String password) {

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CREDENTIAL);

            stm.setString(1, username);
            stm.setString(2, password);

            rs = stm.executeQuery();

            if(rs.next())
                return true;

            return false;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return false;

    }

    /**
     * Get credentials from database
     * @return list of credentials
     */
    public List<Credential> getCredentials() {

        List<Credential> credentials = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CREDENTIALS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_credencial");
                String username = rs.getString("usuario");
                String password = rs.getString("passwd_hash");
                String role = rs.getString("rol");

                int id_teacher = rs.getInt("profesor");

                Teacher teacher = null;
                if(id_teacher > 0)
                    teacher = getTeacher(id_teacher);

                credentials.add(new Credential(id, username, password, role, teacher));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return credentials;

    }

    /**
     * Get credential from database by specified id
     * @return credential
     */
    public Credential getCredential(int _id) {

        Credential credential = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CREDENTIAL_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_credencial");
                String username = rs.getString("usuario");
                String password = rs.getString("passwd_hash");
                String role = rs.getString("rol");

                int id_teacher = rs.getInt("profesor");

                Teacher teacher = getTeacher(id_teacher);

                credential = new Credential(id, username, password, role, teacher);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return credential;

    }

    /**
     * Add a new credential to database
     * @param credential
     * @return credential added
     */
    public boolean addCredential(Credential credential) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCREDENTIAL);

            stm.setString(1, credential.getUsername());
            stm.setString(2, credential.getPassword());
            if(credential.getTeacher() != null)
                stm.setInt(3, credential.getTeacher().getId());
            else
                stm.setNull(3, java.sql.Types.INTEGER);
            stm.setString(4, credential.getRole());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update credential information from database
     * @param credential
     * @return credential updated
     */
    public boolean updateCredential(Credential credential) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECREDENTIAL);

            stm.setString(1, credential.getUsername());
            stm.setString(2, credential.getPassword());
            if(credential.getTeacher() != null)
                stm.setInt(3, credential.getTeacher().getId());
            else
                stm.setNull(3, java.sql.Types.INTEGER);
            stm.setString(4, credential.getRole());
            stm.setInt(5, credential.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove the credential provided from the database
     * @param credential
     * @return credential deleted
     */
    public boolean removeCredential(Credential credential) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVECREDENTIAL);

            stm.setInt(1, credential.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get credential and teacher information and save into provided object
     * @param username
     * @param clientSession
     */
    public void getUserDetails(String username, ClientSession clientSession) {

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_DETAILS);

            stm.setString(1, username);

            rs = stm.executeQuery();

            if(rs.next()) {

                clientSession.setId(rs.getInt("id"));
                clientSession.setName(rs.getString("usuario"));
                clientSession.setRole(rs.getString("rol"));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }


    /**
     * Get teachers from database
     * @return list of teachers
     */
    public List<Teacher> getTeachers() {

        List<Teacher> teachers = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_TEACHERS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_profesor");
                int number = rs.getInt("numero");
                String name = rs.getString("nombre");
                String abreviation = rs.getString("abreviacion");
                int minDayHours = rs.getInt("minhorasdia");
                int maxDayHours = rs.getInt("maxhorasdia");
                String department = rs.getString("departamento");

                teachers.add(new Teacher(id, number, name, abreviation, minDayHours, maxDayHours, department));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }


        return teachers;

    }

    /**
     * Get teacher from database by specified id
     * @return teacher
     */
    public Teacher getTeacher(int _id) {

        Teacher teacher = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_TEACHER_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_profesor");
                int number = rs.getInt("numero");
                String name = rs.getString("nombre");
                String abreviation = rs.getString("abreviacion");
                int minDayHours = rs.getInt("minhorasdia");
                int maxDayHours = rs.getInt("maxhorasdia");
                String department = rs.getString("departamento");

                teacher = new Teacher(id, number, name, abreviation, minDayHours, maxDayHours, department);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return teacher;

    }

    /**
     * Add a new teacher to database
     * @param teacher
     * @return teacher added
     */
    public boolean addTeacher(Teacher teacher) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDTEACHER);

            stm.setInt(1, teacher.getNumber());
            stm.setString(2, teacher.getName());
            stm.setString(3, teacher.getAbreviation());
            stm.setInt(4, teacher.getMinDayHours());
            stm.setInt(5, teacher.getMaxDayHours());
            stm.setString(6, teacher.getDepartment());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update teacher information from database
     * @param teacher
     * @return teacher updated
     */
    public boolean updateTeacher(Teacher teacher) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATETEACHER);

            stm.setInt(1, teacher.getNumber());
            stm.setString(2, teacher.getName());
            stm.setString(3, teacher.getAbreviation());
            stm.setInt(4, teacher.getMinDayHours());
            stm.setInt(5, teacher.getMaxDayHours());
            stm.setString(6, teacher.getDepartment());
            stm.setInt(7, teacher.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove the teacher provided from the database
     * @param teacher
     * @return teacher deleted
     */
    public boolean removeTeacher(Teacher teacher) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVETEACHER);

            stm.setInt(1, teacher.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get classrooms from database
     * @return list of classrooms
     */
    public List<Classroom> getClassrooms() {

        List<Classroom> classrooms = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CLASSROOMS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_aula");
                String name = rs.getString("nombre");

                classrooms.add(new Classroom(id, name));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return classrooms;

    }

    /**
     * Get classroom from database by specified id
     * @return classroom
     */
    public Classroom getClassroom(int _id) {

        Classroom classroom = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CLASSROOM_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_aula");
                String name = rs.getString("nombre");

                classroom = new Classroom(id, name);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return classroom;

    }

    /**
     * Add a new classroom to database
     * @param classroom
     * @return classroom added
     */
    public boolean addClassroom(Classroom classroom) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCLASSROOM);

            stm.setString(1, classroom.getName());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update classroom information from database
     * @param classroom
     * @return classroom updated
     */
    public boolean updateClassroom(Classroom classroom) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECLASSROOM);

            stm.setString(1, classroom.getName());
            stm.setInt(2, classroom.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove classroom provided from the database
     * @param classroom
     * @return classroom deleted
     */
    public boolean removeClassroom(Classroom classroom) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVECLASSROOM);

            stm.setInt(1, classroom.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Get classrooms from database
     * @return list of classrooms
     */
    public List<Classroom> getEmptyClassroomsTimeZone(TimeZone timeZone) {

        List<Classroom> classrooms = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_EMPTYCLASSROOMSTIMEZONE);
            stm.setInt(1, timeZone.getId());

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_aula");
                String name = rs.getString("nombre");

                classrooms.add(new Classroom(id, name));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return classrooms;

    }


    /**
     * Get courses from database
     * @return list of courses
     */
    public List<Course> getCourses() {

        List<Course> courses = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_COURSES);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_curso");
                String level = rs.getString("nivel");
                String name = rs.getString("nombre");

                courses.add(new Course(id, level, name));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return courses;

    }

    /**
     * Get course from database by specified id
     * @return course
     */
    public Course getCourse(int _id) {

        Course course = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_COURSE_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_curso");
                String level = rs.getString("nivel");
                String name = rs.getString("nombre");

                course = new Course(id, level, name);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return course;

    }

    /**
     * Add a new course to database
     * @param course
     * @return course added
     */
    public boolean addCourse(Course course) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCOURSE);

            stm.setString(1, course.getLevel());
            stm.setString(2, course.getName());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update course information from database
     * @param course
     * @return course updated
     */
    public boolean updateCourse(Course course) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECOURSE);

            stm.setString(1, course.getLevel());
            stm.setString(2, course.getName());
            stm.setInt(3, course.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove course provided from the database
     * @param course
     * @return course deleted
     */
    public boolean removeCourse(Course course) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVECOURSE);

            stm.setInt(1, course.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get groups from database
     * @return list of groups
     */
    public List<Group> getGroups() {

        List<Group> groups = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_GROUPS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_grupo");
                int id_group = rs.getInt("curso");
                String letter = rs.getString("letra");

                Course course = getCourse(id_group);

                groups.add(new Group(id, course, letter));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return groups;

    }

    /**
     * Get group from database by specified id
     * @return group
     */
    public Group getGroup(int _id) {

        Group group = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_GROUP_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_grupo");
                int id_group = rs.getInt("curso");
                String letter = rs.getString("letra");

                Course course = getCourse(id_group);

                group = new Group(id, course, letter);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return group;

    }

    /**
     * Add a new group to database
     * @param group
     * @return group added
     */
    public boolean addGroup(Group group) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDGROUP);

            stm.setInt(1, group.getCourse().getId());
            stm.setString(2, group.getLetter());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update group information from database
     * @param group
     * @return group updated
     */
    public boolean updateGroup(Group group) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATEGROUP);

            stm.setInt(1, group.getCourse().getId());
            stm.setString(2, group.getLetter());
            stm.setInt(3, group.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove group provided from the database
     * @param group
     * @return group deleted
     */
    public boolean removeGroup(Group group) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVEGROUP);

            stm.setInt(1, group.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get subjects from database
     * @return list of subjects
     */
    public List<Subject> getSubjects() {

        List<Subject> subjects = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;
        PreparedStatement stm2 = null;
        ResultSet rs2 = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECTS);
            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_asignatura");
                int number = rs.getInt("numero");
                String abreviation = rs.getString("abreviacion");
                String name = rs.getString("nombre");
                String color = rs.getString("color");

                stm2 = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECTS_COURSES);
                stm2.setInt(1, id);
                rs2 = stm2.executeQuery();

                List<Course> subjectCourses = new ArrayList<>();

                while (rs2.next()) {
                    int id_c = rs2.getInt("id_curso");
                    String level_c = rs2.getString("nivel");
                    String name_c = rs2.getString("nombre");

                    subjectCourses.add(new Course(id_c, level_c, name_c));
                }

                stm2.close();
                rs2.close();

                subjects.add(new Subject(id, number, abreviation, name, color, subjectCourses));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return subjects;

    }

    /**
     * Get subject from database by specified id
     * @return subject
     */
    public Subject getSubject(int _id) {

        Subject subject = null;

        PreparedStatement stm = null;
        ResultSet rs = null;
        PreparedStatement stm2 = null;
        ResultSet rs2 = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECT_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_asignatura");
                int number = rs.getInt("numero");
                String abreviation = rs.getString("abreviacion");
                String name = rs.getString("nombre");
                String color = rs.getString("color");

                stm2 = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECTS_COURSES);
                stm2.setInt(1, id);
                rs2 = stm2.executeQuery();

                List<Course> subjectCourses = new ArrayList<>();

                while (rs2.next()) {
                    int id_c = rs2.getInt("id_curso");
                    String level_c = rs2.getString("nivel");
                    String name_c = rs2.getString("nombre");

                    subjectCourses.add(new Course(id_c, level_c, name_c));
                }

                stm2.close();
                rs2.close();

                subject = new Subject(id, number, abreviation, name, color, subjectCourses);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return subject;

    }

    /**
     * Add a new subject to database
     * @param subject
     * @return subject added
     */
    public boolean addSubject(Subject subject) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDSUBJECT);

            stm.setInt(1, subject.getNumber());
            stm.setString(2, subject.getAbreviation());
            stm.setString(3, subject.getName());
            stm.setString(4, ColorUtil.getColor());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update subject information from database
     * @param subject
     * @return subject updated
     */
    public boolean updateSubject(Subject subject) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATESUBJECT);

            stm.setInt(1, subject.getNumber());
            stm.setString(2, subject.getAbreviation());
            stm.setString(3, subject.getName());
            stm.setInt(4, subject.getId());

            rs = stm.executeUpdate();

            removeAllSubjectRelation(subject);

            for(Course course : subject.getCourses())
                addSubjectRelation(subject, course);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove subject provided from the database
     * @param subject
     * @return subject deleted
     */
    public boolean removeSubject(Subject subject) {

        removeAllSubjectRelation(subject);

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVESUBJECT);

            stm.setInt(1, subject.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove subject relation with all courses
     * @param subject
     */
    private void removeAllSubjectRelation(Subject subject) {

        PreparedStatement stm = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVESUBJECT_RELATION);

            stm.setInt(1, subject.getId());

            stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }

    /**
     * Add subject relation with the specified course
     * @param subject
     * @param course
     */
    public void addSubjectRelation(Subject subject, Course course) {

        PreparedStatement stm = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDSUBJECT_RELATION);

            stm.setInt(1, course.getId());
            stm.setInt(2, subject.getId());

            stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

    }


    /**
     * Get days from database
     * @return list of days
     */
    public List<Day> getDays() {

        List<Day> days = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_DAYS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_dia");
                String name = rs.getString("dia");

                days.add(new Day(id, name));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return days;

    }

    /**
     * Update day information from database
     * @param day
     * @return day updated
     */
    public boolean updateDay(Day day) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATEDAY);

            stm.setString(1, day.getName());
            stm.setInt(2, day.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get hours from database
     * @return list of hours
     */
    public List<Hour> getHours() {

        List<Hour> hours = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_HOURS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_hora");
                String name = rs.getString("horas");

                hours.add(new Hour(id, name));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return hours;

    }

    /**
     * Update hour information from database
     * @param hour
     * @return hour updated
     */
    public boolean updateHour(Hour hour) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATEHOUR);

            stm.setString(1, hour.getName());
            stm.setInt(2, hour.getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }


    /**
     * Get timezones from database
     * @return list of timezones
     */
    public List<TimeZone> getTimeZones() {

        List<TimeZone> timeZones = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_TIMEZONES);

            rs = stm.executeQuery();

            while (rs.next()) {

                int idTimeZone = rs.getInt("idTimeZone");

                int idDay = rs.getInt("idDay");
                String dayString = rs.getString("dayString");

                int idHour = rs.getInt("idHour");
                String hourString = rs.getString("hourString");

                timeZones.add(new TimeZone(idTimeZone, new Day(idDay, dayString), new Hour(idHour, hourString)));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return timeZones;

    }

    /**
     * Get timezone from database by specified id
     * @return timezone
     */
    public TimeZone getTimeZone(int _id) {

        TimeZone timeZone = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_TIMEZONE_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int idTimeZone = rs.getInt("idTimeZone");

                int idDay = rs.getInt("idDay");
                String dayString = rs.getString("dayString");

                int idHour = rs.getInt("idHour");
                String hourString = rs.getString("hourString");

                timeZone = new TimeZone(idTimeZone, new Day(idDay, dayString), new Hour(idHour, hourString));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return timeZone;

    }


    /**
     * Clear all table rows from database
     * @return rows deleted
     */
    public boolean clearAll(boolean backup) {

        try {

            Statement scheduleStm = connection.createStatement();
            scheduleStm.execute(DBConstants.DB_QUERY_CLEAR_SCHEDULES);
            scheduleStm.close();

            Statement timezoneStm = connection.createStatement();
            timezoneStm.execute(DBConstants.DB_QUERY_CLEAR_TIMEZONE);
            timezoneStm.close();

            Statement dayStm = connection.createStatement();
            dayStm.execute(DBConstants.DB_QUERY_CLEAR_DAYS);
            dayStm.close();

            Statement hourStm = connection.createStatement();
            hourStm.execute(DBConstants.DB_QUERY_CLEAR_HOURS);
            hourStm.close();

            Statement teacherStm = connection.createStatement();
            teacherStm.execute(DBConstants.DB_QUERY_CLEAR_TEACHERS);
            teacherStm.close();

            if(backup) {

                Statement credentialStm = connection.createStatement();
                credentialStm.execute(DBConstants.DB_QUERY_CLEAR_CREDENTIALS);
                credentialStm.close();

            }

            Statement courseSubjectsStm = connection.createStatement();
            courseSubjectsStm.execute(DBConstants.DB_QUERY_CLEAR_COURSE_SUBJECTS);
            courseSubjectsStm.close();

            Statement subjectStm = connection.createStatement();
            subjectStm.execute(DBConstants.DB_QUERY_CLEAR_SUBJECTS);
            subjectStm.close();

            Statement groupStm = connection.createStatement();
            groupStm.execute(DBConstants.DB_QUERY_CLEAR_GROUPS);
            groupStm.close();

            Statement classroomStm = connection.createStatement();
            classroomStm.execute(DBConstants.DB_QUERY_CLEAR_CLASSROOMS);
            classroomStm.close();

            Statement courseStm = connection.createStatement();
            courseStm.execute(DBConstants.DB_QUERY_CLEAR_COURSES);
            courseStm.close();

            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;

    }

    /**
     * Reset all table from database
     * @return tables reseted
     */
    public boolean resetAll(boolean backup) {

        try {

            Statement scheduleStm = connection.createStatement();
            scheduleStm.execute(DBConstants.DB_QUERY_RESET_SCHEDULES);
            scheduleStm.close();

            Statement timezoneStm = connection.createStatement();
            timezoneStm.execute(DBConstants.DB_QUERY_RESET_TIMEZONE);
            timezoneStm.close();

            Statement dayStm = connection.createStatement();
            dayStm.execute(DBConstants.DB_QUERY_RESET_DAYS);
            dayStm.close();

            Statement hourStm = connection.createStatement();
            hourStm.execute(DBConstants.DB_QUERY_RESET_HOURS);
            hourStm.close();

            Statement teacherStm = connection.createStatement();
            teacherStm.execute(DBConstants.DB_QUERY_RESET_TEACHERS);
            teacherStm.close();

            if(backup) {

                Statement credentialStm = connection.createStatement();
                credentialStm.execute(DBConstants.DB_QUERY_RESET_CREDENTIALS);
                credentialStm.close();

            }

            Statement subjectStm = connection.createStatement();
            subjectStm.execute(DBConstants.DB_QUERY_RESET_SUBJECTS);
            subjectStm.close();

            Statement groupStm = connection.createStatement();
            groupStm.execute(DBConstants.DB_QUERY_RESET_GROUPS);
            groupStm.close();

            Statement classroomStm = connection.createStatement();
            classroomStm.execute(DBConstants.DB_QUERY_RESET_CLASSROOMS);
            classroomStm.close();

            Statement courseStm = connection.createStatement();
            courseStm.execute(DBConstants.DB_QUERY_RESET_COURSES);
            courseStm.close();

            return true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;

    }


    /**
     * Insert days information to database
     * @param sb
     * @return days added
     */
    public boolean insertDaysSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_DAYS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert hours information to database
     * @param sb
     * @return hours added
     */
    public boolean insertHoursSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_HOURS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert timezones information to database
     * @param sb
     * @return timezones added
     */
    public boolean insertTimezoneSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_TIMEZONE + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert teachers information to database
     * @param sb
     * @return teachers added
     */
    public boolean insertTeachersSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_TEACHERS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert subjects information to database
     * @param sb
     * @return subjects added
     */
    public boolean insertSubjectsSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_SUBJECTS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert classrooms information to database
     * @param sb
     * @return classrooms added
     */
    public boolean insertClassroomsSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_CLASSROOMS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert courses information to database
     * @param sb
     * @return courses added
     */
    public boolean insertCoursesSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_COURSES + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert courses subjects information to database
     * @param sb
     * @return courses subjects added
     */
    public boolean insertCourseSubjectsSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_COURSE_SUBJECTS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert groups information to database
     * @param sb
     * @return groups added
     */
    public boolean insertGroupsSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_GROUPS + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Insert schedules information to database
     * @param sb
     * @return schedules added
     */
    public boolean insertSchedulesSB(StringBuilder sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSB_SCHEDULES + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;


    }

    /**
     * Get schedules from database
     * @return list of schedules
     */
    public Integer getSchedulesAmount() {

        Integer schedules = 0;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_SCHEDULES);

            rs = stm.executeQuery();

            if (rs.next()) {

                schedules = rs.getInt("amount");

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return schedules;

    }

    /**
     * Get schedules from database by search query
     * @return list of schedules
     */
    public List<SchedulerItem> searchSchedule(ScheduleSearcheable item) {

        List<SchedulerItem> schedules = new ArrayList<>();

        String sqlSearch = "";

        if(item instanceof Teacher) {
            Teacher teacher = (Teacher) item;
            sqlSearch = "profesor = " + teacher.getId();
        } else if(item instanceof Group) {
            Group group = (Group) item;
            sqlSearch = "grupo = " + group.getId();
        } else if(item instanceof Classroom) {
            Classroom classroom = (Classroom) item;
            sqlSearch = "aula = " + classroom.getId();
        }

        schedules = getSchedule(sqlSearch);

        return schedules;

    }
    private List<SchedulerItem> getSchedule(String params) {

        List<SchedulerItem> schedules = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_SEARCH_SCHEDULE + params + " ORDER BY franja");

            rs = stm.executeQuery();

            List<Schedule> tempSchedule = new ArrayList<>();
            int tempTimezoneId = 0;
            boolean first = true;

            TimeZone tempTimeZone = null;

            while (rs.next()) {

                int id_teacher = rs.getInt("profesor");
                int id_subject = rs.getInt("asignatura");
                int id_group = rs.getInt("grupo");
                int id_classroom = rs.getInt("aula");
                int id_timezone = rs.getInt("franja");
                String uuid = rs.getString("uuid");

                if(first) {
                    first = false;
                    tempTimezoneId = id_timezone;
                    tempSchedule = new ArrayList<>();
                }

                Teacher teacher = getTeacher(id_teacher);
                Subject subject = getSubject(id_subject);
                Group group = getGroup(id_group);
                Classroom classroom = getClassroom(id_classroom);
                TimeZone timeZone = getTimeZone(id_timezone);

                if(id_timezone == tempTimezoneId) {

                    tempSchedule.add(new Schedule(uuid, teacher, subject, group, classroom, timeZone));

                    tempTimeZone = timeZone;

                } else {

                    schedules.add(new SchedulerItem(tempTimeZone, tempSchedule));

                    tempTimeZone = timeZone;

                    tempTimezoneId = id_timezone;
                    tempSchedule = new ArrayList<>();

                    tempSchedule.add(new Schedule(uuid, teacher, subject, group, classroom, timeZone));

                }

            }

            if(!first)
                schedules.add(new SchedulerItem(tempTimeZone, tempSchedule));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return schedules;

    }

    /**
     * Get schedules turns from database by search query
     * @return list of schedules
     */
    public List<ScheduleTurn> getScheduleTurns(TimeZone timeZone) {

        List<ScheduleTurn> schedules = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_SEARCH_SCHEDULE_TURNS + " franja = " + timeZone.getId());

            rs = stm.executeQuery();

            while (rs.next()) {

                int id_teacher = rs.getInt("profesor");
                int id_group = rs.getInt("grupo");
                int id_classroom = rs.getInt("aula");

                Teacher teacher = getTeacher(id_teacher);
                Group group = getGroup(id_group);
                Classroom classroom = getClassroom(id_classroom);

                ScheduleTurn scheduleTurn = new ScheduleTurn();

                if(teacher != null)
                    scheduleTurn.setTeacher(teacher);

                if(group != null && group.getCourse() != null)
                    scheduleTurn.setCourse(group.getCourse());

                if(classroom != null)
                    scheduleTurn.setClassroom(classroom);

                schedules.add(scheduleTurn);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return schedules;

    }

    /**
     * Insert schedule provided to the database
     * @param schedule
     * @return schedule inserted
     */
    public boolean insertSchedule(Schedule schedule) {

        if(schedule.getSubject() == null)
            return false;

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_INSERTSCHEDULE);

            stm.setString(1, schedule.getUuid());

            if(schedule.getTeacher() != null)
                stm.setInt(2, schedule.getTeacher().getId());
            else
                stm.setNull(2, Types.INTEGER);

            if(schedule.getSubject() != null)
                stm.setInt(3, schedule.getSubject().getId());
            else
                stm.setNull(3, Types.INTEGER);

            if(schedule.getGroup() != null)
                stm.setInt(4, schedule.getGroup().getId());
            else
                stm.setNull(4, Types.INTEGER);

            if(schedule.getClassroom() != null)
                stm.setInt(5, schedule.getClassroom().getId());
            else
                stm.setNull(5, Types.INTEGER);

            stm.setInt(6, schedule.getTimeZone().getId());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Update schedule provided from the database
     * @param schedule
     * @return schedule deleted
     */
    public boolean updateSchedule(Schedule schedule) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATESCHEDULE);

            stm.setInt(1, schedule.getTeacher().getId());
            stm.setInt(2, schedule.getSubject().getId());
            stm.setInt(3, schedule.getGroup().getId());
            stm.setInt(4, schedule.getClassroom().getId());
            stm.setInt(5, schedule.getTimeZone().getId());
            stm.setString(6, schedule.getUuid());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove schedule provided from the database
     * @param schedule
     * @return schedule deleted
     */
    public boolean removeSchedule(Schedule schedule) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVESCHEDULE);

            stm.setString(1, schedule.getUuid());

            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;

    }

    /**
     * Remove schedules information to database
     * @param sb
     * @return schedules removed
     */
    public boolean removeSchedulesSB(String sb) {

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_REMOVESCHEDULE_SB + sb.toString());
            rs = stm.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return rs == 1;


    }

    public void insertTables() {

        try {

            Statement s1 = connection.createStatement();
            s1.execute(
                    "CREATE TABLE IF NOT EXISTS profesor (" +
                    "    id_profesor INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    numero INTEGER(5)," +
                    "    nombre VARCHAR(150) NOT NULL," +
                    "    abreviacion VARCHAR(5) NOT NULL," +
                    "    minhorasdia INTEGER(3)," +
                    "    maxhorasdia INTEGER(3)," +
                    "    departamento VARCHAR(50) NOT NULL" +
                    ");"
            );
            s1.close();

            Thread.sleep(250);

            Statement s2 = connection.createStatement();
            s2.execute(
                    "CREATE TABLE IF NOT EXISTS credencial (" +
                    "    id_credencial INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    usuario VARCHAR(255) NOT NULL," +
                    "    passwd_hash VARCHAR(255) NOT NULL," +
                    "    profesor INTEGER(11)," +
                    "    rol VARCHAR(50) NOT NULL," +
                    "    CONSTRAINT credencial_prof_fk FOREIGN KEY (profesor) REFERENCES profesor(id_profesor) ON DELETE CASCADE" +
                    ");"
            );
            s2.close();

            Thread.sleep(250);

            Statement s3 = connection.createStatement();
            s3.execute(
                    "CREATE TABLE IF NOT EXISTS aula (" +
                    "    id_aula INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    nombre VARCHAR(150) NOT NULL" +
                    ");"
            );
            s3.close();

            Thread.sleep(250);

            Statement s4 = connection.createStatement();
            s4.execute(
                    "CREATE TABLE IF NOT EXISTS curso (" +
                    "    id_curso INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    nivel VARCHAR(10) NOT NULL," +
                    "    nombre VARCHAR(150) NOT NULL" +
                    ");"
            );
            s4.close();

            Thread.sleep(250);

            Statement s5 = connection.createStatement();
            s5.execute(
                    "CREATE TABLE IF NOT EXISTS asignatura (" +
                    "    id_asignatura INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    numero INTEGER(5) NOT NULL," +
                    "    abreviacion VARCHAR(150) NOT NULL," +
                    "    nombre VARCHAR(150) NOT NULL," +
                    "    color VARCHAR(9)" +
                    ");"
            );
            s5.close();

            Thread.sleep(250);

            Statement s6 = connection.createStatement();
            s6.execute(
                    "CREATE TABLE IF NOT EXISTS curso_asignatura (" +
                    "    curso INTEGER(11)," +
                    "    asignatura INTEGER(11)," +
                    "    CONSTRAINT c_s_pk PRIMARY KEY (curso, asignatura)," +
                    "    CONSTRAINT c_fk FOREIGN KEY (curso) REFERENCES curso(id_curso) ON DELETE CASCADE," +
                    "    CONSTRAINT a_fk FOREIGN KEY (asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE" +
                    ");"
            );
            s6.close();

            Thread.sleep(250);

            Statement s7 = connection.createStatement();
            s7.execute(
                    "CREATE TABLE IF NOT EXISTS numero_dia (" +
                    "    id_dia INTEGER(11) PRIMARY KEY," +
                    "    dia VARCHAR(150) NOT NULL" +
                    ");"
            );
            s7.close();

            Thread.sleep(250);

            Statement s8 = connection.createStatement();
            s8.execute(
                    "CREATE TABLE IF NOT EXISTS numero_hora (" +
                    "    id_hora INTEGER(11) PRIMARY KEY," +
                    "    horas VARCHAR(150) NOT NULL" +
                    ");"
            );
            s8.close();

            Thread.sleep(250);

            Statement s9 = connection.createStatement();
            s9.execute(
                    "CREATE TABLE IF NOT EXISTS franja_horaria (" +
                    "    id_franja INTEGER(11) PRIMARY KEY," +
                    "    dia INTEGER(11) NOT NULL," +
                    "    hora INTEGER(11) NOT NULL," +
                    "    CONSTRAINT franja_dia FOREIGN KEY (dia) REFERENCES numero_dia(id_dia) ON DELETE CASCADE," +
                    "    CONSTRAINT franja_hora FOREIGN KEY (hora) REFERENCES numero_hora(id_hora) ON DELETE CASCADE" +
                    ");"
            );
            s9.close();

            Thread.sleep(250);

            Statement s10 = connection.createStatement();
            s10.execute(
                    "CREATE TABLE IF NOT EXISTS grupo (" +
                    "    id_grupo INTEGER(11) PRIMARY KEY AUTO_INCREMENT," +
                    "    curso INTEGER(11) NOT NULL," +
                    "    letra VARCHAR(10) NOT NULL," +
                    "    CONSTRAINT grupo_fk FOREIGN KEY (curso) REFERENCES curso(id_curso) ON DELETE CASCADE" +
                    ");"
            );
            s10.close();

            Thread.sleep(250);

            Statement s11 = connection.createStatement();
            s11.execute(
                    "CREATE TABLE IF NOT EXISTS imparte (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "profesor INTEGER(11) NOT NULL," +
                    "asignatura INTEGER(11) NOT NULL," +
                    "grupo INTEGER(11) NULL," +
                    "aula INTEGER(11) NULL," +
                    "franja INTEGER(11) NOT NULL," +
                    "CONSTRAINT imparte_profesor_fk FOREIGN KEY (profesor) REFERENCES profesor(id_profesor) ON DELETE CASCADE," +
                    "CONSTRAINT imparte_asignatura_fk FOREIGN KEY (asignatura) REFERENCES asignatura(id_asignatura) ON DELETE CASCADE," +
                    "CONSTRAINT imparte_grupo_fk FOREIGN KEY (grupo) REFERENCES grupo(id_grupo) ON DELETE CASCADE," +
                    "CONSTRAINT imparte_aula_fk FOREIGN KEY (aula) REFERENCES aula(id_aula) ON DELETE CASCADE," +
                    "CONSTRAINT imparte_franja_fk FOREIGN KEY (franja) REFERENCES franja_horaria(id_franja) ON DELETE CASCADE" +
                    ");"
            );
            s11.close();
            
            Thread.sleep(250);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void insertUser(String username, String password) {

        try {

            Statement s1 = connection.createStatement();
            s1.execute("REPLACE INTO credencial VALUES (1,'" + username + "','" + HashUtil.sha1(password) + "',NULL,'directivo');");
            s1.close();

            Thread.sleep(250);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
