package morales.david.server;

import com.google.gson.Gson;
import morales.david.server.clients.ClientRepository;
import morales.david.server.clients.ClientThread;
import morales.david.server.managers.ClientsManager;
import morales.david.server.ftp.FileTransferManager;
import morales.david.server.managers.ImportManager;
import morales.david.server.utils.ConfigUtil;
import morales.david.server.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {

    private ServerSocket server;

    private ClientRepository clientRepository;

    private ImportManager importManager;
    private FileTransferManager fileTransferManager;

    private boolean running;

    public static final Gson GSON = new Gson();

    /**
     * Init managers, start file tranfer thread, and listen to new client socket connections
     * @throws IOException
     */
    private void init() throws IOException {

        try {

            ConfigUtil configUtil = new ConfigUtil();

            Map<String, String> parameters = configUtil.getConfigParams();
            Constants.SERVER_PORT = Integer.parseInt(parameters.get("server_port"));
            Constants.SERVER_FILE_TRANSFER_PORT = Integer.parseInt(parameters.get("server_file_transfer_port"));
            Constants.CLIENT_CONNECTION_CHECKING_INTERVAL = Integer.parseInt(parameters.get("client_connection_checking_interval"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        server = new ServerSocket(Constants.SERVER_PORT);
        clientRepository = new ClientRepository();
        importManager = new ImportManager(this);
        running = true;

        ClientsManager clientsManager = new ClientsManager(this);
        clientsManager.setDaemon(true);
        clientsManager.start();

        fileTransferManager = new FileTransferManager(this);
        fileTransferManager.setDaemon(true);
        fileTransferManager.start();

        File directory = new File("");
        File fileDir = new File(directory.getAbsolutePath() + File.separator + "files");
        if(!fileDir.exists())
            fileDir.mkdir();

        System.out.println(String.format(Constants.LOG_SERVER_INIT, Constants.SERVER_PORT));

        while(running) {

            Socket clientSocket = server.accept();

            ClientThread clientThread = new ClientThread(this, clientSocket);
            clientThread.setDaemon(true);
            clientThread.start();

            System.out.println(Constants.LOG_SERVER_USER_CONNECTED);

        }

    }

    /**
     * Get client repository where client thread objects are stored
     * @return clientRepository
     */
    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    /**
     * Get access to mysql import manager
     * @return importManager
     */
    public ImportManager getImportManager() { return importManager; }

    /**
     * Get file transfer manager thread
     * @return fileTransferManager
     */
    public FileTransferManager getFileTransferManager() { return fileTransferManager; }

    public static void main(String[] args) {

        Server srv = new Server();
        try {
            srv.init();
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR);
            e.printStackTrace();
        }

    }

}
