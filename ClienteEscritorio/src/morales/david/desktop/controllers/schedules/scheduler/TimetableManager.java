package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.Subject;

import java.util.List;

public class TimetableManager {

    private TimetablePair currentTimetable;

    private boolean isMorning;

    public TimetableManager(List<Schedule> subjectList) {
        currentTimetable = new TimetablePair(subjectList);
    }

    public TimetablePair getCurrentTimetable() {
        return currentTimetable;
    }

    public void setCurrentTimetable(TimetablePair currentTimetable) {
        this.currentTimetable = currentTimetable;
    }

    public boolean isMorning() {
        return isMorning;
    }

    public void setMorning(boolean morning) {
        isMorning = morning;
    }

    public Timetable getCurrentTable() {
        return currentTimetable.get(isMorning);
    }

}
