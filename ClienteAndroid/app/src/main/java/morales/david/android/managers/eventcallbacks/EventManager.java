package morales.david.android.managers.eventcallbacks;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static EventManager INSTANCE = null;

    public static EventManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new EventManager();
        return INSTANCE;
    }

    private Map<String, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(String uuid, EventListener listener) {
        if(!listeners.containsKey(uuid))
            listeners.put(uuid, new ArrayList<>());
        List<EventListener> typeListeners = listeners.get(uuid);
        typeListeners.add(listener);
    }

    public void unsubscribe(String uuid, EventListener listener) {
        List<EventListener> typeListeners = listeners.get(uuid);
        typeListeners.remove(listener);
    }

    public void notify(Activity context, String uuid, EventListenerType eventListenerType) {
        context.runOnUiThread(() -> {
            List<EventListener> typeListeners = listeners.get(uuid);
            if(typeListeners == null)
                return;
            for (EventListener listener : typeListeners) {
                listener.callback(uuid, eventListenerType);
            }
            listeners.remove(uuid);
        });
    }

}
