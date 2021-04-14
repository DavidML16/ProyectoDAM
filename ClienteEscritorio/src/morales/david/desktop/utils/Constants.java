package morales.david.desktop.utils;

public class Constants {

    public static final String WINDOW_TITLE = " (SGH)";

    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 6565;

    public static final String LOG_SERVER_ERROR_IO = "(X) Se ha producido un error con el flujo de E/S";
    public static final String LOG_SERVER_ERROR_IO_SEND = "(X) Se ha producido un error al enviar el mensaje por el flujo de salida";
    public static final String LOG_SERVER_ERROR_IO_READ = "(X) Se ha producido un error al leer el mensaje por el flujo de entrada";

    public static final String MESSAGES_ERROR_LOGIN = "Usuario o contraseña incorrecta";
    public static final String MESSAGES_ERROR_LOGIN_EMPTY = "Campo o campos vacíos";
    public static final String MESSAGES_ERROR_RECEIVEDFILE = "Se ha producido un error al enviar o recibir el archivo";

    public static final String MESSAGES_CONFIRMATION_RECEIVEDFILE = "El archivo se ha recibido correctamente\nIniciando la importación...";



    // REQUEST MESSAGES
    public static final String REQUEST_LOGIN = "R1-LOGIN";
    public static final String REQUEST_DISCONNECT = "R2-DISCONNECT";

    public static final String REQUEST_SENDACCESSFILE = "R3-SENDACCESSFILE";

    public static final String REQUEST_TEACHERS = "R4-TEACHERS";
    public static final String REQUEST_ADDTEACHER = "R5-ADDTEACHER";
    public static final String REQUEST_UPDATETEACHER = "R6-UPDATETEACHER";
    public static final String REQUEST_REMOVETEACHER = "R7-REMOVETEACHER";

    public static final String REQUEST_CLASSROOMS = "R8-CLASSROOMS";
    public static final String REQUEST_ADDCLASSROOM = "R9-ADDCLASSROOM";
    public static final String REQUEST_UPDATECLASSROOM = "R10-UPDATECLASSROOM";
    public static final String REQUEST_REMOVECLASSROOM = "R11-REMOVECLASSROOM";

    public static final String REQUEST_COURSES = "R12-COURSES";
    public static final String REQUEST_ADDCOURSE = "R13-ADDCOURSE";
    public static final String REQUEST_UPDATECOURSE = "R14-UPDATECOURSE";
    public static final String REQUEST_REMOVECOURSE = "R15-REMOVECOURSE";

    public static final String REQUEST_SUBJECTS = "R16-SUBJECTS";
    public static final String REQUEST_ADDSUBJECT = "R17-ADDSUBJECT";
    public static final String REQUEST_UPDATESUBJECT = "R18-UPDATESUBJECT";
    public static final String REQUEST_REMOVESUBJECT = "R19-REMOVESUBJECT";



    // CONFIRMATION MESSAGES
    public static final String CONFIRMATION_LOGIN = "CF1-LOGIN";
    public static final String CONFIRMATION_DISCONNECT = "CF2-DISCONNECT";

    public static final String CONFIRMATION_SENDACCESSFILE = "CF3-SENDACCESSFILE";

    public static final String CONFIRMATION_TEACHERS = "CF4-TEACHERS";
    public static final String CONFIRMATION_ADDTEACHER = "CF5-ADDTEACHER";
    public static final String CONFIRMATION_UPDATETEACHER = "CF6-UPDATETEACHER";
    public static final String CONFIRMATION_REMOVETEACHER = "CF7-REMOVETEACHER";

    public static final String CONFIRMATION_CLASSROOMS = "CF8-CLASSROOMS";
    public static final String CONFIRMATION_ADDCLASSROOM = "CF9-ADDCLASSROOM";
    public static final String CONFIRMATION_UPDATECLASSROOM = "CF10-UPDATECLASSROOM";
    public static final String CONFIRMATION_REMOVECLASSROOM = "CF11-REMOVECLASSROOM";

    public static final String CONFIRMATION_COURSES = "CF12-COURSES";
    public static final String CONFIRMATION_ADDCOURSE = "CF13-ADDCOURSE";
    public static final String CONFIRMATION_UPDATECOURSE = "CF14-UPDATECOURSE";
    public static final String CONFIRMATION_REMOVECOURSE = "CF15-REMOVECOURSE";

    public static final String CONFIRMATION_SUBJECTS = "CF16-SUBJECTS";
    public static final String CONFIRMATION_ADDSUBJECT = "CF17-ADDSUBJECT";
    public static final String CONFIRMATION_UPDATESUBJECT = "CF18-UPDATESUBJECT";
    public static final String CONFIRMATION_REMOVESUBJECT = "CF19-REMOVESUBJECT";



    // ERROR MESSAGES
    public static final String ERROR_LOGIN = "E1-LOGIN";
    public static final String ERROR_DISCONNECT = "E2-DISCONNECT";

    public static final String ERROR_SENDACCESSFILE = "E3-SENDACCESSFILE";

    public static final String ERROR_TEACHERS = "E4-TEACHERS";
    public static final String ERROR_ADDTEACHER = "E5-ADDTEACHER";
    public static final String ERROR_UPDATETEACHER = "E6-UPDATETEACHER" ;
    public static final String ERROR_REMOVETEACHER = "E7-REMOVETEACHER";

    public static final String ERROR_CLASSROOMS = "E8-CLASSROOMS";
    public static final String ERROR_ADDCLASSROOM = "E9-ADDCLASSROOM";
    public static final String ERROR_UPDATECLASSROOM = "E10-UPDATECLASSROOM";
    public static final String ERROR_REMOVECLASSROOM = "E11-REMOVECLASSROOM";

    public static final String ERROR_COURSES = "E12-COURSES";
    public static final String ERROR_ADDCOURSE = "E13-ADDCOURSE";
    public static final String ERROR_UPDATECOURSE = "E14-UPDATECOURSE";
    public static final String ERROR_REMOVECOURSE = "E15-REMOVECOURSE";

    public static final String ERROR_SUBJECTS = "E16-SUBJECTS";
    public static final String ERROR_ADDSUBJECT = "E17-ADDSUBJECT";
    public static final String ERROR_UPDATESUBJECT = "E18-UPDATESUBJECT";
    public static final String ERROR_REMOVESUBJECT = "E19-REMOVESUBJECT";



}
