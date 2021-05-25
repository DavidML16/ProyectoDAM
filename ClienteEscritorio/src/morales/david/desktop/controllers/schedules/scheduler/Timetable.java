package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.TimeZone;

import java.util.List;

public class Timetable {

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
    private Schedule[][] schedules;

    private TimetablePair parentPair;

    public Timetable(TimetablePair parentPair, boolean isMorning) {
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

            schedules = new Schedule[DAY_LENGTH][HOURS_LENGTH];

            for(int day = 0; day < DAY_LENGTH; day++) {

                for(int hour = 0; hour < HOURS_LENGTH; hour++) {

                    Schedule schedule = findSchedule(day + 1, hour + 1);

                    if(schedule != null) {

                        schedules[day][hour] = schedule;

                    }

                }

            }

        }

    }

    public void switchSchedule(int i1, int j1, int i2, int j2) {
        Schedule temp = schedules[i1][j1];
        schedules[i1][j1] = schedules[i2][j2];
        schedules[i2][j2] = temp;
    }

    private Schedule findSchedule(int day, int hour) {
        int displacement = isMorning ? 0 : 6;
        for(Schedule schedule : parentPair.getScheduleList()) {
            if(schedule.getTimeZone().getDay().getId() == day && schedule.getTimeZone().getHour().getId() == (hour + displacement)) {
                return schedule;
            }
        }
        return null;
    }

    public Day[] getDays() {
        return days;
    }

    public Hour[] getHours() {
        return hours;
    }

    public Schedule[][] getSchedules() {
        return schedules;
    }

    public Schedule getSchedule(int day, int hour) {
        return schedules[day][hour];
    }

    public String getScheduleText(int day, int hour) {

        Schedule schedule = schedules[day][hour];

        if(schedule == null || schedule.getTeacher() == null || schedule.getSubject() == null || schedule.getGroup() == null)
            return "";

        StringBuilder sb = new StringBuilder();
        sb.append(schedule.getTeacher().getName());
        sb.append("\n");
        sb.append(schedule.getSubject().getAbreviation() + "     " + schedule.getClassroom().toString());
        sb.append("\n");
        sb.append(schedule.getGroup().toString());

        return sb.toString();

    }

    public void setSchedule(Schedule schedule, int day, int hour) {
        schedules[day][hour] = schedule;
    }

    public void clearSubject(int indexDay, int indexHour) {
        schedules[indexDay][indexHour] = new Schedule();
    }

}
