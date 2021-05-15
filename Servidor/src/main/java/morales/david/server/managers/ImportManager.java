package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.models.Day;
import morales.david.server.models.Hour;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.DBConstants;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ImportManager {

    private File file;

    private Server server;

    private DBConnection dbConnection;
    private Connection acon;

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

        isImporting = true;

        Packet statusPacket = new PacketBuilder()
                .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                .addArgument("importing", isImporting)
                .addArgument("type", "init")
                .addArgument("message", "Archivo recibido, iniciado la importación")
                .build();
        server.getClientRepository().broadcast(statusPacket);

        try {

            acon = DriverManager.getConnection("jdbc:ucanaccess://" + file.getAbsolutePath());

            dbConnection.open();

            if(dbConnection.clearAll()) {

                statusPacket = new PacketBuilder()
                        .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                        .addArgument("importing", isImporting)
                        .addArgument("type", "clear")
                        .addArgument("message", "Se ha limpiado la base de datos correctamente")
                        .build();
                server.getClientRepository().broadcast(statusPacket);

                // DAYS
                {

                    List<Day> dayList = getDays();
                    insertDays(dayList);

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de días importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // HOURS
                {

                    List<Hour> hourList = getHours();
                    insetHours(hourList);


                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de horas importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

            }

            dbConnection.close();

            acon.close();

        } catch (SQLException throwables) {

            statusPacket = new PacketBuilder()
                    .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                    .addArgument("importing", isImporting)
                    .addArgument("type", "error")
                    .addArgument("message", throwables.getMessage())
                    .build();
            server.getClientRepository().broadcast(statusPacket);

        } finally {

            isImporting = false;

            if(file.exists())
                file.delete();

            File directory = new File("files/");
            deleteDirectory(directory, directory);

            statusPacket = new PacketBuilder()
                    .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                    .addArgument("importing", isImporting)
                    .addArgument("type", "end")
                    .addArgument("message", "Importación finalizada!")
                    .build();
            server.getClientRepository().broadcast(statusPacket);

        }

    }

    public List<Day> getDays() {

        List<Day> dayList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT dia FROM `numero-dia`");

            rs = stm.executeQuery();

            int i = 1;

            while (rs.next()) {

                String name = rs.getString("dia");
                dayList.add(new Day(i, name));
                i++;

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

        return dayList;

    }
    public void insertDays(List<Day> dayList) {

        StringBuilder insertString = new StringBuilder();
        for(Day day : dayList) {
            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(").append(day.getId()).append(",'").append(day.getName()).append("')");
            else insertString.append(", (").append(day.getId()).append(",'").append(day.getName()).append("')");
        }

        dbConnection.insertDaysSB(insertString);

    }

    public List<Hour> getHours() {

        List<Hour> hourList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT hora FROM `numero-hora`");

            rs = stm.executeQuery();

            int i = 1;

            while (rs.next()) {

                String name = rs.getString("hora");
                hourList.add(new Hour(i, name));

                i++;

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

        return hourList;

    }
    public void insetHours(List<Hour> hourList) {

        StringBuilder insertString = new StringBuilder();
        for(Hour hour : hourList) {
            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(").append(hour.getId()).append(",'").append(hour.getName()).append("')");
            else insertString.append(", (").append(hour.getId()).append(",'").append(hour.getName()).append("')");
        }

        dbConnection.insertHoursSB(insertString);

    }


    private void deleteDirectory(File parentDirectory, File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(parentDirectory, file);
            }
        }
        if(!parentDirectory.getAbsolutePath().equalsIgnoreCase(directoryToBeDeleted.getAbsolutePath()))
            directoryToBeDeleted.delete();
    }

}
