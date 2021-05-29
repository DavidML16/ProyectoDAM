package morales.david.desktop.managers.eventcallbacks;

import morales.david.desktop.models.Schedule;

public class ScheduleConfirmationListener extends ScheduleListenerType {

    private Schedule schedule;

    public ScheduleConfirmationListener(String uuid, Schedule schedule) {
        super(uuid);
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

}
