package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;
import morales.david.desktop.models.packets.Packet;

import java.util.ArrayList;
import java.util.List;

public class ExportableInspection {

    private TimeZone timeZone;
    private List<ScheduleTurn> scheduleTurns;

    public ExportableInspection(TimeZone timeZone, List<ScheduleTurn> scheduleTurns) {
        this.timeZone = timeZone;
        this.scheduleTurns = scheduleTurns;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public List<ScheduleTurn> getScheduleTurns() {
        return scheduleTurns;
    }

    public void setScheduleTurns(List<ScheduleTurn> scheduleTurns) {
        this.scheduleTurns = scheduleTurns;
    }

    @Override
    public String toString() {
        return "ExportableInspection{" +
                "timeZone=" + timeZone +
                ", scheduleTurns=" + scheduleTurns.size() +
                '}';
    }

    public static ExportableInspection parse(Packet packet) {

        List<LinkedTreeMap> schedules = (List<LinkedTreeMap>) packet.getArgument("schedules");

        LinkedTreeMap timeZoneMap = (LinkedTreeMap) packet.getArgument("timeZone");

        TimeZone timeZone = null;

        if(timeZoneMap != null) {

            timeZone = TimeZone.parse(timeZoneMap);

        }

        if(timeZone == null)
            return null;

        final List<ScheduleTurn> scheduleList = new ArrayList<>();

        for (LinkedTreeMap scheduleMap : schedules)
            scheduleList.add(ScheduleTurn.parse(scheduleMap));

        return new ExportableInspection(timeZone, scheduleList);

    }

}
