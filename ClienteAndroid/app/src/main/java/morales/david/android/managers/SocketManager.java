package morales.david.android.managers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import morales.david.android.R;
import morales.david.android.activities.DashboardActivity;
import morales.david.android.activities.DisconnectedActivity;
import morales.david.android.models.Classroom;
import morales.david.android.models.ClientSession;
import morales.david.android.models.Course;
import morales.david.android.models.Credential;
import morales.david.android.models.Day;
import morales.david.android.models.Hour;
import morales.david.android.models.Subject;
import morales.david.android.models.Teacher;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.Constants;

public class SocketManager extends Thread {

    private static SocketManager INSTANCE = null;

    public static SocketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new SocketManager();
        return INSTANCE;
    }

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private Packet receivedPacket;

    private ClientSession clientSession;

    private final boolean[] closed = { false };
    private boolean opened = false;

    public SocketManager() {

        this.clientSession = new ClientSession();

    }

    public void openSocket() {

        try {

            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            opened = true;

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void close() {

        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        openSocket();

        Packet pingPacket = new PacketBuilder().ofType(PacketType.PING.getRequest()).build();
        sendPacketIO(pingPacket);

        while(!closed[0]) {

            try {

                if (input != null && input.ready()) {

                    receivedPacket = readPacketIO();

                    PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(receivedPacket.getType()));

                    Activity context = ScreenManager.getInstance().getActivity();

                    switch (packetType) {

                        case LOGIN: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.getConfirmation())) {

                                clientSession.setId(((Double) receivedPacket.getArgument("id")).intValue());
                                clientSession.setName((String) receivedPacket.getArgument("name"));
                                clientSession.setRole((String) receivedPacket.getArgument("role"));

                                context.runOnUiThread(() -> {
                                    Intent intent = new Intent(context, DashboardActivity.class);
                                    context.startActivity(intent);
                                });

                                DataManager.getInstance().getTeachers();
                                DataManager.getInstance().getClassrooms();
                                DataManager.getInstance().getSubjects();
                                DataManager.getInstance().getCourses();
                                DataManager.getInstance().getCredentials();
                                DataManager.getInstance().getDays();
                                DataManager.getInstance().getHours();
                                sendPackets();

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.LOGIN.getError())) {

                                context.runOnUiThread(() -> {
                                    Toast.makeText(context, context.getString(R.string.act_login_message_error_credentials), Toast.LENGTH_SHORT).show();
                                });

                            }

                            break;
                        }

                        case EXIT: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.EXIT.getConfirmation())) {

                                close();
                                opened = false;

                                context.runOnUiThread(() -> {
                                    Intent intent = new Intent(context, DisconnectedActivity.class);
                                    context.startActivity(intent);
                                });

                                try {
                                    sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                closed[0] = true;

                                context.finishAffinity();
                                System.exit(0);

                            }

                            break;

                        }

                        case TEACHERS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.TEACHERS.getConfirmation())) {

                                List<LinkedTreeMap> teachers = (List<LinkedTreeMap>) receivedPacket.getArgument("teachers");

                                final List<Teacher> temp = new ArrayList<>();

                                for (LinkedTreeMap teacherMap : teachers)
                                    temp.add(Teacher.parse(teacherMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setTeachers(temp);
                                });

                            }

                            break;

                        }

                        case CREDENTIALS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.CREDENTIALS.getConfirmation())) {

                                List<LinkedTreeMap> credentials = (List<LinkedTreeMap>) receivedPacket.getArgument("credentials");

                                final List<Credential> temp = new ArrayList<>();

                                for (LinkedTreeMap credentialMap : credentials)
                                    temp.add(Credential.parse(credentialMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setCredentials(temp);
                                });

                            }

                            break;

                        }

                        case CLASSROOMS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.CLASSROOMS.getConfirmation())) {

                                List<LinkedTreeMap> classrooms = (List<LinkedTreeMap>) receivedPacket.getArgument("classrooms");

                                final List<Classroom> temp = new ArrayList<>();

                                for (LinkedTreeMap classroomMap : classrooms)
                                    temp.add(Classroom.parse(classroomMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setClassrooms(temp);
                                });

                            }

                            break;

                        }

                        case COURSES: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.COURSES.getConfirmation())) {

                                List<LinkedTreeMap> courses = (List<LinkedTreeMap>) receivedPacket.getArgument("courses");

                                final List<Course> temp = new ArrayList<>();

                                for (LinkedTreeMap courseMap : courses)
                                    temp.add(Course.parse(courseMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setCourses(temp);
                                });

                            }

                            break;

                        }

                        case SUBJECTS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.SUBJECTS.getConfirmation())) {

                                List<LinkedTreeMap> subjects = (List<LinkedTreeMap>) receivedPacket.getArgument("subjects");

                                final List<Subject> temp = new ArrayList<>();

                                for (LinkedTreeMap subjectMap : subjects)
                                    temp.add(Subject.parse(subjectMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setSubjects(temp);
                                });

                            }

                            break;

                        }

                        case DAYS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.DAYS.getConfirmation())) {

                                List<LinkedTreeMap> days = (List<LinkedTreeMap>) receivedPacket.getArgument("days");

                                final List<Day> temp = new ArrayList<>();

                                for (LinkedTreeMap dayMap : days)
                                    temp.add(Day.parse(dayMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setDays(temp);
                                });

                            }

                            break;

                        }

                        case HOURS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.HOURS.getConfirmation())) {

                                List<LinkedTreeMap> hours = (List<LinkedTreeMap>) receivedPacket.getArgument("hours");

                                final List<Hour> temp = new ArrayList<>();

                                for (LinkedTreeMap hourMap : hours)
                                    temp.add(Hour.parse(hourMap));

                                context.runOnUiThread(() -> {
                                    DataManager.getInstance().setHours(temp);
                                });

                            }

                            break;

                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public boolean isOpened() {
        return opened;
    }

    public Socket getSocket() { return socket; }

    public ClientSession getClientSession() { return clientSession; }

    public void closeSocket() {

        Packet exitPacket = new PacketBuilder()
                .ofType(PacketType.EXIT.getRequest())
                .build();

        sendPacketIO(exitPacket);

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        closed[0] = true;

        close();

    }

    private void sendPackets() {

        new Thread(() -> {

            for(PacketType packetType : Constants.INIT_PACKETS) {

                Packet requestPacket = new PacketBuilder().ofType(packetType.getRequest()).build();
                SocketManager.getInstance().sendPacketIO(requestPacket);

                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    // Methods to send data in socket I/O's
    public void sendPacketIO(Packet packet) {
        new Thread(() -> {
            try {
                output.write(packet.toString());
                output.newLine();
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Packet readPacketIO() {
        try {
            String json = input.readLine();
            return PacketBuilder.GSON.fromJson(json, Packet.class);
        } catch (IOException e) {
            closeSocket();
        }
        return null;
    }

}
