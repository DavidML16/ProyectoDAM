package morales.david.android.utils;

import morales.david.android.models.packets.PacketType;

public class Constants {

    public static final String SERVER_IP = "192.168.1.46";
    public static final int SERVER_PORT = 6565;

    public static final PacketType[] INIT_PACKETS = {
            PacketType.CLASSROOMS, PacketType.TEACHERS, PacketType.CREDENTIALS, PacketType.COURSES,
            PacketType.SUBJECTS, PacketType.DAYS, PacketType.HOURS
    };

}
