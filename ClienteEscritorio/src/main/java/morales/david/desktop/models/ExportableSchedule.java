package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.desktop.models.packets.Packet;

import java.util.ArrayList;
import java.util.List;

public class ExportableSchedule {

    private String exportType;
    private String exportQuery;

    private List<SchedulerItem> schedulerItems;

    public ExportableSchedule(String exportType, String exportQuery, List<SchedulerItem> schedulerItems) {
        this.exportType = exportType;
        this.exportQuery = exportQuery;
        this.schedulerItems = schedulerItems;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getExportQuery() {
        return exportQuery;
    }

    public void setExportQuery(String exportQuery) {
        this.exportQuery = exportQuery;
    }

    public List<SchedulerItem> getSchedulerItems() {
        return schedulerItems;
    }

    public void setSchedulerItems(List<SchedulerItem> schedulerItems) {
        this.schedulerItems = schedulerItems;
    }

    @Override
    public String toString() {
        return "ExportableSchedule{" +
                "exportType='" + exportType + '\'' +
                ", exportQuery='" + exportQuery + '\'' +
                ", schedulerItems=" + schedulerItems.size() +
                '}';
    }

    public static ExportableSchedule parse(Packet packet) {

        String exportType = (String) packet.getArgument("exportType");
        String exportQuery = (String) packet.getArgument("exportQuery");

        List<LinkedTreeMap> schedules = (List<LinkedTreeMap>) packet.getArgument("schedules");

        final List<SchedulerItem> scheduleList = new ArrayList<>();

        for (LinkedTreeMap scheduleMap : schedules)
            scheduleList.add(SchedulerItem.parse(scheduleMap));

        return new ExportableSchedule(exportType, exportQuery, scheduleList);

    }

}
