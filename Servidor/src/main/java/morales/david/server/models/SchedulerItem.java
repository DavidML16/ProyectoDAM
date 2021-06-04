package morales.david.server.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulerItem {

    private String uuid;
    private List<Schedule> scheduleList;

    /**
     * Create a new instance of SchedulerItem with given params, and a random UUID
     * @param scheduleList
     */
    public SchedulerItem(List<Schedule> scheduleList) {
        this(UUID.randomUUID().toString(), scheduleList);
    }

    /**
     * Create a new instance of SchedulerItem with given params
     * @param uuid
     * @param scheduleList
     */
    public SchedulerItem(String uuid, List<Schedule> scheduleList) {
        this.uuid = uuid;
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

    @Override
    public String toString() {
        return "SchedulerItem{" +
                "uuid='" + uuid + '\'' +
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

        return new SchedulerItem(uuid, schedules);

    }

    /**
     * Create a new instance of schedulerItem, with the same list of schedules
     * @return schedulerItem object
     */
    public SchedulerItem duplicate() {
        return new SchedulerItem(scheduleList);
    }

    /**
     * Create a new instance of schedulerItem, with the same uuid and list of schedules
     * @return schedulerItem object
     */
    public SchedulerItem duplicateUUID() {
        return new SchedulerItem(uuid, scheduleList);
    }

}
