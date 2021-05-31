package morales.david.desktop.models;

import com.google.gson.internal.LinkedTreeMap;

public class TimeZone {

    private int id;
    private Day day;
    private Hour hour;

    public TimeZone() {
        this(-1, null, null);
    }

    public TimeZone(int id, Day day, Hour hour) {
        this.id = id;
        this.day = day;
        this.hour = hour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }

    public static TimeZone parse(LinkedTreeMap timeZoneMap) {

        int id = ((Double) timeZoneMap.get("id")).intValue();

        LinkedTreeMap dayMap = (LinkedTreeMap) timeZoneMap.get("day");
        Day day = Day.parse(dayMap);

        LinkedTreeMap hourMap = (LinkedTreeMap) timeZoneMap.get("hour");
        Hour hour = Hour.parse(hourMap);

        return new TimeZone(id, day, hour);

    }

    @Override
    public String toString() {
        return day.getName() + ", " + hour.getName();
    }

}
