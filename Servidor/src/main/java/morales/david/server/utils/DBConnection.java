package morales.david.server.utils;

import morales.david.server.clients.ClientSession;
import morales.david.server.clients.ClientThread;
import morales.david.server.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    private Connection connection;

    public DBConnection() {
        this.connection = null;
    }

    public void open() {
        try {
            connection = DriverManager.getConnection(DBConstants.DB_URL, DBConstants.DB_USER, DBConstants.DB_PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

        open();

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

        close();

        return false;

    }

    /**
     * Get credentials from database
     * @return list of credentials
     */
    public List<Credential> getCredentials() {

        open();

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

                Teacher teacher = getTeacher(id_teacher);

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

        close();

        return credentials;

    }

    /**
     * Get credential from database by specified id
     * @return credential
     */
    public Credential getCredential(int _id) {

        open();

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

        close();

        return credential;

    }

    /**
     * Add a new credential to database
     * @param credential
     * @return credential added
     */
    public boolean addCredential(Credential credential) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCREDENTIAL);

            stm.setString(1, credential.getUsername());
            stm.setString(2, credential.getPassword());
            stm.setInt(3, credential.getTeacher().getId());
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

        close();

        return rs == 1;

    }

    /**
     * Update credential information from database
     * @param credential
     * @return credential updated
     */
    public boolean updateCredential(Credential credential) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECREDENTIAL);

            stm.setString(1, credential.getUsername());
            stm.setString(2, credential.getPassword());
            stm.setInt(3, credential.getTeacher().getId());
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

        close();

        return rs == 1;

    }

    /**
     * Remove the credential provided from the database
     * @param credential
     * @return credential deleted
     */
    public boolean removeCredential(Credential credential) {

        open();

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

        close();

        return rs == 1;

    }


    /**
     * Get credential and teacher information and save into provided object
     * @param username
     * @param clientSession
     */
    public void getUserDetails(String username, ClientSession clientSession) {

        open();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_DETAILS);

            stm.setString(1, username);

            rs = stm.executeQuery();

            if(rs.next()) {

                clientSession.setId(rs.getInt("id_profesor"));
                clientSession.setName(rs.getString("nombre"));
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

        close();

    }


    /**
     * Get teachers from database
     * @return list of teachers
     */
    public List<Teacher> getTeachers() {

        open();

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

        close();

        return teachers;

    }

    /**
     * Get teacher from database by specified id
     * @return teacher
     */
    public Teacher getTeacher(int _id) {

        open();

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

        close();

        return teacher;

    }

    /**
     * Add a new teacher to database
     * @param teacher
     * @return teacher added
     */
    public boolean addTeacher(Teacher teacher) {

        open();

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

        close();

        return rs == 1;

    }

    /**
     * Update teacher information from database
     * @param teacher
     * @return teacher updated
     */
    public boolean updateTeacher(Teacher teacher) {

        open();

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

        close();

        return rs == 1;

    }

    /**
     * Remove the teacher provided from the database
     * @param teacher
     * @return teacher deleted
     */
    public boolean removeTeacher(Teacher teacher) {

        open();

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

        close();

        return rs == 1;

    }


    /**
     * Get classrooms from database
     * @return list of classrooms
     */
    public List<Classroom> getClassrooms() {

        open();

        List<Classroom> classrooms = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_CLASSROOMS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_aula");
                String name = rs.getString("nombre");
                int floor = rs.getInt("planta");

                classrooms.add(new Classroom(id, name, floor));

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

        close();

        return classrooms;

    }

    /**
     * Get classroom from database by specified id
     * @return classroom
     */
    public Classroom getClassroom(int _id) {

        open();

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
                int floor = rs.getInt("planta");

                classroom = new Classroom(id, name, floor);

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

        close();

        return classroom;

    }

    /**
     * Add a new classroom to database
     * @param classroom
     * @return classroom added
     */
    public boolean addClassroom(Classroom classroom) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCLASSROOM);

            stm.setString(1, classroom.getName());
            stm.setInt(2, classroom.getFloor());

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

        close();

        return rs == 1;

    }

    /**
     * Update classroom information from database
     * @param classroom
     * @return classroom updated
     */
    public boolean updateClassroom(Classroom classroom) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECLASSROOM);

            stm.setString(1, classroom.getName());
            stm.setInt(2, classroom.getFloor());
            stm.setInt(3, classroom.getId());

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

        close();

        return rs == 1;

    }

    /**
     * Remove classroom provided from the database
     * @param classroom
     * @return classroom deleted
     */
    public boolean removeClassroom(Classroom classroom) {

        open();

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

        close();

        return rs == 1;

    }


    /**
     * Get courses from database
     * @return list of courses
     */
    public List<Course> getCourses() {

        open();

        List<Course> courses = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_COURSES);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_curso");
                int level = rs.getInt("nivel");
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

        close();

        return courses;

    }

    /**
     * Get course from database by specified id
     * @return course
     */
    public Course getCourse(int _id) {

        open();

        Course course = null;

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_COURSE_BY_ID);
            stm.setInt(1, _id);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_curso");
                int level = rs.getInt("nivel");
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

        close();

        return course;

    }

    /**
     * Add a new course to database
     * @param course
     * @return course added
     */
    public boolean addCourse(Course course) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDCOURSE);

            stm.setInt(1, course.getLevel());
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

        close();

        return rs == 1;

    }

    /**
     * Update course information from database
     * @param course
     * @return course updated
     */
    public boolean updateCourse(Course course) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_UPDATECOURSE);

            stm.setInt(1, course.getLevel());
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

        close();

        return rs == 1;

    }

    /**
     * Remove course provided from the database
     * @param course
     * @return course deleted
     */
    public boolean removeCourse(Course course) {

        open();

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

        close();

        return rs == 1;

    }


    /**
     * Get subjects from database
     * @return list of subjects
     */
    public List<Subject> getSubjects() {

        open();

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

                stm2 = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECTS_COURSES);
                stm2.setInt(1, id);
                rs2 = stm2.executeQuery();

                List<Course> subjectCourses = new ArrayList<>();

                while (rs2.next()) {
                    int id_c = rs2.getInt("id_curso");
                    int level_c = rs2.getInt("nivel");
                    String name_c = rs2.getString("nombre");

                    subjectCourses.add(new Course(id_c, level_c, name_c));
                }

                stm2.close();
                rs2.close();

                subjects.add(new Subject(id, number, abreviation, name, subjectCourses));

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

        close();

        return subjects;

    }

    /**
     * Get subject from database by specified id
     * @return subject
     */
    public Subject getSubject(int _id) {

        open();

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

                stm2 = connection.prepareStatement(DBConstants.DB_QUERY_SUBJECTS_COURSES);
                stm2.setInt(1, id);
                rs2 = stm2.executeQuery();

                List<Course> subjectCourses = new ArrayList<>();

                while (rs2.next()) {
                    int id_c = rs2.getInt("id_curso");
                    int level_c = rs2.getInt("nivel");
                    String name_c = rs2.getString("nombre");

                    subjectCourses.add(new Course(id_c, level_c, name_c));
                }

                stm2.close();
                rs2.close();

                subject = new Subject(id, number, abreviation, name, subjectCourses);

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

        close();

        return subject;

    }

    /**
     * Add a new subject to database
     * @param subject
     * @return subject added
     */
    public boolean addSubject(Subject subject) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(DBConstants.DB_QUERY_ADDSUBJECT);

            stm.setInt(1, subject.getNumber());
            stm.setString(2, subject.getAbreviation());
            stm.setString(3, subject.getName());

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

        close();

        return rs == 1;

    }

    /**
     * Update subject information from database
     * @param subject
     * @return subject updated
     */
    public boolean updateSubject(Subject subject) {

        open();

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

        close();

        return rs == 1;

    }

    /**
     * Remove subject provided from the database
     * @param subject
     * @return subject deleted
     */
    public boolean removeSubject(Subject subject) {

        open();

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

        close();

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

        open();

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

        close();

        return days;

    }

    /**
     * Update day information from database
     * @param day
     * @return day updated
     */
    public boolean updateDay(Day day) {

        open();

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

        close();

        return rs == 1;

    }


    /**
     * Get hours from database
     * @return list of hours
     */
    public List<Hour> getHours() {

        open();

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

        close();

        return hours;

    }

    /**
     * Update hour information from database
     * @param hour
     * @return hour updated
     */
    public boolean updateHour(Hour hour) {

        open();

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

        close();

        return rs == 1;

    }

}
