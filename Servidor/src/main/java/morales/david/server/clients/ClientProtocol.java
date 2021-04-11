package morales.david.server.clients;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.Server;
import morales.david.server.models.Classroom;
import morales.david.server.models.Course;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.Teacher;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.*;
import java.net.SocketException;
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

        switch (lastPacket.getType()) {

            case Constants.REQUEST_LOGIN:
                login();
                break;

            case Constants.REQUEST_DISCONNECT:
                disconnect();
                break;

            case Constants.REQUEST_SENDACCESSFILE:
                receiveFile();
                break;

            case Constants.REQUEST_TEACHERS:
                teachersList();
                break;

            case Constants.REQUEST_ADDTEACHER:
                addTeacher();
                break;

            case Constants.REQUEST_UPDATETEACHER:
                updateTeacher();
                break;

            case Constants.REQUEST_REMOVETEACHER:
                removeTeacher();
                break;

            case Constants.REQUEST_CLASSROOMS:
                classroomsList();
                break;

            case Constants.REQUEST_ADDCLASSROOM:
                addClassroom();
                break;

            case Constants.REQUEST_UPDATECLASSROOM:
                updateClassroom();
                break;

            case Constants.REQUEST_REMOVECLASSROOM:
                removeClassroom();
                break;

            case Constants.REQUEST_COURSES:
                coursesList();
                break;

            case Constants.REQUEST_ADDCOURSE:
                addCourse();
                break;

            case Constants.REQUEST_UPDATECOURSE:
                updateCourse();
                break;

            case Constants.REQUEST_REMOVECOURSE:
                removeCourse();
                break;

        }

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
                    .ofType(Constants.CONFIRMATION_LOGIN)
                    .addArgument("id", clientSession.getId())
                    .addArgument("name", clientSession.getName())
                    .addArgument("role", clientSession.getRole())
                    .build();

            sendPacketIO(loginConfirmationPacket);

            logged = true;

        } else {

            Packet loginErrorPacket = new PacketBuilder()
                    .ofType(Constants.ERROR_LOGIN)
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
                    .ofType(Constants.CONFIRMATION_DISCONNECT)
                    .build();

            sendPacketIO(disconnectConfirmationPacket);
            clientThread.setConnected(false);

        } else {

            Packet disconnectErrorPacket = new PacketBuilder()
                    .ofType(Constants.ERROR_DISCONNECT)
                    .build();

            sendPacketIO(disconnectErrorPacket);

        }

    }

    /**
     * Receive file byte data from client's socket input
     */
    private void receiveFile() {

        long fileSize = ((Double)lastPacket.getArgument("size")).longValue();
        String fileName = (String) lastPacket.getArgument("name");

        Packet sendErrorPacket = new PacketBuilder()
                .ofType(Constants.CONFIRMATION_SENDACCESSFILE)
                .build();

        int bytes = 0;
        FileOutputStream fileOutputStream = null;
        try {

            File directory = new File("files");
            if(!directory.exists())
                directory.mkdir();

            fileOutputStream = new FileOutputStream("files/" + fileName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            sendPacketIO(sendErrorPacket);
            return;
        }

        DataInputStream dataInputStream = null;
        try {

            dataInputStream = new DataInputStream(clientThread.getSocket().getInputStream());

            byte[] buffer = new byte[4*1024];
            while (fileSize > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
                fileOutputStream.write(buffer,0,bytes);
                fileSize -= bytes;
            }

            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            sendPacketIO(sendErrorPacket);
            return;
        }

        try {
            int available = dataInputStream.available();
            dataInputStream.skip(available);
        } catch (IOException e) {
            e.printStackTrace();
            sendPacketIO(sendErrorPacket);
            return;
        }

        clientThread.openIO();

        Packet sendConfirmationPacket = new PacketBuilder()
                .ofType(Constants.CONFIRMATION_SENDACCESSFILE)
                .build();

        sendPacketIO(sendConfirmationPacket);

        File dbfile = new File("files/" + fileName);
        if(dbfile.exists() && !clientThread.getServer().getImportManager().isImporting()) {
            clientThread.getServer().getImportManager().setFile(dbfile);
            clientThread.getServer().getImportManager().importDatabase();
        }

    }


    /**
     * Get teachers list from database
     * Send teachers list packet to client
     */
    private void teachersList() {

        List<Teacher> teachers = clientThread.getDbConnection().getTeachers();

        Packet teachersConfirmationPacket = new PacketBuilder()
                .ofType(Constants.CONFIRMATION_TEACHERS)
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
                    .ofType(Constants.ERROR_ADDTEACHER)
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
                    .ofType(Constants.ERROR_UPDATETEACHER)
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
                    .ofType(Constants.ERROR_REMOVETEACHER)
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
                .ofType(Constants.CONFIRMATION_CLASSROOMS)
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
                    .ofType(Constants.ERROR_ADDCLASSROOM)
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
                    .ofType(Constants.ERROR_UPDATECLASSROOM)
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
                    .ofType(Constants.ERROR_REMOVECLASSROOM)
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
                .ofType(Constants.CONFIRMATION_COURSES)
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
                    .ofType(Constants.ERROR_ADDCOURSE)
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
                    .ofType(Constants.ERROR_UPDATECOURSE)
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
                    .ofType(Constants.ERROR_REMOVECOURSE)
                    .build();

            sendPacketIO(removeCourseErrorPacket);

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
            System.out.println(Constants.LOG_SERVER_ERROR_IO_SEND);
            e.printStackTrace();
        }
    }

    /**
     * Read packet from server thread, client's socket input
     * @return packet object parsed by json input
     */
    public Packet readPacketIO() {
        try {
            String json = clientThread.getInput().readLine();
            return Server.GSON.fromJson(json, Packet.class);
        } catch (SocketException e) {
            clientThread.setConnected(false);
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
