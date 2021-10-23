package morales.david.android.netty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import morales.david.android.R;
import morales.david.android.activities.MainActivity;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.eventcallbacks.ConfirmationEventListener;
import morales.david.android.managers.eventcallbacks.EmptyClassroomsConfirmationListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.managers.eventcallbacks.SchedulesConfirmationEventListener;
import morales.david.android.models.Classroom;
import morales.david.android.models.ClientSession;
import morales.david.android.models.Course;
import morales.david.android.models.Credential;
import morales.david.android.models.Day;
import morales.david.android.models.Group;
import morales.david.android.models.Hour;
import morales.david.android.models.Schedule;
import morales.david.android.models.SchedulerItem;
import morales.david.android.models.Subject;
import morales.david.android.models.Teacher;
import morales.david.android.models.TimeZone;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.Constants;

public final class ClientManager {

    private static ClientManager INSTANCE = null;

    public static ClientManager getInstance() {
        if (INSTANCE == null)
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

        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 7000)
                .handler(new ClientInitializer());

        final ChannelFuture f = bootstrap.connect(Constants.SERVER_IP, Constants.SERVER_PORT);

        f.addListener((FutureListener<Void>) voidFuture -> {

            if (!f.isSuccess()) {

                Activity context = ScreenManager.getInstance().getActivity();

                context.runOnUiThread(() -> {

                    EventManager.getInstance().notify(context, "start", new ErrorEventListener("start", context.getString(R.string.act_login_message_error_server)));

                });

            }

        });

        channel = f.channel();

        closed = false;

    }

    public void close() {

        if (group != null) {

            group.shutdownGracefully();
            group = null;

        }

        closed = true;

        pendingPackets.clear();

    }

    public void processPacket(Packet packet) {

        receivedPacket = packet;

        PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(receivedPacket.getType()));

        Activity context = ScreenManager.getInstance().getActivity();

        switch (packetType) {

            case LOGIN: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.getConfirmation())) {

                    clientSession.setId(((Double) receivedPacket.getArgument("id")).intValue());
                    clientSession.setName((String) receivedPacket.getArgument("name"));
                    clientSession.setRole((String) receivedPacket.getArgument("role"));

                    sendPackets();

                    EventManager.getInstance().notify(context, "login", new ConfirmationEventListener("login", "SUCCESS"));

                } else if (receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.LOGIN.getError())) {

                    EventManager.getInstance().notify(context, "login", new ErrorEventListener("login", context.getString(R.string.act_login_message_error_credentials)));

                }

                break;
            }

            case EXIT: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.EXIT.getConfirmation())) {

                    SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.remove(context.getString(R.string.sgh_preference_user));
                    editor.remove(context.getString(R.string.sgh_preference_pass));
                    editor.commit();

                    context.runOnUiThread(() -> {

                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);

                        close();

                    });

                }

                break;

            }

            case TEACHERS: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.TEACHERS.getConfirmation())) {

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

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.CREDENTIALS.getConfirmation())) {

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

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.CLASSROOMS.getConfirmation())) {

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

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.COURSES.getConfirmation())) {

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

            case GROUPS: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.GROUPS.getConfirmation())) {

                    List<LinkedTreeMap> groups = (List<LinkedTreeMap>) receivedPacket.getArgument("groups");

                    final List<Group> temp = new ArrayList<>();

                    for (LinkedTreeMap groupMap : groups)
                        temp.add(Group.parse(groupMap));

                    context.runOnUiThread(() -> {
                        DataManager.getInstance().setGroups(temp);
                    });

                }

                break;

            }

            case SUBJECTS: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.SUBJECTS.getConfirmation())) {

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

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.DAYS.getConfirmation())) {

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

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.HOURS.getConfirmation())) {

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

            case TIMEZONES: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.TIMEZONES.getConfirmation())) {

                    List<LinkedTreeMap> timeZones = (List<LinkedTreeMap>) receivedPacket.getArgument("timeZones");

                    final List<TimeZone> temp = new ArrayList<>();

                    for (LinkedTreeMap timeZonesMap : timeZones)
                        temp.add(TimeZone.parse(timeZonesMap));

                    context.runOnUiThread(() -> {
                        DataManager.getInstance().setTimeZones(temp);
                    });

                }

                break;

            }

            case EMPTYCLASSROOMSTIMEZONE: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.EMPTYCLASSROOMSTIMEZONE.getConfirmation())) {

                    String uuid = (String) receivedPacket.getArgument("uuid");

                    List<LinkedTreeMap> classrooms = (List<LinkedTreeMap>) receivedPacket.getArgument("classrooms");

                    List<Classroom> emptyClassrooms = new ArrayList<>();

                    for (LinkedTreeMap classroomMap : classrooms)
                        emptyClassrooms.add(Classroom.parse(classroomMap));

                    EventManager.getInstance().notify(context, uuid, new EmptyClassroomsConfirmationListener(uuid, emptyClassrooms));

                } else if (receivedPacket.getType().equalsIgnoreCase(PacketType.EMPTYCLASSROOMSTIMEZONE.getError())) {

                    String uuid = (String) receivedPacket.getArgument("uuid");
                    String message = (String) receivedPacket.getArgument("message");

                    EventManager.getInstance().notify(context, uuid, new ErrorEventListener(uuid, message));

                }

                break;

            }

            case SEARCHSCHEDULE: {

                if (receivedPacket.getType().equalsIgnoreCase(PacketType.SEARCHSCHEDULE.getConfirmation())) {

                    List<LinkedTreeMap> schedules = (List<LinkedTreeMap>) receivedPacket.getArgument("schedules");

                    final List<SchedulerItem> scheduleList = new ArrayList<>();

                    for (LinkedTreeMap scheduleMap : schedules)
                        scheduleList.add(SchedulerItem.parse(scheduleMap));

                    final List<Schedule> schedulesList = new ArrayList<>();

                    for (SchedulerItem schedulerItem : scheduleList) {
                        if (schedulerItem.getScheduleList() == null || schedulerItem.getScheduleList().size() == 0)
                            continue;
                        for (Schedule schedule : schedulerItem.getScheduleList()) {
                            schedulesList.add(schedule);
                        }
                    }

                    EventManager.getInstance().notify(context, "searchschedule", new SchedulesConfirmationEventListener("searchschedule", schedulesList));

                }

                break;

            }

            }

        }

    public Channel getChannel () {
        return channel;
    }

    public ClientSession getClientSession () {
        return clientSession;
    }

    public boolean isClosed () {
        return closed;
    }

    public List<Packet> getPendingPackets () {
        return pendingPackets;
    }

    public void addPendingPacket (Packet packet){
        this.pendingPackets.add(packet);
    }

    /**
     * Send packet to client's socket output
     * @param packet
     */
    public synchronized void sendPacketIO (Packet packet){

        String msg = packet.toString() + "\n";

        final ByteBuf byteBufMsg = channel.alloc().buffer(msg.length());
        byteBufMsg.writeBytes(msg.getBytes());

        if (channel != null && channel.isOpen())
            channel.writeAndFlush(byteBufMsg);

    }

    /**
     * Read packet from server, client's socket input
     * @return packet object parsed by json input
     */
    public synchronized Packet readPacketIO (String message){

        return PacketBuilder.GSON.fromJson(message, Packet.class);

    }

    private void sendPackets() {

        for(PacketType packetType : Constants.INIT_PACKETS) {

            Packet requestPacket = new PacketBuilder().ofType(packetType.getRequest()).build();
            sendPacketIO(requestPacket);

        }

    }

}
