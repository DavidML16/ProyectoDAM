package morales.david.desktop.managers.eventcallbacks;

import morales.david.desktop.models.Schedule;

public class ScheduleSwitchConfirmationListener extends ScheduleListenerType {

    private Schedule schedule1;
    private Schedule schedule2;

    public ScheduleSwitchConfirmationListener(String uuid, Schedule schedule1, Schedule schedule2) {
        super(uuid);
        this.schedule1 = schedule1;
        this.schedule2 = schedule2;
    }

    public Schedule getSchedule1() {
        return schedule1;
    }

    public Schedule getSchedule2() {
        return schedule2;
    }

}
