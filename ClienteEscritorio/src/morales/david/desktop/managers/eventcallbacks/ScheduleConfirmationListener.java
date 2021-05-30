package morales.david.desktop.managers.eventcallbacks;

import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

public class ScheduleConfirmationListener extends ScheduleListenerType {

    private SchedulerItem schedule;

    public ScheduleConfirmationListener(String uuid, SchedulerItem schedule) {
        super(uuid);
        this.schedule = schedule;
    }

    public SchedulerItem getScheduleItem() {
        return schedule;
    }

}
