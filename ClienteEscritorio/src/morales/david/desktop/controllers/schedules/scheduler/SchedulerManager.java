package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

import java.util.List;

public class SchedulerManager {

    private SchedulerPair currentTimetable;

    private int selectedIndexDay = 0;
    private int selectedIndexHour = 0;

    private SchedulerItem clipBoard;

    private boolean isMorning;

    private SchedulerGUI gui;

    public SchedulerManager(List<SchedulerItem> scheduleList) {
        currentTimetable = new SchedulerPair(scheduleList, this);
    }

    public SchedulerPair getCurrentTimetable() {
        return currentTimetable;
    }

    public void setCurrentTimetable(SchedulerPair currentTimetable) {
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

    public void deleteSchedule() {
        currentTimetable.get(isMorning).deleteSchedule(selectedIndexDay, selectedIndexHour);
    }

    public SchedulerItem getClipBoard() {
        return clipBoard;
    }

    public void setClipBoard(SchedulerItem clipBoard) {
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

    public Scheduler getCurrentTable() {
        return currentTimetable.get(isMorning);
    }

    public SchedulerGUI getGui() {
        return gui;
    }

    public void setGui(SchedulerGUI gui) {
        this.gui = gui;
    }

}
