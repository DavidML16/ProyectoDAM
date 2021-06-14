package morales.david.android.managers.eventcallbacks;

public class ErrorEventListener extends EventListenerType {

    private String message;

    public ErrorEventListener(String uuid, String message) {
        super(uuid);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
