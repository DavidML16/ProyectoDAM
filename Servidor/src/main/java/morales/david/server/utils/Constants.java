package morales.david.server.utils;

public class Constants {

    public static boolean SETUP_FIRST_TIME = false;

    public static int SERVER_PORT = 6565;
    public static int SERVER_FILE_TRANSFER_PORT = SERVER_PORT + 1;

    public static long CLIENT_CONNECTION_CHECKING_INTERVAL = 5000;

    public static final String LOG_SERVER_INIT = "(+) Servidor iniciado en el puerto '%d'";
    public static final String LOG_SERVER_ERROR = "(X) Se ha producido un error con el servidor";
    public static final String LOG_SERVER_ERROR_IO = "(X) Se ha producido un error con el flujo de E/S";
    public static final String LOG_SERVER_ERROR_IO_SEND = "(X) Se ha producido un error al enviar el mensaje por el flujo de salida";
    public static final String LOG_SERVER_ERROR_IO_READ = "(X) Se ha producido un error al leer el mensaje por el flujo de entrada";
    public static final String LOG_SERVER_USER_CONNECTED = "(+) Nuevo usuario conectado";
    public static final String LOG_SERVER_USER_DISCONNECTED = "(+) Se ha desconectado un usuario";

}
