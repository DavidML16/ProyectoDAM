package morales.david.server.utils;

import morales.david.server.clients.ClientSession;
import morales.david.server.clients.ClientThread;
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
            connection = DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PASS);
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

    public boolean existsCredential(String username, String password) {

        open();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_CREDENTIAL);

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

    public void getUserDetails(String username, ClientSession clientSession) {

        open();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_DETAILS);

            stm.setString(1, username);

            rs = stm.executeQuery();

            if(rs.next()) {

                clientSession.setId(rs.getInt("id_prof"));
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

    public List<Teacher> getTeachers() {

        open();

        List<Teacher> teachers = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_TEACHERS);

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("id_prof");
                int number = rs.getInt("numero");
                String name = rs.getString("nombre");
                String abreviation = rs.getString("abrev");
                int minDayHours = rs.getInt("minhdia");
                int maxDayHours = rs.getInt("maxhdia");
                String department = rs.getString("depart");

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

    public boolean addTeacher(Teacher teacher) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_ADDTEACHER);

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

    public boolean updateTeacher(Teacher teacher) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_UPDATETEACHER);

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

    public boolean removeTeacher(Teacher teacher) {

        open();

        PreparedStatement stm = null;
        int rs = 0;

        try {

            stm = connection.prepareStatement(Constants.DB_QUERY_REMOVETEACHER);

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

}
