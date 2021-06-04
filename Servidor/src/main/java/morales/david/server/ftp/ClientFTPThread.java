package morales.david.server.ftp;

import morales.david.server.Server;
import morales.david.server.models.packets.Packet;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.FileTransferProcessor;

import java.io.*;
import java.net.Socket;

public class ClientFTPThread extends Thread {

    private Server server;

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    /**
     * Create a new instance of client ftp thread with given server and socket
     * @param server
     * @param socket
     */
    public ClientFTPThread(Server server, Socket socket) {

        this.server = server;
        this.socket = socket;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO);
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        boolean running = true;

        while (running) {

            try {

                if(input != null && input.ready()) {

                    String fileName = null;
                    try {

                        fileName = input.readLine();

                        output.write("ok" + "\n");
                        output.flush();

                        String filePath = "files/" + fileName;

                        FileTransferProcessor ftp = new FileTransferProcessor(socket);
                        ftp.receiveFile(filePath);

                        File dbfile = new File(filePath);
                        if(dbfile.exists() && !server.getImportManager().isImporting()) {
                            server.getImportManager().setFile(dbfile);
                            server.getImportManager().importDatabase();
                        }

                        running = false;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get server of client thread
     * @return server
     */
    public Server getServer() { return server; }

    /**
     * Get socket of client thread
     * @return socket
     */
    public Socket getSocket() { return socket; }


}
