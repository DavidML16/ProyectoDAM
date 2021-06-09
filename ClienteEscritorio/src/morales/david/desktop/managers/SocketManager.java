package morales.david.desktop.managers;

import com.google.gson.internal.LinkedTreeMap;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import morales.david.desktop.Client;
import morales.david.desktop.controllers.ImportController;
import morales.david.desktop.controllers.LoginController;
import morales.david.desktop.managers.eventcallbacks.*;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class SocketManager extends Thread {

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

    public SocketManager() {

        openSocket();

        this.clientSession = new ClientSession();

    }

    public void openSocket() {

        try {

            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {

            socketErrorAlert();

        }

    }

    private void close() {

        try {
            if(output != null)
                output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(input != null)
                input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        ScreenManager screenManager = ScreenManager.getInstance();

        while(!closed[0]) {

            try {

                if (input != null && input.ready()) {

                    receivedPacket = readPacketIO();

                    PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(receivedPacket.getType()));

                    switch (packetType) {

                        case LOGIN: {

                            Platform.runLater(() -> {

                                if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.getConfirmation())) {

                                    clientSession.setId(((Double) receivedPacket.getArgument("id")).intValue());
                                    clientSession.setName((String) receivedPacket.getArgument("name"));
                                    clientSession.setRole((String) receivedPacket.getArgument("role"));

                                    if (screenManager.getController() instanceof LoginController) {

                                        screenManager.openScene("dashboard.fxml", "Inicio" + Constants.WINDOW_TITLE);

                                    }

                                } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.LOGIN.getError())) {

                                    if (screenManager.getController() instanceof LoginController) {

                                        LoginController loginController = (LoginController) screenManager.getController();

                                        loginController.getMessageLabel().setText(Constants.MESSAGES_ERROR_LOGIN);
                                        loginController.getMessageLabel().setTextFill(Color.TOMATO);

                                    }

                                }

                                });

                            break;

                        }

                        case DISCONNECT: {

                            Platform.runLater(() -> {

                                if (receivedPacket.getType().equalsIgnoreCase(PacketType.DISCONNECT.getConfirmation())) {

                                    clientSession.setId(-1);
                                    clientSession.setName("");
                                    clientSession.setRole("");

                                    DataManager.getInstance().getHours().clear();
                                    DataManager.getInstance().getDays().clear();
                                    DataManager.getInstance().getTimeZones().clear();
                                    DataManager.getInstance().getTeachers().clear();
                                    DataManager.getInstance().getCredentials().clear();
                                    DataManager.getInstance().getCourses().clear();
                                    DataManager.getInstance().getGroups().clear();
                                    DataManager.getInstance().getSubjects().clear();
                                    DataManager.getInstance().getClassrooms().clear();
                                    DataManager.getInstance().getSubjects().clear();

                                    Constants.FIRST_HOME_VIEW = true;

                                    if (!(screenManager.getController() instanceof LoginController)) {

                                        openSocket();

                                        screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

                                    }

                                }

                            });

                            break;

                        }

                        case EXIT: {

                            Platform.runLater(() -> {

                                if (receivedPacket.getType().equalsIgnoreCase(PacketType.EXIT.getConfirmation())) {

                                    closed[0] = true;
                                    close();
                                    Platform.exit();

                                }

                            });

                            break;

                        }

                        case IMPORTSTATUS: {

                            Platform.runLater(() -> {

                                if (receivedPacket.getType().equalsIgnoreCase(PacketType.IMPORTSTATUS.getConfirmation())) {

                                    final boolean importing = (boolean) receivedPacket.getArgument("importing");
                                    final String message = (String) receivedPacket.getArgument("message");
                                    final String type = (String) receivedPacket.getArgument("type");

                                    if (screenManager.getController() instanceof ImportController) {

                                        ImportController importController = (ImportController) screenManager.getController();

                                        importController.updateStatus(importing, message, type);

                                    }

                                }

                            });

                            break;

                        }

                        case TEACHERS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.TEACHERS.getConfirmation())) {

                                List<LinkedTreeMap> teachers = (List<LinkedTreeMap>) receivedPacket.getArgument("teachers");

                                DataManager.getInstance().getTeachers().clear();

                                for (LinkedTreeMap teacherMap : teachers)
                                    DataManager.getInstance().getTeachers().add(Teacher.parse(teacherMap));

                            }

                            break;

                        }

                        case CREDENTIALS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.CREDENTIALS.getConfirmation())) {

                                List<LinkedTreeMap> credentials = (List<LinkedTreeMap>) receivedPacket.getArgument("credentials");

                                DataManager.getInstance().getCredentials().clear();

                                for (LinkedTreeMap credentialMap : credentials)
                                    DataManager.getInstance().getCredentials().add(Credential.parse(credentialMap));

                            }

                            break;

                        }

                        case CLASSROOMS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.CLASSROOMS.getConfirmation())) {

                                List<LinkedTreeMap> classrooms = (List<LinkedTreeMap>) receivedPacket.getArgument("classrooms");

                                DataManager.getInstance().getClassrooms().clear();

                                for (LinkedTreeMap classroomMap : classrooms)
                                    DataManager.getInstance().getClassrooms().add(Classroom.parse(classroomMap));

                            }

                            break;

                        }

                        case COURSES: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.COURSES.getConfirmation())) {

                                List<LinkedTreeMap> courses = (List<LinkedTreeMap>) receivedPacket.getArgument("courses");

                                DataManager.getInstance().getCourses().clear();

                                for (LinkedTreeMap courseMap : courses)
                                    DataManager.getInstance().getCourses().add(Course.parse(courseMap));

                            }

                            break;

                        }

                        case GROUPS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.GROUPS.getConfirmation())) {

                                List<LinkedTreeMap> groups = (List<LinkedTreeMap>) receivedPacket.getArgument("groups");

                                DataManager.getInstance().getGroups().clear();

                                for (LinkedTreeMap groupMap : groups)
                                    DataManager.getInstance().getGroups().add(Group.parse(groupMap));

                            }

                            break;

                        }

                        case SUBJECTS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.SUBJECTS.getConfirmation())) {

                                List<LinkedTreeMap> subjects = (List<LinkedTreeMap>) receivedPacket.getArgument("subjects");

                                DataManager.getInstance().getSubjects().clear();

                                for (LinkedTreeMap subjectMap : subjects)
                                    DataManager.getInstance().getSubjects().add(Subject.parse(subjectMap));

                            }

                            break;

                        }

                        case DAYS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.DAYS.getConfirmation())) {

                                List<LinkedTreeMap> days = (List<LinkedTreeMap>) receivedPacket.getArgument("days");

                                DataManager.getInstance().getDays().clear();

                                for (LinkedTreeMap dayMap : days)
                                    DataManager.getInstance().getDays().add(Day.parse(dayMap));

                            }

                            break;

                        }

                        case HOURS: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.HOURS.getConfirmation())) {

                                List<LinkedTreeMap> hours = (List<LinkedTreeMap>) receivedPacket.getArgument("hours");

                                DataManager.getInstance().getHours().clear();

                                for (LinkedTreeMap hourMap : hours)
                                    DataManager.getInstance().getHours().add(Hour.parse(hourMap));

                            }

                            break;

                        }

                        case TIMEZONES: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.TIMEZONES.getConfirmation())) {

                                List<LinkedTreeMap> timeZones = (List<LinkedTreeMap>) receivedPacket.getArgument("timeZones");

                                DataManager.getInstance().getTimeZones().clear();

                                for (LinkedTreeMap timeZoneMap : timeZones)
                                    DataManager.getInstance().getTimeZones().add(TimeZone.parse(timeZoneMap));

                            }

                            break;

                        }

                        case SCHEDULES: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.SCHEDULES.getConfirmation())) {

                                Double schedules = (Double) receivedPacket.getArgument("schedules");

                                DataManager.getInstance().getSchedules().clear();
                                DataManager.getInstance().getSchedules().add(schedules.intValue());

                            }

                            break;

                        }

                        case SEARCHSCHEDULE: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.SEARCHSCHEDULE.getConfirmation())) {

                                List<LinkedTreeMap> schedules = (List<LinkedTreeMap>) receivedPacket.getArgument("schedules");

                                String callback = (String) receivedPacket.getArgument("callback");
                                String searchType = (String) receivedPacket.getArgument("searchType");
                                String searchQuery = (String) receivedPacket.getArgument("searchQuery");

                                final List<SchedulerItem> scheduleList = new ArrayList<>();

                                for (LinkedTreeMap scheduleMap : schedules)
                                    scheduleList.add(SchedulerItem.parse(scheduleMap));

                                if(callback.equalsIgnoreCase("SEARCH"))
                                    Platform.runLater(() -> {
                                        ScreenManager.getInstance().openScheduleView(scheduleList, searchType, searchQuery);
                                    });
                                else if(callback.equalsIgnoreCase("EXPORT"))
                                    Platform.runLater(() -> {
                                        try {
                                            ExportManager.getInstance().exportSchedule(scheduleList, searchType, searchQuery);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                    });

                            }

                            break;

                        }

                        case INSERTSCHEDULEITEM: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.INSERTSCHEDULEITEM.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                LinkedTreeMap scheduleMap = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem");

                                SchedulerItem schedule = SchedulerItem.parse(scheduleMap);

                                if (schedule != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleConfirmationListener(uuid, schedule));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.INSERTSCHEDULEITEM.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case SWITCHSCHEDULEITEM: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.SWITCHSCHEDULEITEM.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");

                                LinkedTreeMap schedule1Map = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem1");
                                SchedulerItem schedule1 = SchedulerItem.parse(schedule1Map);

                                LinkedTreeMap schedule2Map = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem2");
                                SchedulerItem schedule2 = SchedulerItem.parse(schedule2Map);

                                if (schedule1 != null && schedule2 != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleSwitchConfirmationListener(uuid, schedule1, schedule2));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.SWITCHSCHEDULEITEM.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case REMOVESCHEDULEITEM: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.REMOVESCHEDULEITEM.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                LinkedTreeMap scheduleMap = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem");

                                SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

                                if (schedulerItem != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleConfirmationListener(uuid, schedulerItem));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.REMOVESCHEDULEITEM.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case ADDSCHEDULE: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.ADDSCHEDULE.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");

                                LinkedTreeMap scheduleItemMap = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem");
                                SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

                                LinkedTreeMap scheduleMap = (LinkedTreeMap) receivedPacket.getArgument("schedule");
                                Schedule schedule = Schedule.parse(scheduleMap);

                                if (schedulerItem != null && schedule != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleConfirmationListener(uuid, schedulerItem));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.ADDSCHEDULE.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case UPDATESCHEDULE: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.UPDATESCHEDULE.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");

                                LinkedTreeMap scheduleItemMap = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem");
                                SchedulerItem schedulerItem = SchedulerItem.parse(scheduleItemMap);

                                LinkedTreeMap scheduleMap = (LinkedTreeMap) receivedPacket.getArgument("schedule");
                                Schedule schedule = Schedule.parse(scheduleMap);

                                if (schedulerItem != null && schedule != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleConfirmationListener(uuid, schedulerItem));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.UPDATESCHEDULE.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case DELETESCHEDULE: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.DELETESCHEDULE.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                LinkedTreeMap scheduleMap = (LinkedTreeMap) receivedPacket.getArgument("scheduleItem");

                                SchedulerItem schedulerItem = SchedulerItem.parse(scheduleMap);

                                if (schedulerItem != null)
                                    EventManager.getInstance().notify(uuid, new ScheduleConfirmationListener(uuid, schedulerItem));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.DELETESCHEDULE.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

                            }

                            break;

                        }

                        case EMPTYCLASSROOMSTIMEZONE: {

                            if(receivedPacket.getType().equalsIgnoreCase(PacketType.EMPTYCLASSROOMSTIMEZONE.getConfirmation())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");

                                List<LinkedTreeMap> classrooms = (List<LinkedTreeMap>) receivedPacket.getArgument("classrooms");

                                List<Classroom> emptyClassrooms = new ArrayList<>();

                                for (LinkedTreeMap classroomMap : classrooms)
                                    emptyClassrooms.add(Classroom.parse(classroomMap));

                                EventManager.getInstance().notify(uuid, new EmptyClassroomsConfirmationListener(uuid, emptyClassrooms));

                            } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.EMPTYCLASSROOMSTIMEZONE.getError())) {

                                String uuid = (String) receivedPacket.getArgument("uuid");
                                String message = (String) receivedPacket.getArgument("message");

                                EventManager.getInstance().notify(uuid, new ScheduleErrorListener(uuid, message));

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

    public Socket getSocket() { return socket; }

    public ClientSession getClientSession() { return clientSession; }

    public void socketErrorAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error con la conexión");
        alert.setHeaderText(null);
        alert.setContentText("Ha ocurrido un error con la conexión con el servidor.\nCerrando la aplicación");
        alert.showAndWait();

        closed[0] = true;
        close();
        Platform.exit();

    }

    // Methods to send data in socket I/O's
    public void sendPacketIO(Packet packet) {
        try {
            output.write(packet.toString());
            output.newLine();
            output.flush();
        } catch (IOException | NullPointerException e) {
            Platform.runLater(() -> {
                socketErrorAlert();
            });
        }
    }

    public Packet readPacketIO() {
        try {
            String json = input.readLine();
            return Client.GSON.fromJson(json, Packet.class);
        } catch (IOException e) {
            Platform.runLater(() -> {
                socketErrorAlert();
            });
        }
        return null;
    }

}
