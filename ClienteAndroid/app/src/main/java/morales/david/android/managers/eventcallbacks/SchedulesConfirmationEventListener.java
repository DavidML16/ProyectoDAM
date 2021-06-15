package morales.david.android.managers.eventcallbacks;

import java.util.List;

import morales.david.android.models.Schedule;

public class SchedulesConfirmationEventListener extends EventListenerType {

    private List<Schedule> schedules;

    public SchedulesConfirmationEventListener(String uuid, List<Schedule> schedules) {
        super(uuid);
        this.schedules = schedules;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

}
