package morales.david.server;

import com.google.gson.Gson;
import morales.david.server.clients.ClientRepository;
import morales.david.server.clients.ClientThread;
import morales.david.server.managers.ClientsManager;
import morales.david.server.ftp.FileTransferManager;
import morales.david.server.managers.ImportManager;
import morales.david.server.utils.ConfigUtil;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.DBConstants;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private ServerSocket server;

    private ClientRepository clientRepository;

    private ImportManager importManager;
    private FileTransferManager fileTransferManager;

    private String dbIP, dbUsername, dbPassword, dbDatabase;

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
            Constants.SETUP_FIRST_TIME = Boolean.parseBoolean(parameters.get("setup_first_time"));

            if(!Constants.SETUP_FIRST_TIME) {

                Constants.SERVER_FILE_TRANSFER_PORT = Integer.parseInt(parameters.get("server_file_transfer_port"));
                Constants.CLIENT_CONNECTION_CHECKING_INTERVAL = Integer.parseInt(parameters.get("client_connection_checking_interval"));
                DBConstants.DB_IP = parameters.get("db_ip");
                DBConstants.DB_PORT = Integer.parseInt(parameters.get("db_port"));
                DBConstants.DB_DATABASE = parameters.get("db_database");
                DBConstants.DB_USER = parameters.get("db_username");
                DBConstants.DB_PASS = parameters.get("db_password");

            } else {

                Scanner sc = new Scanner(System.in);

                System.out.println(" BIENVENIDO AL PANEL DE CONFIGURACIÓN INICIAL ");
                System.out.println(" -------------------------------------------- ");
                System.out.println("");

                while(!requestSQL())
                    requestSQL();

                String db_database = "";
                while (db_database == null || db_database.isEmpty()) {
                    System.out.println(" 4º Introduce el nombre de la base de datos a crear:");
                    db_database = sc.nextLine();
                }

                dbDatabase = db_database;

                DBConnection dbConnection = new DBConnection();

                if(!dbConnection.createDatabase(dbIP, dbUsername, dbPassword, dbDatabase)) {

                    System.out.println(" Se ha producido un error al crear la base de datos ");

                    return;

                } else {

                    Properties properties = configUtil.getProperties();
                    properties.setProperty("setup_first_time", "false");
                    properties.setProperty("db_ip", dbIP);
                    properties.setProperty("db_port", "3306");
                    properties.setProperty("db_database", dbDatabase);
                    properties.setProperty("db_username", dbUsername);
                    properties.setProperty("db_password", dbPassword);
                    configUtil.saveProperties();

                    DBConstants.DB_IP = dbIP;
                    DBConstants.DB_PORT = Integer.parseInt(parameters.get("db_port"));
                    DBConstants.DB_DATABASE = dbDatabase;
                    DBConstants.DB_USER = dbUsername;
                    DBConstants.DB_PASS =dbPassword;

                    System.out.println(" \n(+) Se ha creado la base de datos correctamente, insertando tablas...");

                    dbConnection.open();

                    dbConnection.insertTables();

                    System.out.println(" \n(+) Se han creado las tablas, ahora se configurará el usuario inicial\n");

                    String username = "";
                    while (username == null || username.isEmpty()) {
                        System.out.println(" 5º Introduce el nombre de usuario:");
                        username = sc.nextLine();
                    }

                    String password = "";
                    while (password == null || password.isEmpty()) {
                        System.out.println(" 6º Introduce la contraseña:");
                        password = sc.nextLine();
                    }

                    dbConnection.insertUser(username, password);

                    System.out.println("\n (+) Se ha creado el usuario inicial y se ha finalizado la configuración");
                    System.out.println(" (+) Ahora se iniciará el servidor...\n");

                    dbConnection.close();

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

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

    private boolean requestSQL() {

        Scanner sc = new Scanner(System.in);

        String db_ip = "";
        while (db_ip == null || db_ip.isEmpty()) {
            System.out.println(" 1º Introduce la dirección IP de MySQL:");
            db_ip = sc.nextLine();
        }

        String db_username = "";
        while (db_username == null || db_username.isEmpty()) {
            System.out.println(" 2º Introduce el usuario de MySQL:");
            db_username = sc.nextLine();
        }

        String db_password = "";
        while (db_password == null || db_password.isEmpty()) {
            System.out.println(" 3º Introduce la contraseña de MySQL:");
            db_password = sc.nextLine();
        }

        dbIP = db_ip;
        dbUsername = db_username;
        dbPassword = db_password;

        DBConnection dbConnection = new DBConnection();

        if(dbConnection.testConnection(db_ip, db_username, db_password)) {

            return true;

        } else {

            System.out.println(" Los credenciales de SQL introducidos no son válidos para una conexión, inténtelo de nuevo...");

            return false;

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
