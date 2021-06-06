package morales.david.server.utils;

public class DBConstants {


    public static final String DB_URL = "jdbc:mysql://localhost/db_proyecto";
    public static final String DB_USER = "david";
    public static final String DB_PASS = "161100";


    public static final String DB_QUERY_CREDENTIAL = "SELECT id_credencial FROM credencial WHERE usuario = ? AND passwd_hash = ?";
    public static final String DB_QUERY_CREDENTIALS = "SELECT * FROM credencial";
    public static final String DB_QUERY_CREDENTIAL_BY_ID = "SELECT * FROM credencial WHERE `id_credencial` = ?";
    public static final String DB_QUERY_ADDCREDENTIAL = "INSERT INTO credencial (`usuario`, `passwd_hash`, profesor, rol) VALUES (?, ?, ?, ?)";
    public static final String DB_QUERY_UPDATECREDENTIAL = "UPDATE credencial SET `usuario` = ?, `passwd_hash` = ?, `profesor` = ?, `rol` = ? WHERE `id_credencial` = ?";
    public static final String DB_QUERY_REMOVECREDENTIAL = "DELETE FROM credencial WHERE `id_credencial` = ?";

    public static final String DB_QUERY_DETAILS = "SELECT * FROM credencial, profesor WHERE usuario = ? AND profesor = id_profesor";

    public static final String DB_QUERY_TEACHERS = "SELECT * FROM profesor";
    public static final String DB_QUERY_TEACHER_BY_ID = "SELECT * FROM profesor WHERE `id_profesor` = ?";
    public static final String DB_QUERY_ADDTEACHER = "INSERT INTO profesor (`numero`, `nombre`, `abreviacion`, `minhorasdia`, `maxhorasdia`, `departamento`) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String DB_QUERY_UPDATETEACHER = "UPDATE profesor SET `numero` = ?, `nombre` = ?, `abreviacion` = ?, `minhorasdia` = ?, `maxhorasdia` = ?, `departamento` = ? WHERE `id_profesor` = ?";
    public static final String DB_QUERY_REMOVETEACHER = "DELETE FROM profesor WHERE `id_profesor` = ?";

    public static final String DB_QUERY_CLASSROOMS = "SELECT * FROM aula";
    public static final String DB_QUERY_CLASSROOM_BY_ID = "SELECT * FROM aula WHERE `id_aula` = ?";
    public static final String DB_QUERY_ADDCLASSROOM = "INSERT INTO aula (`nombre`) VALUES (?)";
    public static final String DB_QUERY_UPDATECLASSROOM = "UPDATE aula SET `nombre` = ? WHERE `id_aula` = ?";
    public static final String DB_QUERY_REMOVECLASSROOM = "DELETE FROM aula WHERE `id_aula` = ?";

    public static final String DB_QUERY_COURSES = "SELECT * FROM curso";
    public static final String DB_QUERY_COURSE_BY_ID = "SELECT * FROM curso WHERE `id_curso` = ?";
    public static final String DB_QUERY_ADDCOURSE = "INSERT INTO curso (`nivel`, `nombre`) VALUES (?, ?)";
    public static final String DB_QUERY_UPDATECOURSE = "UPDATE curso SET `nivel` = ?, `nombre` = ? WHERE `id_curso` = ?";
    public static final String DB_QUERY_REMOVECOURSE = "DELETE FROM curso WHERE `id_curso` = ?";

    public static final String DB_QUERY_GROUPS = "SELECT * FROM grupo";
    public static final String DB_QUERY_GROUP_BY_ID = "SELECT * FROM grupo WHERE `id_grupo` = ?";
    public static final String DB_QUERY_ADDGROUP = "INSERT INTO grupo (`curso`, `letra`) VALUES (?, ?)";
    public static final String DB_QUERY_UPDATEGROUP = "UPDATE grupo SET `curso` = ?, `letra` = ? WHERE `id_grupo` = ?";
    public static final String DB_QUERY_REMOVEGROUP = "DELETE FROM grupo WHERE `id_grupo` = ?";

    public static final String DB_QUERY_SUBJECTS = "SELECT * FROM asignatura";
    public static final String DB_QUERY_SUBJECT_BY_ID = "SELECT * FROM asignatura WHERE `id_asignatura` = ?";
    public static final String DB_QUERY_ADDSUBJECT = "INSERT INTO asignatura (`numero`, `abreviacion`, `nombre`, `color`) VALUES (?, ?, ?, ?)";
    public static final String DB_QUERY_ADDSUBJECT_RELATION = "INSERT INTO curso_asignatura (`curso`, `asignatura`) VALUES (?, ?)";
    public static final String DB_QUERY_UPDATESUBJECT = "UPDATE asignatura SET `numero` = ?, `abreviacion` = ?, `nombre` = ? WHERE `id_asignatura` = ?";
    public static final String DB_QUERY_REMOVESUBJECT = "DELETE FROM asignatura WHERE `id_asignatura` = ?";
    public static final String DB_QUERY_REMOVESUBJECT_RELATION = "DELETE FROM curso_asignatura WHERE `asignatura` = ?";
    public static final String DB_QUERY_SUBJECTS_COURSES = "SELECT * FROM curso WHERE id_curso IN ( SELECT curso FROM curso_asignatura WHERE asignatura = ? )";

