package morales.david.server;

import com.google.gson.Gson;
import morales.david.server.clients.ClientRepository;
import morales.david.server.clients.ClientThread;
import morales.david.server.managers.ImportManager;
import morales.david.server.models.Course;
import morales.david.server.models.Subject;
import morales.david.server.utils.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;

    private ClientRepository clientRepository;

    private ImportManager importManager;

    private boolean running;

    public static final Gson GSON = new Gson();

    private void init() throws IOException {

        server = new ServerSocket(Constants.SERVER_PORT);
        clientRepository = new ClientRepository();
        importManager = new ImportManager(this);
        running = true;

        System.out.println(String.format(Constants.LOG_SERVER_INIT, Constants.SERVER_PORT));

        while(running) {

            Socket clientSocket = server.accept();

            ClientThread clientThread = new ClientThread(this, clientSocket);
            clientThread.setDaemon(true);
            clientThread.start();

            System.out.println(Constants.LOG_SERVER_USER_CONNECTED);

        }

    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public ImportManager getImportManager() { return importManager; }

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
