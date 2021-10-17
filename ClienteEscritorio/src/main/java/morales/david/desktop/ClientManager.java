package morales.david.desktop;

import com.google.gson.internal.LinkedTreeMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import morales.david.desktop.controllers.LoginController;
import morales.david.desktop.controllers.options.BackupController;
import morales.david.desktop.controllers.options.ImportController;
import morales.david.desktop.managers.*;
import morales.david.desktop.managers.eventcallbacks.*;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public final class ClientManager {

    private static ClientManager INSTANCE = null;

    public static ClientManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ClientManager();
        return INSTANCE;
    }

    private Packet receivedPacket;

    private ClientSession clientSession;

    private List<Packet> pendingPackets;

    private Bootstrap bootstrap;
    private EventLoopGroup group;

    private Channel channel;

    private boolean closed;

    public ClientManager() {

        this.pendingPackets = new ArrayList<>();
        this.clientSession = new ClientSession();

        this.closed = true;

    }

    public void open() {

        group = new NioEventLoopGroup();

        try {

            bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            closed = false;

            channel = bootstrap.connect(Constants.SERVER_IP, Constants.SERVER_PORT).sync().channel();

        } catch (InterruptedException e) {

            e.printStackTrace();

        }

    }

    public void close() {

        if(group != null) {

            group.shutdownGracefully();
            group = null;

        }

        closed = true;
        INSTANCE = null;

    }

    public void processPacket(Packet packet) {

        ScreenManager screenManager = ScreenManager.getInstance();

        receivedPacket = packet;

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

                    } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.getError())) {

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

                            screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

                        }

                    }

                });

                break;

            }

            case EXIT: {

                Platform.runLater(() -> {

                    if (receivedPacket.getType().equalsIgnoreCase(PacketType.EXIT.getConfirmation())) {

                        close();

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
                                ExportSchedulerManager.getInstance().exportSchedule(scheduleList, searchType, searchQuery, true, null);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        });

                }

                break;

            }

            case ADVSCHEDULE: {

                if(receivedPacket.getType().equalsIgnoreCase(PacketType.ADVSCHEDULE.getConfirmation())) {

                    ExportableSchedule exportableSchedule = ExportableSchedule.parse(receivedPacket);

                    AdvSchedulerManager.getInstance().addExportableSchedule(exportableSchedule);

                }

                break;

            }

            case ADVINSPECTION: {

                if(receivedPacket.getType().equalsIgnoreCase(PacketType.ADVINSPECTION.getConfirmation())) {

                    ExportableInspection exportableInspection = ExportableInspection.parse(receivedPacket);

                    AdvInspectionManager.getInstance().addExportableInspection(exportableInspection);

                }

                break;

            }

            case EXPORTINSPECTION: {

                if(receivedPacket.getType().equalsIgnoreCase(PacketType.EXPORTINSPECTION.getConfirmation())) {

                    List<LinkedTreeMap> schedules = (List<LinkedTreeMap>) receivedPacket.getArgument("schedules");

                    LinkedTreeMap timeZoneMap = (LinkedTreeMap) receivedPacket.getArgument("timeZone");

                    TimeZone timeZone = null;

                    if(timeZoneMap != null) {

                        timeZone = TimeZone.parse(timeZoneMap);

                    }

                    if(timeZone == null)
                        break;

                    final List<ScheduleTurn> scheduleList = new ArrayList<>();

                    for (LinkedTreeMap scheduleMap : schedules)
                        scheduleList.add(ScheduleTurn.parse(scheduleMap));

                    TimeZone finalTimeZone = timeZone;
                    Platform.runLater(() -> {
                        try {
                            ExportInspectorManager.getInstance().exportSchedule(scheduleList, finalTimeZone, true, null);
                        } catch (FileNotFoundException e) {
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

            case DATABASEBACKUP: {

                if(receivedPacket.getType().equalsIgnoreCase(PacketType.DATABASEBACKUP.getConfirmation())) {

                    if (screenManager.getController() instanceof BackupController) {

                        BackupController backupController = (BackupController) screenManager.getController();

                        String date = (String) receivedPacket.getArgument("date");
                        String sql = (String) receivedPacket.getArgument("sql");

                        Platform.runLater(() -> {
                            backupController.setInProgress(false);
                            backupController.receivedSQL(date, sql);
                        });

                    }

                }

                break;

            }

        }

    }

    public Channel getChannel() {
        return channel;
    }

    public ClientSession getClientSession() { return clientSession; }

    public boolean isClosed() {
        return closed;
    }

    public List<Packet> getPendingPackets() {
        return pendingPackets;
    }

    public void addPendingPacket(Packet packet) {
        this.pendingPackets.add(packet);
    }

    public void socketErrorAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error con la conexión");
        alert.setHeaderText(null);
        alert.setContentText("Ha ocurrido un error con la conexión con el servidor.\nCerrando la aplicación");
        alert.showAndWait();

        close();
        Platform.exit();

    }

    /**
     * Send packet to client's socket output
     * @param packet
     */
    public synchronized void sendPacketIO(Packet packet) {

        String msg = packet.toString() + "\n";

        final ByteBuf byteBufMsg = channel.alloc().buffer(msg.length());
        byteBufMsg.writeBytes(msg.getBytes());

        if(channel != null)
            channel.writeAndFlush(byteBufMsg);

    }

    /**
     * Read packet from server, client's socket input
     * @return packet object parsed by json input
     */
    public synchronized Packet readPacketIO(String message) {

        return Client.GSON.fromJson(message, Packet.class);

    }

}
