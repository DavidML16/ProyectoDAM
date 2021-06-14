package morales.david.android.managers.eventcallbacks;

public class ConfirmationEventListener extends EventListenerType {

    private String message;

    public ConfirmationEventListener(String uuid, String message) {
        super(uuid);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
