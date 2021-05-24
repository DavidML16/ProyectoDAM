package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.models.*;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.DBConnection;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
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

    public ImportManager(Server server) {
        this.server = server;
        this.isImporting = false;
        this.dbConnection = new DBConnection();
    }

    public synchronized boolean isImporting() {
        return isImporting;
    }

    public synchronized File getFile() {
        return file;
    }

    public synchronized void setFile(File file) {
        this.file = file;
    }

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
                subjectList.add(new Subject(id, number, abreviation, name, new ArrayList<>()));

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
                .append("')");

        }

        dbConnection.insertSubjectsSB(insertString);

    }

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

    private List<Classroom> getClassrooms() {

        List<Classroom> classroomList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            int i = 1;

            String[] tables = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};

            for(String table : tables) {

                stm = acon.prepareStatement("SELECT AULA FROM `" + table + "` WHERE AULA IS NOT NULL GROUP BY AULA");
                rs = stm.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("AULA");
                    classroomList.add(new Classroom(i, name.trim().replace(" ", "")));
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

        return classroomList;

    }
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

    private List<Course> getCourses() {

        List<Course> courseList = new ArrayList<>();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            int i = 1;

            String[] tables = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};

            for(String table : tables) {

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

    private List<CourseSubject> getCourseSubjects() {

        List<CourseSubject> courseSubjectList = new ArrayList<>();

        List<Course> courseList = dbConnection.getCourses();
        List<Subject> subjectList = dbConnection.getSubjects();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            String[] tables = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};

            for(String table : tables) {

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

    private List<Group> getGroups() {

        List<Group> groupList = new ArrayList<>();

        List<Course> courseList = dbConnection.getCourses();

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            String[] tables = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};

            int i = 1;

            for(String table : tables) {

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

            String[] tables = {"Soluc fp", "Soluc1 fpbasica", "Soluc inf", "Soluc esoycam", "solucion total"};

            int i = 1;

            for(String table : tables) {

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
                        scheduleList.add(new Schedule(i, teacher, subject, group, classroom, timeZone));
                        i++;
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
                    .append(")");

        }

        dbConnection.insertSchedulesSB(insertString);

    }

    private Day getDayBy(List<Day> dayList, int id) {
        for(Day day : dayList) {
            if(day.getId() == id) {
                return day;
            }
        }
        return null;
    }
    private Hour getHourBy(List<Hour> hourList, int id) {
        for(Hour hour : hourList) {
            if(hour.getId() == id) {
                return hour;
            }
        }
        return null;
    }
    private TimeZone getTimeZoneBy(List<TimeZone> timeZoneList, int day, int hour) {
        for(TimeZone timeZone : timeZoneList) {
            if(timeZone.getDay().getId() == day && timeZone.getHour().getId() == hour) {
                return timeZone;
            }
        }
        return null;
    }
    private Subject getSubjectBy(List<Subject> subjectList, String text) {
        for(Subject subject : subjectList) {
            if(subject.getAbreviation().equalsIgnoreCase(text) || Integer.toString(subject.getNumber()).equalsIgnoreCase(text)) {
                return subject;
            }
        }
        return null;
    }
    private Teacher getTeacherBy(List<Teacher> teacherList, String abrev) {
        for(Teacher teacher : teacherList) {
            if(teacher.getAbreviation().equalsIgnoreCase(abrev)) {
                return teacher;
            }
        }
        return null;
    }
    private Classroom getClassroomBy(List<Classroom> classroomList, String name) {
        for(Classroom classroom : classroomList) {
            if(classroom.getName().equalsIgnoreCase(name)) {
                return classroom;
            }
        }
        return null;
    }
    private Course getCourseBy(List<Course> courseList, String level, String name) {
        for(Course course : courseList) {
            if(course.getLevel().equalsIgnoreCase(level) && course.getName().equalsIgnoreCase(name)) {
                return course;
            }
        }
        return null;
    }
    private Group getGroupBy(List<Group> groupList, String level, String name, String letter) {
        for(Group group : groupList) {
            if(group.getLetter().equalsIgnoreCase(letter) && group.getCourse().getLevel().equalsIgnoreCase(level) && group.getCourse().getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }

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