    public static final String DB_QUERY_DAYS = "SELECT * FROM numero_dia";
    public static final String DB_QUERY_ADDDAY = "INSERT INTO numero_dia VALUES (?, ?)";
    public static final String DB_QUERY_UPDATEDAY = "UPDATE numero_dia SET `dia` = ? WHERE `id_dia` = ?";

    public static final String DB_QUERY_HOURS = "SELECT * FROM numero_hora";
    public static final String DB_QUERY_ADDHOUR = "INSERT INTO numero_hora VALUES (?, ?)";
    public static final String DB_QUERY_UPDATEHOUR = "UPDATE numero_hora SET `horas` = ? WHERE `id_hora` = ?";

    public static final String DB_QUERY_TIMEZONES = "SELECT TZ.id_franja idTimeZone, DN.id_dia idDay, DN.dia dayString, HN.id_hora idHour, HN.horas hourString " +
            "FROM franja_horaria TZ, numero_dia DN, numero_hora HN WHERE TZ.dia = DN.id_dia AND TZ.hora = HN.id_hora";
    public static final String DB_QUERY_TIMEZONE_BY_ID = "SELECT TZ.id_franja idTimeZone, DN.id_dia idDay, DN.dia dayString, HN.id_hora idHour, HN.horas hourString " +
            "FROM franja_horaria TZ, numero_dia DN, numero_hora HN WHERE TZ.dia = DN.id_dia AND TZ.hora = HN.id_hora AND TZ.id_franja = ?";

    public static final String DB_QUERY_SCHEDULES = "SELECT COUNT(*) amount FROM imparte";
    public static final String DB_QUERY_SEARCH_SCHEDULE = "SELECT * FROM imparte WHERE ";
    public static final String DB_QUERY_REMOVESCHEDULE = "DELETE FROM imparte WHERE `uuid` = ?";
    public static final String DB_QUERY_REMOVESCHEDULE_SB = "DELETE FROM imparte WHERE `uuid` IN ";
    public static final String DB_QUERY_UPDATESCHEDULE = "UPDATE imparte SET `profesor` = ?, `asignatura` = ?, `grupo` = ?, `aula` = ?, `franja` = ? WHERE uuid = ?";
    public static final String DB_QUERY_INSERTSCHEDULE = "INSERT INTO imparte VALUES (?,?,?,?,?,?)";

    public static final String DB_QUERY_CLEAR_DAYS = "DELETE FROM numero_dia";
    public static final String DB_QUERY_CLEAR_HOURS = "DELETE FROM numero_hora";
    public static final String DB_QUERY_CLEAR_TIMEZONE = "DELETE FROM franja_horaria";
    public static final String DB_QUERY_CLEAR_SUBJECTS = "DELETE FROM asignatura";
    public static final String DB_QUERY_CLEAR_CLASSROOMS = "DELETE FROM aula";
    public static final String DB_QUERY_CLEAR_COURSES = "DELETE FROM curso";
    public static final String DB_QUERY_CLEAR_GROUPS = "DELETE FROM grupo";
    public static final String DB_QUERY_CLEAR_COURSE_SUBJECTS = "DELETE FROM curso_asignatura";
    public static final String DB_QUERY_CLEAR_TEACHERS = "DELETE FROM profesor";
    public static final String DB_QUERY_CLEAR_SCHEDULES = "DELETE FROM imparte";

    public static final String DB_QUERY_INSERTSB_DAYS = "INSERT INTO numero_dia VALUES ";
    public static final String DB_QUERY_INSERTSB_HOURS = "INSERT INTO numero_hora VALUES ";
    public static final String DB_QUERY_INSERTSB_TIMEZONE = "INSERT INTO franja_horaria VALUES ";
    public static final String DB_QUERY_INSERTSB_SUBJECTS = "INSERT INTO asignatura VALUES ";
    public static final String DB_QUERY_INSERTSB_CLASSROOMS = "INSERT INTO aula VALUES ";
    public static final String DB_QUERY_INSERTSB_COURSES = "INSERT INTO curso VALUES ";
    public static final String DB_QUERY_INSERTSB_GROUPS = "INSERT INTO grupo VALUES ";
    public static final String DB_QUERY_INSERTSB_COURSE_SUBJECTS = "INSERT IGNORE INTO curso_asignatura VALUES ";
    public static final String DB_QUERY_INSERTSB_TEACHERS = "INSERT IGNORE INTO profesor VALUES ";
    public static final String DB_QUERY_INSERTSB_SCHEDULES = "INSERT IGNORE INTO imparte VALUES ";


    //public static final String[] SCHEDULE_TABLES = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};
    public static final String[] SCHEDULE_TABLES = {"solucion total"};


}
