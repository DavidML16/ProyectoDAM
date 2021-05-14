package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.models.Day;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.DBConnection;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImportManager {

    private File file;

    private Server server;

    private DBConnection dbConnection;

    private boolean isImporting;

    public ImportManager(Server server) {
        this.server = server;
        this.isImporting = false;
        this.dbConnection = new DBConnection();
    }

    public synchronized boolean isImporting() {
        return isImporting;
    }

    public synchronized File getFile() {
        return file;
    }

    public synchronized void setFile(File file) {
        this.file = file;
    }

    public void importDatabase() {

        try {

            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + file.getAbsolutePath() + ";memory=false");

            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {

            if(file.exists())
                file.delete();

        }

    }

    private List<Day> getDays() {

        List<Day> days = new ArrayList<>();

        return days;

    }

}
