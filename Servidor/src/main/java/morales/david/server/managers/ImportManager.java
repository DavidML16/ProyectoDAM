package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.DBConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImportManager {

    private File file;

    private Server server;

    private DBConnection dbConnection;

    private FileInputStream fileIn;
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

            fileIn = new FileInputStream(file);

            StringBuilder asignaturasSB = getAsignaturas();

            System.out.println(asignaturasSB.toString());

            if(file.exists())
                file.delete();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

            Packet importErrorPacket = new PacketBuilder()
                    .ofType(PacketType.SENDACCESSFILE.getError())
                    .addArgument("message", "Archivo no encontrado")
                    .build();

        } finally {

            if(file.exists())
                file.delete();

        }

    }

    private StringBuilder getAsignaturas() {

        StringBuilder sb = new StringBuilder();

        return sb;

    }

}
