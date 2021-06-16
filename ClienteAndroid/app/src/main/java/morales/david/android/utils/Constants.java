package morales.david.android.utils;

import morales.david.android.models.packets.PacketType;

public class Constants {

    public static String SERVER_IP = "localhost";
    public static int SERVER_PORT = 6565;

    public static final PacketType[] INIT_PACKETS = {
            PacketType.DAYS, PacketType.HOURS, PacketType.TIMEZONES, PacketType.CLASSROOMS, PacketType.TEACHERS,
            PacketType.CREDENTIALS, PacketType.COURSES, PacketType.GROUPS, PacketType.SUBJECTS,
    };

}
