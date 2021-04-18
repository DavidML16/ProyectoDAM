package morales.david.server.models.packets;

public enum PacketType {

    LOGIN, DISCONNECT,

    SENDACCESSFILE,

    CREDENTIALS, ADDCREDENTIAL, UPDATECREDENTIAL, REMOVECREDENTIAL,

    TEACHERS, ADDTEACHER, UPDATETEACHER, REMOVETEACHER,

    CLASSROOMS, ADDCLASSROOM, UPDATECLASSROOM, REMOVECLASSROOM,

    COURSES, ADDCOURSE, UPDATECOURSE, REMOVECOURSE,

    SUBJECTS, ADDSUBJECT, UPDATESUBJECT, REMOVESUBJECT;

    public String getRequest() {
        return "REQUEST_" + ordinal() + "_" + toString();
    }

    public String getConfirmation() {
        return "CONFIRMATION_" + ordinal() + "_" + toString();
    }

    public String getError() {
        return "ERROR_" + ordinal() + "_" + toString();
    }

    public static String getIdentifier(String formated) {
        String[] parts = formated.split("_");
        return parts[2];
    }

}
