package morales.david.server.utils;

import morales.david.server.models.ClientSession;
import morales.david.server.threads.ClientThread;

import java.sql.*;

public class DBConnection {

    private ClientThread clientThread;

    private Connection connection;

    public DBConnection(ClientThread clientThread) {

        this.clientThread = clientThread;
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

}
