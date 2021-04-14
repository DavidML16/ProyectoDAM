package morales.david.server.utils;

import morales.david.server.clients.ClientSession;
import morales.david.server.clients.ClientThread;
import morales.david.server.models.Classroom;
import morales.david.server.models.Course;
import morales.david.server.models.Subject;
import morales.david.server.models.Teacher;

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




}
