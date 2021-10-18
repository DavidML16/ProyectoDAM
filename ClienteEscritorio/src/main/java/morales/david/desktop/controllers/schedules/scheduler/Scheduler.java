package morales.david.desktop.controllers.schedules.scheduler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import morales.david.desktop.controllers.modals.SchedulerItemModalController;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.eventcallbacks.*;
import morales.david.desktop.ClientManager;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    public static int MORNING_START_HOUR = 1;
    public static int MORNING_BREAK_HOUR = 13;
    public static int MORNING_END_HOUR = 6;

    public static int AFTERNOON_START_HOUR = 7;
    public static int AFTERNOON_BREAK_HOUR = 14;
    public static int AFTERNOON_END_HOUR = 12;

    private static final int DAY_LENGTH = 5;
    private static final int HOURS_LENGTH = 7;

    private boolean isMorning;

    private Hour[] hours;
    private Day[] days;
    private SchedulerItem[][] schedules;

    private SchedulerPair parentPair;

    public Scheduler(SchedulerPair parentPair, boolean isMorning) {
        this.parentPair = parentPair;
        this.isMorning = isMorning;
        init();
    }

    private void init() {

        {

            days = new Day[DAY_LENGTH];

            days = parentPair.getDayList().toArray(days);

        }

        {

            hours = new Hour[HOURS_LENGTH];

            int displacement = isMorning ? 0 : 6;

            hours[0] = parentPair.getHourList().get(0 + displacement);
            hours[1] = parentPair.getHourList().get(1 + displacement);
            hours[2] = parentPair.getHourList().get(2 + displacement);
            hours[3] = parentPair.getHourList().get(isMorning ? 12 : 13);
            hours[4] = parentPair.getHourList().get(3 + displacement);
            hours[5] = parentPair.getHourList().get(4 + displacement);
            hours[6] = parentPair.getHourList().get(5 + displacement);

        }

        {

            schedules = new SchedulerItem[DAY_LENGTH][HOURS_LENGTH];

            for(int day = 0; day < DAY_LENGTH; day++) {

                Day dayObject = days[day];

                for(int hour = 0; hour < HOURS_LENGTH; hour++) {

                    Hour hourObject = hours[hour];

                    TimeZone timeZone = getTimeZoneBy(dayObject.getId(), hourObject.getId());

                    SchedulerItem schedulerItem = findSchedule(dayObject, hourObject);

                    if(schedulerItem != null) {

                        schedules[day][hour] = schedulerItem;

                    } else {

                        schedules[day][hour] = new SchedulerItem();
                        schedules[day][hour].setTimeZone(timeZone);

                    }

                }

            }

        }

    }

    private SchedulerItem findSchedule(Day day, Hour hour) {
        for(SchedulerItem schedulerItem : parentPair.getScheduleList()) {
            if (schedulerItem.getTimeZone().getDay().getId() == day.getId() && schedulerItem.getTimeZone().getHour().getId() == hour.getId()) {
                return schedulerItem;
            }
        }
        return null;
    }

    public TimeZone getTimeZoneBy(int day, int hour) {
        for(TimeZone timeZone : new ArrayList<>(DataManager.getInstance().getTimeZones())) {
            if(timeZone.getDay().getId() == day && timeZone.getHour().getId() == hour) {
                return timeZone;
            }
        }
        return null;
    }

    public TimeZone getTimeZoneByIndex(int day, int hour) {

        int tempHour = hour + (hour > 3 ? 0 : 1) + (isMorning ? 0 : 6);

        if(hour == 3) {
            if(isMorning)
                tempHour = 13;
            else
                tempHour = 14;
        }

        TimeZone timeZone = getTimeZoneBy(day + 1, tempHour);

        return timeZone;

    }


    public Day[] getDays() {
        return days;
    }

    public Hour[] getHours() {
        return hours;
    }

    public SchedulerItem[][] getScheduleItems() {
        return schedules;
    }

    public SchedulerItem[] getScheduleItemsByHour(Hour hourObject) {

        SchedulerItem[] schedulesHour = new SchedulerItem[days.length];

        for(int day = 0; day < days.length; day++) {

            for(int hour = 0; hour < hours.length; hour++) {

                SchedulerItem schedulerItem = schedules[day][hour];

                if(schedulerItem.getTimeZone().getHour().getId() == hourObject.getId()) {

                    schedulesHour[day] = schedulerItem;

                }

            }

        }

        return schedulesHour;

    }

    public SchedulerItem getScheduleItem(int day, int hour) {

        if(day < 0 || hour < 0)
            return null;

        return schedules[day][hour];

    }

    public boolean haveSchedules() {

        for(int i = 0; i < schedules.length; i++) {

            for(int j = 0; j < schedules[0].length; j++) {

                SchedulerItem schedulerItem = schedules[i][j];

                if(schedulerItem.getScheduleList().size() > 0)
                    return true;

            }

        }

        return false;

    }

    public List<Teacher> getTeachers() {

        List<Teacher> teachers = new ArrayList<>();
        List<Integer> teachersIds = new ArrayList<>();

        for(int i = 0; i < schedules.length; i++) {

            for(int j = 0; j < schedules[0].length; j++) {

                SchedulerItem schedulerItem = schedules[i][j];

                for(Schedule schedule : schedulerItem.getScheduleList()) {

                    if(schedule.getTeacher() != null && !teachersIds.contains(schedule.getTeacher().getId())) {

                        teachers.add(schedule.getTeacher());
                        teachersIds.add(schedule.getTeacher().getId());

                    }

                }

            }

        }

        return teachers;

    }



    public void setScheduleItem(SchedulerItem schedule, int day, int hour) {

        SchedulerItem oldSchedule = schedules[day][hour];

        if(oldSchedule == null || oldSchedule.getScheduleList().size() == 0) {

            setScheduleItemFinal(schedule, day, hour);

        } else {

            EventManager.getInstance().subscribe(oldSchedule.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleConfirmationListener) {

                    schedules[day][hour] = null;
                    setScheduleItemFinal(schedule, day, hour);

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet deleteScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getRequest())
                    .addArgument("scheduleItem", oldSchedule)
                    .build();

            ClientManager.getInstance().sendPacketIO(deleteScheduleRequestPacket);

        }

    }
    private void setScheduleItemFinal(SchedulerItem schedulerItem, int day, int hour) {

        TimeZone newTimeZone = getTimeZoneByIndex(day, hour);
        TimeZone oldTimeZone = schedulerItem.getTimeZone();

        for(Schedule schedule : schedulerItem.getScheduleList())
            schedule.setTimeZone(newTimeZone);

        if (schedulerItem.getScheduleList().size() > 0) {

            EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

                if (scheduleListenerType instanceof ScheduleConfirmationListener) {

                    schedulerItem.setTimeZone(newTimeZone);
                    schedules[day][hour] = schedulerItem;
                    parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                    for(Schedule schedule : schedulerItem.getScheduleList())
                        schedule.setTimeZone(oldTimeZone);

                    schedulerItem.setTimeZone(oldTimeZone);

                }

            });

            Packet insertScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.INSERTSCHEDULEITEM.getRequest())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            ClientManager.getInstance().sendPacketIO(insertScheduleRequestPacket);

        }

    }

    public void switchSchedule(int i1, int j1, int i2, int j2) {

        SchedulerItem schedule1 = schedules[i1][j1];

        if(schedule1 == null || schedule1.getScheduleList().size() <= 0)
            return;

        SchedulerItem schedule2 = schedules[i2][j2];

        if(schedule2 == null || schedule2.getScheduleList().size() <= 0) {

            deleteScheduleItem(i1, j1);

            Platform.runLater(() -> setScheduleItemFinal(schedule1, i2, j2));

        } else {

            if(schedule1 == schedule2)
                return;

            EventManager.getInstance().subscribe(schedule1.getUuid() + "|" + schedule2.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleSwitchConfirmationListener) {

                    ScheduleSwitchConfirmationListener switchConfirmationListener = (ScheduleSwitchConfirmationListener) scheduleListenerType;

                    schedules[i1][j1] = null;
                    schedules[i2][j2] = null;

                    schedules[i1][j1] = switchConfirmationListener.getSchedule2();
                    schedules[i2][j2] = switchConfirmationListener.getSchedule1();

                    parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet switchScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.SWITCHSCHEDULEITEM.getRequest())
                    .addArgument("scheduleItem1", schedule1)
                    .addArgument("scheduleItem2", schedule2)
                    .build();

            ClientManager.getInstance().sendPacketIO(switchScheduleRequestPacket);

        }

    }

    public void deleteScheduleItem(int indexDay, int indexHour) {

        SchedulerItem schedulerItem = schedules[indexDay][indexHour];

        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {

            TimeZone timeZone = getTimeZoneByIndex(indexDay, indexHour);

            EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleConfirmationListener) {

                    schedules[indexDay][indexHour] = null;
                    schedules[indexDay][indexHour] = new SchedulerItem();
                    schedules[indexDay][indexHour].setTimeZone(timeZone);
                    parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet deleteScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.REMOVESCHEDULEITEM.getRequest())
                    .addArgument("scheduleItem", schedulerItem)
                    .build();

            ClientManager.getInstance().sendPacketIO(deleteScheduleRequestPacket);

        }

    }

    public void deleteSchedule(SchedulerItem schedulerItem, Schedule schedule) {

        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {

            EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleConfirmationListener) {

                    schedulerItem.getScheduleList().remove(schedule);
                    parentPair.getTimetableManager().getOpenedItemModal().initItems();
                    parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet deleteScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.DELETESCHEDULE.getRequest())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            ClientManager.getInstance().sendPacketIO(deleteScheduleRequestPacket);

        }

    }

    public void updateSchedule(SchedulerItem schedulerItem, Schedule schedule) {

        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {

            EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleConfirmationListener) {

                    for(Schedule cachedSchedule : schedulerItem.getScheduleList()) {
                        if(cachedSchedule.getUuid().equalsIgnoreCase(schedule.getUuid())) {

                            cachedSchedule.setTeacher(schedule.getTeacher());
                            cachedSchedule.setSubject(schedule.getSubject());
                            cachedSchedule.setGroup(schedule.getGroup());
                            cachedSchedule.setClassroom(schedule.getClassroom());
                            cachedSchedule.setTimeZone(schedule.getTimeZone());

                            parentPair.getTimetableManager().getOpenedItemModal().initItems();
                            parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                            break;
                        }
                    }

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet updateScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.UPDATESCHEDULE.getRequest())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            ClientManager.getInstance().sendPacketIO(updateScheduleRequestPacket);

        }

    }

    public void addSchedule(SchedulerItem schedulerItem, Schedule schedule) {

        if(schedulerItem != null) {

            EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

                if(scheduleListenerType instanceof ScheduleConfirmationListener) {

                    schedulerItem.getScheduleList().add(schedule);

                    parentPair.getTimetableManager().getOpenedItemModal().initItems();
                    parentPair.getTimetableManager().getGui().displayCurrentTimetable();

                } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                    ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                    System.out.println(errorListener.getMessage());

                }

            });

            Packet addScheduleRequestPacket = new PacketBuilder()
                    .ofType(PacketType.ADDSCHEDULE.getRequest())
                    .addArgument("scheduleItem", schedulerItem)
                    .addArgument("schedule", schedule)
                    .build();

            ClientManager.getInstance().sendPacketIO(addScheduleRequestPacket);

        }

    }

    public void openSchedulerItemModal(SchedulerItem schedulerItem, TimeZone timeZone) {

        EventManager.getInstance().subscribe(schedulerItem.getUuid(), (eventType, scheduleListenerType) -> {

            if(scheduleListenerType instanceof EmptyClassroomsConfirmationListener) {

                List<Classroom> emptyClassrooms = ((EmptyClassroomsConfirmationListener) scheduleListenerType).getClassroomList();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/schedulerItemModal.fxml"));
                try {

                    DialogPane parent = loader.load();
                    SchedulerItemModalController controller = loader.getController();

                    controller.setData(parentPair.getTimetableManager(), schedulerItem, timeZone, emptyClassrooms);

                    Dialog<ButtonType> dialog = new Dialog<>();

                    dialog.setDialogPane(parent);
                    dialog.setTitle("");

                    ButtonType cancelBtn = new ButtonType("Cerrar", ButtonBar.ButtonData.NO);

                    dialog.getDialogPane().getButtonTypes().addAll(cancelBtn);

                    Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
                    cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

                    ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

                    parentPair.getTimetableManager().setOpenedItemModal(controller);

                    dialog.showAndWait();

                    parentPair.getTimetableManager().setOpenedItemModal(null);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                System.out.println(errorListener.getMessage());

            }

        });

        Packet emptyClassroomsRequestPacket = new PacketBuilder()
                .ofType(PacketType.EMPTYCLASSROOMSTIMEZONE.getRequest())
                .addArgument("uuid", schedulerItem.getUuid())
                .addArgument("timeZone", schedulerItem.getTimeZone())
                .build();

        ClientManager.getInstance().sendPacketIO(emptyClassroomsRequestPacket);

    }

}
