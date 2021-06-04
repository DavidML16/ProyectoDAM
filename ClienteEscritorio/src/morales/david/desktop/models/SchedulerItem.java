package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchedulerItem {

    private String uuid;
    private List<Schedule> scheduleList;

    public SchedulerItem() {
        this(new ArrayList<>());
    }

    public SchedulerItem(List<Schedule> scheduleList) {
        this(UUID.randomUUID().toString(), scheduleList);
    }

    public SchedulerItem(String uuid, List<Schedule> scheduleList) {
        this.uuid = uuid;
        this.scheduleList = scheduleList;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

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

    public static SchedulerItem parse(LinkedTreeMap schedulerItemMap) {

        String uuid = (String) schedulerItemMap.get("uuid");

        List<LinkedTreeMap> schs = (List<LinkedTreeMap>) schedulerItemMap.get("scheduleList");

        List<Schedule> schedules = new ArrayList<>();

        for(LinkedTreeMap scheduleMap : schs)
            schedules.add(Schedule.parse(scheduleMap));

        return new SchedulerItem(uuid, schedules);

    }

    public SchedulerItem duplicate() {
        List<Schedule> temp = new ArrayList<>();
        for(Schedule schedule : scheduleList)
            temp.add(schedule.duplicate());
        return new SchedulerItem(temp);
    }

    public SchedulerItem duplicateUUID() {
        List<Schedule> temp = new ArrayList<>();
        for(Schedule schedule : scheduleList)
            temp.add(schedule.duplicate());
        return new SchedulerItem(uuid, temp);
    }

    public String getText() {

        if(getScheduleList().size() == 0)
            return "";

        StringBuilder sb = new StringBuilder();

        for(Schedule schedule : getScheduleList()) {
            sb.append(schedule.getText(0));
        }

        return sb.toString();

    }

}
