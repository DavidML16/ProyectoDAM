package morales.david.desktop.models.packets;

public enum PacketType {

    PING, LOGIN, DISCONNECT, EXIT,

    SENDACCESSFILE, IMPORTSTATUS,

    CREDENTIALS, ADDCREDENTIAL, UPDATECREDENTIAL, REMOVECREDENTIAL,

    TEACHERS, ADDTEACHER, UPDATETEACHER, REMOVETEACHER,

    CLASSROOMS, ADDCLASSROOM, UPDATECLASSROOM, REMOVECLASSROOM,

    COURSES, ADDCOURSE, UPDATECOURSE, REMOVECOURSE,

    SUBJECTS, ADDSUBJECT, UPDATESUBJECT, REMOVESUBJECT,

    GROUPS, ADDGROUP, UPDATEGROUP, REMOVEGROUP,

    DAYS, UPDATEDAY,

    HOURS, UPDATEHOUR,

    TIMEZONES,

    SCHEDULES;

    public String getRequest() {
        return "REQUEST_" + toString();
    }

    public String getConfirmation() {
        return "CONFIRMATION_" + toString();
    }

    public String getError() {
        return "ERROR_" + toString();
    }

    public static String getIdentifier(String formated) {
        String[] parts = formated.split("_");
        return parts[1];
    }

}
