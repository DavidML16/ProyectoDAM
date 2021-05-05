package morales.david.android.managers;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import morales.david.android.interfaces.MessageListener;

public class EventManager {

    private static EventManager INSTANCE = null;

    public static EventManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new EventManager();
        return INSTANCE;
    }

    private Map<String, List<MessageListener>> listeners = new HashMap<>();

    public void addEventTypes(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    public void subscribe(String eventType, MessageListener listener) {
        List<MessageListener> typeListeners = listeners.get(eventType);
        typeListeners.add(listener);
    }

    public void unsubscribe(String eventType, MessageListener listener) {
        List<MessageListener> typeListeners = listeners.get(eventType);
        typeListeners.remove(listener);
    }

    public void notify(Activity context, String eventType, String message) {
        context.runOnUiThread(() -> {
            List<MessageListener> typeListeners = listeners.get(eventType);
            for (MessageListener listener : typeListeners) {
                listener.callback(eventType, message);
            }
        });
    }

}
