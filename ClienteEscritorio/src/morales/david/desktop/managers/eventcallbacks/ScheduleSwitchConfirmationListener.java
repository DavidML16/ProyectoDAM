package morales.david.desktop.managers.eventcallbacks;

import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

public class ScheduleSwitchConfirmationListener extends ScheduleListenerType {

    private SchedulerItem schedule1;
    private SchedulerItem schedule2;

    public ScheduleSwitchConfirmationListener(String uuid, SchedulerItem schedule1, SchedulerItem schedule2) {
        super(uuid);
        this.schedule1 = schedule1;
        this.schedule2 = schedule2;
    }

    public SchedulerItem getSchedule1() {
        return schedule1;
    }

    public SchedulerItem getSchedule2() {
        return schedule2;
    }

}
