package morales.david.desktop.controllers.schedules.scheduler;

import morales.david.desktop.controllers.modals.SchedulerItemModalController;
import morales.david.desktop.models.SchedulerItem;
import morales.david.desktop.models.TimeZone;

import java.util.List;

public class SchedulerManager {

    private SchedulerPair currentTimetable;

    private int selectedIndexDay = 0;
    private int selectedIndexHour = 0;

    private SchedulerItem clipBoard;

    private boolean isMorning;

    private SchedulerGUI gui;

    private SchedulerItemModalController openedItemModal;

    private String searchType, searchQuery;

    public SchedulerManager(List<SchedulerItem> scheduleList, String searchType, String searchQuery) {
        currentTimetable = new SchedulerPair(scheduleList, this);
        this.searchType = searchType;
        this.searchQuery = searchQuery;
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

    public TimeZone getSelectedTimeZone() {
        return getCurrentTable().getTimeZoneBy(selectedIndexDay + 1, (selectedIndexHour + 1) + (isMorning ? 0 : 6));
    }

    public void setSelectedIndexHour(int selectedIndexHour) {
        this.selectedIndexHour = selectedIndexHour;
    }

    public void deleteSchedule() {
        currentTimetable.get(isMorning).deleteScheduleItem(selectedIndexDay, selectedIndexHour);
    }

    public SchedulerItem getClipBoard() {
        return clipBoard;
    }

    public void setClipBoard(SchedulerItem clipBoard) {
        this.clipBoard = clipBoard;
    }

    public void copyCurrentClipboard() {
        if(getCurrentTable().getScheduleItem(selectedIndexDay, selectedIndexHour) != null) {
            clipBoard = getCurrentTable().getScheduleItem(selectedIndexDay, selectedIndexHour).duplicate();
        }
    }

    public void copy(int day, int hour) {
        if(getCurrentTable().getScheduleItem(day, hour) != null) {
            clipBoard = getCurrentTable().getScheduleItem(day, hour).duplicate();
        }
    }

    public void pasteCurrentClipboard() {
        if (clipBoard != null) {
            getCurrentTable().setScheduleItem(clipBoard.duplicate(), selectedIndexDay, selectedIndexHour);
        }
    }

    public void paste(int day, int hour) {
        if (clipBoard != null) {
            getCurrentTable().setScheduleItem(clipBoard.duplicate(), day, hour);
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

    public SchedulerItemModalController getOpenedItemModal() {
        return openedItemModal;
    }

    public void setOpenedItemModal(SchedulerItemModalController openedItemModal) {
        this.openedItemModal = openedItemModal;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

}
