package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.TimeZone;

import java.util.ArrayList;
import java.util.List;

public class SchedulerPair {

    private List<Schedule> scheduleList;
    private List<Day> dayList;
    private List<Hour> hourList;
    private List<TimeZone> timeZoneList;

    private Scheduler schedulerMorning;
    private Scheduler schedulerAfternoon;

    private SchedulerManager schedulerManager;

    public SchedulerPair(List<Schedule> scheduleList, SchedulerManager schedulerManager) {

        this.schedulerManager = schedulerManager;

        this.scheduleList = scheduleList;
        this.dayList = new ArrayList<>(DataManager.getInstance().getDays());
        this.hourList = new ArrayList<>(DataManager.getInstance().getHours());
        this.timeZoneList = new ArrayList<>(DataManager.getInstance().getTimeZones());

        this.schedulerMorning = new Scheduler(this, true);
        this.schedulerAfternoon = new Scheduler(this, false);

    }

    public Scheduler get(boolean isMorning) {
        if (isMorning)
            return schedulerMorning;
        else
            return schedulerAfternoon;
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

    public SchedulerManager getTimetableManager() {
        return schedulerManager;
    }

}
