package morales.david.server.clients;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.interfaces.ScheduleSearcheable;
import morales.david.server.Server;
import morales.david.server.models.*;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientProtocol {

    private ClientThread clientThread;

    private boolean logged;

    private Packet lastPacket;

    public ClientProtocol(ClientThread clientThread) {
        this.clientThread = clientThread;
        this.logged = false;
    }


    /**
     * Parse input packet and execute specified actions
     * @param packet
     */
    public void parseInput(Packet packet) {

        lastPacket = packet;

        PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(lastPacket.getType()));

        clientThread.getDbConnection().open();

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
                credentialsList();
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
                teachersList();
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
                classroomsList();
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
                coursesList();
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
                subjectsList();
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
                groupsList();
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
                daysList();
                break;

            case UPDATEDAY:
                updateDay();
                break;

            case HOURS:
                hoursList();
                break;

            case UPDATEHOUR:
                updateHour();
                break;

            case TIMEZONES:
                timeZoneList();
                break;

            case SCHEDULES:
                searchSchedule();
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

        }

        clientThread.getDbConnection().close();

    }

    /**
     * Receive username and password from packet
     * Check credential from database
     * Send conformation or error packet to client
     */
    private void login() {

        final String username = (String) lastPacket.getArgument("username");
        final String password = (String) lastPacket.getArgument("password");

        ClientSession clientSession = clientThread.getClientSession();
        DBConnection dbConnection = clientThread.getDbConnection();

        if(dbConnection.existsCredential(username, password)) {

            dbConnection.getUserDetails(username, clientSession);

            Packet loginConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.LOGIN.getConfirmation())
                    .addArgument("id", clientSession.getId())
                    .addArgument("name", clientSession.getName())
                    .addArgument("role", clientSession.getRole())
                    .build();

            sendPacketIO(loginConfirmationPacket);

            logged = true;

        } else {

            Packet loginErrorPacket = new PacketBuilder()
                    .ofType(PacketType.LOGIN.getError())
                    .build();

            sendPacketIO(loginErrorPacket);

        }

    }

    /**
     * Disconnect client from server
     * Send conformation or error logout packet to client
     */
    private void disconnect() {

        if(logged) {

            Packet disconnectConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.DISCONNECT.getConfirmation())
                    .build();

            sendPacketIO(disconnectConfirmationPacket);
            clientThread.setConnected(false);

        } else {

            Packet disconnectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.DISCONNECT.getError())
                    .build();

            sendPacketIO(disconnectErrorPacket);

        }

    }

    /**
     * Close client from server
     * Send confirmation or error logout packet to client
     */
    private void exit() {

        Packet exitConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.EXIT.getConfirmation())
                .build();

        sendPacketIO(exitConfirmationPacket);
        clientThread.setConnected(false);

    }

    /**
     * Send if is a file already importing
     */
    private void getImportStatus() {

        boolean importing = clientThread.getServer().getImportManager().isImporting();

        Packet importStatusConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.IMPORTSTATUS.getConfirmation())
                .addArgument("message", "")
                .addArgument("type", "ping")
                .addArgument("importing", importing)
                .build();

        clientThread.getServer().getClientRepository().broadcast(importStatusConfirmationPacket);

    }


    /**
     * Get credentials list from database
     * Send credentials list packet to client
     */
    private void credentialsList() {

        List<Credential> credentials = clientThread.getDbConnection().getCredentials();

        Packet credentialsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.CREDENTIALS.getConfirmation())
                .addArgument("credentials", credentials)
                .build();

        sendPacketIO(credentialsConfirmationPacket);

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Add credential to database, and send confirmation or error packet to client
     */
    private void addCredential() {

        LinkedTreeMap credentialMap = (LinkedTreeMap) lastPacket.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientThread.getDbConnection().addCredential(credential)) {

            credentialsList();

        } else {

            Packet addCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCREDENTIAL.getError())
                    .build();

            sendPacketIO(addCredentialErrorPacket);

        }

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Update credential from database, and send confirmation or error packet to client
     */
    private void updateCredential() {

        LinkedTreeMap credentialMap = (LinkedTreeMap) lastPacket.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientThread.getDbConnection().updateCredential(credential)) {

            credentialsList();

        } else {

            Packet updateCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECREDENTIAL.getError())
                    .build();

            sendPacketIO(updateCredentialErrorPacket);

        }

    }

    /**
     * Get credential data from packet
     * Parse credential data and return credential object
     * Remove credential from database, and send confirmation or error packet to client
     */
    private void removeCredential() {

        LinkedTreeMap credentialMap = (LinkedTreeMap) lastPacket.getArgument("credential");

        Credential credential = Credential.parse(credentialMap);

        if(clientThread.getDbConnection().removeCredential(credential)) {

            credentialsList();

        } else {

            Packet removeCredentialErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECREDENTIAL.getError())
                    .build();

            sendPacketIO(removeCredentialErrorPacket);

        }

    }


    /**
     * Get teachers list from database
     * Send teachers list packet to client
     */
    private void teachersList() {

        List<Teacher> teachers = clientThread.getDbConnection().getTeachers();

        Packet teachersConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.TEACHERS.getConfirmation())
                .addArgument("teachers", teachers)
                .build();

        sendPacketIO(teachersConfirmationPacket);

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Add teacher to database, and send confirmation or error packet to client
     */
    private void addTeacher() {

        LinkedTreeMap teacherMap = (LinkedTreeMap) lastPacket.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientThread.getDbConnection().addTeacher(teacher)) {

            teachersList();

        } else {

            Packet addTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDTEACHER.getError())
                    .build();

            sendPacketIO(addTeacherErrorPacket);

        }

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Update teacher from database, and send confirmation or error packet to client
     */
    private void updateTeacher() {

        LinkedTreeMap teacherMap = (LinkedTreeMap) lastPacket.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientThread.getDbConnection().updateTeacher(teacher)) {

            teachersList();

        } else {

            Packet updateTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATETEACHER.getError())
                    .build();

            sendPacketIO(updateTeacherErrorPacket);

        }

    }

    /**
     * Get teacher data from packet
     * Parse teacher data and return teacher object
     * Remove teacher from database, and send confirmation or error packet to client
     */
    private void removeTeacher() {

        LinkedTreeMap teacherMap = (LinkedTreeMap) lastPacket.getArgument("teacher");

        Teacher teacher = Teacher.parse(teacherMap);

        if(clientThread.getDbConnection().removeTeacher(teacher)) {

            teachersList();

        } else {

            Packet removeTeacherErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVETEACHER.getError())
                    .build();

            sendPacketIO(removeTeacherErrorPacket);

        }

    }


    /**
     * Get classrooms list from database
     * Send classrooms list packet to client
     */
    private void classroomsList() {

        List<Classroom> classrooms = clientThread.getDbConnection().getClassrooms();

        Packet classroomsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.CLASSROOMS.getConfirmation())
                .addArgument("classrooms", classrooms)
                .build();

        sendPacketIO(classroomsConfirmationPacket);

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Add classroom to database, and send confirmation or error packet to client
     */
    private void addClassroom() {

        LinkedTreeMap classroomMap = (LinkedTreeMap) lastPacket.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientThread.getDbConnection().addClassroom(classroom)) {

            classroomsList();

        } else {

            Packet addClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCLASSROOM.getError())
                    .build();

            sendPacketIO(addClassroomErrorPacket);

        }

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Update classroom from database, and send confirmation or error packet to client
     */
    private void updateClassroom() {

        LinkedTreeMap classroomMap = (LinkedTreeMap) lastPacket.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientThread.getDbConnection().updateClassroom(classroom)) {

            classroomsList();

        } else {

            Packet updateClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECLASSROOM.getError())
                    .build();

            sendPacketIO(updateClassroomErrorPacket);

        }

    }

    /**
     * Get classroom data from packet
     * Parse classroom data and return classroom object
     * Remove classroom from database, and send confirmation or error packet to client
     */
    private void removeClassroom() {

        LinkedTreeMap classroomMap = (LinkedTreeMap) lastPacket.getArgument("classroom");

        Classroom classroom = Classroom.parse(classroomMap);

        if(clientThread.getDbConnection().removeClassroom(classroom)) {

            classroomsList();

        } else {

            Packet removeClassroomErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECLASSROOM.getError())
                    .build();

            sendPacketIO(removeClassroomErrorPacket);

        }

    }


    /**
     * Get courses list from database
     * Send courses list packet to client
     */
    private void coursesList() {

        List<Course> courses = clientThread.getDbConnection().getCourses();

        Packet coursesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.COURSES.getConfirmation())
                .addArgument("courses", courses)
                .build();

        sendPacketIO(coursesConfirmationPacket);

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Add course to database, and send confirmation or error packet to client
     */
    private void addCourse() {

        LinkedTreeMap courseMap = (LinkedTreeMap) lastPacket.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientThread.getDbConnection().addCourse(course)) {

            coursesList();

        } else {

            Packet addCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDCOURSE.getError())
                    .build();

            sendPacketIO(addCourseErrorPacket);

        }

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Update course from database, and send confirmation or error packet to client
     */
    private void updateCourse() {

        LinkedTreeMap courseMap = (LinkedTreeMap) lastPacket.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientThread.getDbConnection().updateCourse(course)) {

            coursesList();

        } else {

            Packet updateCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATECOURSE.getError())
                    .build();

            sendPacketIO(updateCourseErrorPacket);

        }

    }

    /**
     * Get course data from packet
     * Parse course data and return course object
     * Remove course from database, and send confirmation or error packet to client
     */
    private void removeCourse() {

        LinkedTreeMap courseMap = (LinkedTreeMap) lastPacket.getArgument("course");

        Course course = Course.parse(courseMap);

        if(clientThread.getDbConnection().removeCourse(course)) {

            coursesList();

        } else {

            Packet removeCourseErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVECOURSE.getError())
                    .build();

            sendPacketIO(removeCourseErrorPacket);

        }

    }


    /**
     * Get subjects list from database
     * Send subjects list packet to client
     */
    private void subjectsList() {

        List<Subject> subjects = clientThread.getDbConnection().getSubjects();

        Packet coursesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.SUBJECTS.getConfirmation())
                .addArgument("subjects", subjects)
                .build();

        sendPacketIO(coursesConfirmationPacket);

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Add subject to database, and send confirmation or error packet to client
     */
    private void addSubject() {

        LinkedTreeMap subjectMap = (LinkedTreeMap) lastPacket.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientThread.getDbConnection().addSubject(subject)) {

            subjectsList();

        } else {

            Packet addSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSUBJECT.getError())
                    .build();

            sendPacketIO(addSubjectErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Update subject from database, and send confirmation or error packet to client
     */
    private void updateSubject() {

        LinkedTreeMap subjectMap = (LinkedTreeMap) lastPacket.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientThread.getDbConnection().updateSubject(subject)) {

            subjectsList();

        } else {

            Packet updateSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESUBJECT.getError())
                    .build();

            sendPacketIO(updateSubjectErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Remove subject from database, and send confirmation or error packet to client
     */
    private void removeSubject() {

        LinkedTreeMap subjectMap = (LinkedTreeMap) lastPacket.getArgument("subject");

        Subject subject = Subject.parse(subjectMap);

        if(clientThread.getDbConnection().removeSubject(subject)) {

            subjectsList();

        } else {

            Packet removeSubjectErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESUBJECT.getError())
                    .build();

            sendPacketIO(removeSubjectErrorPacket);

        }

    }


    /**
     * Get groups list from database
     * Send groups list packet to client
     */
    private void groupsList() {

        List<Group> groups = clientThread.getDbConnection().getGroups();

        Packet groupsConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.GROUPS.getConfirmation())
                .addArgument("groups", groups)
                .build();

        sendPacketIO(groupsConfirmationPacket);

    }

    /**
     * Get group data from packet
     * Parse group data and return group object
     * Add group to database, and send confirmation or error packet to client
     */
    private void addGroup() {

        LinkedTreeMap groupMap = (LinkedTreeMap) lastPacket.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientThread.getDbConnection().addGroup(group)) {

            groupsList();

        } else {

            Packet addGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDGROUP.getError())
                    .build();

            sendPacketIO(addGroupErrorPacket);

        }

    }

    /**
     * Get group data from packet
     * Parse group data and return group object
     * Update group from database, and send confirmation or error packet to client
     */
    private void updateGroup() {

        LinkedTreeMap groupMap = (LinkedTreeMap) lastPacket.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientThread.getDbConnection().updateGroup(group)) {

            groupsList();

        } else {

            Packet updateGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEGROUP.getError())
                    .build();

            sendPacketIO(updateGroupErrorPacket);

        }

    }

    /**
     * Get subject data from packet
     * Parse subject data and return subject object
     * Remove subject from database, and send confirmation or error packet to client
     */
    private void removeGroup() {

        LinkedTreeMap groupMap = (LinkedTreeMap) lastPacket.getArgument("group");

        Group group = Group.parse(groupMap);

        if(clientThread.getDbConnection().removeGroup(group)) {

            groupsList();

        } else {

            Packet removeGroupErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVEGROUP.getError())
                    .build();

            sendPacketIO(removeGroupErrorPacket);

        }

    }


    /**
     * Get days list from database
     * Send days list packet to client
     */
    private void daysList() {

        List<Day> days = clientThread.getDbConnection().getDays();

        Packet daysConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.DAYS.getConfirmation())
                .addArgument("days", days)
                .build();

        sendPacketIO(daysConfirmationPacket);

    }

    /**
     * Get day data from packet
     * Parse day data and return day object
     * Update day from database, and send confirmation or error packet to client
     */
    private void updateDay() {

        LinkedTreeMap dayMap = (LinkedTreeMap) lastPacket.getArgument("day");

        Day day = Day.parse(dayMap);

        if(clientThread.getDbConnection().updateDay(day)) {

            daysList();

        } else {

            Packet updateDayErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEDAY.getError())
                    .build();

            sendPacketIO(updateDayErrorPacket);

        }

    }


    /**
     * Get hours list from database
     * Send hours list packet to client
     */
    private void hoursList() {

        List<Hour> hours = clientThread.getDbConnection().getHours();

        Packet hoursConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.HOURS.getConfirmation())
                .addArgument("hours", hours)
                .build();

        sendPacketIO(hoursConfirmationPacket);

    }

    /**
     * Get hour data from packet
     * Parse hour data and return hour object
     * Update hour from database, and send confirmation or error packet to client
     */
    private void updateHour() {

        LinkedTreeMap hourMap = (LinkedTreeMap) lastPacket.getArgument("hour");

        Hour hour = Hour.parse(hourMap);

        if(clientThread.getDbConnection().updateHour(hour)) {

            hoursList();

        } else {

            Packet updateHourErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEHOUR.getError())
                    .build();

            sendPacketIO(updateHourErrorPacket);

        }

    }


    /**
     * Get timezone list from database
     * Send timezone list packet to client
     */
    private void timeZoneList() {

        List<TimeZone> timeZones = clientThread.getDbConnection().getTimeZones();

        Packet timeZonesConfirmationPacket = new PacketBuilder()
                .ofType(PacketType.TIMEZONES.getConfirmation())
                .addArgument("timeZones", timeZones)
                .build();

        sendPacketIO(timeZonesConfirmationPacket);

    }


    /**
     * Get schedule data from database
     * Search schedule by packet arguments
     */
    private void searchSchedule() {

        String searchType = (String) lastPacket.getArgument("type");

        ScheduleSearcheable scheduleSearcheable = null;

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.SCHEDULES.getConfirmation())
                .addArgument("searchType", searchType);

        if(searchType.equalsIgnoreCase("TEACHER")) {

            LinkedTreeMap teacherMap = (LinkedTreeMap) lastPacket.getArgument("item");

            Teacher teacher = Teacher.parse(teacherMap);

            packetBuilder.addArgument("searchQuery", teacher.getName());

            scheduleSearcheable = teacher;

        } else if(searchType.equalsIgnoreCase("GROUP")) {

            LinkedTreeMap groupMap = (LinkedTreeMap) lastPacket.getArgument("item");

            Group group = Group.parse(groupMap);

            packetBuilder.addArgument("searchQuery", group.toString());

            scheduleSearcheable = group;

        } else if(searchType.equalsIgnoreCase("CLASSROOM")) {

            LinkedTreeMap classroomMap = (LinkedTreeMap) lastPacket.getArgument("item");

            Classroom classroom = Classroom.parse(classroomMap);

            packetBuilder.addArgument("searchQuery", classroom.getName());

            scheduleSearcheable = classroom;

        }

        if(scheduleSearcheable != null) {

            List<SchedulerItem> schedules = clientThread.getDbConnection().searchSchedule(scheduleSearcheable);

            packetBuilder.addArgument("schedules", schedules);

            sendPacketIO(packetBuilder.build());

        }

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Insert schedule item to database, and send confirmation or error packet to client
     */
    private void insertScheduleItem() {

        LinkedTreeMap scheduleMap = (LinkedTreeMap) lastPacket.getArgument("scheduleItem");

        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

        if(schedulerItem == null)
            return;

        int inserted = 1;
        int required = schedulerItem.getScheduleList().size();

        for(Schedule schedule : schedulerItem.getScheduleList()) {
            if(clientThread.getDbConnection().insertSchedule(schedule)) {
                inserted++;
            }
        }

        if(inserted > required) {

            Packet insertScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.INSERTSCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            sendPacketIO(insertScheduleConfirmationPacket);

        } else {

            Packet insertScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.INSERTSCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido insertar ese schedule")
                    .build();

            sendPacketIO(insertScheduleErrorPacket);

        }

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Switch schedule items from database, and send confirmation or error packet to client
     */
    private void switchScheduleItems() {

        LinkedTreeMap schedule1Map = (LinkedTreeMap) lastPacket.getArgument("scheduleItem1");
        SchedulerItem schedule1 = SchedulerItem.parse(schedule1Map);

        LinkedTreeMap schedule2Map = (LinkedTreeMap) lastPacket.getArgument("scheduleItem2");
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
            clientThread.getDbConnection().removeSchedule(schedule);
        }

        for(Schedule schedule : schedule1.getScheduleList()) {
            schedule.setTimeZone(timezone2);
            if(clientThread.getDbConnection().insertSchedule(schedule))
                updated++;
        }

        for(Schedule schedule : schedule2.getScheduleList()) {
            schedule.setTimeZone(timezone1);
            if(clientThread.getDbConnection().insertSchedule(schedule))
                updated++;
        }

        if(updated >= updatedRequired) {

            Packet switchScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.SWITCHSCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedule1.getUuid() + "|" + schedule2.getUuid())
                    .addArgument("scheduleItem1", schedule1)
                    .addArgument("scheduleItem2", schedule2)
                    .build();

            sendPacketIO(switchScheduleConfirmationPacket);

        } else {

            Packet insertScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.SWITCHSCHEDULEITEM.getError())
                    .addArgument("uuid", schedule1.getUuid() + "|" + schedule2.getUuid())
                    .addArgument("message", "No se ha podido insertar ese schedule")
                    .build();

            sendPacketIO(insertScheduleErrorPacket);

        }

    }

    /**
     * Get schedule item data from packet
     * Parse schedule data and return schedule object
     * Remove schedule item from database, and send confirmation or error packet to client
     */
    private void removeScheduleItem() {

        LinkedTreeMap scheduleMap = (LinkedTreeMap) lastPacket.getArgument("scheduleItem");

        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

        if(schedulerItem == null)
            return;

        int removed = 1;
        int required = schedulerItem.getScheduleList().size();

        for(Schedule schedule : schedulerItem.getScheduleList()) {
            if(clientThread.getDbConnection().removeSchedule(schedule)) {
                removed++;
            }
        }

        if(removed > required) {

            Packet deleteScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            sendPacketIO(deleteScheduleConfirmationPacket);

        } else {

            Packet deleteScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido eliminar ese schedule")
                    .build();

            sendPacketIO(deleteScheduleErrorPacket);

        }

    }


    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Add schedule to database, and send confirmation or error packet to client
     */
    private void addSchedule() {

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) lastPacket.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) lastPacket.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientThread.getDbConnection().insertSchedule(schedule)) {

            Packet addScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            sendPacketIO(addScheduleConfirmationPacket);

        } else {

            Packet addScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSCHEDULE.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido editar ese schedule")
                    .build();

            sendPacketIO(addScheduleErrorPacket);

        }

    }

    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Update schedule from database, and send confirmation or error packet to client
     */
    private void updateSchedule() {

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) lastPacket.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) lastPacket.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientThread.getDbConnection().updateSchedule(schedule)) {

            Packet updateScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            sendPacketIO(updateScheduleConfirmationPacket);

        } else {

            Packet updateScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESCHEDULE.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido editar ese schedule")
                    .build();

            sendPacketIO(updateScheduleErrorPacket);

        }

    }

    /**
     * Get schedule data from packet
     * Parse schedule data and return schedule object
     * Remove schedule from database, and send confirmation or error packet to client
     */
    private void deleteSchedule() {

        LinkedTreeMap scheduleItemMap = (LinkedTreeMap) lastPacket.getArgument("scheduleItem");
        SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

        LinkedTreeMap scheduleMap = (LinkedTreeMap) lastPacket.getArgument("schedule");
        Schedule schedule = Schedule.parse(scheduleMap);

        if(schedulerItem == null || schedule == null)
            return;

        if(clientThread.getDbConnection().removeSchedule(schedule)) {

            Packet deleteScheduleConfirmationPacket = new PacketBuilder()
                    .ofType(PacketType.DELETESCHEDULE.getConfirmation())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            sendPacketIO(deleteScheduleConfirmationPacket);

        } else {

            Packet deleteScheduleErrorPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getError())
                    .addArgument("uuid", schedulerItem.getUuid())
                    .addArgument("message", "No se ha podido eliminar ese schedule")
                    .build();

            sendPacketIO(deleteScheduleErrorPacket);

        }

    }


    /**
     * Send packet to client's socket output
     * @param packet
     */
    public void sendPacketIO(Packet packet) {
        try {
            clientThread.getOutput().write(packet.toString());
            clientThread.getOutput().newLine();
            clientThread.getOutput().flush();
        } catch (IOException e) {
            clientThread.setConnected(false);
        }
    }

    /**
     * Read packet from server thread, client's socket input
     * @return packet object parsed by json input
     */
    public Packet readPacketIO() {
        String json = null;
        try {
            json = clientThread.getInput().readLine();
            return Server.GSON.fromJson(json, Packet.class);
        } catch (JsonSyntaxException ignored) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            System.out.println(json);
        } catch (IOException e) {
            clientThread.setConnected(false);
        }
        return null;
    }

}
