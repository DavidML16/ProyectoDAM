package morales.david.android.managers.eventcallbacks;

import java.util.List;

import morales.david.android.models.Classroom;

public class EmptyClassroomsConfirmationListener extends EventListenerType {

    private List<Classroom> classroomList;

    public EmptyClassroomsConfirmationListener(String uuid, List<Classroom> classroomList) {
        super(uuid);
        this.classroomList = classroomList;
    }

    public List<Classroom> getClassroomList() {
        return classroomList;
    }

}
