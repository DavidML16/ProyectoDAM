package morales.david.server.clients;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.server.Server;
import morales.david.server.models.Packet;
import morales.david.server.models.PacketBuilder;
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

        }

    }

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

    private void teachersList() {

        List<Teacher> teachers = clientThread.getDbConnection().getTeachers();

        Packet teachersConfirmationPacket = new PacketBuilder()
                .ofType(Constants.CONFIRMATION_TEACHERS)
                .addArgument("teachers", teachers)
                .build();

        sendPacketIO(teachersConfirmationPacket);

    }

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

    // Methods to send data in socket I/O's
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
