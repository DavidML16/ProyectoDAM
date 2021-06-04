package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.models.*;
import morales.david.server.models.TimeZone;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.ColorUtil;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.DBConstants;

import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.List;

public class ImportManager {

    private File file;

    private Server server;

    private DBConnection dbConnection;
    private Connection acon;

    private List<Day> dayList;
    private List<Hour> hourList;
    private List<TimeZone> timeZoneList;
    private List<Teacher> teacherList;
    private List<Subject> subjectList;
    private List<Classroom> classroomList;
    private List<Course> courseList;
    private List<CourseSubject> courseSubjectList;
    private List<Group> groupList;
    private List<Schedule> scheduleList;

    private boolean isImporting;

    /**
     * Create a new instance of ImportManager with server as parameter
     * @param server
     */
    public ImportManager(Server server) {
        this.server = server;
        this.isImporting = false;
        this.dbConnection = new DBConnection();
    }

    /**
     * Get if database is in import process or not
     * @return isImporting
     */
    public synchronized boolean isImporting() {
        return isImporting;
    }

    /**
     * Get the file to import
     * @return file
     */
    public synchronized File getFile() {
        return file;
    }

    /**
     * Set the received file to import
     * @param file
     */
    public synchronized void setFile(File file) {
        this.file = file;
    }

