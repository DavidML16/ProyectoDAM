package morales.david.server;

import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import morales.david.server.ftp.FileTransferManager;
import morales.david.server.utils.ConfigUtil;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.DBConstants;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private FileTransferManager fileTransferManager;

    private String dbIP, dbUsername, dbPassword, dbDatabase;

    public static final Gson GSON = new Gson();

    /**
     * Init managers, start file tranfer thread, and listen to new client socket connections
     * @throws IOException
     */
    private void init() throws IOException {

        if(!requestSetup())
            return;

        fileTransferManager = new FileTransferManager(this);
        fileTransferManager.setDaemon(true);
        fileTransferManager.start();

        File directory = new File("");
        File fileDir = new File(directory.getAbsolutePath() + File.separator + "files");
        if(!fileDir.exists())
            fileDir.mkdir();

        try {

            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

            bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());

            System.out.println(String.format(Constants.LOG_SERVER_INIT, Constants.SERVER_PORT));

            bootstrap.bind(Constants.SERVER_PORT).sync().channel().closeFuture().sync();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }

    }

    private boolean requestSetup() throws IOException {

        ConfigUtil configUtil = new ConfigUtil();

        Map<String, String> parameters = configUtil.getConfigParams();
        Constants.SETUP_FIRST_TIME = Boolean.parseBoolean(parameters.get("setup_first_time"));
        Constants.SERVER_PORT = Integer.parseInt(parameters.get("server_port"));
        Constants.SERVER_FILE_TRANSFER_PORT = Constants.SERVER_PORT + 1;

        DBConnection dbConnection = new DBConnection();

        if(!Constants.SETUP_FIRST_TIME) {

            DBConstants.DB_IP = parameters.get("db_ip");
            DBConstants.DB_PORT = Integer.parseInt(parameters.get("db_port"));
            DBConstants.DB_DATABASE = parameters.get("db_database");
            DBConstants.DB_USER = parameters.get("db_username");
            DBConstants.DB_PASS = parameters.get("db_password");

            if(!dbConnection.checkDatabaseExists(DBConstants.DB_IP, DBConstants.DB_USER, DBConstants.DB_PASS, DBConstants.DB_DATABASE)) {

                System.out.println("Se está creando la base de datos de nuevo ya que no existe...");

                if(!dbConnection.createDatabase(DBConstants.DB_IP, DBConstants.DB_USER, DBConstants.DB_PASS, DBConstants.DB_DATABASE)) {

                    System.out.println(" Se ha producido un error al crear la base de datos ");

                    return false;

                } else {

                    System.out.println(" \n(+) Se ha creado la base de datos correctamente, insertando tablas...");

                    dbConnection.open();

                    dbConnection.insertTables();

                    System.out.println(" \n(+) Se han creado las tablas, insertando usuario por defecto (admin, admin) ...");

                    dbConnection.insertUser("admin", "admin");

                    System.out.println("\n(+) Se ha creado el usuario inicial.");
                    System.out.println("(+) Ahora se iniciará el servidor...\n");

                    dbConnection.close();

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

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

            if(!dbConnection.createDatabase(dbIP, dbUsername, dbPassword, dbDatabase)) {

                System.out.println(" Se ha producido un error al crear la base de datos ");

                return false;

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

        return true;

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
