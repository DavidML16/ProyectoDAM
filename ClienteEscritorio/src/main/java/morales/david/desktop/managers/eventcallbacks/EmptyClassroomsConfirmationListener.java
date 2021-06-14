package morales.david.desktop.managers.eventcallbacks;

import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.SchedulerItem;

import java.util.List;

public class EmptyClassroomsConfirmationListener extends ScheduleListenerType {

    private List<Classroom> classroomList;

    public EmptyClassroomsConfirmationListener(String uuid, List<Classroom> classroomList) {
        super(uuid);
        this.classroomList = classroomList;
    }

    public List<Classroom> getClassroomList() {
        return classroomList;
    }

}
