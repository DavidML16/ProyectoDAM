package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.Subject;

import java.util.List;

public class TimetableManager {

    private TimetablePair currentTimetable;

    private int selectedIndexDay = 0;
    private int selectedIndexHour = 0;

    private Schedule clipBoard;

    private boolean isMorning;

    public TimetableManager(List<Schedule> scheduleList) {
        currentTimetable = new TimetablePair(scheduleList);
    }

    public TimetablePair getCurrentTimetable() {
        return currentTimetable;
    }

    public void setCurrentTimetable(TimetablePair currentTimetable) {
        this.currentTimetable = currentTimetable;
    }

    public int getSelectedIndexDay() {
        return selectedIndexDay;
    }

    public void setSelectedIndexDay(int selectedIndexDay) {
        this.selectedIndexDay = selectedIndexDay;
    }

    public int getSelectedIndexHour() {
        return selectedIndexHour;
    }

    public void setSelectedIndexHour(int selectedIndexHour) {
        this.selectedIndexHour = selectedIndexHour;
    }

    public void clearSubject() {
        currentTimetable.get(isMorning).clearSubject(selectedIndexDay, selectedIndexHour);
    }

    public Schedule getClipBoard() {
        return clipBoard;
    }

    public void setClipBoard(Schedule clipBoard) {
        this.clipBoard = clipBoard;
    }

    public void copyCurrentClipboard() {
        if(getCurrentTable().getSchedule(selectedIndexDay, selectedIndexHour) != null) {
            clipBoard = getCurrentTable().getSchedule(selectedIndexDay, selectedIndexHour).duplicate();
        }
    }

    public void copy(int day, int hour) {
        if(getCurrentTable().getSchedule(day, hour) != null) {
            clipBoard = getCurrentTable().getSchedule(day, hour).duplicate();
        }
    }

    public void pasteCurrentClipboard() {
        if (clipBoard != null) {
            getCurrentTable().setSchedule(clipBoard, selectedIndexDay, selectedIndexHour);
        }
    }

    public void paste(int day, int hour) {
        if (clipBoard != null) {
            getCurrentTable().setSchedule(clipBoard, day, hour);
        }
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
