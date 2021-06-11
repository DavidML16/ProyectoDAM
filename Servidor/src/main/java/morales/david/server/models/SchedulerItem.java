package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulerItem {

    private String uuid;
    private TimeZone timeZone;
    private List<Schedule> scheduleList;

    public SchedulerItem() {
        this(new TimeZone(), new ArrayList<>());
    }

    /**
     * Create a new instance of SchedulerItem with given params, and a random UUID
     * @param scheduleList
     */
    public SchedulerItem(TimeZone timeZone, List<Schedule> scheduleList) {
        this(UUID.randomUUID().toString(), timeZone, scheduleList);
    }

    /**
     * Create a new instance of SchedulerItem with given params
     * @param uuid
     * @param scheduleList
     */
    public SchedulerItem(String uuid, TimeZone timeZone, List<Schedule> scheduleList) {
        this.uuid = uuid;
        this.timeZone = timeZone;
        this.scheduleList = scheduleList;
    }

    /**
     * Get uuid of the schedulerItem
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set uuid of the schedulerItem
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get associated schedules list of the schedulerItem
     * @return schedules list
     */
    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    /**
     * Set associated schedules list of the schedulerItem
     * @param scheduleList
     */
    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    /**
     * Get timeZone of the schedulerItem
     * @return uuid
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Set timeZone of the schedulerItem
     * @param timeZone
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "SchedulerItem{" +
                "uuid='" + uuid + '\'' +
                ", timeZone=" + timeZone +
                ", scheduleList=" + scheduleList +
                '}';
    }

    /**
     * Convert the received TreeMap to a SchedulerItem object
     * @param schedulerItemMap
     * @return
     */
    public static SchedulerItem parse(LinkedTreeMap schedulerItemMap) {

        String uuid = (String) schedulerItemMap.get("uuid");

        List<LinkedTreeMap> schs = (List<LinkedTreeMap>) schedulerItemMap.get("scheduleList");

        List<Schedule> schedules = new ArrayList<>();

        for(LinkedTreeMap scheduleMap : schs)
            schedules.add(Schedule.parse(scheduleMap));

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) schedulerItemMap.get("timeZone");
        TimeZone timeZone = TimeZone.parse(timeZoneMap);

        return new SchedulerItem(uuid, timeZone, schedules);

    }

    /**
     * Create a new instance of schedulerItem, with the same list of schedules
     * @return schedulerItem object
     */
    public SchedulerItem duplicate() {
        List<Schedule> temp = new ArrayList<>();
        for(Schedule schedule : scheduleList)
            temp.add(schedule.duplicate());
        return new SchedulerItem(timeZone, temp);
    }

    /**
     * Create a new instance of schedulerItem, with the same uuid and list of schedules
     * @return schedulerItem object
     */
    public SchedulerItem duplicateUUID() {
        List<Schedule> temp = new ArrayList<>();
        for(Schedule schedule : scheduleList)
            temp.add(schedule.duplicate());
        return new SchedulerItem(uuid, timeZone, temp);
    }

}
