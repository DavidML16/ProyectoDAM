package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.TimeZone;

import java.util.ArrayList;
import java.util.List;

public class TimetablePair {

    private List<Schedule> scheduleList;
    private List<Day> dayList;
    private List<Hour> hourList;
    private List<TimeZone> timeZoneList;

    private Timetable timetableMorning;
    private Timetable timetableAfternoon;

    public TimetablePair(List<Schedule> scheduleList) {

        this.scheduleList = scheduleList;
        this.dayList = new ArrayList<>(DataManager.getInstance().getDays());
        this.hourList = new ArrayList<>(DataManager.getInstance().getHours());
        this.timeZoneList = new ArrayList<>(DataManager.getInstance().getTimeZones());

        this.timetableMorning = new Timetable(this, true);
        this.timetableAfternoon = new Timetable(this, false);

    }

    public Timetable get(boolean isMorning) {
        if (isMorning)
            return timetableMorning;
        else
            return timetableAfternoon;
    }

    public List<Schedule> getScheduleList() { return scheduleList; }

    public List<Day> getDayList() {
        return dayList;
    }

    public List<Hour> getHourList() {
        return hourList;
    }

    public List<TimeZone> getTimeZoneList() {
        return timeZoneList;
    }

}
