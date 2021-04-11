package morales.david.server.utils;

public class DBConstants {


    public static final String DB_URL = "jdbc:mysql://localhost/db_proyecto";
    public static final String DB_USER = "david";
    public static final String DB_PASS = "161100";


    public static final String DB_QUERY_CREDENTIAL = "SELECT id_credencial FROM credencial WHERE usuario = ? AND passwd_hash = ?";
    public static final String DB_QUERY_DETAILS = "SELECT * FROM credencial, profesor WHERE usuario = ? AND profesor = id_profesor";

    public static final String DB_QUERY_TEACHERS = "SELECT * FROM profesor";
    public static final String DB_QUERY_ADDTEACHER = "INSERT INTO profesor (`numero`, `nombre`, `abreviacion`, `minhorasdia`, `maxhorasdia`, `departamento`) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String DB_QUERY_UPDATETEACHER = "UPDATE profesor SET `numero` = ?, `nombre` = ?, `abreviacion` = ?, `minhorasdia` = ?, `maxhorasdia` = ?, `departamento` = ? WHERE `id_profesor` = ?";
    public static final String DB_QUERY_REMOVETEACHER = "DELETE FROM profesor WHERE `id_profesor` = ?";

    public static final String DB_QUERY_CLASSROOMS = "SELECT * FROM aula";
    public static final String DB_QUERY_ADDCLASSROOM = "INSERT INTO aula (`nombre`, `planta`) VALUES (?, ?)";
    public static final String DB_QUERY_UPDATECLASSROOM = "UPDATE aula SET `nombre` = ?, `planta` = ? WHERE `id_aula` = ?";
    public static final String DB_QUERY_REMOVECLASSROOM = "DELETE FROM aula WHERE `id_aula` = ?";

    public static final String DB_QUERY_COURSES = "SELECT * FROM curso";
    public static final String DB_QUERY_ADDCOURSE = "INSERT INTO curso (`nivel`, `nombre`) VALUES (?, ?)";
    public static final String DB_QUERY_UPDATECOURSE = "UPDATE curso SET `nivel` = ?, `nombre` = ? WHERE `id_curso` = ?";
    public static final String DB_QUERY_REMOVECOURSE = "DELETE FROM curso WHERE `id_curso` = ?";


}
