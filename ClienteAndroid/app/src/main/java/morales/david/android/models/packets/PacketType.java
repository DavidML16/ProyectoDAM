package morales.david.android.models.packets;

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

    SCHEDULES, SEARCHSCHEDULE, INSERTSCHEDULEITEM, SWITCHSCHEDULEITEM, REMOVESCHEDULEITEM, ADDSCHEDULE, DELETESCHEDULE, UPDATESCHEDULE, EMPTYCLASSROOMSDAY, EMPTYCLASSROOMSTIMEZONE;

    /**
     * Get request type string of the packet
     * @return request string
     */
    public String getRequest() {
        return "REQUEST_" + toString();
    }

    /**
     * Get confirmation type string of the packet
     * @return confirmation string
     */
    public String getConfirmation() {
        return "CONFIRMATION_" + toString();
    }

    /**
     * Get error type string of the packet
     * @return error string
     */
    public String getError() {
        return "ERROR_" + toString();
    }

    /**
     * Split and get de identifier of the packet type
     * @param formated
     * @return identifier
     */
    public static String getIdentifier(String formated) {
        String[] parts = formated.split("_");
        return parts[1];
    }

}
