package morales.david.server.models;

import java.sql.Time;

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

    @Override
    public String toString() {
        return "TimeZone{" +
                "id=" + id +
                ", day=" + day +
                ", hour=" + hour +
                '}';
    }
}
