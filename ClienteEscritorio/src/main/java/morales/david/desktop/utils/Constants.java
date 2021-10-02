package morales.david.desktop.utils;

import morales.david.desktop.models.packets.PacketType;

public class Constants {

    public static final String WINDOW_TITLE = " (SGH)";

    public static String SERVER_IP = "localhost";
    public static int SERVER_PORT = 6565;
    public static int SERVER_FILE_TRANSFER_PORT = SERVER_PORT + 1;


    public static final String MESSAGES_ERROR_LOGIN = "Usuario o contraseña incorrecta";
    public static final String MESSAGES_ERROR_LOGIN_EMPTY = "Campo o campos vacíos";
    public static final String MESSAGES_ERROR_SERVER_NO_CONNECTION = "No se a podido conectar con el servidor";

    public static boolean FIRST_HOME_VIEW = true;

    public static final double GAP_SIZE = 5;

    public static final PacketType[] INIT_PACKETS = {
        PacketType.DAYS, PacketType.HOURS, PacketType.TIMEZONES, PacketType.CLASSROOMS, PacketType.TEACHERS,
        PacketType.CREDENTIALS, PacketType.COURSES, PacketType.GROUPS, PacketType.SUBJECTS, PacketType.SCHEDULES
    };

}
