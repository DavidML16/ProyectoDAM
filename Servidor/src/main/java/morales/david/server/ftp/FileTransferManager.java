package morales.david.server.ftp;

import morales.david.server.Server;
import morales.david.server.clients.ClientRepository;
import morales.david.server.utils.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferManager extends Thread {

    private Server server;
    private ClientRepository clientRepository;

    private ServerSocket serverSocket;

    /**
     * Create a new isntance of FileTransferManager with given server
     * @param server
     */
    public FileTransferManager(Server server) {
        this.server = server;
        this.clientRepository = server.getClientRepository();
        init();
    }

    /**
     * Start the socket server with the specified port in the constant
     */
    private void init() {

        try {
            serverSocket = new ServerSocket(Constants.SERVER_FILE_TRANSFER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        while(true) {

            try {

                Socket clientFTPSocket = serverSocket.accept();

                ClientFTPThread clientThread = new ClientFTPThread(server, clientFTPSocket);
                clientThread.setDaemon(true);
                clientThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
