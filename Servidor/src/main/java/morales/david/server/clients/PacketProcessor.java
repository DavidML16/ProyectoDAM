package morales.david.server.clients;

import com.google.gson.internal.LinkedTreeMap;
import com.smattme.MysqlExportService;
import io.netty.channel.Channel;
import morales.david.server.interfaces.ScheduleSearcheable;
import morales.david.server.managers.ImportManager;
import morales.david.server.models.*;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.DBConnection;
import morales.david.server.utils.DBConstants;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class PacketProcessor implements Runnable {

    private Channel channel;
    private Packet packet;
    
    private ClientSession clientSession;
    private DBConnection clientDatabase;

    private ClientRepository clientRepository;

    public PacketProcessor(Channel channel, Packet packet) {
        
        this.channel = channel;
        this.packet = packet;

        clientRepository = ClientRepository.getInstance();

        clientSession = clientRepository.getSession(channel);
        clientDatabase = new DBConnection();
        
    }

    @Override
    public void run() {

        PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(packet.getType()));

        clientDatabase.open();

        switch (packetType) {

            case LOGIN:
                login();
                break;

            case DISCONNECT:
                disconnect();
                break;

            case EXIT:
                exit();
                break;

            case IMPORTSTATUS:
                getImportStatus();
                break;

            case CREDENTIALS:
                credentialsList(false);
                break;

            case ADDCREDENTIAL:
                addCredential();
                break;

            case UPDATECREDENTIAL:
                updateCredential();
                break;

            case REMOVECREDENTIAL:
                removeCredential();
                break;

            case TEACHERS:
                teachersList(false);
                break;

            case ADDTEACHER:
                addTeacher();
                break;

            case UPDATETEACHER:
                updateTeacher();
                break;

            case REMOVETEACHER:
                removeTeacher();
                break;

            case CLASSROOMS:
                classroomsList(false);
                break;

            case ADDCLASSROOM:
                addClassroom();
                break;

            case UPDATECLASSROOM:
                updateClassroom();
                break;

            case REMOVECLASSROOM:
                removeClassroom();
                break;

            case COURSES:
                coursesList(false);
                break;

            case ADDCOURSE:
                addCourse();
                break;

            case UPDATECOURSE:
                updateCourse();
                break;

            case REMOVECOURSE:
                removeCourse();
                break;

            case SUBJECTS:
                subjectsList(false);
                break;

            case ADDSUBJECT:
                addSubject();
                break;

            case UPDATESUBJECT:
                updateSubject();
                break;

            case REMOVESUBJECT:
                removeSubject();
                break;

            case GROUPS:
                groupsList(false);
                break;

            case ADDGROUP:
                addGroup();
                break;

            case UPDATEGROUP:
                updateGroup();
                break;

            case REMOVEGROUP:
                removeGroup();
                break;

            case DAYS:
                daysList(false);
                break;

            case UPDATEDAY:
                updateDay();
                break;

            case HOURS:
                hoursList(false);
                break;

            case UPDATEHOUR:
                updateHour();
                break;

            case TIMEZONES:
                timeZoneList();
                break;

            case SCHEDULES:
                scheduleList();
                break;

            case SEARCHSCHEDULE:
                searchSchedule();
                break;

            case EXPORTINSPECTION:
                exportInspectionReport();
                break;

            case INSERTSCHEDULEITEM:
                insertScheduleItem();
                break;

            case SWITCHSCHEDULEITEM:
                switchScheduleItems();
                break;

            case REMOVESCHEDULEITEM:
                removeScheduleItem();
                break;

            case ADDSCHEDULE:
                addSchedule();
                break;

            case UPDATESCHEDULE:
                updateSchedule();
                break;

            case DELETESCHEDULE:
                deleteSchedule();
                break;

            case EMPTYCLASSROOMSTIMEZONE:
                emptyClassroomsListTimeZone();
                break;

            case ADVSCHEDULE:
                advancedSchedulerExport();
                break;

            case ADVINSPECTION:
                advancedInspectionExport();
                break;

            case DATABASEBACKUP:
                databaseBackup();
                break;

            case DATABASECLEAN:
                try {
                    deleteData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

        }

        clientDatabase.close();

    }

    /**
     * Receive username and password from packet
     * Check credential from database
     * Send conformation or error packet to client
     */
    private void login() {

        final String username = (String) packet.getArgument("username");
        final String password = (String) packet.getArgument("password");

        if(clientDatabase.existsCredential(username, password)) {

            clientDatabase.getUserDetails(username, clientSession);

            Packet loginConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.LOGIN.getConfirmation())
                    .addArgument("id", clientSession.getId())
                    .addArgument("name", clientSession.getName())
                    .addArgument("role", clientSession.getRole())
                    .build();

            clientRepository.sendPacketIO(channel, loginConfirmationPacket);

        } else {

            Packet loginErrorPacket = new PacketBuilder()
                    .ofType(PacketType.LOGIN.getError())
                    .build();

            clientRepository.sendPacketIO(channel, loginErrorPacket);

        }

    }

    /**
     * Disconnect client from server
     * Send conformation or error logout packet to client
     */
    private void disconnect() {

        Packet disconnectErrorPacket = new PacketBuilder()
                .ofType(PacketType.DISCONNECT.getError())
                .build();

        clientRepository.sendPacketIO(channel, disconnectErrorPacket);

    }

    /**
     * Close client from server
     * Send confirmation or error logout packet to client
     */
    private void exit() {

        Packet exitConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.EXIT.getConfirmation())
                .build();

        clientRepository.sendPacketIO(channel, exitConfirmationPacket);

    }

    /**
     * Send if is a file already importing
     */
    private void getImportStatus() {

        boolean importing = ImportManager.getInstance().isImporting();

        Packet importStatusConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                .addArgument("message", "")
                .addArgument("type", "ping")
                .addArgument("importing", importing)
                .build();

        clientRepository.broadcast(importStatusConfirmationPacket);

    }


    /**
     * Get credentials list from database
     * Send credentials list packet to client
     */
    private void credentialsList(boolean broadcast) {

        List<Credential> credentials = clientDatabase.getCredentials();

        Packet credentialsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.CREDENTIALS.getConfirmation())
                .addArgument("credentials", credentials)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, credentialsConfirmationPacket);
        else
            clientRepository.broadcast(credentialsConfirmationPacket);

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Add credential to database, and send confirmation or error packet to client
     */
    private void addCredential() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap credentialMap = (LinkedTreeMap) packet.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientDatabase.addCredential(credential)) {

            credentialsList(true);

        } else {

            Packet addCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCREDENTIAL.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addCredentialErrorPacket);

        }

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Update credential from database, and send confirmation or error packet to client
     */
    private void updateCredential() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap credentialMap = (LinkedTreeMap) packet.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientDatabase.updateCredential(credential)) {

            credentialsList(true);

        } else {

            Packet updateCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECREDENTIAL.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateCredentialErrorPacket);

        }

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Remove credential from database, and send confirmation or error packet to client
     */
    private void removeCredential() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap credentialMap = (LinkedTreeMap) packet.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientDatabase.removeCredential(credential)) {

            credentialsList(true);

        } else {

            Packet removeCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECREDENTIAL.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeCredentialErrorPacket);

        }

    }


    /**
     * Get teachers list from database
     * Send teachers list packet to client
     */
    private void teachersList(boolean broadcast) {

        List<Teacher> teachers = clientDatabase.getTeachers();

        Packet teachersConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.TEACHERS.getConfirmation())
                .addArgument("teachers", teachers)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, teachersConfirmationPacket);
        else
            clientRepository.broadcast(teachersConfirmationPacket);

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Add teacher to database, and send confirmation or error packet to client
     */
    private void addTeacher() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap teacherMap = (LinkedTreeMap) packet.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientDatabase.addTeacher(teacher)) {

            teachersList(true);

        } else {

            Packet addTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDTEACHER.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addTeacherErrorPacket);

        }

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Update teacher from database, and send confirmation or error packet to client
     */
    private void updateTeacher() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap teacherMap = (LinkedTreeMap) packet.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientDatabase.updateTeacher(teacher)) {

            teachersList(true);

        } else {

            Packet updateTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATETEACHER.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateTeacherErrorPacket);

        }

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Remove teacher from database, and send confirmation or error packet to client
     */
    private void removeTeacher() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap teacherMap = (LinkedTreeMap) packet.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientDatabase.removeTeacher(teacher)) {

            teachersList(true);

        } else {

            Packet removeTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVETEACHER.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeTeacherErrorPacket);

        }

    }


    /**
     * Get classrooms list from database
     * Send classrooms list packet to client
     */
    private void classroomsList(boolean broadcast) {

        List<Classroom> classrooms = clientDatabase.getClassrooms();

        Packet classroomsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.CLASSROOMS.getConfirmation())
                .addArgument("classrooms", classrooms)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, classroomsConfirmationPacket);
        else
            clientRepository.broadcast(classroomsConfirmationPacket);

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Add classroom to database, and send confirmation or error packet to client
     */
    private void addClassroom() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap classroomMap = (LinkedTreeMap) packet.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientDatabase.addClassroom(classroom)) {

            classroomsList(true);

        } else {

            Packet addClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCLASSROOM.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addClassroomErrorPacket);

        }

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Update classroom from database, and send confirmation or error packet to client
     */
    private void updateClassroom() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap classroomMap = (LinkedTreeMap) packet.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientDatabase.updateClassroom(classroom)) {

            classroomsList(true);

        } else {

            Packet updateClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECLASSROOM.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateClassroomErrorPacket);

        }

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Remove classroom from database, and send confirmation or error packet to client
     */
    private void removeClassroom() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap classroomMap = (LinkedTreeMap) packet.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientDatabase.removeClassroom(classroom)) {

            classroomsList(true);

        } else {

            Packet removeClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECLASSROOM.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeClassroomErrorPacket);

        }

    }


    /**
     * Get courses list from database
     * Send courses list packet to client
     */
    private void coursesList(boolean broadcast) {

        List<Course> courses = clientDatabase.getCourses();

        Packet coursesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.COURSES.getConfirmation())
                .addArgument("courses", courses)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, coursesConfirmationPacket);
        else
            clientRepository.broadcast(coursesConfirmationPacket);

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Add course to database, and send confirmation or error packet to client
     */
    private void addCourse() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap courseMap = (LinkedTreeMap) packet.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientDatabase.addCourse(course)) {

            coursesList(true);

        } else {

            Packet addCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCOURSE.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addCourseErrorPacket);

        }

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Update course from database, and send confirmation or error packet to client
     */
    private void updateCourse() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap courseMap = (LinkedTreeMap) packet.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientDatabase.updateCourse(course)) {

            coursesList(true);

        } else {

            Packet updateCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECOURSE.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateCourseErrorPacket);

        }

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Remove course from database, and send confirmation or error packet to client
     */
    private void removeCourse() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap courseMap = (LinkedTreeMap) packet.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientDatabase.removeCourse(course)) {

            coursesList(true);

        } else {

            Packet removeCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECOURSE.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeCourseErrorPacket);

        }

    }


    /**
     * Get subjects list from database
     * Send subjects list packet to client
     */
    private void subjectsList(boolean broadcast) {

        List<Subject> subjects = clientDatabase.getSubjects();

        Packet subjectsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.SUBJECTS.getConfirmation())
                .addArgument("subjects", subjects)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, subjectsConfirmationPacket);
        else
            clientRepository.broadcast(subjectsConfirmationPacket);

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Add subject to database, and send confirmation or error packet to client
     */
    private void addSubject() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap subjectMap = (LinkedTreeMap) packet.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientDatabase.addSubject(subject)) {

            subjectsList(true);

        } else {

            Packet addSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSUBJECT.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addSubjectErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Update subject from database, and send confirmation or error packet to client
     */
    private void updateSubject() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap subjectMap = (LinkedTreeMap) packet.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientDatabase.updateSubject(subject)) {

            subjectsList(true);

        } else {

            Packet updateSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESUBJECT.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateSubjectErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Remove subject from database, and send confirmation or error packet to client
     */
    private void removeSubject() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap subjectMap = (LinkedTreeMap) packet.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientDatabase.removeSubject(subject)) {

            subjectsList(true);

        } else {

            Packet removeSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESUBJECT.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeSubjectErrorPacket);

        }

    }


    /**
     * Get groups list from database
     * Send groups list packet to client
     */
    private void groupsList(boolean broadcast) {

        List<Group> groups = clientDatabase.getGroups();

        Packet groupsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.GROUPS.getConfirmation())
                .addArgument("groups", groups)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, groupsConfirmationPacket);
        else
            clientRepository.broadcast(groupsConfirmationPacket);

    }

    /**
     * Get group data from packet
     * Parse group data and return group object
     * Add group to database, and send confirmation or error packet to client
     */
    private void addGroup() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap groupMap = (LinkedTreeMap) packet.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientDatabase.addGroup(group)) {

            groupsList(true);

        } else {

            Packet addGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDGROUP.getError())
                    .build();

            clientRepository.sendPacketIO(channel, addGroupErrorPacket);

        }

    }

    /**
     * Get group data from packet
     * Parse group data and return group object
     * Update group from database, and send confirmation or error packet to client
     */
    private void updateGroup() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap groupMap = (LinkedTreeMap) packet.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientDatabase.updateGroup(group)) {

            groupsList(true);

        } else {

            Packet updateGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEGROUP.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateGroupErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Remove subject from database, and send confirmation or error packet to client
     */
    private void removeGroup() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap groupMap = (LinkedTreeMap) packet.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientDatabase.removeGroup(group)) {

            groupsList(true);

        } else {

            Packet removeGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVEGROUP.getError())
                    .build();

            clientRepository.sendPacketIO(channel, removeGroupErrorPacket);

        }

    }


    /**
     * Get days list from database
     * Send days list packet to client
     */
    private void daysList(boolean broadcast) {

        List<Day> days = clientDatabase.getDays();

        Packet daysConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.DAYS.getConfirmation())
                .addArgument("days", days)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, daysConfirmationPacket);
        else
            clientRepository.broadcast(daysConfirmationPacket);

    }

    /**
     * Get day data from packet
     * Parse day data and return day object
     * Update day from database, and send confirmation or error packet to client
     */
    private void updateDay() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap dayMap = (LinkedTreeMap) packet.getArgument("day");

        Day day = Day.parse(dayMap);

        if(clientDatabase.updateDay(day)) {

            daysList(true);

        } else {

            Packet updateDayErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEDAY.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateDayErrorPacket);

        }

    }


    /**
     * Get hours list from database
     * Send hours list packet to client
     */
    private void hoursList(boolean broadcast) {

        List<Hour> hours = clientDatabase.getHours();

        Packet hoursConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.HOURS.getConfirmation())
                .addArgument("hours", hours)
                .build();

        if(!broadcast)
            clientRepository.sendPacketIO(channel, hoursConfirmationPacket);
        else
            clientRepository.broadcast(hoursConfirmationPacket);

    }

    /**
     * Get hour data from packet
     * Parse hour data and return hour object
     * Update hour from database, and send confirmation or error packet to client
     */
    private void updateHour() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap hourMap = (LinkedTreeMap) packet.getArgument("hour");

        Hour hour = Hour.parse(hourMap);

        if(clientDatabase.updateHour(hour)) {

            hoursList(true);

        } else {

            Packet updateHourErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEHOUR.getError())
                    .build();

            clientRepository.sendPacketIO(channel, updateHourErrorPacket);

        }

    }


    /**
     * Get timezone list from database
     * Send timezone list packet to client
     */
    private void timeZoneList() {

        List<TimeZone> timeZones = clientDatabase.getTimeZones();

        Packet timeZonesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.TIMEZONES.getConfirmation())
                .addArgument("timeZones", timeZones)
                .build();

        clientRepository.sendPacketIO(channel, timeZonesConfirmationPacket);

    }


    /**
     * Get schedule data from database
     * Search all schedules
     */
    private void scheduleList() {

        Integer schedules = clientDatabase.getSchedulesAmount();

        Packet schedulesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.SCHEDULES.getConfirmation())
                .addArgument("schedules", schedules)
                .build();

        clientRepository.sendPacketIO(channel, schedulesConfirmationPacket);

    }

    /**
     * Get schedule data from database
     * Search schedule by packet arguments
     */
    private void searchSchedule() {

        String callback = (String) packet.getArgument("callback");
        String searchType = (String) packet.getArgument("type");

        ScheduleSearcheable scheduleSearcheable = null;

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.SEARCHSCHEDULE.getConfirmation())
                .addArgument("callback", callback)
                .addArgument("searchType", searchType);

        if(searchType.equalsIgnoreCase("TEACHER")) {

            LinkedTreeMap teacherMap = (LinkedTreeMap) packet.getArgument("item");

            Teacher teacher = Teacher.parse(teacherMap);

            packetBuilder.addArgument("searchQuery", teacher.getName());

            scheduleSearcheable = teacher;

        } else if(searchType.equalsIgnoreCase("GROUP")) {

            LinkedTreeMap groupMap = (LinkedTreeMap) packet.getArgument("item");

            Group group = Group.parse(groupMap);

            packetBuilder.addArgument("searchQuery", group.toString());

            scheduleSearcheable = group;

        } else if(searchType.equalsIgnoreCase("CLASSROOM")) {

            LinkedTreeMap classroomMap = (LinkedTreeMap) packet.getArgument("item");

            Classroom classroom = Classroom.parse(classroomMap);

            packetBuilder.addArgument("searchQuery", classroom.getName());

            scheduleSearcheable = classroom;

        }

        if(scheduleSearcheable != null) {

            List<SchedulerItem> schedules = clientDatabase.searchSchedule(scheduleSearcheable);

            packetBuilder.addArgument("schedules", schedules);

            clientRepository.sendPacketIO(channel, packetBuilder.build());

        }

    }

    private void advancedSchedulerExport() {

        List<LinkedTreeMap> exportableItemsRaw = (List<LinkedTreeMap>) packet.getArgument("exportableItems");

        final List<ExportableItem> exportableItems = new ArrayList<>();

        for (LinkedTreeMap map : exportableItemsRaw)
            exportableItems.add(ExportableItem.parse(map));

        for(ExportableItem exportableItem : exportableItems) {

            ScheduleSearcheable scheduleSearcheable = (ScheduleSearcheable) exportableItem.getItem();

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.ADVSCHEDULE.getConfirmation())
                    .addArgument("exportType", exportableItem.getExportType())
                    .addArgument("exportQuery", exportableItem.getExportQuery());

            if(scheduleSearcheable != null) {

                List<SchedulerItem> schedules = clientDatabase.searchSchedule(scheduleSearcheable);

                packetBuilder.addArgument("schedules", schedules);

                clientRepository.sendPacketIO(channel, packetBuilder.build());

            }

        }

    }

    private void advancedInspectionExport() {

        List<LinkedTreeMap> exportableTimeZonesRaw = (List<LinkedTreeMap>) packet.getArgument("timeZones");

        final List<TimeZone> timeZones = new ArrayList<>();

        for (LinkedTreeMap map : exportableTimeZonesRaw)
            timeZones.add(TimeZone.parse(map));

        for(TimeZone timeZone : timeZones) {

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.ADVINSPECTION.getConfirmation())
                    .addArgument("timeZone", timeZone);

            List<ScheduleTurn> schedules = clientDatabase.getScheduleTurns(timeZone);

            packetBuilder.addArgument("schedules", schedules);

            clientRepository.sendPacketIO(channel, packetBuilder.build());

        }

    }

    /**
     * Get schedule data from database
     * Search schedule by packet arguments
     */
    private void exportInspectionReport() {

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) packet.getArgument("timeZone");

        if(timeZoneMap == null)
            return;

        TimeZone timeZone = TimeZone.parse(timeZoneMap);

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.EXPORTINSPECTION.getConfirmation())
                .addArgument("timeZone", timeZone);

        List<ScheduleTurn> schedules = clientDatabase.getScheduleTurns(timeZone);

        packetBuilder.addArgument("schedules", schedules);

        clientRepository.sendPacketIO(channel, packetBuilder.build());

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Insert schedule item to database, and send confirmation or error packet to client
     */
    private void insertScheduleItem() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap scheduleMap = (LinkedTreeMap) packet.getArgument("scheduleItem");

        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

        if(schedulerItem == null)
            return;

        int inserted = 1;
        int required = schedulerItem.getScheduleList().size();

        for(Schedule schedule : schedulerItem.getScheduleList()) {
            if(clientDatabase.insertSchedule(schedule)) {
                inserted++;
            }
        }

        if(inserted > required) {

            Packet insertScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.INSERTSCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            clientRepository.sendPacketIO(channel, insertScheduleConfirmationPacket);

        } else {

            Packet insertScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.INSERTSCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido insertar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, insertScheduleErrorPacket);

        }

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Switch schedule items from database, and send confirmation or error packet to client
     */
    private void switchScheduleItems() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap schedule1Map = (LinkedTreeMap) packet.getArgument("scheduleItem1");
        SchedulerItem schedule1 = SchedulerItem.parse(schedule1Map);

        LinkedTreeMap schedule2Map = (LinkedTreeMap) packet.getArgument("scheduleItem2");
        SchedulerItem schedule2 = SchedulerItem.parse(schedule2Map);

        if(schedule1 == null || schedule2 == null)
            return;

        TimeZone timezone1 = schedule1.getScheduleList().get(0).getTimeZone();
        TimeZone timezone2 = schedule2.getScheduleList().get(0).getTimeZone();

        int updated = 1;
        int updatedRequired = schedule1.getScheduleList().size() + schedule2.getScheduleList().size();

        List<Schedule> tempDelete = new ArrayList<>();
        tempDelete.addAll(schedule1.getScheduleList());
        tempDelete.addAll(schedule2.getScheduleList());

        for(Schedule schedule : tempDelete) {
            clientDatabase.removeSchedule(schedule);
        }

        for(Schedule schedule : schedule1.getScheduleList()) {
            schedule.setTimeZone(timezone2);
            if(clientDatabase.insertSchedule(schedule))
                updated++;
        }

        for(Schedule schedule : schedule2.getScheduleList()) {
            schedule.setTimeZone(timezone1);
            if(clientDatabase.insertSchedule(schedule))
                updated++;
        }

        if(updated >= updatedRequired) {

            Packet switchScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.SWITCHSCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedule1.getUuid() + "|" + schedule2.getUuid())
                    .addArgument("scheduleItem1", schedule1)
                    .addArgument("scheduleItem2", schedule2)
                    .build();

            clientRepository.sendPacketIO(channel, switchScheduleConfirmationPacket);

        } else {

            Packet insertScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.SWITCHSCHEDULEITEM.getError())
                    .addArgument("uuid", schedule1.getUuid() + "|" + schedule2.getUuid())
                    .addArgument("message", "No se ha podido insertar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, insertScheduleErrorPacket);

        }

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Remove schedule item from database, and send confirmation or error packet to client
     */
    private void removeScheduleItem() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap scheduleMap = (LinkedTreeMap) packet.getArgument("scheduleItem");

        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

        if(schedulerItem == null)
            return;

        int removed = 1;
        int required = schedulerItem.getScheduleList().size();

        for(Schedule schedule : schedulerItem.getScheduleList()) {
            if(clientDatabase.removeSchedule(schedule)) {
                removed++;
            }
        }

        if(removed > required) {

            Packet deleteScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            clientRepository.sendPacketIO(channel, deleteScheduleConfirmationPacket);

        } else {

            Packet deleteScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido eliminar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, deleteScheduleErrorPacket);

        }

    }


    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Add schedule to database, and send confirmation or error packet to client
     */
    private void addSchedule() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) packet.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) packet.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientDatabase.insertSchedule(schedule)) {

            Packet addScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            clientRepository.sendPacketIO(channel, addScheduleConfirmationPacket);

        } else {

            Packet addScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSCHEDULE.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido editar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, addScheduleErrorPacket);

        }

    }

    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Update schedule from database, and send confirmation or error packet to client
     */
    private void updateSchedule() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) packet.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) packet.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientDatabase.updateSchedule(schedule)) {

            Packet updateScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            clientRepository.sendPacketIO(channel, updateScheduleConfirmationPacket);

        } else {

            Packet updateScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESCHEDULE.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido editar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, updateScheduleErrorPacket);

        }

    }

    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Remove schedule from database, and send confirmation or error packet to client
     */
    private void deleteSchedule() {

        if(clientSession.isTeacherRole())
            return;

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) packet.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) packet.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientDatabase.removeSchedule(schedule)) {

            Packet deleteScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.DELETESCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            clientRepository.sendPacketIO(channel, deleteScheduleConfirmationPacket);

        } else {

            Packet deleteScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido eliminar ese schedule")
                    .build();

            clientRepository.sendPacketIO(channel, deleteScheduleErrorPacket);

        }

    }


    /**
     * Get empty classrooms list from database in a specified timezone
     * Send classrooms list packet to client
     */
    private void emptyClassroomsListTimeZone() {

        String uuid = (String) packet.getArgument("uuid");

        Packet classroomErrorPacket = new PacketBuilder()
                .ofType(PacketType.EMPTYCLASSROOMSTIMEZONE.getError())
                .addArgument("uuid", uuid)
                .addArgument("message", "No se ha podido buscar las aulas vacías")
                .build();

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) packet.getArgument("timeZone");

        if(timeZoneMap == null) {
            clientRepository.sendPacketIO(channel, classroomErrorPacket);
            return;
        }

        TimeZone timeZone = TimeZone.parse(timeZoneMap);

        if(timeZone == null) {
            clientRepository.sendPacketIO(channel, classroomErrorPacket);
            return;
        }

        List<Classroom> classrooms = clientDatabase.getEmptyClassroomsTimeZone(timeZone);

        Packet classroomsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.EMPTYCLASSROOMSTIMEZONE.getConfirmation())
                .addArgument("uuid", uuid)
                .addArgument("classrooms", classrooms)
                .build();

        clientRepository.sendPacketIO(channel, classroomsConfirmationPacket);

    }


    private void databaseBackup() {

        boolean sendEmail = Boolean.parseBoolean((String) packet.getArgument("sendEmail"));
        String email = (String) packet.getArgument("email");

        String backupDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        Properties properties = new Properties();
        properties.setProperty(MysqlExportService.DB_NAME, DBConstants.DB_DATABASE);
        properties.setProperty(MysqlExportService.DB_USERNAME, DBConstants.DB_USER);
        properties.setProperty(MysqlExportService.DB_PASSWORD, DBConstants.DB_PASS);

        if(sendEmail) {

            properties.setProperty(MysqlExportService.EMAIL_HOST, "smtp.gmail.com");
            properties.setProperty(MysqlExportService.EMAIL_PORT, "587");
            properties.setProperty(MysqlExportService.EMAIL_USERNAME, "sgh.david.morales@gmail.com");
            properties.setProperty(MysqlExportService.EMAIL_PASSWORD, "sghdavid044!");
            properties.setProperty(MysqlExportService.EMAIL_FROM, "sgh.david.morales@gmail.com");
            properties.setProperty(MysqlExportService.EMAIL_SUBJECT, "BACKUP " + backupDate);
            properties.setProperty(MysqlExportService.EMAIL_MESSAGE, "Hola, aquí tienes la copia de seguridad del sistema de horarios del centro. \nPara importarla, necesitas usar un cliente MySQL como PhpMyAdmin");
            properties.setProperty(MysqlExportService.EMAIL_TO, email);

        }

        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, "jdbc:mysql://" + DBConstants.DB_IP + ":" + DBConstants.DB_PORT + "/" + DBConstants.DB_DATABASE);

        properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");

        properties.setProperty(MysqlExportService.TEMP_DIR, new File("backups").getPath());

        MysqlExportService mysqlExportService = new MysqlExportService(properties);
        try {
            mysqlExportService.export();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String generatedSql = mysqlExportService.getGeneratedSql();

        mysqlExportService.clearTempFiles();

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.DATABASEBACKUP.getConfirmation())
                .addArgument("date", backupDate)
                .addArgument("sql", generatedSql);

        clientRepository.sendPacketIO(channel, packetBuilder.build());

    }


    private void deleteData() throws SQLException {

        String type = (String) packet.getArgument("type");

        DBConnection connection = clientDatabase;

        if(type.equalsIgnoreCase("all")) {

            connection.clearAll(false);
            connection.resetAll(false);

        } else if(type.equalsIgnoreCase("schedules")) {

            Statement scheduleStm = connection.getConnection().createStatement();
            scheduleStm.execute(DBConstants.DB_QUERY_CLEAR_SCHEDULES);
            scheduleStm.close();

            Statement schedule2Stm = connection.getConnection().createStatement();
            schedule2Stm.execute(DBConstants.DB_QUERY_RESET_SCHEDULES);
            schedule2Stm.close();

        } else if(type.equalsIgnoreCase("subjects")) {

            Statement courseSubjectsStm = connection.getConnection().createStatement();
            courseSubjectsStm.execute(DBConstants.DB_QUERY_CLEAR_COURSE_SUBJECTS);
            courseSubjectsStm.close();

            Statement subjectStm = connection.getConnection().createStatement();
            subjectStm.execute(DBConstants.DB_QUERY_CLEAR_SUBJECTS);
            subjectStm.close();

            Statement subject2Stm = connection.getConnection().createStatement();
            subject2Stm.execute(DBConstants.DB_QUERY_RESET_SUBJECTS);
            subject2Stm.close();

        } else if(type.equalsIgnoreCase("teachers")) {

            Statement teacherStm = connection.getConnection().createStatement();
            teacherStm.execute(DBConstants.DB_QUERY_CLEAR_TEACHERS);
            teacherStm.close();

            Statement teacher2Stm = connection.getConnection().createStatement();
            teacher2Stm.execute(DBConstants.DB_QUERY_RESET_TEACHERS);
            teacher2Stm.close();

        } else if(type.equalsIgnoreCase("courses")) {

            Statement courseStm = connection.getConnection().createStatement();
            courseStm.execute(DBConstants.DB_QUERY_CLEAR_COURSES);
            courseStm.close();

            Statement course2Stm = connection.getConnection().createStatement();
            course2Stm.execute(DBConstants.DB_QUERY_RESET_COURSES);
            course2Stm.close();

        } else if(type.equalsIgnoreCase("groups")) {

            Statement groupStm = connection.getConnection().createStatement();
            groupStm.execute(DBConstants.DB_QUERY_CLEAR_GROUPS);
            groupStm.close();

            Statement group2Stm = connection.getConnection().createStatement();
            group2Stm.execute(DBConstants.DB_QUERY_RESET_GROUPS);
            group2Stm.close();

        } else if(type.equalsIgnoreCase("classrooms")) {

            Statement classroomStm = connection.getConnection().createStatement();
            classroomStm.execute(DBConstants.DB_QUERY_CLEAR_CLASSROOMS);
            classroomStm.close();

            Statement classroom2Stm = connection.getConnection().createStatement();
            classroom2Stm.execute(DBConstants.DB_QUERY_RESET_CLASSROOMS);
            classroom2Stm.close();

        }

    }

}
