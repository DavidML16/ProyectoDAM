package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.*;

import java.util.ArrayList;
import java.util.List;

public class SchedulerPair {

    private List<SchedulerItem> scheduleList;
    private List<Day> dayList;
    private List<Hour> hourList;
    private List<TimeZone> timeZoneList;

    private Scheduler schedulerMorning;
    private Scheduler schedulerAfternoon;

    private SchedulerManager schedulerManager;

    public SchedulerPair(List<SchedulerItem> scheduleList, SchedulerManager schedulerManager) {

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

    public Scheduler getSchedulerMorning() {
        return schedulerMorning;
    }

    public Scheduler getSchedulerAfternoon() {
        return schedulerAfternoon;
    }

    public List<SchedulerItem> getScheduleList() { return scheduleList; }

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
