package morales.david.server.utils;

import morales.david.server.clients.ClientSession;

import java.io.File;
import java.sql.*;

public class AccessConnection {

    private Connection connection;

    private String filePath;

    public AccessConnection(String filePath) {
        this.filePath = filePath;
        this.connection = null;
    }

    public void open() {
        try {

            String path = Constants.ACCESS_URL + filePath.replace("\\", File.separator);

            connection = DriverManager.getConnection(path);

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

}
