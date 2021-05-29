package morales.david.desktop.managers.eventcallbacks;

public class ScheduleErrorListener extends ScheduleListenerType {

    private String message;

    public ScheduleErrorListener(String uuid, String message) {
        super(uuid);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