    /**
     * Start the proccess of importing the database from Access to MySQL
     * 1º Clear all rows of the tables from MySQL database
     * 2º Get days list and insert it
     * 3º Get hours list and insert it
     * 4º Get timeZones list and insert it
     * 5º Get teachers list and insert it
     * 6º Get subjects list and insert it
     * 7º Get classrooms list and insert it
     * 8º Get courses list and insert it
     * 9º Get the courses association of the subjects and insert it
     * 10º Get groups list and insert it
     * 11º Get the schedules list and insert it
     * 12º Send the confirmation or error if database has been imported correctly
     */
    public void importDatabase() {

        isImporting = true;

        Packet statusPacket = new PacketBuilder()
                .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                .addArgument("importing", isImporting)
                .addArgument("type", "init")
                .addArgument("message", "Archivo recibido, iniciado la importación")
                .build();
        server.getClientRepository().broadcast(statusPacket);

        try {

            acon = DriverManager.getConnection("jdbc:ucanaccess://" + file.getAbsolutePath());

            dbConnection.open();

            if(dbConnection.clearAll()) {

                statusPacket = new PacketBuilder()
                        .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                        .addArgument("importing", isImporting)
                        .addArgument("type", "clear")
                        .addArgument("message", "Se ha limpiado la base de datos correctamente")
                        .build();
                server.getClientRepository().broadcast(statusPacket);

                // DAYS
                dayList = getDays();
                {

                    insertDays();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de días importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // HOURS
                hourList = getHours();
                {

                    insertHours();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de horas importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // TIMEZONES
                timeZoneList = getTimeZones();
                {

                    insertTimeZone();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de franjas horarias importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // TEACHERS
                teacherList = getTeachers();
                {

                    insertTeachers();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de profesores importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // SUBJECTS
                subjectList = getSubjects();
                {

                    insertSubjects();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de asignaturas importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // CLASSROOMS
                classroomList = getClassrooms();
                {

                    insertClassrooms();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de aulas importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // COURSES
                courseList = getCourses();
                {

                    insertCourses();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de cursos importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // COURSE SUBJECTS
                courseSubjectList = getCourseSubjects();
                {

                    insertCourseSubjects();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de cursos-asignaturas importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // GROUPS
                groupList = getGroups();
                {

                    insertGroups();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de grupos importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

                // SCHEDULES
                scheduleList = getSchedules();
                {

                    insertSchedules();

                    statusPacket = new PacketBuilder()
                            .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                            .addArgument("importing", isImporting)
                            .addArgument("type", "import")
                            .addArgument("message", "Tabla de horarios importada")
                            .build();
                    server.getClientRepository().broadcast(statusPacket);

                }

            }

            dbConnection.close();

            acon.close();

        } catch (SQLException throwables) {

            statusPacket = new PacketBuilder()
                    .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                    .addArgument("importing", isImporting)
                    .addArgument("type", "error")
                    .addArgument("message", throwables.getMessage())
                    .build();
            server.getClientRepository().broadcast(statusPacket);

        } finally {

            isImporting = false;

            if(file.exists())
                file.delete();

            File directory = new File("files/");
            deleteDirectory(directory, directory);

            statusPacket = new PacketBuilder()
                    .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                    .addArgument("importing", isImporting)
                    .addArgument("type", "end")
                    .addArgument("message", "Importación finalizada!")
                    .build();
            server.getClientRepository().broadcast(statusPacket);

        }

    }

    /**
     * Get list of days from access db
     * @return list of days
     */
    public List<Day> getDays() {

        List<Day> dayList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT dia FROM `numero-dia`");

            rs = stm.executeQuery();

            int i = 1;

            while (rs.next()) {

                String name = rs.getString("dia");
                dayList.add(new Day(i, name));
                i++;

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return dayList;

    }

    /**
     * Insert the list of days to mysql db
     */
    public void insertDays() {

        StringBuilder insertString = new StringBuilder();
        for(Day day : dayList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(day.getId())
                .append(",'")
                .append(day.getName())
                .append("')");

        }

        dbConnection.insertDaysSB(insertString);

    }

    /**
     * Get list of hours from access db
     * @return list of hours
     */
    public List<Hour> getHours() {

        List<Hour> hourList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT * FROM `numero-hora`");

            rs = stm.executeQuery();

            int i = 1;

            List<Hour> tempHour = new ArrayList<>();

            while (rs.next()) {

                String numhora = Double.toString(rs.getDouble("numhora"));
                String name = rs.getString("hora");

                if(numhora.contains(".0")) {
                    hourList.add(new Hour(i, name));
                    i++;
                } else {
                    tempHour.add(new Hour(i, name));
                }

            }

            for(Hour hour : tempHour) {
                hour.setId(i);
                hourList.add(hour);
                i++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return hourList;

    }

    /**
     * Insert the list of hours to mysql db
     */
    public void insertHours() {

        StringBuilder insertString = new StringBuilder();
        for(Hour hour : hourList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(hour.getId())
                .append(",'")
                .append(hour.getName())
                .append("')");

        }

        dbConnection.insertHoursSB(insertString);

    }

    /**
     * Get list of timezones from the union of days and hours
     * @return list of timezones
     */
    public List<TimeZone> getTimeZones() {

        List<TimeZone> timeZoneList = new ArrayList<>();

        int i = 1;

        for(Day day : dayList) {
            for(Hour hour : hourList) {
                timeZoneList.add(new TimeZone(i, day, hour));
                i++;
            }
        }

        return timeZoneList;

    }

    /**
     * Insert the list of timezones to mysql db
     */
    private void insertTimeZone() {

        StringBuilder insertString = new StringBuilder();
        for(TimeZone timeZone : timeZoneList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString
                    .append(timeZone.getId())
                    .append(",")
                    .append(timeZone.getDay().getId())
                    .append(",")
                    .append(timeZone.getHour().getId())
                    .append(")");

        }

        dbConnection.insertTimezoneSB(insertString);

    }

    /**
     * Get list of subjects from access db
     * @return list of subjects
     */
    private List<Subject> getSubjects() {

        List<Subject> subjectList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT ID, N, ABREV, NOMBRE FROM `NomAsg`");

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("ID");
                int number = rs.getInt("N");
                String abreviation = rs.getString("ABREV");
                String name = rs.getString("NOMBRE");
                subjectList.add(new Subject(id, number, abreviation, name, ColorUtil.getColor(), new ArrayList<>()));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return subjectList;

    }

    /**
     * Insert the list of subjects to mysql db
     */
    private void insertSubjects() {

        StringBuilder insertString = new StringBuilder();
        for(Subject subject : subjectList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(subject.getId())
                .append(",")
                .append(subject.getNumber())
                .append(",'")
                .append(subject.getAbreviation())
                .append("','")
                .append(subject.getName())
                .append("','")
                .append(ColorUtil.getColor())
                .append("')");

        }

        dbConnection.insertSubjectsSB(insertString);

    }

    /**
     * Get list of teachers from access db
     * @return list of teachers
     */
    private List<Teacher> getTeachers() {

        List<Teacher> teacherList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            stm = acon.prepareStatement("SELECT * FROM `Prof`");

            rs = stm.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("ID");
                int number = rs.getInt("N");
                String name = rs.getString("NOMBRE");
                String abreviation = rs.getString("ABREV");
                int mindayhours = rs.getInt("MINHDIA");
                int maxdayhours = rs.getInt("MAXHDIA");
                String department = rs.getString("DEPART");
                teacherList.add(new Teacher(id, number, name, abreviation, mindayhours, maxdayhours, department));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return teacherList;

    }

    /**
     * Insert the list of teachers to mysql db
     */
    private void insertTeachers() {

        StringBuilder insertString = new StringBuilder();
        for(Teacher teacher : teacherList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(teacher.getId())
                .append(",")
                .append(teacher.getNumber())
                .append(",'")
                .append(teacher.getName())
                .append("','")
                .append(teacher.getAbreviation())
                .append("',")
                .append(teacher.getMinDayHours())
                .append(",")
                .append(teacher.getMaxDayHours())
                .append(",'")
                .append(teacher.getDepartment())
                .append("')");

        }

        dbConnection.insertTeachersSB(insertString);

    }

    /**
     * Get list of classrooms from access db
     * @return list of classrooms
     */
    private List<Classroom> getClassrooms() {

        List<Classroom> classroomList = new ArrayList<>();
        Set<String> classroomsSet = new HashSet<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            for(String table : DBConstants.SCHEDULE_TABLES) {

                stm = acon.prepareStatement("SELECT AULA FROM `" + table + "` WHERE AULA IS NOT NULL GROUP BY AULA");
                rs = stm.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("AULA");
                    classroomsSet.add(name.trim().replace(" ", ""));
                }

                rs.close();
                stm.close();

            }


            int i = 1;

            for(String classroomName : classroomsSet) {
                classroomList.add(new Classroom(i, classroomName));
                i++;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return classroomList;

    }

    /**
     * Insert the list of classrooms to mysql db
     */
    private void insertClassrooms() {

        StringBuilder insertString = new StringBuilder();
        for(Classroom classroom : classroomList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(classroom.getId())
                    .append(",'")
                    .append(classroom.getName())
                    .append("')");

        }

        dbConnection.insertClassroomsSB(insertString);

    }

    /**
     * Get list of courses from access db
     * @return list of courses
     */
    private List<Course> getCourses() {

        List<Course> courseList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            int i = 1;

            for(String table : DBConstants.SCHEDULE_TABLES) {

                stm = acon.prepareStatement("SELECT CURSO, NIVEL FROM `" + table + "` WHERE CURSO IS NOT NULL AND NIVEL IS NOT NULL GROUP BY CURSO, NIVEL");
                rs = stm.executeQuery();

                while (rs.next()) {
                    String level = rs.getString("CURSO");
                    String name = rs.getString("NIVEL");
                    courseList.add(new Course(i, level, name));
                    i++;
                }

                rs.close();
                stm.close();

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return courseList;

    }

    /**
     * Insert the list of courses to mysql db
     */
    private void insertCourses() {

        StringBuilder insertString = new StringBuilder();
        for(Course course : courseList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(course.getId())
                    .append(",'")
                    .append(course.getLevel())
                    .append("','")
                    .append(course.getName())
                    .append("')");

        }

        dbConnection.insertCoursesSB(insertString);

    }

    /**
     * Get list of courseSubjects from access db
     * @return list of courseSubjects
     */
    private List<CourseSubject> getCourseSubjects() {

        List<CourseSubject> courseSubjectList = new ArrayList<>();

        List<Course> courseList = dbConnection.getCourses();
        List<Subject> subjectList = dbConnection.getSubjects();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            for(String table : DBConstants.SCHEDULE_TABLES) {

                for(Subject subject : subjectList) {

                    stm = acon.prepareStatement("SELECT CURSO, NIVEL FROM `" + table + "` WHERE ASIG = '" + subject.getAbreviation() + "' OR ASIG = '" + subject.getNumber() + "'");
                    rs = stm.executeQuery();

                    while (rs.next()) {

                        String level = rs.getString("CURSO");
                        String name = rs.getString("NIVEL");

                        Course course = getCourseBy(courseList, level, name);

                        if(course != null)
                            courseSubjectList.add(new CourseSubject(course, subject));

                    }

                    rs.close();
                    stm.close();

                }

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return courseSubjectList;

    }

    /**
     * Insert the list of courseSubjects to mysql db
     */
    private void insertCourseSubjects() {

        StringBuilder insertString = new StringBuilder();
        for(CourseSubject courseSubject : courseSubjectList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString.append(courseSubject.getCourse().getId())
                    .append(",")
                    .append(courseSubject.getSubject().getId())
                    .append(")");

        }

        dbConnection.insertCourseSubjectsSB(insertString);

    }

    /**
     * Get list of groups from access db
     * @return list of groups
     */
    private List<Group> getGroups() {

        List<Group> groupList = new ArrayList<>();

        List<Course> courseList = dbConnection.getCourses();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            int i = 1;

            for(String table : DBConstants.SCHEDULE_TABLES) {

                stm = acon.prepareStatement("SELECT DISTINCT GRUPO, CURSO, NIVEL FROM `" + table + "` WHERE CURSO IS NOT NULL AND NIVEL IS NOT NULL AND GRUPO IS NOT NULL");
                rs = stm.executeQuery();

                while (rs.next()) {

                    String level = rs.getString("CURSO");
                    String name = rs.getString("NIVEL");
                    String letter = rs.getString("GRUPO");

                    Course course = getCourseBy(courseList, level, name);

                    if(course != null)
                        groupList.add(new Group(i, course, letter));

                    i++;

                }

                rs.close();
                stm.close();

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return groupList;

    }

    /**
     * Insert the list of groups to mysql db
     */
    private void insertGroups() {

        StringBuilder insertString = new StringBuilder();
        for(Group group : groupList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString
                    .append(group.getId())
                    .append(",")
                    .append(group.getCourse().getId())
                    .append(",'")
                    .append(group.getLetter())
                    .append("')");

        }

        dbConnection.insertGroupsSB(insertString);

    }

    /**
     * Get list of schedules from access db
     * @return list of schedules
     */
    private List<Schedule> getSchedules() {

        List<Schedule> scheduleList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        List<Subject> asignaturas = dbConnection.getSubjects();
        List<Teacher> profesores = dbConnection.getTeachers();
        List<Group> grupos = dbConnection.getGroups();
        List<Classroom> aulas = dbConnection.getClassrooms();
        List<TimeZone> zonashorarias = dbConnection.getTimeZones();

        try {

            for(String table : DBConstants.SCHEDULE_TABLES) {

                stm = acon.prepareStatement("SELECT ASIG, PROF, CURSO, NIVEL, GRUPO, AULA, DIA, HORA FROM `" + table + "` WHERE AULA IS NOT NULL AND CODGRUPO IS NOT NULL AND ASIG IS NOT NULL");
                rs = stm.executeQuery();

                while (rs.next()) {

                    String asig = rs.getString("ASIG");
                    String prof = rs.getString("PROF");
                    String curso = rs.getString("CURSO");
                    String nivel = rs.getString("nivel");
                    String grupo = rs.getString("GRUPO");
                    String aula = rs.getString("AULA");
                    int dia = rs.getInt("DIA");
                    int hora = rs.getInt("HORA");

                    Subject subject = getSubjectBy(asignaturas, asig);
                    Teacher teacher = getTeacherBy(profesores, prof);
                    Group group = getGroupBy(grupos, curso, nivel, grupo);
                    Classroom classroom = getClassroomBy(aulas, aula);
                    TimeZone timeZone = getTimeZoneBy(zonashorarias, dia, hora);

                    if(subject != null && teacher != null && group != null && classroom != null && timeZone != null) {
                        scheduleList.add(new Schedule(teacher, subject, group, classroom, timeZone));
                    }

                }

                rs.close();
                stm.close();

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if(stm != null) {
                try {
                    stm.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return scheduleList;

    }

    /**
     * Insert the list of schedules to mysql db
     */
    private void insertSchedules() {

        StringBuilder insertString = new StringBuilder();
        for(Schedule schedule : scheduleList) {

            if(insertString.toString().equalsIgnoreCase("")) insertString.append("(");
            else insertString.append(", (");

            insertString
                    .append(schedule.getTeacher().getId())
                    .append(",")
                    .append(schedule.getSubject().getId())
                    .append(",")
                    .append(schedule.getGroup().getId())
                    .append(",")
                    .append(schedule.getClassroom().getId())
                    .append(",")
                    .append(schedule.getTimeZone().getId())
                    .append(",'")
                    .append(schedule.getUuid())
                    .append("')");

        }

        dbConnection.insertSchedulesSB(insertString);

    }

    /**
     * Search the day in the list
     * @param dayList
     * @param id
     * @return day
     */
    private Day getDayBy(List<Day> dayList, int id) {
        for(Day day : dayList) {
            if(day.getId() == id) {
                return day;
            }
        }
        return null;
    }

    /**
     * Search the hour in the list
     * @param hourList
     * @param id
     * @return hour
     */
    private Hour getHourBy(List<Hour> hourList, int id) {
        for(Hour hour : hourList) {
            if(hour.getId() == id) {
                return hour;
            }
        }
        return null;
    }

    /**
     * Search the timeZOne in the list
     * @param timeZoneList
     * @param day
     * @param hour
     * @return timeZone
     */
    private TimeZone getTimeZoneBy(List<TimeZone> timeZoneList, int day, int hour) {
        for(TimeZone timeZone : timeZoneList) {
            if(timeZone.getDay().getId() == day && timeZone.getHour().getId() == hour) {
                return timeZone;
            }
        }
        return null;
    }

    /**
     * Search the subject in the list
     * @param subjectList
     * @param text
     * @return subject
     */
    private Subject getSubjectBy(List<Subject> subjectList, String text) {
        for(Subject subject : subjectList) {
            if(subject.getAbreviation().equalsIgnoreCase(text) || Integer.toString(subject.getNumber()).equalsIgnoreCase(text)) {
                return subject;
            }
        }
        return null;
    }

    /**
     * Search the teacher in the list
     * @param teacherList
     * @param abrev
     * @return teacher
     */
    private Teacher getTeacherBy(List<Teacher> teacherList, String abrev) {
        for(Teacher teacher : teacherList) {
            if(teacher.getAbreviation().equalsIgnoreCase(abrev)) {
                return teacher;
            }
        }
        return null;
    }

    /**
     * Search the classroom in the list
     * @param classroomList
     * @param name
     * @return classroom
     */
    private Classroom getClassroomBy(List<Classroom> classroomList, String name) {
        for(Classroom classroom : classroomList) {
            if(classroom.getName().equalsIgnoreCase(name)) {
                return classroom;
            }
        }
        return null;
    }

    /**
     * Search the course in the list
     * @param courseList
     * @param level
     * @param name
     * @return course
     */
    private Course getCourseBy(List<Course> courseList, String level, String name) {
        for(Course course : courseList) {
            if(course.getLevel().equalsIgnoreCase(level) && course.getName().equalsIgnoreCase(name)) {
                return course;
            }
        }
        return null;
    }

    /**
     * Search the grou in the list
     * @param groupList
     * @param level
     * @param name
     * @param letter
     * @return group
     */
    private Group getGroupBy(List<Group> groupList, String level, String name, String letter) {
        for(Group group : groupList) {
            if(group.getLetter().equalsIgnoreCase(letter) && group.getCourse().getLevel().equalsIgnoreCase(level) && group.getCourse().getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Delete all the remaining files from the import process
     * @param parentDirectory
     * @param directoryToBeDeleted
     */
    private void deleteDirectory(File parentDirectory, File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(parentDirectory, file);
            }
        }
        if(!parentDirectory.getAbsolutePath().equalsIgnoreCase(directoryToBeDeleted.getAbsolutePath()))
            directoryToBeDeleted.delete();
    }

}
